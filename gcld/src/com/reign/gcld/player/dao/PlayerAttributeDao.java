package com.reign.gcld.player.dao;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.common.event.*;
import java.util.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;

@Component("playerAttributeDao")
public class PlayerAttributeDao extends BaseDao<PlayerAttribute> implements IPlayerAttributeDao
{
    @Autowired
    private IPlayerDao playerDao;
    
    @Override
	public PlayerAttribute readForUpdate(final int playerId) {
        return (PlayerAttribute)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<PlayerAttribute> getModels() {
        return (List<PlayerAttribute>)this.getSqlSession().selectList("com.reign.gcld.player.domain.PlayerAttribute.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.getModelSize");
    }
    
    @Override
	public int create(final PlayerAttribute playerAttribute) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.PlayerAttribute.create", playerAttribute);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.PlayerAttribute.deleteById", playerId);
    }
    
    @Override
	public PlayerAttribute read(final int playerId) {
        final PlayerAttribute result = (PlayerAttribute)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.read", (Object)playerId);
        if (result != null) {
            KfgzMatchService.freshPlayerResourceCacheRecruitToken(playerId, result.getRecruitToken());
        }
        return result;
    }
    
    @Override
	public int updateFunction(final int playerId, final String functionId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("functionId", functionId);
        EventListener.fireEvent(new CommonEvent(13, playerId));
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.updateFunction", params);
    }
    
    @Override
	public int addMaxStoreNum(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.addMaxStoreNum", playerId);
    }
    
    @Override
	public int setIsNewArea(final int playerId, final String isAreaNew) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("isAreaNew", isAreaNew);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setIsNewArea", params);
    }
    
    @Override
	public int updateEnterCount(final int playerId, final int enterCount) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("enterCount", enterCount);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.updateEnterCount", params);
    }
    
    @Override
	public void updateLastResetTime(final int playerId, final Date date) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("date", date);
        this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.updateLastResetTime", params);
    }
    
    @Override
	public void resetData(final int playerId, final Date date, final int recruitToken) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("date", date).addParam("recruitToken", recruitToken).addParam("max", 100);
        final int before = this.getRecruitTokenNum(playerId);
        final int result = this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.resetData", params);
        final int after = this.getRecruitTokenNum(playerId);
        if (result >= 1 && after > before) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "token", after - before, "+", "\u6bcf\u65e5\u7b2c\u4e00\u6b21\u767b\u9646\u91cd\u7f6e\u6570\u636e\u589e\u52a0\u52df\u5175\u4ee4", player.getForceId(), player.getConsumeLv()));
        }
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("hasSalary", true));
    }
    
    @Override
	public void addBattleWinTimes(final int playerId) {
        this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.addBattleWinTimes", playerId);
    }
    
    @Override
	public void addBattleRewardTimes(final int playerId) {
        this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.addBattleRewardTimes", playerId);
    }
    
    @Override
	public int updateRecruitToken(final int playerId, final int reduceNum, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("reduceNum", reduceNum);
        final int result = this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.updateRecruitToken", params);
        if (result >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "token", reduceNum, "-", attribute, player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public int addRecruitToken(final int playerId, final int addNum, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addNum", addNum);
        final int result = this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.addRecruitToken", params);
        if (result >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "token", addNum, "+", attribute, player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public void addPayPoint(final int playerId, final int addPoint) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("addPoint", addPoint);
        this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.addPayPoint", params);
    }
    
    @Override
	public void updateLastGiftTime(final int playerId, final Date date) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("date", date);
        this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.updateLastGiftTime", params);
    }
    
    @Override
	public int updateIronMineTime(final Date date, final int playerId) {
        final Params params = new Params();
        params.addParam("date", date).addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.updateIronMineTime", params);
    }
    
    @Override
	public int updateGemMineTime(final Date date, final int playerId) {
        final Params params = new Params();
        params.addParam("date", date).addParam("playerId", playerId);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.updateGemMineTime", params);
    }
    
    @Override
	public int getMaxStoreNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.getMaxStoreNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int setMaxStoreNum(final int playerId, final int maxStoreNum) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("maxStoreNum", maxStoreNum);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setMaxStoreNum", params);
    }
    
    @Override
	public void updateGuideId(final int playerId, final int guideId) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("guideId", guideId);
        this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.updateGuideId", params);
    }
    
    @Override
	public int setBlackMarketCd(final int playerId, final Date date) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("date", date);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setBlackMarketCd", params);
    }
    
    @Override
	public Date getBlackMarketCd(final int playerId) {
        return (Date)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.getBlackMarketCd", (Object)playerId);
    }
    
    @Override
	public String getFunctionId(final int playerId) {
        return (String)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.getFunctionId", (Object)playerId);
    }
    
    @Override
	public int setTechResearch(final int playerId, final int techResearch) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("techResearch", techResearch);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setTechResearch", params);
    }
    
    @Override
	public int setTechOpenAndTechResearch(final int playerId, final int techOpen, final int techResearch) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("techOpen", techOpen);
        params.addParam("techResearch", techResearch);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setTechOpenAndTechResearch", params);
    }
    
    @Override
	public int setTechOpen(final int playerId, final int techOpen) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("techOpen", techOpen);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setTechOpen", params);
    }
    
    @Override
	public int setRecruitToken(final int playerId, final int recruitToken) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("recruitToken", recruitToken);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setRecruitToken", params);
    }
    
    @Override
	public int incrementIntimacy(final int playerId, final int inti) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("intimacy", inti);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.incrementIntimacy", params);
    }
    
    @Override
	public int addFreeConstructionNum(final int playerId, final int num, final String attribute) {
        final Params params = new Params();
        params.addParam("playerId", playerId).addParam("num", num);
        final int result = this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.addFreeConstructionNum", params);
        if (result >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freecons", num, "+", attribute, player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public int getFreeConstructionNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.getFreeConstructionNum", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int consumeFreeConstructionNum(final int playerId, final String attribute) {
        final int result = this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.consumeFreeConstructionNum", playerId);
        if (result >= 1) {
            final Player player = this.playerDao.read(playerId);
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "freecons", 1.0, "-", attribute, player.getForceId(), player.getConsumeLv()));
        }
        return result;
    }
    
    @Override
	public int getRecruitTokenNum(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.getRecruitTokenNum", (Object)playerId);
        if (result != null) {
            KfgzMatchService.freshPlayerResourceCacheRecruitToken(playerId, result);
        }
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int setHasBandit(final int playerId, final int hasBandit) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("hasBandit", hasBandit);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setHasBandit", params);
    }
    
    @Override
	public int setKidnapper(final int playerId, final int kidnapper) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("kidnapper", kidnapper);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.setKidnapper", params);
    }
    
    @Override
	public int getHasBandit(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.getHasBandit", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int getKidnapper(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.PlayerAttribute.getKidnapper", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int killBandit(final int playerId, final int alive) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("alive", alive);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.killBandit", params);
    }
    
    @Override
	public int killKidnapper(final int playerId, final int alive) {
        final Params params = new Params();
        params.addParam("playerId", playerId);
        params.addParam("alive", alive);
        return this.getSqlSession().update("com.reign.gcld.player.domain.PlayerAttribute.killKidnapper", params);
    }
}
