package com.reign.kf.gw.common;

import java.util.regex.*;
import com.reign.util.*;
import com.reign.kf.gw.common.log.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.*;

public final class Configuration
{
    private static final Logger log;
    public static final String TRACE_LEN = "gcld.kfgw.tracelen";
    public static final String FETCH_URL = "gcld.fetch.url";
    public static final String CHECK_SCHEDULT_INTERVAL = "gcld.kfgw.checkScheduleInterval";
    public static final String BG_URL = "background.url";
    private static ConcurrentMap<String, ConcurrentMap<String, String>> propertyMap;
    private static Map<String, Integer> intPropertyMap;
    private static Set<String> intKeySet;
    private static Map<String, Pattern> patternPropertyMap;
    private static Set<String> patternKeySet;
    public static Map<String, Long> modifyMap;
    private static final String[] FILES;
    private static List<Tuple<String, String>> fileList;
    private static FileWatch watch;
    public static final String COMMON_FLAG = "common";
    
    static {
        log = CommonLog.getLog(Configuration.class);
        Configuration.propertyMap = new ConcurrentHashMap<String, ConcurrentMap<String, String>>();
        Configuration.intPropertyMap = new ConcurrentHashMap<String, Integer>();
        Configuration.intKeySet = new HashSet<String>();
        Configuration.patternPropertyMap = new ConcurrentHashMap<String, Pattern>();
        Configuration.patternKeySet = new HashSet<String>();
        Configuration.modifyMap = new HashMap<String, Long>();
        Configuration.fileList = new ArrayList<Tuple<String, String>>();
        Configuration.watch = null;
        FILES = new String[] { "server.properties" };
        setInitKeySet();
        setPatternKeySet();
        reloadAllConfig("common", Configuration.FILES);
        (Configuration.watch = new FileWatch(10000)).start();
    }
    
    private static void setPatternKeySet() {
    }
    
    private static void setInitKeySet() {
        Configuration.intKeySet.add("gcld.kfgw.checkScheduleInterval");
    }
    
    public static String getProperty(final String key) {
        return Configuration.propertyMap.get("common").get(key);
    }
    
    public static String getProperty(final String yx, final String key) {
        return Configuration.propertyMap.get(yx).get(key);
    }
    
    public static int getIntProperty(final String key) {
        return Configuration.intPropertyMap.get(key);
    }
    
    public static Pattern getPatternProperty(final String key) {
        return Configuration.patternPropertyMap.get(key);
    }
    
    public static void saveProperties(final String key, final String value, final String fileName) {
        final String path = ListenerConstants.WEB_PATH;
        final Properties prop = new Properties();
        OutputStream fos = null;
        InputStream fis = null;
        try {
            final File file = new File(String.valueOf(path) + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fis = new FileInputStream(file);
            prop.load(fis);
            fos = new FileOutputStream(String.valueOf(path) + fileName);
            prop.setProperty(key, value);
            prop.store(fos, String.valueOf(key) + " modify");
            fos.flush();
            Configuration.propertyMap.get("common").put(key, value);
        }
        catch (FileNotFoundException e) {
            Configuration.log.error("file not found", e);
        }
        catch (IOException e2) {
            Configuration.log.error("io Exception", e2);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e3) {
                    Configuration.log.error("io Exception", e3);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException e3) {
                    Configuration.log.error("io Exception", e3);
                }
            }
        }
        if (fis != null) {
            try {
                fis.close();
            }
            catch (IOException e3) {
                Configuration.log.error("io Exception", e3);
            }
        }
        if (fos != null) {
            try {
                fos.close();
            }
            catch (IOException e3) {
                Configuration.log.error("io Exception", e3);
            }
        }
    }
    
    private static void reloadAllConfig(final List<Tuple<String, String>> fileList) {
        for (final Tuple<String, String> tuple : fileList) {
            reloadAllConfig(tuple.left, tuple.right);
        }
    }
    
    private static void reloadAllConfig(final String flag, final String... files) {
        for (final String file : files) {
            try {
                checkFile(flag, file);
            }
            catch (Throwable t) {
                Configuration.log.error("load file " + file, t);
            }
        }
    }
    
    private static void checkFile(final String flag, final String fileName) {
        final String path = ListenerConstants.WEB_PATH;
        final File file = new File(String.valueOf(path) + fileName);
        if (!file.exists()) {
            return;
        }
        Long lastModifyTime = Configuration.modifyMap.get(fileName);
        if (lastModifyTime == null || file.lastModified() != lastModifyTime) {
            lastModifyTime = file.lastModified();
            Configuration.modifyMap.put(fileName, lastModifyTime);
            reloadConfig(flag, file);
        }
    }
    
    private static void reloadConfig(final String flag, final File file) {
        final Properties prop = new Properties();
        InputStream fis = null;
        Label_0364: {
            try {
                final Map<String, String> srcMap = Configuration.propertyMap.get(flag);
                final String traceLen = (srcMap == null) ? null : srcMap.get("gcld.kfgw.tracelen");
                final ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();
                fis = new FileInputStream(file);
                prop.load(fis);
                final Set<Map.Entry<Object, Object>> entrySet = prop.entrySet();
                for (final Map.Entry<Object, Object> entry : entrySet) {
                    map.put(entry.getKey(), entry.getValue());
                    checkKey(entry.getKey(), entry.getValue());
                }
                if ((traceLen == null && map.get("gcld.kfgw.tracelen") != null) || (traceLen != null && !traceLen.equalsIgnoreCase(map.get("gcld.kfgw.tracelen")))) {
                    CommonLog.changeLines(map.get("gcld.kfgw.tracelen"));
                }
                if (srcMap != null) {
                    srcMap.putAll(map);
                    break Label_0364;
                }
                Configuration.propertyMap.put(flag, map);
            }
            catch (FileNotFoundException e) {
                Configuration.log.error("file not found", e);
            }
            catch (IOException e2) {
                Configuration.log.error("io Exception", e2);
            }
            finally {
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    catch (IOException e3) {
                        Configuration.log.error("io Exception", e3);
                    }
                }
            }
        }
        if (fis != null) {
            try {
                fis.close();
            }
            catch (IOException e3) {
                Configuration.log.error("io Exception", e3);
            }
        }
    }
    
    private static void checkKey(final String key, final String value) {
        if (Configuration.intKeySet.contains(key)) {
            Configuration.intPropertyMap.put(key, Integer.valueOf(value));
        }
        else if (Configuration.patternKeySet.contains(key)) {
            Configuration.patternPropertyMap.put(key, Pattern.compile(value));
        }
    }
    
    public static class FileWatch extends Thread
    {
        public int interval;
        
        public FileWatch(final int interval) {
            this.interval = interval;
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    Thread.sleep(this.interval);
                }
                catch (InterruptedException e) {
                    Configuration.log.error("", e);
                }
                reloadAllConfig("common", Configuration.FILES);
                reloadAllConfig(Configuration.fileList);
            }
        }
    }
}
