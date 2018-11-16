package com.reign.gcld.activity.dao;

import com.reign.gcld.activity.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;

@Component("playerDragonDao")
public class PlayerDragonDao extends BaseDao<PlayerDragon> implements IPlayerDragonDao
{
    @Autowired
    private IPlayerDao playerDao;
    
    @Override
	public PlayerDragon read(final int playerId) {
        return (PlayerDragon)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerDragon.read", (Object)playerId);
    }
    
    @Override
	public PlayerDragon readForUpdate(final int playerId) {
        return (PlayerDragon)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerDragon.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerDragon> getModels() {
        return (List<PlayerDragon>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.PlayerDragon.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerDragon.getModelSize");
    }
    
    @Override
	public int create(final PlayerDragon playerDragon) {
        return this.getSqlSession().insert("com.reign.gcld.activity.domain.PlayerDragon.create", playerDragon);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.PlayerDragon.deleteById", playerId);
    }
    
    @Override
	public int getDragonNumByPlayerId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerDragon.getDragonNumByPlayerId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int getBoxNumByPlayerId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerDragon.getBoxNumByPlayerId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int setDragonNumByPlayerId(final int playerId, final int dragonNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("dragonNum", dragonNum);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerDragon.setDragonNumByPlayerId", params);
    }
    
    @Override
	public int useDragon(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerDragon.useDragon", playerId);
    }
    
    @Override
	public int useBox(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerDragon.useBox", playerId);
    }
    
    @Override
	public int addDragonNumByPlayerId(final int playerId, final int dragonNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("dragonNum", dragonNum);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerDragon.addDragonNumByPlayerId", params);
    }
    
    @Override
	public int addBoxNumByPlayerId(final int playerId, final int boxNum, final int maxNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("boxNum", boxNum);
        params.addParam("maxNum", maxNum);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerDragon.addBoxNumByPlayerId", params);
    }
    
    @Override
	public List<PlayerDragon> getDragonNumList() {
        return (List<PlayerDragon>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.PlayerDragon.getDragonNumList");
    }
    
    @Override
	public int clearDragon() {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerDragon.clearDragon");
    }
    
    @Override
	public int addFeatBoxNum(final int playerId, final int featBoxNum, final int maxFeatNum, final String attribute) {
        final int before = this.getFeatBoxNum(playerId);
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("featBoxNum", featBoxNum);
        params.addParam("maxFeatNum", maxFeatNum);
        final int result = this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerDragon.addFeatBoxNum", params);
        final int after = this.getFeatBoxNum(playerId);
        if (after > before) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "featbox", after - before, "+", attribute, player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public int getFeatBoxNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.PlayerDragon.getFeatBoxNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int useFeatBoxNum(final int playerId, final String attribute) {
        final int before = this.getFeatBoxNum(playerId);
        final int result = this.getSqlSession().update("com.reign.gcld.activity.domain.PlayerDragon.useFeatBoxNum", playerId);
        final int after = this.getFeatBoxNum(playerId);
        if (before > after) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "featbox", before - after, "-", attribute, player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
}
