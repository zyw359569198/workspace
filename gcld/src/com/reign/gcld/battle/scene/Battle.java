package com.reign.gcld.battle.scene;

import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.scenario.common.*;
import java.util.concurrent.atomic.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.log.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.huizhan.domain.*;
import com.reign.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.world.common.*;
import java.util.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.team.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.battle.common.*;
import java.io.*;

public class Battle implements Serializable, Comparable<Battle>
{
    private static final long serialVersionUID = 5346688949777842772L;
    private static final Logger timerLog;
    long startTime;
    String battleId;
    CampArmy campArmyAtt;
    CampArmy campArmyDef;
    int battleType;
    String oldBattleId;
    public int terrain;
    String terrainName;
    public int terrainPic;
    public int terrainVal;
    boolean auto;
    boolean isNpc;
    public boolean isQuickConquer;
    List<BattleArmy> attList;
    List<BattleArmy> defList;
    LinkedList<CampArmy> attCamp;
    LinkedList<CampArmy> defCamp;
    ConcurrentSkipListSet<Integer> inSceneSet;
    Map<Integer, PlayerInfo> inBattlePlayers;
    public Set<CampArmy> newlyJoinSet;
    BaseInfo attBaseInfo;
    BaseInfo defBaseInfo;
    ScenarioEvent scenarioEvent;
    boolean battleDoing;
    StringBuilder curReport;
    StringBuilder curBattling;
    AtomicInteger attQNum;
    AtomicInteger defQNum;
    AtomicInteger campNum;
    AtomicInteger ticket;
    AtomicInteger battleNum;
    AtomicInteger roundNum;
    public WorldSceneLog worldSceneLog;
    long sendMsgTime;
    double all_damage_e_att;
    double all_damage_e_def;
    int att_gongji_base_add;
    int att_fangyu_base_add;
    int def_gongji_base_add;
    int def_fangyu_base_add;
    int lastRoundWinState;
    long nextMinExeTime;
    long nextMaxExeTime;
    long nextExeTime;
    boolean attChooseTactic;
    boolean defChooseTactic;
    boolean attChoose;
    boolean defChoose;
    public int lastAttOnQueuePosId;
    public int lastDefOnQueuePosId;
    boolean waitting;
    double world_weaken_besiege_e;
    int world_frontLine_buff_type;
    double world_frontLine_buff_exp_e;
    double world_frontLine_buff_att_def_e;
    int world_frontLine_buff_att_def_e_side;
    List<Tuple<Integer, String>> attBuffListRound;
    List<Tuple<Integer, String>> defBuffListRound;
    double world_round_ally_buff_att_e;
    double world_round_ally_buff_def_e;
    private Map<Integer, Long> shaDiLingMap;
    List<Tuple<Integer, String>> attBuffListInit;
    List<Tuple<Integer, String>> defBuffListInit;
    public Map<Integer, Integer> attSideDetail;
    public Map<Integer, Integer> defSideDetail;
    int surround;
    
    static {
        timerLog = new TimerLogger();
    }
    
    public Battle(final String battleId) {
        this.startTime = 0L;
        this.campArmyAtt = null;
        this.campArmyDef = null;
        this.oldBattleId = null;
        this.terrainName = null;
        this.isQuickConquer = false;
        this.attList = new ArrayList<BattleArmy>(6);
        this.defList = new ArrayList<BattleArmy>(6);
        this.attCamp = new LinkedList<CampArmy>();
        this.defCamp = new LinkedList<CampArmy>();
        this.inSceneSet = new ConcurrentSkipListSet<Integer>();
        this.inBattlePlayers = new HashMap<Integer, PlayerInfo>();
        this.newlyJoinSet = new HashSet<CampArmy>();
        this.attBaseInfo = new BaseInfo();
        this.defBaseInfo = new BaseInfo();
        this.scenarioEvent = null;
        this.battleDoing = true;
        this.curReport = new StringBuilder();
        this.curBattling = new StringBuilder();
        this.attQNum = new AtomicInteger(0);
        this.defQNum = new AtomicInteger(0);
        this.campNum = new AtomicInteger(0);
        this.ticket = new AtomicInteger(0);
        this.battleNum = new AtomicInteger(0);
        this.roundNum = new AtomicInteger(0);
        this.worldSceneLog = null;
        this.sendMsgTime = System.currentTimeMillis() - 60000L;
        this.all_damage_e_att = 0.0;
        this.all_damage_e_def = 0.0;
        this.att_gongji_base_add = 0;
        this.att_fangyu_base_add = 0;
        this.def_gongji_base_add = 0;
        this.def_fangyu_base_add = 0;
        this.lastRoundWinState = 0;
        this.nextMinExeTime = System.currentTimeMillis();
        this.nextMaxExeTime = System.currentTimeMillis();
        this.nextExeTime = System.currentTimeMillis();
        this.attChooseTactic = false;
        this.defChooseTactic = false;
        this.attChoose = false;
        this.defChoose = false;
        this.lastAttOnQueuePosId = 0;
        this.lastDefOnQueuePosId = 0;
        this.waitting = false;
        this.world_weaken_besiege_e = 0.0;
        this.world_frontLine_buff_type = 0;
        this.world_frontLine_buff_exp_e = 1.0;
        this.world_frontLine_buff_att_def_e = 0.0;
        this.world_frontLine_buff_att_def_e_side = 0;
        this.attBuffListRound = new LinkedList<Tuple<Integer, String>>();
        this.defBuffListRound = new LinkedList<Tuple<Integer, String>>();
        this.world_round_ally_buff_att_e = 0.0;
        this.world_round_ally_buff_def_e = 0.0;
        this.shaDiLingMap = null;
        this.attBuffListInit = new LinkedList<Tuple<Integer, String>>();
        this.defBuffListInit = new LinkedList<Tuple<Integer, String>>();
        this.attSideDetail = new HashMap<Integer, Integer>();
        this.defSideDetail = new HashMap<Integer, Integer>();
        this.surround = 0;
        this.battleId = battleId;
        this.startTime = System.currentTimeMillis();
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public int getBattleType() {
        return this.battleType;
    }
    
    public int getTerrainPic() {
        return this.terrainPic;
    }
    
    public int getTerrainVal() {
        return this.terrainVal;
    }
    
    public int getTerrain() {
        return this.terrain;
    }
    
    public boolean isAuto() {
        return this.auto;
    }
    
    public List<BattleArmy> getAttList() {
        return this.attList;
    }
    
    public List<BattleArmy> getDefList() {
        return this.defList;
    }
    
    public LinkedList<CampArmy> getAttCamp() {
        return this.attCamp;
    }
    
    public LinkedList<CampArmy> getDefCamp() {
        return this.defCamp;
    }
    
    public void addInSceneSet(final int playerId) {
        this.inSceneSet.add(playerId);
    }
    
    public boolean isInSceneSet(final int playerId) {
        return this.inSceneSet.contains(playerId);
    }
    
    public Map<Integer, PlayerInfo> getInBattlePlayers() {
        return this.inBattlePlayers;
    }
    
    public boolean isBattleDoing() {
        return this.battleDoing;
    }
    
    public AtomicInteger getAttQNum() {
        return this.attQNum;
    }
    
    public AtomicInteger getDefQNum() {
        return this.defQNum;
    }
    
    public AtomicInteger getCampNum() {
        return this.campNum;
    }
    
    public AtomicInteger getTicket() {
        return this.ticket;
    }
    
    public AtomicInteger getBattleNum() {
        return this.battleNum;
    }
    
    public AtomicInteger getRoundNum() {
        return this.roundNum;
    }
    
    public String getBattleId() {
        return this.battleId;
    }
    
    public BaseInfo getAttBaseInfo() {
        return this.attBaseInfo;
    }
    
    public BaseInfo getDefBaseInfo() {
        return this.defBaseInfo;
    }
    
    public boolean isNpc() {
        return this.isNpc;
    }
    
    public void setSendMsgTime(final long sendMsgTime) {
        this.sendMsgTime = sendMsgTime;
    }
    
    public long getSendMsgTime() {
        return this.sendMsgTime;
    }
    
    public void clearNewlyJoinSet() {
        this.newlyJoinSet.clear();
    }
    
    public void reSetAllDamageE(final IDataGetter dataGetter, final int attForceId, final int defForceId) {
        final Tuple<Double, Double> tuple = dataGetter.getCilvilTrickService().afterStateTrick2(this.defBaseInfo.id, attForceId, defForceId);
        if (tuple != null) {
            this.all_damage_e_att = tuple.left;
            this.all_damage_e_def = tuple.right;
        }
    }
    
    public long getNextMinExeTime() {
        return this.nextMinExeTime;
    }
    
    public void setNextMinExeTime(final long nextMinExeTime) {
        this.nextMinExeTime = nextMinExeTime;
    }
    
    public long getNextMaxExeTime() {
        return this.nextMaxExeTime;
    }
    
    public void setNextMaxExeTime(final long nextMaxExeTime) {
        this.nextMaxExeTime = nextMaxExeTime;
    }
    
    public long getNextExeTime() {
        return this.nextExeTime;
    }
    
    public void setNextExeTime(final long exeTime) {
        this.nextExeTime = exeTime;
    }
    
    public boolean isAttChoose() {
        return this.attChoose;
    }
    
    public boolean isDefChoose() {
        return this.defChoose;
    }
    
    public boolean isAttChooseTactic() {
        return this.attChooseTactic;
    }
    
    public boolean isDefChooseTactic() {
        return this.defChooseTactic;
    }
    
    public Long getShaDiLingExpireTime(final Integer forceId) {
        if (this.shaDiLingMap == null) {
            return null;
        }
        return this.shaDiLingMap.get(forceId);
    }
    
    public void useShaDiLing(final Integer forceId, final Long duration) {
        Long baseTime = this.shaDiLingMap.get(forceId);
        if (baseTime == null || baseTime < System.currentTimeMillis()) {
            baseTime = System.currentTimeMillis();
        }
        this.shaDiLingMap.put(forceId, baseTime + duration);
    }
    
    public Boolean IsShaDiLingDoubleed(final Integer forceId) {
        if (this.shaDiLingMap == null) {
            return false;
        }
        final Long baseTime = this.shaDiLingMap.get(forceId);
        if (baseTime != null && baseTime >= System.currentTimeMillis()) {
            return true;
        }
        return false;
    }
    
    public int getSurround() {
        return this.surround;
    }
    
    public void setSurround(final int surround) {
        this.surround = surround;
    }
    
    public static boolean proYoudi(final IDataGetter dataGetter) {
        final C c = (C)dataGetter.getcCache().get((Object)"World.Due.SuccessProb");
        double pro = 0.5;
        if (c != null) {
            pro = c.getValue();
        }
        final double tpro = WebUtil.nextDouble();
        return tpro <= pro;
    }
    
    public static boolean proChuji(final IDataGetter dataGetter) {
        final C c = (C)dataGetter.getcCache().get((Object)"World.InitDue.SuccessProb");
        double pro = 0.5;
        if (c != null) {
            pro = c.getValue();
        }
        return WebUtil.nextDouble() <= pro;
    }
    
    public static boolean pkPro(final int pkType, final IDataGetter dataGetter) {
        if (pkType == 1) {
            return proYoudi(dataGetter);
        }
        return proChuji(dataGetter);
    }
    
    public int chooseCampArmyGeneralId(final IDataGetter dataGetter, final int playerId, final int type) {
        synchronized (this.battleId) {
            if (1 == type) {
                for (int i = 1; i < this.attCamp.size(); ++i) {
                    final CampArmy ca = this.attCamp.get(i);
                    if (ca.getPlayerId() == playerId && !ca.isPhantom && !ca.onQueues) {
                        // monitorexit(this.battleId)
                        return ca.getGeneralId();
                    }
                }
            }
            else if (2 == type) {
                for (int i = 1; i < this.defCamp.size(); ++i) {
                    final CampArmy ca = this.defCamp.get(i);
                    if (ca.getPlayerId() == playerId && !ca.isPhantom && !ca.onQueues) {
                        // monitorexit(this.battleId)
                        return ca.getGeneralId();
                    }
                }
            }
            // monitorexit(this.battleId)
            return 0;
        }
    }
    
    public int chooseCampArmy(final IDataGetter dataGetter, final int playerId, final CampArmy[] cas, final int type) {
        synchronized (this.battleId) {
            if (1 == type) {
                cas[0] = null;
                for (int i = 1; i < this.attCamp.size(); ++i) {
                    final CampArmy ca = this.attCamp.get(i);
                    if (ca.getPlayerId() == playerId && !ca.isPhantom && !ca.onQueues) {
                        cas[0] = ca;
                        break;
                    }
                }
                if (cas[0] == null) {
                    // monitorexit(this.battleId)
                    return 2;
                }
                cas[1] = null;
                final int idx = 9;
                if (this.defCamp.size() >= 10) {
                    cas[1] = this.defCamp.get(idx);
                }
                else {
                    for (int j = this.defCamp.size() - 1; j >= 1; --j) {
                        final CampArmy ca2 = this.getDefCamp().get(j);
                        if (!ca2.onQueues) {
                            cas[1] = ca2;
                            break;
                        }
                    }
                }
                if (cas[1] == null) {
                    // monitorexit(this.battleId)
                    return 4;
                }
                if (!proYoudi(dataGetter)) {
                    // monitorexit(this.battleId)
                    return 3;
                }
                this.attCamp.remove(cas[0]);
                this.defCamp.remove(cas[1]);
                if (cas[0].getPlayerId() > 0 && !cas[0].isPhantom()) {
                    NewBattleManager.getInstance().quitBattle(this, cas[0].getPlayerId(), cas[0].getGeneralId());
                }
                BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#quit:youdi#side:att" + "#playerId:" + cas[0].getPlayerId() + ":" + cas[0].isPhantom + "#general:" + cas[0].getGeneralId() + "#attSize:" + this.attCamp.size());
                if (cas[1].getPlayerId() > 0 && !cas[1].isPhantom()) {
                    NewBattleManager.getInstance().quitBattle(this, cas[1].getPlayerId(), cas[1].getGeneralId());
                }
                BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#quit:youdi#side:def" + "#playerId:" + cas[1].getPlayerId() + ":" + cas[1].isPhantom + "#general:" + cas[1].getGeneralId() + "#defSize:" + this.defCamp.size());
                this.attBaseInfo.setAllNum(this.attBaseInfo.getAllNum() - cas[0].armyHp);
                this.attBaseInfo.setNum(this.attBaseInfo.getNum() - cas[0].armyHp);
                this.defBaseInfo.setAllNum(this.defBaseInfo.getAllNum() - cas[1].armyHp);
                this.defBaseInfo.setNum(this.defBaseInfo.getNum() - cas[1].armyHp);
                // monitorexit(this.battleId)
                return 1;
            }
            else {
                if (2 != type) {
                    // monitorexit(this.battleId)
                    return 2;
                }
                cas[1] = null;
                for (int i = 1; i < this.defCamp.size(); ++i) {
                    final CampArmy ca = this.defCamp.get(i);
                    if (ca.getPlayerId() == playerId && !ca.isPhantom && !ca.onQueues) {
                        cas[1] = ca;
                        break;
                    }
                }
                if (cas[1] == null) {
                    // monitorexit(this.battleId)
                    return 2;
                }
                cas[0] = null;
                final int idx = 9;
                if (this.attCamp.size() >= 10) {
                    cas[0] = this.attCamp.get(idx);
                }
                else {
                    for (int j = this.attCamp.size() - 1; j >= 1; --j) {
                        final CampArmy ca2 = this.attCamp.get(j);
                        if (!ca2.onQueues) {
                            cas[0] = ca2;
                            break;
                        }
                    }
                }
                if (cas[0] == null) {
                    // monitorexit(this.battleId)
                    return 4;
                }
                if (!proChuji(dataGetter)) {
                    // monitorexit(this.battleId)
                    return 3;
                }
                this.attCamp.remove(cas[0]);
                this.defCamp.remove(cas[1]);
                if (cas[0].getPlayerId() > 0 && !cas[0].isPhantom()) {
                    NewBattleManager.getInstance().quitBattle(this, cas[0].getPlayerId(), cas[0].getGeneralId());
                }
                BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#quit:chuji#side:att" + "#playerId:" + cas[0].getPlayerId() + ":" + cas[0].isPhantom + "#general:" + cas[0].getGeneralId() + "#attSize:" + this.attCamp.size());
                if (cas[1].getPlayerId() > 0 && !cas[1].isPhantom()) {
                    NewBattleManager.getInstance().quitBattle(this, cas[1].getPlayerId(), cas[1].getGeneralId());
                }
                BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#quit:chuji#side:def" + "#playerId:" + cas[1].getPlayerId() + ":" + cas[1].isPhantom + "#general:" + cas[1].getGeneralId() + "#defSize:" + this.defCamp.size());
                this.attBaseInfo.setNum(this.attBaseInfo.getNum() - cas[0].armyHp);
                this.attBaseInfo.setAllNum(this.attBaseInfo.getAllNum() - cas[0].armyHp);
                this.defBaseInfo.setNum(this.defBaseInfo.getNum() - cas[1].armyHp);
                this.defBaseInfo.setAllNum(this.defBaseInfo.getAllNum() - cas[1].armyHp);
                // monitorexit(this.battleId)
                return 1;
            }
        }
    }
    
    public int chooseCampArmyForNTYellowTurbans(final IDataGetter dataGetter, final int playerId, final CampArmy[] cas) {
        final Player player = dataGetter.getPlayerDao().read(playerId);
        synchronized (this.battleId) {
            cas[0] = null;
            for (int i = 1; i < this.attCamp.size(); ++i) {
                final CampArmy ca = this.attCamp.get(i);
                if (ca.getPlayerId() == playerId && !ca.isPhantom && !ca.onQueues) {
                    cas[0] = ca;
                    break;
                }
            }
            if (cas[0] == null) {
                // monitorexit(this.battleId)
                return 2;
            }
            cas[1] = null;
            for (int i = 1; i < this.attCamp.size(); ++i) {
                final CampArmy ca = this.attCamp.get(i);
                if (ca.getForceId() != player.getForceId() && !ca.onQueues) {
                    cas[1] = ca;
                    break;
                }
            }
            if (cas[1] == null) {
                for (int i = 1; i < this.defCamp.size(); ++i) {
                    final CampArmy ca = this.defCamp.get(i);
                    if (!ca.onQueues) {
                        cas[1] = ca;
                        break;
                    }
                }
            }
            if (cas[1] == null) {
                // monitorexit(this.battleId)
                return 4;
            }
            if (!proYoudi(dataGetter)) {
                // monitorexit(this.battleId)
                return 3;
            }
            this.attCamp.remove(cas[0]);
            this.attCamp.remove(cas[1]);
            if (cas[0].getPlayerId() > 0 && !cas[0].isPhantom()) {
                NewBattleManager.getInstance().quitBattle(this, cas[0].getPlayerId(), cas[0].getGeneralId());
            }
            BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#quit:youdiNTYellowTurbans#side:att" + "#playerId:" + cas[0].getPlayerId() + ":" + cas[0].isPhantom + "#general:" + cas[0].getGeneralId() + "#attSize:" + this.attCamp.size());
            if (cas[1].getPlayerId() > 0 && !cas[1].isPhantom()) {
                NewBattleManager.getInstance().quitBattle(this, cas[1].getPlayerId(), cas[1].getGeneralId());
            }
            BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#quit:youdiNTYellowTurbans#side:att" + "#playerId:" + cas[1].getPlayerId() + ":" + cas[1].isPhantom + "#general:" + cas[1].getGeneralId() + "#defSize:" + this.defCamp.size());
            this.attBaseInfo.setAllNum(this.attBaseInfo.getAllNum() - cas[0].armyHp - cas[1].armyHp);
            this.attBaseInfo.setNum(this.attBaseInfo.getNum() - cas[0].armyHp - cas[1].armyHp);
            // monitorexit(this.battleId)
            return 1;
        }
    }
    
    public void beginOneToOneBattle(final IDataGetter dataGetter, final int OneToOneBattleType, final int playerId, final CampArmy[] cas, final Battle oldBattle) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(OneToOneBattleType);
        synchronized (this.battleId) {
            this.battleType = OneToOneBattleType;
            this.oldBattleId = oldBattle.battleId;
            this.campArmyAtt = cas[0];
            this.campArmyDef = cas[1];
            this.attBaseInfo.setId(playerId);
            this.attBaseInfo.setAllNum(cas[0].armyHp);
            this.attBaseInfo.setNum(cas[0].armyHp);
            this.attBaseInfo.setForceId(cas[0].forceId);
            cas[0].setArmyHpOrg(cas[0].armyHp);
            cas[0].setArmyHpLoss(0);
            final int defId = oldBattle.defBaseInfo.id;
            this.defBaseInfo.setId(defId);
            this.defBaseInfo.setAllNum(cas[1].armyHp);
            this.defBaseInfo.setNum(cas[1].armyHp);
            this.defBaseInfo.setForceId(cas[1].forceId);
            cas[1].setArmyHpOrg(cas[1].armyHp);
            cas[1].setArmyHpLoss(0);
            this.worldSceneLog = WorldSceneLog.getInstance();
            this.setBattleInitBuff(dataGetter, defId);
            this.caculateCivilTrickBuff(dataGetter);
            final Terrain terrain = builder.getTerrain(playerId, defId, dataGetter);
            this.terrain = terrain.getDisplay();
            switch (this.terrainVal = terrain.getValue()) {
                case 1: {
                    this.terrainName = LocalMessages.TERRAIN_NAME_1;
                    break;
                }
                case 2: {
                    this.terrainName = LocalMessages.TERRAIN_NAME_2;
                    break;
                }
                case 3: {
                    this.terrainName = LocalMessages.TERRAIN_NAME_3;
                    break;
                }
                case 4: {
                    this.terrainName = LocalMessages.TERRAIN_NAME_4;
                    break;
                }
                default: {
                    ErrorSceneLog.getInstance().appendErrorMsg("terrain error").append("builder", builder.battleType).append("terrainType", defId).append("this.terrainVal", this.terrainVal).flush();
                    this.terrainName = LocalMessages.TERRAIN_NAME_DEFAULT;
                    break;
                }
            }
            this.terrainPic = terrain.getTerrainPic();
            this.attCamp.add(cas[0]);
            this.defCamp.add(cas[1]);
            builder.setSurroundState(dataGetter, this);
            if (cas[0].getPlayerId() > 0) {
                final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(cas[0].getPlayerId(), 43);
                int autoStrategy = 0;
                if (zdzsTech > 0) {
                    final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(cas[0].getPlayerId());
                    autoStrategy = pba.getAutoStrategy();
                }
                else {
                    autoStrategy = -1;
                }
                this.inBattlePlayers.put(cas[0].playerId, new PlayerInfo(cas[0].playerId, true, autoStrategy));
                if (!cas[0].isPhantom) {
                    NewBattleManager.getInstance().joinBattle(this, cas[0].playerId, cas[0].getGeneralId());
                }
            }
            BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#add:1Vs1Add#side:att" + "#playerId:" + cas[0].getPlayerId() + ":" + cas[0].isPhantom + "#general:" + cas[0].getGeneralId() + "#attSize:" + this.attCamp.size());
            if (cas[1].getPlayerId() > 0) {
                final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(cas[1].getPlayerId(), 43);
                int autoStrategy = 0;
                if (zdzsTech > 0) {
                    final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(cas[1].getPlayerId());
                    autoStrategy = pba.getAutoStrategy();
                }
                else {
                    autoStrategy = -1;
                }
                this.inBattlePlayers.put(cas[1].playerId, new PlayerInfo(cas[1].playerId, false, autoStrategy));
                if (!cas[1].isPhantom) {
                    NewBattleManager.getInstance().joinBattle(this, cas[1].playerId, cas[1].getGeneralId());
                }
            }
            BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#add:1Vs1Add#side:def" + "#playerId:" + cas[1].getPlayerId() + ":" + cas[1].isPhantom + "#general:" + cas[1].getGeneralId() + "#defSize:" + this.defCamp.size());
            final StringBuilder battleMsg = new StringBuilder();
            final int sn = this.ticket.incrementAndGet();
            battleMsg.append(sn).append("|").append(this.battleId).append("#");
            final List<BattleArmy> attAddQlist = new ArrayList<BattleArmy>();
            Builder.onceAddQueues(attAddQlist, this.attList, this.attCamp, 1, this.attQNum, this.attBaseInfo, this.inBattlePlayers, null, this);
            Builder.getReportType2(battleMsg, attAddQlist, "att");
            final List<BattleArmy> defAddQlist = new ArrayList<BattleArmy>();
            Builder.onceAddQueues(defAddQlist, this.defList, this.defCamp, 0, this.defQNum, this.defBaseInfo, this.inBattlePlayers, null, this);
            Builder.getReportType2(battleMsg, defAddQlist, "def");
            Builder.getReportType13(this, battleMsg);
            Builder.getReportType100(dataGetter, this, battleMsg);
            if (this.attList.size() > 0 && this.defList.size() > 0) {
                this.setBattleRoundBuff(dataGetter, this.attList.get(0).getCampArmy(), this.defList.get(0).getCampArmy());
            }
            if (this.attList.size() > 0) {
                Builder.getReportType16(dataGetter, this, battleMsg, this.attList.get(0).getCampArmy(), "att", true, true);
            }
            if (this.defList.size() > 0) {
                Builder.getReportType16(dataGetter, this, battleMsg, this.defList.get(0).getCampArmy(), "def", true, true);
            }
            this.SaveCurReport(dataGetter);
            this.curBattling.append("#").append(26).append("|").append(12000).append("#");
            Builder.getStrategyInfo(this.curBattling, this, dataGetter);
            battleMsg.append(26).append("|").append(12000).append("#");
            Builder.getStrategyInfo(battleMsg, this, dataGetter);
            this.waitting = true;
            Builder.sendMsgToAll(this, battleMsg);
            PlayerInfo piAtt = null;
            if (this.attList.size() > 0) {
                piAtt = this.inBattlePlayers.get(this.attList.get(0).getCampArmy().playerId);
            }
            final boolean attAutoStChoosed = piAtt != null && piAtt.autoStrategy == 1;
            PlayerInfo piDef = null;
            if (this.defList.size() > 0) {
                piDef = this.inBattlePlayers.get(this.defList.get(0).getCampArmy().playerId);
            }
            final boolean defAutoStChoosed = piDef != null && piDef.autoStrategy == 1;
            if (attAutoStChoosed && this.attList.size() > 0 && this.attList.get(0).getCampArmy().getTacticVal() > 0) {
                this.attChoose = true;
                this.attChooseTactic = true;
            }
            if (defAutoStChoosed && this.defList.size() > 0 && this.getSurround() == 0 && this.defList.get(0).getCampArmy().getTacticVal() > 0) {
                this.defChoose = true;
                this.defChooseTactic = true;
            }
            if (!attAutoStChoosed && !defAutoStChoosed) {
                this.nextMaxExeTime = System.currentTimeMillis() + 12000L;
            }
            else {
                this.nextMaxExeTime = System.currentTimeMillis() + 6000L;
            }
            long exeTime = System.currentTimeMillis();
            final String quickMode = Configuration.getProperty("gcld.battle.quick");
            if (quickMode.equals("1")) {
                final int interval = Integer.parseInt(Configuration.getProperty("gcld.battle.quick.interval"));
                exeTime += interval;
            }
            else {
                exeTime = this.nextMaxExeTime;
            }
            Battle.timerLog.info(LogUtil.formatThreadLog("Battle", "beginOneToOneBattle", 0, 0L, "battleId:" + this.getBattleId() + "|roundNum:" + this.getRoundNum() + "|exeTime:" + exeTime));
            this.changeExeTime(exeTime);
        }
        // monitorexit(this.battleId)
    }
    
    public boolean init(final BattleAttacker battleAttacker, final int battleType, final int defId, final IDataGetter dataGetter, final boolean auto, final int terrainType) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
        synchronized (this.battleId) {
            this.battleType = battleType;
            this.worldSceneLog = WorldSceneLog.getInstance();
            builder.setAttDefBaseInfo(dataGetter, this, battleAttacker, battleType, defId);
            this.auto = auto;
            if (battleType == 8) {
                final Terrain terrain = BattleService.rankTerrainmapValToDis.get(terrainType);
                this.terrain = terrain.getDisplay();
                switch (this.terrainVal = terrain.getValue()) {
                    case 1: {
                        this.terrainName = LocalMessages.TERRAIN_NAME_1;
                        break;
                    }
                    case 2: {
                        this.terrainName = LocalMessages.TERRAIN_NAME_2;
                        break;
                    }
                    case 3: {
                        this.terrainName = LocalMessages.TERRAIN_NAME_3;
                        break;
                    }
                    case 4: {
                        this.terrainName = LocalMessages.TERRAIN_NAME_4;
                        break;
                    }
                    default: {
                        ErrorSceneLog.getInstance().appendErrorMsg("terrain error").append("terrainType", terrainType).append("this.terrainVal", this.terrainVal).flush();
                        this.terrainName = LocalMessages.TERRAIN_NAME_DEFAULT;
                        break;
                    }
                }
                this.terrainPic = terrain.getTerrainPic();
            }
            else {
                final Terrain terrain = builder.getTerrain(battleAttacker.attPlayerId, defId, dataGetter);
                this.terrain = terrain.getDisplay();
                switch (this.terrainVal = terrain.getValue()) {
                    case 1: {
                        this.terrainName = LocalMessages.TERRAIN_NAME_1;
                        break;
                    }
                    case 2: {
                        this.terrainName = LocalMessages.TERRAIN_NAME_2;
                        break;
                    }
                    case 3: {
                        this.terrainName = LocalMessages.TERRAIN_NAME_3;
                        break;
                    }
                    case 4: {
                        this.terrainName = LocalMessages.TERRAIN_NAME_4;
                        break;
                    }
                    default: {
                        ErrorSceneLog.getInstance().appendErrorMsg("terrain error").append("builder", builder.battleType).append("defId", defId).append("this.terrainVal", this.terrainVal).flush();
                        this.terrainName = LocalMessages.TERRAIN_NAME_DEFAULT;
                        break;
                    }
                }
                this.terrainPic = terrain.getTerrainPic();
            }
            if (!this.auto && battleAttacker.attPlayerId > 0) {
                builder.inBattleInfo(battleAttacker.attPlayerId, true);
            }
            this.isNpc = builder.initDefCamp(dataGetter, battleAttacker, defId, this);
            if (battleAttacker.attPlayerId > 0) {
                builder.addInSceneSet(this, battleAttacker.attPlayerId);
            }
            builder.setSurroundState(dataGetter, this);
            builder.initAttCamp(dataGetter, battleAttacker, defId, this);
            if (battleType == 3) {
                this.reSetAllDamageE(dataGetter, this.attBaseInfo.getForceId(), this.defBaseInfo.forceId);
            }
            this.setBattleInitBuff(dataGetter, defId);
            this.setNormalJubenNoExpInitBuff();
            this.caculateCivilTrickBuff(dataGetter);
            final StringBuilder battleMsg = new StringBuilder();
            final int sn = this.ticket.incrementAndGet();
            battleMsg.append(sn).append("|").append(this.battleId).append("#");
            final List<BattleArmy> attAddQlist = new ArrayList<BattleArmy>();
            Builder.onceAddQueues(attAddQlist, this.attList, this.attCamp, 1, this.attQNum, this.attBaseInfo, this.inBattlePlayers, null, this);
            Builder.getReportType2(battleMsg, attAddQlist, "att");
            final List<BattleArmy> defAddQlist = new ArrayList<BattleArmy>();
            Builder.onceAddQueues(defAddQlist, this.defList, this.defCamp, 0, this.defQNum, this.defBaseInfo, this.inBattlePlayers, null, this);
            Builder.getReportType2(battleMsg, defAddQlist, "def");
            Builder.getReportType13(this, battleMsg);
            Builder.getReportType100(dataGetter, this, battleMsg);
            if (this.attList.size() > 0 && this.defList.size() > 0) {
                this.setBattleRoundBuff(dataGetter, this.attList.get(0).getCampArmy(), this.defList.get(0).getCampArmy());
            }
            if (this.attList.size() > 0) {
                Builder.getReportType16(dataGetter, this, battleMsg, this.attList.get(0).getCampArmy(), "att", true, true);
            }
            if (this.defList.size() > 0) {
                Builder.getReportType16(dataGetter, this, battleMsg, this.defList.get(0).getCampArmy(), "def", true, true);
            }
            this.SaveCurReport(dataGetter);
            this.curBattling.append("#").append(26).append("|").append(12000).append("#");
            Builder.getStrategyInfo(this.curBattling, this, dataGetter);
            battleMsg.append(26).append("|").append(12000).append("#");
            Builder.getStrategyInfo(battleMsg, this, dataGetter);
            this.waitting = true;
            Builder.sendMsgToAll(this, battleMsg);
            builder.sendBattleInfo(dataGetter, this, battleAttacker);
            if (battleAttacker.attPlayerId > 0) {
                builder.sendTaskMessage(battleAttacker.attPlayerId, defId, dataGetter);
            }
            dataGetter.getBattleInfoService().saveBattle(this);
            PlayerInfo piAtt = null;
            if (this.attList.size() > 0) {
                piAtt = this.inBattlePlayers.get(this.attList.get(0).getCampArmy().playerId);
            }
            final boolean attAutoStChoosed = piAtt != null && piAtt.autoStrategy == 1;
            PlayerInfo piDef = null;
            if (this.defList.size() > 0) {
                piDef = this.inBattlePlayers.get(this.defList.get(0).getCampArmy().playerId);
            }
            final boolean defAutoStChoosed = piDef != null && piDef.autoStrategy == 1;
            if (attAutoStChoosed && this.attList.size() > 0 && this.attList.get(0).getCampArmy().getTacticVal() > 0) {
                this.attChoose = true;
                this.attChooseTactic = true;
            }
            if (defAutoStChoosed && this.defList.size() > 0 && this.getSurround() == 0 && this.defList.get(0).getCampArmy().getTacticVal() > 0) {
                this.defChoose = true;
                this.defChooseTactic = true;
            }
            if (!attAutoStChoosed && !defAutoStChoosed) {
                this.nextMaxExeTime = System.currentTimeMillis() + 12000L;
            }
            else {
                this.nextMaxExeTime = System.currentTimeMillis() + 6000L;
            }
            this.caculateAgainstInfo(dataGetter);
            long exeTime = System.currentTimeMillis();
            final String quickMode = Configuration.getProperty("gcld.battle.quick");
            if (quickMode.equals("1")) {
                final int interval = Integer.parseInt(Configuration.getProperty("gcld.battle.quick.interval"));
                exeTime += interval;
            }
            else {
                exeTime = this.nextMaxExeTime;
            }
            final HuizhanHistory hh = dataGetter.getHuiZhanService().getTodayHuizhanBySate(1);
            if (hh != null && defId == hh.getCityId()) {
                this.nextMaxExeTime = hh.getStartTime().getTime() + 300000L;
                exeTime = this.nextMaxExeTime;
            }
            Battle.timerLog.info(LogUtil.formatThreadLog("Battle", "init", 0, 0L, "battleId:" + this.getBattleId() + "|roundNum:" + this.getRoundNum() + "|exeTime:" + exeTime));
            this.changeExeTime(exeTime);
            if (this.defCamp.size() == 0) {
                this.isQuickConquer = true;
                // monitorexit(this.battleId)
                return true;
            }
            // monitorexit(this.battleId)
            return false;
        }
    }
    
    public void setBattleRoundBuff(final IDataGetter dataGetter, final CampArmy attCa, final CampArmy defCa) {
        try {
            this.attBuffListRound.clear();
            this.defBuffListRound.clear();
            this.world_round_ally_buff_att_e = 0.0;
            this.world_round_ally_buff_def_e = 0.0;
            if (this.attList.size() == 0 || this.defList.size() == 0) {
                return;
            }
            if (attCa.getTerrainAdd() > 0 && defCa.specialGeneral.generalType != 11) {
                final Tuple<Integer, String> temp = new Tuple();
                temp.left = 23 + attCa.getTerrainQ();
                temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_23_TERRAIN, new Object[] { this.terrainName, attCa.getTerrainAdd() });
                this.attBuffListRound.add(temp);
            }
            if (defCa.getTerrainAdd() > 0 && attCa.specialGeneral.generalType != 11) {
                final Tuple<Integer, String> temp = new Tuple();
                if (defCa.getTerrainQ() == 7) {
                    temp.left = 29;
                    temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_29, new Object[] { defCa.getTerrainAdd() });
                }
                else {
                    temp.left = 23 + defCa.getTerrainQ();
                    temp.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_23_TERRAIN, new Object[] { this.terrainName, defCa.getTerrainAdd() });
                }
                this.defBuffListRound.add(temp);
            }
            if (this.battleType == 3 || this.battleType == 13) {
                final Long attShaDiLingExpireTime = this.shaDiLingMap.get(attCa.getForceId());
                if (attShaDiLingExpireTime != null && System.currentTimeMillis() <= attShaDiLingExpireTime) {
                    final Tuple<Integer, String> temp2 = new Tuple();
                    temp2.left = 39;
                    temp2.right = LocalMessages.BUFF_TIPS_39;
                    this.attBuffListRound.add(temp2);
                }
                final Long defShaDiLingExpireTime = this.shaDiLingMap.get(defCa.getForceId());
                if (defShaDiLingExpireTime != null && System.currentTimeMillis() <= defShaDiLingExpireTime) {
                    final Tuple<Integer, String> temp3 = new Tuple();
                    temp3.left = 39;
                    temp3.right = LocalMessages.BUFF_TIPS_39;
                    this.defBuffListRound.add(temp3);
                }
            }
            final int attFarmBuff = dataGetter.getWorldFarmService().getBuff(attCa.playerId, attCa.generalId);
            if (attFarmBuff > 0 && !attCa.isPhantom) {
                final Tuple<Integer, String> temp2 = new Tuple();
                temp2.left = 49;
                temp2.right = LocalMessages.BUFF_TIPS_49;
                this.attBuffListRound.add(temp2);
            }
            final int defFarmBuff = dataGetter.getWorldFarmService().getBuff(defCa.playerId, defCa.generalId);
            if (defFarmBuff > 0 && !defCa.isPhantom) {
                final Tuple<Integer, String> temp3 = new Tuple();
                temp3.left = 49;
                temp3.right = LocalMessages.BUFF_TIPS_49;
                this.defBuffListRound.add(temp3);
            }
            final Map<Integer, Double> attGemAttrMap = dataGetter.getBattleDataCache().getGemAttribute(attCa.getPlayerId());
            final Map<Integer, Double> defGemAttrMap = dataGetter.getBattleDataCache().getGemAttribute(defCa.getPlayerId());
            if (attGemAttrMap != null && !attGemAttrMap.isEmpty()) {
                if (attGemAttrMap.get(1) > 0.0) {
                    final Tuple<Integer, String> temp4 = new Tuple();
                    temp4.left = 45;
                    temp4.right = LocalMessages.BUFF_TIPS_45;
                    this.attBuffListRound.add(temp4);
                }
                if ((this.battleType == 13 || this.battleType == 15 || this.battleType == 19) && attGemAttrMap.get(5) > 0.0) {
                    final Tuple<Integer, String> temp4 = new Tuple();
                    temp4.left = 46;
                    temp4.right = LocalMessages.BUFF_TIPS_46;
                    this.attBuffListRound.add(temp4);
                }
                if (attGemAttrMap.get(7) > 0.0) {
                    final Tuple<Integer, String> temp4 = new Tuple();
                    temp4.left = 48;
                    temp4.right = LocalMessages.BUFF_TIPS_48;
                    this.attBuffListRound.add(temp4);
                }
            }
            if (defGemAttrMap != null && !defGemAttrMap.isEmpty()) {
                if (defGemAttrMap.get(1) > 0.0) {
                    final Tuple<Integer, String> temp4 = new Tuple();
                    temp4.left = 45;
                    temp4.right = LocalMessages.BUFF_TIPS_45;
                    this.defBuffListRound.add(temp4);
                }
                if ((this.battleType == 13 || this.battleType == 15 || this.battleType == 19) && defGemAttrMap.get(5) > 0.0) {
                    final Tuple<Integer, String> temp4 = new Tuple();
                    temp4.left = 46;
                    temp4.right = LocalMessages.BUFF_TIPS_46;
                    this.defBuffListRound.add(temp4);
                }
                if (defGemAttrMap.get(6) > 0.0) {
                    final Tuple<Integer, String> temp4 = new Tuple();
                    temp4.left = 47;
                    temp4.right = LocalMessages.BUFF_TIPS_47;
                    this.defBuffListRound.add(temp4);
                }
            }
            if (attCa.specialGeneral.generalType == 4) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 30;
                temp4.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_30, new Object[] { (int)(attCa.specialGeneral.param * 100.0) });
                this.attBuffListRound.add(temp4);
            }
            if (attCa.specialGeneral.generalType == 2 && (int)attCa.specialGeneral.param > 0) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 31;
                temp4.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_31, new Object[] { (int)(attCa.specialGeneral.param * 100.0) });
                this.attBuffListRound.add(temp4);
            }
            if (attCa.specialGeneral.generalType == 3) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 32;
                temp4.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_32, new Object[] { (int)(attCa.specialGeneral.param * 100.0) });
                this.attBuffListRound.add(temp4);
            }
            if (attCa.specialGeneral.generalType == 8) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 33;
                temp4.right = LocalMessages.BUFF_TIPS_33;
                this.attBuffListRound.add(temp4);
            }
            if (attCa.activityAddExp > 0.0f) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 34;
                if (attCa.activityAddExp >= 0.3f) {
                    temp4.left = 36;
                }
                else if (attCa.activityAddExp >= 0.2f) {
                    temp4.left = 35;
                }
                else if (attCa.activityAddExp >= 0.1f) {
                    temp4.left = 34;
                }
                temp4.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_34, new Object[] { (int)(attCa.activityAddExp * 100.0f) });
                this.attBuffListRound.add(temp4);
            }
            if (attCa.specialGeneral.generalType == 11) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 44;
                temp4.right = LocalMessages.BUFF_TIPS_44;
                this.attBuffListRound.add(temp4);
            }
            if (defCa.specialGeneral.generalType == 4) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 30;
                temp4.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_30, new Object[] { (int)(defCa.specialGeneral.param * 100.0) });
                this.defBuffListRound.add(temp4);
            }
            if (defCa.specialGeneral.generalType == 2 && (int)defCa.specialGeneral.param > 0) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 31;
                temp4.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_31, new Object[] { (int)(defCa.specialGeneral.param * 100.0) });
                this.defBuffListRound.add(temp4);
            }
            if (defCa.specialGeneral.generalType == 3) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 32;
                temp4.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_32, new Object[] { (int)(defCa.specialGeneral.param * 100.0) });
                this.defBuffListRound.add(temp4);
            }
            if (defCa.specialGeneral.generalType == 8) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 33;
                temp4.right = LocalMessages.BUFF_TIPS_33;
                this.defBuffListRound.add(temp4);
            }
            if (defCa.activityAddExp > 0.0f) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 34;
                if (defCa.activityAddExp >= 0.3f) {
                    temp4.left = 36;
                }
                else if (defCa.activityAddExp >= 0.2f) {
                    temp4.left = 35;
                }
                else if (defCa.activityAddExp >= 0.1f) {
                    temp4.left = 34;
                }
                temp4.right = MessageFormatter.format(LocalMessages.BUFF_TIPS_34, new Object[] { (int)(defCa.activityAddExp * 100.0f) });
                this.defBuffListRound.add(temp4);
            }
            if (defCa.specialGeneral.generalType == 11) {
                final Tuple<Integer, String> temp4 = new Tuple();
                temp4.left = 44;
                temp4.right = LocalMessages.BUFF_TIPS_44;
                this.defBuffListRound.add(temp4);
            }
            if (this.battleType == 3 || this.battleType == 13) {
                this.setRoundAllayBuff(attCa, defCa);
            }
            if (this.battleType == 18 || this.battleType == 19 || this.battleType == 20) {
                this.setJuBenBattleRoundBuff();
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.setBattleRoundBuff catch Exception", e);
        }
    }
    
    private void setRoundAllayBuff(final CampArmy attCa, final CampArmy defCa) {
        try {
            final int attForceId = attCa.forceId;
            final int defForceId = defCa.forceId;
            if (attForceId != 1 && attForceId != 2 && attForceId != 3) {
                return;
            }
            if (defForceId != 1 && defForceId != 2 && defForceId != 3) {
                return;
            }
            final int thirdForceId = 6 - attForceId - defForceId;
            if (thirdForceId != 1 && thirdForceId != 2 && thirdForceId != 3) {
                ErrorSceneLog.getInstance().appendErrorMsg("thirdForceId error").appendBattleId(this.battleId).append("thirdForceId", thirdForceId).append("attForceId", attForceId).append("defForceId", defForceId).appendMethodName("setBattleRoundBuff").flush();
                return;
            }
            final boolean isAlliedType1 = CityDataCache.isAllied(attForceId, defForceId);
            if (isAlliedType1) {
                this.world_round_ally_buff_att_e = -0.2;
                final Tuple<Integer, String> temp = new Tuple();
                temp.left = 21;
                temp.right = LocalMessages.BUFF_TIPS_21;
                this.attBuffListRound.add(temp);
            }
            final boolean isAlliedType2 = CityDataCache.isAllied(attForceId, thirdForceId);
            if (isAlliedType2) {
                this.world_round_ally_buff_att_e = 0.05;
                final Tuple<Integer, String> temp2 = new Tuple();
                temp2.left = 22;
                temp2.right = LocalMessages.BUFF_TIPS_22;
                this.attBuffListRound.add(temp2);
            }
            final boolean isAlliedType3 = CityDataCache.isAllied(defForceId, thirdForceId);
            if (isAlliedType3) {
                this.world_round_ally_buff_def_e = 0.05;
                final Tuple<Integer, String> temp3 = new Tuple();
                temp3.left = 22;
                temp3.right = LocalMessages.BUFF_TIPS_22;
                this.defBuffListRound.add(temp3);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.setBattleRoundAllayBuff catch Exception", e);
        }
    }
    
    private void setJuBenBattleRoundBuffBase() {
        try {
            if (this.attBaseInfo.forceId == 1 || this.attBaseInfo.forceId == 2 || this.attBaseInfo.forceId == 3) {
                final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(this.attBaseInfo.id);
                if (juBenDto != null) {
                    final long now = System.currentTimeMillis();
                    if (juBenDto.juben_att_def_base_buff_player_duration_time == 0L || now <= juBenDto.juben_att_def_base_buff_player_duration_time) {
                        int buffAttAdd = juBenDto.juben_att_base_buff_player;
                        if (buffAttAdd != 0) {
                            this.att_gongji_base_add = juBenDto.juben_att_base_buff_player;
                            String format = null;
                            final Tuple<Integer, String> temp = new Tuple();
                            if (buffAttAdd > 0) {
                                format = LocalMessages.JUBEN_BUFF_Att_ADD_FORMAT;
                                temp.left = 40;
                            }
                            else {
                                buffAttAdd = -buffAttAdd;
                                format = LocalMessages.JUBEN_BUFF_Att_REDUCE_FORMAT;
                                temp.left = 40;
                            }
                            temp.right = format;
                            this.attBuffListRound.add(temp);
                        }
                        final int buffdefAdd = juBenDto.juben_def_base_buff_player;
                        if (buffdefAdd != 0) {
                            this.att_fangyu_base_add = buffdefAdd;
                        }
                    }
                    if (juBenDto.juben_buff_npc_duration_time == 0L || now <= juBenDto.juben_buff_npc_duration_time) {
                        int buffAttAdd = juBenDto.juben_att_base_buff_npc;
                        if (buffAttAdd != 0) {
                            this.def_gongji_base_add = buffAttAdd;
                            String format = null;
                            final Tuple<Integer, String> temp = new Tuple();
                            if (buffAttAdd > 0) {
                                format = LocalMessages.JUBEN_BUFF_Att_ADD_FORMAT;
                                temp.left = 40;
                            }
                            else {
                                buffAttAdd = -buffAttAdd;
                                format = LocalMessages.JUBEN_BUFF_Att_REDUCE_FORMAT;
                                temp.left = 40;
                            }
                            temp.right = format;
                            this.defBuffListRound.add(temp);
                        }
                        final int buffdefAdd = juBenDto.juben_def_base_buff_npc;
                        if (buffdefAdd != 0) {
                            this.def_fangyu_base_add = buffdefAdd;
                        }
                    }
                }
            }
            else if (this.defBaseInfo.forceId == 1 || this.defBaseInfo.forceId == 2 || this.defBaseInfo.forceId == 3) {
                final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(this.defBaseInfo.id);
                if (juBenDto != null) {
                    final long now = System.currentTimeMillis();
                    if (juBenDto.juben_att_def_base_buff_npc_duration_time == 0L || now <= juBenDto.juben_att_def_base_buff_npc_duration_time) {
                        int buffAdd = juBenDto.juben_att_base_buff_npc;
                        if (buffAdd != 0) {
                            this.def_gongji_base_add = buffAdd;
                            String format = null;
                            final Tuple<Integer, String> temp = new Tuple();
                            if (buffAdd > 0) {
                                format = LocalMessages.JUBEN_BUFF_Att_ADD_FORMAT;
                                temp.left = 40;
                            }
                            else {
                                buffAdd = -buffAdd;
                                format = LocalMessages.JUBEN_BUFF_Att_REDUCE_FORMAT;
                                temp.left = 40;
                            }
                            temp.right = format;
                            this.defBuffListRound.add(temp);
                        }
                        buffAdd = juBenDto.juben_def_base_buff_npc;
                        if (buffAdd != 0) {
                            this.def_fangyu_base_add = buffAdd;
                        }
                    }
                    if (juBenDto.juben_att_def_base_buff_player_duration_time == 0L || now <= juBenDto.juben_att_def_base_buff_player_duration_time) {
                        int buffAdd = juBenDto.juben_att_base_buff_player;
                        if (buffAdd != 0) {
                            this.att_gongji_base_add = buffAdd;
                            String format = null;
                            final Tuple<Integer, String> temp = new Tuple();
                            if (buffAdd > 0) {
                                format = LocalMessages.JUBEN_BUFF_Att_ADD_FORMAT;
                                temp.left = 40;
                            }
                            else {
                                buffAdd = -buffAdd;
                                format = LocalMessages.JUBEN_BUFF_Att_REDUCE_FORMAT;
                                temp.left = 40;
                            }
                            temp.right = format;
                            this.attBuffListRound.add(temp);
                        }
                        buffAdd = juBenDto.juben_def_base_buff_player;
                        if (buffAdd != 0) {
                            this.att_fangyu_base_add = buffAdd;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.setJuBenBattleRoundBuffBase catch Exception", e);
        }
    }
    
    private void setJuBenBattleRoundBuffCoe() {
        try {
            if (this.attBaseInfo.forceId == 1 || this.attBaseInfo.forceId == 2 || this.attBaseInfo.forceId == 3) {
                final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(this.attBaseInfo.id);
                if (juBenDto != null) {
                    final long now = System.currentTimeMillis();
                    if (juBenDto.juben_buff_player_duration_time == 0L || now <= juBenDto.juben_buff_player_duration_time) {
                        final double buffAdd = juBenDto.juben_buff_player - 1.0;
                        if (buffAdd != 0.0) {
                            this.all_damage_e_att += buffAdd;
                            String format = null;
                            int num = 0;
                            final Tuple<Integer, String> temp = new Tuple();
                            if (buffAdd > 0.0) {
                                format = LocalMessages.JUBEN_BUFF_ADD_FORMAT;
                                num = (int)Math.ceil(buffAdd * 100.0);
                                temp.left = 37;
                            }
                            else {
                                format = LocalMessages.JUBEN_BUFF_REDUCE_FORMAT;
                                num = (int)Math.ceil(-buffAdd * 100.0);
                                temp.left = 38;
                            }
                            temp.right = MessageFormatter.format(format, new Object[] { num });
                            this.attBuffListRound.add(temp);
                        }
                    }
                    if (juBenDto.juben_buff_npc_duration_time == 0L || now <= juBenDto.juben_buff_npc_duration_time) {
                        final double buffAdd = juBenDto.juben_buff_npc - 1.0;
                        if (buffAdd != 0.0) {
                            this.all_damage_e_def += buffAdd;
                            String format = null;
                            int num = 0;
                            final Tuple<Integer, String> temp = new Tuple();
                            if (buffAdd > 0.0) {
                                format = LocalMessages.JUBEN_BUFF_ADD_FORMAT;
                                num = (int)Math.ceil(buffAdd * 100.0);
                                temp.left = 37;
                            }
                            else {
                                format = LocalMessages.JUBEN_BUFF_REDUCE_FORMAT;
                                num = (int)Math.ceil(-buffAdd * 100.0);
                                temp.left = 38;
                            }
                            temp.right = MessageFormatter.format(format, new Object[] { num });
                            this.defBuffListRound.add(temp);
                        }
                    }
                }
            }
            else if (this.defBaseInfo.forceId == 1 || this.defBaseInfo.forceId == 2 || this.defBaseInfo.forceId == 3) {
                final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(this.defBaseInfo.id);
                if (juBenDto != null) {
                    final long now = System.currentTimeMillis();
                    if (juBenDto.juben_buff_player_duration_time == 0L || now <= juBenDto.juben_buff_player_duration_time) {
                        final double buffAdd = juBenDto.juben_buff_player - 1.0;
                        if (buffAdd != 0.0) {
                            this.all_damage_e_def += buffAdd;
                            String format = null;
                            int num = 0;
                            final Tuple<Integer, String> temp = new Tuple();
                            if (buffAdd > 0.0) {
                                format = LocalMessages.JUBEN_BUFF_ADD_FORMAT;
                                num = (int)Math.ceil(buffAdd * 100.0);
                                temp.left = 37;
                            }
                            else {
                                format = LocalMessages.JUBEN_BUFF_REDUCE_FORMAT;
                                num = (int)Math.ceil(-buffAdd * 100.0);
                                temp.left = 38;
                            }
                            temp.right = MessageFormatter.format(format, new Object[] { num });
                            this.defBuffListRound.add(temp);
                        }
                    }
                    if (juBenDto.juben_buff_npc_duration_time == 0L || now <= juBenDto.juben_buff_npc_duration_time) {
                        final double buffAdd = juBenDto.juben_buff_npc - 1.0;
                        if (buffAdd != 0.0) {
                            this.all_damage_e_att += buffAdd;
                            String format = null;
                            int num = 0;
                            final Tuple<Integer, String> temp = new Tuple();
                            if (buffAdd > 0.0) {
                                format = LocalMessages.JUBEN_BUFF_ADD_FORMAT;
                                num = (int)Math.ceil(buffAdd * 100.0);
                                temp.left = 37;
                            }
                            else {
                                format = LocalMessages.JUBEN_BUFF_REDUCE_FORMAT;
                                num = (int)Math.ceil(-buffAdd * 100.0);
                                temp.left = 38;
                            }
                            temp.right = MessageFormatter.format(format, new Object[] { num });
                            this.attBuffListRound.add(temp);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.setJuBenBattleRoundBuffCoe catch Exception", e);
        }
    }
    
    private void setJuBenBattleRoundBuff() {
        this.setJuBenBattleRoundBuffBase();
        this.setJuBenBattleRoundBuffCoe();
    }
    
    private void setNormalJubenNoExpInitBuff() {
        try {
            if (this.attBaseInfo.forceId == 1 || this.attBaseInfo.forceId == 2 || this.attBaseInfo.forceId == 3) {
                final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(this.attBaseInfo.id);
                if (juBenDto != null && juBenDto.juBen_id <= 10000) {
                    final Tuple<Integer, String> buff = new Tuple();
                    buff.left = 14;
                    buff.right = LocalMessages.BUFF_TIPS_14;
                    this.attBuffListInit.add(buff);
                }
            }
            else if (this.defBaseInfo.forceId == 1 || this.defBaseInfo.forceId == 2 || this.defBaseInfo.forceId == 3) {
                final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(this.defBaseInfo.id);
                if (juBenDto != null) {
                    final Tuple<Integer, String> buff = new Tuple();
                    buff.left = 14;
                    buff.right = LocalMessages.BUFF_TIPS_14;
                    this.defBuffListInit.add(buff);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.setNormalJubenNoExpInitBuff catch Exception", e);
        }
    }
    
    public void setBattleInitBuff(final IDataGetter dataGetter, final int defId) {
        this.world_weaken_besiege_e = 0.0;
        this.world_frontLine_buff_type = 0;
        this.world_frontLine_buff_exp_e = 1.0;
        this.world_frontLine_buff_att_def_e = 0.0;
        if (this.battleType == 3 || this.battleType == 13) {
            final City city = dataGetter.getCityDao().read(defId);
            final int cityTitle = city.getTitle();
            double coe = 0.0;
            if (cityTitle == 1) {
                coe = ((C)dataGetter.getcCache().get((Object)"World.DamageWeaken.Besiege1")).getValue();
                this.world_weaken_besiege_e = coe;
            }
            else if (cityTitle == 2) {
                coe = ((C)dataGetter.getcCache().get((Object)"World.DamageWeaken.Besiege2")).getValue();
                this.world_weaken_besiege_e = coe;
            }
            final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)defId);
            if (city.getForceId() == 1 || city.getForceId() == 2 || city.getForceId() == 3) {
                final int attForceId = this.attBaseInfo.forceId;
                final int defForceId = this.defBaseInfo.forceId;
                final int distanceAtt = worldCity.getDistance(attForceId);
                final int distanceDef = worldCity.getDistance(defForceId);
                final int distance = (distanceAtt > distanceDef) ? distanceDef : distanceAtt;
                final int distanceState = WorldCityCommon.getDistanceState(distance);
                this.world_frontLine_buff_type = distanceState;
                this.world_frontLine_buff_exp_e = WorldCityCommon.getAddExp(distanceState);
                this.world_frontLine_buff_att_def_e = WorldCityCommon.getReduceAttDef(distanceState);
                this.world_frontLine_buff_att_def_e_side = ((distanceAtt > distanceDef) ? 1 : 2);
            }
            if ((this.battleType == 3 || this.battleType == 13) && this.terrainVal == 4) {
                final Tuple<Integer, String> tacticHalfBuff = new Tuple();
                tacticHalfBuff.left = 10;
                tacticHalfBuff.right = LocalMessages.BUFF_TIPS_10;
                this.attBuffListInit.add(tacticHalfBuff);
            }
            if (this.world_frontLine_buff_type != 0) {
                final Tuple<Integer, String> frontBuff = new Tuple();
                switch (this.world_frontLine_buff_type) {
                    case 1: {
                        frontBuff.left = 9;
                        frontBuff.right = LocalMessages.BUFF_TIPS_9;
                        break;
                    }
                    case 2: {
                        frontBuff.left = 8;
                        frontBuff.right = LocalMessages.BUFF_TIPS_8;
                        break;
                    }
                    case 3: {
                        frontBuff.left = 7;
                        frontBuff.right = LocalMessages.BUFF_TIPS_7;
                        break;
                    }
                }
                if (this.world_frontLine_buff_att_def_e_side == 1) {
                    this.attBuffListInit.add(frontBuff);
                }
                else if (this.world_frontLine_buff_att_def_e_side == 2) {
                    this.defBuffListInit.add(frontBuff);
                }
            }
            final Map<Integer, Long> map = dataGetter.getCityService().getShaDiLingInfoInThisCity(defId);
            if (map != null) {
                this.shaDiLingMap = map;
            }
            else {
                this.shaDiLingMap = new HashMap<Integer, Long>();
            }
        }
    }
    
    public byte[] useStrategy(final int playerId, final int strategyId, final IDataGetter dataGetter, final int pos) {
        if (System.currentTimeMillis() > this.nextMaxExeTime) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_TIME_OUT_CANNOT_USE_STRATEGY);
        }
        final PlayerInfo pi = this.inBattlePlayers.get(playerId);
        if (pi == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_CANNOT_USE_STRATEGY);
        }
        if (strategyId != 100 && dataGetter.getFightStrategiesCache().get((Object)strategyId) == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        synchronized (this.battleId) {
            boolean myChange = false;
            if (this.attList.size() == 0) {
                // monitorexit(this.battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INFO_WORLD_BATENDED);
            }
            CampArmy myCA = this.attList.get(0).getCampArmy();
            BattleArmy myBa = this.attList.get(0);
            boolean attSide = true;
            if (myCA.getPlayerId() == playerId) {
                myChange = true;
                attSide = true;
                if (this.attList.get(0).isChoose() || this.attList.get(0).getPosition() != pos) {
                    // monitorexit(this.battleId)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_HAD_CHOOSE);
                }
            }
            if (!myChange) {
                if (this.defList.size() == 0) {
                    // monitorexit(this.battleId)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INFO_WORLD_BATENDED);
                }
                myCA = this.defList.get(0).getCampArmy();
                myBa = this.defList.get(0);
                if (myCA.getPlayerId() == playerId) {
                    myChange = true;
                    attSide = false;
                    if (this.defList.get(0).isChoose() || this.defList.get(0).getPosition() != pos) {
                        // monitorexit(this.battleId)
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_HAD_CHOOSE);
                    }
                }
            }
            if (!myChange) {
                // monitorexit(this.battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_CANNOT_USE_STRATEGY);
            }
            if (myBa.getSpecial() == 1) {
                // monitorexit(this.battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_CONFUSION_CANNOT_USE_STRATEGY);
            }
            if (myCA.isPhantom) {
                // monitorexit(this.battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_CANNOT_CHOOSE_ST_TACTIC);
            }
            boolean canUse = false;
            int[] strategies;
            for (int length = (strategies = myCA.getStrategies()).length, i = 0; i < length; ++i) {
                final int sId = strategies[i];
                if (sId == strategyId) {
                    canUse = true;
                }
            }
            if (!canUse && strategyId != 100) {
                // monitorexit(this.battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (strategyId != 100) {
                boolean change = false;
                final List<Integer> list = new ArrayList<Integer>();
                for (final BattleArmy battleArmy : this.attList) {
                    if (battleArmy.getCampArmy().getPlayerId() == playerId && myCA.generalId == battleArmy.getCampArmy().getGeneralId()) {
                        change = true;
                        battleArmy.setStrategy(strategyId);
                        list.add(battleArmy.getPosition());
                    }
                }
                if (!change) {
                    for (final BattleArmy battleArmy : this.defList) {
                        if (battleArmy.getCampArmy().getPlayerId() == playerId && myCA.generalId == battleArmy.getCampArmy().getGeneralId()) {
                            change = true;
                            battleArmy.setStrategy(strategyId);
                            list.add(battleArmy.getPosition());
                        }
                    }
                }
                if (change) {
                    pi.curStrategy = strategyId;
                    if (!list.isEmpty()) {
                        if (myChange) {
                            if (attSide) {
                                this.attChoose = true;
                                this.attList.get(0).setChoose(true);
                                if (this.waitting && !this.defChoose && this.battleType == 3) {
                                    final CampArmy ca = this.defList.get(0).getCampArmy();
                                    if (ca.getPlayerId() > 0 && !ca.isPhantom && Players.getSession(Integer.valueOf(this.defList.get(0).getCampArmy().getPlayerId())) != null) {
                                        // monitorexit(this.battleId)
                                        return JsonBuilder.getJson(State.SUCCESS, "");
                                    }
                                }
                            }
                            else {
                                this.defChoose = true;
                                this.defList.get(0).setChoose(true);
                                if (this.waitting && !this.attChooseTactic) {
                                    final CampArmy ca = this.attList.get(0).getCampArmy();
                                    if (ca.getPlayerId() > 0 && !ca.isPhantom) {
                                        // monitorexit(this.battleId)
                                        return JsonBuilder.getJson(State.SUCCESS, "");
                                    }
                                }
                            }
                            long time;
                            final long now = time = System.currentTimeMillis();
                            if (time < this.nextMinExeTime) {
                                time = this.nextMinExeTime;
                                BattleSceneLog.getInstance().appendLogMsg("choose strategy before this.nextMinExeTime").appendBattleId(this.battleId).append("now", now).append("this.nextMinExeTime", this.nextMinExeTime).append("bias", now - this.nextMinExeTime).flush();
                            }
                            final StringBuilder battleMsg = new StringBuilder();
                            final int sn = this.ticket.incrementAndGet();
                            battleMsg.append(sn).append("|").append(this.battleId).append("#");
                            battleMsg.append(25).append("|").append(playerId).append("|").append(attSide ? 1 : 0).append(";");
                            for (final Integer position : list) {
                                battleMsg.append(position).append("|").append(strategyId).append(";");
                            }
                            battleMsg.replace(battleMsg.length() - 1, battleMsg.length(), "#");
                            final String SingleHeadInfo = " battleId " + this.battleId + " \u73a9\u5bb6\u9009\u62e9\u6218\u672f\uff0c\u53d1\u9001\u7ed9\u4e2a\u4eba:" + Players.getPlayer(playerId).playerName + "  battleMsg: ";
                            Builder.sendMsgToSingle(this, battleMsg, playerId, SingleHeadInfo, null);
                            Battle.timerLog.info(LogUtil.formatThreadLog("Battle", "useStrategy", 0, 0L, "battleId:" + this.getBattleId() + "|roundNum:" + this.getRoundNum() + "|exeTime:" + time));
                            this.changeExeTime(time);
                        }
                        // monitorexit(this.battleId)
                        return JsonBuilder.getJson(State.SUCCESS, "");
                    }
                }
                // monitorexit(this.battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_CANNOT_USE_STRATEGY);
            }
            if (myCA.getTacticVal() <= 0) {
                // monitorexit(this.battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_CANNOT_USE_TACTIC);
            }
            if (!attSide && this.getSurround() > 0) {
                // monitorexit(this.battleId)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_CANNOT_USE_TACTIC2);
            }
            if (myChange) {
                BattleSceneLog.getInstance().appendBattleId(this.battleId).appendLogMsg("choose tactic").appendPlayerName(Players.getPlayer(playerId).playerName).appendGeneralName(myCA.generalName).append("tactic", ((Tactic)dataGetter.getTacticCache().get((Object)myCA.tacicId)).getName()).flush();
                if (attSide) {
                    this.attChoose = true;
                    this.attChooseTactic = true;
                    if (this.waitting && !this.defChoose && this.battleType == 3) {
                        final CampArmy ca2 = this.defList.get(0).getCampArmy();
                        if (ca2.getPlayerId() > 0 && !ca2.isPhantom) {
                            // monitorexit(this.battleId)
                            return JsonBuilder.getJson(State.SUCCESS, "");
                        }
                    }
                }
                else {
                    this.defChoose = true;
                    this.defChooseTactic = true;
                    if (this.waitting && !this.attChooseTactic) {
                        final CampArmy ca2 = this.attList.get(0).getCampArmy();
                        if (ca2.getPlayerId() > 0 && !ca2.isPhantom) {
                            // monitorexit(this.battleId)
                            return JsonBuilder.getJson(State.SUCCESS, "");
                        }
                    }
                }
                long time2;
                final long now2 = time2 = System.currentTimeMillis();
                if (time2 < this.nextMinExeTime) {
                    time2 = this.nextMinExeTime;
                    BattleSceneLog.getInstance().appendLogMsg("choose tactic before this.nextMinExeTime").appendBattleId(this.battleId).append("now", now2).append("this.nextMinExeTime", this.nextMinExeTime).append("bias", now2 - this.nextMinExeTime).flush();
                }
                Battle.timerLog.info(LogUtil.formatThreadLog("Battle", "useStrategy", 0, 0L, "battleId:" + this.getBattleId() + "|roundNum:" + this.getRoundNum() + "|exeTime:" + time2));
                this.changeExeTime(time2);
            }
            // monitorexit(this.battleId)
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
    }
    
    public void caculateCivilTrickBuff(final IDataGetter dataGetter) {
        if (this.battleType == 3 || this.battleType == 13) {
            final int cityId = this.defBaseInfo.id;
            final int attForceId = this.attBaseInfo.forceId;
            final int defForceId = this.defBaseInfo.forceId;
            Tuple<List<Stratagem>, List<Stratagem>> stratagems = null;
            try {
                this.removeTrickBuff(this.attBuffListInit);
                this.removeTrickBuff(this.defBuffListInit);
                stratagems = dataGetter.getCilvilTrickService().getStateList(cityId, attForceId, defForceId);
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().appendErrorMsg("getStateList catch exception").appendBattleId(this.battleId).append("cityId", cityId).append("attForceId", attForceId).append("defForceId", defForceId).flush();
                return;
            }
            int lv = 0;
            int param = 0;
            final Tuple<Integer, String> buff = new Tuple();
            if (stratagems != null && ((List)stratagems.left).size() > 0) {
                for (final Stratagem st : (List)stratagems.left) {
                    lv = st.getQuality();
                    param = st.getPar1();
                    if (st.getType().equals("guwu")) {
                        buff.left = 0 + lv;
                        buff.right = String.valueOf(LocalMessages.BUFF_TIPS_GUWU_PART1) + lv + LocalMessages.BUFF_TIPS_GUWU_PART2 + param + LocalMessages.BUFF_TIPS_GUWU_PART3;
                    }
                    else if (st.getType().equals("dongyao")) {
                        buff.left = 3 + lv;
                        buff.right = String.valueOf(LocalMessages.BUFF_TIPS_DONGYAO_PART1) + lv + LocalMessages.BUFF_TIPS_DONGYAO_PART2 + param + LocalMessages.BUFF_TIPS_DONGYAO_PART3;
                    }
                    this.attBuffListInit.add(buff);
                }
            }
            if (stratagems != null && ((List)stratagems.right).size() > 0) {
                for (final Stratagem st : (List)stratagems.right) {
                    lv = st.getQuality();
                    param = st.getPar1();
                    if (st.getType().equals("guwu")) {
                        buff.left = 0 + lv;
                        buff.right = String.valueOf(LocalMessages.BUFF_TIPS_GUWU_PART1) + lv + LocalMessages.BUFF_TIPS_GUWU_PART2 + param + LocalMessages.BUFF_TIPS_GUWU_PART3;
                    }
                    else if (st.getType().equals("dongyao")) {
                        buff.left = 3 + lv;
                        buff.right = String.valueOf(LocalMessages.BUFF_TIPS_DONGYAO_PART1) + lv + LocalMessages.BUFF_TIPS_DONGYAO_PART2 + param + LocalMessages.BUFF_TIPS_DONGYAO_PART3;
                    }
                    this.defBuffListInit.add(buff);
                }
            }
        }
    }
    
    private void removeTrickBuff(final List<Tuple<Integer, String>> defBuffList2) {
        final List<Tuple<Integer, String>> removeList = new ArrayList<Tuple<Integer, String>>();
        if (defBuffList2 == null || defBuffList2.isEmpty() || defBuffList2.size() <= 0) {
            return;
        }
        for (final Tuple<Integer, String> tuple : defBuffList2) {
            if (tuple == null) {
                continue;
            }
            final Integer type = tuple.left;
            if ((type < 1 || type > 3) && (type < 4 || type > 6)) {
                continue;
            }
            removeList.add(tuple);
        }
        for (final Tuple<Integer, String> tuple : removeList) {
            if (tuple == null) {
                continue;
            }
            defBuffList2.remove(tuple);
        }
    }
    
    public void caculateAgainstInfo(final IDataGetter dataGetter) {
        for (final Integer side : this.attSideDetail.keySet()) {
            this.attSideDetail.put(side, 0);
        }
        for (final Integer side : this.defSideDetail.keySet()) {
            this.defSideDetail.put(side, 0);
        }
        int forceId = 0;
        Integer num = 0;
        for (final CampArmy temp : this.attCamp) {
            forceId = temp.forceId;
            num = this.attSideDetail.get(forceId);
            if (num != null) {
                this.attSideDetail.put(forceId, num + 1);
            }
            else {
                this.attSideDetail.put(forceId, 1);
            }
        }
        for (final CampArmy temp : this.defCamp) {
            forceId = temp.forceId;
            num = this.defSideDetail.get(forceId);
            if (num != null) {
                this.defSideDetail.put(forceId, num + 1);
            }
            else {
                this.defSideDetail.put(forceId, 1);
            }
        }
    }
    
    public BattleResultRound doBattle(final IDataGetter dataGetter, final long startBatTime) {
        try {
            return this.doBattle2(dataGetter, startBatTime);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.doBattle catch catch", e);
            dataGetter.getBattleService().dobattleExceptionBatId(this.battleId, e);
            return null;
        }
    }
    
    private BattleResultRound doBattle2(final IDataGetter dataGetter, final long startBatTime) {
        final long start = System.currentTimeMillis();
        synchronized (this.battleId) {
            Battle.timerLog.debug(LogUtil.formatThreadLog("Battle", "lock", 2, System.currentTimeMillis() - start, "battleType#" + this.battleType + "#battleId#" + this.battleId));
            final BattleResultRound battleResultRound = new BattleResultRound();
            final Builder builder = BuilderFactory.getInstance().getBuilder(this.battleType);
            int winSide = builder.isBattleEnd(dataGetter, this);
            if (winSide != 1) {
                if (this.getRoundNum().get() == 0 || this.getAttBaseInfo().getAllNum() < 3 || this.getDefBaseInfo().getAllNum() < 3) {
                    ErrorSceneLog.getInstance().appendErrorMsg("empty battle ended at first round").appendBattleId(this.battleId).append("this.roundNum", this.getRoundNum()).flush();
                }
                this.battleDoing = false;
                builder.endBattle(winSide, dataGetter, this);
                battleResultRound.ended = true;
                // monitorexit(this.battleId)
                return battleResultRound;
            }
            CampArmy firstAttCA = null;
            if (this.attList.size() > 0) {
                firstAttCA = this.attList.get(0).getCampArmy();
            }
            boolean attAutoStChoosed = false;
            if (firstAttCA != null && firstAttCA.playerId > 0 && !firstAttCA.isPhantom) {
                final PlayerInfo piAtt = this.inBattlePlayers.get(firstAttCA.playerId);
                attAutoStChoosed = (piAtt != null && piAtt.autoStrategy == 1);
            }
            CampArmy firstDefCA = null;
            if (this.defList.size() > 0) {
                firstDefCA = this.defList.get(0).getCampArmy();
            }
            boolean defAutoStChoosed = false;
            if (firstDefCA != null && firstDefCA.playerId > 0 && !firstDefCA.isPhantom) {
                final PlayerInfo piDef = this.inBattlePlayers.get(firstDefCA.playerId);
                defAutoStChoosed = (piDef != null && piDef.autoStrategy == 1);
            }
            if (this.lastRoundWinState == 1 && defAutoStChoosed) {
                if (this.defList.size() > 0) {
                    if (this.defList.get(0).getCampArmy().getTacticVal() > 0 && this.getSurround() == 0) {
                        this.defChoose = true;
                        this.defChooseTactic = true;
                    }
                    else {
                        final int attSt = this.attList.get(0).getStrategy();
                        final int[] defSts = this.defList.get(0).getCampArmy().getStrategies();
                        FightStragtegyCoe fsc = null;
                        if (this.defList.get(0).getSpecial() == 1) {
                            fsc = dataGetter.getFightStragtegyCoeCache().getDefLose(attSt, defSts);
                        }
                        else {
                            fsc = dataGetter.getFightStragtegyCoeCache().getDefWin(attSt, defSts);
                        }
                        final int defWinSt = (fsc != null) ? fsc.getDefStrategy() : defSts[0];
                        this.defList.get(0).setStrategy(defWinSt);
                    }
                }
            }
            else if (this.lastRoundWinState == 2 && attAutoStChoosed) {
                if (this.attList.size() > 0) {
                    if (this.attList.get(0).getCampArmy().getTacticVal() > 0) {
                        this.attChoose = true;
                        this.attChooseTactic = true;
                    }
                    else {
                        final int defSt = this.defList.get(0).getStrategy();
                        final int[] attSts = this.attList.get(0).getCampArmy().getStrategies();
                        FightStragtegyCoe fsc = null;
                        if (this.attList.get(0).getSpecial() == 1) {
                            fsc = dataGetter.getFightStragtegyCoeCache().getAttLose(defSt, attSts);
                        }
                        else {
                            fsc = dataGetter.getFightStragtegyCoeCache().getAttWin(defSt, attSts);
                        }
                        final int defWinSt = (fsc != null) ? fsc.getAttStrategy() : attSts[0];
                        this.attList.get(0).setStrategy(defWinSt);
                    }
                }
            }
            else if (this.lastRoundWinState == 3 && this.attList.size() > 0 && this.defList.size() > 0) {
                if (attAutoStChoosed) {
                    if (this.attList.get(0).getCampArmy().getTacticVal() > 0) {
                        this.attChoose = true;
                        this.attChooseTactic = true;
                    }
                    else {
                        final int[] attSts2 = this.attList.get(0).getCampArmy().getStrategies();
                        if (this.attList.get(0).getSpecial() == 1) {
                            final int defSt2 = this.defList.get(0).getStrategy();
                            final FightStragtegyCoe fsc = dataGetter.getFightStragtegyCoeCache().getAttLose(defSt2, attSts2);
                            final int loseSt = (fsc != null) ? fsc.getAttStrategy() : attSts2[0];
                            this.attList.get(0).setStrategy(loseSt);
                        }
                        else {
                            this.attList.get(0).setStrategy(attSts2[WebUtil.nextInt(attSts2.length)]);
                        }
                    }
                }
                if (defAutoStChoosed) {
                    if (this.defList.get(0).getCampArmy().getTacticVal() > 0 && this.getSurround() == 0) {
                        this.defChoose = true;
                        this.defChooseTactic = true;
                    }
                    else {
                        final int[] defSts2 = this.defList.get(0).getCampArmy().getStrategies();
                        if (this.defList.get(0).getSpecial() == 1) {
                            final int attSt2 = this.attList.get(0).getStrategy();
                            final FightStragtegyCoe fsc = dataGetter.getFightStragtegyCoeCache().getDefLose(attSt2, defSts2);
                            final int loseSt = (fsc != null) ? fsc.getDefStrategy() : defSts2[0];
                            this.defList.get(0).setStrategy(loseSt);
                        }
                        else {
                            this.defList.get(0).setStrategy(defSts2[WebUtil.nextInt(defSts2.length)]);
                        }
                    }
                }
            }
            if (this.attList.size() > 0 && this.defList.size() > 0) {
                if (this.lastRoundWinState == 2 && this.attList.get(0).getSpecial() == 1) {
                    final int[] attSts2 = this.attList.get(0).getCampArmy().getStrategies();
                    final int defSt2 = this.defList.get(0).getStrategy();
                    final FightStragtegyCoe fsc = dataGetter.getFightStragtegyCoeCache().getAttLose(defSt2, attSts2);
                    final int loseSt = (fsc != null) ? fsc.getAttStrategy() : attSts2[0];
                    this.attList.get(0).setStrategy(loseSt);
                }
                if (this.lastRoundWinState == 1 && this.defList.get(0).getSpecial() == 1) {
                    final int[] defSts2 = this.defList.get(0).getCampArmy().getStrategies();
                    final int attSt2 = this.attList.get(0).getStrategy();
                    final FightStragtegyCoe fsc = dataGetter.getFightStragtegyCoeCache().getDefLose(attSt2, defSts2);
                    final int loseSt = (fsc != null) ? fsc.getDefStrategy() : defSts2[0];
                    this.defList.get(0).setStrategy(loseSt);
                }
            }
            if (this.attList.size() > 0 && this.attList.get(0).getCampArmy().getTacticVal() > 0 && this.attList.get(0).getCampArmy().isPhantom) {
                this.attChoose = true;
                this.attChooseTactic = true;
            }
            if (this.defList.size() > 0 && this.defList.get(0).getCampArmy().getTacticVal() > 0 && this.defList.get(0).getCampArmy().isPhantom) {
                this.defChoose = true;
                this.defChooseTactic = true;
            }
            this.caculateCivilTrickBuff(dataGetter);
            if (this.attList.size() > 0 && this.defList.size() > 0) {
                this.setBattleRoundBuff(dataGetter, this.attList.get(0).getCampArmy(), this.defList.get(0).getCampArmy());
            }
            builder.checkErrorAndHandle(dataGetter, this);
            this.waitting = false;
            final RoundInfo roundInfo = new RoundInfo();
            this.doBattleRound(dataGetter, builder, roundInfo);
            winSide = builder.isBattleEnd(dataGetter, this);
            if (winSide != 1) {
                this.battleDoing = false;
                builder.endBattle(winSide, dataGetter, this);
                battleResultRound.ended = true;
                // monitorexit(this.battleId)
                return battleResultRound;
            }
            this.nextMinExeTime = System.currentTimeMillis() + roundInfo.nextMinExeTime;
            this.nextMaxExeTime = System.currentTimeMillis() + roundInfo.nextMaxExeTime;
            long time = System.currentTimeMillis();
            final String quickMode = Configuration.getProperty("gcld.battle.quick");
            if (quickMode.equals("1")) {
                final int interval = Integer.parseInt(Configuration.getProperty("gcld.battle.quick.interval"));
                time += interval;
            }
            else {
                time = this.nextMaxExeTime;
            }
            Battle.timerLog.info(LogUtil.formatThreadLog("Battle", "doBattle", 0, 0L, "battleId:" + this.getBattleId() + "|roundNum:" + this.getRoundNum() + "|exeTime:" + time));
            this.changeExeTime(time);
            // monitorexit(this.battleId)
            return battleResultRound;
        }
    }
    
    private void doBattleRound(final IDataGetter dataGetter, final Builder builder, final RoundInfo roundInfo) {
        this.getRoundNum().incrementAndGet();
        final CampArmy attCa = this.attList.get(0).getCampArmy();
        final CampArmy defCa = this.defList.get(0).getCampArmy();
        if ((this.battleType == 3 || this.battleType == 13) && attCa.forceId == defCa.forceId) {
            try {
                ErrorSceneLog.getInstance().appendErrorMsg("general from the same force fight each other").appendBattleId(this.battleId).append("this.roundNum", this.getRoundNum()).append("this.defBaseInfo.forceId", this.defBaseInfo.forceId).append("this.attBaseInfo.forceId", this.attBaseInfo.forceId).append("attCa.forceId", attCa.forceId).append("attCa.pgmVId", attCa.pgmVId).append("attCa.playerName", attCa.playerName).append("attCa.playerId", attCa.playerId).append("attCa.generalName", attCa.generalName).append("attCa.generalId", attCa.generalId).append("attCa.armyHpOrg", attCa.armyHpOrg).append("attCa.armyHp", attCa.armyHp).append("defCa.forceId", defCa.forceId).append("defCa.pgmVId", defCa.pgmVId).append("defCa.playerName", defCa.playerName).append("defCa.playerId", defCa.playerId).append("defCa.generalName", defCa.generalName).append("defCa.generalId", defCa.generalId).append("defCa.armyHpOrg", defCa.armyHpOrg).append("defCa.armyHp", defCa.armyHp).flush();
            }
            catch (Exception e) {
                ErrorSceneLog.getInstance().error("attCa.forceId == defCa.forceId catch exception.", e);
            }
        }
        this.attList.get(0).setChoose(true);
        this.defList.get(0).setChoose(true);
        Builder.getCurCampInfo(dataGetter, this, roundInfo);
        final boolean exeFight = builder.dealTacticStrategy(dataGetter, this, roundInfo);
        if (exeFight) {
            Builder.getCurBattleTroop(dataGetter, this, roundInfo);
            Builder.fight(roundInfo);
            builder.dealFight(dataGetter, this, roundInfo);
        }
        builder.roundRemoveCamp(this);
        for (int i = this.attList.size() - 1; i >= 0; --i) {
            final BattleArmy qBa = this.attList.get(i);
            for (final BattleArmy ba : roundInfo.attKilledList) {
                if (qBa.getPosition() == ba.getPosition()) {
                    this.attList.remove(i);
                    break;
                }
            }
        }
        for (int i = this.defList.size() - 1; i >= 0; --i) {
            final BattleArmy qBa = this.defList.get(i);
            for (final BattleArmy ba : roundInfo.defKilledList) {
                if (qBa.getPosition() == ba.getPosition()) {
                    this.defList.remove(i);
                    break;
                }
            }
        }
        builder.addAttNpc(dataGetter, this.battleNum.get(), this.attBaseInfo, this.attCamp, this.campNum, this);
        builder.addDefNpc(dataGetter, this, roundInfo);
        builder.systemSinglePK(dataGetter, this, roundInfo);
        final List<BattleArmy> attAddQlist = new ArrayList<BattleArmy>();
        Builder.onceAddQueues(attAddQlist, this.attList, this.attCamp, 1, this.attQNum, this.attBaseInfo, this.inBattlePlayers, roundInfo, this);
        Builder.getReportType2(roundInfo.battleMsg, attAddQlist, "att");
        final List<BattleArmy> defAddQlist = new ArrayList<BattleArmy>();
        Builder.onceAddQueues(defAddQlist, this.defList, this.defCamp, 0, this.defQNum, this.defBaseInfo, this.inBattlePlayers, roundInfo, this);
        Builder.getReportType2(roundInfo.battleMsg, defAddQlist, "def");
        builder.roundUpdateDB(dataGetter, this, roundInfo);
        Builder.getReportType19(roundInfo.battleMsg, roundInfo);
        if (builder.isBattleEnd(dataGetter, this) == 1) {
            Builder.getReportType27(roundInfo.battleMsg, roundInfo, this, dataGetter, -1L);
        }
        final int sn = this.ticket.incrementAndGet();
        final StringBuilder sb = new StringBuilder();
        sb.append(sn).append("|").append(this.battleId).append("#");
        if (roundInfo.win == 3 && this.defList.size() > 0 && this.defList.get(0).getCampArmy().getPlayerId() > 0) {
            this.waitting = true;
        }
        roundInfo.nextMaxExeTime = roundInfo.nextMinExeTime;
        boolean attAutoStChoosed = false;
        if (roundInfo.attCampArmy.playerId > 0 && !roundInfo.attCampArmy.isPhantom) {
            final PlayerInfo piAtt = this.inBattlePlayers.get(roundInfo.attCampArmy.playerId);
            attAutoStChoosed = (piAtt != null && piAtt.autoStrategy == 1);
        }
        boolean defAutoStChoosed = false;
        if (roundInfo.defCampArmy.playerId > 0 && !roundInfo.defCampArmy.isPhantom) {
            final PlayerInfo piDef = this.inBattlePlayers.get(roundInfo.defCampArmy.playerId);
            defAutoStChoosed = (piDef != null && piDef.autoStrategy == 1);
        }
        this.attChooseTactic = false;
        this.defChooseTactic = false;
        this.attChoose = false;
        this.defChoose = false;
        builder.updateChooseTime(this, roundInfo, attAutoStChoosed, defAutoStChoosed);
        Builder.getReportType26(sb, roundInfo);
        roundInfo.battleMsg.insert(0, sb);
        builder.saveTimePredicationBuffer(roundInfo);
        if (roundInfo.needPushReport13) {
            Builder.getReportType13(this, roundInfo.battleMsg);
        }
        this.SaveCurReport(dataGetter);
        Builder.getReportType100(dataGetter, this, roundInfo.battleMsg);
        Builder.sendMsgToAll(this, roundInfo.battleMsg);
        this.saveCurBattling(dataGetter, roundInfo);
        builder.saveCaculateDebugBuffer(roundInfo);
        this.newlyJoinSet.clear();
        this.lastRoundWinState = roundInfo.win;
        builder.setSurroundState(dataGetter, this);
        this.caculateAgainstInfo(dataGetter);
    }
    
    public void leave(final int playerId) {
        synchronized (this.battleId) {
            this.inSceneSet.remove(playerId);
        }
        // monitorexit(this.battleId)
    }
    
    public Set<CampArmy> quit(final int playerId, final Set<Integer> gIdSet, final IDataGetter dataGetter, final boolean isTuJin) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(this.battleType);
        synchronized (this.battleId) {
            final PlayerInfo pi = this.inBattlePlayers.get(playerId);
            LinkedList<CampArmy> camps = this.attCamp;
            List<BattleArmy> lists = this.attList;
            if (!pi.isAttSide()) {
                camps = this.defCamp;
                lists = this.defList;
            }
            final Set<CampArmy> cset = new HashSet<CampArmy>();
            for (final CampArmy ca : camps) {
                if (ca.getPlayerId() == playerId && ca.getArmyHp() >= 0 && ca.isInBattle() && !ca.isPhantom()) {
                    cset.add(ca);
                }
            }
            final Set<Integer> bset = new HashSet<Integer>();
            for (final BattleArmy bas : lists) {
                bset.add(bas.getCampArmy().getId());
            }
            final Set<CampArmy> quitGids = new HashSet<CampArmy>();
            for (final CampArmy ca2 : cset) {
                if (bset.contains(ca2.getId())) {
                    continue;
                }
                if (!gIdSet.contains(ca2.getGeneralId())) {
                    continue;
                }
                quitGids.add(ca2);
            }
            if (quitGids.size() <= 0) {
                // monitorexit(this.battleId)
                return quitGids;
            }
            final boolean isQuitAll = quitGids.size() == cset.size();
            builder.quit(this, dataGetter, playerId, pi, quitGids, isQuitAll, isTuJin);
            if (isQuitAll) {
                builder.inBattleInfo(playerId, false);
                if (pi.battleMode == 0) {
                    pi.battleMode = 1;
                }
            }
            // monitorexit(this.battleId)
            return quitGids;
        }
    }
    
    public boolean joinOneVsOneBackCA(final int playerId, final int battleSide, final CampArmy ca, final IDataGetter dataGetter, final int generalState) {
        synchronized (this.battleId) {
            if (!this.battleDoing) {
                // monitorexit(this.battleId)
                return false;
            }
            if (generalState != 19 && CityService.getCityFlag(this.getDefBaseInfo().getId()) != 0) {
                // monitorexit(this.battleId)
                return false;
            }
            LinkedList<CampArmy> campMap = null;
            BaseInfo baseInfo = null;
            if (ca.getForceId() != this.defBaseInfo.forceId) {
                campMap = this.attCamp;
                baseInfo = this.attBaseInfo;
            }
            else {
                campMap = this.defCamp;
                baseInfo = this.defBaseInfo;
            }
            campMap.add(ca);
            if (playerId > 0 && !ca.isPhantom) {
                NewBattleManager.getInstance().joinBattle(this, playerId, ca.getGeneralId());
            }
            BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#add:1Vs1Loss#side:" + ((battleSide == 1) ? "att" : "def") + "#playerId:" + ca.getPlayerId() + ":" + ca.isPhantom + "#general:" + ca.getGeneralId() + "#" + ((battleSide == 1) ? "att" : "def") + "Size:" + campMap.size());
            baseInfo.setAllNum(baseInfo.getAllNum() + ca.getArmyHp());
            baseInfo.setNum(baseInfo.getNum() + ca.getArmyHp());
            this.newlyJoinSet.add(ca);
            if (ca.getPlayerId() > 0 && !ca.isPhantom) {
                dataGetter.getPlayerGeneralMilitaryDao().updateState(ca.getPgmVId(), generalState);
            }
            if (ca.isYellowTrubans) {
                dataGetter.getYellowTurbansDao().updateState(ca.getPgmVId(), 3);
            }
            // monitorexit(this.battleId)
            return true;
        }
    }
    
    public boolean joinCityNpc(final WorldCity wc, int battleSide, final IDataGetter dataGetter, final int forceId) {
        synchronized (this.battleId) {
            if (!this.isBattleDoing()) {
                // monitorexit(this.battleId)
                return false;
            }
            battleSide = 1;
            if (this.getDefBaseInfo().getForceId() == forceId) {
                battleSide = 0;
            }
            final LinkedList<CampArmy> campChange = new LinkedList<CampArmy>();
            LinkedList<CampArmy> campMap = null;
            BaseInfo baseInfo = null;
            if (battleSide == 1) {
                campMap = this.attCamp;
                baseInfo = this.attBaseInfo;
            }
            else {
                campMap = this.defCamp;
                baseInfo = this.defBaseInfo;
            }
            final int distance = wc.getDistance(forceId);
            final WorldGuardInfo wgi = (WorldGuardInfo)dataGetter.getWorldGuardInfoCache().get((Object)distance);
            final WorldGuard wg = dataGetter.getWorldGuardCache().getByForceIdDegree(forceId, wgi.getNpcDegreeBase());
            if (wg == null) {
                // monitorexit(this.battleId)
                return false;
            }
            CampArmy campArmy = null;
            int num = 0;
            int id = 0;
            for (int i = 0; i < wgi.getNpcNum(); ++i) {
                id = this.campNum.getAndIncrement();
                final Player player = new Player();
                player.setPlayerId(-1);
                player.setPlayerName(wg.getName());
                player.setForceId(forceId);
                player.setPlayerLv(wg.getLv());
                campArmy = Builder.copyArmyFromCach(player, wg.getArmyId(), dataGetter, id, this.terrainVal, wg.getLv());
                if (campArmy != null) {
                    num += campArmy.getArmyHpOrg();
                    campMap.add(campArmy);
                    BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#add:ciytNpc#side:" + ((battleSide == 1) ? "att" : "def") + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#" + ((battleSide == 1) ? "att" : "def") + "Size:" + campMap.size());
                    campChange.add(campArmy);
                }
            }
            baseInfo.setNum(baseInfo.getNum() + num);
            baseInfo.setAllNum(baseInfo.getAllNum() + num);
            for (final CampArmy ca : campChange) {
                this.newlyJoinSet.add(ca);
            }
            // monitorexit(this.battleId)
            return true;
        }
    }
    
    public JoinTeamBattleInfo joinTeam(final Player player, final Map<Integer, TeamMember> tmMap, final IDataGetter dataGetter, final Team team, final int teamBatType) {
        final JoinTeamBattleInfo jtbi = new JoinTeamBattleInfo();
        synchronized (this.battleId) {
            if (!this.battleDoing) {
                jtbi.error = LocalMessages.BATTLE_END_INFO;
                // monitorexit(this.battleId)
                return jtbi;
            }
            final PlayerInfo pi = this.inBattlePlayers.get(player.getPlayerId());
            if (pi == null) {
                jtbi.error = LocalMessages.T_TEAM_CONDITION_10038;
                // monitorexit(this.battleId)
                return jtbi;
            }
            LinkedList<CampArmy> selfSideCampMap = null;
            LinkedList<CampArmy> otherSideCampMap = null;
            List<BattleArmy> selfList = null;
            BaseInfo selfSideBaseInfo = null;
            String reportType = null;
            int battleSide = 0;
            if (pi.isAttSide) {
                selfSideCampMap = this.attCamp;
                otherSideCampMap = this.defCamp;
                selfSideBaseInfo = this.attBaseInfo;
                selfList = this.attList;
                reportType = "att";
                battleSide = 1;
            }
            else {
                selfSideCampMap = this.defCamp;
                otherSideCampMap = this.attCamp;
                selfSideBaseInfo = this.defBaseInfo;
                selfList = this.defList;
                reportType = "def";
            }
            boolean isInBat = false;
            for (final CampArmy ca : selfSideCampMap) {
                if (ca.getPlayerId() == player.getPlayerId() && !ca.isPhantom) {
                    isInBat = true;
                    break;
                }
            }
            if (!isInBat) {
                jtbi.error = LocalMessages.T_TEAM_CONDITION_10038;
                // monitorexit(this.battleId)
                return jtbi;
            }
            final boolean alreadyIn = true;
            final LinkedList<CampArmy> campJoin = new LinkedList<CampArmy>();
            final LinkedList<CampArmy> campCreate = new LinkedList<CampArmy>();
            Player tempPlayer = null;
            boolean canPK = true;
            int qFull = 0;
            String pkStr = LocalMessages.STR_YOUDI;
            if (battleSide == 0) {
                pkStr = LocalMessages.STR_CHUJI;
            }
            final boolean isNTYellowTurbans = dataGetter.getBattleService().isNTYellowTurbansXiangYangDoing(this.getDefBaseInfo().getId());
            for (final Integer pId : tmMap.keySet()) {
                try {
                    final TeamMember tm = tmMap.get(pId);
                    tempPlayer = dataGetter.getPlayerDao().read(tm.getPlayerId());
                    final PlayerWorld pw = dataGetter.getPlayerWorldDao().read(tm.getPlayerId());
                    final Set<Integer> attedSet = new HashSet<Integer>();
                    int aId = 0;
                    if (pw.getAttedId() != null) {
                        final String[] ids = pw.getAttedId().split(",");
                        String[] array;
                        for (int length = (array = ids).length, i = 0; i < length; ++i) {
                            final String str = array[i];
                            aId = Integer.valueOf(str);
                            attedSet.add(aId);
                        }
                    }
                    if (!attedSet.contains(this.defBaseInfo.getId())) {
                        continue;
                    }
                    if (this.inBattlePlayers.get(tempPlayer.getPlayerId()) == null) {
                        final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(tempPlayer.getPlayerId(), 43);
                        int autoStrategy = 0;
                        if (zdzsTech > 0) {
                            final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(tempPlayer.getPlayerId());
                            autoStrategy = pba.getAutoStrategy();
                        }
                        else {
                            autoStrategy = -1;
                        }
                        this.inBattlePlayers.put(tempPlayer.getPlayerId(), new PlayerInfo(tempPlayer.getPlayerId(), pi.isAttSide, autoStrategy));
                    }
                    final Map<Integer, PlayerGeneralMilitary> pgmMap = dataGetter.getPlayerGeneralMilitaryDao().getMilitaryMap(tempPlayer.getPlayerId());
                    final List<PlayerGeneralMilitary> pgmList = new ArrayList<PlayerGeneralMilitary>();
                    final List<GeneralInfo> giList = tm.getGeneralInfo();
                    boolean pk = false;
                    for (final GeneralInfo gi : giList) {
                        pk = false;
                        final PlayerGeneralMilitary pgm = pgmMap.get(gi.getGeneralId());
                        final int maxHp = dataGetter.getBattleDataCache().getMaxHp(pgm);
                        if (pgm.getForces() * 1.0 / maxHp < 0.05) {
                            continue;
                        }
                        if (pgm.getState() > 1) {
                            continue;
                        }
                        pgmList.add(pgm);
                        if (teamBatType == 1) {
                            if (qFull < 1 && Builder.canAddBattleArmyList(selfList)) {
                                ++qFull;
                            }
                            else {
                                qFull = 2;
                                if (canPK && this.canOneVsOne(pgm, selfSideCampMap, otherSideCampMap, isNTYellowTurbans)) {
                                    pk = true;
                                }
                                else {
                                    canPK = false;
                                }
                            }
                        }
                        if (pk) {
                            if (isNTYellowTurbans) {
                                this.createOneToOneBattleForNTYellowTurbans(pgm, dataGetter, tempPlayer, this, battleSide, new TeamInfo(team.getTeamName(), team.getInspireEffect()), campCreate);
                            }
                            else {
                                this.createOneToOneBattle(pgm, dataGetter, tempPlayer, this, battleSide, new TeamInfo(team.getTeamName(), team.getInspireEffect()), campCreate);
                            }
                        }
                        else {
                            this.initJoinPlayer2(pgm, dataGetter, tempPlayer, this, selfSideBaseInfo, selfSideCampMap, campJoin, battleSide, new TeamInfo(team.getTeamName(), team.getInspireEffect()));
                        }
                    }
                    this.pushTeamGeneralBat(tempPlayer.getPlayerId(), pgmList);
                    for (final PlayerGeneralMilitary pgm2 : pgmList) {
                        dataGetter.getCityService().sendAttMoveInfo(pgm2.getPlayerId(), pgm2.getGeneralId(), pgm2.getLocationId(), this.getDefBaseInfo().getId(), pgm2.getForceId(), "", pgm2.getForces(), true);
                        dataGetter.getBattleService().updateHuizhanNationForce(this.getDefBaseInfo().getId(), pgm2.getForceId(), pgm2.getForces());
                        dataGetter.getBattleService().updateHuizhanPlayerForce(this.getDefBaseInfo().getId(), pgm2.getPlayerId(), pgm2.getForces());
                    }
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error("teamBattle Battle joinTeam playerId:" + pId + e.getMessage());
                }
            }
            for (final CampArmy ca2 : campJoin) {
                this.newlyJoinSet.add(ca2);
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("batSide", battleSide);
            doc.startArray("reports");
            StringBuilder battleMsg = null;
            int count = 1;
            if (teamBatType == 1) {
                for (final CampArmy ca3 : campCreate) {
                    battleMsg = new StringBuilder();
                    battleMsg.append(MessageFormatter.format(LocalMessages.TEAM_BATTLE_INFO, new Object[] { ca3.getTeamGenreal(), ca3.getGeneralName(), pkStr, LocalMessages.STR_SUCC }));
                    doc.startObject();
                    doc.createElement("id", (count++));
                    doc.createElement("msg", battleMsg.toString());
                    doc.endObject();
                }
                for (final CampArmy ca3 : campJoin) {
                    battleMsg = new StringBuilder();
                    battleMsg.append(MessageFormatter.format(LocalMessages.TEAM_BATTLE_INFO2, new Object[] { ca3.getTeamGenreal(), ca3.getGeneralName() }));
                    doc.startObject();
                    doc.createElement("id", (count++));
                    doc.createElement("msg", battleMsg.toString());
                    doc.endObject();
                }
            }
            else {
                for (final CampArmy ca3 : campJoin) {
                    battleMsg = new StringBuilder();
                    battleMsg.append(MessageFormatter.format(LocalMessages.TEAM_BATTLE_INFO2, new Object[] { ca3.getTeamGenreal(), ca3.getGeneralName() }));
                    doc.startObject();
                    doc.createElement("id", (count++));
                    doc.createElement("msg", battleMsg.toString());
                    doc.endObject();
                }
            }
            doc.endArray();
            doc.endObject();
            if (campJoin.size() > 0 || campCreate.size() > 0) {
                for (final Integer playerId : this.inSceneSet) {
                    if (NewBattleManager.getInstance().isWatchBattle(playerId, this.getBattleId())) {
                        Players.push(playerId, PushCommand.PUSH_TEAM_START_BAT, doc.toByte());
                    }
                }
            }
            this.pushJoinInfo(campJoin, reportType, alreadyIn, dataGetter, player.getPlayerId());
            jtbi.result = true;
            jtbi.battleSide = battleSide;
            jtbi.campJoin = campJoin;
            jtbi.campCreate = campCreate;
        }
        // monitorexit(this.battleId)
        return jtbi;
    }
    
    private void createOneToOneBattle(final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final Player player, final Battle bat, final int battleSide, final TeamInfo teamInfo, final LinkedList<CampArmy> campChange) {
        final CampArmy[] cas = new CampArmy[2];
        int OneToOneBattleType = 0;
        switch (bat.getBattleType()) {
            case 3: {
                OneToOneBattleType = 13;
                break;
            }
            case 14: {
                OneToOneBattleType = 15;
                break;
            }
        }
        final Builder builder = BuilderFactory.getInstance().getBuilder(OneToOneBattleType);
        final CampArmy pgmCa = CityBuilder.copyCampFromPlayerTable(player, pgm, dataGetter, builder.getGeneralState(), bat, battleSide);
        if (pgmCa == null) {
            return;
        }
        pgmCa.teamGenreal = teamInfo.teamName;
        pgmCa.teamEffect = teamInfo.teamEffect;
        campChange.add(pgmCa);
        int pkType = 1;
        if (battleSide == 0) {
            pkType = 2;
            cas[1] = pgmCa;
            for (final CampArmy ca : bat.getAttCamp()) {
                if (!ca.onQueues) {
                    cas[0] = ca;
                    bat.getAttCamp().remove(ca);
                    if (ca.getPlayerId() > 0 && !ca.isPhantom()) {
                        NewBattleManager.getInstance().quitBattle(this, ca.getPlayerId(), ca.getGeneralId());
                    }
                    bat.attBaseInfo.setAllNum(bat.attBaseInfo.getAllNum() - cas[0].armyHp);
                    bat.attBaseInfo.setNum(bat.attBaseInfo.getNum() - cas[0].armyHp);
                    break;
                }
            }
        }
        else {
            cas[0] = pgmCa;
            for (final CampArmy ca : bat.getDefCamp()) {
                if (!ca.onQueues) {
                    cas[1] = ca;
                    bat.getDefCamp().remove(ca);
                    if (ca.getPlayerId() > 0 && !ca.isPhantom()) {
                        NewBattleManager.getInstance().quitBattle(this, ca.getPlayerId(), ca.getGeneralId());
                    }
                    bat.defBaseInfo.setAllNum(bat.defBaseInfo.getAllNum() - cas[1].armyHp);
                    bat.defBaseInfo.setNum(bat.defBaseInfo.getNum() - cas[1].armyHp);
                    break;
                }
            }
        }
        dataGetter.getBattleService().createOneToOneBattle(player.getPlayerId(), cas, bat, pkType, 0);
    }
    
    private void createOneToOneBattleForNTYellowTurbans(final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final Player player, final Battle bat, final int battleSide, final TeamInfo teamInfo, final LinkedList<CampArmy> campChange) {
        final CampArmy[] cas = new CampArmy[2];
        int OneToOneBattleType = 0;
        switch (bat.getBattleType()) {
            case 3: {
                OneToOneBattleType = 13;
                break;
            }
            case 14: {
                OneToOneBattleType = 15;
                break;
            }
        }
        final Builder builder = BuilderFactory.getInstance().getBuilder(OneToOneBattleType);
        final CampArmy pgmCa = CityBuilder.copyCampFromPlayerTable(player, pgm, dataGetter, builder.getGeneralState(), bat, battleSide);
        if (pgmCa == null) {
            return;
        }
        pgmCa.teamGenreal = teamInfo.teamName;
        pgmCa.teamEffect = teamInfo.teamEffect;
        campChange.add(pgmCa);
        cas[0] = pgmCa;
        cas[1] = null;
        for (final CampArmy ca : bat.getAttCamp()) {
            if (!ca.onQueues && ca.getForceId() != pgmCa.getForceId()) {
                cas[1] = ca;
                bat.getAttCamp().remove(ca);
                bat.getAttBaseInfo().setNum(bat.getAttBaseInfo().getNum() - ca.getArmyHp());
                bat.getAttBaseInfo().setAllNum(bat.getAttBaseInfo().getAllNum() - ca.getArmyHp());
                break;
            }
        }
        if (cas[1] == null) {
            return;
        }
        dataGetter.getBattleService().createOneToOneBattle(player.getPlayerId(), cas, bat, 2, 0);
    }
    
    private void initJoinPlayer2(final PlayerGeneralMilitary pgm, final IDataGetter dataGetter, final Player player, final Battle bat, final BaseInfo baseInfo, final LinkedList<CampArmy> campMap, final LinkedList<CampArmy> campChange, final int battleSide, final TeamInfo teamInfo) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(bat.getBattleType());
        CampArmy campArmy = null;
        int num = 0;
        campArmy = CityBuilder.copyCampFromPlayerTable(player, pgm, dataGetter, builder.getGeneralState(), bat, battleSide);
        if (campArmy == null) {
            return;
        }
        num += campArmy.getArmyHpOrg();
        campArmy.setTeamGenreal(teamInfo.teamName);
        campArmy.setTeamEffect(teamInfo.teamEffect);
        campMap.add(campArmy);
        BattleSceneLog.getInstance().info("#batId:" + bat.getBattleId() + "_" + bat.getStartTime() + "#add:join#side:" + ((battleSide == 1) ? "att" : "def") + "#playerId:" + campArmy.getPlayerId() + ":" + campArmy.isPhantom + "#general:" + campArmy.getGeneralId() + "#" + ((battleSide == 1) ? "att" : "def") + "Size:" + campMap.size());
        campChange.add(campArmy);
        baseInfo.setNum(baseInfo.getNum() + num);
        baseInfo.setAllNum(baseInfo.getAllNum() + num);
    }
    
    private boolean canOneVsOne(final PlayerGeneralMilitary pgm, final LinkedList<CampArmy> selfSideCampMap, final LinkedList<CampArmy> otherSideCampMap, final boolean isNTYellowTurbans) {
        boolean flag = false;
        if (isNTYellowTurbans) {
            for (final CampArmy ca : selfSideCampMap) {
                if (!ca.onQueues && pgm.getForceId() != ca.getForceId()) {
                    flag = true;
                    break;
                }
            }
        }
        else {
            for (final CampArmy ca : otherSideCampMap) {
                if (!ca.onQueues) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
    
    private void pushTeamGeneralBat(final int playerId, final List<PlayerGeneralMilitary> pgmList) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("batGenerals");
        for (final PlayerGeneralMilitary pgm : pgmList) {
            doc.startObject();
            doc.createElement("generalId", pgm.getGeneralId());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_TEAM_GENERAL_BAT, doc.toByte());
    }
    
    public boolean joinZhengZhaoLing(final IDataGetter dataGetter, final int playerId, final List<PlayerGeneralMilitary> pgmList, final double teamEffect, final int attEffect, final int defEffect, final String playerName) {
        final Builder builder = BuilderFactory.getInstance().getBuilder(this.battleType);
        synchronized (this.battleId) {
            if (!this.battleDoing) {
                // monitorexit(this.battleId)
                return false;
            }
            if (this.battleType <= 0) {
                BattleSceneLog.getInstance().error("Battle join battleType, \u6218\u6597\u8fd8\u6ca1\u521d\u59cb\u5316\u5b8c\u6210\u5c31\u60f3\u52a0\u5165battleId" + this.battleId);
                // monitorexit(this.battleId)
                return false;
            }
            if (System.currentTimeMillis() > this.nextMaxExeTime + 180000L) {
                // monitorexit(this.battleId)
                return false;
            }
            final boolean isNTYellowTurbans = dataGetter.getBattleService().isNTYellowTurbansXiangYangDoing(this.getDefBaseInfo().getId());
            int battleSide = 1;
            final Player player = dataGetter.getPlayerDao().read(playerId);
            if (player != null) {
                battleSide = builder.getBattleSide(dataGetter, player, this.getDefBaseInfo().id);
            }
            builder.inBattleInfo(playerId, true);
            builder.addInSceneSet(this, playerId);
            final LinkedList<CampArmy> campChange = new LinkedList<CampArmy>();
            LinkedList<CampArmy> campMap = null;
            LinkedList<CampArmy> counterCampMap = null;
            BaseInfo baseInfo = null;
            BaseInfo counterBaseInfo = null;
            String reportType = null;
            boolean alreadyIn = false;
            if ((this.battleType == 3 || this.battleType == 14) && this.inSceneSet.contains(playerId)) {
                alreadyIn = true;
            }
            final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(playerId, 43);
            int autoStrategy = 0;
            if (zdzsTech > 0) {
                final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(playerId);
                autoStrategy = pba.getAutoStrategy();
            }
            else {
                autoStrategy = -1;
            }
            if (battleSide == 1) {
                campMap = this.attCamp;
                counterCampMap = this.defCamp;
                baseInfo = this.attBaseInfo;
                counterBaseInfo = this.defBaseInfo;
                reportType = "att";
                if (this.inBattlePlayers.get(playerId) == null) {
                    this.inBattlePlayers.put(playerId, new PlayerInfo(playerId, true, autoStrategy));
                }
            }
            else {
                campMap = this.defCamp;
                counterCampMap = this.attCamp;
                baseInfo = this.defBaseInfo;
                counterBaseInfo = this.attBaseInfo;
                reportType = "def";
                if (this.inBattlePlayers.get(playerId) == null) {
                    this.inBattlePlayers.put(playerId, new PlayerInfo(playerId, false, autoStrategy));
                }
            }
            builder.initZhengZhaoLingJoinPlayer(dataGetter, this, player, battleSide, pgmList, baseInfo, campMap, campChange, teamEffect, attEffect, defEffect, playerName);
            int force = 0;
            for (final CampArmy ca : campChange) {
                force += ca.getArmyHp();
            }
            dataGetter.getBattleService().updateHuizhanPlayerForce(this.getDefBaseInfo().getId(), playerId, force);
            dataGetter.getBattleService().updateHuizhanNationForce(this.getDefBaseInfo().getId(), player.getForceId(), force);
            Set<CampArmy> singlePkSet = new HashSet<CampArmy>();
            if (isNTYellowTurbans) {
                singlePkSet = this.sysSinglePkForNTYellowTurbans(dataGetter, campChange, campMap, baseInfo);
            }
            else {
                singlePkSet = this.sysSinglePk(dataGetter, campChange, campMap, counterCampMap, baseInfo, counterBaseInfo);
            }
            for (final CampArmy ca2 : campChange) {
                if (singlePkSet != null && singlePkSet.contains(ca2)) {
                    continue;
                }
                this.newlyJoinSet.add(ca2);
            }
            this.pushJoinInfo(campChange, reportType, alreadyIn, dataGetter, playerId);
            builder.sendTaskMessage(playerId, this.defBaseInfo.getId(), dataGetter);
        }
        // monitorexit(this.battleId)
        return true;
    }
    
    private Set<CampArmy> sysSinglePk(final IDataGetter dataGetter, final LinkedList<CampArmy> campChange, final LinkedList<CampArmy> myCamp, final LinkedList<CampArmy> counterCamp, final BaseInfo myBaseInfo, final BaseInfo counterBaseInfo) {
        try {
            int battleSide = 1;
            if (myCamp == this.defCamp) {
                battleSide = 0;
            }
            final Set<CampArmy> singlePkSet = new HashSet<CampArmy>();
            for (final CampArmy ca : campChange) {
                final CampArmy[] cas = new CampArmy[2];
                final int counterChooseId = counterCamp.size() - 1;
                cas[0] = ca;
                cas[1] = counterCamp.get(counterChooseId);
                if (!cas[0].onQueues) {
                    if (cas[1].onQueues) {
                        continue;
                    }
                    myCamp.remove(cas[0]);
                    counterCamp.remove(cas[1]);
                    myBaseInfo.setAllNum(myBaseInfo.getAllNum() - cas[0].armyHp);
                    myBaseInfo.setNum(myBaseInfo.getNum() - cas[0].armyHp);
                    counterBaseInfo.setAllNum(counterBaseInfo.getAllNum() - cas[1].armyHp);
                    counterBaseInfo.setNum(counterBaseInfo.getNum() - cas[1].armyHp);
                    if (battleSide == 0) {
                        final CampArmy temp = cas[0];
                        cas[0] = cas[1];
                        cas[1] = temp;
                    }
                    dataGetter.getBattleService().createOneToOneBattle(-1, cas, this, 3, 0);
                    singlePkSet.add(ca);
                }
            }
            return singlePkSet;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.sysSinglePk catch Exception", e);
            return null;
        }
    }
    
    private Set<CampArmy> sysSinglePkForNTYellowTurbans(final IDataGetter dataGetter, final LinkedList<CampArmy> campChange, final LinkedList<CampArmy> myCamp, final BaseInfo myBaseInfo) {
        try {
            final Set<CampArmy> singlePkSet = new HashSet<CampArmy>();
            for (final CampArmy ca : campChange) {
                final int forceId = ca.getForceId();
                final CampArmy[] cas = { ca, null };
                for (int i = 2; i < myCamp.size(); ++i) {
                    final CampArmy tempCa = this.attCamp.get(i);
                    if (tempCa.getForceId() != forceId && !tempCa.onQueues) {
                        cas[1] = tempCa;
                        break;
                    }
                }
                if (!cas[0].onQueues) {
                    if (cas[1] == null) {
                        continue;
                    }
                    myCamp.remove(cas[0]);
                    myCamp.remove(cas[1]);
                    myBaseInfo.setAllNum(myBaseInfo.getAllNum() - cas[0].armyHp - cas[1].armyHp);
                    myBaseInfo.setNum(myBaseInfo.getNum() - cas[0].armyHp - cas[1].armyHp);
                    dataGetter.getBattleService().createOneToOneBattle(-1, cas, this, 3, 0);
                    singlePkSet.add(ca);
                }
            }
            return singlePkSet;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.sysSinglePk catch Exception", e);
            return null;
        }
    }
    
    public boolean join(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter) throws Exception {
        final Builder builder = BuilderFactory.getInstance().getBuilder(this.battleType);
        synchronized (this.battleId) {
            if (!this.battleDoing) {
                // monitorexit(this.battleId)
                return false;
            }
            if (this.battleType <= 0) {
                BattleSceneLog.getInstance().error("Battle join battleType, \u6218\u6597\u8fd8\u6ca1\u521d\u59cb\u5316\u5b8c\u6210\u5c31\u60f3\u52a0\u5165\u3002battleId" + this.battleId);
                // monitorexit(this.battleId)
                return false;
            }
            if (System.currentTimeMillis() > this.nextMaxExeTime + 180000L) {
                throw new Exception("battle exception : battle stop");
            }
            final int playerId = player.getPlayerId();
            builder.inBattleInfo(playerId, true);
            builder.addInSceneSet(this, playerId);
            final int battleSide = builder.getBattleSide(dataGetter, player, this.getDefBaseInfo().id);
            final LinkedList<CampArmy> campChange = new LinkedList<CampArmy>();
            LinkedList<CampArmy> campMap = null;
            BaseInfo baseInfo = null;
            String reportType = null;
            boolean alreadyIn = false;
            if ((this.battleType == 3 || this.battleType == 14 || this.battleType == 18) && this.inSceneSet.contains(playerId)) {
                alreadyIn = true;
            }
            final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(playerId, 43);
            int autoStrategy = 0;
            if (zdzsTech > 0) {
                final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(playerId);
                autoStrategy = pba.getAutoStrategy();
            }
            else {
                autoStrategy = -1;
            }
            if (battleSide == 1) {
                campMap = this.attCamp;
                baseInfo = this.attBaseInfo;
                reportType = "att";
                if (this.inBattlePlayers.get(playerId) == null) {
                    this.inBattlePlayers.put(playerId, new PlayerInfo(playerId, true, autoStrategy));
                }
            }
            else {
                campMap = this.defCamp;
                baseInfo = this.defBaseInfo;
                reportType = "def";
                if (this.inBattlePlayers.get(playerId) == null) {
                    this.inBattlePlayers.put(playerId, new PlayerInfo(playerId, false, autoStrategy));
                }
            }
            builder.initJoinPlayer(pgmList, dataGetter, player, this, baseInfo, campMap, campChange, battleSide, null);
            for (final CampArmy ca : campChange) {
                this.newlyJoinSet.add(ca);
            }
            this.pushJoinInfo(campChange, reportType, alreadyIn, dataGetter, playerId);
            builder.sendTaskMessage(playerId, this.defBaseInfo.getId(), dataGetter);
        }
        // monitorexit(this.battleId)
        return true;
    }
    
    private void pushJoinInfo(final LinkedList<CampArmy> campChange, final String reportType, final boolean alreadyIn, final IDataGetter dataGetter, final int playerId) {
        final StringBuilder battleMsgPart1 = new StringBuilder();
        final int sn = this.ticket.incrementAndGet();
        battleMsgPart1.append(sn).append("|").append(this.battleId).append("#");
        final List<BattleArmy> attAddQlist = new ArrayList<BattleArmy>();
        Builder.onceAddQueues(attAddQlist, this.attList, this.attCamp, 1, this.attQNum, this.attBaseInfo, this.inBattlePlayers, null, this);
        Builder.getReportType2(battleMsgPart1, attAddQlist, "att");
        final List<BattleArmy> defAddQlist = new ArrayList<BattleArmy>();
        Builder.onceAddQueues(defAddQlist, this.defList, this.defCamp, 0, this.defQNum, this.defBaseInfo, this.inBattlePlayers, null, this);
        Builder.getReportType2(battleMsgPart1, defAddQlist, "def");
        Builder.getReportType13(this, battleMsgPart1);
        this.SaveCurReport(dataGetter);
        if (alreadyIn) {
            Builder.sendMsgToAll(this, battleMsgPart1);
        }
        else {
            Builder.sendMsgToAllExcludeOne(this, battleMsgPart1, playerId);
            if (this.curBattling.indexOf("#26") == 0 && this.curBattling.indexOf("#27") > 0) {
                this.curBattling.replace(this.curBattling.indexOf("#26") + 4, this.curBattling.indexOf("#27"), new StringBuilder(String.valueOf(this.nextMaxExeTime - System.currentTimeMillis())).toString());
            }
            else if (this.curBattling.indexOf("#26") > 0 && this.curBattling.indexOf("#27") > 0) {
                this.curBattling.replace(this.curBattling.indexOf("#26") + 4, this.curBattling.indexOf("#27"), new StringBuilder(String.valueOf(this.nextMaxExeTime - System.currentTimeMillis())).toString());
            }
            final StringBuilder msgToCurrentPlayer = new StringBuilder();
            msgToCurrentPlayer.append(sn).append("|").append(this.battleId).append("#").append(this.curReport).append(this.curBattling);
            final String playerName = dataGetter.getPlayerDao().read(playerId).getPlayerName();
            final String SingleHeadInfo = " battleId " + this.battleId + " \u53d1\u9001\u7ed9\u4e2a\u4eba:" + playerName + "  battleMsg: ";
            Builder.sendMsgToSingle(this, msgToCurrentPlayer, playerId, SingleHeadInfo, null);
        }
    }
    
    public boolean joinCampArmy(final IDataGetter dataGetter, int battleSide, final CampArmy CaPhantom) {
        try {
            synchronized (this.battleId) {
                if (!this.battleDoing) {
                    // monitorexit(this.battleId)
                    return false;
                }
                final int defForceId = this.defBaseInfo.forceId;
                if (CaPhantom.forceId == defForceId) {
                    if (battleSide == 1) {
                        try {
                            ErrorSceneLog.getInstance().appendErrorMsg("joinPhantom forceId error").appendBattleId(this.battleId).append("this.roundNum", this.getRoundNum()).append("battleSide", battleSide).append("defForceId", defForceId).append("CaPhantom.forceId", CaPhantom.forceId).append("CaPhantom.pgmVId", CaPhantom.pgmVId).append("CaPhantom.playerName", CaPhantom.playerName).append("CaPhantom.playerId", CaPhantom.playerId).append("CaPhantom.generalName", CaPhantom.generalName).append("CaPhantom.generalId", CaPhantom.generalId).append("CaPhantom.armyHpOrg", CaPhantom.armyHpOrg).append("CaPhantom.armyHp", CaPhantom.armyHp).flush();
                        }
                        catch (Exception e) {
                            ErrorSceneLog.getInstance().error("Battle joinPhantom " + e);
                        }
                        battleSide = 0;
                    }
                }
                else if (battleSide == 0) {
                    try {
                        ErrorSceneLog.getInstance().appendErrorMsg("joinPhantom forceId error").appendBattleId(this.battleId).append("this.battleNum", this.battleNum).append("battleSide", battleSide).append("defForceId", defForceId).append("CaPhantom.forceId", CaPhantom.forceId).append("CaPhantom.pgmVId", CaPhantom.pgmVId).append("CaPhantom.playerName", CaPhantom.playerName).append("CaPhantom.playerId", CaPhantom.playerId).append("CaPhantom.generalName", CaPhantom.generalName).append("CaPhantom.generalId", CaPhantom.generalId).append("CaPhantom.armyHpOrg", CaPhantom.armyHpOrg).append("CaPhantom.armyHp", CaPhantom.armyHp).flush();
                    }
                    catch (Exception e) {
                        ErrorSceneLog.getInstance().error("Battle joinPhantom 2" + e);
                    }
                    battleSide = 1;
                }
                final LinkedList<CampArmy> campChange = new LinkedList<CampArmy>();
                LinkedList<CampArmy> campMap = null;
                BaseInfo baseInfo = null;
                if (battleSide == 1) {
                    campMap = this.attCamp;
                    baseInfo = this.attBaseInfo;
                    this.worldSceneLog.appendLogMsg("CaPhantom join to attCamp").newLine().Indent();
                }
                else {
                    campMap = this.defCamp;
                    baseInfo = this.defBaseInfo;
                    this.worldSceneLog.appendLogMsg("CaPhantom join to attCamp").newLine().Indent();
                }
                this.worldSceneLog.appendPlayerName(CaPhantom.getPlayerName()).appendGeneralName(CaPhantom.getGeneralName()).appendPlayerId(CaPhantom.getPlayerId()).append("phantom vId", CaPhantom.getPgmVId()).newLine();
                campMap.add(CaPhantom);
                BattleSceneLog.getInstance().info("#batId:" + this.getBattleId() + "_" + this.getStartTime() + "#add:phantom#side:" + ((battleSide == 1) ? "att" : "def") + "#playerId:" + CaPhantom.getPlayerId() + ":" + CaPhantom.isPhantom + "#general:" + CaPhantom.getGeneralId() + "#" + ((battleSide == 1) ? "att" : "def") + "Size:" + campMap.size());
                campChange.add(CaPhantom);
                final PlayerInfo piPhantom = this.inBattlePlayers.get(CaPhantom.playerId);
                if (CaPhantom.playerId > 0 && piPhantom == null) {
                    final int zdzsTech = dataGetter.getTechEffectCache().getTechEffect(CaPhantom.playerId, 43);
                    int autoStrategy = 0;
                    if (zdzsTech > 0) {
                        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(CaPhantom.playerId);
                        autoStrategy = pba.getAutoStrategy();
                    }
                    else {
                        autoStrategy = -1;
                    }
                    final boolean isAttside = battleSide == 1;
                    this.inBattlePlayers.put(CaPhantom.playerId, new PlayerInfo(CaPhantom.playerId, isAttside, autoStrategy));
                }
                this.newlyJoinSet.add(CaPhantom);
                baseInfo.setNum(baseInfo.getNum() + CaPhantom.getArmyHpOrg());
                baseInfo.setAllNum(baseInfo.getAllNum() + CaPhantom.getArmyHpOrg());
                final StringBuilder battleMsg = new StringBuilder();
                final int sn = this.ticket.incrementAndGet();
                battleMsg.append(sn).append("|").append(this.battleId).append("#");
                if (this.attList.size() <= 2) {
                    final List<BattleArmy> attAddQlist = new ArrayList<BattleArmy>();
                    Builder.onceAddQueues(attAddQlist, this.attList, this.attCamp, 1, this.attQNum, this.attBaseInfo, this.inBattlePlayers, null, this);
                    Builder.getReportType2(battleMsg, attAddQlist, "att");
                    if (attAddQlist.size() > 0) {
                        final int attAddSize = attAddQlist.size();
                        BattleSceneLog.getInstance().appendLogMsg("joinPhantom att addQueue").appendBattleId(this.battleId).append("attAddSize", attAddSize).append("begin row", attAddQlist.get(0).getPosition()).append("end row", attAddQlist.get(attAddSize - 1).getPosition()).flush();
                    }
                    BattleSceneLog.getInstance().info("joinPhantom attList");
                }
                if (this.defList.size() <= 2) {
                    final List<BattleArmy> defAddQlist = new ArrayList<BattleArmy>();
                    Builder.onceAddQueues(defAddQlist, this.defList, this.defCamp, 0, this.defQNum, this.defBaseInfo, this.inBattlePlayers, null, this);
                    Builder.getReportType2(battleMsg, defAddQlist, "def");
                    if (defAddQlist.size() > 0) {
                        final int defAddSize = defAddQlist.size();
                        BattleSceneLog.getInstance().appendLogMsg("joinPhantom def addQueue").appendBattleId(this.battleId).append("defAddSize", defAddSize).append("begin row", defAddQlist.get(0).getPosition()).append("end row", defAddQlist.get(defAddSize - 1).getPosition()).flush();
                    }
                    BattleSceneLog.getInstance().info("joinPhantom defList");
                }
                Builder.getReportType13(this, battleMsg);
                this.SaveCurReport(dataGetter);
                Builder.sendMsgToAll(this, battleMsg);
                final boolean hzFlag = dataGetter.getHuiZhanService().isHuiZhanInStatePreparation(this.getDefBaseInfo().getId());
                if (hzFlag) {
                    this.caculateAgainstInfo(dataGetter);
                }
                dataGetter.getBattleService().updateHuizhanNationForce(this.defBaseInfo.getId(), CaPhantom.forceId, CaPhantom.getArmyHp());
            }
            // monitorexit(this.battleId)
            return true;
        }
        catch (Exception e2) {
            ErrorSceneLog.getInstance().trace("Battle.joinPhantom catch Exception", e2);
            return false;
        }
    }
    
    public String getCurrentBattleInfo(final int playerId, final int battleSide, final IDataGetter dataGetter, final int backOrJoin) {
        synchronized (this.battleId) {
            if (this.curBattling.indexOf("#26") == 0 && this.curBattling.indexOf("#27") > 0) {
                this.curBattling.replace(this.curBattling.indexOf("#26") + 4, this.curBattling.indexOf("#27"), new StringBuilder(String.valueOf(this.nextMaxExeTime - System.currentTimeMillis())).toString());
            }
            else if (this.curBattling.indexOf("#26") > 0 && this.curBattling.indexOf("#27") > 0) {
                this.curBattling.replace(this.curBattling.indexOf("#26") + 4, this.curBattling.indexOf("#27"), new StringBuilder(String.valueOf(this.nextMaxExeTime - System.currentTimeMillis())).toString());
            }
            final StringBuilder battleMsg = new StringBuilder();
            battleMsg.append(this.ticket.get()).append("|").append(this.battleId).append("#").append(17).append("|").append(battleSide).append("#");
            battleMsg.append(this.curReport).append(this.curBattling);
            Builder.getReportType100(dataGetter, this, battleMsg);
            final String saveReport = Configuration.getProperty("gcld.battle.report.save");
            String headInfo = null;
            if (saveReport.equals("1")) {
                final String playerName = dataGetter.getPlayerDao().read(playerId).getPlayerName();
                if (backOrJoin == 1) {
                    headInfo = " battleId " + this.getBattleId() + " \u8fd4\u56de\u6218\u6597\u573a\u666f, \u53d1\u9001\u7ed9\u4e2a\u4eba  " + playerName + "  battleMsg:";
                }
                else if (backOrJoin == 2) {
                    headInfo = " battleId " + this.getBattleId() + " \u52a0\u5165\u6218\u6597, \u53d1\u9001\u7ed9\u4e2a\u4eba  " + playerName + "  battleMsg:";
                }
                Builder.getLog(battleMsg.toString(), headInfo);
            }
            // monitorexit(this.battleId)
            return battleMsg.toString();
        }
    }
    
    public void SaveCurReport(final IDataGetter dataGetter) {
        final StringBuilder battleMsg = new StringBuilder();
        Builder.getReportType10(battleMsg, this.attList, "att", this.getAttBaseInfo());
        Builder.getReportType10(battleMsg, this.defList, "def", this.getDefBaseInfo());
        Builder.getReportType13(this, battleMsg);
        CampArmy attCampArmy = null;
        if (this.attList.size() > 0) {
            attCampArmy = this.attList.get(0).getCampArmy();
        }
        if (attCampArmy != null) {
            Builder.getReportType16(dataGetter, this, battleMsg, attCampArmy, "att", true, true);
        }
        CampArmy defCampArmy = null;
        if (this.defList.size() > 0) {
            defCampArmy = this.defList.get(0).getCampArmy();
        }
        if (defCampArmy != null) {
            Builder.getReportType16(dataGetter, this, battleMsg, defCampArmy, "def", true, true);
        }
        this.curReport = battleMsg;
    }
    
    private void saveCurBattling(final IDataGetter dataGetter, final RoundInfo roundInfo) {
        String[] strs = null;
        this.curBattling.delete(0, this.curBattling.length());
        this.curBattling.append(12).append("|").append(";");
        final BattleArmy attBa = roundInfo.attBattleArmy;
        int attSpecialType = (attBa.getCampArmy().getTeamGenreal() == null) ? 0 : ((attBa.getCampArmy().getTeamEffect() > 0.0) ? 2 : 1);
        if (attBa.getTD_defense_e() > 0.0) {
            attSpecialType = 3;
        }
        this.curBattling.append(attBa.getPosition()).append("|").append(attBa.getCampArmy().getPlayerId()).append("|").append(attBa.getCampArmy().getTroopSerial()).append("|").append(attBa.getCampArmy().getTroopType()).append("|").append(attBa.getCampArmy().getTroopName()).append("|").append(attBa.getCampArmy().getTroopDropType()).append("|").append(attBa.getStrategy()).append("|").append(attSpecialType).append("|");
        int troopHpMax = attBa.getCampArmy().getTroopHp() / 3;
        for (int j = 0; j < 3; ++j) {
            this.curBattling.append(attBa.getTroopHp()[j]).append("*").append(troopHpMax).append(",");
        }
        this.curBattling.replace(this.curBattling.length() - 1, this.curBattling.length(), ";");
        final BattleArmy defBa = roundInfo.defBattleArmy;
        int defSpecialType = (defBa.getCampArmy().getTeamGenreal() == null) ? 0 : ((defBa.getCampArmy().getTeamEffect() > 0.0) ? 2 : 1);
        if (defBa.getTD_defense_e() > 0.0) {
            defSpecialType = 3;
        }
        this.curBattling.append(defBa.getPosition()).append("|").append(defBa.getCampArmy().getPlayerId()).append("|").append(defBa.getCampArmy().getTroopSerial()).append("|").append(defBa.getCampArmy().getTroopType()).append("|").append(defBa.getCampArmy().getTroopName()).append("|").append(defBa.getCampArmy().getTroopDropType()).append("|").append(defBa.getStrategy()).append("|").append(defSpecialType).append("|");
        troopHpMax = defBa.getCampArmy().getTroopHp() / 3;
        for (int i = 0; i < 3; ++i) {
            this.curBattling.append(defBa.getTroopHp()[i]).append("*").append(troopHpMax).append(",");
        }
        this.curBattling.replace(this.curBattling.length() - 1, this.curBattling.length(), ";");
        if (roundInfo.reports != null) {
            String[] reports;
            for (int length = (reports = roundInfo.reports).length, l = 0; l < length; ++l) {
                final String str = reports[l];
                strs = str.split(";");
                this.curBattling.append(strs[strs.length - 1]).append(",");
            }
        }
        else if (roundInfo.win == 1) {
            for (int k = 0; k < 3; ++k) {
                this.curBattling.append("1001|1").append(",");
            }
        }
        else if (roundInfo.win == 2) {
            for (int k = 0; k < 3; ++k) {
                this.curBattling.append("1001|2").append(",");
            }
        }
        else {
            for (int k = 0; k < 3; ++k) {
                this.curBattling.append("1001|3").append(",");
            }
        }
        this.curBattling.replace(this.curBattling.length() - 1, this.curBattling.length(), "#");
        this.curBattling.append(26).append("|").append(6000).append("#");
        Builder.getReportType27(this.curBattling, roundInfo, this, dataGetter, -1L);
    }
    
    public boolean canCallHelp(final int playerId) {
        return (this.battleType == 2 || this.battleType == 7) && this.attBaseInfo.getId() == playerId;
    }
    
    public boolean fireASinglePk(final IDataGetter dataGetter) {
        try {
            synchronized (this.getBattleId()) {
                final CampArmy[] cas = new CampArmy[2];
                if (this.attCamp.size() == 0 || this.defCamp.size() == 0) {
                    // monitorexit(this.getBattleId())
                    return false;
                }
                final int attChooseId = this.attCamp.size() - 1;
                final int defChooseId = this.defCamp.size() - 1;
                cas[0] = this.attCamp.get(attChooseId);
                cas[1] = this.defCamp.get(defChooseId);
                if (cas[0].onQueues || cas[1].onQueues) {
                    // monitorexit(this.getBattleId())
                    return false;
                }
                this.attCamp.remove(cas[0]);
                this.defCamp.remove(cas[1]);
                this.attBaseInfo.setAllNum(this.attBaseInfo.getAllNum() - cas[0].armyHp);
                this.attBaseInfo.setNum(this.attBaseInfo.getNum() - cas[0].armyHp);
                this.defBaseInfo.setAllNum(this.defBaseInfo.getAllNum() - cas[1].armyHp);
                this.defBaseInfo.setNum(this.defBaseInfo.getNum() - cas[1].armyHp);
                dataGetter.getBattleService().createOneToOneBattle(-1, cas, this, 3, 0);
                // monitorexit(this.getBattleId())
                return true;
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("Battle.fireASinglePk catch Exception", e);
            return false;
        }
    }
    
    public Tuple<Boolean, String> changeExeTime(final long newExeTime) {
        final Tuple<Boolean, String> result = new Tuple();
        result.left = false;
        synchronized (this.battleId) {
            if (newExeTime < this.nextMinExeTime || newExeTime > this.nextMaxExeTime) {
                final String quickMode = Configuration.getProperty("gcld.battle.quick");
                if (!quickMode.equals("1")) {
                    ErrorSceneLog.getInstance().appendErrorMsg("newExeTime is invalid").appendBattleId(this.battleId).append("roundNum", this.getRoundNum()).append("newExeTime", newExeTime).append("nextMinExeTime", this.nextMinExeTime).append("nextMaxExeTime", this.nextMaxExeTime).appendMethodName("changeExeTime").appendClassName("Battle").flush();
                    result.right = LocalMessages.BATTLE_APPOINTED_TIME_CANNOT_EXECUTE;
                    // monitorexit(this.battleId)
                    return result;
                }
            }
            BattleScheduler.getInstance().removeBattle(this);
            this.nextExeTime = newExeTime;
            BattleScheduler.getInstance().addBattleToScheduler(this);
            result.left = true;
            // monitorexit(this.battleId)
            return result;
        }
    }
    
    @Override
    public int compareTo(final Battle battle) {
        return (int)(this.nextExeTime - battle.nextExeTime);
    }
}
