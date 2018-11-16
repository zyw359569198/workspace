package com.reign.gcld.gm.controller;

import org.springframework.stereotype.*;
import com.reign.gcld.gm.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.gm.common.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;

@Component("gMController")
public class GMController implements IGMController
{
    private static final Logger log;
    @Autowired
    private IGMService gmService;
    
    static {
        log = CommonLog.getLog(GMController.class);
    }
    
    @Override
    public byte[] handle(final PlayerDto playerDto, final Command cmd, final Request request, final String... params) {
        try {
            switch (cmd) {
                case SDATA: {
                    return this.gmService.handleSdataCommand(playerDto.playerId, params);
                }
                case COPPER: {
                    return this.gmService.handleCopperCommand(playerDto.playerId, params);
                }
                case GOLD: {
                    return this.gmService.handleGoldCommand(playerDto.playerId, params);
                }
                case WOOD: {
                    return this.gmService.handleWoodCommand(playerDto.playerId, params);
                }
                case FOOD: {
                    return this.gmService.handleFoodCommand(playerDto.playerId, params);
                }
                case IRON: {
                    return this.gmService.handleIronCommand(playerDto.playerId, params);
                }
                case TICKET: {
                    return this.gmService.handleTicketCommand(playerDto.playerId, params);
                }
                case EXPLOIT: {
                    return this.gmService.handleExploitCommand(playerDto.playerId, params);
                }
                case LEVEL: {
                    return this.gmService.handleLevelCommand(playerDto.playerId, params);
                }
                case BATTLE: {
                    return this.gmService.handleBattleCommand(playerDto.playerId, params);
                }
                case BATTLE_END: {
                    return this.gmService.handleBattleEndCommand(playerDto.playerId, params);
                }
                case BATTLE_JOIN: {
                    return this.gmService.handleBattleJoinCommand(playerDto.playerId, params);
                }
                case BATTLE_LEAVE: {
                    return this.gmService.handleBattleLeaveCommand(playerDto.playerId, params);
                }
                case CREATE_BUILDING: {
                    return this.gmService.handleCreateBuildingCommand(playerDto.playerId, params);
                }
                case CONSUME_LV: {
                    return this.gmService.handleConsumeLvCommand(playerDto.playerId, params);
                }
                case GET: {
                    return this.gmService.handleGetCommand(playerDto.playerId, params);
                }
                case ARMY: {
                    return this.gmService.handleArmyCommand(playerDto, params);
                }
                case KILL: {
                    return this.gmService.handleKillCommand(playerDto.playerId, params);
                }
                case FUNCTION: {
                    return this.gmService.handleFunctionCommand(playerDto.playerId, params);
                }
                case GENERAL: {
                    return this.gmService.handleGeneralCommand(playerDto.playerId, params);
                }
                case MOVE: {
                    return this.gmService.handleMoveCommand(playerDto.playerId, params);
                }
                case COPY: {
                    return this.gmService.handleCopyCommand(playerDto.playerId, params);
                }
                case GENERALLV: {
                    return this.gmService.handleGeneralLvCommand(playerDto.playerId, params);
                }
                case TASK: {
                    return this.gmService.handleTaskCommand(playerDto, params);
                }
                case PAY: {
                    return this.gmService.handlePayCommand(playerDto, request, params);
                }
                case HELP: {
                    return this.gmService.handleHelpCommand(playerDto, params);
                }
                case CIVILLV: {
                    return this.gmService.handleCivilLvCommand(playerDto, params);
                }
                case OPENMIST: {
                    return this.gmService.handleOpenMistCommand(playerDto, params);
                }
                case EXP: {
                    return this.gmService.handleExpCommand(playerDto, params);
                }
                case STORE: {
                    return this.gmService.handleStoreCommand(playerDto, params);
                }
                case RESOURCE: {
                    return this.gmService.handleResourceCommand(playerDto, params);
                }
                case SETTASK: {
                    return this.gmService.handleSetTaskCommand(playerDto, params);
                }
                case CIVIL: {
                    return this.gmService.handleCivilCommand(playerDto, params);
                }
                case TOKEN: {
                    return this.gmService.handleTokenCommand(playerDto, params);
                }
                case SLAVE: {
                    return this.gmService.handleSlaveCommand(playerDto, params);
                }
                case RESETSLAVE: {
                    return this.gmService.handleResetSlaveCommand(playerDto, params);
                }
                case STOPWORK: {
                    return this.gmService.handleStopWorkCommand(playerDto, params);
                }
                case STOPAUTO: {
                    return this.gmService.handleStopAutoCommand(playerDto, params);
                }
                case TRICKCHAT: {
                    return this.gmService.handleTrickChatCommand(playerDto, params);
                }
                case WHCCITYCHAT: {
                    return this.gmService.handleWHCCityChatCommand(playerDto, params);
                }
                case LCCITYCHAT: {
                    return this.gmService.handleLCCityChatCommand(playerDto, params);
                }
                case GLPLACECHAT: {
                    return this.gmService.handleLGLPlaceChatCommand(playerDto, params);
                }
                case GWPLACECHAT: {
                    return this.gmService.handleLGWPlaceChatCommand(playerDto, params);
                }
                case RANKCHAT: {
                    return this.gmService.handleRankChatCommand(playerDto, params);
                }
                case WINNPC: {
                    return this.gmService.handleWinNPCCommand(playerDto, params);
                }
                case PASSBONUS: {
                    return this.gmService.handlePassBonusCommand(playerDto, params);
                }
                case CNEUTRALPLACE: {
                    return this.gmService.handleCNeutralPlaceCommand(playerDto, params);
                }
                case WINHCCOMONPLACE: {
                    return this.gmService.handleWinHCCommonPlaceCommand(playerDto, params);
                }
                case LCOMMONPLACE: {
                    return this.gmService.handleLCommonPlaceCommand(playerDto, params);
                }
                case PLACECH: {
                    return this.gmService.handleLPlaceCHCommand(playerDto, params);
                }
                case REOPENBONUS: {
                    return this.gmService.handleReOpenBonusCommand(playerDto, params);
                }
                case CNP: {
                    return this.gmService.handleCNPCommand(playerDto, params);
                }
                case OFFICIALO: {
                    return this.gmService.handleOfficialOCommand(playerDto, params);
                }
                case EXETIMES: {
                    return this.gmService.handleExeTimesCommand(playerDto.playerId, params);
                }
                case GTREASURE: {
                    return this.gmService.handleGTreasureCommand(playerDto, params);
                }
                case BASE: {
                    return this.gmService.handleBaseCommand(playerDto, params);
                }
                case CHECKFUNCTIONID: {
                    return this.gmService.handleCheckFunctionId(playerDto.playerId, params);
                }
                case SETFUNCTIONID: {
                    return this.gmService.handleSetFunctionId(playerDto.playerId, params);
                }
                case OFFICERS: {
                    return this.gmService.handleOfficersCommand(playerDto, params);
                }
                case TECH: {
                    return this.gmService.handleTechCommand(playerDto, params);
                }
                case ADDITIONS: {
                    return this.gmService.handleAdditionsCommand(playerDto, params);
                }
                case TECHEFFECT: {
                    return this.gmService.handleTechEffectCommand(playerDto, params);
                }
                case ADDTECH: {
                    return this.gmService.handleAddTechCommand(playerDto.playerId, params);
                }
                case ADDBLUEPRINT: {
                    return this.gmService.handleAddBluePrintCommand(playerDto.playerId, params);
                }
                case KILLBANDIT: {
                    return this.gmService.handleAddKillBanditCommand(playerDto.playerId, params);
                }
                case CITYID: {
                    return this.gmService.handleCityIdCommand(playerDto.playerId, params);
                }
                case TECHALL: {
                    return this.gmService.handleTechAllCommand(playerDto.playerId, params);
                }
                case UNDEFEATABLE: {
                    return this.gmService.handleUndefeatableCommand(playerDto.playerId, params);
                }
                case ID: {
                    return this.gmService.handleIdCommand(playerDto.playerId, params);
                }
                case ZD: {
                    return this.gmService.handleZdCommand(playerDto.playerId, params);
                }
                case ADDRANKERNUMERS: {
                    return this.gmService.hanleAddRankerNum(playerDto.playerId, params);
                }
                case SHOCITYEVENT: {
                    return this.gmService.hanleCityEvent(playerDto.playerId, params);
                }
                case SETGEM: {
                    return this.gmService.hanleSetGem(playerDto.playerId, params);
                }
                case LIMBOPIC: {
                    return this.gmService.hanleLimboPic(playerDto.playerId, params);
                }
                case SLAVE2: {
                    return this.gmService.hanleSlave2(playerDto.playerId, params);
                }
                case GET_SUIT: {
                    return this.gmService.handleGetSuitPaper(playerDto.playerId, params);
                }
                case SET_CITY: {
                    return this.gmService.handleSetCity(playerDto.playerId, params);
                }
                case SET_DRAGON: {
                    return this.gmService.handleSetDragon(playerDto.playerId, params);
                }
                case ADD_FORCE_EXP: {
                    return this.gmService.handleAddForceExp(playerDto.playerId, params);
                }
                case FIRE_MANWANGLING: {
                    return this.gmService.handleManWangLing(playerDto, params);
                }
                case GET_FOURSTAR_EQUIP: {
                    return this.gmService.handleGetNiubiEquip(playerDto, params);
                }
                case ADD_OFFICER_TOKEN: {
                    return this.gmService.hanleSetOfficerToken(playerDto, params);
                }
                case DEFAULTPAY: {
                    return this.gmService.hanleDefaultPay(playerDto, params);
                }
                case ADD_MOONCAKE: {
                    return this.gmService.handleMooncake(playerDto, params);
                }
                case ADD_BMW: {
                    return this.gmService.handleBmw(playerDto, params);
                }
                case ADD_XO: {
                    return this.gmService.handleXo(playerDto, params);
                }
                case ADD_PICASSO: {
                    return this.gmService.handlePicasso(playerDto, params);
                }
                case MS: {
                    return this.gmService.handleMs(playerDto, params);
                }
                case ENTER_WORLD_DRAMA: {
                    return this.gmService.handleEnterWorldDrama(playerDto, params);
                }
                case IRON_EFFECT: {
                    return this.gmService.handleIronEffect(playerDto, params);
                }
                case WEAPON_LV: {
                    return this.gmService.handleWeaponLv(playerDto, params);
                }
                case CALL_ME_HUANGXILING: {
                    return this.gmService.handleCallMeHXL(playerDto, params);
                }
                case XILIAN: {
                    return this.gmService.handleXiLian(playerDto, params);
                }
                case CHANGE_FORCELV: {
                    return this.gmService.handleChangeForceLv(playerDto, params);
                }
                case CHANGE_NATION_TASK: {
                    return this.gmService.handleChangeNationTask(playerDto, params);
                }
                case CHANGE_INDIV_TASK: {
                    return this.gmService.handleChangeIndivTask(playerDto, params);
                }
            }
        }
        catch (Exception e) {
            GMController.log.error("handle gm command", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10003);
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_GM_10001);
    }
}
