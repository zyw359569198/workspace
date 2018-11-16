package com.reign.gcld.diamondshop.dao;

import com.reign.gcld.diamondshop.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerDiamondShopDao")
public class PlayerDiamondShopDao extends BaseDao<PlayerDiamondShop> implements IPlayerDiamondShopDao
{
    @Override
	public PlayerDiamondShop read(final int vId) {
        return (PlayerDiamondShop)this.getSqlSession().selectOne("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.read", (Object)vId);
    }
    
    @Override
	public PlayerDiamondShop readForUpdate(final int vId) {
        return (PlayerDiamondShop)this.getSqlSession().selectOne("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerDiamondShop> getModels() {
        return (List<PlayerDiamondShop>)this.getSqlSession().selectList("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.getModelSize");
    }
    
    @Override
	public int create(final PlayerDiamondShop playerDiamondShop) {
        return this.getSqlSession().insert("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.create", playerDiamondShop);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.deleteById", vId);
    }
    
    @Override
	public PlayerDiamondShop getMaxShop(final int playerId) {
        return (PlayerDiamondShop)this.getSqlSession().selectOne("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.getMaxShop", (Object)playerId);
    }
    
    @Override
	public int updateDiamondShopLv(final int lv, final int playerId, final int shopId) {
        final Params params = new Params();
        params.addParam("lv", lv).addParam("playerId", playerId).addParam("shopId", shopId);
        return this.getSqlSession().update("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.updateDiamondShopLv", params);
    }
    
    @Override
	public PlayerDiamondShop getByShopId(final int playerId, final int shopId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("shopId", shopId);
        return (PlayerDiamondShop)this.getSqlSession().selectOne("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.getByShopId", (Object)params);
    }
    
    @Override
	public int reduceDailyTimes(final int playerId, final int shopId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("shopId", shopId);
        return this.getSqlSession().update("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.reduceDailyTimes", params);
    }
    
    @Override
	public int resetRTimes(final int shopId, final int rTimes) {
        final Params params = new Params();
        params.addParam("rTimes", rTimes).addParam("shopId", shopId);
        return this.getSqlSession().update("com.reign.gcld.diamondshop.domain.PlayerDiamondShop.resetRTimes", params);
    }
}
