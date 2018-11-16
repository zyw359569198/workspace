package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public interface IPlayerAttributeDao extends IBaseDao<PlayerAttribute>
{
    PlayerAttribute read(final int p0);
    
    PlayerAttribute readForUpdate(final int p0);
    
    List<PlayerAttribute> getModels();
    
    int getModelSize();
    
    int create(final PlayerAttribute p0);
    
    int deleteById(final int p0);
    
    int updateFunction(final int p0, final String p1);
    
    int addMaxStoreNum(final int p0);
    
    int setIsNewArea(final int p0, final String p1);
    
    int updateEnterCount(final int p0, final int p1);
    
    void updateLastResetTime(final int p0, final Date p1);
    
    void updateLastGiftTime(final int p0, final Date p1);
    
    void resetData(final int p0, final Date p1, final int p2);
    
    void addBattleWinTimes(final int p0);
    
    void addBattleRewardTimes(final int p0);
    
    int updateRecruitToken(final int p0, final int p1, final String p2);
    
    int addRecruitToken(final int p0, final int p1, final String p2);
    
    void addPayPoint(final int p0, final int p1);
    
    int updateIronMineTime(final Date p0, final int p1);
    
    int updateGemMineTime(final Date p0, final int p1);
    
    int getMaxStoreNum(final int p0);
    
    int setMaxStoreNum(final int p0, final int p1);
    
    void updateGuideId(final int p0, final int p1);
    
    int setBlackMarketCd(final int p0, final Date p1);
    
    Date getBlackMarketCd(final int p0);
    
    String getFunctionId(final int p0);
    
    int setTechResearch(final int p0, final int p1);
    
    int setTechOpenAndTechResearch(final int p0, final int p1, final int p2);
    
    int setTechOpen(final int p0, final int p1);
    
    int setRecruitToken(final int p0, final int p1);
    
    int incrementIntimacy(final int p0, final int p1);
    
    int addFreeConstructionNum(final int p0, final int p1, final String p2);
    
    int getFreeConstructionNum(final int p0);
    
    int consumeFreeConstructionNum(final int p0, final String p1);
    
    int getRecruitTokenNum(final int p0);
    
    int setHasBandit(final int p0, final int p1);
    
    int setKidnapper(final int p0, final int p1);
    
    int getHasBandit(final int p0);
    
    int getKidnapper(final int p0);
    
    int killBandit(final int p0, final int p1);
    
    int killKidnapper(final int p0, final int p1);
}
