package com.reign.kfzb.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfzbSeasonInfoDDao extends DirectBaseDao<KfzbSeasonInfoD, Integer> implements IKfzbSeasonInfoDDao
{
    @Override
    public KfzbSeasonInfoD getActiveSeasonInfo() {
        final String hql = "from KfzbSeasonInfoD where globalState<4 and endTime>? order by endTime asc";
        final List<KfzbSeasonInfoD> kfsi = (List<KfzbSeasonInfoD>)this.getResultByHQLAndParam(hql, new Date());
        if (kfsi.size() > 0) {
            return kfsi.get(0);
        }
        return null;
    }
    
    @Override
    public List<KfzbSeasonInfoD> getNeedEndSeasonInfo() {
        final String hql = "from KfzbSeasonInfoD where globalState<4 and endTime<?";
        final List<KfzbSeasonInfoD> kfsi = (List<KfzbSeasonInfoD>)this.getResultByHQLAndParam(hql, new Date());
        return kfsi;
    }
    
    @Override
    public KfzbSeasonInfoD getLastSeaonInfo() {
        final String hql = "from KfzbSeasonInfoD where day3BattleTime<? order by day3BattleTime desc";
        final List<KfzbSeasonInfoD> kfsi = (List<KfzbSeasonInfoD>)this.getResultByHQLAndParam(hql, new Date());
        if (kfsi.size() > 0) {
            return kfsi.get(0);
        }
        return null;
    }
}
