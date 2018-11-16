package com.reign.framework.common;

public final class ServerConstants
{
    public static final String JSESSIONID = "REIGNID";
    public static final String COMMAND = "command";
    public static final String CONTENT_TYPE_COMPRESSED = "application/x-gzip-compressed";
    public static final String CONTENT_TYPE = "application/json";
    public static final String LONG_HTTP = "longhttp";
    public static final byte[] CROSSDOMAIN;
    public static final String POLICY_FILE_REQUEST = "<policy-file-request/>\u0000";
    
    static {
        CROSSDOMAIN = "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>\u0000".getBytes();
    }
}
