package com.reign.framework;

import java.util.*;

public class Environment
{
    public static final Map<String, String> ENVIRONMENT;
    
    static {
        (ENVIRONMENT = new HashMap<String, String>()).put("componentName", "reign-tcp-framework");
        Environment.ENVIRONMENT.put("version", "1.2.3.2");
    }
}
