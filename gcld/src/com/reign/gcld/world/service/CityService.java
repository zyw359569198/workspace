package com.reign.gcld.world.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.civiltrick.service.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.grouparmy.dao.*;
import com.reign.gcld.tavern.service.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.rank.dao.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import java.util.concurrent.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.message.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.civiltrick.trick.*;
import java.text.*;
import org.apache.commons.lang.*;
import com.reign.gcld.world.util.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.common.event.*;
import com.reign.gcld.rank.common.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.rank.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.chat.service.*;
import org.dom4j.io.*;
import org.dom4j.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.task.domain.*;
import com.reign.util.*;
import com.reign.gcld.huizhan.domain.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.grouparmy.domain.*;
import com.reign.gcld.task.reward.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.slave.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.sdata.domain.*;
import java.io.*;

@Component("cityService")
public class CityService implements ICityService
{
    private static final Logger timerLog;
    private static final Logger battleLog;
    private static final Logger errorLog;
    private static final int PLAYER_EVENT_TYPE_START = 1000;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private ICityNpcLostDao cityNpcLostDao;
    @Autowired
    private WorldRoadCache worldRoadCache;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private WorldCityAreaCache worldCityAreaCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private ICityDataCache cityDataCache;
    @Autowired
    private ICilvilTrickService cilvilTrickService;
    @Autowired
    private WorldTreasureCache worldTreasureCache;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IPlayerGroupArmyDao playerGroupArmyDao;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private StratagemCache stratagemCache;
    @Autowired
    private IGroupArmyDao groupArmyDao;
    @Autowired
    private IRankService rankService;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IPlayerWorldDao playerWorldDao;
    @Autowired
    private TroopCache troopCache;
    @Autowired
    private IWorldService worldService;
    @Autowired
    private IPlayerKillInfoDao playerKillInfoDao;
    @Autowired
    private WorldOutputPerTimeCache worldOutputPerTimeCache;
    @Autowired
    private ITavernService tavernService;
    @Autowired
    private CityEffectCache cityEffectCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private INationTaskDao nationTaskDao;
    @Autowired
    private KingdomTaskRankingCache kingdomTaskRankingCache;
    @Autowired
    private IWholeKillDao wholeKillDao;
    @Autowired
    private IBattleService battleService;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private IJuBenService juBenService;
    @Autowired
    private IWorldFarmService worldFarmService;
    @Autowired
    private FarmCache farmCache;
    @Autowired
    private IIndividualTaskService individualTaskService;
    public static Object[] cityLocks;
    public static int[] wei;
    public static int[] wu;
    public static int[] shu;
    public static int[] cityFlagSetForNationTaskYellowTurban;
    private static ReentrantLock[] locks;
    private static final int LOCKS_LEN;
    private ReadWriteLock rwLock;
    public static Map<Integer, String> cityBatIdSet;
    public static ConcurrentHashMap<String, GeneralMoveDto> gmMoveDtoMap;
    public static ConcurrentHashMap<Integer, String> worldGeneralCityMap;
    
    static {
        timerLog = new TimerLogger();
        battleLog = new BattleLogger();
        errorLog = CommonLog.getLog(CityService.class);
        CityService.cityLocks = new Object[281];
        CityService.wei = new int[512];
        CityService.wu = new int[512];
        CityService.shu = new int[512];
        CityService.cityFlagSetForNationTaskYellowTurban = new int[512];
        for (int i = 0; i < 280; ++i) {
            CityService.cityLocks[i] = new Object();
        }
        for (int i = 0; i < 512; ++i) {
            CityService.wei[i] = 0;
            CityService.shu[i] = 0;
            CityService.wu[i] = 0;
        }
        for (int i = 0; i < 512; ++i) {
            CityService.cityFlagSetForNationTaskYellowTurban[i] = 0;
        }
        CityService.locks = new ReentrantLock[10240];
        LOCKS_LEN = CityService.locks.length;
        for (int i = 0; i < CityService.LOCKS_LEN; ++i) {
            CityService.locks[i] = new ReentrantLock(false);
        }
        CityService.cityBatIdSet = new HashMap<Integer, String>();
        CityService.gmMoveDtoMap = new ConcurrentHashMap<String, GeneralMoveDto>();
        CityService.worldGeneralCityMap = new ConcurrentHashMap<Integer, String>();
    }
    
    public CityService() {
        this.rwLock = new ReentrantReadWriteLock();
    }
    
    public static String[] getCityLock(final int cityA, final int cityB) {
        final String[] citys = new String[2];
        if (cityA < cityB) {
            citys[0] = CityService.cityBatIdSet.get(cityA);
            citys[1] = CityService.cityBatIdSet.get(cityB);
        }
        else {
            citys[0] = CityService.cityBatIdSet.get(cityB);
            citys[1] = CityService.cityBatIdSet.get(cityA);
        }
        return citys;
    }
    
    public static synchronized int getCityFlag(final int cityId) {
        return CityService.cityFlagSetForNationTaskYellowTurban[cityId];
    }
    
    public static synchronized void setCityFlag(final int cityId, final int flag) {
        CityService.cityFlagSetForNationTaskYellowTurban[cityId] = flag;
    }
    
    public static GeneralMoveDto getGeneralMoveDto(final int playerId, final int generalId) {
        final StringBuilder sb = new StringBuilder();
        sb.append(playerId).append("_").append(generalId);
        final GeneralMoveDto gmd = CityService.gmMoveDtoMap.get(sb.toString());
        if (gmd == null) {
            return null;
        }
        return gmd;
    }
    
    public static GeneralMoveDto getUpdateGeneralMoveDto(final int playerId, final int generalId) {
        final StringBuilder sb = new StringBuilder();
        sb.append(playerId).append("_").append(generalId);
        GeneralMoveDto gmd = CityService.gmMoveDtoMap.get(sb.toString());
        if (gmd == null) {
            gmd = new GeneralMoveDto();
            CityService.gmMoveDtoMap.put(sb.toString(), gmd);
        }
        return gmd;
    }
    
    public static void clearGeneralMove(final int playerId, final int generalId) {
        final StringBuilder sb = new StringBuilder();
        sb.append(playerId).append("_").append(generalId);
        CityService.gmMoveDtoMap.remove(sb.toString());
    }
    
    @Override
    public void clearGeneralsMove(final int playerId) {
        final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm : list) {
            clearGeneralMove(playerId, pgm.getGeneralId());
        }
    }
    
    @Transactional
    @Override
    public byte[] enterWorldScene(final PlayerDto playerDto, final Request request) {
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        this.joinGroup(playerDto.playerId, playerDto.forceId, request);
        TaskMessageHelper.sendVisitAreaTaskMessage(playerId, 6);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        int generalId = 0;
        int initCity = 0;
        int maskCity = 0;
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        final String xyAxis = CityService.worldGeneralCityMap.get(playerId);
        if (xyAxis == null) {
            if (pgmList.size() > 0) {
                final PlayerGeneralMilitary pgm = pgmList.get(0);
                initCity = pgm.getLocationId();
            }
            else {
                generalId = 0;
                initCity = WorldCityCommon.nationMainCityIdMap.get(forceId);
            }
        }
        else {
            final String[] xys = xyAxis.split(":");
            generalId = Integer.valueOf(xys[0]);
            maskCity = Integer.valueOf(xys[1]);
            if (maskCity > 0) {
                final int areaId = this.worldCityCache.getArea(forceId, maskCity);
                final List<WorldCity> list = this.worldCityCache.getAreaCity(forceId, areaId);
                final StringBuilder sb = new StringBuilder();
                for (final WorldCity wc : list) {
                    sb.append(wc.getId()).append("|");
                }
                doc.createElement("maskCity", maskCity);
                doc.createElement("winNpc", true);
                doc.createElement("openCities", sb.toString());
                final int armyId = ((WorldCityArea)this.worldCityAreaCache.get((Object)areaId)).getMaskChief();
                final Army army = (Army)this.dataGetter.getArmyCache().get((Object)armyId);
                final General general = (General)this.dataGetter.getGeneralCache().get((Object)army.getGeneralId());
                doc.createElement("rewardType", this.worldCityAreaCache.getMaskDropType(areaId));
                doc.createElement("gPic", general.getPic());
                CityService.worldGeneralCityMap.put(playerId, String.valueOf(generalId) + ":" + 0);
            }
            if (generalId > 0) {
                PlayerGeneralMilitary pgm2 = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
                if (pgm2 != null) {
                    generalId = pgm2.getGeneralId();
                    initCity = pgm2.getLocationId();
                }
                else {
                    final List<PlayerGeneralMilitary> list2 = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
                    if (list2.size() > 0) {
                        pgm2 = list2.get(0);
                        generalId = pgm2.getGeneralId();
                        initCity = pgm2.getLocationId();
                    }
                    else {
                        initCity = WorldCityCommon.nationMainCityIdMap.get(forceId);
                    }
                }
            }
            else {
                final List<PlayerGeneralMilitary> list3 = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
                if (list3.size() > 0) {
                    final PlayerGeneralMilitary pgm3 = list3.get(0);
                    if ((pa.getEnterCount() & 0x80) == 0x80) {
                        generalId = pgm3.getGeneralId();
                    }
                    initCity = pgm3.getLocationId();
                }
                else {
                    initCity = WorldCityCommon.nationMainCityIdMap.get(forceId);
                }
            }
        }
        doc.createElement("generalId", generalId);
        doc.createElement("initCity", initCity);
        if (cs[34] == '1') {
            doc.createElement("ironOpen", 1);
        }
        else if (cs[40] == '1') {
            doc.createElement("gemOpen", 1);
        }
        doc.createElement("league", CityDataCache.leagueInfo);
        PlayerWorld pw = this.playerWorldDao.read(playerId);
        if (pw == null) {
            this.worldService.createRecord(playerId);
            pw = this.playerWorldDao.read(playerId);
        }
        final List<PlayerGeneralCivil> pgcList = this.playerGeneralCivilDao.getCivilListOrderByLv(playerId);
        doc.startArray("stratagem");
        for (final PlayerGeneralCivil pgc : pgcList) {
            final General general2 = (General)this.generalCache.get((Object)pgc.getGeneralId());
            final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)general2.getStratagemId());
            if (stratagem == null) {
                continue;
            }
            doc.startObject();
            TrickFactory.getTrickInfo(doc, stratagem);
            doc.createElement("stratagemId", stratagem.getId());
            doc.createElement("cilvilId", general2.getId());
            doc.createElement("pic", general2.getPic());
            if (pgc.getCd() != null) {
                final long cd = pgc.getCd().getTime() - new Date().getTime();
                doc.createElement("cd", (cd > 0L) ? cd : 0L);
            }
            doc.createElement("stratagemIntro", stratagem.getIntro());
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("maxCilvilNnum", this.tavernService.getMaxGeneralNum(playerId, playerDto.playerLv, 1));
        doc.createElement("CNP", this.cityDataCache.getCNPNum(forceId));
        doc.createElement("cityNum", this.cityDataCache.getCityNum(forceId));
        doc.createElement("rewardNum", pw.getRewardNum());
        doc.createElement("maxNum", 24);
        doc.createElement("addCNP", this.worldService.getLeagueAddNPC(forceId));
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final PlayerKillInfo playerKillInfo = this.playerKillInfoDao.getByTodayInfo(playerId, format.format(WorldCityCommon.getDateAfter23(new Date())));
        doc.createElement("leftTime", (Object)TimeUtil.nextHourMS());
        doc.createElement("killNum", (playerKillInfo == null || playerKillInfo.getKillNum() == null) ? 0 : playerKillInfo.getKillNum());
        final int time = TimeUtil.nextHour();
        final WorldOutputPerTime worldOutputPerTime = (WorldOutputPerTime)this.worldOutputPerTimeCache.get((Object)(time + 1));
        final int quality = (worldOutputPerTime == null || worldOutputPerTime.getQuality() == null) ? 0 : worldOutputPerTime.getQuality();
        doc.createElement("quality", (worldOutputPerTime.getQuality() == null) ? 0 : quality);
        final byte[] bytes = this.individualTaskService.getRightBarIndivTaskInfo(playerDto);
        if (bytes != null) {
            doc.appendJson(bytes);
        }
        this.getPlayerNationTaskTitle(playerId, forceId, doc);
        final int hasbarTask = this.rankService.hasBarTasks(forceId);
        if (hasbarTask <= 0) {
            this.getWholeKillTitle(playerId, forceId, doc, true);
        }
        final Set<Integer> attedSet = new HashSet<Integer>();
        final Set<Integer> canAttNpc = new HashSet<Integer>();
        int cityId = 0;
        if (pw.getAttedId() != null) {
            final String[] ids = pw.getAttedId().split(",");
            String[] array;
            for (int length = (array = ids).length, k = 0; k < length; ++k) {
                final String str = array[k];
                cityId = Integer.valueOf(str);
                attedSet.add(cityId);
            }
        }
        final Set<Integer> canAttSet = new HashSet<Integer>();
        if (!StringUtils.isBlank(pw.getCanAttId())) {
            final String[] ids2 = pw.getCanAttId().split(",");
            String[] array2;
            for (int length2 = (array2 = ids2).length, l = 0; l < length2; ++l) {
                final String str2 = array2[l];
                cityId = Integer.valueOf(str2);
                if (this.worldCityCache.getMaskSet().contains(cityId)) {
                    canAttSet.add(cityId);
                }
                canAttNpc.add(cityId);
            }
        }
        final StringBuilder sb2 = new StringBuilder();
        final City[] cityList = CityDataCache.cityArray;
        doc.startArray("broders");
        for (int i = 1; i <= 3; ++i) {
            doc.startObject();
            doc.createElement("broderId", i);
            doc.createElement("addExp", (int)Math.ceil(WorldCityCommon.getAddExp(i) * 100.0f));
            doc.createElement("reduceAttDef", (int)Math.ceil(WorldCityCommon.getReduceAttDef(i) * 100.0f));
            doc.endObject();
        }
        doc.endArray();
        int canAttCity = 0;
        int rewardType = 0;
        int borderType = 0;
        int attCity = 0;
        final StringBuilder sbBuff = new StringBuilder();
        WorldCity worldCity = null;
        doc.startArray("cityTrickState");
        int cc = 0;
        City[] array3;
        for (int length3 = (array3 = cityList).length, n = 0; n < length3; ++n) {
            final City city = array3[n];
            if (city == null) {
                if (cc == 0 || cc > 254) {
                    ++cc;
                    continue;
                }
                CityService.errorLog.error("cityId error: " + cc);
            }
            ++cc;
            borderType = 0;
            attCity = 0;
            if (!WorldCityCommon.mainCityNationIdMap.containsKey(city.getId())) {
                if (city.getForceId() == forceId) {
                    if (!this.worldCityCache.getDistanceCities(forceId).containsKey(city.getId()) && this.worldCityCache.getDistanceCities().containsKey(city.getId())) {
                        borderType = WorldCityCommon.getDistanceState(this.worldCityCache.getDistanceCities().get(city.getId()));
                        sbBuff.append(city.getId()).append(((WorldCity)this.worldCityCache.get((Object)city.getId())).getName()).append(":").append(borderType).append(";\t");
                    }
                }
                else if (city.getBorder() == 1 && WorldCityCommon.nationMainCityIdMap.containsKey(city.getForceId())) {
                    final Set<Integer> nbSet = this.worldRoadCache.getNeighbors(city.getId());
                    if (nbSet != null) {
                        City subCity = null;
                        for (final Integer key : nbSet) {
                            subCity = CityDataCache.cityArray[key];
                            if (subCity != null && subCity.getForceId() == forceId) {
                                attCity = 1;
                                if (subCity.getBorder() == 1 && this.worldCityCache.getDistanceCities(city.getForceId()).containsKey(city.getId()) && !this.worldCityCache.getDistanceCities(forceId).containsKey(city.getId())) {
                                    borderType = WorldCityCommon.getDistanceState(this.worldCityCache.getDistanceCities().get(city.getId()));
                                    sbBuff.append(city.getId()).append(((WorldCity)this.worldCityCache.get((Object)city.getId())).getName()).append(":").append(borderType).append(";\t");
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                }
                else if (!WorldCityCommon.nationMainCityIdMap.containsKey(city.getForceId())) {
                    final Set<Integer> nbSet = this.worldRoadCache.getNeighbors(city.getId());
                    if (nbSet != null) {
                        City subCity = null;
                        for (final Integer key : nbSet) {
                            subCity = CityDataCache.cityArray[key];
                            if (subCity != null && subCity.getForceId() == forceId) {
                                attCity = 1;
                                break;
                            }
                        }
                    }
                }
            }
            this.getCityTrickInfo(doc, city, playerDto);
            if (cs[61] == '1') {
                this.getCityEventInfo(doc, city, playerDto);
            }
            this.getPlayerEventInfo(doc, city, playerDto.playerId);
            this.getShaDiLingInfo(doc, city, playerDto);
            sb2.append(city.getId()).append("|").append(city.getForceId()).append("|").append(city.getState()).append("|");
            if (WorldCityCommon.specialNationIdMap.containsKey(city.getId())) {
                if (WorldCityCommon.nationMainCityIdMap.containsKey(city.getId())) {
                    attCity = 0;
                }
                sb2.append(0).append("|");
            }
            else {
                sb2.append(city.getTitle()).append("|");
            }
            canAttCity = (canAttSet.contains(city.getId()) ? 1 : 0);
            rewardType = 0;
            int hp = 0;
            int maxHp = 0;
            int mistHp = 0;
            int mistMaxHp = 0;
            int isInMist = 0;
            if (canAttNpc.contains(city.getId()) || (!canAttNpc.contains(city.getId()) && !attedSet.contains(city.getId()))) {
                isInMist = 1;
            }
            worldCity = (WorldCity)this.worldCityCache.get((Object)city.getId());
            if (canAttCity == 1) {
                final int areaId2 = this.worldCityCache.getArea(forceId, worldCity);
                rewardType = this.worldCityAreaCache.getMaskDropType(areaId2);
                final Tuple<Integer, Integer> blood = this.getMistBlood(city.getId(), pw, areaId2);
                mistHp = (blood.left == null) ? 0 : blood.left;
                mistMaxHp = (blood.right == null) ? 0 : blood.right;
            }
            else if (city.getForceId() == 0) {
                rewardType = this.worldCityCache.getCityDropType(city.getId());
                hp = ((city.getHp() == null) ? 0 : city.getHp());
                maxHp = ((city.getHpMax() == null) ? 0 : city.getHpMax());
            }
            final int gNumState = this.cityDataCache.getGeneralNum(city.getId());
            if (gNumState == 1) {
                sb2.append(1);
            }
            else if (gNumState > 10) {
                sb2.append(3);
            }
            else {
                sb2.append(2);
            }
            sb2.append("|").append(attedSet.contains(city.getId()) ? 1 : 0).append("|").append(canAttCity).append("|").append(rewardType).append("|").append(borderType).append("|").append(hp).append("|").append(maxHp).append("|").append(mistHp).append("|").append(mistMaxHp).append("|").append(isInMist).append("|").append(attCity).append("|").append(worldCity.getOutput()).append("#");
        }
        doc.endArray();
        final List<NationTaskAnd> taskAnds = this.rankService.getNationTaskAnds(forceId);
        if (playerDto.playerLv >= 30) {
            doc.appendJson("curNationTask", this.rankService.getCurNationTaskSimpleInfo(playerId, taskAnds));
        }
        doc.createElement("cities", sb2.toString());
        doc.appendJson("curTryTask", this.dataGetter.getNationService().getTryTaskInfo());
        doc.appendJson("curPTasks", this.dataGetter.getProtectService().getManWangLingInfo());
        this.getBoxInfo(playerId, doc);
        final Integer quiz = pw.getQuizinfo();
        if (quiz == null) {
            if (generalId == 0) {
                doc.createElement("quizInfo", 1);
            }
        }
        else if (quiz == 0 && generalId == 0) {
            doc.createElement("quizInfo", 1);
        }
        if (generalId > 0 && initCity > 0) {
            doc.appendJson(this.getAttMoveInfo(initCity, forceId, playerId));
        }
        final long curTime = System.currentTimeMillis();
        double percentPath = 0.0;
        doc.startArray("generalPaths");
        for (final PlayerGeneralMilitary pgm4 : pgmList) {
            final GeneralMoveDto gmd = getGeneralMoveDto(playerId, pgm4.getGeneralId());
            if (gmd != null && gmd.nextMoveTime > curTime && gmd.type == 1) {
                doc.startObject();
                doc.createElement("generalId", pgm4.getGeneralId());
                doc.createElement("startCityId", gmd.startCityId);
                percentPath = 0.0;
                if (gmd.nextMoveTime > curTime) {
                    percentPath = (curTime - gmd.startMoveTime) * 1.0 / (gmd.nextMoveTime - gmd.startMoveTime);
                    percentPath = Math.ceil(percentPath * 100.0) / 100.0;
                }
                doc.createElement("percent", percentPath);
                doc.appendJson(this.getMovePath(pgm4.getLocationId(), gmd.moveLine));
                doc.endObject();
            }
        }
        doc.endArray();
        final boolean isWholePointKill = WorldUtil.isWholePointKill();
        if (isWholePointKill) {
            doc.createElement("isWholePointKill", 1);
        }
        else {
            doc.createElement("isWholePointKill", 0);
            final WholeKill wholeKill = this.wholeKillDao.read(playerId);
            doc.createElement("received", wholeKill.getReceivedReward());
        }
        final ForceInfo forceInfo = this.dataGetter.getForceInfoDao().read(playerDto.forceId);
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        if (forceInfo.getForceLv() >= 2) {
            final int officerId = (por == null || por.getOfficerId() == null) ? 37 : por.getOfficerId();
            if (this.dataGetter.getHallsCache().getTokenList().contains(officerId)) {
                final OfficerToken token = this.dataGetter.getOfficerTokenDao().getTokenByForceIdAndOfficerId(officerId, playerDto.forceId);
                final int shaDiLingNum = (token == null) ? 0 : token.getKillTokenNum();
                doc.createElement("shaDiLingNum", shaDiLingNum);
            }
        }
        this.dataGetter.getHuiZhanService().getHzInfoInCity(doc);
        doc.startArray("forcastWishEvent");
        for (int j = 0; j < WishEvent.messageList.length; ++j) {
            if (!StringUtils.isBlank(WishEvent.messageList[j])) {
                doc.startObject();
                doc.createElement("content", WishEvent.messageList[j]);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        CityService.battleLog.debug("AAA\u4e16\u754c\u57ce\u5e02\u524d\u7ebf\u72b6\u6001" + sbBuff.toString());
        EventListener.fireEvent(new CommonEvent(22, playerId));
        final List<PlayerGeneralMilitary> generalMilitaries = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        final Set<Integer> locationSet = new HashSet<Integer>();
        for (final PlayerGeneralMilitary pgm5 : generalMilitaries) {
            if (!locationSet.contains(pgm5.getLocationId())) {
                this.fireJoinGroupEvent(pgm5.getLocationId(), request, playerId);
            }
            locationSet.add(pgm5.getLocationId());
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void getShaDiLingInfo(final JsonDocument doc, final City city, final PlayerDto playerDto) {
        final Map<Integer, Long> map = this.getShaDiLingInfoInThisCity(city);
        if (map == null || map.size() == 0) {
            return;
        }
        final Long expireTime = map.get(playerDto.forceId);
        if (expireTime != null && expireTime > System.currentTimeMillis()) {
            doc.startObject();
            doc.createElement("cityId", city.getId());
            doc.startObject("shaDiLingNum");
            doc.createElement("countDown", expireTime - System.currentTimeMillis());
            doc.endObject();
            doc.endObject();
        }
    }
    
    @Override
    public void getPlayerEventInfo(final JsonDocument doc, final City city, final int playerId) {
        final int cityId = city.getId();
        final Map<Integer, PlayerEventObj> map = CityEventManager.getInstance().playerEventMap.get(playerId);
        if (map == null) {
            return;
        }
        for (final Map.Entry<Integer, PlayerEventObj> entry : map.entrySet()) {
            final Integer eventType = entry.getKey();
            final PlayerEventObj playerEventObj = entry.getValue();
            final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)eventType);
            if (playerEventObj.cityId == cityId) {
                final Tuple<List<Map<Integer, Integer>>, List<Map<Integer, Integer>>> tuple = CityEventManager.getInstance().getPlayerEventCost(playerId, eventType, playerEventObj);
                final boolean isWorldDramaEvent = eventType == 5;
                final boolean isLianbingling = eventType == 6;
                final boolean isTuFeiSlave = eventType == 8;
                List<Map<Integer, Integer>> List = null;
                Map<Integer, Integer> consumeMap = null;
                Map<Integer, Integer> gainMap = null;
                if (!isWorldDramaEvent) {
                    List = tuple.left;
                    if (List == null) {
                        return;
                    }
                    if (isLianbingling || isTuFeiSlave) {
                        gainMap = List.get(1);
                    }
                    else {
                        consumeMap = List.get(0);
                        gainMap = List.get(1);
                    }
                }
                doc.startObject();
                doc.createElement("cityId", cityId);
                doc.startObject("playerEvent");
                doc.createElement("eventType", eventType + 1000);
                String pic = wdSjp.getPic();
                if (eventType == 4) {
                    final FstNdEvent fne = (FstNdEvent)this.dataGetter.getFstNdEventCache().get((Object)playerEventObj.nationTreasureEventId);
                    pic = fne.getPic();
                }
                else if (eventType == 5) {
                    final int dramaId = playerEventObj.worldDramaId;
                    final WdSjpDrama drama = (WdSjpDrama)this.dataGetter.getWdSjpDramaCache().get((Object)dramaId);
                    if (drama != null) {
                        doc.createElement("grade", drama.getDifficulty());
                    }
                }
                doc.createElement("pic", pic);
                if (!isWorldDramaEvent) {
                    if (!isLianbingling && !isTuFeiSlave) {
                        doc.startArray("cost");
                        for (final Map.Entry<Integer, Integer> cost : consumeMap.entrySet()) {
                            doc.startObject();
                            doc.createElement("costType", cost.getKey());
                            doc.createElement("costNum", cost.getValue());
                            doc.endObject();
                        }
                        doc.endArray();
                    }
                    doc.startArray("gain");
                    for (final Map.Entry<Integer, Integer> gain : gainMap.entrySet()) {
                        doc.startObject();
                        doc.createElement("gainType", gain.getKey());
                        doc.createElement("gainNum", gain.getValue());
                        doc.endObject();
                    }
                    doc.endArray();
                }
                final List<Map<Integer, Integer>> list2 = tuple.right;
                if (list2 != null) {
                    final Map<Integer, Integer> consumeMap2 = list2.get(0);
                    final Map<Integer, Integer> gainMap2 = list2.get(1);
                    doc.startArray("cost2");
                    for (final Map.Entry<Integer, Integer> cost2 : consumeMap2.entrySet()) {
                        doc.startObject();
                        doc.createElement("costType", cost2.getKey());
                        doc.createElement("costNum", cost2.getValue());
                        doc.endObject();
                    }
                    doc.endArray();
                    doc.startArray("gain2");
                    for (final Map.Entry<Integer, Integer> gain2 : gainMap2.entrySet()) {
                        doc.startObject();
                        doc.createElement("gainType", gain2.getKey());
                        doc.createElement("gainNum", gain2.getValue());
                        doc.endObject();
                    }
                    doc.endArray();
                }
                doc.endObject();
                doc.endObject();
            }
        }
    }
    
    private void getPlayerNationTaskTitle(final int playerId, int forceId, final JsonDocument doc) {
        try {
            final List<NationTaskAnd> tasks = this.rankService.getNationTaskAnds(forceId);
            if (tasks == null || tasks.isEmpty()) {
                return;
            }
            final int hasNationTask = this.rankService.hasNationTasks(forceId);
            int titleMin = 10;
            int taskType = 0;
            int rankNum = 999;
            BaseRanker baseRanker = null;
            int killNum = 0;
            for (final NationTaskAnd t : tasks) {
                if (t.getIsWin() != 0) {
                    continue;
                }
                taskType = ((t.getTaskId() == 0) ? 0 : RankService.getTaskTypeById(t.getTaskId()));
                if (hasNationTask > 0 && taskType <= 0) {
                    continue;
                }
                if (taskType == 0) {
                    baseRanker = RankService.barbarainRanker;
                    final BarbariansKillInfo killInfo = this.dataGetter.getBarbariansKillInfoDao().read(playerId);
                    killNum = ((killInfo == null) ? 0 : killInfo.getKillnum());
                }
                else {
                    baseRanker = RankService.nationTaskKillRanker.getRanker(t.getTaskId());
                    final TaskKillInfo taskKillInfo = this.dataGetter.getTaskKillInfoDao().getTaskKillInfoByPAndT(playerId, t.getTaskId());
                    killNum = ((taskKillInfo == null) ? 0 : taskKillInfo.getKillnum());
                }
                if (baseRanker == null) {
                    continue;
                }
                if (taskType == 5) {
                    forceId = 0;
                }
                final int rank = baseRanker.getRank(1, playerId, forceId);
                final int title = this.kingdomTaskRankingCache.getTaskRankingLv(rank, taskType);
                if (title < titleMin) {
                    titleMin = title;
                }
                if (rank >= rankNum) {
                    continue;
                }
                rankNum = rank;
            }
            if (baseRanker != null) {
                int type = 0;
                final int curRank = rankNum;
                int nextRank = this.kingdomTaskRankingCache.getNextRank(curRank, 1, taskType, 1);
                if (nextRank == 0) {
                    type = 1;
                }
                nextRank = ((nextRank == 0) ? (curRank - 2) : (nextRank - 1));
                final int nextKillNum = this.dataGetter.getRankService().getNationTaskNextKillNum(baseRanker, forceId, nextRank, taskType, 1);
                doc.createElement("type", type);
                doc.createElement("nextKillNum", (nextKillNum - killNum <= 0) ? 1 : (nextKillNum - killNum));
                if (type == 0) {
                    nextRank = ((nextRank == 0) ? 1 : nextRank);
                    final int nextLv = this.kingdomTaskRankingCache.getTaskRankingLv(nextRank, taskType);
                    doc.createElement("nextTitle", this.kingdomTaskRankingCache.getTitleString(nextLv, taskType));
                    doc.createElement("lvNext", this.kingdomTaskRankingCache.getTitleQuality(nextLv, taskType));
                }
            }
            doc.createElement("killTotal", killNum);
            this.rankService.titleInfoByCurTitle(titleMin, taskType, doc, rankNum);
        }
        catch (Exception e) {
            CityService.errorLog.error("getPlayerNationTaskTitle exception", e);
        }
    }
    
    @Override
    public void getWholeKillTitle(final int playerId, final int forceId, final JsonDocument doc, final boolean check) {
        if (check && !WorldUtil.isWholePointKill()) {
            return;
        }
        int titleMin = 10;
        final int taskType = 999;
        int rankNum = 999;
        final BaseRanker baseRanker = RankService.wholeKillRank;
        final WholeKill wk = this.wholeKillDao.read(playerId);
        if (wk == null || wk.getReceivedReward() == null) {
            CityService.errorLog.error("CityService#getWholeKillTitle#null_error#playerId=" + playerId);
        }
        final boolean isReceived = wk.getReceivedReward() == 1;
        int rank = baseRanker.getRank(1, playerId, forceId);
        if (!isReceived) {
            rank = wk.getLastRank();
        }
        final int title = this.kingdomTaskRankingCache.getTaskRankingLv(rank, taskType);
        if (title < titleMin) {
            titleMin = title;
        }
        if (rank < rankNum) {
            rankNum = rank;
        }
        final int killNum = isReceived ? wk.getKillNum() : wk.getLastNum();
        if (!isReceived) {
            rankNum = wk.getLastRank();
        }
        int type = 0;
        final int curRank = rankNum;
        int nextRank = this.kingdomTaskRankingCache.getNextRank(curRank, 1, taskType, 1);
        if (nextRank == 0) {
            type = 1;
        }
        nextRank = ((nextRank == 0) ? (curRank - 2) : (nextRank - 1));
        final int nextKillNum = this.dataGetter.getRankService().getNationTaskNextKillNum(baseRanker, forceId, nextRank, taskType, 1);
        doc.createElement("type", type);
        doc.createElement("nextKillNum", (nextKillNum - killNum <= 0) ? 1 : (nextKillNum - killNum));
        if (type == 0) {
            nextRank = ((nextRank == 0) ? 1 : nextRank);
            final int nextLv = this.kingdomTaskRankingCache.getTaskRankingLv(nextRank, taskType);
            doc.createElement("nextTitle", this.kingdomTaskRankingCache.getTitleString(nextLv, taskType));
            doc.createElement("lvNext", this.kingdomTaskRankingCache.getTitleQuality(nextLv, taskType));
        }
        doc.createElement("killTotal", killNum);
        this.rankService.titleInfoByCurTitle(titleMin, taskType, doc, rankNum);
        doc.createElement("received", wk.getReceivedReward());
        if (isReceived) {
            doc.createElement("time", (Object)TimeUtil.nextHourMS());
        }
        final int lv = this.kingdomTaskRankingCache.getTaskRankingLv(curRank, taskType);
        doc.createElement("lv", this.kingdomTaskRankingCache.getTitleQuality(lv, taskType));
        doc.createElement("titleName", this.kingdomTaskRankingCache.getTitlePicName(lv, taskType));
    }
    
    private void getCityTrickInfo(final JsonDocument doc, final City city, final PlayerDto playerDto) {
        final String cityState = city.getTrickinfo();
        if (!StringUtils.isBlank(cityState)) {
            doc.startObject();
            doc.createElement("cityId", city.getId());
            doc.startArray("trickState");
            String[] split;
            for (int length = (split = cityState.split("#")).length, i = 0; i < length; ++i) {
                final String s = split[i];
                if (!StringUtils.isBlank(s)) {
                    final String[] c = s.split("-");
                    if (c.length > 0) {
                        final int i_d = Integer.parseInt(c[0]);
                        final Stratagem stratagem = (Stratagem)this.stratagemCache.get((Object)i_d);
                        if (stratagem != null) {
                            if (stratagem.getType().equalsIgnoreCase("xianjing")) {
                                final int p_d = Integer.parseInt(c[4]);
                                if (playerDto.forceId != p_d) {
                                    continue;
                                }
                            }
                            else if (stratagem.getType().equalsIgnoreCase("guwu")) {
                                final int p_d = Integer.parseInt(c[3]);
                                if (p_d != playerDto.forceId) {
                                    continue;
                                }
                            }
                            else if (stratagem.getType().equalsIgnoreCase("dongyao")) {
                                final int p_d = Integer.parseInt(c[3]);
                                if (p_d != playerDto.forceId) {
                                    continue;
                                }
                            }
                            else if (stratagem.getType().equalsIgnoreCase("huangbao")) {
                                final int p_d = Integer.parseInt(c[3]);
                                if (p_d != playerDto.forceId) {
                                    continue;
                                }
                            }
                            final long cd = Long.parseLong(c[2]);
                            final long now = new Date().getTime();
                            long protectCd = 0L;
                            boolean flag = false;
                            if (cd > now) {
                                protectCd = cd - now;
                                flag = true;
                            }
                            long lastTime = 0L;
                            if (c.length > 3) {
                                final long lastCd = Long.parseLong(c[c.length - 1]);
                                if (lastCd > now) {
                                    flag = true;
                                    lastTime = lastCd - now;
                                }
                                else {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                doc.startObject();
                                doc.createElement("stratagemId", c[0]);
                                doc.createElement("type", stratagem.getType());
                                doc.createElement("lv", c[1]);
                                if (protectCd > 0L) {
                                    doc.createElement("protectCd", protectCd);
                                }
                                if (lastTime > 0L) {
                                    doc.createElement("lastTime", lastTime);
                                    if (i_d >= 1 && i_d <= 6) {
                                        doc.createElement("stateValue", ((Stratagem)this.stratagemCache.get((Object)i_d)).getPar1());
                                    }
                                }
                                doc.endObject();
                            }
                        }
                    }
                }
            }
            doc.endArray();
            doc.endObject();
        }
    }
    
    private void getCityEventInfo(final JsonDocument doc, final City city, final PlayerDto playerDto) {
        final int cityId = city.getId();
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        if (cityAttribute == null) {
            return;
        }
        if (cityAttribute.countDown == -1L) {
            if (cityAttribute.leftCount <= 0) {
                return;
            }
        }
        else if (System.currentTimeMillis() > cityAttribute.countDown) {
            return;
        }
        final Integer myCount = cityAttribute.playerIdCountMap.get(playerDto.playerId);
        if (myCount != null && myCount >= cityAttribute.eachLimit) {
            return;
        }
        if (cityAttribute.viewForceId != 0 && cityAttribute.viewForceId != playerDto.forceId) {
            return;
        }
        if (cityAttribute.visiable == 0) {
            boolean hasPgmInThis = false;
            final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
            for (final PlayerGeneralMilitary pgm : pgmList) {
                if (pgm.getLocationId() == cityId) {
                    hasPgmInThis = true;
                }
            }
            if (!hasPgmInThis) {
                return;
            }
        }
        doc.startObject();
        doc.createElement("cityId", cityId);
        doc.startObject("cityEvent");
        String dropPic = "0";
        switch (cityAttribute.eventType) {
            case 2: {
                final WdSjEv wdSjEv = (WdSjEv)this.dataGetter.getWdSjEvCache().get((Object)cityAttribute.eventTargetId);
                dropPic = wdSjEv.getPic();
                break;
            }
            case 1: {
                final WdSjBo wdSjBo = (WdSjBo)this.dataGetter.getWdSjBoCache().get((Object)cityAttribute.eventTargetId);
                dropPic = wdSjBo.getPic();
                break;
            }
        }
        doc.createElement("dropType", dropPic);
        if (cityAttribute.countDown == -1L) {
            doc.createElement("eventType", 1);
            doc.createElement("eventCount", cityAttribute.leftCount);
        }
        else {
            doc.createElement("eventType", 2);
            doc.createElement("countDown", cityAttribute.countDown - System.currentTimeMillis());
        }
        doc.endObject();
        doc.endObject();
    }
    
    private Tuple<Integer, Integer> getMistBlood(final Integer id, final PlayerWorld pw, final int areaId) {
        final int maxHp = CityNpcBuilder.getMaxHp(this.dataGetter, areaId);
        final Tuple<Integer, Integer> result = new Tuple();
        if (pw == null) {
            return result;
        }
        if (pw.getNpcLostDetail() == null) {
            result.left = maxHp;
            result.right = maxHp;
            return result;
        }
        final String[] mistHpsString = pw.getNpcLostDetail().split(";");
        if (mistHpsString.length <= 0) {
            return result;
        }
        String[] array;
        for (int length = (array = mistHpsString).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!StringUtils.isBlank(s)) {
                final String[] single = s.split(",");
                if (single.length > 0) {
                    if (areaId == Integer.parseInt(single[0])) {
                        result.left = Integer.valueOf(single[1]);
                        result.right = Integer.valueOf(single[2]);
                        break;
                    }
                }
            }
        }
        return result;
    }
    
    private void fireJoinGroupEvent(final int intValue, final Request request, final int playerId) {
        GroupManager.getInstance().getGroup(String.valueOf(ChatType.WORLD.toString()) + intValue).join(request.getSession());
    }
    
    private void getBoxInfo(final int playerId, final JsonDocument doc) {
        final PlayerWorld playerWorld = this.playerWorldDao.read(playerId);
        final StringBuffer sb = new StringBuffer();
        final String[] boxInfo = SymbolUtil.StringtoArray(playerWorld.getBoxispicked(), "\\|");
        if (boxInfo == null) {
            return;
        }
        for (int i = 0; i < boxInfo.length; ++i) {
            if (Integer.parseInt(boxInfo[i]) > 0) {
                sb.append(1);
            }
            else {
                sb.append(0);
            }
        }
        doc.createElement("boxInfo", sb.toString());
    }
    
    private byte[] getMovePath(final int orgCityId, final int cityId, final String moveLine) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("path");
        doc.startObject();
        doc.createElement("cityId", orgCityId);
        doc.endObject();
        doc.startObject();
        doc.createElement("cityId", cityId);
        doc.endObject();
        if (!StringUtils.isEmpty(moveLine)) {
            final String[] cityIds = moveLine.split(",");
            String[] array;
            for (int length = (array = cityIds).length, i = 0; i < length; ++i) {
                final String id = array[i];
                doc.startObject();
                doc.createElement("cityId", id);
                doc.endObject();
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    private byte[] getMovePath(final int cityId, final String moveLine) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("path");
        doc.startObject();
        doc.createElement("cityId", cityId);
        doc.endObject();
        if (!StringUtils.isEmpty(moveLine)) {
            final String[] cityIds = moveLine.split(",");
            String[] array;
            for (int length = (array = cityIds).length, i = 0; i < length; ++i) {
                final String id = array[i];
                doc.startObject();
                doc.createElement("cityId", id);
                doc.endObject();
            }
        }
        doc.endArray();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public byte[] leaveWorldScene(final PlayerDto playerDto, final int generalId, final Request request) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int playerId = playerDto.playerId;
        CityService.worldGeneralCityMap.put(playerId, String.valueOf(generalId) + ":" + 0);
        doc.createElement("save", true);
        doc.endObject();
        final List<PlayerGeneralMilitary> generalMilitaries = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm : generalMilitaries) {
            GroupManager.getInstance().getGroup(String.valueOf(ChatType.WORLD.toString()) + pgm.getLocationId()).leave(request.getSession().getId());
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public void initCityBattleId() {
        final StringBuilder sb = new StringBuilder();
        for (final City city : this.cityDao.getModels()) {
            if (city == null) {
                continue;
            }
            if (WorldCityCommon.barbarainCitySet.contains(city.getId())) {
                sb.append(14).append("_").append(0).append("_").append(city.getId());
            }
            else {
                sb.append(3).append("_").append(0).append("_").append(city.getId());
            }
            CityService.cityBatIdSet.put(city.getId(), sb.toString());
            sb.delete(0, sb.length());
        }
    }
    
    @Transactional
    @Override
    public void initWorldCity() {
        final List<City> cityList = this.cityDao.getModels();
        final Map<Integer, City> cityMap = new HashMap<Integer, City>();
        for (final City city : cityList) {
            cityMap.put(city.getId(), city);
        }
        final List<CityNpcLost> CityNpcLostList = this.cityNpcLostDao.getModels();
        final Map<Integer, CityNpcLost> CityNpcLostMap = new HashMap<Integer, CityNpcLost>();
        for (final CityNpcLost cityNpcLost : CityNpcLostList) {
            CityNpcLostMap.put(cityNpcLost.getCityId(), cityNpcLost);
        }
        final List<CityDefenceNpc> cityDefenceNpcList = this.dataGetter.getCityDefenceNpcDao().getModels();
        final Map<Integer, CityDefenceNpc> cityDefenceNpcMap = new HashMap<Integer, CityDefenceNpc>();
        for (final CityDefenceNpc CityDefenceNpc : cityDefenceNpcList) {
            cityDefenceNpcMap.put(CityDefenceNpc.getCityId(), CityDefenceNpc);
        }
        boolean being = false;
        if (this.cityDao.getModelSize() > 0) {
            being = true;
        }
        final Document doc = DocumentHelper.createDocument();
        final Element cities = doc.addElement("cities");
        for (final WorldCity cityCach : this.worldCityCache.getModels()) {
            final Element city2 = cities.addElement("city");
            city2.addAttribute("id", cityCach.getId().toString());
            city2.addAttribute("name", cityCach.getName());
            city2.addAttribute("intro", cityCach.getIntro());
            city2.addAttribute("terrain", cityCach.getTerrain().toString());
            final General general = (General)this.generalCache.get((Object)cityCach.getChief());
            city2.addAttribute("chief", general.getName());
            city2.addAttribute("pic", cityCach.getPic());
            city2.addAttribute("cnp", cityCach.getOutput().toString());
            city2.addAttribute("weiDis", cityCach.getWeiDistance().toString());
            city2.addAttribute("shuDis", cityCach.getShuDistance().toString());
            city2.addAttribute("wuDis", cityCach.getWuDistance().toString());
        }
        try {
            final String path = ListenerConstants.WEB_PATH;
            final Writer fileWrite = new FileWriter(String.valueOf(path) + "CityInfo.xml");
            final OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("utf-8");
            final XMLWriter xmlWrite = new XMLWriter(fileWrite, format);
            xmlWrite.write(doc);
            xmlWrite.close();
        }
        catch (IOException e) {
            CityService.errorLog.error("CityService initWorldCity", e);
        }
        for (final WorldCity cityCach : this.worldCityCache.getModels()) {
            WorldCityCommon.barbarainCitySet.contains(cityCach.getId());
            City city3 = cityMap.get(cityCach.getId());
            if (city3 != null) {
                final Integer cityTitle = city3.getTitle();
                if (cityTitle != null && cityTitle != 0 && city3.getStateJobId() != null && city3.getStateJobId() != -1) {
                    this.jobService.cancelJob(city3.getStateJobId(), true);
                }
            }
            else {
                city3 = new City();
                city3.setId(cityCach.getId());
                city3.setState(0);
                city3.setGNum(0);
                city3.setTitle(0);
                if (cityCach.getId() == 254) {
                    city3.setForceId(1);
                }
                else if (cityCach.getId() == 253) {
                    city3.setForceId(2);
                }
                else if (cityCach.getId() == 206) {
                    city3.setForceId(3);
                }
                else {
                    city3.setForceId(0);
                }
                city3.setTrickinfo("");
                city3.setBorder(0);
                final Tuple<Integer, Integer> tuple = CityBuilder.getHpMaxHp(this.dataGetter, cityCach.getId());
                city3.setHp(tuple.left);
                city3.setHpMax(tuple.right);
                city3.setOtherInfo(null);
                this.cityDao.create(city3);
            }
            CityNpcLost cnl = CityNpcLostMap.get(cityCach.getId());
            if (cnl == null) {
                cnl = new CityNpcLost();
                cnl.setCityId(cityCach.getId());
                cnl.setNpcLost(null);
                this.cityNpcLostDao.create(cnl);
            }
            CityDefenceNpc cityDefenceNpc = cityDefenceNpcMap.get(cityCach.getId());
            if (cityDefenceNpc == null) {
                cityDefenceNpc = new CityDefenceNpc();
                cityDefenceNpc.setCityId(cityCach.getId());
                cityDefenceNpc.setPlayerLv(0);
                cityDefenceNpc.setGeneralId(0);
                cityDefenceNpc.setGeneralLv(0);
                cityDefenceNpc.setTroopId(0);
                cityDefenceNpc.setStrength(0);
                cityDefenceNpc.setLeader(0);
                cityDefenceNpc.setAtt(0);
                cityDefenceNpc.setDef(0);
                cityDefenceNpc.setHp(0);
                cityDefenceNpc.setColumnNum(0);
                cityDefenceNpc.setAttB(0);
                cityDefenceNpc.setDefB(0);
                cityDefenceNpc.setTacticAtt(0);
                cityDefenceNpc.setTacticDef(0);
                this.dataGetter.getCityDefenceNpcDao().create(cityDefenceNpc);
            }
        }
        if (!being) {
            for (final Integer cityId : WorldCityCommon.specialNationIdMap.keySet()) {
                this.changeForceIdAndState(cityId, WorldCityCommon.specialNationIdMap.get(cityId), 0, 0, "");
            }
        }
    }
    
    @Override
    public void initResetCityBattleAndCheckCityPGMState() {
        final List<City> cityList = this.cityDao.getModels();
        final Map<Integer, City> cityMap = new HashMap<Integer, City>();
        for (final City city : cityList) {
            cityMap.put(city.getId(), city);
        }
        final Set<Integer> inWarCitySet = this.dataGetter.getBattleInfoService().resetCityBattles(cityMap);
        WorldSceneLog.getInstance().appendLogMsg("initCheckCityPGMState start.").flush();
        for (final City city2 : cityMap.values()) {
            final int cityId = city2.getId();
            if (this.dataGetter.getWorldCityCache().get((Object)cityId) == null) {
                continue;
            }
            if (inWarCitySet.contains(cityId)) {
                continue;
            }
            final int cityState = city2.getState();
            final int cityForceId = city2.getForceId();
            WorldSceneLog.getInstance().newLine().appendCityId(cityId).appendCityName(((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityState", cityState).append("cityForceId", cityForceId).flush();
            if (WorldCityCommon.mainCityNationIdMap.get(cityId) != null) {
                final int capitalForceId = WorldCityCommon.mainCityNationIdMap.get(city2.getId());
                if (capitalForceId == cityForceId) {
                    continue;
                }
                WorldSceneLog.getInstance().Indent().appendLogMsg("catiptal of " + capitalForceId + " was changed to " + cityForceId + ", reset.").flush();
                this.dataGetter.getCityDao().updateForceIdState(cityId, capitalForceId, 0);
                final List<PlayerGeneralMilitary> listPGM = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryByLocationId(cityId);
                final List<PlayerGeneralMilitary> listPGMToBeArrange = new LinkedList<PlayerGeneralMilitary>();
                for (final PlayerGeneralMilitary pgm : listPGM) {
                    final int ownerId = pgm.getPlayerId();
                    final Player owner = this.dataGetter.getPlayerDao().read(ownerId);
                    if (owner == null) {
                        WorldSceneLog.getInstance().Indent().appendLogMsg("pgm has no owner.").append("v_id", pgm.getVId()).flush();
                    }
                    else {
                        if (owner.getForceId() == capitalForceId) {
                            continue;
                        }
                        listPGMToBeArrange.add(pgm);
                    }
                }
                if (listPGMToBeArrange.size() <= 0) {
                    continue;
                }
                for (final PlayerGeneralMilitary pgm : listPGMToBeArrange) {
                    final int ownerId = pgm.getPlayerId();
                    final Player owner = this.dataGetter.getPlayerDao().read(ownerId);
                    WorldSceneLog.getInstance().Indent().appendLogMsg("this pgm is in other country's catiptal city:" + cityId).append("locationId", cityId).append("location", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("locationForceId", city2.getForceId()).append("ownerForceId", owner.getForceId()).append("pgmVId", pgm.getVId()).flush();
                    arrangePGM(this.dataGetter, owner, pgm);
                }
            }
            else {
                if (cityState != 0) {
                    WorldSceneLog.getInstance().Indent().appendLogMsg(" state error:actually it is not in war, reset!").flush();
                    this.dataGetter.getCityDao().updateState(cityId, 0);
                }
                final List<PlayerGeneralMilitary> listPGM2 = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryByLocationId(cityId);
                if (listPGM2 == null || listPGM2.size() <= 0) {
                    continue;
                }
                for (final PlayerGeneralMilitary pgm2 : listPGM2) {
                    final Player player = this.dataGetter.getPlayerDao().read(pgm2.getPlayerId());
                    if (player == null) {
                        WorldSceneLog.getInstance().Indent().error("pgm:" + pgm2.getVId() + " has no owner, need to be delete!");
                        this.dataGetter.getPlayerGeneralMilitaryDao().deleteById(pgm2.getVId());
                    }
                    else {
                        final int playerForceId = player.getForceId();
                        if (playerForceId == cityForceId) {
                            continue;
                        }
                        final int ownerId = pgm2.getPlayerId();
                        final Player owner = this.dataGetter.getPlayerDao().read(ownerId);
                        WorldSceneLog.getInstance().Indent().appendLogMsg("this pgm is in other country's city.").append("locationId", cityId).append("location", ((WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("locationForceId", city2.getForceId()).append("ownerForceId", owner.getForceId()).append("pgmVId", pgm2.getVId()).flush();
                        arrangePGM(this.dataGetter, owner, pgm2);
                    }
                }
            }
        }
        WorldSceneLog.getInstance().appendLogMsg("initCheckCityPGMState end.").newLine().flush();
    }
    
    public static void arrangePGM(final IDataGetter dataGetter, final Player owner, final PlayerGeneralMilitary pgm) {
        final Set<Integer> neibors = dataGetter.getWorldRoadCache().getNeighbors(pgm.getLocationId());
        final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
        boolean neiborDone = false;
        for (final int neiId : neibors) {
            final City neiCity = dataGetter.getCityDao().read(neiId);
            if (neiCity.getForceId() == owner.getForceId() && neiCity.getState() == 0) {
                neiborDone = true;
                dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm.getPlayerId(), pgm.getGeneralId(), neiId);
                dataGetter.getCityService().updateGNumAndSend(pgm.getLocationId(), neiId);
                WorldSceneLog.getInstance().Indent().Indent().appendPlayerId(owner.getPlayerId()).appendPlayerName(owner.getPlayerName()).appendGeneralName(general.getName()).append("pgmVId", pgm.getVId()).append("from cityId", pgm.getLocationId()).append("from city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)pgm.getLocationId())).getName()).append("to cityId", neiId).append("to city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)neiId)).getName()).flush();
            }
        }
        if (!neiborDone) {
            final int capitalId = WorldCityCommon.nationMainCityIdMap.get(owner.getForceId());
            dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(pgm.getPlayerId(), pgm.getGeneralId(), capitalId);
            WorldSceneLog.getInstance().Indent().Indent().appendPlayerId(owner.getPlayerId()).appendPlayerName(owner.getPlayerName()).appendGeneralName(general.getName()).append("pgmVId", pgm.getVId()).append("from cityId", pgm.getLocationId()).append("from city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)pgm.getLocationId())).getName()).append("to cityId", capitalId).append("to city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)capitalId)).getName()).flush();
        }
    }
    
    @Override
    public void initWorldRoadXml() {
        final Document doc = DocumentHelper.createDocument();
        final Element roads = doc.addElement("roads");
        try {
            final BoxLocation boxLocation = new BoxLocation();
            final List<RoadDto> set = boxLocation.getRoadMap(this.worldRoadCache);
            for (final RoadDto e : set) {
                final Element road = roads.addElement("road");
                final int id = e.getId();
                road.addAttribute("key", e.getKey());
                road.addAttribute("x", String.valueOf(e.getX()));
                road.addAttribute("y", String.valueOf(e.getY()));
                final int weiI = e.getWei();
                final int shuI = e.getShu();
                final int wuI = e.getWu();
                CityService.wei[id] = weiI;
                CityService.wu[id] = wuI;
                CityService.shu[id] = shuI;
                road.addAttribute("id", String.valueOf(id));
                road.addAttribute("wei", String.valueOf(weiI));
                road.addAttribute("wu", String.valueOf(wuI));
                road.addAttribute("shu", String.valueOf(shuI));
            }
            final String pathString = ListenerConstants.WEB_PATH;
            final Writer fileWriter = new FileWriter(String.valueOf(pathString) + "WorldRoad.xml");
            final OutputFormat format = OutputFormat.createPrettyPrint();
            final XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
            xmlWriter.write(doc);
            xmlWriter.close();
            format.setEncoding("utf-8");
        }
        catch (IOException e2) {
            e2.printStackTrace();
            CityService.errorLog.error("CityService initWorldRoadXml", e2);
        }
    }
    
    @Override
    public void sendAttMoveInfo(final int playerId, final int generalId, final int cityId, final int nextCityId, final int forceId, final String moveLine, final long forcesShow, final boolean atOnce) {
        String quizInfoString = "";
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("generalId", generalId);
        doc.createElement("orgCityId", cityId);
        doc.createElement("curCityId", nextCityId);
        doc.createElement("atOnce", atOnce);
        doc.createElement("orgCity", ((WorldCity)this.worldCityCache.get((Object)cityId)).getName());
        doc.createElement("curCity", ((WorldCity)this.worldCityCache.get((Object)nextCityId)).getName());
        doc.createElement("id", nextCityId);
        doc.startArray("around");
        final Set<Integer> nextNeibs = this.worldRoadCache.getNeighbors(nextCityId);
        if (cityId > 0) {
            final Set<Integer> curNeibs = this.worldRoadCache.getNeighbors(cityId);
            for (final Integer id : curNeibs) {
                if (nextNeibs.contains(id)) {
                    continue;
                }
                doc.startObject();
                doc.createElement("id", id);
                doc.createElement("attMove", 0);
                doc.createElement("distanceState", 0);
                doc.endObject();
            }
        }
        quizInfoString = this.getAroundCityInfo(doc, nextNeibs, forceId, cityId, playerId);
        doc.endArray();
        doc.createElement("quizInfo", quizInfoString);
        if (cityId != nextCityId) {
            final String generalName = this.getColoredGeneralName(generalId);
            this.cityDataCache.fireCityMoveMessage(playerId, cityId, nextCityId, generalName);
            doc.appendJson(this.getMovePath(cityId, nextCityId, moveLine));
        }
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            CityService.errorLog.error("CityService sendAttMoveInfo playerId:" + playerId + " generalId:" + generalId + " cityId:" + cityId + " nextCityId:" + nextCityId);
            return;
        }
        final GeneralMoveDto gmd = getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
        if (gmd != null && gmd.nextMoveTime > 0L) {
            doc.createElement("cd", CDUtil.getCD(gmd.nextMoveTime, new Date()));
        }
        else {
            doc.createElement("cd", 4000);
        }
        if (!StringUtils.isEmpty(moveLine)) {
            doc.createElement("autoMove", true);
        }
        final WorldRoad road = this.worldRoadCache.getRoad(cityId, nextCityId);
        this.decideBoxInfo(road, playerId, doc, generalId);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_ATTMOV, doc.toByte());
    }
    
    @Override
    public String getColoredGeneralName(final int generalId) {
        final General general = (General)this.generalCache.get((Object)generalId);
        final String generalName = ColorUtil.getColorMsg(general.getQuality(), general.getName());
        return generalName;
    }
    
    @Transactional
    @Override
    public void updateGNumAndSend(final int cityId, final boolean add) {
        if (add) {
            this.cityDataCache.ressetGeneralNum(0, cityId);
        }
        else {
            this.cityDataCache.ressetGeneralNum(cityId, 0);
        }
    }
    
    @Transactional
    @Override
    public void updateGNumAndSend(final int cityId, final int nextCityId) {
        this.cityDataCache.ressetGeneralNum(cityId, nextCityId);
        final int gNumState = this.cityDataCache.getGeneralNum(cityId);
        final int gNumState2 = this.cityDataCache.getGeneralNum(nextCityId);
        if (gNumState != 1 && gNumState != 2 && gNumState != 10 && gNumState != 11 && gNumState2 != 1 && gNumState2 != 2 && gNumState2 != 10 && gNumState2 != 11) {
            return;
        }
    }
    
    @Override
    public byte[] attMoveInfo(final PlayerDto playerDto, final int generalId) {
        final int playerId = playerDto.playerId;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson(this.getAttMoveInfo(pgm.getLocationId(), playerDto.forceId, playerDto.playerId));
        final GeneralMoveDto gmd = getGeneralMoveDto(playerId, generalId);
        if (gmd != null && !StringUtils.isEmpty(gmd.moveLine)) {
            doc.appendJson(this.getMovePath(pgm.getLocationId(), gmd.moveLine));
        }
        doc.endObject();
        TaskMessageHelper.sendChooseGeneralTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getAttMoveInfo(final int nextCityId, final int forceId, final int playerId) {
        final Set<Integer> nextNeibs = this.worldRoadCache.getNeighbors(nextCityId);
        final JsonDocument doc = new JsonDocument();
        doc.createElement("id", nextCityId);
        doc.startArray("around");
        final String quizInfoString = this.getAroundCityInfo(doc, nextNeibs, forceId, nextCityId, playerId);
        doc.endArray();
        doc.createElement("quizInfo", quizInfoString);
        return doc.toByte();
    }
    
    private String getAroundCityInfo(final JsonDocument doc, final Set<Integer> nextNeibs, final int forceId, final int nextCityId, final int playerId) {
        final Set<Integer> set = new HashSet<Integer>();
        final Set<Integer> attedSet = new HashSet<Integer>();
        PlayerWorld pw = this.playerWorldDao.read(playerId);
        if (pw == null) {
            this.worldService.createRecord(playerId);
            pw = this.playerWorldDao.read(playerId);
        }
        final int quizInfo = (pw.getQuizinfo() == null) ? 0 : pw.getQuizinfo();
        if ((quizInfo & 0x2) != 0x2) {
            set.add(1);
        }
        if (quizInfo < 30 && pw.getAttedId() != null) {
            final String[] ids = pw.getAttedId().split(",");
            int key = 0;
            String[] array2;
            for (int length = (array2 = ids).length, k = 0; k < length; ++k) {
                final String str = array2[k];
                key = Integer.valueOf(str);
                attedSet.add(key);
            }
        }
        final PlayerTask task = this.dataGetter.getPlayerTaskDao().getCurMainTask(playerId);
        final int taskiD = (task == null) ? 0 : task.getTaskId();
        boolean flag = false;
        if (taskiD >= 92) {
            flag = true;
        }
        final int[] array = { 1, 3, 7, 15 };
        final StringBuffer result = new StringBuffer();
        final int x = quizInfo + 1;
        if (flag && x < 30) {
            int index = -1;
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == x) {
                    index = i;
                }
            }
            if (index != -1) {
                for (int j = index; j < array.length; ++j) {
                    result.append(array[j]).append("|");
                }
            }
        }
        SymbolUtil.removeTheLast(result);
        return result.toString();
    }
    
    private int getSurroundState(final City city) {
        final Set<Integer> set = this.worldRoadCache.getNeighbors(city.getId());
        City ci = null;
        int sameForceNum = 0;
        City sameForceCity = null;
        for (final Integer cId : set) {
            ci = this.cityDao.read(cId);
            if (city.getForceId() == ci.getForceId()) {
                ++sameForceNum;
                sameForceCity = ci;
            }
        }
        if (sameForceNum == 0) {
            return 1;
        }
        if (sameForceNum == 1) {
            City subCi = null;
            final Set<Integer> subSet = this.worldRoadCache.getNeighbors(sameForceCity.getId());
            for (final Integer subCId : subSet) {
                if (subCId == city.getId()) {
                    continue;
                }
                subCi = this.cityDao.read(subCId);
                if (city.getForceId() == subCi.getForceId()) {
                    return 0;
                }
            }
            return 2;
        }
        return 0;
    }
    
    @Override
    public void changeForceIdAndState(final int cityId, final int forceId, final int state, final int playerId, final String playerName) {
        int orgForceId = 0;
        Label_3297: {
            try {
                this.rwLock.writeLock().lock();
                City city = this.cityDao.read(cityId);
                if (city.getForceId() == forceId) {
                    return;
                }
                if (city.getForceId() == 0) {
                    this.dataGetter.getCityDao().updateHp(cityId, 0);
                }
                orgForceId = city.getForceId();
                this.cityDao.updateForceIdState(cityId, forceId, state);
                this.dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(playerId, forceId), 1, "zhancheng");
                int attType = 0;
                if (WorldCityCommon.nationMainCityIdMap.containsKey(forceId)) {
                    this.rankService.nationTaskIsOver(forceId, cityId);
                    this.rankService.nationTaskHJCityOccupy(forceId, cityId);
                    if (this.worldCityCache.getDistanceCities(forceId).containsKey(cityId)) {
                        attType = 1;
                    }
                    else if (this.worldCityCache.getDistanceCities().containsKey(cityId)) {
                        attType = 2;
                    }
                }
                city = this.cityDao.read(cityId);
                final Map<Integer, Integer> changeMap = new HashMap<Integer, Integer>();
                int orgTitle = city.getTitle();
                final int surroundTitle = this.getSurroundState(city);
                if (orgTitle != surroundTitle && !this.isHJKeyCity(cityId) && !this.isHuizhanCity(cityId)) {
                    this.cityDao.updateTitle(city.getId(), surroundTitle);
                    changeMap.put(city.getId(), surroundTitle);
                }
                final Map<Integer, Integer> weiState = new HashMap<Integer, Integer>();
                final Map<Integer, Integer> shuState = new HashMap<Integer, Integer>();
                final Map<Integer, Integer> wuState = new HashMap<Integer, Integer>();
                final Map<Integer, Map<Integer, Integer>> stateMap = new HashMap<Integer, Map<Integer, Integer>>();
                stateMap.put(1, weiState);
                stateMap.put(2, shuState);
                stateMap.put(3, wuState);
                final Map<Integer, Integer> weiBorder = new HashMap<Integer, Integer>();
                final Map<Integer, Integer> shuBorder = new HashMap<Integer, Integer>();
                final Map<Integer, Integer> wuBorder = new HashMap<Integer, Integer>();
                final Map<Integer, Map<Integer, Integer>> borderChangeMap = new HashMap<Integer, Map<Integer, Integer>>();
                borderChangeMap.put(1, weiBorder);
                borderChangeMap.put(2, shuBorder);
                borderChangeMap.put(3, wuBorder);
                boolean roundOrgCity = false;
                boolean roundOtherCity = false;
                boolean isBorder = false;
                final boolean hasTask = RankService.getTimeDevision() > 0;
                City ci = null;
                final Set<Integer> set = this.worldRoadCache.getNeighbors(cityId);
                final Set<Integer> setBorder = new HashSet<Integer>();
                for (final Integer cId : set) {
                    ci = this.cityDao.read(cId);
                    if (ci.getForceId() != forceId) {
                        isBorder = true;
                        setBorder.add(ci.getForceId());
                    }
                    if (ci.getForceId() == orgForceId) {
                        roundOrgCity = true;
                    }
                    else if (ci.getForceId() != forceId && ci.getForceId() != orgForceId) {
                        roundOtherCity = true;
                    }
                    orgTitle = ci.getTitle();
                    final int subSurroundTitle = this.getSurroundState(ci);
                    if (orgTitle != subSurroundTitle) {
                        if (!this.isHJKeyCity(cId) && !this.isHuizhanCity(cId)) {
                            if (hasTask && subSurroundTitle > orgTitle && forceId != ci.getForceId() && playerId > 0 && !WorldCityCommon.specialNationIdMap.containsKey(ci.getId())) {
                                final String msg = MessageFormatter.format(LocalMessages.CITY_SURROUND_INFO, new Object[] { ColorUtil.getForceMsg(forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(forceId)) + playerName) });
                                this.chatService.sendBigNotice("GLOBAL", new PlayerDto(playerId, forceId), msg, null);
                            }
                            this.cityDao.updateTitle(ci.getId(), subSurroundTitle);
                            changeMap.put(ci.getId(), subSurroundTitle);
                        }
                        final Set<Integer> subSet = this.worldRoadCache.getNeighbors(ci.getId());
                        City subCi = null;
                        int subOrgTitle = 0;
                        for (final Integer subCId : subSet) {
                            if (subCId == city.getId()) {
                                continue;
                            }
                            subCi = this.cityDao.read(subCId);
                            if (subCi.getForceId() != ci.getForceId()) {
                                continue;
                            }
                            subOrgTitle = subCi.getTitle();
                            final int thirdSurroundState = this.getSurroundState(ci);
                            if (subOrgTitle == thirdSurroundState || this.isHJKeyCity(subCId) || this.isHuizhanCity(cId)) {
                                continue;
                            }
                            this.cityDao.updateTitle(subCi.getId(), thirdSurroundState);
                            changeMap.put(subCi.getId(), thirdSurroundState);
                            if (!hasTask || thirdSurroundState <= subOrgTitle || forceId == subCi.getForceId() || playerId <= 0 || WorldCityCommon.specialNationIdMap.containsKey(subCi.getId())) {
                                continue;
                            }
                            final String msg2 = MessageFormatter.format(LocalMessages.CITY_SURROUND_INFO, new Object[] { ColorUtil.getForceMsg(forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(forceId)) + playerName) });
                            this.chatService.sendBigNotice("GLOBAL", new PlayerDto(playerId, forceId), msg2, null);
                        }
                    }
                    if (ci.getForceId() == orgForceId || ci.getForceId() == forceId) {
                        if (orgForceId == ci.getForceId()) {
                            if (ci.getBorder() == 0) {
                                this.cityDao.updateBorder(cId, 1);
                                if (WorldCityCommon.nationMainCityIdMap.containsKey(forceId)) {
                                    if (!WorldCityCommon.nationMainCityIdMap.containsValue(ci.getId())) {
                                        borderChangeMap.get(forceId).put(ci.getId(), 1);
                                    }
                                    if (!WorldCityCommon.nationMainCityIdMap.containsValue(cityId)) {
                                        borderChangeMap.get(forceId).put(cityId, 0);
                                    }
                                }
                            }
                            else if (WorldCityCommon.nationMainCityIdMap.containsKey(forceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(ci.getId())) {
                                borderChangeMap.get(forceId).put(ci.getId(), 1);
                            }
                            if (WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(cityId)) {
                                borderChangeMap.get(orgForceId).put(cityId, 1);
                            }
                            if (!WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) || attType != 2 || !WorldCityCommon.nationMainCityIdMap.containsKey(ci.getForceId()) || !WorldCityCommon.nationMainCityIdMap.containsKey(forceId) || !this.worldCityCache.getDistanceCities(ci.getForceId()).containsKey(ci.getId()) || WorldCityCommon.nationMainCityIdMap.containsValue(ci.getId())) {
                                continue;
                            }
                            stateMap.get(forceId).put(ci.getId(), WorldCityCommon.getDistanceState(this.worldCityCache.getDistanceCities(ci.getForceId()).get(ci.getId())));
                        }
                        else {
                            if (forceId != ci.getForceId()) {
                                continue;
                            }
                            if (ci.getBorder() == 1) {
                                final Set<Integer> subSet = this.worldRoadCache.getNeighbors(cId);
                                City subCi = null;
                                boolean isSubBorder = false;
                                boolean changeOrgForceBorder = false;
                                for (final Integer subCId2 : subSet) {
                                    if (subCId2 == cityId) {
                                        continue;
                                    }
                                    subCi = this.cityDao.read(subCId2);
                                    if (subCi.getForceId() != ci.getForceId()) {
                                        isSubBorder = true;
                                    }
                                    if (subCi.getForceId() != orgForceId) {
                                        continue;
                                    }
                                    changeOrgForceBorder = true;
                                }
                                if (!isSubBorder) {
                                    this.cityDao.updateBorder(cId, 0);
                                    if (WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(cId)) {
                                        borderChangeMap.get(orgForceId).put(cId, 0);
                                    }
                                }
                                if (!changeOrgForceBorder && WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(cId)) {
                                    borderChangeMap.get(orgForceId).put(cId, 0);
                                }
                                if (WorldCityCommon.nationMainCityIdMap.containsKey(forceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(cityId)) {
                                    borderChangeMap.get(forceId).put(cityId, 0);
                                }
                            }
                            if (!WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) || attType != 1 || !this.worldCityCache.getDistanceCities(forceId).containsKey(ci.getId())) {
                                continue;
                            }
                            final Set<Integer> subSet = this.worldRoadCache.getNeighbors(cId);
                            City subCi = null;
                            boolean hasSecondCity = false;
                            for (final Integer subCId : subSet) {
                                if (subCId == cityId) {
                                    continue;
                                }
                                subCi = this.cityDao.read(subCId);
                                if (subCi.getForceId() == orgForceId) {
                                    hasSecondCity = true;
                                    break;
                                }
                            }
                            if (hasSecondCity) {
                                continue;
                            }
                            stateMap.get(orgForceId).put(ci.getId(), 0);
                        }
                    }
                    else if (ci.getBorder() == 0) {
                        this.cityDao.updateBorder(ci.getId(), 1);
                        final Set<Integer> subSet = this.worldRoadCache.getNeighbors(cId);
                        City subCi = null;
                        boolean hasSecondCity = false;
                        for (final Integer subCId : subSet) {
                            if (subCId == cityId) {
                                continue;
                            }
                            subCi = this.cityDao.read(subCId);
                            if (subCi.getForceId() == orgForceId) {
                                hasSecondCity = true;
                                break;
                            }
                        }
                        if (!hasSecondCity && WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(ci.getId())) {
                            borderChangeMap.get(orgForceId).put(ci.getId(), 0);
                        }
                        if (!WorldCityCommon.nationMainCityIdMap.containsKey(forceId) || WorldCityCommon.nationMainCityIdMap.containsValue(ci.getId())) {
                            continue;
                        }
                        borderChangeMap.get(forceId).put(ci.getId(), 1);
                    }
                    else {
                        if (WorldCityCommon.nationMainCityIdMap.containsKey(forceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(ci.getId())) {
                            borderChangeMap.get(forceId).put(ci.getId(), 1);
                        }
                        final Set<Integer> subSet = this.worldRoadCache.getNeighbors(cId);
                        City subCi = null;
                        boolean hasSecondCity = false;
                        for (final Integer subCId : subSet) {
                            if (subCId == cityId) {
                                continue;
                            }
                            subCi = this.cityDao.read(subCId);
                            if (subCi.getForceId() == orgForceId) {
                                hasSecondCity = true;
                                break;
                            }
                        }
                        if (hasSecondCity || !WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) || WorldCityCommon.nationMainCityIdMap.containsValue(ci.getId())) {
                            continue;
                        }
                        borderChangeMap.get(orgForceId).put(ci.getId(), 0);
                    }
                }
                if (!isBorder) {
                    this.cityDao.updateBorder(city.getId(), 0);
                }
                else {
                    if (city.getBorder() == 0) {
                        this.cityDao.updateBorder(city.getId(), 1);
                    }
                    for (final Integer kk : setBorder) {
                        if (WorldCityCommon.nationMainCityIdMap.containsKey(kk) && !WorldCityCommon.nationMainCityIdMap.containsValue(city.getId())) {
                            borderChangeMap.get(kk).put(city.getId(), 1);
                        }
                    }
                }
                if (attType == 1) {
                    if (this.worldCityCache.getDistanceCities(forceId).containsKey(cityId)) {
                        if (roundOrgCity && WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(cityId)) {
                            stateMap.get(orgForceId).put(cityId, WorldCityCommon.getDistanceState(this.worldCityCache.getDistanceCities(forceId).get(cityId)));
                        }
                        if (roundOtherCity && WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) && WorldCityCommon.nationMainCityIdMap.containsKey(forceId) && !WorldCityCommon.nationMainCityIdMap.containsValue(cityId)) {
                            stateMap.get(6 - forceId - orgForceId).put(cityId, WorldCityCommon.getDistanceState(this.worldCityCache.getDistanceCities(forceId).get(cityId)));
                        }
                    }
                }
                else if (attType == 2 && WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId) && WorldCityCommon.nationMainCityIdMap.containsKey(forceId) && this.worldCityCache.getDistanceCities(orgForceId).containsKey(cityId) && roundOtherCity && !WorldCityCommon.nationMainCityIdMap.containsValue(cityId)) {
                    stateMap.get(6 - forceId - orgForceId).put(cityId, 0);
                }
                this.cityDao.updateTrickInfo(cityId, "");
                this.cityDataCache.fireCityNumChangeEvent(orgForceId, forceId);
                this.sendCityUpdateInfo(city.getId(), changeMap, stateMap, borderChangeMap, true);
                boolean hasExpandTask = false;
                final int hasNationTask = this.rankService.hasNationTasks(1);
                if (hasNationTask == 6) {
                    hasExpandTask = true;
                }
                this.sendCNPInfo(city.getId(), forceId, orgForceId, hasExpandTask);
                this.cityEffectCache.refreshCityEffect(forceId, orgForceId, cityId);
            }
            catch (Exception e) {
                CityService.errorLog.error("CityService, modify city force and state exception.", e);
                break Label_3297;
            }
            finally {
                this.rwLock.writeLock().unlock();
            }
            this.rwLock.writeLock().unlock();
        }
        CityEventManager.getInstance().removePlayerEventAfterConquerCityCheck(cityId);
        final String manWangLingParam = String.valueOf(cityId) + "#" + orgForceId;
        this.dataGetter.getJobService().addJob("battleService", "removeManWangLingTryAfterCityConquered", manWangLingParam, System.currentTimeMillis(), false);
    }
    
    private boolean isHJKeyCity(final int cId) {
        return 105 == cId && RankService.taskInfo != null && RankService.taskInfo.getState() != 2;
    }
    
    private boolean isHuizhanCity(final int cId) {
        final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
        return hh != null && cId == hh.getCityId();
    }
    
    private void doStateJob(final int cityId, final int title) {
        final City city = this.cityDao.read(cityId);
        if (city == null || title == 0) {
            return;
        }
        final String stringTime = (title == 1) ? "World.Besiege1.DeserterTime" : "World.Besiege2.DeserterTime";
        final String stringForces = (title == 1) ? "World.Besiege1.DeserterProportion" : "World.Besiege2.DeserterProportion";
        final double forcesCost = ((C)this.dataGetter.getcCache().get((Object)stringForces)).getValue();
        final long executionTime = (long)(((C)this.dataGetter.getcCache().get((Object)stringTime)).getValue() * 60000.0f);
        final String parasString = SymbolUtil.toString(new String[] { String.valueOf(cityId), String.valueOf(title), String.valueOf(executionTime), String.valueOf(forcesCost) }, ";");
        if (city.getStateJobId() != null && city.getStateJobId() != -1) {
            this.jobService.cancelJob(city.getStateJobId(), true);
        }
        final int jobId = this.jobService.addJob("cityService", "stateJob", parasString, System.currentTimeMillis(), true);
        this.cityDao.updateJobId(cityId, jobId);
    }
    
    private void sendCNPInfo(final int cityId, final int forceId, final int orgForceId, final boolean hasExpandTask) {
        if (WorldCityCommon.nationMainCityIdMap.containsKey(forceId)) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("CNP", this.cityDataCache.getCNPNum(forceId));
            doc.createElement("cityNum", this.cityDataCache.getCityNum(forceId));
            doc.createElement("addCNP", this.worldService.getLeagueAddNPC(forceId));
            doc.endObject();
            Group g = null;
            if (hasExpandTask) {
                final int groupId = forceId - 1 + ChatType.WORLD_OPENED_1.ordinal();
                final String groupString = ChatType.values()[groupId].toString();
                g = GroupManager.getInstance().getGroup(groupString);
            }
            else {
                g = GroupManager.getInstance().getGroup("WORLD_" + forceId);
            }
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_WORLD_CNP.getModule(), doc.toByte()));
            if (g != null) {
                g.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_WORLD_CNP.getCommand(), 0, bytes));
            }
        }
        if (WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId)) {
            final JsonDocument docOrg = new JsonDocument();
            docOrg.startObject();
            docOrg.createElement("CNP", this.cityDataCache.getCNPNum(orgForceId));
            docOrg.createElement("cityNum", this.cityDataCache.getCityNum(orgForceId));
            docOrg.createElement("addCNP", this.worldService.getLeagueAddNPC(orgForceId));
            docOrg.endObject();
            Group gOrg = null;
            if (hasExpandTask) {
                final int groupId = orgForceId - 1 + ChatType.WORLD_OPENED_1.ordinal();
                final String groupString = ChatType.values()[groupId].toString();
                gOrg = GroupManager.getInstance().getGroup(groupString);
            }
            else {
                gOrg = GroupManager.getInstance().getGroup("WORLD_" + orgForceId);
            }
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_WORLD_CNP.getModule(), docOrg.toByte()));
            if (gOrg != null) {
                gOrg.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_WORLD_CNP.getCommand(), 0, bytes));
            }
        }
    }
    
    @Override
    public void changeState(final int cityId, final int state, final boolean attWin) {
        this.cityDao.updateState(cityId, state);
        this.sendCityUpdateInfo(cityId, null, null, null, false);
    }
    
    @Override
    public void sendCityUpdateInfo(final int id, final Map<Integer, Integer> changeMap, final Map<Integer, Map<Integer, Integer>> stateMaps, final Map<Integer, Map<Integer, Integer>> borderChangeMaps, final boolean flag) {
        final City city = this.cityDao.read(id);
        if (stateMaps != null || borderChangeMaps != null) {
            for (int i = 1; i <= 3; ++i) {
                Map<Integer, Integer> map = null;
                if (stateMaps != null) {
                    map = stateMaps.get(i);
                }
                Map<Integer, Integer> bmaps = null;
                if (borderChangeMaps != null) {
                    bmaps = borderChangeMaps.get(i);
                }
                final Group g = GroupManager.getInstance().getGroup("WORLD_" + i);
                if (g != null) {
                    final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITIES.getModule(), this.getSendCityUpdateInfo(city, map, changeMap, bmaps, flag)));
                    g.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_CITIES.getCommand(), 0, bytes));
                }
            }
        }
        else {
            final Group g2 = GroupManager.getInstance().getGroup(ChatType.WORLD.toString());
            if (g2 != null) {
                final byte[] bytes2 = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_CITIES.getModule(), this.getSendCityUpdateInfo(city, null, changeMap, null, flag)));
                g2.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_CITIES.getCommand(), 0, bytes2));
            }
        }
    }
    
    private byte[] getSendCityUpdateInfo(final City city, final Map<Integer, Integer> map, final Map<Integer, Integer> changeMap, final Map<Integer, Integer> borderChangeMap, final boolean flag) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("cities");
        doc.startObject();
        doc.createElement("id", city.getId());
        doc.createElement("belong", city.getForceId());
        doc.createElement("state", city.getState());
        if (WorldCityCommon.specialNationIdMap.containsKey(city.getId())) {
            doc.createElement("nameId", 0);
        }
        else {
            doc.createElement("nameId", city.getTitle());
        }
        if (flag) {
            doc.createElement("hp", 0);
            doc.createElement("maxHp", 0);
        }
        else if (city.getHp() != null && city.getHp() > 0) {
            doc.createElement("hp", (city.getHp() == null) ? 0 : city.getHp());
            doc.createElement("maxHp", (city.getHpMax() == null) ? 0 : city.getHpMax());
        }
        if (map != null && map.get(city.getId()) != null) {
            doc.createElement("border", map.get(city.getId()));
        }
        if (borderChangeMap != null && borderChangeMap.get(city.getId()) != null) {
            doc.createElement("attCity", borderChangeMap.get(city.getId()));
        }
        doc.endObject();
        if (changeMap != null) {
            for (final Integer key : changeMap.keySet()) {
                if (WorldCityCommon.specialNationIdMap.containsKey(key)) {
                    continue;
                }
                doc.startObject();
                doc.createElement("id", key);
                doc.createElement("nameId", changeMap.get(key));
                if (map != null && map.get(key) != null) {
                    doc.createElement("border", map.get(key));
                    map.remove(key);
                }
                if (borderChangeMap != null && borderChangeMap.get(key) != null) {
                    doc.createElement("attCity", borderChangeMap.get(key));
                    borderChangeMap.remove(key);
                }
                doc.endObject();
            }
        }
        if (map != null) {
            for (final Integer key : map.keySet()) {
                doc.startObject();
                doc.createElement("id", key);
                doc.createElement("border", map.get(key));
                if (borderChangeMap != null && borderChangeMap.get(key) != null) {
                    doc.createElement("attCity", borderChangeMap.get(key));
                    borderChangeMap.remove(key);
                }
                doc.endObject();
            }
        }
        if (borderChangeMap != null) {
            for (final Integer key : borderChangeMap.keySet()) {
                doc.startObject();
                doc.createElement("id", key);
                doc.createElement("attCity", borderChangeMap.get(key));
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
    
    @Transactional
    @Override
    public byte[] moveByCityName(final int playerId, final String cityNameOrId) {
        WorldCity wc = this.worldCityCache.getCityIdByName(cityNameOrId);
        if (wc == null) {
            int cityId = 0;
            try {
                cityId = Integer.valueOf(cityNameOrId);
            }
            catch (Exception e) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_NO_CITY);
            }
            wc = (WorldCity)this.worldCityCache.get((Object)cityId);
            if (wc == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_NO_CITY);
            }
        }
        final City city = this.cityDao.read(wc.getId());
        if (city == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_NO_CITY);
        }
        final Player player = this.playerDao.read(playerId);
        int moveNum = 0;
        if (city.getForceId() == player.getForceId()) {
            if (city.getState() == 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_STOP_IN_BATTLE);
            }
            final List<PlayerGeneralMilitary> pList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
            for (final PlayerGeneralMilitary pgm : pList) {
                if (pgm.getState() == 0) {
                    ++moveNum;
                    this.playerGeneralMilitaryDao.updateLocationId(playerId, pgm.getGeneralId(), city.getId());
                    this.dataGetter.getCityService().updateGNumAndSend(pgm.getLocationId(), city.getId());
                    final GeneralMoveDto gmd = getGeneralMoveDto(playerId, pgm.getGeneralId());
                    this.sendAttMoveInfo(pgm.getPlayerId(), pgm.getGeneralId(), pgm.getLocationId(), city.getId(), player.getForceId(), (gmd != null) ? gmd.moveLine : "", pgm.getForces(), true);
                }
            }
            if (moveNum <= 0) {
                return JsonBuilder.getJson(State.SUCCESS, LocalMessages.MOVE_GENERAL_BUSY);
            }
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        else {
            if (WorldCityCommon.mainCityNationIdMap.containsKey(city.getId()) && player.getForceId() != city.getForceId()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_STOP_OTHER_MAINCITY);
            }
            final List<PlayerGeneralMilitary> oList = this.playerGeneralMilitaryDao.getMilitaryByLocationIdOrderByPlayerIdLvDesc(city.getId());
            Player p = null;
            for (final PlayerGeneralMilitary pgm2 : oList) {
                p = this.playerDao.read(pgm2.getPlayerId());
                if (p.getForceId() != player.getForceId()) {
                    this.playerGeneralMilitaryDao.updateLocationId(pgm2.getPlayerId(), pgm2.getGeneralId(), WorldCityCommon.nationMainCityIdMap.get(p.getForceId()));
                }
            }
            moveNum = 0;
            final List<PlayerGeneralMilitary> pList2 = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
            for (final PlayerGeneralMilitary pgm3 : pList2) {
                if (pgm3.getState() == 0) {
                    ++moveNum;
                    this.playerGeneralMilitaryDao.updateLocationId(playerId, pgm3.getGeneralId(), city.getId());
                    final GeneralMoveDto gmd2 = getGeneralMoveDto(playerId, pgm3.getGeneralId());
                    this.sendAttMoveInfo(playerId, pgm3.getGeneralId(), pgm3.getLocationId(), city.getId(), player.getForceId(), (gmd2 != null) ? gmd2.moveLine : "", pgm3.getForces(), true);
                }
            }
            if (moveNum <= 0) {
                return JsonBuilder.getJson(State.SUCCESS, LocalMessages.MOVE_GENERAL_BUSY);
            }
            this.changeForceIdAndState(city.getId(), player.getForceId(), 0, player.getPlayerId(), "");
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
    }
    
    @Transactional
    @Override
    public byte[] autoMoveStop(final PlayerDto playerDto, final int generalId) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final int playerId = playerDto.playerId;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final GeneralMoveDto gmd = getGeneralMoveDto(playerId, generalId);
        if (gmd == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (gmd.cityState != 6) {
            gmd.moveLine = "";
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        gmd.moveLine = "";
        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
        this.generalService.sendGenerlMoveInfo(playerId, generalId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] autoMoveInfo(final PlayerDto playerDto, final int cityId, final int generalId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int playerId = playerDto.playerId;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_GENERAL_MOVE);
        }
        if (pgm.getLocationId() == cityId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final int state = pgm.getState();
        if (state > 1) {
            final Battle battle = NewBattleManager.getInstance().getBattleByGId(playerId, generalId);
            if (battle == null) {
                CityService.errorLog.error("PlayerGeneralMilitary state error-move! playerId:" + playerId + " generalId:" + generalId + " state:" + pgm.getState());
                this.dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(playerId, generalId, 1, new Date());
                this.dataGetter.getGeneralService().sendGmUpdate(playerId, generalId, false);
            }
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_BUSY);
        }
        final WorldCity wc = (WorldCity)this.worldCityCache.get((Object)cityId);
        final City targetCity = this.cityDao.read(cityId);
        boolean adjoining = false;
        final Set<Integer> nbSet = this.worldRoadCache.getNeighbors(pgm.getLocationId());
        for (final Integer key : nbSet) {
            if (key == cityId) {
                adjoining = true;
            }
        }
        if (targetCity.getForceId() != playerDto.forceId && adjoining) {
            doc.createElement("type", "att");
        }
        else {
            doc.createElement("type", "move");
            final PlayerWorld pw = this.playerWorldDao.read(playerId);
            final String[] ids = pw.getAttedId().split(",");
            int cityTemp = 0;
            final Set<Integer> listTemp = new HashSet<Integer>();
            listTemp.add(pgm.getLocationId());
            if (targetCity.getForceId() != playerDto.forceId) {
                listTemp.add(cityId);
            }
            City ci = null;
            final Set<Integer> fireCities = new HashSet<Integer>();
            for (int i = 0; i < ids.length; ++i) {
                cityTemp = Integer.valueOf(ids[i]);
                ci = CityDataCache.cityArray[cityTemp];
                if (ci == null) {
                    CityService.errorLog.error("CityService cityId:" + cityTemp + " playerId:" + playerId);
                }
                else if (ci.getForceId() == playerDto.forceId) {
                    listTemp.add(cityTemp);
                    if (ci.getState() != 0) {
                        fireCities.add(ci.getId());
                    }
                }
            }
            final int[] arr = new int[listTemp.size()];
            int j = 0;
            for (final Integer key2 : listTemp) {
                arr[j] = key2;
                ++j;
            }
            final List<Integer> list = this.cityDataCache.getMinPathFire(pgm.getLocationId(), cityId, arr, fireCities);
            if (list == null || list.size() <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_AUTO_NO_ROAD);
            }
            final double food = 0.0;
            final double force = 0.0;
            doc.createElement("from", ((WorldCity)this.worldCityCache.get((Object)pgm.getLocationId())).getName());
            doc.createElement("to", wc.getName());
            doc.startArray("cities");
            for (int k = 0; k < list.size(); ++k) {
                doc.startObject();
                doc.createElement("id", list.get(k));
                doc.endObject();
            }
            doc.endArray();
            doc.createElement("food", (int)(Object)Double.valueOf(food));
            doc.createElement("force", (int)(Object)Double.valueOf(force));
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private double getRecuitFactor(final PlayerGeneralMilitary pgm, final int playerId, final int forceId, final WorldCity worldCity) {
        final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
        final Troop troop = this.troopCache.getTroop(general.getTroop(), playerId);
        return this.generalService.getRecuitConsume(troop.getId(), forceId, worldCity);
    }
    
    public static String getParams(final int playerId, final int generalId, final int cityId, final boolean autoBat) {
        final StringBuilder sb = new StringBuilder();
        sb.append(playerId).append("#").append(generalId).append("#").append(cityId).append("#").append(autoBat ? 1 : 0);
        return sb.toString();
    }
    
    @Override
    public byte[] autoMove(final int playerId, final int generalId, final int cityId, final int kick) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final Player player = this.playerDao.read(playerId);
        final int forceId = player.getForceId();
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.IN_JUBEN_CANNT_MOVE);
        }
        final Integer farmCityId = WorldFarmCache.forceCityIdMap.get(player.getForceId());
        if (farmCityId != null && farmCityId == cityId) {
            final List<StoreHouse> shList = this.dataGetter.getStoreHouseDao().getByItemId(playerId, 1701, 20);
            if (shList == null || shList.size() <= 0 || shList.get(0).getNum() < 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.FARM_TOKEN_IS_NOT_ENOUGH);
            }
        }
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_GENERAL_MOVE);
        }
        final int state = pgm.getState();
        if (state != 0 && state != 1 && state != 24) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_BUSY);
        }
        if (!RankService.canEnterHJCenterCity(cityId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NATION_TASK_XY_CANNOT_ENTER);
        }
        if (pgm.getLocationId() == cityId) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (TeamManager.getInstance().isJoinTeam(playerId, generalId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10030);
        }
        final Stratagem trick = this.cilvilTrickService.afterStateTrick(pgm.getLocationId(), 6, player.getForceId());
        if (trick != null) {
            return JsonBuilder.getJson(State.FAIL, trick.getError());
        }
        if (RankService.isBarCity(cityId) && this.rankService.hasBarTasks(forceId) <= 0 && this.dataGetter.getNationService().getStageByForceId(forceId) >= 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DO_NOT_HAVA_BAR_TASK);
        }
        if (RankService.isBarCity(cityId) && WorldCityCommon.forcIdManzuCityIdMap.get(forceId) != cityId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LOCATION_NOT_OPT);
        }
        final PlayerWorld pw = this.playerWorldDao.read(playerId);
        final Set<Integer> attedSet = new HashSet<Integer>();
        if (pw.getAttedId() != null) {
            final String[] ids = pw.getAttedId().split(",");
            int key = 0;
            String[] array;
            for (int length = (array = ids).length, l = 0; l < length; ++l) {
                final String str = array[l];
                key = Integer.valueOf(str);
                attedSet.add(key);
            }
        }
        if (!attedSet.contains(cityId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LOCATION_NOT_OPT);
        }
        boolean adjoining = false;
        final Set<Integer> nbSet = this.worldRoadCache.getNeighbors(pgm.getLocationId());
        for (final Integer key2 : nbSet) {
            if (key2 == cityId) {
                adjoining = true;
            }
        }
        City targetCity = null;
        GeneralMoveDto gmd = getGeneralMoveDto(playerId, generalId);
        if (gmd != null && CDUtil.getCD(gmd.nextMoveTime, new Date()) > 0L) {
            targetCity = this.cityDao.read(cityId);
        }
        else {
            final String[] cityBatLocks = getCityLock(cityId, pgm.getLocationId());
            synchronized (cityBatLocks[0]) {
                // monitorenter(s = cityBatLocks[1])
                try {
                    targetCity = this.cityDao.read(cityId);
                    if (targetCity.getForceId() != player.getForceId() && adjoining) {
                        gmd = getGeneralMoveDto(playerId, generalId);
                        if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, new Date())) {
                            // monitorexit(s)
                            // monitorexit(cityBatLocks[0])
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENERAL_IN_MOVE_CD);
                        }
                        if (gmd != null && (gmd.cityState == 22 || gmd.cityState == 23)) {
                            // monitorexit(s)
                            // monitorexit(cityBatLocks[0])
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENTEAL_IN_CELL);
                        }
                        final Stratagem stratagem = this.cilvilTrickService.afterStateTrick(cityId, 5, -1);
                        if (stratagem != null) {
                            // monitorexit(s)
                            // monitorexit(cityBatLocks[0])
                            return JsonBuilder.getJson(State.FAIL, stratagem.getError());
                        }
                        final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                        if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                            // monitorexit(s)
                            // monitorexit(cityBatLocks[0])
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH);
                        }
                        this.dealTrap(player, pgm, cityId);
                        int battleType = 3;
                        if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
                            battleType = 14;
                        }
                        this.dataGetter.getCityService().sendAttMoveInfo(playerId, generalId, pgm.getLocationId(), cityId, player.getForceId(), "", pgm.getForces(), false);
                        this.battleService.battleStart(playerId, battleType, cityId, String.valueOf(generalId), 0);
                        gmd = getGeneralMoveDto(playerId, generalId);
                        if (gmd != null) {
                            gmd.moveLine = "";
                        }
                        doc.createElement("endCityId", cityId);
                        doc.endObject();
                        // monitorexit(s)
                        // monitorexit(cityBatLocks[0])
                        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                    }
                    else {
                    }
                    // monitorexit(s)
                }
                finally {}
            }
            // monitorexit(cityBatLocks[0])
        }
        if (this.cityDao.read(pgm.getLocationId()).getForceId() != player.getForceId()) {
            CityService.errorLog.error("CityService autoMove playerId:" + playerId + " playerForceId:" + player.getForceId() + " generalId:" + generalId + " pgm.getLocationId():" + pgm.getLocationId() + " targetCityId:" + cityId);
        }
        if (gmd != null && (gmd.cityState == 22 || gmd.cityState == 23)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GENTEAL_IN_CELL_NO_MOVE);
        }
        final String[] ids2 = pw.getAttedId().split(",");
        int cityTemp = 0;
        final Set<Integer> listTemp = new HashSet<Integer>();
        listTemp.add(pgm.getLocationId());
        if (targetCity.getForceId() != player.getForceId()) {
            listTemp.add(cityId);
        }
        City ci = null;
        final Set<Integer> fireCities = new HashSet<Integer>();
        for (int i = 0; i < ids2.length; ++i) {
            cityTemp = Integer.valueOf(ids2[i]);
            ci = CityDataCache.cityArray[cityTemp];
            if (ci.getForceId() == player.getForceId()) {
                listTemp.add(cityTemp);
                if (ci.getState() != 0) {
                    fireCities.add(ci.getId());
                }
            }
        }
        final int[] arr = new int[listTemp.size()];
        int j = 0;
        for (final Integer key3 : listTemp) {
            arr[j] = key3;
            ++j;
        }
        final List<Integer> list = this.cityDataCache.getMinPathFire(pgm.getLocationId(), cityId, arr, fireCities);
        if (list == null || list.size() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MOVE_AUTO_NO_ROAD);
        }
        final StringBuilder sb = new StringBuilder();
        for (int k = 1; k < list.size(); ++k) {
            sb.append(list.get(k)).append(",");
        }
        gmd = getUpdateGeneralMoveDto(playerId, generalId);
        gmd.moveLine = sb.toString();
        gmd.type = 1;
        if (sb.length() <= 0) {
            this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
            this.generalService.sendGenerlMoveInfo(playerId, generalId);
        }
        gmd.cityState = 6;
        if (gmd == null || gmd.nextMoveTime <= System.currentTimeMillis()) {
            final Tuple<Integer, String> tupleMove = this.move(playerId, generalId, list.get(1), true, kick);
            if (tupleMove.left != 6 && tupleMove.left != 1) {
                return JsonBuilder.getJson(State.FAIL, tupleMove.right);
            }
            if (tupleMove.left == 6) {
                doc.startArray("path");
                for (final int id : list) {
                    doc.startObject();
                    doc.createElement("cityId", id);
                    doc.endObject();
                }
                doc.endArray();
                this.generalService.sendGenerlMoveInfo(playerId, generalId);
            }
        }
        else {
            doc.startArray("path");
            for (final int id2 : list) {
                doc.startObject();
                doc.createElement("cityId", id2);
                doc.endObject();
            }
            doc.endArray();
            this.generalService.sendGenerlMoveInfo(playerId, generalId);
        }
        doc.createElement("endCityId", cityId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public Tuple<Boolean, String> assembleMove(final int playerId, final int generalId, final int cityId, final int kick) {
        final Tuple<Boolean, String> tuple = new Tuple();
        final Player player = this.playerDao.read(playerId);
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            tuple.left = false;
            tuple.right = LocalMessages.T_NO_SUCH_GENERAL_MOVE;
            return tuple;
        }
        final Stratagem trick = this.cilvilTrickService.afterStateTrick(pgm.getLocationId(), 6, player.getForceId());
        if (trick != null) {
            tuple.left = false;
            tuple.right = trick.getError();
            return tuple;
        }
        if (!RankService.canEnterHJCenterCity(cityId)) {
            tuple.left = false;
            tuple.right = LocalMessages.NATION_TASK_XY_CANNOT_ENTER;
            return tuple;
        }
        final PlayerWorld pw = this.playerWorldDao.read(playerId);
        final Set<Integer> attedSet = new HashSet<Integer>();
        if (pw.getAttedId() != null) {
            final String[] ids = pw.getAttedId().split(",");
            int key = 0;
            String[] array;
            for (int length = (array = ids).length, l = 0; l < length; ++l) {
                final String str = array[l];
                key = Integer.valueOf(str);
                attedSet.add(key);
            }
        }
        if (!attedSet.contains(cityId)) {
            tuple.left = false;
            tuple.right = LocalMessages.LOCATION_NOT_OPT;
            return tuple;
        }
        City targetCity = null;
        boolean adjoining = false;
        final Set<Integer> nbSet = this.worldRoadCache.getNeighbors(pgm.getLocationId());
        for (final Integer key2 : nbSet) {
            if (key2 == cityId) {
                adjoining = true;
            }
        }
        GeneralMoveDto gmd = getGeneralMoveDto(playerId, generalId);
        final String[] cityBatLocks = getCityLock(cityId, pgm.getLocationId());
        synchronized (cityBatLocks[0]) {
            // monitorenter(s = cityBatLocks[1])
            try {
                targetCity = this.cityDao.read(cityId);
                if (targetCity.getForceId() != player.getForceId() && adjoining) {
                    final Stratagem stratagem = this.cilvilTrickService.afterStateTrick(cityId, 5, -1);
                    if (stratagem != null) {
                        tuple.left = false;
                        tuple.right = stratagem.getError();
                        // monitorexit(s)
                        // monitorexit(cityBatLocks[0])
                        return tuple;
                    }
                    this.dealTrap(player, pgm, cityId);
                    int battleType = 3;
                    if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
                        battleType = 14;
                    }
                    this.dataGetter.getCityService().sendAttMoveInfo(playerId, generalId, pgm.getLocationId(), cityId, player.getForceId(), "", pgm.getForces(), false);
                    this.battleService.battleStart(playerId, battleType, cityId, String.valueOf(generalId), 0);
                    if (gmd != null) {
                        gmd.moveLine = "";
                    }
                    tuple.left = true;
                    tuple.right = "";
                    // monitorexit(s)
                    // monitorexit(cityBatLocks[0])
                    return tuple;
                }
                else {
                }
                // monitorexit(s)
            }
            finally {}
        }
        // monitorexit(cityBatLocks[0])
        final int state = pgm.getState();
        if (state != 0 && state != 1 && state != 24) {
            tuple.left = false;
            tuple.right = LocalMessages.GENERAL_BUSY;
            return tuple;
        }
        if (this.cityDao.read(pgm.getLocationId()).getForceId() != player.getForceId()) {
            CityService.errorLog.error("CityService autoMove playerId:" + playerId + " playerForceId:" + player.getForceId() + " generalId:" + generalId + " pgm.getLocationId():" + pgm.getLocationId() + " targetCityId:" + cityId);
            tuple.left = false;
            tuple.right = LocalMessages.T_NO_SUCH_GENERAL_MOVE;
            return tuple;
        }
        if (pgm.getLocationId() == cityId) {
            tuple.left = false;
            tuple.right = LocalMessages.GENERAL_IN_CURCITY_NO_MOVE;
            return tuple;
        }
        final String[] ids2 = pw.getAttedId().split(",");
        int cityTemp = 0;
        final List<Integer> listTemp = new ArrayList<Integer>();
        if (targetCity.getForceId() != player.getForceId()) {
            listTemp.add(cityId);
        }
        City ci = null;
        final Set<Integer> fireCities = new HashSet<Integer>();
        for (int i = 0; i < ids2.length; ++i) {
            cityTemp = Integer.valueOf(ids2[i]);
            ci = CityDataCache.cityArray[cityTemp];
            if (ci.getForceId() == player.getForceId()) {
                listTemp.add(cityTemp);
                if (ci.getState() != 0) {
                    fireCities.add(ci.getId());
                }
            }
        }
        final int[] arr = new int[listTemp.size()];
        for (int j = 0; j < listTemp.size(); ++j) {
            arr[j] = listTemp.get(j);
        }
        final List<Integer> list = this.cityDataCache.getMinPathFire(pgm.getLocationId(), cityId, arr, fireCities);
        if (list == null || list.size() <= 0) {
            tuple.left = false;
            tuple.right = LocalMessages.MOVE_AUTO_NO_ROAD;
            return tuple;
        }
        final PlayerGroupArmy pga = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, generalId);
        if (pga != null && pga.getIsLeader() != 1) {
            tuple.left = false;
            tuple.right = LocalMessages.IN_GROUPARMY_CANNOT_MOVE;
            return tuple;
        }
        final StringBuilder sb = new StringBuilder();
        for (int k = 1; k < list.size(); ++k) {
            sb.append(list.get(k)).append(",");
        }
        gmd = getUpdateGeneralMoveDto(playerId, generalId);
        gmd.moveLine = sb.toString();
        gmd.type = 1;
        if (sb.length() <= 0) {
            this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
            this.generalService.sendGenerlMoveInfo(playerId, generalId);
        }
        gmd.cityState = 6;
        final Tuple<Integer, String> tupleMove = this.move(playerId, generalId, list.get(1), true, kick);
        if (tupleMove.left != 6 && tupleMove.left != 1) {
            tuple.left = false;
            tuple.right = tupleMove.right;
            return tuple;
        }
        if (tupleMove.left == 6) {
            this.generalService.sendGenerlMoveInfo(playerId, generalId);
        }
        tuple.left = true;
        tuple.right = LocalMessages.ASSEMBLE_SUCC;
        return tuple;
    }
    
    @Override
    public void dealTrap(final Player player, final PlayerGeneralMilitary pgm, final int cityId) {
        final int playerId = player.getPlayerId();
        final int generalId = pgm.getGeneralId();
        final Tuple<Integer, Stratagem> reTuple = this.cilvilTrickService.afterTrapTrick(cityId, player.getForceId());
        if (reTuple != null && reTuple.left != 0) {
            final PlayerGroupArmy pgArmy = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, generalId);
            if (pgArmy != null && pgArmy.getIsLeader() == 1) {
                final List<PlayerGroupArmy> list = this.playerGroupArmyDao.getList(pgArmy.getArmyId());
                for (final PlayerGroupArmy pga : list) {
                    final Player memPlayer = this.playerDao.read(pga.getPlayerId());
                    final PlayerGeneralMilitary memPgm = this.playerGeneralMilitaryDao.getMilitary(pga.getPlayerId(), pga.getGeneralId());
                    final int forceReduce = (reTuple.right.getPar2() > memPgm.getForces()) ? (memPgm.getForces() - 1) : reTuple.right.getPar2();
                    final int res = this.playerGeneralMilitaryDao.consumeForcesByState(pga.getPlayerId(), pga.getGeneralId(), forceReduce, new Date());
                    if (res > 0) {
                        this.generalService.sendGeneralMilitaryRecruitInfo(pga.getPlayerId(), pga.getGeneralId());
                        this.cilvilTrickService.updateTrickInfo(cityId, memPlayer.getForceId(), reTuple.right);
                        final JsonDocument document = new JsonDocument();
                        document.startObject();
                        document.createElement("generalId", pga.getGeneralId());
                        document.createElement("trickInfo", (Object)MessageFormatter.format(LocalMessages.TRICK_TRAP_INFO, new Object[] { reTuple.right.getName(), reTuple.right.getPar2() }));
                        document.endObject();
                        Players.push(pga.getPlayerId(), PushCommand.PUSH_TRICKINFO, document.toByte());
                    }
                }
            }
            else {
                int forceReduce2 = 0;
                boolean isTrapvalid = true;
                if (reTuple.right.getQuality() == 1) {
                    final int tech = this.techEffectCache.getTechEffect(playerId, 38);
                    if (tech > 0) {
                        isTrapvalid = false;
                    }
                }
                int res2 = 0;
                if (isTrapvalid) {
                    forceReduce2 = ((reTuple.right.getPar2() > pgm.getForces()) ? (pgm.getForces() - 1) : reTuple.right.getPar2());
                    res2 = this.playerGeneralMilitaryDao.consumeForcesByState(playerId, generalId, forceReduce2, new Date());
                }
                else {
                    res2 = 1;
                }
                if (res2 > 0) {
                    this.cilvilTrickService.updateTrickInfo(cityId, player.getForceId(), reTuple.right);
                    this.generalService.sendGeneralMilitaryRecruitInfo(playerId, generalId);
                    final JsonDocument document2 = new JsonDocument();
                    document2.startObject();
                    document2.createElement("generalId", generalId);
                    document2.createElement("cityId", cityId);
                    document2.createElement("forceReduce", forceReduce2);
                    document2.createElement("trickName", reTuple.right.getName());
                    document2.createElement("isTrapvalid", isTrapvalid);
                    document2.createElement("trickInfo", (Object)MessageFormatter.format(LocalMessages.TRICK_TRAP_INFO, new Object[] { reTuple.right.getName(), reTuple.right.getPar2() }));
                    document2.endObject();
                    Players.push(playerId, PushCommand.PUSH_TRICKINFO, document2.toByte());
                }
            }
        }
    }
    
    private int isDrawBack(final Integer locationId, final int cityId, final int forceId) {
        final int curDistance = ((WorldCity)this.worldCityCache.get((Object)locationId)).getDistance(forceId);
        final int nextDistance = ((WorldCity)this.worldCityCache.get((Object)cityId)).getDistance(forceId);
        return nextDistance - curDistance;
    }
    
    @Override
    public void changeState(final String param) {
        final String[] s = param.split(";");
        final int playerId = Integer.valueOf(s[0]);
        final int generalId = Integer.valueOf(s[1]);
        final boolean wasRecruiting = Integer.valueOf(s[2]) == 1;
        final boolean auto = Integer.valueOf(s[3]) == 1;
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (pgm == null) {
            return;
        }
        final GeneralMoveDto gmd = getUpdateGeneralMoveDto(playerId, generalId);
        synchronized (gmd) {
            if (gmd.type != 1) {
                // monitorexit(gmd)
                return;
            }
            if (gmd.nextMoveTime == 0L || System.currentTimeMillis() < gmd.nextMoveTime) {
                // monitorexit(gmd)
                return;
            }
            gmd.nextMoveTime = 0L;
            gmd.type = 1;
        }
        // monitorexit(gmd)
        if (auto && this.inAutoMove(playerId, generalId)) {
            return;
        }
        gmd.cityState = 0;
        gmd.nextMoveTime = 0L;
        if (pgm.getState() == 1 || pgm.getState() == 0) {
            final Date nowDate = new Date();
            if (wasRecruiting) {
                this.playerGeneralMilitaryDao.restartRecruit(playerId, generalId, 1, nowDate);
            }
        }
        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, generalId);
        this.generalService.sendGenerlMoveInfo(playerId, generalId);
    }
    
    private Tuple<Integer, String> checkMoveCondition(final PlayerGeneralMilitary pgm, final Player player, final int cityId, final boolean auto) {
        final Tuple<Integer, String> tuple = new Tuple();
        if (pgm == null) {
            tuple.left = 2;
            tuple.right = LocalMessages.T_NO_SUCH_GENERAL_MOVE;
            return tuple;
        }
        final int playerId = pgm.getPlayerId();
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            tuple.left = 12;
            tuple.right = LocalMessages.IN_JUBEN_CANNT_MOVE;
            return tuple;
        }
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final Date nowDate = new Date();
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[24] == '0') {
            tuple.left = 4;
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        final int state = pgm.getState();
        if (state != 0 && state != 1 && state != 24) {
            tuple.left = 5;
            tuple.right = LocalMessages.GENERAL_BUSY;
            return tuple;
        }
        final GeneralMoveDto gmd = getGeneralMoveDto(playerId, pgm.getGeneralId());
        if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, nowDate)) {
            tuple.left = 6;
            tuple.right = LocalMessages.GENERAL_IN_MOVE_CD;
            return tuple;
        }
        final Stratagem stratagem = this.cilvilTrickService.afterStateTrick(pgm.getLocationId(), 6, player.getForceId());
        if (stratagem != null) {
            tuple.left = 11;
            tuple.right = stratagem.getError();
            TrickFactory.sendLiesInfo(playerId, pgm.getGeneralId(), stratagem);
            return tuple;
        }
        if (this.cityDao.read(cityId).getForceId() != player.getForceId()) {
            tuple.left = 7;
            tuple.right = LocalMessages.FORCE_WRONG;
            return tuple;
        }
        final Set<Integer> neighbors = this.worldRoadCache.getNeighbors(pgm.getLocationId());
        if (neighbors.isEmpty() || !neighbors.contains(cityId)) {
            tuple.left = 8;
            tuple.right = LocalMessages.LOCATION_NOT_NEIGHBOR;
            return tuple;
        }
        final PlayerWorld pw = this.playerWorldDao.read(playerId);
        final Set<Integer> attedSet = new HashSet<Integer>();
        if (pw.getAttedId() != null) {
            final String[] ids = pw.getAttedId().split(",");
            int key = 0;
            String[] array;
            for (int length = (array = ids).length, i = 0; i < length; ++i) {
                final String str = array[i];
                key = Integer.valueOf(str);
                attedSet.add(key);
            }
        }
        if (!attedSet.contains(cityId)) {
            tuple.left = 10;
            tuple.right = LocalMessages.LOCATION_NOT_OPT;
            return tuple;
        }
        tuple.left = 1;
        return tuple;
    }
    
    @Override
    public Tuple<Integer, String> move(final int playerId, final int generalId, final int cityId, final boolean auto, final int kick) {
        final Tuple<Integer, String> tupleMove = new Tuple();
        final Player player = this.playerDao.read(playerId);
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        if (this.worldFarmService.isInFarmForbiddenOperation(pgm, true)) {
            tupleMove.left = 0;
            tupleMove.right = LocalMessages.FARM_WORKING;
            return tupleMove;
        }
        final Set<Integer> neighbors = this.worldRoadCache.getNeighbors(pgm.getLocationId());
        if (neighbors.isEmpty() || !neighbors.contains(cityId)) {
            final GeneralMoveDto gmd = getGeneralMoveDto(playerId, generalId);
            if (gmd != null) {
                gmd.moveLine = "";
            }
            tupleMove.left = 0;
            tupleMove.right = LocalMessages.LOCATION_NOT_NEIGHBOR;
            return tupleMove;
        }
        if (!RankService.canEnterHJCenterCity(cityId)) {
            final GeneralMoveDto gmd = getGeneralMoveDto(playerId, generalId);
            if (gmd != null) {
                gmd.moveLine = "";
            }
            tupleMove.left = 0;
            tupleMove.right = LocalMessages.NATION_TASK_XY_CANNOT_ENTER;
            return tupleMove;
        }
        final PlayerWorld pw = this.playerWorldDao.read(playerId);
        final Set<Integer> attedSet = new HashSet<Integer>();
        if (pw.getAttedId() != null) {
            final String[] ids = pw.getAttedId().split(",");
            int key = 0;
            String[] array;
            for (int length = (array = ids).length, j = 0; j < length; ++j) {
                final String str = array[j];
                key = Integer.valueOf(str);
                attedSet.add(key);
            }
        }
        if (!attedSet.contains(cityId)) {
            final GeneralMoveDto gmd2 = getGeneralMoveDto(playerId, generalId);
            if (gmd2 != null) {
                gmd2.moveLine = "";
            }
            tupleMove.left = 0;
            tupleMove.right = LocalMessages.LOCATION_NOT_OPT;
            return tupleMove;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            final GeneralMoveDto gmd3 = getGeneralMoveDto(playerId, generalId);
            if (gmd3 != null) {
                gmd3.moveLine = "";
            }
            tupleMove.left = 0;
            tupleMove.right = LocalMessages.IN_JUBEN_CANNT_MOVE;
            return tupleMove;
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (battle != null) {
            final String[] cityBatLocks = getCityLock(cityId, pgm.getLocationId());
            synchronized (cityBatLocks[0]) {
                // monitorenter(s = cityBatLocks[1])
                try {
                    if (battle.getDefBaseInfo().getForceId() != player.getForceId()) {
                        final Stratagem stratagem = this.cilvilTrickService.afterStateTrick(cityId, 5, -1);
                        if (stratagem != null) {
                            final GeneralMoveDto gmd4 = getGeneralMoveDto(playerId, generalId);
                            if (gmd4 != null) {
                                gmd4.moveLine = "";
                            }
                            this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                            tupleMove.left = 0;
                            tupleMove.right = stratagem.getError();
                            this.generalService.sendGenerlMoveInfo(playerId, generalId);
                            // monitorexit(s)
                            // monitorexit(cityBatLocks[0])
                            return tupleMove;
                        }
                    }
                    final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                    if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                        final GeneralMoveDto gmd4 = getGeneralMoveDto(playerId, generalId);
                        if (gmd4 != null) {
                            gmd4.moveLine = "";
                        }
                        tupleMove.left = 0;
                        tupleMove.right = LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH;
                        // monitorexit(s)
                        // monitorexit(cityBatLocks[0])
                        return tupleMove;
                    }
                    this.dealTrap(player, pgm, cityId);
                    int battleType = 3;
                    if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
                        battleType = 14;
                    }
                    this.dataGetter.getCityService().sendAttMoveInfo(playerId, generalId, pgm.getLocationId(), cityId, player.getForceId(), "", pgm.getForces(), false);
                    this.battleService.battleStart(playerId, battleType, cityId, String.valueOf(generalId), 0);
                    final GeneralMoveDto gmd5 = getGeneralMoveDto(playerId, generalId);
                    if (gmd5 != null) {
                        gmd5.moveLine = "";
                    }
                    tupleMove.left = 1;
                    tupleMove.right = "";
                    // monitorexit(s)
                    // monitorexit(cityBatLocks[0])
                    return tupleMove;
                }
                finally {}
            }
        }
        boolean adjoining = false;
        final Set<Integer> nbSet = this.worldRoadCache.getNeighbors(pgm.getLocationId());
        for (final Integer key2 : nbSet) {
            if (key2 == cityId) {
                adjoining = true;
            }
        }
        final String[] cityBatLocks2 = getCityLock(cityId, pgm.getLocationId());
        synchronized (cityBatLocks2[0]) {
            // monitorenter(s2 = cityBatLocks2[1])
            try {
                if (this.cityDao.read(cityId).getForceId() != player.getForceId() && adjoining) {
                    final int forceId = player.getForceId();
                    if (RankService.isBarCity(cityId) && this.rankService.hasBarTasks(forceId) < 0 && this.dataGetter.getNationService().getStageByForceId(forceId) >= 4) {
                        final GeneralMoveDto gmd6 = getGeneralMoveDto(playerId, generalId);
                        if (gmd6 != null) {
                            gmd6.moveLine = "";
                        }
                        tupleMove.left = 0;
                        tupleMove.right = LocalMessages.DO_NOT_HAVA_BAR_TASK;
                        // monitorexit(s2)
                        // monitorexit(cityBatLocks2[0])
                        return tupleMove;
                    }
                    final Stratagem stratagem2 = this.cilvilTrickService.afterStateTrick(cityId, 5, -1);
                    if (stratagem2 != null) {
                        final GeneralMoveDto gmd7 = getGeneralMoveDto(playerId, generalId);
                        if (gmd7 != null) {
                            gmd7.moveLine = "";
                        }
                        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                        tupleMove.left = 0;
                        tupleMove.right = stratagem2.getError();
                        this.generalService.sendGenerlMoveInfo(playerId, generalId);
                        // monitorexit(s2)
                        // monitorexit(cityBatLocks2[0])
                        return tupleMove;
                    }
                    final int maxHp2 = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                    if (pgm.getForces() * 1.0 / maxHp2 < 0.05) {
                        final GeneralMoveDto gmd8 = getGeneralMoveDto(playerId, generalId);
                        if (gmd8 != null) {
                            gmd8.moveLine = "";
                        }
                        tupleMove.left = 0;
                        tupleMove.right = LocalMessages.BATTLE_INT_BATTLE_BLOOD_NO_ENOUGH;
                        // monitorexit(s2)
                        // monitorexit(cityBatLocks2[0])
                        return tupleMove;
                    }
                    this.dealTrap(player, pgm, cityId);
                    int battleType2 = 3;
                    if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
                        battleType2 = 14;
                    }
                    this.dataGetter.getCityService().sendAttMoveInfo(playerId, generalId, pgm.getLocationId(), cityId, player.getForceId(), "", pgm.getForces(), false);
                    this.battleService.battleStart(playerId, battleType2, cityId, String.valueOf(generalId), 0);
                    final GeneralMoveDto gmd9 = getGeneralMoveDto(playerId, generalId);
                    if (gmd9 != null) {
                        gmd9.moveLine = "";
                    }
                    tupleMove.left = 1;
                    tupleMove.right = "";
                    // monitorexit(s2)
                    // monitorexit(cityBatLocks2[0])
                    return tupleMove;
                }
                else {
                }
                // monitorexit(s2)
            }
            finally {}
        }
        // monitorexit(cityBatLocks2[0])
        final Tuple<Integer, String> tuple = this.checkMoveCondition(pgm, player, cityId, auto);
        if (tuple.left != 1) {
            if (tuple.left != 6) {
                final GeneralMoveDto gmd4 = getGeneralMoveDto(playerId, generalId);
                if (gmd4 != null) {
                    gmd4.moveLine = "";
                }
            }
            this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
            tupleMove.left = tuple.left;
            tupleMove.right = tuple.right;
            return tupleMove;
        }
        this.startMove(playerId, generalId, cityId, auto);
        final GeneralMoveDto gmd4 = getGeneralMoveDto(playerId, generalId);
        StringBuilder sb = new StringBuilder();
        if (gmd4 != null && gmd4.moveLine != null && gmd4.moveLine.length() > 0) {
            final String[] strs = gmd4.moveLine.split(",");
            for (int i = 1; i < strs.length; ++i) {
                if (i > 1 || cityId != Integer.valueOf(strs[i])) {
                    sb.append(strs[i]).append(",");
                }
            }
            this.sendAttMoveInfo(playerId, generalId, pgm.getLocationId(), cityId, player.getForceId(), sb.toString(), pgm.getForces(), false);
            sb = this.updateMoveLine(gmd4.moveLine, cityId);
            gmd4.moveLine = sb.toString();
            if (sb.length() <= 0) {
                gmd4.moveLine = "";
            }
            gmd4.cityState = 6;
            TaskMessageHelper.sendWorldMoveTaskMessage(playerId);
        }
        tupleMove.left = 1;
        tupleMove.right = "";
        return tupleMove;
    }
    
    private StringBuilder updateMoveLine(final String moveLine, final int cityId) {
        final StringBuilder sb = new StringBuilder();
        final String[] strs = moveLine.split(",");
        for (int i = 1; i < strs.length; ++i) {
            if (i > 1 || cityId != Integer.valueOf(strs[i])) {
                sb.append(strs[i]).append(",");
            }
        }
        return sb;
    }
    
    @Override
    public boolean startMove(final int playerId, final int generalId, final int cityId, final boolean auto) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final Player player = this.playerDao.read(playerId);
        final String[] cityBatLocks = getCityLock(cityId, pgm.getLocationId());
        synchronized (cityBatLocks[0]) {
            // monitorenter(s = cityBatLocks[1])
            try {
                final int state = pgm.getState();
                final int curCityId = pgm.getLocationId();
                final WorldRoad road = this.worldRoadCache.getRoad(curCityId, cityId);
                long cd = 0L;
                final General general = (General)this.generalCache.get((Object)pgm.getGeneralId());
                final Troop troop = this.troopCache.getTroop(general.getTroop(), player.getPlayerId());
                cd = WorldCityCommon.getNextMoveCd(road, troop.getSpeed());
                final Date nextMoveDate = new Date(System.currentTimeMillis() + cd);
                final boolean isRecruiting = state == 1;
                final String params = WorldCityCommon.makeTaskParam(playerId, generalId, isRecruiting, auto);
                final StringBuffer sb = new StringBuffer(params);
                sb.append(";").append(cityId);
                final long nextMoveTime = nextMoveDate.getTime();
                final int taskId = this.jobService.addJob("cityService", "changeState", sb.toString(), nextMoveTime + 1000L, false);
                final GeneralMoveDto gmd = getUpdateGeneralMoveDto(playerId, generalId);
                gmd.startMoveTime = System.currentTimeMillis();
                gmd.startCityId = curCityId;
                gmd.cityState = 6;
                gmd.nextMoveTime = nextMoveTime;
                gmd.taskId = taskId;
                gmd.type = 1;
                this.playerGeneralMilitaryDao.move(playerId, generalId, state, cityId);
                this.generalService.sendGeneralMilitaryRecruitInfo(playerId, generalId);
                this.generalService.sendGenerlMoveInfo(playerId, generalId);
                this.updateGNumAndSend(pgm.getLocationId(), cityId);
                this.dealTrap(player, pgm, cityId);
                CityEventManager.getInstance().pushCityEventChangeInfoDueToMove(playerId, curCityId, cityId);
                final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
                final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
                if (hh != null && hh.getCityId() == cityId && battle == null) {
                    this.dataGetter.getBattleService().updateHuizhanPlayerForce(cityId, playerId, pgm.getForces());
                    this.dataGetter.getBattleService().updateHuizhanNationForce(cityId, player.getForceId(), pgm.getForces());
                }
                this.dealMoveInAndOutFarm(curCityId, cityId, player.getForceId(), pgm);
                // monitorexit(s)
                // monitorexit(cityBatLocks[0])
                return true;
            }
            finally {}
        }
    }
    
    private void dealMoveInAndOutFarm(final int curCityId, final int cityId, final int forceId, final PlayerGeneralMilitary pgm) {
        try {
            final Integer farmCityId = WorldFarmCache.forceCityIdMap.get(forceId);
            if (farmCityId == null) {
                return;
            }
            if (curCityId == farmCityId || (pgm.getState() == 24 && cityId != farmCityId)) {
                this.worldFarmService.changeGeneralState(1, pgm);
            }
            if (cityId == farmCityId) {
                final Player player = this.playerDao.read(pgm.getPlayerId());
                final PlayerDto playerDto = new PlayerDto(pgm.getPlayerId(), forceId);
                playerDto.playerLv = player.getPlayerLv();
                final MultiResult result = this.worldFarmService.doStart(playerDto, false, 1, pgm);
                if (result.result1) {
                    return;
                }
                this.worldFarmService.changeGeneralState(24, pgm);
            }
        }
        catch (Exception e) {
            CityService.errorLog.error(this, e);
        }
    }
    
    private void decideBoxInfo(final WorldRoad road, final int playerId, final JsonDocument doc, final int generalId) {
        if (road == null) {
            return;
        }
        final Player player = this.playerDao.read(playerId);
        try {
            CityService.locks[playerId % CityService.LOCKS_LEN].lock();
            final PlayerWorld playerWorld = this.playerWorldDao.read(playerId);
            String boxInfo = playerWorld.getBoxispicked();
            final String[] box = SymbolUtil.StringtoArray(boxInfo, "\\|");
            final int id = road.getId();
            final int boxType = Integer.parseInt(box[id]);
            if (boxType != 0) {
                box[id] = String.valueOf(0);
                final WorldTreasure treasure = (WorldTreasure)this.worldTreasureCache.get((Object)boxType);
                final String reward = treasure.getReward();
                final int boxTrueType = treasure.getType();
                final String generalName = ((General)this.generalCache.get((Object)generalId)).getName();
                final ITaskReward rewardThing = TaskRewardFactory.getInstance().getTaskReward(reward);
                final Map<Integer, Reward> map = rewardThing.rewardPlayer(new PlayerDto(playerId, player.getForceId()), this.dataGetter, "\u4e16\u754c\u8def\u5f84\u5b9d\u7bb1", null);
                doc.startArray("curReward");
                final StringBuffer sb = new StringBuffer();
                for (final Integer key : map.keySet()) {
                    final Reward rd = map.get(key);
                    doc.startObject();
                    final int type = rd.getType();
                    final int num = rd.getNum();
                    if (type == 31) {
                        final Equip equip = (Equip)this.equipCache.get((Object)num);
                        final String name = equip.getName();
                        doc.createElement("type", type);
                        doc.createElement("equipName", name);
                        doc.createElement("pic", equip.getPic());
                        doc.createElement("intro", equip.getIntro());
                        doc.createElement("quality", equip.getQuality());
                        sb.append(name).append('\u3001');
                    }
                    else {
                        doc.createElement("type", type);
                        doc.createElement("num", num);
                        sb.append(num).append(rd.getName());
                    }
                    doc.endObject();
                }
                if (sb.charAt(sb.length() - 1) == '\u3001') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                doc.endArray();
                final String content = MessageFormatter.format(LocalMessages.BOX_MAIL_CONTENT, new Object[] { generalName, sb.toString() });
                this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.BOX_MAIL_TITLE, content, 1, playerId, 0);
                TaskMessageHelper.sendWorldTreasureGetTaskMessage(playerId, boxType);
                TaskMessageHelper.sendWorldTreasureByTypeGetTaskMessage(playerId, boxTrueType);
                this.dataGetter.getCourtesyService().addPlayerEvent(playerId, 7, 0);
                boxInfo = SymbolUtil.toString(box, "|");
                this.playerWorldDao.updateBoxInfo(playerId, boxInfo);
            }
        }
        finally {
            CityService.locks[playerId % CityService.LOCKS_LEN].unlock();
        }
        CityService.locks[playerId % CityService.LOCKS_LEN].unlock();
    }
    
    public boolean inAutoMove(final int playerId, final int generalId) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final GeneralMoveDto gmd = getGeneralMoveDto(playerId, generalId);
        if (gmd != null && gmd.moveLine != null && gmd.moveLine.length() > 0) {
            final String[] strs = gmd.moveLine.split(",");
            if (strs.length > 0) {
                int cityId = Integer.valueOf(strs[0]);
                if (cityId == pgm.getLocationId()) {
                    if (strs.length <= 1) {
                        gmd.moveLine = "";
                        this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                        this.generalService.sendGenerlMoveInfo(playerId, generalId);
                        return false;
                    }
                    cityId = Integer.valueOf(strs[1]);
                }
                this.move(playerId, generalId, cityId, true, 1);
                return true;
            }
            gmd.moveLine = "";
            this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
            this.generalService.sendGenerlMoveInfo(playerId, generalId);
        }
        return false;
    }
    
    @Transactional
    @Override
    public void inAutoMove(final String param) {
        final String[] params = param.split(",");
        final int playerId = Integer.valueOf(params[0]);
        final int generalId = Integer.valueOf(params[1]);
        final boolean isWin = Integer.valueOf(params[2]) == 1;
        final PlayerGroupArmy pga = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, generalId);
        if (pga != null && pga.getIsLeader() != 1) {
            return;
        }
        if (!isWin) {
            if (pga != null) {
                this.groupArmyDao.deleteById(pga.getArmyId());
                this.playerGroupArmyDao.deleteByArmyId(pga.getArmyId());
            }
            final GeneralMoveDto gmd = getGeneralMoveDto(playerId, generalId);
            if (gmd != null) {
                gmd.moveLine = "";
            }
            this.generalService.sendGeneralMilitaryRecruitInfo(playerId, generalId);
            this.generalService.sendGenerlMoveInfo(playerId, generalId);
            return;
        }
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerId, generalId);
        final Date nowDate = new Date();
        final GeneralMoveDto gmd2 = getGeneralMoveDto(playerId, generalId);
        if (gmd2 != null && gmd2.moveLine != null && gmd2.moveLine.length() > 0) {
            if (CDUtil.isInCD(gmd2.nextMoveTime, nowDate)) {
                return;
            }
            final String[] strs = gmd2.moveLine.split(",");
            if (strs.length > 1) {
                final int cityId = Integer.valueOf(strs[1]);
                this.move(playerId, generalId, cityId, true, 1);
            }
            else {
                gmd2.moveLine = "";
                this.generalService.sendGeneralMilitaryRecruitInfo(playerId, pgm);
                this.generalService.sendGenerlMoveInfo(playerId, generalId);
            }
        }
    }
    
    @Override
    public void joinGroup(final int playerId, final int forceId, final Request request) {
        GroupManager.getInstance().getGroup(ChatType.WORLD.toString()).join(request.getSession());
        if (forceId == 1) {
            GroupManager.getInstance().getGroup(ChatType.WORLD_1.toString()).join(request.getSession());
        }
        else if (forceId == 2) {
            GroupManager.getInstance().getGroup(ChatType.WORLD_2.toString()).join(request.getSession());
        }
        else if (forceId == 3) {
            GroupManager.getInstance().getGroup(ChatType.WORLD_3.toString()).join(request.getSession());
        }
        this.rankService.addWorld(playerId, forceId);
    }
    
    @Override
    public void leaveGroup(final int playerId, final int forceId, final Request request) {
        GroupManager.getInstance().getGroup(ChatType.WORLD.toString()).leave(request.getSession().getId());
        if (forceId == 1) {
            GroupManager.getInstance().getGroup(ChatType.WORLD_1.toString()).leave(request.getSession().getId());
        }
        else if (forceId == 2) {
            GroupManager.getInstance().getGroup(ChatType.WORLD_2.toString()).leave(request.getSession().getId());
        }
        else if (forceId == 3) {
            GroupManager.getInstance().getGroup(ChatType.WORLD_3.toString()).leave(request.getSession().getId());
        }
        this.rankService.leaveWorld(playerId);
    }
    
    @Override
    public Tuple<Double, Double> getMilitariesCost(final List<PlayerGeneralMilitary> list, final int curCity, final int nextCity, final int playerId, final int forceId) {
        final Tuple<Double, Double> result = new Tuple();
        double food = 0.0;
        double force = 0.0;
        final int distance = this.isDrawBack(curCity, nextCity, forceId);
        final double moveCostFactor = ((C)this.dataGetter.getcCache().get((Object)"World.Move.FoodConsume")).getValue();
        final double backCostFactor = ((C)this.dataGetter.getcCache().get((Object)"World.MoveBack.DeserterProportion")).getValue();
        for (final PlayerGeneralMilitary p : list) {
            if (distance < 0) {
                final double forcesCost = p.getForces() * backCostFactor;
                force += forcesCost;
            }
            final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)p.getLocationId());
            final double foodCost = this.getRecuitFactor(p, playerId, forceId, worldCity) * p.getForces() * moveCostFactor;
            food += foodCost;
        }
        result.left = food;
        result.right = force;
        return result;
    }
    
    @Override
    public void stateJob(final String params) {
        if (StringUtils.isBlank(params)) {
            return;
        }
        final String[] pars = params.split(";");
        final int cityId = Integer.parseInt(pars[0]);
        if (WorldCityCommon.mainCityNationIdMap.containsKey(cityId)) {
            return;
        }
        final int title = Integer.parseInt(pars[1]);
        final long executionTime = Long.parseLong(pars[2]);
        final double forcesCost = Double.parseDouble(pars[3]);
        final City city = this.cityDao.read(cityId);
        if (city == null || city.getState() == null) {
            return;
        }
        if ((city.getTitle() == null || city.getTitle() == 0) && city.getStateJobId() != null) {
            this.jobService.cancelJob(city.getStateJobId(), true);
        }
        if (city.getState() == 0) {
            this.playerGeneralMilitaryDao.consumeCityForces(cityId, forcesCost, new Date());
            final List<PlayerGeneralMilitary> list = this.playerGeneralMilitaryDao.getMilitaryByLocationId(cityId);
            for (final PlayerGeneralMilitary pgm : list) {
                if (pgm.getState() > 1 && pgm.getForces() < 1) {
                    continue;
                }
                this.generalService.sendGeneralMilitaryRecruitInfo(pgm.getPlayerId(), pgm);
            }
        }
        final String parasString = SymbolUtil.toString(new String[] { String.valueOf(cityId), String.valueOf(title), String.valueOf(executionTime), String.valueOf(forcesCost) }, ";");
        final int jobId = this.jobService.addJob("cityService", "stateJob", parasString, System.currentTimeMillis() + executionTime, true);
        this.cityDao.updateJobId(cityId, jobId);
    }
    
    @Override
    public void updateTitle(final int cityId, final int newTitle) {
        final City city = this.cityDao.read(cityId);
        if (city == null) {
            return;
        }
        final Integer orignTitle = city.getTitle();
        if (orignTitle != newTitle) {
            if (newTitle == 0) {
                final Integer jobId = city.getStateJobId();
                if (jobId != null) {
                    this.jobService.cancelJob(jobId, true);
                    this.cityDao.updateJobId(cityId, -1);
                }
            }
            else {
                this.doStateJob(cityId, newTitle);
                this.dataGetter.getCityDataCache().fireCityStateMessage(cityId, city.getForceId(), LocalMessages.Message_TYPE_6, 10, 0);
            }
        }
        this.cityDao.updateTitle(cityId, newTitle);
    }
    
    @Override
    public byte[] pleaseGiveMeAReply(final PlayerDto playerDto, final int generalId) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.getMilitary(playerDto.playerId, generalId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final GeneralMoveDto gmd = getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
        if (gmd == null || StringUtils.isBlank(gmd.moveLine)) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        if (gmd.type == 2) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        final String[] split = gmd.moveLine.split(",");
        if (split.length > 0) {
            final boolean isRecruiting = pgm.getState() == 1;
            final String params = WorldCityCommon.makeTaskParam(playerDto.playerId, generalId, isRecruiting, true);
            final StringBuffer sb = new StringBuffer(params);
            sb.append(";").append(pgm.getLocationId());
            this.jobService.addJob("cityService", "changeState", sb.toString(), gmd.nextMoveTime, false);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] moveStop(final int vId) {
        final PlayerGeneralMilitary pgm = this.playerGeneralMilitaryDao.read(vId);
        if (pgm == null) {
            return JsonBuilder.getJson(State.SUCCESS, "vId");
        }
        final GeneralMoveDto gmd = getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
        if (gmd == null) {
            return JsonBuilder.getJson(State.FAIL, "");
        }
        gmd.moveLine = "";
        gmd.nextMoveTime = 0L;
        gmd.cityState = 0;
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
    public byte[] getCityDetailInfo(final int cityId, final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (WorldCityCommon.mainCityNationIdMap.get(cityId) != null) {
            doc.startObject("capitalCityInfo");
            final long now = System.currentTimeMillis();
            final long CountDown1 = this.dataGetter.getTimerBattleService().getLastCountryEAsAddTime() + 1800000L - now;
            final Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(new Date(now));
            calendar1.set(6, calendar1.get(6));
            calendar1.set(11, calendar1.get(11) + 1);
            calendar1.set(12, 0);
            calendar1.set(13, 0);
            final long nextJinWeiTime = calendar1.getTime().getTime();
            final long CountDown2 = nextJinWeiTime - now;
            doc.createElement("CountDown1", CountDown1);
            doc.createElement("CountDown2", CountDown2);
            final City city = this.cityDao.read(cityId);
            doc.createElement("nationLv", RankService.nationLv.get(city.getForceId()));
            doc.endObject();
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        int battleType = 0;
        Battle battle = null;
        if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
            battleType = 14;
            battle = NewBattleManager.getInstance().getBattleByDefId(battleType, cityId);
        }
        else {
            battleType = 3;
            battle = NewBattleManager.getInstance().getBattleByDefId(battleType, cityId);
        }
        if (battle != null) {
            doc.createElement("inBattle", true);
            final boolean canWatchBat = true;
            doc.startArray("attSide");
            for (final Map.Entry<Integer, Integer> entry : battle.attSideDetail.entrySet()) {
                if (entry.getValue() <= 0) {
                    continue;
                }
                doc.startObject();
                doc.createElement("forceId", entry.getKey());
                doc.createElement("num", entry.getValue());
                doc.endObject();
            }
            doc.endArray();
            doc.startArray("defSide");
            for (final Map.Entry<Integer, Integer> entry : battle.defSideDetail.entrySet()) {
                if (entry.getValue() <= 0) {
                    continue;
                }
                doc.startObject();
                doc.createElement("forceId", entry.getKey());
                doc.createElement("num", entry.getValue());
                doc.endObject();
                entry.getKey();
                final int forceId = playerDto.forceId;
            }
            doc.endArray();
            if (canWatchBat) {
                doc.createElement("battleId", battle.getBattleId());
                doc.createElement("targetId", battle.getDefBaseInfo().getId());
                doc.createElement("type", battle.getBattleType());
            }
        }
        else {
            doc.createElement("inBattle", false);
        }
        if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
            doc.startObject("shouMaiInfo");
            int ourManzuCityId = 0;
            switch (playerDto.forceId) {
                case 1: {
                    ourManzuCityId = 251;
                    break;
                }
                case 2: {
                    ourManzuCityId = 250;
                    break;
                }
                case 3: {
                    ourManzuCityId = 252;
                    break;
                }
                default: {
                    ErrorSceneLog.getInstance().appendErrorMsg("myForceId is invalid").append("forceId", playerDto.forceId).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).appendMethodName("getCityDetailInfo").flush();
                    return null;
                }
            }
            final Battle manzuShengJiBattle = NewBattleManager.getInstance().getBattleByDefId(14, cityId);
            if (cityId == ourManzuCityId) {
                if (manzuShengJiBattle != null) {
                    doc.createElement("type", 5);
                }
                else {
                    doc.createElement("type", 4);
                }
            }
            else {
                final CountryPrivilege countryPrivilege = WorldCityCommon.countryPrivilegeMap.get(playerDto.forceId);
                if (countryPrivilege.canShouMaiManzu) {
                    doc.createElement("type", 3);
                    final ForceInfo forceInfo = this.dataGetter.getForceInfoDao().read(playerDto.forceId);
                    int leftCount = 0;
                    int qinMinDu = 0;
                    switch (cityId) {
                        case 251: {
                            leftCount = forceInfo.getBeidiShoumaiCount();
                            qinMinDu = forceInfo.getBeidiQinmidu();
                            break;
                        }
                        case 250: {
                            leftCount = forceInfo.getXirongShoumaiCount();
                            qinMinDu = forceInfo.getXirongQinmidu();
                            break;
                        }
                        case 252: {
                            leftCount = forceInfo.getDongyiShoumaiCount();
                            qinMinDu = forceInfo.getDongyiQinmidu();
                            break;
                        }
                    }
                    final WorldPaidB worldPaidB = this.dataGetter.getWorldPaidBCache().getWorldPaidBByCountryLvAndQmd(qinMinDu);
                    final int nextId = worldPaidB.getId() + 1;
                    final WorldPaidB nextWorldPaidB = (WorldPaidB)this.dataGetter.getWorldPaidBCache().get((Object)nextId);
                    int nextQinMiDu = 0;
                    if (nextWorldPaidB != null) {
                        nextQinMiDu = nextWorldPaidB.getQ();
                    }
                    doc.createElement("qinMiDu", qinMinDu);
                    doc.createElement("manzuLv", worldPaidB.getLv());
                    doc.createElement("nextQinMiDu", nextQinMiDu);
                    doc.createElement("copper", worldPaidB.getC1());
                    doc.createElement("leftCount", leftCount);
                }
                else {
                    doc.createElement("type", 1);
                }
            }
            doc.endObject();
        }
        else {
            final City city2 = this.cityDao.read(cityId);
            doc.startArray("cityTrickState");
            if (city2 != null) {
                this.getCityTrickInfo(doc, city2, playerDto);
            }
            doc.endArray();
        }
        final Map<Integer, Long> map = this.getShaDiLingInfoInThisCity(cityId);
        if (map != null) {
            final Long shaDiLingExpireTime = map.get(playerDto.forceId);
            if (shaDiLingExpireTime != null && shaDiLingExpireTime >= System.currentTimeMillis()) {
                doc.startObject("shaDiLingNum");
                doc.createElement("countDown", shaDiLingExpireTime - System.currentTimeMillis());
                doc.endObject();
            }
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void initCountryPrivilege() {
        try {
            final List<ForceInfo> list = this.dataGetter.getForceInfoDao().getModels();
            if (list == null || list.size() == 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("ForceInfo list is empty").appendClassName("CityService").appendMethodName("initCountryPrivilege").flush();
                throw new RuntimeException("ForceInfo list is empty.");
            }
            int maxCountryLv = Integer.MIN_VALUE;
            for (final ForceInfo forceInfo : list) {
                if (forceInfo.getForceLv() > maxCountryLv) {
                    maxCountryLv = forceInfo.getForceLv();
                }
                final CountryPrivilege countryPrivilege = new CountryPrivilege();
                if (forceInfo.getForceLv() >= WorldCityCommon.MIN_MANZU_SHOUMAI_COUNTRY_LV) {
                    countryPrivilege.canShouMaiManzu = true;
                }
                else {
                    countryPrivilege.canShouMaiManzu = false;
                }
                WorldCityCommon.countryPrivilegeMap.put(forceInfo.getForceId(), countryPrivilege);
            }
            WorldCityCommon.MAX_COUNTRY_LV = maxCountryLv;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityService.initCountryPrivilege catch Exception", e);
        }
    }
    
    @Override
    public byte[] getManzuShoumaiInfo(final PlayerDto playerDto) {
        final int myForceId = playerDto.forceId;
        final ForceInfo forceInfo = this.dataGetter.getForceInfoDao().read(myForceId);
        int ourManzuCityId = 0;
        switch (myForceId) {
            case 1: {
                ourManzuCityId = 251;
                break;
            }
            case 2: {
                ourManzuCityId = 250;
                break;
            }
            case 3: {
                ourManzuCityId = 252;
                break;
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("myForceId is invalid").append("myForceId", myForceId).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).appendMethodName("getManzuShoumaiInfo").flush();
                return null;
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("manzuInfo");
        final long shouMaiTime = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId).getShoumaiManzuTime();
        final long now = System.currentTimeMillis();
        long cd = shouMaiTime - now;
        if (cd < 0L) {
            cd = 0L;
        }
        final CountryPrivilege countryPrivilege = WorldCityCommon.countryPrivilegeMap.get(myForceId);
        for (final Integer manzuCityId : WorldCityCommon.barbarainCitySet) {
            doc.startObject();
            doc.createElement("cityId", manzuCityId);
            if (manzuCityId == ourManzuCityId) {
                doc.createElement("isOurs", true);
            }
            else {
                doc.createElement("isOurs", false);
                if (countryPrivilege.canShouMaiManzu) {
                    doc.createElement("canShoumai", true);
                    final ManZuShouMaiDetail manZuShouMaiDetail = this.getManZuShouMaiDetail(forceInfo, manzuCityId);
                    final int qinMinDu = manZuShouMaiDetail.qinMiDu;
                    final int shouMaiSum = manZuShouMaiDetail.shouMaiSum;
                    final WorldPaidB worldPaidB = this.dataGetter.getWorldPaidBCache().getWorldPaidBByCountryLvAndQmd(qinMinDu);
                    if (shouMaiSum >= worldPaidB.getCm()) {
                        doc.createElement("canFadong", true);
                        doc.createElement("qinMiDu", qinMinDu);
                        doc.endObject();
                        continue;
                    }
                    final int percent = 100 * shouMaiSum / worldPaidB.getCm();
                    if (cd >= worldPaidB.getCdm() * 60000L) {
                        doc.createElement("cd", cd);
                        doc.createElement("percent", percent);
                    }
                    else {
                        doc.createElement("copper", worldPaidB.getC1());
                        doc.createElement("cd", cd);
                        doc.createElement("percent", percent);
                    }
                }
                else {
                    doc.createElement("canShoumai", false);
                }
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public ManZuShouMaiDetail getManZuShouMaiDetail(final ForceInfo forceInfo, final int manZuCapitalId) {
        final ManZuShouMaiDetail result = new ManZuShouMaiDetail();
        switch (manZuCapitalId) {
            case 251: {
                result.leftCount = forceInfo.getBeidiShoumaiCount();
                result.qinMiDu = forceInfo.getBeidiQinmidu();
                result.shouMaiSum = forceInfo.getBeidiShoumaiSum();
                return result;
            }
            case 250: {
                result.leftCount = forceInfo.getXirongShoumaiCount();
                result.qinMiDu = forceInfo.getXirongQinmidu();
                result.shouMaiSum = forceInfo.getXirongShoumaiSum();
                return result;
            }
            case 252: {
                result.leftCount = forceInfo.getDongyiShoumaiCount();
                result.qinMiDu = forceInfo.getDongyiQinmidu();
                result.shouMaiSum = forceInfo.getDongyiShoumaiSum();
                return result;
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("cityId is not manzu capital").append("cityId", manZuCapitalId).appendMethodName("getManZuShouMaiDetail").appendClassName("CityService").flush();
                return null;
            }
        }
    }
    
    @Override
    public byte[] manzuShoumai(final PlayerDto playerDto, final int cityId) {
        if (!WorldCityCommon.barbarainCitySet.contains(cityId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_00000);
        }
        int ourManZuCapital = 0;
        switch (playerDto.forceId) {
            case 1: {
                ourManZuCapital = 251;
                break;
            }
            case 2: {
                ourManZuCapital = 250;
                break;
            }
            case 3: {
                ourManZuCapital = 252;
                break;
            }
        }
        if (ourManZuCapital == cityId) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_COMM_00000) + ":\u4e0d\u53ef\u6536\u4e70\u672c\u56fd\u86ee\u65cf");
        }
        final CountryPrivilege countryPrivilege = WorldCityCommon.countryPrivilegeMap.get(playerDto.forceId);
        if (!countryPrivilege.canShouMaiManzu) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_COMM_00000) + "\u56fd\u5bb6\u7279\u6743\u672a\u5f00\u542f");
        }
        final ForceInfo forceInfo = this.dataGetter.getForceInfoDao().read(playerDto.forceId);
        final ManZuShouMaiDetail manZuShouMaiDetail = this.getManZuShouMaiDetail(forceInfo, cityId);
        final WorldPaidB worldPaidB = this.dataGetter.getWorldPaidBCache().getWorldPaidBByCountryLvAndQmd(manZuShouMaiDetail.qinMiDu);
        if (manZuShouMaiDetail.shouMaiSum >= worldPaidB.getCm()) {
            if (manZuShouMaiDetail.shouMaiSum > worldPaidB.getCm()) {
                ErrorSceneLog.getInstance().appendErrorMsg("manZuShouMaiDetail.shouMaiSum error").append("forceId", forceInfo.getForceId()).append("cityId", cityId).append("shouMaiSum", manZuShouMaiDetail.shouMaiSum).append("qinMiDu", manZuShouMaiDetail.qinMiDu).append("worldPaidB id", worldPaidB.getId()).append("worldPaidB.getCm()", worldPaidB.getCm()).appendMethodName("manzuShoumai").flush();
            }
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MANZU_SHOUMAI_FAIL_DUE_TO_SHOUMAISUM);
        }
        long shouMaiTime = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId).getShoumaiManzuTime();
        final long now = System.currentTimeMillis();
        if (shouMaiTime < now) {
            shouMaiTime = now;
        }
        final long cd = shouMaiTime - now;
        if (cd >= worldPaidB.getCdm() * 60000L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MANZU_SHOUMAI_FAIL_DUE_TO_CD);
        }
        final int shouMaiCopper = worldPaidB.getC1();
        final boolean consumeCopperDone = this.dataGetter.getPlayerResourceDao().consumeCopper(playerDto.playerId, shouMaiCopper, "\u6536\u4e70\u86ee\u65cf\u82b1\u8d39\u94f6\u5e01");
        if (!consumeCopperDone) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
        }
        final long newShouMaiTime = shouMaiTime + worldPaidB.getCd() * 60000L;
        this.dataGetter.getPlayerBattleAttributeDao().updateShouMaiManZuTime(playerDto.playerId, newShouMaiTime);
        final int addExp = worldPaidB.getCe();
        this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerDto.playerId, addExp, "\u6536\u4e70\u86ee\u65cf\u589e\u52a0\u7ecf\u9a8c");
        this.dataGetter.getForceInfoDao().addShouMaiSum(cityId, forceInfo.getForceId(), shouMaiCopper);
        final int newShouMaiSum = manZuShouMaiDetail.shouMaiSum + shouMaiCopper;
        if (newShouMaiSum >= worldPaidB.getCm()) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("type", 1);
            doc.endObject();
            String string = null;
            switch (playerDto.forceId) {
                case 1: {
                    string = ChatType.WORLD_1.toString();
                    break;
                }
                case 2: {
                    string = ChatType.WORLD_2.toString();
                    break;
                }
                case 3: {
                    string = ChatType.WORLD_3.toString();
                    break;
                }
            }
            final Group group = GroupManager.getInstance().getGroup(string);
            if (group != null) {
                final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_BARBARAIN_FADONG.getModule(), doc.toByte()));
                group.notify(WrapperUtil.wrapper(PushCommand.PUSH_BARBARAIN_FADONG.getCommand(), 0, bytes));
            }
            else {
                ErrorSceneLog.getInstance().appendErrorMsg("group is null").append("string", string).appendMethodName("faDongmanzu").appendClassName("CityService").flush();
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("addNum", shouMaiCopper);
        doc.createElement("exp", addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getCoverManzuShoumaiCdCost(final PlayerDto playerDto) {
        int gold = 0;
        final long shouMaiTime = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId).getShoumaiManzuTime();
        final long now = System.currentTimeMillis();
        final long cd = shouMaiTime - now;
        if (cd > 0L) {
            final Chargeitem chargeitem = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)64);
            final int part1 = (int)Math.ceil(cd / (chargeitem.getParam() * 60000.0));
            gold = part1 * chargeitem.getCost();
        }
        else {
            gold = 0;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", gold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] coverManzuShoumaiCd(final PlayerDto playerDto) {
        int gold = 0;
        final long shouMaiTime = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId).getShoumaiManzuTime();
        final long now = System.currentTimeMillis();
        final long cd = shouMaiTime - now;
        if (cd > 0L) {
            final Chargeitem chargeitem = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)64);
            final int part1 = (int)Math.ceil(cd / (chargeitem.getParam() * 60000.0));
            gold = part1 * chargeitem.getCost();
        }
        else {
            gold = 0;
        }
        final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
        if (!this.dataGetter.getPlayerDao().consumeGold(player, gold, "\u6536\u4e70\u86ee\u65cf\u79d2cd\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final int done = this.dataGetter.getPlayerBattleAttributeDao().updateShouMaiManZuTime(playerDto.playerId, now);
        if (done != 1) {
            ErrorSceneLog.getInstance().appendErrorMsg("updateShouMaiManZuTime failed").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("now", now).appendMethodName("faDongmanzu").appendClassName("CityService").flush();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("done", done);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] faDongmanzu(final PlayerDto playerDto, final int cityId) {
        if (!WorldCityCommon.barbarainCitySet.contains(cityId)) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_COMM_00000) + ":\u4e0d\u662f\u86ee\u65cf\u90fd\u57ce");
        }
        int ourManZuCapital = 0;
        switch (playerDto.forceId) {
            case 1: {
                ourManZuCapital = 251;
                break;
            }
            case 2: {
                ourManZuCapital = 250;
                break;
            }
            case 3: {
                ourManZuCapital = 252;
                break;
            }
        }
        if (ourManZuCapital == cityId) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_COMM_00000) + ":\u4e0d\u53ef\u53d1\u52a8\u672c\u56fd\u86ee\u65cf");
        }
        final CountryPrivilege countryPrivilege = WorldCityCommon.countryPrivilegeMap.get(playerDto.forceId);
        if (!countryPrivilege.canShouMaiManzu) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MANZU_SHOUMAI_NO_PRIVILEGE_CAN_NOT_FADONG);
        }
        final PlayerOfficeRelative por = this.dataGetter.getPlayerOfficeRelativeDao().read(playerDto.playerId);
        final int officerId = (por == null || por.getOfficerId() == null) ? 37 : por.getOfficerId();
        if (!this.dataGetter.getHallsCache().getTokenList().contains(officerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MANZU_SHOUMAI_ONLY_KING_CAN_FADONG);
        }
        final ForceInfo forceInfo = this.dataGetter.getForceInfoDao().read(playerDto.forceId);
        final ManZuShouMaiDetail manZuShouMaiDetail = this.getManZuShouMaiDetail(forceInfo, cityId);
        if (manZuShouMaiDetail.leftCount <= 0) {
            if (manZuShouMaiDetail.leftCount < 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("manZuShouMaiDetail.leftCount error").append("forceId", forceInfo.getForceId()).append("cityId", cityId).append("leftCount", manZuShouMaiDetail.leftCount).appendMethodName("faDongmanzu").flush();
            }
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MANZU_FADONG_FAIL_DUE_TO_LEFT_COUNT);
        }
        final int qinMinDu = manZuShouMaiDetail.qinMiDu;
        final int shouMaiSum = manZuShouMaiDetail.shouMaiSum;
        final WorldPaidB worldPaidB = this.dataGetter.getWorldPaidBCache().getWorldPaidBByCountryLvAndQmd(qinMinDu);
        if (shouMaiSum < worldPaidB.getCm()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MANZU_SHOUMAI_LITTLE_SHOUMAI_CAN_NOT_FADONG);
        }
        final int maxQmd = this.dataGetter.getWorldPaidBCache().getMaxQmdByKl(forceInfo.getForceLv());
        this.dataGetter.getForceInfoDao().decreaseLeftCountAndAddQmdAndResetShouMaiSum(cityId, forceInfo, maxQmd);
        this.dataGetter.getTimerBattleService().manZuExpeditionAddAfterFaDong(forceInfo, worldPaidB, cityId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("type", 2);
        doc.endObject();
        String string = null;
        switch (playerDto.forceId) {
            case 1: {
                string = ChatType.WORLD_1.toString();
                break;
            }
            case 2: {
                string = ChatType.WORLD_2.toString();
                break;
            }
            case 3: {
                string = ChatType.WORLD_3.toString();
                break;
            }
        }
        final Group group = GroupManager.getInstance().getGroup(string);
        if (group != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_BARBARAIN_FADONG.getModule(), doc.toByte()));
            group.notify(WrapperUtil.wrapper(PushCommand.PUSH_BARBARAIN_FADONG.getCommand(), 0, bytes));
        }
        else {
            ErrorSceneLog.getInstance().appendErrorMsg("group is null").append("string", string).appendMethodName("faDongmanzu").appendClassName("CityService").flush();
        }
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("done", true);
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    private Tuple<Boolean, String> canHandleCityEvent(final PlayerDto playerDto, final CityAttribute cityAttribute, final int cityId, final List<PlayerGeneralMilitary> pgmList) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final PlayerAttribute pa = this.dataGetter.getPlayerAttributeDao().read(playerDto.playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[61] != '1') {
            tuple.right = LocalMessages.T_COMM_10020;
            return tuple;
        }
        if (cityAttribute == null) {
            tuple.right = LocalMessages.CITY_EVENT_TYPE_ERROR;
            return tuple;
        }
        if (cityAttribute.countDown == -1L) {
            if (cityAttribute.leftCount <= 0) {
                tuple.right = LocalMessages.CITY_EVENT_NO_LEFT_COUNT_OR_TIMEOUT;
                return tuple;
            }
        }
        else if (System.currentTimeMillis() > cityAttribute.countDown) {
            tuple.right = LocalMessages.CITY_EVENT_NO_LEFT_COUNT_OR_TIMEOUT;
            return tuple;
        }
        final Integer myCount = cityAttribute.playerIdCountMap.get(playerDto.playerId);
        if (myCount != null && myCount >= cityAttribute.eachLimit) {
            tuple.right = LocalMessages.CITY_EVENT_EXCEED_EACH_LIMIT;
            return tuple;
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (battle != null) {
            tuple.right = LocalMessages.CITY_EVENT_IN_BATTLE_CANNOT_DEAL;
            return tuple;
        }
        if (cityAttribute.viewForceId != 0 && cityAttribute.viewForceId != playerDto.forceId) {
            tuple.right = LocalMessages.CITY_EVENT_INVISIBLE_TO_THIS_COUNTRY;
            return tuple;
        }
        boolean hasPgmInThis = false;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getLocationId() == cityId) {
                hasPgmInThis = true;
            }
        }
        if (!hasPgmInThis) {
            tuple.right = LocalMessages.CITY_EVENT_NO_PGM_IN_THIS_CITY;
            return tuple;
        }
        tuple.left = true;
        return tuple;
    }
    
    @Override
    public byte[] getCityEventPanel(final PlayerDto playerDto, final int cityId) {
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final Tuple<Boolean, String> tuple = this.canHandleCityEvent(playerDto, cityAttribute, cityId, pgmList);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        PlayerGeneralMilitary highestPgm = null;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getLocationId() == cityId && (highestPgm == null || pgm.getLv() > highestPgm.getLv())) {
                highestPgm = pgm;
            }
        }
        final General general = (General)this.dataGetter.getGeneralCache().get((Object)highestPgm.getGeneralId());
        final int eventTargetId = cityAttribute.eventTargetId;
        switch (cityAttribute.eventType) {
            case 2: {
                final WdSjEv wdSjEv = (WdSjEv)this.dataGetter.getWdSjEvCache().get((Object)eventTargetId);
                if (wdSjEv == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("wdSjEv is null").append("eventTargetId", eventTargetId).appendMethodName("dealCityEvent").appendClassName("CityService").flush();
                }
                doc.startObject("eventOrdinary");
                doc.createElement("gPic", general.getPic());
                doc.createElement("desc", wdSjEv.getDisc());
                doc.createElement("desc1", wdSjEv.getDisc1());
                doc.createElement("gold1", wdSjEv.getGoldConsume1());
                doc.createElement("reward1Type", BattleDrop.getDropType(wdSjEv.getReward1()));
                doc.createElement("reward1Num", this.getRewardNum(wdSjEv.getTaskReward1(), playerDto));
                doc.createElement("desc2", wdSjEv.getDisc2());
                doc.createElement("gold2", wdSjEv.getGoldConsume2());
                doc.createElement("reward2Type", BattleDrop.getDropType(wdSjEv.getReward2()));
                doc.createElement("reward2Num", this.getRewardNum(wdSjEv.getTaskReward2(), playerDto));
                doc.endObject();
                break;
            }
            case 1: {
                final WdSjBo wdSjBo = (WdSjBo)this.dataGetter.getWdSjBoCache().get((Object)eventTargetId);
                doc.startObject("eventFight");
                doc.createElement("gPic", general.getPic());
                doc.createElement("desc", wdSjBo.getPlot());
                doc.createElement("targetId", cityId);
                doc.createElement("type", 17);
                doc.endObject();
                break;
            }
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getRewardNum(final ITaskReward taskReward, final PlayerDto playerDto) {
        final Map<Integer, Reward> map = taskReward.getReward(playerDto, this.dataGetter, null);
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
        if (map.containsKey(5)) {
            return map.get(5).getNum();
        }
        return 0;
    }
    
    @Override
    public byte[] dealCityEvent(final PlayerDto playerDto, final int cityId, final int option) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        if (option < 1 || option > 2) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_COMM_10011) + "option:" + option);
        }
        final CityAttribute cityAttribute = CityEventManager.getInstance().cityAttributeMap.get(cityId);
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        final Tuple<Boolean, String> tuple = this.canHandleCityEvent(playerDto, cityAttribute, cityId, pgmList);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cityId", cityId);
        final int eventTargetId = cityAttribute.eventTargetId;
        switch (cityAttribute.eventType) {
            case 2: {
                final WdSjEv wdSjEv = (WdSjEv)this.dataGetter.getWdSjEvCache().get((Object)eventTargetId);
                if (wdSjEv == null) {
                    ErrorSceneLog.getInstance().appendErrorMsg("wdSjEv is null").append("eventTargetId", eventTargetId).appendMethodName("dealCityEvent").appendClassName("CityService").flush();
                }
                int gold = 0;
                ITaskReward taskReward = null;
                if (1 == option) {
                    gold = wdSjEv.getGoldConsume1();
                    taskReward = wdSjEv.getTaskReward1();
                }
                else if (2 == option) {
                    gold = wdSjEv.getGoldConsume2();
                    taskReward = wdSjEv.getTaskReward2();
                }
                if (gold > 0 && !this.playerDao.consumeGold(player, gold, "\u57ce\u6c60\u4e8b\u4ef6\u6d88\u8017\u91d1\u5e01")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_ENOUGH_GOLD);
                }
                final Map<Integer, Reward> rewardMap = taskReward.rewardPlayer(playerDto, this.dataGetter, "\u57ce\u6c60\u4e8b\u4ef6\u5956\u52b1", null);
                if (rewardMap.size() > 0) {
                    doc.startArray("eventReward");
                    for (final Integer key : rewardMap.keySet()) {
                        final Reward rd = rewardMap.get(key);
                        doc.startObject();
                        doc.createElement("type", rd.getType());
                        doc.createElement("num", rd.getNum());
                        doc.endObject();
                    }
                    doc.endArray();
                }
                cityAttribute.addPlayerCount(playerDto.playerId, option);
                CityEventManager.getInstance().pushCityEventChangeInfo(cityId);
                doc.startObject("cityEvent");
                final String dropPic = wdSjEv.getPic();
                doc.createElement("dropType", dropPic);
                final Integer gainCount = cityAttribute.playerIdCountMap.get(playerDto.playerId);
                if (gainCount != null && gainCount >= cityAttribute.eachLimit) {
                    doc.createElement("eventType", 1);
                    doc.createElement("eventCount", 0);
                }
                else if (cityAttribute.countDown == -1L) {
                    doc.createElement("eventType", 1);
                    doc.createElement("eventCount", cityAttribute.leftCount);
                }
                else {
                    doc.createElement("eventType", 2);
                    doc.createElement("countDown", cityAttribute.countDown - System.currentTimeMillis());
                }
                doc.endObject();
                break;
            }
            case 1: {
                doc.startObject("eventFight");
                doc.createElement("cityId", eventTargetId);
                doc.createElement("type", 17);
                doc.endObject();
                break;
            }
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getPlayerEventPanel(final PlayerDto playerDto, final int cityId) {
        final Map<Integer, PlayerEventObj> map = CityEventManager.getInstance().playerEventMap.get(playerDto.playerId);
        if (map == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_NO_SUCH_EVENT);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (battle != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CITY_EVENT_IN_BATTLE_CANNOT_DEAL);
        }
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        boolean hasPgmInThis = false;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getLocationId() == cityId) {
                hasPgmInThis = true;
                break;
            }
        }
        if (!hasPgmInThis) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CITY_EVENT_NO_PGM_IN_THIS_CITY);
        }
        for (final Map.Entry<Integer, PlayerEventObj> entry : map.entrySet()) {
            final Integer eventType = entry.getKey();
            final PlayerEventObj playerEventObj = entry.getValue();
            final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)eventType);
            if (playerEventObj.cityId == cityId) {
                final Tuple<List<Map<Integer, Integer>>, List<Map<Integer, Integer>>> tuple = CityEventManager.getInstance().getPlayerEventCost(playerDto.playerId, eventType, playerEventObj);
                final boolean isWorldDrama = eventType == 5;
                final boolean isLianbingling = eventType == 6;
                final boolean isTuFeiSlave = eventType == 8;
                List<Map<Integer, Integer>> List = null;
                Map<Integer, Integer> consumeMap = null;
                Map<Integer, Integer> gainMap = null;
                if (!isWorldDrama) {
                    List = tuple.left;
                    if (List == null) {
                        return null;
                    }
                    if (!isLianbingling && !isTuFeiSlave) {
                        consumeMap = List.get(0);
                        gainMap = List.get(1);
                    }
                    else {
                        gainMap = List.get(1);
                    }
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("cityId", cityId);
                doc.startObject("playerEvent");
                doc.createElement("eventType", eventType + 1000);
                String pic = wdSjp.getPic();
                if (eventType == 4) {
                    final FstNdEvent fne = (FstNdEvent)this.dataGetter.getFstNdEventCache().get((Object)playerEventObj.nationTreasureEventId);
                    pic = fne.getPic();
                    doc.createElement("greeting", fne.getGreeting());
                    doc.createElement("plot1", fne.getPlot1());
                    doc.createElement("plot2", fne.getPlot2());
                    doc.createElement("bye1", fne.getBye1());
                    doc.createElement("bye2", fne.getBye2());
                }
                else if (eventType == 5) {
                    final int dramaId = playerEventObj.worldDramaId;
                    final WdSjpDrama drama = (WdSjpDrama)this.dataGetter.getWdSjpDramaCache().get((Object)dramaId);
                    if (drama == null) {
                        continue;
                    }
                    final SoloDrama soloDrama = (SoloDrama)this.dataGetter.getSoloDramaCache().get((Object)drama.getDramaId());
                    final WorldDramaTimesCache cache = WorldDramaTimesCache.getInstatnce();
                    final int allTimes = cache.getTimesByPIDAndSIdAndGrade(playerDto.playerId, drama.getDramaId(), drama.getDifficulty());
                    int leftTimes = drama.getNumMax() - allTimes;
                    leftTimes = ((leftTimes >= 0) ? leftTimes : 0);
                    doc.createElement("name", soloDrama.getName());
                    doc.createElement("plot", soloDrama.getPlot());
                    doc.createElement("grade", drama.getDifficulty());
                    doc.createElement("leftTimes", leftTimes);
                    final Map<Integer, SoloReward> reward = this.dataGetter.getSoloRewardCache().getBySoloId(drama.getDramaId(), drama.getDifficulty());
                    final String rewardAll = this.getAllStarReward(reward);
                    final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(rewardAll);
                    if (taskReward != null) {
                        final Map<Integer, Reward> rewards = taskReward.getReward(playerDto, this.dataGetter, null);
                        doc.startArray("soloReward");
                        for (final Integer key : rewards.keySet()) {
                            final Reward single = rewards.get(key);
                            doc.startObject();
                            doc.createElement("type", single.getType());
                            doc.createElement("value", single.getNum());
                            doc.endObject();
                        }
                        doc.endArray();
                    }
                    doc.createElement("soloId", soloDrama.getId());
                }
                doc.createElement("pic", pic);
                if (!isWorldDrama) {
                    if (!isLianbingling && !isTuFeiSlave) {
                        doc.startArray("cost");
                        for (final Map.Entry<Integer, Integer> cost : consumeMap.entrySet()) {
                            doc.startObject();
                            doc.createElement("costType", cost.getKey());
                            doc.createElement("costNum", cost.getValue());
                            doc.endObject();
                        }
                        doc.endArray();
                    }
                    doc.startArray("gain");
                    for (final Map.Entry<Integer, Integer> gain : gainMap.entrySet()) {
                        doc.startObject();
                        doc.createElement("gainType", gain.getKey());
                        doc.createElement("gainNum", gain.getValue());
                        doc.endObject();
                    }
                    doc.endArray();
                }
                final List<Map<Integer, Integer>> list2 = tuple.right;
                if (list2 != null) {
                    final Map<Integer, Integer> consumeMap2 = list2.get(0);
                    final Map<Integer, Integer> gainMap2 = list2.get(1);
                    doc.startArray("cost2");
                    for (final Map.Entry<Integer, Integer> cost2 : consumeMap2.entrySet()) {
                        doc.startObject();
                        doc.createElement("costType", cost2.getKey());
                        doc.createElement("costNum", cost2.getValue());
                        doc.endObject();
                    }
                    doc.endArray();
                    doc.startArray("gain2");
                    for (final Map.Entry<Integer, Integer> gain2 : gainMap2.entrySet()) {
                        doc.startObject();
                        doc.createElement("gainType", gain2.getKey());
                        doc.createElement("gainNum", gain2.getValue());
                        doc.endObject();
                    }
                    doc.endArray();
                }
                doc.endObject();
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
        }
        return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLAYER_EVENT_NO_EVENT_THIS_CITY) + "cityId:" + cityId);
    }
    
    private String getAllStarReward(final Map<Integer, SoloReward> reward) {
        String resul = "";
        for (final Integer key : reward.keySet()) {
            final SoloReward single = reward.get(key);
            resul = RewardType.mergeRewards2(single.getReward(), resul);
        }
        return resul;
    }
    
    @Transactional
    @Override
    public byte[] dealPlayerEvent(final PlayerDto playerDto, final int cityId, final int id) {
        final Map<Integer, PlayerEventObj> map = CityEventManager.getInstance().playerEventMap.get(playerDto.playerId);
        if (map == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_NO_SUCH_EVENT);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (battle != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CITY_EVENT_IN_BATTLE_CANNOT_DEAL);
        }
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerDto.playerId);
        boolean hasPgmInThis = false;
        boolean hasPgmInFight = false;
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getState() > 1) {
                hasPgmInFight = true;
            }
            else if (TeamManager.getInstance().isJoinTeam2(playerDto.playerId, pgm.getGeneralId())) {
                hasPgmInFight = true;
            }
            else if (this.worldFarmService.isInFarmForbiddenOperation(pgm, false)) {
                hasPgmInFight = true;
            }
            if (pgm.getLocationId() == cityId) {
                hasPgmInThis = true;
                break;
            }
        }
        if (!hasPgmInThis) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CITY_EVENT_NO_PGM_IN_THIS_CITY);
        }
        boolean consumeGold = false;
        for (final Map.Entry<Integer, PlayerEventObj> entry : map.entrySet()) {
            final Integer eventType = entry.getKey();
            final PlayerEventObj playerEventObj = entry.getValue();
            if (playerEventObj.cityId != cityId) {
                continue;
            }
            final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)eventType);
            final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
            boolean needAddNext = true;
            int consumeType = 0;
            int consumeNum = 0;
            int gainType = 0;
            int gainNum = 0;
            final Map<Integer, Integer> consumeMap = new HashMap<Integer, Integer>();
            final Map<Integer, Integer> gainMap = new HashMap<Integer, Integer>();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("cityId", cityId);
            doc.startObject("playerEvent");
            doc.createElement("eventType", eventType + 1000);
            doc.createElement("pic", wdSjp.getPic());
            if (eventType == 1) {
                if (pba.getEventGemCountToday() >= wdSjp.getNumMax()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                if (pba.getEventGemCountToday() == wdSjp.getNumMax() - 1) {
                    needAddNext = false;
                }
                consumeType = 4;
                final int consumeEach = this.dataGetter.getWdSjpGemCache().getIronCost(pba.getEventGemCount());
                consumeNum = consumeEach * playerEventObj.gemBaoJiCount;
                if (!this.dataGetter.getPlayerResourceDao().consumeIron(playerDto.playerId, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u5151\u6362\u5b9d\u77f3\u6d88\u8017\u9554\u94c1")) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                }
                consumeMap.put(consumeType, consumeNum);
                gainType = 7;
                gainNum = playerEventObj.gemBaoJiCount;
                final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
                this.dataGetter.getStoreHouseService().gainGem(player, gainNum, 1, LocalMessages.T_LOG_GEM_16, null);
                gainMap.put(gainType, gainNum);
                this.dataGetter.getPlayerBattleAttributeDao().addEventGemCountToday(playerDto.playerId, gainNum);
                this.dataGetter.getPlayerBattleAttributeDao().addEventGemCount(playerDto.playerId, gainNum);
            }
            else if (eventType == 2) {
                final int jieBingNumLimit = this.dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 47);
                if (pba.getEventJiebingCountToday() >= jieBingNumLimit) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                final WdSjpHy wdSjpHy = (WdSjpHy)this.dataGetter.getWdSjpHyCache().get((Object)playerEventObj.eventId);
                gainNum = wdSjpHy.getHyNum();
                if (pba.getVip3PhantomCount() + gainNum > 30) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.REACH_FREE_PHANTOM_COUNT_MAX);
                }
                if (pba.getEventJiebingCountToday() == jieBingNumLimit - 1) {
                    needAddNext = false;
                }
                doc.createElement("leftCount", jieBingNumLimit - 1 - pba.getEventJiebingCountToday());
                consumeType = 1;
                consumeNum = wdSjpHy.getConsumeCopper();
                if (consumeNum > 0) {
                    if (!this.dataGetter.getPlayerResourceDao().consumeCopper(playerDto.playerId, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u5151\u6362\u514d\u8d39\u501f\u5175\u82b1\u8d39\u94f6\u5e01")) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                    }
                    consumeMap.put(consumeType, consumeNum);
                }
                consumeType = 2;
                consumeNum = wdSjpHy.getConsumeLumber();
                if (consumeNum > 0) {
                    if (!this.dataGetter.getPlayerResourceDao().consumeWood(playerDto.playerId, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u5151\u6362\u514d\u8d39\u501f\u5175\u6d88\u8017\u6728\u6750")) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                    }
                    consumeMap.put(consumeType, consumeNum);
                }
                consumeType = 3;
                consumeNum = wdSjpHy.getConsumeFood();
                if (consumeNum > 0) {
                    if (!this.dataGetter.getPlayerResourceDao().consumeFood(playerDto.playerId, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u5151\u6362\u514d\u8d39\u501f\u5175\u6d88\u8017\u7cae\u98df")) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                    }
                    consumeMap.put(consumeType, consumeNum);
                }
                gainType = 24;
                final int originalCount = pba.getVip3PhantomCount();
                int nowCount = originalCount + gainNum;
                if (nowCount > 30) {
                    nowCount = 30;
                }
                gainNum = nowCount - originalCount;
                this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerDto.playerId, gainNum, "\u73a9\u5bb6\u4e8b\u4ef6\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
                gainMap.put(gainType, gainNum);
                this.dataGetter.getPlayerBattleAttributeDao().addEventJiebingCountToday(playerDto.playerId, 1);
            }
            else if (eventType == 3) {
                if (pba.getEventXtysCountToday() >= wdSjp.getNumMax()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                final WdSjpXtys wdSjpXtys = (WdSjpXtys)this.dataGetter.getWdSjpXtysCache().get((Object)playerEventObj.stoneSrcEventId);
                if (pba.getEventXtysCountToday() == wdSjp.getNumMax() - 1) {
                    needAddNext = false;
                }
                doc.createElement("leftCount", wdSjp.getNumMax() - 1 - pba.getEventXtysCountToday());
                consumeType = 1;
                consumeNum = wdSjpXtys.getConsumeCopper();
                if (consumeNum > 0) {
                    if (!this.dataGetter.getPlayerResourceDao().consumeCopper(playerDto.playerId, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u5151\u6362\u7384\u94c1\u539f\u77f3\u82b1\u8d39\u94f6\u5e01")) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                    }
                    consumeMap.put(consumeType, consumeNum);
                }
                consumeType = 2;
                consumeNum = wdSjpXtys.getConsumeLumber();
                if (consumeNum > 0) {
                    if (!this.dataGetter.getPlayerResourceDao().consumeWood(playerDto.playerId, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u5151\u6362\u514d\u7384\u94c1\u539f\u77f3\u6d88\u8017\u6728\u6750")) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                    }
                    consumeMap.put(consumeType, consumeNum);
                }
                consumeType = 3;
                consumeNum = wdSjpXtys.getConsumeFood();
                if (consumeNum > 0) {
                    if (!this.dataGetter.getPlayerResourceDao().consumeFood(playerDto.playerId, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u5151\u6362\u7384\u94c1\u539f\u77f3\u6d88\u8017\u7cae\u98df")) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                    }
                    consumeMap.put(consumeType, consumeNum);
                }
                gainType = 29;
                gainNum = wdSjpXtys.getXtysNum();
                this.dataGetter.getStoreHouseService().gainItems(playerDto.playerId, gainNum, 1402, "\u73a9\u5bb6\u4e8b\u4ef6\u5151\u6362\u83b7\u5f97\u7384\u94c1\u539f\u77f3");
                gainMap.put(gainType, gainNum);
                this.dataGetter.getPlayerBattleAttributeDao().addEventXtysCountToday(playerDto.playerId);
            }
            else if (eventType == 4) {
                if (id < 1 || id > 2) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                }
                if (pba.getEventNationalTreasureCountToday() >= wdSjp.getNumMax()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                final FstNdEvent fne = (FstNdEvent)this.dataGetter.getFstNdEventCache().get((Object)playerEventObj.nationTreasureEventId);
                if (pba.getEventNationalTreasureCountToday() == wdSjp.getNumMax() - 1) {
                    needAddNext = false;
                }
                doc.createElement("leftCount", wdSjp.getNumMax() - 1 - pba.getEventNationalTreasureCountToday());
                final Player player = this.playerDao.read(playerDto.playerId);
                Tuple<Integer, Integer> tuple = null;
                String rewardStr = null;
                if (1 == id) {
                    tuple = CityEventManager.getInstance().getCostTypeAndNum(fne.getCost1());
                    rewardStr = fne.getReward1();
                }
                else {
                    tuple = CityEventManager.getInstance().getCostTypeAndNum(fne.getCost2());
                    rewardStr = fne.getReward2();
                }
                consumeType = tuple.left;
                consumeNum = tuple.right;
                if (consumeNum > 0) {
                    if (consumeType == 31) {
                        if (!this.dataGetter.getPlayerEventDao().consumeBmw(playerDto.playerId, 11, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u6d88\u8017\u5b9d\u9a6c")) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                        }
                        consumeMap.put(consumeType, consumeNum);
                    }
                    else if (consumeType == 32) {
                        if (!this.dataGetter.getPlayerEventDao().consumeXo(playerDto.playerId, 11, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u6d88\u8017\u7f8e\u9152")) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                        }
                        consumeMap.put(consumeType, consumeNum);
                    }
                    else if (consumeType == 33) {
                        if (!this.dataGetter.getPlayerEventDao().consumePicasso(playerDto.playerId, 11, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u6d88\u8017\u4e66\u753b")) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
                        }
                        consumeMap.put(consumeType, consumeNum);
                    }
                    else if (consumeType == 11) {
                        if (!this.dataGetter.getPlayerDao().consumeGold(player, consumeNum, "\u73a9\u5bb6\u4e8b\u4ef6\u6d88\u8017\u91d1\u5e01")) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
                        }
                        consumeMap.put(consumeType, consumeNum);
                        consumeGold = true;
                    }
                }
                final ITaskReward itr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                final double rate = this.dataGetter.getFstDbLveCache().getRate(player.getPlayerLv());
                final CityEventRate cer = new CityEventRate();
                cer.rate = rate;
                final Map<Integer, Reward> rMap = itr.rewardPlayer(playerDto, this.dataGetter, "\u73a9\u5bb6\u4e8b\u4ef6", cer);
                for (final Map.Entry<Integer, Reward> temp : rMap.entrySet()) {
                    gainMap.put(temp.getKey(), temp.getValue().getNum());
                }
                this.dataGetter.getPlayerBattleAttributeDao().addEventNationalTreasureCountToday(playerDto.playerId);
                this.dataGetter.getPlayerEventDao().updateParam8(playerDto.playerId, 11, playerEventObj.nationTreasureEventId);
            }
            else if (eventType == 5) {
                final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerDto.playerId);
                if (juBenDto != null) {
                    hasPgmInFight = true;
                }
                final int perDayMax = wdSjp.getNumMax();
                final int todayCount = pba.getEventWorldDramaCountToday();
                if (todayCount >= perDayMax) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                final WorldDramaTimesCache cache = WorldDramaTimesCache.getInstatnce();
                final int dramaId = playerEventObj.worldDramaId;
                final WdSjpDrama drama = (WdSjpDrama)this.dataGetter.getWdSjpDramaCache().get((Object)dramaId);
                if (drama == null) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                }
                final int allTimes = cache.getTimesByPIDAndSIdAndGrade(playerDto.playerId, drama.getDramaId(), drama.getDifficulty());
                if (allTimes >= drama.getNumMax()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                if (hasPgmInFight) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.JUBEN_CANNT_JOIN_GENREAL_BUSY);
                }
                if (pba.getEventWorldDramaCountToday() >= wdSjp.getNumMax() - 1) {
                    needAddNext = false;
                }
                doc.createElement("leftCount", perDayMax - 1 - todayCount);
                final OperationResult result = this.juBenService.enterWorldDramaScene(playerDto, drama.getDramaId(), drama.getDifficulty());
                if (!result.getResult()) {
                    return JsonBuilder.getJson(State.FAIL, result.getResultContent());
                }
                doc.createElement("soloId", drama.getDramaId());
            }
            else if (eventType == 6) {
                if (pba.getEventTrainningTokenCountToday() >= wdSjp.getNumMax()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                final WdSjpLbl wdSjplbl = (WdSjpLbl)this.dataGetter.getWdSjpLblCache().get((Object)playerEventObj.stoneSrcEventId);
                if (pba.getEventXtysCountToday() == wdSjp.getNumMax() - 1) {
                    needAddNext = false;
                }
                doc.createElement("leftCount", wdSjp.getNumMax() - 1 - pba.getEventTrainningTokenCountToday());
                gainType = 43;
                gainNum = wdSjplbl.getNum();
                this.dataGetter.getStoreHouseService().gainItems(playerDto.playerId, gainNum, 1701, "\u73a9\u5bb6\u4e8b\u4ef6\u83b7\u5f97\u7ec3\u5175\u4ee4");
                gainMap.put(gainType, gainNum);
                this.dataGetter.getPlayerBattleAttributeDao().addEventLblCountToday(playerDto.playerId);
            }
            else if (eventType == 7) {
                if (pba.getEventSdlrCountToday() >= wdSjp.getNumMax()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                final WdSjpSdlr wdSjpsdlr = (WdSjpSdlr)this.dataGetter.getWdSjpSdlrCache().get((Object)playerEventObj.eventId);
                if (pba.getEventSdlrCountToday() == wdSjp.getNumMax() - 1) {
                    needAddNext = false;
                }
                doc.createElement("leftCount", wdSjp.getNumMax() - 1 - pba.getEventXtysCountToday());
                final int type = wdSjpsdlr.getType();
                if (1 == type) {
                    gainType = 35;
                }
                else if (2 == type) {
                    gainType = 36;
                }
                else if (3 == type) {
                    gainType = 37;
                }
                gainNum = wdSjpsdlr.getNum();
                final int val = type * 1000 + gainNum;
                EventUtil.handleOperation(playerDto.playerId, 16, val);
                gainMap.put(gainType, gainNum);
                this.dataGetter.getPlayerBattleAttributeDao().addEventSdlrCountToday(playerDto.playerId);
            }
            else {
                if (eventType != 8) {
                    continue;
                }
                if (pba.getEventSlaveCountToday() >= wdSjp.getNumMax()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_REACH_COUNT_LIMIT);
                }
                if (pba.getEventXtysCountToday() == wdSjp.getNumMax() - 1) {
                    needAddNext = false;
                }
                final Slaveholder slaveholder = this.dataGetter.getSlaveholderDao().read(playerDto.playerId);
                if (slaveholder.getGrabNum() > 40) {
                    needAddNext = false;
                }
                final General general = (General)this.dataGetter.getGeneralCache().get((Object)1003);
                final StringBuilder slaveParam = new StringBuilder();
                slaveParam.append(playerDto.playerId).append("#").append(0).append("#").append(0).append("#").append(general.getId()).append("#").append(0).append("#").append(3).append("#").append(0).append("#").append(general.getName()).append("#").append(76).append("#");
                this.dataGetter.getJobService().addJob("slaveService", "dealSlave", slaveParam.toString(), System.currentTimeMillis(), true);
                gainType = 44;
                gainNum = 1;
                gainMap.put(gainType, gainNum);
                this.dataGetter.getPlayerBattleAttributeDao().addEventSlaveCountToday(playerDto.playerId);
            }
            if (eventType != 5) {
                if (eventType != 6) {
                    doc.startArray("cost");
                    for (final Map.Entry<Integer, Integer> cost : consumeMap.entrySet()) {
                        doc.startObject();
                        doc.createElement("costType", cost.getKey());
                        doc.createElement("costNum", cost.getValue());
                        doc.endObject();
                    }
                    doc.endArray();
                }
                doc.startArray("gain");
                for (final Map.Entry<Integer, Integer> gain : gainMap.entrySet()) {
                    doc.startObject();
                    doc.createElement("gainType", gain.getKey());
                    doc.createElement("gainNum", gain.getValue());
                    doc.endObject();
                }
                doc.endArray();
            }
            doc.endObject();
            doc.endObject();
            map.remove(eventType);
            if (needAddNext && eventType != 5) {
                this.addPlayerNextEvent(playerDto.playerId, playerDto.forceId, eventType, consumeGold);
            }
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.PLAYER_EVENT_NO_EVENT_THIS_CITY);
    }
    
    @Override
    public void addPlayerNextEvent(final int playerId, final int forceId, final Integer eventType, final boolean consumeGold) {
        try {
            final String params = String.valueOf(playerId) + "#" + forceId + "#" + eventType;
            final WdSjp wdSjp = (WdSjp)this.dataGetter.getWdSjpCache().get((Object)eventType);
            final int scale = wdSjp.getIntervalMax() - wdSjp.getIntervalMin();
            int timeInSec = wdSjp.getIntervalMin() + WebUtil.nextInt(scale);
            if (consumeGold) {
                timeInSec = 0;
            }
            final long executionTime = System.currentTimeMillis() + timeInSec * 1000L;
            this.jobService.addJob("cityService", "addPlayerEvent", params, executionTime);
        }
        catch (Exception e) {
            CityService.errorLog.error(this, e);
        }
    }
    
    @Override
    public void addPlayerEvent(final String params) {
        try {
            final String[] array = params.split("#");
            if (array.length != 3) {
                ErrorSceneLog.getInstance().appendErrorMsg("params is invalid").append("params", params).appendClassName("CityService").appendMethodName("addPlayerEvent").flush();
                return;
            }
            final int playerId = Integer.parseInt(array[0]);
            final int eventType = Integer.parseInt(array[2]);
            if (!this.inValidTimeWindow(eventType)) {
                ErrorSceneLog.getInstance().appendErrorMsg("out of time window").append("params", params).appendClassName("CityService").appendMethodName("addPlayerEvent").flush();
                return;
            }
            final boolean add = CityEventManager.getInstance().addPlayerEvent(playerId, eventType);
            if (add) {
                CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, eventType);
            }
            else {
                final Player player = this.dataGetter.getPlayerDao().read(playerId);
                this.dataGetter.getCityService().addPlayerNextEvent(playerId, player.getForceId(), eventType, false);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityService.addPlayerEvent Exception", e);
        }
    }
    
    private boolean inValidTimeWindow(final int eventType) {
        switch (eventType) {
            case 1:
            case 2: {
                if (!TimeUtil.in0To8()) {
                    return true;
                }
            }
            case 3: {
                if (!TimeUtil.in0To8()) {
                    return true;
                }
            }
            case 4: {
                if (EventUtil.isEventTime(11)) {
                    return true;
                }
            }
            case 5: {
                if (!TimeUtil.in0To8()) {
                    return true;
                }
                return true;
            }
            case 6: {
                return true;
            }
            case 7: {
                if (EventUtil.isEventTime(16)) {
                    return true;
                }
                return true;
            }
            case 8: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public void clearPlayerEventPerDay() {
        try {
            final long start = System.currentTimeMillis();
            this.dataGetter.getPlayerBattleAttributeDao().resetEventNumToday();
            CityEventManager.getInstance().removePlayerEventByEventType(1);
            CityEventManager.getInstance().removePlayerEventByEventType(2);
            CityEventManager.getInstance().removePlayerEventByEventType(3);
            CityEventManager.getInstance().removePlayerEventByEventType(5);
            CityEventManager.getInstance().removePlayerEventByEventType(6);
            if (EventUtil.isEventTime(16)) {
                CityEventManager.getInstance().removePlayerEventByEventType(7);
                final Tuple<Integer, Integer> tuple = this.dataGetter.getWdSjpCache().timeWindowtMap.get(7);
                if (CityEventManager.getInstance().isInPlayerEventTimeWindow(tuple.left, tuple.right)) {
                    CityEventManager.getInstance().addFirstRoundPlayerEvent(7);
                }
            }
            CityEventManager.getInstance().addTuFeiSlaveEventForOnLinePlayersAtZeroOclockAfterReset();
            CityService.timerLog.info(LogUtil.formatThreadLog("CityService", "clearPlayerEventPerDay", 2, System.currentTimeMillis() - start, ""));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("CityService.addPlayerEvent Exception", e);
        }
    }
    
    @Override
    public void addPlayerEventPerDay() {
        try {
            final long start = System.currentTimeMillis();
            for (final Integer eventType : this.dataGetter.getWdSjpCache().getCacheMap().keySet()) {
                try {
                    if (eventType == 4 || eventType == 7 || eventType == 8) {
                        continue;
                    }
                    CityEventManager.getInstance().addFirstRoundPlayerEvent(eventType);
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error("CityService.addPlayerEventPerDay Exception eventType " + eventType, e);
                }
            }
            CityService.timerLog.info(LogUtil.formatThreadLog("CityService", "addPlayerEventPerDay", 2, System.currentTimeMillis() - start, ""));
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error("CityService.addPlayerEvent Exception", e2);
        }
    }
    
    @Override
    public Map<Integer, Long> getShaDiLingInfoInThisCity(final int cityId) {
        final City city = this.dataGetter.getCityDao().read(cityId);
        return this.getShaDiLingInfoInThisCity(city);
    }
    
    @Override
    public Map<Integer, Long> getShaDiLingInfoInThisCity(final City city) {
        if (city == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("city is null").appendClassName("BattleService").appendMethodName("useKillToken").flush();
            return null;
        }
        if (city.getOtherInfo() == null || city.getOtherInfo().trim().isEmpty()) {
            return null;
        }
        final Map<Integer, Long> resultMap = new HashMap<Integer, Long>();
        final String[] array = city.getOtherInfo().split(";");
        String[] array2;
        for (int length = (array2 = array).length, i = 0; i < length; ++i) {
            final String s = array2[i];
            final String[] aArray = s.split(",");
            try {
                final int type = Integer.parseInt(aArray[0]);
                if (type == 1) {
                    final int forceId = Integer.parseInt(aArray[1]);
                    final Long expireTime = Long.parseLong(aArray[2]);
                    resultMap.put(forceId, expireTime);
                }
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("BattleService.useKillToken parse catch Exception", e);
                return null;
            }
        }
        return resultMap;
    }
    
    @Override
    public Long updateShaDiLingInfoInThisCity(final int cityId, final int forceId) {
        try {
            final long start = System.currentTimeMillis();
            CityService.timerLog.info(LogUtil.formatThreadLog("CityService", "updateShaDiLingInfoInThisCity", 0, 0L, "cityId:" + cityId + ",forceId:" + forceId));
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (city == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city is null").append("cityId", cityId).appendClassName("CityService").appendMethodName("updateShaDiLingInfoInThisCity").flush();
                return null;
            }
            final StringBuilder sb = new StringBuilder();
            Long expireTime = null;
            boolean needAppend = true;
            if (city.getOtherInfo() == null || city.getOtherInfo().trim().isEmpty()) {
                needAppend = true;
            }
            else {
                final String[] array = city.getOtherInfo().split(";");
                String[] array2;
                for (int length = (array2 = array).length, i = 0; i < length; ++i) {
                    final String s = array2[i];
                    final String[] aArray = s.split(",");
                    try {
                        final int type = Integer.parseInt(aArray[0]);
                        if (type != 1) {
                            sb.append(s).append(";");
                        }
                        else {
                            final int forceTemp = Integer.parseInt(aArray[1]);
                            if (forceTemp == forceId) {
                                expireTime = Long.parseLong(aArray[2]);
                                if (expireTime < System.currentTimeMillis()) {
                                    expireTime = System.currentTimeMillis();
                                }
                                expireTime += 1800000L;
                                sb.append(1).append(",").append(forceId).append(",").append(expireTime).append(";");
                                needAppend = false;
                                break;
                            }
                            sb.append(s).append(";");
                        }
                    }
                    catch (Exception e) {
                        ErrorSceneLog.getInstance().error("CityService.updateShaDiLingInfoInThisCity parse catch Exception", e);
                        return null;
                    }
                }
            }
            if (needAppend) {
                expireTime = System.currentTimeMillis() + 1800000L;
                sb.append(1).append(",").append(forceId).append(",").append(expireTime).append(";");
            }
            CityService.timerLog.info("CityDao.updateOtherInfo. city.getOtherInfo():" + sb.toString());
            CityService.timerLog.info(LogUtil.formatThreadLog("CityService", "updateShaDiLingInfoInThisCity", 2, System.currentTimeMillis() - start, "cityId:" + cityId + ",forceId:" + forceId));
            this.dataGetter.getCityDao().updateOtherInfo(cityId, sb.toString());
            return expireTime;
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error("CityService.updateShaDiLingInfoInThisCity catch Exception", e2);
            return null;
        }
    }
    
    @Override
    public void removeExpiredShaDiLingInfoInThisCity(final String param) {
        final long start = System.currentTimeMillis();
        try {
            CityService.timerLog.info(LogUtil.formatThreadLog("CityService", "removeExpiredShaDiLingInfoInThisCity", 0, 0L, "param:" + param));
            final int cityId = Integer.parseInt(param);
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (city == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("city is null").append("cityId", cityId).appendClassName("BattleService").appendMethodName("useKillToken").flush();
                CityService.timerLog.info(LogUtil.formatThreadLog("CityService", "removeExpiredShaDiLingInfoInThisCity", 2, System.currentTimeMillis() - start, "param:" + param));
                return;
            }
            if (city.getOtherInfo() == null || city.getOtherInfo().trim().isEmpty()) {
                CityService.timerLog.info(LogUtil.formatThreadLog("CityService", "removeExpiredShaDiLingInfoInThisCity", 2, System.currentTimeMillis() - start, "param:" + param));
                return;
            }
            final StringBuilder sb = new StringBuilder();
            boolean needupdate = false;
            final String[] array = city.getOtherInfo().split(";");
            String[] array2;
            for (int length = (array2 = array).length, i = 0; i < length; ++i) {
                final String s = array2[i];
                final String[] aArray = s.split(",");
                try {
                    final int type = Integer.parseInt(aArray[0]);
                    if (type != 1) {
                        sb.append(s).append(";");
                    }
                    else {
                        final Long expireTime = Long.parseLong(aArray[2]);
                        if (expireTime > System.currentTimeMillis()) {
                            sb.append(s).append(";");
                        }
                        else {
                            needupdate = true;
                        }
                    }
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error("BattleService.removeExpireShaDiLingInfoInThisCity parse catch Exception", e);
                    return;
                }
            }
            if (needupdate) {
                CityService.timerLog.info("CityDao.updateOtherInfo. city.getOtherInfo():" + sb.toString());
                this.dataGetter.getCityDao().updateOtherInfo(cityId, sb.toString());
            }
            CityService.timerLog.info(LogUtil.formatThreadLog("CityService", "removeExpiredShaDiLingInfoInThisCity", 2, System.currentTimeMillis() - start, "param:" + param));
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().error("BattleService.removeExpireShaDiLingInfoInThisCity catch Exception", e2);
        }
    }
    
    @Override
    public byte[] getFarmCityInfo(final PlayerDto playerDto) {
        final Integer cityId = WorldFarmCache.forceCityIdMap.get(playerDto.forceId);
        if (cityId == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final ForceInfo forceInfo = this.dataGetter.getForceInfoDao().read(playerDto.forceId);
        final WorldFarmCache cache = WorldFarmCache.getInstatnce();
        final long cd = cache.getCdByPlayerId(playerDto.playerId);
        int percentage = 100;
        final int lv = forceInfo.getLv();
        final Farm nextFarm = (Farm)this.farmCache.get((Object)(lv + 1));
        if (nextFarm != null) {
            final int upperCopper = nextFarm.getUpCopper();
            percentage = (int)(forceInfo.getFarmInvestSum() / upperCopper * 100.0);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("cd", cd - System.currentTimeMillis());
        doc.createElement("lv", forceInfo.getLv());
        doc.createElement("percentage", percentage);
        doc.createElement("copper", 10000);
        doc.createElement("maxCd", 3600000);
        doc.createElement("isFullLv", this.farmCache.getMaxLv() == lv);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void initPlayerWorld() {
        final List<PlayerWorld> list = this.dataGetter.getPlayerWorldDao().getModels();
        for (final PlayerWorld pw : list) {
            final Player player = this.dataGetter.getPlayerDao().read(pw.getPlayerId());
            final Set<Integer> attedSet = new HashSet<Integer>();
            final Set<Integer> canAttSet = new HashSet<Integer>();
            if (!StringUtils.isBlank(pw.getCanAttId()) && pw.getAttedId() != null) {
                final String[] ids = pw.getAttedId().split(",");
                String[] array;
                for (int length = (array = ids).length, i = 0; i < length; ++i) {
                    final String str = array[i];
                    attedSet.add(Integer.valueOf(str));
                }
                if (pw.getCanAttId() == null) {
                    continue;
                }
                final String[] caIds = pw.getCanAttId().split(",");
                String[] array2;
                for (int length2 = (array2 = caIds).length, j = 0; j < length2; ++j) {
                    final String str2 = array2[j];
                    try {
                        canAttSet.add(Integer.valueOf(str2));
                    }
                    catch (Exception e) {
                        CityService.errorLog.error("XXXX:" + str2, e);
                    }
                }
                int cityId = 0;
                String[] array3;
                for (int length3 = (array3 = caIds).length, k = 0; k < length3; ++k) {
                    final String str3 = array3[k];
                    try {
                        cityId = Integer.valueOf(str3);
                        final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
                        final int attackingArea = this.dataGetter.getWorldCityCache().getArea(player.getForceId(), wc);
                        final List<WorldCity> wcList = this.dataGetter.getWorldCityCache().getAreaCity(player.getForceId(), attackingArea);
                        for (final WorldCity wcSub : wcList) {
                            if (attedSet.contains(wcSub.getId())) {
                                attedSet.remove(wcSub.getId());
                            }
                            canAttSet.add(wcSub.getId());
                        }
                    }
                    catch (Exception e2) {
                        CityService.errorLog.error("XXXX:" + str3, e2);
                    }
                }
                final StringBuilder attedSb = new StringBuilder();
                for (final Integer key : attedSet) {
                    attedSb.append(key).append(",");
                }
                final StringBuilder canAttSb = new StringBuilder();
                for (final Integer key2 : canAttSet) {
                    canAttSb.append(key2).append(",");
                }
                this.dataGetter.getPlayerWorldDao().updateAttInfo(pw.getPlayerId(), attedSb.toString(), canAttSb.toString());
            }
        }
    }
}
