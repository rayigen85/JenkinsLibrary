package com.jenkins.lib;

public class CallCounter {

    static private int count = 0;

    public static int count() {
        return count++;
    }
}
