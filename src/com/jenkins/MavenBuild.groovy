package com.jenkins;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenBuild {
    static int cleanInstall(def steps) {
        steps.withMaven(jdk: 'linux_jdk8u221', maven: 'linux_M3') {
            steps.sh 'mvn clean install'
            steps.echo config.test
        }
        return CallCounter.count()
    }
}