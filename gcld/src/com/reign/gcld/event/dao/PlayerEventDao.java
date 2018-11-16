package com.reign.gcld.event.dao;

import com.reign.gcld.event.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

@Component("playerEventDao")
public class PlayerEventDao extends BaseDao<PlayerEvent> implements IPlayerEventDao
{
    @Autowired
    private IPlayerDao playerDao;
    
    @Override
	public PlayerEvent read(final int vId) {
        return (PlayerEvent)this.getSqlSession().selectOne("com.reign.gcld.event.domain.PlayerEvent.read", (Object)vId);
    }
    
    @Override
	public PlayerEvent readForUpdate(final int vId) {
        return (PlayerEvent)this.getSqlSession().selectOne("com.reign.gcld.event.domain.PlayerEvent.readForUpdate", (Object)vId);
    }
    
    @Override
	public List<PlayerEvent> getModels() {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.event.domain.PlayerEvent.getModelSize");
    }
    
    @Override
	public int create(final PlayerEvent playerEvent) {
        return this.getSqlSession().insert("com.reign.gcld.event.domain.PlayerEvent.create", playerEvent);
    }
    
    @Override
	public int deleteById(final int vId) {
        return this.getSqlSession().delete("com.reign.gcld.event.domain.PlayerEvent.deleteById", vId);
    }
    
    @Override
	public PlayerEvent getPlayerEvent(final int playerId, final int eventId) {
        final Params param = new Params();
        param.addParam("playerId", playerId).addParam("eventId", eventId);
        return (PlayerEvent)this.getSqlSession().selectOne("com.reign.gcld.event.domain.PlayerEvent.getPlayerEvent", (Object)param);
    }
    
    @Override
	public int setParam1(final int playerId, final int eventId, final int pos) {
        final Params param = new Params();
        param.addParam("playerId", playerId).addParam("eventId", eventId).addParam("pos", pos);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.setParam1", param);
    }
    
    @Override
	public int setParam2(final int playerId, final int eventId, final int pos) {
        final Params param = new Params();
        param.addParam("playerId", playerId).addParam("eventId", eventId).addParam("pos", pos);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.setParam2", param);
    }
    
    @Override
	public int setParam3(final int playerId, final int eventId, final int pos) {
        final Params param = new Params();
        param.addParam("playerId", playerId).addParam("eventId", eventId).addParam("pos", pos);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.setParam3", param);
    }
    
    @Override
	public int setParam4(final int playerId, final int eventId, final int pos) {
        final Params param = new Params();
        param.addParam("playerId", playerId).addParam("eventId", eventId).addParam("pos", pos);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.setParam4", param);
    }
    
    @Override
	public int setParam5(final int playerId, final int eventId, final int pos) {
        final Params param = new Params();
        param.addParam("playerId", playerId).addParam("eventId", eventId).addParam("pos", pos);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.setParam5", param);
    }
    
    @Override
	public List<PlayerEvent> getPlayerEventList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getPlayerEventList", (Object)eventId);
    }
    
    @Override
	public int clearEvent(final int eventId) {
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.clearEvent", eventId);
    }
    
    @Override
	public int clearMidAutumn(final int eventId, final int dayNum) {
        final Params param = new Params();
        param.addParam("eventId", eventId);
        param.addParam("dayNum", dayNum);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.clearMidAutumn", param);
    }
    
    @Override
	public int updateInfo(final int playerId, final int eventId, final int param1, final int param2, final int param3, final int param4) {
        final Params param5 = new Params();
        param5.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param1", param1).addParam("param2", param2).addParam("param3", param3).addParam("param4", param4);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateInfo", param5);
    }
    
    @Override
	public int updateInfo2(final int playerId, final int eventId, final int param1, final int param2, final int param3) {
        final Params param4 = new Params();
        param4.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param1", param1).addParam("param2", param2).addParam("param3", param3);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateInfo2", param4);
    }
    
    @Override
	public int updateInfo3(final int playerId, final int eventId, final int param1, final int param2) {
        final Params param3 = new Params();
        param3.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param1", param1).addParam("param2", param2);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateInfo3", param3);
    }
    
    @Override
	public List<Integer> getPlayerIdListByEventId(final int eventId) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getPlayerIdListByEventId", (Object)eventId);
    }
    
    @Override
	public boolean consumeBmw(final int playerId, final int eventId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        final int result = this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.consumeBmw", params);
        final boolean succ = result == 1;
        if (succ) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "bmw", num, "-", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return succ;
    }
    
    @Override
	public boolean consumeXo(final int playerId, final int eventId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        final int result = this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.consumeXo", params);
        final boolean succ = result == 1;
        if (succ) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "xo", num, "-", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return succ;
    }
    
    @Override
	public boolean consumePicasso(final int playerId, final int eventId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        final int result = this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.consumePicasso", params);
        final boolean succ = result == 1;
        if (succ) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "picasso", num, "-", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return succ;
    }
    
    @Override
	public int updateParam8(final int playerId, final int eventId, final int param8) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param8", param8);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam8", params);
    }
    
    @Override
	public int updateParam10(final int playerId, final int eventId, final int param10) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param10", param10);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam10", params);
    }
    
    @Override
	public int updateParam1(final int playerId, final int eventId, final int param1) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param1", param1);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam1", params);
    }
    
    @Override
	public int updateParam1All(final int eventId, final int param1) {
        final Params params = new Params();
        params.addParam("eventId", eventId).addParam("param1", param1);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam1All", params);
    }
    
    @Override
	public int updateParam2(final int playerId, final int eventId, final int param2) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param2", param2);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam2", params);
    }
    
    @Override
	public int addParam2WithParam3(final int eventId) {
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addParam2WithParam3", eventId);
    }
    
    @Override
	public int updateParam3(final int playerId, final int eventId, final int param3) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param3", param3);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam3", params);
    }
    
    @Override
	public int updateParam3All(final int eventId, final int param3) {
        final Params params = new Params();
        params.addParam("eventId", eventId).addParam("param3", param3);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam3All", params);
    }
    
    @Override
	public int updateParam4(final int playerId, final int eventId, final int param4) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param4", param4);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam4", params);
    }
    
    @Override
	public int updateParam4All(final int eventId, final int param4) {
        final Params params = new Params();
        params.addParam("eventId", eventId).addParam("param4", param4);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam4All", params);
    }
    
    @Override
	public int updateParam5(final int playerId, final int eventId, final int param5) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param5", param5);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam5", params);
    }
    
    @Override
	public int updateParam6(final int playerId, final int eventId, final int param6) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param6", param6);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam6", params);
    }
    
    @Override
	public int updateCd1(final int playerId, final int eventId, final Date cd1) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("cd1", cd1);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateCd1", params);
    }
    
    @Override
	public int updateCd1All(final int eventId, final Date cd1) {
        final Params params = new Params();
        params.addParam("eventId", eventId).addParam("cd1", cd1);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateCd1All", params);
    }
    
    @Override
	public int updateParam8All(final int eventId, final int param8) {
        final Params params = new Params();
        params.addParam("eventId", eventId).addParam("param8", param8);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam8All", params);
    }
    
    @Override
	public int addBmw(final int playerId, final int eventId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        final Integer result = this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addBmw", params);
        if (result >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "bmw", num, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public int addXo(final int playerId, final int eventId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        final Integer result = this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addXo", params);
        if (result >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "xo", num, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public int addPicasso(final int playerId, final int eventId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        final Integer result = this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addPicasso", params);
        if (result >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "picasso", num, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public List<PlayerEvent> getIronRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getIronRewardList", (Object)eventId);
    }
    
    @Override
	public int addParam1(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addParam1", params);
    }
    
    @Override
	public int addParam2(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addParam2", params);
    }
    
    @Override
	public int addParam3(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addParam3", params);
    }
    
    @Override
	public int addParam4(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addParam4", params);
    }
    
    @Override
	public int addParam4Reduce5(final int playerId, final int eventId, final int num4, final int num5) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num4", num4).addParam("num5", num5);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addParam4Reduce5", params);
    }
    
    @Override
	public int addParam5(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addParam5", params);
    }
    
    @Override
	public int addParam1andParam2(final int playerId, final int eventId, final int num1, final int num2) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num1", num1).addParam("num2", num2);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.addParam1andParam2", params);
    }
    
    @Override
	public int reduceParam1(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.reduceParam1", params);
    }
    
    @Override
	public int reduceParam2(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.reduceParam2", params);
    }
    
    @Override
	public int reduceParam4(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.reduceParam4", params);
    }
    
    @Override
	public int reduceParam5(final int playerId, final int eventId, final int num) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num", num);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.reduceParam5", params);
    }
    
    @Override
	public List<PlayerEvent> getXiLianRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getXiLianRewardList", (Object)eventId);
    }
    
    @Override
	public int reduceParam1AndParam6(final int playerId, final int eventId, final int num1, final int num6) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num1", num1).addParam("num6", num6);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.reduceParam1AndParam6", params);
    }
    
    @Override
	public int reduceParam2AndParam7(final int playerId, final int eventId, final int num2, final int num7) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num2", num2).addParam("num7", num7);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.reduceParam2AndParam7", params);
    }
    
    @Override
	public int reduceParam3AndParam8(final int playerId, final int eventId, final int num3, final int num8) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num3", num3).addParam("num8", num8);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.reduceParam3AndParam8", params);
    }
    
    @Override
	public int updateParam9UpdateParam10(final int playerId, final int eventId, final int num9, final int num10) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("num9", num9).addParam("num10", num10);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam9UpdateParam10", params);
    }
    
    @Override
	public List<PlayerEvent> getChristmasDayRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getChristmasDayRewardList", (Object)eventId);
    }
    
    @Override
	public List<PlayerEvent> getWishRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getWishRewardList", (Object)eventId);
    }
    
    @Override
	public List<PlayerEvent> getBeastRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getBeastRewardList", (Object)eventId);
    }
    
    @Override
	public List<PlayerEvent> getRedPaperRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getRedPaperRewardList", (Object)eventId);
    }
    
    @Override
	public List<PlayerEvent> getBaiNianRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getBaiNianRewardList", (Object)eventId);
    }
    
    @Override
	public List<PlayerEvent> getLanternRankList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getLanternRankList", (Object)eventId);
    }
    
    @Override
	public List<PlayerEvent> getLanternRankRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getLanternRankRewardList", (Object)eventId);
    }
    
    @Override
	public List<PlayerEvent> getLanternTitleRewardList(final int eventId) {
        return (List<PlayerEvent>)this.getSqlSession().selectList("com.reign.gcld.event.domain.PlayerEvent.getLanternTitleRewardList", (Object)eventId);
    }
    
    @Override
	public int updateParam1updateParam2(final int playerId, final int eventId, final int param1, final int param2) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param1", param1).addParam("param2", param2);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam1updateParam2", params);
    }
    
    @Override
	public int updateParam1updateParam2updateParam5updateCd1(final int playerId, final int eventId, final int param1, final int param2, final int param5, final Date cd1) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param1", param1).addParam("param2", param2).addParam("param5", param5).addParam("cd1", cd1);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam1updateParam2updateParam5updateCd1", params);
    }
    
    @Override
	public int updateParam2updateCD1(final int playerId, final int eventId, final int param2, final Date cd1) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("eventId", eventId).addParam("param2", param2).addParam("cd1", cd1);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam2updateCD1", params);
    }
    
    @Override
	public int updateParam1updateParam2WithCondition(final int eventId, final int param1, final int param2) {
        final Params params = new Params();
        params.addParam("eventId", eventId).addParam("param1", param1).addParam("param2", param2);
        return this.getSqlSession().update("com.reign.gcld.event.domain.PlayerEvent.updateParam1updateParam2WithCondition", params);
    }
}
