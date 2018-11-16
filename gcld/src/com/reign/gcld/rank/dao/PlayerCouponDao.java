package com.reign.gcld.rank.dao;

import com.reign.gcld.rank.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerCouponDao")
public class PlayerCouponDao extends BaseDao<PlayerCoupon> implements IPlayerCouponDao
{
    @Override
	public PlayerCoupon read(final int vid) {
        return (PlayerCoupon)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerCoupon.read", (Object)vid);
    }
    
    @Override
	public PlayerCoupon readForUpdate(final int vid) {
        return (PlayerCoupon)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerCoupon.readForUpdate", (Object)vid);
    }
    
    @Override
	public List<PlayerCoupon> getModels() {
        return (List<PlayerCoupon>)this.getSqlSession().selectList("com.reign.gcld.rank.domain.PlayerCoupon.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerCoupon.getModelSize");
    }
    
    @Override
	public int create(final PlayerCoupon playerCoupon) {
        return this.getSqlSession().insert("com.reign.gcld.rank.domain.PlayerCoupon.create", playerCoupon);
    }
    
    @Override
	public int deleteById(final int vid) {
        return this.getSqlSession().delete("com.reign.gcld.rank.domain.PlayerCoupon.deleteById", vid);
    }
    
    @Override
	public PlayerCoupon getPlayerCouponByPT(final int playerId, final int couponTypeInvest) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("type", couponTypeInvest);
        return (PlayerCoupon)this.getSqlSession().selectOne("com.reign.gcld.rank.domain.PlayerCoupon.getPlayerCouponByPT", (Object)params);
    }
    
    @Override
	public int reduceNum(final int vid, final int i) {
        final PlayerCoupon playerCoupon = this.read(vid);
        if (playerCoupon == null) {
            return 0;
        }
        if (playerCoupon.getCouponNum() == 0) {
            this.deleteById(vid);
            return 0;
        }
        int num = playerCoupon.getCouponNum();
        if (--num == 0) {
            return this.deleteById(vid);
        }
        return this.updateCouponNum(vid, num);
    }
    
    @Override
	public int updateCouponNum(final int vid, final int num) {
        final Params params = new Params();
        params.addParam("vId", vid);
        params.addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.rank.domain.PlayerCoupon.updateCouponNum", params);
    }
    
    @Override
	public int addCouponNum(final int playerId, final int addNum) {
        PlayerCoupon playerCoupon = this.getPlayerCouponByPT(playerId, 1);
        if (playerCoupon == null) {
            playerCoupon = new PlayerCoupon();
            playerCoupon.setPlayerId(playerId);
            playerCoupon.setCouponType(1);
            playerCoupon.setCouponNum(addNum);
            return this.create(playerCoupon);
        }
        return this.updateCouponNum(playerCoupon.getVid(), playerCoupon.getCouponNum() + addNum);
    }
}
