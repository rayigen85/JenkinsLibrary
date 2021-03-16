import com.jenkins.*;

def call(body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {

        tools {
            jdk 'linux_jdk8u221'
            maven 'linux_M3'
        }

        triggers {
            cron(config.cron)
        }

        agent any

        stages {
            stage('Compile/Test/Install') {
                steps {
                    script {
                        MavenBuild.cleanInstall(this)
                    }
                }
            }

            stage('Code Analysis') {
                steps {
                    withMaven(jdk: 'linux_jdk8u221', maven: 'linux_M3') {
                        withSonarQubeEnv('jenkins') {
                            script {
                                try {
                                    sh 'mvn sonar:sonar'
                                } catch (exc) {
                                    echo 'sonar checks failed: ' + exc.message
                                    //throw new hudson.AbortException("sonar checks failed: " + exc.message)
                                } finally  {
                                    echo 'sonar step finished'
                                }
                            }
                        }
                    }
                }
            }

            stage('Deploy') {
                steps {
                    withMaven(jdk: 'linux_jdk8u221', maven: 'linux_M3') {
                        sh 'echo mvn deploy -Dmaven.test.skip=true'
                    }
                }
            }

            stage('Archive artifacts') {
                steps {
                    archiveArtifacts artifacts: '**/*.jar',  allowEmptyArchive: true
                    archiveArtifacts artifacts: 'target/surefire-reports/*.xml',  allowEmptyArchive: true
                }
            }
        }
    }
}