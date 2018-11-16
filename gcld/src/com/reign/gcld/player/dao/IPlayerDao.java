package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;

public interface IPlayerDao extends IBaseDao<Player>
{
    Player read(final int p0);
    
    Player readForUpdate(final int p0);
    
    List<Player> getModels();
    
    int getModelSize();
    
    int create(final Player p0);
    
    int deleteById(final int p0);
    
    Player getPlayerByName(final String p0);
    
    Player getPlayerByNameAndYx(final String p0, final String p1);
    
    Player queryPlayer(final String p0, final String p1, final String p2);
    
    int getPlayerIdByNameAndYx(final String p0, final String p1);
    
    List<Player> getPlayerByUserId(final String p0, final String p1);
    
    List<Player> getPlayerLevelRankList(final int p0);
    
    List<Player> getSamePlayerLevel(final int p0, final int p1, final int p2);
    
    List<Player> getPlayerLevelRankList(final int p0, final int p1);
    
    boolean consumeGold(final Player p0, final Chargeitem p1);
    
    boolean consumeGold(final Player p0, final int p1, final Object p2);
    
    void addSysGold(final Player p0, final int p1, final Object p2);
    
    boolean addUserGold(final Player p0, final int p1, final Object p2);
    
    void addConsumeLv(final int p0, final int p1);
    
    void setSysGold(final Player p0, final int p1, final Object p2);
    
    void setUserGold(final Player p0, final int p1, final Object p2);
    
    boolean updatePlayerLv(final int p0, final int p1);
    
    boolean updateMaxLv(final int p0, final int p1);
    
    int getForceMemberCount(final int p0);
    
    int updatePlayerForceId(final int p0, final int p1);
    
    int updatePlayerNameAndPic(final int p0, final String p1, final int p2);
    
    int updatePlayerConsumeLv(final int p0, final int p1);
    
    int updatePowerId(final int p0, final int p1);
    
    int updateLoginTime(final int p0, final Date p1);
    
    int updateState(final int p0, final int p1);
    
    int updateQuitTime(final int p0, final Date p1);
    
    void deletePlayer(final int p0, final Date p1);
    
    void retrievePlayer(final int p0);
    
    int getRoleCount(final String p0, final String p1);
    
    List<Player> getPlayerByLevel(final int p0);
    
    boolean canConsumeMoney(final Player p0, final int p1);
    
    Tuple<Integer, Integer> getGoldStatistics(final int p0, final int p1);
    
    int updatePlayerName(final int p0, final String p1);
    
    void updatePlayerServerId(final Integer p0, final int p1);
    
    List<Player> getPlayerHasRegisterAuction();
    
    void consumeUserGold(final Player p0, final int p1, final Object p2);
    
    int getPlayerLv(final int p0);
    
    int getConsumeLv(final int p0);
    
    List<Integer> getPlayerIdList();
    
    String getPlayerName(final int p0);
    
    int getForceIdByPlayerName(final String p0);
    
    List<Integer> getPlayerIdListByUserIdAndYx(final String p0, final String p1);
    
    int getTotalUserGold(final int p0);
    
    int getTotalTicketGold(final int p0);
    
    boolean addTotalUserGold(final int p0, final int p1);
    
    int addTotalTicketGold(final int p0, final int p1);
    
    boolean setTotalUserGold(final Player p0, final int p1);
    
    int setYxSource(final int p0, final String p1);
    
    void resetTotalUserGold();
    
    int resetTotalTicketGold();
    
    String getYxSource(final int p0);
    
    List<Player> getPlayerNoDeleteByUserIdAndYx(final String p0, final String p1);
    
    List<Integer> getPlayerIdByUp(final int p0, final int p1);
    
    List<Integer> getPlayerIdListByDown(final int p0, final int p1);
    
    int getForceId(final int p0);
    
    List<Integer> getPlayerIdListByEqual(final int p0, final int p1);
    
    int getDefaultPlayerId(final String p0, final String p1);
    
    boolean consumeGoldForKFGZ(final Player p0, final int p1);
    
    List<Player> getPlayerList(final String p0, final Date p1, final int p2, final int p3);
    
    int getSizeByYxAndDate(final String p0, final Date p1);
    
    Date getLoginTimeByPlayerId(final int p0);
    
    Date getQuitTimeByPlayerId(final int p0);
    
    void updateDailyOnlineTime(final int p0, final int p1);
    
    void resetDailyOnlineTime();
    
    int getDailyOnlineTime(final int p0);
    
    void setDailyOnlineTime(final int p0, final int p1);
    
    int setGm(final int p0, final int p1);
    
    List<Player> getByGm();
    
    int clearDefaultPay(final String p0, final String p1);
    
    int setDefaultPay(final int p0);
    
    int modifyUserId(final String p0, final String p1, final String p2);
    
    List<Integer> getPlayerIdListAboveMinLv(final int p0);
    
    int getRoleCountByUid(final String p0);
    
    List<Player> getPlayerOnlyByUserId(final String p0);
    
    List<PlayerPicLv> getPicLvsByPids(final List<Integer> p0);
    
    List<Player> getByPlayerIds(final List<Integer> p0);
    
    List<Player> getByPlayerIdsCC(final List<Integer> p0);
    
    int updatePics(final List<Player> p0);
    
    int updatePlayerSSP(final Integer p0, final String p1);
}
