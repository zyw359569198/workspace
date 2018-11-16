package com.reign.kf.match.common;

import java.util.regex.*;
import com.reign.util.*;
import com.reign.kf.match.log.*;
import java.util.concurrent.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public final class Configuration
{
    private static final Logger log;
    private static ConcurrentMap<String, ConcurrentMap<String, String>> propertyMap;
    private static Map<String, Integer> intPropertyMap;
    private static Set<String> intKeySet;
    private static Map<String, Pattern> patternPropertyMap;
    private static Set<String> patternKeySet;
    public static Map<String, Long> modifyMap;
    private static final String[] FILES;
    private static List<Tuple<String, String>> fileList;
    private static FileWatch watch;
    public static final String SERVER_STATE_FILE = "serverstate.properties";
    public static final String COMMON_FLAG = "common";
    public static volatile boolean init;
    public static final String TRACE_LEN = "gcld.kfmatch.tracelen";
    public static final String SYNC_SEASON_INTERVAL = "gcld.kfmatch.syncSeasonInterval";
    public static final String MATCH_INTERVAL = "gcld.kfmatch.matchInterval";
    public static final String STEP_INTERVAL = "gcld.kfmatch.stepInterval";
    public static final String STEP_MATCHNUM = "gcld.kfmatch.stepMatchNum";
    public static final String PREPARE_MATCHSEC = "gcld.kfmatch.prepareMatchSec";
    public static final String GW_HOST = "gcld.match.gwHost";
    public static final String GW_PORT = "gcld.match.gwPort";
    public static final String GW_COMMAND = "gcld.match.gwCommand";
    public static final String IDENTITY_ID = "gcld.kfmatch.identityId";
    public static final String KF_CHECK_CONNECT_INTERVAL;
    public static final String KF_RECONNECT_INTERVAL;
    public static final String MATCH_URL = "match.url";
    public static final String MATCH_NAME = "match.name";
    public static final String FORCE_NUM = "match.forceNum";
    public static final String PLAYER_FORCE_NUM = "match.playerForceNum";
    
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
        Configuration.init = false;
        KF_CHECK_CONNECT_INTERVAL = null;
        KF_RECONNECT_INTERVAL = null;
        FILES = new String[] { "server.properties" };
        setInitKeySet();
        setPatternKeySet();
        reloadAllConfig("common", Configuration.FILES);
        Configuration.init = true;
        (Configuration.watch = new FileWatch(10000)).start();
    }
    
    private static void setPatternKeySet() {
    }
    
    private static void setInitKeySet() {
        Configuration.intKeySet.add("gcld.kfmatch.syncSeasonInterval");
        Configuration.intKeySet.add("gcld.kfmatch.matchInterval");
        Configuration.intKeySet.add("gcld.kfmatch.stepInterval");
        Configuration.intKeySet.add("gcld.kfmatch.stepMatchNum");
        Configuration.intKeySet.add("gcld.kfmatch.prepareMatchSec");
        Configuration.intKeySet.add("gcld.match.gwPort");
    }
    
    public static String getProperty(final String key) {
        return Configuration.propertyMap.get("common").get(key);
    }
    
    public static String getProperty(final String yx, final String key) {
        return Configuration.propertyMap.get(yx).get(key);
    }
    
    public static String getProperty(final HttpServletRequest request, final String key) {
        final String host = request.getHeader("X-Real-Server");
        if (host == null) {
            return "-1";
        }
        return getProperty(host.toLowerCase(), key);
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
        reloadConfig(flag, file);
    }
    
    private static void reloadConfig(final String flag, final File file) {
        final Properties prop = new Properties();
        InputStream fis = null;
        Label_0369: {
            try {
                final Map<String, String> srcMap = Configuration.propertyMap.get(flag);
                final String traceLen = (srcMap == null) ? null : srcMap.get("gcld.kfmatch.tracelen");
                final ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();
                fis = new FileInputStream(file);
                prop.load(fis);
                final Set<Map.Entry<Object, Object>> entrySet = prop.entrySet();
                for (final Map.Entry<Object, Object> entry : entrySet) {
                    map.put(entry.getKey(), entry.getValue());
                    checkKey(entry.getKey(), entry.getValue());
                }
                if ((traceLen == null && map.get("gcld.kfmatch.tracelen") != null) || (traceLen != null && !traceLen.equalsIgnoreCase(map.get("gcld.kfmatch.tracelen")))) {
                    CommonLog.changeLines(map.get("gcld.kfmatch.tracelen"));
                }
                if (srcMap != null) {
                    srcMap.putAll(map);
                    break Label_0369;
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
