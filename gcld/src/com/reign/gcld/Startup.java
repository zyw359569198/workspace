package com.reign.gcld;

import com.reign.framework.common.*;
import org.apache.ibatis.io.*;
import com.reign.framework.plugin.*;
import com.reign.framework.netty.mvc.freemarker.*;
import com.reign.gcld.common.*;
import com.reign.framework.*;

public class Startup
{
    public void startup() throws Exception {
        this.setPropertiesPath();
        VFS.addImplClass(MyBatisVFS.class);
        PluginLoader.getInstance().init();
        TemplateManager.getInstance().init(ListenerConstants.WEB_PATH);
        final ServletBootstrap bootstrap = new ServletBootstrap();
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
