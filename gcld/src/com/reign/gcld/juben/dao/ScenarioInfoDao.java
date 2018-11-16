package com.reign.gcld.juben.dao;

import com.reign.gcld.juben.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("scenarioInfoDao")
public class ScenarioInfoDao extends BaseDao<ScenarioInfo> implements IScenarioInfoDao
{
    @Override
	public ScenarioInfo read(final int vId) {
        return (ScenarioInfo)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.ScenarioInfo.read", (Object)vId);
    }
    
    @Override
	public ScenarioInfo readForUpdate(final int vId) {
        return (ScenarioInfo)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.ScenarioInfo.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<ScenarioInfo> getModels() {
        return (List<ScenarioInfo>)this.getSqlSession().selectList("com.reign.gcld.juben.domain.ScenarioInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.ScenarioInfo.getModelSize");
    }
    
    @Override
	public int create(final ScenarioInfo scenarioInfo) {
        return this.getSqlSession().insert("com.reign.gcld.juben.domain.ScenarioInfo.create", scenarioInfo);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.juben.domain.ScenarioInfo.deleteById", vId);
    }
    
    @Override
	public int updateMinTime(final int soloId, final int grade, final int minTime, final int playerId, final int forceId, final String playerName) {
        final Params params = new Params();
        params.addParam("soloId", soloId).addParam("grade", grade).addParam("minTime", minTime).addParam("playerId", playerId).addParam("forceId", forceId).addParam("playerName", playerName);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.ScenarioInfo.updateMinTime", params);
    }
}
