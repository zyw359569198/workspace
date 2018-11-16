package com.reign.gcld.auto.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.auto.common.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.world.common.*;
import com.reign.util.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.player.domain.*;
import java.util.*;

@Component("autoBattleService")
public class AutoBattleService implements IAutoBattleService
{
    @Autowired
    private IDataGetter dataGetter;
    private static final Logger timerLog;
    
    static {
        timerLog = new TimerLogger();
    }
    
    @Override
    public void resetAtZeroClock() {
    }
    
    @Override
    public void nextRoundAfterGeneralDead(final int playerId, final int gId) {
    }
    
    @Override
    public void recoverAutoBattleAfterMuBing(final int playerId, final int gId) {
    }
    
    @Override
    public void stopAutoBattleAfterBattleEnded(final String param) {
        try {
            final int cityId = Integer.parseInt(param);
            final City city = this.dataGetter.getCityDao().read(cityId);
            if (city == null) {
                return;
            }
            PlayerAutoBattleManager.getInstance().stopAutoBattleAfterBattleEnded(city);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " stopAutoBattleAfterBattleEnded catch exception", e);
        }
    }
    
    @Override
    public void increaseGeneralCount(final String param) {
    }
    
    @Override
    public void increaseExp(final String param) {
        try {
            final String[] paramStrings = param.split("#");
            if (paramStrings.length != 2) {
                ErrorSceneLog.getInstance().appendErrorMsg("param error").append("param", param).appendClassName(this.getClass().getSimpleName()).appendMethodName("increaseExp").flush();
                return;
            }
            final int playerId = Integer.parseInt(paramStrings[0]);
            final int expAdd = Integer.parseInt(paramStrings[1]);
            PlayerAutoBattleManager.getInstance().increaseExp(playerId, expAdd);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " increaseExp catch exception", e);
        }
    }
    
    @Override
    public void increaseLost(final String param) {
        try {
            final String[] paramStrings = param.split("#");
            if (paramStrings.length != 2) {
                ErrorSceneLog.getInstance().appendErrorMsg("param error").append("param", param).appendClassName(this.getClass().getSimpleName()).appendMethodName("increaseLost").flush();
                return;
            }
            final int playerId = Integer.parseInt(paramStrings[0]);
            final int lostAdd = Integer.parseInt(paramStrings[1]);
            PlayerAutoBattleManager.getInstance().increaseLost(playerId, lostAdd);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " increaseLost catch exception", e);
        }
    }
    
    @Override
    public void assembleOneGeneral(final PlayerAutoBattleObj playerAutoBattleObj, final PlayerGeneralMilitary pgm) {
        try {
            List<Integer> tempCityIdlist = null;
            final Integer capitalId = WorldCityCommon.nationMainCityIdMap.get(playerAutoBattleObj.forceId);
            if (pgm.getLocationId().equals(capitalId)) {
                tempCityIdlist = playerAutoBattleObj.cityIdlist;
            }
            else {
                tempCityIdlist = this.dataGetter.getCityDataCache().getMinPath(pgm.getLocationId(), playerAutoBattleObj.targetCityId);
            }
            for (int i = tempCityIdlist.size() - 1; i >= 0; --i) {
                final Integer cityId = tempCityIdlist.get(i);
                final Tuple<Boolean, String> tuple = this.dataGetter.getCityService().assembleMove(playerAutoBattleObj.playerId, pgm.getGeneralId(), cityId, 1);
                if (tuple.left) {
                    return;
                }
            }
            AutoBattleService.timerLog.info("autobattle#generalfail#DijkstraPathFailAlso#" + playerAutoBattleObj.playerId + "#" + pgm.getGeneralId());
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " assembleOneGeneral catch exception", e);
        }
    }
    
    @Override
    public void zidongdantiao(final PlayerGeneralMilitary pgm) {
        final Battle battle = NewBattleManager.getInstance().getBattleByGId(pgm.getPlayerId(), pgm.getGeneralId());
        if (battle == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("battle == null").appendPlayerId(pgm.getPlayerId()).appendGeneralId(pgm.getGeneralId()).appendClassName(this.getClass().getSimpleName()).appendMethodName("zidongdantiao").flush();
            return;
        }
        final PlayerInfo playerInfo = battle.getInBattlePlayers().get(pgm.getPlayerId());
        if (playerInfo.isAttSide()) {
            this.dataGetter.getBattleService().exeYoudiChuji(pgm.getPlayerId(), battle.getBattleId(), 1);
        }
        else {
            this.dataGetter.getBattleService().exeYoudiChuji(pgm.getPlayerId(), battle.getBattleId(), 2);
        }
    }
    
    @Override
    public byte[] startAutoBattle(final PlayerDto playerDto, final int cityId) {
        final int playerId = playerDto.playerId;
        PlayerAutoBattleObj playerAutoBattleObj = PlayerAutoBattleManager.getInstance().getPlayerAutoBattleObj(playerId);
        if (playerAutoBattleObj != null && playerAutoBattleObj.state == 1) {
            return JsonBuilder.getJson(State.FAIL, "\u5df2\u7ecf\u5728\u81ea\u52a8\u6218\u6597\u72b6\u6001\u4e2d");
        }
        if (this.dataGetter.getTechEffectCache().getTechEffect(playerId, 59) <= 0) {
            return JsonBuilder.getJson(State.FAIL, "\u4f60\u8fd8\u672a\u83b7\u5f97\u81ea\u52a8\u56fd\u6218\u79d1\u6280");
        }
        final City city = this.dataGetter.getCityDao().read(cityId);
        if (city == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + " cityId:" + cityId);
        }
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        if (battle == null && city.getForceId() == playerDto.forceId) {
            return JsonBuilder.getJson(State.FAIL, "\u6ca1\u6709\u53d1\u751f\u6218\u6597\uff0c\u4e0d\u53ef\u4ee5\u575a\u5b88");
        }
        if (WorldCityCommon.barbarainCitySet.contains(cityId)) {
            final Builder barbarBuilder = BuilderFactory.getInstance().getBuilder(14);
            final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
            final Tuple<Boolean, String> tuple = barbarBuilder.canCreateBattle(player, cityId, this.dataGetter);
            if (!(boolean)tuple.left) {
                return JsonBuilder.getJson(State.FAIL, tuple.right);
            }
        }
        boolean hasFreePgm = false;
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            if (pgm.getState() != 24 && pgm.getState() != 25 && pgm.getState() != 26 && pgm.getState() != 27) {
                if (pgm.getState() == 28) {
                    continue;
                }
                hasFreePgm = true;
            }
        }
        if (!hasFreePgm) {
            return JsonBuilder.getJson(State.FAIL, "\u4f60\u6ca1\u6709\u7a7a\u95f2\u6b66\u5c06");
        }
        final int food = 50000;
        if (!this.dataGetter.getPlayerResourceDao().consumeFood(playerId, food, "\u81ea\u52a8\u56fd\u6218\u6d88\u8017\u7cae\u98df")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10021);
        }
        if (playerAutoBattleObj == null) {
            playerAutoBattleObj = new PlayerAutoBattleObj();
            playerAutoBattleObj.playerId = playerId;
            playerAutoBattleObj.forceId = playerDto.forceId;
            PlayerAutoBattleManager.getInstance().putToMap(playerAutoBattleObj.playerId, playerAutoBattleObj);
        }
        playerAutoBattleObj.state = 1;
        playerAutoBattleObj.exp = 0;
        playerAutoBattleObj.lost = 0;
        playerAutoBattleObj.endTime = System.currentTimeMillis() + 1800000L;
        playerAutoBattleObj.targetCityId = cityId;
        if (city.getForceId() == playerDto.forceId) {
            playerAutoBattleObj.autoType = 2;
        }
        else {
            playerAutoBattleObj.autoType = 1;
        }
        final int capitalId = WorldCityCommon.nationMainCityIdMap.get(playerDto.forceId);
        playerAutoBattleObj.cityIdlist = this.dataGetter.getCityDataCache().getMinPath(capitalId, playerAutoBattleObj.targetCityId);
        playerAutoBattleObj.needCheckTime = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("count", 1);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] stopAutoBattle(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerAutoBattleObj playerAutoBattleObj = PlayerAutoBattleManager.getInstance().getPlayerAutoBattleObj(playerId);
        if (playerAutoBattleObj == null || playerAutoBattleObj.state != 1) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u5728\u81ea\u52a8\u6218\u6597\u72b6\u6001\u4e2d");
        }
        PlayerAutoBattleManager.getInstance().stopOnePlayer(playerAutoBattleObj);
        final List<PlayerGeneralMilitary> pgmList = this.dataGetter.getPlayerGeneralMilitaryDao().getMilitaryList(playerId);
        for (final PlayerGeneralMilitary pgm : pgmList) {
            this.dataGetter.getCityService().autoMoveStop(playerDto, pgm.getGeneralId());
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] getAutoBattleDetail(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerAutoBattleObj playerAutoBattleObj = PlayerAutoBattleManager.getInstance().getPlayerAutoBattleObj(playerId);
        if (playerAutoBattleObj == null) {
            return JsonBuilder.getJson(State.FAIL, "\u4e0d\u5728\u81ea\u52a8\u6218\u6597\u72b6\u6001\u4e2d");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("state", playerAutoBattleObj.state);
        if (playerAutoBattleObj.state == 1) {
            doc.createElement("cd", playerAutoBattleObj.endTime - System.currentTimeMillis());
        }
        else {
            doc.createElement("result", playerAutoBattleObj.result);
        }
        doc.createElement("exp", playerAutoBattleObj.exp);
        doc.createElement("lost", playerAutoBattleObj.lost);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public boolean InAutoBattleMode(final int playerId) {
        final PlayerAutoBattleObj playerAutoBattleObj = PlayerAutoBattleManager.getInstance().getPlayerAutoBattleObj(playerId);
        return playerAutoBattleObj != null && playerAutoBattleObj.state == 1;
    }
    
    @Override
    public void appendAutoBattleInfo(final Player player, final JsonDocument doc) {
        final int playerId = player.getPlayerId();
        if (this.dataGetter.getTechEffectCache().getTechEffect(playerId, 59) > 0) {
            doc.createElement("autoBattleTechGain", true);
        }
        else {
            doc.createElement("autoBattleTechGain", false);
        }
        final PlayerAutoBattleObj playerAutoBattleObj = PlayerAutoBattleManager.getInstance().getPlayerAutoBattleObj(playerId);
        if (playerAutoBattleObj != null && playerAutoBattleObj.state == 1) {
            doc.createElement("autoBattleCityId", playerAutoBattleObj.targetCityId);
            doc.createElement("autoType", playerAutoBattleObj.autoType);
        }
    }
}
