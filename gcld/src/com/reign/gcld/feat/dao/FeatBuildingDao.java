package com.reign.gcld.feat.dao;

import com.reign.gcld.feat.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("featBuildingDao")
public class FeatBuildingDao extends BaseDao<FeatBuilding> implements IFeatBuildingDao
{
    @Override
	public FeatBuilding read(final int playerId) {
        return (FeatBuilding)this.getSqlSession().selectOne("com.reign.gcld.feat.domain.FeatBuilding.read", (Object)playerId);
    }
    
    @Override
	public FeatBuilding readForUpdate(final int playerId) {
        return (FeatBuilding)this.getSqlSession().selectOne("com.reign.gcld.feat.domain.FeatBuilding.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<FeatBuilding> getModels() {
        return (List<FeatBuilding>)this.getSqlSession().selectList("com.reign.gcld.feat.domain.FeatBuilding.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.feat.domain.FeatBuilding.getModelSize");
    }
    
    @Override
	public int create(final FeatBuilding featBuilding) {
        return this.getSqlSession().insert("com.reign.gcld.feat.domain.FeatBuilding.create", featBuilding);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.feat.domain.FeatBuilding.deleteById", playerId);
    }
    
    @Override
	public int getFeat(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.feat.domain.FeatBuilding.getFeat", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int addFeat(final int playerId, final int feat) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("feat", feat);
        return this.getSqlSession().update("com.reign.gcld.feat.domain.FeatBuilding.addFeat", params);
    }
    
    @Override
	public int resetFeat(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.feat.domain.FeatBuilding.resetFeat", playerId);
    }
}
