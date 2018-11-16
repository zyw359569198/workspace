package com.reign.gcld.tickets.dao;

import com.reign.gcld.tickets.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;

@Component("playerTicketsDao")
public class PlayerTicketsDao extends BaseDao<PlayerTickets> implements IPlayerTicketsDao
{
    @Autowired
    private IPlayerDao playerDao;
    
    @Override
	public PlayerTickets read(final int playerId) {
        return (PlayerTickets)this.getSqlSession().selectOne("com.reign.gcld.tickets.domain.PlayerTickets.read", (Object)playerId);
    }
    
    @Override
	public PlayerTickets readForUpdate(final int playerId) {
        return (PlayerTickets)this.getSqlSession().selectOne("com.reign.gcld.tickets.domain.PlayerTickets.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerTickets> getModels() {
        return (List<PlayerTickets>)this.getSqlSession().selectList("com.reign.gcld.tickets.domain.PlayerTickets.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.tickets.domain.PlayerTickets.getModelSize");
    }
    
    @Override
	public int create(final PlayerTickets playerTickets) {
        return this.getSqlSession().insert("com.reign.gcld.tickets.domain.PlayerTickets.create", playerTickets);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.tickets.domain.PlayerTickets.deleteById", playerId);
    }
    
    @Override
	public PlayerTickets safeGetPlayerTickets(final int playerId) {
        PlayerTickets result = this.read(playerId);
        if (result == null) {
            result = new PlayerTickets();
            result.setPlayerId(playerId);
            result.setNoTips(0);
            result.setTickets(0);
            this.create(result);
        }
        return result;
    }
    
    @Override
	public void addTickets(final int playerId, final int tickets, final Object attribute, final boolean sendEvent) {
        this.safeGetPlayerTickets(playerId);
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("tickets", tickets);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.tickets.domain.PlayerTickets.addTickets", params);
        if (affectRows >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "ticket", tickets, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
    }
    
    @Override
	public boolean consumeTickets(final int playerId, final int tickets, final Object attribute) {
        this.safeGetPlayerTickets(playerId);
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("tickets", tickets);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.tickets.domain.PlayerTickets.consumeTickets", params);
        if (affectRows >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "ticket", tickets, "-", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return affectRows >= 1;
    }
}
