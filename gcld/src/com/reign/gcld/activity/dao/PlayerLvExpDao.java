package com.reign.gcld.activity.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.activity.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("playerLvExpDao")
public class PlayerLvExpDao extends BaseDao<PlayerLvExp> implements IPlayerLvExpDao
{
    @Override
	public PlayerLvExp read(final int playerId) {
        return (PlayerLvExp)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerLvExp.read", (Object)playerId);
    }
    
    @Override
	public PlayerLvExp readForUpdate(final int playerId) {
        return (PlayerLvExp)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerLvExp.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerLvExp> getModels() {
        return (List<PlayerLvExp>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.PlayerLvExp.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerLvExp.getModelSize");
    }
    
    @Override
	public int create(final PlayerLvExp playerLvExp) {
        return this.getSqlSession().insert("com.reign.gcld.activity.domain.PlayerLvExp.create", playerLvExp);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.PlayerLvExp.deleteById", playerId);
    }
    
    @Override
	public int deleteAll() {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.PlayerLvExp.deleteAll");
    }
    
    @Override
	public int updateReward(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerLvExp.updateReward", playerId);
    }
    
    @Override
	public int initActivity() {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerLvExp.initActivity");
    }
    
    @Override
	public int endActivity() {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerLvExp.endActivity");
    }
}
