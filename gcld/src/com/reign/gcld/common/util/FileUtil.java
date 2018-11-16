package com.reign.gcld.common.util;

import java.io.*;
import com.reign.gcld.common.file.*;
import com.reign.gcld.common.*;

public class FileUtil
{
    public static final int LENGTH = 32;
    public static final int BIT_LENGTH = 4;
    
    public static String saveToFile(final byte[] bytes) {
        ByteArrayOutputStream bos = null;
        String result = "";
        try {
            result = FileWriter.getInstance().writeFile(bytes);
        }
        finally {
            if (bos != null) {
                try {
                    bos.close();
                    bos = null;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (bos != null) {
            try {
                bos.close();
                bos = null;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public static byte[] getFightReport(final String id) throws IOException {
        return FileReader.getInstance().readFile(id);
    }
    
    public static String getPath() {
        return (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) ? Configuration.getProperty("gcld.report.windows.path") : Configuration.getProperty("gcld.report.linux.path");
    }
}
