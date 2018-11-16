package com.reign.gcld.task.reward;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.event.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.phantom.common.*;
import com.reign.gcld.activity.service.*;
import com.reign.gcld.courtesy.domain.*;
import com.reign.gcld.courtesy.common.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

public class TaskRewardNewFunction implements ITaskReward
{
    private int funtionId;
    
    public TaskRewardNewFunction(final int id) {
        this.funtionId = id;
    }
    
    public TaskRewardNewFunction(final String[] s) {
        if (s.length > 1) {
            this.funtionId = Integer.parseInt(s[1]);
        }
        else {
            this.funtionId = 0;
        }
    }
    
    @Override
    public Map<Integer, Reward> rewardPlayer(final PlayerDto playerDto, final IDataGetter taskDataGetter, final String prefixAttribute, final Object obj) {
        final PlayerAttribute pa = taskDataGetter.getPlayerAttributeDao().read(playerDto.playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        cs[this.funtionId] = '1';
        playerDto.cs = cs;
        taskDataGetter.getPlayerAttributeDao().updateFunction(playerDto.playerId, new String(cs));
        taskDataGetter.getPlayerService().afterOpenFunction(this.funtionId, playerDto.playerId);
        if (this.funtionId == 5) {
            taskDataGetter.getBuildingService().openLumberArea(playerDto.playerId);
        }
        else if (this.funtionId == 27) {
            taskDataGetter.getMarketService().openMarketFunction(playerDto);
        }
        else if (this.funtionId != 63) {
            if (this.funtionId == 31) {
                taskDataGetter.getFeatService().openFeatRecord(playerDto.playerId);
            }
            else if (this.funtionId == 32) {
                taskDataGetter.getFeatService().openFeatBuilding(playerDto.playerId);
            }
            else if (this.funtionId == 33) {
                taskDataGetter.getDinnerService().openDinnerFunction(playerDto.playerId);
            }
            else if (this.funtionId == 39) {
                taskDataGetter.getGiftService().openOnlineGiftFunctin(playerDto.playerId);
            }
            else if (this.funtionId == 10) {
                taskDataGetter.getWorldService().createRecord(playerDto.playerId);
                taskDataGetter.getWorldService().createWholeKill(playerDto.playerId);
                taskDataGetter.getActivityService().openPlayerScoreRank(playerDto.playerId);
                taskDataGetter.getActivityService().openDragonRecord(playerDto.playerId);
                taskDataGetter.getNationService().createPlayerTryRank(playerDto.playerId);
                taskDataGetter.getProtectService().openPRank(playerDto.playerId);
                if (TeamManager.leagueOpen(playerDto.forceId)) {
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("openLegion", 1);
                    doc.createElement("batTeamNum", TeamManager.getInstance().getTeamNumByForceId(playerDto.forceId));
                    doc.endObject();
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                }
                if (EventUtil.isEventTime(17)) {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveWishActivity", 1));
                }
                if (EventUtil.isEventTime(19)) {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveBaiNianActivity", 1));
                }
            }
            else if (this.funtionId == 14) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("refreshMainCity", true);
                doc.endObject();
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
            else if (this.funtionId == 16) {
                taskDataGetter.getIncenseService().openIncense(playerDto.playerId);
            }
            else if (this.funtionId == 46) {
                final PlayerBatRank playerBatRank = new PlayerBatRank();
                playerBatRank.setPlayerId(playerDto.playerId);
                playerBatRank.setRank(0);
                playerBatRank.setReward(null);
                playerBatRank.setLastRankTime(new Date());
                playerBatRank.setRankScore(0);
                playerBatRank.setBuyTimesToday(0);
                playerBatRank.setRankBatNum(10);
                final int done = taskDataGetter.getPlayerBatRankDao().create(playerBatRank);
            }
            else if (this.funtionId == 17) {
                taskDataGetter.getStoreService().refreshItem(playerDto.playerId, 2, taskDataGetter.getPlayerStoreDao().read(playerDto.playerId), (Chargeitem)taskDataGetter.getChargeitemCache().get((Object)9));
            }
            else if (this.funtionId != 52) {
                if (this.funtionId == 19) {
                    taskDataGetter.getTechService().openTechFunction(playerDto.playerId);
                }
                else if (this.funtionId == 38) {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveDayGift", true));
                }
                else if (this.funtionId == 56) {
                    taskDataGetter.getBuildingService().openFreeConstruction(playerDto.playerId);
                }
                else if (this.funtionId == 29) {
                    taskDataGetter.getWeaponService().openWeaponFunction(playerDto.playerId);
                }
                else if (this.funtionId != 62) {
                    if (this.funtionId == 64) {
                        PhantomManager.getInstance().refreshOnePlayer(playerDto.playerId);
                    }
                    else if (this.funtionId == 51) {
                        if (ActivityService.inQuenching) {
                            final byte[] send = JsonBuilder.getSimpleJson("haveQuenchingActivity", 1);
                            Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                        }
                    }
                    else if (this.funtionId == 22) {
                        taskDataGetter.getPoliticsService().openPolitcsEvent(playerDto.playerId);
                    }
                    else if (this.funtionId == 67) {
                        final PlayerLiYi playerLiYi = new PlayerLiYi();
                        playerLiYi.setPlayerId(playerDto.playerId);
                        playerLiYi.setLiYiDu(0);
                        playerLiYi.setRewardInfo(null);
                        taskDataGetter.getPlayerLiYiDao().create(playerLiYi);
                        CourtesyManager.getInstance().addPlayerToContainer(playerDto.playerId, true);
                        taskDataGetter.getCourtesyService().addXiaoQianEvent(playerDto.playerId, 1);
                        final JsonDocument doc2 = new JsonDocument();
                        doc2.startObject();
                        doc2.createElement("liShangWangLai", true);
                        doc2.endObject();
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
                    }
                    else if (this.funtionId == 68) {
                        final JsonDocument doc = new JsonDocument();
                        doc.startObject();
                        doc.createElement("diamondShopOpen", true);
                        doc.endObject();
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                    }
                }
            }
        }
        return this.getReward(playerDto, taskDataGetter, obj);
    }
    
    @Override
    public Map<Integer, Reward> getReward(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Object obj) {
        final Map<Integer, Reward> map = new HashMap<Integer, Reward>();
        map.put(8, new Reward(8, "fun", this.funtionId));
        return map;
    }
}
