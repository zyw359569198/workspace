package com.reign.gcld.team.dao;

import com.reign.gcld.team.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerTeamDao")
public class PlayerTeamDao extends BaseDao<PlayerTeam> implements IPlayerTeamDao
{
    @Override
	public PlayerTeam read(final int playerId) {
        return (PlayerTeam)this.getSqlSession().selectOne("com.reign.gcld.team.domain.PlayerTeam.read", (Object)playerId);
    }
    
    @Override
	public PlayerTeam readForUpdate(final int playerId) {
        return (PlayerTeam)this.getSqlSession().selectOne("com.reign.gcld.team.domain.PlayerTeam.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerTeam> getModels() {
        return (List<PlayerTeam>)this.getSqlSession().selectList("com.reign.gcld.team.domain.PlayerTeam.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.team.domain.PlayerTeam.getModelSize");
    }
    
    @Override
	public int create(final PlayerTeam playerTeam) {
        return this.getSqlSession().insert("com.reign.gcld.team.domain.PlayerTeam.create", playerTeam);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.team.domain.PlayerTeam.deleteById", playerId);
    }
    
    @Override
	public int updatePlayerGenrealIds(final int playerId, final String gpIds) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("gpIds", gpIds);
        return this.getSqlSession().update("com.reign.gcld.team.domain.PlayerTeam.updatePlayerGenrealIds", params);
    }
}
