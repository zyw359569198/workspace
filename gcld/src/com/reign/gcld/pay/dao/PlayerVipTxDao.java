package com.reign.gcld.pay.dao;

import com.reign.gcld.pay.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerVipTxDao")
public class PlayerVipTxDao extends BaseDao<PlayerVipTx> implements IPlayerVipTxDao
{
    @Override
	public PlayerVipTx read(final int vId) {
        return (PlayerVipTx)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVipTx.read", (Object)vId);
    }
    
    @Override
	public PlayerVipTx readForUpdate(final int vId) {
        return (PlayerVipTx)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVipTx.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerVipTx> getModels() {
        return (List<PlayerVipTx>)this.getSqlSession().selectList("com.reign.gcld.pay.domain.PlayerVipTx.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVipTx.getModelSize");
    }
    
    @Override
	public int create(final PlayerVipTx playerVipTx) {
        return this.getSqlSession().insert("com.reign.gcld.pay.domain.PlayerVipTx.create", playerVipTx);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.pay.domain.PlayerVipTx.deleteById", vId);
    }
    
    @Override
	public PlayerVipTx getByPlayerId(final int playerId) {
        return (PlayerVipTx)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVipTx.getByPlayerId", (Object)playerId);
    }
    
    @Override
	public int updateDailyStatus(final int playerId, final int status) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("status", status);
        return this.getSqlSession().update("com.reign.gcld.pay.domain.PlayerVipTx.updateDailyStatus", params);
    }
    
    @Override
	public int updateRookieStatus(final int playerId, final int status) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("status", status);
        return this.getSqlSession().update("com.reign.gcld.pay.domain.PlayerVipTx.updateRookieStatus", params);
    }
    
    @Override
	public int updateUgradeStatus(final int playerId, final int status, final int seq) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seq", seq).addParam("status", status);
        return this.getSqlSession().update("com.reign.gcld.pay.domain.PlayerVipTx.updateUgradeStatus", params);
    }
    
    @Override
	public int updateExtraStatus(final int playerId, final int status) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("status", status);
        return this.getSqlSession().update("com.reign.gcld.pay.domain.PlayerVipTx.updateExtraStatus", params);
    }
    
    @Override
	public int resetDailyStatus() {
        return this.getSqlSession().update("com.reign.gcld.pay.domain.PlayerVipTx.resetDailyStatus");
    }
}
