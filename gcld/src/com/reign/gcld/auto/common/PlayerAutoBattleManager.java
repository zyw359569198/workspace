package com.reign.gcld.auto.common;

import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.log.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.world.service.*;
import com.reign.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.general.dto.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public class PlayerAutoBattleManager
{
    private static final Logger timerLog;
    public static final int SleepTime = 5000;
    private IDataGetter dataGetter;
    private DaemonThread daemonThread;
    private static final PlayerAutoBattleManager instance;
    public final ConcurrentHashMap<Integer, PlayerAutoBattleObj> playerAutoBattleObjMap;
    
    static {
        timerLog = new TimerLogger();
        instance = new PlayerAutoBattleManager();
    }
    
    private PlayerAutoBattleManager() {
        this.dataGetter = null;
        this.daemonThread = null;
        this.playerAutoBattleObjMap = new ConcurrentHashMap<Integer, PlayerAutoBattleObj>();
    }
    
    public static PlayerAutoBattleManager getInstance() {
        return PlayerAutoBattleManager.instance;
    }
    
    public void initPlayerAutoBattleManager(final IDataGetter dataGetter) {
        this.dataGetter = dataGetter;
        (this.daemonThread = new DaemonThread()).start();
    }
    
    public void putToMap(final int playerId, final PlayerAutoBattleObj playerAutoBattleObj) {
        this.playerAutoBattleObjMap.put(playerId, playerAutoBattleObj);
    }
    
    public PlayerAutoBattleObj getPlayerAutoBattleObj(final int playerId) {
        return this.playerAutoBattleObjMap.get(playerId);
    }
    
    public void stopOnePlayer(final PlayerAutoBattleObj playerAutoBattleObj) {
        synchronized (PlayerAutoBattleManager.instance) {
            playerAutoBattleObj.state = 0;
            playerAutoBattleObj.targetCityId = 0;
            playerAutoBattleObj.autoType = 0;
            playerAutoBattleObj.cityIdlist = null;
        }
        // monitorexit(PlayerAutoBattleManager.instance)
    }
    
    public void increaseExp(final int playerId, final int expAdd) {
        try {
            final PlayerAutoBattleObj playerAutoBattleObj = this.playerAutoBattleObjMap.get(playerId);
            if (playerAutoBattleObj == null || playerAutoBattleObj.state != 1) {
                return;
            }
            synchronized (playerAutoBattleObj) {
                final PlayerAutoBattleObj playerAutoBattleObj2 = playerAutoBattleObj;
                playerAutoBattleObj2.exp += expAdd;
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " increaseExp catch Exception", e);
        }
    }
    
    public void increaseLost(final int playerId, final int lostAdd) {
        try {
            final PlayerAutoBattleObj playerAutoBattleObj = this.playerAutoBattleObjMap.get(playerId);
            if (playerAutoBattleObj == null || playerAutoBattleObj.state != 1) {
                return;
            }
            synchronized (playerAutoBattleObj) {
                final PlayerAutoBattleObj playerAutoBattleObj2 = playerAutoBattleObj;
                playerAutoBattleObj2.lost += lostAdd;
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " increaseLost catch Exception", e);
        }
    }
    
    public void stopAutoBattleAfterBattleEnded(final City city) {
        synchronized (PlayerAutoBattleManager.instance) {
            for (final PlayerAutoBattleObj playerAutoBattleObj : getInstance().playerAutoBattleObjMap.values()) {
                if (playerAutoBattleObj.state == 1) {
                    if (playerAutoBattleObj.targetCityId != city.getId()) {
                        continue;
                    }
                    int flag = 0;
                    if (playerAutoBattleObj.autoType == 1) {
                        if (city.getForceId() == playerAutoBattleObj.forceId) {
                            flag = 1;
                        }
                    }
                    else if (city.getForceId() == playerAutoBattleObj.forceId) {
                        flag = 3;
                    }
                    else {
                        flag = 4;
                    }
                    if (flag == 0) {
                        continue;
                    }
                    playerAutoBattleObj.result = flag;
                    getInstance().stopOnePlayer(playerAutoBattleObj);
                }
            }
        }
        // monitorexit(PlayerAutoBattleManager.instance)
    }
    
    private boolean scheduleOneGeneral(final PlayerAutoBattleObj playerAutoBattleObj, PlayerGeneralMilitary pgm) {
        try {
            final int playerId = playerAutoBattleObj.playerId;
            final int generalId = pgm.getGeneralId();
            final GeneralMoveDto gmd = CityService.getGeneralMoveDto(playerId, generalId);
            if (gmd != null && CDUtil.isInCD(gmd.nextMoveTime, new Date())) {
                return false;
            }
            if (gmd != null && (gmd.cityState == 22 || gmd.cityState == 23)) {
                if (gmd.cityState == 22) {
                    PlayerDto playerDto = Players.getPlayer(playerId);
                    if (playerDto == null) {
                        final Player player = this.dataGetter.getPlayerDao().read(playerId);
                        final PlayerAttribute playerAttribute = this.dataGetter.getPlayerAttributeDao().read(playerId);
                        playerDto = PlayerDtoUtil.getPlayerDto(player, playerAttribute);
                    }
                    this.dataGetter.getSlaveService().escape(playerDto, generalId);
                }
                return false;
            }
            if (pgm.getState() == 3 || pgm.getState() == 14) {
                this.dataGetter.getAutoBattleService().zidongdantiao(pgm);
                return false;
            }
            final int forcesMax = this.dataGetter.getBattleDataCache().getMaxHp(pgm);
            if (pgm.getForces() < forcesMax) {
                final Player player = this.dataGetter.getPlayerDao().read(playerId);
                final PlayerDto playerDto2 = new PlayerDto();
                playerDto2.playerId = player.getPlayerId();
                playerDto2.playerLv = player.getPlayerLv();
                playerDto2.playerName = player.getPlayerName();
                playerDto2.platForm = PlatForm.PC;
                playerDto2.forceId = player.getForceId();
                this.dataGetter.getGeneralService().cdRecoverConfirm(playerDto2, generalId, 1);
            }
            pgm = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitary(playerId, generalId);
            if (pgm.getForces() >= forcesMax) {
                this.dataGetter.getAutoBattleService().assembleOneGeneral(playerAutoBattleObj, pgm);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " scheduleOneGeneral catch Exception", e);
            return false;
        }
    }
    
    private class DaemonThread extends Thread
    {
        public DaemonThread() {
            super("PlayerAutoBattleManager-DaemonThread");
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (PlayerAutoBattleManager.instance) {
                        for (final PlayerAutoBattleObj playerAutoBattleObj : PlayerAutoBattleManager.this.playerAutoBattleObjMap.values()) {
                            if (playerAutoBattleObj.state == 1) {
                                if (System.currentTimeMillis() < playerAutoBattleObj.needCheckTime) {
                                    continue;
                                }
                                if (playerAutoBattleObj.endTime <= System.currentTimeMillis()) {
                                    if (playerAutoBattleObj.autoType == 1) {
                                        playerAutoBattleObj.result = 2;
                                    }
                                    else {
                                        playerAutoBattleObj.result = 5;
                                    }
                                    PlayerAutoBattleManager.this.stopOnePlayer(playerAutoBattleObj);
                                }
                                else {
                                    final List<PlayerGeneralMilitary> pgmList = PlayerAutoBattleManager.this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerAutoBattleObj.playerId);
                                    for (final PlayerGeneralMilitary pgm : pgmList) {
                                        PlayerAutoBattleManager.this.scheduleOneGeneral(playerAutoBattleObj, pgm);
                                    }
                                    playerAutoBattleObj.needCheckTime = System.currentTimeMillis() + 10000L;
                                }
                            }
                        }
                    }
                    // monitorexit(PlayerAutoBattleManager.access$0())
                }
                catch (Exception e) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run catch Exception", e);
                    try {
                        Thread.sleep(5000L);
                    }
                    catch (InterruptedException e2) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                    }
                    continue;
                }
                finally {
                    try {
                        Thread.sleep(5000L);
                    }
                    catch (InterruptedException e2) {
                        ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                    }
                }
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e2) {
                    ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " run.sleep catch Exception", e2);
                }
            }
        }
    }
}
