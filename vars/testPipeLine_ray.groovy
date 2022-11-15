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
        stage('WS cleanup'){
            steps{cleanWs()}
            
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
