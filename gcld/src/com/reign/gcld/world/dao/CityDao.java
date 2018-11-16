package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.world.common.*;

@Component("cityDao")
public class CityDao extends BaseDao<City> implements ICityDao
{
    @Override
	public City read(final int id) {
        return (City)this.getSqlSession().selectOne("com.reign.gcld.world.domain.City.read", (Object)id);
    }
    
    @Override
	public City readForUpdate(final int id) {
        return (City)this.getSqlSession().selectOne("com.reign.gcld.world.domain.City.readForUpdate", (Object)id);
    }
    
    @Override
	public List<City> getModels() {
        return (List<City>)this.getSqlSession().selectList("com.reign.gcld.world.domain.City.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.City.getModelSize");
    }
    
    @Override
	public int create(final City city) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.City.create", city);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.City.deleteById", id);
    }
    
    @Override
	public int updateTitle(final int id, final int title) {
        final Params params = new Params();
        params.addParam("id", id).addParam("title", title);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.updateTitle", params);
        CityDataCache.cityArray[id] = this.read(id);
        if (title > 0) {
            CityEventManager.getInstance().removeCityEventAfterBesieged(id);
        }
        return res;
    }
    
    @Override
	public int updateForceIdState(final int id, final int forceId, final int state) {
        final Params params = new Params();
        params.addParam("id", id).addParam("forceId", forceId).addParam("state", state);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.updateForceIdState", params);
        CityDataCache.cityArray[id] = this.read(id);
        return res;
    }
    
    @Override
	public int updateState(final int id, final int state) {
        final Params params = new Params();
        params.addParam("id", id).addParam("state", state);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.updateState", params);
        CityDataCache.cityArray[id] = this.read(id);
        return res;
    }
    
    @Override
	public int addGNum(final int id, final int num) {
        final Params params = new Params();
        params.addParam("id", id).addParam("num", num);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.addGNum", params);
        CityDataCache.cityArray[id] = this.read(id);
        return res;
    }
    
    @Override
	public int reduceGNum(final int id, final int num) {
        final Params params = new Params();
        params.addParam("id", id).addParam("num", num);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.reduceGNum", params);
        CityDataCache.cityArray[id] = this.read(id);
        return res;
    }
    
    @Override
	public List<City> getForceCities(final int forceId) {
        return (List<City>)this.getSqlSession().selectList("com.reign.gcld.world.domain.City.getForceCities", (Object)forceId);
    }
    
    @Override
	public int getForceCounts(final int forceId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.City.getForceCounts", (Object)forceId);
    }
    
    @Override
	public int updateTrickInfo(final int id, final String newtrickInfo) {
        final Params params = new Params();
        params.addParam("trickinfo", newtrickInfo).addParam("id", id);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.updateTrickInfo", params);
        CityDataCache.cityArray[id] = this.read(id);
        return res;
    }
    
    @Override
	public int updateBorder(final int id, final int border) {
        final Params params = new Params();
        params.addParam("id", id).addParam("border", border);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.updateBorder", params);
        CityDataCache.cityArray[id] = this.read(id);
        return res;
    }
    
    @Override
	public int initUpdateBorder(final int id, final int border) {
        final Params params = new Params();
        params.addParam("id", id).addParam("border", border);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.updateBorder", params);
        CityDataCache.cityArray[id] = this.read(id);
        return res;
    }
    
    @Override
	public void updateJobId(final Integer id, final int jobId) {
        final Params params = new Params();
        params.addParam("cityId", id);
        params.addParam("jobId", jobId);
        this.getSqlSession().update("com.reign.gcld.world.domain.City.updateJobId", params);
        CityDataCache.cityArray[id] = this.read(id);
    }
    
    @Override
	public int resetBorder() {
        return this.getSqlSession().update("com.reign.gcld.world.domain.City.resetBorder");
    }
    
    @Override
	public int resetTitle() {
        return this.getSqlSession().update("com.reign.gcld.world.domain.City.resetTitle");
    }
    
    @Override
	public void updateHp(final int cityId, final int hp) {
        final Params params = new Params();
        params.addParam("cityId", cityId);
        params.addParam("hp", hp);
        this.getSqlSession().update("com.reign.gcld.world.domain.City.updateHp", params);
        CityDataCache.cityArray[cityId] = this.read(cityId);
    }
    
    @Override
	public void updateHpMaxHp(final int cityId, final int hp, final int hpMax) {
        final Params params = new Params();
        params.addParam("cityId", cityId);
        params.addParam("hp", hp);
        params.addParam("hpMax", hpMax);
        this.getSqlSession().update("com.reign.gcld.world.domain.City.updateHpMaxHp", params);
        CityDataCache.cityArray[cityId] = this.read(cityId);
    }
    
    @Override
	public List<Integer> getCityIdListByForceId(final int forceId) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.world.domain.City.getCityIdListByForceId", (Object)forceId);
    }
    
    @Override
	public int updateOtherInfo(final int cityId, final String otherInfo) {
        final Params params = new Params();
        params.addParam("cityId", cityId);
        params.addParam("otherInfo", otherInfo);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.updateOtherInfo", params);
        CityDataCache.cityArray[cityId] = this.read(cityId);
        return res;
    }
    
    @Override
	public int updateForceIdStateTitleBorder(final int id, final int forceId, final int state, final int title, final int border) {
        final Params params = new Params();
        params.addParam("cityId", id).addParam("forceId", forceId).addParam("state", state).addParam("title", title).addParam("border", border);
        final int res = this.getSqlSession().update("com.reign.gcld.world.domain.City.updateForceIdStateTitleBorder", params);
        CityDataCache.cityArray[id] = this.read(id);
        return res;
    }
}
