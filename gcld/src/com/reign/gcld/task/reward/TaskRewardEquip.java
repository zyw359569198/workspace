package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.log.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.task.message.*;
import java.util.*;

public class TaskRewardEquip implements ITaskReward
{
    int equipId;
    private static final DayReportLogger logger;
    
    static {
        logger = new DayReportLogger();
    }
    
    public TaskRewardEquip(final int equipId) {
        this.equipId = equipId;
    }
    
    public TaskRewardEquip(final String[] s) {
        if (s.length > 1) {
            this.equipId = Integer.valueOf(s[1]);
        }
        else {
            this.equipId = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final int playerId = playerDto.playerId;
        final Equip equip = (Equip)taskDataGetter.getEquipCache().get((Object)this.equipId);
        if (equip != null) {
            final int maxNum = taskDataGetter.getPlayerAttributeDao().getMaxStoreNum(playerId);
            final int usedNum = taskDataGetter.getStoreHouseDao().getCountByPlayerId(playerId);
            if (usedNum < maxNum) {
                final StoreHouse sh = new StoreHouse();
                sh.setItemId(this.equipId);
                sh.setPlayerId(playerId);
                sh.setLv(equip.getDefaultLevel());
                sh.setOwner(0);
                sh.setType(1);
                sh.setGoodsType(equip.getType());
                sh.setAttribute(new StringBuilder().append(equip.getAttribute()).toString());
                sh.setQuality(equip.getQuality());
                sh.setGemId(0);
                sh.setNum(1);
                sh.setState(0);
                sh.setRefreshAttribute("");
                sh.setQuenchingTimes(0);
                sh.setMarkId(0);
                taskDataGetter.getStoreHouseDao().create(sh);
                TaskRewardEquip.logger.info(LogUtil.formatEquipLog(taskDataGetter.getPlayerDao().read(playerId), "+", "\u83b7\u5f97", true, equip, sh, LocalMessages.T_LOG_EQUIP_6));
            }
            else {
                final StoreHouseSell shs = new StoreHouseSell();
                shs.setAttribute(new StringBuilder().append(equip.getAttribute()).toString());
                shs.setGemId(0);
                shs.setType(1);
                shs.setGoodsType(equip.getType());
                shs.setItemId(this.equipId);
                shs.setLv(equip.getDefaultLevel());
                shs.setNum(1);
                shs.setPlayerId(playerId);
                shs.setQuality(equip.getQuality());
                shs.setSellTime(new Date());
                shs.setRefreshAttribute("");
                shs.setQuenchingTimes(0);
                taskDataGetter.getStoreHouseSellDao().create(shs);
                TaskRewardEquip.logger.info(LogUtil.formatEquipLog2(taskDataGetter.getPlayerDao().read(playerId), "+", "\u83b7\u5f97", true, equip, shs, LocalMessages.T_LOG_EQUIP_6));
            }
            TaskMessageHelper.sendEquipTaskMessage(playerId);
        }
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(31, new Reward(31, LocalMessages.T_COMM_10030, this.equipId));
        return map;
    }
}
