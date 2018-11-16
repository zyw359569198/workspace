package com.reign.gcld.pay.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.tickets.dao.*;
import com.reign.plugin.yx.common.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.pay.dao.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.system.util.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.event.util.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.common.event.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import java.util.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.rank.common.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.util.*;
import com.reign.gcld.activity.domain.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.user.dto.*;
import com.reign.gcld.pay.domain.*;
import java.io.*;

@Component("payService")
public class PayService implements IPayService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerPayDao playerPayDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private IActivityDao activityDao;
    @Autowired
    private IPlayerVipDao playerVipDao;
    @Autowired
    private ItemsCache itemsCache;
    @Autowired
    private IPlayerTicketsDao playerTicketsDao;
    @Autowired
    private IYxOperation yxOperation;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private GiftTxCache giftTxCache;
    @Autowired
    private PlayerVipTxDao playerVipTxDao;
    private static final Logger rtLog;
    private static final Logger timerLog;
    private static final Logger opPeport;
    private static final Logger errorLog;
    public static Map<Integer, Integer> PAY_MAP;
    public static final int MAX_CONSUME_LV = 11;
    public static boolean initTicket;
    public static boolean inTicket;
    public static Map<Integer, Tuple<Integer, Integer>> ticketMap;
    
    static {
        rtLog = new RTReportLogger();
        timerLog = new TimerLogger();
        opPeport = new OpReportLogger();
        errorLog = CommonLog.getLog(PayService.class);
        (PayService.PAY_MAP = new HashMap<Integer, Integer>()).put(1, 10);
        PayService.PAY_MAP.put(2, 300);
        PayService.PAY_MAP.put(3, 1000);
        PayService.PAY_MAP.put(4, 2000);
        PayService.PAY_MAP.put(5, 5000);
        PayService.PAY_MAP.put(6, 10000);
        PayService.PAY_MAP.put(7, 20000);
        PayService.PAY_MAP.put(8, 50000);
        PayService.PAY_MAP.put(9, 100000);
        PayService.PAY_MAP.put(10, 200000);
        PayService.PAY_MAP.put(11, 500000);
        PayService.initTicket = false;
        PayService.inTicket = false;
        PayService.ticketMap = new ConcurrentHashMap<Integer, Tuple<Integer, Integer>>();
    }
    
    @Transactional
    @Override
    public byte[] pay(final String orderId, final int playerId, final String userId, final String yx, final int gold, final String yxSource, final Request request) {
        final long start = System.currentTimeMillis();
        final Player player = this.playerDao.read(playerId);
        if (this.playerPayDao.containsOrderId(orderId, yx)) {
            PayService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ORDER_ID_EXIST", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(208)), 208));
            return BackstageUtil.returnError(208);
        }
        if (!this.commonPay(orderId, gold, userId, yx, 0, "\u5145\u503c\u83b7\u5f97\u91d1\u5e01", player, new Date(), yxSource, request)) {
            PayService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_ADD_USER_GOLD_FAIL", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(210)), 210));
            return BackstageUtil.returnError(210);
        }
        PayService.opPeport.info(OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", WebUtil.getIpAddr(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnSuccess()), 1));
        try {
            this.addAdditionalGold(playerId, gold);
        }
        catch (Exception e) {
            PayService.errorLog.error("#pay_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#");
            PayService.errorLog.error("#pay_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#", e);
        }
        try {
            this.addTicketGold(playerId, gold);
        }
        catch (Exception e) {
            PayService.errorLog.error("#pay_ticket_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#");
            PayService.errorLog.error("#pay_ticket_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#", e);
        }
        try {
            EventUtil.handleOperation(playerId, 20, gold);
        }
        catch (Exception e) {
            PayService.errorLog.error("#pay_redpaper_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#");
            PayService.errorLog.error("#pay_redpaper_activity_error#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold + "#", e);
        }
        return BackstageUtil.returnSuccess();
    }
    
    @Transactional
    @Override
    public boolean commonPay(final String orderId, final int gold, final String userId, final String yx, final int payType, final Object attributeKey, Player player, final Date payDate, final String yxSource, final Request request) {
        final int playerId = player.getPlayerId();
        Tuple<Boolean, String> result = new Tuple(true, "");
        final int oldConsumeLev = player.getConsumeLv();
        if (payType == 0) {
            if (!this.playerDao.addUserGold(player, gold, attributeKey)) {
                return false;
            }
        }
        else {
            this.playerDao.addSysGold(player, gold, attributeKey);
        }
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        pa.setPayPoint(pa.getPayPoint() + gold);
        final int lv = this.calculateConsumeLv(pa.getPayPoint());
        int addConusemLv = lv - player.getConsumeLv();
        final int consumeLv = player.getConsumeLv();
        if (addConusemLv > 0) {
            this.playerDao.addConsumeLv(playerId, addConusemLv);
            if (consumeLv < 5 && consumeLv + addConusemLv >= 5) {
                final ArmiesReward armiesReward = (ArmiesReward)this.dataGetter.getArmiesRewardCache().get((Object)(-5));
                if (armiesReward == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("armiesReward of vip 5 is null from ArmiesRewardCache. armyId=-5").appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).appendClassName("PayService").appendMethodName("commonPay").flush();
                }
                else {
                    PlayerArmyReward playerArmyReward = this.dataGetter.getPlayerArmyRewardDao().getPlayerArmyRewardByArmyId(playerId, -5);
                    if (playerArmyReward == null) {
                        playerArmyReward = new PlayerArmyReward();
                        playerArmyReward.setPlayerId(playerId);
                        playerArmyReward.setPowerId(armiesReward.getPowerId());
                        playerArmyReward.setArmyId(armiesReward.getId());
                        playerArmyReward.setFirst(0);
                        playerArmyReward.setExpireTime(new Date());
                        playerArmyReward.setNpcLost(null);
                        final int num = OneVsRewardNpcBuilder.getMaxHp(this.dataGetter, armiesReward.getId());
                        playerArmyReward.setHp(num);
                        playerArmyReward.setHpMax(num);
                        playerArmyReward.setState(0);
                        playerArmyReward.setBuyCount(0);
                        playerArmyReward.setFirstWin(0);
                        playerArmyReward.setWinCount(0);
                        this.dataGetter.getPlayerArmyRewardDao().create(playerArmyReward);
                    }
                    else {
                        ErrorSceneLog.getInstance().appendErrorMsg("playerArmyReward of vip 5 already exists!!! WHY?").appendPlayerId(player.getPlayerId()).appendPlayerName(player.getPlayerName()).appendClassName("PayService").appendMethodName("commonPay").flush();
                    }
                }
            }
            EventListener.fireEvent(new CommonEvent(6, playerId));
        }
        else {
            addConusemLv = 0;
        }
        this.playerAttributeDao.addPayPoint(playerId, gold);
        this.record(playerId, userId, yx, gold, orderId, payType, payDate);
        PayService.rtLog.info(LogUtil.formatPayLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), yx, userId, gold, orderId, payType, player.getForceId(), player.getConsumeLv() + addConusemLv, yxSource, request));
        player = this.playerDao.read(playerId);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("gold", player.getUserGold() + player.getSysGold()));
        final int newConsumeLev = player.getConsumeLv();
        if (newConsumeLev > oldConsumeLev) {
            try {
                result = this.updateVipTimes(oldConsumeLev, newConsumeLev, playerId);
                if (result.left) {
                    this.playerVipDao.setVipRemainingTimes(playerId, result.right);
                }
                else {
                    PayService.errorLog.error("#Vip_Error#commonPay##reason#Error In updateVipTimes#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold);
                }
            }
            catch (Exception e) {
                PayService.errorLog.error("#Vip_Error#commonPay##reason#" + e + "#orderId#" + orderId + "#playerId#" + playerId + "#userId#" + userId + "#gold#" + gold);
                return true;
            }
        }
        return true;
    }
    
    @Override
    public Tuple<Boolean, String> updateVipTimes(final int oldConsumeLev, final int newConsumeLev, final int playerId) {
        final Tuple<Boolean, String> result = new Tuple(true, "");
        Tuple<Boolean, String> result2 = new Tuple(true, "");
        try {
            String temp = this.playerVipDao.getVipRemainingTimes(playerId);
            if (StringUtils.isBlank(temp)) {
                temp = "";
            }
            final StringBuffer sb = new StringBuffer(temp);
            for (int i = 0; i < newConsumeLev - oldConsumeLev; ++i) {
                result2 = this.getVipTimes(i + oldConsumeLev + 1);
                if (!(boolean)result2.left) {
                    result.left = false;
                    result.right = result2.right;
                    return result;
                }
                sb.append("#");
                sb.append(result2.right);
            }
            if (sb.charAt(0) == '#') {
                sb.deleteCharAt(0);
            }
            result.right = sb.toString();
            return result;
        }
        catch (Exception e) {
            result.left = false;
            result.right = e.getMessage();
            PayService.errorLog.error("#Vip_Error#updateVipTimes##reason#" + e);
            return result;
        }
    }
    
    private Tuple<Boolean, String> getVipTimes(final int vip) {
        final Tuple<Boolean, String> result = new Tuple(true, "");
        try {
            final List<Chargeitem> list = this.chargeitemCache.getLvList(vip);
            final StringBuilder sb = new StringBuilder("");
            if (vip <= 0) {
                return result;
            }
            if (vip == 1) {
                result.right = "0";
                return result;
            }
            if (vip <= 6) {
                if (list != null) {
                    for (int i = 0; i < list.size(); ++i) {
                        if (i == 0) {
                            sb.append("0,");
                        }
                        else {
                            sb.append((int)(Object)list.get(i).getParam());
                            sb.append(",");
                        }
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    result.right = sb.toString();
                    return result;
                }
                result.left = false;
                result.right = LocalMessages.NO_SUCH_VIP;
                return result;
            }
            else {
                if (vip <= 11) {
                    sb.append("0");
                    result.right = sb.toString();
                    return result;
                }
                result.left = false;
                result.right = LocalMessages.OVER_MAX_LEV;
                return result;
            }
        }
        catch (Exception e) {
            result.left = false;
            result.right = e.getMessage();
            PayService.errorLog.error("#Vip_Error#getVipTimes##reason#" + e);
            return result;
        }
    }
    
    private int calculateConsumeLv(final int payPoint) {
        int lv = 0;
        final Set<Map.Entry<Integer, Integer>> entrySet = PayService.PAY_MAP.entrySet();
        for (final Map.Entry<Integer, Integer> entry : entrySet) {
            if (entry.getValue() <= payPoint) {
                lv = entry.getKey();
            }
        }
        return lv;
    }
    
    private void record(final int playerId, final String userId, final String yx, final int gold, final String orderId, final int payType, final Date payDate) {
        final PlayerPay pay = new PlayerPay();
        pay.setCreateTime(payDate);
        pay.setGold(gold);
        pay.setOrderId(orderId);
        pay.setPlayerId(playerId);
        pay.setUserId(userId);
        pay.setYx(yx);
        pay.setType(payType);
        this.playerPayDao.create(pay);
    }
    
    @Override
    public byte[] getVipInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final int currentLv = player.getConsumeLv();
        final int nextLv = currentLv + 1;
        int tipsType = 0;
        final String vipRemainingTimesAll = this.updateVipTimes(0, 11, 0).right;
        final String[] vipRemaingTimesAllArr = vipRemainingTimesAll.split("#");
        String vipRemainingTimes = this.playerVipDao.getVipRemainingTimes(playerDto.playerId);
        if (vipRemainingTimes == null) {
            vipRemainingTimes = vipRemainingTimesAll;
        }
        String[] vipRemainingTimesArr = vipRemainingTimes.split("#");
        if (vipRemainingTimesArr.length < vipRemaingTimesAllArr.length) {
            final int length2 = vipRemainingTimesArr.length;
            vipRemainingTimesArr = Arrays.copyOf(vipRemainingTimesArr, vipRemaingTimesAllArr.length);
            System.arraycopy(vipRemaingTimesAllArr, length2, vipRemainingTimesArr, length2, vipRemaingTimesAllArr.length - length2);
        }
        final String vipStatus = this.playerVipDao.getVipStatus(playerDto.playerId);
        final String[] vips = vipStatus.split("#");
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int currentPay = this.playerPayDao.queryPaySum(playerId);
        int totalPay = 0;
        if (nextLv <= 11) {
            totalPay = PayService.PAY_MAP.get(nextLv);
        }
        doc.createElement("currentLv", currentLv);
        doc.createElement("nextLv", nextLv);
        doc.createElement("maxLv", 11);
        doc.createElement("currentPay", currentPay);
        doc.createElement("totalPay", totalPay);
        doc.createElement("payURL", PayUtil.getAbsolutePayUrl(playerDto.yx, player.getUserId(), player.getPlayerId()));
        doc.startArray("privileges");
        for (int i = 0; i < vipRemaingTimesAllArr.length; ++i) {
            final List<Chargeitem> list = this.chargeitemCache.getLvList(i + 1);
            if (list == null) {
                break;
            }
            doc.startArray();
            final String[] tempAllArr = vipRemaingTimesAllArr[i].split(",");
            final String[] tempArr = vipRemainingTimesArr[i].split(",");
            final String tempStatusArr = vips[i];
            for (int j = 0; j < tempAllArr.length; ++j) {
                doc.startObject();
                doc.createElement("intro", list.get(j).getIntro());
                doc.createElement("status", tempStatusArr.substring(j, j + 1));
                doc.createElement("remainingTimes", tempArr[j]);
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endArray();
        final String tempStr = vips[4];
        final int[] status = new int[tempStr.length()];
        try {
            for (int k = 0; k < tempStr.length(); ++k) {
                status[k] = Integer.parseInt(new StringBuilder(String.valueOf(tempStr.charAt(k))).toString());
            }
        }
        catch (NumberFormatException e) {
            PayService.errorLog.error("#Vip_Error#getVipInfo##reason#" + e);
            return JsonBuilder.getJson(State.FAIL, e.getMessage());
        }
        final PlayerArmyReward playerArmyReward = this.dataGetter.getPlayerArmyRewardDao().getPlayerArmyRewardByArmyId(player.getPlayerId(), -5);
        final ArmiesReward armiesReward = (ArmiesReward)this.dataGetter.getArmiesRewardCache().get((Object)(-5));
        final int defId = -5;
        final int battleType = 11;
        doc.startObject("vip5BonusNpc");
        boolean canAttack = false;
        if (playerArmyReward != null) {
            if (playerDto.playerLv >= armiesReward.getLevel()) {
                canAttack = true;
            }
            else {
                canAttack = false;
                tipsType = 1;
            }
        }
        else {
            canAttack = false;
            tipsType = 2;
            if (currentLv >= 5 && status[0] != 1) {
                Tuple<Boolean, String> result = new Tuple(false, "");
                result = this.changeVipStatus(this.playerVipDao.getVipStatus(playerDto.playerId), 5, 1);
                if (result.left) {
                    this.playerVipDao.setVipStatus(playerDto.playerId, result.right);
                }
            }
        }
        doc.createElement("canAttack", canAttack);
        doc.createElement("tipsType", tipsType);
        doc.createElement("defId", defId);
        doc.createElement("battleType", battleType);
        doc.createElement("openLv", armiesReward.getLevel());
        final int dropType = this.dataGetter.getArmiesRewardCache().getBonusDropType(defId);
        final int dropNum = OneVsRewardNpcBuilder.getMaxHp(this.dataGetter, defId);
        doc.createElement("dropType", dropType);
        doc.createElement("dropNum", dropNum);
        doc.endObject();
        if (this.yxOperation.checkTencentPf(playerDto.yx)) {
            final List<GiftTx> list2 = this.giftTxCache.getGiftByType(5);
            doc.startArray("packageList");
            for (final GiftTx gt : list2) {
                doc.startObject();
                doc.createElement("gold", gt.getGold());
                doc.createElement("pic", gt.getPic());
                doc.createElement("goodId", gt.getLv());
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] handleVipPrivilege(final String pic, final PlayerDto playerDto) {
        Tuple<Boolean, byte[]> tuple = null;
        if (pic.equals("2_1")) {
            final int vip = 2;
            final int seq = 1;
            tuple = this.checkStatus(playerDto, vip, seq);
            if (!(boolean)tuple.left) {
                return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
            }
            final String vipStatus = this.playerVipDao.getVipStatus(playerDto.playerId);
            if (StringUtils.isBlank(vipStatus)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.INVALID_VIP_PARAM);
            }
            String finalStatus = "";
            final Chargeitem chargeitem = (Chargeitem)this.chargeitemCache.get((Object)55);
            final int num = (chargeitem == null) ? 50 : ((int)(Object)chargeitem.getParam());
            this.playerAttributeDao.addFreeConstructionNum(playerDto.playerId, num, "vip2\u8d60\u9001\u9ec4\u91d1\u5efa\u7b51\u961f");
            Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("freeConsNum", num));
            if (this.changeVipStatus(vipStatus, vip, seq).left) {
                finalStatus = this.changeVipStatus(vipStatus, vip, seq).right;
                this.playerVipDao.setVipStatus(playerDto.playerId, finalStatus);
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("remainingTimes", 0);
                doc.createElement("status", 1);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            return JsonBuilder.getJson(State.FAIL, this.changeVipStatus(vipStatus, vip, seq).right);
        }
        else if (pic.equals("2_2")) {
            final int vip = 2;
            final int seq = 2;
            tuple = this.checkStatus(playerDto, vip, seq);
            if (!(boolean)tuple.left) {
                return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
            }
            return this.putTokenInStoreHouse(playerDto.playerId, vip, seq, 4, LocalMessages.VIP_MSG_WOOD_300_DAY);
        }
        else if (pic.equals("3_1")) {
            final int vip = 3;
            final int seq = 1;
            tuple = this.checkStatus(playerDto, vip, seq);
            if (!(boolean)tuple.left) {
                return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
            }
            String finalStatus2 = "";
            final Chargeitem chargeitem2 = (Chargeitem)this.chargeitemCache.get((Object)56);
            final int phantomNum = (int)(Object)chargeitem2.getParam();
            this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerDto.playerId, phantomNum, "vip3\u7279\u6743\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
            final String vipStatus2 = this.playerVipDao.getVipStatus(playerDto.playerId);
            if (StringUtils.isBlank(vipStatus2)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.INVALID_VIP_PARAM);
            }
            if (this.changeVipStatus(vipStatus2, vip, seq).left) {
                finalStatus2 = this.changeVipStatus(vipStatus2, vip, seq).right;
                this.playerVipDao.setVipStatus(playerDto.playerId, finalStatus2);
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("remainingTimes", 0);
                doc.createElement("status", 1);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            return JsonBuilder.getJson(State.FAIL, this.changeVipStatus(vipStatus2, vip, seq).right);
        }
        else if (pic.equals("3_2")) {
            final int vip = 3;
            final int seq = 2;
            tuple = this.checkStatus(playerDto, vip, seq);
            if (!(boolean)tuple.left) {
                return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
            }
            return this.putTokenInStoreHouse(playerDto.playerId, vip, seq, 1, LocalMessages.VIP_MSG_COOPER_300_DAY);
        }
        else {
            if (pic.equals("4_1")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_VIP_PRIVILEGE);
            }
            if (pic.equals("4_2")) {
                final int vip = 4;
                final int seq = 2;
                tuple = this.checkStatus(playerDto, vip, seq);
                if (!(boolean)tuple.left) {
                    return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
                }
                return this.putTokenInStoreHouse(playerDto.playerId, vip, seq, 7, LocalMessages.VIP_MSG_FOOD_300_DAY);
            }
            else {
                if (pic.equals("5_1")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_VIP_PRIVILEGE);
                }
                if (pic.equals("5_2")) {
                    final int vip = 5;
                    final int seq = 2;
                    tuple = this.checkStatus(playerDto, vip, seq);
                    if (!(boolean)tuple.left) {
                        return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
                    }
                    return this.putTokenInStoreHouse(playerDto.playerId, vip, seq, 11, LocalMessages.VIP_MSG_TROOP_300_DAY);
                }
                else if (pic.equals("6_1")) {
                    final int vip = 6;
                    final int seq = 1;
                    tuple = this.checkStatus(playerDto, vip, seq);
                    if (!(boolean)tuple.left) {
                        return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
                    }
                    String finalStatus2 = "";
                    final int usedStoreNum = this.storeHouseDao.getCountByPlayerId(playerDto.playerId);
                    final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
                    final int maxStoreNum = pa.getMaxStoreNum();
                    if (playerDto.playerLv < 60) {
                        final JsonDocument doc = new JsonDocument();
                        doc.startObject();
                        doc.createElement("ErrorMessage", (Object)LocalMessages.LEV_NOT_REACHED);
                        doc.endObject();
                        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                    }
                    if (maxStoreNum - usedStoreNum < 3) {
                        final JsonDocument doc = new JsonDocument();
                        doc.startObject();
                        doc.createElement("ErrorMessage", (Object)LocalMessages.NO_ENOUGH_STORE_NUM);
                        doc.endObject();
                        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                    }
                    final int[] type = Constants.VIP6EquipType;
                    final int[] quality = Constants.VIP6EquipQuality;
                    for (int i = 0; i < type.length; ++i) {
                        final StringBuffer sb = new StringBuffer();
                        final List<Equip> tempList = this.equipCache.getEquipsByTypeQuality(type[i], quality[i]);
                        final List<Equip> equipList = new ArrayList<Equip>();
                        for (final Equip equip : tempList) {
                            if (this.equipCache.getJinpinEquips().contains(equip.getId())) {
                                equipList.add(equip);
                            }
                        }
                        final Random random = new Random();
                        final int index = Math.abs(random.nextInt()) % equipList.size();
                        final Equip item = equipList.get(index);
                        for (int j = 0; j < 3; ++j) {
                            final int skill = Math.abs(random.nextInt()) % 7 + 1;
                            sb.append(skill);
                            sb.append(":1;");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        final StoreHouse sh = new StoreHouse();
                        sh.setItemId(item.getId());
                        sh.setPlayerId(playerDto.playerId);
                        sh.setLv(1);
                        sh.setOwner(0);
                        sh.setType(1);
                        sh.setGoodsType(item.getType());
                        sh.setAttribute(new StringBuilder().append(item.getAttribute()).toString());
                        sh.setQuality(item.getQuality());
                        sh.setGemId(0);
                        sh.setNum(1);
                        sh.setState(0);
                        sh.setRefreshAttribute(sb.toString());
                        sh.setQuenchingTimes(0);
                        sh.setBindExpireTime(0L);
                        sh.setMarkId(0);
                        this.storeHouseDao.create(sh);
                    }
                    final String vipStatus3 = this.playerVipDao.getVipStatus(playerDto.playerId);
                    if (StringUtils.isBlank(vipStatus3)) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.INVALID_VIP_PARAM);
                    }
                    final Tuple<Boolean, String> result = this.changeVipStatus(vipStatus3, vip, seq);
                    if (result.left) {
                        finalStatus2 = result.right;
                        this.playerVipDao.setVipStatus(playerDto.playerId, finalStatus2);
                        return JsonBuilder.getJson(State.SUCCESS, "");
                    }
                    return JsonBuilder.getJson(State.FAIL, result.right);
                }
                else if (pic.equals("6_2")) {
                    final int vip = 6;
                    final int seq = 2;
                    tuple = this.checkStatus(playerDto, vip, seq);
                    if (!(boolean)tuple.left) {
                        return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
                    }
                    return this.putTokenInStoreHouse(playerDto.playerId, vip, seq, 13, LocalMessages.VIP_MSG_IRON_300_DAY);
                }
                else {
                    if (!pic.equals("7_1")) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_VIP_PRIVILEGE);
                    }
                    final int vip = 7;
                    final int seq = 1;
                    final PlayerAttribute pa2 = this.playerAttributeDao.read(playerDto.playerId);
                    final boolean isOpen = RankComm.functionIsOpen(59, playerDto.playerId, pa2);
                    if (!isOpen) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.OPEN_AFTER_TEAM_IS_OPEN);
                    }
                    tuple = this.checkStatus(playerDto, vip, seq);
                    if (!(boolean)tuple.left) {
                        return JsonBuilder.getJson(State.FAIL, new String(tuple.right));
                    }
                    String finalStatus3 = "";
                    int times = 2;
                    final Chargeitem chargeitem3 = (Chargeitem)this.chargeitemCache.get((Object)71);
                    if (chargeitem3 != null) {
                        times = (int)(Object)chargeitem3.getParam();
                    }
                    this.playerBattleAttributeDao.addTeamTimes(playerDto.playerId, times);
                    final String vipStatus4 = this.playerVipDao.getVipStatus(playerDto.playerId);
                    if (StringUtils.isBlank(vipStatus4)) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.INVALID_VIP_PARAM);
                    }
                    if (this.changeVipStatus(vipStatus4, vip, seq).left) {
                        finalStatus3 = this.changeVipStatus(vipStatus4, vip, seq).right;
                        this.playerVipDao.setVipStatus(playerDto.playerId, finalStatus3);
                        final JsonDocument doc2 = new JsonDocument();
                        doc2.startObject();
                        doc2.createElement("remainingTimes", 0);
                        doc2.createElement("status", 1);
                        doc2.endObject();
                        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
                    }
                    return JsonBuilder.getJson(State.FAIL, this.changeVipStatus(vipStatus4, vip, seq).right);
                }
            }
        }
    }
    
    public byte[] putTokenInStoreHouse(final int playerId, final int vip, final int seq, final int itemIndex, final String msg) {
        Tuple<Boolean, String> result1 = new Tuple(false, (Object)null);
        Tuple<Boolean, String> result2 = new Tuple(false, (Object)null);
        final String VipRemainingTimes = this.playerVipDao.getVipRemainingTimes(playerId);
        final String[] vips = VipRemainingTimes.split("#");
        final StringBuffer sb = new StringBuffer(vips[vip - 1]);
        final String[] privileges = sb.toString().split(",");
        final int originalTimes = Integer.parseInt(privileges[seq - 1]);
        final Items item = this.itemsCache.getItemsByTypeAndIndex(13, itemIndex);
        if (item == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_SUCH_VIP);
        }
        final int usedStoreNum = this.storeHouseDao.getCountByPlayerId(playerId);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final int maxStoreNum = pa.getMaxStoreNum();
        final List<StoreHouse> shList = this.dataGetter.getStoreHouseDao().getByItemId(playerId, item.getId(), StoreHouseService.getStoreHouseType(item.getType()));
        if (shList != null && !shList.isEmpty()) {
            this.dataGetter.getStoreHouseService().gainItems(playerId, originalTimes, item.getId(), msg);
        }
        else {
            if (maxStoreNum <= usedStoreNum) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_ENOUGH_STORE_NUM);
            }
            this.dataGetter.getStoreHouseService().gainItems(playerId, originalTimes, item.getId(), msg);
        }
        result1 = this.reduceRemainingTimes(VipRemainingTimes, vip, seq, originalTimes);
        if (!(boolean)result1.left) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.VIP_EXCEPTION);
        }
        this.playerVipDao.setVipRemainingTimes(playerId, result1.right);
        final String vipStatus = this.playerVipDao.getVipStatus(playerId);
        if (StringUtils.isBlank(vipStatus)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.INVALID_VIP_PARAM);
        }
        String finalStatus = "";
        result2 = this.changeVipStatus(vipStatus, vip, seq);
        if (result2.left) {
            finalStatus = result2.right;
            this.playerVipDao.setVipStatus(playerId, finalStatus);
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.FAIL, this.changeVipStatus(vipStatus, vip, seq).right);
    }
    
    Tuple<Boolean, byte[]> checkStatus(final PlayerDto playerDto, final int vip, final int seq) {
        final Tuple<Boolean, byte[]> result = new Tuple(false, "".getBytes());
        if (this.playerDao.getConsumeLv(playerDto.playerId) < vip) {
            result.right = LocalMessages.VIP_LV_NOT_REACHED.getBytes();
            return result;
        }
        final String VipRemainingTimes = this.playerVipDao.getVipRemainingTimes(playerDto.playerId);
        if (StringUtils.isBlank(VipRemainingTimes)) {
            result.right = LocalMessages.INVALID_VIP_PARAM.getBytes();
            return result;
        }
        final String vipStatus = this.playerVipDao.getVipStatus(playerDto.playerId);
        if (StringUtils.isBlank(vipStatus)) {
            result.right = LocalMessages.INVALID_VIP_PARAM.getBytes();
            return result;
        }
        final String[] vips = vipStatus.split("#");
        final StringBuffer sb = new StringBuffer(vips[vip - 1]);
        if (sb.charAt(seq - 1) == '1') {
            result.right = LocalMessages.CAN_NOT_GET_TWICE.getBytes();
            return result;
        }
        result.left = true;
        return result;
    }
    
    private Tuple<Boolean, String> changeVipStatus(final String originalStatus, final int vip, final int seq) {
        final Tuple<Boolean, String> result = new Tuple(false, "");
        String finalStatus = "";
        if (seq <= 0 || vip <= 0 || StringUtils.isBlank(originalStatus)) {
            result.right = LocalMessages.INVALID_VIP_PARAM;
            return result;
        }
        try {
            final String[] vips = originalStatus.split("#");
            final StringBuffer sb = new StringBuffer(vips[vip - 1]);
            sb.setCharAt(seq - 1, '1');
            for (int i = 0; i < vips.length; ++i) {
                if (i == vip - 1) {
                    finalStatus = String.valueOf(finalStatus) + sb.toString() + "#";
                }
                else {
                    finalStatus = String.valueOf(finalStatus) + vips[i] + "#";
                }
            }
            final int len = finalStatus.length();
            if (len > 0 && finalStatus.charAt(len - 1) == '#') {
                finalStatus = finalStatus.substring(0, len - 1);
            }
        }
        catch (Exception e) {
            result.right = e.getMessage();
            PayService.errorLog.error("#Vip_Error#changeVipStatus##reason#" + e);
            return result;
        }
        result.left = true;
        result.right = finalStatus;
        return result;
    }
    
    private Tuple<Boolean, String> reduceRemainingTimes(final String originalRemainingTimes, final int vip, final int seq, final int reduceNum) {
        final Tuple<Boolean, String> result = new Tuple(false, "");
        String remainingTimes = "";
        String finalRemainingTimes = "";
        if (vip <= 0 || seq <= 0 || StringUtils.isBlank(originalRemainingTimes)) {
            result.right = LocalMessages.INVALID_VIP_PARAM;
            return result;
        }
        try {
            final String[] vips = originalRemainingTimes.split("#");
            final StringBuffer sb = new StringBuffer(vips[vip - 1]);
            final String[] privileges = sb.toString().split(",");
            final StringBuffer temp = new StringBuffer("");
            int originalTimes = Integer.parseInt(privileges[seq - 1]);
            originalTimes -= reduceNum;
            if (reduceNum <= 0) {
                originalTimes = 0;
            }
            privileges[seq - 1] = new StringBuilder(String.valueOf(originalTimes)).toString();
            for (int j = 0; j < privileges.length; ++j) {
                temp.append(privileges[j]);
                temp.append(",");
            }
            if (temp.length() > 0 && temp.charAt(temp.length() - 1) == ',') {
                temp.deleteCharAt(temp.length() - 1);
            }
            for (int i = 0; i < vips.length; ++i) {
                if (i == vip - 1) {
                    remainingTimes = String.valueOf(remainingTimes) + temp.toString() + "#";
                }
                else {
                    remainingTimes = String.valueOf(remainingTimes) + vips[i] + "#";
                }
            }
            if (remainingTimes.length() > 0 && remainingTimes.charAt(remainingTimes.length() - 1) == '#') {
                finalRemainingTimes = remainingTimes.substring(0, remainingTimes.length() - 1);
            }
        }
        catch (Exception e) {
            PayService.errorLog.error("#Vip_Error#reduceRemainingTimes##reason#" + e);
            result.right = LocalMessages.VIP_EXCEPTION_IN_REDUCE_TIMES;
            return result;
        }
        result.left = true;
        result.right = finalRemainingTimes;
        return result;
    }
    
    @Override
    public boolean addAdditionalGold(final int playerId, final int gold) {
        final Activity activity = this.activityDao.read(2);
        if (activity == null) {
            return false;
        }
        if (StringUtils.isBlank(activity.getParamsInfo()) || StringUtils.isBlank(activity.getStartTime().toString()) || StringUtils.isBlank(activity.getEndTime().toString()) || gold <= 0) {
            return false;
        }
        final Date startTime = activity.getStartTime();
        final Date endTime = activity.getEndTime();
        if (new Date().before(startTime) || new Date().after(endTime)) {
            return false;
        }
        final int totalUserGold = this.playerDao.getTotalUserGold(playerId);
        final String[] payRuleSTR = activity.getParamsInfo().split(";");
        final int len = payRuleSTR.length;
        if (len <= 0) {
            return false;
        }
        final int[] left = new int[len];
        final int[] right = new int[len];
        for (int i = 0; i < len; ++i) {
            final String[] tuple = payRuleSTR[i].split(",");
            if (tuple.length != 2) {
                return false;
            }
            try {
                left[i] = Integer.parseInt(tuple[0]);
                right[i] = Integer.parseInt(tuple[1]);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }
        int additionalGold = 0;
        int oldPosition = 0;
        boolean flag1 = true;
        for (int j = 0; j < left.length; ++j) {
            if (totalUserGold < left[j]) {
                flag1 = false;
                oldPosition = j;
                break;
            }
        }
        if (flag1) {
            oldPosition = left.length;
        }
        int newPosition = 0;
        boolean flag2 = true;
        for (int k = oldPosition; k < right.length; ++k) {
            if (totalUserGold + gold < left[k]) {
                flag2 = false;
                newPosition = k;
                break;
            }
        }
        if (flag2) {
            newPosition = left.length;
        }
        for (int l = oldPosition; l < newPosition; ++l) {
            additionalGold += right[l];
        }
        if (additionalGold > 0) {
            this.playerDao.addSysGold(this.playerDao.read(playerId), additionalGold, "\u5145\u503c\u6d3b\u52a8\u8d60\u9001\u91d1\u5e01");
            final String content = MessageFormatter.format(LocalMessages.PAY_ACTIVITY_MAIL_CONTENT, new Object[] { gold, totalUserGold + gold, left[newPosition - 1], additionalGold });
            this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.PAY_ACTIVITY_MAIL_TITLE, content, 1, playerId, 0);
        }
        this.playerDao.addTotalUserGold(playerId, gold);
        try {
            final String content = activity.getParamsInfo();
            final String[] payArry = content.split(";");
            final String[] temp = payArry[payArry.length - 1].split(",");
            final int nGold = Integer.parseInt(temp[0]);
            final boolean havePayActivity = this.playerDao.getTotalUserGold(playerId) < nGold;
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("havePayActivity", havePayActivity ? 1 : 0));
        }
        catch (Exception e2) {
            PayService.errorLog.error("PayService addAdditionalGold", e2);
        }
        return true;
    }
    
    @Override
    public void addTicketGold(final int playerId, final int gold) {
        if (!PayService.inTicket) {
            return;
        }
        final int totalTicketGoldBefore = this.playerDao.getTotalTicketGold(playerId);
        this.playerDao.addTotalTicketGold(playerId, gold);
        final int totalTicketGoldAfter = this.playerDao.getTotalTicketGold(playerId);
        final int rewardBefore = this.getReward(totalTicketGoldBefore);
        final int rewardAfter = this.getReward(totalTicketGoldAfter);
        final int rewardTicket = rewardAfter - rewardBefore;
        if (rewardTicket <= 0) {
            return;
        }
        final int currentNGold = this.getCurrentNGold(totalTicketGoldAfter);
        this.playerTicketsDao.addTickets(playerId, rewardTicket, "\u5145\u503c\u9001\u70b9\u5238\u6d3b\u52a8\u83b7\u5f97\u70b9\u5238", true);
        final String content = MessageFormatter.format(LocalMessages.TICKET_ACTIVITY_MAIL_CONTENT, new Object[] { gold, totalTicketGoldAfter, currentNGold, rewardTicket });
        this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.TICKET_ACTIVITY_MAIL_TITLE, content, 1, playerId, 0);
        if (totalTicketGoldAfter >= PayService.ticketMap.get(PayService.ticketMap.size()).left) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveTicketActivity", 0));
        }
    }
    
    @Override
    public byte[] getPayAcitivityInfo(final PlayerDto playerDto) {
        final Activity activity = this.activityDao.read(2);
        final Date now = new Date();
        if (activity == null || activity.getStartTime().after(now) || activity.getEndTime().before(now)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PAY_ACTIVITY_NO_ACTIVITY);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final String content = activity.getParamsInfo();
        final String[] payArry = content.split(";");
        doc.startArray("lvs");
        int totalGold = 0;
        int haveGold = 0;
        int lv = 0;
        int count = 0;
        final int playerId = playerDto.playerId;
        final int gold = this.playerDao.getTotalUserGold(playerId);
        int needGold = 0;
        boolean flag_needGold = false;
        String[] array;
        for (int length = (array = payArry).length, i = 0; i < length; ++i) {
            final String payInfo = array[i];
            final String[] temp = payInfo.split(",");
            final int nGold = Integer.parseInt(temp[0]);
            final int rGold = Integer.parseInt(temp[1]);
            doc.startObject();
            doc.createElement("gold", nGold);
            doc.createElement("rewardGold", rGold);
            doc.endObject();
            ++count;
            if (gold >= nGold) {
                lv = count;
                haveGold += rGold;
            }
            if (!flag_needGold && gold < nGold) {
                needGold = nGold - gold;
                flag_needGold = true;
            }
            totalGold += rGold;
        }
        doc.endArray();
        doc.createElement("day", (Object)TimeUtil.now2specMs(activity.getEndTime().getTime()));
        doc.createElement("remainingGold", totalGold - haveGold);
        doc.createElement("gold", gold);
        doc.createElement("lv", lv + 1);
        doc.createElement("needGold", needGold);
        doc.createElement("payURL", PayUtil.getAbsolutePayUrl(playerDto.yx, playerDto.userId, playerId));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getTicketAcitivityInfo(final PlayerDto playerDto) {
        if (!PayService.inTicket) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TICKET_ACTIVITY_NO_ACTIVITY);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("lvs");
        int totalTicket = 0;
        int haveTicket = 0;
        int lv = 0;
        int count = 0;
        final int playerId = playerDto.playerId;
        final int gold = this.playerDao.getTotalTicketGold(playerId);
        int needGold = 0;
        boolean flag_needGold = false;
        for (int size = PayService.ticketMap.size(), i = 1; i <= size; ++i) {
            final Tuple<Integer, Integer> tuple = PayService.ticketMap.get(i);
            final int nGold = tuple.left;
            final int rTicket = tuple.right;
            doc.startObject();
            doc.createElement("gold", nGold);
            doc.createElement("rewardTicket", rTicket);
            doc.endObject();
            ++count;
            if (gold >= nGold) {
                lv = count;
                haveTicket += rTicket;
            }
            if (!flag_needGold && gold < nGold) {
                needGold = nGold - gold;
                flag_needGold = true;
            }
            totalTicket += rTicket;
        }
        doc.endArray();
        final Activity activity = this.activityDao.read(7);
        doc.createElement("day", (Object)TimeUtil.now2specMs(activity.getEndTime().getTime()));
        doc.createElement("remainingTicket", totalTicket - haveTicket);
        doc.createElement("gold", gold);
        doc.createElement("lv", lv + 1);
        doc.createElement("needGold", needGold);
        doc.createElement("payURL", PayUtil.getAbsolutePayUrl(playerDto.yx, playerDto.userId, playerId));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void pushPayActivityInfo(final String params) {
        final Activity activity = this.activityDao.read(2);
        final Date now = new Date();
        if (activity == null || activity.getStartTime().after(now) || activity.getEndTime().before(now)) {
            return;
        }
        final String content = activity.getParamsInfo();
        final String[] payArry = content.split(";");
        final String[] temp = payArry[payArry.length - 1].split(",");
        final int nGold = Integer.parseInt(temp[0]);
        final byte[] send = JsonBuilder.getSimpleJson("havePayActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (this.playerDao.getTotalUserGold(dto.playerId) < nGold) {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
    
    @Override
    public void pushPayActivityInfoEnd(final String params) {
        final Activity activity = this.activityDao.read(2);
        final Date now = new Date();
        if (activity == null || activity.getEndTime().before(now)) {
            final byte[] send = JsonBuilder.getSimpleJson("havePayActivity", 0);
            for (final PlayerDto dto : Players.getAllPlayer()) {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
    
    private int getReward(final int gold) {
        int reward = 0;
        for (int size = PayService.ticketMap.size(), i = 1; i <= size; ++i) {
            final Tuple<Integer, Integer> tuple = PayService.ticketMap.get(i);
            if (gold < tuple.left) {
                break;
            }
            reward += tuple.right;
        }
        return reward;
    }
    
    private int getCurrentNGold(final int gold) {
        int currentNGold = 0;
        for (int size = PayService.ticketMap.size(), i = 1; i <= size; ++i) {
            final Tuple<Integer, Integer> tuple = PayService.ticketMap.get(i);
            if (gold < tuple.left) {
                break;
            }
            currentNGold = tuple.left;
        }
        return currentNGold;
    }
    
    @Override
    public void checkVipForLogin(final Player player, final JsonDocument doc) {
        try {
            final int playerId = player.getPlayerId();
            final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
            boolean hasVipPrivilege = false;
            final String vipStatus = this.playerVipDao.getVipStatus(playerId);
            if (StringUtils.isBlank(vipStatus)) {
                doc.createElement("hasVipPrivilege", false);
            }
            else {
                final String[] vips = vipStatus.split("#");
                for (int j = 0; j < Math.min(this.playerDao.getConsumeLv(playerId), 7); ++j) {
                    final String vip = vips[j];
                    if (j + 1 != 1) {
                        if (j + 1 == 2) {
                            if (vip.charAt(0) == '0') {
                                hasVipPrivilege = true;
                                break;
                            }
                            if (vip.charAt(1) == '0' && this.checkVipResourceOpen(2, playerId)) {
                                hasVipPrivilege = true;
                                break;
                            }
                        }
                        else if (j + 1 == 3) {
                            if (vip.charAt(0) == '0') {
                                hasVipPrivilege = true;
                                break;
                            }
                            if (vip.charAt(1) == '0' && this.checkVipResourceOpen(1, playerId)) {
                                hasVipPrivilege = true;
                                break;
                            }
                        }
                        else if (j + 1 == 4) {
                            if (vip.charAt(1) == '0' && this.checkVipResourceOpen(3, playerId)) {
                                hasVipPrivilege = true;
                                break;
                            }
                        }
                        else if (j + 1 == 5) {
                            if (vip.charAt(0) == '0' && player.getPlayerLv() >= 50) {
                                hasVipPrivilege = true;
                                break;
                            }
                            if (vip.charAt(1) == '0' && this.checkVipResourceOpen(5, playerId)) {
                                hasVipPrivilege = true;
                                break;
                            }
                        }
                        else if (j + 1 == 6) {
                            if (vip.charAt(0) == '0' && player.getPlayerLv() >= 60) {
                                hasVipPrivilege = true;
                                break;
                            }
                            if (vip.charAt(1) == '0' && this.checkVipResourceOpen(4, playerId)) {
                                hasVipPrivilege = true;
                                break;
                            }
                        }
                        else {
                            if (j + 1 != 7) {
                                break;
                            }
                            final boolean isOpen = RankComm.functionIsOpen(59, playerId, pa);
                            if (vip.charAt(0) == '0' && isOpen) {
                                hasVipPrivilege = true;
                                break;
                            }
                        }
                    }
                }
                doc.createElement("hasVipPrivilege", hasVipPrivilege);
            }
        }
        catch (Exception e) {
            PayService.errorLog.error("#className:payService#methodName:checkVipForLogin", e);
        }
    }
    
    private boolean checkVipResourceOpen(final int buildingType, final int playerId) {
        final BuildingService bs = new BuildingService();
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        final boolean isOpen = bs.checkBuildingTypeIsOpen(buildingType, pa);
        return isOpen && cs[41] == '1';
    }
    
    @Override
    public byte[] getYellowVipInfo(final PlayerDto playerDto) {
        if (!this.yxOperation.checkTencentPf(playerDto.yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TENCENT_INVALID_PLATFORM);
        }
        final UserDto userDto = Users.getUserDto(playerDto.userId, playerDto.yx);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final List<GiftTx> list1 = this.giftTxCache.getGiftByType(1);
        doc.startArray("dailyGift");
        doc.startObject();
        doc.createElement("curLv", userDto.getYellowVipLevel());
        final boolean canRecv1 = this.canRecvTxReward(playerDto, 1, 0);
        doc.createElement("canRecv", canRecv1);
        doc.endObject();
        for (final GiftTx gt : list1) {
            doc.startObject();
            doc.createElement("name", gt.getName());
            doc.createElement("lv", gt.getLv());
            doc.createElement("food", gt.getFood());
            doc.createElement("recruitToken", gt.getRecruitToken());
            doc.createElement("danshutiejuan", gt.getDanshutiejuan());
            doc.endObject();
        }
        doc.endArray();
        final List<GiftTx> list2 = this.giftTxCache.getGiftByType(2);
        doc.startArray("yearVipExtraGift");
        for (final GiftTx gt2 : list2) {
            doc.startObject();
            doc.createElement("gold", gt2.getGold());
            final boolean canRecv2 = this.canRecvTxReward(playerDto, 2, 0);
            doc.createElement("canRecv", canRecv2);
            doc.endObject();
        }
        doc.endArray();
        final List<GiftTx> list3 = this.giftTxCache.getGiftByType(3);
        doc.startArray("rookieGift");
        for (final GiftTx gt3 : list3) {
            doc.startObject();
            doc.startArray("equipList");
            final String[] equipIds = gt3.getEquip().split(";");
            String[] array;
            for (int length = (array = equipIds).length, i = 0; i < length; ++i) {
                final String eIdStr = array[i];
                final int equipId = Integer.parseInt(eIdStr);
                final Equip equip = (Equip)this.equipCache.get((Object)equipId);
                if (equip != null) {
                    doc.startObject();
                    doc.createElement("name", equip.getName());
                    doc.createElement("pic", equip.getPic());
                    final boolean canRecv3 = this.canRecvTxReward(playerDto, 3, 0);
                    doc.createElement("canRecv", canRecv3);
                    doc.endObject();
                }
            }
            doc.endArray();
            doc.endObject();
        }
        doc.endArray();
        final List<GiftTx> list4 = this.giftTxCache.getGiftByType(4);
        doc.startArray("upgradeGift");
        int seq = 1;
        for (final GiftTx gt4 : list4) {
            doc.startObject();
            doc.createElement("lv", gt4.getLv());
            doc.createElement("food", gt4.getFood());
            doc.createElement("freeCons", gt4.getFreeCons());
            doc.createElement("recruitToken", gt4.getRecruitToken());
            final boolean isLvReached = playerDto.playerLv >= gt4.getLv();
            final boolean canRecv4 = this.canRecvTxReward(playerDto, 4, seq);
            if (isLvReached && canRecv4) {
                doc.createElement("canRecv", true);
            }
            else {
                doc.createElement("canRecv", false);
            }
            doc.endObject();
            ++seq;
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private boolean canRecvTxReward(final PlayerDto playerDto, final int type, final int seq) {
        final PlayerVipTx pvt = this.playerVipTxDao.getByPlayerId(playerDto.playerId);
        if (pvt == null) {
            return false;
        }
        final UserDto userDto = Users.getUserDto(playerDto.userId, playerDto.yx);
        if (userDto.getIsYellowVip() == 0) {
            return false;
        }
        if (type == 1) {
            final int status = pvt.getDailyStatus();
            return status == 0;
        }
        if (type == 2) {
            final int status = pvt.getExtraStatus();
            return status == 0 && userDto.getIsYellowYearVip() == 1;
        }
        if (type == 3) {
            final int status = pvt.getRookieStatus();
            return status == 0;
        }
        if (type == 4) {
            final String status2 = pvt.getUpgradeStatus();
            return seq > 0 && seq <= status2.length() && status2.charAt(seq - 1) == '0';
        }
        return false;
    }
    
    @Override
    public byte[] recvYellowVipReward(final PlayerDto playerDto, final int type, final int seq) {
        final PlayerVipTx pvt = this.playerVipTxDao.getByPlayerId(playerDto.playerId);
        final UserDto userDto = Users.getUserDto(playerDto.userId, playerDto.yx);
        if (userDto.getIsYellowVip() == 0 || pvt == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TENCENT_NOT_YELLOW_VIP);
        }
        final List<GiftTx> list = this.giftTxCache.getGiftByType(type);
        if (list == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TENCENT_NO_SUCH_REWARD);
        }
        final boolean canRecv = this.canRecvTxReward(playerDto, type, seq);
        if (!canRecv) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TENCENT_REWARD_HAS_BEEN_RECEIVED);
        }
        final JsonDocument doc = new JsonDocument();
        if (type == 1) {
            int curLv = userDto.getYellowVipLevel();
            if (curLv > 8) {
                curLv = list.size();
            }
            final GiftTx giftTx = list.get(curLv - 1);
            final int food = giftTx.getFood();
            final int recruitToken = giftTx.getRecruitToken();
            final int danshutiejuan = giftTx.getDanshutiejuan();
            doc.startObject();
            if (food > 0) {
                this.playerResourceDao.addFoodIgnoreMax(playerDto.playerId, food, "\u817e\u8baf\u9ec4\u94bb\u793c\u5305\u83b7\u5f97\u7cae\u98df");
                doc.createElement("food", food);
            }
            if (recruitToken > 0) {
                this.playerAttributeDao.addRecruitToken(playerDto.playerId, recruitToken, "\u817e\u8baf\u9ec4\u94bb\u793c\u5305\u83b7\u5f97\u52df\u5175\u4ee4");
                doc.createElement("recruitToken", recruitToken);
            }
            if (danshutiejuan > 0) {
                this.storeHouseService.gainSearchItems(101, danshutiejuan, playerDto, "\u817e\u8baf\u9ec4\u94bb\u793c\u5305\u83b7\u5f97\u4e39\u4e66\u94c1\u5238");
                doc.createElement("danshutiejuan", danshutiejuan);
            }
            doc.endObject();
            this.playerVipTxDao.updateDailyStatus(playerDto.playerId, 1);
        }
        else if (type == 2) {
            final Player player = this.playerDao.read(playerDto.playerId);
            final GiftTx giftTx = list.get(0);
            final int gold = giftTx.getGold();
            doc.startObject();
            if (gold > 0) {
                this.playerDao.addSysGold(player, gold, "\u817e\u8baf\u9ec4\u94bb\u793c\u5305\u83b7\u5f97\u91d1\u5e01");
                doc.createElement("gold", gold);
            }
            doc.endObject();
            this.playerVipTxDao.updateExtraStatus(playerDto.playerId, 1);
        }
        else if (type == 3) {
            if (playerDto.playerLv < 30) {
                return JsonBuilder.getJson(State.FAIL, "\u8fbe\u523030\u7ea7\u624d\u53ef\u4ee5\u9886\u53d6\uff01");
            }
            final GiftTx giftTx2 = list.get(0);
            final String equipIdStr = giftTx2.getEquip();
            final String[] equipIds = equipIdStr.split(";");
            final int equipNum = equipIds.length;
            final int usedStoreNum = this.storeHouseDao.getCountByPlayerId(playerDto.playerId);
            final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
            final int maxStoreNum = pa.getMaxStoreNum();
            if (usedStoreNum + equipNum > maxStoreNum) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_ENOUGH_STORE_NUM);
            }
            doc.startObject();
            doc.startArray("equipList");
            String[] array;
            for (int length = (array = equipIds).length, i = 0; i < length; ++i) {
                final String tempStr = array[i];
                final int equipId = Integer.parseInt(tempStr);
                final Equip item = (Equip)this.equipCache.get((Object)equipId);
                final StoreHouse sh = new StoreHouse();
                sh.setItemId(item.getId());
                sh.setPlayerId(playerDto.playerId);
                sh.setLv(item.getLevel());
                sh.setOwner(0);
                sh.setType(1);
                sh.setGoodsType(item.getType());
                sh.setAttribute(new StringBuilder().append(item.getAttribute()).toString());
                sh.setQuality(item.getQuality());
                sh.setGemId(0);
                sh.setNum(1);
                sh.setState(0);
                sh.setRefreshAttribute("");
                sh.setSpecialSkillId(0);
                sh.setQuenchingTimes(0);
                sh.setBindExpireTime(0L);
                sh.setMarkId(0);
                this.storeHouseDao.create(sh);
                doc.startObject();
                doc.createElement("name", item.getName());
                doc.createElement("pic", item.getPic());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            this.playerVipTxDao.updateRookieStatus(playerDto.playerId, 1);
        }
        else {
            if (type != 4) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.TENCENT_NO_SUCH_REWARD);
            }
            if (list == null || seq > list.size() || seq <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.TENCENT_INVALID_PARAMS);
            }
            final GiftTx giftTx2 = list.get(seq - 1);
            final int upLv = giftTx2.getLv();
            if (playerDto.playerLv < upLv) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.TENCENT_LEVEL_NO_REACHED);
            }
            final int food = giftTx2.getFood();
            final int recruitToken = giftTx2.getRecruitToken();
            final int freeCons = giftTx2.getFreeCons();
            doc.startObject();
            if (food > 0) {
                this.playerResourceDao.addFoodIgnoreMax(playerDto.playerId, food, "\u817e\u8baf\u9ec4\u94bb\u793c\u5305\u83b7\u5f97\u7cae\u98df");
                doc.createElement("food", food);
            }
            if (recruitToken > 0) {
                this.playerAttributeDao.addRecruitToken(playerDto.playerId, recruitToken, "\u817e\u8baf\u9ec4\u94bb\u793c\u5305\u83b7\u5f97\u52df\u5175\u4ee4");
                doc.createElement("recruitToken", recruitToken);
            }
            if (freeCons > 0) {
                this.playerAttributeDao.addFreeConstructionNum(playerDto.playerId, freeCons, "\u817e\u8baf\u9ec4\u94bb\u793c\u5305\u83b7\u5f97\u9ec4\u91d1\u9524");
                doc.createElement("freeCons", freeCons);
            }
            doc.endObject();
            this.playerVipTxDao.updateUgradeStatus(playerDto.playerId, 1, seq);
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void resetTxVipStatus() {
        final long start = System.currentTimeMillis();
        this.playerVipTxDao.resetDailyStatus();
        PayService.timerLog.info(LogUtil.formatThreadLog("PayService", "resetTxVipStatus", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public void addTxYellowVipInfo(final int playerId, final JsonDocument doc) {
        final Player player = this.playerDao.read(playerId);
        if (this.yxOperation.checkTencentPf(player.getYx())) {
            final PlayerVipTx pvt = this.playerVipTxDao.getByPlayerId(playerId);
            if (pvt != null) {
                doc.createElement("isYellowVip", true);
                doc.createElement("yellowVipLv", pvt.getYellowVipLv());
            }
        }
    }
}
