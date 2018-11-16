package com.reign.gcld.battle.scene;

import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.sdata.domain.*;

public class BattleAttacker
{
    public int attType;
    public int attForceId;
    public int attPlayerId;
    public int defPlayerId;
    public Player attPlayer;
    public List<PlayerGeneralMilitary> pgmList;
    public PlayerGeneralMilitaryPhantom pgmPhantom;
    public Barbarain attBarbarain;
    public BarbarainPhantom barPhantom;
    public ExpeditionArmy EA;
    public EfLv eflv;
    public BarbarainExpeditionArmy barEA;
    public WorldPaidB worldPaidB;
    
    public BattleAttacker() {
        this.attType = 0;
        this.attForceId = 0;
        this.attPlayerId = 0;
        this.defPlayerId = 0;
        this.attPlayer = null;
        this.pgmList = null;
        this.pgmPhantom = null;
        this.attBarbarain = null;
        this.barPhantom = null;
        this.EA = null;
        this.eflv = null;
        this.barEA = null;
        this.worldPaidB = null;
    }
}
