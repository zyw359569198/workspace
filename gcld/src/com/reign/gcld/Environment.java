package com.reign.gcld;

import java.util.*;

public class Environment
{
    public static final Map<String, String> COMPONENT_MAP;
    private static final String MAIN_VERSION = "3.1.3.3";
    public static final Map<String, String> ENVIRONMENT;
    
    static {
        (COMPONENT_MAP = new HashMap<String, String>()).put("reign-tcp-framework", "1.2.3.2");
        Environment.COMPONENT_MAP.put("gcldcore", "1.0.1.8");
        Environment.COMPONENT_MAP.put("sdata", "3.1.3.1");
        Environment.COMPONENT_MAP.put("db", "3.1.1.23");
        Environment.COMPONENT_MAP.put("gcld", "3.1.3.3");
        Environment.COMPONENT_MAP.put("reign-util", "1.1.5.0");
        Environment.COMPONENT_MAP.put("reign-yxoperation", "1.1.10.0");
        Environment.COMPONENT_MAP.put("gcld-kf-comm", "1.0.1.0");
        Environment.COMPONENT_MAP.put("gcld-gw", "1.0.4.0");
        Environment.COMPONENT_MAP.put("gcld-match", "1.1.14.0");
        (ENVIRONMENT = new HashMap<String, String>()).put("componentName", "gcld");
        Environment.ENVIRONMENT.put("version", "3.1.3.3");
    }
    
    public static String getMainVersion() {
        return "3.1.3.3";
    }
}
