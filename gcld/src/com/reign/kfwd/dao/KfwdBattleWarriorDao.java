package com.reign.kfwd.dao;

import com.reign.kf.match.common.*;
import com.reign.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdBattleWarriorDao extends DirectBaseDao<KfwdBattleWarrior, Integer> implements IKfwdBattleWarriorDao
{
    @Override
    public List<KfwdBattleWarrior> getAllWarriorBySeasonId(final int seasonId) {
        final String hql = "from KfwdBattleWarrior where seasonId=?";
        return (List<KfwdBattleWarrior>)this.getResultByHQLAndParam(hql, seasonId);
    }
    
    @Override
    public KfwdBattleWarrior getPlayer(final String gameServer, final Integer playerId, final int curSeasonId) {
        final String hql = "from KfwdBattleWarrior where seasonId=? and gameServer=? and playerId=?  ";
        return ((DirectBaseDao<KfwdBattleWarrior, PK>)this).getFirstResultByHQLAndParam(hql, curSeasonId, gameServer, playerId);
    }
}
