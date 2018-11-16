package com.reign.gcld.battle.scene;

import java.io.*;
import com.reign.util.*;
import com.reign.gcld.battle.common.*;

public class CampArmy implements Serializable
{
    private static final long serialVersionUID = -3690402358011857583L;
    int id;
    int pgmVId;
    String armyName;
    int playerId;
    int forceId;
    String playerName;
    int playerLv;
    int generalId;
    int generalLv;
    String generalName;
    int civilId;
    float cIdentify;
    int cDifyType;
    int troopId;
    int troopSerial;
    int troopType;
    int armyHp;
    int armyHpOrg;
    int armyHpLoss;
    int armyHpKill;
    int barbarainHpKill;
    int maxForces;
    String troopName;
    int troopDropType;
    int troopHp;
    int attEffect;
    int defEffect;
    int bdEffect;
    AttDef_B attDef_B;
    int TACTIC_ATT;
    int TACTIC_DEF;
    boolean isInRecruit;
    String generalPic;
    int leader;
    int strength;
    int quality;
    boolean inBattle;
    int terrainAdd;
    double terrain;
    int terrainQ;
    int tacticVal;
    int tacicId;
    int killGeneral;
    boolean updateDB;
    int rewardDoubleType;
    double rewardDouble;
    BattleDropAnd troopDrop;
    int[] strategies;
    int column;
    Tuple<Integer, Integer> terrainAttDefAdd;
    int rbTop;
    int expTop;
    int curStrategy;
    boolean isPhantom;
    boolean isBarPhantom;
    boolean isEA;
    boolean isBarEA;
    boolean isDefenceNpc;
    public boolean isYellowTrubans;
    int nationTaskEAType;
    public static final int NATION_TASK_CONQUER_EA = 1;
    public static final int NATION_TASK_DEFENCE_EA = 2;
    public int npcType;
    public static final int NPC_TYPE_1_ACTIVITY_MOONCAKE = 1;
    public int scenarioArmyType;
    String teamGenreal;
    double teamEffect;
    boolean onQueues;
    SpecialGeneral specialGeneral;
    float activityAddExp;
    
    public CampArmy() {
        this.cIdentify = -1.0f;
        this.cDifyType = 0;
        this.troopDropType = 0;
        this.attDef_B = new AttDef_B();
        this.isInRecruit = false;
        this.inBattle = true;
        this.terrainAdd = 0;
        this.killGeneral = 0;
        this.updateDB = false;
        this.rewardDoubleType = 0;
        this.rewardDouble = 1.0;
        this.troopDrop = null;
        this.terrainAttDefAdd = new Tuple(0, 0);
        this.rbTop = 0;
        this.expTop = 0;
        this.curStrategy = 0;
        this.isPhantom = false;
        this.isBarPhantom = false;
        this.isEA = false;
        this.isBarEA = false;
        this.isDefenceNpc = false;
        this.isYellowTrubans = false;
        this.nationTaskEAType = 0;
        this.npcType = 0;
        this.scenarioArmyType = 0;
        this.onQueues = false;
        this.activityAddExp = 0.0f;
    }
    
    public float getActivityAddExp() {
        return this.activityAddExp;
    }
    
    public void setActivityAddExp(final float activityAddExp) {
        this.activityAddExp = activityAddExp;
    }
    
    public void setBdEffect(final int bdEffect) {
        this.bdEffect = bdEffect;
    }
    
    public int getBdEffect() {
        return this.bdEffect;
    }
    
    public SpecialGeneral getSpecialGeneral() {
        return this.specialGeneral;
    }
    
    public void setSpecialGeneral(final SpecialGeneral specialGeneral) {
        this.specialGeneral = specialGeneral;
    }
    
    public double getTeamEffect() {
        return this.teamEffect;
    }
    
    public void setTeamEffect(final double teamEffect) {
        this.teamEffect = teamEffect;
    }
    
    public boolean isOnQueues() {
        return this.onQueues;
    }
    
    public void setOnQueues(final boolean onQueues) {
        this.onQueues = onQueues;
    }
    
    public String getTeamGenreal() {
        return this.teamGenreal;
    }
    
    public void setTeamGenreal(final String teamGenreal) {
        this.teamGenreal = teamGenreal;
    }
    
    public boolean isPhantom() {
        return this.isPhantom;
    }
    
    public boolean isBarPhantom() {
        return this.isBarPhantom;
    }
    
    public int getScenarioArmyType() {
        return this.scenarioArmyType;
    }
    
    public void setScenarioArmyType(final int scenarioArmyType) {
        this.scenarioArmyType = scenarioArmyType;
    }
    
    public int getNationTaskEAType() {
        return this.nationTaskEAType;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public void setColumn(final int column) {
        this.column = column;
    }
    
    public int[] getStrategies() {
        return this.strategies;
    }
    
    public void setStrategies(final int[] strategies) {
        this.strategies = strategies;
    }
    
    BattleDropAnd getTroopDrop() {
        return this.troopDrop;
    }
    
    void setTroopDrop(final BattleDropAnd troopDrop) {
        this.troopDrop = troopDrop;
    }
    
    public int getTerrainQ() {
        return this.terrainQ;
    }
    
    public void setTerrainQ(final int terrainQ) {
        this.terrainQ = terrainQ;
    }
    
    public int getRewardDoubleType() {
        return this.rewardDoubleType;
    }
    
    public void setRewardDoubleType(final int rewardDoubleType) {
        this.rewardDoubleType = rewardDoubleType;
    }
    
    public double getRewardDouble() {
        return this.rewardDouble;
    }
    
    public void setRewardDouble(final double rewardDouble) {
        this.rewardDouble = rewardDouble;
    }
    
    public boolean isUpdateDB() {
        return this.updateDB;
    }
    
    public void setUpdateDB(final boolean updateDB) {
        this.updateDB = updateDB;
    }
    
    public int getKillGeneral() {
        return this.killGeneral;
    }
    
    public void setKillGeneral(final int killGeneral) {
        this.killGeneral = killGeneral;
    }
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
    }
    
    public float getcIdentify() {
        return this.cIdentify;
    }
    
    public void setcIdentify(final float cIdentify) {
        this.cIdentify = cIdentify;
    }
    
    public int getCivilId() {
        return this.civilId;
    }
    
    public void setCivilId(final int civilId) {
        this.civilId = civilId;
    }
    
    public int getTacicId() {
        return this.tacicId;
    }
    
    public void setTacicId(final int tacicId) {
        this.tacicId = tacicId;
    }
    
    public int getTacticVal() {
        return this.tacticVal;
    }
    
    public void setTacticVal(final int tacticVal) {
        this.tacticVal = tacticVal;
    }
    
    public double getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(final double terrain) {
        this.terrain = terrain;
    }
    
    public int getTerrainAdd() {
        return this.terrainAdd;
    }
    
    public void setTerrainAdd(final int terrainAdd) {
        this.terrainAdd = terrainAdd;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getLeader() {
        return this.leader;
    }
    
    public void setLeader(final int leader) {
        this.leader = leader;
    }
    
    public int getStrength() {
        return this.strength;
    }
    
    public void setStrength(final int strength) {
        this.strength = strength;
    }
    
    public boolean isInBattle() {
        return this.inBattle;
    }
    
    public void setInBattle(final boolean inBattle) {
        this.inBattle = inBattle;
    }
    
    public int getArmyHpKill() {
        return this.armyHpKill;
    }
    
    public void setArmyHpKill(final int armyHpKill) {
        this.armyHpKill = armyHpKill;
    }
    
    public String getGeneralPic() {
        return this.generalPic;
    }
    
    public void setGeneralPic(final String generalPic) {
        this.generalPic = generalPic;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    public int getTroopId() {
        return this.troopId;
    }
    
    public void setTroopId(final int troopId) {
        this.troopId = troopId;
    }
    
    public int getTroopType() {
        return this.troopType;
    }
    
    public void setTroopType(final int troopType) {
        this.troopType = troopType;
    }
    
    public int getTroopSerial() {
        return this.troopSerial;
    }
    
    public void setTroopSerial(final int troopSerial) {
        this.troopSerial = troopSerial;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public String getGeneralName() {
        return this.generalName;
    }
    
    public void setGeneralName(final String generalName) {
        this.generalName = generalName;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getTroopHp() {
        return this.troopHp;
    }
    
    public void setTroopHp(final int troopHp) {
        this.troopHp = troopHp;
    }
    
    public int getPgmVId() {
        return this.pgmVId;
    }
    
    public void setPgmVId(final int pgmVId) {
        this.pgmVId = pgmVId;
    }
    
    public String getArmyName() {
        return this.armyName;
    }
    
    public void setArmyName(final String armyName) {
        this.armyName = armyName;
    }
    
    public String getTroopName() {
        return this.troopName;
    }
    
    public void setTroopName(final String troopName) {
        this.troopName = troopName;
    }
    
    public int getArmyHp() {
        return this.armyHp;
    }
    
    public void setArmyHp(final int armyHp) {
        this.armyHp = armyHp;
    }
    
    public int getAttEffect() {
        return this.attEffect;
    }
    
    public void setAttEffect(final int attEffect) {
        this.attEffect = attEffect;
    }
    
    public int getDefEffect() {
        return this.defEffect;
    }
    
    public void setDefEffect(final int defEffect) {
        this.defEffect = defEffect;
    }
    
    public int getMaxForces() {
        return this.maxForces;
    }
    
    public void setMaxForces(final int maxForces) {
        this.maxForces = maxForces;
    }
    
    public int getArmyHpOrg() {
        return this.armyHpOrg;
    }
    
    public void setArmyHpOrg(final int armyHpOrg) {
        this.armyHpOrg = armyHpOrg;
    }
    
    public int getGeneralLv() {
        return this.generalLv;
    }
    
    public void setGeneralLv(final int generalLv) {
        this.generalLv = generalLv;
    }
    
    public boolean isInRecruit() {
        return this.isInRecruit;
    }
    
    public void setInRecruit(final boolean isInRecruit) {
        this.isInRecruit = isInRecruit;
    }
    
    public int getArmyHpLoss() {
        return this.armyHpLoss;
    }
    
    public void setArmyHpLoss(final int armyHpLoss) {
        this.armyHpLoss = armyHpLoss;
    }
    
    public int getQuality() {
        return this.quality;
    }
    
    public void setQuality(final int quality) {
        this.quality = quality;
    }
    
    public int getcDifyType() {
        return this.cDifyType;
    }
    
    public void setcDifyType(final int cDifyType) {
        this.cDifyType = cDifyType;
    }
    
    public int getTroopDropType() {
        return this.troopDropType;
    }
    
    public void setTroopDropType(final int troopDropType) {
        this.troopDropType = troopDropType;
    }
    
    public AttDef_B getAttDef_B() {
        return this.attDef_B;
    }
    
    public void setAttDef_B(final AttDef_B attDef_B) {
        this.attDef_B = attDef_B;
    }
    
    public int getTACTIC_ATT() {
        return this.TACTIC_ATT;
    }
    
    public void setTACTIC_ATT(final int tACTIC_ATT) {
        this.TACTIC_ATT = tACTIC_ATT;
    }
    
    public int getTACTIC_DEF() {
        return this.TACTIC_DEF;
    }
    
    public void setTACTIC_DEF(final int tACTIC_DEF) {
        this.TACTIC_DEF = tACTIC_DEF;
    }
}
