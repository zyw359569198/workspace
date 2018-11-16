package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerWorldDao")
public class PlayerWorldDao extends BaseDao<PlayerWorld> implements IPlayerWorldDao
{
    @Override
	public PlayerWorld read(final int playerId) {
        return (PlayerWorld)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerWorld.read", (Object)playerId);
    }
    
    @Override
	public PlayerWorld readForUpdate(final int playerId) {
        return (PlayerWorld)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerWorld.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerWorld> getModels() {
        return (List<PlayerWorld>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerWorld.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerWorld.getModelSize");
    }
    
    @Override
	public int create(final PlayerWorld playerWorld) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.PlayerWorld.create", playerWorld);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerWorld.deleteById", playerId);
    }
    
    @Override
	public int addRewardNum(final int maxNum) {
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.addRewardNum", maxNum);
    }
    
    @Override
	public int reduceRewardNum(final int playerId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.reduceRewardNum", params);
    }
    
    @Override
	public int updateAttInfo(final int playerId, final String attedId, final String canAttId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("attedId", attedId).addParam("canAttId", canAttId);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.updateAttInfo", params);
    }
    
    @Override
	public List<Integer> getByRewardNum(final int rewardNumMax) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerWorld.getByRewardNum", (Object)rewardNumMax);
    }
    
    @Override
	public int addRewards(final int playerId, final String reward) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("reward", reward);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.addRewards", params);
    }
    
    @Override
	public int reduceReward(final int playerId, final String rewards, final String reward) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("rewards", rewards).addParam("reward", reward);
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.reduceReward", params);
    }
    
    @Override
	public void updateBoxInfo(final int playerId, final String boxInfo) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("boxInfo", boxInfo);
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.updateBoxInfo", params);
    }
    
    @Override
	public void updateQuizInfo(final int playerId, final int quizInfo) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("quizInfo", quizInfo);
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.updateQuizInfo", params);
    }
    
    @Override
	public void updateNpcLostDetail(final int playerId, final String npcLostDetail) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("npcLostDetail", npcLostDetail);
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.updateNpcLostDetail", params);
    }
    
    @Override
	public int clearRewardNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.world.domain.PlayerWorld.clearRewardNum", playerId);
    }
}
