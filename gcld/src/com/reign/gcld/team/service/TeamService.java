package com.reign.gcld.team.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.team.dao.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.team.domain.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.battle.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.log.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.team.common.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.battle.scene.*;
import java.io.*;

@Component("teamService")
public class TeamService implements ITeamService
{
    private static final Logger log;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerTeamDao playerTeamDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private WorldLegionCache worldLegionCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    private static final Logger timerLog;
    
    static {
        log = CommonLog.getLog(TeamService.class);
        timerLog = new TimerLogger();
    }
    
    @Transactional
    @Override
    public byte[] createTeam(final PlayerDto playerDto, final int maxNumType, String teamName) {
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[59] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10034);
        }
        teamName = String.valueOf(playerDto.playerName) + LocalMessages.T_TEAM_CONDITION_10029;
        final Player player = this.playerDao.read(playerId);
        final WorldLegion wl = (WorldLegion)this.worldLegionCache.get((Object)maxNumType);
        if (wl == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int maxNum = wl.getMax();
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)59);
        if (player.getConsumeLv() < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final String teamId = TeamManager.getTeamId(player.getPlayerId(), player.getForceId(), "batTeam");
        Team team = TeamManager.getInstance().getTeam(teamId);
        if (team != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10010);
        }
        final PlayerBattleAttribute pba = this.playerBattleAttributeDao.read(playerId);
        if ((pba == null || pba.getTeamTimes() <= 0) && !this.playerDao.consumeGold(player, wl.getGoldInit(), ci.getName())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        team = TeamManager.getInstance().createTeam(teamId, teamName, maxNum, player, "batTeam", wl.getId(), this.dataGetter);
        final PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setPlayerGeneralId("");
        playerTeam.setPlayerId(player.getPlayerId());
        playerTeam.setTeamId(teamId);
        playerTeam.setTeamName(teamName);
        playerTeam.setWorldLegionId(wl.getId());
        playerTeam.setCreateTime(System.currentTimeMillis());
        this.playerTeamDao.create(playerTeam);
        final StringBuilder sb = new StringBuilder();
        sb.append(player.getPlayerId()).append("#").append(team.getTeamId()).append("#").append(team.getCreateTime().getTime());
        this.jobService.addJob("teamService", "cancelTeam", sb.toString(), System.currentTimeMillis() + 3600000L);
        final String msg = MessageFormatter.format(LocalMessages.T_TEAM_CONDITION_10035, new Object[] { ColorUtil.getSpecialColorMsg(team.getCreator().getPlayerName()) });
        this.dataGetter.getChatService().sendSystemChat("GLOBAL", player.getPlayerId(), player.getForceId(), msg, null);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", team.getTeamId());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void cancelTeam(final String param) {
        final long start = System.currentTimeMillis();
        TeamService.timerLog.info(LogUtil.formatThreadLog("TeamService", "cancelTeam", 0, 0L, "param:" + param));
        final String[] str = param.split("#");
        final int playerId = Integer.valueOf(str[0]);
        final long createTime = Long.valueOf(str[2]);
        final Team team = TeamManager.getInstance().getTeam(str[1]);
        if (team != null && team.getCreateTime().getTime() <= createTime) {
            final OperationResult opRes = TeamManager.getInstance().dismissTeam(str[1], playerId, this.dataGetter);
            if (!opRes.getResult()) {
                TeamService.log.error("TeamService cancelTeam " + opRes.getResultContent());
            }
            this.playerTeamDao.deleteById(playerId);
            TeamService.log.info("TeamService cancelTeam createPlayerId[" + playerId + "] createTime[" + team.getCreateTime().getTime() + "]");
        }
        TeamService.timerLog.info(LogUtil.formatThreadLog("TeamService", "cancelTeam", 2, System.currentTimeMillis() - start, "param:" + param));
    }
    
    @Override
    public byte[] closeTeamInfo(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("close", 1);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getTeamInfo(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final List<WorldLegion> wlList = this.worldLegionCache.getModels();
        doc.startArray("types");
        for (final WorldLegion wl : wlList) {
            doc.startObject();
            doc.createElement("teamType", wl.getId());
            doc.createElement("maxNum", wl.getMax());
            doc.createElement("cost", wl.getGoldInit());
            final int s = this.dataGetter.getSerialCache().get(wl.getOwnerExpS(), playerDto.playerLv);
            doc.createElement("exp", s * wl.getMax());
            doc.endObject();
        }
        doc.endArray();
        final PlayerBattleAttribute pba = this.playerBattleAttributeDao.read(playerId);
        if (pba != null && pba.getTeamTimes() > 0) {
            doc.createElement("teamTimes", pba.getTeamTimes());
        }
        doc.startArray("teamList");
        final Set<Team> teamSet = TeamManager.getInstance().getJoinTeam(playerId);
        if (teamSet != null && teamSet.size() > 0) {
            for (final Team team : teamSet) {
                if (team.getCreator().getPlayerId() == playerId) {
                    if (team.getCreator().getPlayerLv() < playerDto.playerLv) {
                        team.getCreator().setPlayerLv(playerDto.playerLv);
                        final WorldLegion wl2 = (WorldLegion)this.dataGetter.getWorldLegionCache().get((Object)team.getWorldLegionId());
                        final int s2 = this.dataGetter.getSerialCache().get(wl2.getOwnerExpS(), playerDto.playerLv);
                        team.setOwnerAddExp(s2);
                    }
                    teamJoinInfo(doc, team, playerId);
                }
            }
            for (final Team team : teamSet) {
                if (team.getCreator().getPlayerId() != playerId) {
                    teamJoinInfo(doc, team, playerId);
                }
            }
        }
        for (final Team team : TeamManager.getInstance().getAllTeam()) {
            if (team.getCreator().getForceId() != forceId) {
                continue;
            }
            if (team.getCreator().getPlayerId() == playerId) {
                continue;
            }
            if (team.getMemberMap().containsKey(playerId)) {
                continue;
            }
            teamJoinInfo(doc, team, playerId);
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getGeneralInfo(final PlayerDto playerDto, final String teamId, final int teamType) {
        final int playerId = playerDto.playerId;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.IN_JUBEN_NOT_JOIN_TEAM);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryListOrder(playerId);
        final Set<Integer> joinedSet = new HashSet<Integer>();
        final Set<Team> set = TeamManager.getInstance().getJoinTeam(playerId);
        if (set != null) {
            for (final Team team : set) {
                final TeamMember tm = team.getMemberMap().get(playerId);
                if (tm == null) {
                    continue;
                }
                final List<GeneralInfo> list = tm.getGeneralInfo();
                if (list == null) {
                    continue;
                }
                for (final GeneralInfo gi : list) {
                    joinedSet.add(gi.getGeneralId());
                }
            }
        }
        if (teamId == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        Team team = TeamManager.getInstance().getTeam(teamId);
        if (team == null || team.isOrder()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10040);
        }
        doc.startArray("military");
        int generalNum = 0;
        final StringBuilder sb = new StringBuilder();
        final long now = System.currentTimeMillis();
        String tip = null;
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pgm = pgmList.get(i);
            tip = "";
            if (pgm.getState() >= 24) {
                tip = LocalMessages.FARM_FARM;
            }
            else if (pgm.getState() > 1) {
                tip = LocalMessages.BATTLE_INT_BATTLE;
            }
            if (joinedSet.contains(pgm.getGeneralId())) {
                tip = LocalMessages.BATTLE_INT_BATTLE_TEAM;
            }
            sb.delete(0, sb.length());
            sb.append(pgm.getPlayerId()).append("_").append(pgm.getGeneralId());
            if (team.getKickMap().containsKey(sb.toString()) && team.getKickMap().get(sb.toString()) > now) {
                tip = LocalMessages.T_TEAM_CONDITION_10043;
            }
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
            if (gmd != null && (gmd.cityState == 22 || gmd.cityState == 23)) {
                tip = LocalMessages.GENERAL_IN_CELL;
            }
            ++generalNum;
            final General gm = (General)this.dataGetter.getGeneralCache().get((Object)pgm.getGeneralId());
            doc.startObject();
            doc.createElement("generalId", pgm.getGeneralId());
            doc.createElement("generalName", gm.getName());
            doc.createElement("generalLv", pgm.getLv());
            doc.createElement("quality", gm.getQuality());
            doc.createElement("pic", gm.getPic());
            doc.createElement("forcesMax", this.dataGetter.getBattleDataCache().getMaxHp(pgm));
            doc.createElement("forces", pgm.getForces());
            doc.createElement("joined", joinedSet.contains(pgm.getGeneralId()));
            doc.createElement("state", pgm.getState());
            doc.createElement("tip", tip);
            doc.endObject();
            joinedSet.add(pgm.getGeneralId());
        }
        doc.endArray();
        WorldLegion wl = (WorldLegion)this.worldLegionCache.get((Object)teamType);
        if (wl != null) {
            doc.createElement("food", this.dataGetter.getSerialCache().get(wl.getPFoodS(), playerDto.playerLv));
        }
        else {
            wl = (WorldLegion)this.worldLegionCache.get((Object)1);
            doc.createElement("food", this.dataGetter.getSerialCache().get(wl.getPFoodS(), playerDto.playerLv));
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    public static void teamJoinInfo(final JsonDocument doc, final Team team, final int playerId) {
        doc.startObject();
        doc.createElement("teamId", team.getTeamId());
        doc.createElement("teamName", team.getTeamName());
        doc.createElement("creatorName", team.getCreator().getPlayerName());
        doc.createElement("generalNum", team.getCurNum());
        doc.createElement("generalMaxNum", team.getMaxNum());
        doc.createElement("pic", team.getCreator().getPic());
        doc.createElement("totalForces", team.getTotalForces());
        doc.createElement("totalMaxForces", team.getTotalMaxForces());
        doc.createElement("lv", team.getCreator().getPlayerLv());
        doc.createElement("teamExp", team.getOwnerAddExp() * team.getCurNum());
        long countDown = 3600000L - (System.currentTimeMillis() - team.getCreateTime().getTime());
        countDown = ((countDown > 0L) ? countDown : 0L);
        doc.createElement("countDown", countDown);
        boolean isCreator = false;
        if (team.getCreator().getPlayerId() == playerId) {
            isCreator = true;
        }
        doc.createElement("isCreator", isCreator);
        doc.createElement("creator", team.getCreator().getPlayerId());
        final TeamMember ownerTm = team.getMemberMap().get(playerId);
        if (ownerTm != null) {
            doc.startArray("myGeneral");
            for (final GeneralInfo gi : ownerTm.getGeneralInfo()) {
                doc.startObject();
                doc.createElement("gId", gi.getGeneralId());
                doc.createElement("gName", gi.getGeneralName());
                doc.endObject();
            }
            doc.endArray();
        }
        doc.startArray("generalList");
        if (ownerTm != null) {
            memberJoinInfo(doc, ownerTm);
        }
        final List<TeamMember> mList = new ArrayList<TeamMember>();
        for (final TeamMember tm : team.getMemberMap().values()) {
            if (tm.getPlayerId() == playerId) {
                continue;
            }
            mList.add(tm);
        }
        Collections.sort(mList, new TeamComparator());
        for (final TeamMember tm : mList) {
            memberJoinInfo(doc, tm);
        }
        doc.endArray();
        doc.endObject();
    }
    
    public static void memberJoinInfo(final JsonDocument doc, final TeamMember tm) {
        Collections.sort(tm.getGeneralInfo(), new TeamGeneralComparator());
        for (final GeneralInfo gi : tm.getGeneralInfo()) {
            doc.startObject();
            doc.createElement("pId", tm.getPlayerId());
            doc.createElement("pName", tm.getPlayerName());
            doc.createElement("generalId", gi.getGeneralId());
            doc.createElement("generalLv", gi.getGeneralLv());
            doc.createElement("generalQuality", gi.getGeneralQuality());
            doc.createElement("generalName", gi.getGeneralName());
            doc.endObject();
        }
    }
    
    @Transactional
    @Override
    public byte[] joinTeam(final PlayerDto playerDto, final String teamId, final String gIds) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final int playerId = playerDto.playerId;
        if (gIds == null || gIds.length() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.IN_JUBEN_NOT_JOIN_TEAM);
        }
        final Set<Team> teamSet = TeamManager.getInstance().getJoinTeam(playerId);
        final Set<Integer> set = new HashSet<Integer>();
        if (teamSet != null) {
            for (final Team team : teamSet) {
                if (team.getMemberMap().get(playerId) != null) {
                    final List<GeneralInfo> giList = team.getMemberMap().get(playerId).getGeneralInfo();
                    if (giList == null || giList.size() <= 0) {
                        continue;
                    }
                    for (final GeneralInfo gi : giList) {
                        set.add(gi.getGeneralId());
                    }
                }
            }
        }
        final String[] generalIds = gIds.split("#");
        final Map<Integer, PlayerGeneralMilitary> pgmMap = this.playerGeneralMilitaryDao.getMilitaryMap(playerId);
        final List<PlayerGeneralMilitary> pgmList = new ArrayList<PlayerGeneralMilitary>();
        int gId = 0;
        PlayerGeneralMilitary pgm = null;
        final StringBuilder sb = new StringBuilder();
        final Team team2 = TeamManager.getInstance().getTeam(teamId);
        final long now = System.currentTimeMillis();
        String[] array;
        for (int length = (array = generalIds).length, i = 0; i < length; ++i) {
            final String str = array[i];
            gId = Integer.valueOf(str);
            if (gId > 0) {
                pgm = pgmMap.get(gId);
                if (pgm != null) {
                    if (pgm.getState() <= 1) {
                        final int maxHp = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
                        pgm.getForces();
                        if (!set.contains(gId)) {
                            if (team2 != null) {
                                sb.delete(0, sb.length());
                                sb.append(pgm.getPlayerId()).append("_").append(pgm.getGeneralId());
                                if (team2.getKickMap().containsKey(sb.toString()) && team2.getKickMap().get(sb.toString()) > now) {
                                    continue;
                                }
                            }
                            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(pgm.getPlayerId(), pgm.getGeneralId());
                            if (gmd != null) {
                                if (gmd.cityState == 22) {
                                    continue;
                                }
                                if (gmd.cityState == 23) {
                                    continue;
                                }
                            }
                            pgmList.add(pgm);
                        }
                    }
                }
            }
        }
        if (pgmList.size() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10021);
        }
        int consume = 0;
        for (final Integer gidTemp : set) {
            final PlayerGeneralMilitary playerGm = pgmMap.get(gidTemp);
            final int forcesMax = this.dataGetter.getBattleDataCache().getMaxHp(playerGm);
            final long needForces = forcesMax - playerGm.getForces();
            final General general = (General)this.dataGetter.getGeneralCache().get((Object)playerGm.getGeneralId());
            final Troop troop = this.dataGetter.getTroopCache().getTroop(general.getTroop(), playerGm.getPlayerId());
            final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)playerGm.getLocationId());
            consume += (int)(needForces * this.dataGetter.getGeneralService().getRecuitConsume(troop.getId(), playerGm.getForceId(), worldCity));
        }
        for (final PlayerGeneralMilitary playerGm2 : pgmList) {
            final int forcesMax2 = this.dataGetter.getBattleDataCache().getMaxHp(playerGm2);
            final long needForces2 = forcesMax2 - playerGm2.getForces();
            final General general2 = (General)this.dataGetter.getGeneralCache().get((Object)playerGm2.getGeneralId());
            final Troop troop2 = this.dataGetter.getTroopCache().getTroop(general2.getTroop(), playerGm2.getPlayerId());
            final WorldCity worldCity2 = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)playerGm2.getLocationId());
            consume += (int)(needForces2 * this.dataGetter.getGeneralService().getRecuitConsume(troop2.getId(), playerGm2.getForceId(), worldCity2));
        }
        final PlayerResource pr = this.dataGetter.getPlayerResourceDao().read(playerId);
        if (pr.getFood() < consume) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10027);
        }
        final Player player = this.playerDao.read(playerId);
        final OperationResult opRes = TeamManager.getInstance().joinTeam(teamId, player, pgmList, this.dataGetter);
        if (!opRes.getResult()) {
            return JsonBuilder.getJson(State.FAIL, opRes.getResultContent());
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", teamId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] kickOutTeam(final PlayerDto playerDto, final String teamId, final int kickPid, final int kickGid) {
        final int playerId = playerDto.playerId;
        final OperationResult opRes = TeamManager.getInstance().kickOutTeam(teamId, playerId, kickPid, kickGid, this.dataGetter);
        if (!opRes.getResult()) {
            return JsonBuilder.getJson(State.FAIL, opRes.getResultContent());
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("kickPid", kickPid);
        doc.createElement("kickGid", kickGid);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] dismissTeam(final PlayerDto playerDto, final String teamId) {
        final int playerId = playerDto.playerId;
        final Team team = TeamManager.getInstance().getTeam(teamId);
        if (team == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10017);
        }
        if (team.getCreator().getPlayerId() != playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10024);
        }
        final OperationResult opRes = TeamManager.getInstance().dismissTeam(teamId, playerId, this.dataGetter);
        if (!opRes.getResult()) {
            return JsonBuilder.getJson(State.FAIL, opRes.getResultContent());
        }
        this.playerTeamDao.deleteById(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", teamId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] leaveTeam(final PlayerDto playerDto, String teamId, final int generalId) {
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE);
        }
        final int playerId = playerDto.playerId;
        Team team = null;
        if (teamId == null || teamId.endsWith("")) {
            team = TeamManager.getInstance().getJoinTeam(playerId, generalId);
            if (team == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10017);
            }
            teamId = team.getTeamId();
        }
        else {
            team = TeamManager.getInstance().getTeam(teamId);
        }
        if (team == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10017);
        }
        final OperationResult opRes = TeamManager.getInstance().leaveTeam(teamId, playerId, generalId, this.dataGetter);
        if (!opRes.getResult()) {
            return JsonBuilder.getJson(State.FAIL, opRes.getResultContent());
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", teamId);
        doc.createElement("generalId", generalId);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getBatCost(final PlayerDto playerDto, final int teamType) {
        final Team team = TeamManager.getInstance().getCreateTeam(playerDto.playerId);
        if (team == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10017);
        }
        final WorldLegion wl = (WorldLegion)this.worldLegionCache.get((Object)teamType);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", team.getTeamId());
        final PlayerBattleAttribute pba = this.playerBattleAttributeDao.read(playerDto.playerId);
        if (pba != null && pba.getTeamTimes() > 0) {
            doc.createElement("free", true);
        }
        else {
            doc.createElement("free", false);
        }
        doc.createElement("gold", team.getCurNum() * wl.getGoldDeploy());
        doc.createElement("curNum", team.getCurNum());
        doc.createElement("maxNum", team.getMaxNum());
        doc.createElement("teamExp", team.getOwnerAddExp() * team.getCurNum());
        doc.createElement("totalForces", team.getTotalForces());
        doc.createElement("totalMaxForces", team.getTotalMaxForces());
        doc.createElement("order", team.isOrder());
        final Chargeitem orderCi = (Chargeitem)this.chargeitemCache.get((Object)72);
        final int cost = (int)Math.ceil((1.0 - team.getTotalForces() * 1.0 / team.getTotalMaxForces()) * orderCi.getCost() * team.getCurNum());
        doc.createElement("orderGold", cost);
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)63);
        doc.createElement("inspireCost", ci.getCost());
        doc.createElement("inspireEffect", team.getInspireEffect() * 100.0);
        final int addExp = 500 * ci.getCost();
        doc.createElement("addExp", addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] teamInspire(final PlayerDto playerDto, final String teamId) {
        final Team team = TeamManager.getInstance().getTeam(teamId);
        if (team == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10017);
        }
        if (team.getCreator().getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (team.getInspireEffect() > 0.0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10036);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)63);
        final Player player = this.playerDao.read(playerDto.playerId);
        if (!this.playerDao.consumeGold(player, ci)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final int addExp = 500 * ci.getCost();
        team.setInspireEffect(ci.getParam());
        final AddExpInfo addExpInfo = this.playerService.updateExpAndPlayerLevel(playerDto.playerId, addExp, "\u96c6\u56e2\u519b\u9f13\u821e\u589e\u52a0\u7ecf\u9a8c");
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final WorldLegion wl = (WorldLegion)this.worldLegionCache.get((Object)team.getWorldLegionId());
        doc.createElement("teamId", team.getTeamId());
        doc.createElement("gold", team.getCurNum() * wl.getGoldDeploy());
        doc.createElement("curNum", team.getCurNum());
        doc.createElement("maxNum", team.getMaxNum());
        doc.createElement("teamExp", team.getOwnerAddExp() * team.getCurNum());
        doc.createElement("inspireCost", ci.getCost());
        doc.createElement("inspireEffect", team.getInspireEffect() * 100.0);
        doc.createElement("addExp", addExpInfo.addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] teamOrder(final PlayerDto playerDto, final String teamId) {
        final Team team = TeamManager.getInstance().getTeam(teamId);
        if (team == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10017);
        }
        if (team.getCreator().getPlayerId() != playerDto.playerId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (team.isOrder()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10039);
        }
        if (team.getCurNum() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10042);
        }
        if (team.getTotalForces() >= team.getTotalMaxForces()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10041);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)72);
        final int cost = (int)Math.ceil((1.0 - team.getTotalForces() * 1.0 / team.getTotalMaxForces()) * ci.getCost() * team.getCurNum());
        final Player player = this.playerDao.read(playerDto.playerId);
        if (!this.playerDao.consumeGold(player, cost, ci.getName())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final int addExp = (int)(ci.getParam() * cost);
        final AddExpInfo addExpInfo = this.playerService.updateExpAndPlayerLevel(playerDto.playerId, addExp, "\u96c6\u56e2\u519b\u53f7\u4ee4\u589e\u52a0\u7ecf\u9a8c");
        team.setOrder(true);
        TeamManager.getInstance().teamOrder(team, this.dataGetter);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("teamId", team.getTeamId());
        doc.createElement("order", team.isOrder());
        doc.createElement("addOrderExp", addExpInfo.addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] teamBattle(final PlayerDto playerDto, final String battleId, final int curNum, final int teamBatType) {
        if (teamBatType < 0 || teamBatType > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_END_INFO);
        }
        final boolean hzFlag = this.dataGetter.getHuiZhanService().isHuiZhanInStatePreparation(battle.getDefBaseInfo().getId());
        if (hzFlag) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TEAM_CAN_NOT_USE_IN_HUIZHAN);
        }
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final Team team = TeamManager.getInstance().getCreateTeam(playerId);
        if (team == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TEAM_CONDITION_10017);
        }
        final WorldLegion wl = (WorldLegion)this.worldLegionCache.get((Object)team.getWorldLegionId());
        if (curNum != team.getCurNum()) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("teamBattle", false);
            doc.createElement("gold", team.getCurNum() * wl.getGoldDeploy());
            doc.createElement("curNum", team.getCurNum());
            doc.createElement("maxNum", team.getMaxNum());
            doc.createElement("teamExp", team.getOwnerAddExp() * team.getCurNum());
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final PlayerBattleAttribute pba = this.playerBattleAttributeDao.read(playerId);
        boolean free = false;
        if (pba != null && pba.getTeamTimes() > 0) {
            free = true;
        }
        else if (!this.playerDao.canConsumeMoney(player, team.getCurNum() * wl.getGoldDeploy())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final OperationResult opRes = TeamManager.getInstance().teamBattle(team.getTeamId(), battleId, player, this.dataGetter, wl, teamBatType, free);
        if (!opRes.getResult()) {
            return JsonBuilder.getJson(State.FAIL, opRes.getResultContent());
        }
        this.playerTeamDao.deleteById(playerId);
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("teamBattle", true);
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    @Override
    public void initTeam() {
        final List<PlayerTeam> list = this.dataGetter.getPlayerTeamDao().getModels();
        for (final PlayerTeam pt : list) {
            TeamManager.getInstance().initCreateTeam(pt, "batTeam", this.dataGetter);
        }
    }
}
