package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("countryRewardPerHourDao")
public class CountryRewardPerHourDao extends BaseDao<CountryRewardPerHour> implements ICountryRewardPerHourDao
{
    @Override
	public CountryRewardPerHour read(final int forceId) {
        return (CountryRewardPerHour)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CountryRewardPerHour.read", (Object)forceId);
    }
    
    @Override
	public CountryRewardPerHour readForUpdate(final int forceId) {
        return (CountryRewardPerHour)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CountryRewardPerHour.readForUpdate", (Object)forceId);
    }
    
    @Override
	public List<CountryRewardPerHour> getModels() {
        return (List<CountryRewardPerHour>)this.getSqlSession().selectList("com.reign.gcld.world.domain.CountryRewardPerHour.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CountryRewardPerHour.getModelSize");
    }
    
    @Override
	public int create(final CountryRewardPerHour countryRewardPerHour) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.CountryRewardPerHour.create", countryRewardPerHour);
    }
    
    @Override
	public int deleteById(final int forceId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.CountryRewardPerHour.deleteById", forceId);
    }
    
    @Override
	public CountryRewardPerHour getByHourAndForceId(final int hour, final int forceId) {
        final Params params = new Params();
        params.addParam("hour", hour).addParam("forceId", forceId);
        return (CountryRewardPerHour)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CountryRewardPerHour.getByHourAndForceId", (Object)params);
    }
    
    @Override
	public int UpdateByHourAndForceId(final String rewards, final int hour, final int forceId) {
        final Params params = new Params();
        params.addParam("hour", hour).addParam("rewards", rewards).addParam("forceId", forceId);
        return this.getSqlSession().update("com.reign.gcld.world.domain.CountryRewardPerHour.UpdateByHourAndForceId", params);
    }
}
