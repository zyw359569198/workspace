package com.reign.gcld.store.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.store.domain.*;
import java.util.*;

public interface IStoreHouseDao extends IBaseDao<StoreHouse>
{
    StoreHouse read(final int p0);
    
    StoreHouse readForUpdate(final int p0);
    
    List<StoreHouse> getModels();
    
    int getModelSize();
    
    int create(final StoreHouse p0);
    
    int deleteById(final int p0);
    
    int getCountByPlayerId(final int p0);
    
    List<StoreHouse> getMilitaryEquipList(final int p0, final int p1);
    
    int equipRebuild(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int equipUpLv(final int p0, final int p1, final int p2);
    
    List<StoreHouse> getStoreHouseGoods(final int p0);
    
    List<StoreHouse> getGernerlEquip(final int p0);
    
    List<StoreHouse> getWearableEquip(final int p0, final int p1, final int p2);
    
    int resetOwnerByVId(final int p0, final int p1);
    
    int resetOwnerByGeneralId(final int p0, final int p1);
    
    int resetOwnerByGeneralIdAndType(final int p0, final int p1, final int p2, final int p3);
    
    int getGeneralEquipCount(final int p0, final int p1);
    
    List<StoreHouse> getGeneralEquipList(final int p0, final int p1, final int p2);
    
    int getCountByQualityNType(final int p0, final int p1, final int p2, final int p3);
    
    int getNumByLvOrQuality(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int getWearNumByLvOrQuality(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    StoreHouse getByGeneralIdAndType(final int p0, final int p1, final int p2, final int p3);
    
    int updateGemId(final int p0, final int p1);
    
    int updateGemIdByPlayerIdAndGemId(final int p0, final int p1, final int p2);
    
    int updateItemIdAndGemLvAndRefreshAttributeAndGoosType(final int p0, final int p1, final int p2, final String p3, final int p4);
    
    int updateItemIdAndGemLvAndAttribute(final int p0, final int p1, final int p2, final String p3);
    
    int updateItemIdAndGemLvAndAttributeAndRefreshAttribute(final int p0, final int p1, final int p2, final String p3, final String p4);
    
    int getGemNumByPlayerId(final int p0);
    
    int reduceNum(final int p0, final int p1);
    
    int addNum(final int p0, final int p1);
    
    List<StoreHouse> getByItemId(final int p0, final int p1, final int p2);
    
    int deleteByType(final int p0, final int p1, final int p2);
    
    List<StoreHouse> getByType(final int p0, final int p1);
    
    List<StoreHouse> getOwnerByType(final int p0, final int p1);
    
    List<StoreHouse> getNoOwnerByType(final int p0, final int p1);
    
    int resetGeneralTreasure(final int p0);
    
    void changeState(final int p0, final int p1);
    
    List<StoreHouse> getCanAuctionTreasure(final int p0);
    
    int changeGeneralTreasure(final int p0, final int p1, final int p2);
    
    List<StoreHouse> getGeneralTreasureByType(final int p0, final int p1);
    
    List<StoreHouse> getByPlayerId(final int p0);
    
    List<StoreHouse> getSigningTreasure(final int p0);
    
    List<StoreHouse> getUnSetGems(final int p0);
    
    List<StoreHouse> getSetGemsDiamonds(final int p0);
    
    int getSetGemsDiamondsCount(final int p0, final int p1, final int p2);
    
    int updateRefreshAttribute(final Integer p0, final String p1);
    
    int updateQuenchingTimes(final Integer p0, final int p1, final int p2);
    
    List<StoreHouse> getAllEquip(final int p0);
    
    int getGeneralTreasureNum(final int p0, final int p1);
    
    List<StoreHouse> getByQualityNType(final int p0, final int p1, final int p2, final int p3);
    
    int updateGeneralEquip(final int p0, final int p1, final int p2, final int p3);
    
    void updateRefreshAttriAndSpecial(final int p0, final String p1, final int p2);
    
    void demoutCreate(final List<StoreHouse> p0);
    
    void resetOwnerByGeneralIdExceptTreasure(final int p0, final int p1);
    
    List<StoreHouse> getStoreHouseByPlayerId(final int p0);
    
    void updateBindExpireTime(final int p0, final long p1);
    
    int updateRefreshAttributeAndMarkId(final int p0, final String p1, final int p2);
    
    int updateRefreshAttriAndSpecialAndMarkId(final int p0, final String p1, final int p2, final int p3);
    
    StoreHouse getStoreHouseByPlayerIdAndMarkId(final int p0, final int p1);
    
    int clearMarkId();
    
    int deleteWeaponAndClearOwner(final int p0);
    
    int updateAttribute(final int p0, final String p1);
    
    StoreHouse getStoreHouseByWeaponId(final int p0, final int p1);
}
