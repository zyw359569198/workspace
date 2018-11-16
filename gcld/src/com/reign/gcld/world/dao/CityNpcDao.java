package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("cityNpcDao")
public class CityNpcDao extends BaseDao<CityNpc> implements ICityNpcDao
{
    @Override
	public CityNpc read(final int vId) {
        return (CityNpc)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CityNpc.read", (Object)vId);
    }
    
    @Override
	public CityNpc readForUpdate(final int vId) {
        return (CityNpc)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CityNpc.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<CityNpc> getModels() {
        return (List<CityNpc>)this.getSqlSession().selectList("com.reign.gcld.world.domain.CityNpc.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CityNpc.getModelSize");
    }
    
    @Override
	public int create(final CityNpc cityNpc) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.CityNpc.create", cityNpc);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.CityNpc.deleteById", vId);
    }
    
    @Override
	public List<CityNpc> getByCityId(final int cityId) {
        return (List<CityNpc>)this.getSqlSession().selectList("com.reign.gcld.world.domain.CityNpc.getByCityId", (Object)cityId);
    }
    
    @Override
	public int getSizeByCityId(final int cityId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.CityNpc.getSizeByCityId", (Object)cityId);
    }
    
    @Override
	public List<CityNpc> getByForceId(final int forceId) {
        return (List<CityNpc>)this.getSqlSession().selectList("com.reign.gcld.world.domain.CityNpc.getByForceId", (Object)forceId);
    }
    
    @Override
	public int update(final CityNpc cityNpc) {
        return this.getSqlSession().update("com.reign.gcld.world.domain.CityNpc.update", cityNpc);
    }
    
    @Override
	public int reduceHp(final int hp) {
        return this.getSqlSession().update("com.reign.gcld.world.domain.CityNpc.reduceHp", hp);
    }
    
    @Override
	public int updateHpById(final Integer id, final int hp) {
        final Params params = new Params();
        params.addParam("id", id);
        params.addParam("hp", hp);
        return this.getSqlSession().update("com.reign.gcld.world.domain.CityNpc.updateHpById", params);
    }
    
    @Override
	public int delByCityId(final int cityId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.CityNpc.delByCityId", cityId);
    }
    
    @Override
	public int updateArmyHp(final int vId) {
        return this.getSqlSession().update("com.reign.gcld.world.domain.CityNpc.updateArmyHp", vId);
    }
}
