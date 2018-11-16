package com.reign.gcld.phantom.dao;

import com.reign.gcld.phantom.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.mybatis.*;
import java.util.*;

@Component("playerWizardDao")
public class PlayerWizardDao extends BaseDao<PlayerWizard> implements IPlayerWizardDao
{
    @Override
	public PlayerWizard read(final int vId) {
        return (PlayerWizard)this.getSqlSession().selectOne("com.reign.gcld.phantom.domain.PlayerWizard.read", (Object)vId);
    }
    
    @Override
	public PlayerWizard readForUpdate(final int vId) {
        return (PlayerWizard)this.getSqlSession().selectOne("com.reign.gcld.phantom.domain.PlayerWizard.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerWizard> getModels() {
        return (List<PlayerWizard>)this.getSqlSession().selectList("com.reign.gcld.phantom.domain.PlayerWizard.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.phantom.domain.PlayerWizard.getModelSize");
    }
    
    @Override
	public int create(final PlayerWizard playerWizard) {
        return this.getSqlSession().insert("com.reign.gcld.phantom.domain.PlayerWizard.create", playerWizard);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.phantom.domain.PlayerWizard.deleteById", vId);
    }
    
    @Override
	public List<PlayerWizard> getNeedRecoverList() {
        return (List<PlayerWizard>)this.getSqlSession().selectList("com.reign.gcld.phantom.domain.PlayerWizard.getNeedRecoverList");
    }
    
    @Override
	public List<PlayerWizard> getListByPlayerId(final int playerId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        return (List<PlayerWizard>)this.getSqlSession().selectList("com.reign.gcld.phantom.domain.PlayerWizard.getListByPlayerId", (Object)playerId);
    }
    
    @Override
	public PlayerWizard getByPlayerIdWizardId(final int playerId, final int wizardId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("wizardId", wizardId);
        return (PlayerWizard)this.getSqlSession().selectOne("com.reign.gcld.phantom.domain.PlayerWizard.getByPlayerIdWizardId", (Object)params);
    }
    
    @Override
	public int increaseTodayNum(final Integer vId, final int num) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.phantom.domain.PlayerWizard.increaseTodayNum", params);
    }
    
    @Override
	public int gainPhantom(final Integer vId, final int flag, final String reserve) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("flag", flag);
        params.addParam("reserve", reserve);
        return this.getSqlSession().update("com.reign.gcld.phantom.domain.PlayerWizard.gainPhantom", params);
    }
    
    @Override
	public int updateSuccTimeFlag(final Integer vId, final int num, final Date date, final int flag) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("flag", flag);
        params.addParam("num", num);
        params.addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.phantom.domain.PlayerWizard.updateSuccTime", params);
    }
    
    @Override
	public int updateFlag(final Integer vId, final int flag) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("flag", flag);
        return this.getSqlSession().update("com.reign.gcld.phantom.domain.PlayerWizard.updateFlag", params);
    }
    
    @Override
	public int updateLevel(final Integer vId, final int level) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("level", level);
        return this.getSqlSession().update("com.reign.gcld.phantom.domain.PlayerWizard.updateLevel", params);
    }
    
    @Override
	public int updateExtraPicked(final Integer vId, final int extraPicked) {
        final Params params = new Params();
        params.addParam("vId", vId);
        params.addParam("extraPicked", extraPicked);
        return this.getSqlSession().update("com.reign.gcld.phantom.domain.PlayerWizard.updateExtraPicked", params);
    }
    
    @Override
	public int resetAllWiazrd() {
        return this.getSqlSession().update("com.reign.gcld.phantom.domain.PlayerWizard.resetAllWiazrd");
    }
}
