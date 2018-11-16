package com.reign.kf.gw;

import java.util.*;

public class Environment
{
    public static final Map<String, String> ENVIRONMENT;
    
    static {
        (ENVIRONMENT = new HashMap<String, String>()).put("componentName", "gcld-gw");
        Environment.ENVIRONMENT.put("version", "1.0.4.0");
    }
}
