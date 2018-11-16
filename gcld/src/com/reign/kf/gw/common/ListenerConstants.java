package com.reign.kf.gw.common;

import java.io.*;

public class ListenerConstants
{
    public static final String PLAYER = "PLAYER";
    public static final String USER = "user";
    public static final String DIR = "properties";
    public static String WEB_PATH;
    
    static {
        ListenerConstants.WEB_PATH = String.valueOf(System.getProperty("user.dir")) + File.separator + "properties" + File.separator;
    }
}
