package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public class TaskRewardHuangYuFu implements ITaskReward
{
    private static final DayReportLogger logger;
    private int num;
    private final int ITEM_ID = 5041;
    private final String REFRESH_ATTRIBUTE = "2:1";
    
    static {
        logger = new DayReportLogger();
    }
    
    public TaskRewardHuangYuFu(final int num) {
        this.num = num;
    }
    
    public TaskRewardHuangYuFu(final String[] s) {
        if (s.length > 1) {
            this.num = Integer.parseInt(s[1]);
        }
        else {
            this.num = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final Equip item = (Equip)taskDataGetter.getEquipCache().get((Object)5041);
        final int playerId = playerDto.playerId;
        final Player player = taskDataGetter.getPlayerDao().read(playerId);
        for (int i = 0; i < this.num; ++i) {
            final StoreHouse sh = new StoreHouse();
            sh.setItemId(5041);
            sh.setPlayerId(playerId);
            sh.setLv(item.getDefaultLevel());
            sh.setOwner(0);
            sh.setType(1);
            sh.setGoodsType(item.getType());
            sh.setAttribute(new StringBuilder().append(item.getAttribute()).toString());
            sh.setQuality(item.getQuality());
            sh.setGemId(0);
            sh.setNum(1);
            sh.setState(0);
            sh.setRefreshAttribute("2:1");
            sh.setQuenchingTimes(0);
            sh.setMarkId(0);
            taskDataGetter.getStoreHouseDao().create(sh);
            TaskRewardHuangYuFu.logger.info(LogUtil.formatEquipLog(player, "+", "\u83b7\u5f97", true, item, sh, prefixAttribute));
        }
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(46, new Reward(46, "huangyufu", this.num));
        return map;
    }
}
