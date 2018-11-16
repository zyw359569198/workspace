package com.reign.gcld.rank.service;

import java.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.rank.common.*;
import com.reign.framework.json.*;
import com.reign.util.*;
import com.reign.gcld.rank.*;

public interface IRankService
{
    void fireRankEvent(final int p0, final RankData p1);
    
    int getRank(final int p0, final int p1);
    
    int getRank(final int p0, final int p1, final int p2);
    
    byte[] getRankList(final int p0);
    
    List<RankData> getRankDataList(final int p0);
    
    List<Integer> getLevelRankList(final int p0, final int p1);
    
    List<RankData> getRankDataList(final int p0, final int p1);
    
    List<Integer> getForceLevelRankList(final int p0, final int p1, final int p2);
    
    int getTotalRankNumByForceId(final int p0);
    
    List<Integer> getForcePositionRankList(final int p0, final int p1, final int p2);
    
    List<Integer> getRankInfo(final int p0, final int p1, final int p2);
    
    int getPlayerPositionRank(final int p0, final int p1);
    
    void firePositionRank(final int p0, final MultiRankData p1);
    
    int getTotalPostionRankNumByForceId(final int p0);
    
    void updatePlayerLv(final int p0, final int p1);
    
    byte[] getCurRankInfo(final PlayerDto p0, final int p1);
    
    byte[] getNationTaskReward(final PlayerDto p0, final int p1);
    
    byte[] getRewardWholePointKill(final PlayerDto p0);
    
    byte[] getCurNationTaskSimpleInfo(final int p0, final List<NationTaskAnd> p1);
    
    List<NationTaskAnd> getNationTaskAnds(final int p0);
    
    byte[] startNationTask(final PlayerDto p0, final int p1);
    
    void initRankerAndRelativeInfo();
    
    int hasBarTasks(final int p0);
    
    void updateKillNum(final int p0, final int p1, final int p2, final long p3);
    
    void updateWholeKillNum(final int p0, final int p1);
    
    void updateScoreRank(final int p0, final int p1, final int p2);
    
    void nationTaskIsOver(final int p0, final int p1);
    
    int hasNationTasks(final int p0);
    
    int getBarbCity(final int p0);
    
    void barTaskTimeIsOver(final String p0);
    
    void startNationTasks();
    
    void scanNationTask();
    
    void nationTaskTimeIsOver();
    
    byte[] getPersonalInvestmentInfo(final PlayerDto p0);
    
    byte[] investCopper(final PlayerDto p0);
    
    byte[] investCdRecover(final PlayerDto p0);
    
    void checkInvestSerialChange();
    
    long getBarbarianNationTaskEnd();
    
    byte[] investCdRecoverConfirm(final PlayerDto p0);
    
    byte[] getOccupyRankInfo(final int p0, final PlayerDto p1, final int p2);
    
    byte[] getRankerReward(final PlayerDto p0, final int p1);
    
    void updatePlayerChallengeInfo(final int p0, final int p1, final int p2);
    
    void updatePlayerOccupyCItyInfo(final int p0, final int p1, final int p2);
    
    void titleInfoByCurTitle(final int p0, final int p1, final JsonDocument p2, final int p3);
    
    boolean duringTaskByTarget(final int p0);
    
    List<Tuple<Integer, Long>> getAttDefNationTaskInfos();
    
    int getNationTaskNextKillNum(final BaseRanker p0, final int p1, final int p2, final int p3, final int p4);
    
    byte[] useInvestCoupon(final PlayerDto p0, final int p1);
    
    List<Integer> getLastAttDefChooseCities();
    
    void clearWholeKill();
    
    void clearScoreRank();
    
    void pushWholeKill();
    
    void addWorld(final int p0, final int p1);
    
    void leaveWorld(final int p0);
    
    void initNationLv();
    
    void boBaoWhenBarbarainInvadeIsOver();
    
    int nowDays();
    
    int getCountryNpcDefDays();
    
    void initForceInfo();
    
    void updateTodayScoreRank(final String p0);
    
    void updateTryRank(final String p0);
    
    void updatePRank(final String p0);
    
    ScoreRank getScoreRank();
    
    TryRank getTryRank();
    
    PRank getPRank();
    
    void clearTryRank(final int p0);
    
    void clearPRank(final int p0);
    
    Tuple<Integer, Long> getNextInvadeInfo(final int p0);
    
    void fireMangWangLing(final String p0);
    
    void updateTodayKillNum(final int p0, final int p1);
    
    void clearFeatRank();
    
    FeatRank getFeatRank();
    
    void nationTaskHJCityOccupy(final int p0, final int p1);
    
    void initTaskRelativeInfo();
    
    void addFeat(final int p0, final int p1);
    
    int getFromDatabase(final int p0);
    
    boolean startNationTaskByType(final int p0, final int p1);
    
    void checkIsYuanXiaoNight(final boolean p0, final int p1);
    
    byte[] investYx(final PlayerDto p0, final int p1);
    
    byte[] eatLantern(final PlayerDto p0);
    
    LanternRank getLanternRank();
    
    NationFestival getNationFestival();
}
