package com.reign.gcld.battle.scene;

import com.reign.gcld.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import java.util.*;
import com.reign.gcld.general.domain.*;

public class ScenarioOneToOneBuilder extends ScenarioBuilder
{
    public ScenarioOneToOneBuilder(final int battleType) {
        super(battleType);
    }
    
    @Override
    public int getGeneralState() {
        return 20;
    }
    
    @Override
    public void endBattle(final int winSide, final IDataGetter dataGetter, final Battle oneToOneBattle) {
        final boolean attWin = winSide == 3;
        if (attWin && oneToOneBattle.getDefBaseInfo().getNum() != 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("att Win\uff0c\u5b88\u65b9\u5175\u529b\u4e0d\u4e3a0").appendClassName("ScenarioOneToOneBuilder").appendMethodName("endBattle").append("battleId", oneToOneBattle.getBattleId()).append("defLeftForce", oneToOneBattle.getDefBaseInfo().getNum()).flush();
        }
        else if (!attWin && oneToOneBattle.getAttBaseInfo().getNum() != 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("def Win\uff0c\u653b\u65b9\u5175\u529b\u4e0d\u4e3a0 ").appendClassName("ScenarioOneToOneBuilder").appendMethodName("endBattle").append("battleId", oneToOneBattle.getBattleId()).append("attLeftForce", oneToOneBattle.getAttBaseInfo().getNum()).flush();
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
            ErrorSceneLog.getInstance().appendErrorMsg("oneToOneBattle ended, winCa is null.").appendBattleId(oneToOneBattle.getBattleId()).append("city", ((WorldCity)dataGetter.getWorldCityCache().get((Object)cityId)).getName()).append("cityId", cityId).flush();
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
        if (winCa.isPhantom && winCa.armyHpLoss > 0) {
            dataGetter.getPlayerGeneralMilitaryPhantomDao().updateHp(winCa.pgmVId, winCa.armyHpOrg - winCa.armyHpLoss);
        }
        int playerId = 0;
        if (oneToOneBattle.attBaseInfo.id > 0) {
            playerId = oneToOneBattle.attBaseInfo.id;
        }
        else {
            if (oneToOneBattle.defBaseInfo.id <= 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("oneToOneBattle BaseInfo error").appendBattleId(oneToOneBattle.battleId).append("oneToOneBattle.attBaseInfo.id", oneToOneBattle.attBaseInfo.id).append("oneToOneBattle.defBaseInfo.id", oneToOneBattle.defBaseInfo.id).flush();
                return;
            }
            playerId = oneToOneBattle.defBaseInfo.id;
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        final int capitalId = juBenDto.capital;
        final JuBenCityDto juBenCityDto = JuBenManager.getInstance().getJuBenCityDto(playerId, cityId);
        final Battle scenarioBattle = NewBattleManager.getInstance().getBattleByBatId(juBenCityDto.battleId);
        if (scenarioBattle == null) {
            if (juBenCityDto.forceId == winCa.getForceId()) {
                dataGetter.getPlayerGeneralMilitaryDao().updateStateByPidAndGid(winCa.getPlayerId(), winCa.getGeneralId(), 1, new Date());
            }
            else if (winCa.getPlayerId() > 0 && !winCa.isPhantom) {
                dataGetter.getPlayerGeneralMilitaryDao().moveJuben(winCa.playerId, winCa.generalId, 1, capitalId);
            }
        }
        else {
            boolean isJoin = false;
            winCa.setArmyHp(winCa.getArmyHpOrg() - winCa.getArmyHpLoss());
            winCa.setArmyHpLoss(0);
            winCa.setArmyHpKill(0);
            winCa.setArmyHpOrg(winCa.getArmyHp());
            winCa.setOnQueues(false);
            if (scenarioBattle.getDefBaseInfo().getForceId() == winCa.getForceId()) {
                isJoin = scenarioBattle.joinOneVsOneBackCA(winCa.getPlayerId(), 0, winCa, dataGetter, 19);
            }
            else {
                isJoin = scenarioBattle.joinOneVsOneBackCA(winCa.getPlayerId(), 1, winCa, dataGetter, 19);
            }
            if (winCa.getPlayerId() > 0 && !winCa.isPhantom) {
                if (!isJoin) {
                    dataGetter.getPlayerGeneralMilitaryDao().moveJuben(winCa.playerId, winCa.generalId, 1, capitalId);
                }
                dataGetter.getGeneralService().sendGeneralMilitaryRecruitInfo(winCa.getPlayerId(), winCa.getGeneralId());
            }
        }
        this.endBattleMsg(attWin, new BattleResult(), oneToOneBattle, dataGetter);
        NewBattleManager.getInstance().deleteBattle(oneToOneBattle.getBattleId());
    }
    
    @Override
    public void checkErrorAndHandle(final IDataGetter dataGetter, final Battle battle) {
        for (final CampArmy campArmy : battle.attCamp) {
            if (campArmy.playerId > 0 && campArmy.updateDB && !campArmy.isPhantom) {
                final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(campArmy.playerId, campArmy.generalId);
                if (pgm.getState() == this.getGeneralState()) {
                    continue;
                }
                dataGetter.getPlayerGeneralMilitaryDao().updateState(pgm.getVId(), this.getGeneralState());
                dataGetter.getGeneralService().sendGmStateSet(pgm.getPlayerId(), pgm.getGeneralId(), this.getGeneralState());
            }
        }
        for (final CampArmy campArmy : battle.defCamp) {
            if (campArmy.playerId > 0 && campArmy.updateDB && !campArmy.isPhantom) {
                final PlayerGeneralMilitary pgm = dataGetter.getPlayerGeneralMilitaryDao().getMilitary(campArmy.playerId, campArmy.generalId);
                if (pgm.getState() == this.getGeneralState()) {
                    continue;
                }
                dataGetter.getPlayerGeneralMilitaryDao().updateState(pgm.getVId(), this.getGeneralState());
                dataGetter.getGeneralService().sendGmStateSet(pgm.getPlayerId(), pgm.getGeneralId(), this.getGeneralState());
            }
        }
    }
}
