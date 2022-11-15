import com.jenkins.*;

def call(body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

   pipeline {
    agent {
        label 'rbaruth'
    }
    stages {
        stage('Echo config map') {
            steps {
                echo config.branch_name
            }
        }
        stage('WS cleanup'){
            steps{cleanWs()}
            
        }
        stage('SCM checkout') {
            steps {
                git credentialsId: '1743a11b-ed27-416c-a3b9-de0a1555a33f', url: 'https://github.com/ThomasMosigFrey/jee8.git/'
                }
        }
        stage('compile/test') {
            steps {
                withMaven(maven: 'Maven 3.8.6') {
                    sh "mvn clean package"
                }
            }
        }
        stage('install') {
            steps {
                sh '''
                    echo install
                '''
            }
        }
        stage('publish') {
            steps {
                sh '''
                    echo publish
                '''
            }
        }
        stage('report') {
            steps {
                echo 'report'
                sh '''
                    echo report
                '''
            }
        }
    }
}
}
