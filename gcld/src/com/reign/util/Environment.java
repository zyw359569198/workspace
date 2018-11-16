package com.reign.util;

import java.util.*;

public class Environment
{
    public static final Map<String, String> ENVIRONMENT;
    
    static {
        (ENVIRONMENT = new HashMap<String, String>()).put("componentName", "reign-util");
        Environment.ENVIRONMENT.put("version", "1.1.5.0");
    }
}
