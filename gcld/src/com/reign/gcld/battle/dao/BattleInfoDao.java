package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("battleInfoDao")
public class BattleInfoDao extends BaseDao<BattleInfo> implements IBattleInfoDao
{
    @Override
	public BattleInfo read(final int battleId) {
        return (BattleInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BattleInfo.read", (Object)battleId);
    }
    
    @Override
	public BattleInfo readForUpdate(final int battleId) {
        return (BattleInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BattleInfo.readForUpdate", (Object)battleId);
    }
    
    @Override
	public List<BattleInfo> getModels() {
        return (List<BattleInfo>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.BattleInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.BattleInfo.getModelSize");
    }
    
    @Override
	public int create(final BattleInfo battleInfo) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.BattleInfo.create", battleInfo);
    }
    
    @Override
	public int deleteById(final int battleId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.BattleInfo.deleteById", battleId);
    }
    
    @Override
	public int deleteByBattleId(final String battleId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.BattleInfo.deleteByBattleId", battleId);
    }
    
    @Override
	public int createBattle(final BattleInfo battleInfo) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.BattleInfo.createBattle", battleInfo);
    }
}
