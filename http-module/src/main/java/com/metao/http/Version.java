package com.metao.http;

public class Version {

    private final static String version = "1.0";

    public static String getVersion() {
        return version;
    }

    public static String getClient() {
        return "x-client-metao1";
    }
}
