package com.reign.gcld.kfzb.dao;

import com.reign.gcld.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfzbInfoDao")
public class KfzbInfoDao extends BaseDao<KfzbInfo> implements IKfzbInfoDao
{
    @Override
	public KfzbInfo read(final int seasonId) {
        return (KfzbInfo)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbInfo.read", (Object)seasonId);
    }
    
    @Override
	public KfzbInfo readForUpdate(final int seasonId) {
        return (KfzbInfo)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbInfo.readForUpdate", (Object)seasonId);
    }
    
    @Override
	public List<KfzbInfo> getModels() {
        return (List<KfzbInfo>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbInfo.getModelSize");
    }
    
    @Override
	public int create(final KfzbInfo kfzbInfo) {
        return this.getSqlSession().insert("com.reign.gcld.kfzb.domain.KfzbInfo.create", kfzbInfo);
    }
    
    @Override
	public int deleteById(final int seasonId) {
        return this.getSqlSession().delete("com.reign.gcld.kfzb.domain.KfzbInfo.deleteById", seasonId);
    }
    
    @Override
	public KfzbInfo getByPlayerIdSeasonId(final int playerId, final int seasonId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId);
        return (KfzbInfo)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbInfo.getByPlayerIdSeasonId", (Object)params);
    }
    
    @Override
	public int updateSupport1DecreaseFlower1(final int playerId, final int seasonId, final String info) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId).addParam("support1", info);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbInfo.updateSupport1DecreaseFlower1", params);
    }
    
    @Override
	public int updateSupport2DecreaseFlower2(final int playerId, final int seasonId, final String info) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId).addParam("support2", info);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbInfo.updateSupport2DecreaseFlower2", params);
    }
    
    @Override
	public List<KfzbInfo> getBySeasonId(final int seasonId) {
        return (List<KfzbInfo>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbInfo.getBySeasonId", (Object)seasonId);
    }
    
    @Override
	public int buyFlower1(final Integer playerId, final Integer seasonId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbInfo.buyFlower1", params);
    }
    
    @Override
	public int buyFlower2(final Integer playerId, final Integer seasonId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbInfo.buyFlower2", params);
    }
}
