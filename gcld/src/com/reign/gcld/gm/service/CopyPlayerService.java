package com.reign.gcld.gm.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.task.dao.*;
import com.reign.gcld.tavern.dao.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.treasure.dao.*;
import com.reign.gcld.weapon.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.gift.dao.*;
import com.reign.gcld.grouparmy.dao.*;
import com.reign.gcld.market.dao.*;
import com.reign.gcld.building.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.slave.dao.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.gift.domain.*;
import com.reign.gcld.grouparmy.domain.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.treasure.domain.*;
import com.reign.gcld.weapon.domain.*;
import com.reign.framework.json.*;
import java.text.*;
import java.util.*;
import com.reign.gcld.market.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.tavern.domain.*;
import com.reign.gcld.slave.domain.*;
import com.reign.gcld.world.domain.*;
import org.springframework.transaction.annotation.*;

@Component("copyPlayerService")
public class CopyPlayerService implements ICopyPlayerService
{
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerArmyDao playerArmyDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerBuildingDao playerBuildingDao;
    @Autowired
    private IPlayerBuildingWorkDao playerBuildingWorkDao;
    @Autowired
    private IPlayerConstantsDao playerConstantsDao;
    @Autowired
    private IPlayerGeneralDao playerGeneralDao;
    @Autowired
    private IPlayerGeneralCivilDao playerGeneralCivilDao;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IPlayerGeneralRefreshDao playerGeneralRefreshDao;
    @Autowired
    private IPlayerItemRefreshDao playerItemRefreshDao;
    @Autowired
    private IPlayerNameDao playerNameDao;
    @Autowired
    private IPlayerPowerDao playerPowerDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerSearchDao playerSearchDao;
    @Autowired
    private IPlayerStoreDao playerStoreDao;
    @Autowired
    private IPlayerTaskDao playerTaskDao;
    @Autowired
    private IPlayerTavernDao playerTavernDao;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private IPlayerTreasureDao playerTreasureDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private IPlayerWeaponDao playerWeaponDao;
    @Autowired
    private IPlayerBakDao playerBakDao;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private IPlayerBattleAutoDao playerBattleAutoDao;
    @Autowired
    private IPlayerBattleRewardDao playerBattleRewardDao;
    @Autowired
    private IPlayerBatRankDao playerBatRankDao;
    @Autowired
    private IPlayerGiftDao playerGiftDao;
    @Autowired
    private IPlayerGroupArmyDao playerGroupArmyDao;
    @Autowired
    private IPlayerKillInfoDao playerKillInfoDao;
    @Autowired
    private IPlayerKillRewardDao playerKillRewardDao;
    @Autowired
    private IPlayerMarketDao playerMarketDao;
    @Autowired
    private IPlayerOfficerBuildingDao playerOfficerBuildingDao;
    @Autowired
    private IPlayerResourceAdditionDao playerResourceAdditionDao;
    @Autowired
    private IPlayerWorldDao playerWorldDao;
    @Autowired
    private IStoreHouseSellDao storeHourseSellDao;
    @Autowired
    private ISlaveholderDao slaveholderDao;
    @Autowired
    private IPlayerArmyExtraDao playerArmyExtraDao;
    @Autowired
    private IPlayerArmyRewardDao playerArmyRewardDao;
    @Autowired
    private IWholeKillDao wholeKillDao;
    
    @Transactional
    @Override
    public byte[] copyPlayerTable(final int playerId, final int num) {
        final Player player = this.playerDao.read(playerId);
        final Date date = new Date();
        final DateFormat df = new SimpleDateFormat("MMddhhmmss");
        final String playerName = df.format(new Date());
        String realplayerName = null;
        for (int i = 0; i < num; ++i) {
            realplayerName = String.valueOf(playerId) + "Q" + playerName + i + 1;
            player.setPlayerId(0);
            player.setPlayerName(realplayerName);
            this.playerDao.create(player);
            final int copyPlayerId = player.getPlayerId();
            this.playerDao.updatePlayerName(copyPlayerId, String.valueOf(playerId) + "N" + copyPlayerId);
            final List<PlayerArmy> paList = this.playerArmyDao.getPlayerArmyList(playerId);
            for (final PlayerArmy playerArmy : paList) {
                playerArmy.setVId(0);
                playerArmy.setPlayerId(copyPlayerId);
                playerArmy.setFirstOpen(1);
                playerArmy.setDropCount(0);
                this.playerArmyDao.create(playerArmy);
            }
            final List<PlayerArmyExtra> paeList = this.playerArmyExtraDao.getListByPlayerId(playerId);
            for (final PlayerArmyExtra pae : paeList) {
                pae.setVId(null);
                pae.setPlayerId(copyPlayerId);
                this.playerArmyExtraDao.create(pae);
            }
            final List<PlayerArmyReward> parList = this.playerArmyRewardDao.getListByPlayerId(playerId);
            for (final PlayerArmyReward par : parList) {
                par.setVId(null);
                par.setPlayerId(copyPlayerId);
                this.playerArmyRewardDao.create(par);
            }
            final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
            pa.setPlayerId(copyPlayerId);
            this.playerAttributeDao.create(pa);
            final PlayerBak pb = this.playerBakDao.read(playerId);
            if (pb != null) {
                pb.setPlayerId(copyPlayerId);
                this.playerBakDao.create(pb);
            }
            final PlayerBattleAttribute pba = this.playerBattleAttributeDao.read(playerId);
            if (pba != null) {
                pba.setPlayerId(copyPlayerId);
                this.playerBattleAttributeDao.create(pba);
            }
            final PlayerBattleAuto pba2 = this.playerBattleAutoDao.read(playerId);
            if (pba2 != null) {
                pba2.setPlayerId(copyPlayerId);
                this.playerBattleAutoDao.create(pba2);
            }
            final List<PlayerBattleReward> pbrList = this.playerBattleRewardDao.getListByPlayerId(playerId);
            for (final PlayerBattleReward pbr : pbrList) {
                pbr.setVId(0);
                this.playerBattleRewardDao.create(pbr);
            }
            final PlayerBatRank pbrr = this.playerBatRankDao.read(playerId);
            if (pbrr != null) {
                pbrr.setPlayerId(copyPlayerId);
                this.playerBatRankDao.create(pbrr);
            }
            final List<PlayerBuilding> pbList = this.playerBuildingDao.getPlayerBuildings(playerId);
            for (final PlayerBuilding playerBuilding : pbList) {
                playerBuilding.setVId(0);
                playerBuilding.setPlayerId(copyPlayerId);
                playerBuilding.setState(0);
                this.playerBuildingDao.create(playerBuilding);
            }
            final List<PlayerBuildingWork> pbwList = this.playerBuildingWorkDao.getPlayerBuildingWork(playerId);
            for (final PlayerBuildingWork pbw : pbwList) {
                pbw.setVId(0);
                pbw.setPlayerId(copyPlayerId);
                pbw.setStartTime(date);
                pbw.setEndTime(date);
                pbw.setTargetBuildId(0);
                pbw.setWorkState(0);
                pbw.setTaskId(null);
                this.playerBuildingWorkDao.create(pbw);
            }
            final PlayerConstants playerConstants = this.playerConstantsDao.read(playerId);
            if (playerConstants != null) {
                playerConstants.setPlayerId(copyPlayerId);
                this.playerConstantsDao.create(playerConstants);
            }
            final List<PlayerGeneral> pgList = this.playerGeneralDao.getGeneralList(playerId);
            for (final PlayerGeneral playerGeneral : pgList) {
                playerGeneral.setVId(0);
                playerGeneral.setPlayerId(copyPlayerId);
                this.playerGeneralDao.create(playerGeneral);
            }
            final List<PlayerGeneralCivil> pgcList = this.playerGeneralCivilDao.getCivilList(playerId);
            for (final PlayerGeneralCivil playerGeneralCivil : pgcList) {
                playerGeneralCivil.setVId(0);
                playerGeneralCivil.setPlayerId(copyPlayerId);
                playerGeneralCivil.setNextMoveTime(date);
                playerGeneralCivil.setState(0);
                playerGeneralCivil.setTaskId(null);
                this.playerGeneralCivilDao.create(playerGeneralCivil);
            }
            final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
            for (final PlayerGeneralMilitary playerGeneralMilitary : pgmList) {
                playerGeneralMilitary.setVId(0);
                playerGeneralMilitary.setPlayerId(copyPlayerId);
                playerGeneralMilitary.setState(0);
                this.playerGeneralMilitaryDao.create(playerGeneralMilitary);
            }
            final List<PlayerGeneralRefresh> pgrList = this.playerGeneralRefreshDao.getListByPlayerId(playerId);
            for (final PlayerGeneralRefresh playerGeneralRefresh : pgrList) {
                playerGeneralRefresh.setVId(0);
                playerGeneralRefresh.setPlayerId(copyPlayerId);
                this.playerGeneralRefreshDao.create(playerGeneralRefresh);
            }
            final List<PlayerGift> pgList2 = this.playerGiftDao.getAllGiftByPlayerId(playerId);
            for (final PlayerGift pg : pgList2) {
                pg.setId(0);
                pg.setPlayerId(copyPlayerId);
                this.playerGiftDao.create(pg);
            }
            final Map<Integer, PlayerGroupArmy> pgaMap = this.playerGroupArmyDao.getGroupArmies(playerId);
            for (final PlayerGroupArmy pga : pgaMap.values()) {
                pga.setVId(0);
                pga.setPlayerId(copyPlayerId);
                pga.setIsLeader(0);
            }
            final List<PlayerItemRefresh> pirList = this.playerItemRefreshDao.getListByPlayerId(playerId);
            for (final PlayerItemRefresh playerItemRefresh : pirList) {
                playerItemRefresh.setVId(0);
                playerItemRefresh.setPlayerId(copyPlayerId);
                playerItemRefresh.setRefreshAttribute("");
                this.playerItemRefreshDao.create(playerItemRefresh);
            }
            final List<PlayerKillInfo> pkiList = this.playerKillInfoDao.getListByPlayerId(playerId);
            for (final PlayerKillInfo pki : pkiList) {
                pki.setVId(0);
                pki.setPlayerId(copyPlayerId);
                this.playerKillInfoDao.create(pki);
            }
            final PlayerKillReward pkr = this.playerKillRewardDao.read(playerId);
            if (pkr != null) {
                pkr.setPlayerId(copyPlayerId);
                this.playerKillRewardDao.create(pkr);
            }
            final PlayerMarket pm = this.playerMarketDao.read(playerId);
            if (pm != null) {
                pm.setPlayerId(copyPlayerId);
                this.playerMarketDao.create(pm);
            }
            final PlayerName pName = new PlayerName();
            pName.setPlayerName(String.valueOf(playerId) + "N" + copyPlayerId);
            this.playerNameDao.create(pName);
            final PlayerOfficerBuilding pob = this.playerOfficerBuildingDao.read(playerId);
            if (pob != null) {
                pob.setIsLeader(0);
                pob.setState(0);
                pob.setPlayerId(copyPlayerId);
                this.playerOfficerBuildingDao.create(pob);
            }
            final List<PlayerPower> ppList = this.playerPowerDao.getPlayerPowers(playerId);
            for (final PlayerPower playerPower : ppList) {
                playerPower.setVId(0);
                playerPower.setPlayerId(copyPlayerId);
                this.playerPowerDao.create(playerPower);
            }
            final PlayerResource playerResource = this.playerResourceDao.read(playerId);
            if (playerResource != null) {
                playerResource.setPlayerId(copyPlayerId);
                this.playerResourceDao.create(playerResource);
            }
            final List<PlayerResourceAddition> praList = this.playerResourceAdditionDao.getListByPlayerId(playerId);
            for (final PlayerResourceAddition pra : praList) {
                pra.setVId(0);
                pra.setPlayerId(copyPlayerId);
                pra.setTaskId(null);
                this.playerResourceAdditionDao.create(pra);
            }
            final PlayerSearch playerSearch = this.playerSearchDao.read(playerId);
            if (playerSearch != null) {
                playerSearch.setPlayerId(copyPlayerId);
                this.playerSearchDao.create(playerSearch);
            }
            final PlayerStore playerStore = this.playerStoreDao.read(playerId);
            if (playerStore != null) {
                playerStore.setPlayerId(copyPlayerId);
                this.playerStoreDao.create(playerStore);
            }
            final List<PlayerTask> ptList = this.playerTaskDao.getPlayerTasks(playerId);
            for (final PlayerTask playerTask : ptList) {
                playerTask.setVId(0);
                playerTask.setPlayerId(copyPlayerId);
                this.playerTaskDao.create(playerTask);
            }
            final PlayerTavern playerTavern = this.playerTavernDao.read(playerId);
            if (playerTavern != null) {
                playerTavern.setPlayerId(copyPlayerId);
                this.playerTavernDao.create(playerTavern);
            }
            final List<PlayerTech> playerTechList = this.playerTechDao.getPlayerTechList(playerId);
            for (final PlayerTech playerTech : playerTechList) {
                playerTech.setVId(0);
                playerTech.setPlayerId(copyPlayerId);
                this.playerTechDao.create(playerTech);
            }
            final List<PlayerTreasure> playerTreasureList = this.playerTreasureDao.getPlayerTreasures(playerId);
            for (final PlayerTreasure playerTreasure : playerTreasureList) {
                playerTreasure.setVId(0);
                playerTreasure.setPlayerId(copyPlayerId);
                this.playerTreasureDao.create(playerTreasure);
            }
            final List<PlayerWeapon> playerWeaponList = this.playerWeaponDao.getPlayerWeapons(playerId);
            for (final PlayerWeapon layerWeapon : playerWeaponList) {
                layerWeapon.setVId(0);
                layerWeapon.setPlayerId(copyPlayerId);
                this.playerWeaponDao.create(layerWeapon);
            }
            final PlayerWorld pw = this.playerWorldDao.read(playerId);
            if (pw != null) {
                pw.setPlayerId(copyPlayerId);
                this.playerWorldDao.create(pw);
            }
            final Slaveholder sh = this.slaveholderDao.read(playerId);
            if (sh != null) {
                sh.setPlayerId(copyPlayerId);
            }
            final List<StoreHouse> storeHouseList = this.storeHouseDao.getGernerlEquip(playerId);
            for (final StoreHouse storeHouse : storeHouseList) {
                storeHouse.setVId(0);
                storeHouse.setPlayerId(copyPlayerId);
                storeHouse.setRefreshAttribute("");
                storeHouse.setQuenchingTimes(0);
                storeHouse.setBindExpireTime(0L);
                storeHouse.setMarkId(0);
                this.storeHouseDao.create(storeHouse);
            }
            final List<StoreHouseSell> shsList = this.storeHourseSellDao.getByPlayerId(playerId);
            for (final StoreHouseSell shs : shsList) {
                shs.setVId(0);
                shs.setPlayerId(copyPlayerId);
                shs.setRefreshAttribute("");
                shs.setQuenchingTimes(0);
                this.storeHourseSellDao.create(shs);
            }
            final WholeKill wholeKill = this.wholeKillDao.read(playerId);
            if (wholeKill != null) {
                wholeKill.setPlayerId(copyPlayerId);
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
}
