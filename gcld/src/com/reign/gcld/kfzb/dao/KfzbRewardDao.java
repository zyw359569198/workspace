package com.reign.gcld.kfzb.dao;

import com.reign.gcld.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfzbRewardDao")
public class KfzbRewardDao extends BaseDao<KfzbReward> implements IKfzbRewardDao
{
    @Override
	public KfzbReward read(final int seasonId) {
        return (KfzbReward)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbReward.read", (Object)seasonId);
    }
    
    @Override
	public KfzbReward readForUpdate(final int seasonId) {
        return (KfzbReward)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbReward.readForUpdate", (Object)seasonId);
    }
    
    @Override
	public List<KfzbReward> getModels() {
        return (List<KfzbReward>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbReward.getModelSize");
    }
    
    @Override
	public int create(final KfzbReward kfzbReward) {
        return this.getSqlSession().insert("com.reign.gcld.kfzb.domain.KfzbReward.create", kfzbReward);
    }
    
    @Override
	public int deleteById(final int seasonId) {
        return this.getSqlSession().delete("com.reign.gcld.kfzb.domain.KfzbReward.deleteById", seasonId);
    }
    
    @Override
	public List<KfzbReward> getBySeasonId(final int seasonId) {
        return (List<KfzbReward>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbReward.getBySeasonId", (Object)seasonId);
    }
    
    @Override
	public int updateDoneNum(final int playerId, final int seasonId, final int doneNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId).addParam("doneNum", doneNum);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbReward.updateDoneNum", params);
    }
    
    @Override
	public int updateRewardInfo(final Integer playerId, final int seasonId, final String rewardInfo) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId).addParam("rewardInfo", rewardInfo);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbReward.updateRewardInfo", params);
    }
    
    @Override
	public int getMaxSeasonId() {
        final Integer max = (Integer)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbReward.getMaxSeasonId");
        if (max != null) {
            return max;
        }
        return Integer.MIN_VALUE;
    }
    
    @Override
	public KfzbReward getByPlayerIdSeasonId(final Integer playerId, final int seasonId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId);
        return (KfzbReward)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbReward.getByPlayerIdSeasonId", (Object)params);
    }
    
    @Override
	public int updateTitle(final Integer playerId, final int seasonId, final String kfZbTitle, final int lastPos) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId).addParam("kfZbTitle", kfZbTitle).addParam("lastPos", lastPos);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbReward.updateTitle", params);
    }
    
    @Override
	public List<KfzbReward> getHaveTitleBySeasonId(final int seasonId) {
        return (List<KfzbReward>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbReward.getHaveTitleBySeasonId", (Object)seasonId);
    }
}
