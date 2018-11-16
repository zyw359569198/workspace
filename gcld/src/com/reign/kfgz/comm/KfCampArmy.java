package com.reign.kfgz.comm;

import com.reign.util.*;
import com.reign.kfgz.battle.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kfgz.resource.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;

public class KfCampArmy
{
    public KfGeneralInfo generalInfo;
    public int id;
    public int pgmVId;
    public String armyName;
    public int playerId;
    public int forceId;
    public String playerName;
    public int playerLv;
    public int generalId;
    public int generalLv;
    public String generalName;
    public int civilId;
    float cIdentify;
    public int cDifyType;
    public int troopId;
    public int troopSerial;
    public int troopType;
    public int armyHp;
    public int armyHpOrg;
    public int armyHpLoss;
    public int armyHpKill;
    public int maxForces;
    public String troopName;
    public int troopDropType;
    public int troopHp;
    public int attEffect;
    public int defEffect;
    public int ATT_B;
    public int DEF_B;
    public int TACTIC_ATT;
    public int TACTIC_DEF;
    boolean isInRecruit;
    public String generalPic;
    public int leader;
    public int strength;
    public int quality;
    double terrain;
    public int terrainQ;
    public int tacticVal;
    public int terrainAdd;
    public int tacicId;
    public int killGeneral;
    public int[] strategies;
    public int column;
    Tuple<Integer, Integer> terrainAttDefAdd;
    public String teamGenreal;
    double teamEffect;
    public int techYinYong;
    public int techJianRen;
    public boolean isPhantom;
    public int curStrategy;
    boolean isBarPhantom;
    boolean isEA;
    boolean isBarEA;
    boolean onQueues;
    public KfSpecialGeneral kfspecialGeneral;
    BattleDropAnd troopDrop;
    int bdEffect;
    public final double rewardDouble = 2.0;
    public boolean tacticRemain;
    private GemAttribute gemAttribute;
    
    public KfCampArmy() {
        this.cIdentify = -1.0f;
        this.cDifyType = 0;
        this.troopDropType = 0;
        this.ATT_B = 0;
        this.DEF_B = 0;
        this.isInRecruit = false;
        this.tacticVal = 1;
        this.terrainAdd = 0;
        this.killGeneral = 0;
        this.terrainAttDefAdd = new Tuple(0, 0);
        this.isPhantom = false;
        this.curStrategy = 0;
        this.isBarPhantom = false;
        this.isEA = false;
        this.isBarEA = false;
        this.onQueues = false;
        this.troopDrop = null;
        this.tacticRemain = false;
    }
    
    public boolean isTacticRemain() {
        return this.tacticRemain;
    }
    
    public void setTacticRemain(final boolean tacticRemain) {
        this.tacticRemain = tacticRemain;
    }
    
    public double getRewardDouble() {
        return 2.0;
    }
    
    public int calcuArmyNum() {
        return (this.armyHp - 3) / this.troopHp + 1;
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
        if (this.generalInfo != null) {
            this.generalInfo.easySetGeneralState();
        }
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
    
    public int getTerrainAdd() {
        return this.terrainAdd;
    }
    
    public void setTerrainAdd(final int terrainAdd) {
        this.terrainAdd = terrainAdd;
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
    
    public boolean isPhantom() {
        return this.isPhantom;
    }
    
    public void setPhantom(final boolean isPhantom) {
        this.isPhantom = isPhantom;
    }
    
    public int getCurStrategy() {
        return this.curStrategy;
    }
    
    public void setCurStrategy(final int curStrategy) {
        this.curStrategy = curStrategy;
    }
    
    public boolean isBarPhantom() {
        return this.isBarPhantom;
    }
    
    public void setBarPhantom(final boolean isBarPhantom) {
        this.isBarPhantom = isBarPhantom;
    }
    
    public boolean isEA() {
        return this.isEA;
    }
    
    public void setEA(final boolean isEA) {
        this.isEA = isEA;
    }
    
    public boolean isBarEA() {
        return this.isBarEA;
    }
    
    public void setBarEA(final boolean isBarEA) {
        this.isBarEA = isBarEA;
    }
    
    public boolean isOnQueues() {
        return this.onQueues;
    }
    
    public void setOnQueues(final boolean onQueues) {
        this.onQueues = onQueues;
    }
    
    public KfSpecialGeneral getKfspecialGeneral() {
        return this.kfspecialGeneral;
    }
    
    public void setKfspecialGeneral(final KfSpecialGeneral kfspecialGeneral) {
        this.kfspecialGeneral = kfspecialGeneral;
    }
    
    public KfGeneralInfo getGeneralInfo() {
        return this.generalInfo;
    }
    
    public void setGeneralInfo(final KfGeneralInfo generalInfo) {
        this.generalInfo = generalInfo;
    }
    
    public boolean needSendMsg() {
        return this.generalInfo.isPlayerRealGeneral();
    }
    
    public BattleDropAnd getTroopDrop() {
        return this.troopDrop;
    }
    
    public void setTroopDrop(final BattleDropAnd troopDrop) {
        this.troopDrop = troopDrop;
    }
    
    public int getBdEffect() {
        return this.bdEffect;
    }
    
    public void setBdEffect(final int bdEffect) {
        this.bdEffect = bdEffect;
    }
    
    public GemAttribute getGemAttribute() {
        return this.gemAttribute;
    }
    
    public void setGemAttribute(final GemAttribute gemAttribute) {
        this.gemAttribute = gemAttribute;
    }
    
    public int needHp() {
        return this.armyHpOrg - this.armyHp;
    }
    
    public void MubingIgnoreResource(final int num) {
        this.armyHp += num;
        if (this.armyHp > this.armyHpOrg) {
            this.armyHp = this.armyHpOrg;
        }
        this.generalInfo.easySetGeneralState();
    }
    
    public boolean Mubing(int num) {
        if (this.armyHp >= this.armyHpOrg) {
            return false;
        }
        if (this.armyHp + num > this.armyHpOrg) {
            num = this.armyHpOrg - this.armyHp;
        }
        final int food = this.getRecuitConsume(num);
        final boolean result = KfgzResChangeManager.consumeResource(this.playerId, food, "food", "\u52df\u5175");
        if (result) {
            this.armyHp += num;
            this.generalInfo.easySetGeneralState();
            this.generalInfo.pushHpData();
        }
        else {
            this.generalInfo.setGeneralState(0);
            this.generalInfo.pushHpData();
        }
        return result;
    }
    
    private int getRecuitConsume(final int num) {
        final TroopConscribe troopConscribe = TroopConscribeCache.getTroopConscribeById(this.troopId);
        final double comsume = (troopConscribe == null) ? 1.0 : troopConscribe.getFood();
        return (int)(comsume * num);
    }
    
    public int getRecuitConsumeForPublic(final int num) {
        if (this.armyHp + num > this.armyHpOrg) {
            return this.getRecuitConsume(this.armyHpOrg - this.armyHp);
        }
        return this.getRecuitConsume(num);
    }
}
