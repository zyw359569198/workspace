package com.reign.gcld.tech.dao;

import com.reign.gcld.tech.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerTechDao")
public class PlayerTechDao extends BaseDao<PlayerTech> implements IPlayerTechDao
{
    @Override
	public PlayerTech read(final int vId) {
        return (PlayerTech)this.getSqlSession().selectOne("com.reign.gcld.tech.domain.PlayerTech.read", (Object)vId);
    }
    
    @Override
	public PlayerTech readForUpdate(final int vId) {
        return (PlayerTech)this.getSqlSession().selectOne("com.reign.gcld.tech.domain.PlayerTech.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerTech> getModels() {
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.tech.domain.PlayerTech.getModelSize");
    }
    
    @Override
	public int create(final PlayerTech playerTech) {
        return this.getSqlSession().insert("com.reign.gcld.tech.domain.PlayerTech.create", playerTech);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.tech.domain.PlayerTech.deleteById", vId);
    }
    
    @Override
	public List<PlayerTech> getPlayerTechList(final int playerId) {
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getPlayerTechList", (Object)playerId);
    }
    
    @Override
	public PlayerTech getPlayerTech(final int playerId, final int techId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("techId", techId);
        return (PlayerTech)this.getSqlSession().selectOne("com.reign.gcld.tech.domain.PlayerTech.getPlayerTech", (Object)params);
    }
    
    @Override
	public List<PlayerTech> getTechListByLimit(final int playerId, final int offset, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("offset", offset);
        params.addParam("num", num);
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getTechListByLimit", (Object)params);
    }
    
    @Override
	public int setNumAndStatus(final int vId, final int num, final int status) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("num", num);
        params.addParam("status", status);
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.setNumAndStatus", params);
    }
    
    @Override
	public int setNum(final int vId, final int num) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.setNum", params);
    }
    
    @Override
	public int setCd(final int vId, final Date cd, final int status, final int jobId) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("cd", cd);
        params.addParam("status", status);
        params.addParam("jobId", jobId);
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.setCd", params);
    }
    
    @Override
	public int getSizeByPlayerId(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.tech.domain.PlayerTech.getSizeByPlayerId", (Object)playerId);
    }
    
    @Override
	public List<PlayerTech> getByPlayerIdAndTechKey(final int playerId, final int techKey) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("techKey", techKey);
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getByPlayerIdAndKey", (Object)params);
    }
    
    @Override
	public List<PlayerTech> getAllTechByKey(final int playerId, final int techKey) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("techKey", techKey);
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getAllTechByKey", (Object)params);
    }
    
    @Override
	public int getTechIdByVId(final int vId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.tech.domain.PlayerTech.getTechIdByVId", (Object)vId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public List<PlayerTech> getListByTechKey(final int techKey) {
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getListByTechKey", (Object)techKey);
    }
    
    @Override
	public List<PlayerTech> getByTechKeys(final int playerId, final int techKey1, final int techKey2, final int techKey3) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("techKey1", techKey1).addParam("techKey2", techKey2).addParam("techKey3", techKey3).addParam("status", 5);
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getByTechKeys", (Object)params);
    }
    
    @Override
	public List<PlayerTech> getByTechKeys2(final int playerId, final int techKey1, final int techKey2) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("techKey1", techKey1).addParam("techKey2", techKey2).addParam("status", 5);
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getByTechKeys2", (Object)params);
    }
    
    @Override
	public List<PlayerTech> getByTechKey1(final int playerId, final int techKey) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("techKey", techKey).addParam("status", 5);
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getByTechKey1", (Object)params);
    }
    
    @Override
	public int setStatusAndIsNew(final int playerId, final int techId, final int status, final int isNew) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("techId", techId);
        params.addParam("status", status);
        params.addParam("isNew", isNew);
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.setStatusAndIsNew", params);
    }
    
    @Override
	public int setIsNewAndFinishNew(final int vId, final int isNew, final int finishNew) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("isNew", isNew);
        params.addParam("finishNew", finishNew);
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.setIsNewAndFinishNew", params);
    }
    
    @Override
	public int deleteByPlayerIdAndKey(final int playerId, final int key, final int status) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("key", key);
        params.addParam("status", status);
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.deleteByPlayerIdAndKey", params);
    }
    
    @Override
	public int setStatus(final int vId, final int status) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("status", status);
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.setStatus", params);
    }
    
    @Override
	public List<PlayerTech> getListByPlayerIdAndStatus(final int playerId, final int status) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("status", status);
        return (List<PlayerTech>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getListByPlayerIdAndStatus", (Object)params);
    }
    
    @Override
	public int deleteByPlayerIdAndStatus(final int playerId, final int status) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("status", status);
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.deleteByPlayerIdAndStatus", params);
    }
    
    @Override
	public int getNumDisPlayButton(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.tech.domain.PlayerTech.getNumDisPlayButton", (Object)playerId);
    }
    
    @Override
	public int techAll(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.tech.domain.PlayerTech.techAll", playerId);
    }
    
    @Override
	public List<Integer> getPlayerIdListByKey(final int techKey) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getPlayerIdListByKey", (Object)techKey);
    }
    
    @Override
	public int getEffectSizeByTechKey(final int playerId, final int techKey) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("techKey", techKey);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.tech.domain.PlayerTech.getEffectSizeByTechKey", (Object)params);
    }
    
    @Override
	public List<Integer> getPlayerIdListByFirstWorldDramaKey(final List<Integer> techList) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.tech.domain.PlayerTech.getPlayerIdListByFirstWorldDramaKey", (Object)techList);
    }
}
