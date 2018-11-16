package com.reign.gcld.courtesy.dao;

import com.reign.gcld.courtesy.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerLiYiDao")
public class PlayerLiYiDao extends BaseDao<PlayerLiYi> implements IPlayerLiYiDao
{
    @Override
	public PlayerLiYi read(final int playerId) {
        return (PlayerLiYi)this.getSqlSession().selectOne("com.reign.gcld.courtesy.domain.PlayerLiYi.read", (Object)playerId);
    }
    
    @Override
	public PlayerLiYi readForUpdate(final int playerId) {
        return (PlayerLiYi)this.getSqlSession().selectOne("com.reign.gcld.courtesy.domain.PlayerLiYi.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerLiYi> getModels() {
        return (List<PlayerLiYi>)this.getSqlSession().selectList("com.reign.gcld.courtesy.domain.PlayerLiYi.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.courtesy.domain.PlayerLiYi.getModelSize");
    }
    
    @Override
	public int create(final PlayerLiYi playerLiYi) {
        return this.getSqlSession().insert("com.reign.gcld.courtesy.domain.PlayerLiYi.create", playerLiYi);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.courtesy.domain.PlayerLiYi.deleteById", playerId);
    }
    
    @Override
	public int addliYiDu(final int playerId, final int liYiDuGain) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("liYiDuGain", liYiDuGain);
        return this.getSqlSession().update("com.reign.gcld.courtesy.domain.PlayerLiYi.addliYiDu", params);
    }
    
    @Override
	public int updateRewardInfo(final int playerId, final String rewardInfo) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("rewardInfo", rewardInfo);
        return this.getSqlSession().update("com.reign.gcld.courtesy.domain.PlayerLiYi.updateRewardInfo", params);
    }
}
