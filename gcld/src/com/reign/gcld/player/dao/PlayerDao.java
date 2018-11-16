package com.reign.gcld.player.dao;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.framework.mybatis.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.event.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.framework.jdbc.*;
import java.util.*;

@Component("playerDao")
public class PlayerDao extends BaseDao<Player> implements IPlayerDao
{
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IBatchExecute batchExecute;
    
    @Override
	public Player read(final int playerId) {
        final Player result = (Player)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.read", (Object)playerId);
        if (result != null) {
            KfgzMatchService.freshPlayerResourceCacheGold(playerId, result.getUserGold() + result.getSysGold());
        }
        return result;
    }
    
    @Override
	public Player readForUpdate(final int playerId) {
        return (Player)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.readForUpdate", (Object)playerId);
    }
    
    @Override
	public List<Player> getModels() {
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getModels");
    }
    
    @Override
	public int getModelSize() {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getModelSize");
    }
    
    @Override
	public int create(final Player player) {
        return this.getSqlSession().insert("com.reign.gcld.player.domain.Player.create", player);
    }
    
    @Override
	public int deleteById(final int playerId) {
        return this.getSqlSession().delete("com.reign.gcld.player.domain.Player.deleteById", playerId);
    }
    
    @Override
	public Player getPlayerByName(final String playerName) {
        return (Player)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getPlayerByName", (Object)playerName);
    }
    
    @Override
	public List<Player> getPlayerByUserId(final String userId, final String yx) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        params.addParam("yx", (Object)yx);
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerByUserId", (Object)params);
    }
    
    @Override
	public List<Player> getPlayerOnlyByUserId(final String userId) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerOnlyByUserId", (Object)params);
    }
    
    @Override
	public int getRoleCount(final String userId, final String yx) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        params.addParam("yx", (Object)yx);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getRoleCount", (Object)params);
    }
    
    @Override
	public int getRoleCountByUid(final String userId) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getRoleCountByUid", (Object)params);
    }
    
    @Override
	public List<Player> getPlayerLevelRankList(final int count) {
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerLevelRankList", (Object)count);
    }
    
    @Override
	public List<Player> getSamePlayerLevel(final int count, final int playerLv, final int forceId) {
        final Params params = new Params();
        params.addParam("count", (Object)count).addParam("playerLv", (Object)playerLv).addParam("forceId", (Object)forceId);
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getSamePlayerLevel", (Object)params);
    }
    
    @Override
	public List<Player> getPlayerLevelRankList(final int forceId, final int count) {
        final Params params = new Params();
        params.addParam("forceId", (Object)forceId).addParam("count", (Object)count);
        final List<Player> p = (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerLevelRankListByForceId", (Object)params);
        return p;
    }
    
    @Override
	public boolean consumeGold(final Player player, final Chargeitem ci) {
        return this._consumeGold(player, ci.getCost(), ci.getName(), false);
    }
    
    @Override
	public boolean consumeGold(final Player player, final int gold, final Object attribute) {
        return this._consumeGold(player, gold, attribute, false);
    }
    
    @Override
	public void consumeUserGold(final Player player, final int gold, final Object attribute) {
        final Params params = new Params();
        params.addParam("playerId", (Object)player.getPlayerId());
        params.addParam("gold", (Object)gold);
        final int row = this.getSqlSession().update("com.reign.gcld.player.domain.Player.consumeUserGold", (Object)params);
        if (row > 0) {
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "ugold", gold, "-", attribute, player.getForceId(), player.getConsumeLv()));
        }
    }
    
    @Override
	public void addSysGold(final Player player, final int gold, final Object attribute) {
        final int playerId = player.getPlayerId();
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("sysGold", (Object)gold);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.Player.addSysGold", (Object)params);
        if (affectRows == 1) {
            EventListener.fireEvent(new CommonEvent(1, player.getPlayerId()));
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "sgold", gold, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
    }
    
    @Override
	public boolean addUserGold(final Player player, final int gold, final Object attribute) {
        final int playerId = player.getPlayerId();
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("userGold", (Object)gold);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.Player.addUserGold", (Object)params);
        if (affectRows != 1) {
            return false;
        }
        EventListener.fireEvent(new CommonEvent(1, player.getPlayerId()));
        ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "ugold", gold, "+", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        return true;
    }
    
    @Override
	public void setSysGold(final Player player, final int gold, final Object attribute) {
        final int playerId = player.getPlayerId();
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("sysGold", (Object)gold);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.Player.setSysGold", (Object)params);
        if (affectRows == 1) {
            EventListener.fireEvent(new CommonEvent(1, player.getPlayerId()));
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "sgold", gold, "=", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
    }
    
    @Override
	public void setUserGold(final Player player, final int gold, final Object attribute) {
        final int playerId = player.getPlayerId();
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("userGold", (Object)gold);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.Player.setUserGold", (Object)params);
        if (affectRows == 1) {
            EventListener.fireEvent(new CommonEvent(1, player.getPlayerId()));
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "ugold", gold, "=", attribute.toString(), player.getForceId(), player.getConsumeLv()));
        }
    }
    
    @Override
	public boolean updatePlayerLv(final int playerId, final int playerLv) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("playerLv", (Object)playerLv);
        final boolean done = this.getSqlSession().update("com.reign.gcld.player.domain.Player.updatePlayerLv", (Object)params) == 1;
        if (done) {
            this.dataGetter.getCourtesyService().addPlayerEvent(playerId, 1, 0);
        }
        return done;
    }
    
    @Override
	public boolean updateMaxLv(final int playerId, final int maxLv) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("maxLv", (Object)maxLv);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updateMaxLv", (Object)params) == 1;
    }
    
    @Override
	public boolean canConsumeMoney(final Player player, final int gold) {
        if (player.getGold() < gold) {
            this.pushIncenseData(player.getPlayerId());
            return false;
        }
        return true;
    }
    
    @Override
	public boolean consumeGoldForKFGZ(final Player player, final int gold) {
        return this._consumeGold(player, gold, LocalMessages.ATTRIBUTEKEY_KFGZ_1, true);
    }
    
    private boolean _consumeGold(Player player, final int gold, final Object itemName, final boolean isKFGZ) {
        if (gold <= 0) {
            return true;
        }
        if (!isKFGZ && player.getGold() < gold) {
            this.pushIncenseData(player.getPlayerId());
            return false;
        }
        int userGold = player.getUserGold();
        final int sysGold = player.getSysGold();
        if (sysGold < gold) {
            userGold = sysGold + userGold - gold;
            final Params params = new Params();
            params.addParam("playerId", (Object)player.getPlayerId());
            params.addParam("userGold", (Object)player.getUserGold());
            params.addParam("sysGold", (Object)player.getSysGold());
            params.addParam("newUserGold", (Object)userGold);
            params.addParam("newSysGold", (Object)0);
            final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.Player.consumeGold", (Object)params);
            if (1 != affectRows) {
                this.pushIncenseData(player.getPlayerId());
                return false;
            }
            if (sysGold > 0) {
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "sgold", sysGold, "-", itemName, player.getForceId(), player.getConsumeLv()));
            }
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "ugold", gold - sysGold, "-", itemName, player.getForceId(), player.getConsumeLv()));
            EventListener.fireEvent(new CommonEvent(1, player.getPlayerId()));
            return true;
        }
        else {
            final Params params = new Params();
            params.addParam("playerId", (Object)player.getPlayerId());
            params.addParam("gold", (Object)gold);
            final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.Player.consumeSysGold", (Object)params);
            if (1 != affectRows) {
                final int playerId = player.getPlayerId();
                player = (Player)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.read", (Object)playerId);
                return this._consumeGold(player, gold, itemName, isKFGZ);
            }
            ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "sgold", gold, "-", itemName, player.getForceId(), player.getConsumeLv()));
            EventListener.fireEvent(new CommonEvent(1, player.getPlayerId()));
            return true;
        }
    }
    
    @Override
	public int getForceMemberCount(final int forceId) {
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getForceMemberCount", (Object)forceId);
    }
    
    @Override
	public int updatePlayerForceId(final int playerId, final int forceId) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("forceId", (Object)forceId);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.setUserGold", (Object)params);
    }
    
    @Override
	public int updatePlayerNameAndPic(final int playerId, final String playerName, final int pic) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("playerName", (Object)playerName).addParam("pic", (Object)pic);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updatePlayerNameAndPic", (Object)params);
    }
    
    @Override
	public int updatePlayerName(final int playerId, final String playerName) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("playerName", (Object)playerName);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updatePlayerName", (Object)params);
    }
    
    @Override
	public int updatePlayerConsumeLv(final int playerId, final int consumeLv) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("consumeLv", (Object)consumeLv);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updatePlayerConsumeLv", (Object)params);
    }
    
    @Override
	public int updatePowerId(final int playerId, final int powerId) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("powerId", (Object)powerId);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updatePowerId", (Object)params);
    }
    
    @Override
	public int updateLoginTime(final int playerId, final Date loginTime) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("loginTime", (Object)loginTime);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updateLoginTime", (Object)params);
    }
    
    @Override
	public int updateQuitTime(final int playerId, final Date quitTime) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("quitTime", (Object)quitTime);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updateQuitTime", (Object)params);
    }
    
    @Override
	public void deletePlayer(final int playerId, final Date date) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("date", (Object)date);
        this.getSqlSession().update("com.reign.gcld.player.domain.Player.deletePlayer", (Object)params);
    }
    
    @Override
	public void retrievePlayer(final int playerId) {
        this.getSqlSession().update("com.reign.gcld.player.domain.Player.retrievePlayer", playerId);
    }
    
    private void pushIncenseData(final int playerId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("showIncense", true);
        doc.createElement("resourceType", 0);
        final Player player = this.read(playerId);
        doc.createElement("payUrl", PayUtil.getAbsolutePayUrl(player.getYx(), player.getUserId(), playerId));
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    @Override
	public List<Player> getPlayerByLevel(final int num) {
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerByLevel", (Object)num);
    }
    
    @Override
	public Tuple<Integer, Integer> getGoldStatistics(final int lvLimit, final int loginLimit) {
        final Params params = new Params();
        params.addParam("lv", (Object)lvLimit);
        params.addParam("date", (Object)new Date(System.currentTimeMillis() - loginLimit * 24 * 60 * 60 * 1000L));
        final Tuple<Integer, Integer> tuple = new Tuple();
        final Integer uGold = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getUgoldStatistics", (Object)params);
        final Integer sGold = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getSgoldStatistics", (Object)params);
        tuple.left = ((uGold == null) ? 0 : uGold);
        tuple.right = ((sGold == null) ? 0 : sGold);
        return tuple;
    }
    
    @Override
	public void addConsumeLv(final int playerId, final int addLv) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("addLv", (Object)addLv);
        this.getSqlSession().update("com.reign.gcld.player.domain.Player.addConsumeLv", (Object)params);
    }
    
    @Override
	public Player getPlayerByNameAndYx(final String playerName, final String yx) {
        final Params params = new Params();
        params.addParam("playerName", (Object)playerName);
        params.addParam("yx", (Object)yx);
        return (Player)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getPlayerByNameAndYx", (Object)params);
    }
    
    @Override
	public Player queryPlayer(final String userId, final String yx, final String playerName) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        params.addParam("yx", (Object)yx);
        params.addParam("playerName", (Object)playerName);
        return (Player)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.queryPlayer", (Object)params);
    }
    
    @Override
	public int getPlayerIdByNameAndYx(final String playerName, final String yx) {
        final Params params = new Params();
        params.addParam("playerName", (Object)playerName);
        params.addParam("yx", (Object)yx);
        final Integer temp = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getPlayerIdByNameAndYx", (Object)params);
        return (temp == null) ? 0 : temp;
    }
    
    @Override
	public int updateState(final int playerId, final int state) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("state", (Object)state);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updateState", (Object)params);
    }
    
    @Override
	public void updatePlayerServerId(final Integer playerId, final int playerServerId) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("playerServerId", (Object)playerServerId);
        this.getSqlSession().update("com.reign.gcld.player.domain.Player.updatePlayerServerId", (Object)params);
    }
    
    @Override
	public List<Player> getPlayerHasRegisterAuction() {
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerHasRegisterAuction");
    }
    
    @Override
	public int getPlayerLv(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getPlayerLv", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int getConsumeLv(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getConsumeLv", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public List<Integer> getPlayerIdList() {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerIdList");
    }
    
    @Override
	public String getPlayerName(final int playerId) {
        return (String)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getPlayerName", (Object)playerId);
    }
    
    @Override
	public int getForceIdByPlayerName(final String playerName) {
        final Integer temp = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getForceIdByPlayerName", (Object)playerName);
        if (temp == null) {
            return 0;
        }
        return temp;
    }
    
    @Override
	public List<Integer> getPlayerIdListByUserIdAndYx(final String userId, final String yx) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        params.addParam("yx", (Object)yx);
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerIdListByUserIdAndYx", (Object)params);
    }
    
    @Override
	public int getTotalUserGold(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getTotalUserGold", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public int getTotalTicketGold(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getTotalTicketGold", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public boolean addTotalUserGold(final int playerId, final int gold) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("totalUserGold", (Object)gold);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.Player.addTotalUserGold", (Object)params);
        return affectRows == 1;
    }
    
    @Override
	public int addTotalTicketGold(final int playerId, final int gold) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("gold", (Object)gold);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.addTotalTicketGold", (Object)params);
    }
    
    @Override
	public boolean setTotalUserGold(final Player player, final int gold) {
        final int playerId = player.getPlayerId();
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId).addParam("totalUserGold", (Object)gold);
        final int affectRows = this.getSqlSession().update("com.reign.gcld.player.domain.Player.setTotalUserGold", (Object)params);
        return affectRows == 1;
    }
    
    @Override
	public void resetTotalUserGold() {
        this.getSqlSession().update("com.reign.gcld.player.domain.Player.resetTotalUserGold");
    }
    
    @Override
	public int resetTotalTicketGold() {
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.resetTotalTicketGold");
    }
    
    @Override
	public int setYxSource(final int playerId, final String yxSource) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("yxSource", (Object)yxSource);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.setYxSource", (Object)params);
    }
    
    @Override
	public String getYxSource(final int playerId) {
        return (String)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getYxSource", (Object)playerId);
    }
    
    @Override
	public List<Player> getPlayerNoDeleteByUserIdAndYx(final String userId, final String yx) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        params.addParam("yx", (Object)yx);
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerNoDeleteByUserIdAndYx", (Object)params);
    }
    
    @Override
	public List<Integer> getPlayerIdByUp(final int playerId, final int forceId) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("forceId", (Object)forceId);
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerIdByUp", (Object)params);
    }
    
    @Override
	public List<Integer> getPlayerIdListByDown(final int playerId, final int forceId) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("forceId", (Object)forceId);
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerIdByDown", (Object)params);
    }
    
    @Override
	public int getForceId(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getForceId", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public List<Integer> getPlayerIdListByEqual(final int playerId, final int forceId) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("forceId", (Object)forceId);
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerIdListByEqual", (Object)params);
    }
    
    @Override
	public int getDefaultPlayerId(final String userId, final String yx) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        params.addParam("yx", (Object)yx);
        Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getDefaultPayPlayerId", (Object)params);
        if (result != null) {
            return result;
        }
        result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getDefaultPlayerId", (Object)params);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public List<Player> getPlayerList(final String yx, final Date date, final int page, final int size) {
        final Params params = new Params();
        params.addParam("yx", (Object)yx);
        params.addParam("date", (Object)date);
        params.addParam("start", (Object)((page - 1) * size));
        params.addParam("size", (Object)size);
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerList", (Object)params);
    }
    
    @Override
	public int getSizeByYxAndDate(final String yx, final Date date) {
        final Params params = new Params();
        params.addParam("yx", (Object)yx);
        params.addParam("date", (Object)date);
        return (int)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getSizeByYxAndDate", (Object)params);
    }
    
    @Override
	public Date getLoginTimeByPlayerId(final int playerId) {
        return (Date)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getLoginTimeByPlayerId", (Object)playerId);
    }
    
    @Override
	public Date getQuitTimeByPlayerId(final int playerId) {
        return (Date)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getQuitTimeByPlayerId", (Object)playerId);
    }
    
    @Override
	public void updateDailyOnlineTime(final int onlineTime, final int playerId) {
        final Params params = new Params();
        params.addParam("onlineTime", (Object)onlineTime);
        params.addParam("playerId", (Object)playerId);
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getDailyOnlineTime", (Object)playerId);
        if (result == null) {
            this.setDailyOnlineTime(0, playerId);
        }
        this.getSqlSession().update("com.reign.gcld.player.domain.Player.updateDailyOnlineTime", (Object)params);
    }
    
    @Override
	public void resetDailyOnlineTime() {
        this.getSqlSession().update("com.reign.gcld.player.domain.Player.resetDailyOnlineTime");
    }
    
    @Override
	public int getDailyOnlineTime(final int playerId) {
        final Integer result = (Integer)this.getSqlSession().selectOne("com.reign.gcld.player.domain.Player.getDailyOnlineTime", (Object)playerId);
        return (result == null) ? 0 : result;
    }
    
    @Override
	public void setDailyOnlineTime(final int value, final int playerId) {
        final Params params = new Params();
        params.addParam("value", (Object)value);
        params.addParam("playerId", (Object)playerId);
        this.getSqlSession().update("com.reign.gcld.player.domain.Player.setDailyOnlineTime", (Object)params);
    }
    
    @Override
	public int setGm(final int playerId, final int gm) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("gm", (Object)gm);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.setGm", (Object)params);
    }
    
    @Override
	public List<Player> getByGm() {
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getByGm");
    }
    
    @Override
	public int clearDefaultPay(final String userId, final String yx) {
        final Params params = new Params();
        params.addParam("userId", (Object)userId);
        params.addParam("yx", (Object)yx);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.clearDefaultPay", (Object)params);
    }
    
    @Override
	public int setDefaultPay(final int playerId) {
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.setDefaultPay", playerId);
    }
    
    @Override
	public int modifyUserId(final String yx, final String oldUserId, final String newUserId) {
        final Params params = new Params();
        params.addParam("yx", (Object)yx);
        params.addParam("oldUserId", (Object)oldUserId);
        params.addParam("newUserId", (Object)newUserId);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.modifyUserId", (Object)params);
    }
    
    @Override
	public List<Integer> getPlayerIdListAboveMinLv(final int minLv) {
        return (List<Integer>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPlayerIdListAboveMinLv", (Object)minLv);
    }
    
    @Override
	public List<PlayerPicLv> getPicLvsByPids(final List<Integer> playerIds) {
        return (List<PlayerPicLv>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getPicLvsByPids", (Object)playerIds);
    }
    
    @Override
	public List<Player> getByPlayerIds(final List<Integer> playerIds) {
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getByPlayerIds", (Object)playerIds);
    }
    
    @Override
	public List<Player> getByPlayerIdsCC(final List<Integer> playerIds) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("playerList", playerIds);
        map.put("maxLv", 70);
        return (List<Player>)this.getSqlSession().selectList("com.reign.gcld.player.domain.Player.getByPlayerIdsCC", (Object)map);
    }
    
    @Override
	public int updatePics(final List<Player> ps) {
        final String sql = "update player set pic = ? where player_id = ?";
        final List<List<Param>> paramsList = new ArrayList<List<Param>>();
        for (final Player player : ps) {
            final List<Param> params = new ArrayList<Param>();
            params.add(new Param(player.getPic(), Type.Int));
            params.add(new Param(player.getPlayerId(), Type.Int));
            paramsList.add(params);
        }
        return this.batchExecute.batch(this.getSqlSession(), sql, paramsList);
    }
    
    @Override
	public int updatePlayerSSP(final Integer playerId, final String serverNameServeridPlayerid) {
        final Params params = new Params();
        params.addParam("playerId", (Object)playerId);
        params.addParam("ssp", (Object)serverNameServeridPlayerid);
        return this.getSqlSession().update("com.reign.gcld.player.domain.Player.updatePlayerSSP", (Object)params);
    }
}
