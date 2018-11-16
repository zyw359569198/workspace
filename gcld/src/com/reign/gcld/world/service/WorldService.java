package com.reign.gcld.world.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.framework.json.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.battle.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.common.*;
import java.util.*;

@Component("worldService")
public class WorldService implements IWorldService, InitializingBean
{
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private IPlayerWorldDao playerWorldDao;
    @Autowired
    private ICityDataCache cityDataCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private WorldRoadCache worldRoadCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IKillRankService killRankService;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private WorldOutputPerTimeCache worldOutputPerTimeCache;
    @Autowired
    private ICitiesPerHourDao citiesPerHourDao;
    @Autowired
    private ICountryRewardPerHourDao countryRewardPerHourDao;
    @Autowired
    private WorldCitySpecialCache worldCitySpecialCache;
    @Autowired
    private CityEffectCache cityEffectCache;
    @Autowired
    private IWholeKillDao wholeKillDao;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    private static final Logger timerLog;
    private static final Logger errorLog;
    public static final Map<Integer, Integer> playerCityOccupyMap;
    
    static {
        timerLog = new TimerLogger();
        errorLog = CommonLog.getLog(WorldService.class);
        playerCityOccupyMap = new ConcurrentHashMap<Integer, Integer>();
    }
    
    @Override
    public byte[] getOperations(final PlayerDto playerDto, final int cityId) {
        final boolean canSearch = this.canSearch(playerDto, cityId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("canSearch", canSearch);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private boolean canSearch(final PlayerDto playerDto, final int cityId) {
        if (this.cityDao.read(cityId).getForceId() != playerDto.forceId) {
            return false;
        }
        final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)cityId);
        return worldCity != null;
    }
    
    @Override
    public void addCountryRewards() {
        final long start = System.currentTimeMillis();
        final Calendar calendar = Calendar.getInstance();
        final long nowMS = calendar.getTimeInMillis();
        final int hour = TimeUtil.getHour();
        final WorldOutputPerTime worldOutputPerTime = (WorldOutputPerTime)this.worldOutputPerTimeCache.get((Object)(TimeUtil.nextHour() + 1));
        if (worldOutputPerTime != null) {
            this.sendCountryRewardPushData(worldOutputPerTime);
        }
        final List<Integer> playerIdList = this.playerWorldDao.getByRewardNum(24);
        double reward = 0.0;
        final int PS1 = this.cityDataCache.getCNPNum(1);
        final int PS2 = this.cityDataCache.getCNPNum(2);
        final int PS3 = this.cityDataCache.getCNPNum(3);
        StringBuilder rewards = null;
        int addP1 = 0;
        int addP2 = 0;
        int addP3 = 0;
        if (CityDataCache.leagueInfo != 0) {
            if (CityDataCache.leagueInfo == 1) {
                addP1 = (int)(PS2 * 0.5f);
                addP2 = (int)(PS1 * 0.5f);
            }
            else if (CityDataCache.leagueInfo == 2) {
                addP1 = (int)(PS3 * 0.5f);
                addP3 = (int)(PS1 * 0.5f);
            }
            else {
                addP2 = (int)(PS3 * 0.5f);
                addP3 = (int)(PS2 * 0.5f);
            }
        }
        final int P1 = PS1 + addP1;
        final int P2 = PS2 + addP2;
        final int P3 = PS3 + addP3;
        int P4 = 0;
        int forceId = 0;
        for (final int playerId : playerIdList) {
            if (this.canGetReward(playerId, nowMS)) {
                forceId = this.playerDao.read(playerId).getForceId();
                P4 = ((forceId == 1) ? P1 : ((forceId == 2) ? P2 : P3));
                reward = P4;
                final int real = this.getRealValue(playerId, reward, false, hour);
                rewards = new StringBuilder();
                rewards.append(hour).append(",").append(real).append(SymbolConstants.B_COLON);
                try {
                    Constants.locks[playerId % Constants.LOCKS_LEN].lock();
                    this.playerWorldDao.addRewards(playerId, rewards.toString());
                    WorldService.timerLog.info("job.world.addCountryRewards#success#player_id:" + playerId);
                }
                catch (Exception e) {
                    WorldService.errorLog.error("Exception Happens In addCountryRewards() WorldService.java: ", e);
                    continue;
                }
                finally {
                    Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
                }
                Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
            }
        }
        this.updateCitiesPerHour();
        this.updateCountryRewardPerHour();
        this.pushCountryRewardNum();
        WorldService.timerLog.info(LogUtil.formatThreadLog("WolrdService", "addCountryRewards", 2, System.currentTimeMillis() - start, ""));
    }
    
    private void sendCountryRewardPushData(final WorldOutputPerTime worldOutputPerTime) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int quality = worldOutputPerTime.getQuality();
        doc.createElement("quality", quality);
        doc.createElement("leftTime", (Object)TimeUtil.nextHourMS());
        doc.endObject();
        final String string = ChatType.GLOBAL.toString();
        final Group group = GroupManager.getInstance().getGroup(string);
        if (group != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_WORLD_REWARD.getModule(), doc.toByte()));
            group.notify(WrapperUtil.wrapper(PushCommand.PUSH_WORLD_REWARD.getCommand(), 0, bytes));
        }
    }
    
    @Transactional
    @Override
    public void createRecord(final int playerId) {
        PlayerWorld playerWorld = this.playerWorldDao.read(playerId);
        if (playerWorld != null) {
            return;
        }
        playerWorld = new PlayerWorld();
        playerWorld.setPlayerId(playerId);
        playerWorld.setRewardNum(1);
        playerWorld.setQuizinfo(0);
        final Player player = this.playerDao.read(playerId);
        final int forceId = player.getForceId();
        this.setBoxInfo(playerId, forceId, playerWorld);
        final int mCityId = WorldCityCommon.nationMainCityIdMap.get(forceId);
        Set<Integer> neighbors = this.worldRoadCache.getNeighbors(mCityId);
        final int areaId = this.worldCityCache.getArea(forceId, mCityId);
        final List<WorldCity> list = this.worldCityCache.getAreaCity(forceId, areaId);
        final Set<Integer> attedSet = new HashSet<Integer>();
        for (final WorldCity wcTemp : list) {
            attedSet.add(wcTemp.getId());
        }
        final Set<Integer> openAreaSet = new HashSet<Integer>();
        final Set<Integer> canAttSet = new HashSet<Integer>();
        for (final WorldCity wcTemp2 : list) {
            if (WorldCityCommon.barbarainCitySet.contains(wcTemp2.getId())) {
                continue;
            }
            neighbors = this.worldRoadCache.getNeighbors(wcTemp2.getId());
            for (final Integer nbId : neighbors) {
                if (attedSet.contains(nbId)) {
                    continue;
                }
                if (canAttSet.contains(nbId)) {
                    continue;
                }
                canAttSet.add(nbId);
                final int openArea = this.worldCityCache.getArea(forceId, nbId);
                openAreaSet.add(openArea);
                final List<WorldCity> wcList = this.worldCityCache.getAreaCity(forceId, openArea);
                for (final WorldCity wcc : wcList) {
                    canAttSet.add(wcc.getId());
                }
            }
        }
        final StringBuilder canAttSb = new StringBuilder();
        for (final Integer key : canAttSet) {
            canAttSb.append(key).append(",");
        }
        final StringBuilder attedIdSb = new StringBuilder();
        for (final Integer key2 : attedSet) {
            attedIdSb.append(key2).append(",");
        }
        playerWorld.setAttedId(attedIdSb.toString());
        playerWorld.setCanAttId(canAttSb.toString());
        final StringBuilder rewards = new StringBuilder();
        rewards.append(TimeUtil.getHour()).append(",");
        final int P = this.cityDataCache.getCNPNum(player.getForceId());
        rewards.append(P).append(",");
        final int killRank = this.killRankService.getRank(1, playerId, forceId);
        rewards.append(killRank).append(",");
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        final int officerId = (por == null) ? 37 : por.getOfficerId();
        rewards.append(officerId).append(",");
        final double reward = P;
        rewards.append(this.getRealValue(playerId, reward, false, TimeUtil.getHour())).append(",");
        rewards.append(this.getLeagueAddNPC(forceId));
        rewards.append(":");
        playerWorld.setRewards(rewards.toString());
        playerWorld.setReward("");
        final StringBuilder details = new StringBuilder();
        for (final Integer openArea2 : openAreaSet) {
            final int maxHp = CityNpcBuilder.getMaxHp(this.dataGetter, openArea2);
            final String detailTemp = openArea2 + "," + maxHp + "," + maxHp + ";";
            details.append(detailTemp);
            if (this.dataGetter.getPlayerMistLostDao().getMist(playerId, openArea2) == null) {
                final PlayerMistLost playerMistLost = new PlayerMistLost();
                playerMistLost.setPlayerId(playerId);
                playerMistLost.setAreaId(openArea2);
                playerMistLost.setNpcLost(null);
                this.dataGetter.getPlayerMistLostDao().create(playerMistLost);
            }
            else {
                this.dataGetter.getPlayerMistLostDao().updateNpcLostDetail(playerId, openArea2, null);
            }
        }
        playerWorld.setNpcLostDetail(details.toString());
        this.playerWorldDao.create(playerWorld);
    }
    
    @Transactional
    @Override
    public void createWholeKill(final int playerId) {
        WholeKill wk = this.wholeKillDao.read(playerId);
        if (wk != null) {
            return;
        }
        wk = new WholeKill();
        wk.setPlayerId(playerId);
        wk.setKillNum(0);
        wk.setReceivedReward(1);
        wk.setLastNum(0);
        wk.setLastRank(0);
        this.wholeKillDao.create(wk);
    }
    
    private void setBoxInfo(final int playerId, final int forceId, final PlayerWorld playerWorld) {
        switch (forceId) {
            case 1: {
                playerWorld.setBoxispicked(SymbolUtil.toString(CityService.wei, "|"));
                break;
            }
            case 2: {
                playerWorld.setBoxispicked(SymbolUtil.toString(CityService.shu, "|"));
                break;
            }
            case 3: {
                playerWorld.setBoxispicked(SymbolUtil.toString(CityService.wu, "|"));
                break;
            }
        }
    }
    
    @Override
    public int getLeagueAddNPC(final int forceId) {
        int addP = 0;
        if (CityDataCache.leagueInfo != 0) {
            if (CityDataCache.leagueInfo == 1) {
                if (forceId == 1) {
                    addP = (int)(this.cityDataCache.getCNPNum(2) * 0.5f);
                }
                else if (forceId == 2) {
                    addP = (int)(this.cityDataCache.getCNPNum(1) * 0.5f);
                }
            }
            else if (CityDataCache.leagueInfo == 2) {
                if (forceId == 1) {
                    addP = (int)(this.cityDataCache.getCNPNum(3) * 0.5f);
                }
                else if (forceId == 3) {
                    addP = (int)(this.cityDataCache.getCNPNum(1) * 0.5f);
                }
            }
            else if (CityDataCache.leagueInfo == 3) {
                if (forceId == 2) {
                    addP = (int)(this.cityDataCache.getCNPNum(3) * 0.5f);
                }
                else if (forceId == 3) {
                    addP = (int)(this.cityDataCache.getCNPNum(2) * 0.5f);
                }
            }
        }
        return addP;
    }
    
    @Transactional
    @Override
    public byte[] getCountryReward(final int id, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        int countryRewardNum = 0;
        if (cs[53] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WORLD_CNP_REWARD_NO_OPEN);
        }
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            PlayerWorld pw = this.playerWorldDao.read(playerId);
            if (pw == null) {
                this.createRecord(playerId);
                pw = this.playerWorldDao.read(playerId);
            }
            countryRewardNum = pw.getRewardNum();
            if (countryRewardNum <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.WORLD_NO_REWARD_NUM);
            }
            final String rewards = pw.getRewards();
            final String[] rewardArry = rewards.split(":");
            final String[] temp = rewardArry[0].split(",");
            final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
            cs = pa.getFunctionId().toCharArray();
            final int hour = Integer.parseInt(temp[0]);
            int CNP = 0;
            int addCNP = 0;
            int addNum = 0;
            if (temp.length > 2) {
                CNP = Integer.parseInt(temp[1]);
                addCNP = Integer.parseInt(temp[5]);
                addNum = Integer.parseInt(temp[4]);
            }
            else {
                addNum = Integer.parseInt(temp[1]);
            }
            final CountryRewardPerHour countryRewardPerHour = this.countryRewardPerHourDao.getByHourAndForceId(hour, playerDto.forceId);
            if (countryRewardPerHour != null) {
                final String[] tempArray = countryRewardPerHour.getRewards().split(",");
                CNP = Integer.parseInt(tempArray[0]);
                addCNP = Integer.parseInt(tempArray[1]);
                addNum = this.getRealValue(playerId, CNP + addCNP, false, hour);
            }
            final List<ResourceDto> list = new ArrayList<ResourceDto>();
            ResourceDto rd = null;
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("rewards");
            for (int i = 1; i <= 1; ++i) {
                rd = new ResourceDto(i, addNum);
                list.add(rd);
                doc.startObject();
                doc.createElement("type", i);
                doc.createElement("value", addNum);
                doc.endObject();
            }
            final int forceId = playerDto.forceId;
            final int techShadi = this.techEffectCache.getTechEffect(playerId, 33);
            int iron = (int)this.cityEffectCache.getCityEffect(forceId, 4);
            if (countryRewardPerHour != null) {
                final String[] tempArray2 = countryRewardPerHour.getRewards().split(",");
                if (tempArray2.length >= 3) {
                    iron = Integer.parseInt(tempArray2[2]);
                }
            }
            iron = this.getRealValue(playerId, iron, false, hour);
            if (techShadi > 0 && iron > 0) {
                rd = new ResourceDto(4, iron);
                list.add(rd);
                doc.startObject();
                doc.createElement("type", 4);
                doc.createElement("value", iron);
                doc.endObject();
            }
            doc.endArray();
            doc.createElement("id", id);
            doc.createElement("CNP", this.cityDataCache.getCNPNum(forceId));
            doc.createElement("cityNum", this.cityDataCache.getCityNum(forceId));
            doc.createElement("rewardNum", pw.getRewardNum() - 1);
            doc.createElement("maxNum", 24);
            doc.createElement("addCNP", this.getLeagueAddNPC(forceId));
            doc.createElement("quality", ((WorldOutputPerTime)this.worldOutputPerTimeCache.get((Object)(hour + 1))).getQuality());
            doc.endObject();
            final boolean res = this.playerResourceDao.addResourceIgnoreMax(playerId, list, "\u56fd\u529b\u503c\u5956\u52b1", true);
            if (!res) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10025);
            }
            final StringBuilder sb = new StringBuilder();
            for (int j = 1; j < rewardArry.length; ++j) {
                sb.append(rewardArry[j]);
                sb.append(":");
            }
            this.playerWorldDao.reduceReward(playerId, sb.toString(), rewardArry[0]);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            WorldService.errorLog.error("Exception Happens In getCountryReward() WorldService.java: ");
            WorldService.errorLog.error("Exception Happens In getCountryReward() WorldService.java: ", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10037);
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
    }
    
    private boolean canGetReward(final int playerId, final long nowMS) {
        WorldService.timerLog.info("job.world.addCountryRewards#entermethod:canGetReward#player_id:" + playerId);
        final Player player = this.playerDao.read(playerId);
        if (player.getPlayerLv() < 30) {
            WorldService.timerLog.info("job.world.addCountryRewards#leavemethod1:canGetReward#player_id:" + playerId);
            return false;
        }
        if (Players.getPlayer(playerId) != null) {
            WorldService.timerLog.info("job.world.addCountryRewards#leavemethod2:canGetReward#player_id:" + playerId);
            return true;
        }
        Date timeMs = player.getQuitTime();
        if (timeMs != null && nowMS - timeMs.getTime() < 432000000L) {
            WorldService.timerLog.info("job.world.addCountryRewards#leavemethod3:canGetReward#player_id:" + playerId);
            return true;
        }
        timeMs = player.getLoginTime();
        if (timeMs != null && nowMS - timeMs.getTime() < 432000000L) {
            WorldService.timerLog.info("job.world.addCountryRewards#leavemethod4:canGetReward#player_id:" + playerId);
            return true;
        }
        WorldService.timerLog.info("job.world.addCountryRewards#leavemethod5:canGetReward#player_id:" + playerId);
        return false;
    }
    
    @Transactional
    @Override
    public byte[] getRewardInfo(final PlayerDto playerDto, int id) {
        final int time = TimeUtil.nextHour();
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[53] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WORLD_CNP_REWARD_NO_OPEN);
        }
        final int forceId = playerDto.forceId;
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            PlayerWorld pw = this.playerWorldDao.read(playerId);
            if (pw == null) {
                this.createRecord(playerId);
                pw = this.playerWorldDao.read(playerId);
            }
            final JsonDocument doc = new JsonDocument();
            CountryRewardPerHour countryRewardPerHour = null;
            CitiesPerHour citiesPerHour = null;
            int silver = 0;
            int CNP = 0;
            int addCNP = 0;
            String cities = "";
            final String rewards = pw.getRewards();
            if (StringUtils.isBlank(rewards)) {
                final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
                final int officerId = (por == null) ? 37 : por.getOfficerId();
                final int killRank = this.killRankService.getRank(1, playerId, forceId);
                int P1 = this.cityDataCache.getCNPNum(1);
                int P2 = this.cityDataCache.getCNPNum(2);
                int P3 = this.cityDataCache.getCNPNum(3);
                int addP1 = 0;
                int addP2 = 0;
                int addP3 = 0;
                if (CityDataCache.leagueInfo != 0) {
                    if (CityDataCache.leagueInfo == 1) {
                        addP1 = (int)(P2 * 0.5f);
                        addP2 = (int)(P1 * 0.5f);
                    }
                    else if (CityDataCache.leagueInfo == 2) {
                        addP1 = (int)(P3 * 0.5f);
                        addP3 = (int)(P1 * 0.5f);
                    }
                    else {
                        addP2 = (int)(P3 * 0.5f);
                        addP3 = (int)(P2 * 0.5f);
                    }
                }
                P1 += addP1;
                P2 += addP2;
                P3 += addP3;
                final List<City> cityList = this.cityDao.getModels();
                final StringBuilder sb = new StringBuilder();
                for (final City city : cityList) {
                    sb.append(city.getId()).append("|").append(city.getForceId()).append(";");
                }
                cities = sb.toString();
                final int P4 = (forceId == 1) ? P1 : ((forceId == 2) ? P2 : P3);
                final double reward = P4;
                final int real = this.getRealValue(playerId, reward, true, TimeUtil.nextHour());
                doc.startObject();
                doc.createElement("id", 1);
                doc.createElement("time", time);
                doc.createElement("CNP", P4);
                doc.createElement("silver", real);
                doc.createElement("residueNum", pw.getRewardNum());
                doc.createElement("max", 24);
                doc.createElement("ms", (Object)(TimeUtil.nextHourMS() + 10000L));
                doc.createElement("officer", ((Halls)this.hallsCache.get((Object)officerId)).getNameList());
                doc.createElement("received", 0);
                doc.createElement("quality", this.getTimeQuality(time));
                doc.createElement("killRank", killRank);
                doc.createElement("cities", cities);
                doc.createElement("addCNP", this.getLeagueAddNPC(forceId));
                doc.startArray("specialCities");
                final Integer cityId = this.worldCitySpecialCache.getCityIdDisplayByKey(4);
                if (cityId != null) {
                    doc.startObject();
                    doc.createElement("cityId", cityId);
                    doc.createElement("cityName", ((WorldCity)this.worldCityCache.get((Object)cityId)).getName());
                    doc.createElement("hasSpecialCity", this.cityDataCache.hasCity(playerDto.forceId, cityId) ? 1 : 0);
                    doc.endObject();
                }
                doc.endArray();
                final int techShadi = this.techEffectCache.getTechEffect(playerId, 33);
                int iron = (int)this.cityEffectCache.getCityEffect(forceId, 4);
                iron = this.getRealValue(playerId, iron, true, TimeUtil.nextHour());
                if (techShadi > 0 && iron > 0) {
                    doc.createElement("iron", iron);
                }
                doc.createElement("shadi", (techShadi > 0) ? 1 : 0);
                if (techShadi <= 0) {
                    doc.createElement("reason", (Object)LocalMessages.REWARD_IRON_NO_TECH_SHADI);
                }
                doc.createElement("quality", ((WorldOutputPerTime)this.worldOutputPerTimeCache.get((Object)(TimeUtil.nextHour() + 1))).getQuality());
                doc.endObject();
                if (pw.getRewardNum() > 0) {
                    this.playerWorldDao.clearRewardNum(playerId);
                    WorldService.errorLog.error("Error deal rewrad and rewards not match, and playerId:" + playerId + ", rewardNum:" + pw.getRewardNum());
                }
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            final String[] rewardArry = rewards.split(":");
            if (id < 0 || id + 1 > rewardArry.length) {
                id = 0;
            }
            final String[] temp = rewardArry[id].split(",");
            if (temp.length <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10037);
            }
            final int hour = Integer.parseInt(temp[0]);
            if (temp.length > 2) {
                CNP = Integer.parseInt(temp[1]);
                addCNP = Integer.parseInt(temp[5]);
                silver = Integer.parseInt(temp[4]);
            }
            else {
                silver = Integer.parseInt(temp[1]);
            }
            countryRewardPerHour = this.countryRewardPerHourDao.getByHourAndForceId(hour, forceId);
            if (countryRewardPerHour != null) {
                final String[] tempArray = countryRewardPerHour.getRewards().split(",");
                CNP = Integer.parseInt(tempArray[0]);
                addCNP = Integer.parseInt(tempArray[1]);
                silver = this.getRealValue(playerId, CNP + addCNP, false, hour);
            }
            citiesPerHour = this.citiesPerHourDao.read(hour);
            if (citiesPerHour != null) {
                cities = citiesPerHour.getCities();
            }
            doc.startObject();
            doc.createElement("id", id);
            doc.createElement("time", hour);
            doc.createElement("CNP", CNP + addCNP);
            doc.createElement("addCNP", addCNP);
            doc.createElement("silver", silver);
            doc.createElement("cities", cities);
            doc.createElement("received", 2);
            doc.createElement("residueNum", pw.getRewardNum());
            doc.createElement("max", 24);
            doc.startArray("specialCities");
            final Integer cityId2 = this.worldCitySpecialCache.getCityIdDisplayByKey(4);
            if (cityId2 != null) {
                doc.startObject();
                doc.createElement("cityId", cityId2);
                doc.createElement("cityName", ((WorldCity)this.worldCityCache.get((Object)cityId2)).getName());
                doc.createElement("hasSpecialCity", this.cityDataCache.hasCity(playerDto.forceId, cityId2) ? 1 : 0);
                doc.endObject();
            }
            doc.endArray();
            final int techShadi2 = this.techEffectCache.getTechEffect(playerId, 33);
            int iron2 = (int)this.cityEffectCache.getCityEffect(forceId, 4);
            if (countryRewardPerHour != null) {
                final String[] tempArray2 = countryRewardPerHour.getRewards().split(",");
                if (tempArray2.length >= 3) {
                    iron2 = Integer.parseInt(tempArray2[2]);
                }
            }
            iron2 = this.getRealValue(playerId, iron2, false, hour);
            if (techShadi2 > 0 && iron2 > 0) {
                doc.createElement("iron", iron2);
            }
            doc.createElement("shadi", (techShadi2 > 0) ? 1 : 0);
            if (techShadi2 <= 0) {
                doc.createElement("reason", (Object)LocalMessages.REWARD_IRON_NO_TECH_SHADI);
            }
            doc.createElement("quality", ((WorldOutputPerTime)this.worldOutputPerTimeCache.get((Object)(hour + 1))).getQuality());
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            WorldService.errorLog.error("Exception Happens In getRewardInfo() WorldService.java: ");
            WorldService.errorLog.error("Exception Happens In getRewardInfo() WorldService.java: ", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10037);
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
    }
    
    private int getTimeQuality(final int time) {
        final WorldOutputPerTime worldOutputPerTime = (WorldOutputPerTime)this.worldOutputPerTimeCache.get((Object)(time % 24 + 1));
        final Integer qualityGet = worldOutputPerTime.getQuality();
        final int quality = (qualityGet == null) ? 0 : qualityGet;
        return quality;
    }
    
    private int getRealValue(final int playerId, final double src, final boolean display, final int hour) {
        double rate = 1.0 + this.techEffectCache.getTechEffect(playerId, 18) / 100.0;
        if (display) {
            rate *= ((WorldOutputPerTime)this.worldOutputPerTimeCache.get((Object)(TimeUtil.nextHour() + 1))).getBeta();
        }
        else {
            rate *= ((WorldOutputPerTime)this.worldOutputPerTimeCache.get((Object)(hour + 1))).getBeta();
        }
        return DataCastUtil.double2int(Math.ceil(src * rate));
    }
    
    @Transactional
    @Override
    public void dealLeague() {
        final long start = System.currentTimeMillis();
        long startServer = 0L;
        if (Configuration.getProperty("gcld.server.time") != null) {
            startServer = Long.valueOf(Configuration.getProperty("gcld.server.time"));
        }
        else {
            CityDataCache.leagueInfo = 0;
            Configuration.saveProperties("gcld.nation.leagueInfo", new StringBuilder().append(CityDataCache.leagueInfo).toString(), "serverstate.properties");
        }
        if (System.currentTimeMillis() < startServer + 86400000L) {
            return;
        }
        final int weiNum = this.cityDataCache.getCityNum(1);
        final int shuNum = this.cityDataCache.getCityNum(2);
        final int wuNum = this.cityDataCache.getCityNum(3);
        CityDataCache.leagueInfo = 0;
        if (weiNum + shuNum < wuNum) {
            CityDataCache.leagueInfo = 1;
        }
        else if (weiNum + wuNum < shuNum) {
            CityDataCache.leagueInfo = 2;
        }
        else if (shuNum + wuNum < weiNum) {
            CityDataCache.leagueInfo = 3;
        }
        Configuration.saveProperties("gcld.nation.leagueInfo", new StringBuilder().append(CityDataCache.leagueInfo).toString(), "serverstate.properties");
        WorldService.timerLog.info(LogUtil.formatThreadLog("WorldService", "dealLeague", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final String info = Configuration.getProperty("gcld.nation.leagueInfo");
        if (info != null) {
            CityDataCache.leagueInfo = Integer.valueOf(info);
        }
    }
    
    @Override
    public byte[] getQuizReward(final PlayerDto playerDto, final int quizId) {
        final int id = WorldCityCommon.getStaticIndex(quizId);
        final PlayerWorld pw = this.playerWorldDao.read(playerDto.playerId);
        int quizInfo = (pw.getQuizinfo() == null) ? 0 : pw.getQuizinfo();
        if (quizId < quizInfo) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
        }
        quizInfo |= (int)Math.pow(2.0, id);
        this.playerResourceDao.addCopperIgnoreMax(playerDto.playerId, 1000.0, "\u7b54\u9898\u5956\u52b1\u83b7\u5f97\u8d44\u6e90", true);
        this.playerWorldDao.updateQuizInfo(playerDto.playerId, quizInfo);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("curReward");
        doc.startObject();
        doc.createElement("type", 1);
        doc.createElement("value", 1000);
        doc.endObject();
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void updateCitiesPerHour() {
        final int hour = TimeUtil.getHour();
        final List<City> cityList = this.cityDao.getModels();
        final StringBuilder cities = new StringBuilder();
        for (final City city : cityList) {
            cities.append(city.getId()).append("|").append(city.getForceId()).append(";");
        }
        final CitiesPerHour citiesPerHour = new CitiesPerHour();
        citiesPerHour.setHour(hour);
        citiesPerHour.setCities(cities.toString());
        this.citiesPerHourDao.create(citiesPerHour);
    }
    
    @Override
    public void updateCountryRewardPerHour() {
        final int hour = TimeUtil.getHour();
        String rewards = null;
        CountryRewardPerHour countryRewardPerHour = null;
        for (int forceId = 1; forceId < 4; ++forceId) {
            rewards = this.getCountryRewardsPerHour(forceId);
            if (this.countryRewardPerHourDao.getByHourAndForceId(hour, forceId) == null) {
                countryRewardPerHour = new CountryRewardPerHour();
                countryRewardPerHour.setForceId(forceId);
                countryRewardPerHour.setHour(hour);
                countryRewardPerHour.setRewards(rewards);
                this.countryRewardPerHourDao.create(countryRewardPerHour);
            }
            else {
                this.countryRewardPerHourDao.UpdateByHourAndForceId(rewards, hour, forceId);
            }
        }
    }
    
    @Override
    public String getCountryRewardsPerHour(final int forceId) {
        final int PS1 = this.cityDataCache.getCNPNum(1);
        final int PS2 = this.cityDataCache.getCNPNum(2);
        final int PS3 = this.cityDataCache.getCNPNum(3);
        int addP1 = 0;
        int addP2 = 0;
        int addP3 = 0;
        if (CityDataCache.leagueInfo != 0) {
            if (CityDataCache.leagueInfo == 1) {
                addP1 = (int)(PS2 * 0.5f);
                addP2 = (int)(PS1 * 0.5f);
            }
            else if (CityDataCache.leagueInfo == 2) {
                addP1 = (int)(PS3 * 0.5f);
                addP3 = (int)(PS1 * 0.5f);
            }
            else {
                addP2 = (int)(PS3 * 0.5f);
                addP3 = (int)(PS2 * 0.5f);
            }
        }
        final StringBuilder rewards = new StringBuilder();
        rewards.append((forceId == 1) ? PS1 : ((forceId == 2) ? PS2 : PS3)).append(",");
        rewards.append((forceId == 1) ? addP1 : ((forceId == 2) ? addP2 : addP3));
        rewards.append(",");
        rewards.append((int)this.cityEffectCache.getCityEffect(forceId, 4));
        return rewards.toString();
    }
    
    @Override
    public void pushCountryRewardNum() {
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        JsonDocument doc = null;
        int playerId = 0;
        int countryRewardNum = 0;
        for (final PlayerDto onlinePlayer : onlinePlayerList) {
            playerId = onlinePlayer.playerId;
            final PlayerWorld pw = this.playerWorldDao.read(playerId);
            if (pw != null) {
                doc = new JsonDocument();
                doc.startObject();
                if (pw.getRewardNum() != null) {
                    countryRewardNum = pw.getRewardNum();
                }
                doc.createElement("countryRewardNum", countryRewardNum);
                doc.endObject();
                Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
        }
    }
}
