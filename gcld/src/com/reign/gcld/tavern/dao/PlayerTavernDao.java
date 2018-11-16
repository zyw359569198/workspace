package com.reign.gcld.tavern.dao;

import com.reign.gcld.tavern.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import org.apache.commons.lang.*;

@Component("playerTavernDao")
public class PlayerTavernDao extends BaseDao<PlayerTavern> implements IPlayerTavernDao
{
    @Override
	public PlayerTavern read(final int playerId) {
        return (PlayerTavern)this.getSqlSession().selectOne("com.reign.gcld.tavern.domain.PlayerTavern.read", (Object)playerId);
    }
    
    @Override
	public PlayerTavern readForUpdate(final int playerId) {
        return (PlayerTavern)this.getSqlSession().selectOne("com.reign.gcld.tavern.domain.PlayerTavern.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerTavern> getModels() {
        return (List<PlayerTavern>)this.getSqlSession().selectList("com.reign.gcld.tavern.domain.PlayerTavern.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.tavern.domain.PlayerTavern.getModelSize");
    }
    
    @Override
	public int create(final PlayerTavern playerTavern) {
        return this.getSqlSession().insert("com.reign.gcld.tavern.domain.PlayerTavern.create", playerTavern);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.tavern.domain.PlayerTavern.deleteById", playerId);
    }
    
    @Override
	public void updatePlayerTavern(final PlayerTavern playerTavern) {
        this.getSqlSession().update("com.reign.gcld.tavern.domain.PlayerTavern.updatePlayerTavern", playerTavern);
    }
    
    @Override
	public void updateLockId(final int playerId, final String generalId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("generalId", generalId);
        this.getSqlSession().update("com.reign.gcld.tavern.domain.PlayerTavern.updateLockId", params);
    }
    
    @Override
	public int updateMilitaryInfo(final int playerId, final String gIds) {
        if (StringUtils.isBlank(gIds)) {
            return 0;
        }
        final String militaryInfo = this.getMilitaryInfo(playerId);
        if (StringUtils.isNotBlank(militaryInfo)) {
            final String[] infos = militaryInfo.split(",");
            String[] array;
            for (int length = (array = infos).length, i = 0; i < length; ++i) {
                final String info = array[i];
                if (info.equals(gIds)) {
                    return 0;
                }
            }
        }
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("gIds", gIds);
        return this.getSqlSession().update("com.reign.gcld.tavern.domain.PlayerTavern.updateMilitaryInfo", params);
    }
    
    @Override
	public int updateCivilInfo(final int playerId, final String gIds) {
        if (StringUtils.isBlank(gIds)) {
            return 0;
        }
        final String civilInfo = this.getCivilInfo(playerId);
        if (StringUtils.isNotBlank(civilInfo)) {
            final String[] infos = civilInfo.split(",");
            String[] array;
            for (int length = (array = infos).length, i = 0; i < length; ++i) {
                final String info = array[i];
                if (info.equals(gIds)) {
                    return 0;
                }
            }
        }
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("gIds", gIds);
        return this.getSqlSession().update("com.reign.gcld.tavern.domain.PlayerTavern.updateCivilInfo", params);
    }
    
    @Override
	public String getCivilInfo(final int playerId) {
        return (String)this.getSqlSession().selectOne("com.reign.gcld.tavern.domain.PlayerTavern.getCivilInfo", (Object)playerId);
    }
    
    @Override
	public String getMilitaryInfo(final int playerId) {
        return (String)this.getSqlSession().selectOne("com.reign.gcld.tavern.domain.PlayerTavern.getMilitaryInfo", (Object)playerId);
    }
}
