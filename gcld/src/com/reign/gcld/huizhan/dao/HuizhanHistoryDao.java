package com.reign.gcld.huizhan.dao;

import com.reign.gcld.huizhan.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("huizhanHistoryDao")
public class HuizhanHistoryDao extends BaseDao<HuizhanHistory> implements IHuizhanHistoryDao
{
    @Override
	public HuizhanHistory read(final int vId) {
        return (HuizhanHistory)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.HuizhanHistory.read", (Object)vId);
    }
    
    @Override
	public HuizhanHistory readForUpdate(final int vId) {
        return (HuizhanHistory)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.HuizhanHistory.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<HuizhanHistory> getModels() {
        return (List<HuizhanHistory>)this.getSqlSession().selectList("com.reign.gcld.huizhan.domain.HuizhanHistory.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.HuizhanHistory.getModelSize");
    }
    
    @Override
	public int create(final HuizhanHistory huizhanHistory) {
        return this.getSqlSession().insert("com.reign.gcld.huizhan.domain.HuizhanHistory.create", huizhanHistory);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.huizhan.domain.HuizhanHistory.deleteById", vId);
    }
    
    @Override
	public HuizhanHistory getLatestHuizhan() {
        return (HuizhanHistory)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.HuizhanHistory.getLatestHuizhan");
    }
    
    @Override
	public int updateHzStateById(final int state, final int vId) {
        final Params params = new Params();
        params.addParam("state", state).addParam("id", vId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.HuizhanHistory.updateHzStateById", params);
    }
    
    @Override
	public int updateHzEndTimeById(final Date endTime, final int vId) {
        final Params params = new Params();
        params.addParam("endTime", endTime).addParam("id", vId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.HuizhanHistory.updateHzEndTimeById", params);
    }
    
    @Override
	public int updateWinnerByVid(final int winnerForceId, final int vId) {
        final Params params = new Params();
        params.addParam("winner", winnerForceId).addParam("id", vId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.HuizhanHistory.updateWinnerByVid", params);
    }
    
    @Override
	public List<HuizhanHistory> getHuizhanByDate(final Date clearTime) {
        return (List<HuizhanHistory>)this.getSqlSession().selectList("com.reign.gcld.huizhan.domain.HuizhanHistory.getHuizhanByDate", (Object)clearTime);
    }
    
    @Override
	public int getWinNumByForceId(final int forceId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.HuizhanHistory.getWinNumByForceId", (Object)forceId);
    }
    
    @Override
	public int updateHzAttForce1ByVid(final int attForce, final int vId) {
        final Params params = new Params();
        params.addParam("attForce", attForce).addParam("id", vId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.HuizhanHistory.updateHzAttForce1ByVid", params);
    }
    
    @Override
	public int updateHzAttForce2ByVid(final int attForce, final int vId) {
        final Params params = new Params();
        params.addParam("attForce", attForce).addParam("id", vId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.HuizhanHistory.updateHzAttForce2ByVid", params);
    }
    
    @Override
	public int updateHzDefForceByVid(final int defForce, final int vId) {
        final Params params = new Params();
        params.addParam("defForce", defForce).addParam("id", vId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.HuizhanHistory.updateHzDefForceByVid", params);
    }
    
    @Override
	public int updateGatherFlagByVid(final int gFlag, final int vId) {
        final Params params = new Params();
        params.addParam("gFlag", gFlag).addParam("id", vId);
        return this.getSqlSession().update("com.reign.gcld.huizhan.domain.HuizhanHistory.updateGatherFlagByVid", params);
    }
    
    @Override
	public int getFinishedHzNum() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.huizhan.domain.HuizhanHistory.getFinishedHzNum");
    }
}
