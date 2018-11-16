package com.reign.gcld.common;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.treasure.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.grouparmy.service.*;
import com.reign.gcld.weapon.dao.*;
import com.reign.gcld.mine.dao.*;
import com.reign.gcld.mine.service.*;
import com.reign.gcld.grouparmy.dao.*;
import com.reign.gcld.tavern.dao.*;
import com.reign.gcld.tavern.service.*;
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
import com.reign.gcld.civiltrick.service.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.slave.service.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.weapon.service.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.team.dao.*;
import com.reign.gcld.duel.cache.*;
import com.reign.gcld.juben.dao.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.activity.service.*;
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
import com.reign.gcld.huizhan.dao.*;
import com.reign.gcld.tickets.dao.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.event.dao.*;
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
import com.reign.gcld.rank.dao.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.auto.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.asynchronousDB.operation.*;
import org.springframework.transaction.annotation.*;
import java.util.*;
import com.reign.gcld.kfwd.common.handler.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.civiltrick.trick.*;

@Component("dataGetter")
public class DataGetter implements IDataGetter
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private ArmiesCache armiesCache;
    @Autowired
    private ArmyCache armyCache;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private TroopCache troopCache;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private EquipCache equipCache;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private StratagemCache stratagemCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IPlayerArmyDao playerArmyDao;
    @Autowired
    private CityTrickStateCache cityTrickStateCache;
    @Autowired
    private IBattleInfoDao battleInfoDao;
    @Autowired
    private IPlayerPowerDao playerPowerDao;
    @Autowired
    private PowerCache powerCache;
    @Autowired
    private IOfficerBuildingInfoDao officerBuildingInfoDao;
    @Autowired
    private IOccupyService occupyService;
    @Autowired
    private IChatService chatService;
    @Autowired
    private CCache cCache;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private ICityDataCache cityDataCache;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private WorldRoadCache worldRoadCache;
    @Autowired
    private ICityService cityService;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private TechCache techCache;
    @Autowired
    private IPlayerTreasureDao playerTreasureDao;
    @Autowired
    private TreasureCache treasureCache;
    @Autowired
    private BattleDataCache battleDataCache;
    @Autowired
    private BattleStatCache battleStatCache;
    @Autowired
    private RandomNamer randomNamer;
    @Autowired
    private IBattleInfoService battleInfoService;
    @Autowired
    private IRankService rankService;
    @Autowired
    private TacticCache tacticCache;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private IPlayerBattleRewardDao playerBattleRewardDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IBattleService battleService;
    @Autowired
    private IPlayerBattleAutoDao playerBattleAutoDao;
    @Autowired
    private IPlayerOfficerBuildingDao playerOfficerBuildingDao;
    @Autowired
    private IPlayerGroupArmyDao playerGroupArmyDao;
    @Autowired
    private IGroupArmyService groupArmyService;
    @Autowired
    private IPlayerKillInfoDao playerKillInfoDao;
    @Autowired
    private WorldCountryNpcCache worldCountryNpcCache;
    @Autowired
    private ICityNpcDao cityNpcDao;
    @Autowired
    private IKillRankService killRankService;
    @Autowired
    private IPlayerWeaponDao playerWeaponDao;
    @Autowired
    private ArmsWeaponCache armsWeaponCache;
    @Autowired
    private ArmsGemCache armsGemCache;
    @Autowired
    private PlayerMineDao playerMineDao;
    @Autowired
    private IPlayerMineBatInfoDao playerMineBatInfoDao;
    @Autowired
    private MineCache mineCache;
    @Autowired
    private IMineService mineService;
    @Autowired
    private IGroupArmyDao groupArmyDao;
    @Autowired
    private IPlayerTavernDao playerTavernDao;
    @Autowired
    private ITavernService tavernService;
    @Autowired
    private IPlayerWorldDao playerWorldDao;
    @Autowired
    private WorldCityAreaCache worldCityAreaCache;
    @Autowired
    private IRankBatService rankBatService;
    @Autowired
    private QualifyingLevelCache qualifyingLevelCache;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private FightRewardCoeCache fightRewardCoeCache;
    @Autowired
    private IPlayerArmyRewardDao playerArmyRewardDao;
    @Autowired
    private ArmiesRewardCache armiesRewardCache;
    @Autowired
    private IMarketService marketService;
    @Autowired
    private IWorldService worldService;
    @Autowired
    private KfwdMatchSignDao kfwdMatchSignDao;
    @Autowired
    private BuildingOutputCache buildingOutputCache;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private BuildingCache buildingCache;
    @Autowired
    private PlayerTaskDao playerTaskDao;
    @Autowired
    private PlayerBuildingDao playerBuildingDao;
    @Autowired
    private PlayerSearchDao playerSearchDao;
    @Autowired
    private IPlayerTaskService playerTaskService;
    @Autowired
    private ItemsCache itemsCache;
    @Autowired
    private PlayerMarketDao playerMarketDao;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private PlayerConstantsDao playerConstantsDao;
    @Autowired
    private PlayerBuildingWorkDao playerBuildingWorkDao;
    @Autowired
    private IPoliticsService politicsService;
    @Autowired
    private IDinnerService dinnerService;
    @Autowired
    private IGiftService giftService;
    @Autowired
    private IIncenseService incenseService;
    @Autowired
    private PlayerStoreDao playerStoreDao;
    @Autowired
    private IStoreService storeService;
    @Autowired
    private PlayerBatRankDao playerBatRankDao;
    @Autowired
    private ITechService techService;
    @Autowired
    private QualifyingGroupCache qualifyingGroupCache;
    @Autowired
    private ICilvilTrickService cilvilTrickService;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private ArmiesExtraCache armiesExtraCache;
    @Autowired
    private IPlayerArmyExtraDao playerArmyExtraDao;
    @Autowired
    private ISlaveService slaveService;
    @Autowired
    private BattleDropFactory battleDropFactory;
    @Autowired
    private BattleDropService battleDropService;
    @Autowired
    private IStoreHouseSellDao storeHouseSellDao;
    @Autowired
    private WorldTreasureCache worldTreasureCache;
    @Autowired
    private FightStragtegyCoeCache fightStragtegyCoeCache;
    @Autowired
    private FightStrategiesCache fightStrategiesCache;
    @Autowired
    private TaskCache taskCache;
    @Autowired
    private TroopConscribeCache troopConscribeCache;
    @Autowired
    private ChatUtil chatUtil;
    @Autowired
    private BroadCastUtil broadCastUtil;
    @Autowired
    private ICityNpcLostDao cityNpcLostDao;
    @Autowired
    private IPlayerMistLostDao playerMistLostDao;
    @Autowired
    private IWeaponService weaponService;
    @Autowired
    private EquipSkillEffectCache equipSkillEffectCache;
    @Autowired
    private CityDefenceNpcDao cityDefenceNpcDao;
    @Autowired
    private BuildingDrawingCache buildingDrawingCache;
    @Autowired
    private WorldCityDistanceNpcNumCache worldCityDistanceNpcNumCache;
    @Autowired
    private IBluePrintDao bluePrintDao;
    @Autowired
    private IPlayerGeneralMilitaryPhantomDao playerGeneralMilitaryPhantomDao;
    @Autowired
    private IOfficerTokenDao officerTokenDao;
    @Autowired
    private CityEffectCache cityEffectCache;
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    private BarbarainCache barbarainCache;
    @Autowired
    private IBarbariansKillInfoDao barbariansKillInfoDao;
    @Autowired
    private ITaskKillInfoDao taskKillInfoDao;
    @Autowired
    private INationTaskDao nationTaskDao;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IPlayerTeamDao PlayerTeamDao;
    @Autowired
    private WorldLegionCache worldLegionCache;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private KtMzSCache ktMzSCache;
    @Autowired
    private BarbarainPhantomDao barPhantomDao;
    @Autowired
    private EfLvCache efLvCache;
    @Autowired
    private EfLCache efLCache;
    @Autowired
    private ExpeditionArmyDao expeditionArmyDao;
    @Autowired
    private IPlayerOccupyCityDao playerOccupyCityDao;
    @Autowired
    private IPlayerChallengeInfoDao playerChallengeInfoDao;
    @Autowired
    private DuelRecordsCache duelRecordsCache;
    @Autowired
    private WorldPaidBCache worldPaidBCache;
    @Autowired
    private ITimerBattleService timerBattleService;
    @Autowired
    private DuelsCache duelsCache;
    @Autowired
    private BarbarainExpeditionArmyDao barbarainExpeditionArmyDao;
    @Autowired
    private OfficialCache officialCache;
    @Autowired
    private WdSjBoCache wdSjBoCache;
    @Autowired
    private WdSjEvCache wdSjEvCache;
    @Autowired
    private WdSjFeCache wdSjFeCache;
    @Autowired
    private WdSjInCache wdSjInCache;
    @Autowired
    private WdSjSeCache wdSjSeCache;
    @Autowired
    private NationTaskExpeditionArmyDao nationTaskExpeditionArmyDao;
    @Autowired
    private IPlayerCouponDao playerCouponDao;
    @Autowired
    private IPlayerScenarioCityDao playerScenarioCityDao;
    @Autowired
    private IPlayerScenarioDao playerScenarioDao;
    @Autowired
    private SoloCityCache soloCityCache;
    @Autowired
    private SoloRoadCache soloRoadCache;
    @Autowired
    private IJuBenService juBenService;
    @Autowired
    private IScenarioNpcDao scenarioNpcDao;
    @Autowired
    private WdSjpHyCache wdSjpHyCache;
    @Autowired
    private WdSjpCache wdSjpCache;
    @Autowired
    private WdSjpGemCache wdSjpGemCache;
    @Autowired
    private SoloRewardCache soloRewardCache;
    @Autowired
    private IActivityService activityService;
    @Autowired
    private SoloEventCache soloEventCache;
    @Autowired
    private KtSdmzSCache ktSdmzSCache;
    @Autowired
    private SoloDramaCache soloDramaCache;
    @Autowired
    private ISlaveholderDao slaveholderDao;
    @Autowired
    private WnCitynpcLvCache wnCitynpcLvCache;
    @Autowired
    private IPlayerScoreRankDao playerScoreRankDao;
    @Autowired
    private IPlayerWizardDao playerWizardDao;
    @Autowired
    private HmPwMainCache hmPwMainCache;
    @Autowired
    private HmPwCritCache hmPwCritCache;
    @Autowired
    private INationService nationService;
    @Autowired
    private IPhantomService phantomService;
    @Autowired
    private CdExamsCache cdExamsCache;
    @Autowired
    private IProtectService protectService;
    @Autowired
    private FbGuideCache fbGuideCache;
    @Autowired
    private IFeatService featService;
    @Autowired
    private IPowerService powerService;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private IPlayerIncenseDao playerIncenseDao;
    @Autowired
    private IPlayerPoliticsEventDao playerPoliticsEventDao;
    @Autowired
    private YellowTurbansDao yellowTurbansDao;
    @Autowired
    private IPlayerBlacksmithDao playerBlacksmithDao;
    @Autowired
    private WdSjpXtysCache wdSjpXtysCache;
    @Autowired
    private ChatWordsCache chatWordsCache;
    @Autowired
    private EtiqueteEventCache etiqueteEventCache;
    @Autowired
    private EtiquetePointCache etiquetePointCache;
    @Autowired
    private IPlayerLiYiDao playerLiYiDao;
    @Autowired
    private ICourtesyService courtesyService;
    @Autowired
    private KtCoNpcCache ktCoNpcCache;
    @Autowired
    private IHuiZhanService huiZhanService;
    @Autowired
    private IHuizhanHistoryDao huizhanHistoryDao;
    @Autowired
    private IPlayerHuizhanDao playerHuizhanDao;
    @Autowired
    private IPlayerTicketsDao playerTicketsDao;
    @Autowired
    private KtMrCache ktMrCache;
    @Autowired
    private IJuBenDataCache juBenDataCache;
    @Autowired
    private IPlayerEventDao playerEventDao;
    @Autowired
    private IActivityNpcDao activityNpcDao;
    @Autowired
    private FstNdEventCache fstNdEventCache;
    @Autowired
    private FstDbLveCache fstDbLveCache;
    @Autowired
    private ITreasureService treasureService;
    @Autowired
    private IEventService eventService;
    @Autowired
    private IKfzbSeasonService kfzbSeasonService;
    @Autowired
    private IKfzbMatchService kfzbMatchService;
    @Autowired
    private IKfzbInfoDao kfzbInfoDao;
    @Autowired
    private IKfzbRewardDao kfzbRewardDao;
    @Autowired
    private IKfzbSignupDao kfzbSignupDao;
    @Autowired
    private WdSjpDramaCache wdSjpDramaCache;
    @Autowired
    private IPlayerResourceAdditionDao playerResourceAdditionDao;
    @Autowired
    private IPlayerIncenseWeaponEffectDao playerIncenseWeaponEffectDao;
    @Autowired
    private IPlayerQuenchingRelativeDao playerQuenchingRelativeDao;
    @Autowired
    private IBlacksmithService blacksmithService;
    @Autowired
    private EquipSkillCache equipSkillCache;
    @Autowired
    private IKfgzSignupDao kfgzSignupDao;
    @Autowired
    private GeneralRecruitCache generalRecruitCache;
    @Autowired
    private KfzbSupportDao kfzbSupportDao;
    @Autowired
    private FarmCache farmCache;
    @Autowired
    private IWorldFarmService worldFarmService;
    @Autowired
    private IPayService payService;
    @Autowired
    private GeneralTreasureCache generalTreasureCache;
    @Autowired
    private IDiamondShopService diamondShopService;
    @Autowired
    private IPlayerDiamondShopDao playerDiamondShopDao;
    @Autowired
    private IFeatBuildingDao featBuildingDao;
    @Autowired
    private IPlayerDinnerDao playerDinnerDao;
    @Autowired
    private NationIndivTaskCache nationIndivTaskCache;
    @Autowired
    private IPlayerIndivTaskDao playerIndivTaskDao;
    @Autowired
    private WdSjpLblCache wdSjpLblCache;
    @Autowired
    private IIndividualTaskService individualTaskService;
    @Autowired
    private WdSjpSdlrCache wdSjpSdlrCache;
    @Autowired
    private INationInfoDao nationInfoDao;
    @Autowired
    private KtNfCache ktNfCache;
    @Autowired
    private ArmsJsSkillCache armsJsSkillCache;
    @Autowired
    private IAutoBattleService autoBattleService;
    @Autowired
    private HmGtDropCache hmGtDropCache;
    @Autowired
    private WorldGuardCache worldGuardCache;
    @Autowired
    private WorldGuardInfoCache worldGuardInfoCache;
    
    @Override
    public ArmsJsSkillCache getArmsJsSkillCache() {
        return this.armsJsSkillCache;
    }
    
    @Override
    public IIndividualTaskService getIndividualTaskService() {
        return this.individualTaskService;
    }
    
    public void setIndividualTaskService(final IIndividualTaskService individualTaskService) {
        this.individualTaskService = individualTaskService;
    }
    
    @Override
    public IPlayerIndivTaskDao getPlayerIndivTaskDao() {
        return this.playerIndivTaskDao;
    }
    
    @Override
    public WdSjpLblCache getWdSjpLblCache() {
        return this.wdSjpLblCache;
    }
    
    public void setPlayerIndivTaskDao(final IPlayerIndivTaskDao playerIndivTaskDao) {
        this.playerIndivTaskDao = playerIndivTaskDao;
    }
    
    @Override
    public NationIndivTaskCache getNationIndivTaskCache() {
        return this.nationIndivTaskCache;
    }
    
    public void setNationIndivTaskCache(final NationIndivTaskCache nationIndivTaskCache) {
        this.nationIndivTaskCache = nationIndivTaskCache;
    }
    
    public void setWdSjpLblCache(final WdSjpLblCache wdSjpLblCache) {
        this.wdSjpLblCache = wdSjpLblCache;
    }
    
    @Override
    public IWorldFarmService getWorldFarmService() {
        return this.worldFarmService;
    }
    
    public void setWorldFarmService(final IWorldFarmService worldFarmService) {
        this.worldFarmService = worldFarmService;
    }
    
    @Override
    public FarmCache getFarmCache() {
        return this.farmCache;
    }
    
    public void setFarmCache(final FarmCache farmCache) {
        this.farmCache = farmCache;
    }
    
    @Override
    public GeneralRecruitCache getGeneralRecruitCache() {
        return this.generalRecruitCache;
    }
    
    public void setGeneralRecruitCache(final GeneralRecruitCache generalRecruitCache) {
        this.generalRecruitCache = generalRecruitCache;
    }
    
    @Override
    public WdSjpDramaCache getWdSjpDramaCache() {
        return this.wdSjpDramaCache;
    }
    
    public void setWdSjpDramaCache(final WdSjpDramaCache wdSjpDramaCache) {
        this.wdSjpDramaCache = wdSjpDramaCache;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleAsynchronousDBOperation(final IAsynchronousDBOperation asynchronousDBOperation) {
        asynchronousDBOperation.handleOperation(this);
    }
    
    @Override
    public void handleAsynchronousDBOperationNoTrans(final IAsynchronousDBOperation asynchronousDBOperation) {
        asynchronousDBOperation.handleOperation(this);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleAsynchronousDBOperationListInNewTrans(final List<IAsynchronousDBOperation> operationList) {
        for (final IAsynchronousDBOperation operation : operationList) {
            operation.handleOperation(this);
        }
    }
    
    @Override
    public SoloDramaCache getSoloDramaCache() {
        return this.soloDramaCache;
    }
    
    public void setSoloDramaCache(final SoloDramaCache soloDramaCache) {
        this.soloDramaCache = soloDramaCache;
    }
    
    @Override
    public IActivityService getActivityService() {
        return this.activityService;
    }
    
    @Override
    public OfficialCache getOfficialCache() {
        return this.officialCache;
    }
    
    @Override
    public SerialCache getSerialCache() {
        return this.serialCache;
    }
    
    @Override
    public WorldLegionCache getWorldLegionCache() {
        return this.worldLegionCache;
    }
    
    @Override
    public IPlayerTeamDao getPlayerTeamDao() {
        return this.PlayerTeamDao;
    }
    
    @Override
    public INationTaskDao getNationTaskDao() {
        return this.nationTaskDao;
    }
    
    @Override
    public ITaskKillInfoDao getTaskKillInfoDao() {
        return this.taskKillInfoDao;
    }
    
    public void setTaskKillInfoDao(final ITaskKillInfoDao taskKillInfoDao) {
        this.taskKillInfoDao = taskKillInfoDao;
    }
    
    @Override
    public IBarbariansKillInfoDao getBarbariansKillInfoDao() {
        return this.barbariansKillInfoDao;
    }
    
    public void setBarbariansKillInfoDao(final IBarbariansKillInfoDao barbariansKillInfoDao) {
        this.barbariansKillInfoDao = barbariansKillInfoDao;
    }
    
    @Override
    public IForceInfoDao getForceInfoDao() {
        return this.forceInfoDao;
    }
    
    public void setForceInfoDao(final IForceInfoDao forceInfoDao) {
        this.forceInfoDao = forceInfoDao;
    }
    
    @Override
    public IOfficerTokenDao getOfficerTokenDao() {
        return this.officerTokenDao;
    }
    
    public void setOfficerTokenDao(final IOfficerTokenDao officerTokenDao) {
        this.officerTokenDao = officerTokenDao;
    }
    
    @Override
    public WorldGuardInfoCache getWorldGuardInfoCache() {
        return this.worldGuardInfoCache;
    }
    
    @Override
    public WorldGuardCache getWorldGuardCache() {
        return this.worldGuardCache;
    }
    
    @Override
    public IWeaponService getWeaponService() {
        return this.weaponService;
    }
    
    public void setWeaponService(final IWeaponService weaponService) {
        this.weaponService = weaponService;
    }
    
    @Override
    public FightStrategiesCache getFightStrategiesCache() {
        return this.fightStrategiesCache;
    }
    
    @Override
    public FightStragtegyCoeCache getFightStragtegyCoeCache() {
        return this.fightStragtegyCoeCache;
    }
    
    @Override
    public ISlaveService getSlaveService() {
        return this.slaveService;
    }
    
    @Override
    public ICilvilTrickService getCilvilTrickService() {
        return this.cilvilTrickService;
    }
    
    @Override
    public FightRewardCoeCache getFightRewardCoeCache() {
        return this.fightRewardCoeCache;
    }
    
    @Override
    public IPlayerBattleAttributeDao getPlayerBattleAttributeDao() {
        return this.playerBattleAttributeDao;
    }
    
    @Override
    public QualifyingLevelCache getQualifyingLevelCache() {
        return this.qualifyingLevelCache;
    }
    
    @Override
    public IRankBatService getRankBatService() {
        return this.rankBatService;
    }
    
    @Override
    public WorldCityAreaCache getWorldCityAreaCache() {
        return this.worldCityAreaCache;
    }
    
    @Override
    public IPlayerWorldDao getPlayerWorldDao() {
        return this.playerWorldDao;
    }
    
    @Override
    public ITavernService getTavernService() {
        return this.tavernService;
    }
    
    @Override
    public IPlayerTavernDao getPlayerTavernDao() {
        return this.playerTavernDao;
    }
    
    @Override
    public IGroupArmyDao getGroupArmyDao() {
        return this.groupArmyDao;
    }
    
    @Override
    public IMineService getMineService() {
        return this.mineService;
    }
    
    @Override
    public MineCache getMineCache() {
        return this.mineCache;
    }
    
    @Override
    public PlayerMineDao getPlayerMineDao() {
        return this.playerMineDao;
    }
    
    @Override
    public IPlayerMineBatInfoDao getPlayerMineBatInfoDao() {
        return this.playerMineBatInfoDao;
    }
    
    @Override
    public ArmsGemCache getArmsGemCache() {
        return this.armsGemCache;
    }
    
    @Override
    public IPlayerWeaponDao getPlayerWeaponDao() {
        return this.playerWeaponDao;
    }
    
    @Override
    public ArmsWeaponCache getArmsWeaponCache() {
        return this.armsWeaponCache;
    }
    
    @Override
    public IKillRankService getKillRankService() {
        return this.killRankService;
    }
    
    @Override
    public ICityNpcDao getCityNpcDao() {
        return this.cityNpcDao;
    }
    
    @Override
    public WorldCountryNpcCache getWorldCountryNpcCache() {
        return this.worldCountryNpcCache;
    }
    
    @Override
    public IPlayerKillInfoDao getPlayerKillInfoDao() {
        return this.playerKillInfoDao;
    }
    
    @Override
    public IGroupArmyService getGroupArmyService() {
        return this.groupArmyService;
    }
    
    @Override
    public IPlayerGroupArmyDao getPlayerGroupArmyDao() {
        return this.playerGroupArmyDao;
    }
    
    @Override
    public IPlayerOfficerBuildingDao getPlayerOfficerBuildingDao() {
        return this.playerOfficerBuildingDao;
    }
    
    @Override
    public IPlayerBattleAutoDao getPlayerBattleAutoDao() {
        return this.playerBattleAutoDao;
    }
    
    @Override
    public IBattleService getBattleService() {
        return this.battleService;
    }
    
    @Override
    public ChargeitemCache getChargeitemCache() {
        return this.chargeitemCache;
    }
    
    @Override
    public IPlayerBattleRewardDao getPlayerBattleRewardDao() {
        return this.playerBattleRewardDao;
    }
    
    @Override
    public IPlayerGeneralCivilDao getPlayerGeneralCivilDao() {
        return this.playerGeneralCivilDao;
    }
    
    @Override
    public TacticCache getTacticCache() {
        return this.tacticCache;
    }
    
    public void setTacticCache(final TacticCache tacticCache) {
        this.tacticCache = tacticCache;
    }
    
    @Override
    public IRankService getRankService() {
        return this.rankService;
    }
    
    @Override
    public IBattleInfoService getBattleInfoService() {
        return this.battleInfoService;
    }
    
    @Override
    public RandomNamer getRandomNamer() {
        return this.randomNamer;
    }
    
    @Override
    public BattleStatCache getBattleStatCache() {
        return this.battleStatCache;
    }
    
    @Override
    public BattleDataCache getBattleDataCache() {
        return this.battleDataCache;
    }
    
    @Override
    public IPlayerTreasureDao getPlayerTreasureDao() {
        return this.playerTreasureDao;
    }
    
    @Override
    public TreasureCache getTreasureCache() {
        return this.treasureCache;
    }
    
    @Override
    public IPlayerTechDao getPlayerTechDao() {
        return this.playerTechDao;
    }
    
    @Override
    public TechCache getTechCache() {
        return this.techCache;
    }
    
    @Override
    public IJobService getJobService() {
        return this.jobService;
    }
    
    @Override
    public ICityService getCityService() {
        return this.cityService;
    }
    
    @Override
    public WorldRoadCache getWorldRoadCache() {
        return this.worldRoadCache;
    }
    
    @Override
    public WorldCityCache getWorldCityCache() {
        return this.worldCityCache;
    }
    
    @Override
    public ICityDao getCityDao() {
        return this.cityDao;
    }
    
    @Override
    public HallsCache getHallsCache() {
        return this.hallsCache;
    }
    
    @Override
    public CCache getcCache() {
        return this.cCache;
    }
    
    @Override
    public IChatService getChatService() {
        return this.chatService;
    }
    
    @Override
    public IOccupyService getOccupyService() {
        return this.occupyService;
    }
    
    @Override
    public IOfficerBuildingInfoDao getOfficerBuildingInfoDao() {
        return this.officerBuildingInfoDao;
    }
    
    @Override
    public IPlayerPowerDao getPlayerPowerDao() {
        return this.playerPowerDao;
    }
    
    @Override
    public PowerCache getPowerCache() {
        return this.powerCache;
    }
    
    @Override
    public IBattleInfoDao getBattleInfoDao() {
        return this.battleInfoDao;
    }
    
    @Override
    public IPlayerArmyDao getPlayerArmyDao() {
        return this.playerArmyDao;
    }
    
    @Override
    public IPlayerArmyRewardDao getPlayerArmyRewardDao() {
        return this.playerArmyRewardDao;
    }
    
    @Override
    public IGeneralService getGeneralService() {
        return this.generalService;
    }
    
    @Override
    public IPlayerResourceDao getPlayerResourceDao() {
        return this.playerResourceDao;
    }
    
    @Override
    public IPlayerService getPlayerService() {
        return this.playerService;
    }
    
    @Override
    public IPlayerAttributeDao getPlayerAttributeDao() {
        return this.playerAttributeDao;
    }
    
    @Override
    public IStoreHouseDao getStoreHouseDao() {
        return this.storeHouseDao;
    }
    
    @Override
    public EquipCache getEquipCache() {
        return this.equipCache;
    }
    
    @Override
    public IPlayerGeneralMilitaryDao getPlayerGeneralMilitaryDao() {
        return this.playerGeneralMilitaryDao;
    }
    
    @Override
    public IPlayerDao getPlayerDao() {
        return this.playerDao;
    }
    
    @Override
    public ArmiesCache getArmiesCache() {
        return this.armiesCache;
    }
    
    @Override
    public ArmyCache getArmyCache() {
        return this.armyCache;
    }
    
    @Override
    public GeneralCache getGeneralCache() {
        return this.generalCache;
    }
    
    @Override
    public TroopCache getTroopCache() {
        return this.troopCache;
    }
    
    @Override
    public ArmiesRewardCache getArmiesRewardCache() {
        return this.armiesRewardCache;
    }
    
    @Override
    public IMarketService getMarketService() {
        return this.marketService;
    }
    
    @Override
    public IWorldService getWorldService() {
        return this.worldService;
    }
    
    @Transactional
    @Override
    public void doHandleWithTrans(final List<IRewardOperationHandler> list) {
        for (final IRewardOperationHandler handler : list) {
            handler.handle(this);
        }
    }
    
    @Override
    public KfwdMatchSignDao getKfwdMatchSignDao() {
        return this.kfwdMatchSignDao;
    }
    
    @Override
    public BuildingOutputCache getBuildingOutputCache() {
        return this.buildingOutputCache;
    }
    
    @Override
    public IStoreHouseService getStoreHouseService() {
        return this.storeHouseService;
    }
    
    @Override
    public BuildingCache getBuildingCache() {
        return this.buildingCache;
    }
    
    @Override
    public PlayerTaskDao getPlayerTaskDao() {
        return this.playerTaskDao;
    }
    
    @Override
    public PlayerBuildingDao getPlayerBuildingDao() {
        return this.playerBuildingDao;
    }
    
    @Override
    public PlayerSearchDao getPlayerSearchDao() {
        return this.playerSearchDao;
    }
    
    @Override
    public IPlayerTaskService getPlayerTaskService() {
        return this.playerTaskService;
    }
    
    @Override
    public ItemsCache getItemsCache() {
        return this.itemsCache;
    }
    
    @Override
    public PlayerMarketDao getPlayerMarketDao() {
        return this.playerMarketDao;
    }
    
    @Override
    public IBuildingService getBuildingService() {
        return this.buildingService;
    }
    
    @Override
    public PlayerConstantsDao getPlayerConstantsDao() {
        return this.playerConstantsDao;
    }
    
    @Override
    public PlayerBuildingWorkDao getPlayerBuildingWorkDao() {
        return this.playerBuildingWorkDao;
    }
    
    @Override
    public IPoliticsService getPoliticsService() {
        return this.politicsService;
    }
    
    @Override
    public IDinnerService getDinnerService() {
        return this.dinnerService;
    }
    
    @Override
    public IGiftService getGiftService() {
        return this.giftService;
    }
    
    @Override
    public IIncenseService getIncenseService() {
        return this.incenseService;
    }
    
    @Override
    public PlayerStoreDao getPlayerStoreDao() {
        return this.playerStoreDao;
    }
    
    @Override
    public IStoreService getStoreService() {
        return this.storeService;
    }
    
    @Override
    public ITechService getTechService() {
        return this.techService;
    }
    
    @Override
    public IPlayerBatRankDao getPlayerBatRankDao() {
        return this.playerBatRankDao;
    }
    
    @Override
    public QualifyingGroupCache getQualifyingGroupCache() {
        return this.qualifyingGroupCache;
    }
    
    public void setStratagemCache(final StratagemCache stratagemCache) {
        this.stratagemCache = stratagemCache;
    }
    
    @Override
    public StratagemCache getStratagemCache() {
        return this.stratagemCache;
    }
    
    public void setCityTrickStateCache(final CityTrickStateCache cityTrickStateCache) {
        this.cityTrickStateCache = cityTrickStateCache;
    }
    
    @Override
    public CityTrickStateCache getCityTrickStateCache() {
        return this.cityTrickStateCache;
    }
    
    @Override
    public TechEffectCache getTechEffectCache() {
        return this.techEffectCache;
    }
    
    @Override
    public ArmiesExtraCache getArmiesExtraCache() {
        return this.armiesExtraCache;
    }
    
    @Override
    public IPlayerArmyExtraDao getPlayerArmyExtraDao() {
        return this.playerArmyExtraDao;
    }
    
    @Override
    public BattleDropFactory getBattleDropFactory() {
        return this.battleDropFactory;
    }
    
    @Override
    public BattleDropService getBattleDropService() {
        return this.battleDropService;
    }
    
    @Override
    public IStoreHouseSellDao getStoreHouseSellDao() {
        return this.storeHouseSellDao;
    }
    
    public void setWorldTreasureCache(final WorldTreasureCache worldTreasureCache) {
        this.worldTreasureCache = worldTreasureCache;
    }
    
    @Override
    public WorldTreasureCache getWorldTreasureCache() {
        return this.worldTreasureCache;
    }
    
    @Override
    public TaskCache getTaskCache() {
        return this.taskCache;
    }
    
    @Override
    public TroopConscribeCache getTroopConscribeCache() {
        return this.troopConscribeCache;
    }
    
    public void setCityDataCache(final ICityDataCache cityDataCache) {
        this.cityDataCache = cityDataCache;
    }
    
    @Override
    public ICityDataCache getCityDataCache() {
        return this.cityDataCache;
    }
    
    public void setChatUtil(final ChatUtil chatUtil) {
        this.chatUtil = chatUtil;
    }
    
    @Override
    public BroadCastUtil getBroadCastUtil() {
        return this.broadCastUtil;
    }
    
    @Override
    public ChatUtil getChatUtil() {
        return this.chatUtil;
    }
    
    @Override
    public ICityNpcLostDao getCityNpcLostDao() {
        return this.cityNpcLostDao;
    }
    
    @Override
    public IPlayerMistLostDao getPlayerMistLostDao() {
        return this.playerMistLostDao;
    }
    
    @Override
    public EquipSkillEffectCache getEquipSkillEffectCache() {
        return this.equipSkillEffectCache;
    }
    
    @Override
    public CityDefenceNpcDao getCityDefenceNpcDao() {
        return this.cityDefenceNpcDao;
    }
    
    @Override
    public BuildingDrawingCache getBuildingDrawingCache() {
        return this.buildingDrawingCache;
    }
    
    @Override
    public WorldCityDistanceNpcNumCache getWorldCityDistanceNpcNumCache() {
        return this.worldCityDistanceNpcNumCache;
    }
    
    @Override
    public IBluePrintDao getBluePrintDao() {
        return this.bluePrintDao;
    }
    
    @Override
    public IPlayerGeneralMilitaryPhantomDao getPlayerGeneralMilitaryPhantomDao() {
        return this.playerGeneralMilitaryPhantomDao;
    }
    
    @Override
    public CityEffectCache getCityEffectCache() {
        return this.cityEffectCache;
    }
    
    @Override
    public BarbarainCache getBarbarainCache() {
        return this.barbarainCache;
    }
    
    @Override
    public IMailService getMailService() {
        return this.mailService;
    }
    
    @Override
    public KtMzSCache getKtMzSCache() {
        return this.ktMzSCache;
    }
    
    @Override
    public BarbarainPhantomDao getBarbarainPhantomDao() {
        return this.barPhantomDao;
    }
    
    @Override
    public EfLvCache getEfLvCache() {
        return this.efLvCache;
    }
    
    @Override
    public EfLCache getEfLCache() {
        return this.efLCache;
    }
    
    @Override
    public ExpeditionArmyDao getExpeditionArmyDao() {
        return this.expeditionArmyDao;
    }
    
    public void setPlayerOccupyCityDao(final IPlayerOccupyCityDao playerOccupyCityDao) {
        this.playerOccupyCityDao = playerOccupyCityDao;
    }
    
    @Override
    public IPlayerOccupyCityDao getPlayerOccupyCityDao() {
        return this.playerOccupyCityDao;
    }
    
    public void setPlayerChallengeInfoDao(final IPlayerChallengeInfoDao playerChallengeInfoDao) {
        this.playerChallengeInfoDao = playerChallengeInfoDao;
    }
    
    @Override
    public IPlayerChallengeInfoDao getPlayerChallengeInfoDao() {
        return this.playerChallengeInfoDao;
    }
    
    @Override
    public DuelRecordsCache getDuelRecordsCache() {
        return this.duelRecordsCache;
    }
    
    @Override
    public WorldPaidBCache getWorldPaidBCache() {
        return this.worldPaidBCache;
    }
    
    @Override
    public ITimerBattleService getTimerBattleService() {
        return this.timerBattleService;
    }
    
    @Override
    public DuelsCache getDuelsCache() {
        return this.duelsCache;
    }
    
    @Override
    public BarbarainExpeditionArmyDao getBarbarainExpeditionArmyDao() {
        return this.barbarainExpeditionArmyDao;
    }
    
    @Override
    public WdSjBoCache getWdSjBoCache() {
        return this.wdSjBoCache;
    }
    
    @Override
    public WdSjEvCache getWdSjEvCache() {
        return this.wdSjEvCache;
    }
    
    @Override
    public WdSjFeCache getWdSjFeCache() {
        return this.wdSjFeCache;
    }
    
    @Override
    public WdSjInCache getWdSjInCache() {
        return this.wdSjInCache;
    }
    
    @Override
    public WdSjSeCache getWdSjSeCache() {
        return this.wdSjSeCache;
    }
    
    @Override
    public NationTaskExpeditionArmyDao getNationTaskExpeditionArmyDao() {
        return this.nationTaskExpeditionArmyDao;
    }
    
    @Override
    public IPlayerCouponDao getPlayerCouponDao() {
        return this.playerCouponDao;
    }
    
    @Override
    public IPlayerScenarioCityDao getPlayerScenarioCityDao() {
        return this.playerScenarioCityDao;
    }
    
    @Override
    public IPlayerScenarioDao getPlayerScenarioDao() {
        return this.playerScenarioDao;
    }
    
    @Override
    public SoloCityCache getSoloCityCache() {
        return this.soloCityCache;
    }
    
    @Override
    public IJuBenService getJuBenService() {
        return this.juBenService;
    }
    
    @Override
    public SoloRoadCache getSoloRoadCache() {
        return this.soloRoadCache;
    }
    
    @Override
    public IScenarioNpcDao getScenarioNpcDao() {
        return this.scenarioNpcDao;
    }
    
    @Override
    public WdSjpHyCache getWdSjpHyCache() {
        return this.wdSjpHyCache;
    }
    
    @Override
    public WdSjpCache getWdSjpCache() {
        return this.wdSjpCache;
    }
    
    @Override
    public WdSjpGemCache getWdSjpGemCache() {
        return this.wdSjpGemCache;
    }
    
    @Override
    public SoloRewardCache getSoloRewardCache() {
        return this.soloRewardCache;
    }
    
    public void setSoloEventCache(final SoloEventCache soloEventCache) {
        this.soloEventCache = soloEventCache;
    }
    
    @Override
    public SoloEventCache getSoloEventCache() {
        return this.soloEventCache;
    }
    
    @Override
    public KtSdmzSCache getKtSdmzSCache() {
        return this.ktSdmzSCache;
    }
    
    @Override
    public ISlaveholderDao getSlaveholderDao() {
        return this.slaveholderDao;
    }
    
    @Override
    public WnCitynpcLvCache getWnCitynpcLvCache() {
        return this.wnCitynpcLvCache;
    }
    
    @Override
    public IPlayerScoreRankDao getPlayerScoreRankDao() {
        return this.playerScoreRankDao;
    }
    
    @Override
    public IPlayerWizardDao getPlayerWizardDao() {
        return this.playerWizardDao;
    }
    
    @Override
    public HmPwMainCache getHmPwMainCache() {
        return this.hmPwMainCache;
    }
    
    @Override
    public HmPwCritCache getHmPwCritCache() {
        return this.hmPwCritCache;
    }
    
    @Override
    public INationService getNationService() {
        return this.nationService;
    }
    
    @Override
    public IPhantomService getPhantomService() {
        return this.phantomService;
    }
    
    @Override
    public CdExamsCache getCdExamsCache() {
        return this.cdExamsCache;
    }
    
    @Override
    public IProtectService getProtectService() {
        return this.protectService;
    }
    
    @Override
    public IFeatService getFeatService() {
        return this.featService;
    }
    
    @Override
    public FbGuideCache getFbGuideCache() {
        return this.fbGuideCache;
    }
    
    @Override
    public IPowerService getPowerService() {
        return this.powerService;
    }
    
    @Override
    public IPlayerIncenseDao getPlayerIncenseDao() {
        return this.playerIncenseDao;
    }
    
    @Override
    public IPlayerOfficeRelativeDao getPlayerOfficeRelativeDao() {
        return this.playerOfficeRelativeDao;
    }
    
    @Override
    public IPlayerPoliticsEventDao getPlayerPoliticsEventDao() {
        return this.playerPoliticsEventDao;
    }
    
    @Override
    public YellowTurbansDao getYellowTurbansDao() {
        return this.yellowTurbansDao;
    }
    
    @Override
    public IPlayerBlacksmithDao getPlayerBlacksmithDao() {
        return this.playerBlacksmithDao;
    }
    
    @Override
    public WdSjpXtysCache getWdSjpXtysCache() {
        return this.wdSjpXtysCache;
    }
    
    @Override
    public ChatWordsCache getChatWordsCache() {
        return this.chatWordsCache;
    }
    
    @Override
    public EtiqueteEventCache getEtiqueteEventCache() {
        return this.etiqueteEventCache;
    }
    
    @Override
    public EtiquetePointCache getEtiquetePointCache() {
        return this.etiquetePointCache;
    }
    
    @Override
    public IPlayerLiYiDao getPlayerLiYiDao() {
        return this.playerLiYiDao;
    }
    
    @Override
    public ICourtesyService getCourtesyService() {
        return this.courtesyService;
    }
    
    @Override
    public KtCoNpcCache getKtCoNpcCache() {
        return this.ktCoNpcCache;
    }
    
    @Override
    public IPlayerTicketsDao getPlayerTicketsDao() {
        return this.playerTicketsDao;
    }
    
    public void setHuiZhanService(final IHuiZhanService huiZhanService) {
        this.huiZhanService = huiZhanService;
    }
    
    @Override
    public KtMrCache getKtMrCache() {
        return this.ktMrCache;
    }
    
    @Override
    public IHuiZhanService getHuiZhanService() {
        return this.huiZhanService;
    }
    
    public void setKtMrCache(final KtMrCache ktMrCache) {
        this.ktMrCache = ktMrCache;
    }
    
    public void setHuizhanHistoryDao(final IHuizhanHistoryDao huizhanHistoryDao) {
        this.huizhanHistoryDao = huizhanHistoryDao;
    }
    
    @Override
    public IHuizhanHistoryDao getHuizhanHistoryDao() {
        return this.huizhanHistoryDao;
    }
    
    public void setPlayerHuizhanDao(final IPlayerHuizhanDao playerHuizhanDao) {
        this.playerHuizhanDao = playerHuizhanDao;
    }
    
    @Override
    public IPlayerHuizhanDao getPlayerHuizhanDao() {
        return this.playerHuizhanDao;
    }
    
    @Override
    public IJuBenDataCache getJuBenDataCache() {
        return this.juBenDataCache;
    }
    
    public void setJuBenDataCache(final IJuBenDataCache juBenDataCache) {
        this.juBenDataCache = juBenDataCache;
    }
    
    @Override
    public IPlayerEventDao getPlayerEventDao() {
        return this.playerEventDao;
    }
    
    @Override
    public IActivityNpcDao getActivityNpcDao() {
        return this.activityNpcDao;
    }
    
    @Override
    public FstNdEventCache getFstNdEventCache() {
        return this.fstNdEventCache;
    }
    
    @Override
    public FstDbLveCache getFstDbLveCache() {
        return this.fstDbLveCache;
    }
    
    @Override
    public ITreasureService getTreasureService() {
        return this.treasureService;
    }
    
    @Override
    public IEventService getEventService() {
        return this.eventService;
    }
    
    @Override
    public IPlayerResourceAdditionDao getPlayerResourceAdditionDao() {
        return this.playerResourceAdditionDao;
    }
    
    @Override
    public IPlayerIncenseWeaponEffectDao getPlayerIncenseWeaponEffectDao() {
        return this.playerIncenseWeaponEffectDao;
    }
    
    @Override
    public IPlayerQuenchingRelativeDao getPlayerQuenchingRelativeDao() {
        return this.playerQuenchingRelativeDao;
    }
    
    @Override
    public IKfzbSeasonService getKfzbSeasonService() {
        return this.kfzbSeasonService;
    }
    
    @Override
    public IKfzbMatchService getKfzbMatchService() {
        return this.kfzbMatchService;
    }
    
    @Override
    public IKfzbInfoDao getKfzbInfoDao() {
        return this.kfzbInfoDao;
    }
    
    @Override
    public IKfzbRewardDao getKfzbRewardDao() {
        return this.kfzbRewardDao;
    }
    
    @Override
    public IKfzbSignupDao getKfzbSignupDao() {
        return this.kfzbSignupDao;
    }
    
    @Override
    public IBlacksmithService getBlacksmithService() {
        return this.blacksmithService;
    }
    
    @Override
    public EquipSkillCache getEquipSkillCache() {
        return this.equipSkillCache;
    }
    
    @Override
    public IKfgzSignupDao getKfgzSignupDao() {
        return this.kfgzSignupDao;
    }
    
    @Override
    public KfzbSupportDao getKfzbSupportDao() {
        return this.kfzbSupportDao;
    }
    
    @Override
    public IPayService getPayService() {
        return this.payService;
    }
    
    @Override
    public GeneralTreasureCache getGeneralTreasureCache() {
        return this.generalTreasureCache;
    }
    
    @Override
    public IDiamondShopService getDiamondShopService() {
        return this.diamondShopService;
    }
    
    @Override
    public IPlayerDiamondShopDao getPlayerDiamondShopDao() {
        return this.playerDiamondShopDao;
    }
    
    @Override
    public IFeatBuildingDao getFeatBuildingDao() {
        return this.featBuildingDao;
    }
    
    @Override
    public IPlayerDinnerDao getPlayerDinnerDao() {
        return this.playerDinnerDao;
    }
    
    @Override
    public WdSjpSdlrCache getWdSjpSdlrCache() {
        return this.wdSjpSdlrCache;
    }
    
    @Override
    public INationInfoDao getNationInfoDao() {
        return this.nationInfoDao;
    }
    
    @Override
    public KtNfCache getKtNfCache() {
        return this.ktNfCache;
    }
    
    @Override
    public IAutoBattleService getAutoBattleService() {
        return this.autoBattleService;
    }
    
    @Override
    public HmGtDropCache getHmGtDropCache() {
        return this.hmGtDropCache;
    }
}
