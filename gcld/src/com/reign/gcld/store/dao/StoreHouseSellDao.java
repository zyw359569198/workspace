package com.reign.gcld.store.dao;

import com.reign.gcld.store.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("storeHouseSellDao")
public class StoreHouseSellDao extends BaseDao<StoreHouseSell> implements IStoreHouseSellDao
{
    @Override
	public StoreHouseSell read(final int vId) {
        return (StoreHouseSell)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouseSell.read", (Object)vId);
    }
    
    @Override
	public StoreHouseSell readForUpdate(final int vId) {
        return (StoreHouseSell)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouseSell.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<StoreHouseSell> getModels() {
        return (List<StoreHouseSell>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouseSell.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouseSell.getModelSize");
    }
    
    @Override
	public int create(final StoreHouseSell storeHouseSell) {
        return this.getSqlSession().insert("com.reign.gcld.store.domain.StoreHouseSell.create", storeHouseSell);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.store.domain.StoreHouseSell.deleteById", vId);
    }
    
    @Override
	public List<StoreHouseSell> getByPlayerId(final int playerId) {
        return (List<StoreHouseSell>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouseSell.getByPlayerId", (Object)playerId);
    }
    
    @Override
	public StoreHouseSell getByItemId(final int playerId, final int itemId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("itemId", itemId).addParam("type", type);
        return (StoreHouseSell)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouseSell.getByItemId", (Object)params);
    }
    
    @Override
	public int addNumByVid(final int vId, final int num) {
        final Params params = new Params();
        params.addParam("id", vId).addParam("addNum", num);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouseSell.addNumByVid", params);
    }
}
