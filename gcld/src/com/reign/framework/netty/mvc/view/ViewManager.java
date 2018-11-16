package com.reign.framework.netty.mvc.view;

import java.util.*;

public class ViewManager
{
    private ResponseView defaultView;
    private Map<String, ResponseView> viewMap;
    
    public ViewManager(final ResponseView defaultView) {
        this.viewMap = new HashMap<String, ResponseView>();
        this.defaultView = defaultView;
    }
    
    public ResponseView getView(final String viewName) {
        final ResponseView view = this.viewMap.get(viewName);
        return (view == null) ? this.defaultView : view;
    }
    
    public void addView(final String viewName, final ResponseView view) {
        this.viewMap.put(viewName, view);
    }
}
