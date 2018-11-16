package com.reign.gcld.player.dao;

import com.reign.gcld.player.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerConstantsDao")
public class PlayerConstantsDao extends BaseDao<PlayerConstants> implements IPlayerConstantsDao
{
    @Override
	public PlayerConstants read(final int playerId) {
        return (PlayerConstants)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerConstants.read", (Object)playerId);
    }
    
    @Override
	public PlayerConstants readForUpdate(final int playerId) {
        return (PlayerConstants)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerConstants.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerConstants> getModels() {
        return (List<PlayerConstants>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerConstants.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerConstants.getModelSize");
    }
    
    @Override
	public int create(final PlayerConstants playerConstants) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.PlayerConstants.create", playerConstants);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerConstants.deleteById", playerId);
    }
    
    @Override
	public void updateExtraNum(final int playerId, final int extraNum) {
        final Params param = new Params();
        param.addParam("addExtraNum", extraNum);
        param.addParam("playerId", playerId);
        this.getSqlSession().update("com.reign.gcld.player.domain.PlayerConstants.updateExtraNum", param);
    }
    
    @Override
	public void updateExpression(final int playerId, final String expression) {
        final Params param = new Params();
        param.addParam("expression", expression);
        param.addParam("playerId", playerId);
        this.getSqlSession().update("com.reign.gcld.player.domain.PlayerConstants.updateExpression", param);
    }
}
