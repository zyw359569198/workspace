package com.reign.framework.netty.mvc.freemarker;

import freemarker.template.*;
import java.io.*;
import freemarker.cache.*;

public class TemplateManager
{
    private static final TemplateManager instance;
    private Configuration configuration;
    private boolean init;
    
    static {
        instance = new TemplateManager();
    }
    
    private TemplateManager() {
        this.init = false;
        (this.configuration = new Configuration()).setDefaultEncoding("utf-8");
    }
    
    public static TemplateManager getInstance() {
        return TemplateManager.instance;
    }
    
    public Template getTemplate(final String templateFileName) throws IOException {
        return this.configuration.getTemplate(templateFileName);
    }
    
    public void init(final String templateDir) throws IOException {
        if (this.init) {
            return;
        }
        final File file = new File(templateDir);
        if (!file.exists() || !file.isDirectory()) {
            throw new RuntimeException("template dir must be a directory, " + templateDir + " is not a valid directory");
        }
        this.configuration.setTemplateLoader(new FileTemplateLoader(new File(templateDir)));
        this.init = true;
    }
}
