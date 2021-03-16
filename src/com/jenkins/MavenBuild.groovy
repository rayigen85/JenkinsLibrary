package com.jenkins;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenBuild {
    static void cleanInstall(def steps) {
        steps.withMaven(jdk: 'linux_jdk8u221', maven: 'linux_M3') {
            steps.echo "CallCounter: " + CallCounter.count()
            steps.sh 'mvn clean install'
        }
    }
}