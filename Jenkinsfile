withEnv([   "HOST=18.196.37.97",
            "PORT=30081"]) {
    node {
        def mvnHome = tool 'M3'
        env.PATH = "${mvnHome}/bin/:${env.PATH}"

        stage('checkout & unit tests & build') {
            git url: "https://github.com/khinkali/sink"
            sh "mvn clean package"
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
        }

        stage('build image') {
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
            sh "git tag -a ${env.VERSION} -m \"Setze Version auf ${env.VERSION}\""
            withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                sh "git config user.email \"jenkins@khinkali.ch\""
                sh "git config user.name \"Jenkins\""

                sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/khinkali/sink.git --tags"
            }
            sh "docker ps"
        }

        stage('test') {
            sh "kubectl --kubeconfig /tmp/admin.conf get no"
        }
    }
}