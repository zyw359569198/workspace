package com.reign.gcld.battle.service;

import com.reign.gcld.player.dto.*;
import com.reign.kf.comm.param.match.*;
import com.reign.util.*;
import com.reign.gcld.battle.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.general.domain.*;
import java.util.*;

public interface IBattleService
{
    byte[] attPermit(final PlayerDto p0, final int p1, final int p2, final int p3, final int p4);
    
    byte[] battlePrepare(final PlayerDto p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    byte[] battleStart(final int p0, final int p1, final int p2, final String p3, final int p4);
    
    byte[] useStrategy(final PlayerDto p0, final String p1, final int p2, final int p3);
    
    void dobattleExceptionBatId(final String p0, final Exception p1);
    
    void leaveBattle(final int p0, final int p1, final String p2);
    
    byte[] quitBattle(final PlayerDto p0, final String p1, final String p2, final int p3);
    
    byte[] getQuitGeneral(final PlayerDto p0, final String p1, final String p2);
    
    byte[] getBattleResult(final PlayerDto p0, final int p1);
    
    byte[] deleteResult(final PlayerDto p0, final int p1);
    
    byte[] helpInfo(final PlayerDto p0, final String p1);
    
    CampArmyParam[] getKfCampDatas(final int p0, final String p1);
    
    void reSetAllDamageE(final int p0);
    
    byte[] AssembleBattle(final PlayerDto p0, final int p1, final String p2);
    
    byte[] youdi(final String p0, final PlayerDto p1);
    
    byte[] getCopyArmyCost(final PlayerDto p0);
    
    byte[] chuji(final String p0, final PlayerDto p1);
    
    byte[] doCopyArmy(final PlayerDto p0, final String p1);
    
    byte[] setChangeBat(final PlayerDto p0);
    
    byte[] getCoverChujuCd(final PlayerDto p0);
    
    byte[] getCoverYoudiCd(final PlayerDto p0);
    
    byte[] doCoverChujuCd(final PlayerDto p0);
    
    byte[] doCoverYoudiCd(final PlayerDto p0);
    
    CampArmyParam[] getKfwdCampDatas(final int p0, final String p1);
    
    byte[] getTuJinGenerals(final PlayerDto p0, final String p1);
    
    byte[] TuJin(final PlayerDto p0, final String p1, final String p2, final int p3);
    
    Tuple<Boolean, String> useOfficerToken(final int p0, final int p1, final int p2, final String p3);
    
    byte[] getCurrentTokenInfo(final PlayerDto p0, final int p1);
    
    void getCurrentOfficerTokenPushInfo(final List<OfficerTokenUseInfo> p0, final JsonDocument p1, final int p2);
    
    void addOfficerToken();
    
    byte[] useOfficerTokenInBattle(final PlayerDto p0, final int p1, final String p2);
    
    byte[] replyOfficerToken(final PlayerDto p0, final Battle p1, final int p2, final String p3);
    
    Tuple<Boolean, String> getReplyReward(final int p0, final int p1, final String p2, final int p3, final String p4);
    
    void deleteTheTokenReward(final String p0);
    
    void dealTokenBattle(final String p0, final int p1);
    
    List<OfficerTokenUseInfo> getCurrentInUseToken(final int p0);
    
    void npcStartOrJoinBattle(final WorldCity p0, final int p1);
    
    void battleReStart(final String p0);
    
    byte[] useAutoStrategy(final PlayerDto p0);
    
    byte[] cancelAutoStrategy(final PlayerDto p0);
    
    byte[] createOneToOneBattle(final int p0, final CampArmy[] p1, final Battle p2, final int p3, final int p4);
    
    void addBarbarainNpc(final Barbarain p0, final int p1, final int p2, final int p3);
    
    void addBarbarainTryNpc(final CdExams p0, final int p1, final int p2);
    
    CdExamsObj getCdExamsObjByStageAndForceId(final CdExams p0, final int p1, final int p2);
    
    void modifyWorldCityAndGeneralState(final IDataGetter p0, final Battle p1);
    
    void dealNextNpc(final Builder p0, final boolean p1, final IDataGetter p2, final Battle p3, final BattleResult p4);
    
    void dealNextNpcBuidler(final Builder p0, final boolean p1, final IDataGetter p2, final Battle p3, final BattleResult p4);
    
    byte[] watchBattle(final PlayerDto p0, final String p1);
    
    void initBarbarainPhantomMaxId();
    
    void resetAtZeroClock();
    
    int trickReduceHpBarbarain(final int p0, final int p1, final int p2);
    
    byte[] joinBattle(final PlayerDto p0, final String p1);
    
    byte[] getAssembleGeneral(final PlayerDto p0, final int p1);
    
    byte[] AssembleBattleAll(final PlayerDto p0, final String p1, final int p2);
    
    void fireManWangLing(final int p0, final int p1);
    
    void removeManWangLing(final String p0);
    
    byte[] getReplyMWLInfo(final PlayerDto p0);
    
    byte[] replyManWangLing(final PlayerDto p0, final String p1);
    
    byte[] useKillToken(final PlayerDto p0, final int p1);
    
    void resetOfficeKillToken();
    
    boolean fireManWangLing(final int p0, final int p1, final int p2, final Long p3);
    
    void removeManWangLingTryAfterCityConquered(final String p0);
    
    byte[] useGoldOrder(final PlayerDto p0, final String p1);
    
    void pushGoldOrderMsg(final int p0);
    
    void deleteGoldOrder(final String p0);
    
    void deleteAllGoldOrderInBattle(final int p0);
    
    byte[] getGoldOrderInfo(final PlayerDto p0);
    
    byte[] replyGoldOrder(final PlayerDto p0, final Battle p1, final int p2, final String p3);
    
    void getGoldOrderInfoForLogin(final int p0, final int p1, final JsonDocument p2);
    
    void loadGoldOrderFromDB(final int p0);
    
    boolean hasGoldOrderInCertainCity(final int p0);
    
    void clearBattleForNTYellowTurbans(final int p0);
    
    void addXiangYangPhantomForTimer(final String p0);
    
    int trickReduceHpYellowTurbans(final int p0, final int p1, final int p2);
    
    boolean isNTYellowTurbansXiangYangDoing(final int p0);
    
    void clearBattleForHuiZhan(final int p0, final int p1);
    
    boolean canAttackInMist(final int p0, final int p1);
    
    Tuple<Boolean, byte[]> checkGeneralForces(final PlayerDto p0, final List<Integer> p1);
    
    List<PlayerGeneralMilitary> getAvailableGenerals(final int p0, final List<Integer> p1);
    
    void updateHuizhanNationForce(final int p0, final int p1, final int p2);
    
    void updateHuizhanPlayerForce(final int p0, final int p1, final int p2);
    
    void refreshWorld();
    
    void pushMessageToCitizen(final String p0);
    
    Tuple<Boolean, Object> AssembleBattleAllFree(final PlayerDto p0, final int p1, final Set<Integer> p2);
    
    Tuple<Boolean, Object> AssembleBattleAllBattle(final PlayerDto p0, final Battle p1, final Set<Integer> p2);
    
    byte[] exeYoudiChuji(final int p0, final String p1, final int p2);
    
    byte[] getCampList(final String p0, final int p1, final int p2);
}
