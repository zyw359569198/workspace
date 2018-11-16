package com.reign.gcld.mine.service;

import org.springframework.stereotype.*;
import com.reign.gcld.mine.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.blacksmith.dao.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.mine.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.blacksmith.domain.*;
import java.util.*;
import com.reign.gcld.player.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.common.*;
import com.reign.util.*;

@Component("mineService")
public class MineService implements IMineService
{
    @Autowired
    private IPlayerMineDao playerMineDao;
    @Autowired
    private MineCache mineCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IPlayerMineBatInfoDao playerMineBatInfoDao;
    @Autowired
    private IPlayerBlacksmithDao playerBlacksmithDao;
    
    @Override
    public byte[] getMineInfo(int page, final int style, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        if (style != 1 && style != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (page < 0 || page > MineCache.maxPageNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int type = 0;
        int forceMineType = 0;
        if (style == 1) {
            type = 2;
            forceMineType = 1;
        }
        else {
            type = 4;
            forceMineType = 3;
        }
        if (page == 0) {
            final PlayerMine playerMine = this.playerMineDao.getByOwner(playerId, type);
            if (playerMine != null) {
                page = playerMine.getPage();
            }
            else {
                page = 1;
            }
        }
        final List<Mine> totalMines = this.mineCache.getMines(type, page);
        final Map<Integer, PlayerMine> map = this.playerMineDao.getByPage(page, type);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerBlacksmith pb = this.playerBlacksmithDao.getByPlayerIdAndSmithId(playerId, 1);
        doc.createElement("haveSmith", pb != null);
        doc.startArray("mines");
        for (final Mine mine : totalMines) {
            doc.startObject();
            doc.createElement("id", mine.getId());
            doc.createElement("type", mine.getType());
            doc.createElement("mineName", mine.getName());
            doc.createElement("position", mine.getPagePos());
            doc.createElement("pic", mine.getPic());
            doc.createElement("output", mine.getOutput());
            doc.createElement("stone", mine.getStone());
            if (map != null && map.containsKey(mine.getId())) {
                final PlayerMine playerMine2 = map.get(mine.getId());
                doc.appendJson(this.getMineDetail(playerDto, mine, playerMine2));
            }
            else {
                doc.appendJson(this.getMineDetail(playerDto, mine, null));
            }
            doc.endObject();
        }
        final Mine forceMine = this.mineCache.getMines(forceMineType, 0).get(0);
        doc.startObject();
        doc.createElement("id", forceMine.getId());
        doc.createElement("type", forceMine.getType());
        doc.createElement("mineName", forceMine.getName());
        doc.createElement("position", forceMine.getPagePos());
        doc.createElement("pic", forceMine.getPic());
        doc.createElement("output", forceMine.getOutput());
        final PlayerMine PlayerForceMine = this.playerMineDao.getByMineId(forceMine.getId());
        doc.appendJson(this.getMineDetail(playerDto, forceMine, PlayerForceMine));
        doc.endObject();
        doc.endArray();
        doc.createElement("currentPage", page);
        doc.createElement("totalPage", MineCache.maxPageNum);
        doc.endObject();
        if (type == 4) {
            TaskMessageHelper.sendWorldMineJadeVisitMessage(playerId);
        }
        else {
            TaskMessageHelper.sendWorldMineIronVisitMessage(playerId);
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getMineDetail(final PlayerDto playerDto, final Mine mine, final PlayerMine playerMine) {
        final JsonDocument doc = new JsonDocument();
        if (mine.getType() == 3 || mine.getType() == 1) {
            if (playerMine == null) {
                doc.createElement("operation", "occupy");
                doc.createElement("canDo", true);
                return doc.toByte();
            }
            doc.createElement("forceId", playerMine.getOwnerId());
            if (playerMine.getOwnerId() != playerDto.forceId) {
                doc.createElement("operation", "occupy");
                doc.createElement("canDo", true);
                return doc.toByte();
            }
            final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
            Date lastMineTime = null;
            if (mine.getType() == 1) {
                lastMineTime = pa.getIronForcemineTime();
            }
            else {
                lastMineTime = pa.getGemForcemineTime();
            }
            if (lastMineTime == null) {
                doc.createElement("operation", "mine");
                doc.createElement("canDo", true);
            }
            else {
                final Calendar cg = Calendar.getInstance();
                cg.setTime(lastMineTime);
                if (cg.get(6) == Calendar.getInstance().get(6)) {
                    doc.createElement("operation", "mine");
                    doc.createElement("canDo", false);
                }
                else {
                    doc.createElement("operation", "mine");
                    doc.createElement("canDo", true);
                }
            }
        }
        else {
            if (playerMine == null) {
                doc.createElement("operation", "occupy");
                doc.createElement("canDo", true);
                return doc.toByte();
            }
            final Date nowDate = new Date();
            final int ownerId = playerMine.getOwnerId();
            final Player owner = this.playerDao.read(ownerId);
            if (ownerId == playerDto.playerId) {
                doc.createElement("isNew", playerMine.getIsNew());
                if (playerMine.getIsNew() == 1) {
                    this.playerMineDao.updateIsNew(playerMine.getVId(), 0);
                }
                doc.createElement("myself", true);
                doc.createElement("currentOutPut", this.getOutPut(mine, playerMine));
                doc.createElement("currentOutPutStone", this.getOutPutStone(mine, playerMine));
                doc.createElement("double", playerMine.getMode() == 2);
                final long time = CDUtil.getCD(nowDate, playerMine.getStartTime());
                final long timeLeft = Math.max(mine.getTime() * 60000L - time, 0L);
                doc.createElement("cd", timeLeft);
                doc.createElement("ownerPic", owner.getPic());
                doc.createElement("forceId", playerDto.forceId);
                doc.createElement("flag", playerMine.getMode() == 1);
                if (time < 900000L) {
                    if (playerMine.getMode() == 1) {
                        doc.createElement("operation", "rush");
                        doc.createElement("canDo", true);
                    }
                    else {
                        doc.createElement("operation", "abandon");
                        doc.createElement("canDo", false);
                    }
                }
                else {
                    doc.createElement("operation", "abandon");
                    doc.createElement("canDo", true);
                }
            }
            else if (owner.getForceId() == playerDto.forceId) {
                doc.createElement("forceId", owner.getForceId());
                if (playerMine.getMode() == 1) {
                    doc.createElement("ownerName", owner.getPlayerName());
                    doc.createElement("ownerPic", owner.getPic());
                    doc.createElement("flag", true);
                }
                doc.createElement("double", playerMine.getMode() == 2);
                doc.createElement("operation", "rob");
                doc.createElement("canDo", true);
            }
            else {
                if (playerMine.getMode() == 1) {
                    doc.createElement("forceId", owner.getForceId());
                    doc.createElement("ownerName", owner.getPlayerName());
                    doc.createElement("flag", true);
                }
                doc.createElement("operation", "occupy");
                doc.createElement("canDo", true);
            }
        }
        return doc.toByte();
    }
    
    private int getOutPut(final Mine mine, final PlayerMine playerMine) {
        final Date nowDate = new Date();
        if (playerMine.getHideTime() == null) {
            final long cd = CDUtil.getCD(nowDate, playerMine.getStartTime());
            long minutes = cd / 60000L;
            minutes = Math.min(minutes, mine.getTime());
            return (int)(mine.getOutput() * (minutes / 60.0));
        }
        final long normalCd = CDUtil.getCD(playerMine.getHideTime(), playerMine.getStartTime());
        final long normalMins = normalCd / 60000L;
        final long rushCd = CDUtil.getCD(nowDate, playerMine.getHideTime());
        final long rushMins = rushCd / 60000L;
        return (int)(normalMins / 60.0 * mine.getOutput() + rushMins / 60.0 * mine.getOutput() * 2.0);
    }
    
    private int getOutPutStone(final Mine mine, final PlayerMine playerMine) {
        final Date nowDate = new Date();
        final long cd = CDUtil.getCD(nowDate, playerMine.getStartTime());
        long minutes = cd / 60000L;
        minutes = Math.min(minutes, mine.getTime());
        return (int)(mine.getStone() * minutes * 1.0 / mine.getTime());
    }
    
    @Transactional
    @Override
    public byte[] rush(final int style, final PlayerDto playerDto) {
        if (style != 1 && style != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int type = 0;
        if (style == 1) {
            type = 2;
        }
        else {
            type = 4;
        }
        final Date nowDate = new Date();
        final int playerId = playerDto.playerId;
        final PlayerMine playerMine = this.playerMineDao.getByOwner(playerId, type);
        if (playerMine == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_HAVE_NO_MINE);
        }
        if (playerMine.getMode() == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_ALREADY_RUSH);
        }
        if (CDUtil.getCD(nowDate, playerMine.getStartTime()) > 900000L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_TIME_PASS);
        }
        this.playerMineDao.updateMode(playerMine.getVId(), 2, nowDate);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] abandon(final int style, final PlayerDto playerDto) {
        if (style != 1 && style != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Date nowDate = new Date();
        final int playerId = playerDto.playerId;
        boolean functionOpen = false;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        int type = 0;
        if (style == 1) {
            type = 2;
            if (cs[34] == '1') {
                functionOpen = true;
            }
        }
        else {
            type = 4;
            if (cs[40] == '1') {
                functionOpen = true;
            }
        }
        if (!functionOpen) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final Player player = this.playerDao.read(playerId);
        final PlayerMine playerMine = this.playerMineDao.getByOwner(playerId, type);
        if (playerMine == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_HAVE_NO_MINE);
        }
        if (CDUtil.getCD(nowDate, playerMine.getStartTime()) < 900000L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_TIME_NOT_ENOUGH);
        }
        final Mine mine = (Mine)this.mineCache.get((Object)playerMine.getMineId());
        if (mine == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_NO_SUCH_MINE);
        }
        final int output = this.getOutPut(mine, playerMine);
        if (type == 4) {
            this.storeHouseService.gainGem(player, output, 1, LocalMessages.T_LOG_GEM_5, null);
        }
        else {
            this.playerResourceDao.addIronIgnoreMax(playerId, output, "\u4e16\u754c\u94c1\u77ff\u6536\u83b7\u9554\u94c1", true);
            final PlayerBlacksmith pb = this.playerBlacksmithDao.getByPlayerIdAndSmithId(playerId, 1);
            if (pb != null) {
                final int stone = this.getOutPutStone(mine, playerMine);
                if (stone > 0) {
                    this.storeHouseService.gainItems(playerId, stone, 1401, "\u653e\u5f03\u4e2a\u4eba\u94c1\u77ff\u83b7\u5f97\u7384\u94c1\u77f3");
                }
            }
        }
        this.jobService.cancelJob(playerMine.getTaskId(), true);
        this.playerMineDao.deleteById(playerMine.getVId());
        this.playerMineBatInfoDao.deleteById(playerMine.getMineId());
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public OccupyMineInfo handleAfterBattle(final int mineId, final int winnerId) {
        final Date nowDate = new Date();
        final Mine mine = (Mine)this.mineCache.get((Object)mineId);
        if (mine == null) {
            return null;
        }
        final int type = mine.getType();
        final OccupyMineInfo omi = new OccupyMineInfo();
        omi.type = type;
        omi.outPut = mine.getOutput();
        if (type == 4 || type == 2) {
            final PlayerMine ownerMine = this.playerMineDao.getByOwner(winnerId, type);
            if (ownerMine != null) {
                this.playerMineDao.deleteById(ownerMine.getVId());
                this.playerMineBatInfoDao.deleteById(ownerMine.getMineId());
            }
            int output = 0;
            int stone = 0;
            final PlayerMine playerMine = this.playerMineDao.getByMineId(mineId);
            final Player winPlayer = this.playerDao.read(winnerId);
            PlayerBlacksmith pb = null;
            final PlayerBlacksmith pbWin = this.playerBlacksmithDao.getByPlayerIdAndSmithId(winnerId, 1);
            if (playerMine != null) {
                final int playerId = playerMine.getOwnerId();
                final Player player = this.playerDao.read(playerId);
                if (player.getForceId() == winPlayer.getForceId()) {
                    omi.state = 2;
                }
                else {
                    omi.state = 3;
                }
                output = this.getOutPut(mine, playerMine);
                stone = this.getOutPutStone(mine, playerMine);
                stone /= 2;
                if (type == 4) {
                    this.storeHouseService.gainGem(player, output / 2, 1, LocalMessages.T_LOG_GEM_7, null);
                }
                else {
                    this.playerResourceDao.addIronIgnoreMax(playerId, output / 2, "\u4e16\u754c\u94c1\u77ff\u6536\u83b7\u9554\u94c1", true);
                    pb = this.playerBlacksmithDao.getByPlayerIdAndSmithId(playerId, 1);
                    if (pb != null && stone > 0) {
                        this.storeHouseService.gainItems(playerId, stone, 1401, "\u4e2a\u4eba\u94c1\u77ff\u88ab\u62a2\u83b7\u5f97\u7384\u94c1\u77f3");
                    }
                }
                omi.num = output / 2;
                if (pbWin != null && pb != null) {
                    omi.stoneNum = stone;
                }
                this.jobService.cancelJob(playerMine.getTaskId(), true);
                this.playerMineDao.deleteById(playerMine.getVId());
                this.playerMineBatInfoDao.deleteById(playerMine.getMineId());
                String mailContent = "";
                if (mine.getType() == 4) {
                    mailContent = MessageFormatter.format(LocalMessages.MINE_GEM_MAIL_ROBBED, new Object[] { winPlayer.getPlayerName(), output / 2 });
                }
                else if (pb != null && stone > 0) {
                    mailContent = MessageFormatter.format(LocalMessages.MINE_IRON_MAIL_ROBBED_WITH_STONE, new Object[] { winPlayer.getPlayerName(), output / 2, stone });
                }
                else {
                    mailContent = MessageFormatter.format(LocalMessages.MINE_IRON_MAIL_ROBBED, new Object[] { winPlayer.getPlayerName(), output / 2 });
                }
                this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.MINE_MAIL_TITLE, mailContent, 1, playerId, 9);
            }
            else {
                omi.state = 1;
            }
            final PlayerMine newPlayerMine = new PlayerMine();
            newPlayerMine.setMineId(mineId);
            newPlayerMine.setOwnerId(winnerId);
            newPlayerMine.setMode(1);
            newPlayerMine.setPage(mine.getPage());
            newPlayerMine.setStartTime(nowDate);
            newPlayerMine.setType(type);
            newPlayerMine.setState(1);
            final int taskId = this.jobService.addJob("mineService", "releaseMineOutput", String.valueOf(mineId), nowDate.getTime() + mine.getTime() * 60000L);
            newPlayerMine.setTaskId(taskId);
            newPlayerMine.setIsNew(1);
            this.playerMineDao.create(newPlayerMine);
            if (output > 0) {
                if (type == 4) {
                    this.storeHouseService.gainGem(winPlayer, output / 2, 1, LocalMessages.T_LOG_GEM_10, null);
                }
                else {
                    this.playerResourceDao.addIronIgnoreMax(winnerId, output / 2, "\u4e16\u754c\u94c1\u77ff\u6536\u83b7\u9554\u94c1", true);
                    if (pb != null && pbWin != null && stone > 0) {
                        this.storeHouseService.gainItems(winnerId, stone, 1401, "\u62a2\u6218\u4e2a\u4eba\u94c1\u77ff\u83b7\u5f97\u7384\u94c1\u77f3");
                    }
                }
            }
            if (pbWin != null) {
                omi.outPutStone = mine.getStone();
            }
            return omi;
        }
        if (type == 3 || type == 1) {
            final PlayerMine oldMine = this.playerMineDao.getByMineId(mineId);
            if (oldMine != null) {
                this.playerMineDao.deleteById(oldMine.getVId());
                this.playerMineBatInfoDao.deleteById(oldMine.getMineId());
                if (oldMine.getOwnerId() == winnerId) {
                    omi.state = 2;
                }
                else {
                    omi.state = 3;
                }
            }
            else {
                omi.state = 1;
            }
            final PlayerMine playerMine2 = new PlayerMine();
            playerMine2.setMineId(mineId);
            playerMine2.setOwnerId(winnerId);
            playerMine2.setMode(1);
            playerMine2.setPage(mine.getPage());
            playerMine2.setStartTime(nowDate);
            playerMine2.setType(type);
            playerMine2.setState(1);
            playerMine2.setIsNew(0);
            this.playerMineDao.create(playerMine2);
            return omi;
        }
        return null;
    }
    
    @Transactional
    @Override
    public void releaseMineOutput(final String params) {
        final int mineId = Integer.valueOf(params);
        final PlayerMine playerMine = this.playerMineDao.getByMineId(mineId);
        if (playerMine != null) {
            final Mine mine = (Mine)this.mineCache.get((Object)playerMine.getMineId());
            if (mine == null || mine.getType() == 3 || mine.getType() == 1) {
                return;
            }
            final int playerId = playerMine.getOwnerId();
            final Player player = this.playerDao.read(playerId);
            final int type = mine.getType();
            final int output = this.getOutPut(mine, playerMine);
            final PlayerBlacksmith pb = this.playerBlacksmithDao.getByPlayerIdAndSmithId(playerId, 1);
            final int stone = this.getOutPutStone(mine, playerMine);
            if (type == 4) {
                this.storeHouseService.gainGem(player, output, 1, LocalMessages.T_LOG_GEM_9, null);
            }
            else {
                this.playerResourceDao.addIronIgnoreMax(playerId, output, "\u4e16\u754c\u94c1\u77ff\u6536\u83b7\u9554\u94c1", true);
                if (pb != null && stone > 0) {
                    this.storeHouseService.gainItems(playerId, stone, 1401, "\u4e2a\u4eba\u94c1\u77ff\u5b9a\u65f6\u4efb\u52a1\u7ed3\u7b97\u83b7\u5f97\u7384\u94c1\u77f3");
                }
            }
            this.playerMineDao.deleteById(playerMine.getVId());
            this.playerMineBatInfoDao.deleteById(playerMine.getMineId());
            String mailContent = "";
            if (mine.getType() == 4) {
                mailContent = MessageFormatter.format(LocalMessages.MINE_GEM_MAIL_FINISH, new Object[] { output });
            }
            else if (pb != null && stone > 0) {
                mailContent = MessageFormatter.format(LocalMessages.MINE_IRON_MAIL_FINISH_WITH_STONE, new Object[] { output, stone });
            }
            else {
                mailContent = MessageFormatter.format(LocalMessages.MINE_IRON_MAIL_FINISH, new Object[] { output });
            }
            this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.MINE_MAIL_TITLE, mailContent, 1, playerId, 9);
        }
    }
    
    @Transactional
    @Override
    public byte[] mine(final int style, final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final Date nowDate = new Date();
        if (style != 1 && style != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        int type = 0;
        int mineId = 0;
        if (style == 1) {
            type = 1;
            mineId = MineCache.ironForceMineId;
        }
        else {
            type = 3;
            mineId = MineCache.gemForceMineId;
        }
        final PlayerMine playerMine = this.playerMineDao.getByMineId(mineId);
        if (playerMine == null || playerMine.getOwnerId() != playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_FORCE_MINE_NOT_OWN);
        }
        if (playerMine.getType() != type) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        boolean functionOpen = false;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        Date lastMineTime = null;
        if (type == 1) {
            lastMineTime = pa.getIronForcemineTime();
            if (cs[34] == '1') {
                functionOpen = true;
            }
        }
        else {
            lastMineTime = pa.getGemForcemineTime();
            if (cs[40] == '1') {
                functionOpen = true;
            }
        }
        if (!functionOpen) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        boolean canMine = false;
        if (lastMineTime == null) {
            canMine = true;
        }
        else {
            final Calendar cg = Calendar.getInstance();
            cg.setTime(lastMineTime);
            if (cg.get(6) != Calendar.getInstance().get(6)) {
                canMine = true;
            }
        }
        if (!canMine) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_ALREADY_MINE);
        }
        final Mine mine = (Mine)this.mineCache.get((Object)mineId);
        if (mine == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.MINE_NO_SUCH_MINE);
        }
        final int output = mine.getOutput();
        if (type == 1) {
            this.playerResourceDao.addIronIgnoreMax(playerId, output, "\u4e16\u754c\u94c1\u77ff\u6536\u83b7\u9554\u94c1", true);
            this.playerAttributeDao.updateIronMineTime(nowDate, playerId);
        }
        else if (type == 3) {
            final Player player = this.playerDao.read(playerId);
            this.storeHouseService.gainGem(player, output, 1, LocalMessages.T_LOG_GEM_8, null);
            this.playerAttributeDao.updateGemMineTime(nowDate, playerId);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("output", output);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
}
