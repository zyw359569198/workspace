package com.reign.gcld.pay.dao;

import com.reign.gcld.pay.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerVipDao")
public class PlayerVipDao extends BaseDao<PlayerVip> implements IPlayerVipDao
{
    @Override
	public PlayerVip read(final int vId) {
        return (PlayerVip)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVip.read", (Object)vId);
    }
    
    @Override
	public PlayerVip readForUpdate(final int vId) {
        return (PlayerVip)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVip.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerVip> getModels() {
        return (List<PlayerVip>)this.getSqlSession().selectList("com.reign.gcld.pay.domain.PlayerVip.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVip.getModelSize");
    }
    
    @Override
	public int create(final PlayerVip playerVip) {
        return this.getSqlSession().insert("com.reign.gcld.pay.domain.PlayerVip.create", playerVip);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.pay.domain.PlayerVip.deleteById", vId);
    }
    
    @Override
	public String getVipStatus(final int playerId) {
        return (String)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVip.getVipStatus", (Object)playerId);
    }
    
    @Override
	public int setVipStatus(final int playerId, final String vipStatus) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("vipStatus", vipStatus);
        return this.getSqlSession().update("com.reign.gcld.pay.domain.PlayerVip.setVipStatus", params);
    }
    
    @Override
	public String getVipRemainingTimes(final int playerId) {
        return (String)this.getSqlSession().selectOne("com.reign.gcld.pay.domain.PlayerVip.getVipRemainingTimes", (Object)playerId);
    }
    
    @Override
	public int setVipRemainingTimes(final int playerId, final String remainingTimes) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("remainingTimes", remainingTimes);
        return this.getSqlSession().update("com.reign.gcld.pay.domain.PlayerVip.setVipRemainingTimes", params);
    }
}
