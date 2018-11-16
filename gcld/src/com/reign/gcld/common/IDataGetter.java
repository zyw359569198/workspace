package com.reign.gcld.common;

import com.reign.gcld.activity.service.*;
import com.reign.gcld.team.dao.*;
import com.reign.gcld.slave.service.*;
import com.reign.gcld.civiltrick.service.*;
import com.reign.gcld.tavern.service.*;
import com.reign.gcld.tavern.dao.*;
import com.reign.gcld.mine.service.*;
import com.reign.gcld.mine.dao.*;
import com.reign.gcld.weapon.dao.*;
import com.reign.gcld.grouparmy.service.*;
import com.reign.gcld.grouparmy.dao.*;
import com.reign.gcld.treasure.dao.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.market.service.*;
import com.reign.gcld.kfwd.dao.*;
import com.reign.gcld.task.dao.*;
import com.reign.gcld.task.service.*;
import com.reign.gcld.market.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.politics.service.*;
import com.reign.gcld.dinner.service.*;
import com.reign.gcld.gift.service.*;
import com.reign.gcld.incense.service.*;
import com.reign.gcld.store.service.*;
import java.util.*;
import com.reign.gcld.kfwd.common.handler.*;
import com.reign.gcld.civiltrick.trick.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.weapon.service.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.duel.cache.*;
import com.reign.gcld.rank.dao.*;
import com.reign.gcld.juben.dao.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.slave.dao.*;
import com.reign.gcld.score.dao.*;
import com.reign.gcld.phantom.dao.*;
import com.reign.gcld.phantom.service.*;
import com.reign.gcld.nation.service.*;
import com.reign.gcld.feat.service.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.incense.dao.*;
import com.reign.gcld.politics.dao.*;
import com.reign.gcld.blacksmith.dao.*;
import com.reign.gcld.courtesy.dao.*;
import com.reign.gcld.courtesy.service.*;
import com.reign.gcld.huizhan.service.*;
import com.reign.gcld.tickets.dao.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.huizhan.dao.*;
import com.reign.gcld.event.dao.*;
import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.treasure.service.*;
import com.reign.gcld.event.service.*;
import com.reign.gcld.kfzb.service.*;
import com.reign.gcld.building.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.blacksmith.service.*;
import com.reign.gcld.kfgz.dao.*;
import com.reign.gcld.kfzb.dao.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.pay.service.*;
import com.reign.gcld.diamondshop.service.*;
import com.reign.gcld.diamondshop.dao.*;
import com.reign.gcld.feat.dao.*;
import com.reign.gcld.dinner.dao.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.auto.service.*;
import com.reign.gcld.sdata.cache.*;

public interface IDataGetter
{
    ArmsJsSkillCache getArmsJsSkillCache();
    
    IActivityService getActivityService();
    
    OfficialCache getOfficialCache();
    
    SerialCache getSerialCache();
    
    WorldLegionCache getWorldLegionCache();
    
    IPlayerTeamDao getPlayerTeamDao();
    
    INationTaskDao getNationTaskDao();
    
    WorldGuardInfoCache getWorldGuardInfoCache();
    
    WorldGuardCache getWorldGuardCache();
    
    FightStrategiesCache getFightStrategiesCache();
    
    FightStragtegyCoeCache getFightStragtegyCoeCache();
    
    IStoreHouseService getStoreHouseService();
    
    ISlaveService getSlaveService();
    
    ICilvilTrickService getCilvilTrickService();
    
    FightRewardCoeCache getFightRewardCoeCache();
    
    IPlayerBattleAttributeDao getPlayerBattleAttributeDao();
    
    QualifyingLevelCache getQualifyingLevelCache();
    
    IRankBatService getRankBatService();
    
    WorldCityAreaCache getWorldCityAreaCache();
    
    IPlayerWorldDao getPlayerWorldDao();
    
    ITavernService getTavernService();
    
    IPlayerTavernDao getPlayerTavernDao();
    
    IGroupArmyDao getGroupArmyDao();
    
    WorldTreasureCache getWorldTreasureCache();
    
    IBarbariansKillInfoDao getBarbariansKillInfoDao();
    
    IPlayerOfficeRelativeDao getPlayerOfficeRelativeDao();
    
    IPlayerIndivTaskDao getPlayerIndivTaskDao();
    
    IMineService getMineService();
    
    MineCache getMineCache();
    
    PlayerMineDao getPlayerMineDao();
    
    IPlayerMineBatInfoDao getPlayerMineBatInfoDao();
    
    ArmsGemCache getArmsGemCache();
    
    IPlayerWeaponDao getPlayerWeaponDao();
    
    ArmsWeaponCache getArmsWeaponCache();
    
    IKillRankService getKillRankService();
    
    ICityNpcDao getCityNpcDao();
    
    WorldCountryNpcCache getWorldCountryNpcCache();
    
    IPlayerKillInfoDao getPlayerKillInfoDao();
    
    IGroupArmyService getGroupArmyService();
    
    IPlayerGroupArmyDao getPlayerGroupArmyDao();
    
    IPlayerOfficerBuildingDao getPlayerOfficerBuildingDao();
    
    IPlayerBattleAutoDao getPlayerBattleAutoDao();
    
    IBattleService getBattleService();
    
    ChargeitemCache getChargeitemCache();
    
    IPlayerBattleRewardDao getPlayerBattleRewardDao();
    
    IPlayerGeneralCivilDao getPlayerGeneralCivilDao();
    
    TacticCache getTacticCache();
    
    IRankService getRankService();
    
    IBattleInfoService getBattleInfoService();
    
    RandomNamer getRandomNamer();
    
    BattleStatCache getBattleStatCache();
    
    BattleDataCache getBattleDataCache();
    
    IPlayerTreasureDao getPlayerTreasureDao();
    
    TreasureCache getTreasureCache();
    
    TechCache getTechCache();
    
    IPlayerTechDao getPlayerTechDao();
    
    IJobService getJobService();
    
    ICityService getCityService();
    
    WorldRoadCache getWorldRoadCache();
    
    WorldCityCache getWorldCityCache();
    
    ICityDao getCityDao();
    
    HallsCache getHallsCache();
    
    CCache getcCache();
    
    IChatService getChatService();
    
    IOccupyService getOccupyService();
    
    IOfficerBuildingInfoDao getOfficerBuildingInfoDao();
    
    PowerCache getPowerCache();
    
    IPlayerPowerDao getPlayerPowerDao();
    
    IBattleInfoDao getBattleInfoDao();
    
    IPlayerArmyDao getPlayerArmyDao();
    
    IPlayerArmyRewardDao getPlayerArmyRewardDao();
    
    IGeneralService getGeneralService();
    
    IPlayerResourceDao getPlayerResourceDao();
    
    IPlayerService getPlayerService();
    
    IPlayerAttributeDao getPlayerAttributeDao();
    
    IStoreHouseDao getStoreHouseDao();
    
    EquipCache getEquipCache();
    
    IPlayerGeneralMilitaryDao getPlayerGeneralMilitaryDao();
    
    IPlayerDao getPlayerDao();
    
    ArmiesCache getArmiesCache();
    
    ArmyCache getArmyCache();
    
    GeneralCache getGeneralCache();
    
    TroopCache getTroopCache();
    
    ArmiesRewardCache getArmiesRewardCache();
    
    IMarketService getMarketService();
    
    IWorldService getWorldService();
    
    KfwdMatchSignDao getKfwdMatchSignDao();
    
    BuildingOutputCache getBuildingOutputCache();
    
    BuildingCache getBuildingCache();
    
    PlayerTaskDao getPlayerTaskDao();
    
    PlayerBuildingDao getPlayerBuildingDao();
    
    PlayerSearchDao getPlayerSearchDao();
    
    IPlayerTaskService getPlayerTaskService();
    
    ItemsCache getItemsCache();
    
    PlayerMarketDao getPlayerMarketDao();
    
    IBuildingService getBuildingService();
    
    PlayerConstantsDao getPlayerConstantsDao();
    
    PlayerBuildingWorkDao getPlayerBuildingWorkDao();
    
    IPoliticsService getPoliticsService();
    
    IDinnerService getDinnerService();
    
    IGiftService getGiftService();
    
    IIncenseService getIncenseService();
    
    PlayerStoreDao getPlayerStoreDao();
    
    IStoreService getStoreService();
    
    IPlayerBatRankDao getPlayerBatRankDao();
    
    QualifyingGroupCache getQualifyingGroupCache();
    
    void doHandleWithTrans(final List<IRewardOperationHandler> p0);
    
    ITechService getTechService();
    
    StratagemCache getStratagemCache();
    
    ICityTrickStateCache getCityTrickStateCache();
    
    TechEffectCache getTechEffectCache();
    
    ArmiesExtraCache getArmiesExtraCache();
    
    IPlayerArmyExtraDao getPlayerArmyExtraDao();
    
    BattleDropFactory getBattleDropFactory();
    
    BattleDropService getBattleDropService();
    
    IStoreHouseSellDao getStoreHouseSellDao();
    
    TaskCache getTaskCache();
    
    TroopConscribeCache getTroopConscribeCache();
    
    ICityDataCache getCityDataCache();
    
    ChatUtil getChatUtil();
    
    BroadCastUtil getBroadCastUtil();
    
    ICityNpcLostDao getCityNpcLostDao();
    
    IPlayerMistLostDao getPlayerMistLostDao();
    
    IWeaponService getWeaponService();
    
    EquipSkillEffectCache getEquipSkillEffectCache();
    
    CityDefenceNpcDao getCityDefenceNpcDao();
    
    BuildingDrawingCache getBuildingDrawingCache();
    
    WorldCityDistanceNpcNumCache getWorldCityDistanceNpcNumCache();
    
    IBluePrintDao getBluePrintDao();
    
    IPlayerGeneralMilitaryPhantomDao getPlayerGeneralMilitaryPhantomDao();
    
    IOfficerTokenDao getOfficerTokenDao();
    
    CityEffectCache getCityEffectCache();
    
    IForceInfoDao getForceInfoDao();
    
    BarbarainCache getBarbarainCache();
    
    ITaskKillInfoDao getTaskKillInfoDao();
    
    IMailService getMailService();
    
    KtMzSCache getKtMzSCache();
    
    BarbarainPhantomDao getBarbarainPhantomDao();
    
    EfLvCache getEfLvCache();
    
    EfLCache getEfLCache();
    
    ExpeditionArmyDao getExpeditionArmyDao();
    
    IPlayerChallengeInfoDao getPlayerChallengeInfoDao();
    
    IPlayerOccupyCityDao getPlayerOccupyCityDao();
    
    DuelRecordsCache getDuelRecordsCache();
    
    WorldPaidBCache getWorldPaidBCache();
    
    ITimerBattleService getTimerBattleService();
    
    DuelsCache getDuelsCache();
    
    BarbarainExpeditionArmyDao getBarbarainExpeditionArmyDao();
    
    WdSjBoCache getWdSjBoCache();
    
    WdSjEvCache getWdSjEvCache();
    
    WdSjFeCache getWdSjFeCache();
    
    WdSjInCache getWdSjInCache();
    
    WdSjSeCache getWdSjSeCache();
    
    NationTaskExpeditionArmyDao getNationTaskExpeditionArmyDao();
    
    IPlayerCouponDao getPlayerCouponDao();
    
    IPlayerScenarioCityDao getPlayerScenarioCityDao();
    
    IPlayerScenarioDao getPlayerScenarioDao();
    
    SoloCityCache getSoloCityCache();
    
    IJuBenService getJuBenService();
    
    SoloRoadCache getSoloRoadCache();
    
    IScenarioNpcDao getScenarioNpcDao();
    
    WdSjpHyCache getWdSjpHyCache();
    
    WdSjpCache getWdSjpCache();
    
    WdSjpGemCache getWdSjpGemCache();
    
    SoloRewardCache getSoloRewardCache();
    
    SoloEventCache getSoloEventCache();
    
    KtSdmzSCache getKtSdmzSCache();
    
    SoloDramaCache getSoloDramaCache();
    
    ISlaveholderDao getSlaveholderDao();
    
    WnCitynpcLvCache getWnCitynpcLvCache();
    
    IPlayerScoreRankDao getPlayerScoreRankDao();
    
    IPlayerWizardDao getPlayerWizardDao();
    
    INationService getNationService();
    
    HmPwMainCache getHmPwMainCache();
    
    HmPwCritCache getHmPwCritCache();
    
    IPhantomService getPhantomService();
    
    CdExamsCache getCdExamsCache();
    
    IProtectService getProtectService();
    
    IFeatService getFeatService();
    
    FbGuideCache getFbGuideCache();
    
    IPowerService getPowerService();
    
    IPlayerIncenseDao getPlayerIncenseDao();
    
    IPlayerPoliticsEventDao getPlayerPoliticsEventDao();
    
    YellowTurbansDao getYellowTurbansDao();
    
    IPlayerBlacksmithDao getPlayerBlacksmithDao();
    
    WdSjpXtysCache getWdSjpXtysCache();
    
    ChatWordsCache getChatWordsCache();
    
    EtiqueteEventCache getEtiqueteEventCache();
    
    EtiquetePointCache getEtiquetePointCache();
    
    IPlayerLiYiDao getPlayerLiYiDao();
    
    ICourtesyService getCourtesyService();
    
    KtCoNpcCache getKtCoNpcCache();
    
    IHuiZhanService getHuiZhanService();
    
    IHuizhanHistoryDao getHuizhanHistoryDao();
    
    IPlayerTicketsDao getPlayerTicketsDao();
    
    KtMrCache getKtMrCache();
    
    IJuBenDataCache getJuBenDataCache();
    
    IPlayerHuizhanDao getPlayerHuizhanDao();
    
    IPlayerEventDao getPlayerEventDao();
    
    void handleAsynchronousDBOperation(final IAsynchronousDBOperation p0);
    
    void handleAsynchronousDBOperationNoTrans(final IAsynchronousDBOperation p0);
    
    void handleAsynchronousDBOperationListInNewTrans(final List<IAsynchronousDBOperation> p0);
    
    IActivityNpcDao getActivityNpcDao();
    
    FstNdEventCache getFstNdEventCache();
    
    FstDbLveCache getFstDbLveCache();
    
    ITreasureService getTreasureService();
    
    IEventService getEventService();
    
    IKfzbSeasonService getKfzbSeasonService();
    
    IKfzbMatchService getKfzbMatchService();
    
    IKfzbInfoDao getKfzbInfoDao();
    
    IKfzbRewardDao getKfzbRewardDao();
    
    IKfzbSignupDao getKfzbSignupDao();
    
    WdSjpDramaCache getWdSjpDramaCache();
    
    IPlayerResourceAdditionDao getPlayerResourceAdditionDao();
    
    IPlayerIncenseWeaponEffectDao getPlayerIncenseWeaponEffectDao();
    
    IPlayerQuenchingRelativeDao getPlayerQuenchingRelativeDao();
    
    IBlacksmithService getBlacksmithService();
    
    EquipSkillCache getEquipSkillCache();
    
    IKfgzSignupDao getKfgzSignupDao();
    
    GeneralRecruitCache getGeneralRecruitCache();
    
    KfzbSupportDao getKfzbSupportDao();
    
    FarmCache getFarmCache();
    
    IWorldFarmService getWorldFarmService();
    
    IPayService getPayService();
    
    GeneralTreasureCache getGeneralTreasureCache();
    
    IDiamondShopService getDiamondShopService();
    
    IPlayerDiamondShopDao getPlayerDiamondShopDao();
    
    IFeatBuildingDao getFeatBuildingDao();
    
    IPlayerDinnerDao getPlayerDinnerDao();
    
    NationIndivTaskCache getNationIndivTaskCache();
    
    WdSjpLblCache getWdSjpLblCache();
    
    IIndividualTaskService getIndividualTaskService();
    
    WdSjpSdlrCache getWdSjpSdlrCache();
    
    INationInfoDao getNationInfoDao();
    
    KtNfCache getKtNfCache();
    
    IAutoBattleService getAutoBattleService();
    
    HmGtDropCache getHmGtDropCache();
}
