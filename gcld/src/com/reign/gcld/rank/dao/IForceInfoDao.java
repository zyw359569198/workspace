package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IForceInfoDao extends IBaseDao<ForceInfo>
{
    ForceInfo read(final int p0);
    
    ForceInfo readForUpdate(final int p0);
    
    List<ForceInfo> getModels();
    
    int getModelSize();
    
    int create(final ForceInfo p0);
    
    int deleteById(final int p0);
    
    int updateEndTime(final Date p0, final int p1);
    
    void updateNationExp(final int p0, final int p1);
    
    void updateIsWin(final Integer p0, final int p1);
    
    int updateIsWinAndLvAndExp(final Integer p0, final int p1, final int p2);
    
    void updateEndTimeAndIsWin(final Date p0, final int p1, final int p2);
    
    int addBeiDiShouMaiSum(final int p0, final int p1);
    
    int addXiRongShouMaiSum(final int p0, final int p1);
    
    int addDongYiShouMaiSum(final int p0, final int p1);
    
    int addShouMaiSum(final int p0, final int p1, final int p2);
    
    int decreaseLeftCountAndAddQmdAndResetShouMaiSum(final int p0, final ForceInfo p1, final int p2);
    
    int decreaseLeftCountAndAddQmdAndResetShouMaiSumOfBeiDi(final int p0);
    
    int decreaseLeftCountAndAddQmdAndResetShouMaiSumOfXiRong(final int p0);
    
    int decreaseLeftCountAndAddQmdAndResetShouMaiSumOfDongYi(final int p0);
    
    int resetShouMaiCount(final int p0);
    
    int decreaseLeftCountAndResetShouMaiSumOfDongYi(final int p0);
    
    int decreaseLeftCountAndResetShouMaiSumOfXiRong(final int p0);
    
    int decreaseLeftCountAndResetShouMaiSumOfBeiDi(final int p0);
    
    int getMaxLv();
    
    int addIds(final int p0, final String p1);
    
    int updateTryInfo(final int p0, final int p1, final Date p2, final int p3, final int p4, final int p5);
    
    int updateTryWin(final int p0, final Date p1, final int p2);
    
    int updateTryFail(final int p0, final int p1);
    
    int addGeneralNum(final int p0, final int p1);
    
    int getGeneralNum(final int p0);
    
    int updateStage(final int p0, final int p1);
    
    int updatePWin(final int p0, final int p1);
    
    int startPTask(final int p0, final int p1, final int p2, final int p3);
    
    int updateInvestSum(final int p0, final long p1);
    
    void updateFarmLv(final int p0, final int p1);
    
    int updateForceLvAndAddExp(final int p0, final int p1, final int p2);
    
    int getMaxExp();
    
    int updateNationIndivId(final int p0, final String p1);
}
