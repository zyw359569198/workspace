package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerChallengeInfoDao")
public class PlayerChallengeInfoDao extends BaseDao<PlayerChallengeInfo> implements IPlayerChallengeInfoDao
{
    @Override
	public PlayerChallengeInfo read(final int vid) {
        return (PlayerChallengeInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerChallengeInfo.read", (Object)vid);
    }
    
    @Override
	public PlayerChallengeInfo readForUpdate(final int vid) {
        return (PlayerChallengeInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerChallengeInfo.readForUpdate", (Object)vid);
    }
    
    @Override
	public List<PlayerChallengeInfo> getModels() {
        return (List<PlayerChallengeInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerChallengeInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerChallengeInfo.getModelSize");
    }
    
    @Override
	public int create(final PlayerChallengeInfo playerChallengeInfo) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.PlayerChallengeInfo.create", playerChallengeInfo);
    }
    
    @Override
	public int deleteById(final int vid) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerChallengeInfo.deleteById", vid);
    }
    
    @Override
	public int getByPlayerId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerChallengeInfo.getByPlayerId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public List<PlayerChallengeInfo> getListByPlayerIdOrderByNum(final int playerId) {
        return (List<PlayerChallengeInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerChallengeInfo.getListByPlayerIdOrderByNum", (Object)playerId);
    }
    
    @Override
	public int updateVtimes(final int playerId, final int generalId, final int times) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        params.addParam("times", times);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerChallengeInfo.updateVtimes", params);
    }
    
    @Override
	public PlayerChallengeInfo getInfoByPAndG(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return (PlayerChallengeInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerChallengeInfo.getInfoByPAndG", (Object)params);
    }
}
