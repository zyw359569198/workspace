package com.reign.util.file;

import java.io.*;

public class FileUtils
{
    public static final int LENGTH = 32;
    public static final int BIT_LENGTH = 4;
    public static String BREPORT_WINDOWS_PATH;
    public static String BREPORT_LINUX_PATH;
    
    static {
        FileUtils.BREPORT_WINDOWS_PATH = null;
        FileUtils.BREPORT_LINUX_PATH = null;
    }
    
    public static void initPath(final String winPath, final String linuxPath) {
        FileUtils.BREPORT_WINDOWS_PATH = winPath;
        FileUtils.BREPORT_LINUX_PATH = linuxPath;
    }
    
    public static String saveToRandomAccessFile(final byte[] bytes) {
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
    
    public static String getPath() {
        return (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) ? FileUtils.BREPORT_WINDOWS_PATH : FileUtils.BREPORT_LINUX_PATH;
    }
    
    public static byte[] getFightReport(final String id) throws IOException {
        if (id.startsWith("N")) {
            return getFightReportFromRandomAccessFile(id);
        }
        if (id.length() < 6) {
            throw new FileNotFoundException("no such file");
        }
        final String filePath = String.valueOf(getPath()) + File.separator + id.substring(0, 8) + File.separator + id.substring(8);
        final File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("no such file");
        }
        return getBytesFromFile(file);
    }
    
    private static byte[] getBytesFromFile(final File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        final byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        return bytes;
    }
    
    public static byte[] getFightReportFromRandomAccessFile(final String id) {
        return FileReader.getInstance().readFile(id);
    }
}
