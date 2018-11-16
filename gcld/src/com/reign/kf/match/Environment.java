package com.reign.kf.match;

import java.util.*;

public class Environment
{
    public static final Map<String, String> ENVIRONMENT;
    
    static {
        (ENVIRONMENT = new HashMap<String, String>()).put("componentName", "gcld-match");
        Environment.ENVIRONMENT.put("version", "1.1.14.0");
    }
}
