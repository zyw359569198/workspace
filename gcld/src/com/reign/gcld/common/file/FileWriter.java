package com.reign.gcld.common.file;

import com.reign.gcld.common.log.*;
import java.io.*;
import java.util.*;
import com.reign.util.*;
import com.reign.gcld.common.*;

public class FileWriter
{
    private static final Logger log;
    private static FileWriter filewriter;
    private RandomAccessFile raf;
    private File file;
    private Date day;
    
    static {
        log = CommonLog.getLog(FileWriter.class);
    }
    
    public static FileWriter getInstance() {
        if (FileWriter.filewriter == null) {
            FileWriter.filewriter = new FileWriter();
        }
        return FileWriter.filewriter;
    }
    
    public synchronized String writeFile(final byte[] bytes) {
        final StringBuilder fid = new StringBuilder("");
        try {
            final RandomAccessFile raf = this.getAccessFile();
            if (raf != null) {
                final long offset = raf.getFilePointer();
                raf.write(ByteUtil.intToBytes(bytes.length, 32));
                raf.write(bytes);
                fid.append(this.file.getName()).append(offset);
            }
        }
        catch (IOException e) {
            FileWriter.log.error("writeFile ", e);
        }
        return fid.toString();
    }
    
    private RandomAccessFile getAccessFile() throws IOException {
        final Calendar cg = Calendar.getInstance();
        if (this.raf == null) {
            final String filePath = String.valueOf(getPath()) + File.separator + DateUtil.formatDate(cg.getTime(), "yyyyMMdd");
            this.file = new File(filePath);
            if (!this.file.exists() && !this.file.getParentFile().exists()) {
                this.file.getParentFile().mkdirs();
            }
            this.raf = new RandomAccessFile(this.file, "rw");
            if (this.file.exists()) {
                this.raf.seek(this.raf.length());
            }
            this.day = cg.getTime();
        }
        if (cg.get(11) == 0 && !isSameDay(this.day, cg.getTime())) {
            this.day = cg.getTime();
            this.raf.close();
            final String filePath = String.valueOf(getPath()) + File.separator + DateUtil.formatDate(cg.getTime(), "yyyyMMdd");
            this.file = new File(filePath);
            if (!this.file.exists() && !this.file.getParentFile().exists()) {
                this.file.getParentFile().mkdirs();
            }
            this.raf = new RandomAccessFile(this.file, "rw");
        }
        return this.raf;
    }
    
    public static String getPath() {
        return (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) ? Configuration.getProperty("gcld.report.windows.path") : Configuration.getProperty("gcld.report.linux.path");
    }
    
    private static boolean isSameDay(final Date date1, final Date date2) {
        final Calendar cg1 = Calendar.getInstance();
        final Calendar cg2 = Calendar.getInstance();
        cg1.setTime(date1);
        cg2.setTime(date2);
        return cg1.get(1) == cg2.get(1) && cg1.get(6) == cg2.get(6);
    }
}
