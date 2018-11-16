package com.reign.gcld.kfgz.dao;

import com.reign.gcld.kfgz.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfgzSignupDao")
public class KfgzSignupDao extends BaseDao<KfgzSignup> implements IKfgzSignupDao
{
    @Override
	public KfgzSignup read(final int playerId) {
        return (KfgzSignup)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzSignup.read", (Object)playerId);
    }
    
    @Override
	public KfgzSignup readForUpdate(final int playerId) {
        return (KfgzSignup)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzSignup.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<KfgzSignup> getModels() {
        return (List<KfgzSignup>)this.getSqlSession().selectList("com.reign.gcld.kfgz.domain.KfgzSignup.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzSignup.getModelSize");
    }
    
    @Override
	public int create(final KfgzSignup kfgzSignup) {
        return this.getSqlSession().insert("com.reign.gcld.kfgz.domain.KfgzSignup.create", kfgzSignup);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.kfgz.domain.KfgzSignup.deleteById", playerId);
    }
    
    @Override
	public KfgzSignup getByCid(final int cId) {
        final Params params = new Params();
        params.addParam("cId", cId);
        return (KfgzSignup)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzSignup.getByCid", (Object)params);
    }
}
