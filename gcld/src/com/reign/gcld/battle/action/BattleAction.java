package com.reign.gcld.battle.action;

import com.reign.gcld.common.web.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.annotation.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.battle.scene.*;
import com.reign.util.*;

public class BattleAction extends BaseAction
{
    private static final long serialVersionUID = 1609790300016265755L;
    @Autowired
    private IBattleService battleService;
    @Autowired
    private IDataGetter dataGetter;
    
    @Command("battle@battlePermit")
    public ByteResult attPermit(@RequestParam("targetId") final int targetId, @RequestParam("type") final int type, @RequestParam("reserve") final int reserve, @RequestParam("generalId") final int generalId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        GroupManager.getInstance().getGroup(ChatType.BATTLE.toString()).join(request.getSession());
        return this.getResult(this.battleService.attPermit(playerDto, type, targetId, reserve, generalId), request);
    }
    
    @Command("battle@battlePrepare")
    public ByteResult attPrepare(@RequestParam("targetId") final int targetId, @RequestParam("type") final int type, @RequestParam("generalId") final int generalId, @RequestParam("join") final int join, @RequestParam("terrainType") final int terrainType, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.battleService.battlePrepare(playerDto, type, targetId, generalId, join, terrainType), request);
    }
    
    @Command("battle@battleStart")
    public ByteResult attStart(@RequestParam("targetId") final int targetId, @RequestParam("gIds") final String gIds, @RequestParam("type") final int type, @RequestParam("side") final int side, @RequestParam("reinforce") final int reinforce, @RequestParam("join") final int join, @RequestParam("terrainType") final int terrainType, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        if (!this.attStartCheck(playerDto, targetId, gIds, type, side, reinforce, join, terrainType)) {
            return null;
        }
        if (this.dataGetter.getAutoBattleService().InAutoBattleMode(playerDto.playerId)) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.AUTO_BATTLE_CANNOT_OPERATE), request);
        }
        return this.getResult(this.battleService.battleStart(playerDto.playerId, type, targetId, gIds, terrainType), request);
    }
    
    private boolean attStartCheck(final PlayerDto playerDto, final int targetId, final String gIds, final int type, final int side, final int reinforce, final int join, final int terrainType) {
        switch (type) {
            case 1: {
                return targetId >= 0 && side == 1;
            }
            case 2: {
                return targetId >= 0 && side == 1;
            }
            case 3: {
                ErrorSceneLog.getInstance().appendErrorMsg("BATTLE_CITY battle@battleStart plug attack!").appendMethodName("attStart").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("battleType", type).append("side", side).append("gIds", gIds).flush();
                return false;
            }
            case 4: {
                return targetId >= 0 && side == 1 && this.dataGetter.getHallsCache().getHalls(targetId, 1) != null;
            }
            case 5: {
                ErrorSceneLog.getInstance().appendErrorMsg("BATTLE_ARMY_AUTO battle@battleStart plug attack!").appendMethodName("attStart").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("battleType", type).append("side", side).append("gIds", gIds).flush();
                return false;
            }
            case 6: {
                return targetId >= 0 && side == 1;
            }
            case 7: {
                return targetId >= 0 && side == 1;
            }
            case 8: {
                return side == 1;
            }
            case 9: {
                return true;
            }
            case 10: {
                return targetId >= 0 && side == 1;
            }
            case 11: {
                return side == 1;
            }
            case 12: {
                return targetId >= 0 && side == 1;
            }
            case 333: {
                return targetId >= 0 && side == 1;
            }
            case 13: {
                ErrorSceneLog.getInstance().appendErrorMsg("BATTLE_CITY_ONE2ONE battle@battleStart plug attack!").appendMethodName("attStart").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("battleType", type).append("side", side).append("gIds", gIds).flush();
                return false;
            }
            case 16: {
                return targetId > 0 && side == 1;
            }
            case 17: {
                return targetId > 0 && side == 1;
            }
            case 20: {
                return targetId > 0 && side == 1;
            }
            default: {
                return false;
            }
        }
    }
    
    @Command("battle@useStrategy")
    public ByteResult useStrategy(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, @RequestParam("strategyId") final int strategyId, @RequestParam("position") final int position, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.useStrategy(playerDto, battleId, strategyId, position), request);
    }
    
    @Command("battle@leaveBattle")
    public ByteResult attLeave(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("type") final int type, @RequestParam("battleId") final String battleId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        this.battleService.leaveBattle(playerDto.playerId, type, battleId);
        GroupManager.getInstance().getGroup(ChatType.BATTLE.toString()).leave(request.getSession().getId());
        return this.getResult(JsonBuilder.getJson(State.SUCCESS, BattleJsonBuilder.getJson("leave", true)), request);
    }
    
    @Command("battle@getQuitGeneral")
    public ByteResult getQuitGeneral(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, @RequestParam("gIds") final String gIds, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getQuitGeneral(playerDto, battleId, gIds), request);
    }
    
    @Command("battle@quitBattle")
    public ByteResult attQuit(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, @RequestParam("gIds") final String gIds, @RequestParam("cityId") final int cityId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.quitBattle(playerDto, gIds, battleId, cityId), request);
    }
    
    @Command("battle@helpInfo")
    public ByteResult helpInfo(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.helpInfo(playerDto, battleId), request);
    }
    
    @Command("battle@getBattleResult")
    public ByteResult getBattleResult(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("vId") final int vId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getBattleResult(playerDto, vId), request);
    }
    
    @Command("battle@getAssembleGeneral")
    public ByteResult getAssembleGeneral(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("cityId") final int cityId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getAssembleGeneral(playerDto, cityId), request);
    }
    
    @Command("battle@AssembleBattleAll")
    public ByteResult AssembleBattleAll(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("cityId") final int cityId, @RequestParam("gIds") final String gIds, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.AssembleBattleAll(playerDto, gIds, cityId), request);
    }
    
    @Command("battle@AssembleBattle")
    public ByteResult AssembleBattle(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, @RequestParam("gId") final int gId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.AssembleBattle(playerDto, gId, battleId), request);
    }
    
    @Command("battle@getCopyArmyCost")
    public ByteResult getCopyArmyCost(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getCopyArmyCost(playerDto), request);
    }
    
    @Command("battle@doCopyArmy")
    public ByteResult doCopyArmy(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.doCopyArmy(playerDto, battleId), request);
    }
    
    @Command("battle@youdi")
    public ByteResult youdi(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.youdi(battleId, playerDto), request);
    }
    
    @Command("battle@chuji")
    public ByteResult chuji(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.chuji(battleId, playerDto), request);
    }
    
    @Command("battle@setChangeBat")
    public ByteResult setChangeBat(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.setChangeBat(playerDto), request);
    }
    
    @Command("battle@getCoverChujuCd")
    public ByteResult getCoverChujuCd(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getCoverChujuCd(playerDto), request);
    }
    
    @Command("battle@getCoverYoudiCd")
    public ByteResult getCoverYoudiCd(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getCoverYoudiCd(playerDto), request);
    }
    
    @Command("battle@doCoverChujuCd")
    public ByteResult doCoverChujuCd(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.doCoverChujuCd(playerDto), request);
    }
    
    @Command("battle@doCoverYoudiCd")
    public ByteResult doCoverYoudiCd(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.doCoverYoudiCd(playerDto), request);
    }
    
    @Command("battle@getCurrentTokenInfo")
    public ByteResult getCurrentTokenInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request, @RequestParam("index") final int index) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getCurrentTokenInfo(playerDto, index), request);
    }
    
    @Command("battle@getTuJinGenerals")
    public ByteResult getTuJinGenerals(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getTuJinGenerals(playerDto, battleId), request);
    }
    
    @Command("battle@tuJin")
    public ByteResult tuJin(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, @RequestParam("gIds") final String gIds, @RequestParam("cityId") final int cityId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.TuJin(playerDto, battleId, gIds, cityId), request);
    }
    
    @Command("battle@useOfficerTokenInBattle")
    public ByteResult useOfficerToken(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, @RequestParam("cityId") final int cityId, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.useOfficerTokenInBattle(playerDto, cityId, battleId), request);
    }
    
    @Command("battle@replyOfficerToken")
    public ByteResult replyOfficerToken(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, @RequestParam("cityId") final int cityId, @RequestParam("gIds") final String gids, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INFO_WORLD_BATENDED), request);
        }
        final Tuple<Boolean, String> reward = this.battleService.getReplyReward(cityId, playerDto.forceId, battleId, playerDto.playerId, gids);
        if (!(boolean)reward.left) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, reward.right), request);
        }
        return this.getResult(this.battleService.replyOfficerToken(playerDto, battle, cityId, reward.right), request);
    }
    
    @Command("battle@useAutoStrategy")
    public ByteResult useAutoStrategy(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.useAutoStrategy(playerDto), request);
    }
    
    @Command("battle@cancelAutoStrategy")
    public ByteResult cancelAutoStrategy(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.cancelAutoStrategy(playerDto), request);
    }
    
    @Command("battle@watchBattle")
    public ByteResult watchBattle(@RequestParam("battleId") final String battleId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        GroupManager.getInstance().getGroup(ChatType.BATTLE.toString()).join(request.getSession());
        return this.getResult(this.battleService.watchBattle(playerDto, battleId), request);
    }
    
    @Command("battle@joinBattle")
    public ByteResult joinGuanZhi(@RequestParam("battleId") final String battleId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.battleService.joinBattle(playerDto, battleId), request);
    }
    
    @Command("battle@getReplyMWLInfo")
    public ByteResult getReplyMWLInfo(@SessionParam("PLAYER") final PlayerDto playerDto, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.getReplyMWLInfo(playerDto), request);
    }
    
    @Command("battle@replyManWangLing")
    public ByteResult replyManWangLing(@RequestParam("gIds") final String gIds, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.replyManWangLing(playerDto, gIds), request);
    }
    
    @Command("battle@useKillToken")
    public ByteResult useKillToken(@RequestParam("cityId") final int cityId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        return this.getResult(this.battleService.useKillToken(playerDto, cityId), request);
    }
    
    @Command("battle@useGoldOrder")
    public ByteResult useGoldOrder(@RequestParam("battleId") final String battleId, final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.battleService.useGoldOrder(playerDto, battleId), request);
    }
    
    @Command("battle@getGoldOrderInfo")
    public ByteResult getGoldOrderInfo(final Request request) {
        final PlayerDto playerDto = (PlayerDto)this.getFromSession("PLAYER", request);
        if (playerDto == null) {
            return null;
        }
        return this.getResult(this.battleService.getGoldOrderInfo(playerDto), request);
    }
    
    @Command("battle@replyGoldOrder")
    public ByteResult replyGoldOrder(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("battleId") final String battleId, @RequestParam("cityId") final int cityId, @RequestParam("gIds") final String gids, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INFO_WORLD_BATENDED), request);
        }
        return this.getResult(this.battleService.replyGoldOrder(playerDto, battle, cityId, gids), request);
    }
    
    @Command("battle@getCampList")
    public ByteResult getCampList(@SessionParam("PLAYER") final PlayerDto playerDto, @RequestParam("batId") final String battleId, @RequestParam("page") final int page, @RequestParam("side") final int side, final Request request) {
        if (playerDto == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10004), request);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByBatId(battleId);
        if (battle == null) {
            return this.getResult(JsonBuilder.getJson(State.FAIL, LocalMessages.BATTLE_INFO_WORLD_BATENDED), request);
        }
        return this.getResult(this.battleService.getCampList(battleId, page, side), request);
    }
}
