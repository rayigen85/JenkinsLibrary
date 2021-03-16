import com.jenkins.*;

def cleanInstall(def steps) {
    steps.withMaven(jdk: 'linux_jdk8u221', maven: 'linux_M3') {
        steps.sh 'mvn clean install'
    }
}

def call(body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    lock(env.BRANCH_NAME) {

        pipeline {

            tools {
                jdk 'linux_jdk8u221'
                maven 'linux_M3'
            }

            triggers {
                cron(config.cron)
            }

            agent any

            environment {
                MYKEY = "value"
            }

            stages {

                stage('Print build Variables') {
                    steps {
                        echo currentBuild.buildVariables.MYKEY
                    }
                }

                stage('Print previous build Variables') {
                    when {
                        not {
                            equals expected: null, actual: currentBuild.previousBuild
                        }
                    }
                    steps {
                        echo currentBuild.previousBuild.buildVariables.MYKEY
                    }
                }

                stage('Compile/Test/Install') {
                    steps {
                        script {
                            cleanInstall(this)
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
                                    } finally {
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
                        archiveArtifacts artifacts: '**/*.jar', allowEmptyArchive: true
                        archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
                    }
                }
            }
        }
    }
}