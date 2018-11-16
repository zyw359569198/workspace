package com.reign.gcld.store.dao;

import com.reign.gcld.store.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerItemRefreshDao")
public class PlayerItemRefreshDao extends BaseDao<PlayerItemRefresh> implements IPlayerItemRefreshDao
{
    @Override
	public PlayerItemRefresh read(final int vId) {
        return (PlayerItemRefresh)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerItemRefresh.read", (Object)vId);
    }
    
    @Override
	public PlayerItemRefresh readForUpdate(final int vId) {
        return (PlayerItemRefresh)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerItemRefresh.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerItemRefresh> getModels() {
        return (List<PlayerItemRefresh>)this.getSqlSession().selectList("com.reign.gcld.store.domain.PlayerItemRefresh.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerItemRefresh.getModelSize");
    }
    
    @Override
	public int create(final PlayerItemRefresh playerItemRefresh) {
        return this.getSqlSession().insert("com.reign.gcld.store.domain.PlayerItemRefresh.create", playerItemRefresh);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.store.domain.PlayerItemRefresh.deleteById", vId);
    }
    
    @Override
	public PlayerItemRefresh getPlayerItemRefresh(final int playerId, final int itemId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("itemId", itemId);
        final List<PlayerItemRefresh> list = (List<PlayerItemRefresh>)this.getSqlSession().selectList("com.reign.gcld.store.domain.PlayerItemRefresh.getPlayerItemRefresh", (Object)params);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }
    
    @Override
	public void lockItem(final int vId) {
        this.getSqlSession().update("com.reign.gcld.store.domain.PlayerItemRefresh.lockItem", vId);
    }
    
    @Override
	public void unlockItem(final int vId, final Date unlockTime) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("unlockTime", unlockTime);
        this.getSqlSession().update("com.reign.gcld.store.domain.PlayerItemRefresh.unlockItem", params);
    }
    
    @Override
	public List<PlayerItemRefresh> getListByPlayerId(final int playerId) {
        return (List<PlayerItemRefresh>)this.getSqlSession().selectList("com.reign.gcld.store.domain.PlayerItemRefresh.getListByPlayerId", (Object)playerId);
    }
    
    @Override
	public void buyItem(final int playerId, final int itemId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("itemId", itemId);
        this.getSqlSession().update("com.reign.gcld.store.domain.PlayerItemRefresh.buyItem", params);
    }
    
    @Override
	public void update(final PlayerItemRefresh pir) {
        this.getSqlSession().update("com.reign.gcld.store.domain.PlayerItemRefresh.update", pir);
    }
}
