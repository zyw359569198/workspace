package com.reign.gcld.kfgz.dao;

import com.reign.gcld.kfgz.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.framework.mybatis.*;

@Component("kfgzPlayerFinalRewardDao")
public class KfgzPlayerFinalRewardDao extends BaseDao<KfgzPlayerFinalReward> implements IKfgzPlayerFinalRewardDao
{
    @Override
	public KfgzPlayerFinalReward read(final int playerId) {
        return (KfgzPlayerFinalReward)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.read", (Object)playerId);
    }
    
    @Override
	public KfgzPlayerFinalReward readForUpdate(final int playerId) {
        return (KfgzPlayerFinalReward)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<KfgzPlayerFinalReward> getModels() {
        return (List<KfgzPlayerFinalReward>)this.getSqlSession().selectList("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.getModelSize");
    }
    
    @Override
	public int create(final KfgzPlayerFinalReward kfgzPlayerFinalReward) {
        return this.getSqlSession().insert("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.create", kfgzPlayerFinalReward);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.deleteById", playerId);
    }
    
    @Override
	public KfgzPlayerFinalReward safeGetKfgzPlayerFinalReward(final int playerId, final int seasonId, final int nationScore) {
        KfgzPlayerFinalReward result = this.read(playerId);
        if (result == null) {
            result = new KfgzPlayerFinalReward();
            result.setPlayerId(playerId);
            result.setSeasonId(seasonId);
            result.setNationScore(nationScore);
            result.setRewardTimes(KfgzSeasonService.initFinalReward);
            this.create(result);
        }
        if (result.getSeasonId() != seasonId) {
            final Params params = new Params();
            params.addParam("playerId", playerId);
            params.addParam("seasonId", seasonId);
            params.addParam("nationScore", nationScore);
            params.addParam("rewardTimes", KfgzSeasonService.initFinalReward);
            this.getSqlSession().update("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.initKfgzPlayerFinalReward", params);
            result = this.read(playerId);
        }
        return result;
    }
    
    @Override
	public int addGetFinalReward(final int playerId, final int seasonId, final String rewardTimes, final String oldRewardTimes, final int nationScore) {
        this.safeGetKfgzPlayerFinalReward(playerId, seasonId, nationScore);
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("seasonId", seasonId);
        params.addParam("rewardTimes", rewardTimes);
        params.addParam("oldRewardTimes", oldRewardTimes);
        return this.getSqlSession().update("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.addGetFinalReward", params);
    }
    
    @Override
	public List<KfgzPlayerFinalReward> getBySeasonId(final int seasonId) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        return (List<KfgzPlayerFinalReward>)this.getSqlSession().selectList("com.reign.gcld.kfgz.domain.KfgzPlayerFinalReward.getBySeasonId", (Object)params);
    }
}
