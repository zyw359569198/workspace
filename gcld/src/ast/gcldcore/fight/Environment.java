package ast.gcldcore.fight;

import java.util.*;

public class Environment
{
    public static final Map<String, String> ENVIRONMENT;
    
    static {
        (ENVIRONMENT = new HashMap<String, String>()).put("componentName", "gcldcore");
        Environment.ENVIRONMENT.put("version", "1.0.1.8");
    }
}
