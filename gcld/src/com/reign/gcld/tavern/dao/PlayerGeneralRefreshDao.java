package com.reign.gcld.tavern.dao;

import com.reign.gcld.tavern.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerGeneralRefreshDao")
public class PlayerGeneralRefreshDao extends BaseDao<PlayerGeneralRefresh> implements IPlayerGeneralRefreshDao
{
    @Override
	public PlayerGeneralRefresh read(final int vId) {
        return (PlayerGeneralRefresh)this.getSqlSession().selectOne("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.read", (Object)vId);
    }
    
    @Override
	public PlayerGeneralRefresh readForUpdate(final int vId) {
        return (PlayerGeneralRefresh)this.getSqlSession().selectOne("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerGeneralRefresh> getModels() {
        return (List<PlayerGeneralRefresh>)this.getSqlSession().selectList("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.getModelSize");
    }
    
    @Override
	public int create(final PlayerGeneralRefresh playerGeneralRefresh) {
        return this.getSqlSession().insert("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.create", playerGeneralRefresh);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.deleteById", vId);
    }
    
    @Override
	public List<PlayerGeneralRefresh> getListByPlayerId(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        final List<PlayerGeneralRefresh> resultList = (List<PlayerGeneralRefresh>)this.getSqlSession().selectList("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.getListByPlayerId", (Object)params);
        return resultList;
    }
    
    @Override
	public PlayerGeneralRefresh getPlayerGeneralRefresh(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return this.getSqlSession().selectList("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.getPlayerGeneralRefresh", (Object)params).get(0);
    }
    
    @Override
	public void lockGeneral(final int id) {
        final Params params = new Params();
        params.addParam("id", id);
        this.getSqlSession().update("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.lockGeneral", params);
    }
    
    @Override
	public void unlockGeneral(final int id, final Date unlockTime) {
        final Params params = new Params();
        params.addParam("id", id);
        params.addParam("unlockTime", unlockTime);
        this.getSqlSession().update("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.unlockGeneral", params);
    }
    
    @Override
	public void recruitGeneral(final int vId) {
        final Params params = new Params();
        params.addParam("vId", vId);
        this.getSqlSession().update("com.reign.gcld.tavern.domain.PlayerGeneralRefresh.recruitGeneral", params);
    }
}
