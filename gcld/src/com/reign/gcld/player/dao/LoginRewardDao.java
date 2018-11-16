package com.reign.gcld.player.dao;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.common.*;
import com.reign.framework.jdbc.*;
import java.util.*;

@Component("loginRewardDao")
public class LoginRewardDao extends BaseDao<LoginReward> implements ILoginRewardDao
{
    @Autowired
    private IBatchExecute batchExecute;
    
    @Override
	public LoginReward read(final int playerId) {
        return (LoginReward)this.getSqlSession().selectOne("com.reign.gcld.player.domain.LoginReward.read", (Object)playerId);
    }
    
    @Override
	public LoginReward readForUpdate(final int playerId) {
        return (LoginReward)this.getSqlSession().selectOne("com.reign.gcld.player.domain.LoginReward.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<LoginReward> getModels() {
        return (List<LoginReward>)this.getSqlSession().selectList("com.reign.gcld.player.domain.LoginReward.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.LoginReward.getModelSize");
    }
    
    @Override
	public int create(final LoginReward loginReward) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.LoginReward.create", loginReward);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.LoginReward.deleteById", playerId);
    }
    
    @Override
	public int received(final int playerId, final int mark) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("mark", (Object)mark);
        return this.getSqlSession().update("com.reign.gcld.player.domain.LoginReward.received", (Object)params);
    }
    
    @Override
	public int receivedAll(final int mark) {
        return this.getSqlSession().update("com.reign.gcld.player.domain.LoginReward.receivedAll", mark);
    }
    
    @Override
	public int deleteByTotalDay(final int totalDay) {
        return this.getSqlSession().update("com.reign.gcld.player.domain.LoginReward.deleteByTotalDay", totalDay);
    }
    
    @Override
	public int updateRecord(final int playerId, final int totalDay, final int haveReward, final Date todayFirstLoginTime) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("totalDay", (Object)totalDay);
        params.addParam("haveReward", (Object)haveReward);
        params.addParam("todayFirstLoginTime", (Object)todayFirstLoginTime);
        return this.getSqlSession().update("com.reign.gcld.player.domain.LoginReward.updateRecord", (Object)params);
    }
    
    @Override
	public List<PlayerTotalDay> getList(final List<Integer> playerIds) {
        return (List<PlayerTotalDay>)this.getSqlSession().selectList("com.reign.gcld.player.domain.LoginReward.getList", (Object)playerIds);
    }
    
    @Override
	public int battchUpdate(final List<Integer> playerIds) {
        final String sql = "UPDATE LOGIN_REWARD SET TOTAL_DAY = TOTAL_DAY + 1, HAVE_REWARD = 1, TODAY_FIRST_LOGIN_TIME = '" + TimeUtil.getMysqlDateString(new Date()) + "' WHERE PLAYER_ID = ?";
        final List<List<Param>> paramsList = new ArrayList<List<Param>>();
        for (final Integer playerId : playerIds) {
            final List<Param> params = new ArrayList<Param>();
            params.add(new Param(playerId, Type.Int));
            paramsList.add(params);
        }
        return this.batchExecute.batch(this.getSqlSession(), sql, paramsList);
    }
}
