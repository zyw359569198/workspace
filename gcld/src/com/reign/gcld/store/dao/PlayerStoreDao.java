package com.reign.gcld.store.dao;

import com.reign.gcld.store.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerStoreDao")
public class PlayerStoreDao extends BaseDao<PlayerStore> implements IPlayerStoreDao
{
    @Override
	public PlayerStore read(final int playerId) {
        return (PlayerStore)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerStore.read", (Object)playerId);
    }
    
    @Override
	public PlayerStore readForUpdate(final int playerId) {
        return (PlayerStore)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerStore.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerStore> getModels() {
        return (List<PlayerStore>)this.getSqlSession().selectList("com.reign.gcld.store.domain.PlayerStore.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerStore.getModelSize");
    }
    
    @Override
	public int create(final PlayerStore playerStore) {
        return this.getSqlSession().insert("com.reign.gcld.store.domain.PlayerStore.create", playerStore);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.store.domain.PlayerStore.deleteById", playerId);
    }
    
    @Override
	public void updatePlayerStore(final PlayerStore playerStore) {
        this.getSqlSession().update("com.reign.gcld.store.domain.PlayerStore.updatePlayerStore", playerStore);
    }
    
    @Override
	public void updateLockId(final int playerId, final String itemId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("itemId", itemId);
        this.getSqlSession().update("com.reign.gcld.store.domain.PlayerStore.updateLockId", params);
    }
    
    @Override
	public int updateUnrefreshEquip(final int playerId, final String updateString) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("unrefreshEquip", updateString);
        return this.getSqlSession().update("com.reign.gcld.store.domain.PlayerStore.updateUnrefreshEquip", params);
    }
}
