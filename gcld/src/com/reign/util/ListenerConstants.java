package com.reign.util;

import java.io.*;

public class ListenerConstants
{
    public static final String PLAYER = "PLAYER";
    public static final String USER = "USER";
    public static final String PROPERTIES = "properties";
    public static String WEB_PATH;
    public static String SDATA_URL;
    public static String YX;
    
    static {
        ListenerConstants.WEB_PATH = String.valueOf(System.getProperty("user.dir")) + File.separator + "properties" + File.separator;
        ListenerConstants.SDATA_URL = "";
        ListenerConstants.YX = "yx";
    }
}
