package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("barbariansKillInfoDao")
public class BarbariansKillInfoDao extends BaseDao<BarbariansKillInfo> implements IBarbariansKillInfoDao
{
    @Override
	public BarbariansKillInfo read(final int playerid) {
        return (BarbariansKillInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.BarbariansKillInfo.read", (Object)playerid);
    }
    
    @Override
	public BarbariansKillInfo readForUpdate(final int playerid) {
        return (BarbariansKillInfo)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.BarbariansKillInfo.readForUpdate", (Object)playerid);
    }
    
    @Override
	public List<BarbariansKillInfo> getModels() {
        return (List<BarbariansKillInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.BarbariansKillInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.BarbariansKillInfo.getModelSize");
    }
    
    @Override
	public int create(final BarbariansKillInfo barbariansKillInfo) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.BarbariansKillInfo.create", barbariansKillInfo);
    }
    
    @Override
	public int deleteById(final int playerid) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.BarbariansKillInfo.deleteById", playerid);
    }
    
    @Override
	public List<BarbariansKillInfo> getByforceId(final int i) {
        final Params params = new Params();
        params.addParam("forceId", i);
        final List<BarbariansKillInfo> result = (List<BarbariansKillInfo>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.BarbariansKillInfo.getByforceId", (Object)params);
        return result;
    }
    
    public void updateIsRewarded(final int playerId, final int i) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("value", i);
        this.getSqlSession().update("com.reign.gcld.rank.domain.BarbariansKillInfo.updateIsRewarded", params);
    }
    
    public void updateKillNum(final int playerId, final int killTotal) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("killTotal", killTotal);
        this.getSqlSession().update("com.reign.gcld.rank.domain.BarbariansKillInfo.updateKillNum", params);
    }
    
    public int getKillSumByForceId(final int i) {
        final Params params = new Params();
        params.addParam("forceId", i);
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.BarbariansKillInfo.getKillSumByForceId", (Object)params);
        return (result == null) ? 0 : result;
    }
    
    public void deleteByForceId(final Integer forceId) {
        final Params params = new Params();
        params.addParam("forceId", forceId);
        this.getSqlSession().update("com.reign.gcld.rank.domain.BarbariansKillInfo.deleteByForceId", params);
    }
}
