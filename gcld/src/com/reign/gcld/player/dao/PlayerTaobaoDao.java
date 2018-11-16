package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component("playerTaobaoDao")
public class PlayerTaobaoDao extends BaseDao<PlayerTaobao> implements IPlayerTaobaoDao
{
    @Override
	public PlayerTaobao read(final int playerId) {
        return (PlayerTaobao)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerTaobao.read", (Object)playerId);
    }
    
    @Override
	public PlayerTaobao readForUpdate(final int playerId) {
        return (PlayerTaobao)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerTaobao.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerTaobao> getModels() {
        return (List<PlayerTaobao>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerTaobao.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerTaobao.getModelSize");
    }
    
    @Override
	public int create(final PlayerTaobao playerTaobao) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.PlayerTaobao.create", playerTaobao);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerTaobao.deleteById", playerId);
    }
}
