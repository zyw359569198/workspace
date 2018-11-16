package com.reign.gcld.player.dao;

import com.reign.gcld.player.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Component("playerOfficeRelativeDao")
public class PlayerOfficeRelativeDao extends BaseDao<PlayerOfficeRelative> implements IPlayerOfficeRelativeDao
{
    @Override
	public PlayerOfficeRelative read(final int playerId) {
        return (PlayerOfficeRelative)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerOfficeRelative.read", (Object)playerId);
    }
    
    @Override
	public PlayerOfficeRelative readForUpdate(final int playerId) {
        return (PlayerOfficeRelative)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerOfficeRelative.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerOfficeRelative> getModels() {
        return (List<PlayerOfficeRelative>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerOfficeRelative.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerOfficeRelative.getModelSize");
    }
    
    @Override
	public int create(final PlayerOfficeRelative playerOfficeRelative) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.PlayerOfficeRelative.create", playerOfficeRelative);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerOfficeRelative.deleteById", playerId);
    }
    
    @Override
	public int updateReputationTime(final int playerId, final Date reputationTime, final int lastOfficerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("reputationTime", reputationTime);
        params.addParam("lastOfficer", lastOfficerId);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerOfficeRelative.updateReputationTime", params);
    }
    
    @Override
	public List<PlayerOfficeRelative> getListByOfficerId(final int officerId) {
        return (List<PlayerOfficeRelative>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerOfficeRelative.getListByOfficerId", (Object)officerId);
    }
    
    @Override
	public List<PlayerOfficeRelative> getListByReputationTime() {
        return (List<PlayerOfficeRelative>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerOfficeRelative.getListByReputationTime");
    }
    
    @Override
	public int updateSalaryGot(final int playerId, final int salaryGot) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("salaryGot", salaryGot);
        if (salaryGot == 0) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("hasSalary", true));
        }
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerOfficeRelative.updateSalaryGot", params);
    }
    
    @Override
	public List<PlayerOfficeRelative> getListOtherOfficerId(final int leftGuardOfficerid, final int rightGuardOfficerid, final int defaultOfficerid) {
        final Params params = new Params();
        params.addParam("left", leftGuardOfficerid);
        params.addParam("right", rightGuardOfficerid);
        params.addParam("farmer", defaultOfficerid);
        return (List<PlayerOfficeRelative>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerOfficeRelative.getListOtherOfficerId", (Object)params);
    }
    
    @Override
	public int updateOfficerId(final int playerId, final int officerId) {
        final Date date = new Date();
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("officerId", officerId).addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerOfficeRelative.updateOfficerId", params);
    }
    
    @Override
	public int updateOfficerNpc(final int playerId, final int buildingId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("buildingId", buildingId);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerOfficeRelative.updateOfficerNpc", params);
    }
    
    @Override
	public int updateHighestOfficer(final int playerId, final int newOfficerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("officerId", newOfficerId);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerOfficeRelative.updateHighestOfficer", params);
    }
    
    @Override
	public int getOfficerId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerOfficeRelative.getOfficerId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
}
