package com.reign.gcld.kfwd.dao;

import com.reign.gcld.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfwdMatchSignDao")
public class KfwdMatchSignDao extends BaseDao<KfwdMatchSign> implements IKfwdMatchSignDao
{
    @Override
	public KfwdMatchSign read(final int vid) {
        return (KfwdMatchSign)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdMatchSign.read", (Object)vid);
    }
    
    @Override
	public KfwdMatchSign readForUpdate(final int vid) {
        return (KfwdMatchSign)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdMatchSign.readForUpdate", (Object)vid);
    }
    
    @Override
	public List<KfwdMatchSign> getModels() {
        return (List<KfwdMatchSign>)this.getSqlSession().selectList("com.reign.gcld.kfwd.domain.KfwdMatchSign.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdMatchSign.getModelSize");
    }
    
    @Override
	public int create(final KfwdMatchSign kfwdMatchSign) {
        return this.getSqlSession().insert("com.reign.gcld.kfwd.domain.KfwdMatchSign.create", kfwdMatchSign);
    }
    
    @Override
	public int deleteById(final int vid) {
        return this.getSqlSession().delete("com.reign.gcld.kfwd.domain.KfwdMatchSign.deleteById", vid);
    }
    
    @Override
	public KfwdMatchSign getWorldMatchSign(final String matchTag, final int playerId) {
        final Params params = new Params();
        params.addParam("matchTag", matchTag);
        params.addParam("playerId", playerId);
        return (KfwdMatchSign)this.getSqlSession().selectOne("com.reign.gcld.kfwd.domain.KfwdMatchSign.getWorldMatchSign", (Object)params);
    }
    
    @Override
	public void update(final KfwdMatchSign worldMatchSign) {
        this.getSqlSession().update("com.reign.gcld.kfwd.domain.KfwdMatchSign.update", worldMatchSign);
    }
    
    @Override
	public List<KfwdMatchSign> getWorldMatchSignListByMatchTag(final String matchTag) {
        return (List<KfwdMatchSign>)this.getSqlSession().selectList("com.reign.gcld.kfwd.domain.KfwdMatchSign.getWorldMatchSignListByMatchTag", (Object)matchTag);
    }
}
