package com.reign.gcld.slave.dao;

import com.reign.gcld.slave.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerSlaveDao")
public class PlayerSlaveDao extends BaseDao<PlayerSlave> implements IPlayerSlaveDao
{
    @Override
	public PlayerSlave read(final int vId) {
        return (PlayerSlave)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.read", (Object)vId);
    }
    
    @Override
	public PlayerSlave readForUpdate(final int vId) {
        return (PlayerSlave)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerSlave> getModels() {
        return (List<PlayerSlave>)this.getSqlSession().selectList("com.reign.gcld.slave.domain.PlayerSlave.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getModelSize");
    }
    
    @Override
	public int create(final PlayerSlave playerSlave) {
        return this.getSqlSession().insert("com.reign.gcld.slave.domain.PlayerSlave.create", playerSlave);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.slave.domain.PlayerSlave.deleteById", vId);
    }
    
    @Override
	public boolean isSlave(final int slaveId) {
        final int result = (int)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.isSlave", (Object)slaveId);
        return result >= 1;
    }
    
    @Override
	public boolean isSlave2(final int slaveId, final int generalId) {
        final Params params = new Params();
        params.addParam("slaveId", slaveId);
        params.addParam("generalId", generalId);
        final int result = (int)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.isSlave2", (Object)params);
        return result >= 1;
    }
    
    @Override
	public List<PlayerSlave> getListByPlayerId(final int playerId) {
        return (List<PlayerSlave>)this.getSqlSession().selectList("com.reign.gcld.slave.domain.PlayerSlave.getListByPlayerId", (Object)playerId);
    }
    
    @Override
	@Deprecated
    public PlayerSlave getBySlaveId(final int slaveId) {
        return (PlayerSlave)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getBySlaveId", (Object)slaveId);
    }
    
    @Override
	public List<PlayerSlave> getListByPlayerIdAndSlaveId(final int playerId, final int slaveId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("slaveId", slaveId);
        return (List<PlayerSlave>)this.getSqlSession().selectList("com.reign.gcld.slave.domain.PlayerSlave.getListByPlayerIdAndSlaveId", (Object)params);
    }
    
    @Override
	public PlayerSlave getOneSlave(final int playerId, final int slaveId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("slaveId", slaveId);
        return (PlayerSlave)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getOneSlave", (Object)params);
    }
    
    @Override
	public int setTotalCdAndSlashTimes(final int vId, final int totalCd) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("totalCd", totalCd);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.setTotalCdAndSlashTimes", params);
    }
    
    @Override
	public int setCdAndJobIdAndSlashTimes(final int vId, final Date cd, final int jobId) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("cd", cd);
        params.addParam("jobId", jobId);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.setCdAndJobIdAndSlashTimes", params);
    }
    
    @Override
	public int getSizeByPlayerId(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getSizeByPlayerId", (Object)playerId);
    }
    
    @Override
	public int clearCell(final int vId) {
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.clearCell", vId);
    }
    
    @Override
	public int getEmptyCellSize(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getEmptyCellSize", (Object)playerId);
    }
    
    @Override
	public PlayerSlave getSlave(final int playerId) {
        return (PlayerSlave)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getSlave", (Object)playerId);
    }
    
    @Override
	public int getEmptyCell(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getEmptyCell", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int placeSlaveInCell(final int vId, final int slaveId, final Date beginWorkTime, final int totalCd) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("slaveId", slaveId);
        params.addParam("beginWorkTime", beginWorkTime);
        params.addParam("totalCd", totalCd);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.placeSlaveInCell", params);
    }
    
    @Override
	public int setCdAndJobId(final int vId, final Date cd, final int jobId) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("cd", cd);
        params.addParam("jobId", jobId);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.setCdAndJobId", params);
    }
    
    @Override
	public int setCd(final int vId, final Date cd) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("cd", cd);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.setCd", params);
    }
    
    @Override
	public List<PlayerSlave> getMySlaveList(final int playerId) {
        return (List<PlayerSlave>)this.getSqlSession().selectList("com.reign.gcld.slave.domain.PlayerSlave.getMySlaveList", (Object)playerId);
    }
    
    @Override
	public int setBeginWorkTime(final int vId, final Date beginWorkTime) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("beginWorkTime", beginWorkTime);
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.setBeginWorkTime", params);
    }
    
    @Override
	public List<Integer> getSlaveIdList(final int playerId) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.slave.domain.PlayerSlave.getSlaveIdList", (Object)playerId);
    }
    
    @Override
	public int getPlayerIdByVId(final int vId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getPlayerIdByVId", (Object)vId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int clear(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.clear", playerId);
    }
    
    @Override
	public int getCellIndex(final int playerId, final int vId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("vId", vId);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getCellIndex", (Object)params);
    }
    
    @Override
	public int getCatchNumToday(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getCatchNumToday", (Object)playerId);
    }
    
    @Override
	public int lashSlave(final int vId) {
        return this.getSqlSession().update("com.reign.gcld.slave.domain.PlayerSlave.lashSlave", vId);
    }
    
    @Override
	public PlayerSlave getBySlaveIdAndGeneralId(final int slaveId, final int generalId) {
        final Params params = new Params();
        params.addParam("slaveId", slaveId);
        params.addParam("generalId", generalId);
        return (PlayerSlave)this.getSqlSession().selectOne("com.reign.gcld.slave.domain.PlayerSlave.getBySlaveIdAndGeneralId", (Object)params);
    }
    
    @Override
	public int releaseSlave(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.slave.domain.PlayerSlave.releaseSlave", playerId);
    }
    
    @Override
	public int releaseAll() {
        return this.getSqlSession().delete("com.reign.gcld.slave.domain.PlayerSlave.releaseAll");
    }
}
