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
            def commitHistoryText = sh(
                        script: "git log `git describe --tags --abbrev=0`..HEAD --oneline",
                        returnStdout: true
                ).trim()

            def allMarkers = []
            def array = commitHistoryText.split("\n")
            for (def i = 0; i < array.size(); i++) {
                def entry = array[i]
                echo "entry: ${entry}"
                def startIndex = entry.indexOf("[")
                def endIndex = entry.indexOf("]")
                if (startIndex == -1 || endIndex == -1) {
                    continue
                }
                allMarkers << entry.substring(startIndex + 1, endIndex).trim()
            }

            def oldTag = sh(
                    script: "git describe --tags --abbrev=0",
                    returnStdout: true
            ).trim()
            def versionParts = oldTag.split("\\.")
            def major = versionParts[0].toInteger()
            def minor = versionParts[1].toInteger()
            def bug = versionParts[2].toInteger()

            if(allMarkers.contains("API")) {
                major += 1;
                minor = 0;
                bug = 0;
            } else if(allMarkers.contains("FEAT")) {
                minor += 1;
                bug = 0;
            } else {
                bug += 1;
            }

            env.VERSION = "${major}.${minor}.${bug}"
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
            sh "while [ `curl --write-out %{http_code} --silent --output /dev/null http://${HOST}:${PORT}/sink/resources/balances --max-time 2` -eq 404 ]; do sleep 1 && echo \"waiting for service\"; done;"
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