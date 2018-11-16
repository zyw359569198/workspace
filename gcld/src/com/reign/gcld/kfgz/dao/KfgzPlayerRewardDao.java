package com.reign.gcld.kfgz.dao;

import com.reign.gcld.kfgz.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfgzPlayerRewardDao")
public class KfgzPlayerRewardDao extends BaseDao<KfgzPlayerReward> implements IKfgzPlayerRewardDao
{
    @Override
	public KfgzPlayerReward read(final int id) {
        return (KfgzPlayerReward)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerReward.read", (Object)id);
    }
    
    @Override
	public KfgzPlayerReward readForUpdate(final int id) {
        return (KfgzPlayerReward)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerReward.readForUpdate", (Object)id);
    }
    
    @Override
	public List<KfgzPlayerReward> getModels() {
        return (List<KfgzPlayerReward>)this.getSqlSession().selectList("com.reign.gcld.kfgz.domain.KfgzPlayerReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerReward.getModelSize");
    }
    
    @Override
	public int create(final KfgzPlayerReward kfgzPlayerReward) {
        return this.getSqlSession().insert("com.reign.gcld.kfgz.domain.KfgzPlayerReward.create", kfgzPlayerReward);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.kfgz.domain.KfgzPlayerReward.deleteById", id);
    }
    
    @Override
	public int hasData(final int seasonId, final int gzId, final int nation) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        params.addParam("gzId", gzId);
        params.addParam("nation", nation);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerReward.hasData", (Object)params);
    }
    
    @Override
	public KfgzPlayerReward getKfgzPlayerReward(final int seasonId, final int gzId, final int cId) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        params.addParam("gzId", gzId);
        params.addParam("cId", cId);
        return (KfgzPlayerReward)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerReward.getKfgzPlayerReward", (Object)params);
    }
    
    @Override
	public int addRewardTimes(final int id, final int num) {
        final Params params = new Params();
        params.addParam("id", id);
        params.addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.kfgz.domain.KfgzPlayerReward.addRewardTimes", params);
    }
    
    @Override
	public List<KfgzPlayerReward> getModelsBySeasonIdAndgzIdForReward(final int seasonId, final int gzId) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        params.addParam("gzId", gzId);
        return (List<KfgzPlayerReward>)this.getSqlSession().selectList("com.reign.gcld.kfgz.domain.KfgzPlayerReward.getModelsBySeasonIdAndgzId", (Object)params);
    }
    
    @Override
	public List<KfgzPlayerReward> getModelsBySeasonIdForReward(final int seasonId, final int nation) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        params.addParam("nation", nation);
        return (List<KfgzPlayerReward>)this.getSqlSession().selectList("com.reign.gcld.kfgz.domain.KfgzPlayerReward.getModelsBySeasonIdForReward", (Object)params);
    }
    
    @Override
	public int getMaxGzId(final int seasonId) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerReward.getMaxGzId", (Object)params);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int getMaxSeasonId() {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzPlayerReward.getMaxSeasonId");
        return (result == null) ? 0 : result;
    }
}
