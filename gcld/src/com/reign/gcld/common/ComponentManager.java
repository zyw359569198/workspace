package com.reign.gcld.common;

import com.reign.gcld.common.component.*;
import java.util.*;

public class ComponentManager
{
    private static final ComponentManager instance;
    private List<ComponentMessage> componentList;
    
    static {
        instance = new ComponentManager();
    }
    
    private ComponentManager() {
        this.componentList = new ArrayList<ComponentMessage>();
    }
    
    public static ComponentManager getInstance() {
        return ComponentManager.instance;
    }
    
    public void addComponent(final ComponentMessage message) {
        this.componentList.add(message);
    }
    
    public List<ComponentMessage> getAllComponent() {
        return this.componentList;
    }
}
