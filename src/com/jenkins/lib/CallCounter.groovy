package com.jenkins.lib;

class CallCounter {

    static private int count = 0;

    int count() {
        return count++;
    }
}
