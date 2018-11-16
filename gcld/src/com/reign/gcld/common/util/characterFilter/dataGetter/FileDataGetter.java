package com.reign.gcld.common.util.characterFilter.dataGetter;

import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import java.io.*;

public class FileDataGetter implements IDataGetter
{
    Logger logger;
    private String fileName;
    private boolean useDefaultPath;
    
    public FileDataGetter(final String fileName) {
        this.logger = CommonLog.getLog(FileDataGetter.class);
        this.fileName = fileName;
        this.useDefaultPath = true;
    }
    
    public FileDataGetter(final String fileName, final boolean useDefaultPath) {
        this.logger = CommonLog.getLog(FileDataGetter.class);
        this.fileName = fileName;
        this.useDefaultPath = useDefaultPath;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public BufferedReader getData() {
        String fn = this.fileName;
        if (this.useDefaultPath) {
            fn = String.valueOf(ListenerConstants.WEB_PATH) + fn;
        }
        final File file = new File(fn);
        BufferedReader br = null;
        try {
            final FileInputStream in = new FileInputStream(file);
            final InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
            br = new BufferedReader(inReader);
        }
        catch (FileNotFoundException e) {
            this.logger.error("\u4f60\u60f3\u52a0\u8f7d\u7684\u6587\u4ef6\u6ca1\u6709\u627e\u5230!WebPath=" + ListenerConstants.WEB_PATH + ",FileName=" + this.fileName, e);
        }
        catch (UnsupportedEncodingException e2) {
            this.logger.error("\u4f60\u6307\u5b9a\u7684\u7f16\u7801\u7c7b\u578b\u4e0d\u652f\u6301!", e2);
        }
        return br;
    }
}
