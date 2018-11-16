package com.reign.gcld.gift.dao;

import com.reign.gcld.gift.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerGiftDao")
public class PlayerGiftDao extends BaseDao<PlayerGift> implements IPlayerGiftDao
{
    @Override
	public PlayerGift read(final int id) {
        return (PlayerGift)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerGift.read", (Object)id);
    }
    
    @Override
	public PlayerGift readForUpdate(final int id) {
        return (PlayerGift)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerGift.readForUpdate", (Object)id);
    }
    
    @Override
	public List<PlayerGift> getModels() {
        return (List<PlayerGift>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.PlayerGift.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerGift.getModelSize");
    }
    
    @Override
	public int create(final PlayerGift playerGift) {
        return this.getSqlSession().insert("com.reign.gcld.gift.domain.PlayerGift.create", playerGift);
    }
    
    @Override
	public int deleteById(final int id) {
        return this.getSqlSession().delete("com.reign.gcld.gift.domain.PlayerGift.deleteById", id);
    }
    
    @Override
	public List<PlayerGift> getByPlayerId(final int playerId) {
        return (List<PlayerGift>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.PlayerGift.getByPlayerId", (Object)playerId);
    }
    
    @Override
	public int getAllServerByPlayerId(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerGift.getAllServerByPlayerId", (Object)playerId);
    }
    
    @Override
	public List<PlayerGift> getAllGiftByPlayerId(final int playerId) {
        return (List<PlayerGift>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.PlayerGift.getAllGiftByPlayerId", (Object)playerId);
    }
    
    @Override
	public PlayerGift getByPlayerIdAndGiftId(final int playerId, final int giftId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("giftId", giftId);
        return (PlayerGift)this.getSqlSession().selectOne("com.reign.gcld.gift.domain.PlayerGift.getByPlayerIdAndGiftId", (Object)params);
    }
    
    @Override
	public int updateGift(final int id, final Date receivedTime) {
        final Params params = new Params();
        params.addParam("id", id);
        params.addParam("receivedTime", receivedTime);
        return this.getSqlSession().update("com.reign.gcld.gift.domain.PlayerGift.updateGift", params);
    }
    
    @Override
	public int deleteByGiftId(final int giftId) {
        return this.getSqlSession().delete("com.reign.gcld.gift.domain.PlayerGift.deleteByGiftId", giftId);
    }
    
    @Override
	public List<PlayerGift> getByGiftId(final int giftId) {
        return (List<PlayerGift>)this.getSqlSession().selectList("com.reign.gcld.gift.domain.PlayerGift.getByGiftId", (Object)giftId);
    }
}
