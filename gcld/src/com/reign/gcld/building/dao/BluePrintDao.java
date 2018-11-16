package com.reign.gcld.building.dao;

import com.reign.gcld.building.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("bluePrintDao")
public class BluePrintDao extends BaseDao<BluePrint> implements IBluePrintDao
{
    @Override
	public BluePrint read(final int vId) {
        return (BluePrint)this.getSqlSession().selectOne("com.reign.gcld.building.domain.BluePrint.read", (Object)vId);
    }
    
    @Override
	public BluePrint readForUpdate(final int vId) {
        return (BluePrint)this.getSqlSession().selectOne("com.reign.gcld.building.domain.BluePrint.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<BluePrint> getModels() {
        return (List<BluePrint>)this.getSqlSession().selectList("com.reign.gcld.building.domain.BluePrint.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.building.domain.BluePrint.getModelSize");
    }
    
    @Override
	public int create(final BluePrint bluePrint) {
        return this.getSqlSession().insert("com.reign.gcld.building.domain.BluePrint.create", bluePrint);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.building.domain.BluePrint.deleteById", vId);
    }
    
    @Override
	public int getCount(final int playerId, final int index) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("index", index);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.building.domain.BluePrint.getCount", (Object)params);
    }
    
    @Override
	public BluePrint getByPlayerIdAndIndex(final int playerId, final int index) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("index", index);
        return (BluePrint)this.getSqlSession().selectOne("com.reign.gcld.building.domain.BluePrint.getByPlayerIdAndIndex", (Object)params);
    }
    
    @Override
	public int updateState(final int vId, final int state) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.building.domain.BluePrint.updateState", params);
    }
    
    @Override
	public int updateStateByPlayerIdAndIndex(final int playerId, final int index, final int state) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("index", index).addParam("state", state);
        return this.getSqlSession().update("com.reign.gcld.building.domain.BluePrint.updateStateByPlayerIdAndIndex", params);
    }
    
    @Override
	public int cons(final int vId, final int state, final Date cd, final int jobId) {
        final Params params = new Params();
        params.addParam("vId", vId).addParam("state", state).addParam("cd", cd).addParam("jobId", jobId);
        return this.getSqlSession().update("com.reign.gcld.building.domain.BluePrint.cons", params);
    }
    
    @Override
	public List<Integer> getBluePrintIndexList(final int playerId) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.building.domain.BluePrint.getBluePrintIndexList", (Object)playerId);
    }
}
