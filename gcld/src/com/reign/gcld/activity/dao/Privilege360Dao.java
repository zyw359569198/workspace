package com.reign.gcld.activity.dao;

import com.reign.gcld.activity.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("privilege360Dao")
public class Privilege360Dao extends BaseDao<Privilege360> implements IPrivilege360Dao
{
    @Override
	public Privilege360 read(final int playerId) {
        return (Privilege360)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.Privilege360.read", (Object)playerId);
    }
    
    @Override
	public Privilege360 readForUpdate(final int playerId) {
        return (Privilege360)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.Privilege360.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<Privilege360> getModels() {
        return (List<Privilege360>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.Privilege360.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.Privilege360.getModelSize");
    }
    
    @Override
	public int create(final Privilege360 privilege360) {
        return this.getSqlSession().insert("com.reign.gcld.activity.domain.Privilege360.create", privilege360);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.Privilege360.deleteById", playerId);
    }
    
    @Override
	public int setStatusByPid(final int playerId, final int seq) {
        final Params params = new Params();
        params.addParam("id", playerId).addParam("seq", seq);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.Privilege360.setStatusByPid", params);
    }
    
    @Override
	public int setTitleByPid(final int playerId, final String title) {
        final Params params = new Params();
        params.addParam("id", playerId).addParam("title", title);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.Privilege360.setTitleByPid", params);
    }
    
    @Override
	public List<Privilege360> getTitleList() {
        return (List<Privilege360>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.Privilege360.getTitleList");
    }
}
