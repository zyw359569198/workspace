package com.reign.kf.match.log;

import com.reign.kf.match.common.*;
import com.reign.util.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;

public class LogUtil
{
    public static final String TAB = "\t";
    public static final String BLANK = " ";
    public static final String SPLIT = "|";
    public static final String SHARP = "#";
    public static final String COMMA = ",";
    public static final String SIGN_PLUS = "+";
    public static final String SIGN_MINUS = "-";
    public static final String SIGN_EQUAL = "=";
    public static final String PLAYER = "p:";
    public static final String USER = "u:";
    public static final String IP = "ip:";
    public static final String RESULT = "r:";
    public static final String REASON = "re:";
    public static final String PARAMETER = "pa:";
    public static final String TIME = "t:";
    public static final String UNIT_COPPER = "copper";
    public static final String UNIT_HGCOPPER = "hgcopper";
    public static final String UNIT_FOOD = "food";
    public static final String UNIT_GOLD = "gold";
    public static final String UNIT_UGOLD = "ugold";
    public static final String UNIT_GGOLD = "ggold";
    public static final String UNIT_SGOLD = "sgold";
    public static final String UNIT_IGOLD = "igold";
    public static final String UNIT_PDGOLD = "pdgold";
    public static final String UNIT_PWGOLD = "pwgold";
    public static final String UNIT_HGGOLD = "hggold";
    public static final String UNIT_PRESTIGE = "prestige";
    public static final String UNIT_FORCES = "forces";
    public static final String UNIT_JYUNGONG = "jyungong";
    public static final String UNIT_HGJYUNGONG = "hgjyungong";
    public static final String UNIT_TOKEN = "token";
    public static final String UNIT_SEARCH_TOKEN = "stoken";
    public static final String UNIT_TF = "tf";
    public static final String UNIT_PAY_POINT = "paypoint";
    public static final String UNIT_HONOR = "honor";
    public static final String PAY = "p";
    public static final String ONLINE = "ol";
    public static final String LOGIN = "li";
    public static final String LOGINOUT = "lo";
    public static final String CREATE = "c";
    public static final String INTERFACE = "i";
    public static final String GOODS = "g";
    public static final String ATTRIBUTE = "a";
    public static final String WEAPON = "w";
    public static final String EQUIP = "e";
    public static final String HONOR = "h";
    public static final String BUY = "b";
    public static final String SELL = "s";
    public static final String DECREASE = "d";
    public static final String BUY_BACK = "bb";
    public static final String GET_NEW = "n";
    public static final String GET_NEW_TO_BACK_LIST = "nb";
    public static final String UPGRADE = "u";
    public static final String BLOODY_CITY_HOLD = "bch";
    public static final String BLOODY_FIGHT = "bf";
    public static final String LEGION_QUALIFYING = "lq";
    public static final String UPGRADE_OFFICER = "uo";
    public static final String UPGRADE_TITLE = "ut";
    public static final String BUY_SECOND_EFFECT = "b2e";
    public static final String BUY_THIRD_EFFECT = "b3e";
    public static final String BUY_SKILL_TRAIN_BAR = "bstb";
    public static final String GEM = "gem";
    
    public static String formatInterfaceLog(final Request re, final String ip, final int port, final long time) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append("i").append("#").append(ip).append("#").append(port).append("#");
        builder.append(time).append("#");
        final CommandWatch watch = (CommandWatch)ThreadLocalFactory.getThreadLocalObj();
        if (watch != null) {
            builder.append(watch.toString());
            ThreadLocalFactory.setThreadLocalObj((Object)null);
        }
        return builder.toString();
    }
    
    public static String formatInterfaceLog(final Request re, final String ip, final int port, final long time, final Response response) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append("i").append("#").append(ip).append("#").append(port).append("#");
        builder.append("[");
        if (re != null) {
            try {
                builder.append(Types.OBJECT_MAPPER.writeValueAsString(re));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        builder.append("]").append("#");
        builder.append(time).append("#");
        final CommandWatch watch = (CommandWatch)ThreadLocalFactory.getThreadLocalObj();
        if (watch != null) {
            builder.append(watch.toString()).append("#");
            ThreadLocalFactory.setThreadLocalObj((Object)null);
        }
        builder.append("[");
        if (response != null) {
            try {
                builder.append(Types.OBJECT_MAPPER.writeValueAsString(response));
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        builder.append("]");
        return builder.toString();
    }
    
    public static String formatInterfaceLog(final RequestChunk chunk, final String ip, final int port, final long time, final List<Response> responseList) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append("i").append("#").append(ip).append("#");
        builder.append((chunk != null) ? chunk.getMachineId() : "").append("#");
        builder.append("[");
        if (chunk != null) {
            try {
                builder.append(Types.OBJECT_MAPPER.writeValueAsString(chunk));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        builder.append("]").append("#");
        builder.append(time).append("#");
        final CommandWatch watch = (CommandWatch)ThreadLocalFactory.getThreadLocalObj();
        if (watch != null) {
            builder.append(watch.toString()).append("#");
            ThreadLocalFactory.setThreadLocalObj((Object)null);
        }
        builder.append("[");
        if (responseList != null) {
            try {
                builder.append(Types.OBJECT_MAPPER.writeValueAsString(responseList));
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        builder.append("]");
        return builder.toString();
    }
    
    public static String formatInterfaceLog(final RequestChunk chunk, final String ip, final int port, final long time) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append("i").append("#").append(ip).append("#");
        builder.append((chunk != null) ? chunk.getMachineId() : "").append("#").append((chunk != null && chunk.getRequestList() != null) ? chunk.getRequestList().size() : 0).append("#");
        builder.append(time).append("#");
        final CommandWatch watch = (CommandWatch)ThreadLocalFactory.getThreadLocalObj();
        if (watch != null) {
            builder.append(watch.toString());
            ThreadLocalFactory.setThreadLocalObj((Object)null);
        }
        return builder.toString();
    }
    
    public static String formatPlayerInfoLog(final Integer playerId, final String playerName, final Integer playerLv, final String serverName, final String serverId, final String unit, final long num, final String sign, final long id, final int vip, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(playerId).append("#").append(playerName).append("#").append(playerLv).append("#");
        builder.append(serverName).append("#").append(serverId).append("#");
        builder.append(unit).append("#").append(num).append("#").append(sign).append("#").append(id).append("#").append(vip).append("#").append(reason);
        return builder.toString();
    }
}
