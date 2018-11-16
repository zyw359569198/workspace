package com.reign.gcld.building.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.building.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.timer.dao.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.player.domain.*;
import com.reign.util.*;
import com.reign.gcld.building.domain.*;
import java.util.*;
import com.reign.gcld.timer.domain.*;
import com.reign.gcld.common.*;

@Component("buildingController")
public class BuildingController implements IBuildingController
{
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IPlayerBuildingWorkDao playerBuildingWorkDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerJobDao playerJobDao;
    
    @Override
    public byte[] freeCdRecoverConfirm(final int playerId) {
        final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        if (cs[56] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int count = this.playerBuildingWorkDao.getBusyWorkNum(playerId);
        if (count <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10011);
        }
        final int num = this.playerAttributeDao.getFreeConstructionNum(playerId);
        if (num <= 0) {
            final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)43);
            final Player player = this.playerDao.read(playerId);
            if (ci.getLv() > player.getConsumeLv()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
            }
            if (!this.playerDao.consumeGold(player, ci)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
        }
        else {
            this.playerAttributeDao.consumeFreeConstructionNum(playerId, "\u5347\u7ea7\u5efa\u7b51\u4f7f\u7528\u9ec4\u91d1\u5efa\u7b51\u961f");
        }
        this.constructionComplete(playerId);
        TaskMessageHelper.sendUseFreeConsTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("freeConsNum", this.playerAttributeDao.getFreeConstructionNum(playerId)));
    }
    
    @Override
    public void constructionComplete(final int playerId) {
        final Tuple<Boolean, Object> tuple = new Tuple();
        tuple.left = false;
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            final List<PlayerBuildingWork> pbwList = this.playerBuildingWorkDao.getBusyWorkList(playerId);
            PlayerBuilding pb = null;
            for (final PlayerBuildingWork pbw : pbwList) {
                final PlayerJob playerJob = this.playerJobDao.read(pbw.getTaskId());
                pb = this.buildingService.getPlayerBuilding(playerId, pbw.getTargetBuildId());
                if (playerJob != null) {
                    final Object[] params = BuildingService.parseParams(playerJob.getParams(), 2);
                    final boolean isAuto = Integer.valueOf(params[3].toString()) == 1;
                    if (pb == null) {
                        continue;
                    }
                    final CallBack cBack = this.buildingService.doUpgrade(BuildingService.getParams(playerId, pbw.getTargetBuildId(), pbw.getWorkId(), isAuto, pb.getLv()));
                    if (cBack == null) {
                        continue;
                    }
                    cBack.call();
                }
                else {
                    final CallBack cBack2 = this.buildingService.doUpgrade(BuildingService.getParams(playerId, pbw.getTargetBuildId(), pbw.getWorkId(), false, pb.getLv()));
                    if (cBack2 == null) {
                        continue;
                    }
                    cBack2.call();
                }
            }
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
        Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
    }
}
