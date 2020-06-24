pipeline {
    agent {
        docker {
            image 'maven:3.6.3-jdk-11' 
            args '-v /root/.m2:/root/.m2' 
        }
    }
    stages {
        stage('Build Maven') { 
            steps {
                echo "=== Build Maven ==="
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
                sh 'mvn -B -DskipTests clean package' 
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }

        stage('Build Docker') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
            }
        }
    }
}