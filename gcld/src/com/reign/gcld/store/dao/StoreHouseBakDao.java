package com.reign.gcld.store.dao;

import com.reign.gcld.store.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("storeHouseBakDao")
public class StoreHouseBakDao extends BaseDao<StoreHouseBak> implements IStoreHouseBakDao
{
    @Override
	public StoreHouseBak read(final int vId) {
        return (StoreHouseBak)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouseBak.read", (Object)vId);
    }
    
    @Override
	public StoreHouseBak readForUpdate(final int vId) {
        return (StoreHouseBak)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouseBak.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<StoreHouseBak> getModels() {
        return (List<StoreHouseBak>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouseBak.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouseBak.getModelSize");
    }
    
    @Override
	public int create(final StoreHouseBak storeHouseBak) {
        return this.getSqlSession().insert("com.reign.gcld.store.domain.StoreHouseBak.create", storeHouseBak);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.store.domain.StoreHouseBak.deleteById", vId);
    }
    
    @Override
	public List<StoreHouseBak> getListByStoreId(final int vId) {
        return (List<StoreHouseBak>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouseBak.getListByStoreId", (Object)vId);
    }
    
    @Override
	public void demoutDelete(final int suitId) {
        this.getSqlSession().delete("com.reign.gcld.store.domain.StoreHouseBak.demoutDelete", suitId);
    }
    
    @Override
	public void changeSuitId(final int suitVid, final int playerId, final int suitId, final int index) {
        final Params params = new Params();
        params.addParam("suitVid", suitVid);
        params.addParam("playerId", playerId);
        params.addParam("suitId", suitId);
        params.addParam("index", index);
        this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouseBak.changeSuitId", params);
    }
    
    @Override
	public void changeBackJunior(final int suitId, final int vId, final int index, final int playerId) {
        final Params params = new Params();
        params.addParam("suitId", suitId);
        params.addParam("vId", vId);
        params.addParam("index", index);
        params.addParam("playerId", playerId);
        this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouseBak.changeBackJunior", params);
    }
    
    @Override
	public List<StoreHouseBak> getBySuitIdAndIndex(final int vId, final int index) {
        final Params params = new Params();
        params.addParam("suitId", vId);
        params.addParam("index", index);
        return (List<StoreHouseBak>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouseBak.getBySuitIdAndIndex", (Object)params);
    }
    
    @Override
	public int deleteByPlayerId(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.store.domain.StoreHouseBak.deleteByPlayerId", playerId);
    }
}
