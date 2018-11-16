package com.reign.gcld.incense.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.treasure.service.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.incense.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.json.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.incense.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.util.*;

@Component("incenseService")
public class IncenseService implements IIncenseService
{
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private ITreasureService treasureService;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IStoreHouseService getStoreHouseService;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private TechCache techCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IPlayerIncenseDao playerIncenseDao;
    @Autowired
    private IDataGetter dataGetter;
    private static final Logger errorLog;
    private static Map<Integer, Integer> godResourceMap;
    
    static {
        errorLog = CommonLog.getLog(IncenseService.class);
        (IncenseService.godResourceMap = new HashMap<Integer, Integer>()).put(1, 1);
        IncenseService.godResourceMap.put(2, 2);
        IncenseService.godResourceMap.put(3, 3);
        IncenseService.godResourceMap.put(4, 4);
        IncenseService.godResourceMap.put(5, 5);
    }
    
    @Transactional
    @Override
    public byte[] getIncenseInfo(final PlayerDto playerDto) {
        if (playerDto.cs[16] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final PlayerIncense pi = this.playerIncenseDao.read(playerId);
        if (pi == null) {
            this.openIncense(playerId);
            IncenseService.errorLog.error("class:IncenseService#method:getIncenseInfo#playerId:" + playerId + "#openIncense");
        }
        final int currentIncenseNum = pi.getIncenseNum();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("gods");
        int totalGoldConsume = 0;
        final boolean bigIncense = false;
        int pow = 0;
        final int open_bit = pi.getOpenBit();
        for (int i = 1; i <= 5; ++i) {
            pow = (int)Math.pow(2.0, i - 1);
            if ((open_bit & pow) == pow) {
                final int incenseTime = this.getTimes(i, pi);
                doc.startObject();
                doc.createElement("godId", i);
                doc.createElement("resourceType", IncenseService.godResourceMap.get(i));
                if (currentIncenseNum > 0 && i <= 3) {
                    doc.createElement("gold", 0);
                }
                else {
                    int incenseGold = incenseTime / 1 + 2;
                    if (i < 3) {
                        final C c = (C)this.cCache.get((Object)"Incense.Max.Ordinary");
                        final int maxGold = (int)(Object)c.getValue();
                        if (incenseGold > maxGold) {
                            incenseGold = maxGold;
                        }
                    }
                    else if (i == 3) {
                        final C c = (C)this.cCache.get((Object)"Incense.Max.Food");
                        final int maxGold = (int)(Object)c.getValue();
                        if (incenseGold > maxGold) {
                            incenseGold = maxGold;
                        }
                    }
                    else if (i == 4) {
                        final C c = (C)this.cCache.get((Object)"Incense.Max.Iron");
                        final int maxGold = (int)(Object)c.getValue();
                        if (incenseGold > maxGold) {
                            incenseGold = maxGold;
                        }
                    }
                    else if (5 == i) {
                        incenseGold = 10;
                    }
                    doc.createElement("gold", incenseGold);
                }
                totalGoldConsume += incenseTime / 1 + 2;
                doc.endObject();
            }
        }
        doc.endArray();
        doc.createElement("currentIncenseNum", currentIncenseNum);
        doc.createElement("incenseGold", (int)(totalGoldConsume * 0.8));
        doc.createElement("bigIncense", bigIncense);
        doc.createElement("incenseMax", 25);
        doc.endObject();
        TaskMessageHelper.sendIncenseTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getTimes(final int goldId, final PlayerIncense pi) {
        int times = 0;
        if (1 == goldId) {
            times = pi.getCopperTimes();
        }
        else if (2 == goldId) {
            times = pi.getWoodTimes();
        }
        else if (3 == goldId) {
            times = pi.getFoodTimes();
        }
        else if (4 == goldId) {
            times = pi.getIronTimes();
        }
        else if (5 == goldId) {
            times = pi.getGemTimes();
        }
        return times;
    }
    
    private int getMultiple(final int playerId, final int godId) {
        int multiple = 1;
        if (4 == godId) {
            final PlayerIncenseWeaponEffect piwe = this.dataGetter.getPlayerIncenseWeaponEffectDao().read(playerId);
            if (piwe != null) {
                final int incenseId = piwe.getIncenseId();
                final int incenseLimit = piwe.getIncenseLimit();
                final int incenseMulti = piwe.getIncenseMulti();
                if (1 == incenseId) {
                    if (incenseLimit > 0) {
                        this.dataGetter.getPlayerIncenseWeaponEffectDao().reduceIncenseLimit(playerId);
                        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("ironIncenseEffect", (incenseLimit > 1) ? 1 : 0));
                        return incenseMulti;
                    }
                }
                else if (3 == incenseId) {
                    final Date now = new Date();
                    if (now.before(piwe.getIncenseEndTime()) && incenseLimit > 0) {
                        this.dataGetter.getPlayerIncenseWeaponEffectDao().reduceIncenseLimit(playerId);
                        return incenseMulti;
                    }
                }
            }
        }
        final double rushProb = WebUtil.nextDouble();
        if (rushProb <= 0.07) {
            multiple = 10;
        }
        else if (rushProb <= 0.17) {
            multiple = 4;
        }
        else if (rushProb <= 0.67) {
            multiple = 2;
        }
        else {
            multiple = 1;
        }
        return multiple;
    }
    
    @Transactional
    @Override
    public void addIncenseGod(final int playerId, final int godId) {
        if (godId < 1 || godId > 5) {
            IncenseService.errorLog.error("class:IncenseService#method:addIncenseGod#playerId:" + playerId + "#godId" + godId + "#god_is_err");
            return;
        }
        PlayerIncense pi = this.playerIncenseDao.read(playerId);
        if (pi == null) {
            this.openIncense(playerId);
            IncenseService.errorLog.error("class:IncenseService#method:addIncenseGod#playerId:" + playerId + "#pi_is_null");
        }
        pi = this.playerIncenseDao.read(playerId);
        final int pow = (int)Math.pow(2.0, godId - 1);
        if ((pi.getOpenBit() & pow) == pow) {
            IncenseService.errorLog.error("class:IncenseService#method:addIncenseGod#playerId:" + playerId + "#godId:" + godId + "#openBit_is_open");
            return;
        }
        this.playerIncenseDao.setOpenBit(playerId, godId);
        if (5 == godId) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("openGem", 1));
        }
    }
    
    @Transactional
    @Override
    public byte[] doWorship(final int godId, final PlayerDto playerDto) {
        if (godId < 1 || godId > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        if (playerDto.cs[16] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerIncense pi = this.playerIncenseDao.read(playerId);
        if (pi == null) {
            this.openIncense(playerId);
            IncenseService.errorLog.error("class:IncenseService#method:doWorship#playerId:" + playerId + "#openIncense");
        }
        final int pow = (int)Math.pow(2.0, godId - 1);
        if ((pi.getOpenBit() & pow) != pow) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INCENSE_GOD);
        }
        int incenseGold = 0;
        boolean free = false;
        if (pi.getIncenseNum() > 0 && godId < 4) {
            free = true;
        }
        else {
            int incenseTime = 0;
            if (1 == godId) {
                incenseTime = pi.getCopperTimes();
            }
            else if (2 == godId) {
                incenseTime = pi.getWoodTimes();
            }
            else if (3 == godId) {
                incenseTime = pi.getFoodTimes();
            }
            else if (4 == godId) {
                incenseTime = pi.getIronTimes();
            }
            else if (5 == godId) {
                incenseTime = pi.getGemTimes();
            }
            incenseGold = incenseTime / 1 + 2;
        }
        if (!free) {
            if (godId < 3) {
                final C c = (C)this.cCache.get((Object)"Incense.Max.Ordinary");
                final int maxGold = (int)(Object)c.getValue();
                if (incenseGold > maxGold) {
                    incenseGold = maxGold;
                }
            }
            else if (godId == 3) {
                final C c = (C)this.cCache.get((Object)"Incense.Max.Food");
                final int maxGold = (int)(Object)c.getValue();
                if (incenseGold > maxGold) {
                    incenseGold = maxGold;
                }
            }
            else if (godId == 4) {
                final C c = (C)this.cCache.get((Object)"Incense.Max.Iron");
                final int maxGold = (int)(Object)c.getValue();
                if (incenseGold > maxGold) {
                    incenseGold = maxGold;
                }
            }
            else if (5 == godId) {
                incenseGold = 10;
            }
            if (incenseGold <= 0) {
                IncenseService.errorLog.error("class:IncenseService#method:doWorship#playerId:" + playerId + "#incenseGold:" + incenseGold);
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INCENSE_GOD);
            }
            if (!this.playerDao.consumeGold(player, incenseGold, this.getReason(godId))) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
        }
        final int multiple = this.getMultiple(playerId, godId);
        final int resourceType = IncenseService.godResourceMap.get(godId);
        int hour = 2;
        if (4 == godId) {
            hour = 4;
        }
        int hourOutPut = hour * this.buildingOutputCache.getBuildingsOutputBase(playerId, resourceType);
        if (free) {
            hourOutPut *= multiple;
        }
        else {
            hourOutPut *= (int)(multiple + this.techEffectCache.getTechEffect(playerId, 31) / 100.0);
        }
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        if (resourceType == 1 && hourOutPut + pr.getCopper() > 500000000) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10025);
        }
        switch (resourceType) {
            case 1: {
                if (hourOutPut > 0) {
                    this.playerResourceDao.addCopperIgnoreMax(playerId, hourOutPut, "\u796d\u7940\u83b7\u5f97\u94f6\u5e01", true);
                    this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, hourOutPut, "jisiyb");
                    break;
                }
                break;
            }
            case 3: {
                if (hourOutPut > 0) {
                    this.playerResourceDao.addFoodIgnoreMax(playerId, hourOutPut, "\u796d\u7940\u83b7\u5f97\u7cae\u98df");
                    break;
                }
                break;
            }
            case 2: {
                if (hourOutPut > 0) {
                    this.playerResourceDao.addWoodIgnoreMax(playerId, hourOutPut, "\u796d\u7940\u83b7\u5f97\u6728\u6750", true);
                    break;
                }
                break;
            }
            case 4: {
                if (hourOutPut > 0) {
                    this.playerResourceDao.addIronIgnoreMax(playerId, hourOutPut, "\u796d\u7940\u83b7\u5f97\u9554\u94c1", true);
                    break;
                }
                break;
            }
            case 5: {
                int gemNum = 1;
                if (free) {
                    gemNum *= multiple;
                }
                else {
                    gemNum *= (int)(multiple + this.techEffectCache.getTechEffect(playerId, 31) / 100.0);
                }
                this.getStoreHouseService.gainGem(player, gemNum, 1, LocalMessages.T_LOG_GEM_6, null);
                hourOutPut = gemNum;
                break;
            }
        }
        if (free) {
            this.playerIncenseDao.useIncenseNum(playerId);
        }
        else if (1 == godId) {
            this.playerIncenseDao.addCopperTimes(playerId);
        }
        else if (2 == godId) {
            this.playerIncenseDao.addWoodTimes(playerId);
        }
        else if (3 == godId) {
            this.playerIncenseDao.addFoodTimes(playerId);
        }
        else if (4 == godId) {
            this.playerIncenseDao.addIronTimes(playerId);
        }
        else if (5 == godId) {
            this.playerIncenseDao.addGemTimes(playerId);
        }
        else {
            IncenseService.errorLog.error("class:IncenseService#method:doWorship#playerId:" + playerId + "#godId:" + godId + "#decrease_times");
        }
        if (multiple == 10) {
            final List<ResourceDto> list = new ArrayList<ResourceDto>();
            list.add(new ResourceDto(resourceType, hourOutPut, multiple));
            this.sendBigNotice(list, playerDto);
        }
        final double dropRate = ((C)this.cCache.get((Object)"Treasure.GetProb.Incense")).getValue();
        final Treasure treasure = this.treasureService.tryGetTreasure(playerDto, 1, dropRate);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("resourceGot");
        doc.startObject();
        doc.createElement("resourceType", resourceType);
        doc.createElement("addNum", hourOutPut);
        doc.endObject();
        doc.endArray();
        doc.createElement("multiple", multiple);
        if (treasure != null) {
            doc.createElement("treasureName", treasure.getName());
        }
        if (4 == godId) {
            EventUtil.handleOperation(playerId, 13, 1);
            if (EventUtil.isEventTime(13)) {
                final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 13);
                if (pe != null && pe.getParam1() % 3 == 0) {
                    doc.createElement("extraIron", IronRewardEvent.IRON);
                }
            }
        }
        doc.endObject();
        TaskMessageHelper.sendUseIncenseTaskMessage(playerId, godId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void addIncense() {
        final List<PlayerTech> ptList = this.playerTechDao.getListByTechKey(1);
        for (final PlayerTech pt : ptList) {
            this.playerIncenseDao.addIncenseNum(pt.getPlayerId(), ((Tech)this.techCache.get((Object)pt.getTechId())).getPar1());
        }
    }
    
    @Override
    public int openIncense(final int playerId) {
        PlayerIncense pi = this.playerIncenseDao.read(playerId);
        if (pi != null) {
            return 0;
        }
        pi = new PlayerIncense();
        pi.setPlayerId(playerId);
        pi.setIncenseNum(10);
        pi.setOpenBit(0);
        pi.setCopperTimes(0);
        pi.setWoodTimes(0);
        pi.setFoodTimes(0);
        pi.setIronTimes(0);
        pi.setGemTimes(0);
        final int result = this.playerIncenseDao.create(pi);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("freeIncenseNum", this.playerIncenseDao.getIncenseNum(playerId)));
        return result;
    }
    
    @Override
    public int getGold(final int playerId, final int goldId, final int times) {
        if (goldId < 1 || goldId > 5 || times <= 0) {
            return 0;
        }
        final PlayerIncense pi = this.playerIncenseDao.read(playerId);
        if (pi == null) {
            return 0;
        }
        if (4 == goldId) {
            int incenseTime = this.getTimes(goldId, pi);
            int gold = 0;
            for (int i = 0; i < times; ++i) {
                int incenseGold = incenseTime / 1 + 2;
                final C c = (C)this.cCache.get((Object)"Incense.Max.Iron");
                final int maxGold = (int)(Object)c.getValue();
                if (incenseGold > maxGold) {
                    incenseGold = maxGold;
                }
                gold += incenseGold;
                ++incenseTime;
            }
            return gold;
        }
        return 0;
    }
    
    @Override
    public int getIndexGold(final int playerId, final int goldId, final int index) {
        if (goldId < 1 || goldId > 5 || index <= 0) {
            return 0;
        }
        final PlayerIncense pi = this.playerIncenseDao.read(playerId);
        if (pi == null) {
            return 0;
        }
        if (4 != goldId) {
            return 0;
        }
        int incenseTime = this.getTimes(goldId, pi);
        incenseTime += index - 1;
        final int incenseGold = incenseTime / 1 + 2;
        final C c = (C)this.cCache.get((Object)"Incense.Max.Iron");
        final int maxGold = (int)(Object)c.getValue();
        if (incenseGold > maxGold) {
            return maxGold;
        }
        return incenseGold;
    }
    
    private String sendBigNotice(final List<ResourceDto> list, final PlayerDto playerDto) {
        final StringBuffer sb = new StringBuffer();
        String msg = null;
        for (final ResourceDto dto : list) {
            if (10 == dto.getMultiple()) {
                sb.setLength(0);
                switch (dto.getType()) {
                    case 1: {
                        sb.append(LocalMessages.T_COMM_10004);
                        break;
                    }
                    case 2: {
                        sb.append(LocalMessages.T_COMM_10005);
                        break;
                    }
                    case 3: {
                        sb.append(LocalMessages.T_COMM_10017);
                        break;
                    }
                    case 4: {
                        sb.append(LocalMessages.T_COMM_10018);
                        break;
                    }
                    case 5: {
                        sb.append(LocalMessages.T_COMM_10023);
                        break;
                    }
                }
                sb.append("\u00d7");
                sb.append((int)dto.getValue());
                msg = MessageFormatter.format(LocalMessages.INCENSE_RUSH, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(sb.toString()) });
                this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
            }
        }
        return sb.substring(0, sb.length() - 1);
    }
    
    private String getReason(final int godId) {
        switch (godId) {
            case 1: {
                return "\u94f6\u5e01\u796d\u7940\u6d88\u8017\u91d1\u5e01";
            }
            case 2: {
                return "\u6728\u6750\u796d\u7940\u6d88\u8017\u91d1\u5e01";
            }
            case 3: {
                return "\u7cae\u98df\u796d\u7940\u6d88\u8017\u91d1\u5e01";
            }
            case 4: {
                return "\u9554\u94c1\u796d\u7940\u6d88\u8017\u91d1\u5e01";
            }
            case 5: {
                return "\u5b9d\u77f3\u796d\u7940\u6d88\u8017\u91d1\u5e01";
            }
            default: {
                return "";
            }
        }
    }
}
