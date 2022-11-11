package com.jenkins;

import com.jenkins.lib.CallCounter;
import hudson.*;

public class MavenBuild {
    static void cleanInstall(def steps) {
        steps.withMaven(maven: 'linux_M3') {
            steps.echo "CallCounter: " + CallCounter.count()
            steps.sh 'mvn clean install'
        }
    }
}