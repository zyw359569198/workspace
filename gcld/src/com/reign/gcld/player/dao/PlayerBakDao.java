package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("playerBakDao")
public class PlayerBakDao extends BaseDao<PlayerBak> implements IPlayerBakDao
{
    @Override
	public PlayerBak read(final int playerId) {
        return (PlayerBak)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerBak.read", (Object)playerId);
    }
    
    @Override
	public PlayerBak readForUpdate(final int playerId) {
        return (PlayerBak)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerBak.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerBak> getModels() {
        return (List<PlayerBak>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerBak.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerBak.getModelSize");
    }
    
    @Override
	public int create(final PlayerBak playerBak) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.PlayerBak.create", playerBak);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerBak.deleteById", playerId);
    }
}
