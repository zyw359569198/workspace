package com.reign.gcld.player.dao;

import com.reign.gcld.player.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerIncenseWeaponEffectDao")
public class PlayerIncenseWeaponEffectDao extends BaseDao<PlayerIncenseWeaponEffect> implements IPlayerIncenseWeaponEffectDao
{
    @Override
	public PlayerIncenseWeaponEffect read(final int playerId) {
        return (PlayerIncenseWeaponEffect)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.read", (Object)playerId);
    }
    
    @Override
	public PlayerIncenseWeaponEffect readForUpdate(final int playerId) {
        return (PlayerIncenseWeaponEffect)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerIncenseWeaponEffect> getModels() {
        return (List<PlayerIncenseWeaponEffect>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.getModelSize");
    }
    
    @Override
	public int create(final PlayerIncenseWeaponEffect playerIncenseWeaponEffect) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.create", playerIncenseWeaponEffect);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.deleteById", playerId);
    }
    
    @Override
	public int updateIncenseEffect(final int playerId, final int incenseId, final int incenseLimit, final int incenseMulti, final Date incenseEndTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("incenseId", incenseId);
        params.addParam("incenseLimit", incenseLimit);
        params.addParam("incenseMulti", incenseMulti);
        params.addParam("incenseEndTime", incenseEndTime);
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.updateIncenseEffect", params);
    }
    
    @Override
	public int updateWeaponEffect(final int playerId, final int weaponId, final int weaponLimit, final int weaponMulti, final Date weaponEndTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("weaponId", weaponId);
        params.addParam("weaponLimit", weaponLimit);
        params.addParam("weaponMulti", weaponMulti);
        params.addParam("weaponEndTime", weaponEndTime);
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.updateWeaponEffect", params);
    }
    
    @Override
	public int reduceIncenseLimit(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.reduceIncenseLimit", playerId);
    }
    
    @Override
	public int reduceWeaponLimit(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerIncenseWeaponEffect.reduceWeaponLimit", playerId);
    }
}
