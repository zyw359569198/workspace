package com.reign.gcld.general.dao;

import com.reign.gcld.general.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerGeneralDao")
public class PlayerGeneralDao extends BaseDao<PlayerGeneral> implements IPlayerGeneralDao
{
    @Override
	public PlayerGeneral read(final int vId) {
        return (PlayerGeneral)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneral.read", (Object)vId);
    }
    
    @Override
	public PlayerGeneral readForUpdate(final int vId) {
        return (PlayerGeneral)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneral.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerGeneral> getModels() {
        return (List<PlayerGeneral>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneral.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneral.getModelSize");
    }
    
    @Override
	public int create(final PlayerGeneral playerGeneral) {
        return this.getSqlSession().insert("com.reign.gcld.general.domain.PlayerGeneral.create", playerGeneral);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.general.domain.PlayerGeneral.deleteById", vId);
    }
    
    @Override
	public PlayerGeneral getPlayerGeneral(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return (PlayerGeneral)this.getSqlSession().selectOne("com.reign.gcld.general.domain.PlayerGeneral.getPlayerGeneral", (Object)params);
    }
    
    @Override
	public List<PlayerGeneral> getGeneralList(final int playerId) {
        return (List<PlayerGeneral>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneral.getGeneralList", (Object)playerId);
    }
    
    @Override
	public List<PlayerGeneral> getGeneralListByType(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", type);
        return (List<PlayerGeneral>)this.getSqlSession().selectList("com.reign.gcld.general.domain.PlayerGeneral.getGeneralListByType", (Object)params);
    }
    
    @Override
	public int updateForcesDate(final int playerId, final int generalId, final Date date, double forces, final long max) {
        forces = (int)forces;
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("forces", forces).addParam("date", date).addParam("max", max);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneral.updateForcesDate", params);
    }
    
    @Override
	public int updateState(final int playerId, final int generalId, final Date date, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId).addParam("date", date).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.general.domain.PlayerGeneral.updateState", params);
    }
    
    @Override
	public int deleteByPlayerIdAndGeneralId(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("generalId", generalId);
        return this.getSqlSession().delete("com.reign.gcld.general.domain.PlayerGeneral.deleteByPlayerIdAndGeneralId", params);
    }
}
