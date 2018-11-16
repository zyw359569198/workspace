package com.reign.kf.match.common;

public class CampArmy
{
    public int id;
    public int playerId;
    public int armyHpOrg;
    public int generalId;
    public String generalName;
    public String generalPic;
    public int generalLv;
    public int quality;
    public int leader;
    public int strength;
    public int troopId;
    public String troopName;
    public int attEffect;
    public int defEffect;
    public double terrain;
    public int troopHp;
    public int morale;
    public int tacicDisplayId;
    public int tacticVal;
    public int tacicId;
    public float cIdentify;
    public int cDifyType;
    public double tactic_damage_e;
    public int tactic_range;
    public String specialEffect;
    public double rewardDouble;
    public int armyHp;
    public int killGeneral;
    public int maxHp;
    public int armyHpLoss;
    
    public CampArmy() {
        this.rewardDouble = 1.0;
        this.killGeneral = 0;
    }
}
