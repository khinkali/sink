@Library('semantic_releasing')_

podTemplate(label: 'mypod', containers: [
    containerTemplate(name: 'khinkali', image: 'khinkali/jenkinstemplate:0.0.2', ttyEnabled: true, command: 'cat')
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
              container('khinkali') {
                env.VERSION = semanticReleasing()
                currentBuild.displayName = env.VERSION

                sh "mvn versions:set -DnewVersion=${env.VERSION}"
                sh "git config user.email \"jenkins@khinkali.ch\""
                sh "git config user.name \"Jenkins\""
                sh "git tag -a ${env.VERSION} -m \"${env.VERSION}\""
                withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/khinkali/sink.git --tags"
                }

                sh "docker build -t khinkali/sink:${env.VERSION} ."
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "docker login --username ${DOCKER_USERNAME} --password ${DOCKER_PASSWORD}"
                }
                sh "docker push khinkali/sink:${env.VERSION}"
              }
            }

            stage('deploy to test') {
              container('khinkali') {
                sh "sed -i -e 's/        image: khinkali\\/sink:0.0.1/        image: khinkali\\/sink:${env.VERSION}/' startup.yml"
                sh "sed -i -e 's/          value: \"0.0.1\"/          value: \"${env.VERSION}\"/' startup.yml"
                sh "kubectl --kubeconfig /tmp/admin.conf apply -f startup.yml"
                checkVersion(env.VERSION, "http://${HOST}:${PORT}/sink/resources/health")
              }
            }

            stage('system tests') {
                withCredentials([usernamePassword(credentialsId: 'application', passwordVariable: 'APPLICATION_PASSWORD', usernameVariable: 'APPLICATION_USER_NAME')]) {
                    sh "mvn clean verify failsafe:integration-test failsafe:verify"
                }
                junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml'
            }

            stage('deploy to prod') {
              container('khinkali') {
                input(message: 'manuel user tests ok?' )
                withCredentials([usernamePassword(credentialsId: 'github-api-token', passwordVariable: 'GITHUB_TOKEN', usernameVariable: 'GIT_USERNAME')]) {
                    gitHubRelease(env.VERSION, 'khinkali', 'sink', GITHUB_TOKEN)
                }
                sh "sed -i -e 's/  namespace: test/  namespace: default/' startup.yml"
                sh "sed -i -e 's/    nodePort: 31081/    nodePort: 30081/' startup.yml"
                sh "sed -i -e 's/          value: \"http:\\/\\/18.196.37.97:31190\\/auth\"/          value: \"http:\\/\\/18.196.37.97:30190\\/auth\"/' startup.yml"
                sh "kubectl --kubeconfig /tmp/admin.conf apply -f startup.yml"
                checkVersion(env.VERSION, 'http://18.196.37.97:30081/sink/resources/health')
              }
            }
        }
    }
}