node {
    stage('Gitclone') {
        echo 'Test Git Clone !'
        checkout scm
        echo '=== End Git clone === '
        sh 'mvn -B -DskipTests clean package'
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

    stage('Build Docker') {
        steps {
            script {
                sh 'docker ps'
                withDockerRegistry(credentialsId: '77ae6c02-d40b-4bae-82bf-ade4eeff03e3', url: 'https://scm.dimensiondata.com:5050') {
                    def newApp = docker.build "scm.dimensiondata.com:5050/ddth/appteam/devops-is-culture:${BUILD_ID}"
                    newApp.push()
                }
            }
        }
    }
}
