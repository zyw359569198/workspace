package com.reign.gcld.kfzb.dao;

import com.reign.gcld.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfzbSignupDao")
public class KfzbSignupDao extends BaseDao<KfzbSignup> implements IKfzbSignupDao
{
    @Override
	public KfzbSignup read(final int seasonId) {
        return (KfzbSignup)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbSignup.read", (Object)seasonId);
    }
    
    @Override
	public KfzbSignup readForUpdate(final int seasonId) {
        return (KfzbSignup)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbSignup.readForUpdate", (Object)seasonId);
    }
    
    @Override
	public List<KfzbSignup> getModels() {
        return (List<KfzbSignup>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbSignup.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbSignup.getModelSize");
    }
    
    @Override
	public int create(final KfzbSignup kfzbSignup) {
        return this.getSqlSession().insert("com.reign.gcld.kfzb.domain.KfzbSignup.create", kfzbSignup);
    }
    
    @Override
	public int deleteById(final int seasonId) {
        return this.getSqlSession().delete("com.reign.gcld.kfzb.domain.KfzbSignup.deleteById", seasonId);
    }
    
    @Override
	public KfzbSignup getByPlayerIdAndSeasonId(final int playerId, final int seasonId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId);
        return (KfzbSignup)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbSignup.getByPlayerIdAndSeasonId", (Object)params);
    }
    
    @Override
	public List<KfzbSignup> getBySeasonId(final int seasonId) {
        return (List<KfzbSignup>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbSignup.getBySeasonId", (Object)seasonId);
    }
}
