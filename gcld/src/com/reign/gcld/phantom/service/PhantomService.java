package com.reign.gcld.phantom.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.phantom.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.phantom.common.*;
import com.reign.util.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.common.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.common.*;

@Component("phantomService")
public class PhantomService implements IPhantomService
{
    @Autowired
    private IDataGetter dataGetter;
    private static final Logger timeLog;
    public static final int MAX_WIAZRD_LEVEL = 1;
    public static final int PLAYER_WIAZRD_STATE_STOP = -1;
    public static final int PLAYER_WIAZRD_STATE_FREE = 0;
    public static final int PLAYER_WIAZRD_STATE_DOING = 1;
    public static final int PLAYER_WIAZRD_STATE_DONE = 2;
    public static final int PLAYER_WIAZRD_HIST_MAX = 4;
    public static final long PLAYER_WIAZRD_AUTO_TIME_BIAS = 7000L;
    public static final int PLAYER_WIAZRD_PUSH_TYPE_0_REMOVE = 0;
    public static final int PLAYER_WIAZRD_PUSH_TYPE_1_NEW_COUNT = 1;
    public static final int PLAYER_WIAZRD_PUSH_TYPE_2_NO_WOOD = 2;
    public static final int PLAYER_WIAZRD_PUSH_TYPE_3_REACH_MAX = 3;
    
    static {
        timeLog = new TimerLogger();
    }
    
    @Override
    public void recoverPhantomJob() {
        try {
            final List<PlayerWizard> playerWizardList = this.dataGetter.getPlayerWizardDao().getNeedRecoverList();
            for (final PlayerWizard playerWizard : playerWizardList) {
                final String params2 = playerWizard.getPlayerId() + "#" + playerWizard.getWizardId();
                this.dataGetter.getJobService().addJob("phantomService", "changePhantomFlag", params2, playerWizard.getSuccTime().getTime(), false);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error(String.valueOf(this.getClass().getSimpleName()) + " changePhantomFlag catch Exception", e);
        }
    }
    
    @Override
    public byte[] getPhantomPanel(final PlayerDto playerDto) {
        final Tuple<Boolean, String> tuple = this.canDoPhantomOperation(playerDto);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startObject("workShop");
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
        final int state = pba.getPhantomWorkShopLv();
        final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerDto.playerId);
        if (playerPhantomObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final List<PlayerWizard> list = this.dataGetter.getPlayerWizardDao().getListByPlayerId(playerDto.playerId);
        doc.createElement("state", state);
        doc.createElement("canLevelUp", this.canLevelUpWorkShop(playerDto.playerId).left);
        if (state > 0) {
            doc.createElement("nowNum", pba.getVip3PhantomCount());
            doc.createElement("maxNum", playerPhantomObj.maxPhantomNum);
            final Map<Integer, PlayerWizard> map = new HashMap<Integer, PlayerWizard>();
            for (final PlayerWizard playerWizard : list) {
                map.put(playerWizard.getWizardId(), playerWizard);
            }
            doc.startArray("panel");
            for (final HmPwMain hmPwMain : this.dataGetter.getHmPwMainCache().getModels()) {
                final int wizardId = hmPwMain.getId();
                doc.startObject();
                doc.createElement("wizardId", wizardId);
                doc.createElement("name", hmPwMain.getName());
                doc.createElement("speed", hmPwMain.getPTime());
                final PlayerWizard playerWizard2 = map.get(wizardId);
                if (playerWizard2 != null) {
                    final int level = playerWizard2.getLevel();
                    int todayMax = hmPwMain.getPBase();
                    int upgradeGold = 0;
                    int extraNum = 0;
                    if (level == 0) {
                        upgradeGold = hmPwMain.getLv1Consume();
                        extraNum = 0;
                    }
                    else if (level == 1) {
                        todayMax += hmPwMain.getLv1Num();
                        upgradeGold = hmPwMain.getLv1Consume();
                        extraNum = hmPwMain.getLv1Num();
                    }
                    else if (level == 2) {
                        todayMax += hmPwMain.getLv1Num() + hmPwMain.getLv2Num();
                        upgradeGold = hmPwMain.getLv2Consume();
                        extraNum = hmPwMain.getLv2Num();
                    }
                    if (playerWizard2.getExtraPicked() == 1) {
                        extraNum = 0;
                    }
                    int canGainNum = extraNum;
                    if (playerWizard2.getFlag() == 2) {
                        canGainNum += playerWizard2.getNum();
                    }
                    doc.createElement("level", level);
                    doc.createElement("todayNum", playerWizard2.getTodayNum());
                    doc.createElement("todayMax", todayMax);
                    doc.createElement("canGainNum", canGainNum);
                    doc.createElement("upgradeGold", upgradeGold);
                }
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endObject();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getWiazrdDetail(final PlayerDto playerDto, final int wizardId) {
        final Tuple<Boolean, String> tuple = this.canDoPhantomOperation(playerDto);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerDto.playerId);
        if (playerPhantomObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final HmPwMain hmPwMain = (HmPwMain)this.dataGetter.getHmPwMainCache().get((Object)wizardId);
        if (hmPwMain == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "wizardId:" + wizardId);
        }
        final PlayerWizard playerWizard = this.dataGetter.getPlayerWizardDao().getByPlayerIdWizardId(playerDto.playerId, wizardId);
        if (playerWizard == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + LocalMessages.PHANTOM_NO_THIS_WIZARD);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startObject("wizard");
        final int level = playerWizard.getLevel();
        final int todayMax = hmPwMain.getDMax();
        int upgradeGold = 0;
        int extraNum = 0;
        int nextExtraNum = 0;
        if (level == 0) {
            upgradeGold = hmPwMain.getLv1Consume();
            nextExtraNum = hmPwMain.getLv1Num();
        }
        else if (level == 1) {
            upgradeGold = hmPwMain.getLv2Consume();
            extraNum = hmPwMain.getLv1Num();
            nextExtraNum = hmPwMain.getLv2Num();
        }
        else if (level == 2) {
            extraNum = hmPwMain.getLv1Num() + hmPwMain.getLv2Num();
        }
        if (playerWizard.getExtraPicked() == 1) {
            extraNum = 0;
        }
        int canGainNum = extraNum;
        int doneNum = 0;
        if (playerWizard.getFlag() == 2) {
            doneNum = playerWizard.getNum();
            canGainNum += doneNum;
        }
        doc.createElement("wizardId", wizardId);
        doc.createElement("name", hmPwMain.getName());
        doc.createElement("level", level);
        doc.createElement("speed", hmPwMain.getPTime());
        doc.createElement("todayNum", playerWizard.getTodayNum());
        doc.createElement("todayMax", todayMax);
        doc.createElement("extraNum", extraNum);
        doc.createElement("doneNum", doneNum);
        doc.createElement("canGainNum", canGainNum);
        doc.createElement("nextExtraNum", nextExtraNum);
        doc.createElement("upgradeGold", upgradeGold);
        int flag = playerWizard.getFlag();
        if (flag == -1) {
            flag = 0;
        }
        doc.createElement("flag", flag);
        switch (flag) {
            case -1: {
                break;
            }
            case 0: {
                final int cost = (int)(hmPwMain.getPConsume() * playerPhantomObj.costCoe);
                final int gainNum = hmPwMain.getPBase() * playerPhantomObj.baoJiNum;
                doc.createElement("cost", cost);
                doc.createElement("gainNum", gainNum);
                break;
            }
            case 1: {
                final Date succTime = playerWizard.getSuccTime();
                doc.createElement("time", succTime.getTime() + 7000L);
                doc.createElement("nowTime", System.currentTimeMillis());
                break;
            }
            case 2: {
                break;
            }
            default: {
                ErrorSceneLog.getInstance().appendErrorMsg("flag error").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("wizardId", wizardId).append("flag", flag).append("playerWizard", playerWizard.getVId()).appendClassName("PhantomService").appendMethodName("getWiazrdDetail").flush();
                break;
            }
        }
        Long pre = null;
        Integer preCost = null;
        Integer preNum = null;
        if (playerWizard.getReserve() != null && !playerWizard.getReserve().trim().isEmpty()) {
            final String[] parmStrings = playerWizard.getReserve().split(";");
            if (parmStrings.length > 0) {
                doc.startArray("hisList");
                String[] array;
                for (int length = (array = parmStrings).length, i = 0; i < length; ++i) {
                    final String hisString = array[i];
                    try {
                        final String[] hisStrings = hisString.split(",");
                        pre = Long.parseLong(hisStrings[0]);
                        preCost = Integer.parseInt(hisStrings[1]);
                        preNum = Integer.parseInt(hisStrings[2]);
                        doc.startObject();
                        doc.createElement("pre", (pre == null) ? "null" : pre);
                        doc.createElement("preCost", (preCost == null) ? "null" : preCost);
                        doc.createElement("preNum", (preNum == null) ? "null" : preNum);
                        doc.endObject();
                    }
                    catch (Exception e) {
                        ErrorSceneLog.getInstance().appendErrorMsg("PhantomService.getWiazrdDetail parser hisList catch Exception").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("playerWizard", playerWizard.getVId()).append("hisString", hisString).flush();
                    }
                }
                doc.endArray();
            }
        }
        doc.endObject();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public Tuple<Boolean, String> canDoPhantomOperation(final PlayerDto playerDto) {
        final Tuple<Boolean, String> resuTuple = new Tuple();
        resuTuple.left = false;
        final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerDto.playerId).toCharArray();
        if (cs[64] != '1') {
            resuTuple.right = LocalMessages.T_COMM_10020;
            return resuTuple;
        }
        resuTuple.left = true;
        return resuTuple;
    }
    
    @Transactional
    @Override
    public byte[] gainPhantom(final int wizardId, final PlayerDto playerDto) {
        final Tuple<Boolean, String> tuple = this.canDoPhantomOperation(playerDto);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerDto.playerId);
        if (playerPhantomObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final HmPwMain hmPwMain = (HmPwMain)this.dataGetter.getHmPwMainCache().get((Object)wizardId);
        if (hmPwMain == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "wizardId:" + wizardId);
        }
        final PlayerWizard playerWizard = this.dataGetter.getPlayerWizardDao().getByPlayerIdWizardId(playerDto.playerId, wizardId);
        if (playerWizard == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + LocalMessages.PHANTOM_NO_THIS_WIZARD);
        }
        if (playerWizard.getFlag() != -1 && playerWizard.getFlag() != 0) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + LocalMessages.PHANTOM_FLAG_INVALID_CONNOT_RESEARCH);
        }
        final Tuple<Boolean, String> resuTuple = this.startResearch(playerPhantomObj, hmPwMain, playerWizard);
        if (!(boolean)resuTuple.left) {
            return JsonBuilder.getJson(State.FAIL, resuTuple.right);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private Tuple<Boolean, String> startResearch(final PlayerPhantomObj playerPhantomObj, final HmPwMain hmPwMain, final PlayerWizard playerWizard) {
        final Tuple<Boolean, String> resuTuple = new Tuple();
        resuTuple.left = false;
        if (playerWizard.getTodayNum() >= hmPwMain.getDMax()) {
            resuTuple.right = LocalMessages.PHANTOM_THIS_WIZARD_REACH_TODAY_LIMIT;
            return resuTuple;
        }
        final int cost = (int)(hmPwMain.getPConsume() * playerPhantomObj.costCoe);
        if (!this.dataGetter.getPlayerResourceDao().consumeWood(playerWizard.getPlayerId(), cost, "\u672f\u58eb\u5de5\u574a\u6d88\u8017\u6728\u6750")) {
            this.dataGetter.getPlayerWizardDao().updateFlag(playerWizard.getVId(), -1);
            resuTuple.right = LocalMessages.PHANTOM_NOT_ENOUGH_WOOD;
            return resuTuple;
        }
        if (playerPhantomObj.isAutoOutPut) {
            final JsonDocument doc1 = new JsonDocument();
            doc1.startObject();
            doc1.createElement("type", 0);
            doc1.endObject();
            final byte[] send = doc1.toByte();
            Players.push(playerWizard.getPlayerId(), PushCommand.PUSH_WIZARD_WORKSHOP, send);
        }
        final String params = playerWizard.getPlayerId() + "#" + playerWizard.getWizardId();
        final long exeTime = System.currentTimeMillis() + hmPwMain.getPTime() * 1000L;
        playerPhantomObj.refreshBaoJiNum(this.dataGetter);
        final int num = hmPwMain.getPBase() * playerPhantomObj.baoJiNum;
        this.dataGetter.getPlayerWizardDao().updateSuccTimeFlag(playerWizard.getVId(), num, new Date(exeTime), 1);
        this.dataGetter.getPlayerWizardDao().increaseTodayNum(playerWizard.getVId(), 1);
        this.dataGetter.getJobService().addJob("phantomService", "changePhantomFlag", params, exeTime, false);
        resuTuple.left = true;
        return resuTuple;
    }
    
    @Transactional
    @Override
    public byte[] gainExtraNum(final PlayerDto playerDto, final int wizardId) {
        final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerDto.playerId);
        if (playerPhantomObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final HmPwMain hmPwMain = (HmPwMain)this.dataGetter.getHmPwMainCache().get((Object)wizardId);
        if (hmPwMain == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "wizardId:" + wizardId);
        }
        final PlayerWizard playerWizard = this.dataGetter.getPlayerWizardDao().getByPlayerIdWizardId(playerDto.playerId, wizardId);
        if (playerWizard == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + LocalMessages.PHANTOM_NO_THIS_WIZARD);
        }
        if (playerWizard.getLevel() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_THIS_WIZARD_NO_EXTRA);
        }
        if (playerWizard.getExtraPicked() != 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_THIS_WIZARD_EXTRA_PIACKED);
        }
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
        final int numLimit = playerPhantomObj.maxPhantomNum - pba.getVip3PhantomCount();
        if (numLimit <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_YOU_REACH_TODAY_LIMIT);
        }
        if (playerWizard.getNum() + pba.getVip3PhantomCount() > playerPhantomObj.maxPhantomNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_YOU_REACH_TODAY_LIMIT);
        }
        this.dataGetter.getPlayerWizardDao().updateExtraPicked(playerWizard.getVId(), 1);
        int num = 0;
        if (playerWizard.getLevel() == 1) {
            num = hmPwMain.getLv1Num();
        }
        else if (playerWizard.getLevel() == 2) {
            num = hmPwMain.getLv1Num() + hmPwMain.getLv2Num();
        }
        if (num > numLimit) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_EXCEEDED_CAN_NOT_GAIN);
        }
        if (!playerPhantomObj.isAutoOutPut) {
            final JsonDocument doc1 = new JsonDocument();
            doc1.startObject();
            doc1.createElement("type", 0);
            doc1.endObject();
            final byte[] send = doc1.toByte();
            Players.push(playerDto.playerId, PushCommand.PUSH_WIZARD_WORKSHOP, send);
        }
        this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerDto.playerId, num, "\u672f\u58eb\u5de5\u574a\u91d1\u5e01\u5347\u7ea7\u989d\u5916\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("num", num);
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    @Transactional
    @Override
    public byte[] gainDoneNum(final PlayerDto playerDto, final int wizardId) {
        final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerDto.playerId);
        if (playerPhantomObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final HmPwMain hmPwMain = (HmPwMain)this.dataGetter.getHmPwMainCache().get((Object)wizardId);
        if (hmPwMain == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "wizardId:" + wizardId);
        }
        final PlayerWizard playerWizard = this.dataGetter.getPlayerWizardDao().getByPlayerIdWizardId(playerDto.playerId, wizardId);
        if (playerWizard == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + LocalMessages.PHANTOM_NO_THIS_WIZARD);
        }
        if (playerWizard.getFlag() != 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_THIS_WIZARD_CAN_NOT_GAIN);
        }
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerDto.playerId);
        final int numLimit = playerPhantomObj.maxPhantomNum - pba.getVip3PhantomCount();
        if (numLimit <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_YOU_REACH_TODAY_LIMIT);
        }
        if (playerWizard.getNum() + pba.getVip3PhantomCount() > playerPhantomObj.maxPhantomNum) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_YOU_REACH_TODAY_LIMIT);
        }
        if (!playerPhantomObj.isAutoOutPut) {
            final JsonDocument doc1 = new JsonDocument();
            doc1.startObject();
            doc1.createElement("type", 0);
            doc1.endObject();
            final byte[] send = doc1.toByte();
            Players.push(playerDto.playerId, PushCommand.PUSH_WIZARD_WORKSHOP, send);
        }
        final int num = this.saveDoneNum(playerPhantomObj, hmPwMain, playerWizard, numLimit);
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("num", num);
        doc2.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
    }
    
    private int saveDoneNum(final PlayerPhantomObj playerPhantomObj, final HmPwMain hmPwMain, final PlayerWizard playerWizard, final int numLimit) {
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerWizard.getPlayerId());
        if (playerWizard.getNum() + pba.getVip3PhantomCount() > playerPhantomObj.maxPhantomNum) {
            return 0;
        }
        final int cost = (int)(hmPwMain.getPConsume() * playerPhantomObj.costCoe);
        int num = playerWizard.getNum();
        if (num > numLimit) {
            num = numLimit;
        }
        final StringBuilder reserve = new StringBuilder();
        reserve.append(System.currentTimeMillis()).append(",").append(cost).append(",").append(num).append(";");
        if (playerWizard.getReserve() != null && !playerWizard.getReserve().trim().isEmpty()) {
            final String[] hisList = playerWizard.getReserve().split(";");
            int up = 4;
            if (hisList.length < up) {
                up = hisList.length;
            }
            for (int i = 0; i < up; ++i) {
                reserve.append(hisList[i]).append(";");
            }
        }
        this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerWizard.getPlayerId(), num, "\u672f\u58eb\u5de5\u574a\u7814\u7a76\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
        this.dataGetter.getPlayerWizardDao().gainPhantom(playerWizard.getVId(), 0, reserve.toString());
        return num;
    }
    
    @Transactional
    @Override
    public void changePhantomFlag(final String params) {
        try {
            final long start = System.currentTimeMillis();
            PhantomService.timeLog.info(LogUtil.formatThreadLog("PhantomService", "changePhantomFlag", 0, 0L, "params:" + params));
            final String[] parramArray = params.split("#");
            final Integer playerId = Integer.parseInt(parramArray[0]);
            final Integer wizardId = Integer.parseInt(parramArray[1]);
            PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerId);
            if (playerPhantomObj == null) {
                PhantomManager.getInstance().refreshOnePlayer(playerId);
                playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerId);
            }
            if (playerPhantomObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("playerPhantomObj is null").appendPlayerId(playerId).append("params", params).appendClassName("PhantomService").appendMethodName("changePhantomFlag").flush();
                return;
            }
            final HmPwMain hmPwMain = (HmPwMain)this.dataGetter.getHmPwMainCache().get((Object)wizardId);
            if (hmPwMain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("hmPwMain is null").appendPlayerId(playerId).append("wizardId", wizardId).append("params", params).appendClassName("PhantomService").appendMethodName("changePhantomFlag").flush();
                return;
            }
            final PlayerWizard playerWizard = this.dataGetter.getPlayerWizardDao().getByPlayerIdWizardId(playerId, wizardId);
            if (playerWizard == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("playerWizard is null").appendPlayerId(playerId).append("wizardId", wizardId).appendClassName("PhantomService").appendMethodName("changePhantomFlag").flush();
                return;
            }
            if (playerWizard.getFlag() != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("flag error").appendPlayerId(playerId).append("wizardId", wizardId).append("Flag", playerWizard.getFlag()).append("playerWizard", playerWizard.getVId()).appendClassName("PhantomService").appendMethodName("changePhantomFlag").flush();
                return;
            }
            if (System.currentTimeMillis() < playerWizard.getSuccTime().getTime()) {
                ErrorSceneLog.getInstance().appendErrorMsg("SuccTime error").appendPlayerId(playerId).append("wizardId", wizardId).append("SuccTime", playerWizard.getSuccTime()).append("now", new Date()).append("playerWizard", playerWizard.getVId()).appendClassName("PhantomService").appendMethodName("changePhantomFlag").flush();
                return;
            }
            final int done = this.dataGetter.getPlayerWizardDao().updateSuccTimeFlag(playerWizard.getVId(), playerWizard.getNum(), null, 2);
            if (done != 1) {
                ErrorSceneLog.getInstance().appendErrorMsg("done != 1").appendPlayerId(playerId).append("wizardId", wizardId).append("playerWizard", playerWizard.getVId()).append("done", done).appendClassName("PhantomService").appendMethodName("changePhantomFlag").flush();
                return;
            }
            if (playerPhantomObj.isAutoOutPut) {
                final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
                final int numLimit = playerPhantomObj.maxPhantomNum - pba.getVip3PhantomCount();
                if (numLimit > 0) {
                    this.saveDoneNum(playerPhantomObj, hmPwMain, playerWizard, numLimit);
                    this.startResearchTool(playerWizard.getPlayerId(), playerWizard.getWizardId());
                }
                else {
                    final String info = playerId + "#" + playerWizard.getWizardId() + "#autoSaveFail#reachMax";
                    PhantomService.timeLog.info(info);
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("type", 3);
                    doc.endObject();
                    final byte[] send = doc.toByte();
                    Players.push(playerId, PushCommand.PUSH_WIZARD_WORKSHOP, send);
                }
            }
            else {
                final JsonDocument doc2 = new JsonDocument();
                doc2.startObject();
                doc2.createElement("type", 1);
                doc2.endObject();
                final byte[] send2 = doc2.toByte();
                Players.push(playerId, PushCommand.PUSH_WIZARD_WORKSHOP, send2);
            }
            PhantomService.timeLog.info(LogUtil.formatThreadLog("PhantomService", "changePhantomFlag", 2, System.currentTimeMillis() - start, "params:" + params));
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomService.changePhantomFlag catch Exception", e);
        }
    }
    
    private void startResearchTool(final int playerId, final int wizardId) {
        try {
            final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerId);
            if (playerPhantomObj == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("playerPhantomObj is null").appendPlayerId(playerId).append("wizardId", wizardId).appendClassName("PhantomService").appendMethodName("startResearchJob").flush();
                return;
            }
            final HmPwMain hmPwMain = (HmPwMain)this.dataGetter.getHmPwMainCache().get((Object)wizardId);
            if (hmPwMain == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("hmPwMain is null").appendPlayerId(playerId).append("wizardId", wizardId).append("wizardId", wizardId).appendClassName("PhantomService").appendMethodName("startResearchJob").flush();
                return;
            }
            final PlayerWizard playerWizard = this.dataGetter.getPlayerWizardDao().getByPlayerIdWizardId(playerId, wizardId);
            if (playerWizard == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("playerWizard is null").appendPlayerId(playerId).append("wizardId", wizardId).appendClassName("PhantomService").appendMethodName("startResearchJob").flush();
                return;
            }
            if (playerWizard.getFlag() != -1 && playerWizard.getFlag() != 0) {
                ErrorSceneLog.getInstance().appendErrorMsg("flag error").appendPlayerId(playerId).append("wizardId", wizardId).append("Flag", playerWizard.getFlag()).append("playerWizard", playerWizard.getVId()).appendClassName("PhantomService").appendMethodName("startResearchJob").flush();
                return;
            }
            final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerWizard.getPlayerId());
            if (pba.getVip3PhantomCount() >= playerPhantomObj.maxPhantomNum) {
                final String info = String.valueOf(playerId) + "#" + playerWizard.getWizardId() + "#startResearchFail#reachMax";
                PhantomService.timeLog.info(info);
                return;
            }
            if (playerWizard.getTodayNum() >= hmPwMain.getDMax()) {
                final String info = String.valueOf(playerId) + "#" + playerWizard.getWizardId() + "#startResearchFail#thisWizardReachTodayMax";
                PhantomService.timeLog.info(info);
                return;
            }
            final int cost = (int)(hmPwMain.getPConsume() * playerPhantomObj.costCoe);
            if (!this.dataGetter.getPlayerResourceDao().consumeWood(playerWizard.getPlayerId(), cost, "\u672f\u58eb\u5de5\u574a\u6d88\u8017\u6728\u6750")) {
                final String info2 = String.valueOf(playerId) + "#" + playerWizard.getWizardId() + "#startResearchFail#noWood";
                PhantomService.timeLog.info(info2);
                this.dataGetter.getPlayerWizardDao().updateFlag(playerWizard.getVId(), -1);
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("type", 2);
                doc.endObject();
                final byte[] send = doc.toByte();
                Players.push(playerId, PushCommand.PUSH_WIZARD_WORKSHOP, send);
                return;
            }
            final long exeTime = System.currentTimeMillis() + hmPwMain.getPTime() * 1000L;
            playerPhantomObj.refreshBaoJiNum(this.dataGetter);
            final int num = hmPwMain.getPBase() * playerPhantomObj.baoJiNum;
            this.dataGetter.getPlayerWizardDao().updateSuccTimeFlag(playerWizard.getVId(), num, new Date(exeTime), 1);
            this.dataGetter.getPlayerWizardDao().increaseTodayNum(playerWizard.getVId(), 1);
            final String params2 = playerWizard.getPlayerId() + "#" + playerWizard.getWizardId();
            this.dataGetter.getJobService().addJob("phantomService", "changePhantomFlag", params2, exeTime, false);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomService.startResearchJob catch Exception", e);
        }
    }
    
    @Transactional
    @Override
    public byte[] upgradeWizard(final int wizardId, final PlayerDto playerDto) {
        final Tuple<Boolean, String> tuple = this.canDoPhantomOperation(playerDto);
        if (!(boolean)tuple.left) {
            return JsonBuilder.getJson(State.FAIL, tuple.right);
        }
        final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerDto.playerId);
        if (playerPhantomObj == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PLUG_IS_SHAMEFUL);
        }
        final HmPwMain hmPwMain = (HmPwMain)this.dataGetter.getHmPwMainCache().get((Object)wizardId);
        if (hmPwMain == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + "wizardId:" + wizardId);
        }
        final PlayerWizard playerWizard = this.dataGetter.getPlayerWizardDao().getByPlayerIdWizardId(playerDto.playerId, wizardId);
        if (playerWizard == null) {
            return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.PLUG_IS_SHAMEFUL) + LocalMessages.PHANTOM_NO_THIS_WIZARD);
        }
        if (playerWizard.getLevel() == 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_WIZARD_REACH_MAX_LEVEL);
        }
        int gold = 0;
        if (playerWizard.getLevel() == 0) {
            gold = hmPwMain.getLv1Consume();
        }
        else if (playerWizard.getLevel() == 1) {
            gold = hmPwMain.getLv2Consume();
        }
        final Player player = this.dataGetter.getPlayerDao().read(playerDto.playerId);
        if (!this.dataGetter.getPlayerDao().consumeGold(player, gold, "\u5347\u7ea7\u672f\u58eb\u5de5\u574a\u672f\u58eb\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.dataGetter.getPlayerWizardDao().updateLevel(playerWizard.getVId(), playerWizard.getLevel() + 1);
        final Integer type = this.getPahntomWorkShopIconType(playerDto.playerId);
        if (type != null) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("type", type);
            doc.endObject();
            final byte[] send = doc.toByte();
            Players.push(playerDto.playerId, PushCommand.PUSH_WIZARD_WORKSHOP, send);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public Integer getPhantomWorkShopLv(final int playerId) {
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
        return pba.getPhantomWorkShopLv();
    }
    
    private Tuple<Boolean, String> canLevelUpWorkShop(final int playerId) {
        final Tuple<Boolean, String> resuTuple = new Tuple();
        resuTuple.left = false;
        final List<StoreHouse> storeHouseList = this.dataGetter.getStoreHouseDao().getByType(playerId, 12);
        if (storeHouseList == null || storeHouseList.size() == 0) {
            resuTuple.right = LocalMessages.PHANTOM_NO_PIC;
            return resuTuple;
        }
        if (storeHouseList.size() > 1) {
            resuTuple.right = LocalMessages.PHANTOM_TOO_MANY_PIC;
            return resuTuple;
        }
        final StoreHouse storeHouse = storeHouseList.get(0);
        final int itemId = storeHouse.getItemId();
        final Items picItem = (Items)this.dataGetter.getItemsCache().get((Object)itemId);
        if (picItem == null || picItem.getType() != 10) {
            ErrorSceneLog.getInstance().appendErrorMsg("picItem is null").appendPlayerId(playerId).append("storeHouse", storeHouse.getVId()).append("itemId", itemId).append("picItem.getType()", picItem.getType()).appendClassName("PhantomService").appendMethodName("canLevelUpWorkShop").flush();
            resuTuple.right = LocalMessages.PHANTOM_INVALID_PIC;
            return resuTuple;
        }
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
        if (pba.getPhantomWorkShopLv() + 1 != picItem.getIndex()) {
            ErrorSceneLog.getInstance().appendErrorMsg("PhantomWorkShopLv error").appendPlayerId(playerId).append("storeHouse", storeHouse.getVId()).append("itemId", itemId).append("picItem.getType()", picItem.getType()).append("picItem.getIndex()", picItem.getIndex()).append("pba.getPhantomWorkShopLv()", pba.getPhantomWorkShopLv()).appendClassName("PhantomService").appendMethodName("canLevelUpWorkShop").flush();
            resuTuple.right = LocalMessages.PHANTOM_INVALID_PIC_LEVEL;
            return resuTuple;
        }
        resuTuple.left = true;
        return resuTuple;
    }
    
    @Transactional
    @Override
    public byte[] buildWorkShop(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerBattleAttribute pba = this.dataGetter.getPlayerBattleAttributeDao().read(playerId);
        if (pba.getPhantomWorkShopLv() == 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_WORK_SHOP_REACH_TODAY_LIMIT);
        }
        final List<StoreHouse> storeHouseList = this.dataGetter.getStoreHouseDao().getByType(playerId, 12);
        if (storeHouseList == null || storeHouseList.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_NO_PIC);
        }
        if (storeHouseList.size() > 1) {
            return JsonBuilder.getJson(State.FAIL, "\u592a\u591a\u56fe\u7eb8");
        }
        final StoreHouse storeHouse = storeHouseList.get(0);
        final int itemId = storeHouse.getItemId();
        final Items picItem = (Items)this.dataGetter.getItemsCache().get((Object)itemId);
        if (picItem == null || picItem.getType() != 10) {
            ErrorSceneLog.getInstance().appendErrorMsg("picItem is null").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("storeHouse", storeHouse.getVId()).append("itemId", itemId).append("picItem.getType()", picItem.getType()).appendClassName("PhantomService").appendMethodName("buildWorkShop").flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_INVALID_PIC);
        }
        if (pba.getPhantomWorkShopLv() + 1 != picItem.getIndex()) {
            ErrorSceneLog.getInstance().appendErrorMsg("PhantomWorkShopLv error").appendPlayerId(playerDto.playerId).appendPlayerName(playerDto.playerName).append("storeHouse", storeHouse.getVId()).append("itemId", itemId).append("picItem.getType()", picItem.getType()).append("picItem.getIndex()", picItem.getIndex()).append("pba.getPhantomWorkShopLv()", pba.getPhantomWorkShopLv()).appendClassName("PhantomService").appendMethodName("buildWorkShop").flush();
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PHANTOM_INVALID_PIC_LEVEL);
        }
        final int step1 = this.dataGetter.getStoreHouseDao().deleteById(storeHouse.getVId());
        final int step2 = this.dataGetter.getPlayerBattleAttributeDao().updatePhantomWorkShopLv(playerId, pba.getPhantomWorkShopLv() + 1);
        final int step3 = this.inviteWiazrdCheck(playerId, pba.getPhantomWorkShopLv() + 1);
        if (step1 == 1 && step2 == 1 && step3 == 1) {
            PhantomManager.getInstance().refreshOnePlayer(playerId);
            if (picItem.getIndex() == 1) {
                CityEventManager.getInstance().removePlayerEventAfterWorkShopBuild(playerDto.playerId);
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("state", picItem.getIndex());
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        throw new RuntimeException("buildWorkShop failed!");
    }
    
    private int inviteWiazrdCheck(final int playerId, final int workShopLv) {
        final List<PlayerWizard> list = this.dataGetter.getPlayerWizardDao().getListByPlayerId(playerId);
        int maxLv = 0;
        if (list != null && list.size() > 0) {
            for (final PlayerWizard playerWizard : list) {
                if (playerWizard.getWizardId() > maxLv) {
                    maxLv = playerWizard.getWizardId();
                }
            }
        }
        if (maxLv != workShopLv - 1) {
            ErrorSceneLog.getInstance().appendErrorMsg("PlayerWizard list level error").appendPlayerId(playerId).append("workShopLv", workShopLv).append("maxLv", maxLv).appendClassName("PhantomService").appendMethodName("inviteWiazrdCheck").flush();
            return 0;
        }
        final HmPwMain hmPwMain = (HmPwMain)this.dataGetter.getHmPwMainCache().get((Object)workShopLv);
        if (hmPwMain == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("hmPwMain is null").appendPlayerId(playerId).append("workShopLv", workShopLv).appendClassName("PhantomService").appendMethodName("inviteWiazrdCheck").flush();
            return 0;
        }
        final PlayerWizard playerWizard2 = new PlayerWizard();
        playerWizard2.setPlayerId(playerId);
        playerWizard2.setWizardId(hmPwMain.getId());
        playerWizard2.setLevel(0);
        playerWizard2.setFlag(0);
        playerWizard2.setTodayNum(0);
        playerWizard2.setNum(0);
        playerWizard2.setSuccTime(null);
        playerWizard2.setReserve(null);
        playerWizard2.setExtraPicked(0);
        return this.dataGetter.getPlayerWizardDao().create(playerWizard2);
    }
    
    @Override
    public Integer getPahntomWorkShopIconType(final int playerId) {
        try {
            final char[] cs = this.dataGetter.getPlayerAttributeDao().getFunctionId(playerId).toCharArray();
            if (cs[64] != '1') {
                return null;
            }
            final PlayerPhantomObj playerPhantomObj = PhantomManager.getInstance().playerPhantomObjMap.get(playerId);
            if (playerPhantomObj == null) {
                return null;
            }
            final List<PlayerWizard> list = this.dataGetter.getPlayerWizardDao().getListByPlayerId(playerId);
            if (list.size() >= 4 && playerPhantomObj.isAutoOutPut) {
                for (final PlayerWizard playerWizard : list) {
                    if (playerWizard.getFlag() == 2) {
                        return 3;
                    }
                    if (playerWizard.getFlag() == -1) {
                        return 2;
                    }
                }
            }
            else {
                for (final PlayerWizard playerWizard : list) {
                    if (playerWizard.getFlag() == 2 || (playerWizard.getLevel() > 0 && playerWizard.getExtraPicked() == 0)) {
                        return 1;
                    }
                }
            }
            return null;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomService.getPahntomWorkShopIconType catch Exception", e);
            return null;
        }
    }
    
    @Override
    public void resetPahntomWorkShopTodayNum() {
        try {
            this.dataGetter.getPlayerWizardDao().resetAllWiazrd();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomService.resetPahntomWorkShopTodayNum catch Exception", e);
        }
    }
    
    @Override
    public void pushIconForOnLinePlayers() {
        try {
            for (final PlayerDto playerDto : Players.getAllPlayer()) {
                final Integer type = this.getPahntomWorkShopIconType(playerDto.playerId);
                if (type != null) {
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("type", type);
                    doc.endObject();
                    final byte[] send = doc.toByte();
                    Players.push(playerDto.playerId, PushCommand.PUSH_WIZARD_WORKSHOP, send);
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomService.pushIconForOnLinePlayers catch Exception", e);
        }
    }
}
