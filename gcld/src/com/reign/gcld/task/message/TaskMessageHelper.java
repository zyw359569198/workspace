package com.reign.gcld.task.message;

import com.reign.gcld.common.message.*;
import com.reign.gcld.store.service.*;

public class TaskMessageHelper
{
    public static void sendChoseSideTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageChoseSide(playerId));
    }
    
    public static void sendBuildingTaskMessage(final int playerId, final int buildingId, final int lv) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageBuilding(playerId, buildingId, lv));
    }
    
    public static void sendResourceTaskMessage(final int playerId, final int outputType, final int num) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageResource(playerId, outputType, num));
    }
    
    public static void sendVisitAreaTaskMessage(final int playerId, final int areaId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageVisitArea(playerId, areaId));
    }
    
    public static void sendTavernRefreshTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTavernRefresh(playerId));
    }
    
    public static void sendOfficerTaskMessage(final int playerId, final int num) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageOfficer(playerId, num));
    }
    
    public static void sendGeneralTaskMessage(final int playerId, final int num) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageGeneral(playerId, num));
    }
    
    public static void sendApplyLegionTaskMessage(final int playerId, final int num) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageApplyLegion(playerId, num));
    }
    
    public static void sendBattleWinTaskMessage(final int playerId, final int armyId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageBattleWin(playerId, armyId));
    }
    
    public static void sendGetPowerTaskMessage(final int playerId, final int powerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageGetPower(playerId, powerId));
    }
    
    public static void sendIncenseTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageIncense(playerId));
    }
    
    public static void sendRecruitForcesTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageRecruitForces(playerId));
    }
    
    public static void sendStoreBuyTaskMessage(final int playerId, final int num) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageStoreBuy(playerId, num));
    }
    
    public static void sendStoreRefreshTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageStoreRefresh(playerId));
    }
    
    public static void sendUpdateEquipTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageUpdateEquip(playerId));
    }
    
    public static void sendWearEquipTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWearEquip(playerId));
    }
    
    public static void sendHallsFightTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageHallsFight(playerId));
    }
    
    public static void sendHallsPositionTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageHallsPosition(playerId));
    }
    
    public static void sendGeneralLvTaskMessage(final int playerId, final int lv) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageGeneralLv(playerId, lv));
    }
    
    public static void sendGetExploitTaskMessage(final int playerId, final int number) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageGetExploit(playerId, number));
    }
    
    public static void sendChiefLvTaskMessage(final int playerId, final int lv) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageChiefLv(playerId, lv));
    }
    
    public static void sendWorldMoveTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageMove(playerId));
    }
    
    public static void sendWorldSearchTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageSearch(playerId));
    }
    
    public static void sendWorldPvpTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWorldPvp(playerId));
    }
    
    public static void sendAttTaskMessage(final int playerId, final int number) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageAtt(playerId, number));
    }
    
    public static void sendDefTaskMessage(final int playerId, final int number) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageDef(playerId, number));
    }
    
    public static void sendBloodTaskMessage(final int playerId, final int number) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageBlood(playerId, number));
    }
    
    public static void sendBattleWinTimesTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageBattleWinTimes(playerId));
    }
    
    public static void sendFullBloodTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageFullBlood(playerId));
    }
    
    public static void sendSearchTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageSearchExhaust(playerId));
    }
    
    public static void sendEquipTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageEquip(playerId));
    }
    
    public static void sendStoreBuySTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageStoreBuyS(playerId));
    }
    
    public static void sendEventDailyMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageEventDaily(playerId));
    }
    
    public static void sendHallsVisitMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageHallsVisit(playerId));
    }
    
    public static void sendTreasureMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTreasure(playerId));
    }
    
    public static void sendEquipOnTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageEquipOn(playerId));
    }
    
    public static void sendArmyAdviserTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageArmyAdviser(playerId));
    }
    
    public static void sendCheckDailyKillTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageCheckDailyKill(playerId));
    }
    
    public static void sendOfficerAffairTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageOfficerAffair(playerId));
    }
    
    public static void sendOfficerHarvestTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageOfficerHarvest(playerId));
    }
    
    public static void sendMarketBuyTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageMarketBuy(playerId));
    }
    
    public static void sendWorldMineIronOwnTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWorldMineIronOwn(playerId));
    }
    
    public static void sendCheckCountryListTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageCheckCountryList(playerId));
    }
    
    public static void sendTradeTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTrade(playerId));
    }
    
    public static void sendWorldMineJadeOwnTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageMarketBuy(playerId));
    }
    
    public static void sendTreasureVisitMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTreasureVisit(playerId));
    }
    
    public static void sendDinnerMessage(final int playerId, final int triggerType) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageDinner(playerId, triggerType));
    }
    
    public static void sendArmsWeaponOnMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageArmsWeaponOn(playerId));
    }
    
    public static void sendWorldMineIronVisitMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWorldMineIronVisit(playerId));
    }
    
    public static void sendWorldMineJadeVisitMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWorldMineJadeVisit(playerId));
    }
    
    public static void sendBlackMarketBuyMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageBlackMarketBuy(playerId));
    }
    
    public static void sendFireGeneralMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageFireGeneral(playerId));
    }
    
    public static void sendGetSalaryMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageGetSalary(playerId));
    }
    
    public static void sendOfficialMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageOfficial(playerId));
    }
    
    public static void sendChangeNameMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageChangeName(playerId));
    }
    
    public static void sendAutoBattleMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageOneAutoArmies(playerId));
    }
    
    public static void sendNationalRankBattleMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageOneNationalRankBattle(playerId));
    }
    
    public static void sendNationalRankBattleWinMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageOneNationalRankBattleWin(playerId));
    }
    
    public static void sendBonusBattleWinMessage(final int playerId, final int bonusId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageBonusBattle(playerId, bonusId));
    }
    
    public static void sendUseIncenseTaskMessage(final int playerId, final int id) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageUseIncense(playerId, id));
    }
    
    public static void sendSellEquipTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageSellEquip(playerId));
    }
    
    public static void sendResourceTotalTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageResourceTotal(playerId));
    }
    
    public static void sendTechInjectTaskMessage(final int playerId, final int techId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTechInject(playerId, techId));
    }
    
    public static void sendTechResearchTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTechResearch(playerId));
    }
    
    public static void sendTechResearchBeginTaskMessage(final int playerId, final int techId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTechResearchBegin(playerId, techId));
    }
    
    public static void sendTechResearchDoneTaskMessage(final int playerId, final int techId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTechResearchDone(playerId, techId));
    }
    
    public static void sendItemCollectTaskMessage(final int playerId, final int itemId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageCollect(playerId, itemId));
    }
    
    public static void sendWorldMistWinTaskMessage(final int playerId, final int area) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWorldMistWin(playerId, area));
    }
    
    public static void sendChooseGeneralTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageChooseGeneral(playerId));
    }
    
    public static void sendWorldTreasureGetTaskMessage(final int playerId, final int boxId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWorldTreasure(playerId, boxId));
    }
    
    public static void sendWorldTreasureByTypeGetTaskMessage(final int playerId, final int boxId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWorldTreasureByType(playerId, boxId));
    }
    
    public static void sendUseFreeConsTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageUseFreeCons(playerId));
    }
    
    public static void sendQuenchingTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskQuenchingMessage(playerId));
    }
    
    public static void sendOpenWeaponTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageArmesWeapon(playerId));
    }
    
    public static void sendBlackMarketVisitTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageBlackMarketVisit(playerId));
    }
    
    public static void sendKillBanditTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageKillBandit(playerId));
    }
    
    public static void sendKillKidnapperTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageKillKidnapper(playerId));
    }
    
    public static void sendOpenBluePrintTaskMessage(final int playerId, final int id) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageOpenBluePrint(playerId, id));
    }
    
    public static void sendKillNumMessage(final int playerId, final int killNum, final int killTotal) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageKillNum(playerId, killNum, killTotal));
    }
    
    public static void sendWeaponMakeDoneTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageWeaponMakeDone(playerId));
    }
    
    public static void sendTimesUpTaskMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageTimesUp(playerId));
    }
    
    public static void sendRecuitGeneralMessage(final int playerId, final int generalType, final int generalId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageRecuitGeneral(playerId, generalType, generalId));
    }
    
    public static void sendBuildedLimboMessage(final int playerId) {
        HandlerManager.getHandler(TaskMessage.class).handler(new TaskMessageBuildedLimbo(playerId));
    }
}
