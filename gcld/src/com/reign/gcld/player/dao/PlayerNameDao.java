package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("playerNameDao")
public class PlayerNameDao extends BaseDao<PlayerName> implements IPlayerNameDao
{
    @Override
	public PlayerName read(final int vId) {
        return (PlayerName)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerName.read", (Object)vId);
    }
    
    @Override
	public PlayerName readForUpdate(final int vId) {
        return (PlayerName)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerName.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerName> getModels() {
        return (List<PlayerName>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerName.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerName.getModelSize");
    }
    
    @Override
	public int create(final PlayerName playerName) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.PlayerName.create", playerName);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerName.deleteById", vId);
    }
    
    @Override
	public List<String> getNameList(final int num) {
        return (List<String>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerName.getNameList", (Object)num);
    }
}
