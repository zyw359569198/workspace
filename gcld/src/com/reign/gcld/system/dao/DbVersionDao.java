package com.reign.gcld.system.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.system.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("dbVersionDao")
public class DbVersionDao extends BaseDao<DbVersion> implements IDbVersionDao
{
    @Override
	public DbVersion read(final int dbVersion) {
        return (DbVersion)this.getSqlSession().selectOne("com.reign.gcld.system.domain.DbVersion.read", (Object)dbVersion);
    }
    
    @Override
	public DbVersion readForUpdate(final int dbVersion) {
        return (DbVersion)this.getSqlSession().selectOne("com.reign.gcld.system.domain.DbVersion.readForUpdate", (Object)dbVersion);
    }
    
    @Override
	public List<DbVersion> getModels() {
        return (List<DbVersion>)this.getSqlSession().selectList("com.reign.gcld.system.domain.DbVersion.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.system.domain.DbVersion.getModelSize");
    }
    
    @Override
	public int create(final DbVersion dbVersion) {
        return this.getSqlSession().insert("com.reign.gcld.system.domain.DbVersion.create", dbVersion);
    }
    
    @Override
	public int deleteById(final int dbVersion) {
        return this.getSqlSession().delete("com.reign.gcld.system.domain.DbVersion.deleteById", dbVersion);
    }
    
    @Override
	public int updateServerTime(final Date date) {
        return this.getSqlSession().update("com.reign.gcld.system.domain.DbVersion.updateServerTime", date);
    }
    
    @Override
	public int getDiffDay() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.system.domain.DbVersion.getDiffDay");
    }
    
    @Override
	public int updateSeasonId(final int seasonId) {
        return this.getSqlSession().update("com.reign.gcld.system.domain.DbVersion.updateSeasonId", seasonId);
    }
}
