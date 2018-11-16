package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("nationInfoDao")
public class NationInfoDao extends BaseDao<NationInfo> implements INationInfoDao
{
    @Override
	public NationInfo read(final int forceId) {
        return (NationInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.NationInfo.read", (Object)forceId);
    }
    
    @Override
	public NationInfo readForUpdate(final int forceId) {
        return (NationInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.NationInfo.readForUpdate", (Object)forceId);
    }
    
    @Override
	public List<NationInfo> getModels() {
        return (List<NationInfo>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.NationInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.NationInfo.getModelSize");
    }
    
    @Override
	public int create(final NationInfo nationInfo) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.NationInfo.create", nationInfo);
    }
    
    @Override
	public int deleteById(final int forceId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.NationInfo.deleteById", forceId);
    }
    
    @Override
	public int updateRankInfo(final int forceId, final String rankInfo) {
        final Params params = new Params();
        params.addParam("forceId", forceId).addParam("rankInfo", rankInfo);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.NationInfo.updateRankInfo", params);
    }
    
    @Override
	public int addHzWinNumByForceId(final int forceId) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.NationInfo.addHzWinNumByForceId", forceId);
    }
}
