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
                echo "=== Build Maven + ==="
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
                sh 'mvn -B -DskipTests clean package' 
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }

        stage('Build Docker') { 
            steps {
                sh 'docker build -t scm.dimensiondata.com:5050/ddth/appteam/devops-is-culture:${BUILD_ID} .' 
                sh 'docker login -u gitlab-ci-token -p ${GITLAB_PASS} scm.dimensiondata.com:5050' 
                sh 'docker push scm.dimensiondata.com:5050/ddth/appteam/devops-is-culture' 

            }
        }
    }
}