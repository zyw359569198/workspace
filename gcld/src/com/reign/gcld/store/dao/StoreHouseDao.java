package com.reign.gcld.store.dao;

import com.reign.gcld.store.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("storeHouseDao")
public class StoreHouseDao extends BaseDao<StoreHouse> implements IStoreHouseDao
{
    @Override
	public StoreHouse read(final int vId) {
        return (StoreHouse)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.read", (Object)vId);
    }
    
    @Override
	public StoreHouse readForUpdate(final int vId) {
        return (StoreHouse)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<StoreHouse> getModels() {
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getModelSize");
    }
    
    @Override
	public int create(final StoreHouse storeHouse) {
        return this.getSqlSession().insert("com.reign.gcld.store.domain.StoreHouse.create", storeHouse);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.store.domain.StoreHouse.deleteById", vId);
    }
    
    @Override
	public int getCountByPlayerId(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("state", 0);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getCountByPlayerId", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getMilitaryEquipList(final int playerId, final int quality) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("quality", quality);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getMilitaryEquipList", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getStoreHouseGoods(final int playerId) {
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getStoreHouseGoods", (Object)playerId);
    }
    
    @Override
	public List<StoreHouse> getStoreHouseByPlayerId(final int playerId) {
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getStoreHouseByPlayerId", (Object)playerId);
    }
    
    @Override
	public List<StoreHouse> getByPlayerId(final int playerId) {
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getByPlayerId", (Object)playerId);
    }
    
    @Override
	public int equipRebuild(final int vId, final int itemId, final int lv, final int attribute, final int quality) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("itemId", itemId).addParam("lv", lv).addParam("attribute", attribute).addParam("quality", quality);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.equipRebuild", params);
    }
    
    @Override
	public int equipUpLv(final int vId, final int addLv, final int addAttribute) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("addLv", addLv).addParam("addAttribute", addAttribute);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.equipUpLv", params);
    }
    
    @Override
	public List<StoreHouse> getGernerlEquip(final int playerId) {
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getGernerlEquip", (Object)playerId);
    }
    
    @Override
	public List<StoreHouse> getWearableEquip(final int playerId, final int goodsType, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("goodsType", goodsType).addParam("type", type);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getWearableEquip", (Object)params);
    }
    
    @Override
	public StoreHouse getByGeneralIdAndType(final int playerId, final int owner, final int goodsType, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("owner", owner).addParam("type", type).addParam("goodsType", goodsType);
        return (StoreHouse)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getByGeneralIdAndType", (Object)params);
    }
    
    @Override
	public int resetOwnerByGeneralIdAndType(final int playerId, final int owner, final int goodsType, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("owner", owner).addParam("goodsType", goodsType).addParam("type", type);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.resetOwnerByGeneralIdAndType", params);
    }
    
    @Override
	public int resetOwnerByGeneralId(final int playerId, final int owner) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("owner", owner);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.resetOwnerByGeneralId", params);
    }
    
    @Override
	public int resetOwnerByVId(final int vId, final int owner) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("owner", owner);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.resetOwnerByVId", params);
    }
    
    @Override
	public int getGeneralEquipCount(final int playerId, final int owner) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("owner", owner);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getGeneralEquipCount", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getGeneralEquipList(final int playerId, final int owner, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("owner", owner).addParam("type", type);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getGeneralEquipList", (Object)params);
    }
    
    @Override
	public int getNumByLvOrQuality(final int playerId, final int goodsType, final int level, final int quality, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("goodsType", goodsType).addParam("level", level).addParam("quality", quality).addParam("type", type);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getNumByLvOrQuality", (Object)params);
    }
    
    @Override
	public int getWearNumByLvOrQuality(final int playerId, final int goodsType, final int level, final int quality, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type).addParam("goodsType", goodsType).addParam("level", level).addParam("quality", quality);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getWearNumByLvOrQuality", (Object)params);
    }
    
    @Override
	public int getCountByQualityNType(final int playerId, final int goodsType, final int quality, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type).addParam("goodsType", goodsType).addParam("quality", quality);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getCountByQualityNType", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getByQualityNType(final int playerId, final int goodsType, final int quality, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type).addParam("goodsType", goodsType).addParam("quality", quality);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getByQualityNType", (Object)params);
    }
    
    @Override
	public int updateGemId(final int vId, final int gemId) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("gemId", gemId);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateGemId", params);
    }
    
    @Override
	public int updateGemIdByPlayerIdAndGemId(final int playerId, final int gemId, final int newGemId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", 2);
        params.addParam("goodsType", 1);
        params.addParam("gemId", gemId);
        params.addParam("newGemId", newGemId);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateGemIdByPlayerIdAndGemId", params);
    }
    
    @Override
	public int updateItemIdAndGemLvAndRefreshAttributeAndGoosType(final int vId, final int itemId, final int gemLv, final String refreshAttribute, final int goodsType) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("itemId", itemId);
        params.addParam("gemLv", gemLv);
        params.addParam("refreshAttribute", refreshAttribute);
        params.addParam("goodsType", goodsType);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateItemIdAndGemLvAndRefreshAttributeAndGoosType", params);
    }
    
    @Override
	public int updateItemIdAndGemLvAndAttribute(final int vId, final int itemId, final int gemLv, final String attribute) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("itemId", itemId);
        params.addParam("gemLv", gemLv);
        params.addParam("attribute", attribute);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateItemIdAndGemLvAndAttribute", params);
    }
    
    @Override
	public int updateItemIdAndGemLvAndAttributeAndRefreshAttribute(final int vId, final int itemId, final int gemLv, final String attribute, final String refreshAttribute) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("itemId", itemId);
        params.addParam("gemLv", gemLv);
        params.addParam("attribute", attribute);
        params.addParam("refreshAttribute", refreshAttribute);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateItemIdAndGemLvAndAttributeAndRefreshAttribute", params);
    }
    
    @Override
	public int getGemNumByPlayerId(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getGemNumByPlayerId", (Object)playerId);
    }
    
    @Override
	public int reduceNum(final int vId, final int reduceNum) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("reduceNum", reduceNum);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.reduceNum", params);
    }
    
    @Override
	public int addNum(final int vId, final int addNum) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("addNum", addNum);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.addNum", params);
    }
    
    @Override
	public List<StoreHouse> getByItemId(final int playerId, final int itemId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("itemId", itemId).addParam("type", type);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getByItemId", (Object)params);
    }
    
    @Override
	public int deleteByType(final int playerId, final int itemId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("itemId", itemId).addParam("type", type);
        return this.getSqlSession().delete("com.reign.gcld.store.domain.StoreHouse.deleteByType", params);
    }
    
    @Override
	public List<StoreHouse> getByType(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getByType", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getOwnerByType(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getOwnerByType", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getNoOwnerByType(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getNoOwnerByType", (Object)params);
    }
    
    @Override
	public int resetGeneralTreasure(final int vId) {
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.resetGeneralTreasure", vId);
    }
    
    @Override
	public void changeState(final int vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("state", state);
        this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.changeState", params);
    }
    
    @Override
	public List<StoreHouse> getCanAuctionTreasure(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", 3);
        params.addParam("state", 0);
        params.addParam("quality", 5);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getCanAuctionTreasure", (Object)params);
    }
    
    @Override
	public int changeGeneralTreasure(final int vId, final int owner, final int location) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("owner", owner).addParam("location", location);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.changeGeneralTreasure", params);
    }
    
    @Override
	public int updateGeneralEquip(final int playerId, final int orgGeneralId, final int nowGeneralId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("orgGeneralId", orgGeneralId).addParam("nowGeneralId", nowGeneralId).addParam("type", type);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateGeneralEquip", params);
    }
    
    @Override
	public List<StoreHouse> getGeneralTreasureByType(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getGeneralTreasureByType", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getSigningTreasure(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", 3);
        params.addParam("state", 1);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getSigningTreasure", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getUnSetGems(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", 2);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getUnSetGems", (Object)params);
    }
    
    @Override
	public List<StoreHouse> getSetGemsDiamonds(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", 2).addParam("goodsType", 1);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getSetGemsDiamonds", (Object)params);
    }
    
    @Override
	public int getSetGemsDiamondsCount(final int playerId, final int type, final int goodsType) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type).addParam("goodsType", goodsType);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getSetGemsDiamondsCount", (Object)params);
    }
    
    @Override
	public int updateRefreshAttribute(final Integer vId, final String s) {
        final Params params = new Params();
        params.addParam("id", vId).addParam("attr", s);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateRefreshAttribute", params);
    }
    
    @Override
	public int updateQuenchingTimes(final Integer vId, final int t, final int type) {
        final Params params = new Params();
        params.addParam("id", vId).addParam("t", t);
        if (type == 1) {
            return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateQuenchingTimes", params);
        }
        if (type == 2) {
            return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateQuenchingFreeTimes", params);
        }
        return 0;
    }
    
    @Override
	public List<StoreHouse> getAllEquip(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (List<StoreHouse>)this.getSqlSession().selectList("com.reign.gcld.store.domain.StoreHouse.getAllEquip", (Object)params);
    }
    
    @Override
	public int getGeneralTreasureNum(final int playerId, final int owner) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("owner", owner);
        params.addParam("type", 3);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getGeneralTreasureNum", (Object)params);
    }
    
    @Override
	public void updateRefreshAttriAndSpecial(final int equipId, final String string, final int skillId) {
        final Params params = new Params();
        params.addParam("id", equipId).addParam("attr", string).addParam("skillId", skillId);
        this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateRefreshAttriAndSpecial", params);
    }
    
    @Override
	public void demoutCreate(final List<StoreHouse> toStore) {
        this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.demoutCreate", toStore);
    }
    
    @Override
	public void resetOwnerByGeneralIdExceptTreasure(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("owner", generalId);
        this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.resetOwnerByGeneralIdExceptTreasure", params);
    }
    
    @Override
	public void updateBindExpireTime(final int vId, final long expireTime) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("expireTime", expireTime);
        this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateBindExpireTime", params);
    }
    
    @Override
	public int updateRefreshAttributeAndMarkId(final int vId, final String refreshAttribute, final int markId) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("refreshAttribute", refreshAttribute);
        params.addParam("markId", markId);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateRefreshAttributeAndMarkId", params);
    }
    
    @Override
	public int updateRefreshAttriAndSpecialAndMarkId(final int vId, final String refreshAttribute, final int specialSkillId, final int markId) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("refreshAttribute", refreshAttribute);
        params.addParam("specialSkillId", specialSkillId);
        params.addParam("markId", markId);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateRefreshAttriAndSpecialAndMarkId", params);
    }
    
    @Override
	public StoreHouse getStoreHouseByPlayerIdAndMarkId(final int playerId, final int markId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("markId", markId);
        return (StoreHouse)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getStoreHouseByPlayerIdAndMarkId", (Object)params);
    }
    
    @Override
	public int clearMarkId() {
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.clearMarkId");
    }
    
    @Override
	public int deleteWeaponAndClearOwner(final int playerId) {
        this.resetOwnerByGeneralId(playerId, 0);
        return this.getSqlSession().delete("com.reign.gcld.store.domain.StoreHouse.deleteWeaponAndClearOwner", playerId);
    }
    
    @Override
	public int updateAttribute(final int vId, final String attribute) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("attribute", attribute);
        return this.getSqlSession().update("com.reign.gcld.store.domain.StoreHouse.updateAttribute", params);
    }
    
    @Override
	public StoreHouse getStoreHouseByWeaponId(final int playerId, final int weaponId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("weaponId", weaponId);
        return (StoreHouse)this.getSqlSession().selectOne("com.reign.gcld.store.domain.StoreHouse.getStoreHouseByWeaponId", (Object)params);
    }
}
