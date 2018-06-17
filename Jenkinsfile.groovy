@Library('semantic_releasing') _

podTemplate(label: 'mypod', containers: [
        containerTemplate(name: 'docker', image: 'docker', ttyEnabled: true, command: 'cat'),
        containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.0', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'curl', image: 'khinkali/jenkinstemplate:0.0.3', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'klar', image: 'khinkali/klar:0.0.5', command: 'cat', ttyEnabled: true),
        containerTemplate(name: 'maven', image: 'maven:3.5.2-jdk-8', command: 'cat', ttyEnabled: true)
],
        volumes: [
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
        ]) {
    withEnv(['HOST=5.189.154.24',
             'PORT=31081',
             'KEYCLOAK_URL=http://5.189.154.24:31190/auth',
             'APPLICATION_USER_ID=7f3ae29b-abd0-409b-89a0-2d21cee0a9a2',
             'APPLICATION_USER_WITHOUT_COINS_ID=091fdc4a-ae93-4c69-bfb6-306e3f29006b']) {
        node('mypod') {
            properties([
                    buildDiscarder(
                            logRotator(artifactDaysToKeepStr: '',
                                    artifactNumToKeepStr: '',
                                    daysToKeepStr: '',
                                    numToKeepStr: '30'
                            )
                    ),
                    pipelineTriggers([])
            ])

            stage('checkout & unit tests & build') {
                git url: 'https://github.com/khinkali/sink'
                withCredentials([usernamePassword(credentialsId: 'nexus', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                    container('maven') {
                        sh 'mvn -s settings.xml clean package'
                    }
                }
                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
            }

            stage('vulnerability check for java libraries') {
                container('maven') {
                    sh "mvn -s settings.xml compile net.ossindex:ossindex-maven-plugin:audit -Daudit.ignore=com.sun.mail:javax.mail:1.5.0"
                }
            }

            stage('build image & git tag & docker push') {
                env.VERSION = semanticReleasing()
                currentBuild.displayName = env.VERSION
                wrap([$class: 'BuildUser']) {
                    currentBuild.description = "Started by: ${BUILD_USER} (${BUILD_USER_EMAIL})"
                }

                container('maven') {
                    sh "mvn versions:set -DnewVersion=${env.VERSION}"
                }
                sh "git config user.email \"jenkins@khinkali.ch\""
                sh "git config user.name \"Jenkins\""
                sh "git tag -a ${env.VERSION} -m \"${env.VERSION}\""
                withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/khinkali/sink.git --tags"
                }

                container('docker') {
                    sh "docker build -t khinkali/sink:${env.VERSION} ."
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "docker login --username ${DOCKER_USERNAME} --password ${DOCKER_PASSWORD}"
                    }
                    sh "docker push khinkali/sink:${env.VERSION}"
                }
            }

            stage('vulnerability check for docker image') {
                container('klar') {
                    def statusCode = sh script: "CLAIR_ADDR=http://clair:6060 klar khinkali/sink:${env.VERSION}", returnStatus: true
                    if (statusCode != 0) {
                        currentBuild.result = 'FAILURE'
                        error "Docker Image did not pass Clair testing."
                    }
                }
            }

            stage('sonar analysis') {
                withCredentials([string(credentialsId: 'sonar-key', variable: 'SONAR_KEY')]) {
                    withCredentials([usernamePassword(credentialsId: 'nexus', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                        withSonarQubeEnv('Sonar') {
                            container('maven') {
                                sh """
                                    mvn -s settings.xml sonar:sonar \
                                     -Dsonar.host.url=http://sonar:9000 \
                                     -Dsonar.login=${SONAR_KEY} \
                                     -Dsonar.exclusions=**/Jenkinsfile.groovy,**/target/**
                                   """
                            }
                        }
                    }
                }
            }
            stage("Quality Gate") {
                timeout(time: 5, unit: 'MINUTES') {
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                        error "Pipeline aborted due to quality gate failure: ${qg.status}"
                    }
                }
            }

            stage('deploy to test') {
                sh "sed -i -e 's/image: khinkali\\/sink:0.0.1/image: khinkali\\/sink:${env.VERSION}/' startup.yml"
                sh "sed -i -e 's/value: \"0.0.1\"/value: \"${env.VERSION}\"/' startup.yml"
                container('kubectl') {
                    sh "kubectl apply -f startup.yml"
                }
                container('curl') {
                    checkVersion(env.VERSION, "http://${HOST}:${PORT}/sink/resources/health", 1, 5)
                }
            }

            stage('system tests') {
                withCredentials([usernamePassword(credentialsId: 'no-coins', passwordVariable: 'NO_COINS_PASSWORD', usernameVariable: 'NO_COINS_USER_NAME')]) {
                    withCredentials([usernamePassword(credentialsId: 'application', passwordVariable: 'APPLICATION_PASSWORD', usernameVariable: 'APPLICATION_USER_NAME')]) {
                        container('maven') {
                            sh "mvn -s settings.xml clean integration-test failsafe:integration-test failsafe:verify"
                        }
                    }
                }
                junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml'
            }

            stage('last test') {
                dir('testing') {
                    stage('Performance Tests') {
                        git url: 'https://github.com/khinkali/sink-testing'
                        container('maven') {
                            sh 'mvn clean gatling:integration-test'
                        }
                        archiveArtifacts artifacts: 'target/gatling/**/*.*', fingerprint: true
                        sh 'mkdir site'
                        sh 'cp -r target/gatling/healthsimulation*/* site'
                    }

                    stage('Build Report Image') {
                        container('docker') {
                            sh "docker build -t khinkali/sink-testing:${env.VERSION} ."
                            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                                sh "docker login --username ${DOCKER_USERNAME} --password ${DOCKER_PASSWORD}"
                            }
                            sh "docker push khinkali/sink-testing:${env.VERSION}"
                        }
                    }

                    stage('Deploy Testing on Dev') {
                        sh "sed -i -e 's/image: khinkali\\/sink-testing:todo/image: khinkali\\/sink-testing:${env.VERSION}/' kubeconfig.yml"
                        sh "sed -i -e 's/value: \"todo\"/value: \"${env.VERSION}\"/' kubeconfig.yml"
                        container('kubectl') {
                            sh "kubectl apply -f kubeconfig.yml"
                        }
                    }
                }
            }

            stage('deploy to prod') {
                try {
                    def userInput = input(message: 'manuel user tests ok?', submitterParameter: 'submitter')
                    currentBuild.description = "${currentBuild.description}\nGo for Prod by: ${userInput}"

                    withCredentials([usernamePassword(credentialsId: 'github-api-token', passwordVariable: 'GITHUB_TOKEN', usernameVariable: 'GIT_USERNAME')]) {
                        container('curl') {
                            gitHubRelease(env.VERSION, 'khinkali', 'sink', GITHUB_TOKEN)
                        }
                    }
                    sh "sed -i -e 's/namespace: test/namespace: default/' startup.yml"
                    sh "sed -i -e 's/nodePort: 31081/nodePort: 30081/' startup.yml"
                    sh "sed -i -e 's/value: \"http:\\/\\/5.189.154.24:31190\\/auth\"/value: \"http:\\/\\/5.189.154.24:30190\\/auth\"/' startup.yml"
                    container('kubectl') {
                        sh "kubectl apply -f startup.yml"
                    }
                    container('curl') {
                        checkVersion(env.VERSION, 'http://5.189.154.24:30081/sink/resources/health', 1, 5)
                    }
                } catch (err) {
                    def user = err.getCauses()[0].getUser()
                    currentBuild.description = "${currentBuild.description}\nNoGo for Prod by: ${user}"
                    currentBuild.result = 'ABORTED'
                }
            }
        }
    }
}