package com.reign.gcld.kfwd.dao;

import com.reign.gcld.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfwdRewardDoubleDao")
public class KfwdRewardDoubleDao extends BaseDao<KfwdRewardDouble> implements IKfwdRewardDoubleDao
{
    @Override
	public KfwdRewardDouble read(final int pk) {
        return (KfwdRewardDouble)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdRewardDouble.read", (Object)pk);
    }
    
    @Override
	public KfwdRewardDouble readForUpdate(final int pk) {
        return (KfwdRewardDouble)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdRewardDouble.readForUpdate", (Object)pk);
    }
    
    @Override
	public List<KfwdRewardDouble> getModels() {
        return (List<KfwdRewardDouble>)this.getSqlSession().selectList("com.reign.gcld.kfwd.domain.KfwdRewardDouble.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdRewardDouble.getModelSize");
    }
    
    @Override
	public int create(final KfwdRewardDouble kfwdRewardDouble) {
        return this.getSqlSession().insert("com.reign.gcld.kfwd.domain.KfwdRewardDouble.create", kfwdRewardDouble);
    }
    
    @Override
	public int deleteById(final int pk) {
        return this.getSqlSession().delete("com.reign.gcld.kfwd.domain.KfwdRewardDouble.deleteById", pk);
    }
    
    @Override
	public KfwdRewardDouble getPlayerRewardInfoByPIdAndSeasonId(final int seasonId, final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("seasonId", seasonId);
        return (KfwdRewardDouble)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdRewardDouble.getPlayerRewardInfoByPIdAndSeasonId", (Object)params);
    }
    
    @Override
	public void updateNewDoubleInfo(final KfwdRewardDouble kfwdRewardDouble) {
        final Params params = new Params();
        params.addParam("pk", kfwdRewardDouble.getPk()).addParam("doubleInfo", kfwdRewardDouble.getDoubleinfo());
        this.getSqlSession().update("com.reign.gcld.kfwd.domain.KfwdRewardDouble.updateNewDoubleInfo", params);
    }
}
