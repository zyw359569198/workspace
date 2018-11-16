package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfzbSeasonFeastInfoDao extends BaseDao<KfzbSeasonFeastInfo, Integer> implements IKfzbSeasonFeastInfoDao
{
    @Override
	public List<KfzbSeasonFeastInfo> getFeastInfoBySeasonId(final int seasonId) {
        final String hql = "from KfzbSeasonFeastInfo where seasonId=?";
        return (List<KfzbSeasonFeastInfo>)this.getResultByHQLAndParam(hql, new Object[] { seasonId });
    }
    
    @Override
	public KfzbSeasonFeastInfo getInfoBySeasonAndPos(final int seasonId, final int pos) {
        final String hql = "from KfzbSeasonFeastInfo where seasonId=? and pos=?";
        final List<KfzbSeasonFeastInfo> list = (List<KfzbSeasonFeastInfo>)this.getResultByHQLAndParam(hql, new Object[] { seasonId, pos });
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
