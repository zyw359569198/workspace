package com.reign.gcld.gift.dao;

import com.reign.gcld.gift.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("giftInfoDao")
public class GiftInfoDao extends BaseDao<GiftInfo> implements IGiftInfoDao
{
    @Override
	public GiftInfo read(final int id) {
        return (GiftInfo)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.GiftInfo.read", (Object)id);
    }
    
    @Override
	public GiftInfo readForUpdate(final int id) {
        return (GiftInfo)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.GiftInfo.readForUpdate", (Object)id);
    }
    
    @Override
	public List<GiftInfo> getModels() {
        return (List<GiftInfo>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.GiftInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.GiftInfo.getModelSize");
    }
    
    @Override
	public int create(final GiftInfo giftInfo) {
        return this.getSqlSession().insert("com.reign.gcld.gift.domain.GiftInfo.create", giftInfo);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.gift.domain.GiftInfo.deleteById", id);
    }
    
    @Override
	public int getAllServerNum(final String yx, final Date now, final Date playerDate) {
        final Params params = new Params();
        params.addParam("yx", yx);
        params.addParam("now", now);
        params.addParam("playerDate", playerDate);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.GiftInfo.getAllServerNum", (Object)params);
    }
    
    @Override
	public int deleteByDate(final Date date) {
        return this.getSqlSession().delete("com.reign.gcld.gift.domain.GiftInfo.deleteByDate", date);
    }
    
    @Override
	public List<GiftInfo> getByDate(final Date date) {
        return (List<GiftInfo>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.GiftInfo.getByDate", (Object)date);
    }
    
    @Override
	public List<GiftInfo> getByYx(final String yx) {
        return (List<GiftInfo>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.GiftInfo.getByYx", (Object)yx);
    }
}
