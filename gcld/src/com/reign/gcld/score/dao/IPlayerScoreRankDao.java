package com.reign.gcld.score.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.score.domain.*;
import java.util.*;

public interface IPlayerScoreRankDao extends IBaseDao<PlayerScoreRank>
{
    PlayerScoreRank read(final int p0);
    
    PlayerScoreRank readForUpdate(final int p0);
    
    List<PlayerScoreRank> getModels();
    
    int getModelSize();
    
    int create(final PlayerScoreRank p0);
    
    int deleteById(final int p0);
    
    int updateOccupyNumAndScore(final int p0, final int p1);
    
    int updateOccupyAndScore(final int p0, final int p1);
    
    int updateOccupyNum(final int p0);
    
    int updateAssistNumAndScore(final int p0, final int p1);
    
    int updateAssistAndScore(final int p0, final int p1);
    
    int updateAssistNum(final int p0);
    
    int updateCheerNumAndScore(final int p0, final int p1);
    
    int updateCheerAndScore(final int p0, final int p1);
    
    int updateCheerNum(final int p0);
    
    int getScore(final int p0);
    
    int getScore2(final int p0);
    
    int clearAll();
    
    int clearAll2();
    
    List<PlayerScoreRank> getRankList();
    
    List<PlayerScoreRank> getRewardRankList();
    
    int updateLastRank(final int p0, final int p1);
    
    int received(final int p0);
}
