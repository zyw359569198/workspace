package com.reign.gcld.battle.scene;

import com.reign.gcld.common.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

public class BarbarainOneToOneBuilder extends BarbarainBuilder
{
    public BarbarainOneToOneBuilder(final int battleType) {
        super(battleType);
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
        CampArmy winCa = null;
        int batSide = 1;
        if (winSide == 2) {
            if (oneToOneBattle.getDefCamp().size() > 0 && oneToOneBattle.getDefCamp().get(0).isBarPhantom) {
                winCa = oneToOneBattle.getDefCamp().get(0);
                batSide = 0;
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
            this.endBattleMsg(false, new BattleResult(), oneToOneBattle, dataGetter);
            return;
        }
        for (final CampArmy campArmy : oneToOneBattle.attCamp) {
            if (campArmy.isUpdateDB() && campArmy.armyHpKill > 0) {
                final int forceId = campArmy.forceId;
                final int killAdd = campArmy.armyHpKill;
                NewBattleManager.getInstance().addBarbarainKill(forceId, killAdd);
                this.dealKillrank(false, campArmy.armyHpKill, oneToOneBattle, dataGetter, campArmy.getPlayerId());
            }
        }
        try {
            if (winCa.playerId > 0 && !winCa.isPhantom) {
                dataGetter.getRankService().updatePlayerChallengeInfo(winCa.playerId, winCa.generalId, 1);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("ivoke updatePlayerChallengeInfo failed , playerId :" + winCa.playerId + "generalId :" + winCa.generalId).appendBattleId(oneToOneBattle.getBattleId()).append("cityId", oneToOneBattle.getDefBaseInfo().id);
        }
        final Battle oldBattle = NewBattleManager.getInstance().getBattleByBatId(oneToOneBattle.oldBattleId);
        if (oldBattle == null) {
            if (winCa.playerId > 0 && !winCa.isPhantom) {
                final int capitalId = WorldCityCommon.nationMainCityIdMap.get(winCa.getForceId());
                dataGetter.getPlayerGeneralMilitaryDao().updateLocationForceSetState1(winCa.playerId, winCa.generalId, capitalId, winCa.armyHp, new Date());
            }
        }
        else {
            winCa.setArmyHp(winCa.getArmyHpOrg() - winCa.getArmyHpLoss());
            winCa.setArmyHpLoss(0);
            winCa.setArmyHpKill(0);
            winCa.setArmyHpOrg(winCa.getArmyHp());
            winCa.setOnQueues(false);
            final boolean isJoin = oldBattle.joinOneVsOneBackCA(winCa.getPlayerId(), batSide, winCa, dataGetter, 14);
            if (!winCa.isPhantom && winCa.getPlayerId() > 0) {
                if (!isJoin) {
                    final int capitalId2 = WorldCityCommon.nationMainCityIdMap.get(winCa.getForceId());
                    dataGetter.getPlayerGeneralMilitaryDao().updateStateLocationId(winCa.getPlayerId(), winCa.getGeneralId(), 1, capitalId2);
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
        this.endBattleMsg(attWin, new BattleResult(), oneToOneBattle, dataGetter);
        NewBattleManager.getInstance().deleteBattle(oneToOneBattle.getBattleId());
    }
    
    @Override
    public void dealNextNpc(final boolean attWin, final IDataGetter dataGetter, final Battle bat, final BattleResult battleResult) {
    }
    
    @Override
    public void getExReward(final boolean attWin, final IDataGetter dataGetter, final PlayerInfo pi, final Battle bat) {
    }
    
    @Override
    public LinkedList<CampArmy> addDefNpc(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        return null;
    }
    
    @Override
    public int isBattleEnd(final IDataGetter dataGetter, final Battle bat) {
        final int aQLsize = bat.attList.size();
        final int dQLsize = bat.defList.size();
        if (aQLsize < 1 && dQLsize < 1) {
            return 4;
        }
        if (aQLsize < 1) {
            return 2;
        }
        if (dQLsize < 1) {
            return 3;
        }
        return 1;
    }
}
