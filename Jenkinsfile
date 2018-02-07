@Library('semantic_releasing')_

withEnv([   "HOST=18.196.37.97",
            "PORT=31081",
            "KEYCLOAK_URL=http://18.196.37.97:31190/auth"]) {
    node {
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
            sh "mvn clean package"
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
        }

        stage('build image & git tag & docker push') {
            env.VERSION = semanticReleasing()
            currentBuild.displayName = env.VERSION

            sh "mvn versions:set -DnewVersion=${env.VERSION}"
            sh "git config user.email \"jenkins@khinkali.ch\""
            sh "git config user.name \"Jenkins\""
            sh "git tag -a ${env.VERSION} -m \"Setze Version auf ${env.VERSION}\""
            withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/khinkali/sink.git --tags"
            }

            sh "docker build -t khinkali/sink:${env.VERSION} ."
            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                sh "docker login --username ${DOCKER_USERNAME} --password ${DOCKER_PASSWORD}"
            }
            sh "docker push khinkali/sink:${env.VERSION}"
        }

        stage('deploy to test') {
            sh "sed -i -e 's/        image: khinkali\\/sink:0.0.1/        image: khinkali\\/sink:${env.VERSION}/' startup.yml"
            sh "kubectl --kubeconfig /tmp/admin.conf apply -f startup.yml"
        }

        stage('system tests') {
            withCredentials([usernamePassword(credentialsId: 'application', passwordVariable: 'APPLICATION_PASSWORD', usernameVariable: 'APPLICATION_USER_NAME')]) {
                sh "mvn clean install failsafe:integration-test failsafe:verify"
            }
            junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml'
        }

        stage('deploy to prod') {
            input(message: 'deploy to prod?' )
            sh "sed -i -e 's/  namespace: test/  namespace: default/' startup.yml"
            sh "sed -i -e 's/    nodePort: 31081/    nodePort: 30081/' startup.yml"
            sh "sed -i -e 's/          value: \"http:\\/\\/18.196.37.97:31190\\/auth\"/          value: \"http:\\/\\/18.196.37.97:30190\\/auth\"/' startup.yml"
            sh "kubectl --kubeconfig /tmp/admin.conf apply -f startup.yml"
        }
    }
}