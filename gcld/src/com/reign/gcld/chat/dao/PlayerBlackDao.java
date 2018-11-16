package com.reign.gcld.chat.dao;

import com.reign.gcld.chat.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.mybatis.*;

@Component("playerBlackDao")
public class PlayerBlackDao extends BaseDao<PlayerBlack> implements IPlayerBlackDao
{
    @Override
	public PlayerBlack read(final int vId) {
        return (PlayerBlack)this.getSqlSession().selectOne("com.reign.gcld.chat.domain.PlayerBlack.read", (Object)vId);
    }
    
    @Override
	public PlayerBlack readForUpdate(final int vId) {
        return (PlayerBlack)this.getSqlSession().selectOne("com.reign.gcld.chat.domain.PlayerBlack.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerBlack> getModels() {
        return (List<PlayerBlack>)this.getSqlSession().selectList("com.reign.gcld.chat.domain.PlayerBlack.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.chat.domain.PlayerBlack.getModelSize");
    }
    
    @Override
	public int create(final PlayerBlack playerBlack) {
        return this.getSqlSession().insert("com.reign.gcld.chat.domain.PlayerBlack.create", playerBlack);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.chat.domain.PlayerBlack.deleteById", vId);
    }
    
    @Override
	public List<PlayerBlack> getPlayerBlackList(final int playerId) {
        return (List<PlayerBlack>)this.getSqlSession().selectList("com.reign.gcld.chat.domain.PlayerBlack.getPlayerBlackList", (Object)playerId);
    }
    
    @Override
	public List<PlayerBlack> getBlackPlayerList(final int blackId) {
        return (List<PlayerBlack>)this.getSqlSession().selectList("com.reign.gcld.chat.domain.PlayerBlack.getBlackPlayerList", (Object)blackId);
    }
    
    @Override
	public PlayerBlack getPlayerBlackByBid(final int playerId, final int blackId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("blackId", blackId);
        final List<PlayerBlack> list = (List<PlayerBlack>)this.getSqlSession().selectList("com.reign.gcld.chat.domain.PlayerBlack.getPlayerBlackByBid", (Object)params);
        if (list == null || list.size() < 1) {
            return null;
        }
        return list.get(0);
    }
}
