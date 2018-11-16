package com.reign.gcld.dinner.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.message.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.dinner.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.action.*;
import java.util.*;

@Component("dinnerService")
public class DinnerService implements IDinnerService
{
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private DinnerChatCache dinnerChatCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private TechCache techCache;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private TroopConscribeSpeedCache troopConscribeSpeedCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IDataGetter dataGetter;
    private static final Logger timerLog;
    private static final Logger log;
    
    static {
        timerLog = new TimerLogger();
        log = CommonLog.getLog(DinnerService.class);
    }
    
    @Transactional
    @Override
    public void addDinnerNum() {
        final long start = System.currentTimeMillis();
        this.dataGetter.getPlayerDinnerDao().addDinnerNum(6);
        this.pushDinnerData();
        DinnerService.timerLog.info(LogUtil.formatThreadLog("DinnerService", "addDinnerNum", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Transactional
    @Override
    public byte[] getDinnerInfo(final PlayerDto playerDto) {
        final int dinnerNum = this.dataGetter.getPlayerDinnerDao().getDinnerNum(playerDto.playerId);
        TaskMessageHelper.sendDinnerMessage(playerDto.playerId, 0);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (dinnerNum <= 0) {
            doc.createElement("dinnerNum", 0);
            doc.createElement("millisecond", this.getNextTime());
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        doc.createElement("dinnerNum", dinnerNum);
        final List<PlayerGeneralMilitary> playerGeneralMilitary = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerDto.playerId);
        final List<PlayerGeneralMilitary> pgm1 = new ArrayList<PlayerGeneralMilitary>();
        final List<PlayerGeneralMilitary> pgm2 = new ArrayList<PlayerGeneralMilitary>();
        int count = 0;
        for (final PlayerGeneralMilitary pgm3 : playerGeneralMilitary) {
            final int state = pgm3.getState();
            if (state > 1) {
                pgm2.add(pgm3);
            }
            else {
                pgm1.add(pgm3);
                if (pgm3.getForces() >= this.battleDataCache.getMaxHp(pgm3)) {
                    continue;
                }
                ++count;
            }
        }
        if (pgm1.size() != 0 && count == 0) {
            doc.createElement("isFull", 1);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        doc.createElement("isFull", 0);
        int pos = 1;
        doc.startArray("generals");
        for (final PlayerGeneralMilitary pgm4 : pgm1) {
            doc.startObject();
            doc.createElement("type", 1);
            doc.createElement("pos", (pos++));
            doc.createElement("generalId", pgm4.getGeneralId());
            doc.createElement("name", ((General)this.generalCache.get((Object)pgm4.getGeneralId())).getName());
            doc.createElement("totalForces", this.battleDataCache.getMaxHp(pgm4));
            doc.createElement("forces", pgm4.getForces());
            doc.endObject();
        }
        for (final PlayerGeneralMilitary pgm4 : pgm2) {
            doc.startObject();
            doc.createElement("type", 0);
            doc.createElement("pos", (pos++));
            doc.createElement("generalId", pgm4.getGeneralId());
            doc.createElement("name", ((General)this.generalCache.get((Object)pgm4.getGeneralId())).getName());
            doc.createElement("reason", this.getReason(pgm4.getState()));
            doc.endObject();
        }
        doc.endArray();
        Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)26);
        doc.startArray("liqueur");
        doc.startObject();
        doc.createElement("liqueurId", 1);
        doc.createElement("name", ci.getName());
        doc.createElement("cost", ci.getCost());
        doc.createElement("rate", ci.getParam());
        doc.endObject();
        ci = (Chargeitem)this.chargeitemCache.get((Object)27);
        doc.startObject();
        doc.createElement("liqueurId", 2);
        doc.createElement("name", ci.getName());
        doc.createElement("cost", ci.getCost());
        doc.createElement("rate", ci.getParam());
        doc.endObject();
        ci = (Chargeitem)this.chargeitemCache.get((Object)28);
        doc.startObject();
        doc.createElement("liqueurId", 3);
        doc.createElement("name", ci.getName());
        doc.createElement("cost", ci.getCost());
        doc.createElement("rate", ci.getParam());
        doc.endObject();
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private long getNextTime() {
        final Date now = new Date();
        final long sNow = now.getTime();
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(1);
        final int month = calendar.get(2);
        final int day = calendar.get(5);
        final int hour = calendar.get(11);
        calendar.set(year, month, day, hour, 0, 0);
        calendar.add(11, 1);
        final long sNext = calendar.getTimeInMillis();
        return sNext - sNow + (3 - hour % 4) * 60 * 60 * 1000;
    }
    
    private String getReason(final int state) {
        switch (state) {
            case 2: {
                return LocalMessages.BATTLE_INT_BATTLE_ARMY;
            }
            case 3: {
                return LocalMessages.BATTLE_INT_BATTLE_WORLD_CITY;
            }
            case 4: {
                return LocalMessages.BATTLE_INT_BATTLE_OCCUPY;
            }
            case 5: {
                return LocalMessages.GENERAL_SEARCH;
            }
            case 6: {
                return LocalMessages.GENERAL_MOVE;
            }
            case 7: {
                return LocalMessages.BATTLE_INT_BATTLE_MINE;
            }
            case 8: {
                return LocalMessages.BATTLE_INT_BATTLE_NATION_RANK;
            }
            case 9: {
                return LocalMessages.BATTLE_INT_BATTLE_KU_WD;
            }
            case 10: {
                return LocalMessages.BATTLE_INT_BATTLE_CITY;
            }
            case 13: {
                return LocalMessages.BATTLE_INT_BATTLE_ONEVSONE;
            }
            case 14: {
                return LocalMessages.BATTLE_INT_BATTLE_BARBARAIN;
            }
            case 15: {
                return LocalMessages.BATTLE_INT_BATTLE_BARBARAIN_ONEVSONE;
            }
            case 16: {
                return LocalMessages.BATTLE_INT_BATTLE_TEAM;
            }
            case 17: {
                return LocalMessages.BATTLE_INT_DUEL;
            }
            case 18: {
                return LocalMessages.BATTLE_INT_CITY_EVENT;
            }
            case 19: {
                return LocalMessages.BATTLE_INT_JUBEN;
            }
            case 20: {
                return LocalMessages.BATTLE_INT_JUBEN_ONEVSONE;
            }
            case 21: {
                return LocalMessages.BATTLE_INT_JUBEN_EVENT;
            }
            case 22: {
                return LocalMessages.BATTLE_INT_IN_CELL;
            }
            case 23: {
                return LocalMessages.BATTLE_INT_IN_RUNAWAY;
            }
            case 24:
            case 25:
            case 26:
            case 27: {
                return LocalMessages.FARM_FARM;
            }
            default: {
                return "";
            }
        }
    }
    
    @Transactional
    @Override
    public byte[] choiceLiqueurId(final PlayerDto playerDto, final int liqueurId) {
        if (liqueurId <= 0 || 3 < liqueurId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int realLiqueurId = this.getRealLiqueurId(liqueurId);
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)realLiqueurId);
        final int needGold = ci.getCost();
        final Player player = this.playerDao.read(playerDto.playerId);
        final int haveGold = player.getGold();
        int choiceResult = 0;
        if (haveGold >= needGold) {
            choiceResult = 1;
        }
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("choiceResult", choiceResult));
    }
    
    private int getRealLiqueurId(final int liqueurId) {
        switch (liqueurId) {
            case 1: {
                return 26;
            }
            case 2: {
                return 27;
            }
            case 3: {
                return 28;
            }
            default: {
                return -1;
            }
        }
    }
    
    @Transactional
    @Override
    public byte[] haveDinner(final PlayerDto playerDto, final int liqueurId) {
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[33] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (liqueurId < 0 || liqueurId > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int dinnerNum = this.dataGetter.getPlayerDinnerDao().getDinnerNum(playerId);
        if (dinnerNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DINNER_TIMES_NO_ENOUGHT);
        }
        final List<PlayerGeneralMilitary> playerGeneralMilitary = this.playerGeneralMilitaryDao.getMilitaryList(playerDto.playerId);
        final List<PlayerGeneralMilitary> pgm1 = new ArrayList<PlayerGeneralMilitary>();
        final List<PlayerGeneralMilitary> pgm2 = new ArrayList<PlayerGeneralMilitary>();
        int count = 0;
        for (final PlayerGeneralMilitary pgm3 : playerGeneralMilitary) {
            final int state = pgm3.getState();
            if (state > 1) {
                pgm2.add(pgm3);
            }
            else {
                pgm1.add(pgm3);
                if (pgm3.getForces() >= this.battleDataCache.getMaxHp(pgm3)) {
                    continue;
                }
                ++count;
            }
        }
        if (pgm1.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DINNER_NO_DENERAL_JOIN);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (pgm1.size() != 0 && count == 0) {
            doc.createElement("isFull", 1);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        doc.createElement("isFull", 0);
        double param = 1.0;
        if (liqueurId != 0) {
            final int realLiqueurId = this.getRealLiqueurId(liqueurId);
            final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)realLiqueurId);
            final Player player = this.playerDao.read(playerDto.playerId);
            if (player.getConsumeLv() < ci.getLv()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
            }
            if (!this.playerDao.consumeGold(player, ci.getCost(), "\u5bb4\u4f1a\u9009\u9152\u6d88\u8017\u91d1\u5e01")) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
            param = ci.getParam();
        }
        final C c = (C)this.cCache.get((Object)"Dinner.Base.Speed");
        final float value = c.getValue();
        int pos = 1;
        doc.startArray("generals");
        final double troopOutput = this.buildingOutputCache.getBuildingsOutputBase(playerId, 5) / ((TroopConscribeSpeed)this.troopConscribeSpeedCache.get((Object)(this.techEffectCache.getTechEffect(playerId, 28) + 1))).getSpeedMutiE();
        this.dataGetter.getPlayerDinnerDao().consumeDinnerNum(playerId);
        for (final PlayerGeneralMilitary pgm4 : pgm1) {
            doc.startObject();
            doc.createElement("type", 1);
            doc.createElement("pos", (pos++));
            doc.createElement("generalId", pgm4.getGeneralId());
            final General gm = (General)this.generalCache.get((Object)pgm4.getGeneralId());
            doc.createElement("name", gm.getName());
            final int totalForces = this.battleDataCache.getMaxHp(pgm4);
            final long forces = pgm4.getForces();
            doc.createElement("totalForces", totalForces);
            doc.createElement("forces", forces);
            final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)pgm4.getLocationId());
            final int rate = this.generalService.getRate(playerDto.forceId, worldCity);
            final int baseForces = (int)(troopOutput / 60.0 * rate / 100.0 * value);
            final int realForces = (int)(baseForces * param);
            int addForces = (int)((forces + realForces <= totalForces) ? realForces : (totalForces - forces));
            if (forces >= totalForces) {
                addForces = 0;
            }
            this.playerGeneralMilitaryDao.addGeneralForces2(pgm4.getPlayerId(), pgm4.getGeneralId(), addForces);
            if (1 == pgm4.getState() && addForces + forces >= totalForces) {
                this.playerGeneralMilitaryDao.updateState(pgm4.getVId(), 0);
            }
            doc.createElement("addForces", addForces);
            doc.endObject();
        }
        for (final PlayerGeneralMilitary pgm4 : pgm2) {
            doc.startObject();
            doc.createElement("type", 0);
            doc.createElement("pos", (pos++));
            doc.createElement("generalId", pgm4.getGeneralId());
            doc.createElement("name", ((General)this.generalCache.get((Object)pgm4.getGeneralId())).getName());
            doc.createElement("reason", this.getReason(pgm4.getState()));
            doc.endObject();
        }
        doc.endArray();
        this.generalService.sendGeneralMilitaryRecruitInfo(playerDto);
        doc.createElement("dinnerNum", dinnerNum - 1);
        if (dinnerNum - 1 <= 0) {
            doc.createElement("millisecond", this.getNextTime());
        }
        final DinnerChat dinnerChat = (DinnerChat)this.dinnerChatCache.get((Object)(WebUtil.nextInt(this.dinnerChatCache.getCacheMap().size()) + 1));
        doc.createElement("chiefContents", dinnerChat.getChief());
        doc.createElement("generalContents", dinnerChat.getGeneral());
        doc.createElement("speakGeneralId", pgm1.get(WebUtil.nextInt(pgm1.size())).getGeneralId());
        doc.endObject();
        TaskMessageHelper.sendDinnerMessage(playerDto.playerId, 1);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public void openDinnerFunction(final int playerId) {
        PlayerDinner pd = this.dataGetter.getPlayerDinnerDao().read(playerId);
        if (pd == null) {
            pd = new PlayerDinner();
            pd.setPlayerId(playerId);
            pd.setDinnerNum(6);
            this.dataGetter.getPlayerDinnerDao().create(pd);
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("dinnerNum", 6));
        }
    }
    
    @Override
    public void addDinnerNumByTech() {
        final long start = System.currentTimeMillis();
        final List<PlayerTech> ptList = this.playerTechDao.getListByTechKey(19);
        for (final PlayerTech pt : ptList) {
            this.dataGetter.getPlayerDinnerDao().rewardDinnerNum(pt.getPlayerId(), ((Tech)this.techCache.get((Object)pt.getTechId())).getPar1());
        }
        try {
            final Calendar cc = Calendar.getInstance();
            if (cc.get(7) == 3) {
                PlayerAction.clearPKeyMaps();
                DinnerService.log.error("exe PlayerAction clearPKeyMaps");
            }
        }
        catch (Exception e) {
            DinnerService.log.error("PlayerAction clearPKeyMaps ", e);
        }
        DinnerService.timerLog.info(LogUtil.formatThreadLog("DinnerService", "addDinnerNumByTech", 2, System.currentTimeMillis() - start, ""));
    }
    
    public void pushDinnerData() {
        final Collection<PlayerDto> dtoList = Players.getAllPlayer();
        for (final PlayerDto dto : dtoList) {
            if (dto.cs[33] == '1') {
                final int dinnerNum = this.dataGetter.getPlayerDinnerDao().getDinnerNum(dto.playerId);
                if (dinnerNum <= 0) {
                    continue;
                }
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("dinnerNum", dinnerNum));
            }
        }
    }
}
