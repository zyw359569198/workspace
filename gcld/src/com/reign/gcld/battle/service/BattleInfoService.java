package com.reign.gcld.battle.service;

import org.springframework.stereotype.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.battle.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.log.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.world.common.*;
import java.util.*;
import com.reign.gcld.huizhan.domain.*;
import com.reign.gcld.rank.domain.*;

@Component("battleInfoService")
public class BattleInfoService implements IBattleInfoService
{
    protected static final Logger battleLog;
    @Autowired
    private IBattleInfoDao battleInfoDao;
    @Autowired
    private IDataGetter dataGetter;
    
    static {
        battleLog = new BattleLogger();
    }
    
    @Override
    public void saveBattle(final Battle bat) {
        if (bat.getBattleType() != 3 && bat.getBattleType() != 14) {
            return;
        }
        int i = 0;
        while (i < 3) {
            try {
                final BattleInfo battleInfo = new BattleInfo();
                battleInfo.setBattleId(bat.getBattleId());
                battleInfo.setAttForceId(bat.getAttBaseInfo().getForceId());
                this.battleInfoDao.createBattle(battleInfo);
                break;
            }
            catch (Exception e) {
                WorldSceneLog.getInstance().appendLogMsg("Save battleInfo Exception try three times").appendBattleId(bat.getBattleId()).appendMethodName("saveBattleMirror").newLine().flush();
                ErrorSceneLog.getInstance().appendErrorMsg("Save battleInfo Exception try three times").appendBattleId(bat.getBattleId()).appendMethodName("saveBattleMirror").flush();
                ErrorSceneLog.getInstance().error("BattleInfoService saveBattle ", e);
                ++i;
            }
        }
    }
    
    @Override
    public void deleteBattle(final Battle bat) {
        int i = 0;
        while (i < 3) {
            try {
                this.battleInfoDao.deleteByBattleId(bat.getBattleId());
                break;
            }
            catch (Exception e) {
                WorldSceneLog.getInstance().appendLogMsg("delete battleInfo Exception after try three times").appendBattleId(bat.getBattleId()).appendMethodName("deleteBattle").newLine().flush();
                ErrorSceneLog.getInstance().appendErrorMsg("delete battleInfo Exception after try three times").appendBattleId(bat.getBattleId()).appendMethodName("deleteBattle").flush();
                ErrorSceneLog.getInstance().error("BattleInfoService deleteBattle ", e);
                ++i;
            }
        }
    }
    
    @Override
    public void recoverCountryLevelUpBattles() {
        try {
            int forceId = 1;
            int cityId = 251;
            int degree = this.dataGetter.getRankService().hasBarTasks(forceId);
            if (degree > 0) {
                this.recoverCountryLevelUpBattlesForOne(forceId, cityId);
            }
            forceId = 2;
            cityId = 250;
            degree = this.dataGetter.getRankService().hasBarTasks(forceId);
            if (degree > 0) {
                this.recoverCountryLevelUpBattlesForOne(forceId, cityId);
            }
            forceId = 3;
            cityId = 252;
            degree = this.dataGetter.getRankService().hasBarTasks(forceId);
            if (degree > 0) {
                this.recoverCountryLevelUpBattlesForOne(forceId, cityId);
            }
        }
        catch (Exception e) {
            WorldSceneLog.getInstance().error("BattleInfoService.recoverCountryLevelUpBattles catch Exception.", e);
        }
    }
    
    private void recoverCountryLevelUpBattlesForOne(final int forceId, final int cityId) {
        try {
            final int battleType = 14;
            final Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
            final String battleId = CityService.cityBatIdSet.get(cityId);
            final Battle cityBattle = NewBattleManager.getInstance().createBattle(battleId);
            if (cityBattle == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("recover battle, create battle fail").appendBattleId(battleId).append("battleId", battleId).append("forceId", forceId).append("cityId", cityId).appendClassName("BattleInfoService").appendMethodName("recoverCountryLevelUpBattlesForOne").flush();
                return;
            }
            final BattleAttacker battleAttacker = new BattleAttacker();
            battleAttacker.attType = Integer.MAX_VALUE;
            battleAttacker.attForceId = forceId;
            battleAttacker.attPlayerId = -1;
            final Player attPlayer = new Player();
            attPlayer.setPlayerId(battleAttacker.attPlayerId);
            attPlayer.setForceId(forceId);
            battleAttacker.attPlayer = attPlayer;
            cityBattle.init(battleAttacker, battleType, cityId, this.dataGetter, false, terrain.getValue());
            builder.dealUniqueStaff(this.dataGetter, cityBattle, -1, cityId);
        }
        catch (Exception e) {
            WorldSceneLog.getInstance().error("BattleInfoService.recoverCountryLevelUpBattlesForOne catch Exception. forceId:" + forceId, e);
        }
    }
    
    @Override
    public Set<Integer> resetCityBattles(final Map<Integer, City> cityMap) {
        final Set<Integer> inWarCitySet = new HashSet<Integer>();
        final List<BattleInfo> biList = this.battleInfoDao.getModels();
        if (biList.size() == 0) {
            WorldSceneLog.getInstance().appendLogMsg("there is no BATTLE_CITY, nice.").flush();
            return inWarCitySet;
        }
        WorldSceneLog.getInstance().appendLogMsg("BATTLE_CITY reset start:").flush();
        for (final BattleInfo bi : biList) {
            WorldSceneLog.getInstance().appendBattleId(bi.getBattleId()).flush();
            try {
                final String[] params = bi.getBattleId().split("_");
                final int attForceId = bi.getAttForceId();
                final int cityId = Integer.parseInt(params[2]);
                final String battleId = CityService.cityBatIdSet.get(cityId);
                inWarCitySet.add(cityId);
                final City city = cityMap.get(cityId);
                final int defForceId = city.getForceId();
                final HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanInProcess();
                if ((hh == null || hh.getCityId() != cityId) && attForceId == defForceId) {
                    WorldSceneLog.getInstance().Indent().appendLogMsg("attForceId == defForceId").append("attForceId", attForceId).append("defForceId", defForceId).append("cityState", city.getState()).flush();
                }
                else {
                    WorldSceneLog.getInstance().Indent().appendCityId(cityId).append("attForceId", attForceId).append("defForceId", defForceId).append("cityState", city.getState()).flush();
                    int battleType = 3;
                    if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
                        battleType = 14;
                        final ForceInfo fi = this.dataGetter.getForceInfoDao().read(attForceId);
                        if (fi.getStage() >= 4) {
                            continue;
                        }
                    }
                    final Builder builder = BuilderFactory.getInstance().getBuilder(battleType);
                    final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
                    final Terrain terrain = builder.getTerrain(-1, wc.getId(), this.dataGetter);
                    final Battle cityBattle = NewBattleManager.getInstance().createBattle(battleId);
                    if (cityBattle == null) {
                        ErrorSceneLog.getInstance().appendErrorMsg("recover battle, create battle fail").appendBattleId(battleId).append("attForceId", attForceId).append("defForceId", defForceId).append("cityId", cityId).appendClassName("BattleInfoService").appendMethodName("resetCityBattles").flush();
                    }
                    else {
                        final BattleAttacker battleAttacker = new BattleAttacker();
                        battleAttacker.attType = Integer.MAX_VALUE;
                        battleAttacker.attForceId = attForceId;
                        battleAttacker.attPlayerId = -1;
                        final Player attPlayer = new Player();
                        attPlayer.setPlayerId(battleAttacker.attPlayerId);
                        attPlayer.setForceId(attForceId);
                        battleAttacker.attPlayer = attPlayer;
                        cityBattle.init(battleAttacker, battleType, cityId, this.dataGetter, false, terrain.getValue());
                        builder.dealUniqueStaff(this.dataGetter, cityBattle, -1, cityId);
                    }
                }
            }
            catch (Exception e) {
                WorldSceneLog.getInstance().error("battle reset catch Exception. battleId=" + bi.getBattleId(), e);
            }
        }
        return inWarCitySet;
    }
}
