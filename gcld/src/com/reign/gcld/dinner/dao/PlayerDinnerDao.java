package com.reign.gcld.dinner.dao;

import com.reign.gcld.dinner.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Component("playerDinnerDao")
public class PlayerDinnerDao extends BaseDao<PlayerDinner> implements IPlayerDinnerDao
{
    @Override
	public PlayerDinner read(final int playerId) {
        return (PlayerDinner)this.getSqlSession().selectOne("com.reign.gcld.dinner.domain.PlayerDinner.read", (Object)playerId);
    }
    
    @Override
	public PlayerDinner readForUpdate(final int playerId) {
        return (PlayerDinner)this.getSqlSession().selectOne("com.reign.gcld.dinner.domain.PlayerDinner.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerDinner> getModels() {
        return (List<PlayerDinner>)this.getSqlSession().selectList("com.reign.gcld.dinner.domain.PlayerDinner.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.dinner.domain.PlayerDinner.getModelSize");
    }
    
    @Override
	public int create(final PlayerDinner playerDinner) {
        return this.getSqlSession().insert("com.reign.gcld.dinner.domain.PlayerDinner.create", playerDinner);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.dinner.domain.PlayerDinner.deleteById", playerId);
    }
    
    @Override
	public void addDinnerNum(final int dinnerMaxNum) {
        this.getSqlSession().update("com.reign.gcld.dinner.domain.PlayerDinner.addDinnerNum", dinnerMaxNum);
    }
    
    @Override
	public int consumeDinnerNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.dinner.domain.PlayerDinner.consumeDinnerNum", playerId);
    }
    
    @Override
	public void rewardDinnerNum(final int playerId, final int addNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("addNum", addNum);
        params.addParam("max", 6);
        this.getSqlSession().update("com.reign.gcld.dinner.domain.PlayerDinner.rewardDinnerNum", params);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("dinnerNum", this.getDinnerNum(playerId)));
    }
    
    @Override
	public int getDinnerNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.dinner.domain.PlayerDinner.getDinnerNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
}
