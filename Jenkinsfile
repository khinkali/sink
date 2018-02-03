withEnv([   "HOST=18.196.37.97",
            "PORT=30081"]) {
    node {
        def mvnHome = tool 'M3'
        env.PATH = "${mvnHome}/bin/;${env.PATH}"

        stage('checkout & unit tests & build') {
            git url: "https://github.com/khinkali/sink"
            sh "${mvnHome}/bin/mvn clean build"
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
        }
        stage('test') {
            sh "/tmp/kubectl --kubeconfig /tmp/admin.conf get no"
        }
    }
}