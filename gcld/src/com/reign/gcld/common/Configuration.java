package com.reign.gcld.common;

import java.util.regex.*;
import com.reign.util.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;
import com.reign.framework.exception.*;
import com.reign.gcld.common.util.characterFilter.*;
import com.reign.gcld.common.util.*;
import java.io.*;
import java.util.*;

public final class Configuration
{
    private static final Logger log;
    public static final String GAME_URL = "gcld.game.url";
    public static final String UNPRELOGIN_REDIRECT_URL = "gcld.unprelogin.redirect.url";
    public static final String UNLOGIN_REDIRECT_URL = "gcld.unlogin.redirect.url";
    public static final String PAY_URL = "gcld.pay.url";
    public static final String YX_QUERY_KEY = "gcld.query.key";
    public static final String YX_LOGIN_KEY = "gcld.login.key";
    public static final String YX_PAY_KEY = "gcld.pay.key";
    public static final String YX_NEED_ANTI_ADDICTION = "gcld.anti.addiction";
    public static final String ANTI_ADDICTION_URL = "gcld.addiction.url";
    public static final String REGISTER_USER = "gcld.register.user";
    public static final String USE_GM_COMMOND = "gcld.use.gm.commond";
    public static final String SYS_TOP_PLAYERLV = "gcld.sys.player.lv";
    public static final String QUICK_BATTLE_MODE = "gcld.battle.quick";
    public static final String QUICK_BATTLE_MODE_INTERVAL = "gcld.battle.quick.interval";
    public static final String BATTLE_REPORT_SAVE = "gcld.battle.report.save";
    public static final String BATTLE_CACULATE_DEBUG = "gcld.battle.caculate.debug";
    public static final String BATTLE_TACTIC_KILL = "gcld.battle.tactic.kill";
    public static final String BATTLE_SYS_NPC = "gcld.battle.sysNpc";
    public static final String PAY_ACTIVITY_START_TIME = "gcld.pay.activity.startTime";
    public static final String PAY_ACTIVITY_END_TIME = "gcld.pay.activity.endTime";
    public static final String PAY_ACTIVITY_RULE = "gcld.pay.activity.rule";
    public static final String PLAYER_MACHINE_LOGIN_LIMIT = "gcld.player.machine.login.limit";
    public static final String PLAYER_TEST_PLAYERFORM = "gcld.player.testPlatForm";
    public static final String SDATA_URL = "gcld.sdata.url";
    public static final String SERVER_ID = "gcld.serverid";
    public static final String SERVER_ID_S = "gcld.serverids";
    public static final String SERVER_NAME = "gcld.servername";
    public static final String SHOW_SERVER_NAME = "gcld.showservername";
    public static final String YX_FLAG = "gcld.yx";
    public static final String TRACE_LEN = "gcld.tracelen";
    public static final String LOCALE = "gcld.locale";
    public static final String BREPORT_WINDOWS_PATH = "gcld.report.windows.path";
    public static final String BREPORT_LINUX_PATH = "gcld.report.linux.path";
    public static final String SERVER_TIME = "gcld.server.time";
    public static final String LEAGUE_INFO = "gcld.nation.leagueInfo";
    public static final String CITY_NUM_WEI = "gcld.nation.cityInfo.wei";
    public static final String CITY_NUM_SHU = "gcld.nation.cityInfo.shu";
    public static final String CITY_NUM_WU = "gcld.nation.cityInfo.wu";
    public static final String CITY_ATT_WEI = "gcld.nation.attInfo.wei";
    public static final String CITY_ATT_SHU = "gcld.nation.attInfo.shu";
    public static final String CITY_ATT_WU = "gcld.nation.attInfo.wu";
    public static final String LEAGUE_OPEN_WEI = "gcld.open.league.wei";
    public static final String LEAGUE_OPEN_SHU = "gcld.open.league.shu";
    public static final String LEAGUE_OPEN_WU = "gcld.open.league.wu";
    public static final String PLAYER_NAME_LENGTH = "gcld.playername.len";
    public static final String CHARACTER_PATTERN = "gcld.character.pattern";
    public static final String CHAT_PATTERN = "gcld.chat.pattern";
    public static final String CHAT_LEN = "gcld.chat.len";
    public static final String LEGION_NAME_LEN = "gcld.legion.name.len";
    public static final String LEGION_DECLARATION_LEN = "gcld.legion.declaration.len";
    public static final String PLAYER_SYSGOLD = "gcld.player.sysgold";
    public static final String PLAYER_COPPER = "gcld.player.copper";
    public static final String PLAYER_WOOD = "gcld.player.wood";
    public static final String PLAYER_FOOD = "gcld.player.food";
    public static final String PLAYER_IRON = "gcld.player.iron";
    public static final String PLAYER_EXPLOIT = "gcld.player.exploit";
    public static final String PLAYER_LEVEL = "gcld.player.level";
    public static final String PLAYER_CONSUMELV = "gcld.player.consumeLv";
    public static final String PLAYER_MAX_ROLE_NUM = "gcld.player.maxRoleNum";
    public static final String GIFT_IP = "gcld.gift.ip";
    public static final String GIFT_PORT = "gcld.gift.port";
    public static final String GIFT_KEY = "gcld.gift.key";
    public static final String BACKSTAGE_IP = "gcld.backstage.ip";
    public static final String YX_IP = "gcld.yx.ip";
    public static final String LIMIT_YX_IP = "gcld.limit.yx.ip";
    public static final String GIFTINFO_ID_LIST = "gcld.giftinfo.id.list";
    public static final String USER_REWARD_OPEN = "gcld.old.user.reward.open";
    public static final String CHAT_HISTORY_MAX_COUNT = "gcld.chat.history.maxCount";
    public static final String CHAT_HISTORY_EXPIRE_TIME = "gcld.chat.history.expireTime";
    public static final String CHAT_MAX_REPEATED_TIMES = "gcld.chat.maxRepeatedTimes";
    public static final String CHAT_MIN_SIM_POINT = "gcld.chat.minSimPoint";
    public static final String CHAT_CHECK_REPEAT = "gcld.chat.repeatCheck";
    public static String KF_CHECK_CONNECT_INTERVAL;
    public static String KF_RECONNECT_INTERVAL;
    public static String GW_HOST;
    public static String GW_PORT;
    public static String SERVER_KEY;
    public static String GCLD_PLAYERNAME_STOPWORDS_EXTENDS;
    public static final String SERVER_NAME_CHINESE = "gcld.serverchinesename";
    public static final String MOBILE_APP_PAY_TESTING_URL = "gcld.mobile.app.pay.testing.url";
    public static final String MOBILE_APP_PAY_URL = "gcld.mobile.app.pay.url";
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
    public static final String RENREN_CODE = "gcld.renren.code";
    public static final String RENREN_SECRET = "gcld.renren.secret";
    public static final String RENREN_GAME_NAME = "gcld.renren.game.name";
    public static final String RENREN_QUERY_URL = "gcld.renren.query.url";
    public static final String RENREN_PAY_URL = "gcld.renren.pay.url";
    public static final String RENRENDOU_PAY_URL = "gcld.renrendou.pay.url";
    public static final String RENREN_LOGIN_URL = "gcld.renren.login.url";
    public static final String RENREN_PAY_CHECK_URL = "gcld.renren.pay.check.url";
    public static final String RENREN_YX_RENREN_START_PAY = "gcld.renren.yx.renren.start.pay";
    public static final String RENREN_NOTIFY_URL = "gcld.renren.notify.url";
    public static final String RENREN_PAY_SERVICE_URL = "gcld.renren.pay.service.url";
    public static final String RENREN_LOGIN_LIMT = "gcld.renren.login.limt";
    public static final String KAIXIN001_LOGIN_URL = "gcld.kaixin001.login.url";
    public static final String KAIXIN001_QUERY_URL = "gcld.kaixin001.query.url";
    public static final String KAIXIN001_PAY_URL = "gcld.kaixin001.pay.url";
    public static final String KAIXIN001_PAY_KAIXIN001_URL = "gcld.kaixin001.pay.kaixin001.url";
    public static final String KAIXIN001_GAME_AID = "gcld.kaixin001.game.aid";
    public static final String KAIXIN001_GAME_VENDOR = "gcld.kaixin001.game.vendor";
    public static final String KAIXIN001_GAME_APPNAME = "gcld.kaixin001.game.appname";
    public static final String KAIXIN001_GAME_GOODS = "gcld.kaixin001.game.goods";
    public static final String KAIXIN001_SECRET = "gcld.kaixin001.secret";
    public static final String KAIXIN001_PAY_HTML = "gcld.kaixin001.pay.html";
    public static final String XUNLEI_AUTH_SERVER_HOST1 = "gcld.xunlei.authServer.host1";
    public static final String XUNLEI_AUTH_SERVER_PORT1 = "gcld.xunlei.authServer.port1";
    public static final String XUNLEI_AUTH_SERVER_HOST2 = "gcld.xunlei.authServer.host2";
    public static final String XUNLEI_AUTH_SERVER_PORT2 = "gcld.xunlei.authServer.port2";
    public static final String XUNLEI_KEY_BYTES = "gcld.xunlei.keybytes";
    public static final String XUNLEI_GAME_ID = "gcld.xunlei.gameid";
    public static final String XUNLEI_VERSION = "gcld.xunlei.version";
    public static final String XUNLEI_CMD = "gcld.xunlei.cmd";
    public static final String XUNLEI_AESKEY = "gcld.xunlei.aeskey";
    public static final String XUNLEI_LOGIN_URL = "gcld.game.login.url";
    public static final String SOGOU_SECRET = "gcld.sogou.secret";
    public static final String CMWEBGAME_API_URL = "cmwebgame_api_url";
    public static final String CMWEBGAME_SERVER_URL = "cmwebgame_server_url";
    public static final String CMWEBGAME_KEY = "cmwebgame_key";
    public static final String CALLBACK = "gcld.callback";
    public static final String YX_360_PUSH_PLAYER_INFO_URL = "gcld.360.pushPlayerInfo.url";
    public static final String YX_360_PRIVILEGE_CHECK_URL = "gcld.privilege.check.url";
    public static final String YX_360_PRIVILEGE_PAGE_URL = "gcld.privilege.page.url";
    public static final String YX_360_PRIVILEGE_AID = "gcld.privilege.aid";
    public static final String YX_360_PRIVILEGE_GKEY = "gcld.privilege.gkey";
    public static final String YX_360_PRIVILEGE_TYPE = "gcld.privilege.type";
    public static final String YX_360_PRIVILEGE_PRIVIKEY = "gcld.privilege.privikey";
    public static final String YX_360_PRIVILEGE_START_TIME = "gcld.privilege.start.time";
    public static final String YX_360_PRIVILEGE_END_TIME = "gcld.privilege.end.time";
    public static final String YX_JD_REDIRECT_URL = "gcld.jd.redirect.url";
    public static final String YX_JD_HEFU_LIST = "gcld.jd.hefu.list";
    public static final String YX_SINA_REDIRECT_URL = "gcld.sina.redirect.url";
    public static final String YX_SINA_HEFU_LIST = "gcld.sina.hefu.list";
    public static final String YX_SINA_RECEIPT_VERIFICATION_URL = "yx.sina.receipt.verification.url";
    public static final String YX_SINA_IDENTIFIER = "yx.sina.identifier";
    public static final String YX_TAOBAO_COOPID = "yx.taobao.coopId";
    public static final String YX_TAOBAO_CARDID = "yx.taobao.cardId";
    public static final String YX_TAOBAO_REDIRECT_URL = "gcld.taobao.redirect.url";
    public static final String CODE_WRONG_LIMIT = "gcld.code.wrong.limit";
    public static final String VALIDATE_CODE_INVALID_TIME = "gcld.validate.code.invalid.time";
    public static final String TENCENT_APP_ID = "gcld.tencent.app.id";
    public static final String TENCENT_APP_NAME = "gcld.tencent.app.name";
    public static final String TENCENT_APP_KEY = "gcld.tencent.app.key";
    public static final String TENCENT_YUN_URL = "gcld.tencent.yun.url";
    public static final String TENCENT_USER_INFO_URI = "gcld.tencent.userinfo.uri";
    public static final String TENCENT_PAY_BUY_GOODS_URI = "gcld.tencent.pay.buy.goods";
    public static final String TENCENT_PAY_CONFIRM_DELIVERY_URI = "gcld.tencent.pay.confirm.delivery";
    public static final String TENCENT_GOODS_META = "gcld.tencent.pay.goodsmeta";
    public static final String TENCENT_REDIRECT_URL = "gcld.tencent.pay.redirect.url";
    public static final String TENCENT_HOST_ID = "gcld.host.id";
    public static final String YX_5211GAME_APP_ID = "yx.5211game.app.id";
    public static final String YX_5211GAME_LOGIN_URL = "yx.5211game.login.url";
    public static final String YX_5211GAME_ACCESS_URL = "yx.5211game.access.url";
    public static final String YX_5211GAME_CALL_BACK = "yx.5211game.callback";
    public static final String YX_5211GAME_LV_IDS = "yx.5211game.lv.ids";
    public static final String YX_TOKEN_URL = "yx.pingan.token.url";
    public static final String YX_REQUEST_URL = "yx.pingan.request.url";
    
    static {
        log = CommonLog.getLog(Configuration.class);
        Configuration.KF_CHECK_CONNECT_INTERVAL = "gcld.kf.check.connect.interval";
        Configuration.KF_RECONNECT_INTERVAL = "gcld.kf.reconnect.interval";
        Configuration.GW_HOST = "gcld.kf.gw.host";
        Configuration.GW_PORT = "gcld.kf.gw.port";
        Configuration.SERVER_KEY = "gcld.serverkey";
        Configuration.GCLD_PLAYERNAME_STOPWORDS_EXTENDS = "gcld.playername.stopwords.extends";
        Configuration.propertyMap = new ConcurrentHashMap<String, ConcurrentMap<String, String>>();
        Configuration.intPropertyMap = new ConcurrentHashMap<String, Integer>();
        Configuration.intKeySet = new HashSet<String>();
        Configuration.patternPropertyMap = new ConcurrentHashMap<String, Pattern>();
        Configuration.patternKeySet = new HashSet<String>();
        Configuration.modifyMap = new HashMap<String, Long>();
        Configuration.fileList = new ArrayList<Tuple<String, String>>();
        Configuration.watch = null;
        FILES = new String[] { "server.properties", "job.properties", "serverstate.properties" };
        setInitKeySet();
        setPatternKeySet();
        reloadAllConfig("common", Configuration.FILES);
        final String yx = getProperty("gcld.yx");
        if (StringUtils.isBlank(yx)) {
            throw new InternalException("error server.properties doesn't have gcld.yx");
        }
        loadYxConfig(yx);
        (Configuration.watch = new FileWatch(10000)).start();
        setServerTime();
        CharacterFilterFactory.getInstance().init();
    }
    
    private static void setPatternKeySet() {
        Configuration.patternKeySet.add("gcld.character.pattern");
        Configuration.patternKeySet.add("gcld.chat.pattern");
    }
    
    private static void loadYxConfig(final String yx) {
        final String[] strs = yx.split(",");
        String[] array;
        for (int length = (array = strs).length, i = 0; i < length; ++i) {
            final String str = array[i];
            Configuration.fileList.add(new Tuple(str.toLowerCase().trim(), String.valueOf(str.trim()) + ".properties"));
        }
        reloadAllConfig(Configuration.fileList);
    }
    
    private static void setInitKeySet() {
        Configuration.intKeySet.add("gcld.playername.len");
        Configuration.intKeySet.add("gcld.player.sysgold");
        Configuration.intKeySet.add("gcld.player.copper");
        Configuration.intKeySet.add("gcld.player.food");
        Configuration.intKeySet.add("gcld.player.wood");
        Configuration.intKeySet.add("gcld.player.iron");
        Configuration.intKeySet.add("gcld.player.exploit");
        Configuration.intKeySet.add("gcld.player.consumeLv");
        Configuration.intKeySet.add("gcld.player.level");
        Configuration.intKeySet.add("gcld.player.maxRoleNum");
        Configuration.intKeySet.add("gcld.chat.len");
        Configuration.intKeySet.add("gcld.legion.name.len");
        Configuration.intKeySet.add("gcld.legion.declaration.len");
        Configuration.intKeySet.add("gcld.gift.port");
        Configuration.intKeySet.add("gcld.use.gm.commond");
        Configuration.intKeySet.add("gcld.register.user");
        Configuration.intKeySet.add("gcld.sys.player.lv");
        Configuration.intKeySet.add("gcld.player.machine.login.limit");
        Configuration.intKeySet.add("gcld.validate.code.invalid.time");
        Configuration.intKeySet.add("gcld.code.wrong.limit");
    }
    
    public static String getProperty(final String key) {
        return Configuration.propertyMap.get("common").get(key);
    }
    
    public static String getProperty(final String yx, final String key) {
        final Map<String, String> map = Configuration.propertyMap.get(yx);
        return (map == null) ? null : map.get(key);
    }
    
    public static int getIntProperty(final String key) {
        return Configuration.intPropertyMap.get(key);
    }
    
    public static Pattern getPatternProperty(final String key) {
        return Configuration.patternPropertyMap.get(key);
    }
    
    private static void setServerTime() {
        final String serverTime = getProperty("gcld.server.time");
        long time = 0L;
        if (serverTime == null || StringUtils.isBlank(serverTime)) {
            time = System.currentTimeMillis();
            TimeUtil.init(time);
            saveProperties("gcld.server.time", String.valueOf(time), "serverstate.properties");
        }
        else {
            try {
                time = Long.valueOf(serverTime);
            }
            catch (NumberFormatException e) {
                time = System.currentTimeMillis();
                saveProperties("gcld.server.time", String.valueOf(time), "serverstate.properties");
            }
            TimeUtil.init(time);
        }
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
        Label_0369: {
            try {
                final Map<String, String> srcMap = Configuration.propertyMap.get(flag);
                final String traceLen = (srcMap == null) ? null : srcMap.get("gcld.tracelen");
                final ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();
                fis = new FileInputStream(file);
                prop.load(fis);
                final Set<Map.Entry<Object, Object>> entrySet = prop.entrySet();
                for (final Map.Entry<Object, Object> entry : entrySet) {
                    map.put(entry.getKey(), entry.getValue());
                    checkKey(entry.getKey(), entry.getValue());
                }
                if ((traceLen == null && map.get("gcld.tracelen") != null) || (traceLen != null && !traceLen.equalsIgnoreCase(map.get("gcld.tracelen")))) {
                    CommonLog.changeLines(map.get("gcld.tracelen"));
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
                    Configuration.log.error("Configuration", e);
                }
                reloadAllConfig("common", Configuration.FILES);
                reloadAllConfig(Configuration.fileList);
            }
        }
    }
}
