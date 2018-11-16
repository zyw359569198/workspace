package com.reign.gcld.treasure.dao;

import com.reign.gcld.treasure.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.chat.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.mybatis.*;

@Component("playerTreasureDao")
public class PlayerTreasureDao extends BaseDao<PlayerTreasure> implements IPlayerTreasureDao
{
    @Autowired
    private IChatService chatService;
    @Autowired
    private TreasureCache treasureCache;
    
    @Override
	public PlayerTreasure read(final int vId) {
        return (PlayerTreasure)this.getSqlSession().selectOne("com.reign.gcld.treasure.domain.PlayerTreasure.read", (Object)vId);
    }
    
    @Override
	public PlayerTreasure readForUpdate(final int vId) {
        return (PlayerTreasure)this.getSqlSession().selectOne("com.reign.gcld.treasure.domain.PlayerTreasure.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerTreasure> getModels() {
        return (List<PlayerTreasure>)this.getSqlSession().selectList("com.reign.gcld.treasure.domain.PlayerTreasure.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.treasure.domain.PlayerTreasure.getModelSize");
    }
    
    @Override
	public int create(final PlayerTreasure playerTreasure) {
        final Integer result = this.getSqlSession().insert("com.reign.gcld.treasure.domain.PlayerTreasure.create", playerTreasure);
        if (result != null && result >= 1) {
            final PlayerDto playerDto = Players.getPlayer(playerTreasure.getPlayerId());
            final String msg = MessageFormatter.format(LocalMessages.BROADCAST_CHAT_TREASURE, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName), ColorUtil.getGreenMsg(((Treasure)this.treasureCache.get((Object)playerTreasure.getTreasureId())).getName()) });
            this.chatService.sendBigNotice("COUNTRY", playerDto, msg, null);
            this.chatService.sendSystemChat("GLOBAL", playerDto.playerId, playerDto.forceId, msg, null);
            return result;
        }
        return 0;
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.treasure.domain.PlayerTreasure.deleteById", vId);
    }
    
    @Override
	public List<PlayerTreasure> getPlayerTreasures(final int playerId) {
        return (List<PlayerTreasure>)this.getSqlSession().selectList("com.reign.gcld.treasure.domain.PlayerTreasure.getPlayerTreasures", (Object)playerId);
    }
    
    @Override
	public PlayerTreasure getPlayerTreasure(final int playerId, final int treasureId) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("treasureId", treasureId);
        return (PlayerTreasure)this.getSqlSession().selectOne("com.reign.gcld.treasure.domain.PlayerTreasure.getPlayerTreasure", (Object)params);
    }
    
    @Override
	public int getTreasureCount(final int playerId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.treasure.domain.PlayerTreasure.getTreasureCount", (Object)playerId);
    }
}
