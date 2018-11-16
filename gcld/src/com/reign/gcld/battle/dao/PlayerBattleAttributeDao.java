package com.reign.gcld.battle.dao;

import com.reign.gcld.battle.domain.*;
import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.kfgz.service.*;
import java.util.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;

@Component("playerBattleAttributeDao")
public class PlayerBattleAttributeDao extends BaseDao<PlayerBattleAttribute> implements IPlayerBattleAttributeDao
{
    @Autowired
    private IPlayerDao playerDao;
    
    @Override
	public PlayerBattleAttribute readForUpdate(final int playerId) {
        return (PlayerBattleAttribute)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleAttribute.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerBattleAttribute> getModels() {
        return (List<PlayerBattleAttribute>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerBattleAttribute.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleAttribute.getModelSize");
    }
    
    @Override
	public int create(final PlayerBattleAttribute playerBattleAttribute) {
        return this.getSqlSession().insert("com.reign.gcld.battle.domain.PlayerBattleAttribute.create", playerBattleAttribute);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.battle.domain.PlayerBattleAttribute.deleteById", playerId);
    }
    
    @Override
	public PlayerBattleAttribute read(final int playerId) {
        final PlayerBattleAttribute result = (PlayerBattleAttribute)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleAttribute.read", (Object)playerId);
        if (result != null) {
            KfgzMatchService.freshPlayerResourceCachePhantomCount(playerId, result.getVip3PhantomCount());
        }
        return result;
    }
    
    @Override
	public int updateModeTime(final int playerId, final int type, final Date supportTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("type", type).addParam("supportTime", supportTime);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateModeTime", params);
    }
    
    @Override
	public int updateArmiesBattleOrder(final int playerId, final String battleOrder) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("battleOrder", battleOrder);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateArmiesBattleOrder", params);
    }
    
    @Override
	public int updateArmiesAutoCount(final int playerId, final int autoCount) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("autoCount", autoCount);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateArmiesAutoCount", params);
    }
    
    @Override
	public int updateYoudiTime(final int playerId, final long youdiTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("youdiTime", youdiTime);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateYoudiTime", params);
    }
    
    @Override
	public int updateChujiTime(final int playerId, final long chujiTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("chujiTime", chujiTime);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateChujiTime", params);
    }
    
    @Override
	@Deprecated
    public int resetVip3PhantomCount(final int vipLv, final int phantomNum) {
        final Params params = new Params();
        params.addParam("vipLv", vipLv).addParam("phantomNum", phantomNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.resetVip3PhantomCount", params);
    }
    
    @Override
	@Deprecated
    public int updateVip3PhantomCount(final int playerId, final int phantomNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("phantomNum", phantomNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateVip3PhantomCount", params);
    }
    
    @Override
	public int addVip3PhantomCount(final int playerId, final int addNum, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addNum", addNum);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addVip3PhantomCount", params);
        if (affectRows >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freephantom", addNum, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return affectRows;
    }
    
    @Override
	public int addVip3PhantomCountMax(final int playerId, final int addNum, final int max, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addNum", addNum).addParam("max", max);
        final int before = this.getVip3PhantomCount(playerId);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addVip3PhantomCountMax", params);
        if (affectRows >= 1) {
            final int after = this.getVip3PhantomCount(playerId);
            final int num = after - before;
            if (num > 0) {
                final Player player = this.playerDao.read(playerId);
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freephantom", addNum, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
            }
        }
        return affectRows;
    }
    
    @Override
	public int decreaseVip3PhantomCount(final int playerId, final String attribute) {
        return this.decreaseSomeVip3PhantomCount(playerId, 1, attribute);
    }
    
    @Override
	public int decreaseSomeVip3PhantomCount(final int playerId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("num", num);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.decreaseSomeVip3PhantomCount", params);
        if (affectRows >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freephantom", num, "-", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
        return affectRows;
    }
    
    @Override
	public int updateAutoStrategy(final int playerId, final int autoStrategy) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("autoStrategy", autoStrategy);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateAutoStrategy", params);
    }
    
    @Override
	public int reduceTeamTimes(final int playerId, final int reduceTeamTimes) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("reduceTeamTimes", reduceTeamTimes);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.reduceTeamTimes", params);
    }
    
    @Override
	public int addTeamTimes(final int playerId, final int addTeamTimes) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addTeamTimes", addTeamTimes);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addTeamTimes", params);
    }
    
    @Override
	public int updateShouMaiManZuTime(final int playerId, final long shouMaiTime) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("shouMaiTime", shouMaiTime);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateShouMaiManZuTime", params);
    }
    
    @Override
	public int clearWinTimesAndFailTimes() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.clearWinTimesAndFailTimes");
    }
    
    @Override
	public int addWinTimes(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addWinTimes", playerId);
    }
    
    @Override
	public int addFailTimes(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addFailTimes", playerId);
    }
    
    @Override
	public int updateChangeBat(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateChangeBat", playerId);
    }
    
    @Override
	public int resetEventNationalTreasureCountToday() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.resetEventNationalTreasureCountToday");
    }
    
    @Override
	public int addEventGemCountToday(final int playerId, final int addNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addNum", addNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventGemCountToday", params);
    }
    
    @Override
	public int addEventJiebingCountToday(final int playerId, final int addNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addNum", addNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventJiebingCountToday", params);
    }
    
    @Override
	public int addEventGemCount(final int playerId, final int addNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addNum", addNum);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventGemCount", params);
    }
    
    @Override
	public int clearBatExpActivity() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.clearBatExpActivity");
    }
    
    @Override
	public int updateBatExpActivity(final int playerId, final int activityBatExp) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("activityBatExp", activityBatExp);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updateBatExpActivity", params);
    }
    
    @Override
	public int getVip3PhantomCount(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleAttribute.getVip3PhantomCount", (Object)playerId);
        if (result != null) {
            KfgzMatchService.freshPlayerResourceCachePhantomCount(playerId, result);
        }
        else {
            KfgzMatchService.freshPlayerResourceCachePhantomCount(playerId, 0);
        }
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int updatePhantomWorkShopLv(final int playerId, final int pwkLv) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("pwkLv", pwkLv);
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.updatePhantomWorkShopLv", params);
    }
    
    @Override
	public List<PlayerBattleAttribute> getHasPhantomWorkShopModels() {
        return (List<PlayerBattleAttribute>)this.getSqlSession().selectList("com.reign.gcld.battle.domain.PlayerBattleAttribute.getHasPhantomWorkShopModels");
    }
    
    @Override
	public int addEventXtysCountToday(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventXtysCountToday", playerId);
    }
    
    @Override
	public int addEventSdlrCountToday(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventSdlrCountToday", playerId);
    }
    
    @Override
	public int addEventNationalTreasureCountToday(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventNationalTreasureCountToday", playerId);
    }
    
    @Override
	public int getChangebat(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.battle.domain.PlayerBattleAttribute.getChangebat", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public void addEventWorldDramaCountToday(final int playerId) {
        this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventWorldDramaCountToday", playerId);
    }
    
    @Override
	public int resetEventNumToday() {
        return this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.resetEventNumToday");
    }
    
    @Override
	public void addEventLblCountToday(final int playerId) {
        this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventLblCountToday", playerId);
    }
    
    @Override
	public void addEventSlaveCountToday(final int playerId) {
        this.getSqlSession().update("com.reign.gcld.battle.domain.PlayerBattleAttribute.addEventSlaveCountToday", playerId);
    }
}
