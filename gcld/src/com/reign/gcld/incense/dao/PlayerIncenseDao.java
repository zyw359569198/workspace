package com.reign.gcld.incense.dao;

import com.reign.gcld.incense.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;

@Component("playerIncenseDao")
public class PlayerIncenseDao extends BaseDao<PlayerIncense> implements IPlayerIncenseDao
{
    @Override
	public PlayerIncense read(final int playerId) {
        return (PlayerIncense)this.getSqlSession().selectOne("com.reign.gcld.incense.domain.PlayerIncense.read", (Object)playerId);
    }
    
    @Override
	public PlayerIncense readForUpdate(final int playerId) {
        return (PlayerIncense)this.getSqlSession().selectOne("com.reign.gcld.incense.domain.PlayerIncense.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerIncense> getModels() {
        return (List<PlayerIncense>)this.getSqlSession().selectList("com.reign.gcld.incense.domain.PlayerIncense.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.incense.domain.PlayerIncense.getModelSize");
    }
    
    @Override
	public int create(final PlayerIncense playerIncense) {
        return this.getSqlSession().insert("com.reign.gcld.incense.domain.PlayerIncense.create", playerIncense);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.incense.domain.PlayerIncense.deleteById", playerId);
    }
    
    @Override
	public int useIncenseNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.useIncenseNum", playerId);
    }
    
    @Override
	public int addCopperTimes(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.addCopperTimes", playerId);
    }
    
    @Override
	public int addWoodTimes(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.addWoodTimes", playerId);
    }
    
    @Override
	public int addFoodTimes(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.addFoodTimes", playerId);
    }
    
    @Override
	public int addIronTimes(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.addIronTimes", playerId);
    }
    
    @Override
	public int addGemTimes(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.addGemTimes", playerId);
    }
    
    @Override
	public int setOpenBit(final int playerId, final int godId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("godId", godId);
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.setOpenBit", params);
    }
    
    @Override
	public int addIncenseNum(final int playerId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("num", num).addParam("max", 25);
        final int result = this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.addIncenseNum", params);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("freeIncenseNum", this.getIncenseNum(playerId)));
        return result;
    }
    
    @Override
	public int getIncenseNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.incense.domain.PlayerIncense.getIncenseNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int resetIncenseTimes() {
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.resetIncenseTimes");
    }
    
    @Override
	public int getOpenBit(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.incense.domain.PlayerIncense.getOpenBit", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int addIncenseNumByForceId(final int forceId, final int num) {
        final Params params = new Params();
        params.addParam("forceId", forceId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.incense.domain.PlayerIncense.addIncenseNumByForceId", params);
    }
}
