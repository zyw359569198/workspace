package com.reign.gcld.grouparmy.dao;

import com.reign.gcld.grouparmy.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerGroupArmyDao")
public class PlayerGroupArmyDao extends BaseDao<PlayerGroupArmy> implements IPlayerGroupArmyDao
{
    @Override
	public PlayerGroupArmy read(final int vId) {
        return (PlayerGroupArmy)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.read", (Object)vId);
    }
    
    @Override
	public PlayerGroupArmy readForUpdate(final int vId) {
        return (PlayerGroupArmy)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerGroupArmy> getModels() {
        return (List<PlayerGroupArmy>)this.getSqlSession().selectList("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.getModelSize");
    }
    
    @Override
	public int create(final PlayerGroupArmy playerGroupArmy) {
        return this.getSqlSession().insert("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.create", playerGroupArmy);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.deleteById", vId);
    }
    
    @Override
	public PlayerGroupArmy getPlayerGroupArmy(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return (PlayerGroupArmy)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.getPlayerGroupArmy", (Object)params);
    }
    
    @Override
	public List<PlayerGroupArmy> getList(final int armyId) {
        return (List<PlayerGroupArmy>)this.getSqlSession().selectList("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.getList", (Object)armyId);
    }
    
    @Override
	public void updateIsLeader(final int vId, final int isLeader) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("isLeader", isLeader);
        this.getSqlSession().update("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.updateIsLeader", params);
    }
    
    @Override
	public Map<Integer, PlayerGroupArmy> getGroupArmies(final int playerId) {
        return (Map<Integer, PlayerGroupArmy>)this.getSqlSession().selectMap("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.getGroupArmies", (Object)playerId, "generalId");
    }
    
    @Override
	public int deleteByArmyId(final int armyId) {
        return this.getSqlSession().delete("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.deleteByArmyId", armyId);
    }
    
    @Override
	public int updateArmyId(final int oldArmyId, final int newArmyId) {
        final Params params = new Params();
        params.addParam("oldArmyId", oldArmyId);
        params.addParam("newArmyId", newArmyId);
        return this.getSqlSession().update("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.updateArmyId", params);
    }
    
    @Override
	public int deleteByPlayerId(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.deleteByPlayerId", playerId);
    }
    
    @Override
	public int getCountByArmyId(final int armyId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.grouparmy.domain.PlayerGroupArmy.getCountByArmyId", (Object)armyId);
    }
}
