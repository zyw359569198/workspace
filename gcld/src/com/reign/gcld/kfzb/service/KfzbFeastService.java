package com.reign.gcld.kfzb.service;

import org.springframework.stereotype.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.gcld.kfzb.dao.*;
import com.reign.gcld.system.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.kf.comm.transfer.oio.*;
import com.reign.gcld.log.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.kf.comm.transfer.*;
import java.util.concurrent.*;
import com.reign.framework.json.*;
import com.reign.gcld.kfzb.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.dto.response.*;
import com.reign.gcld.common.*;
import com.reign.gcld.system.domain.*;
import java.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.kfgz.domain.*;
import com.reign.util.*;

@Component("kfzbFeastService")
public class KfzbFeastService implements IKfzbFeastService
{
    private static Log feastLogger;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IKfgzSeasonService kfgzSeasonService;
    @Autowired
    private IKfzbFeastDao kfzbFeastDao;
    @Autowired
    private IDbVersionDao dbVersionDao;
    private static final Logger dayReportLogger;
    private static final long sleepTimeSlow = 3000L;
    private static final int GOLD_DRINK = 500;
    private static final int GOLD_CARD_INIT = 20;
    private static final int GOLD_CARD_INC = 2;
    private static final int CARD_1 = 1;
    private static final int CARD_10 = 10;
    private static final int BASE_TICKET_NO_DRINK_FREE = 500;
    private static final int BASE_TICKET_NO_DRINK_GOLD = 2500;
    private static final int BASE_TICKET_HAVE_DRINK_FREE = 800;
    private static final int BASE_TICKET_HAVE_DRINK_GOLD = 4000;
    private static final long CD = 180000L;
    private static Map<Integer, Integer> cardMap;
    private static int INIT_FREE_CARD;
    private static Map<Integer, Integer> peopleNumIdMap;
    private static Map<Integer, Integer> extTicketMap;
    private Map<Integer, Integer> cardTypeMap;
    Map<Integer, Long> playerIdRoomIdMap;
    Map<Long, KfzbRoomInfo> roomMap;
    Map<Integer, KfzbFeastParticipator> listMap;
    private SyncFeastThread syncFeastThread;
    private KfzbFeastInfo kfzbFeastInfo;
    private KfzbWinnerInfo kfzbWinnerInfo;
    private Map<Integer, Integer> playerIdMap;
    private Map<Integer, Integer> playerIdReverseMap;
    private Map<Integer, Integer> top16Map;
    private Map<Integer, Integer> top16ReverseMap;
    private static KfConnection connectionGW;
    
    static {
        KfzbFeastService.feastLogger = new FeastLogger();
        dayReportLogger = new DayReportLogger();
        (KfzbFeastService.cardMap = new HashMap<Integer, Integer>()).put(1, 1);
        KfzbFeastService.cardMap.put(2, 10);
        KfzbFeastService.INIT_FREE_CARD = 10;
        (KfzbFeastService.peopleNumIdMap = new HashMap<Integer, Integer>()).put(0, 1);
        KfzbFeastService.peopleNumIdMap.put(1, 1);
        KfzbFeastService.peopleNumIdMap.put(2, 1);
        KfzbFeastService.peopleNumIdMap.put(3, 1);
        KfzbFeastService.peopleNumIdMap.put(4, 1);
        KfzbFeastService.peopleNumIdMap.put(5, 1);
        KfzbFeastService.peopleNumIdMap.put(6, 2);
        KfzbFeastService.peopleNumIdMap.put(7, 3);
        KfzbFeastService.peopleNumIdMap.put(8, 4);
        KfzbFeastService.peopleNumIdMap.put(9, 5);
        KfzbFeastService.peopleNumIdMap.put(10, 6);
        KfzbFeastService.peopleNumIdMap.put(11, 7);
        (KfzbFeastService.extTicketMap = new HashMap<Integer, Integer>()).put(1, 0);
        KfzbFeastService.extTicketMap.put(2, 600);
        KfzbFeastService.extTicketMap.put(3, 700);
        KfzbFeastService.extTicketMap.put(4, 800);
        KfzbFeastService.extTicketMap.put(5, 900);
        KfzbFeastService.extTicketMap.put(6, 1000);
        KfzbFeastService.extTicketMap.put(7, 1200);
        KfzbFeastService.connectionGW = new KfConnection((TransferConfig)new TransferConfigGW(), KfzbFeastService.feastLogger);
    }
    
    public KfzbFeastService() {
        this.cardTypeMap = new ConcurrentHashMap<Integer, Integer>();
        this.playerIdRoomIdMap = new ConcurrentHashMap<Integer, Long>();
        this.roomMap = new ConcurrentHashMap<Long, KfzbRoomInfo>();
        this.listMap = new ConcurrentHashMap<Integer, KfzbFeastParticipator>();
        this.syncFeastThread = null;
        this.kfzbFeastInfo = null;
        this.kfzbWinnerInfo = null;
        this.playerIdMap = new ConcurrentHashMap<Integer, Integer>();
        this.playerIdReverseMap = new ConcurrentHashMap<Integer, Integer>();
        this.top16Map = new ConcurrentHashMap<Integer, Integer>();
        this.top16ReverseMap = new ConcurrentHashMap<Integer, Integer>();
    }
    
    @Override
    public void init() {
        if (this.syncFeastThread == null) {
            (this.syncFeastThread = new SyncFeastThread()).start();
        }
    }
    
    @Override
    public KfzbFeastInfo getKfzbFeastInfo() {
        return this.kfzbFeastInfo;
    }
    
    @Override
    public boolean inFeast() {
        return this.kfzbFeastInfo != null && this.kfzbFeastInfo.getState() == 1;
    }
    
    @Override
    public byte[] getFeastInfo(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (!this.inFeast()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFZB_FEAST_NO_FEAST);
        }
        final KfzbWinnerInfo kfzbWinnerInfo = this.getKfzbWinnerInfo();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rooms");
        final Map<Integer, KfzbTopPlayerInfo> tpiMap = kfzbWinnerInfo.getKfzbWinnerMap();
        final Map<Integer, KfzbFeastOrganizer> kfoMap = this.kfzbFeastInfo.getMap();
        for (int rank = 1; rank <= 16; ++rank) {
            final KfzbTopPlayerInfo tpi = tpiMap.get(rank);
            doc.startObject();
            final int playerId = this.getPlayerId(tpi.getCompetitorId());
            doc.createElement("playerId", playerId);
            doc.createElement("playerName", tpi.getPlayerName());
            doc.createElement("pos", tpi.getPos());
            final KfzbFeastOrganizer kfo = kfoMap.get(rank);
            doc.createElement("weiNum", kfo.getWeiNum());
            doc.createElement("shuNum", kfo.getShuNum());
            doc.createElement("wuNum", kfo.getWuNum());
            doc.createElement("haveDrink", kfo.getGoldFeastRemainTime());
            doc.endObject();
        }
        doc.endArray();
        KfzbFeast kf = this.kfzbFeastDao.read(playerDto.playerId);
        if (kf == null) {
            this.initKfzbFeast(playerDto.playerId);
            kf = this.kfzbFeastDao.read(playerDto.playerId);
        }
        final Integer pos = this.top16Map.get(playerDto.playerId);
        final boolean isTop16 = pos != null;
        doc.createElement("isTop16", isTop16);
        doc.createElement("freeCard", kf.getFreeCard());
        doc.createElement("goldCard", kf.getGoldCard());
        if (isTop16) {
            final KfzbFeastOrganizer kfo = kfoMap.get(pos);
            doc.createElement("drink", kfo.getGoldFeastRemainTime());
        }
        doc.createElement("goldCard1", this.getNeedGold(kf.getBuyCard(), 1));
        doc.createElement("goldCard10", this.getNeedGold(kf.getBuyCard(), 10));
        doc.createElement("goldDrink", 500);
        final int xq = kf.getXiaoqian();
        if (xq == 0) {
            this.kfzbFeastDao.setXiqoqian(1);
        }
        doc.createElement("xixoqian", xq);
        doc.createElement("inRoom", (this.playerIdRoomIdMap.get(playerDto.playerId) != null) ? 1 : 0);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] buyDrink(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (!this.inFeast()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFZB_FEAST_NO_FEAST);
        }
        final int playerId = playerDto.playerId;
        if (this.top16Map.get(playerId) == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFZB_FEAST_NOT_TOP16);
        }
        if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerId), 500, "\u8de8\u670d\u4e89\u9738\u8d5b\u5bb4\u4f1a\u8d2d\u4e70\u9152\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final KfzbFeast kf = this.kfzbFeastDao.read(playerId);
        if (kf == null) {
            this.initKfzbFeast(playerId);
        }
        this.kfzbFeastDao.addDrink(playerDto.playerId, 500);
        final int drinkNum = this.kfzbFeastDao.getDrink(playerId);
        this.sendBuyDrinkInfo(this.top16Map.get(playerId), drinkNum);
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("drinkNum", 500));
    }
    
    @Override
    public byte[] buyCard(final PlayerDto playerDto, final int type) {
        if (type < 1 || type > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (!this.inFeast()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFZB_FEAST_NO_FEAST);
        }
        KfzbFeast kf = this.kfzbFeastDao.read(playerDto.playerId);
        if (kf == null) {
            this.initKfzbFeast(playerDto.playerId);
            kf = this.kfzbFeastDao.read(playerDto.playerId);
        }
        final int card = KfzbFeastService.cardMap.get(type);
        final int gold = this.getNeedGold(kf.getBuyCard(), card);
        if (!this.dataGetter.getPlayerDao().consumeGold(this.dataGetter.getPlayerDao().read(playerDto.playerId), gold, "\u8de8\u670d\u4e89\u9738\u8d5b\u5bb4\u4f1a\u8d2d\u4e70\u8bf7\u5e16\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.kfzbFeastDao.addGoldCard(playerDto.playerId, card);
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("card", card));
    }
    
    private void sendBuyDrinkInfo(final int pos, final int drink) {
        final Request request = new Request();
        request.setCommand(Command.SI_ORGANIZER_ADDFEAST);
        final KfzbFeastOrganizer kfzbFeastOrganizer = new KfzbFeastOrganizer();
        kfzbFeastOrganizer.setPos(pos);
        kfzbFeastOrganizer.setGoldAddFeastTimes(drink);
        request.setMessage(kfzbFeastOrganizer);
        Response response = null;
        try {
            response = KfzbFeastService.connectionGW.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("response is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestSendBuyDrinkInfo").flush();
        }
        final Integer result = (Integer)response.getMessage();
        if (!"1".equals(result)) {
            KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:sendBuyDrinkInfo#result:" + result);
        }
    }
    
    @Override
    public byte[] getRoomInfo(final PlayerDto playerDto, final int pos, final int cardType) {
        final int playerId = playerDto.playerId;
        final Long roomId = this.playerIdRoomIdMap.get(playerId);
        if ((pos < 1 || pos > 16 || cardType < 1 || cardType > 2) && roomId == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (!this.inFeast()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.KFZB_FEAST_NO_FEAST);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rooms");
        int realPos = pos;
        boolean drink = false;
        long cd = 0L;
        if (roomId == null) {
            KfzbFeast kf = this.kfzbFeastDao.read(playerId);
            if (kf == null) {
                this.initKfzbFeast(playerId);
                kf = this.kfzbFeastDao.read(playerId);
            }
            if (1 == cardType) {
                if (kf.getFreeCard() <= 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.KFZB_FEAST_NO_CARD);
                }
            }
            else if (kf.getGoldCard() <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.KFZB_FEAST_NO_CARD);
            }
            this.cardTypeMap.put(playerId, cardType);
            if (!this.listMap.containsKey(playerId)) {
                final KfzbFeastParticipator kfp = new KfzbFeastParticipator();
                kfp.setPlayerId(this.getCompetitorId(playerId));
                kfp.setPlayerName(playerDto.playerName);
                kfp.setNation(playerDto.forceId);
                kfp.setServerId(Configuration.getProperty(playerDto.yx, "gcld.serverid"));
                kfp.setServerName(Configuration.getProperty(playerDto.yx, "gcld.showservername"));
                kfp.setRank(pos);
                this.listMap.put(playerId, kfp);
            }
            doc.startObject();
            doc.createElement("playerName", playerDto.playerName);
            doc.createElement("forceId", playerDto.forceId);
            doc.endObject();
            cd = 180000L;
        }
        else {
            final KfzbRoomInfo kri = this.roomMap.get(roomId);
            if (kri == null) {
                KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:getRoomInfo#playerId:" + playerId + "#playerName:" + playerDto.playerName + "#roomId" + roomId);
            }
            for (final KfzbFeastParticipator kpi : kri.getList()) {
                doc.startObject();
                doc.createElement("playerName", kpi.getPlayerName());
                doc.createElement("forceId", kpi.getNation());
                doc.endObject();
            }
            realPos = kri.getRoomRank();
            if (kri.getBuff() == 1) {
                drink = true;
            }
            cd = 180000L + TimeUtil.now2specMs(kri.getCreateDate().getTime());
        }
        doc.endArray();
        doc.createElement("drink", drink ? 1 : 0);
        doc.createElement("pos", realPos);
        doc.createElement("cd", (cd < 0L) ? 0L : cd);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void addFreeCard(final int playerId, final int num) {
        final KfzbFeast kf = this.kfzbFeastDao.read(playerId);
        if (kf == null) {
            this.initKfzbFeast(playerId);
        }
        this.kfzbFeastDao.addFreeCard(playerId, num);
    }
    
    private KfzbWinnerInfo getKfzbWinnerInfo() {
        if (this.kfzbWinnerInfo != null) {
            return this.kfzbWinnerInfo;
        }
        final Request request = new Request();
        request.setCommand(Command.KFZB_GETTOP16PLAYERINFO);
        final GameServerEntity gameServerEntity = new GameServerEntity();
        gameServerEntity.setServerKey(Configuration.getProperty(Configuration.SERVER_KEY));
        request.setMessage(gameServerEntity);
        Response response = null;
        try {
            response = KfzbFeastService.connectionGW.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (response == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("response is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestKfzbWinnerInfoFromGW").flush();
            return null;
        }
        final KfzbWinnerInfo kfzbWinnerInfo = (KfzbWinnerInfo)response.getMessage();
        this.kfzbWinnerInfo = kfzbWinnerInfo;
        for (final KfzbTopPlayerInfo tpi : kfzbWinnerInfo.getList()) {
            final int playerId = this.getPlayerId(tpi.getCompetitorId());
            this.top16Map.put(playerId, tpi.getPos());
            this.top16ReverseMap.put(tpi.getPos(), playerId);
        }
        return kfzbWinnerInfo;
    }
    
    private void joinRoom() {
        final Request request = new Request();
        request.setCommand(Command.SI_PATICIPATEFEAST);
        final KfzbFeastParticipateInfo kfzbFeastParticipateInfo = new KfzbFeastParticipateInfo();
        kfzbFeastParticipateInfo.setList(new ArrayList(this.listMap.values()));
        request.setMessage(kfzbFeastParticipateInfo);
        Response response = null;
        try {
            response = KfzbFeastService.connectionGW.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("response is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("joinRoom").flush();
        }
        final KfzbFeastPlayerRoomInfo kfzbFeastPlayerRoomInfo = (KfzbFeastPlayerRoomInfo)response.getMessage();
        if (kfzbFeastPlayerRoomInfo == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("kfzbFeastPlayerRoomInfo is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("joinRoom").flush();
        }
        else {
            final Map<Integer, Long> map = kfzbFeastPlayerRoomInfo.getMap();
            for (final Map.Entry<Integer, Long> entry : map.entrySet()) {
                final int cId = entry.getKey();
                final int playerId = this.getPlayerId(cId);
                final Long roomId = entry.getValue();
                this.playerIdRoomIdMap.put(playerId, roomId);
            }
        }
    }
    
    private KfzbRoomInfoList getKfzbRoomInfoList(final KfzbRoomKeyList keyList) {
        final Request request = new Request();
        request.setCommand(Command.SI_FEASTROOMINFO);
        request.setMessage(keyList);
        Response response = null;
        try {
            response = KfzbFeastService.connectionGW.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (response == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("response is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("getKfzbRoomInfoList").flush();
            return null;
        }
        final KfzbRoomInfoList kfzbRoomInfoList = (KfzbRoomInfoList)response.getMessage();
        if (kfzbRoomInfoList == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("KfzbRoomInfoList is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("getKfzbRoomInfoList").flush();
        }
        return kfzbRoomInfoList;
    }
    
    private KfzbFeastInfo requestKfzbFeastInfoFromGW() {
        final Request request = new Request();
        request.setCommand(Command.SI_SYNFEAST_INFO);
        final GameServerEntity gameServerEntity = new GameServerEntity();
        gameServerEntity.setServerKey(Configuration.getProperty(Configuration.SERVER_KEY));
        request.setMessage(gameServerEntity);
        Response response = null;
        try {
            response = KfzbFeastService.connectionGW.sendSyncAndGetResponse(request);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (response == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("response is null").appendClassName(this.getClass().getSimpleName()).appendMethodName("requestSeasonInfoFromGW").flush();
            return null;
        }
        final KfzbFeastInfo kfzbFeastInfo = (KfzbFeastInfo)response.getMessage();
        return kfzbFeastInfo;
    }
    
    private void handleNewlyKfzbFeastInfo(final KfzbFeastInfo newKfzbFeastInfo) {
        try {
            if (newKfzbFeastInfo == null || newKfzbFeastInfo.getState() == 0) {
                return;
            }
            if ((this.kfzbFeastInfo == null || this.kfzbFeastInfo.getState() != 1) && newKfzbFeastInfo != null && newKfzbFeastInfo.getState() == 1) {
                final byte[] send = JsonBuilder.getSimpleJson("haveFeast", 1);
                for (final PlayerDto playerDto : Players.getAllPlayer()) {
                    if (playerDto.cs[10] == '1') {
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                    }
                }
            }
            else if (this.kfzbFeastInfo != null && this.kfzbFeastInfo.getState() < 2 && newKfzbFeastInfo != null && newKfzbFeastInfo.getState() >= 2) {
                this.endFeast(newKfzbFeastInfo);
            }
            int beforeSeasonId = 0;
            boolean addFreeCard = true;
            if (this.kfzbFeastInfo != null) {
                beforeSeasonId = this.kfzbFeastInfo.getSeasonId();
            }
            else {
                final List<DbVersion> list = this.dbVersionDao.getModels();
                if (list != null && list.size() > 0) {
                    beforeSeasonId = list.get(0).getSeasonId();
                }
                else {
                    KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:handleNewlyKfzbFeastInfo#dbversion is null");
                    addFreeCard = false;
                }
            }
            if (newKfzbFeastInfo != null && newKfzbFeastInfo.getState() == 1) {
                final int newSeasonId = newKfzbFeastInfo.getSeasonId();
                if (addFreeCard && beforeSeasonId != newSeasonId) {
                    this.startFeast();
                    this.dbVersionDao.updateSeasonId(newSeasonId);
                }
            }
            this.kfzbFeastInfo = newKfzbFeastInfo;
            if (this.kfzbFeastInfo != null && this.kfzbFeastInfo.getState() == 1) {
                if (this.listMap.size() > 0) {
                    this.joinRoom();
                    this.listMap.clear();
                }
                final KfzbRoomKeyList keyList = new KfzbRoomKeyList();
                keyList.setList(new HashSet(this.playerIdRoomIdMap.values()));
                final KfzbRoomInfoList kroiList = this.getKfzbRoomInfoList(keyList);
                if (kroiList != null) {
                    for (final KfzbRoomInfo kri : kroiList.getList()) {
                        final List<KfzbFeastParticipator> list2 = kri.getList();
                        final int state = kri.getRoomState();
                        boolean remove = false;
                        if (state == 3) {
                            remove = true;
                            final byte[] send2 = JsonBuilder.getSimpleJson("kfzbFeastState", 3);
                            for (final KfzbFeastParticipator fpi : list2) {
                                final int cId = fpi.getPlayerId();
                                final Integer playerId = this.getPlayerId(cId);
                                if (playerId != null) {
                                    Players.push(playerId, PushCommand.PUSH_UPDATE, send2);
                                }
                            }
                        }
                        else if (state == 2) {
                            remove = true;
                            final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                            map.put(1, 0);
                            map.put(2, 0);
                            map.put(3, 0);
                            for (final KfzbFeastParticipator fpi : list2) {
                                final int forceId = fpi.getNation();
                                final int before = map.get(forceId);
                                map.put(forceId, before + 1);
                            }
                            final Map<Integer, Integer> titleMap = new HashMap<Integer, Integer>();
                            boolean have9 = false;
                            boolean have10 = false;
                            for (final Integer temp : map.values()) {
                                if (temp.equals(9)) {
                                    have9 = true;
                                }
                                else {
                                    if (!temp.equals(1)) {
                                        continue;
                                    }
                                    have10 = true;
                                }
                            }
                            if (have9 && have10) {
                                for (int forceId2 = 1; forceId2 <= 3; ++forceId2) {
                                    final int num = map.get(forceId2);
                                    if (1 == num) {
                                        titleMap.put(forceId2, 7);
                                    }
                                    else {
                                        titleMap.put(forceId2, KfzbFeastService.peopleNumIdMap.get(num));
                                    }
                                }
                            }
                            else {
                                for (int forceId2 = 1; forceId2 <= 3; ++forceId2) {
                                    titleMap.put(forceId2, KfzbFeastService.peopleNumIdMap.get(map.get(forceId2)));
                                }
                            }
                            final JsonDocument doc2 = new JsonDocument();
                            doc2.startArray("rooms");
                            for (final KfzbFeastParticipator kpi : kri.getList()) {
                                doc2.startObject();
                                doc2.createElement("playerName", kpi.getPlayerName());
                                doc2.createElement("forceId", kpi.getNation());
                                doc2.endObject();
                            }
                            doc2.endArray();
                            boolean drink = false;
                            if (kri.getBuff() == 1) {
                                drink = true;
                            }
                            doc2.createElement("drink", drink ? 1 : 0);
                            doc2.createElement("pos", kri.getRoomRank());
                            for (final KfzbFeastParticipator fpi2 : list2) {
                                final int cId2 = fpi2.getPlayerId();
                                final Integer playerId2 = this.getPlayerId(cId2);
                                if (playerId2 != null) {
                                    final int forceId3 = fpi2.getNation();
                                    int ticket = 0;
                                    final Integer cardType = this.cardTypeMap.get(playerId2);
                                    if (cardType == null) {
                                        KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:handleNewlyKfzbFeastInfo#playerId:" + playerId2 + "#cardType_is_null");
                                    }
                                    else {
                                        if (cardType.equals(1)) {
                                            final int result = this.kfzbFeastDao.consumeFreeCard(playerId2);
                                            if (result < 1) {
                                                KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:handleNewlyKfzbFeastInfo#playerId:" + playerId2 + "#freeCard_is_0");
                                                continue;
                                            }
                                            if (kri.getBuff() == 1) {
                                                ticket = 800;
                                            }
                                            else {
                                                ticket = 500;
                                            }
                                        }
                                        else {
                                            final int result = this.kfzbFeastDao.consumeGoldCard(playerId2);
                                            if (result < 1) {
                                                KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:handleNewlyKfzbFeastInfo#playerId:" + playerId2 + "#goldCard_is_0");
                                                continue;
                                            }
                                            if (kri.getBuff() == 1) {
                                                ticket = 4000;
                                            }
                                            else {
                                                ticket = 2500;
                                            }
                                        }
                                        this.cardTypeMap.remove(playerId2);
                                        final int titleId = titleMap.get(forceId3);
                                        ticket += KfzbFeastService.extTicketMap.get(titleId);
                                        this.dataGetter.getPlayerTicketsDao().addTickets(playerId2, ticket, "\u8de8\u670d\u4e89\u9738\u76db\u5bb4\u83b7\u5f97\u70b9\u5238", true);
                                        final JsonDocument doc3 = new JsonDocument();
                                        doc3.startObject();
                                        doc3.createElement("kfzbFeastState", 2);
                                        doc3.createElement("titieId", titleId);
                                        doc3.createElement("ticket", ticket);
                                        doc3.createElement("cardType", cardType);
                                        doc3.appendJson(doc2.toByte());
                                        doc3.endObject();
                                        Players.push(playerId2, PushCommand.PUSH_UPDATE, doc3.toByte());
                                    }
                                }
                            }
                        }
                        else if (state == 1) {
                            final KfzbRoomInfo oldKri = this.roomMap.get(kri.getRoomId());
                            final int size = list2.size();
                            if ((oldKri == null && size > 0) || size > oldKri.getList().size()) {
                                final byte[] send3 = JsonBuilder.getSimpleJson("kfzbFeastState", 1);
                                for (final KfzbFeastParticipator fpi3 : list2) {
                                    final int cId3 = fpi3.getPlayerId();
                                    final Integer playerId3 = this.getPlayerId(cId3);
                                    if (playerId3 != null) {
                                        Players.push(playerId3, PushCommand.PUSH_UPDATE, send3);
                                    }
                                }
                            }
                            this.roomMap.put(kri.getRoomId(), kri);
                        }
                        if (remove) {
                            final List<Integer> removeList = new ArrayList<Integer>();
                            for (final Map.Entry<Integer, Long> entry : this.playerIdRoomIdMap.entrySet()) {
                                if (entry.getValue().equals(kri.getRoomId())) {
                                    removeList.add(entry.getKey());
                                }
                            }
                            for (final Integer playerId4 : removeList) {
                                this.playerIdRoomIdMap.remove(playerId4);
                            }
                            this.roomMap.remove(kri.getRoomId());
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void startFeast() {
        try {
            KfzbFeastService.feastLogger.info("startFeat");
            this.kfzbFeastDao.clearData();
            this.kfzbFeastDao.setFreeCardInit(KfzbFeastService.INIT_FREE_CARD);
            final byte[] send = JsonBuilder.getSimpleJson("haveFeast", 1);
            for (final PlayerDto playerDto : Players.getAllPlayer()) {
                if (playerDto.cs[10] == '1') {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                }
            }
            KfzbFeastService.feastLogger.info("startFeatSucces");
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private void endFeast(final KfzbFeastInfo kfi) {
        try {
            KfzbFeastService.feastLogger.info("endFeastStart");
            final byte[] send = JsonBuilder.getSimpleJson("haveFeast", 0);
            for (final PlayerDto playerDto : Players.getAllPlayer()) {
                if (playerDto.cs[10] == '1') {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                }
            }
            int pos = 1;
            int num = 0;
            final Map<Integer, KfzbFeastOrganizer> map = kfi.getMap();
            for (int i = 1; i <= 16; ++i) {
                final KfzbFeastOrganizer kfo = map.get(i);
                KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:endFeast#pos_i:" + i + "#feastTime:" + kfo.getFeastTimes());
                if (kfo.getFeastTimes() >= num) {
                    pos = kfo.getPos();
                    num = kfo.getFeastTimes();
                }
            }
            final int playerId = this.top16ReverseMap.get(pos);
            KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:endFeast#pos:" + pos + "#playerId:" + playerId);
            if (playerId > 0) {
                PlayerDto dto = Players.getPlayer(playerId);
                if (dto == null) {
                    dto = PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId));
                }
                final int ID = 6;
                final int TONG = 25;
                final int YONG = 25;
                this.dataGetter.getTreasureService().tryGetGeneralTreasure(dto, ID, true, TONG, YONG, false, "\u8de8\u670d\u4e89\u9738\u76db\u5bb4\u4eba\u6c14\u738b\u83b7\u5f97\u5fa1\u5b9d");
                final String msg = MessageFormatter.format(LocalMessages.KFZB_FEAST_MAIL_CONTENT, new Object[] { TONG, YONG });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.KFZB_FEAST_MAIL_TITLE, msg, 1, playerId, 0);
                final KfzbTopPlayerInfo tpi = this.kfzbWinnerInfo.getKfzbWinnerMap().get(pos);
                final int forceId = tpi.getNation();
                final int INCENSE_NUM = 30;
                this.dataGetter.getPlayerIncenseDao().addIncenseNumByForceId(forceId, INCENSE_NUM);
                KfzbFeastService.feastLogger.error("class:KfzbFeastService#method:endFeast#playerId:" + dto.playerId + "#playerName:" + dto.playerName + "#forceId:" + forceId + "#incense_num:" + INCENSE_NUM);
            }
            this.kfzbFeastDao.clearData();
            this.cardTypeMap.clear();
            this.playerIdRoomIdMap.clear();
            this.roomMap.clear();
            this.listMap.clear();
            this.playerIdMap.clear();
            this.playerIdReverseMap.clear();
            this.top16Map.clear();
            this.top16ReverseMap.clear();
            this.kfzbWinnerInfo = null;
            KfzbFeastService.feastLogger.info("endFeastEnd");
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("", e);
        }
    }
    
    private int getNeedGold(final int currentBuyNum, final int needNum) {
        int temp = currentBuyNum;
        int gold = 0;
        for (int i = 1; i <= needNum; ++i) {
            gold += 20 + temp * 2;
            ++temp;
        }
        return gold;
    }
    
    private int getCompetitorId(final int playerId) {
        Integer competitorId = this.playerIdMap.get(playerId);
        if (competitorId != null) {
            return competitorId;
        }
        KfgzSignup kfgzSignup = this.dataGetter.getKfgzSignupDao().read(playerId);
        if (kfgzSignup == null) {
            final KfgzSignup cidFromGW = this.kfgzSeasonService.requestCidFromGW(playerId);
            if (cidFromGW == null) {
                ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + "#method:getCompetitorId#" + LocalMessages.KF_ZB_CID_ERROR_FROM_GW);
                return 0;
            }
            kfgzSignup = cidFromGW;
        }
        competitorId = kfgzSignup.getCompetitorId();
        this.playerIdMap.put(playerId, competitorId);
        this.playerIdReverseMap.put(competitorId, playerId);
        return competitorId;
    }
    
    private int getPlayerId(final int competitorId) {
        Integer playerId = this.playerIdReverseMap.get(competitorId);
        if (playerId != null) {
            return playerId;
        }
        final KfgzSignup kfgzSignup = this.dataGetter.getKfgzSignupDao().getByCid(competitorId);
        if (kfgzSignup == null) {
            return 0;
        }
        playerId = kfgzSignup.getPlayerId();
        this.playerIdMap.put(playerId, competitorId);
        this.playerIdReverseMap.put(competitorId, playerId);
        return playerId;
    }
    
    private void initKfzbFeast(final int playerId) {
        KfzbFeast kf = this.kfzbFeastDao.read(playerId);
        if (kf == null) {
            kf = new KfzbFeast();
            kf.setPlayerId(playerId);
            if (this.inFeast()) {
                kf.setFreeCard(KfzbFeastService.INIT_FREE_CARD);
            }
            else {
                kf.setFreeCard(0);
            }
            kf.setGoldCard(0);
            kf.setBuyCard(0);
            kf.setDrinkNum(0);
            kf.setXiaoqian(0);
            this.kfzbFeastDao.create(kf);
        }
    }
    
    public static void main(final String[] args) {
        final JsonDocument doc2 = new JsonDocument();
        doc2.startArray("rooms");
        for (int i = 1; i <= 2; ++i) {
            doc2.startObject();
            doc2.createElement("key1", 2);
            doc2.createElement("key2", 4);
            doc2.endObject();
        }
        doc2.endArray();
        final JsonDocument doc3 = new JsonDocument();
        doc3.startObject();
        doc3.createElement("test1", 1);
        doc3.appendJson(doc2.toByte());
        doc3.createElement("test2", 2);
        doc3.endObject();
        System.out.println(new String(doc3.toByte()));
    }
    
    private class SyncFeastThread extends Thread
    {
        public SyncFeastThread() {
            super("KfgzFeastService-SyncFeastThread");
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    final KfzbFeastInfo kfzbFeastInfo = KfzbFeastService.this.requestKfzbFeastInfoFromGW();
                    KfzbFeastService.this.handleNewlyKfzbFeastInfo(kfzbFeastInfo);
                    for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                        KfzbFeastService.dayReportLogger.info(log);
                    }
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run catch Exception", e);
                    ThreadLocalFactory.clearTreadLocalLog();
                    ThreadLocalFactory.getTreadLocalLog();
                    try {
                        Thread.sleep(3000L);
                    }
                    catch (InterruptedException e2) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch InterruptedException", e2);
                    }
                    catch (Exception e3) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e3);
                    }
                    continue;
                }
                finally {
                    ThreadLocalFactory.clearTreadLocalLog();
                    ThreadLocalFactory.getTreadLocalLog();
                    try {
                        Thread.sleep(3000L);
                    }
                    catch (InterruptedException e2) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch InterruptedException", e2);
                    }
                    catch (Exception e3) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e3);
                    }
                }
                ThreadLocalFactory.clearTreadLocalLog();
                ThreadLocalFactory.getTreadLocalLog();
                try {
                    Thread.sleep(3000L);
                }
                catch (InterruptedException e2) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch InterruptedException", e2);
                }
                catch (Exception e3) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e3);
                }
            }
        }
    }
}
