package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("officerTokenDao")
public class OfficerTokenDao extends BaseDao<OfficerToken> implements IOfficerTokenDao
{
    @Override
	public OfficerToken read(final int forceid) {
        return (OfficerToken)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerToken.read", (Object)forceid);
    }
    
    @Override
	public OfficerToken readForUpdate(final int forceid) {
        return (OfficerToken)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerToken.readForUpdate", (Object)forceid);
    }
    
    @Override
	public List<OfficerToken> getModels() {
        return (List<OfficerToken>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.OfficerToken.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerToken.getModelSize");
    }
    
    @Override
	public int create(final OfficerToken officerToken) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.OfficerToken.create", officerToken);
    }
    
    @Override
	public int deleteById(final int forceid) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.OfficerToken.deleteById", forceid);
    }
    
    @Override
	public OfficerToken getTokenByForceIdAndOfficerId(final int officerId, final int forceId) {
        final Params params = new Params();
        params.addParam("officerId", officerId);
        params.addParam("forceId", forceId);
        return (OfficerToken)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.OfficerToken.getTokenByForceIdAndOfficerId", (Object)params);
    }
    
    @Override
	public void addTokenTimer() {
        this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerToken.updateTokenTimer");
    }
    
    @Override
	public void resetBattle(final int forceId, final int officerId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("officerId", officerId);
        this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerToken.resetBattle", params);
    }
    
    @Override
	public void addKillTokenLimited(final int forceId, final int limit) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("limit", limit);
        this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerToken.addKillTokenLimited", params);
    }
    
    @Override
	public void decreaseKillTokenLimited(final int forceId, final int officerId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("officerId", officerId);
        this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerToken.decreaseKillTokenLimited", params);
    }
    
    @Override
	public void updateTokenInfo(final int forceId, final int officerId, final String tokenInfo) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        params.addParam("officerId", officerId);
        params.addParam("tokenInfo", tokenInfo);
        this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerToken.updateTokenInfo", params);
    }
    
    @Override
	public void setTokenNum(final int forceId, final int officerId, final int num) {
        final Params params = new Params();
        params.addParam("officerId", officerId);
        params.addParam("forceId", forceId);
        params.addParam("num", num);
        this.getSqlSession().update("com.reign.gcld.battle.domain.OfficerToken.setTokenNum", params);
    }
}
