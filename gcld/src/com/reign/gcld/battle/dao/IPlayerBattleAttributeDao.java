package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerBattleAttributeDao extends IBaseDao<PlayerBattleAttribute>
{
    PlayerBattleAttribute read(final int p0);
    
    PlayerBattleAttribute readForUpdate(final int p0);
    
    List<PlayerBattleAttribute> getModels();
    
    int getModelSize();
    
    int create(final PlayerBattleAttribute p0);
    
    int deleteById(final int p0);
    
    int updateModeTime(final int p0, final int p1, final Date p2);
    
    int updateArmiesBattleOrder(final int p0, final String p1);
    
    int updateArmiesAutoCount(final int p0, final int p1);
    
    int updateYoudiTime(final int p0, final long p1);
    
    int updateChujiTime(final int p0, final long p1);
    
    @Deprecated
    int resetVip3PhantomCount(final int p0, final int p1);
    
    int decreaseVip3PhantomCount(final int p0, final String p1);
    
    @Deprecated
    int updateVip3PhantomCount(final int p0, final int p1);
    
    int updateAutoStrategy(final int p0, final int p1);
    
    int addVip3PhantomCount(final int p0, final int p1, final String p2);
    
    int reduceTeamTimes(final int p0, final int p1);
    
    int addTeamTimes(final int p0, final int p1);
    
    int updateShouMaiManZuTime(final int p0, final long p1);
    
    int clearWinTimesAndFailTimes();
    
    int addWinTimes(final int p0);
    
    int addFailTimes(final int p0);
    
    int addVip3PhantomCountMax(final int p0, final int p1, final int p2, final String p3);
    
    int updateChangeBat(final int p0);
    
    int resetEventNationalTreasureCountToday();
    
    int addEventGemCountToday(final int p0, final int p1);
    
    int addEventJiebingCountToday(final int p0, final int p1);
    
    int addEventGemCount(final int p0, final int p1);
    
    int clearBatExpActivity();
    
    int updateBatExpActivity(final int p0, final int p1);
    
    int getVip3PhantomCount(final int p0);
    
    int updatePhantomWorkShopLv(final int p0, final int p1);
    
    List<PlayerBattleAttribute> getHasPhantomWorkShopModels();
    
    int decreaseSomeVip3PhantomCount(final int p0, final int p1, final String p2);
    
    int addEventXtysCountToday(final int p0);
    
    int addEventSdlrCountToday(final int p0);
    
    int addEventNationalTreasureCountToday(final int p0);
    
    int getChangebat(final int p0);
    
    void addEventWorldDramaCountToday(final int p0);
    
    int resetEventNumToday();
    
    void addEventLblCountToday(final int p0);
    
    void addEventSlaveCountToday(final int p0);
}
