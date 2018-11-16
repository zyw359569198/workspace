package com.reign.gcld.mine.dao;

import com.reign.gcld.mine.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerMineDao")
public class PlayerMineDao extends BaseDao<PlayerMine> implements IPlayerMineDao
{
    @Override
	public PlayerMine read(final int vId) {
        return (PlayerMine)this.getSqlSession().selectOne("com.reign.gcld.mine.domain.PlayerMine.read", (Object)vId);
    }
    
    @Override
	public PlayerMine readForUpdate(final int vId) {
        return (PlayerMine)this.getSqlSession().selectOne("com.reign.gcld.mine.domain.PlayerMine.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerMine> getModels() {
        return (List<PlayerMine>)this.getSqlSession().selectList("com.reign.gcld.mine.domain.PlayerMine.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.mine.domain.PlayerMine.getModelSize");
    }
    
    @Override
	public int create(final PlayerMine playerMine) {
        return this.getSqlSession().insert("com.reign.gcld.mine.domain.PlayerMine.create", playerMine);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.mine.domain.PlayerMine.deleteById", vId);
    }
    
    @Override
	public PlayerMine getByOwner(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", type);
        return (PlayerMine)this.getSqlSession().selectOne("com.reign.gcld.mine.domain.PlayerMine.getByOwner", (Object)params);
    }
    
    @Override
	public Map<Integer, PlayerMine> getByPage(final int pageNo, final int type) {
        final Params params = new Params();
        params.addParam("pageNo", pageNo);
        params.addParam("type", type);
        return (Map<Integer, PlayerMine>)this.getSqlSession().selectMap("com.reign.gcld.mine.domain.PlayerMine.getByPage", (Object)params, "mineId");
    }
    
    @Override
	public PlayerMine getByMineId(final int mineId) {
        return (PlayerMine)this.getSqlSession().selectOne("com.reign.gcld.mine.domain.PlayerMine.getByMineId", (Object)mineId);
    }
    
    @Override
	public int updateMode(final int vId, final int mode, final Date date) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("mode", mode);
        params.addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.mine.domain.PlayerMine.updateMode", params);
    }
    
    @Override
	public int updateIsNew(final int vId, final int isNew) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("isNew", isNew);
        return this.getSqlSession().update("com.reign.gcld.mine.domain.PlayerMine.updateIsNew", params);
    }
}
