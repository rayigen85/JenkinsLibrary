import com.jenkins.lib.*;

int cleanInstall() {
    withMaven(jdk: 'linux_jdk8u221', maven: 'linux_M3') {
        sh 'mvn clean install'
        echo config.test
    }
    return CallCounter.count()
}