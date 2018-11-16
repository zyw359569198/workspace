package com.reign.gcld.team.service;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.team.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.team.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.world.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.util.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.common.*;

public class TeamManager
{
    private static final Logger log;
    private Map<String, Team> teamMap;
    private Map<Integer, ConcurrentHashMap<Integer, String>> pMap;
    private Map<Integer, Integer> forceTeamMap;
    public static final int TEAM_CREATE_GENERAL_ID = 0;
    private final ReentrantReadWriteLock lock;
    private final Lock readLock;
    private final Lock writeLock;
    private static TeamManager tm;
    
    static {
        log = CommonLog.getLog(TeamManager.class);
    }
    
    private TeamManager() {
        this.teamMap = new ConcurrentHashMap<String, Team>();
        this.pMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, String>>();
        this.forceTeamMap = new ConcurrentHashMap<Integer, Integer>();
        this.lock = new ReentrantReadWriteLock();
        this.readLock = this.lock.readLock();
        this.writeLock = this.lock.writeLock();
        this.forceTeamMap.put(1, 0);
        this.forceTeamMap.put(2, 0);
        this.forceTeamMap.put(3, 0);
    }
    
    public static TeamManager getInstance() {
        if (TeamManager.tm == null) {
            TeamManager.tm = new TeamManager();
        }
        return TeamManager.tm;
    }
    
    public int getTeamNumByForceId(final int forceId) {
        return this.forceTeamMap.get(forceId);
    }
    
    public Team createTeam(final String teamId, final String teamName, final int maxNum, final Player player, final String teamType, final int worldLegionId, final IDataGetter dataGetter) {
        this.writeLock.lock();
        try {
            final TeamMember creator = new TeamMember(teamId, player, true, null);
            final WorldLegion wl = (WorldLegion)dataGetter.getWorldLegionCache().get((Object)worldLegionId);
            final int s = dataGetter.getSerialCache().get(wl.getOwnerExpS(), player.getPlayerLv());
            final Team team = new Team(teamId, teamName, maxNum, teamType, worldLegionId, creator, s, new Date());
            this.teamMap.put(teamId, team);
            ConcurrentHashMap<Integer, String> gMap = this.pMap.get(player.getPlayerId());
            if (gMap == null) {
                gMap = new ConcurrentHashMap<Integer, String>();
                this.pMap.put(player.getPlayerId(), gMap);
            }
            gMap.put(0, team.getTeamId());
            this.forceTeamMap.put(player.getForceId(), this.forceTeamMap.get(player.getForceId()) + 1);
            this.pushCreateTeam(this.forceTeamMap.get(player.getForceId()), player.getForceId());
            this.pushTeamMemberChange3(team.getCreator().getForceId());
            return team;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    private void pushCreateTeam(final int teamNum, final int forceId) {
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        for (final PlayerDto playerDto : onlinePlayerList) {
            if (playerDto.playerId <= 0) {
                continue;
            }
            if (playerDto.forceId != forceId) {
                continue;
            }
            if (playerDto.cs[10] != '1') {
                continue;
            }
            Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("batTeamNum", teamNum));
        }
    }
    
    public Team initCreateTeam(final PlayerTeam pt, final String teamType, final IDataGetter dataGetter) {
        this.writeLock.lock();
        try {
            Team team = this.teamMap.get(pt.getTeamId());
            if (team != null) {
                return null;
            }
            final Player player = dataGetter.getPlayerDao().read(pt.getPlayerId());
            final TeamMember creator = new TeamMember(pt.getTeamId(), player, true, null);
            final WorldLegion wl = (WorldLegion)dataGetter.getWorldLegionCache().get((Object)pt.getWorldLegionId());
            final int s = dataGetter.getSerialCache().get(wl.getOwnerExpS(), player.getPlayerLv());
            long createTime = 0L;
            if (pt.getCreateTime() == null) {
                createTime = System.currentTimeMillis();
            }
            else if (pt.getCreateTime() == 0L) {
                createTime = System.currentTimeMillis();
            }
            else {
                createTime = pt.getCreateTime();
            }
            team = new Team(pt.getTeamId(), pt.getTeamName(), wl.getMax(), teamType, wl.getId(), creator, s, new Date(createTime));
            this.teamMap.put(pt.getTeamId(), team);
            ConcurrentHashMap<Integer, String> gMap = this.pMap.get(pt.getPlayerId());
            if (gMap == null) {
                gMap = new ConcurrentHashMap<Integer, String>();
                this.pMap.put(pt.getPlayerId(), gMap);
            }
            gMap.put(0, team.getTeamId());
            final String[] strs = pt.getPlayerGeneralId().split(",");
            int tPlayerId = 0;
            Player tPlayer = null;
            String[] array;
            for (int length = (array = strs).length, j = 0; j < length; ++j) {
                final String str = array[j];
                final String[] ss = str.split("#");
                if (ss.length > 2) {
                    tPlayerId = Integer.valueOf(ss[0]);
                    tPlayer = dataGetter.getPlayerDao().read(tPlayerId);
                    ConcurrentHashMap<Integer, String> tgMap = this.pMap.get(tPlayer.getPlayerId());
                    if (tgMap == null) {
                        tgMap = new ConcurrentHashMap<Integer, String>();
                        this.pMap.put(tPlayer.getPlayerId(), tgMap);
                    }
                    final Map<Integer, PlayerGeneralMilitary> pgmMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(tPlayerId);
                    int tgId = 0;
                    PlayerGeneralMilitary tpgm = null;
                    final List<GeneralInfo> giList = new ArrayList<GeneralInfo>();
                    for (int i = 1; i < ss.length; ++i) {
                        tgId = Integer.valueOf(ss[i]);
                        tpgm = pgmMap.get(tgId);
                        if (tpgm != null) {
                            final GeneralInfo gi = new GeneralInfo();
                            gi.setGeneralId(tpgm.getGeneralId());
                            gi.setGeneralLv(tpgm.getLv());
                            final General general = (General)dataGetter.getGeneralCache().get((Object)tpgm.getGeneralId());
                            gi.setGeneralName(general.getName());
                            gi.setGeneralQuality(general.getQuality());
                            gi.setMaxFroces(dataGetter.getBattleDataCache().getMaxHp(tpgm));
                            gi.setFroces(tpgm.getForces());
                            giList.add(gi);
                            team.setTotalMaxForces(gi.getMaxFroces() + team.getTotalMaxForces());
                            team.setTotalForces(gi.getFroces() + team.getTotalForces());
                            tgMap.put(tpgm.getGeneralId(), team.getTeamId());
                        }
                    }
                    TeamMember member = team.getMemberMap().get(tPlayer.getPlayerId());
                    if (member == null) {
                        member = new TeamMember(pt.getTeamId(), tPlayer, false, giList);
                        team.getMemberMap().put(tPlayer.getPlayerId(), member);
                    }
                    else {
                        member.getGeneralInfo().addAll(giList);
                    }
                    team.setCurNum(team.getCurNum() + giList.size());
                }
            }
            this.forceTeamMap.put(creator.getForceId(), this.forceTeamMap.get(creator.getForceId()) + 1);
            this.pushCreateTeam(this.forceTeamMap.get(creator.getForceId()), creator.getForceId());
            final StringBuilder sb = new StringBuilder();
            sb.append(player.getPlayerId()).append("#").append(team.getTeamId()).append("#").append(team.getCreateTime().getTime());
            dataGetter.getJobService().addJob("teamService", "cancelTeam", sb.toString(), team.getCreateTime().getTime() + 3600000L);
            return team;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public Collection<Team> getAllTeam() {
        this.readLock.lock();
        try {
            return new ArrayList<Team>(this.teamMap.values());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public OperationResult joinTeam(final String teamId, final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter) {
        this.readLock.lock();
        try {
            final Team team = this.teamMap.get(teamId);
            if (team == null) {
                return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10017);
            }
            synchronized (team) {
                if (team.getCreator().getPlayerId() == player.getPlayerId()) {
                    // monitorexit(team)
                    return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10032);
                }
                if (team.getCreator().getForceId() != player.getForceId()) {
                    // monitorexit(team)
                    return new OperationResult(false, LocalMessages.T_COMM_10012);
                }
                if (team.getCurNum() + pgmList.size() > team.getMaxNum()) {
                    // monitorexit(team)
                    return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10018);
                }
                if (team.isOrder()) {
                    // monitorexit(team)
                    return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10040);
                }
                ConcurrentHashMap<Integer, String> gMap = this.pMap.get(player.getPlayerId());
                if (gMap == null) {
                    gMap = new ConcurrentHashMap<Integer, String>();
                    this.pMap.put(player.getPlayerId(), gMap);
                }
                final List<GeneralInfo> giList = new ArrayList<GeneralInfo>();
                for (final PlayerGeneralMilitary pgm : pgmList) {
                    final GeneralInfo gi = new GeneralInfo();
                    gi.setGeneralId(pgm.getGeneralId());
                    gi.setGeneralLv(pgm.getLv());
                    final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
                    gi.setGeneralName(general.getName());
                    gi.setGeneralQuality(general.getQuality());
                    gi.setMaxFroces(dataGetter.getBattleDataCache().getMaxHp(pgm));
                    gi.setFroces(pgm.getForces());
                    giList.add(gi);
                    gMap.put(pgm.getGeneralId(), team.getTeamId());
                    team.setTotalMaxForces(gi.getMaxFroces() + team.getTotalMaxForces());
                    team.setTotalForces(gi.getFroces() + team.getTotalForces());
                    final int capitalId = WorldCityCommon.nationMainCityIdMap.get(pgm.getForceId());
                    dataGetter.getPlayerGeneralMilitaryDao().updateLocationId(player.getPlayerId(), pgm.getGeneralId(), capitalId);
                    dataGetter.getCityService().sendAttMoveInfo(player.getPlayerId(), pgm.getGeneralId(), pgm.getLocationId(), capitalId, pgm.getForceId(), "", pgm.getForces(), true);
                    final GeneralMoveDto gmd = CityService.getGeneralMoveDto(player.getPlayerId(), pgm.getGeneralId());
                    if (gmd != null) {
                        gmd.moveLine = "";
                        gmd.cityState = 0;
                        gmd.nextMoveTime = 0L;
                    }
                }
                TeamMember member = team.getMemberMap().get(player.getPlayerId());
                if (member == null) {
                    member = new TeamMember(teamId, player, false, giList);
                    team.getMemberMap().put(player.getPlayerId(), member);
                }
                else {
                    member.getGeneralInfo().addAll(giList);
                }
                team.setCurNum(team.getCurNum() + giList.size());
                final StringBuilder sb = new StringBuilder();
                for (final TeamMember tm : team.getMemberMap().values()) {
                    sb.append(tm.getPlayerId()).append("#");
                    for (final GeneralInfo gi2 : tm.getGeneralInfo()) {
                        sb.append(gi2.getGeneralId()).append("#");
                    }
                    sb.append(",");
                }
                dataGetter.getPlayerTeamDao().updatePlayerGenrealIds(team.getCreator().getPlayerId(), sb.toString());
                if (team.getCurNum() >= team.getMaxNum()) {
                    final JsonDocument doc2 = new JsonDocument();
                    doc2.startObject();
                    doc2.createElement("teamId", team.getTeamId());
                    doc2.createElement("curNum", team.getCurNum());
                    doc2.createElement("maxNum", team.getMaxNum());
                    doc2.endObject();
                    Players.push(team.getCreator().getPlayerId(), PushCommand.PUSH_TEAM_FULL, doc2.toByte());
                }
                final Iterator<GeneralInfo> iterator4 = giList.iterator();
                while (iterator4.hasNext()) {
                    final GeneralInfo gi = iterator4.next();
                    dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(player.getPlayerId(), gi.getGeneralId());
                }
                this.pushTeamMemberChange3(player.getForceId());
                final OperationResult operationResult = new OperationResult(true);
                return operationResult;
            }
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private void pushTeamMemberChange3(final int forceId) {
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("change", true);
        doc2.endObject();
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        for (final PlayerDto playerDto : onlinePlayerList) {
            if (playerDto.playerId <= 0) {
                continue;
            }
            if (playerDto.forceId != forceId) {
                continue;
            }
            Players.push(playerDto.playerId, PushCommand.PUSH_TEAM_CHANGE, doc2.toByte());
        }
    }
    
    public OperationResult leaveTeam(final String teamId, final int playerId, final int generalId, final IDataGetter dataGetter) {
        this.readLock.lock();
        try {
            final Team team = this.teamMap.get(teamId);
            if (team == null) {
                return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10017);
            }
            synchronized (team) {
                final TeamMember teamMember = team.getMemberMap().get(playerId);
                if (teamMember == null) {
                    // monitorexit(team)
                    return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10023);
                }
                boolean leave = false;
                for (int i = teamMember.getGeneralInfo().size() - 1; i >= 0; --i) {
                    if (teamMember.getGeneralInfo().get(i).getGeneralId() == generalId) {
                        final GeneralInfo gi = teamMember.getGeneralInfo().remove(i);
                        team.setTotalMaxForces(team.getTotalMaxForces() - gi.getMaxFroces());
                        team.setTotalForces(team.getTotalForces() - gi.getFroces());
                        final Map<Integer, String> gMap = this.pMap.get(playerId);
                        if (gMap != null) {
                            gMap.remove(generalId);
                        }
                        leave = true;
                        break;
                    }
                }
                if (!leave) {
                    // monitorexit(team)
                    return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10021);
                }
                team.setCurNum(team.getCurNum() - 1);
                if (teamMember.getGeneralInfo().size() <= 0) {
                    team.getMemberMap().remove(playerId);
                }
                final StringBuilder sb = new StringBuilder();
                for (final TeamMember tm : team.getMemberMap().values()) {
                    sb.append(tm.getPlayerId()).append("#");
                    for (final GeneralInfo gi2 : tm.getGeneralInfo()) {
                        sb.append(gi2.getGeneralId()).append("#");
                    }
                    sb.append(",");
                }
                dataGetter.getPlayerTeamDao().updatePlayerGenrealIds(team.getCreator().getPlayerId(), sb.toString());
            }
            this.pushTeamMemberChange3(dataGetter.getPlayerDao().read(playerId).getForceId());
            dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(playerId, generalId);
            final OperationResult operationResult = new OperationResult(true);
            return operationResult;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public OperationResult dismissTeam(final String teamId, final int playerId, final IDataGetter dataGetter) {
        this.writeLock.lock();
        try {
            final Team team = this.teamMap.get(teamId);
            if (team == null) {
                return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10017);
            }
            for (final TeamMember tm : team.getMemberMap().values()) {
                final Map<Integer, String> gMap = this.pMap.get(tm.getPlayerId());
                if (gMap != null) {
                    for (final GeneralInfo gi : tm.getGeneralInfo()) {
                        gMap.remove(gi.getGeneralId());
                        dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(tm.getPlayerId(), gi.getGeneralId());
                    }
                }
                dataGetter.getChatService().sendSystemChat("SYS2ONE", tm.getPlayerId(), team.getCreator().getForceId(), LocalMessages.T_TEAM_CONDITION_10026, null);
            }
            final Map<Integer, String> gMap2 = this.pMap.get(playerId);
            if (gMap2 != null) {
                gMap2.remove(0);
            }
            this.forceTeamMap.put(team.getCreator().getForceId(), this.forceTeamMap.get(team.getCreator().getForceId()) - 1);
            this.teamMap.remove(team.getTeamId());
            this.pushCreateTeam(this.forceTeamMap.get(team.getCreator().getForceId()), team.getCreator().getForceId());
            this.pushTeamMemberChange3(team.getCreator().getForceId());
            final OperationResult operationResult = new OperationResult(true);
            return operationResult;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    private void pushTeamReward(final int playerId, final int exp, final int food) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("exp", exp);
        doc.createElement("food", food);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_TEAM_REWARD, doc.toByte());
    }
    
    public OperationResult teamBattle(final String teamId, final String battleId, final Player player, final IDataGetter dataGetter, final WorldLegion wl, final int teamBatType, final boolean free) {
        this.writeLock.lock();
        try {
            final Team team = this.teamMap.get(teamId);
            if (team == null) {
                return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10017);
            }
            final ConcurrentHashMap<Integer, TeamMember> map = team.getMemberMap();
            final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
            if (battle == null) {
                return new OperationResult(false, LocalMessages.BATTLE_END_INFO);
            }
            if (battle.getBattleType() != 3 && battle.getBattleType() != 14) {
                return new OperationResult(false, LocalMessages.T_COMM_10012);
            }
            final JoinTeamBattleInfo jtbi = battle.joinTeam(player, map, dataGetter, team, teamBatType);
            if (!jtbi.result) {
                return new OperationResult(false, jtbi.error);
            }
            try {
                final int pExp = dataGetter.getSerialCache().get(wl.getOwnerExpS(), player.getPlayerLv()) * team.getCurNum();
                dataGetter.getPlayerService().updateExpAndPlayerLevel(player.getPlayerId(), pExp, "\u53d1\u8d77\u4e16\u754c\u519b\u56e2\u6218\u6597\u589e\u52a0\u7ecf\u9a8c");
                this.pushTeamReward(player.getPlayerId(), pExp, 0);
            }
            catch (Exception e) {
                TeamManager.log.error("teamBattle Creator addExp Error playerId:" + player.getPlayerId());
            }
            final String cityName = ((WorldCity)dataGetter.getWorldCityCache().get((Object)battle.getDefBaseInfo().getId())).getName();
            int otherPlayerAddFood = 0;
            int generalNum = 0;
            for (final TeamMember tm : team.getMemberMap().values()) {
                if (tm.getGeneralInfo().size() <= 0) {
                    continue;
                }
                otherPlayerAddFood = 0;
                try {
                    otherPlayerAddFood = dataGetter.getSerialCache().get(wl.getPFoodS(), tm.getPlayerLv()) * tm.getGeneralInfo().size();
                    dataGetter.getPlayerResourceDao().addFoodIgnoreMax(tm.getPlayerId(), otherPlayerAddFood, "\u53c2\u52a0\u4e16\u754c\u519b\u56e2\u6218\u6597\u589e\u52a0\u7cae\u98df");
                    this.pushTeamReward(tm.getPlayerId(), 0, otherPlayerAddFood);
                }
                catch (Exception e2) {
                    TeamManager.log.error("teamBattle joinPlayer addExp Error playerId:" + player.getPlayerId());
                }
                final Map<Integer, String> gMap = this.pMap.get(tm.getPlayerId());
                if (gMap != null) {
                    for (final GeneralInfo gi : tm.getGeneralInfo()) {
                        gMap.remove(gi.getGeneralId());
                        dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(tm.getPlayerId(), gi.getGeneralId());
                    }
                }
                generalNum += tm.getGeneralInfo().size();
                final String msg = MessageFormatter.format(LocalMessages.T_TEAM_CONDITION_10027, new Object[] { cityName });
                dataGetter.getChatService().sendSystemChat("SYS2ONE", tm.getPlayerId(), player.getForceId(), msg, null);
            }
            if (free) {
                dataGetter.getPlayerBattleAttributeDao().reduceTeamTimes(player.getPlayerId(), 1);
            }
            else {
                final Chargeitem ci = (Chargeitem)dataGetter.getChargeitemCache().get((Object)60);
                dataGetter.getPlayerDao().consumeGold(player, wl.getGoldDeploy() * generalNum, ci.getName());
            }
            final Map<Integer, String> gMap2 = this.pMap.get(player.getPlayerId());
            if (gMap2 != null) {
                gMap2.remove(0);
            }
            this.forceTeamMap.put(team.getCreator().getForceId(), this.forceTeamMap.get(team.getCreator().getForceId()) - 1);
            this.teamMap.remove(team.getTeamId());
            this.pushCreateTeam(this.forceTeamMap.get(team.getCreator().getForceId()), team.getCreator().getForceId());
            final String msg2 = MessageFormatter.format(LocalMessages.T_TEAM_CONDITION_10028, new Object[] { ColorUtil.getSpecialColorMsg(team.getCreator().getPlayerName()), team.getTotalForces(), ColorUtil.getSpecialColorMsg(cityName) });
            dataGetter.getChatService().sendBigNotice("COUNTRY", new PlayerDto(player.getPlayerId(), player.getForceId()), msg2, null);
            this.pushTeamMemberChange3(team.getCreator().getForceId());
            final OperationResult operationResult = new OperationResult(true);
            operationResult.setExtraInfo(jtbi);
            return operationResult;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public OperationResult teamOrder(final Team team, final IDataGetter dataGetter) {
        this.writeLock.lock();
        try {
            for (final TeamMember tm : team.getMemberMap().values()) {
                final List<GeneralInfo> list = tm.getGeneralInfo();
                final JsonDocument doc2 = new JsonDocument();
                doc2.startObject();
                doc2.startArray("gIds");
                boolean send = false;
                final Map<Integer, PlayerGeneralMilitary> map = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(tm.getPlayerId());
                for (final GeneralInfo gi : list) {
                    if (gi.getFroces() < gi.getMaxFroces()) {
                        team.setTotalMaxForces(team.getTotalMaxForces() - gi.getMaxFroces());
                        team.setTotalForces(team.getTotalForces() - gi.getFroces());
                        try {
                            final PlayerGeneralMilitary pgm = map.get(gi.getGeneralId());
                            final int forcesMax = dataGetter.getBattleDataCache().getMaxHp(pgm);
                            if (pgm.getForces() >= forcesMax) {
                                gi.setFroces(pgm.getForces());
                                gi.setMaxFroces(forcesMax);
                                team.setTotalMaxForces(team.getTotalMaxForces() + gi.getMaxFroces());
                                team.setTotalForces(team.getTotalForces() + gi.getFroces());
                                continue;
                            }
                            final long needForces = forcesMax - pgm.getForces();
                            final General general = (General)dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
                            final Troop troop = dataGetter.getTroopCache().getTroop(general.getTroop(), tm.getPlayerId());
                            final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)pgm.getLocationId());
                            final double consume = needForces * dataGetter.getGeneralService().getRecuitConsume(troop.getId(), tm.getForceId(), worldCity);
                            if (!dataGetter.getPlayerResourceDao().consumeFood(tm.getPlayerId(), (int)consume, "\u52df\u5175\u6d88\u8017\u8d44\u6e90")) {
                                team.setTotalMaxForces(team.getTotalMaxForces() + gi.getMaxFroces());
                                team.setTotalForces(team.getTotalForces() + gi.getFroces());
                                continue;
                            }
                            dataGetter.getPlayerGeneralMilitaryDao().addGeneralForces(tm.getPlayerId(), pgm.getGeneralId(), new Date(), 0, needForces);
                            dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(tm.getPlayerId(), pgm.getGeneralId());
                            gi.setFroces(forcesMax);
                            gi.setMaxFroces(forcesMax);
                            team.setTotalMaxForces(team.getTotalMaxForces() + forcesMax);
                            team.setTotalForces(team.getTotalForces() + forcesMax);
                        }
                        catch (Exception e) {
                            TeamManager.log.error("team@teamOrder ERROR teamId:" + team.getTeamId() + " playerId:" + tm.getPlayerId() + " generalId:" + gi.getGeneralId());
                        }
                        doc2.startObject();
                        doc2.createElement("gId", gi.getGeneralId());
                        doc2.endObject();
                        send = true;
                    }
                }
                doc2.endArray();
                doc2.endObject();
                if (send) {
                    Players.push(tm.getPlayerId(), PushCommand.PUSH_GENERAL_INFO3, doc2.toByte());
                }
            }
            final OperationResult operationResult = new OperationResult(true);
            return operationResult;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public OperationResult kickOutTeam(final String teamId, final int playerId, final int kickPid, final int kickGid, final IDataGetter dataGetter) {
        this.readLock.lock();
        try {
            final Team team = this.teamMap.get(teamId);
            if (team == null) {
                return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10017);
            }
            if (team.getCreator().getPlayerId() != playerId) {
                return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10022);
            }
            synchronized (team) {
                final TeamMember teamMember = team.getMemberMap().get(kickPid);
                if (teamMember == null) {
                    // monitorexit(team)
                    return new OperationResult(false, LocalMessages.T_TEAM_CONDITION_10023);
                }
                for (int i = teamMember.getGeneralInfo().size() - 1; i >= 0; --i) {
                    if (teamMember.getGeneralInfo().get(i).getGeneralId() == kickGid) {
                        final GeneralInfo gi = teamMember.getGeneralInfo().remove(i);
                        team.setTotalMaxForces(team.getTotalMaxForces() - gi.getMaxFroces());
                        team.setTotalForces(team.getTotalForces() - gi.getFroces());
                        final Map<Integer, String> gMap = this.pMap.get(kickPid);
                        if (gMap != null) {
                            gMap.remove(kickGid);
                            team.setCurNum(team.getCurNum() - 1);
                            final StringBuilder sb = new StringBuilder();
                            sb.append(kickPid).append("_").append(gi.getGeneralId());
                            team.getKickMap().put(sb.toString(), System.currentTimeMillis() + 300000L);
                        }
                    }
                }
                if (teamMember.getGeneralInfo().size() <= 0) {
                    team.getMemberMap().remove(kickPid);
                }
                final StringBuilder sb2 = new StringBuilder();
                for (final TeamMember tm : team.getMemberMap().values()) {
                    sb2.append(tm.getPlayerId()).append("#");
                    for (final GeneralInfo gi2 : tm.getGeneralInfo()) {
                        sb2.append(gi2.getGeneralId()).append("#");
                    }
                    sb2.append(",");
                }
                dataGetter.getPlayerTeamDao().updatePlayerGenrealIds(team.getCreator().getPlayerId(), sb2.toString());
                final General general = (General)dataGetter.getGeneralCache().get((Object)kickGid);
                final String msg = MessageFormatter.format(LocalMessages.T_TEAM_CONDITION_10025, new Object[] { general.getName(), team.getCreator().getPlayerName() });
                dataGetter.getChatService().sendSystemChat("SYS2ONE", kickPid, team.getCreator().getForceId(), msg, null);
            }
            dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(kickPid, kickGid);
            this.pushTeamMemberChange3(team.getCreator().getForceId());
            final OperationResult operationResult = new OperationResult(true);
            return operationResult;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public Team getTeam(final String teamId) {
        this.readLock.lock();
        try {
            return this.teamMap.get(teamId);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public Team getCreateTeam2(final int playerId) {
        final Map<Integer, String> map = this.pMap.get(playerId);
        if (map == null) {
            return null;
        }
        final String teamId = map.get(0);
        if (teamId != null) {
            return this.getTeam2(teamId);
        }
        return null;
    }
    
    public Team getTeam2(final String teamId) {
        return this.teamMap.get(teamId);
    }
    
    public boolean isJoinTeam2(final int playerId, final int generalId) {
        final Map<Integer, String> map = this.pMap.get(playerId);
        if (map == null) {
            return false;
        }
        final String teamId = map.get(generalId);
        return teamId != null;
    }
    
    public boolean isJoinTeam(final int playerId, final int generalId) {
        final Map<Integer, String> map = this.pMap.get(playerId);
        if (map == null) {
            return false;
        }
        final String teamId = map.get(generalId);
        if (teamId == null) {
            return false;
        }
        final Team team = this.getTeam(teamId);
        if (team == null) {
            map.remove(generalId);
            return false;
        }
        return true;
    }
    
    public Team getJoinTeam(final int playerId, final int generalId) {
        final Map<Integer, String> map = this.pMap.get(playerId);
        if (map == null) {
            return null;
        }
        final String teamId = map.get(generalId);
        if (teamId == null) {
            return null;
        }
        final Team team = this.getTeam(teamId);
        if (team == null) {
            return null;
        }
        return team;
    }
    
    public Set<Team> getJoinTeam(final int playerId) {
        final Map<Integer, String> map = this.pMap.get(playerId);
        if (map == null) {
            return null;
        }
        final Set<Team> set = new HashSet<Team>();
        Team team = null;
        for (final Integer key : map.keySet()) {
            final String teamId = map.get(key);
            if (teamId != null) {
                team = this.getTeam(teamId);
                if (team == null) {
                    continue;
                }
                set.add(team);
            }
        }
        return set;
    }
    
    public Team getCreateTeam(final int playerId) {
        final Map<Integer, String> map = this.pMap.get(playerId);
        if (map == null) {
            return null;
        }
        final String teamId = map.get(0);
        if (teamId != null) {
            return this.getTeam(teamId);
        }
        return null;
    }
    
    public static boolean leagueOpen(final int forceId) {
        Integer openLeague = null;
        if (forceId == 1) {
            openLeague = Integer.valueOf(Configuration.getProperty("gcld.open.league.wei"));
        }
        else if (forceId == 2) {
            openLeague = Integer.valueOf(Configuration.getProperty("gcld.open.league.shu"));
        }
        else if (forceId == 3) {
            openLeague = Integer.valueOf(Configuration.getProperty("gcld.open.league.wu"));
        }
        return openLeague != null && openLeague != 0;
    }
    
    public static void setLeagueOpen(final int forceId) {
        if (forceId == 1) {
            Configuration.saveProperties("gcld.open.league.wei", "1", "serverstate.properties");
        }
        else if (forceId == 2) {
            Configuration.saveProperties("gcld.open.league.shu", "1", "serverstate.properties");
        }
        else if (forceId == 3) {
            Configuration.saveProperties("gcld.open.league.wu", "1", "serverstate.properties");
        }
        pushOpenLegion(forceId);
    }
    
    private static void pushOpenLegion(final int forceId) {
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        for (final PlayerDto playerDto : onlinePlayerList) {
            if (playerDto.playerId <= 0) {
                continue;
            }
            if (playerDto.forceId != forceId) {
                continue;
            }
            if (playerDto.cs[10] != '1') {
                continue;
            }
            Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("openLegion", 1));
        }
    }
    
    public static String getTeamId(final int playerId, final int forceId, final String teamType) {
        final StringBuilder sb = new StringBuilder();
        sb.append(forceId).append("_").append(playerId).append("_").append(teamType);
        return sb.toString();
    }
}
