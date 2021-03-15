import com.jenkins.*;

def call(body) {
    LinkedHashMap config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    properties([
            pipelineTriggers([
                    [$class: "SCMTrigger", scmpoll_spec: "* * * * *"],
            ])
    ])

    pipeline {

        agent { label 'mac'}

        stages {

            stage ('parallel build/test/push') {
                parallel {
                    stage('MAC Build (Java Threading)') {
                        agent { label 'mac' }
                        tools {
                            jdk 'linux_jdk1.8.0_172'
                            maven 'linux_M3'
                        }
                        steps {
                            // checkout
                            git poll: true, branch: 'master', credentialsId: 'd6ae3020-ebc8-4fa7-9071-5f96e10ce3f8', url: 'https://thomasmosigfrey@git.code.sf.net/p/threadingexample/code'

                            // build
                            withMaven(jdk: 'linux_jdk1.8.0_172', maven: 'linux_M3') {
                                sh 'mvn clean install'
                            }

                            // tests
                            withMaven(jdk: 'linux_jdk1.8.0_172', maven: 'linux_M3') {
                                sh 'mvn test'
                            }
                            // archive artifacts
                            stash allowEmpty: true, includes: 'target/Thr*.jar', name: 'jarFilesThreading'
                        }
                    }
                    stage('UNIX Build (Kotlin) ') {
                        agent { label 'unix' }
                        tools {
                            jdk 'linux_jdk1.8.0_172'
                            maven 'linux_M3'
                        }
                        steps {
                            // checkout
                            git poll: true, branch: 'master', credentialsId: 'd6ae3020-ebc8-4fa7-9071-5f96e10ce3f8', url: 'https://thomasmosigfrey@git.code.sf.net/p/the-example-app-kotlin/code'

                            // build
                            withMaven(jdk: 'linux_jdk1.8.0_172', maven: 'linux_M3') {
                                sh 'mvn clean install'
                            }

                            // tests
                            withMaven(jdk: 'linux_jdk1.8.0_172', maven: 'linux_M3') {
                                sh 'mvn test'
                            }
                            // archive artifacts
                            stash allowEmpty: true, includes: 'target/dagg*.jar', name: 'jarFilesKotlin'
                        }
                    }
                }
            }
            stage ('SIT Test') {
                agent { label 'test' }
                tools {
                    jdk 'linux_jdk1.8.0_172'
                    maven 'linux_M3'
                }
                steps {
                    // retrieve artifacts
                    unstash 'jarFilesKotlin'
                    unstash 'jarFilesThreading'

                    // use them testwise
                    // save test results
                }
            }
        }
    }
}