package com.reign.gcld.common.component;

import com.reign.gcld.common.message.*;

public class ComponentMessage implements Message
{
    private String componentName;
    private String version;
    
    public ComponentMessage(final String componentName, final String version) {
        this.componentName = componentName;
        this.version = version;
    }
    
    public String getComponentName() {
        return this.componentName;
    }
    
    public String getVersion() {
        return this.version;
    }
}
