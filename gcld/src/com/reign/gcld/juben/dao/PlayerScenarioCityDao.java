package com.reign.gcld.juben.dao;

import com.reign.gcld.juben.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.scenario.message.*;

@Component("playerScenarioCityDao")
public class PlayerScenarioCityDao extends BaseDao<PlayerScenarioCity> implements IPlayerScenarioCityDao
{
    @Override
	public PlayerScenarioCity read(final int vId) {
        return (PlayerScenarioCity)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.PlayerScenarioCity.read", (Object)vId);
    }
    
    @Override
	public PlayerScenarioCity readForUpdate(final int vId) {
        return (PlayerScenarioCity)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.PlayerScenarioCity.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerScenarioCity> getModels() {
        return (List<PlayerScenarioCity>)this.getSqlSession().selectList("com.reign.gcld.juben.domain.PlayerScenarioCity.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.PlayerScenarioCity.getModelSize");
    }
    
    @Override
	public int create(final PlayerScenarioCity playerScenarioCity) {
        return this.getSqlSession().insert("com.reign.gcld.juben.domain.PlayerScenarioCity.create", playerScenarioCity);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.juben.domain.PlayerScenarioCity.deleteById", vId);
    }
    
    @Override
	public List<PlayerScenarioCity> getSCityByPidSid(final int playerId, final int scenarioId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", scenarioId);
        return (List<PlayerScenarioCity>)this.getSqlSession().selectList("com.reign.gcld.juben.domain.PlayerScenarioCity.getSCityByPidSid", (Object)params);
    }
    
    @Override
	public int updateInit(final int playerId, final int soloId, final int cityId, final int state, final int forceId, final String trickinfo, final long updatetime, final int title, final int border, final String eventInfo) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("cityId", cityId).addParam("state", state).addParam("forceId", forceId).addParam("trickinfo", trickinfo).addParam("updatetime", updatetime).addParam("title", title).addParam("border", border).addParam("eventInfo", eventInfo);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenarioCity.updateInit", params);
    }
    
    @Override
	public int updateForceId(final int playerId, final int soloId, final int cityId, final int forceId, final int playerForceId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("cityId", cityId).addParam("forceId", forceId);
        if (forceId == playerForceId) {
            ScenarioEventMessageHelper.sendHoldCityScenarioMessage(playerId);
        }
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenarioCity.updateForceId", params);
    }
    
    @Override
	public int updateTrickInfo(final String newtrickInfo, final int playerId, final int jubenId, final int cityId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("scenarioId", jubenId);
        params.addParam("trickInfo", newtrickInfo);
        params.addParam("cityId", cityId);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenarioCity.updateTrickInfo", params);
    }
}
