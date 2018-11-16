package com.reign.kfzb.battle;

import java.util.concurrent.*;
import com.reign.kfzb.domain.*;
import com.reign.kfzb.constants.*;

public class KfzbBattleManager
{
    static ConcurrentHashMap<Long, KfzbBattle> wdBattleMap;
    
    static {
        KfzbBattleManager.wdBattleMap = new ConcurrentHashMap<Long, KfzbBattle>();
    }
    
    public static KfzbBattle createNewBattle(final KfzbRuntimeMatch match) {
        final KfzbBattle wdBattle = new KfzbBattle();
        wdBattle.setBattleId(KfzbCommonConstants.getBattleIdByMatch(match.getSeasonId(), match.getMatchId(), match.getRound()));
        wdBattle.setMatch(match);
        KfzbBattleManager.wdBattleMap.put(wdBattle.getBattleId(), wdBattle);
        return wdBattle;
    }
    
    public static KfzbBattle getBattleById(final long battleId) {
        return KfzbBattleManager.wdBattleMap.get(battleId);
    }
    
    public static void clear() {
        KfzbBattleManager.wdBattleMap.clear();
    }
}
