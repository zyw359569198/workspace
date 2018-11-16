package com.reign.kf.comm;

import java.util.*;

public class Environment
{
    public static final Map<String, String> ENVIRONMENT;
    
    static {
        (ENVIRONMENT = new HashMap<String, String>()).put("componentName", "gcld-kf-comm");
        Environment.ENVIRONMENT.put("version", "1.0.1.0");
    }
}
