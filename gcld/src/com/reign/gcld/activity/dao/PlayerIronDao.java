package com.reign.gcld.activity.dao;

import com.reign.gcld.activity.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerIronDao")
public class PlayerIronDao extends BaseDao<PlayerIron> implements IPlayerIronDao
{
    @Override
	public PlayerIron read(final int playerId) {
        return (PlayerIron)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerIron.read", (Object)playerId);
    }
    
    @Override
	public PlayerIron readForUpdate(final int playerId) {
        return (PlayerIron)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerIron.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerIron> getModels() {
        return (List<PlayerIron>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.PlayerIron.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerIron.getModelSize");
    }
    
    @Override
	public int create(final PlayerIron playerIron) {
        return this.getSqlSession().insert("com.reign.gcld.activity.domain.PlayerIron.create", playerIron);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.PlayerIron.deleteById", playerId);
    }
    
    @Override
	public List<PlayerIron> getReceivedList() {
        return (List<PlayerIron>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.PlayerIron.getReceivedList");
    }
    
    @Override
	public int clearAll() {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerIron.clearAll");
    }
    
    @Override
	public int getIron(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerIron.getIron");
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int addIron(final int playerId, final int iron) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("iron", iron);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerIron.addIron", params);
    }
    
    @Override
	public int updateReward(final int playerId, final int reward) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("reward", reward);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerIron.updateReward", params);
    }
    
    @Override
	public int useIron(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerIron.useIron", playerId);
    }
}
