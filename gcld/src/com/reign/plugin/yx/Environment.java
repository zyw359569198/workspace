package com.reign.plugin.yx;

import java.util.*;

public class Environment
{
    public static final Map<String, String> ENVIRONMENT;
    
    static {
        (ENVIRONMENT = new HashMap<String, String>()).put("componentName", "reign-yxoperation");
        Environment.ENVIRONMENT.put("version", "1.1.10.0");
    }
}
