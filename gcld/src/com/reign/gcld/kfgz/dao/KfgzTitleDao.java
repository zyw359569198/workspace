package com.reign.gcld.kfgz.dao;

import com.reign.gcld.kfgz.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfgzTitleDao")
public class KfgzTitleDao extends BaseDao<KfgzTitle> implements IKfgzTitleDao
{
    @Override
	public KfgzTitle read(final int id) {
        return (KfgzTitle)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzTitle.read", (Object)id);
    }
    
    @Override
	public KfgzTitle readForUpdate(final int id) {
        return (KfgzTitle)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzTitle.readForUpdate", (Object)id);
    }
    
    @Override
	public List<KfgzTitle> getModels() {
        return (List<KfgzTitle>)this.getSqlSession().selectList("com.reign.gcld.kfgz.domain.KfgzTitle.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfgz.domain.KfgzTitle.getModelSize");
    }
    
    @Override
	public int create(final KfgzTitle kfgzTitle) {
        return this.getSqlSession().insert("com.reign.gcld.kfgz.domain.KfgzTitle.create", kfgzTitle);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.kfgz.domain.KfgzTitle.deleteById", id);
    }
    
    @Override
	public List<KfgzTitle> getKfgzTitleListBySeasonId(final int seasonId) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        return (List<KfgzTitle>)this.getSqlSession().selectList("com.reign.gcld.kfgz.domain.KfgzTitle.getKfgzTitleListBySeasonId", (Object)params);
    }
}
