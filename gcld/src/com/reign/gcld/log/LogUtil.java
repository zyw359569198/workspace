package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import com.reign.framework.common.*;
import com.reign.gcld.player.common.*;
import org.apache.commons.lang.*;
import com.reign.gcld.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import java.text.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.juben.common.*;

public class LogUtil
{
    private static final Logger errorLog;
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
    public static final String UNIT_FOOD = "food";
    public static final String UNIT_WOOD = "wood";
    public static final String UNIT_IRON = "iron";
    public static final String UNIT_EXP = "exp";
    public static final String UNIT_GEM = "gem";
    public static final String UNIT_MOONCAKE = "moonCake";
    public static final String UNIT_BAOMA = "bmw";
    public static final String UNIT_MEIJIU = "xo";
    public static final String UNIT_SHUHUA = "picasso";
    public static final String UNIT_IRONTICKET = "ironTicket";
    public static final String UNIT_GIFTBOX = "giftBox";
    public static final String UNIT_BALL = "ball";
    public static final String UNIT_SNOW = "snow";
    public static final String UNIT_GENERAL_EXP = "gExp";
    public static final String UNIT_EXPLOIT = "exploit";
    public static final String UNIT_TICKET = "ticket";
    public static final String UNIT_TOKEN = "token";
    public static final String UNIT_TOUZI_DOUBLE_TICKET = "touzi_double_ticket";
    public static final String UNIT_FREECONS = "freecons";
    public static final String UNIT_FREEPHANTOM = "freephantom";
    public static final String UNIT_FREE_GOLD_QUENCHING = "freegoldquenching";
    public static final String UNIT_BMW = "bmw";
    public static final String UNIT_XO = "xo";
    public static final String UNIT_PICASSO = "picasso";
    public static final String UNIT_BAOZHU = "baozhu";
    public static final String UNIT_FEATBOX = "featbox";
    public static final String UNIT_HGCOPPER = "hgcopper";
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
    public static final String UNIT_SEARCH_TOKEN = "stoken";
    public static final String UNIT_TF = "tf";
    public static final String UNIT_PAY_POINT = "paypoint";
    public static final String UNIT_HONOR = "honor";
    public static final String UNIT_STATE = "state";
    public static final String UNIT_SUCCESS = "success";
    public static final String ERROR = "ERROR";
    public static final String BLOCK = "BLOCK";
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
    public static final String ITEMS_SJWP = "sjwp";
    public static final String BUY = "\u4e70\u5165";
    public static final String SELL = "\u5356\u51fa";
    public static final String USE = "\u4f7f\u7528";
    public static final String POLISH = "\u6253\u78e8";
    public static final String XIANGQIAN = "\u88c5\u5907\u9576\u5d4c";
    public static final String JSJINJIE = "\u6676\u77f3\u8fdb\u9636";
    public static final String BUY_BACK = "\u4e70\u56de";
    public static final String GET_NEW = "\u83b7\u5f97";
    public static final String GET_NEW_TO_BACK_LIST = "\u83b7\u5f97\u5e76\u8fdb\u5165\u56de\u8d2d";
    public static final String UPGRADE = "\u5347\u7ea7";
    public static final String GEM = "bs";
    public static final String TREASURE = "bw";
    public static final String GOLD_STATISTICS = "\u91d1\u5e01\u7edf\u8ba1";
    public static final String GENERAL_TREASURE = "yb";
    public static final String CODE = "rb";
    public static final String UNBLOCK = "ul";
    
    static {
        errorLog = CommonLog.getLog(LogUtil.class);
    }
    
    public static String formatInterfaceLog(final Integer playerId, final String playerName, final Integer playerLv, final String userId, final String yx, final String ip, final String actionName, final String methodName, final Map<String, String[]> parameterMap, byte[] param, final long time, final boolean block, final boolean exception, final boolean firstLogin, final String yxSource, final int forceId, final int vip, final ServerProtocol protocol, final String threadLogInfo, final PlatForm platForm) {
        final StringBuilder builder = new StringBuilder();
        builder.append(yx).append("#").append("i").append("#").append(userId).append("#").append(playerId).append("#").append(playerName).append("#");
        builder.append(playerLv).append("#").append(vip).append("#").append(forceId).append("#");
        builder.append(ip).append("#");
        builder.append(actionName).append("#").append(methodName).append("#");
        builder.append(time).append("#");
        builder.append(block ? 1 : 0).append("#");
        if (StringUtils.isNotBlank(threadLogInfo)) {
            builder.append(threadLogInfo).append("#");
        }
        else {
            builder.append(exception ? "ERROR" : "").append("#");
        }
        builder.append(firstLogin ? 1 : 0).append("#");
        builder.append(yxSource).append("#");
        if (parameterMap != null && !parameterMap.isEmpty()) {
            final Set<Map.Entry<String, String[]>> entrySet = parameterMap.entrySet();
            int index = 0;
            for (final Map.Entry<String, String[]> entry : entrySet) {
                if (index != 0) {
                    builder.append(",");
                }
                if (entry.getValue() == null) {
                    continue;
                }
                final String key = entry.getKey();
                String paramComment = entry.getValue()[0];
                paramComment = paramComment.replaceAll(":", "|");
                paramComment = paramComment.replaceAll("#", "|");
                paramComment = paramComment.replaceAll(",", "|");
                paramComment = paramComment.replaceAll("\n", "");
                builder.append(key).append(":").append(paramComment);
                ++index;
            }
        }
        if (param == null) {
            param = "".getBytes();
        }
        builder.append("#").append(new String(param)).append("#");
        builder.append(Environment.getMainVersion()).append("#");
        builder.append(protocol + getPlatForm(parameterMap, param, platForm).getValue());
        return builder.toString();
    }
    
    public static String formatPlayerInfoLog(final Integer playerId, final String playerName, final Integer playerLv, final String userId, final String yx, final String unit, final double num, final String sign, final Object itemName, final int forceId, final int vip) {
        final StringBuilder builder = new StringBuilder();
        builder.append(yx).append("#").append("a").append("#").append(userId).append("#").append(playerId).append("#").append(playerName).append("#");
        builder.append(playerLv).append("#").append(vip).append("#").append(forceId).append("#");
        builder.append(sign).append("#").append(unit).append("#").append((long)num).append("#").append(itemName);
        builder.append("#").append(Environment.getMainVersion()).append("#");
        final PlayerDto playerDto = Players.getPlayer(playerId);
        builder.append(getPlatForm(null, null, (playerDto == null) ? null : playerDto.platForm).getValue());
        return builder.toString();
    }
    
    public static String formatReportLoginLog(final Integer playerId, final String playerName, final Integer playerLv, final String userId, final String yx, final String action, final boolean firstLogin, final String yxSource, final int forceId, final int vip, final PlayerDto playerDto) {
        final StringBuilder builder = new StringBuilder();
        builder.append(yx).append("#").append(action).append("#").append(userId).append("#").append(playerId).append("#").append(playerName).append("#");
        builder.append(playerLv).append("#").append(vip).append("#").append(forceId).append("#").append(yxSource);
        builder.append("#").append(Environment.getMainVersion()).append("#");
        builder.append(getPlatForm(null, null, (playerDto == null) ? null : playerDto.platForm).getValue());
        return builder.toString();
    }
    
    public static String formatReportCreateLog(final Integer playerId, final String playerName, final Integer playerLv, final String userId, final String yx, final String action, final int roleIndex, final int forceId, final int vip, final String yxSource, final Request request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(yx).append("#").append(action).append("#").append(userId).append("#").append(playerId).append("#").append(playerName).append("#");
        builder.append(playerLv).append("#").append(vip).append("#").append(forceId).append("#");
        builder.append(roleIndex).append("#").append(yxSource);
        builder.append("#").append(Environment.getMainVersion()).append("#");
        final PlayerDto dto = Players.getPlayer(playerId);
        if (request == null) {
            builder.append(getPlatForm(null, null, (dto == null) ? null : dto.platForm).getValue());
        }
        else {
            builder.append(getPlatForm(request.getParamterMap(), request.getContent(), (dto == null) ? null : dto.platForm).getValue());
        }
        return builder.toString();
    }
    
    public static String formatPayLog(final Integer playerId, final String playerName, final Integer playerLv, final String yx, final String userId, final int gold, final String orderId, final int payType, final int forceId, final int vip, final String yxSource, final Request request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(yx).append("#").append("p").append("#").append(userId).append("#").append(playerId).append("#").append(playerName).append("#");
        builder.append(playerLv).append("#").append(vip).append("#").append(forceId).append("#");
        builder.append(orderId).append("#");
        builder.append(payType).append("#");
        builder.append(gold).append("#");
        builder.append(yxSource).append("#");
        builder.append(Environment.getMainVersion()).append("#");
        final PlayerDto dto = Players.getPlayer(playerId);
        builder.append(getPlatForm(request.getParamterMap(), request.getContent(), (dto == null) ? null : dto.platForm).getValue());
        return builder.toString();
    }
    
    public static String formatOnlineLog(final String yx, final int num, final int numP, final int numI, final int numA) {
        final StringBuilder builder = new StringBuilder();
        builder.append(yx).append("#").append("ol").append("#").append(num).append("#");
        builder.append(numP).append("#").append(numI).append("#");
        builder.append(numA).append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatGoldStatisticsLog(final String unit, final int gold) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String time = sdf.format(new Date());
        final StringBuilder builder = new StringBuilder();
        builder.append("gcld").append("#").append("\u91d1\u5e01\u7edf\u8ba1").append("#").append(time).append("#").append(unit).append("#").append(gold);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatWeaponLog(final Player player, final String sign, final String action, final ArmsWeapon weapon, final PlayerWeapon playerWeapon, final int cost, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("w").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#");
        builder.append(player.getPlayerLv()).append("#").append(player.getConsumeLv()).append("#").append(player.getForceId()).append("#");
        builder.append(sign).append("#").append(cost).append("#");
        builder.append(weapon.getType()).append("#").append(weapon.getId()).append("#").append(weapon.getName()).append("#");
        builder.append(playerWeapon.getLv()).append("#").append(reason);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatEquipLog(final Player player, final String sign, final String action, final boolean success, final Equip equip, final StoreHouse storeHouse, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("e").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#");
        builder.append(player.getPlayerLv()).append("#").append(player.getConsumeLv()).append("#").append(player.getForceId()).append("#");
        builder.append(sign).append("#");
        builder.append(equip.getType()).append("#").append(equip.getQuality()).append("#").append(equip.getId()).append("#").append(equip.getName()).append("#").append(storeHouse.getLv()).append("#").append(reason);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatEquipLog2(final Player player, final String sign, final String action, final boolean success, final Equip equip, final StoreHouseSell storeHouseSell, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("e").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#");
        builder.append(player.getPlayerLv()).append("#").append(player.getConsumeLv()).append("#").append(player.getForceId()).append("#");
        builder.append(sign).append("#");
        builder.append(equip.getType()).append("#").append(equip.getQuality()).append("#").append(equip.getId()).append("#").append(equip.getName()).append("#").append(storeHouseSell.getLv()).append("#").append(reason);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatGemLog(final Player player, final String sign, final String action, final boolean success, final ArmsGem armsgem, final int num, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("bs").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#");
        builder.append(player.getPlayerLv()).append("#").append(player.getConsumeLv()).append("#").append(player.getForceId()).append("#");
        builder.append(sign).append("#");
        builder.append(armsgem.getId()).append("#").append(armsgem.getGemLv()).append("#").append(armsgem.getName()).append("#").append(num).append("#").append(reason);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatTreasureLog(final Player player, final String sign, final String action, final Treasure treasure, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("bw").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#");
        builder.append(player.getPlayerLv()).append("#").append(player.getConsumeLv()).append("#").append(player.getForceId()).append("#");
        builder.append(sign).append("#");
        builder.append(treasure.getId()).append("#").append(treasure.getName()).append("#").append(reason);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatGeneralTreasureLog(final Player player, final String sign, final String action, final GeneralTreasure generalTreasure, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("yb").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#");
        builder.append(player.getPlayerLv()).append("#").append(player.getConsumeLv()).append("#").append(player.getForceId()).append("#");
        builder.append(sign).append("#");
        builder.append(generalTreasure.getId()).append("#").append(generalTreasure.getName()).append("#").append(reason);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatThreadLog(final String className, final String methodName, final int startOrEnd, final long executeTime, final String params) {
        final StringBuilder builder = new StringBuilder();
        builder.append("#").append(className).append("#").append(methodName).append("#").append(startOrEnd).append("#").append(executeTime).append("#").append(params).append("#");
        return builder.toString();
    }
    
    public static String formatItemsLog(final Player player, final String sign, final String action, final boolean success, final Items item, final StoreHouse storeHouse, final int num, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("sjwp").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#");
        builder.append(player.getPlayerLv()).append("#").append(player.getConsumeLv()).append("#").append(player.getForceId()).append("#");
        builder.append(sign).append("#");
        builder.append(item.getType()).append("#").append(item.getQuality()).append("#").append(item.getId()).append("#").append(item.getName()).append("#");
        builder.append(num).append("#");
        builder.append(storeHouse.getLv()).append("#").append(reason);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatItemsLog2(final Player player, final String sign, final String action, final boolean success, final Items item, final StoreHouseSell storeHouseSell, final int num, final String reason) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("sjwp").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#");
        builder.append(player.getPlayerLv()).append("#").append(player.getConsumeLv()).append("#").append(player.getForceId()).append("#");
        builder.append(sign).append("#");
        builder.append(item.getType()).append("#").append(item.getQuality()).append("#").append(item.getId()).append("#").append(item.getName()).append("#").append(storeHouseSell.getLv()).append("#");
        builder.append(num).append("#");
        builder.append(reason);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatValidateCode(final Player player) {
        final StringBuilder builder = new StringBuilder();
        builder.append(player.getYx()).append("#").append("rb").append("#").append(player.getUserId()).append("#").append(player.getPlayerId()).append("#").append(player.getPlayerName()).append("#").append(player.getPlayerLv()).append("#").append(player.getConsumeLv());
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static String formatUnBlock(final PlayerDto playerDto) {
        final StringBuilder builder = new StringBuilder();
        builder.append(playerDto.yx).append("#").append("ul").append("#").append(playerDto.userId).append("#").append(playerDto.playerId).append("#").append(playerDto.playerName).append("#").append(playerDto.playerLv).append("#").append(playerDto.consumeLv);
        builder.append("#").append(Environment.getMainVersion());
        return builder.toString();
    }
    
    public static PlatForm getPlatForm(final Map<String, String[]> parameterMap, final byte[] param, final PlatForm platForm) {
        try {
            if (platForm != null) {
                return platForm;
            }
            if (parameterMap != null) {
                final String[] pfs = parameterMap.get("platform");
                if (pfs != null && pfs.length > 0) {
                    final String platform = pfs[0];
                    if (StringUtils.isNotBlank(platform)) {
                        return PlatForm.valueOf(platform);
                    }
                }
            }
            return PlatForm.PC;
        }
        catch (Exception e) {
            LogUtil.errorLog.error("class:LogUtil#method:getPlatForm#", e);
            return PlatForm.PC;
        }
    }
    
    public static void main(final String[] args) {
        final Map<String, String[]> paramMap = new HashMap<String, String[]>();
        paramMap.put("platform", new String[] { "PC" });
        System.out.println(getPlatForm(paramMap, null, null));
    }
    
    public static String formatTimerLog(final String className, final String methodName, final long executeTime) {
        final StringBuilder builder = new StringBuilder();
        builder.append("#").append("TimerExeTimeAtZeroClock").append("#").append(className).append("#").append(methodName).append("#").append(executeTime);
        return builder.toString();
    }
    
    public static String formatWorldDramaLog(final PlayerDto playerDto, final JuBenDto juBenDto, final int star) {
        final StringBuilder builder = new StringBuilder();
        builder.append("worldDramaLog").append("#").append(playerDto.playerId).append("#").append(playerDto.forceId).append("#").append(playerDto.playerLv).append("#").append(juBenDto.juBen_id).append("#").append(juBenDto.grade).append("#").append(star).append("#");
        return builder.toString();
    }
    
    public static String formatNewUserCreateLog(final String yx, final String userId, final Integer playerId, final String serverNameServeridPlayerid, final String mainVersion) {
        final StringBuilder builder = new StringBuilder();
        builder.append(yx).append("#").append("pt").append("#").append(userId).append("#").append(playerId).append("#").append(serverNameServeridPlayerid).append("#").append(mainVersion);
        return builder.toString();
    }
}
