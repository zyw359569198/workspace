package com.reign.gcld.kfzb.dao;

import com.reign.gcld.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfzbFeastDao")
public class KfzbFeastDao extends BaseDao<KfzbFeast> implements IKfzbFeastDao
{
    @Override
	public KfzbFeast read(final int playerId) {
        return (KfzbFeast)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbFeast.read", (Object)playerId);
    }
    
    @Override
	public KfzbFeast readForUpdate(final int playerId) {
        return (KfzbFeast)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbFeast.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<KfzbFeast> getModels() {
        return (List<KfzbFeast>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbFeast.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbFeast.getModelSize");
    }
    
    @Override
	public int create(final KfzbFeast kfzbFeast) {
        return this.getSqlSession().insert("com.reign.gcld.kfzb.domain.KfzbFeast.create", kfzbFeast);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.kfzb.domain.KfzbFeast.deleteById", playerId);
    }
    
    @Override
	public int addDrink(final int playerId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.addDrink", params);
    }
    
    @Override
	public int consumeDrink(final int playerId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.consumeDrink", params);
    }
    
    @Override
	public int setFreeCardInit(final int freeCard) {
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.setFreeCardInit", freeCard);
    }
    
    @Override
	public int consumeFreeCard(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.consumeFreeCard", playerId);
    }
    
    @Override
	public int addGoldCard(final int playerId, final int goldCard) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("goldCard", goldCard);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.addGoldCard", params);
    }
    
    @Override
	public int consumeGoldCard(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.consumeGoldCard", playerId);
    }
    
    @Override
	public int getDrink(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbFeast.getDrink", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int setXiqoqian(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.setXiqoqian", playerId);
    }
    
    @Override
	public int clearData() {
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.clearData");
    }
    
    @Override
	public List<KfzbFeast> getMailList() {
        return (List<KfzbFeast>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbFeast.getMailList");
    }
    
    @Override
	public int addFreeCard(final int playerId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbFeast.addFreeCard", params);
    }
}
