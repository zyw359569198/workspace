package com.reign.gcld.market.dao;

import com.reign.gcld.market.domain.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.jdbc.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Component("playerMarketDao")
public class PlayerMarketDao extends BaseDao<PlayerMarket> implements IPlayerMarketDao
{
    @Autowired
    private IBatchExecute batchExecute;
    
    @Override
	public PlayerMarket read(final int playerId) {
        return (PlayerMarket)this.getSqlSession().selectOne("com.reign.gcld.marke.domain.PlayerMarket.read", (Object)playerId);
    }
    
    @Override
	public PlayerMarket readForUpdate(final int playerId) {
        return (PlayerMarket)this.getSqlSession().selectOne("com.reign.gcld.marke.domain.PlayerMarket.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerMarket> getModels() {
        return (List<PlayerMarket>)this.getSqlSession().selectList("com.reign.gcld.marke.domain.PlayerMarket.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.marke.domain.PlayerMarket.getModelSize");
    }
    
    @Override
	public int create(final PlayerMarket playerMarket) {
        return this.getSqlSession().insert("com.reign.gcld.marke.domain.PlayerMarket.create", playerMarket);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.marke.domain.PlayerMarket.deleteById", playerId);
    }
    
    @Override
	public int batchAddCanbuyNum(final Map<Integer, Double> playerMarketInfoMap, final int maxNum, final Date date) {
        if (playerMarketInfoMap == null || playerMarketInfoMap.size() <= 0) {
            return -1;
        }
        final String sql = "UPDATE PLAYER_MARKET SET CANBUY_NUM = GREATEST(CANBUY_NUM, LEAST(?, CANBUY_NUM + ?)) ,GET_BUYNUM_TIME=? WHERE PLAYER_ID = ?";
        final List<List<Param>> paramsList = new ArrayList<List<Param>>();
        for (final Integer playerId : playerMarketInfoMap.keySet()) {
            final Double addNum = playerMarketInfoMap.get(playerId);
            final List<Param> params = new ArrayList<Param>();
            params.add(new Param(maxNum, Type.Int));
            params.add(new Param(addNum, Type.Double));
            params.add(new Param(date, Type.Date));
            params.add(new Param(playerId, Type.Int));
            paramsList.add(params);
        }
        return this.batchExecute.batch(this.getSqlSession(), sql, paramsList);
    }
    
    @Override
	public void addCanbuyNum(final int playerId, final double addNum, final int maxNum, final Date date) {
        final Params param = new Params();
        param.addParam("playerId", (Object)playerId);
        param.addParam("addNum", (Object)addNum);
        param.addParam("maxNum", (Object)maxNum);
        param.addParam("date", (Object)date);
        this.getSqlSession().update("com.reign.gcld.marke.domain.PlayerMarket.addCanbuyNum", (Object)param);
    }
    
    @Override
	public List<Integer> getCanBuyNumList(final List<Integer> pIdList) {
        final List<Integer> result = new ArrayList<Integer>();
        if (pIdList == null || pIdList.size() <= 0) {
            return result;
        }
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.marke.domain.PlayerMarket.getCanBuyNumList", (Object)pIdList);
    }
    
    @Override
	public void minuseCanbuyNum(final int playerId) {
        final Params param = new Params();
        param.addParam("playerId", (Object)playerId);
        this.getSqlSession().update("com.reign.gcld.marke.domain.PlayerMarket.minuseCanbuyNum", (Object)param);
    }
    
    @Override
	public void updateInfo(final int playerId, final String info, final Date refreshTime) {
        final Params param = new Params();
        param.addParam("playerId", (Object)playerId);
        param.addParam("info", (Object)info);
        param.addParam("refreshTime", (Object)refreshTime);
        this.getSqlSession().update("com.reign.gcld.marke.domain.PlayerMarket.updateInfo", (Object)param);
    }
    
    @Override
	public void rewardCanbuyNum(final int playerId, final int addNum) {
        final Params param = new Params();
        param.addParam("playerId", (Object)playerId);
        param.addParam("addNum", (Object)addNum);
        param.addParam("max", (Object)24);
        this.getSqlSession().update("com.reign.gcld.marke.domain.PlayerMarket.rewardCanbuyNum", (Object)param);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("marketCanBuyNum", (int)this.getNum(playerId)));
    }
    
    @Override
	public double getNum(final int playerId) {
        final Double result = (Double)this.getSqlSession().selectOne("com.reign.gcld.marke.domain.PlayerMarket.getNum", (Object)playerId);
        if (result == null) {
            return 0.0;
        }
        return result;
    }
}
