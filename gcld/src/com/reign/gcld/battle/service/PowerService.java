package com.reign.gcld.battle.service;

import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.tavern.dao.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.juben.dao.*;
import com.reign.gcld.sdata.cache.*;
import java.util.concurrent.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.event.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.common.*;
import org.apache.commons.lang.*;
import com.reign.gcld.juben.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.tavern.domain.*;
import java.util.*;
import com.reign.gcld.battle.scene.*;
import com.reign.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.task.domain.*;

@Component("powerService")
public class PowerService implements IPowerService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerArmyDao playerArmyDao;
    @Autowired
    private IPlayerArmyExtraDao playerArmyExtraDao;
    @Autowired
    private IPlayerArmyRewardDao playerArmyRewardDao;
    @Autowired
    private ArmiesRewardCache armiesRewardCache;
    @Autowired
    private IPlayerPowerDao playerPowerDao;
    @Autowired
    private ArmiesCache armiesCache;
    @Autowired
    private PowerCache powerCache;
    @Autowired
    private IPlayerBattleRewardDao playerBattleRewardDao;
    @Autowired
    private IPlayerBattleAutoDao playerBattleAutoDao;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IPlayerTavernDao playerTavernDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private IPlayerScenarioDao playerScenarioDao;
    @Autowired
    private SoloDramaCache soloDramaCache;
    private static final ErrorSceneLog errorSceneLog;
    public static ConcurrentHashMap<Integer, String[]> batInfoMap;
    public static int[] indexs;
    
    static {
        errorSceneLog = ErrorSceneLog.getInstance();
        PowerService.batInfoMap = new ConcurrentHashMap<Integer, String[]>();
        PowerService.indexs = new int[500];
    }
    
    public static String[] getBatInfo(final int armiesId) {
        return PowerService.batInfoMap.get(armiesId);
    }
    
    @Transactional
    @Override
    public byte[] switchPowerInfo(final PlayerDto playerDto, final int powerId) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerPower curPlayerPower = this.playerPowerDao.getPlayerPower(playerId, powerId);
        if (curPlayerPower == null && powerId > 100) {
            this.playerDao.updatePowerId(playerId, this.dataGetter.getPowerCache().fromExtraToArmy(powerId));
            return this.getPowerInfo(playerDto);
        }
        if (curPlayerPower == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("plug attack.").append("powerId", powerId).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).appendMethodName("switchPowerInfo").appendClassName("PowerService").flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (curPlayerPower.getAttackable() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.POWER_CANNOT_ATTACKABLE);
        }
        if (powerId == 2) {
            EventListener.fireEvent(new CommonEvent(28, playerId));
        }
        if (powerId == 3) {
            EventListener.fireEvent(new CommonEvent(32, playerId));
        }
        if (powerId == 5) {
            EventListener.fireEvent(new CommonEvent(37, playerId));
        }
        if (powerId == 6) {
            EventListener.fireEvent(new CommonEvent(38, playerId));
        }
        if (powerId == 7) {
            EventListener.fireEvent(new CommonEvent(39, playerId));
        }
        this.playerDao.updatePowerId(playerId, powerId);
        return this.getPowerInfo(playerDto);
    }
    
    @Transactional
    @Override
    public void pushBattleRewardInfo(final int playerId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final List<PlayerBattleReward> pbrList = this.playerBattleRewardDao.getListBy2Type(playerId, 1, 2);
        doc.startArray("results");
        int num = 1;
        for (final PlayerBattleReward pbr : pbrList) {
            if (num > 5) {
                this.playerBattleRewardDao.deleteById(pbr.getVId());
            }
            else {
                ++num;
                doc.startObject();
                doc.createElement("vId", pbr.getVId());
                doc.createElement("defId", pbr.getDefId());
                final Armies armies = (Armies)this.armiesCache.get((Object)pbr.getDefId());
                doc.createElement("name", armies.getName());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_BATTLE_REWARD, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getPowerInfo(final PlayerDto playerDto) {
        final String sysTopLv = Configuration.getProperty("gcld.sys.player.lv");
        int topLv = Integer.MAX_VALUE;
        if (StringUtils.isNotBlank(sysTopLv)) {
            topLv = Integer.valueOf(sysTopLv);
        }
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        int powerId = player.getPowerId();
        if (powerId > 100) {
            final PlayerPower playerPower = this.dataGetter.getPlayerPowerDao().getPlayerPower(playerId, powerId);
            if (playerPower != null && playerPower.getExpireTime().getTime() >= System.currentTimeMillis()) {
                return this.getExtraPowerInfo(playerDto, powerId);
            }
            powerId = this.dataGetter.getPowerCache().fromExtraToArmy(powerId);
            this.playerDao.updatePowerId(playerId, powerId);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("topLv", topLv);
        final List<PlayerScenario> psList = this.playerScenarioDao.getScenarioByPid(playerId);
        if (psList != null) {
            int maxLv = 0;
            String soloName = null;
            for (final PlayerScenario ps : psList) {
                if (this.dataGetter.getWdSjpDramaCache().getWorldDramaByDramaId(ps.getScenarioId()) != null) {
                    continue;
                }
                final SoloDrama soloDrama = (SoloDrama)this.soloDramaCache.get((Object)ps.getScenarioId());
                if (maxLv >= soloDrama.getLv()) {
                    continue;
                }
                maxLv = soloDrama.getLv();
                soloName = soloDrama.getName();
            }
            if (soloName != null) {
                doc.createElement("soloName", soloName);
            }
        }
        final Power power = (Power)this.powerCache.get((Object)powerId);
        PlayerPower curPlayerPower = null;
        final Power prePower = this.powerCache.getPrePower(powerId);
        final int prePowerId = (prePower != null) ? prePower.getId() : 0;
        final Power nextPower = (Power)this.powerCache.get((Object)power.getNextPower());
        final int nextPowerId = (nextPower != null) ? nextPower.getId() : 0;
        PlayerPower nextPlayerPower = null;
        final int extraId = this.dataGetter.getPowerCache().fromArmyToExtra(powerId);
        PlayerPower extraPower = null;
        final List<PlayerPower> fourPlayerPower = this.playerPowerDao.getFourPlayerPower(playerId, powerId, prePowerId, nextPowerId, extraId);
        for (final PlayerPower pp : fourPlayerPower) {
            if (pp.getPowerId() == powerId) {
                curPlayerPower = pp;
            }
            else if (nextPower != null && pp.getPowerId() == nextPower.getId()) {
                nextPlayerPower = pp;
            }
            else {
                if (pp.getPowerId() != extraId) {
                    continue;
                }
                extraPower = pp;
            }
        }
        if (prePower != null) {
            doc.createElement("prePowerId", prePower.getId());
            doc.createElement("prePowerName", prePower.getName());
            doc.createElement("prePowerIntro", prePower.getIntro());
        }
        if (curPlayerPower == null) {
            curPlayerPower = this.createPlayerPower(playerId, powerId, 1);
            this.createPlayerArmies(playerId, powerId);
        }
        if (nextPower != null && (nextPower.getId() + 1) * 10 <= topLv) {
            if (nextPlayerPower == null) {
                final int attackable = this.caculateNextPowerAttackable(playerId, powerId);
                nextPlayerPower = this.createPlayerPower(playerId, nextPower.getId(), attackable);
            }
            doc.createElement("nextPowerId", nextPower.getId());
            doc.createElement("nextPowerName", nextPower.getName());
            doc.createElement("nextPowerIntro", nextPower.getIntro());
            doc.createElement("attackable", nextPlayerPower.getAttackable() == 1);
        }
        if (extraPower != null) {
            if (System.currentTimeMillis() > extraPower.getExpireTime().getTime()) {
                this.dataGetter.getPlayerArmyExtraDao().deleteByPlayerIdPowerId(extraPower.getPlayerId(), extraPower.getPowerId());
                this.dataGetter.getPlayerPowerDao().updateState(extraPower.getPlayerId(), extraPower.getPowerId(), 1);
                extraPower.setState(1);
            }
            final Power exPower = (Power)this.dataGetter.getPowerCache().get((Object)extraId);
            final List<ArmiesExtra> armiesExtraList = this.dataGetter.getArmiesExtraCache().getArmiesExtraByPowerId(extraId);
            final int id = armiesExtraList.size() - 2;
            final int generalId = armiesExtraList.get(id).getChief();
            final General general = (General)this.dataGetter.getGeneralCache().get((Object)generalId);
            if (extraPower.getState() == -1 || extraPower.getState() == 0) {
                final List<PlayerArmyExtra> paeList = this.playerArmyExtraDao.getArmiesByPowerIdAndAttackable(playerId, extraPower.getPowerId(), 1);
                if (paeList != null && paeList.size() > 0) {
                    doc.createElement("extraType", 0);
                    doc.createElement("extraPowerId", extraId);
                    doc.createElement("extraName", exPower.getName());
                    doc.createElement("extraPic", general.getPic());
                    doc.createElement("extraCountDown", extraPower.getExpireTime().getTime() - System.currentTimeMillis());
                    if (extraPower.getState() == -1) {
                        doc.createElement("extraPlot", true);
                        this.playerPowerDao.updateState(playerId, extraId, 0);
                    }
                    else {
                        doc.createElement("extraPlot", false);
                    }
                }
                else {
                    this.dataGetter.getPlayerArmyExtraDao().deleteByPlayerIdPowerId(extraPower.getPlayerId(), extraPower.getPowerId());
                    this.dataGetter.getPlayerPowerDao().updateState(extraPower.getPlayerId(), extraPower.getPowerId(), 1);
                }
            }
            else if (extraPower.getState() == 1) {
                doc.createElement("extraType", 1);
                doc.createElement("extraPowerId", extraId);
                doc.createElement("extraName", exPower.getName());
                doc.createElement("extraPic", general.getPic());
                doc.createElement("extraquality", general.getQuality());
                final int gold = exPower.getGoldInit() + exPower.getGoldIncrease() * extraPower.getBuyCount();
                doc.createElement("extraGold", gold);
            }
        }
        doc.createElement("type", 1);
        doc.createElement("powerId", power.getId());
        doc.createElement("powerName", power.getName());
        doc.createElement("powerIntro", power.getIntro());
        doc.createElement("autoCount", this.playerBattleAttributeDao.read(playerId).getArmiesAutoCount());
        List<PlayerArmy> paList = this.playerArmyDao.getPlayerPowerArmies(playerId, powerId);
        if (paList.size() <= 0) {
            this.createPlayerArmies(playerId, powerId);
            paList = this.playerArmyDao.getPlayerPowerArmies(playerId, powerId);
        }
        final PlayerBattleAuto pba = this.playerBattleAutoDao.read(playerId);
        int autoAtt = 0;
        if (pba != null) {
            autoAtt = pba.getDefId();
        }
        doc.startArray("npcs");
        for (int i = 0; i < paList.size(); ++i) {
            doc.startObject();
            final PlayerArmy pa = paList.get(i);
            final int npcId = pa.getArmyId();
            final Armies armies = (Armies)this.armiesCache.get((Object)npcId);
            if (pa.getWinNum() <= 0 && pa.getAttackable() == 1) {
                doc.createElement("attLv", armies.getLevel());
            }
            else {
                doc.createElement("attLv", 0);
            }
            doc.createElement("npcId", armies.getId());
            doc.createElement("npcName", armies.getName());
            doc.createElement("npcIntro", armies.getIntro());
            doc.createElement("attackable", pa.getAttackable() == 1);
            doc.createElement("type", (armies.getType() == 1) ? 1 : 2);
            doc.createElement("quality", ((General)this.generalCache.get((Object)armies.getChief())).getQuality());
            doc.createElement("firstWin", pa.getFirstWin());
            doc.createElement("firstAtt", pa.getAttNum() == 0);
            if (pa.getFirstWin() == 1) {
                this.playerArmyDao.resetFirstWin(pa.getPlayerId(), pa.getArmyId(), 0);
            }
            if (armies.getType() == 3) {
                doc.createElement("startTime", new StringBuilder(String.valueOf(BattleConstant.CAN_BATTLESTART_HOUR)).toString());
                doc.createElement("endTime", new StringBuilder(String.valueOf(BattleConstant.CAN_BATTLE_END_HOUR)).toString());
                doc.createElement("countTime", BattleConstant.getCountdownTime(BattleConstant.CAN_BATTLESTART_HOUR, BattleConstant.CAN_BATTLE_END_HOUR));
            }
            if (armies.getTerrain() < 6) {
                doc.createElement("terrain", armies.getTerrain());
            }
            if (armies.getId() == autoAtt) {
                doc.createElement("mode", pba.getMode());
                doc.createElement("times", pba.getTimes());
            }
            if (pa.getWinNum() < 1 && (pa.getAttackable() == 1 || (pa.getAttackable() == 0 && i > 0 && paList.get(i - 1).getAttackable() == 1) || (pa.getAttackable() == 0 && i > 1 && paList.get(i - 1).getAttackable() == 0 && paList.get(i - 2).getAttackable() == 1))) {
                final String[] strs = PowerService.batInfoMap.get(armies.getId());
                if (strs != null) {
                    doc.startArray("batInfos");
                    String[] array;
                    for (int length = (array = strs).length, j = 0; j < length; ++j) {
                        final String str = array[j];
                        if (str != null) {
                            doc.startObject();
                            final String[] ss = str.split("#");
                            doc.createElement("forceId", ss[0]);
                            doc.createElement("info", ss[1]);
                            doc.endObject();
                        }
                    }
                    doc.endArray();
                }
            }
            if (armies.getType() != 1) {
                final String battleId = NewBattleManager.getBattleId(2, playerDto.forceId, npcId);
                final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
                if (battle != null) {
                    doc.createElement("battle", true);
                }
                else {
                    doc.createElement("battle", false);
                }
            }
            doc.createElement("attacked", pa.getWinNum() > 0);
            doc.createElement("terrain", armies.getTerrain());
            if (armies.getDropMap() != null && armies.getDropMap().get(101) != null) {
                final int dropGId = armies.getDropMap().get(101).id;
                final General general2 = (General)this.generalCache.get((Object)dropGId);
                if (general2 != null) {
                    boolean show = true;
                    final PlayerTavern playerTavern = this.playerTavernDao.read(playerId);
                    if (playerTavern != null) {
                        final String gId = general2.getId() + ",";
                        if (general2.getType() == 1) {
                            if (playerTavern.getCivilInfo() != null && playerTavern.getCivilInfo().contains(gId)) {
                                show = false;
                            }
                        }
                        else if (playerTavern.getMilitaryInfo() != null && playerTavern.getMilitaryInfo().contains(gId)) {
                            show = false;
                        }
                    }
                    if (show) {
                        doc.createElement("firstOpen", pa.getFirstOpen());
                        if (pa.getAttackable() == 1 && pa.getFirstOpen() == 1) {
                            this.playerArmyDao.resetFirstOpen(pa.getPlayerId(), pa.getArmyId(), 0);
                        }
                        doc.createElement("dropGName", general2.getName());
                        doc.createElement("dropPic", general2.getPic());
                        doc.createElement("dropPro", armies.getDropMap().get(101).pro);
                        doc.createElement("dropQa", general2.getQuality());
                        if (general2.getId() == 225 || general2.getId() == 266) {
                            doc.createElement("dropGget", 1);
                        }
                        else {
                            doc.createElement("dropGget", 0);
                        }
                    }
                }
            }
            doc.endObject();
        }
        doc.endArray();
        final LinkedList<ArmiesReward> list = this.armiesRewardCache.getArmiesRewardByPowerId(powerId);
        if (list != null && list.size() > 0) {
            doc.startArray("rewardNpcsPos");
            for (final ArmiesReward armiesReward : list) {
                doc.createElement(armiesReward.getPos());
            }
            doc.endArray();
        }
        final List<PlayerArmyReward> parList = this.playerArmyRewardDao.getPlayerArmyRewardByPowerId(playerId, powerId);
        if (parList != null && parList.size() > 0) {
            doc.startArray("rewardNpcs");
            for (final PlayerArmyReward par : parList) {
                if (par.getArmyId() <= 0) {
                    continue;
                }
                if (par.getState() != 0) {
                    continue;
                }
                if (System.currentTimeMillis() > par.getExpireTime().getTime()) {
                    if (par.getState() != 0) {
                        continue;
                    }
                    this.playerArmyRewardDao.updateState(par.getPlayerId(), par.getArmyId(), 1);
                    par.setState(1);
                    if (par.getArmyId() != 201) {
                        continue;
                    }
                    this.dataGetter.getPlayerTaskService().startPushFreshManTaskIcon(player);
                }
                else {
                    final ArmiesReward armiesReward2 = (ArmiesReward)this.armiesRewardCache.get((Object)par.getArmyId());
                    final General general2 = (General)this.generalCache.get((Object)armiesReward2.getChief());
                    final Troop troop = (Troop)this.dataGetter.getTroopCache().get((Object)general2.getTroop());
                    doc.startObject();
                    doc.createElement("id", armiesReward2.getId());
                    doc.createElement("name", armiesReward2.getName());
                    doc.createElement("quality", general2.getQuality());
                    doc.createElement("pic", general2.getPic());
                    doc.createElement("terrian", armiesReward2.getTerrain());
                    doc.createElement("pos", armiesReward2.getPos());
                    doc.createElement("level", armiesReward2.getLevel());
                    doc.createElement("plot", armiesReward2.getPlot());
                    doc.createElement("hp", par.getHp());
                    doc.createElement("maxHp", par.getHpMax());
                    doc.createElement("type", BattleDrop.getDropType(troop.getDrop()));
                    doc.createElement("countDown", par.getExpireTime().getTime() - System.currentTimeMillis());
                    doc.createElement("first", par.getFirst());
                    if (par.getFirst() == 1) {
                        this.playerArmyRewardDao.updateFirst(playerId, par.getArmyId(), 0);
                    }
                    doc.endObject();
                }
            }
            doc.endArray();
        }
        if (parList != null && parList.size() > 0) {
            doc.startArray("rewardNpcsRight");
            for (final PlayerArmyReward par : parList) {
                if (par.getArmyId() <= 0) {
                    continue;
                }
                if (par.getState() != 1) {
                    continue;
                }
                final ArmiesReward armiesReward2 = (ArmiesReward)this.armiesRewardCache.get((Object)par.getArmyId());
                doc.startObject();
                final General general2 = (General)this.generalCache.get((Object)armiesReward2.getChief());
                final Troop troop = (Troop)this.dataGetter.getTroopCache().get((Object)general2.getTroop());
                doc.createElement("id", armiesReward2.getId());
                doc.createElement("name", armiesReward2.getName());
                doc.createElement("quality", general2.getQuality());
                doc.createElement("type", BattleDrop.getDropType(troop.getDrop()));
                doc.createElement("pic", general2.getPic());
                doc.createElement("pos", armiesReward2.getPos());
                doc.createElement("markTrace", armiesReward2.getMarkTrace());
                final int gold2 = armiesReward2.getGoldInit() + armiesReward2.getGoldIncrease() * par.getBuyCount();
                doc.createElement("gold", gold2);
                doc.createElement("firstWin", par.getFirstWin());
                if (par.getFirstWin() == 1) {
                    this.dataGetter.getPlayerArmyRewardDao().updateFirstWin(par.getPlayerId(), par.getArmyId(), 0);
                }
                doc.endObject();
            }
            doc.endArray();
        }
        if (powerId == 5) {
            final boolean isVip5 = player.getConsumeLv() >= 5;
            final PlayerArmyReward vip5ArmyReward = this.dataGetter.getPlayerArmyRewardDao().getPlayerArmyRewardByArmyId(playerId, -5);
            final boolean defeated = isVip5 && vip5ArmyReward == null;
            if (!defeated) {
                doc.startObject("vip5BonusArmy");
                final ArmiesReward armiesReward3 = (ArmiesReward)this.armiesRewardCache.get((Object)(-5));
                final General general3 = (General)this.generalCache.get((Object)armiesReward3.getChief());
                final Troop troop2 = (Troop)this.dataGetter.getTroopCache().get((Object)general3.getTroop());
                int state = 0;
                if (!isVip5) {
                    state = 1;
                }
                else if (player.getPlayerLv() < 50) {
                    state = 2;
                }
                else {
                    state = 3;
                    doc.createElement("state", state);
                    doc.createElement("id", armiesReward3.getId());
                    doc.createElement("hp", vip5ArmyReward.getHp());
                    doc.createElement("maxHp", vip5ArmyReward.getHpMax());
                }
                doc.createElement("type", BattleDrop.getDropType(troop2.getDrop()));
                doc.createElement("state", state);
                doc.endObject();
            }
        }
        doc.endObject();
        TaskMessageHelper.sendGetPowerTaskMessage(playerId, powerId);
        EventListener.fireEvent(new CommonEvent(17, playerId));
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int caculateNextPowerAttackable(final int playerId, final Integer nextPowerId) {
        final List<Armies> currentList = this.dataGetter.getArmiesCache().getArmiesByPowerId(nextPowerId);
        int checkArmyId = 0;
        for (final Armies armies : currentList) {
            if (this.dataGetter.getArmiesCache().isLastArmies(armies.getId())) {
                checkArmyId = armies.getId();
                break;
            }
        }
        if (checkArmyId == 0) {
            return 0;
        }
        final PlayerArmy playerArmy = this.dataGetter.getPlayerArmyDao().getPlayerArmy(playerId, checkArmyId);
        if (playerArmy == null) {
            return 0;
        }
        if (playerArmy.getWinNum() > 0) {
            return 1;
        }
        return 0;
    }
    
    public PlayerPower createPlayerPower(final int playerId, final int powerId, final int attackable) {
        final PlayerPower playerPower = new PlayerPower();
        playerPower.setPlayerId(playerId);
        playerPower.setPowerId(powerId);
        playerPower.setComplete(0);
        playerPower.setAttackable(attackable);
        playerPower.setReward(0);
        playerPower.setExpireTime(null);
        playerPower.setState(0);
        playerPower.setBuyCount(0);
        this.playerPowerDao.create(playerPower);
        return playerPower;
    }
    
    public void createPlayerArmies(final int playerId, final int powerId) {
        final LinkedList<Armies> list = this.armiesCache.getArmiesByPowerId(powerId);
        for (int i = 0; i < list.size(); ++i) {
            final Armies armies = list.get(i);
            final PlayerArmy playerArmy = new PlayerArmy();
            playerArmy.setArmyId(armies.getId());
            playerArmy.setPlayerId(playerId);
            playerArmy.setPowerId(powerId);
            playerArmy.setAttNum(0);
            playerArmy.setFirstWin(0);
            playerArmy.setAttackable(0);
            if (i == 0) {
                playerArmy.setAttackable(1);
            }
            playerArmy.setWinNum(0);
            playerArmy.setFirstOpen(1);
            playerArmy.setDropCount(0);
            playerArmy.setGoldReward(0);
            this.playerArmyDao.create(playerArmy);
        }
    }
    
    @Override
    public byte[] getExtraPowerInfo(final PlayerDto playerDto, final int extraPowerId) {
        final int playerId = playerDto.playerId;
        if (extraPowerId < 100) {
            return JsonBuilder.getJson(State.FAIL, "\u989d\u5916\u526f\u672cpowerId\u975e\u6cd5,playerId:" + playerId + " powerId:" + extraPowerId);
        }
        final PlayerPower playerPower = this.dataGetter.getPlayerPowerDao().getPlayerPower(playerId, extraPowerId);
        if (playerPower == null || playerPower.getExpireTime().getTime() < System.currentTimeMillis()) {
            if (playerPower != null) {
                final int powerId = this.dataGetter.getPowerCache().fromExtraToArmy(extraPowerId);
                this.dataGetter.getPlayerDao().updatePowerId(playerId, powerId);
                this.dataGetter.getPlayerPowerDao().deleteByPowerId(extraPowerId);
                this.dataGetter.getPlayerArmyExtraDao().deleteByPlayerIdPowerId(playerId, extraPowerId);
            }
            return JsonBuilder.getJson(State.FAIL, "\u8fd9\u8d27\u662f\u5916\u6302");
        }
        final List<PlayerArmyExtra> paeList = this.playerArmyExtraDao.getArmiesByPowerId(playerId, extraPowerId);
        if (paeList == null || paeList.size() <= 0) {
            final int powerId2 = this.dataGetter.getPowerCache().fromExtraToArmy(extraPowerId);
            this.dataGetter.getPlayerDao().updatePowerId(playerId, powerId2);
            ErrorSceneLog.getInstance().error("\u989d\u5916\u526f\u672c\u5f02\u5e38,player_power\u8868\u6709\u6570\u636e,\u4f46\u662fplayer_army_extra\u65e0\u6570\u636e, playerId:" + playerId + " powerId:" + extraPowerId);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        this.playerDao.updatePowerId(playerId, extraPowerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final Power extraPower = (Power)this.powerCache.get((Object)extraPowerId);
        final Power returnPower = (Power)this.powerCache.get((Object)this.dataGetter.getPowerCache().fromExtraToArmy(extraPowerId));
        doc.createElement("type", 12);
        doc.createElement("extraPowerId", extraPowerId);
        doc.createElement("extraName", extraPower.getName());
        doc.createElement("extraCountDown", playerPower.getExpireTime().getTime() - System.currentTimeMillis());
        doc.createElement("returnPowerId", returnPower.getId());
        doc.createElement("returnName", returnPower.getName());
        doc.startArray("extraNpcs");
        for (int i = 0; i < paeList.size(); ++i) {
            doc.startObject();
            final PlayerArmyExtra pae = paeList.get(i);
            final ArmiesExtra armiesExtra = (ArmiesExtra)this.dataGetter.getArmiesExtraCache().get((Object)pae.getArmyId());
            final General general = (General)this.dataGetter.getGeneralCache().get((Object)armiesExtra.getChief());
            doc.createElement("npcId", armiesExtra.getId());
            doc.createElement("npcName", armiesExtra.getName());
            doc.createElement("attackable", pae.getAttackable());
            doc.createElement("attacked", pae.getWinNum() > 0);
            doc.createElement("terrian", armiesExtra.getTerrain());
            doc.createElement("quality", ((General)this.generalCache.get((Object)armiesExtra.getChief())).getQuality());
            doc.createElement("hp", pae.getHp());
            doc.createElement("hpMax", pae.getHpMax());
            doc.createElement("dropType", this.dataGetter.getArmiesExtraCache().getExtraDropType(pae.getArmyId()));
            doc.createElement("pic", general.getPic());
            if (pae.getFirstWin() == 1) {
                this.playerArmyExtraDao.resetFirstWin(pae.getPlayerId(), pae.getArmyId(), 0);
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] buyBonusNpc(final PlayerDto playerDto, final int armyId) {
        if (armyId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerArmyReward playerArmyReward = this.playerArmyRewardDao.getPlayerArmyRewardByArmyId(playerId, armyId);
        if (playerArmyReward == null) {
            return JsonBuilder.getJson(State.FAIL, "\u8fd9\u8d27\u662f\u5916\u6302");
        }
        if (playerArmyReward.getState() != 1) {
            return JsonBuilder.getJson(State.FAIL, "\u8fd9\u8d27\u662f\u5916\u6302");
        }
        final Player player = this.playerDao.read(playerId);
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)35);
        if (this.playerDao.getConsumeLv(playerId) < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final ArmiesReward armiesReward = (ArmiesReward)this.armiesRewardCache.get((Object)playerArmyReward.getArmyId());
        final int gold = armiesReward.getGoldInit() + playerArmyReward.getBuyCount() * armiesReward.getGoldIncrease();
        if (!this.playerDao.consumeGold(player, gold, "\u8d2d\u4e70bonus\u526f\u672c\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final PlayerArmyReward playerArmyRewardNew = new PlayerArmyReward();
        playerArmyRewardNew.setPlayerId(playerArmyReward.getPlayerId());
        playerArmyRewardNew.setPowerId(playerArmyReward.getPowerId());
        playerArmyRewardNew.setArmyId(playerArmyReward.getArmyId());
        playerArmyRewardNew.setFirst(0);
        final int miniuteNum = ((ArmiesReward)this.dataGetter.getArmiesRewardCache().get((Object)playerArmyReward.getArmyId())).getTime();
        playerArmyRewardNew.setExpireTime(new Date(System.currentTimeMillis() + miniuteNum * 60000L));
        playerArmyRewardNew.setNpcLost(null);
        final int num = OneVsRewardNpcBuilder.getMaxHp(this.dataGetter, playerArmyReward.getArmyId());
        playerArmyRewardNew.setHp(num);
        playerArmyRewardNew.setHpMax(num);
        playerArmyRewardNew.setState(0);
        playerArmyRewardNew.setBuyCount(playerArmyReward.getBuyCount() + 1);
        this.dataGetter.getPlayerArmyRewardDao().update(playerArmyReward.getVId(), playerArmyRewardNew);
        this.dataGetter.getActivityService().addDstqGold(playerId, gold);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("done", true);
        doc.endObject();
        try {
            this.dataGetter.getBroadCastUtil().sendReOpenBonusBroadCast(playerId, playerArmyReward.getPowerId(), armiesReward.getName());
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PowerService buyBonusNpc " + e);
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] buyPowerExtra(final PlayerDto playerDto, final int extraPowerId) {
        if (this.dataGetter.getPowerCache().get((Object)extraPowerId) == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final PlayerPower playerExtraPower = this.dataGetter.getPlayerPowerDao().getPlayerPower(playerId, extraPowerId);
        if (playerExtraPower == null) {
            return JsonBuilder.getJson(State.FAIL, "\u8fd9\u8d27\u662f\u5916\u6302");
        }
        if (playerExtraPower.getState() != 1) {
            return JsonBuilder.getJson(State.FAIL, "\u8fd9\u8d27\u662f\u5916\u6302");
        }
        final Player player = this.playerDao.read(playerId);
        final Chargeitem ci = (Chargeitem)this.dataGetter.getChargeitemCache().get((Object)41);
        if (this.playerDao.getConsumeLv(playerId) < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final Power extraPower = (Power)this.dataGetter.getPowerCache().get((Object)playerExtraPower.getPowerId());
        final int gold = extraPower.getGoldInit() + playerExtraPower.getBuyCount() * extraPower.getGoldIncrease();
        if (!this.playerDao.consumeGold(player, gold, "\u8d2d\u4e70extra\u526f\u672c\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final Date expireTime = new Date(System.currentTimeMillis() + 86400000L);
        this.dataGetter.getPlayerPowerDao().updateStateAndBuyCountAndExpireTime(playerId, extraPowerId, 0, playerExtraPower.getBuyCount() + 1, expireTime);
        final List<PlayerArmyExtra> PlayerArmiesExtraList = this.dataGetter.getPlayerArmyExtraDao().getArmiesByPowerId(playerId, extraPowerId);
        if (PlayerArmiesExtraList != null && PlayerArmiesExtraList.size() > 0) {
            PowerService.errorSceneLog.appendErrorMsg("power_extra\u8d2d\u4e70,PlayerPower\u65e0\u8bb0\u5f55,PlayerArmyExtra\u5df2\u7ecf\u6709\u8bb0\u5f55.").appendClassName("PowerService").appendMethodName("buyPowerExtra").append("playerId", playerId).append("extraPowerId", extraPowerId).flush();
            return JsonBuilder.getJson(State.FAIL, "\u8fd9\u8d27\u662f\u5916\u6302");
        }
        final List<ArmiesExtra> armiesExtraList = this.dataGetter.getArmiesExtraCache().getArmiesExtraByPowerId(extraPowerId);
        for (int i = 0; i < armiesExtraList.size(); ++i) {
            final ArmiesExtra armiesExtra = armiesExtraList.get(i);
            final PlayerArmyExtra playerArmyExtra = new PlayerArmyExtra();
            playerArmyExtra.setPlayerId(playerId);
            playerArmyExtra.setPowerId(armiesExtra.getPowerId());
            playerArmyExtra.setArmyId(armiesExtra.getId());
            playerArmyExtra.setAttackable(0);
            if (i == 0) {
                playerArmyExtra.setAttackable(1);
            }
            playerArmyExtra.setAttNum(0);
            playerArmyExtra.setFirstOpen(1);
            playerArmyExtra.setFirstWin(0);
            playerArmyExtra.setWinNum(0);
            final int num = OneVsExtraBuilder.getMaxHp(this.dataGetter, armiesExtra.getId());
            playerArmyExtra.setHp(num);
            playerArmyExtra.setHpMax(num);
            playerArmyExtra.setNpcLost(null);
            this.dataGetter.getPlayerArmyExtraDao().create(playerArmyExtra);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("done", true);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public boolean bonusLiaoHuaDefeatedOrTimeOut(final int playerId) {
        final int bonusId = 201;
        final PlayerArmyReward par = this.dataGetter.getPlayerArmyRewardDao().getPlayerArmyRewardByArmyId(playerId, bonusId);
        return par != null && (par.getBuyCount() != 0 || par.getState() != 0);
    }
    
    @Override
    public byte[] getPowerGuide(final PlayerDto playerDto) {
        final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerDto.playerId).toCharArray();
        if (cs[65] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        int maxPowerId = Integer.MIN_VALUE;
        final List<PlayerPower> powerList = this.dataGetter.getPlayerPowerDao().getPlayerPowers(playerDto.playerId);
        for (final PlayerPower playerPower : powerList) {
            if (playerPower.getPowerId() <= 0) {
                continue;
            }
            if (playerPower.getPowerId() >= 100) {
                continue;
            }
            if (playerPower.getAttackable() != 1 || playerPower.getPowerId() <= maxPowerId) {
                continue;
            }
            maxPowerId = playerPower.getPowerId();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("guides");
        for (final Power power : this.dataGetter.getPowerCache().getModels()) {
            final int powerId = power.getId();
            if (powerId > 100) {
                continue;
            }
            final Tuple<Integer, Integer> tuple = this.dataGetter.getPowerCache().getLvScale(powerId);
            if (tuple == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("LvScale is null").append("powerId", powerId).appendClassName("PowerService").appendMethodName("getPowerGuide").flush();
            }
            else {
                doc.startObject();
                doc.createElement("powerId", powerId);
                doc.createElement("name", power.getName());
                doc.createElement("beginLv", tuple.left);
                doc.createElement("endLv", tuple.right);
                int state = 0;
                if (powerId < maxPowerId) {
                    state = 1;
                }
                else if (powerId == maxPowerId) {
                    state = 2;
                }
                else {
                    state = 3;
                }
                doc.createElement("state", state);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getCurrentGuide(final PlayerDto playerDto, final int powerId) {
        final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerDto.playerId).toCharArray();
        if (cs[65] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (powerId > 100) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final Power power = (Power)this.dataGetter.getPowerCache().get((Object)powerId);
        if (power == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final PlayerTask playerTask = this.dataGetter.getPlayerTaskDao().getCurMainTask(playerDto.playerId);
        final int currentTaskId = playerTask.getTaskId();
        final Integer currentTaskSerialId = this.dataGetter.getTaskCache().getTaskSerialId(currentTaskId);
        final PlayerTavern playerTavern = this.dataGetter.getPlayerTavernDao().read(playerDto.playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("guides");
        final List<FbGuide> guideList = this.dataGetter.getFbGuideCache().getListByPowerId(powerId);
        boolean currentFound = false;
        if (guideList != null) {
            for (final FbGuide fbGuide : guideList) {
                doc.startObject();
                doc.createElement("id", fbGuide.getId());
                doc.createElement("target", fbGuide.getTarget());
                doc.createElement("brief", fbGuide.getBrief());
                doc.createElement("detail", fbGuide.getDetail());
                doc.createElement("pic", fbGuide.getPic());
                doc.createElement("lv", fbGuide.getLv());
                final int foreignKey = fbGuide.getForeignKey();
                int state = 0;
                if (fbGuide.getGuideType() == 2) {
                    final Integer tempTaskSerialId = this.dataGetter.getTaskCache().getTaskSerialId(foreignKey);
                    if (tempTaskSerialId < currentTaskSerialId) {
                        state = 1;
                    }
                    else if (!currentFound && tempTaskSerialId >= currentTaskSerialId) {
                        state = 2;
                        currentFound = true;
                    }
                    else {
                        state = 3;
                    }
                }
                else if (fbGuide.getGuideType() == 3) {
                    final General general = (General)this.dataGetter.getGeneralCache().get((Object)foreignKey);
                    if (general == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("general is null").append("fbGuide", fbGuide.getId()).append("foreignKey", foreignKey).appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).appendClassName("PowerService").appendMethodName("getPowerGuideInfo").flush();
                    }
                    String generalString = null;
                    if (general.getType() == 1) {
                        generalString = playerTavern.getCivilInfo();
                    }
                    else if (general.getType() == 2) {
                        generalString = playerTavern.getMilitaryInfo();
                    }
                    if (generalString == null) {
                        if (!currentFound) {
                            state = 2;
                            currentFound = true;
                        }
                        else {
                            state = 3;
                        }
                    }
                    else if (generalString.contains(Integer.toString(foreignKey))) {
                        state = 1;
                    }
                    else if (!currentFound) {
                        state = 2;
                        currentFound = true;
                    }
                    else {
                        state = 3;
                    }
                }
                else {
                    state = 3;
                }
                doc.createElement("state", state);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
}
