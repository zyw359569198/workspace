package com.reign.gcld.affair.dao;

import com.reign.gcld.affair.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("civilAffairDao")
public class CivilAffairDao extends BaseDao<CivilAffair> implements ICivilAffairDao
{
    @Override
	public CivilAffair read(final int vId) {
        return (CivilAffair)this.getSqlSession().selectOne("com.reign.gcld.affair.domain.CivilAffair.read", (Object)vId);
    }
    
    @Override
	public CivilAffair readForUpdate(final int vId) {
        return (CivilAffair)this.getSqlSession().selectOne("com.reign.gcld.affair.domain.CivilAffair.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<CivilAffair> getModels() {
        return (List<CivilAffair>)this.getSqlSession().selectList("com.reign.gcld.affair.domain.CivilAffair.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.affair.domain.CivilAffair.getModelSize");
    }
    
    @Override
	public int create(final CivilAffair civilAffair) {
        return this.getSqlSession().insert("com.reign.gcld.affair.domain.CivilAffair.create", civilAffair);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.affair.domain.CivilAffair.deleteById", vId);
    }
    
    @Override
	public List<CivilAffair> getAffairList(final int playerId, final int generalId) {
        final Params param = new Params();
        param.addParam("playerId", playerId);
        param.addParam("generalId", generalId);
        return (List<CivilAffair>)this.getSqlSession().selectList("com.reign.gcld.affair.domain.CivilAffair.getAffairList", (Object)param);
    }
    
    @Override
	public List<CivilAffair> getAffairs(final int playerId) {
        return (List<CivilAffair>)this.getSqlSession().selectList("com.reign.gcld.affair.domain.CivilAffair.getAffairs", (Object)playerId);
    }
    
    @Override
	public int getRunningAffairCount(final int playerId, final int generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.affair.domain.CivilAffair.getRunningAffairCount", (Object)params);
    }
    
    @Override
	public CivilAffair getAffair(final int playerId, final int generalId, final int affairId) {
        final Params param = new Params();
        param.addParam("playerId", playerId);
        param.addParam("generalId", generalId);
        param.addParam("affairId", affairId);
        return (CivilAffair)this.getSqlSession().selectOne("com.reign.gcld.affair.domain.CivilAffair.getAffair", (Object)param);
    }
    
    @Override
	public void updageStartTime(final int playerId, final int generalId, final int affairId, final Date date) {
        final Params param = new Params();
        param.addParam("playerId", playerId);
        param.addParam("generalId", generalId);
        param.addParam("affairId", affairId);
        param.addParam("date", date);
        this.getSqlSession().update("com.reign.gcld.affair.domain.CivilAffair.updageStartTime", param);
    }
    
    @Override
	public void updageCivilStartTime(final int playerId, final int generalId, final Date date) {
        final Params param = new Params();
        param.addParam("playerId", playerId);
        param.addParam("generalId", generalId);
        param.addParam("date", date);
        this.getSqlSession().update("com.reign.gcld.affair.domain.CivilAffair.updageCivilStartTime", param);
    }
    
    @Override
	public void addAffairLevel(final int vId, final int addLv) {
        final Params param = new Params();
        param.addParam("vId", vId);
        param.addParam("addLv", addLv);
        this.getSqlSession().update("com.reign.gcld.affair.domain.CivilAffair.addAffairLevel", param);
    }
    
    @Override
	public void changeUpgradeShow(final int vId, final int show) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("show", show);
        this.getSqlSession().update("com.reign.gcld.affair.domain.CivilAffair.changeUpgradeShow", params);
    }
}
