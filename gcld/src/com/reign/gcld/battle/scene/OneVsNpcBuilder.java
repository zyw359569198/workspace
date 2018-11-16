package com.reign.gcld.battle.scene;

import com.reign.gcld.battle.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.player.common.*;
import java.util.*;
import com.reign.gcld.general.domain.*;
import com.reign.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.common.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.world.common.*;

public class OneVsNpcBuilder extends Builder
{
    public OneVsNpcBuilder(final int battleType) {
        this.battleType = battleType;
    }
    
    @Override
    public boolean conSumeFood(final int playerId, final int defId, final IDataGetter dataGetter) {
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)defId);
        return armies.getFoodConsume() == 0 || dataGetter.getPlayerResourceDao().consumeFood(playerId, armies.getFoodConsume(), "\u526f\u672c\u6218\u6597\u6d88\u8017\u7cae\u98df");
    }
    
    @Override
    public void afterBat(final boolean attWin, final IDataGetter dataGetter, final Battle bat) {
        if (bat.auto && bat.getBattleType() == 5) {
            int result = 0;
            if (attWin) {
                result = 1;
            }
            else {
                result = 2;
            }
            final StringBuilder report = new StringBuilder();
            report.append(result).append("_").append(bat.attBaseInfo.allNum - bat.attBaseInfo.num).append(";");
        }
        try {
            final String[] strs = PowerService.getBatInfo(bat.getDefBaseInfo().getId());
            if (strs != null) {
                for (int i = 0; i < strs.length; ++i) {
                    if (strs[i] != null) {
                        if (bat.attBaseInfo.getId() > 0) {
                            final Player player = dataGetter.getPlayerDao().read(bat.attBaseInfo.getId());
                            if (strs[i].endsWith("\u25aa" + player.getPlayerName())) {
                                strs[i] = null;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {}
        Players.push(bat.getAttBaseInfo().id, PushCommand.PUSH_POWER, JsonBuilder.getSimpleJson("refresh", true));
    }
    
    @Override
    public void roundCaculateReward(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        FightRewardCoe frc = (FightRewardCoe)dataGetter.getFightRewardCoeCache().get((Object)this.getFightRewardCoeId(dataGetter, bat));
        if (frc == null) {
            BattleSceneLog.getInstance().debug("FightRewardCoe is null. battle type:" + bat.getBattleType());
            frc = new FightRewardCoe();
        }
        this.roundCaculateAttReward(dataGetter, frc, bat, roundInfo);
    }
    
    @Override
    public void updateGeneralDB(final IDataGetter dataGetter, final Battle bat, final CampArmy ca, final boolean attWin) {
        final int state = 1;
        dataGetter.getPlayerGeneralMilitaryDao().restartRecruit(ca.getPlayerId(), ca.getGeneralId(), state, new Date());
        dataGetter.getGeneralService().sendGmUpdate(ca.getPlayerId(), ca.getGeneralId(), bat.inSceneSet.contains(ca.getPlayerId()));
    }
    
    @Override
    public int getDefPlayerLevel(final IDataGetter dataGetter, final Battle bat, final RoundInfo roundInfo) {
        final int defId = bat.defBaseInfo.getId();
        return ((Armies)dataGetter.getArmiesCache().get((Object)defId)).getLevel();
    }
    
    @Override
    public void getExReward(final boolean attWin, final IDataGetter dataGetter, final PlayerInfo pi, final Battle bat) {
        if (!attWin) {
            return;
        }
        final Armies armies = (Armies)dataGetter.getArmiesCache().get((Object)bat.defBaseInfo.getId());
        final FightRewardCoe frc = (FightRewardCoe)dataGetter.getFightRewardCoeCache().get((Object)this.getFightRewardCoeId(dataGetter, bat));
        final int selfLevel = dataGetter.getPlayerDao().read(pi.getPlayerId()).getPlayerLv();
        final int rivalLevel = armies.getLevel();
        final double L = Builder.getLevelDifferCoe(frc, selfLevel, rivalLevel);
        double rewardDouble = 1.0;
        final PlayerBattleAttribute pba = dataGetter.getPlayerBattleAttributeDao().read(pi.getPlayerId());
        if (pba != null && pba.getSupportTime() != null && pba.getSupportTime().getTime() > System.currentTimeMillis()) {
            if (pba.getType() == 1) {
                rewardDouble = 1.5;
            }
            else if (pba.getType() == 2) {
                rewardDouble = 2.0;
            }
        }
        final int addmExp = (int)(armies.getExpReward() * L * rewardDouble);
        final String debug = Configuration.getProperty("gcld.battle.caculate.debug");
        if (debug.equals("1")) {
            final StringBuilder sb = new StringBuilder();
            sb.append("ExReward.").append(" | ").append("battleId:" + bat.getBattleId()).append(" | ").append("playerId:" + pi.playerId).append(" | ").append("selfLevel:" + selfLevel).append(" | ").append("rivalLevel:" + rivalLevel).append(" | ").append("armies:" + armies.getId()).append(" | ").append("addmExp:" + addmExp).append(" | ");
            BattleSceneLog.getInstance().debug(sb);
        }
        try {
            final AddExpInfo addExpInfo = dataGetter.getPlayerService().updateExpAndPlayerLevel(pi.playerId, addmExp, "\u6218\u6597\u589e\u52a0\u989d\u5916\u7ecf\u9a8c");
            final boolean upLv = addExpInfo.upLv;
            final BattleDrop battleDrop = pi.dropMap.get(5);
            battleDrop.num += addmExp;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("OneVsNpcBuilder getExReward \u589e\u52a0\u73a9\u5bb6\u7b49\u7ea7\u7ecf\u9a8c Exception").appendClassName("OneVsNpcBuilder").appendMethodName("getExReward").append("PlayerId", pi.playerId).append("mExp", addmExp).flush();
            ErrorSceneLog.getInstance().error("OneVsNpcBuilder getExReward \u589e\u52a0\u73a9\u5bb6\u7b49\u7ea7\u7ecf\u9a8c Exception", e);
        }
    }
    
    @Override
    public String getDefName(final IDataGetter dataGetter, final int defId) {
        return ((Armies)dataGetter.getArmiesCache().get((Object)defId)).getName();
    }
    
    @Override
    public Tuple<Boolean, String> canJoinBattle(final Player player, final List<PlayerGeneralMilitary> pgmList, final IDataGetter dataGetter, final Battle bat) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(player.getPlayerId());
        if (juBenDto != null) {
            tuple.right = LocalMessages.IN_JUBEN_CANNT_BATTLE;
            return tuple;
        }
        tuple.left = false;
        tuple.right = LocalMessages.T_COMM_10012;
        return tuple;
    }
    
    @Override
    public void sendBattleInfo(final IDataGetter dataGetter, final Battle bat, final BattleAttacker battleAttacker) {
        final Player player = battleAttacker.attPlayer;
        String[] strs = PowerService.getBatInfo(bat.getDefBaseInfo().getId());
        if (strs == null) {
            strs = new String[5];
            PowerService.batInfoMap.put(bat.getDefBaseInfo().getId(), strs);
        }
        final int index = (PowerService.indexs[bat.getDefBaseInfo().getId()] + 1) % 5;
        PowerService.indexs[bat.getDefBaseInfo().getId()] = index;
        final StringBuilder sb = new StringBuilder();
        sb.append(bat.getAttBaseInfo().getForceId()).append("#");
        sb.append(WorldCityCommon.nationIdNameMapDot.get(bat.getAttBaseInfo().getForceId())).append(player.getPlayerName());
        strs[index] = sb.toString();
    }
}
