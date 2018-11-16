package com.reign.gcld.slave.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.slave.domain.*;
import java.util.*;

public interface IPlayerSlaveDao extends IBaseDao<PlayerSlave>
{
    PlayerSlave read(final int p0);
    
    PlayerSlave readForUpdate(final int p0);
    
    List<PlayerSlave> getModels();
    
    int getModelSize();
    
    int create(final PlayerSlave p0);
    
    int deleteById(final int p0);
    
    @Deprecated
    boolean isSlave(final int p0);
    
    boolean isSlave2(final int p0, final int p1);
    
    List<PlayerSlave> getListByPlayerId(final int p0);
    
    PlayerSlave getBySlaveId(final int p0);
    
    List<PlayerSlave> getListByPlayerIdAndSlaveId(final int p0, final int p1);
    
    PlayerSlave getOneSlave(final int p0, final int p1);
    
    int setTotalCdAndSlashTimes(final int p0, final int p1);
    
    int setCdAndJobIdAndSlashTimes(final int p0, final Date p1, final int p2);
    
    int getSizeByPlayerId(final int p0);
    
    int clearCell(final int p0);
    
    int getEmptyCellSize(final int p0);
    
    PlayerSlave getSlave(final int p0);
    
    int getEmptyCell(final int p0);
    
    int placeSlaveInCell(final int p0, final int p1, final Date p2, final int p3);
    
    @Deprecated
    int setCdAndJobId(final int p0, final Date p1, final int p2);
    
    int setCd(final int p0, final Date p1);
    
    List<PlayerSlave> getMySlaveList(final int p0);
    
    int setBeginWorkTime(final int p0, final Date p1);
    
    List<Integer> getSlaveIdList(final int p0);
    
    int getPlayerIdByVId(final int p0);
    
    int clear(final int p0);
    
    int getCellIndex(final int p0, final int p1);
    
    int getCatchNumToday(final int p0);
    
    int lashSlave(final int p0);
    
    PlayerSlave getBySlaveIdAndGeneralId(final int p0, final int p1);
    
    int releaseSlave(final int p0);
    
    int releaseAll();
}
