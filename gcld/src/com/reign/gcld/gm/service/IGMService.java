package com.reign.gcld.gm.service;

import com.reign.gcld.player.dto.*;
import com.reign.framework.netty.servlet.*;

public interface IGMService
{
    byte[] handleCopperCommand(final int p0, final String... p1);
    
    byte[] handleWoodCommand(final int p0, final String... p1);
    
    byte[] handleFoodCommand(final int p0, final String... p1);
    
    byte[] handleIronCommand(final int p0, final String... p1);
    
    byte[] handleTicketCommand(final int p0, final String... p1);
    
    byte[] handleExploitCommand(final int p0, final String... p1);
    
    byte[] handleArmyCommand(final PlayerDto p0, final String... p1);
    
    byte[] handleGoldCommand(final int p0, final String... p1);
    
    byte[] handleLevelCommand(final int p0, final String... p1);
    
    byte[] handleBattleCommand(final int p0, final String... p1);
    
    byte[] handleBattleJoinCommand(final int p0, final String... p1);
    
    byte[] handleBattleEndCommand(final int p0, final String... p1);
    
    byte[] handleBattleLeaveCommand(final int p0, final String... p1);
    
    byte[] handleSdataCommand(final int p0, final String... p1);
    
    byte[] handleCreateBuildingCommand(final int p0, final String... p1);
    
    byte[] handleConsumeLvCommand(final int p0, final String... p1);
    
    byte[] handleGetCommand(final int p0, final String... p1);
    
    byte[] handleKillCommand(final int p0, final String... p1);
    
    byte[] handleFunctionCommand(final int p0, final String... p1);
    
    byte[] handleGeneralCommand(final int p0, final String... p1);
    
    byte[] handleMoveCommand(final int p0, final String... p1);
    
    byte[] handleCopyCommand(final int p0, final String... p1);
    
    byte[] handleGeneralLvCommand(final int p0, final String... p1);
    
    byte[] handleTaskCommand(final PlayerDto p0, final String... p1);
    
    byte[] handlePayCommand(final PlayerDto p0, final Request p1, final String... p2);
    
    byte[] handleHelpCommand(final PlayerDto p0, final String... p1);
    
    byte[] handleCivilLvCommand(final PlayerDto p0, final String... p1);
    
    byte[] handleOpenMistCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleExpCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleStoreCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleResourceCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleSetTaskCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleCivilCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleTokenCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleSlaveCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleResetSlaveCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleStopWorkCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleStopAutoCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleTrickChatCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleWHCCityChatCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleLCCityChatCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleLGLPlaceChatCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleLGWPlaceChatCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleRankChatCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleWinNPCCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handlePassBonusCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleCNeutralPlaceCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleWinHCCommonPlaceCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleLCommonPlaceCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleLPlaceCHCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleReOpenBonusCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleCNPCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleOfficialOCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleExeTimesCommand(final int p0, final String[] p1);
    
    byte[] handleGTreasureCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleBaseCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleCheckFunctionId(final int p0, final String[] p1);
    
    byte[] handleSetFunctionId(final int p0, final String[] p1);
    
    byte[] handleOfficersCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleTechCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleAdditionsCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleTechEffectCommand(final PlayerDto p0, final String[] p1);
    
    byte[] handleAddTechCommand(final int p0, final String[] p1);
    
    byte[] handleAddBluePrintCommand(final int p0, final String[] p1);
    
    byte[] handleAddKillBanditCommand(final int p0, final String[] p1);
    
    byte[] handleCityIdCommand(final int p0, final String[] p1);
    
    byte[] handleTechAllCommand(final int p0, final String[] p1);
    
    byte[] handleUndefeatableCommand(final int p0, final String[] p1);
    
    byte[] handleIdCommand(final int p0, final String[] p1);
    
    byte[] handleZdCommand(final int p0, final String[] p1);
    
    byte[] hanleAddRankerNum(final int p0, final String[] p1);
    
    byte[] hanleCityEvent(final int p0, final String[] p1);
    
    byte[] hanleSetGem(final int p0, final String[] p1);
    
    byte[] hanleLimboPic(final int p0, final String[] p1);
    
    byte[] hanleSlave2(final int p0, final String[] p1);
    
    byte[] handleGetSuitPaper(final int p0, final String[] p1);
    
    byte[] handleSetCity(final int p0, final String[] p1);
    
    byte[] handleSetDragon(final int p0, final String[] p1);
    
    byte[] handleAddForceExp(final int p0, final String[] p1);
    
    byte[] handleManWangLing(final PlayerDto p0, final String[] p1);
    
    byte[] handleGetNiubiEquip(final PlayerDto p0, final String[] p1);
    
    byte[] hanleSetOfficerToken(final PlayerDto p0, final String[] p1);
    
    byte[] hanleDefaultPay(final PlayerDto p0, final String[] p1);
    
    byte[] handleMooncake(final PlayerDto p0, final String[] p1);
    
    byte[] handleBmw(final PlayerDto p0, final String[] p1);
    
    byte[] handleXo(final PlayerDto p0, final String[] p1);
    
    byte[] handlePicasso(final PlayerDto p0, final String[] p1);
    
    byte[] handleMs(final PlayerDto p0, final String[] p1);
    
    byte[] handleEnterWorldDrama(final PlayerDto p0, final String[] p1);
    
    byte[] handleIronEffect(final PlayerDto p0, final String[] p1);
    
    byte[] handleWeaponLv(final PlayerDto p0, final String[] p1);
    
    byte[] handleCallMeHXL(final PlayerDto p0, final String[] p1);
    
    byte[] handleXiLian(final PlayerDto p0, final String[] p1);
    
    byte[] handleChangeForceLv(final PlayerDto p0, final String[] p1);
    
    byte[] handleChangeNationTask(final PlayerDto p0, final String[] p1);
    
    byte[] handleChangeIndivTask(final PlayerDto p0, final String[] p1);
}
