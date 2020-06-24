node {
        stage('Gitclone') {
        checkout scm
        def newApp = docker.build "scm.dimensiondata.com:5050/ddth/appteam/devops-is-culture:${BUILD_ID}"
        }
}
