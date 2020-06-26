pipeline {
    agent any
    stages {
        stage('Gitclone') {
            agent {
                docker {
                    image 'maven:3.6.3-jdk-11'
                    args '-v /root/.m2:/root/.m2'
                }
            }

            steps {
                script {
                    sh 'echo Test Git Clone !'
                    checkout scm
                    sh 'echo === End Git clone === '
                    sh 'mvn -B -DskipTests clean package'
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                    sh 'ls target/'
                }
            }
        }

        stage('Build Docker') {
            steps {
                script {
                    sh 'ls target/'
                    docker.withRegistry("https://${AZ_CONTAINER_REGISTRY_URL}",'77ae6c02-d40b-4bae-82bf-ade4eeff03e3') {
                        def newApp = docker.build "${AZ_CONTAINER_REGISTRY_URL}/dev/elasticsearchapi:${BUILD_ID}"
                        newApp.push()
                    }
                }
            }
        }
    }
}
