package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("citiesPerHourDao")
public class CitiesPerHourDao extends BaseDao<CitiesPerHour> implements ICitiesPerHourDao
{
    @Override
	public CitiesPerHour read(final int hour) {
        return (CitiesPerHour)this.getSqlSession().selectOne("com.reign.gcld.worl.domain.CitiesPerHour.read", (Object)hour);
    }
    
    @Override
	public CitiesPerHour readForUpdate(final int hour) {
        return (CitiesPerHour)this.getSqlSession().selectOne("com.reign.gcld.worl.domain.CitiesPerHour.readForUpdate", (Object)hour);
    }
    
    @Override
	public List<CitiesPerHour> getModels() {
        return (List<CitiesPerHour>)this.getSqlSession().selectList("com.reign.gcld.worl.domain.CitiesPerHour.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.worl.domain.CitiesPerHour.getModelSize");
    }
    
    @Override
	public int create(final CitiesPerHour citiesPerHour) {
        return this.getSqlSession().insert("com.reign.gcld.worl.domain.CitiesPerHour.create", citiesPerHour);
    }
    
    @Override
	public int deleteById(final int hour) {
        return this.getSqlSession().delete("com.reign.gcld.worl.domain.CitiesPerHour.deleteById", hour);
    }
}
