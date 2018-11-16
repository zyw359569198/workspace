package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Component("playerSearchDao")
public class PlayerSearchDao extends BaseDao<PlayerSearch> implements IPlayerSearchDao
{
    @Override
	public PlayerSearch read(final int playerId) {
        return (PlayerSearch)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerSearch.read", (Object)playerId);
    }
    
    @Override
	public PlayerSearch readForUpdate(final int playerId) {
        return (PlayerSearch)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerSearch.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerSearch> getModels() {
        return (List<PlayerSearch>)this.getSqlSession().selectList("com.reign.gcld.world.domain.PlayerSearch.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerSearch.getModelSize");
    }
    
    @Override
	public int create(final PlayerSearch playerSearch) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.PlayerSearch.create", playerSearch);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.PlayerSearch.deleteById", playerId);
    }
    
    @Override
	public void addSearchNum(final int playerId, final int addNum, final int topSearchNum, final Date nowDate) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("addNum", addNum);
        params.addParam("topSearchNum", topSearchNum);
        params.addParam("nowDate", nowDate);
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerSearch.addSearchNum", params);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("searchNum", this.getSearchNum(playerId)));
    }
    
    @Override
	public void rewardSearchNum(final int playerId, final int addNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("addNum", addNum);
        params.addParam("max", 20);
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerSearch.rewardSearchNum", params);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("searchNum", this.getSearchNum(playerId)));
    }
    
    @Override
	public void addSearchBuyNum(final int playerId, final int addNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("addNum", addNum);
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerSearch.addSearchBuyNum", params);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("searchNum", this.getSearchNum(playerId)));
    }
    
    @Override
	public void updateCanSearchInfo(final int playerId, final String info) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("info", info);
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerSearch.updateCanSearchInfo", params);
    }
    
    @Override
	public void updateCurrSearchInfoAndUse(final int playerId, final String info) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("info", info);
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerSearch.updateCurrSearchInfo", params);
    }
    
    @Override
	public void resetBuyNum(final int playerId) {
        this.getSqlSession().update("com.reign.gcld.world.domain.PlayerSearch.resetBuyNum", playerId);
    }
    
    @Override
	public int getSearchNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.world.domain.PlayerSearch.getSearchNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
}
