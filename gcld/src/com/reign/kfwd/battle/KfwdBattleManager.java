package com.reign.kfwd.battle;

import java.util.concurrent.*;
import com.reign.kfwd.domain.*;
import com.reign.kfwd.constants.*;

public class KfwdBattleManager
{
    static ConcurrentHashMap<Long, KfwdBattle> wdBattleMap;
    
    static {
        KfwdBattleManager.wdBattleMap = new ConcurrentHashMap<Long, KfwdBattle>();
    }
    
    public static KfwdBattle createNewBattle(final KfwdRuntimeMatch match) {
        final KfwdBattle wdBattle = new KfwdBattle();
        wdBattle.setBattleId(KfwdConstantsAndMethod.getBattleIdByMatch(match.getSeasonId(), match.getScheduleId(), match.getMatchId(), match.getRound()));
        wdBattle.setMatch(match);
        KfwdBattleManager.wdBattleMap.put(wdBattle.getBattleId(), wdBattle);
        return wdBattle;
    }
    
    public static KfwdBattle getBattleById(final long battleId) {
        return KfwdBattleManager.wdBattleMap.get(battleId);
    }
    
    public static void clear() {
        KfwdBattleManager.wdBattleMap.clear();
    }
}
