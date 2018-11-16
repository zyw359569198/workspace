package com.reign.gcld.battle.scene;

import com.reign.gcld.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.world.domain.*;

public class CityOneToOneBuilder extends CityBuilder
{
    public CityOneToOneBuilder(final int battleType) {
        super(battleType);
    }
    
    @Override
    public int getGeneralState() {
        return 13;
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    @Override
    public void endBattle(final int winSide, final IDataGetter dataGetter, final Battle oneToOneBattle) {
        final boolean attWin = winSide == 3;
        if (attWin && oneToOneBattle.getDefBaseInfo().getNum() != 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("att Win\uff0c\u5b88\u65b9\u5175\u529b\u4e0d\u4e3a0").appendClassName("Builder").appendMethodName("endBattle").append("battleId", oneToOneBattle.getBattleId()).append("defLeftForce", oneToOneBattle.getDefBaseInfo().getNum()).flush();
        }
        else if (!attWin && oneToOneBattle.getAttBaseInfo().getNum() != 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("def Win\uff0c\u653b\u65b9\u5175\u529b\u4e0d\u4e3a0 ").appendClassName("Builder").appendMethodName("endBattle").append("battleId", oneToOneBattle.getBattleId()).append("attLeftForce", oneToOneBattle.getAttBaseInfo().getNum()).flush();
        }
        final int cityId = oneToOneBattle.getDefBaseInfo().getId();
        CampArmy winCa = null;
        if (winSide == 2) {
            winCa = oneToOneBattle.campArmyDef;
            if (oneToOneBattle.getDefCamp().size() == 0) {
                BattleSceneLog.getInstance().error("CityOneToOneBuilder error armyHp:" + oneToOneBattle.campArmyDef.getArmyHp() + " armyHpLoss:" + oneToOneBattle.campArmyDef.getArmyHpLoss() + " armyHpOrg:" + oneToOneBattle.campArmyDef.armyHpOrg + " armyHpKill:" + oneToOneBattle.campArmyDef.armyHpKill);
            }
            if (winCa.getPlayerId() > 0 && !winCa.isPhantom) {
                BattleSceneLog.getInstance().info("#batId:" + oneToOneBattle.getBattleId() + "_" + oneToOneBattle.getStartTime() + "#quit:1Vs1Loss#side:att" + "#playerId:" + winCa.getPlayerId() + ":" + winCa.isPhantom + "#general:" + winCa.getGeneralId() + "#defSize:" + oneToOneBattle.getDefCamp().size());
                NewBattleManager.getInstance().quitBattle(oneToOneBattle, winCa.getPlayerId(), winCa.getGeneralId());
            }
        }
        else if (winSide == 3) {
            if (oneToOneBattle.getAttCamp().size() > 0) {
                winCa = oneToOneBattle.getAttCamp().get(0);
            }
            else {
                winCa = oneToOneBattle.campArmyAtt;
            }
            if (winCa.getPlayerId() > 0 && !winCa.isPhantom) {
                BattleSceneLog.getInstance().info("#batId:" + oneToOneBattle.getBattleId() + "_" + oneToOneBattle.getStartTime() + "#quit:1Vs1Loss#side:def" + "#playerId:" + winCa.getPlayerId() + ":" + winCa.isPhantom + "#general:" + winCa.getGeneralId() + "#attSize:" + oneToOneBattle.getAttCamp().size());
                NewBattleManager.getInstance().quitBattle(oneToOneBattle, winCa.getPlayerId(), winCa.getGeneralId());
            }
        }
        if (winCa == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("oneToOneBattle ended, winCa is null.").appendBattleId(oneToOneBattle.getBattleId()).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId);
            this.endBattleMsg(true, new BattleResult(), oneToOneBattle, dataGetter);
            return;
        }
        try {
            if (winCa.playerId > 0 && !winCa.isPhantom) {
                dataGetter.getRankService().updatePlayerChallengeInfo(winCa.playerId, winCa.generalId, 1);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("ivoke updatePlayerChallengeInfo failed , playerId :" + winCa.playerId + "generalId :" + winCa.generalId).appendBattleId(oneToOneBattle.getBattleId()).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId);
        }
        final Battle cityBattle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (winCa.isPhantom && winCa.armyHpLoss > 0) {
            dataGetter.getPlayerGeneralMilitaryPhantomDao().updateHp(winCa.pgmVId, winCa.armyHpOrg - winCa.armyHpLoss);
        }
        final int armyHpKill = winCa.getArmyHpKill();
        final int barbarainHpKill = winCa.barbarainHpKill;
        if (cityBattle == null) {
            final City city = dataGetter.getCityDao().read(cityId);
            if (city.getForceId() == winCa.getForceId()) {
                dataGetter.getPlayerGeneralMilitaryDao().updateStateByPidAndGid(winCa.getPlayerId(), winCa.getGeneralId(), 1, new Date());
            }
            else if (winCa.getPlayerId() > 0 && !winCa.isPhantom) {
                final int capitalId = WorldCityCommon.nationMainCityIdMap.get(winCa.getForceId());
                dataGetter.getPlayerGeneralMilitaryDao().updateStateLocationId(winCa.playerId, winCa.generalId, 1, capitalId);
                dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(winCa.getPlayerId(), winCa.getGeneralId());
            }
        }
        else {
            boolean isJoin = false;
            winCa.setArmyHp(winCa.getArmyHpOrg() - winCa.getArmyHpLoss());
            winCa.setArmyHpLoss(0);
            winCa.setArmyHpKill(0);
            winCa.setArmyHpOrg(winCa.getArmyHp());
            winCa.setOnQueues(false);
            winCa.barbarainHpKill = 0;
            winCa.setId(cityBattle.getCampNum().getAndIncrement());
            if (cityBattle.getDefBaseInfo().getForceId() == winCa.getForceId()) {
                isJoin = cityBattle.joinOneVsOneBackCA(winCa.getPlayerId(), 0, winCa, dataGetter, 3);
            }
            else {
                isJoin = cityBattle.joinOneVsOneBackCA(winCa.getPlayerId(), 1, winCa, dataGetter, 3);
            }
            if (winCa.getPlayerId() > 0 && !winCa.isPhantom) {
                if (!isJoin) {
                    final int capitalId = WorldCityCommon.nationMainCityIdMap.get(winCa.getForceId());
                    dataGetter.getPlayerGeneralMilitaryDao().updateStateLocationId(winCa.getPlayerId(), winCa.getGeneralId(), 1, capitalId);
                    dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(winCa.getPlayerId(), winCa.getGeneralId());
                }
                else {
                    dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(winCa.getPlayerId(), winCa.getGeneralId());
                }
            }
            if (winCa.isBarPhantom) {
                if (!isJoin) {
                    dataGetter.getBarbarainPhantomDao().updateState(winCa.pgmVId, 0);
                }
                else {
                    dataGetter.getBarbarainPhantomDao().updateState(winCa.pgmVId, 3);
                }
            }
        }
        if (winCa.isUpdateDB() && winCa.playerId > 0) {
            this.dealKillrank(false, armyHpKill, oneToOneBattle, dataGetter, winCa.getPlayerId());
            this.dealKillrank(true, barbarainHpKill, oneToOneBattle, dataGetter, winCa.getPlayerId());
        }
        this.endBattleMsg(attWin, new BattleResult(), oneToOneBattle, dataGetter);
        NewBattleManager.getInstance().deleteBattle(oneToOneBattle.getBattleId());
    }
}
