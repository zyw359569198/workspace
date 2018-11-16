package com.reign.gcld.kfwd.dao;

import com.reign.gcld.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfwdRewardDao")
public class KfwdRewardDao extends BaseDao<KfwdReward> implements IKfwdRewardDao
{
    @Override
	public KfwdReward read(final int pk) {
        return (KfwdReward)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdReward.read", (Object)pk);
    }
    
    @Override
	public KfwdReward readForUpdate(final int pk) {
        return (KfwdReward)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdReward.readForUpdate", (Object)pk);
    }
    
    @Override
	public List<KfwdReward> getModels() {
        return (List<KfwdReward>)this.getSqlSession().selectList("com.reign.gcld.kfwd.domain.KfwdReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdReward.getModelSize");
    }
    
    @Override
	public int create(final KfwdReward kfwdReward) {
        return this.getSqlSession().insert("com.reign.gcld.kfwd.domain.KfwdReward.create", kfwdReward);
    }
    
    @Override
	public int deleteById(final int pk) {
        return this.getSqlSession().delete("com.reign.gcld.kfwd.domain.KfwdReward.deleteById", pk);
    }
    
    @Override
	public KfwdReward getRewardByPlayerIdAndSeasonId(final Integer playerId, final int seasonId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("seasonId", seasonId);
        return (KfwdReward)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdReward.getRewardByPlayerIdAndSeasonId", (Object)params);
    }
    
    @Override
	public void updateNewRewardInfo(final KfwdReward kfwdReward) {
        final Params params = new Params();
        params.addParam("pk", kfwdReward.getPk()).addParam("rewardinfo", kfwdReward.getRewardinfo()).addParam("ticket", kfwdReward.getTickets()).addParam("dayReward", kfwdReward.getDayReward()).addParam("dayRanking", kfwdReward.getDayRanking()).addParam("dayRewardTicket", kfwdReward.getDayRewardTicket());
        this.getSqlSession().update("com.reign.gcld.kfwd.domain.KfwdReward.updateNewTicketReward", params);
    }
    
    @Override
	public void updateGetTreasure(final KfwdReward kfwdReward) {
        final Params params = new Params();
        params.addParam("pk", kfwdReward.getPk());
        this.getSqlSession().update("com.reign.gcld.kfwd.domain.KfwdReward.updateGetTreasure", params);
    }
    
    @Override
	public List<KfwdReward> getRewardBySeasonId(final int seasonId) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        return (List<KfwdReward>)this.getSqlSession().selectList("com.reign.gcld.kfwd.domain.KfwdReward.getRewardBySeasonId", (Object)params);
    }
    
    @Override
	public Integer getMaxSeasonId() {
        return (Integer)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdReward.getMaxSeasonId");
    }
}
