import com.jenkins.lib.*;

public class mavenBuild {
    int cleanInstall() {
        withMaven(jdk: 'linux_jdk8u221', maven: 'linux_M3') {
            sh 'mvn clean install'
            echo config.test
        }
        return new CallCounter().count()
    }
}