package com.reign.gcld.kfzb.dao;

import com.reign.gcld.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("kfzbSupportDao")
public class KfzbSupportDao extends BaseDao<KfzbSupport> implements IKfzbSupportDao
{
    @Override
	public KfzbSupport read(final int roundId) {
        return (KfzbSupport)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbSupport.read", (Object)roundId);
    }
    
    @Override
	public KfzbSupport readForUpdate(final int roundId) {
        return (KfzbSupport)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbSupport.readForUpdate", (Object)roundId);
    }
    
    @Override
	public List<KfzbSupport> getModels() {
        return (List<KfzbSupport>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbSupport.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbSupport.getModelSize");
    }
    
    @Override
	public int create(final KfzbSupport kfzbSupport) {
        return this.getSqlSession().insert("com.reign.gcld.kfzb.domain.KfzbSupport.create", kfzbSupport);
    }
    
    @Override
	public int deleteById(final int roundId) {
        return this.getSqlSession().delete("com.reign.gcld.kfzb.domain.KfzbSupport.deleteById", roundId);
    }
    
    @Override
	public List<KfzbSupport> getUnRewardedListByWithIndex(final int seasonId, final int matchId, final int roundId, final int flag) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId).addParam("matchId", matchId).addParam("roundId", roundId).addParam("flag", flag);
        return (List<KfzbSupport>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbSupport.getUnRewardedListByWithIndex", (Object)params);
    }
    
    @Override
	public int updateAsRewarded(final int seasonId, final int matchId, final int roundId, final int winnerCId, final int flag) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId).addParam("matchId", matchId).addParam("roundId", roundId).addParam("winnerCId", winnerCId).addParam("flag", flag);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbSupport.updateAsRewarded", params);
    }
    
    @Override
	public int updateAsFailed(final int seasonId, final int matchId, final int roundId, final int winnerCId, final int flag) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId).addParam("matchId", matchId).addParam("roundId", roundId).addParam("winnerCId", winnerCId).addParam("flag", flag);
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbSupport.updateAsFailed", params);
    }
    
    @Override
	public KfzbSupport getByUniqIndex(final int playerId, final int seasonId, final int matchId, final int roundId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId).addParam("matchId", matchId).addParam("roundId", roundId);
        return (KfzbSupport)this.getSqlSession().selectOne("com.reign.gcld.kfzb.domain.KfzbSupport.getByUniqIndex", (Object)params);
    }
    
    @Override
	public List<KfzbSupport> getUnTakedSupportInfo(final int playerId, final int seasonId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId);
        return (List<KfzbSupport>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbSupport.getUnTakedSupportTicket", (Object)params);
    }
    
    @Override
	public List<KfzbSupport> getUnTakedSupportInfoBySeasonId(final int seasonId) {
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        return (List<KfzbSupport>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbSupport.getUnTakedSupportInfoBySeasonId", (Object)params);
    }
    
    @Override
	public int updateTaketIt(final KfzbSupport sup) {
        final Params params = new Params();
        params.addParam("seasonId", sup.getSeasonId()).addParam("playerId", sup.getPlayerId()).addParam("matchId", sup.getMatchId()).addParam("roundId", sup.getRoundId());
        return this.getSqlSession().update("com.reign.gcld.kfzb.domain.KfzbSupport.updateTakeIt", params);
    }
    
    public int getPlayerByLayerAndRoundNum(final int seasonId, final Integer playerId, final int currentLayer, final int currentRound) {
        if (currentLayer <= 0 || currentLayer > 4) {
            return 0;
        }
        final int matchIdMin = 1 << currentLayer - 1;
        final int matchIdMax = (1 << currentLayer) - 1;
        final Params params = new Params();
        params.addParam("seasonId", seasonId);
        params.addParam("playerId", playerId);
        params.addParam("matchIdMin", matchIdMin);
        params.addParam("matchIdMax", matchIdMax);
        params.addParam("round", currentRound);
        final List<KfzbSupport> list = (List<KfzbSupport>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbSupport.getPlayerByLayerAndRound", (Object)params);
        if (list == null) {
            return 0;
        }
        return list.size();
    }
    
    public List<KfzbSupport> getByMatchId(final int playerId, final int seasonId, final int matchId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("seasonId", seasonId).addParam("matchId", matchId);
        return (List<KfzbSupport>)this.getSqlSession().selectList("com.reign.gcld.kfzb.domain.KfzbSupport.getByMatchId", (Object)params);
    }
}
