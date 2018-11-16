package com.reign.gcld.common.util;

import org.springframework.stereotype.*;
import com.reign.gcld.player.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;

@Component("dataPushCenterUtil")
public class DataPushCenterUtil
{
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private IRankService rankService;
    private static ConcurrentMap<Integer, Long> blackMarketCdMap;
    private static ConcurrentMap<Integer, Long> investCdMap;
    private static byte[] send;
    private static byte[] sendInvest;
    private static final Logger timerLog;
    
    static {
        DataPushCenterUtil.blackMarketCdMap = new ConcurrentHashMap<Integer, Long>();
        DataPushCenterUtil.investCdMap = new ConcurrentHashMap<Integer, Long>();
        DataPushCenterUtil.send = JsonBuilder.getSimpleJson("displayBlack", 1);
        DataPushCenterUtil.sendInvest = JsonBuilder.getSimpleJson("displayInvest", 1);
        timerLog = new TimerLogger();
    }
    
    public void addBlackMarketCd(final int playerId, final Long cd) {
        DataPushCenterUtil.blackMarketCdMap.put(playerId, cd);
    }
    
    public void addInvestCd(final int playerId, final Long cd) {
        DataPushCenterUtil.investCdMap.put(playerId, cd);
    }
    
    public void removeBlackMarketCd(final int playerId) {
        DataPushCenterUtil.blackMarketCdMap.remove(playerId);
    }
    
    public void removeInvestCd(final int playerId) {
        DataPushCenterUtil.investCdMap.remove(playerId);
    }
    
    private void pushBlackMarketCd() {
        final List<Integer> delList = new ArrayList<Integer>();
        final long now = System.currentTimeMillis();
        for (final Map.Entry<Integer, Long> entry : DataPushCenterUtil.blackMarketCdMap.entrySet()) {
            final int playerId = entry.getKey();
            if (Players.getPlayer(playerId) == null) {
                delList.add(playerId);
            }
            else {
                final long cd = entry.getValue();
                if (cd > now || !this.canPushBlackPrompt(playerId)) {
                    continue;
                }
                Players.push(playerId, PushCommand.PUSH_UPDATE, DataPushCenterUtil.send);
                delList.add(playerId);
            }
        }
        for (final int playerId2 : delList) {
            DataPushCenterUtil.blackMarketCdMap.remove(playerId2);
        }
    }
    
    private void pushInvestCd() {
        final int nationTask1 = this.rankService.hasNationTasks(1);
        final int nationTask2 = this.rankService.hasNationTasks(2);
        final int nationTask3 = this.rankService.hasNationTasks(3);
        final int[] type = { nationTask1, nationTask2, nationTask3 };
        final List<Integer> delList = new ArrayList<Integer>();
        final long now = System.currentTimeMillis();
        for (final Map.Entry<Integer, Long> entry : DataPushCenterUtil.investCdMap.entrySet()) {
            final int playerId = entry.getKey();
            if (Players.getPlayer(playerId) == null) {
                delList.add(playerId);
            }
            else {
                final PlayerDto playerDto = Players.getPlayer(playerId);
                if (type[playerDto.forceId - 1] != 4) {
                    delList.add(playerId);
                }
                final long cd = entry.getValue();
                if (cd > now) {
                    continue;
                }
                Players.push(playerId, PushCommand.PUSH_UPDATE, DataPushCenterUtil.sendInvest);
                delList.add(playerId);
            }
        }
        for (final int playerId2 : delList) {
            DataPushCenterUtil.investCdMap.remove(playerId2);
        }
    }
    
    public void pushData() {
        final long start = System.currentTimeMillis();
        this.pushBlackMarketCd();
        this.pushInvestCd();
        DataPushCenterUtil.timerLog.info(LogUtil.formatThreadLog("DataPushCenterUtil", "pushData", 2, System.currentTimeMillis() - start, ""));
    }
    
    public void remove(final int playerId) {
        this.removeBlackMarketCd(playerId);
        this.removeInvestCd(playerId);
    }
    
    public boolean canPushBlackPrompt(final int playerId) {
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        return pr.getFood() < this.buildingOutputCache.getBuildingOutput(playerId, 48) && (pr.getCopper() > this.buildingOutputCache.getBuildingOutput(playerId, 16) * 0.75 || pr.getWood() > this.buildingOutputCache.getBuildingOutput(playerId, 32) * 0.75);
    }
}
