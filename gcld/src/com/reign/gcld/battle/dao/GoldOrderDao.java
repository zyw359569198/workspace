package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("goldOrderDao")
public class GoldOrderDao extends BaseDao<GoldOrder> implements IGoldOrderDao
{
    @Override
	public GoldOrder read(final int vId) {
        return (GoldOrder)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.GoldOrder.read", (Object)vId);
    }
    
    @Override
	public GoldOrder readForUpdate(final int vId) {
        return (GoldOrder)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.GoldOrder.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<GoldOrder> getModels() {
        return (List<GoldOrder>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.GoldOrder.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.GoldOrder.getModelSize");
    }
    
    @Override
	public int create(final GoldOrder goldOrder) {
        final GoldOrder GO = this.getByForceIdAndCityId(goldOrder.getForceId(), goldOrder.getCityId());
        if (GO != null) {
            this.deleteByForceIdAndCityId(goldOrder.getForceId(), goldOrder.getCityId());
        }
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.GoldOrder.create", goldOrder);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.GoldOrder.deleteById", vId);
    }
    
    @Override
	public int deleteByForceIdAndCityId(final int forceId, final int cityId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("cityId", cityId);
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.GoldOrder.deleteByForceIdAndCityId", params);
    }
    
    @Override
	public GoldOrder getByForceIdAndCityId(final int forceId, final int cityId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("cityId", cityId);
        return (GoldOrder)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.GoldOrder.getByForceIdAndCityId", (Object)params);
    }
    
    @Override
	public int updateNumByForceIdAndCityId(final int forceId, final int cityId, final int num) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("cityId", cityId);
        params.addParam("addNum", num);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.GoldOrder.updateNumByForceIdAndCityId", params);
    }
    
    @Override
	public int deleteByCityId(final int cityId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.GoldOrder.deleteByCityId", cityId);
    }
}
