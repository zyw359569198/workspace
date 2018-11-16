package com.reign.gcld.battle.common;

import java.util.*;
import com.reign.gcld.battle.scene.*;

public class JoinTeamBattleInfo
{
    public boolean result;
    public String error;
    public int battleSide;
    public LinkedList<CampArmy> campJoin;
    public LinkedList<CampArmy> campCreate;
    
    public JoinTeamBattleInfo() {
        this.result = false;
    }
}
