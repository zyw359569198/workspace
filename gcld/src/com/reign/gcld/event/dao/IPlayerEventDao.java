package com.reign.gcld.event.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.event.domain.*;
import java.util.*;

public interface IPlayerEventDao extends IBaseDao<PlayerEvent>
{
    PlayerEvent read(final int p0);
    
    PlayerEvent readForUpdate(final int p0);
    
    List<PlayerEvent> getModels();
    
    int getModelSize();
    
    int create(final PlayerEvent p0);
    
    int deleteById(final int p0);
    
    PlayerEvent getPlayerEvent(final int p0, final int p1);
    
    int setParam1(final int p0, final int p1, final int p2);
    
    int setParam2(final int p0, final int p1, final int p2);
    
    int setParam3(final int p0, final int p1, final int p2);
    
    int setParam4(final int p0, final int p1, final int p2);
    
    int setParam5(final int p0, final int p1, final int p2);
    
    List<PlayerEvent> getPlayerEventList(final int p0);
    
    int clearEvent(final int p0);
    
    int clearMidAutumn(final int p0, final int p1);
    
    int updateInfo(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    int updateInfo2(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int updateInfo3(final int p0, final int p1, final int p2, final int p3);
    
    List<Integer> getPlayerIdListByEventId(final int p0);
    
    boolean consumeBmw(final int p0, final int p1, final int p2, final String p3);
    
    boolean consumeXo(final int p0, final int p1, final int p2, final String p3);
    
    boolean consumePicasso(final int p0, final int p1, final int p2, final String p3);
    
    int updateParam8(final int p0, final int p1, final int p2);
    
    int updateParam10(final int p0, final int p1, final int p2);
    
    int updateParam1(final int p0, final int p1, final int p2);
    
    int updateParam1All(final int p0, final int p1);
    
    int updateParam2(final int p0, final int p1, final int p2);
    
    int addParam2WithParam3(final int p0);
    
    int updateParam3(final int p0, final int p1, final int p2);
    
    int updateParam3All(final int p0, final int p1);
    
    int updateParam4(final int p0, final int p1, final int p2);
    
    int updateParam4All(final int p0, final int p1);
    
    int updateParam5(final int p0, final int p1, final int p2);
    
    int updateParam6(final int p0, final int p1, final int p2);
    
    int updateCd1(final int p0, final int p1, final Date p2);
    
    int updateCd1All(final int p0, final Date p1);
    
    int updateParam8All(final int p0, final int p1);
    
    int addBmw(final int p0, final int p1, final int p2, final String p3);
    
    int addXo(final int p0, final int p1, final int p2, final String p3);
    
    int addPicasso(final int p0, final int p1, final int p2, final String p3);
    
    List<PlayerEvent> getIronRewardList(final int p0);
    
    int addParam1(final int p0, final int p1, final int p2);
    
    int addParam2(final int p0, final int p1, final int p2);
    
    int addParam3(final int p0, final int p1, final int p2);
    
    int addParam4(final int p0, final int p1, final int p2);
    
    int addParam4Reduce5(final int p0, final int p1, final int p2, final int p3);
    
    int addParam5(final int p0, final int p1, final int p2);
    
    int addParam1andParam2(final int p0, final int p1, final int p2, final int p3);
    
    int reduceParam1(final int p0, final int p1, final int p2);
    
    int reduceParam2(final int p0, final int p1, final int p2);
    
    int reduceParam4(final int p0, final int p1, final int p2);
    
    int reduceParam5(final int p0, final int p1, final int p2);
    
    List<PlayerEvent> getXiLianRewardList(final int p0);
    
    int reduceParam1AndParam6(final int p0, final int p1, final int p2, final int p3);
    
    int reduceParam2AndParam7(final int p0, final int p1, final int p2, final int p3);
    
    int reduceParam3AndParam8(final int p0, final int p1, final int p2, final int p3);
    
    int updateParam9UpdateParam10(final int p0, final int p1, final int p2, final int p3);
    
    List<PlayerEvent> getChristmasDayRewardList(final int p0);
    
    List<PlayerEvent> getWishRewardList(final int p0);
    
    List<PlayerEvent> getBeastRewardList(final int p0);
    
    List<PlayerEvent> getRedPaperRewardList(final int p0);
    
    List<PlayerEvent> getBaiNianRewardList(final int p0);
    
    List<PlayerEvent> getLanternRankList(final int p0);
    
    List<PlayerEvent> getLanternRankRewardList(final int p0);
    
    List<PlayerEvent> getLanternTitleRewardList(final int p0);
    
    int updateParam1updateParam2(final int p0, final int p1, final int p2, final int p3);
    
    int updateParam1updateParam2updateParam5updateCd1(final int p0, final int p1, final int p2, final int p3, final int p4, final Date p5);
    
    int updateParam2updateCD1(final int p0, final int p1, final int p2, final Date p3);
    
    int updateParam1updateParam2WithCondition(final int p0, final int p1, final int p2);
}
