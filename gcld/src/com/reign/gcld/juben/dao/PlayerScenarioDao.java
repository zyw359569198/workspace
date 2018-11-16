package com.reign.gcld.juben.dao;

import com.reign.gcld.juben.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerScenarioDao")
public class PlayerScenarioDao extends BaseDao<PlayerScenario> implements IPlayerScenarioDao
{
    @Override
	public PlayerScenario read(final int vId) {
        return (PlayerScenario)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.PlayerScenario.read", (Object)vId);
    }
    
    @Override
	public PlayerScenario readForUpdate(final int vId) {
        return (PlayerScenario)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.PlayerScenario.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerScenario> getModels() {
        return (List<PlayerScenario>)this.getSqlSession().selectList("com.reign.gcld.juben.domain.PlayerScenario.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.PlayerScenario.getModelSize");
    }
    
    @Override
	public int create(final PlayerScenario playerScenario) {
        return this.getSqlSession().insert("com.reign.gcld.juben.domain.PlayerScenario.create", playerScenario);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.juben.domain.PlayerScenario.deleteById", vId);
    }
    
    @Override
	public List<PlayerScenario> getScenarioByPid(final int playerId) {
        return (List<PlayerScenario>)this.getSqlSession().selectList("com.reign.gcld.juben.domain.PlayerScenario.getScenarioByPid", (Object)playerId);
    }
    
    @Override
	public PlayerScenario getScenarioByPidSid(final int playerId, final int scenarioId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", scenarioId);
        return (PlayerScenario)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.PlayerScenario.getScenarioByPidSid", (Object)params);
    }
    
    @Override
	public int updateInit(final int playerId, final int soloId, final int grade, final Date starttime, final Date endtime, final int curstar, final int jieBingCount) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("grade", grade).addParam("starttime", starttime).addParam("endtime", endtime).addParam("curstar", curstar).addParam("jieBingCount", jieBingCount);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateInit", params);
    }
    
    @Override
	public int updateState(final int playerId, final int soloId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateState", params);
    }
    
    @Override
	public int updateStateOverTime(final int playerId, final int soloId, final int state, final long overTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("state", state).addParam("overTime", overTime);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateStateOverTime", params);
    }
    
    @Override
	public int updateStar(final int playerId, final int soloId, final int curstar, final Date endtime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("curstar", curstar).addParam("endtime", endtime);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateStar", params);
    }
    
    @Override
	public List<PlayerScenario> getListByState() {
        return (List<PlayerScenario>)this.getSqlSession().selectList("com.reign.gcld.juben.domain.PlayerScenario.getListByState");
    }
    
    @Override
	public void updateScenarioInfo(final int playerId, final int soleId, final String string) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("info", string);
        params.addParam("scenarioId", soleId);
        this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateScenarioInfo", params);
    }
    
    @Override
	public void updateRewardStarLv(final int playerId, final int soleId, final String openStarLv, final int rewarded) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("starlv", openStarLv);
        params.addParam("scenarioId", soleId);
        params.addParam("rewarded", rewarded);
        this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateRewardStarLv", params);
    }
    
    @Override
	public int updateEndTime(final int playerId, final int soloId, final Date endtime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("endtime", endtime);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateEndTime", params);
    }
    
    @Override
	public int updateEndTimeCurStar(final int playerId, final int soloId, final int curstar, final Date endtime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("endtime", endtime).addParam("curstar", curstar);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateEndTimeCurStar", params);
    }
    
    @Override
	public int updateJieBingCount(final int playerId, final int soloId, final int count) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("scenarioId", soloId).addParam("count", count);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateJieBingCount", params);
    }
    
    @Override
	public String getDramaTimes(final Object playerId, final Object soloId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("soloId", soloId);
        return (String)this.getSqlSession().selectOne("com.reign.gcld.juben.domain.PlayerScenario.getDramaTimes", (Object)params);
    }
    
    @Override
	public int updateDramaTimes(final Object playerId, final Object soloId, final String value) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("soloId", soloId).addParam("value", value);
        return this.getSqlSession().update("com.reign.gcld.juben.domain.PlayerScenario.updateDramaTimes", params);
    }
}
