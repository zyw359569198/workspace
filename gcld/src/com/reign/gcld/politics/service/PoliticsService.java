package com.reign.gcld.politics.service;

import org.springframework.stereotype.*;
import com.reign.gcld.building.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.treasure.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.politics.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.politics.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.task.reward.*;

@Component("politicsService")
public class PoliticsService implements IPoliticsService
{
    @Autowired
    private IPlayerBuildingDao playerBuildingDao;
    @Autowired
    private BuildingCache buildingCache;
    @Autowired
    private EventDailyCache eventDailyCache;
    @Autowired
    private IDataGetter taskDataGetter;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private StringCCache stringCCache;
    @Autowired
    private ITreasureService treasureService;
    @Autowired
    private CCache cCache;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IPlayerPoliticsEventDao playerPoliticsEventDao;
    private static final Logger timerLog;
    
    static {
        timerLog = new TimerLogger();
    }
    
    @Override
    public void producePoliticsEvent() {
        final long start = System.currentTimeMillis();
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        final List<Integer> playerIdList = new ArrayList<Integer>();
        for (final PlayerDto dto : onlinePlayerList) {
            if (dto.cs[22] == '1') {
                playerIdList.add(dto.playerId);
            }
        }
        if (playerIdList.size() <= 0) {
            return;
        }
        final List<Integer> canAddplayerIdList = this.playerPoliticsEventDao.getByPlayerIdsAndNum(playerIdList, 24);
        if (canAddplayerIdList.size() <= 0) {
            return;
        }
        final List<Integer> addNumPlayerIdList = new ArrayList<Integer>();
        for (final Integer playerId : canAddplayerIdList) {
            final boolean succ = this.producePolitcsEvent(playerId);
            if (succ) {
                addNumPlayerIdList.add(playerId);
            }
        }
        if (addNumPlayerIdList.size() > 0) {
            this.playerPoliticsEventDao.addPoliticsNum(addNumPlayerIdList, 1, new Date());
        }
        PoliticsService.timerLog.info(LogUtil.formatThreadLog("PoliticsService", "producePoliticsEvent", 2, System.currentTimeMillis() - start, ""));
    }
    
    private boolean producePolitcsEvent(final int playerId) {
        final List<PlayerBuilding> list = this.playerBuildingDao.getPlayerBuildingWithoutEvent(playerId);
        if (!list.isEmpty()) {
            final int index = WebUtil.nextInt(list.size());
            final PlayerBuilding pb = list.get(index);
            final List<EventDaily> eventList = this.eventDailyCache.getEventDailyMap(pb.getAreaId());
            final int eventIndex = WebUtil.nextInt(eventList.size());
            final int eventId = eventList.get(eventIndex).getId();
            this.buildingService.updateEventId(pb, eventId);
            final Building building = (Building)this.buildingCache.get((Object)pb.getBuildingId());
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("eventBuildings");
            doc.startObject();
            doc.createElement("buildingId", pb.getBuildingId());
            doc.createElement("type", building.getType());
            doc.endObject();
            doc.endArray();
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_POLITICS_EVENT, doc.toByte());
            return true;
        }
        return false;
    }
    
    @Transactional
    @Override
    public void assignPoliticsEvent(final PlayerAttribute pa) {
        final Date nowDate = new Date();
        if (pa.getFunctionId().toCharArray()[22] == '0') {
            return;
        }
        final int playerId = pa.getPlayerId();
        PlayerPoliticsEvent pe = this.playerPoliticsEventDao.read(playerId);
        if (pe == null) {
            this.openPolitcsEvent(playerId);
            pe = this.playerPoliticsEventDao.read(playerId);
        }
        final Date lastGetTime = pe.getLastEventTime();
        if (lastGetTime == null) {
            this.playerPoliticsEventDao.addPoliticEventNum(playerId, 0, nowDate);
            return;
        }
        final int hours = (int)(CDUtil.getCD(nowDate, lastGetTime) / 3600000L);
        final int nowEventNum = pe.getPoliticsEventNum();
        int needAssign = Math.min(hours, 24 - nowEventNum);
        int realAddNum = 0;
        if (needAssign > 0) {
            final List<PlayerBuilding> list = this.playerBuildingDao.getPlayerBuildingWithoutEvent(playerId);
            Collections.shuffle(list);
            int eventIndex = 0;
            List<EventDaily> eventList = null;
            for (final PlayerBuilding pb : list) {
                if (needAssign <= 0) {
                    break;
                }
                eventList = this.eventDailyCache.getEventDailyMap(pb.getAreaId());
                if (eventList == null) {
                    continue;
                }
                eventIndex = WebUtil.nextInt(eventList.size());
                final int eventId = eventList.get(eventIndex).getId();
                this.buildingService.updateEventId(pb, eventId);
                ++realAddNum;
                --needAssign;
            }
        }
        this.playerPoliticsEventDao.addPoliticEventNum(playerId, realAddNum, nowDate);
    }
    
    @Override
    public byte[] getEventInfo(final PlayerDto playerDto, final int buildingId) {
        final int playerId = playerDto.playerId;
        final PlayerBuilding pb = this.buildingService.getPlayerBuilding(playerId, buildingId);
        if (pb == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pb.getEventId() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_POLITICS_NO_EVENT);
        }
        final EventDaily event = (EventDaily)this.eventDailyCache.get((Object)pb.getEventId());
        if (playerDto.cs[22] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        General general1 = null;
        General general2 = null;
        final List<PlayerGeneralMilitary> militaryGenerals = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        final List<PlayerGeneralCivil> civilGenerals = this.playerGeneralCivilDao.getCivilList(playerId);
        final List<Integer> generalIds = new ArrayList<Integer>();
        for (final PlayerGeneralMilitary pgm : militaryGenerals) {
            generalIds.add(pgm.getGeneralId());
        }
        for (final PlayerGeneralCivil pgc : civilGenerals) {
            generalIds.add(pgc.getGeneralId());
        }
        Collections.shuffle(generalIds);
        final int currentGeneralNum = generalIds.size();
        if (currentGeneralNum >= 2) {
            general1 = (General)this.generalCache.get((Object)generalIds.get(0));
            general2 = (General)this.generalCache.get((Object)generalIds.get(1));
        }
        else {
            final List<General> list = this.generalCache.getGeneralByQuality(1, 1);
            list.addAll(this.generalCache.getGeneralByQuality(1, 2));
            Collections.shuffle(list);
            general1 = list.get(0);
            general2 = list.get(1);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("eventName", event.getName());
        doc.createElement("eventDisc", event.getDisc());
        doc.createElement("eventPic", event.getPic());
        doc.createElement("eventOption1", event.getDisc1());
        doc.createElement("eventOption2", event.getDisc2());
        doc.createElement("rewardDisc1", String.valueOf(event.getRewardDisc1()) + " +" + this.getRewardNum(event.getTaskReward1(), playerDto));
        doc.createElement("type1", this.getType(playerDto, event.getTaskReward1(), null));
        doc.createElement("rewardDisc2", String.valueOf(event.getRewardDisc2()) + " +" + this.getRewardNum(event.getTaskReward2(), playerDto));
        doc.createElement("type2", this.getType(playerDto, event.getTaskReward2(), null));
        doc.createElement("goldConsume1", event.getGoldConsume1());
        doc.createElement("goldConsume2", event.getGoldConsume2());
        doc.createElement("general1", general1.getPic());
        doc.createElement("general2", general2.getPic());
        doc.createElement("forceId", playerDto.forceId);
        doc.createElement("peopleLoyal", this.playerPoliticsEventDao.getPeopleLoyal(playerId));
        doc.createElement("peopleLoyalMax", 100);
        doc.createElement("currentEventNum", this.playerPoliticsEventDao.getEventNum(playerId));
        final PlayerBuilding nextPb = this.playerBuildingDao.getNextBuildingWithEvent(playerId, buildingId);
        if (nextPb != null) {
            doc.createElement("nextBuildingWithEvent", nextPb.getBuildingId());
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getRewardNum(final ITaskReward taskReward, final PlayerDto playerDto) {
        final Map<Integer, Reward> map = taskReward.getReward(playerDto, this.taskDataGetter, 21);
        if (map.containsKey(1)) {
            return map.get(1).getNum();
        }
        if (map.containsKey(3)) {
            return map.get(3).getNum();
        }
        if (map.containsKey(2)) {
            return map.get(2).getNum();
        }
        if (map.containsKey(4)) {
            return map.get(4).getNum();
        }
        if (map.containsKey(23)) {
            return map.get(23).getNum();
        }
        return 0;
    }
    
    @Override
    public byte[] chooseEventOption(final PlayerDto playerDto, final int buildingId, final int option) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        if (option < 1 || option > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerBuilding pb = this.buildingService.getPlayerBuilding(playerId, buildingId);
        if (pb == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pb.getEventId() <= 0 || this.playerPoliticsEventDao.getEventNum(playerId) < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_POLITICS_NO_EVENT);
        }
        final EventDaily event = (EventDaily)this.eventDailyCache.get((Object)pb.getEventId());
        if (event == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        int gold = 0;
        ITaskReward taskReward = null;
        if (1 == option) {
            gold = event.getGoldConsume1();
            taskReward = event.getTaskReward1();
        }
        else if (2 == option) {
            gold = event.getGoldConsume2();
            taskReward = event.getTaskReward2();
        }
        if (gold > 0 && !this.playerDao.consumeGold(player, gold, "\u653f\u52a1\u4e8b\u4ef6\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_ENOUGH_GOLD);
        }
        final double dropRate = ((C)this.cCache.get((Object)"Treasure.GetProb.Event_Daily")).getValue();
        final Treasure treasure = this.treasureService.tryGetTreasure(playerDto, 5, dropRate);
        final Map<Integer, Reward> rewardMap = taskReward.rewardPlayer(playerDto, this.taskDataGetter, "\u653f\u52a1", 21);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("eventReward");
        String msg = null;
        for (final Integer key : rewardMap.keySet()) {
            final Reward rd = rewardMap.get(key);
            doc.startObject();
            doc.createElement("type", rd.getType());
            doc.createElement("value", rd.getNum());
            doc.endObject();
            if (gold > 0) {
                msg = MessageFormatter.format(LocalMessages.BROADCAST_POLITICS, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(gold), ColorUtil.getGreenMsg(String.valueOf(rd.getName()) + "\u00d7" + rd.getNum()) });
                this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
            }
        }
        doc.endArray();
        if (treasure != null) {
            doc.createElement("treasureType", treasure.getType());
            doc.createElement("treasureName", treasure.getName());
            doc.createElement("pic", treasure.getPic());
            if (gold > 0) {
                msg = MessageFormatter.format(LocalMessages.BROADCAST_POLITICS, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(gold), ColorUtil.getGreenMsg(String.valueOf(treasure.getName()) + "\u00d7" + 1) });
                this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
            }
        }
        doc.endObject();
        TaskMessageHelper.sendEventDailyMessage(playerId);
        this.buildingService.updateEventId(pb, 0);
        this.playerPoliticsEventDao.minusePoliticEventNum(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public Map<Integer, Integer> getBuildingTypeWithEvent(final int playerId) {
        final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        final List<PlayerBuilding> list = this.playerBuildingDao.getPlayerBuildingWithEvent(playerId);
        for (final PlayerBuilding pb : list) {
            int outputType = pb.getOutputType();
            if (outputType >= 5 && outputType <= 8) {
                outputType = 5;
            }
            if (!map.containsKey(outputType)) {
                map.put(outputType, 1);
            }
            else {
                map.put(outputType, map.get(outputType) + 1);
            }
        }
        return map;
    }
    
    @Transactional
    @Override
    public void rewardPolitcsEvent(final int areaId, final int playerId) {
        final int num = this.playerPoliticsEventDao.getEventNum(playerId);
        if (num >= 24) {
            return;
        }
        final Date nowDate = new Date();
        final List<PlayerBuilding> list = this.buildingService.getPlayerBuildings(playerId, areaId);
        for (final PlayerBuilding pb : list) {
            if (pb.getEventId() == 0) {
                final List<EventDaily> eventList = this.eventDailyCache.getEventDailyMap(pb.getAreaId());
                if (eventList == null) {
                    continue;
                }
                final int eventIndex = WebUtil.nextInt(eventList.size());
                final int eventId = eventList.get(eventIndex).getId();
                this.buildingService.updateEventId(pb, eventId);
                this.playerPoliticsEventDao.addPoliticEventNum(playerId, 1, nowDate);
                break;
            }
        }
    }
    
    @Override
    public byte[] getReward(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        if (playerDto.cs[22] != '1' || this.playerPoliticsEventDao.getPeopleLoyal(playerId) < 100) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final StringC sc = (StringC)this.stringCCache.get((Object)8);
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(sc.getValue());
        final Map<Integer, Reward> rewardMap = reward.rewardPlayer(playerDto, this.taskDataGetter, "\u653f\u52a1\u5956\u52b1", null);
        this.playerPoliticsEventDao.resetPeopleLoyal(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Map.Entry<Integer, Reward> entry : rewardMap.entrySet()) {
            doc.startObject();
            doc.createElement("type", entry.getKey().toString());
            doc.createElement("value", entry.getValue().getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        this.sendBigNotice(rewardMap, playerDto);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getType(final PlayerDto playerDto, final ITaskReward reward, final Object obj) {
        final Map<Integer, Reward> rewardMap = reward.getReward(playerDto, this.taskDataGetter, obj);
        final Iterator<Reward> iterator = rewardMap.values().iterator();
        if (iterator.hasNext()) {
            final Reward temp = iterator.next();
            return temp.getType();
        }
        return 0;
    }
    
    private void sendBigNotice(final Map<Integer, Reward> rewardMap, final PlayerDto playerDto) {
        final StringBuffer sb = new StringBuffer();
        String msg = null;
        for (final Reward reward : rewardMap.values()) {
            sb.append(reward.getName());
            sb.append("\u00d7");
            sb.append(reward.getNum());
            sb.append("\uff0c");
        }
        msg = MessageFormatter.format(LocalMessages.BROADCAST_GET_PEOPLE_LOYAL_REWARD, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(sb.substring(0, sb.length() - 1)) });
        this.chatService.sendBigNotice("GLOBAL", playerDto, msg, null);
    }
    
    @Override
    public int openPolitcsEvent(final int playerId) {
        PlayerPoliticsEvent pe = this.playerPoliticsEventDao.read(playerId);
        if (pe != null) {
            return 0;
        }
        pe = new PlayerPoliticsEvent();
        pe.setPlayerId(playerId);
        pe.setPoliticsEventNum(0);
        pe.setLastEventTime(new Date());
        pe.setPeopleLoyal(0);
        return this.playerPoliticsEventDao.create(pe);
    }
}
