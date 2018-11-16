package com.reign.kfzb.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;
import org.hibernate.*;
import java.util.*;

@Component
public class KfzbWinnerInfoDDao extends DirectBaseDao<KfzbWinnerInfoD, Integer> implements IKfzbWinnerInfoDDao
{
    @Override
    public void deleteAllSeasonInfo(final int seasonId) {
        final String hql = "delete from KfzbWinnerInfoD where seasonId=?";
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.setParameter(0, seasonId);
        query.executeUpdate();
        this.releaseSession(session);
    }
    
    @Override
    public List<KfzbWinnerInfoD> getTop16PlayerInfo() {
        final String hql = "from KfzbWinnerInfoD order by seasonId desc";
        final KfzbWinnerInfoD kfzbWinnerInfoD = ((DirectBaseDao<KfzbWinnerInfoD, PK>)this).getFirstResultByHQLAndParam(hql);
        if (kfzbWinnerInfoD == null) {
            return null;
        }
        final String hql2 = "from KfzbWinnerInfoD where seasonId=? order by pos asc, pk asc";
        return (List<KfzbWinnerInfoD>)this.getResultByHQLAndParam(hql2, kfzbWinnerInfoD.getSeasonId());
    }
}
