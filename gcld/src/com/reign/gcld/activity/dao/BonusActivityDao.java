package com.reign.gcld.activity.dao;

import com.reign.gcld.activity.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("bonusActivityDao")
public class BonusActivityDao extends BaseDao<BonusActivity> implements IBonusActivityDao
{
    @Override
	public BonusActivity read(final int playerId) {
        return (BonusActivity)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.BonusActivity.read", (Object)playerId);
    }
    
    @Override
	public BonusActivity readForUpdate(final int playerId) {
        return (BonusActivity)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.BonusActivity.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<BonusActivity> getModels() {
        return (List<BonusActivity>)this.getSqlSession().selectList("com.reign.gcld.activity.domain.BonusActivity.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.BonusActivity.getModelSize");
    }
    
    @Override
	public int create(final BonusActivity bonusActivity) {
        return this.getSqlSession().insert("com.reign.gcld.activity.domain.BonusActivity.create", bonusActivity);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.activity.domain.BonusActivity.deleteById", playerId);
    }
    
    @Override
	public int clearAll() {
        return this.getSqlSession().update("com.reign.gcld.activity.domain.BonusActivity.clearAll");
    }
    
    @Override
	public int addConsumeGold(final int playerId, final int gold) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("gold", gold);
        return this.getSqlSession().update("com.reign.gcld.activity.domain.BonusActivity.addConsumeGold", params);
    }
    
    @Override
	public int getConsumeGold(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.activity.domain.BonusActivity.getConsumeGold", (Object)playerId);
        return (result == null) ? 0 : result;
    }
}
