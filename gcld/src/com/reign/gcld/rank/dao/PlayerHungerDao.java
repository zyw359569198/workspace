package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("playerHungerDao")
public class PlayerHungerDao extends BaseDao<PlayerHunger> implements IPlayerHungerDao
{
    @Override
	public PlayerHunger read(final int playerId) {
        return (PlayerHunger)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerHunger.read", (Object)playerId);
    }
    
    @Override
	public PlayerHunger readForUpdate(final int playerId) {
        return (PlayerHunger)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerHunger.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerHunger> getModels() {
        return (List<PlayerHunger>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerHunger.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerHunger.getModelSize");
    }
    
    @Override
	public int create(final PlayerHunger playerHunger) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.PlayerHunger.create", playerHunger);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerHunger.deleteById", playerId);
    }
    
    @Override
	public int batchUpdate(final List<PlayerHunger> toUpdate) {
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerHunger.batchUpdate", toUpdate);
    }
    
    @Override
	public int batchCreate(final List<PlayerHunger> toCreate) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.PlayerHunger.batchCreate", toCreate);
    }
}
