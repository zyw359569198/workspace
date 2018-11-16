package com.reign.gcld.battle.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.asynchronousDB.manager.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.player.domain.*;

@Component("BattleDropService")
public class BattleDropService implements IBattleDropService
{
    @Autowired
    private IDataGetter dataGetter;
    private static final Logger errorSceneLog;
    
    static {
        errorSceneLog = ErrorSceneLog.getInstance();
    }
    
    @Override
    public void saveBattleDrop(final int playerId, final BattleDrop drop, final String reason) {
        if (drop == null || drop.type == 0) {
            return;
        }
        try {
            switch (drop.type) {
                case 1:
                case 1001: {
                    this.dropCopperIgnoreMax(playerId, drop, reason);
                    break;
                }
                case 2:
                case 1002: {
                    this.dropLumberIgnoreMax(playerId, drop, reason);
                    break;
                }
                case 3:
                case 1003: {
                    this.dropFoodIgnoreMax(playerId, drop, reason);
                    break;
                }
                case 4:
                case 1004: {
                    this.dropIronIgnoreMax(playerId, drop, reason);
                    break;
                }
                case 5:
                case 1005: {
                    this.dropChiefExpIgnoreMax(playerId, drop, reason);
                    break;
                }
                case 7:
                case 1007: {
                    this.dropGem(playerId, drop, reason);
                    break;
                }
                case 23:
                case 1023: {
                    this.dropTouZiDoubleTicket(playerId, drop, reason);
                    break;
                }
                case 42: {
                    this.dropMuBingLing(playerId, drop, reason);
                    break;
                }
            }
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("BattleDropService.saveBattleDrop#" + playerId + "reason", e);
        }
    }
    
    @Override
    public void saveBattleDrop(final int playerId, final BattleDropAnd drops) {
        AsynchronousDBOperationManager.getInstance().addBattleDropMapRetry(playerId, drops.getDropAndMap(), "\u6218\u6597\u989d\u5916\u6389\u843d");
    }
    
    @Override
    public void dropCopperIgnoreMax(final int playerId, final BattleDrop copper, final String reason) {
        try {
            if (copper.num > 0) {
                this.dataGetter.getPlayerResourceDao().addCopperIgnoreMax(playerId, copper.num, String.valueOf(reason) + "\u94f6\u5e01", true);
            }
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("\u6218\u6597\u589e\u52a0\u94f6\u5e01\u629b\u51fa\u5f02\u5e38", e);
        }
    }
    
    @Override
    public void dropLumberIgnoreMax(final int playerId, final BattleDrop lumber, final String reason) {
        try {
            if (lumber.num > 0) {
                this.dataGetter.getPlayerResourceDao().addWoodIgnoreMax(playerId, lumber.num, String.valueOf(reason) + "\u6728\u6750", true);
            }
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("\u6218\u6597\u589e\u52a0\u6728\u6750\u629b\u51fa\u5f02\u5e38", e);
        }
    }
    
    @Override
    public void dropFoodIgnoreMax(final int playerId, final BattleDrop food, final String reason) {
        try {
            if (food.num > 0) {
                this.dataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerId, food.num, String.valueOf(reason) + "\u7cae\u98df");
            }
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("\u6218\u6597\u589e\u52a0\u7cae\u98df\u629b\u51fa\u5f02\u5e38", e);
        }
    }
    
    @Override
    public void dropIronIgnoreMax(final int playerId, final BattleDrop iron, final String reason) {
        try {
            if (iron.num > 0) {
                this.dataGetter.getPlayerResourceDao().addIronIgnoreMax(playerId, iron.num, String.valueOf(reason) + "\u9554\u94c1", true);
            }
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("\u6218\u6597\u589e\u52a0\u9554\u94c1\u629b\u51fa\u5f02\u5e38", e);
        }
    }
    
    @Override
    public void dropChiefExpIgnoreMax(final int playerId, final BattleDrop chiefExp, final String reason) {
        try {
            if (chiefExp.num > 0) {
                this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, chiefExp.num, String.valueOf(reason) + "\u73a9\u5bb6\u7ecf\u9a8c\u503c");
            }
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("\u6218\u6597\u589e\u52a0\u73a9\u5bb6\u7ecf\u9a8c\u503c\u629b\u51fa\u5f02\u5e38", e);
        }
    }
    
    @Override
    public void dropGem(final int playerId, final BattleDrop rankBatGem, final String reason) {
        try {
            this.dataGetter.getStoreHouseService().gainGem(this.dataGetter.getPlayerDao().read(playerId), rankBatGem.num, rankBatGem.id, LocalMessages.T_LOG_GEM_14, null);
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("\u6218\u6597\u6389\u843d\u5b9d\u77f3\u629b\u51fa\u5f02\u5e38", e);
        }
    }
    
    @Override
    public void dropTouZiDoubleTicket(final int playerId, final BattleDrop drop, final String reason) {
        try {
            this.dataGetter.getStoreHouseService().gainItems(playerId, drop.num, 401, LocalMessages.T_LOG_ITEM_3);
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            this.dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(playerId, player.getForceId()), drop.num, "fanbeiquan");
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("\u6218\u6597\u589e\u52a0\u6295\u8d44\u4efb\u52a1\u53cc\u500d\u5238\u629b\u51fa\u5f02\u5e38", e);
        }
    }
    
    private void dropMuBingLing(final int playerId, final BattleDrop drop, final String reason) {
        try {
            this.dataGetter.getPlayerAttributeDao().addRecruitToken(playerId, drop.num, String.valueOf(reason) + "\u52df\u5175\u4ee4");
        }
        catch (Exception e) {
            BattleDropService.errorSceneLog.error("\u589e\u52a0\u52df\u5175\u4ee4\u629b\u51fa\u5f02\u5e38", e);
        }
    }
}
