withEnv([   "HOST=18.196.37.97",
            "PORT=30081"]) {
    node {
        tools {
            maven 'M3'
        }

        stage('checkout & unit tests & build') {
            git url: "https://github.com/khinkali/sink"
            sh "mvn clean package"
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
        }
        stage('test') {
            sh "/tmp/kubectl --kubeconfig /tmp/admin.conf get no"
        }
    }
}