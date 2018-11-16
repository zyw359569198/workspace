package com.reign.gcld.gift.dao;

import com.reign.gcld.gift.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerOnlineRewardDao")
public class PlayerOnlineRewardDao extends BaseDao<PlayerOnlineReward> implements IPlayerOnlineRewardDao
{
    @Override
	public PlayerOnlineReward read(final int playerId) {
        return (PlayerOnlineReward)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerOnlineReward.read", (Object)playerId);
    }
    
    @Override
	public PlayerOnlineReward readForUpdate(final int playerId) {
        return (PlayerOnlineReward)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerOnlineReward.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerOnlineReward> getModels() {
        return (List<PlayerOnlineReward>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.PlayerOnlineReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerOnlineReward.getModelSize");
    }
    
    @Override
	public int create(final PlayerOnlineReward playerOnlineReward) {
        return this.getSqlSession().insert("com.reign.gcld.gift.domain.PlayerOnlineReward.create", playerOnlineReward);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.gift.domain.PlayerOnlineReward.deleteById", playerId);
    }
    
    @Override
	public int getOnlineNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerOnlineReward.getOnlineNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int useOnlineNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.gift.domain.PlayerOnlineReward.useOnlineNum", playerId);
    }
    
    @Override
	public int addOnlineNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.gift.domain.PlayerOnlineReward.addOnlineNum", playerId);
    }
    
    @Override
	public int setOnlineGiftBaseData(final int playerId, final int remainOnlineNum, final int onlineNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("remainOnlineNum", remainOnlineNum).addParam("onlineNum", onlineNum);
        return this.getSqlSession().update("com.reign.gcld.gift.domain.PlayerOnlineReward.setOnlineGiftBaseData", params);
    }
    
    @Override
	public int resetOnlineGiftData(final int remainOnlineNum) {
        return this.getSqlSession().update("com.reign.gcld.gift.domain.PlayerOnlineReward.resetOnlineGiftData", remainOnlineNum);
    }
}
