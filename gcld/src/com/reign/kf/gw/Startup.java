package com.reign.kf.gw;

import com.reign.framework.*;
import com.reign.kf.gw.common.*;

public class Startup
{
    public void startup() throws InstantiationException, IllegalAccessException {
        this.setPropertiesPath();
        final ServletBootstrapNoHttp bootstrap = new ServletBootstrapNoHttp();
        try {
            bootstrap.startup();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setPropertiesPath() {
        final String token = "${start.home}";
        String path = "${start.home}/apps/";
        int i = -1;
        while ((i = path.indexOf(token)) >= 0) {
            if (i > 0) {
                path = String.valueOf(path.substring(0, i)) + System.getProperty("start.home") + path.substring(i + token.length());
            }
            else {
                path = String.valueOf(System.getProperty("start.home")) + path.substring(token.length());
            }
        }
        ListenerConstants.WEB_PATH = path;
    }
}
