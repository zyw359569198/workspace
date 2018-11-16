package com.reign.kfgz.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfgz.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfgzRewardDao extends DirectBaseDao<KfgzReward, Integer> implements IKfgzRewardDao
{
    @Override
    public List<KfgzReward> getRewardListByGId(final int rewardgId) {
        final String hql = "from KfgzReward where groupId=?";
        return (List<KfgzReward>)this.getResultByHQLAndParam(hql, rewardgId);
    }
    
    @Override
    public KfgzReward getRewardByGIdAndLayer(final int rewardgId, final int layerId) {
        final String hql = "from KfgzReward where groupId=? and layerId=?";
        return ((DirectBaseDao<KfgzReward, PK>)this).getFirstResultByHQLAndParam(hql, rewardgId, layerId);
    }
}
