package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("cityNpcLostDao")
public class CityNpcLostDao extends BaseDao<CityNpcLost> implements ICityNpcLostDao
{
    @Override
	public CityNpcLost read(final int cityId) {
        return (CityNpcLost)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CityNpcLost.read", (Object)cityId);
    }
    
    @Override
	public CityNpcLost readForUpdate(final int cityId) {
        return (CityNpcLost)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CityNpcLost.readForUpdate", (Object)cityId);
    }
    
    @Override
	public List<CityNpcLost> getModels() {
        return (List<CityNpcLost>)this.getSqlSession().selectList("com.reign.gcld.world.domain.CityNpcLost.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CityNpcLost.getModelSize");
    }
    
    @Override
	public int create(final CityNpcLost cityNpcLost) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.CityNpcLost.create", cityNpcLost);
    }
    
    @Override
	public int deleteById(final int cityId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.CityNpcLost.deleteById", cityId);
    }
    
    @Override
	public int updateNpcLost(final int cityId, final String npcLost) {
        final Params params = new Params();
        params.addParam("cityId", cityId);
        params.addParam("npcLost", npcLost);
        return this.getSqlSession().update("com.reign.gcld.world.domain.CityNpcLost.updateNpcLost", params);
    }
}
