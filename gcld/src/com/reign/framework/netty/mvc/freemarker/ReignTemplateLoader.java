package com.reign.framework.netty.mvc.freemarker;

import freemarker.cache.*;
import java.io.*;

public class ReignTemplateLoader implements TemplateLoader
{
    @Override
	public void closeTemplateSource(final Object obj) throws IOException {
    }
    
    @Override
	public Object findTemplateSource(final String s) throws IOException {
        return null;
    }
    
    @Override
	public long getLastModified(final Object obj) {
        return 0L;
    }
    
    @Override
	public Reader getReader(final Object obj, final String s) throws IOException {
        return null;
    }
}
