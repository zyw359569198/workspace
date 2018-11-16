package com.reign.framework.netty.mvc.result;

import org.apache.commons.logging.*;
import com.reign.framework.netty.mvc.freemarker.*;
import java.io.*;
import freemarker.template.*;
import java.util.*;

public class FreeMarkerResult implements Result<String>
{
    private static final Log log;
    private Map<String, Object> root;
    private String templateFileName;
    
    static {
        log = LogFactory.getLog("com.reign.freemaker");
    }
    
    public void put(final String name, final Object value) {
        this.root.put(name, value);
    }
    
    public void process(final OutputStream os) {
        try {
            final Template template = TemplateManager.getInstance().getTemplate(this.templateFileName);
            template.process(this.root, new OutputStreamWriter(os));
        }
        catch (Exception e) {
            FreeMarkerResult.log.error("process error, templateFileName: " + this.templateFileName, e);
            throw new RuntimeException(e);
        }
    }
    
    public FreeMarkerResult(final String templateFileName) {
        this.templateFileName = templateFileName;
        this.root = new HashMap<String, Object>();
    }
    
    @Override
    public String getViewName() {
        return "freemarker";
    }
    
    @Override
    public String getResult() {
        return "";
    }
}
