package com.reign.gcld.weapon.dao;

import com.reign.gcld.weapon.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerWeaponDao")
public class PlayerWeaponDao extends BaseDao<PlayerWeapon> implements IPlayerWeaponDao
{
    @Override
	public PlayerWeapon read(final int vId) {
        return (PlayerWeapon)this.getSqlSession().selectOne("com.reign.gcld.weapon.domain.PlayerWeapon.read", (Object)vId);
    }
    
    @Override
	public PlayerWeapon readForUpdate(final int vId) {
        return (PlayerWeapon)this.getSqlSession().selectOne("com.reign.gcld.weapon.domain.PlayerWeapon.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerWeapon> getModels() {
        return (List<PlayerWeapon>)this.getSqlSession().selectList("com.reign.gcld.weapon.domain.PlayerWeapon.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.weapon.domain.PlayerWeapon.getModelSize");
    }
    
    @Override
	public int create(final PlayerWeapon playerWeapon) {
        return this.getSqlSession().insert("com.reign.gcld.weapon.domain.PlayerWeapon.create", playerWeapon);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.weapon.domain.PlayerWeapon.deleteById", vId);
    }
    
    @Override
	public List<PlayerWeapon> getPlayerWeapons(final int playerId) {
        return (List<PlayerWeapon>)this.getSqlSession().selectList("com.reign.gcld.weapon.domain.PlayerWeapon.getPlayerWeapons", (Object)playerId);
    }
    
    @Override
	public PlayerWeapon getPlayerWeapon(final int playerId, final int weaponId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("weaponId", weaponId);
        return (PlayerWeapon)this.getSqlSession().selectOne("com.reign.gcld.weapon.domain.PlayerWeapon.getPlayerWeapon", (Object)params);
    }
    
    @Override
	public void upgradeWeapon(final int playerId, final int weaponId, final int upLv, final int times) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("weaponId", weaponId);
        params.addParam("upLv", upLv);
        params.addParam("times", times);
        this.getSqlSession().update("com.reign.gcld.weapon.domain.PlayerWeapon.upgradeWeapon", params);
    }
    
    @Override
	public List<PlayerWeapon> getPlayerWeaponsByType(final int playerId, final int type) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", type);
        return (List<PlayerWeapon>)this.getSqlSession().selectList("com.reign.gcld.weapon.domain.PlayerWeapon.getPlayerWeaponsByType", (Object)params);
    }
    
    @Override
	public void upgradeLoadGem(final int playerId, final int weaponId, final String gemId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("weaponId", weaponId);
        params.addParam("gemId", gemId);
        this.getSqlSession().update("com.reign.gcld.weapon.domain.PlayerWeapon.upgradeLoadGem", params);
    }
    
    @Override
	public void setWeaponLv(final int playerId, final int weaponId, final int lv) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("weaponId", weaponId);
        params.addParam("lv", lv);
        this.getSqlSession().update("com.reign.gcld.weapon.domain.PlayerWeapon.setWeaponLv", params);
    }
    
    @Override
	public int deleteByPlayerId(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.weapon.domain.PlayerWeapon.deleteByPlayerId", playerId);
    }
}
