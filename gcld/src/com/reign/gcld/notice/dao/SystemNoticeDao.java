package com.reign.gcld.notice.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.notice.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("systemNoticeDao")
public class SystemNoticeDao extends BaseDao<SystemNotice> implements ISystemNoticeDao
{
    @Override
	public SystemNotice read(final int id) {
        return (SystemNotice)this.getSqlSession().selectOne("com.reign.gcld.notice.domain.SystemNotice.read", (Object)id);
    }
    
    @Override
	public SystemNotice readForUpdate(final int id) {
        return (SystemNotice)this.getSqlSession().selectOne("com.reign.gcld.notice.domain.SystemNotice.readForUpdate", (Object)id);
    }
    
    @Override
	public List<SystemNotice> getModels() {
        return (List<SystemNotice>)this.getSqlSession().selectList("com.reign.gcld.notice.domain.SystemNotice.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.notice.domain.SystemNotice.getModelSize");
    }
    
    @Override
	public int create(final SystemNotice systemNotice) {
        return this.getSqlSession().insert("com.reign.gcld.notice.domain.SystemNotice.create", systemNotice);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.notice.domain.SystemNotice.deleteById", id);
    }
    
    @Override
	public void update(final SystemNotice systemNotice) {
        this.getSqlSession().update("com.reign.gcld.notice.domain.SystemNotice.update", systemNotice);
    }
    
    @Override
	public String getYxById(final int id) {
        return (String)this.getSqlSession().selectOne("com.reign.gcld.notice.domain.SystemNotice.getYxById", (Object)id);
    }
}
