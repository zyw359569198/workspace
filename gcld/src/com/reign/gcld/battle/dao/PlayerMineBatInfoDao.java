package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerMineBatInfoDao")
public class PlayerMineBatInfoDao extends BaseDao<PlayerMineBatInfo> implements IPlayerMineBatInfoDao
{
    @Override
	public PlayerMineBatInfo read(final int mineId) {
        return (PlayerMineBatInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerMineBatInfo.read", (Object)mineId);
    }
    
    @Override
	public PlayerMineBatInfo readForUpdate(final int mineId) {
        return (PlayerMineBatInfo)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerMineBatInfo.readForUpdate", (Object)mineId);
    }
    
    @Override
	public List<PlayerMineBatInfo> getModels() {
        return (List<PlayerMineBatInfo>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerMineBatInfo.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerMineBatInfo.getModelSize");
    }
    
    @Override
	public int create(final PlayerMineBatInfo playerMineBatInfo) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerMineBatInfo.create", playerMineBatInfo);
    }
    
    @Override
	public int deleteById(final int mineId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerMineBatInfo.deleteById", mineId);
    }
    
    @Override
	public int updateInfo(final int mineId, final String battleInfo) {
        final Params params = new Params();
        params.addParam("mineId", mineId).addParam("battleInfo", battleInfo);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerMineBatInfo.updateInfo", params);
    }
}
