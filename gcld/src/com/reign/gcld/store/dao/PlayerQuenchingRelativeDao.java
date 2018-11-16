package com.reign.gcld.store.dao;

import com.reign.gcld.store.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;

@Component("playerQuenchingRelativeDao")
public class PlayerQuenchingRelativeDao extends BaseDao<PlayerQuenchingRelative> implements IPlayerQuenchingRelativeDao
{
    @Autowired
    private IPlayerDao playerDao;
    
    @Override
	public PlayerQuenchingRelative read(final int playerId) {
        return (PlayerQuenchingRelative)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerQuenchingRelative.read", (Object)playerId);
    }
    
    @Override
	public PlayerQuenchingRelative readForUpdate(final int playerId) {
        return (PlayerQuenchingRelative)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerQuenchingRelative.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerQuenchingRelative> getModels() {
        return (List<PlayerQuenchingRelative>)this.getSqlSession().selectList("com.reign.gcld.store.domain.PlayerQuenchingRelative.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.store.domain.PlayerQuenchingRelative.getModelSize");
    }
    
    @Override
	public int create(final PlayerQuenchingRelative playerQuenchingRelative) {
        return this.getSqlSession().insert("com.reign.gcld.store.domain.PlayerQuenchingRelative.create", playerQuenchingRelative);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.store.domain.PlayerQuenchingRelative.deleteById", playerId);
    }
    
    @Override
	public int updateFreeQuenchingTimes(final int playerId, final int freeTimes) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("freeTimes", freeTimes);
        return this.getSqlSession().update("com.reign.gcld.store.domain.PlayerQuenchingRelative.updateFreeQuenchingTimes", params);
    }
    
    @Override
	public int updateFreeNiubiTimes(final int playerId, final int resultNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("resultNum", resultNum);
        return this.getSqlSession().update("com.reign.gcld.store.domain.PlayerQuenchingRelative.updateFreeNiubiTimes", params);
    }
    
    @Override
	public int addFreeQuenchingTimes(final int playerId, final int addNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("addNum", addNum);
        return this.getSqlSession().update("com.reign.gcld.store.domain.PlayerQuenchingRelative.addFreeQuenchingTimes", params);
    }
    
    @Override
	public int updateAllFreeQuenchingTimes(final int maxTimes) {
        return this.getSqlSession().update("com.reign.gcld.store.domain.PlayerQuenchingRelative.updateAllFreeQuenchingTimes", maxTimes);
    }
    
    @Override
	public int addFreeNiubiTimes(final int playerId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("num", num);
        final int result = this.getSqlSession().update("com.reign.gcld.store.domain.PlayerQuenchingRelative.addFreeNiubiTimes", params);
        if (result > 0) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freegoldquenching", num, "+", attribute, player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public List<PlayerQuenchingRelative> getListByIds(final List<Integer> allPlayerIds) {
        return (List<PlayerQuenchingRelative>)this.getSqlSession().selectList("com.reign.gcld.store.domain.PlayerQuenchingRelative.getListByIds", (Object)allPlayerIds);
    }
    
    @Override
	public int updateRemindQuenching(final int playerId, final int remind) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("remind", remind);
        return this.getSqlSession().update("com.reign.gcld.store.domain.PlayerQuenchingRelative.updateRemindQuenching", params);
    }
}
