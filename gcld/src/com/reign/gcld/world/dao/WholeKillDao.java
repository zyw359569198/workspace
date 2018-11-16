package com.reign.gcld.world.dao;

import com.reign.gcld.world.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("wholeKillDao")
public class WholeKillDao extends BaseDao<WholeKill> implements IWholeKillDao
{
    @Override
	public WholeKill read(final int playerId) {
        return (WholeKill)this.getSqlSession().selectOne("com.reign.gcld.world.domain.WholeKill.read", (Object)playerId);
    }
    
    @Override
	public WholeKill readForUpdate(final int playerId) {
        return (WholeKill)this.getSqlSession().selectOne("com.reign.gcld.world.domain.WholeKill.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<WholeKill> getModels() {
        return (List<WholeKill>)this.getSqlSession().selectList("com.reign.gcld.world.domain.WholeKill.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.world.domain.WholeKill.getModelSize");
    }
    
    @Override
	public int create(final WholeKill wholeKill) {
        return this.getSqlSession().insert("com.reign.gcld.world.domain.WholeKill.create", wholeKill);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.world.domain.WholeKill.deleteById", playerId);
    }
    
    @Override
	public int updateKillNum(final int playerId, final int killNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("killNum", killNum);
        return this.getSqlSession().update("com.reign.gcld.world.domain.WholeKill.updateKillNum", params);
    }
    
    @Override
	public List<WholeKill> getRankList() {
        return (List<WholeKill>)this.getSqlSession().selectList("com.reign.gcld.world.domain.WholeKill.getRankList");
    }
    
    @Override
	public void updateWholeKill() {
        this.getSqlSession().update("com.reign.gcld.world.domain.WholeKill.updateWholeKill");
    }
    
    @Override
	public void updateKillRank(final int playerId, final int lastRank) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("lastRank", lastRank);
        this.getSqlSession().update("com.reign.gcld.world.domain.WholeKill.updateKillRank", params);
    }
    
    @Override
	public void received(final int playerId) {
        this.getSqlSession().update("com.reign.gcld.world.domain.WholeKill.received", playerId);
    }
}
