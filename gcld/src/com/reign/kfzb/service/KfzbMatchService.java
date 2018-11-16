package com.reign.kfzb.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.kfzb.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfzb.dao.*;
import org.springframework.context.*;
import org.apache.commons.logging.*;
import org.springframework.beans.*;
import com.reign.framework.hibernate.model.*;
import com.reign.kfzb.dto.request.*;
import org.springframework.transaction.annotation.*;
import com.reign.kfzb.domain.*;
import com.reign.kfzb.dto.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kfzb.constants.*;
import com.reign.kfzb.dto.response.*;
import java.util.*;
import com.reign.kfzb.battle.*;
import com.reign.kf.match.common.util.*;

@Component
public class KfzbMatchService implements IKfzbMatchService, ApplicationContextAware, InitializingBean
{
    @Autowired
    IKfzbCacheManager kfzbCacheManager;
    @Autowired
    IKfzbBattleWarriorDao kfzbBattleWarriorDao;
    @Autowired
    IKfzbBattleWarriorGeneralDao kfzbBattleWarriorGeneralDao;
    @Autowired
    IKfzbRuntimeResultDao kfzbRuntimeResultDao;
    @Autowired
    IKfzbRuntimeMatchDao kfzbRuntimeMatchDao;
    @Autowired
    IKfzbSeasonService kfzbSeasonService;
    @Autowired
    IKfzbScheduleService kfzbScheduleService;
    private static Log commonLog;
    private static Log scheduleInfoLog;
    private ApplicationContext context;
    private static IKfzbMatchService self;
    private static Log battleReportLog;
    
    static {
        KfzbMatchService.commonLog = LogFactory.getLog("astd.kfzb.log.comm");
        KfzbMatchService.scheduleInfoLog = LogFactory.getLog("astd.kfzb.log.scheduleInfo");
        KfzbMatchService.battleReportLog = LogFactory.getLog("mj.kfwd.battleReport.log");
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        KfzbMatchService.self = (IKfzbMatchService)this.context.getBean("kfzbMatchService");
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Transactional
    @Override
    public KfzbSignResult signUp(final String gameServer, final KfzbSignInfo signInfo, final boolean isSignUp) {
        final KfzbPlayerInfo playerInfo = signInfo.getPlayerInfo();
        final int curSeasonId = signInfo.getSeasonId();
        if (curSeasonId != KfzbTimeControlService.getSeasonId() || signInfo.getPlayerInfo().getCompetitorId() == 0) {
            final KfzbSignResult res = new KfzbSignResult();
            res.setPlayerId(playerInfo.getPlayerId());
            res.setState(0);
            return res;
        }
        if (isSignUp && !KfzbTimeControlService.isInSignUpTime(curSeasonId)) {
            final KfzbSignResult res = new KfzbSignResult();
            res.setPlayerId(playerInfo.getPlayerId());
            res.setState(0);
            return res;
        }
        if (!isSignUp && !KfzbTimeControlService.inSynDataTime(curSeasonId)) {
            final KfzbSignResult res = new KfzbSignResult();
            res.setPlayerId(playerInfo.getPlayerId());
            res.setState(0);
            return res;
        }
        KfzbBattleWarrior warrior = this.kfzbBattleWarriorDao.getPlayerByCId(curSeasonId, signInfo.getPlayerInfo().getCompetitorId());
        if (warrior != null) {
            BeanUtils.copyProperties(playerInfo, warrior);
            this.kfzbBattleWarriorDao.update((IModel)warrior);
            this.kfzbCacheManager.putIntoCache(warrior);
            final KfzbBattleWarriorGeneral general = this.kfzbBattleWarriorGeneralDao.getGInfoByCIdAndSeasonId(warrior.getCompetitorId(), curSeasonId);
            general.setCompetitorId(warrior.getCompetitorId());
            general.setGeneralInfo(signInfo.getCampInfo());
            general.setSeasonId(curSeasonId);
            this.kfzbBattleWarriorGeneralDao.update((IModel)general);
            this.kfzbCacheManager.putIntoCache(general);
            final KfzbSignResult res2 = new KfzbSignResult();
            res2.setPlayerId(warrior.getPlayerId());
            res2.setState(1);
            return res2;
        }
        if (!isSignUp) {
            final KfzbSignResult res3 = new KfzbSignResult();
            res3.setPlayerId(playerInfo.getPlayerId());
            res3.setState(0);
            KfzbMatchService.commonLog.info("cId=" + playerInfo.getCompetitorId() + "#cIdNotExist");
            return res3;
        }
        warrior = new KfzbBattleWarrior();
        BeanUtils.copyProperties(playerInfo, warrior);
        warrior.setGameServer(gameServer);
        warrior.setSeasonId(curSeasonId);
        final int competitorId = playerInfo.getCompetitorId();
        this.kfzbBattleWarriorDao.create((IModel)warrior);
        if (warrior.getCompetitorId() <= 0) {
            warrior.setCompetitorId(competitorId);
        }
        this.kfzbCacheManager.putIntoCache(warrior);
        final KfzbBattleWarriorGeneral general2 = new KfzbBattleWarriorGeneral();
        general2.setCompetitorId(competitorId);
        general2.setGeneralInfo(signInfo.getCampInfo());
        general2.setSeasonId(curSeasonId);
        this.kfzbBattleWarriorGeneralDao.create((IModel)general2);
        this.kfzbCacheManager.putIntoCache(general2);
        final KfzbRuntimeResult wdresult = new KfzbRuntimeResult();
        BeanUtils.copyProperties(warrior, wdresult);
        wdresult.setCompetitorId(competitorId);
        wdresult.setLayer(0);
        wdresult.setSeasonId(curSeasonId);
        this.kfzbRuntimeResultDao.create((IModel)wdresult);
        final KfzbSignResult res4 = new KfzbSignResult();
        res4.setPlayerId(warrior.getPlayerId());
        res4.setState(1);
        return res4;
    }
    
    @Override
    public KfzbRuntimeMatchResult runMatch(final KfzbRuntimeMatch resMatch) {
        final KfzbRuntimeMatchResult matchResult = new KfzbRuntimeMatchResult();
        matchResult.setMatch(resMatch);
        final int layerTotalRound = KfzbTimeControlService.getLayerBattleNum(resMatch.getLayer());
        final int player1Id = resMatch.getPlayer1Id();
        final int player2Id = resMatch.getPlayer2Id();
        if (player2Id == 0) {
            resMatch.setRoundWinner(player1Id);
            resMatch.setLayerWinner(player1Id);
            resMatch.setPlayer1Win(resMatch.getPlayer1Win() + 1);
            KfzbMatchService.self.doBattleRes(null, resMatch);
        }
        else if (player1Id == 0) {
            resMatch.setRoundWinner(player2Id);
            resMatch.setLayerWinner(player2Id);
            resMatch.setPlayer2Win(resMatch.getPlayer2Win() + 1);
            KfzbMatchService.self.doBattleRes(null, resMatch);
        }
        else {
            KfzbBattleWarrior w1 = this.kfzbCacheManager.getBattleWarrior(player1Id);
            if (w1 == null) {
                System.out.println("MIss w" + player1Id);
                w1 = this.kfzbBattleWarriorDao.getPlayerByCId(resMatch.getSeasonId(), player1Id);
                this.kfzbCacheManager.putIntoCache(w1);
            }
            KfzbBattleWarriorGeneral g1 = this.kfzbCacheManager.getBattleWarriorGeneral(player1Id);
            if (g1 == null) {
                System.out.println("MIss g" + player1Id);
                g1 = this.kfzbBattleWarriorGeneralDao.getGInfoByCIdAndSeasonId(player1Id, resMatch.getSeasonId());
                this.kfzbCacheManager.putIntoCache(g1);
            }
            KfzbBattleWarrior w2 = this.kfzbCacheManager.getBattleWarrior(player2Id);
            if (w2 == null) {
                System.out.println("MIss w" + player2Id);
                w2 = this.kfzbBattleWarriorDao.getPlayerByCId(resMatch.getSeasonId(), player2Id);
                this.kfzbCacheManager.putIntoCache(w2);
            }
            KfzbBattleWarriorGeneral g2 = this.kfzbCacheManager.getBattleWarriorGeneral(player2Id);
            if (g2 == null) {
                System.out.println("MIss g" + player2Id);
                g2 = this.kfzbBattleWarriorGeneralDao.getGInfoByCIdAndSeasonId(player2Id, resMatch.getSeasonId());
                this.kfzbCacheManager.putIntoCache(g2);
            }
            final CampArmyParam[] p1FightData = g1.getCampList();
            final CampArmyParam[] p2FightData = g2.getCampList();
            boolean needChange = false;
            needChange = getNeedChangeFromMatch(resMatch.getMatchId(), resMatch.getRound());
            if (needChange) {
                this.doBattle(resMatch, p2FightData, p1FightData);
            }
            else {
                this.doBattle(resMatch, p1FightData, p2FightData);
            }
        }
        return matchResult;
    }
    
    @Transactional
    @Override
    public void doBattleRes(final FightResult fightRes, final KfzbRuntimeMatch resMatch) {
        if (fightRes == null) {
            final KfzbBattle battle = KfzbBattleManager.createNewBattle(resMatch);
            battle.state = 1;
        }
        final int player1Id = resMatch.getPlayer1Id();
        final int player2Id = resMatch.getPlayer2Id();
        final int layerTotalRound = KfzbTimeControlService.getLayerBattleNum(resMatch.getLayer());
        final boolean needChange = getNeedChangeFromMatch(resMatch.getMatchId(), resMatch.getRound());
        if (fightRes != null) {
            final String reportId = this.getReportId(resMatch);
            resMatch.setReportId(reportId);
            final boolean isAttWin = fightRes.getIsAttWin();
            boolean isp1Win = true;
            if ((isAttWin && needChange) || (!isAttWin && !needChange)) {
                isp1Win = false;
            }
            if (isp1Win) {
                resMatch.setRoundWinner(resMatch.getPlayer1Id());
                resMatch.setPlayer1Win(resMatch.getPlayer1Win() + 1);
            }
            else {
                resMatch.setRoundWinner(resMatch.getPlayer2Id());
                resMatch.setPlayer2Win(resMatch.getPlayer2Win() + 1);
            }
        }
        if (resMatch.getPlayer1Win() > layerTotalRound / 2) {
            resMatch.setLayerWinner(resMatch.getPlayer1Id());
        }
        if (resMatch.getPlayer2Win() > layerTotalRound / 2) {
            resMatch.setLayerWinner(resMatch.getPlayer2Id());
        }
        this.kfzbRuntimeMatchDao.update((IModel)resMatch);
        final KfzbRuntimeResult rres1 = this.kfzbRuntimeResultDao.getInfoByCIdAndSeasonId(player1Id, resMatch.getSeasonId());
        final KfzbRuntimeResult rres2 = this.kfzbRuntimeResultDao.getInfoByCIdAndSeasonId(player2Id, resMatch.getSeasonId());
        if (resMatch.getRoundWinner() == player1Id) {
            if (rres1 != null) {
                rres1.setRes(KfzbCommonConstants.addRoundBattleRes(rres1.getRes(), resMatch.getRound(), 1));
            }
            if (rres2 != null) {
                rres2.setRes(KfzbCommonConstants.addRoundBattleRes(rres2.getRes(), resMatch.getRound(), 2));
            }
        }
        else {
            if (rres1 != null) {
                rres1.setRes(KfzbCommonConstants.addRoundBattleRes(rres1.getRes(), resMatch.getRound(), 2));
            }
            if (rres2 != null) {
                rres2.setRes(KfzbCommonConstants.addRoundBattleRes(rres2.getRes(), resMatch.getRound(), 1));
            }
        }
        if (resMatch.getRound() == layerTotalRound) {
            if (resMatch.getLayerWinner() == player1Id && rres2 != null) {
                rres2.setIsfinsh(3);
            }
            if (resMatch.getLayerWinner() == player2Id && rres1 != null) {
                rres1.setIsfinsh(3);
            }
        }
        if (rres1 != null) {
            this.kfzbRuntimeResultDao.update((IModel)rres1);
        }
        if (rres2 != null) {
            this.kfzbRuntimeResultDao.update((IModel)rres2);
        }
        final long battleId = KfzbCommonConstants.getBattleIdByMatch(resMatch.getSeasonId(), resMatch.getMatchId(), resMatch.getRound());
        final KfzbBattle battle2 = KfzbBattleManager.getBattleById(battleId);
        final KfzbBattleRes battleRes = battle2.getNotNullRes();
        if (fightRes != null) {
            battleRes.setP1Lost(fightRes.getAttKilledForce());
            battleRes.setP2Lost(fightRes.getDefKilledForce());
            if (needChange) {
                battleRes.setPlayer2Id(player1Id);
                battleRes.setPlayer1Id(player2Id);
                battleRes.setP1Win(fightRes.getIsAttWin());
                battleRes.setP1WinNum(resMatch.getPlayer2Win());
                battleRes.setP2WinNum(resMatch.getPlayer1Win());
            }
            else {
                battleRes.setPlayer1Id(player1Id);
                battleRes.setPlayer2Id(player2Id);
                battleRes.setP1Win(fightRes.getIsAttWin());
                battleRes.setP2WinNum(resMatch.getPlayer2Win());
                battleRes.setP1WinNum(resMatch.getPlayer1Win());
            }
        }
        final int layerWinner = resMatch.getLayerWinner();
        if (resMatch.getRound() == layerTotalRound && layerWinner > 0) {
            final int lostTicket = KfzbTimeControlService.getAllTicketsByLayerAndFinish(resMatch.getLayer(), true, false);
            int winTickets = 0;
            if (resMatch.getMatchId() == 1) {
                winTickets = KfzbTimeControlService.getAllTicketsByLayerAndFinish(resMatch.getLayer(), false, true);
            }
            else {
                winTickets = KfzbTimeControlService.getAllTicketsByLayerAndFinish(resMatch.getLayer() - 1, false, false);
            }
            if (layerWinner == resMatch.getPlayer1Id()) {
                if (needChange) {
                    battleRes.setP2Ticket(winTickets);
                    battleRes.setP1Ticket(lostTicket);
                }
                else {
                    battleRes.setP1Ticket(winTickets);
                    battleRes.setP2Ticket(lostTicket);
                }
            }
            else if (needChange) {
                battleRes.setP2Ticket(lostTicket);
                battleRes.setP1Ticket(winTickets);
            }
            else {
                battleRes.setP1Ticket(lostTicket);
                battleRes.setP2Ticket(winTickets);
            }
        }
        else {
            final boolean playerFinished = false;
            final boolean allFinished = false;
            final int tickets = KfzbTimeControlService.getAllTicketsByLayerAndFinish(resMatch.getLayer(), playerFinished, allFinished);
            battleRes.setP1Ticket(tickets);
            battleRes.setP2Ticket(tickets);
        }
        if (player1Id > 0) {
            KfzbMatchService.battleReportLog.info(battle2.getResReport7(player1Id));
            KfzbBuilder.sendMsgToOne(player1Id, battle2.getResReport7(player1Id));
            if (resMatch.getLayer() <= 4) {
                final FrameBattleReport fbr = new FrameBattleReport();
                final StringBuilder resSb = battle2.getResReport7(-1);
                final long nextRoundCd = KfzbTimeControlService.getNextBattleStartCD(resMatch.getLayer(), resMatch.getRound());
                long nextRoundCdShowCD = 0L;
                nextRoundCdShowCD = nextRoundCd - (KfzbTimeControlService.getRoundInteval() - KfzbTimeControlService.getBattleInterval()) * 1000L;
                fbr.setNextRoundTime(new Date(new Date().getTime() + nextRoundCdShowCD));
                fbr.setState(2);
                fbr.setIniReport(resSb.toString());
                fbr.setBattleReport(resSb.toString());
                fbr.setFrame(battle2.fightRound + 1);
                fbr.setEnd(true);
                KfzbScheduleService.addNewFrameReport(fbr, resMatch);
            }
        }
        if (player2Id > 0) {
            KfzbMatchService.battleReportLog.info(battle2.getResReport7(player2Id));
            KfzbBuilder.sendMsgToOne(player2Id, battle2.getResReport7(player2Id));
        }
        this.kfzbScheduleService.doFinishMatch(resMatch);
    }
    
    private String getReportId(final KfzbRuntimeMatch resMatch) {
        final String seasonRId = String.valueOf(resMatch.getSeasonId() + 10000).substring(1);
        final String matchRId = String.valueOf(resMatch.getMatchId() + 10000).substring(1);
        final String roundRId = String.valueOf(resMatch.getRound() + 1000).substring(1);
        final StringBuilder builder = new StringBuilder();
        builder.append("zbt");
        builder.append(seasonRId).append("t");
        builder.append(matchRId).append("t");
        builder.append(roundRId).append("t");
        builder.append(10000 + WebUtil.nextInt(10000));
        return builder.toString();
    }
    
    public static void doFinishBattle(final FightResult fightRes, final KfzbRuntimeMatch resMatch) {
        KfzbMatchService.self.doBattleRes(fightRes, resMatch);
    }
    
    private void doBattle(final KfzbRuntimeMatch match, final CampArmyParam[] p1FightData, final CampArmyParam[] p2FightData) {
        final KfzbBattle wdBattle = KfzbBattleManager.createNewBattle(match);
        wdBattle.iniBattleInfo(p1FightData, p2FightData);
        wdBattle.runBattle();
    }
    
    public static boolean getNeedChangeFromMatch(final int matchId, final int round) {
        boolean needChange = false;
        if (round % 2 == 0) {
            needChange = true;
        }
        return needChange;
    }
}
