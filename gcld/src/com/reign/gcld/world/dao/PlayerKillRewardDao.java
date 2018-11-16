package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerKillRewardDao")
public class PlayerKillRewardDao extends BaseDao<PlayerKillReward> implements IPlayerKillRewardDao
{
    @Override
	public PlayerKillReward read(final int playerId) {
        return (PlayerKillReward)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerKillReward.read", (Object)playerId);
    }
    
    @Override
	public PlayerKillReward readForUpdate(final int playerId) {
        return (PlayerKillReward)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerKillReward.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerKillReward> getModels() {
        return (List<PlayerKillReward>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerKillReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerKillReward.getModelSize");
    }
    
    @Override
	public int create(final PlayerKillReward playerKillReward) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.PlayerKillReward.create", playerKillReward);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerKillReward.deleteById", playerId);
    }
    
    @Override
	public int updateReward(final int playerId, final int reduceNum, final long rewardTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("reduceNum", reduceNum).addParam("rewardTime", rewardTime);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerKillReward.updateReward", params);
    }
    
    @Override
	public int deleteAll() {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerKillReward.deleteAll");
    }
    
    @Override
	public int updateKillReward(final int playerId, final int killNum, final int nameList, final int reward, final long rewardTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("killNum", killNum).addParam("nameList", nameList).addParam("reward", reward).addParam("rewardTime", rewardTime);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerKillReward.updateKillReward", params);
    }
    
    @Override
	public List<PlayerKillReward> getCanRewardList() {
        return (List<PlayerKillReward>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerKillReward.getCanRewardList");
    }
}
