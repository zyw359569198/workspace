package com.reign.kfwd.battle;

import com.reign.util.*;
import com.reign.kf.comm.param.match.*;

public class KfwdCampArmy
{
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
    int maxForces;
    String troopName;
    int troopDropType;
    int troopHp;
    int attEffect;
    int defEffect;
    public int ATT_B;
    public int DEF_B;
    int TACTIC_ATT;
    int TACTIC_DEF;
    boolean isInRecruit;
    String generalPic;
    int leader;
    int strength;
    int quality;
    boolean inBattle;
    double terrain;
    int terrainQ;
    int tacticVal;
    int terrainAdd;
    int tacicId;
    int killGeneral;
    int[] strategies;
    int column;
    Tuple<Integer, Integer> terrainAttDefAdd;
    String teamGenreal;
    double teamEffect;
    int techYinYong;
    int techJianRen;
    KfSpecialGeneral kfspecialGeneral;
    private GemAttribute gemAttribute;
    
    public KfwdCampArmy() {
        this.cIdentify = -1.0f;
        this.cDifyType = 0;
        this.troopDropType = 0;
        this.ATT_B = 0;
        this.DEF_B = 0;
        this.isInRecruit = false;
        this.inBattle = true;
        this.tacticVal = 1;
        this.terrainAdd = 0;
        this.killGeneral = 0;
        this.terrainAttDefAdd = new Tuple(0, 0);
    }
    
    public KfSpecialGeneral getKfspecialGeneral() {
        return this.kfspecialGeneral;
    }
    
    public void setKfspecialGeneral(final KfSpecialGeneral kfspecialGeneral) {
        this.kfspecialGeneral = kfspecialGeneral;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
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
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
    }
    
    public int getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    public int getGeneralLv() {
        return this.generalLv;
    }
    
    public void setGeneralLv(final int generalLv) {
        this.generalLv = generalLv;
    }
    
    public String getGeneralName() {
        return this.generalName;
    }
    
    public void setGeneralName(final String generalName) {
        this.generalName = generalName;
    }
    
    public int getCivilId() {
        return this.civilId;
    }
    
    public void setCivilId(final int civilId) {
        this.civilId = civilId;
    }
    
    public float getcIdentify() {
        return this.cIdentify;
    }
    
    public void setcIdentify(final float cIdentify) {
        this.cIdentify = cIdentify;
    }
    
    public int getcDifyType() {
        return this.cDifyType;
    }
    
    public void setcDifyType(final int cDifyType) {
        this.cDifyType = cDifyType;
    }
    
    public int getTroopId() {
        return this.troopId;
    }
    
    public void setTroopId(final int troopId) {
        this.troopId = troopId;
    }
    
    public int getTroopSerial() {
        return this.troopSerial;
    }
    
    public void setTroopSerial(final int troopSerial) {
        this.troopSerial = troopSerial;
    }
    
    public int getTroopType() {
        return this.troopType;
    }
    
    public void setTroopType(final int troopType) {
        this.troopType = troopType;
    }
    
    public int getArmyHp() {
        return this.armyHp;
    }
    
    public void setArmyHp(final int armyHp) {
        this.armyHp = armyHp;
    }
    
    public int getArmyHpOrg() {
        return this.armyHpOrg;
    }
    
    public void setArmyHpOrg(final int armyHpOrg) {
        this.armyHpOrg = armyHpOrg;
    }
    
    public int getArmyHpLoss() {
        return this.armyHpLoss;
    }
    
    public void setArmyHpLoss(final int armyHpLoss) {
        this.armyHpLoss = armyHpLoss;
    }
    
    public int getArmyHpKill() {
        return this.armyHpKill;
    }
    
    public void setArmyHpKill(final int armyHpKill) {
        this.armyHpKill = armyHpKill;
    }
    
    public int getMaxForces() {
        return this.maxForces;
    }
    
    public void setMaxForces(final int maxForces) {
        this.maxForces = maxForces;
    }
    
    public String getTroopName() {
        return this.troopName;
    }
    
    public void setTroopName(final String troopName) {
        this.troopName = troopName;
    }
    
    public int getTroopDropType() {
        return this.troopDropType;
    }
    
    public void setTroopDropType(final int troopDropType) {
        this.troopDropType = troopDropType;
    }
    
    public int getTroopHp() {
        return this.troopHp;
    }
    
    public void setTroopHp(final int troopHp) {
        this.troopHp = troopHp;
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
    
    public int getATT_B() {
        return this.ATT_B;
    }
    
    public void setATT_B(final int aTT_B) {
        this.ATT_B = aTT_B;
    }
    
    public int getDEF_B() {
        return this.DEF_B;
    }
    
    public void setDEF_B(final int dEF_B) {
        this.DEF_B = dEF_B;
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
    
    public boolean isInRecruit() {
        return this.isInRecruit;
    }
    
    public void setInRecruit(final boolean isInRecruit) {
        this.isInRecruit = isInRecruit;
    }
    
    public String getGeneralPic() {
        return this.generalPic;
    }
    
    public void setGeneralPic(final String generalPic) {
        this.generalPic = generalPic;
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
    
    public int getQuality() {
        return this.quality;
    }
    
    public void setQuality(final int quality) {
        this.quality = quality;
    }
    
    public boolean isInBattle() {
        return this.inBattle;
    }
    
    public void setInBattle(final boolean inBattle) {
        this.inBattle = inBattle;
    }
    
    public double getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(final double terrain) {
        this.terrain = terrain;
    }
    
    public int getTerrainQ() {
        return this.terrainQ;
    }
    
    public void setTerrainQ(final int terrainQ) {
        this.terrainQ = terrainQ;
    }
    
    public int getTacticVal() {
        return this.tacticVal;
    }
    
    public void setTacticVal(final int tacticVal) {
        this.tacticVal = tacticVal;
    }
    
    public int getTacicId() {
        return this.tacicId;
    }
    
    public void setTacicId(final int tacicId) {
        this.tacicId = tacicId;
    }
    
    public int getKillGeneral() {
        return this.killGeneral;
    }
    
    public void setKillGeneral(final int killGeneral) {
        this.killGeneral = killGeneral;
    }
    
    public int[] getStrategies() {
        return this.strategies;
    }
    
    public void setStrategies(final int[] strategies) {
        this.strategies = strategies;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public void setColumn(final int column) {
        this.column = column;
    }
    
    public Tuple<Integer, Integer> getTerrainAttDefAdd() {
        return this.terrainAttDefAdd;
    }
    
    public void setTerrainAttDefAdd(final Tuple<Integer, Integer> terrainAttDefAdd) {
        this.terrainAttDefAdd = terrainAttDefAdd;
    }
    
    public String getTeamGenreal() {
        return this.teamGenreal;
    }
    
    public void setTeamGenreal(final String teamGenreal) {
        this.teamGenreal = teamGenreal;
    }
    
    public double getTeamEffect() {
        return this.teamEffect;
    }
    
    public void setTeamEffect(final double teamEffect) {
        this.teamEffect = teamEffect;
    }
    
    public int getTechYinYong() {
        return this.techYinYong;
    }
    
    public void setTechYinYong(final int techYinYong) {
        this.techYinYong = techYinYong;
    }
    
    public int getTechJianRen() {
        return this.techJianRen;
    }
    
    public void setTechJianRen(final int techJianRen) {
        this.techJianRen = techJianRen;
    }
    
    public int getTerrainAdd() {
        return this.terrainAdd;
    }
    
    public void setTerrainAdd(final int terrainAdd) {
        this.terrainAdd = terrainAdd;
    }
    
    public GemAttribute getGemAttribute() {
        return this.gemAttribute;
    }
    
    public void setGemAttribute(final GemAttribute gemAttribute) {
        this.gemAttribute = gemAttribute;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return ((KfwdCampArmy)obj).getId() == this.id;
    }
}
