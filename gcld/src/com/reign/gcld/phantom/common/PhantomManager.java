package com.reign.gcld.phantom.common;

import com.reign.gcld.common.log.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.log.*;
import java.util.concurrent.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.domain.*;

public class PhantomManager
{
    private static final Logger timeLog;
    public Map<Integer, PlayerPhantomObj> playerPhantomObjMap;
    private static final PhantomManager instance;
    IDataGetter dataGetter;
    
    static {
        timeLog = new TimerLogger();
        instance = new PhantomManager();
    }
    
    private PhantomManager() {
        this.playerPhantomObjMap = new ConcurrentHashMap<Integer, PlayerPhantomObj>();
        this.dataGetter = null;
    }
    
    public static PhantomManager getInstance() {
        return PhantomManager.instance;
    }
    
    public void initPhantomManager(final IDataGetter dataGetter) {
        try {
            PhantomManager.timeLog.info("PhantomManager.initPhantomManager start.");
            this.dataGetter = dataGetter;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomManager.initPhantomManager catch Exception", e);
        }
    }
    
    private void caculateOnePlayerPhantomObj(final int playerId, final int maxLv) {
        try {
            final PlayerPhantomObj playerPhantomObj = new PlayerPhantomObj();
            if (maxLv >= 2) {
                playerPhantomObj.costCoe = 0.75;
            }
            if (maxLv >= 3) {
                playerPhantomObj.maxPhantomNum = 60;
            }
            if (maxLv >= 4) {
                playerPhantomObj.isAutoOutPut = true;
            }
            if (maxLv >= 5) {
                playerPhantomObj.baoJiType = 2;
            }
            this.playerPhantomObjMap.put(playerId, playerPhantomObj);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomManager.caculateOnePlayerPhantomObj catch Exception", e);
        }
    }
    
    public void refreshOnePlayer(final int playerId) {
        try {
            final String fString = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerId);
            final char[] cs = fString.toCharArray();
            if (cs[64] != '1') {
                return;
            }
            final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
            this.caculateOnePlayerPhantomObj(pba.getPlayerId(), pba.getPhantomWorkShopLv());
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomManager.refreshOnePlayer catch Exception", e);
        }
    }
}
