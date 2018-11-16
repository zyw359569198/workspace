package com.reign.gcld.pay.dao;

import com.reign.gcld.pay.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerPayDao")
public class PlayerPayDao extends BaseDao<PlayerPay> implements IPlayerPayDao
{
    @Override
	public PlayerPay read(final int vId) {
        return (PlayerPay)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerPay.read", (Object)vId);
    }
    
    @Override
	public PlayerPay readForUpdate(final int vId) {
        return (PlayerPay)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerPay.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerPay> getModels() {
        return (List<PlayerPay>)this.getSqlSession().selectList("com.reign.gcld.pay.domain.PlayerPay.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerPay.getModelSize");
    }
    
    @Override
	public int create(final PlayerPay playerPay) {
        return this.getSqlSession().insert("com.reign.gcld.pay.domain.PlayerPay.create", playerPay);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.pay.domain.PlayerPay.deleteById", vId);
    }
    
    @Override
	public List<PlayerPay> getPlayerPayByPlayerId(final int playerId) {
        return (List<PlayerPay>)this.getSqlSession().selectList("com.reign.gcld.pay.domain.PlayerPay.getPlayerPayByPlayerId", (Object)playerId);
    }
    
    @Override
	public int queryPaySum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerPay.queryPaySum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public boolean containsOrderId(final String orderId, final String yx) {
        final Params params = new Params();
        params.addParam("orderId", orderId);
        params.addParam("yx", yx);
        return !this.getSqlSession().selectList("com.reign.gcld.pay.domain.PlayerPay.getByOrderIdYx", (Object)params).isEmpty();
    }
    
    @Override
	public PlayerPay queryOrder(final String orderId, final String yx) {
        final Params params = new Params();
        params.addParam("orderId", orderId);
        params.addParam("yx", yx);
        return (PlayerPay)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerPay.queryOrder", (Object)params);
    }
    
    @Override
	public int getTotalGold(final Date startTime, final Date endTime, final String yx) {
        final Params params = new Params();
        params.addParam("startTime", startTime);
        params.addParam("endTime", endTime);
        params.addParam("yx", yx);
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerPay.getTotalGold", (Object)params);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int getPlayerCount(final Date startTime, final Date endTime, final String yx) {
        final Params params = new Params();
        params.addParam("startTime", startTime);
        params.addParam("endTime", endTime);
        params.addParam("yx", yx);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerPay.getPlayerCount", (Object)params);
    }
    
    @Override
	public int getOrderCount(final Date startTime, final Date endTime, final String yx) {
        final Params params = new Params();
        params.addParam("startTime", startTime);
        params.addParam("endTime", endTime);
        params.addParam("yx", yx);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerPay.getOrderCount", (Object)params);
    }
    
    @Override
	public List<PlayerPay> getPlayerPayByDateAndYx(final Date startTime, final Date endTime, final String yx) {
        final Params params = new Params();
        params.addParam("startTime", startTime);
        params.addParam("endTime", endTime);
        params.addParam("yx", yx);
        return (List<PlayerPay>)this.getSqlSession().selectList("com.reign.gcld.pay.domain.PlayerPay.getPlayerPayByDateAndYx", (Object)params);
    }
    
    @Override
	public List<PlayerPay> getPlayerPayByDateAndPage(final Date startTime, final Date endTime, final int begin, final int size, final String yx) {
        final Params params = new Params();
        params.addParam("startTime", startTime);
        params.addParam("endTime", endTime);
        params.addParam("begin", begin);
        params.addParam("size", size);
        params.addParam("yx", yx);
        return (List<PlayerPay>)this.getSqlSession().selectList("com.reign.gcld.pay.domain.PlayerPay.getPlayerPayByDateAndPage", (Object)params);
    }
}
