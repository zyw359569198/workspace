package com.reign.gcld.player.common;

import org.springframework.stereotype.*;
import com.reign.gcld.common.log.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.activity.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.common.event.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.user.dto.*;
import com.reign.gcld.player.domain.*;
import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import sun.misc.*;
import java.util.*;

@Component("resourceUpdateSynService")
public class ResourceUpdateSynService implements IResourceUpdateSynService
{
    private static final Logger logger;
    private static ReentrantLock[] locks;
    private static final int LOCKS_LEN;
    public static ConcurrentHashMap<Integer, Resource> playerResourceMap;
    public static final int LIMIT_VALUE = 1000;
    public static final int LIMIT_IRON_VALUE = 100;
    public static final int RETURN_STATE_0 = -1;
    public static final int RETURN_STATE_1 = 1;
    public static final int RETURN_STATE_2 = -2;
    public static final int RETURN_STATE_3 = -3;
    public static final int RETURN_STATE_4 = -4;
    public static final int RETURN_STATE_5 = -5;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IActivityService activityService;
    
    static {
        logger = new AsynchronousDBOperationLogger();
        ResourceUpdateSynService.locks = new ReentrantLock[10240];
        LOCKS_LEN = ResourceUpdateSynService.locks.length;
        for (int i = 0; i < ResourceUpdateSynService.LOCKS_LEN; ++i) {
            ResourceUpdateSynService.locks[i] = new ReentrantLock(false);
        }
        ResourceUpdateSynService.playerResourceMap = new ConcurrentHashMap<Integer, Resource>();
    }
    
    @Override
    public void clearPlayerResourceMap() {
        for (final Integer playerId : ResourceUpdateSynService.playerResourceMap.keySet()) {
            final Resource rs = ResourceUpdateSynService.playerResourceMap.get(playerId);
            if (System.currentTimeMillis() - rs.getUpdateTime() > 86400000L) {
                ResourceUpdateSynService.playerResourceMap.remove(playerId);
            }
        }
    }
    
    @Override
    public int getCopper(final int playerId) {
        return this.getResouce(playerId).getCopper();
    }
    
    @Override
    public int getWood(final int playerId) {
        return this.getResouce(playerId).getWood();
    }
    
    @Override
    public int getFood(final int playerId) {
        return this.getResouce(playerId).getFood();
    }
    
    @Override
    public int getIron(final int playerId) {
        return this.getResouce(playerId).getIron();
    }
    
    @Override
    public Date getUpdateTime(final int playerId) {
        return new Date(this.getResouce(playerId).getUpdateTime());
    }
    
    @Override
    public long getKfgzVersion(final int playerId) {
        return this.getResouce(playerId).getKfgzVersion();
    }
    
    @Override
    public void setKfgzVersion(final int playerId, final long kfgzVersion) {
        this.getResouce(playerId).setKfgzVersion(kfgzVersion);
    }
    
    @Override
    public int updateResource(final ResourceParams rParams) {
        if (rParams.copper == 0 && rParams.wood == 0 && rParams.food == 0 && rParams.iron == 0) {
            ResourceUpdateSynService.logger.info("#rs#return#" + rParams.playerId + "#0#0#0#0");
            return 1;
        }
        final Player player = this.playerDao.read(rParams.playerId);
        try {
            ResourceUpdateSynService.locks[rParams.playerId % ResourceUpdateSynService.LOCKS_LEN].lock();
            Resource rs = this.getResouce(rParams.playerId);
            final PlayerDto playerDto = Players.getPlayer(rParams.playerId);
            if (playerDto != null) {
                final UserDto usrDto = Users.getUserDto(playerDto.userId, playerDto.yx);
                if (usrDto != null) {
                    if (rParams.copper > 0) {
                        rParams.copper = (int)usrDto.getAntiAddictionStateMachine().getCurrentState().getIntDataAfterAntiAddiction(rParams.copper);
                    }
                    if (rParams.wood > 0) {
                        rParams.wood = (int)usrDto.getAntiAddictionStateMachine().getCurrentState().getIntDataAfterAntiAddiction(rParams.wood);
                    }
                    if (rParams.food > 0) {
                        rParams.food = (int)usrDto.getAntiAddictionStateMachine().getCurrentState().getIntDataAfterAntiAddiction(rParams.food);
                    }
                    if (rParams.iron > 0) {
                        rParams.iron = (int)usrDto.getAntiAddictionStateMachine().getCurrentState().getIntDataAfterAntiAddiction(rParams.iron);
                    }
                }
            }
            final int tC = rs.getCopper() + rParams.copper;
            final int tW = rs.getWood() + rParams.wood;
            final int tF = rs.getFood() + rParams.food;
            final int tI = rs.getIron() + rParams.iron;
            Label_0473: {
                if (!rParams.isKF) {
                    if (tC < 0 && rParams.copper < 0 && !rParams.canMinus) {
                        this.playerResourceDao.pushIncenseData(rParams.playerId, 1);
                    }
                    else if (tW < 0 && rParams.wood < 0) {
                        this.playerResourceDao.pushIncenseData(rParams.playerId, 2);
                    }
                    else {
                        if (tF < 0 && rParams.food < 0) {
                            this.playerResourceDao.pushIncenseData(rParams.playerId, 3);
                            return -4;
                        }
                        if (tI < 0 && rParams.iron < 0) {
                            this.playerResourceDao.pushIncenseData(rParams.playerId, 4);
                            return -5;
                        }
                        break Label_0473;
                    }
                    return -2;
                }
            }
            if (tC > 500000000) {
                rParams.copper = calAddValue(rParams.copper, rs.getCopper(), 500000000);
            }
            if (rParams.isCareMax) {
                if (tC > rParams.maxC) {
                    rParams.copper = calAddValue(rParams.copper, rs.getCopper(), rParams.maxC);
                }
                if (tW > rParams.maxW) {
                    rParams.wood = calAddValue(rParams.wood, rs.getWood(), rParams.maxW);
                }
                if (tF > rParams.maxF) {
                    rParams.food = calAddValue(rParams.food, rs.getFood(), rParams.maxF);
                }
                if (tI > rParams.maxI) {
                    rParams.iron = calAddValue(rParams.iron, rs.getIron(), rParams.maxI);
                }
            }
            if (rParams.copper > 0) {
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "copper", rParams.copper, "+", String.valueOf(rParams.attribute.toString()) + "\u94f6\u5e01", player.getForceId(), player.getConsumeLv()));
                EventListener.fireEvent(new CommonEvent(2, rParams.playerId));
            }
            else if (rParams.copper < 0) {
                EventListener.fireEvent(new CommonEvent(2, rParams.playerId));
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "copper", rParams.copper, "-", String.valueOf(rParams.attribute.toString()) + "\u94f6\u5e01", player.getForceId(), player.getConsumeLv()));
            }
            if (rParams.wood > 0) {
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "wood", rParams.wood, "+", String.valueOf(rParams.attribute.toString()) + "\u6728\u6750", player.getForceId(), player.getConsumeLv()));
                EventListener.fireEvent(new CommonEvent(7, rParams.playerId));
            }
            else if (rParams.wood < 0) {
                EventListener.fireEvent(new CommonEvent(7, rParams.playerId));
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "wood", rParams.wood, "-", String.valueOf(rParams.attribute.toString()) + "\u6728\u6750", player.getForceId(), player.getConsumeLv()));
            }
            if (rParams.food > 0) {
                EventListener.fireEvent(new CommonEvent(8, rParams.playerId));
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "food", rParams.food, "+", String.valueOf(rParams.attribute.toString()) + "\u7cae\u98df", player.getForceId(), player.getConsumeLv()));
            }
            else if (rParams.food < 0) {
                EventListener.fireEvent(new CommonEvent(8, rParams.playerId));
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "food", rParams.food, "-", String.valueOf(rParams.attribute.toString()) + "\u7cae\u98df", player.getForceId(), player.getConsumeLv()));
            }
            if (rParams.iron > 0) {
                EventListener.fireEvent(new CommonEvent(12, rParams.playerId));
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "iron", rParams.iron, "+", String.valueOf(rParams.attribute.toString()) + "\u9554\u94c1", player.getForceId(), player.getConsumeLv()));
                if (rParams.joinActivity && this.activityService.inIronActivity()) {
                    final String temp = String.valueOf(rParams.playerId) + "#" + rParams.iron;
                    this.jobService.addJob("activityService", "updateIron", temp, System.currentTimeMillis(), false);
                }
            }
            else if (rParams.iron < 0) {
                EventListener.fireEvent(new CommonEvent(12, rParams.playerId));
                ThreadLocalFactory.addTreadLocalLog(LogUtil.formatPlayerInfoLog(player.getPlayerId(), player.getPlayerName(), player.getPlayerLv(), player.getUserId(), player.getYx(), "iron", rParams.iron, "-", String.valueOf(rParams.attribute.toString()) + "\u9554\u94c1", player.getForceId(), player.getConsumeLv()));
            }
            TaskMessageHelper.sendResourceTotalTaskMessage(rParams.playerId);
            rs.setCopper(rs.getCopper() + rParams.copper);
            rs.setWood(rs.getWood() + rParams.wood);
            rs.setFood(rs.getFood() + rParams.food);
            rs.setIron(rs.getIron() + rParams.iron);
            rs.setC(rs.getC() + rParams.copper);
            rs.setW(rs.getW() + rParams.wood);
            rs.setF(rs.getF() + rParams.food);
            rs.setI(rs.getI() + rParams.iron);
            if (rParams.updateTime) {
                rs.setUpdateTime(System.currentTimeMillis());
            }
            if (Math.abs(rs.getC()) > 1000 || Math.abs(rs.getW()) > 1000 || Math.abs(rs.getF()) > 1000 || Math.abs(rs.getI()) > 100) {
                final int res = this.playerResourceDao.resourceUpdate(rParams.playerId, rs);
                ResourceUpdateSynService.logger.info("#rs#db#" + rParams.playerId + "#" + rs.getC() + "#" + rs.getW() + "#" + rs.getF() + "#" + rs.getI() + "#" + rs.getCopper() + "#" + rs.getWood() + "#" + rs.getFood() + "#" + rs.getIron() + "#" + res);
                if (res == 1) {
                    rs = this.getResouce(rParams.playerId, rs);
                    rs.setC(0);
                    rs.setW(0);
                    rs.setF(0);
                    rs.setI(0);
                }
                else {
                    ResourceUpdateSynService.logger.info("#rs#dbFail#" + rParams.playerId + "#" + rParams.copper + "#" + rParams.wood + "#" + rParams.food + "#" + rParams.iron + "#" + res);
                    rs.setCopper(rs.getCopper() - rParams.copper);
                    rs.setWood(rs.getWood() - rParams.wood);
                    rs.setFood(rs.getFood() - rParams.food);
                    rs.setIron(rs.getIron() - rParams.iron);
                    rs.setC(rs.getC() - rParams.copper);
                    rs.setW(rs.getW() - rParams.wood);
                    rs.setF(rs.getF() - rParams.food);
                    rs.setI(rs.getI() - rParams.iron);
                }
            }
            return 1;
        }
        catch (Exception e) {
            ResourceUpdateSynService.logger.error("ResourceUpdateSynService ERROR", e);
        }
        finally {
            ResourceUpdateSynService.locks[rParams.playerId % ResourceUpdateSynService.LOCKS_LEN].unlock();
        }
        return -1;
    }
    
    public static int calAddValue(int add, final int cur, final int max) {
        if (cur > max) {
            add = 0;
        }
        else if (cur + add > max) {
            add = max - cur;
        }
        return add;
    }
    
    @Override
    public Resource getResouce(final int playerId) {
        if (playerId < 0) {
            return null;
        }
        Resource rs = ResourceUpdateSynService.playerResourceMap.get(playerId);
        if (rs == null) {
            final PlayerResource pr = this.playerResourceDao.get(playerId);
            rs = new Resource(pr.getCopper(), pr.getWood(), pr.getFood(), pr.getIron());
            rs.setUpdateTime(pr.getUpdateTime().getTime());
            rs.setKfgzVersion((pr.getKfgzVersion() == null) ? 0L : ((long)pr.getKfgzVersion()));
            ResourceUpdateSynService.playerResourceMap.put(playerId, rs);
        }
        return rs;
    }
    
    private Resource getResouce(final int playerId, final Resource rs) {
        Resource newRs = new Resource();
        final PlayerResource pr = this.playerResourceDao.get(playerId);
        newRs = new Resource(pr.getCopper(), pr.getWood(), pr.getFood(), pr.getIron());
        newRs.setUpdateTime(pr.getUpdateTime().getTime());
        newRs.setKfgzVersion((pr.getKfgzVersion() == null) ? 0L : ((long)pr.getKfgzVersion()));
        ResourceUpdateSynService.playerResourceMap.put(playerId, newRs);
        return newRs;
    }
}
