package com.reign.gcld.battle.service;

import org.springframework.stereotype.*;
import org.apache.log4j.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.grouparmy.dao.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.pay.dao.*;
import com.reign.plugin.yx.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.common.event.*;
import com.reign.framework.json.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.sdata.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.rank.common.*;
import org.springframework.transaction.annotation.*;
import com.reign.util.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.pay.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.grouparmy.domain.*;
import java.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.phantom.common.*;
import org.apache.commons.lang.*;

@Component("occupyService")
public class OccupyService implements IOccupyService
{
    private static final Logger occupyLogger;
    @Autowired
    private IOfficerBuildingInfoDao officerBuildingInfoDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private OfficialCache officialCache;
    @Autowired
    private ArmyCache armyCache;
    @Autowired
    private IRankService rankService;
    @Autowired
    private IPlayerOfficerBuildingDao playerOfficerBuildingDao;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerGroupArmyDao playerGroupArmyDao;
    @Autowired
    private IGroupArmyDao groupArmyDao;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private IOfficerTokenDao officerTokenDao;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private PlayerVipTxDao playerVipTxDao;
    @Autowired
    private IYxOperation yxOperation;
    private static final TimerLogger timerLog;
    private final Object[][] buildingLocks;
    
    static {
        occupyLogger = Logger.getLogger(OccupyService.class);
        timerLog = new TimerLogger();
    }
    
    public OccupyService() {
        this.buildingLocks = new Object[3][40];
        for (int forceId = 0; forceId < 3; ++forceId) {
            for (int i = 0; i < 40; ++i) {
                this.buildingLocks[forceId][i] = new Object();
            }
        }
    }
    
    @Override
    public byte[] getAllOfficerBuilding(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final List<OfficerBuildingInfo> obiList = this.officerBuildingInfoDao.getByForceId(playerDto.forceId);
        final Map<Integer, OfficerBuildingInfo> map = new HashMap<Integer, OfficerBuildingInfo>();
        for (final OfficerBuildingInfo obi : obiList) {
            map.put(obi.getBuildingId(), obi);
        }
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerDto.playerId);
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        Halls mineHalls = null;
        if (por != null) {
            mineHalls = (Halls)this.hallsCache.get((Object)por.getOfficerId());
            doc.createElement("reward", por.getSalaryGotToday());
        }
        doc.startArray("buildings");
        long protectTime = 0L;
        int buildingId = 0;
        for (int i = 0; i < 40; ++i) {
            buildingId = i + 1;
            if (buildingId < 18 || buildingId > 20) {
                final OfficerBuildingInfo obi2 = map.get(buildingId);
                doc.startObject();
                doc.createElement("buildingId", buildingId);
                doc.createElement("maxMemberNum", 2);
                final Battle battle = NewBattleManager.getInstance().getBattleByDefId(4, buildingId);
                if (battle != null) {
                    if (battle.getDefBaseInfo().getForceId() == playerDto.forceId || battle.getAttBaseInfo().getForceId() == playerDto.forceId) {
                        doc.createElement("inBattle", 1);
                    }
                    else {
                        doc.createElement("inBattle", 2);
                    }
                    final int attPlayerId = battle.getAttBaseInfo().getId();
                    doc.createElement("attName", this.playerDao.read(attPlayerId).getPlayerName());
                    final int defPlayerId = battle.getDefBaseInfo().getDefChiefId();
                    final boolean isNpc = battle.isNpc();
                    if (isNpc) {
                        final General general = (General)this.generalCache.get((Object)defPlayerId);
                        doc.createElement("defName", general.getName());
                    }
                    else {
                        final Player defPlayer = this.playerDao.read(defPlayerId);
                        if (defPlayer != null) {
                            doc.createElement("defName", defPlayer.getPlayerName());
                        }
                        else {
                            OccupyService.occupyLogger.error("battle.getDefBaseInfo().getDefChiefId():" + defPlayerId);
                            doc.createElement("defName", "npc");
                        }
                    }
                    doc.createElement("canJoin", battle.isBattleDoing());
                }
                else {
                    doc.createElement("inBattle", 0);
                }
                boolean canBattle = false;
                if (battle == null && obi2 == null) {
                    canBattle = true;
                }
                if (obi2 != null) {
                    doc.createElement("isOccupy", true);
                    protectTime = 3600000L - (System.currentTimeMillis() - obi2.getOccupyTime().getTime());
                    if (protectTime <= 0L) {
                        canBattle = true;
                    }
                    doc.createElement("currentMemberNum", obi2.getMemberCount());
                    final Player ownerPlayer = this.playerDao.read(obi2.getPlayerId());
                    if (ownerPlayer != null) {
                        doc.createElement("owner", ownerPlayer.getPlayerName());
                        doc.createElement("forceId", ownerPlayer.getForceId());
                    }
                }
                else {
                    doc.createElement("isOccupy", false);
                    final ChiefNpc cn = this.hallsCache.getChiefNpc(buildingId, 1);
                    final Army army = (Army)this.armyCache.get((Object)cn.getCheif());
                    doc.createElement("currentMemberNum", 0);
                    doc.createElement("owner", army.getName());
                    doc.createElement("forceId", 0);
                }
                final Halls halls = this.hallsCache.getHalls(buildingId, 1);
                final Halls normalHalls = this.hallsCache.getHalls(buildingId, 2);
                doc.createElement("occupyBuildingName", BattleConstant.getBuildingName(halls, playerDto.forceId));
                doc.createElement("pin", halls.getQuality());
                doc.createElement("maxAddition", halls.getOutput());
                doc.createElement("normalAddition", normalHalls.getOutput());
                if (pob != null && pob.getBuildingId() == buildingId && pob.getState() == 1) {
                    final int nowOfficerId = por.getOfficerId();
                    doc.createElement("ownAddition", ((Halls)this.hallsCache.get((Object)nowOfficerId)).getOutput());
                }
                doc.createElement("officerName", halls.getNameList());
                doc.createElement("normalOfficerName", normalHalls.getNameList());
                doc.createElement("protectTime", (protectTime < 0L) ? 0L : protectTime);
                doc.createElement("canBattle", canBattle);
                doc.endObject();
            }
        }
        for (int id = 18; id <= 19; ++id) {
            doc.startObject();
            doc.createElement("buildingId", id);
            doc.createElement("maxMemberNum", 1);
            doc.createElement("inBattle", 0);
            if (pob != null && pob.getBuildingId() == id) {
                doc.createElement("isOccupy", true);
                doc.createElement("currentMemberNum", 1);
                final Player ownerPlayer2 = this.playerDao.read(pob.getPlayerId());
                if (ownerPlayer2 != null) {
                    doc.createElement("owner", ownerPlayer2.getPlayerName());
                    doc.createElement("forceId", ownerPlayer2.getForceId());
                }
            }
            else {
                doc.createElement("isOccupy", false);
                final ChiefNpc cn2 = this.hallsCache.getChiefNpc(id, 1);
                final Army army2 = (Army)this.armyCache.get((Object)cn2.getCheif());
                doc.createElement("currentMemberNum", 0);
                doc.createElement("owner", army2.getName());
                doc.createElement("forceId", 0);
            }
            final Halls halls2 = this.hallsCache.getHalls(id, 1);
            doc.createElement("occupyBuildingName", BattleConstant.getBuildingName(halls2, playerDto.forceId));
            doc.createElement("pin", halls2.getQuality());
            doc.createElement("maxAddition", halls2.getOutput());
            if (pob != null && pob.getBuildingId() == id && pob.getState() == 1) {
                final int nowOfficerId2 = por.getOfficerId();
                doc.createElement("ownAddition", ((Halls)this.hallsCache.get((Object)nowOfficerId2)).getOutput());
            }
            doc.createElement("officerName", halls2.getNameList());
            doc.endObject();
        }
        doc.endArray();
        if (pob != null) {
            doc.createElement("isNew", pob.getIsNew());
            if (pob.getIsNew() == 1) {
                this.playerOfficerBuildingDao.updateIsNew(pob.getPlayerId(), 0);
            }
            if (por.getOfficerId() != null && por.getOfficerId() != 37) {
                doc.createElement("myBuildingId", pob.getBuildingId());
            }
        }
        if (mineHalls != null) {
            final long now = System.currentTimeMillis();
            long remainTime = 0L;
            int pri = 0;
            if (por != null) {
                final Date reputationDate = por.getReputationTime();
                remainTime = ((reputationDate == null) ? 0L : (reputationDate.getTime() - now));
                pri = ((por.getLastOfficerId() == null) ? 0 : por.getLastOfficerId());
            }
            doc.createElement("inReputationTime", remainTime > 0L);
            doc.createElement("myOfficerName", mineHalls.getPic());
            doc.startArray("resourceAddition");
            for (int resourceType = 1; resourceType <= 4; ++resourceType) {
                doc.startObject();
                doc.createElement("resourceType", resourceType);
                doc.createElement("count", this.buildingOutputCache.getOfficersOutput(playerId, resourceType));
                doc.endObject();
            }
            int hyT = mineHalls.getHyT();
            final int pin = mineHalls.getOfficialId();
            if (pin <= 5) {
                final Date occupyTime = por.getOccupyOfficialTime();
                if (occupyTime != null) {
                    doc.startObject();
                    doc.createElement("resourceType", 6);
                    long count = now - occupyTime.getTime();
                    count = Math.min(count / 2L, 604800000L);
                    doc.createElement("count", (count >= 3600000L) ? count : 0L);
                    doc.endObject();
                }
            }
            else if (remainTime > 0L) {
                doc.startObject();
                doc.createElement("resourceType", 6);
                doc.createElement("count", remainTime);
                doc.endObject();
                final Halls reputation = (Halls)this.hallsCache.get((Object)pri);
                hyT = ((reputation == null) ? 0 : reputation.getHyT());
            }
            doc.startObject();
            doc.createElement("resourceType", 5);
            doc.createElement("count", hyT);
            doc.endObject();
            doc.endArray();
        }
        doc.createElement("canAttack", por.getOfficerNpc());
        doc.endObject();
        TaskMessageHelper.sendHallsVisitMessage(playerDto.playerId);
        EventListener.fireEvent(new CommonEvent(35, playerId));
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void getNextOfficerInfo(final JsonDocument doc, final int officerId, final int forceId, final int playerId) {
        final Halls hall = (Halls)this.hallsCache.get((Object)officerId);
        final int nowBuilding = hall.getId();
        if (nowBuilding == 1 && hall.getDegree() == 1) {
            doc.createElement("nextRequest", (Object)LocalMessages.ALREADY_BEST);
            doc.createElement("nextOfficer", (Object)LocalMessages.NONE);
            doc.createElement("nextRecourceAddition", (Object)LocalMessages.NONE);
            return;
        }
        final Halls nextHalls = this.hallsCache.getNextHalls(officerId);
        final Official nextOfficer = (Official)this.officialCache.get((Object)nextHalls.getOfficialId());
        String message = LocalMessages.OCCUPY_BUILDING;
        if (nextOfficer.getId() > 5 && nextOfficer.getId() < 11) {
            message = LocalMessages.OCCUYP_OR_APPLY_BUILDING;
        }
        doc.createElement("nextRequest", MessageFormatter.format(message, new Object[] { BattleConstant.getBuildingName(nextHalls, forceId) }));
        if (nextOfficer.getId() == 1) {
            doc.createElement("nextOfficer", (Object)(String.valueOf(WebUtil.getForceName(forceId)) + LocalMessages.RANK_CONSTANTS_KING));
        }
        else {
            doc.createElement("nextOfficer", String.valueOf(nextOfficer.getNameShort()) + nextHalls.getNameList());
        }
        final int base = this.hallsCache.getOutputByType(nextHalls.getPri(), 1);
        final double addition = 1.0 + this.techEffectCache.getTechEffect(playerId, 22) / 100.0;
        final int output = (int)(base * addition);
        doc.createElement("nextRecourceAddition", output);
    }
    
    @Override
    public byte[] getOperation(final PlayerDto playerDto, final int buildingId) {
        final int playerId = playerDto.playerId;
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        if (por == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (BattleConstant.officerBatMap.contains(buildingId)) {
            if (buildingId == 19) {
                doc.createElement("canOccupy", true);
                doc.createElement("canQuit", pob != null && pob.getBuildingId() == buildingId);
                doc.createElement("canApply", false);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            doc.createElement("canOccupy", por.getOfficerNpc() == 18 || por.getOfficerNpc() == 0);
            doc.createElement("canQuit", pob != null && pob.getBuildingId() == buildingId);
            doc.createElement("canApply", false);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        else {
            final OfficerBuildingInfo obi = this.officerBuildingInfoDao.getByBuildingId(playerDto.forceId, buildingId);
            if (obi == null) {
                doc.createElement("canOccupy", por.getOfficerNpc() == 0);
                doc.createElement("canApply", false);
                doc.createElement("canQuit", false);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            final int ownerPlayerId = obi.getPlayerId();
            final Player ownerPlayer = this.playerDao.read(ownerPlayerId);
            if (ownerPlayerId == playerId) {
                doc.createElement("canOccupy", false);
                doc.createElement("canApply", false);
                doc.createElement("canQuit", true);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            if (ownerPlayer.getForceId() == playerDto.forceId) {
                doc.createElement("canOccupy", por.getOfficerNpc() == 0);
                doc.createElement("canApply", por.getOfficerNpc() == 0);
                doc.createElement("canQuit", pob != null && pob.getBuildingId() == buildingId);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            doc.createElement("canOccupy", false);
            doc.createElement("canApply", false);
            doc.createElement("canQuit", false);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
    }
    
    @Transactional
    @Override
    public byte[] applyBuilding(final PlayerDto playerDto, final int buildingId) {
        if (BattleConstant.officerBatMap.contains(buildingId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerDto.playerId);
        if (por == null || por.getOfficerNpc() != 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.APPLY_NEED_CONQUER_lEFT);
        }
        synchronized (this.buildingLocks[playerDto.forceId - 1][buildingId - 1]) {
            final int playerId = playerDto.playerId;
            final OfficerBuildingInfo obi = this.officerBuildingInfoDao.getByBuildingId(playerDto.forceId, buildingId);
            if (obi == null) {
                // monitorexit(this.buildingLocks[playerDto.forceId - 1][buildingId - 1])
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BUILDING_NO_OWNER);
            }
            final Player ownerPlayer = this.playerDao.read(obi.getPlayerId());
            PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
            if (pob != null) {
                if (por.getOfficerId() != 37) {
                    // monitorexit(this.buildingLocks[playerDto.forceId - 1][buildingId - 1])
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.ALREADY_IN_BUILDING);
                }
                if (pob.getBuildingId() == buildingId) {
                    // monitorexit(this.buildingLocks[playerDto.forceId - 1][buildingId - 1])
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.APPLYING);
                }
                // monitorexit(this.buildingLocks[playerDto.forceId - 1][buildingId - 1])
                return JsonBuilder.getJson(State.FAIL, LocalMessages.APPLYING_OTHER);
            }
            else {
                if (ownerPlayer.getForceId() != playerDto.forceId) {
                    // monitorexit(this.buildingLocks[playerDto.forceId - 1][buildingId - 1])
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_SAME_FORCE);
                }
                JsonDocument doc = null;
                pob = new PlayerOfficerBuilding();
                pob.setForceId(playerDto.forceId);
                pob.setPlayerId(playerId);
                pob.setBuildingId(buildingId);
                String message = null;
                if (obi.getAutoPass() != 1) {
                    pob.setState(0);
                    message = LocalMessages.BUILDING_APPLY_SEND;
                    pob.setIsLeader(0);
                    pob.setIsNew(0);
                    this.playerOfficerBuildingDao.create(pob);
                    doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("hasOfficerBuildingApply", true);
                    doc.endObject();
                    Players.push(ownerPlayer.getPlayerId(), PushCommand.PUSH_OFFICER_BUILDING_APPLY, doc.toByte());
                    doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("message", message);
                    doc.createElement("isAuto", false);
                    doc.endObject();
                    // monitorexit(this.buildingLocks[playerDto.forceId - 1][buildingId - 1])
                    return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                }
                if (obi.getMemberCount() >= 3) {
                    // monitorexit(this.buildingLocks[playerDto.forceId - 1][buildingId - 1])
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.MEMBER_FULL);
                }
                pob.setState(1);
                this.officerBuildingInfoDao.addMemberNum(playerDto.forceId, pob.getBuildingId());
                final Halls normalHalls = this.hallsCache.getHalls(buildingId, 2);
                this.onUpdateOfficerId(playerId, normalHalls.getPri());
                final ComparableFactor[] arrays = MultiRankData.orgnizeValue(normalHalls.getOfficialId(), 1, this.playerDao.read(playerId).getPlayerLv(), 0);
                this.rankService.firePositionRank(playerDto.forceId, new MultiRankData(playerId, arrays));
                message = MessageFormatter.format(LocalMessages.BUILDING_ENTER_NOTICE, new Object[] { BattleConstant.getBuildingName(normalHalls, playerDto.forceId), normalHalls.getNameList() });
                pob.setIsLeader(0);
                pob.setIsNew(0);
                this.playerOfficerBuildingDao.create(pob);
                doc = new JsonDocument();
                doc.startObject();
                doc.createElement("isAuto", true);
                doc.createElement("message", message);
                doc.endObject();
                this.buildingOutputCache.clearOfficer(playerId);
                // monitorexit(this.buildingLocks[playerDto.forceId - 1][buildingId - 1])
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
        }
    }
    
    @Override
    public byte[] getApplyList(final int type, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
        if (type != 0 && type != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (pob == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        if (pob.getIsLeader() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_LEADER);
        }
        List<PlayerOfficerBuilding> list = null;
        if (type == 0) {
            list = this.playerOfficerBuildingDao.getApplyingMembers(playerDto.forceId, pob.getBuildingId());
        }
        else {
            list = this.playerOfficerBuildingDao.getBuildingMembers(playerDto.forceId, pob.getBuildingId());
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        for (final PlayerOfficerBuilding temp : list) {
            if (temp.getIsLeader() != 1) {
                final Player tempPlayer = this.playerDao.read(temp.getPlayerId());
                doc.startObject();
                doc.createElement("playerId", tempPlayer.getPlayerId());
                doc.createElement("playerName", tempPlayer.getPlayerName());
                doc.createElement("playerLv", tempPlayer.getPlayerLv());
                doc.endObject();
            }
        }
        doc.endArray();
        final OfficerBuildingInfo buildingInfo = this.officerBuildingInfoDao.getByBuildingId(playerDto.forceId, pob.getBuildingId());
        if (buildingInfo != null) {
            doc.createElement("auto_pass", buildingInfo.getAutoPass());
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] changeAutoPass(final PlayerDto playerDto, final int state) {
        final int playerId = playerDto.playerId;
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
        if (pob == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        if (BattleConstant.officerBatMap.contains(pob.getBuildingId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pob.getIsLeader() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_LEADER);
        }
        if (state != 1 && state != 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        this.officerBuildingInfoDao.updateAutoPass(playerDto.forceId, pob.getBuildingId(), state);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public Tuple<Boolean, Boolean> hasApply(final int playerId) {
        final Tuple<Boolean, Boolean> tuple = new Tuple();
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
        if (pob == null || pob.getIsLeader() == 0) {
            tuple.left = false;
            tuple.right = false;
            return tuple;
        }
        if (BattleConstant.officerBatMap.contains(pob.getBuildingId())) {
            tuple.left = false;
            tuple.right = false;
            return tuple;
        }
        final List<PlayerOfficerBuilding> officialMembers = this.playerOfficerBuildingDao.getBuildingMembers(pob.getForceId(), pob.getBuildingId());
        if (officialMembers.size() >= 1) {
            tuple.left = true;
        }
        final List<PlayerOfficerBuilding> applyingList = this.playerOfficerBuildingDao.getApplyingMembers(pob.getForceId(), pob.getBuildingId());
        if (applyingList.isEmpty() || applyingList.size() == 0) {
            tuple.right = false;
        }
        else {
            tuple.right = true;
        }
        return tuple;
    }
    
    @Transactional
    @Override
    public byte[] passApply(final PlayerDto playerDto, final int applyPlayerId) {
        final int currentPlayerId = playerDto.playerId;
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(currentPlayerId);
        if (pob == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        if (BattleConstant.officerBatMap.contains(pob.getBuildingId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pob.getIsLeader() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_LEADER);
        }
        final OfficerBuildingInfo obi = this.officerBuildingInfoDao.getByBuildingId(playerDto.forceId, pob.getBuildingId());
        if (obi.getMemberCount() >= 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MEMBER_FULL);
        }
        final PlayerOfficerBuilding applyPob = this.playerOfficerBuildingDao.read(applyPlayerId);
        if (applyPob == null || applyPob.getBuildingId() != pob.getBuildingId()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        this.playerOfficerBuildingDao.updateState(applyPlayerId, 1);
        this.officerBuildingInfoDao.addMemberNum(playerDto.forceId, pob.getBuildingId());
        final Halls normalHalls = this.hallsCache.getHalls(pob.getBuildingId(), 2);
        this.onUpdateOfficerId(applyPlayerId, normalHalls.getPri());
        final ComparableFactor[] arrays = MultiRankData.orgnizeValue(normalHalls.getOfficialId(), 1, this.playerDao.read(applyPlayerId).getPlayerLv(), 0);
        this.rankService.firePositionRank(playerDto.forceId, new MultiRankData(applyPlayerId, arrays));
        this.buildingOutputCache.clearOfficer(applyPlayerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] refuseApply(final PlayerDto playerDto, final int applyPlayerId) {
        final int currentPlayerId = playerDto.playerId;
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(currentPlayerId);
        if (pob == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        if (BattleConstant.officerBatMap.contains(pob.getBuildingId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pob.getIsLeader() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_LEADER);
        }
        final PlayerOfficerBuilding applyPob = this.playerOfficerBuildingDao.read(applyPlayerId);
        if (applyPob == null || applyPob.getBuildingId() != pob.getBuildingId()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        this.playerOfficerBuildingDao.deleteById(applyPlayerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] kickMember(final PlayerDto playerDto, final int kickedPlayerId) {
        final int currentPlayerId = playerDto.playerId;
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(currentPlayerId);
        if (pob == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        if (BattleConstant.officerBatMap.contains(pob.getBuildingId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (pob.getIsLeader() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_LEADER);
        }
        final PlayerOfficerBuilding kickPob = this.playerOfficerBuildingDao.read(kickedPlayerId);
        if (kickPob == null || kickPob.getBuildingId() != pob.getBuildingId()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        if (kickPob.getIsLeader() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.CANNOT_KICK_LEADER);
        }
        this.onUpdateOfficerId(kickedPlayerId, 37);
        final ComparableFactor[] arrays = MultiRankData.orgnizeValue(13, 1, this.playerDao.read(kickedPlayerId).getPlayerLv(), 0);
        this.rankService.firePositionRank(playerDto.forceId, new MultiRankData(kickedPlayerId, arrays));
        this.disbandFollowTeam(kickedPlayerId);
        this.playerOfficerBuildingDao.deleteById(kickedPlayerId);
        this.officerBuildingInfoDao.minuseMemberNum(pob.getForceId(), pob.getBuildingId());
        this.buildingOutputCache.clearOfficer(kickedPlayerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] quitBuilding(final PlayerDto playerDto, final int buildingId) {
        final int playerId = playerDto.playerId;
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
        if (pob == null || pob.getBuildingId() != buildingId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NOT_IN_BUILDING);
        }
        if (BattleConstant.officerBatMap.contains(buildingId)) {
            this.playerOfficerBuildingDao.deleteById(playerId);
            this.onUpdateOfficerId(playerId, 37);
            final ComparableFactor[] arrays = MultiRankData.orgnizeValue(13, 1, this.playerDao.read(playerId).getPlayerLv(), 0);
            this.rankService.firePositionRank(playerDto.forceId, new MultiRankData(playerId, arrays));
            this.disbandFollowTeam(playerId);
            this.buildingOutputCache.clearOfficer(playerId);
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        final OfficerBuildingInfo obi = this.officerBuildingInfoDao.getByBuildingId(playerDto.forceId, buildingId);
        if (obi == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BUILDING_NO_OWNER);
        }
        if (pob.getIsLeader() == 0) {
            this.playerOfficerBuildingDao.deleteById(playerId);
            if (pob.getState() != 0) {
                this.officerBuildingInfoDao.minuseMemberNum(playerDto.forceId, buildingId);
            }
            this.onUpdateOfficerId(playerId, 37);
            final ComparableFactor[] arrays2 = MultiRankData.orgnizeValue(13, 1, this.playerDao.read(playerId).getPlayerLv(), 0);
            this.rankService.firePositionRank(playerDto.forceId, new MultiRankData(playerId, arrays2));
            this.disbandFollowTeam(playerId);
        }
        else {
            this.playerOfficerBuildingDao.deleteById(playerId);
            this.onUpdateOfficerId(playerId, 37);
            final ComparableFactor[] arrays2 = MultiRankData.orgnizeValue(13, 1, this.playerDao.read(playerId).getPlayerLv(), 0);
            this.rankService.firePositionRank(playerDto.forceId, new MultiRankData(playerId, arrays2));
            this.disbandFollowTeam(playerId);
            final List<PlayerOfficerBuilding> currentList = this.playerOfficerBuildingDao.getBuildingMembers(playerDto.forceId, buildingId);
            if (currentList.isEmpty()) {
                this.playerOfficerBuildingDao.deleteByState(playerDto.forceId, buildingId, 0);
                this.officerBuildingInfoDao.deleteById(obi.getVId());
            }
            else {
                Player nextLeader = null;
                for (final PlayerOfficerBuilding temp : currentList) {
                    final Player tempPlayer = this.playerDao.read(temp.getPlayerId());
                    if (nextLeader == null) {
                        nextLeader = tempPlayer;
                    }
                    else {
                        if (tempPlayer.getPlayerLv() <= nextLeader.getPlayerLv()) {
                            continue;
                        }
                        nextLeader = tempPlayer;
                    }
                }
                this.officerBuildingInfoDao.updatePlayerId(nextLeader.getPlayerId(), obi.getVId());
                this.playerOfficerBuildingDao.changeLeader(nextLeader.getPlayerId(), 1);
                this.officerBuildingInfoDao.minuseMemberNum(playerDto.forceId, buildingId);
                final Halls leaderHalls = this.hallsCache.getHalls(buildingId, 1);
                this.onUpdateOfficerId(nextLeader.getPlayerId(), leaderHalls.getPri());
                final ComparableFactor[] arrays3 = MultiRankData.orgnizeValue(leaderHalls.getOfficialId(), 1, this.playerDao.read(nextLeader.getPlayerId()).getPlayerLv(), 0);
                this.rankService.firePositionRank(nextLeader.getForceId(), new MultiRankData(nextLeader.getPlayerId(), arrays3));
                this.buildingOutputCache.clearOfficer(nextLeader.getPlayerId());
            }
        }
        this.buildingOutputCache.clearOfficer(playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public Map<Integer, String> handleOfficerAfterBattle(final int forceId, final int buildingId, final Map<Integer, Integer> playerMap) {
        for (final int playerId : playerMap.keySet()) {
            final PlayerOfficerBuilding pobOld = this.playerOfficerBuildingDao.read(playerId);
            if (pobOld != null) {
                final PlayerDto tempDto = new PlayerDto();
                tempDto.playerId = playerId;
                tempDto.forceId = forceId;
                this.quitBuilding(tempDto, pobOld.getBuildingId());
            }
        }
        final Map<Integer, String> map = new HashMap<Integer, String>();
        if (BattleConstant.officerBatMap.contains(buildingId)) {
            final Halls leaderHall = this.hallsCache.getHalls(buildingId, 1);
            for (final Map.Entry<Integer, Integer> entry : playerMap.entrySet()) {
                final int playerId2 = entry.getKey();
                this.playerOfficerBuildingDao.deleteById(playerId2);
                final PlayerOfficerBuilding pob = new PlayerOfficerBuilding();
                pob.setPlayerId(playerId2);
                pob.setForceId(forceId);
                pob.setBuildingId(buildingId);
                pob.setState(1);
                pob.setIsLeader(1);
                pob.setIsNew(1);
                this.playerOfficerBuildingDao.create(pob);
                this.onUpdateOfficerId(playerId2, leaderHall.getPri());
                final ComparableFactor[] arrays2 = MultiRankData.orgnizeValue(leaderHall.getOfficialId(), 1, this.playerDao.read(playerId2).getPlayerLv(), 0);
                this.rankService.firePositionRank(forceId, new MultiRankData(playerId2, arrays2));
                TaskMessageHelper.sendHallsPositionTaskMessage(playerId2);
                map.put(playerId2, leaderHall.getPic1());
                final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId2);
                if (por.getOfficerNpc() != 0) {
                    if (buildingId == 18) {
                        this.playerOfficeRelativeDao.updateOfficerNpc(playerId2, 0);
                    }
                    else if (buildingId == 19 && por.getOfficerNpc() == 19) {
                        this.playerOfficeRelativeDao.updateOfficerNpc(playerId2, 18);
                    }
                }
                this.buildingOutputCache.clearOfficer(playerId2);
                if (buildingId == 18) {
                    EventListener.fireEvent(new CommonEvent(36, playerId2));
                }
            }
            return map;
        }
        final List<PlayerOfficerBuilding> currentList = this.playerOfficerBuildingDao.getBuildingMembers(forceId, buildingId);
        for (final PlayerOfficerBuilding pob2 : currentList) {
            final int playerId2 = pob2.getPlayerId();
            this.playerOfficerBuildingDao.deleteById(playerId2);
            final PlayerOfficerBuilding newPob = new PlayerOfficerBuilding();
            newPob.setPlayerId(playerId2);
            newPob.setForceId(forceId);
            newPob.setBuildingId(18);
            newPob.setState(1);
            newPob.setIsLeader(1);
            newPob.setIsNew(1);
            this.playerOfficerBuildingDao.create(newPob);
            this.onUpdateOfficerId(playerId2, 35);
            final ComparableFactor[] arrays2 = MultiRankData.orgnizeValue(11, 1, this.playerDao.read(playerId2).getPlayerLv(), 0);
            this.rankService.firePositionRank(forceId, new MultiRankData(playerId2, arrays2));
            Players.push(playerId2, PushCommand.PUSH_WINDOW, JsonBuilder.getSimpleJson("loseOffice", 1));
            this.disbandFollowTeam(playerId2);
            this.buildingOutputCache.clearOfficer(playerId2);
        }
        this.playerOfficerBuildingDao.deleteByBuildingId(forceId, buildingId);
        final Halls leaderHall2 = this.hallsCache.getHalls(buildingId, 1);
        final Halls normalHall = this.hallsCache.getHalls(buildingId, 2);
        String leaderHallName = leaderHall2.getNameList();
        if (buildingId == 1) {
            leaderHallName = LocalMessages.RANK_CONSTANTS_KING;
        }
        int leaderPlayerId = 0;
        final int mapSize = playerMap.entrySet().size();
        StringBuffer sb = null;
        String assPlayerName = null;
        String firstName = null;
        for (final Map.Entry<Integer, Integer> entry2 : playerMap.entrySet()) {
            final int playerId3 = entry2.getKey();
            final int level = entry2.getValue();
            this.playerOfficerBuildingDao.deleteById(playerId3);
            final PlayerOfficerBuilding pob3 = new PlayerOfficerBuilding();
            pob3.setPlayerId(playerId3);
            pob3.setForceId(forceId);
            pob3.setBuildingId(buildingId);
            pob3.setState(1);
            if (level == 1) {
                leaderPlayerId = playerId3;
                this.onUpdateOfficerId(playerId3, leaderHall2.getPri());
                final ComparableFactor[] arrays3 = MultiRankData.orgnizeValue(leaderHall2.getOfficialId(), 1, this.playerDao.read(playerId3).getPlayerLv(), 0);
                this.rankService.firePositionRank(forceId, new MultiRankData(playerId3, arrays3));
                TaskMessageHelper.sendHallsPositionTaskMessage(playerId3);
                pob3.setIsLeader(1);
                pob3.setIsNew(1);
                this.playerOfficerBuildingDao.create(pob3);
                if (playerMap.size() >= 1) {
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("hasOfficerBuildingApply", true);
                    doc.endObject();
                    Players.push(playerId3, PushCommand.PUSH_OFFICER_BUILDING_APPLY, doc.toByte());
                }
                map.put(playerId3, leaderHall2.getPic1());
                firstName = this.playerDao.getPlayerName(leaderPlayerId);
            }
            else {
                this.onUpdateOfficerId(playerId3, normalHall.getPri());
                final ComparableFactor[] arrays3 = MultiRankData.orgnizeValue(normalHall.getOfficialId(), 1, this.playerDao.read(playerId3).getPlayerLv(), 0);
                this.rankService.firePositionRank(forceId, new MultiRankData(playerId3, arrays3));
                TaskMessageHelper.sendHallsPositionTaskMessage(playerId3);
                pob3.setIsLeader(0);
                pob3.setIsNew(1);
                this.playerOfficerBuildingDao.create(pob3);
                map.put(playerId3, normalHall.getPic1());
                assPlayerName = this.playerDao.getPlayerName(playerId3);
                if (sb == null) {
                    sb = new StringBuffer();
                }
                sb.append(assPlayerName).append(",");
            }
            this.buildingOutputCache.clearOfficer(playerId3);
            TaskMessageHelper.sendOfficialMessage(playerId3);
        }
        if (sb != null) {
            SymbolUtil.removeTheLast(sb);
        }
        OfficerBuildingInfo obi = this.officerBuildingInfoDao.getByBuildingId(forceId, buildingId);
        if (obi != null) {
            this.officerBuildingInfoDao.update(forceId, buildingId, leaderPlayerId, playerMap.size());
        }
        else {
            obi = new OfficerBuildingInfo();
            obi.setForceId(forceId);
            obi.setBuildingId(buildingId);
            obi.setOccupyTime(new Date());
            obi.setBattleData(new byte[1]);
            obi.setState(2);
            obi.setAutoPass(0);
            obi.setMemberCount(playerMap.size());
            obi.setPlayerId(leaderPlayerId);
            this.officerBuildingInfoDao.create(obi);
        }
        this.createOfficerTokenInfo(buildingId, forceId);
        try {
            String msg;
            if (mapSize > 1) {
                msg = MessageFormatter.format(LocalMessages.BATTLE_BUILDING_OCCUPIED2, new Object[] { firstName, sb.toString(), leaderHallName });
            }
            else {
                msg = MessageFormatter.format(LocalMessages.BATTLE_BUILDING_OCCUPIED1, new Object[] { firstName, leaderHallName });
            }
            for (final PlayerOfficerBuilding pob4 : currentList) {
                final int toPlayerId = pob4.getPlayerId();
                this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.BATTLE_BUILDING_OCCUPY_TITLE, msg, 1, toPlayerId, new Date());
            }
        }
        catch (Exception e) {
            OccupyService.occupyLogger.error(this, e);
        }
        return map;
    }
    
    private void createOfficerTokenInfo(final int buildingId, final int forceId) {
        final Halls halls = this.hallsCache.getHalls(buildingId, 1);
        final OfficerToken token = this.officerTokenDao.getTokenByForceIdAndOfficerId(halls.getPri(), forceId);
        if (halls.getOrder() != 0 && token == null) {
            final OfficerToken newToken = new OfficerToken();
            newToken.setForceid(forceId);
            newToken.setOfficerid(halls.getPri());
            newToken.setNum(0);
            final ForceInfo forceInfo = this.dataGetter.getForceInfoDao().read(forceId);
            if (forceInfo.getForceLv() >= 2) {
                newToken.setKillTokenNum(1);
            }
            else {
                newToken.setKillTokenNum(0);
            }
            this.officerTokenDao.create(newToken);
        }
    }
    
    @Override
    public byte[] getRankInfo(final PlayerDto playerDto, final int page) {
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        boolean isTx = false;
        if (this.yxOperation.checkTencentPf(playerDto.yx)) {
            isTx = true;
        }
        final Player currPlayer = this.playerDao.read(playerId);
        final PlayerOfficeRelative playerOfficeRelative = this.playerOfficeRelativeDao.read(playerId);
        List<Integer> list = new ArrayList<Integer>();
        List<Integer> containPlayer = new ArrayList<Integer>();
        boolean flag = false;
        int startRank = 1;
        final int count = 10;
        int nowRank = this.rankService.getPlayerPositionRank(playerId, forceId);
        final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
        if (pob != null && pob.getState() == 0) {
            Integer ownerPlayerId = null;
            try {
                ownerPlayerId = this.playerOfficerBuildingDao.getOwnerIdByBuilding(pob.getBuildingId(), forceId);
            }
            catch (Exception e) {
                OccupyService.occupyLogger.error(this, e);
            }
            if (ownerPlayerId != null) {
                nowRank = this.rankService.getPlayerPositionRank(ownerPlayerId, forceId);
            }
        }
        int totalNum = this.rankService.getTotalPostionRankNumByForceId(forceId);
        if (page == 0) {
            if (nowRank == -1) {
                if (totalNum == 200) {
                    startRank = totalNum - count + 1;
                }
                else {
                    startRank = totalNum / count * count + 1;
                    flag = true;
                    ++totalNum;
                }
            }
            else {
                final int isDevided = (nowRank % count == 0) ? -1 : 0;
                startRank = nowRank / count * count + isDevided * 10 + 1;
            }
        }
        else {
            if (nowRank == -1) {
                if (totalNum < 200) {
                    nowRank = ++totalNum;
                }
                if (totalNum / 10 + 1 == page) {
                    flag = true;
                }
            }
            if (page > totalNum / count + 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            startRank = (page - 1) * count + 1;
        }
        final int currentPage = startRank / 10 + 1;
        if (flag) {
            containPlayer = this.rankService.getForcePositionRankList(forceId, startRank, count);
            for (int i = 0; i < containPlayer.size(); ++i) {
                list.add(containPlayer.get(i));
            }
            if (list.size() < 200) {
                list.add(playerId);
                nowRank = totalNum;
            }
        }
        else {
            list = this.rankService.getForcePositionRankList(forceId, startRank, count);
        }
        final int totalPage = (int)Math.ceil(totalNum / Double.valueOf(count));
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rankList");
        for (final int pId : list) {
            doc.startObject();
            final Player player = this.playerDao.read(pId);
            final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(pId);
            doc.createElement("rank", startRank);
            doc.createElement("playerId", pId);
            doc.createElement("playerLv", player.getPlayerLv());
            doc.createElement("playerName", player.getPlayerName());
            doc.createElement("forceId", player.getForceId());
            final Halls hall = (Halls)this.hallsCache.get((Object)por.getOfficerId());
            final Official official = (Official)this.officialCache.get((Object)hall.getOfficialId());
            if (official.getId() == 1) {
                doc.createElement("officer", (Object)(String.valueOf(WebUtil.getForceName(forceId)) + LocalMessages.RANK_CONSTANTS_KING));
            }
            else {
                doc.createElement("officer", String.valueOf(official.getNameShort()) + hall.getNameList());
            }
            doc.createElement("buildingId", hall.getId());
            this.appendStatusInfo(official, hall, player, doc, playerId);
            doc.createElement("freeNum", hall.getHyN());
            doc.createElement("hour", hall.getHyT());
            if (isTx) {
                final PlayerVipTx pvt = this.playerVipTxDao.getByPlayerId(playerId);
                if (pvt != null) {
                    doc.createElement("isYellowVip", true);
                    doc.createElement("yellowVipLv", pvt.getYellowVipLv());
                }
            }
            ++startRank;
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("currentPage", currentPage);
        doc.createElement("totalPage", totalPage);
        doc.createElement("playerId", playerId);
        doc.createElement("playerName", currPlayer.getPlayerName());
        doc.createElement("playerLv", currPlayer.getPlayerLv());
        doc.createElement("pic", currPlayer.getPic());
        doc.createElement("rank", nowRank);
        doc.createElement("forceId", playerDto.forceId);
        final Halls hall2 = (Halls)this.hallsCache.get((Object)playerOfficeRelative.getOfficerId());
        doc.createElement("buildingId", hall2.getId());
        doc.createElement("hasSalary", playerOfficeRelative.getSalaryGotToday() == 0);
        final Official official2 = (Official)this.officialCache.get((Object)hall2.getOfficialId());
        if (official2.getId() == 1) {
            doc.createElement("nowOfficer", (Object)(String.valueOf(WebUtil.getForceName(forceId)) + LocalMessages.RANK_CONSTANTS_KING));
        }
        else {
            doc.createElement("nowOfficer", String.valueOf(official2.getNameShort()) + hall2.getNameList());
        }
        doc.createElement("resourceAddition", this.buildingOutputCache.getOfficersOutput(playerId, 1));
        this.getNextOfficerInfo(doc, playerOfficeRelative.getOfficerId(), playerDto.forceId, playerId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void appendStatusInfo(final Official official, final Halls hall, final Player player, final JsonDocument doc, final int playerId) {
        try {
            if (official != null) {
                if (player.getPlayerId() == playerId) {
                    doc.createElement("status", (hall.getPri() != 37) ? 1 : 0);
                }
                else if (official.getId() <= 5) {
                    final List<PlayerOfficerBuilding> memberList = this.playerOfficerBuildingDao.getBuildingMembers(player.getForceId(), hall.getId());
                    final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
                    final int number = (memberList == null) ? 0 : memberList.size();
                    doc.createElement("status", (pob != null && pob.getState() == 0 && pob.getBuildingId() == hall.getId()) ? 4 : ((number >= 3) ? 3 : 2));
                }
                else {
                    doc.createElement("status", 0);
                }
            }
        }
        catch (Exception e) {
            OccupyService.occupyLogger.error(this, e);
        }
    }
    
    @Transactional
    @Override
    public byte[] getSalary(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        if (por.getSalaryGotToday() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.SALARY_GOT);
        }
        final int officerId = por.getOfficerId();
        final Halls halls = (Halls)this.hallsCache.get((Object)officerId);
        final Official official = (Official)this.officialCache.get((Object)halls.getOfficialId());
        final int output = official.getOutput();
        if (output > 0) {
            this.playerResourceDao.addCopperIgnoreMax(playerId, output, "\u9886\u53d6\u4ff8\u7984\u589e\u52a0\u94f6\u5e01", true);
        }
        this.playerOfficeRelativeDao.updateSalaryGot(playerId, 1);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("output", output);
        doc.endObject();
        TaskMessageHelper.sendGetSalaryMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void disbandFollowTeam(final int playerId) {
        boolean sendMail = false;
        PlayerGroupArmy pga = null;
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            pga = this.playerGroupArmyDao.getPlayerGroupArmy(playerId, pgm.getGeneralId());
            if (pga == null) {
                continue;
            }
            sendMail = true;
            if (1 == pga.getIsLeader() || this.playerGroupArmyDao.getCountByArmyId(pga.getArmyId()) <= 2) {
                this.playerGroupArmyDao.deleteByArmyId(pga.getArmyId());
                this.groupArmyDao.deleteById(pga.getArmyId());
            }
            else {
                this.playerGroupArmyDao.deleteById(pga.getVId());
            }
        }
        if (sendMail) {
            this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.LOSS_OFFICIER_EMAIL_CONTENT, 1, playerId, 0);
        }
    }
    
    @Override
    public void onUpdateOfficerId(final int playerId, final int officerId) {
        try {
            final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
            final Integer highestOfficer = por.getHighestOfficer();
            final Halls newHalls = (Halls)this.hallsCache.get((Object)officerId);
            if (newHalls == null) {
                return;
            }
            final Halls oldHalls = (Halls)this.hallsCache.get((Object)por.getOfficerId());
            if (oldHalls == null) {
                return;
            }
            final long now = System.currentTimeMillis();
            final int newOfficerId = newHalls.getOfficialId();
            final int oldOfficerId = oldHalls.getOfficialId();
            if (oldOfficerId <= 5 && newOfficerId > 5) {
                final Date date = (por.getOccupyOfficialTime() == null) ? new Date() : por.getOccupyOfficialTime();
                long result = (now - date.getTime()) / 2L;
                result = Math.min(result, 604800000L);
                if (result > 3600000L) {
                    this.playerOfficeRelativeDao.updateReputationTime(playerId, new Date(now + result), por.getOfficerId());
                    this.pushReputationInfo(playerId, por.getOfficerId(), result);
                }
            }
            else if (newOfficerId <= 5) {
                this.playerOfficeRelativeDao.updateReputationTime(playerId, null, 0);
                this.pushReputationInfo(playerId, officerId, 0L);
            }
            if (highestOfficer == null) {
                this.playerOfficeRelativeDao.updateSalaryGot(playerId, 0);
                this.playerOfficeRelativeDao.updateHighestOfficer(playerId, newOfficerId);
            }
            else if (newOfficerId < highestOfficer) {
                this.playerOfficeRelativeDao.updateSalaryGot(playerId, 0);
                this.playerOfficeRelativeDao.updateHighestOfficer(playerId, newOfficerId);
            }
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("pin", newOfficerId - 1));
        }
        catch (Exception e) {
            OccupyService.occupyLogger.error("playerId:" + playerId + "officerId" + officerId);
            return;
        }
        finally {
            this.playerOfficeRelativeDao.updateOfficerId(playerId, officerId);
        }
        this.playerOfficeRelativeDao.updateOfficerId(playerId, officerId);
    }
    
    private void pushReputationInfo(final int playerId, final Integer officerId, final long result) {
        final Halls halls = (Halls)this.hallsCache.get((Object)officerId);
        if (halls == null) {
            return;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("reputationTime", result);
        doc.createElement("officerId", halls.getOfficialId());
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_REPUTAION, doc.toByte());
    }
    
    @Override
    public void addFreePhantom() {
        final long start = System.currentTimeMillis();
        final Calendar calendar = Calendar.getInstance();
        final int curHour = calendar.get(11);
        int hour = 0;
        for (final Halls hall : this.hallsCache.getModels()) {
            hour = hall.getHyT();
            if (hour <= 0) {
                continue;
            }
            if (curHour % hour != 0) {
                continue;
            }
            final List<PlayerOfficeRelative> pabList = this.playerOfficeRelativeDao.getListByOfficerId(hall.getPri());
            for (final PlayerOfficeRelative pa : pabList) {
                if (pa == null) {
                    continue;
                }
                boolean flag = true;
                final Date date = pa.getReputationTime();
                flag = (date == null || date.before(new Date()));
                if (!flag) {
                    continue;
                }
                final int maxNum = this.getMaxFreePhontom(pa.getPlayerId());
                this.playerBattleAttributeDao.addVip3PhantomCountMax(pa.getPlayerId(), hall.getHyN(), maxNum, "\u5b98\u804c\u589e\u52a0\u514d\u8d39\u501f\u5175\u6b21\u6570");
            }
        }
        final List<PlayerOfficeRelative> list = this.playerOfficeRelativeDao.getListByReputationTime();
        Halls halls = null;
        int hour2 = 0;
        for (final PlayerOfficeRelative playerAttribute : list) {
            if (playerAttribute == null) {
                continue;
            }
            try {
                final int priId = (playerAttribute.getLastOfficerId() == null) ? 0 : playerAttribute.getLastOfficerId();
                final long reputationTime = (playerAttribute.getReputationTime() == null) ? 0L : playerAttribute.getReputationTime().getTime();
                if (reputationTime < start) {
                    continue;
                }
                halls = (Halls)this.hallsCache.get((Object)priId);
                if (halls == null) {
                    continue;
                }
                if (halls.getHyN() <= 0) {
                    continue;
                }
                hour2 = halls.getHyT();
                if (hour2 <= 0) {
                    continue;
                }
                if (curHour % hour2 != 0) {
                    continue;
                }
                final int maxNum2 = this.getMaxFreePhontom(playerAttribute.getPlayerId());
                this.playerBattleAttributeDao.addVip3PhantomCountMax(playerAttribute.getPlayerId(), halls.getHyN(), maxNum2, "\u5b98\u5a01\u589e\u52a0\u514d\u8d39\u501f\u5175\u6b21\u6570");
            }
            catch (Exception e) {
                OccupyService.occupyLogger.error("addFreePhantom", e);
            }
        }
        OccupyService.timerLog.info(LogUtil.formatThreadLog("OccupyService", "addFreePhantom", 2, System.currentTimeMillis() - start, ""));
    }
    
    private int getMaxFreePhontom(final Integer playerId) {
        try {
            int maxFreePc = 30;
            final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerId);
            if (playerPhantomObj != null) {
                maxFreePc = playerPhantomObj.maxPhantomNum;
            }
            return maxFreePc;
        }
        catch (Exception e) {
            OccupyService.occupyLogger.error("getMaxFreePhontom", e);
            return 30;
        }
    }
    
    public static Tuple<Integer, Long> getReputationTime(final String reputationTime) {
        if (StringUtils.isBlank(reputationTime)) {
            return null;
        }
        Tuple<Integer, Long> tuple = null;
        try {
            final String[] single = reputationTime.split(";");
            if (single.length < 2) {
                return null;
            }
            final int priId = Integer.parseInt(single[0]);
            final long time = Long.parseLong(single[1]);
            tuple = new Tuple();
            tuple.left = priId;
            tuple.right = time;
        }
        catch (Exception e) {
            OccupyService.occupyLogger.error("getReputationTime", e);
        }
        return tuple;
    }
}
