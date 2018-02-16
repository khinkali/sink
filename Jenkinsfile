@Library('semantic_releasing')_

podTemplate(label: 'mypod', containers: [
    containerTemplate(name: 'docker', image: 'docker', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.0', command: 'cat', ttyEnabled: true),
    containerTemplate(name: 'curl', image: 'khinkali/jenkinstemplate:0.0.3', command: 'cat', ttyEnabled: true),
    containerTemplate(name: 'maven', image: 'maven:3.5.2-jdk-8', command: 'cat', ttyEnabled: true)
  ],
  volumes: [
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
  ]) {
    withEnv([   "HOST=18.196.37.97",
                "PORT=31081",
                "KEYCLOAK_URL=http://18.196.37.97:31190/auth"]) {
        node('mypod') {
            def mvnHome = tool 'M3'
            env.PATH = "${mvnHome}/bin/:${env.PATH}"
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
                git url: "https://github.com/khinkali/sink"
                withCredentials([usernamePassword(credentialsId: 'nexus', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                    sh 'mvn -s settings.xml clean package'
                }
                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
            }

            stage('build image & git tag & docker push') {
                env.VERSION = semanticReleasing()
                currentBuild.displayName = env.VERSION

                sh "mvn versions:set -DnewVersion=${env.VERSION}"
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

            stage('deploy to test') {
                sh "sed -i -e 's/        image: khinkali\\/sink:0.0.1/        image: khinkali\\/sink:${env.VERSION}/' startup.yml"
                sh "sed -i -e 's/          value: \"0.0.1\"/          value: \"${env.VERSION}\"/' startup.yml"
                container('kubectl') {
                    sh "kubectl apply -f startup.yml"
                }
                container('curl') {
                    checkVersion(env.VERSION, "http://${HOST}:${PORT}/sink/resources/health")
                }
            }

            stage('system tests') {
                withCredentials([usernamePassword(credentialsId: 'application', passwordVariable: 'APPLICATION_PASSWORD', usernameVariable: 'APPLICATION_USER_NAME')]) {
                    container('maven') {
                        sh "mvn -s settings.xml clean integration-test failsafe:integration-test failsafe:verify"
                    }
                }
                junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml'
            }

            stage('last test') {
                withCredentials([usernamePassword(credentialsId: 'application', passwordVariable: 'APPLICATION_PASSWORD', usernameVariable: 'APPLICATION_USER_NAME')]) {
                    container('maven') {
                        def tokenAll = sh(
                                script: "curl -k -v -X POST -H \"Content-Type: application/x-www-form-urlencoded\" -d \"username=${APPLICATION_USER_NAME}\" -d \"password=${APPLICATION_PASSWORD}\" -d 'grant_type=password' -d \"client_id=sink-frontend\" http://18.196.37.97:31190/auth/realms/cryptowatch/protocol/openid-connect/token",
                                returnStdout: true
                        ).trim()
                        echo "tokenAll: ${tokenAll}"
                        def data = readJSON text: "${tokenAll}"
                        echo "token: ${data.access_token}"
                        sh "mvn -s settings.xml clean jmeter:jmeter -Dlt.domain=${HOST} -Dlt.port=${PORT} -Dlt.keycloak_token=${data.access_token}"
                        sh "mvn -s settings.xml jmeter-analysis:analyze"
                    }
                }
                archiveArtifacts artifacts: 'target/reports/*.*', fingerprint: true
            }

            stage('deploy to prod') {
                input(message: 'manuel user tests ok?' )
                withCredentials([usernamePassword(credentialsId: 'github-api-token', passwordVariable: 'GITHUB_TOKEN', usernameVariable: 'GIT_USERNAME')]) {
                    container('curl') {
                        gitHubRelease(env.VERSION, 'khinkali', 'sink', GITHUB_TOKEN)
                    }
                }
                sh "sed -i -e 's/  namespace: test/  namespace: default/' startup.yml"
                sh "sed -i -e 's/    nodePort: 31081/    nodePort: 30081/' startup.yml"
                sh "sed -i -e 's/          value: \"http:\\/\\/18.196.37.97:31190\\/auth\"/          value: \"http:\\/\\/18.196.37.97:30190\\/auth\"/' startup.yml"
                container('kubectl') {
                    sh "kubectl apply -f startup.yml"
                }
                container('curl') {
                    checkVersion(env.VERSION, 'http://18.196.37.97:30081/sink/resources/health')
                }
            }
        }
    }
}