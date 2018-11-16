package com.reign.gcld.blacksmith.dao;

import com.reign.gcld.blacksmith.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerBlacksmithDao")
public class PlayerBlacksmithDao extends BaseDao<PlayerBlacksmith> implements IPlayerBlacksmithDao
{
    @Override
	public PlayerBlacksmith read(final int vId) {
        return (PlayerBlacksmith)this.getSqlSession().selectOne("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.read", (Object)vId);
    }
    
    @Override
	public PlayerBlacksmith readForUpdate(final int vId) {
        return (PlayerBlacksmith)this.getSqlSession().selectOne("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerBlacksmith> getModels() {
        return (List<PlayerBlacksmith>)this.getSqlSession().selectList("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.getModelSize");
    }
    
    @Override
	public int create(final PlayerBlacksmith playerBlacksmith) {
        return this.getSqlSession().insert("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.create", playerBlacksmith);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.deleteById", vId);
    }
    
    @Override
	public List<PlayerBlacksmith> getListByPlayerId(final int playerId) {
        return (List<PlayerBlacksmith>)this.getSqlSession().selectList("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.getListByPlayerId", (Object)playerId);
    }
    
    @Override
	public int getSizeByPlayerId(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.getSizeByPlayerId", (Object)playerId);
    }
    
    @Override
	public PlayerBlacksmith getByPlayerIdAndSmithId(final int playerId, final int smithId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("smithId", smithId);
        return (PlayerBlacksmith)this.getSqlSession().selectOne("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.getByPlayerIdAndSmithId", (Object)params);
    }
    
    @Override
	public int addSmithNum(final int playerId, final int smithId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("smithId", smithId);
        return this.getSqlSession().update("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.addSmithNum", params);
    }
    
    @Override
	public int useSmithNum(final int playerId, final int smithId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("smithId", smithId);
        return this.getSqlSession().update("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.useSmithNum", params);
    }
    
    @Override
	public int addSmithLv(final int playerId, final int smithId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("smithId", smithId);
        return this.getSqlSession().update("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.addSmithLv", params);
    }
    
    @Override
	public List<Integer> getPlayerIdListBySmithId(final int smithId) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.getPlayerIdListBySmithId", (Object)smithId);
    }
    
    @Override
	public int resetSmithNum() {
        return this.getSqlSession().update("com.reign.gcld.blacksmith.domain.PlayerBlacksmith.resetSmithNum");
    }
}
