package com.reign.kfwd.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfwd.cache.*;
import com.reign.kfwd.dao.*;
import org.springframework.context.*;
import org.apache.commons.logging.*;
import org.springframework.beans.*;
import com.reign.framework.hibernate.model.*;
import java.io.*;
import com.reign.kf.comm.entity.kfwd.request.*;
import org.springframework.transaction.annotation.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kfwd.constants.*;
import com.reign.util.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import java.util.*;
import com.reign.kfwd.battle.*;
import com.reign.kfwd.notice.*;
import com.reign.kfwd.domain.*;
import com.reign.kfwd.dto.*;

@Component
public class KfwdMatchService implements IKfwdMatchService, ApplicationContextAware, InitializingBean
{
    @Autowired
    IKfwdBattleWarriorDao kfwdBattleWarriorDao;
    @Autowired
    IKfwdBattleWarriorGeneralDao kfwdBattleWarriorGeneralDao;
    @Autowired
    IKfwdRuntimeResultDao kfwdRuntimeResultDao;
    @Autowired
    IKfwdRuntimeMatchDao kfwdRuntimeMatchDao;
    @Autowired
    IKfwdCacheManager kfwdCacheManager;
    @Autowired
    IKfwdRuntimeInspireDao kfwdRuntimeInspireDao;
    @Autowired
    IKfwdSeasonService kfwdSeasonService;
    @Autowired
    IKfwdRewardDoubleDao kfwdRewardDoubleDao;
    @Autowired
    IKfwdTicketRewardDao kfwdTicketRewardDao;
    private static Log commonLog;
    private ApplicationContext context;
    @Autowired
    IKfwdScheduleService kfwdScheduleService;
    private static IKfwdMatchService self;
    private static Log battleReportLog;
    HashMap<String, HashMap<Integer, PResultInfo>> serverSeasonInfoMap;
    int lastGetResultSeason;
    HashMap<Integer, PTopPlayerInfo> topPlayerInfo;
    Object lock;
    
    static {
        KfwdMatchService.commonLog = LogFactory.getLog("astd.kfwd.log.comm");
        KfwdMatchService.battleReportLog = LogFactory.getLog("mj.kfwd.battleReport.log");
    }
    
    public KfwdMatchService() {
        this.serverSeasonInfoMap = new HashMap<String, HashMap<Integer, PResultInfo>>();
        this.lastGetResultSeason = 0;
        this.topPlayerInfo = new HashMap<Integer, PTopPlayerInfo>();
        this.lock = new Object();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        KfwdMatchService.self = (IKfwdMatchService)this.context.getBean("kfwdMatchService");
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Transactional
    @Override
    public KfwdSignResult signUp(final String gameServer, final KfwdSignInfoParam signInfo, final int curSeasonId, final boolean isSignUp) {
        final KfwdPlayerInfo playerInfo = signInfo.getPlayerInfo();
        if (signInfo.getScheduleId() / 100000 % 1000 != curSeasonId % 1000) {
            final KfwdSignResult res = new KfwdSignResult();
            res.setPlayerId(playerInfo.getPlayerId());
            res.setCompetitor(playerInfo.getCompetitorId());
            res.setState(0);
            return res;
        }
        KfwdBattleWarrior warrior = this.kfwdBattleWarriorDao.getPlayer(gameServer, playerInfo.getPlayerId(), curSeasonId);
        if (warrior != null) {
            if (playerInfo.getCompetitorId() == null || !playerInfo.getCompetitorId().equals(warrior.getCompetitorId())) {
                final KfwdSignResult res2 = new KfwdSignResult();
                res2.setPlayerId(warrior.getPlayerId());
                res2.setCompetitor(warrior.getCompetitorId());
                res2.setState(1);
                KfwdMatchService.commonLog.info("cId=" + playerInfo.getCompetitorId() + "#cIdNotCorrect");
                return res2;
            }
            BeanUtils.copyProperties(playerInfo, warrior);
            this.kfwdBattleWarriorDao.update((IModel)warrior);
            this.kfwdCacheManager.putIntoCache(warrior);
            final KfwdBattleWarriorGeneral general = (KfwdBattleWarriorGeneral)this.kfwdBattleWarriorGeneralDao.load((Serializable)warrior.getCompetitorId());
            general.setCompetitorId(warrior.getCompetitorId());
            general.setGeneralInfo(signInfo.getCampInfo());
            general.setSeasonId(curSeasonId);
            this.kfwdBattleWarriorGeneralDao.update((IModel)general);
            this.kfwdCacheManager.putIntoCache(general);
            final KfwdSignResult res3 = new KfwdSignResult();
            res3.setPlayerId(warrior.getPlayerId());
            res3.setCompetitor(warrior.getCompetitorId());
            res3.setState(1);
            return res3;
        }
        else {
            if (!isSignUp) {
                final KfwdSignResult res2 = new KfwdSignResult();
                res2.setPlayerId(playerInfo.getPlayerId());
                res2.setCompetitor(playerInfo.getCompetitorId());
                res2.setState(0);
                KfwdMatchService.commonLog.info("cId=" + playerInfo.getCompetitorId() + "#cIdNotExist");
                return res2;
            }
            warrior = new KfwdBattleWarrior();
            BeanUtils.copyProperties(playerInfo, warrior);
            warrior.setGameServer(gameServer);
            warrior.setSeasonId(curSeasonId);
            final int competitorId = (int)this.kfwdBattleWarriorDao.create((IModel)warrior);
            if (warrior.getCompetitorId() <= 0) {
                warrior.setCompetitorId(competitorId);
            }
            this.kfwdCacheManager.putIntoCache(warrior);
            final KfwdBattleWarriorGeneral general2 = new KfwdBattleWarriorGeneral();
            general2.setCompetitorId(competitorId);
            general2.setGeneralInfo(signInfo.getCampInfo());
            general2.setSeasonId(curSeasonId);
            this.kfwdBattleWarriorGeneralDao.create((IModel)general2);
            this.kfwdCacheManager.putIntoCache(general2);
            final KfwdRuntimeResult wdresult = new KfwdRuntimeResult();
            BeanUtils.copyProperties(warrior, wdresult);
            wdresult.setCompetitorId(competitorId);
            wdresult.setScheduleId(signInfo.getScheduleId());
            wdresult.setGameServer(gameServer);
            wdresult.setSeasonId(curSeasonId);
            wdresult.setPlayerName(playerInfo.getPlayerName());
            wdresult.setWinNum(0);
            wdresult.setWinRes(0L);
            wdresult.setServerStartTime(signInfo.getServerStartTime());
            wdresult.setNation(playerInfo.getNation());
            wdresult.setServerName(playerInfo.getServerName());
            wdresult.setServerId(playerInfo.getServerId());
            wdresult.setPlv(playerInfo.getPlayerLevel());
            this.kfwdRuntimeResultDao.create((IModel)wdresult);
            final KfwdSignResult res4 = new KfwdSignResult();
            res4.setPlayerId(warrior.getPlayerId());
            res4.setCompetitor(competitorId);
            res4.setState(1);
            return res4;
        }
    }
    
    @Transactional
    @Override
    public KfwdRuntimeMatchResult runMatch(final KfwdRuntimeMatch resMatch) {
        final KfwdRuntimeMatchResult matchResult = new KfwdRuntimeMatchResult();
        matchResult.setMatch(resMatch);
        final int player1Id = resMatch.getPlayer1Id();
        final int player2Id = resMatch.getPlayer2Id();
        if (player2Id == 0) {
            resMatch.setWinnerId(player1Id);
            resMatch.setsRoundWinner(player1Id);
            resMatch.setPlayer1Win(resMatch.getPlayer1Win() + 1);
            resMatch.setP1WinScore(resMatch.getP1WinScore() + 32);
            KfwdMatchService.self.doBattleRes(null, resMatch);
        }
        else if (resMatch.getWinnerId() != 0) {
            resMatch.setWinnerId(resMatch.getWinnerId());
            resMatch.setsRoundWinner(resMatch.getWinnerId());
            if (resMatch.getWinnerId() == player1Id) {
                resMatch.setPlayer1Win(resMatch.getPlayer1Win() + 1);
                resMatch.setP1WinScore(resMatch.getP1WinScore() + 32);
            }
            else {
                resMatch.setPlayer2Win(resMatch.getPlayer2Win() + 1);
                resMatch.setP2WinScore(resMatch.getP2WinScore() + 32);
            }
            KfwdMatchService.self.doBattleRes(null, resMatch);
        }
        else {
            KfwdBattleWarrior w1 = this.kfwdCacheManager.getBattleWarrior(player1Id);
            if (w1 == null) {
                System.out.println("MIss w" + player1Id);
                w1 = (KfwdBattleWarrior)this.kfwdBattleWarriorDao.read((Serializable)player1Id);
                this.kfwdCacheManager.putIntoCache(w1);
            }
            KfwdBattleWarriorGeneral g1 = this.kfwdCacheManager.getBattleWarriorGeneral(player1Id);
            if (g1 == null) {
                System.out.println("MIss g" + player1Id);
                g1 = (KfwdBattleWarriorGeneral)this.kfwdBattleWarriorGeneralDao.read((Serializable)player1Id);
                this.kfwdCacheManager.putIntoCache(g1);
            }
            KfwdRuntimeInspire in1 = this.kfwdCacheManager.getInspire(player1Id, resMatch.getRound());
            if (in1 == null) {
                in1 = this.kfwdRuntimeInspireDao.getInspire(resMatch.getSeasonId(), player1Id, resMatch.getRound());
                if (in1 != null) {
                    this.kfwdCacheManager.putIntoCache(in1);
                }
            }
            KfwdBattleWarrior w2 = this.kfwdCacheManager.getBattleWarrior(player2Id);
            if (w2 == null) {
                System.out.println("MIss w" + player2Id);
                w2 = (KfwdBattleWarrior)this.kfwdBattleWarriorDao.read((Serializable)player2Id);
                this.kfwdCacheManager.putIntoCache(w2);
            }
            KfwdBattleWarriorGeneral g2 = this.kfwdCacheManager.getBattleWarriorGeneral(player2Id);
            if (g2 == null) {
                System.out.println("MIss g" + player2Id);
                g2 = (KfwdBattleWarriorGeneral)this.kfwdBattleWarriorGeneralDao.read((Serializable)player2Id);
                this.kfwdCacheManager.putIntoCache(g2);
            }
            KfwdRuntimeInspire in2 = this.kfwdCacheManager.getInspire(player2Id, resMatch.getRound());
            if (in2 == null) {
                in2 = this.kfwdRuntimeInspireDao.getInspire(resMatch.getSeasonId(), player2Id, resMatch.getRound());
                if (in2 != null) {
                    this.kfwdCacheManager.putIntoCache(in2);
                }
            }
            final CampArmyParam[] p1FightData = g1.getCampList();
            final CampArmyParam[] p2FightData = g2.getCampList();
            boolean needChange = false;
            needChange = getNeedChangeFromMatch(resMatch);
            Integer levelLimit = KfwdScheduleService.scheduleIdLevelLimitMap.get(resMatch.getScheduleId());
            if (levelLimit == null) {
                levelLimit = 500;
            }
            boolean needMinus1 = false;
            boolean needMinus2 = false;
            if (w1.getPlayerLevel() != null && w1.getPlayerLevel() > levelLimit) {
                needMinus1 = true;
            }
            if (w2.getPlayerLevel() != null && w2.getPlayerLevel() > levelLimit) {
                needMinus2 = true;
            }
            this.doBattle(resMatch, p1FightData, p2FightData);
        }
        return matchResult;
    }
    
    private static boolean getNeedChangeFromMatch(final KfwdRuntimeMatch resMatch) {
        boolean needChange = false;
        if (resMatch.getsRound() % 2 == 0) {
            needChange = true;
        }
        if (resMatch.getsRound() == 3) {
            needChange = !KfwdConstantsAndMethod.isC1AttackerRound3(resMatch.getMatchId(), resMatch.getRound(), resMatch.getScheduleId());
        }
        return needChange;
    }
    
    private void doBattle(final KfwdRuntimeMatch match, final CampArmyParam[] p1FightData, final CampArmyParam[] p2FightData) {
        final KfwdBattle wdBattle = KfwdBattleManager.createNewBattle(match);
        wdBattle.iniBattleInfo(p1FightData, p2FightData);
        wdBattle.runBattle();
    }
    
    private String getReportId(final KfwdRuntimeMatch resMatch) {
        final StringBuilder builder = new StringBuilder();
        builder.append("wd,");
        builder.append(resMatch.getSeasonId()).append(",");
        builder.append(resMatch.getScheduleId()).append(",");
        builder.append(resMatch.getRound()).append(",");
        builder.append(resMatch.getsRound()).append(",");
        builder.append(resMatch.getMatchId());
        return MD5SecurityUtil.code(builder.toString());
    }
    
    @Override
    public KfwdSeasonBattleRes getNationResultInfo(final String gameServer, final int seasonId) {
        if (seasonId == 0) {
            return null;
        }
        final long now = System.currentTimeMillis();
        synchronized (this.lock) {
            if (this.lastGetResultSeason != seasonId) {
                final boolean isOver = this.checkOver(seasonId);
                if (!isOver) {
                    // monitorexit(this.lock)
                    return null;
                }
                this.builderResultInfo(seasonId);
                this.lastGetResultSeason = seasonId;
            }
        }
        // monitorexit(this.lock)
        final HashMap<Integer, PResultInfo> nRes = this.serverSeasonInfoMap.get(gameServer);
        final KfwdSeasonBattleRes bRes = new KfwdSeasonBattleRes();
        final List<PTopPlayerInfo> topList = new ArrayList<PTopPlayerInfo>();
        final List<PResultInfo> selfNationInfo = new ArrayList<PResultInfo>();
        bRes.setSelfNationInfo(selfNationInfo);
        bRes.setTopList(topList);
        if (nRes != null) {
            for (int i = 1; i <= 3; ++i) {
                final PResultInfo pInfo = nRes.get(i);
                if (pInfo != null) {
                    selfNationInfo.add(pInfo);
                }
            }
        }
        for (int i = 1; i <= 10; ++i) {
            final PTopPlayerInfo pInfo2 = this.topPlayerInfo.get(i);
            if (pInfo2 != null) {
                topList.add(pInfo2);
            }
        }
        return bRes;
    }
    
    private void builderResultInfo(final int seasonId) {
        this.topPlayerInfo.clear();
        this.serverSeasonInfoMap.clear();
        final List<KfwdRuntimeResult> list = this.kfwdRuntimeResultDao.getSortResultBySeasonId(seasonId);
        for (int size = list.size(), i = 0; i < size; ++i) {
            final KfwdRuntimeResult res = list.get(i);
            if (i <= 10) {
                final KfwdBattleWarrior kb = (KfwdBattleWarrior)this.kfwdBattleWarriorDao.read((Serializable)res.getCompetitorId());
                final PTopPlayerInfo topp = new PTopPlayerInfo();
                BeanUtils.copyProperties(kb, topp);
                topp.setWinNum(res.getWinNum());
                topp.setScore(res.getScore());
                topp.setPlayerName(res.getPlayerName());
                topp.setPos(i + 1);
                this.topPlayerInfo.put(i + 1, topp);
            }
            final String gs = res.getGameServer();
            final int nation = res.getNation();
            final int winNum = res.getWinNum();
            final int score = res.getScore();
            final String playerName = res.getPlayerName();
            if (gs != null && nation != 0) {
                HashMap<Integer, PResultInfo> rmap = this.serverSeasonInfoMap.get(gs);
                if (rmap == null) {
                    rmap = new HashMap<Integer, PResultInfo>();
                    this.serverSeasonInfoMap.put(gs, rmap);
                }
                PResultInfo prInfo = rmap.get(nation);
                if (prInfo == null) {
                    prInfo = new PResultInfo();
                    prInfo.setNation(nation);
                    prInfo.setWinNum(winNum);
                    prInfo.setScore(score);
                    prInfo.setPlayerName(playerName);
                    rmap.put(nation, prInfo);
                }
                if (prInfo.getScore() <= score) {
                    if (prInfo.getScore() != score || prInfo.getWinNum() < winNum) {
                        prInfo.setNation(nation);
                        prInfo.setWinNum(winNum);
                        prInfo.setScore(score);
                        prInfo.setPlayerName(playerName);
                    }
                }
            }
        }
    }
    
    private boolean checkOver(final int seasonId) {
        final KfwdRuntimeMatch match = this.kfwdRuntimeMatchDao.getOneLastRoundLastMatch(seasonId);
        return match != null;
    }
    
    public static void doFinishBattle(final FightResult fightRes, final KfwdRuntimeMatch resMatch) {
        KfwdMatchService.self.doBattleRes(fightRes, resMatch);
    }
    
    @Transactional
    @Override
    public void doBattleRes(final FightResult fightRes, final KfwdRuntimeMatch resMatch) {
        if (fightRes == null) {
            final KfwdBattle battle = KfwdBattleManager.createNewBattle(resMatch);
            battle.state = 1;
        }
        final int player1Id = resMatch.getPlayer1Id();
        final int player2Id = resMatch.getPlayer2Id();
        if (fightRes != null) {
            final String reportId = this.getReportId(resMatch);
            resMatch.setReportId(reportId);
            final int[] scores = KfwdConstantsAndMethod.getWinScore(fightRes.getIsAttWin(), fightRes.getAttRemainNum(), fightRes.getDefRemainNum());
            final boolean isAttWin = fightRes.getIsAttWin();
            final boolean needChange = getNeedChangeFromMatch(resMatch);
            boolean isp1Win = true;
            if ((isAttWin && needChange) || (!isAttWin && !needChange)) {
                isp1Win = false;
            }
            int p1AddScore = 0;
            int p2AddScore = 0;
            if (!needChange) {
                p1AddScore = scores[0];
                p2AddScore = scores[1];
            }
            else {
                p1AddScore = scores[1];
                p2AddScore = scores[0];
            }
            if (isp1Win) {
                resMatch.setsRoundWinner(resMatch.getPlayer1Id());
                resMatch.setPlayer1Win(resMatch.getPlayer1Win() + 1);
                resMatch.setP1WinScore(resMatch.getP1WinScore() + p1AddScore);
                resMatch.setP2WinScore(resMatch.getP2WinScore() + p2AddScore);
            }
            else {
                resMatch.setsRoundWinner(resMatch.getPlayer2Id());
                resMatch.setPlayer2Win(resMatch.getPlayer2Win() + 1);
                resMatch.setP2WinScore(resMatch.getP2WinScore() + p2AddScore);
                resMatch.setP1WinScore(resMatch.getP1WinScore() + p1AddScore);
            }
            if (resMatch.getPlayer1Win() == 1) {
                resMatch.setWinnerId(resMatch.getPlayer1Id());
            }
            if (resMatch.getPlayer2Win() == 1) {
                resMatch.setWinnerId(resMatch.getPlayer2Id());
            }
        }
        this.kfwdRuntimeMatchDao.update((IModel)resMatch);
        if (resMatch.getsRound() == 1) {
            if (resMatch.getWinnerId() == player1Id) {
                KfwdRuntimeResult rres = this.kfwdRuntimeResultDao.getPlayerByCId(resMatch.getSeasonId(), resMatch.getScheduleId(), player1Id);
                if (rres != null) {
                    rres.setWinNum(rres.getWinNum() + 1);
                    rres.setWinRes(KfwdConstantsAndMethod.addHisRoundBattleRes(rres.getWinRes(), resMatch.getRound(), 1L));
                    rres.setLastScore(rres.getScore());
                    rres.addScore(resMatch.getP1WinScore(), resMatch.getRound());
                    final int getTicket = this.setNewTicketReward(resMatch, player1Id, resMatch.getP1Score(), true, rres.getWinRes());
                    this.createMactchTitle(resMatch, fightRes, player1Id, getTicket, rres.getScore());
                    this.kfwdRuntimeResultDao.update((IModel)rres);
                }
                if (player2Id != 0) {
                    rres = this.kfwdRuntimeResultDao.getPlayerByCId(resMatch.getSeasonId(), resMatch.getScheduleId(), player2Id);
                    if (rres != null) {
                        rres.setWinRes(KfwdConstantsAndMethod.addHisRoundBattleRes(rres.getWinRes(), resMatch.getRound(), 2L));
                        rres.setLastScore(rres.getScore());
                        rres.addScore(resMatch.getP2WinScore(), resMatch.getRound());
                        final int getTicket = this.setNewTicketReward(resMatch, player2Id, resMatch.getP2Score(), false, rres.getWinRes());
                        this.createMactchTitle(resMatch, fightRes, player2Id, getTicket, rres.getScore());
                        this.kfwdRuntimeResultDao.update((IModel)rres);
                    }
                }
            }
            else if (resMatch.getWinnerId() != 0) {
                KfwdRuntimeResult rres = this.kfwdRuntimeResultDao.getPlayerByCId(resMatch.getSeasonId(), resMatch.getScheduleId(), player1Id);
                if (rres != null) {
                    rres.setLastScore(rres.getScore());
                    rres.addScore(resMatch.getP1WinScore(), resMatch.getRound());
                    rres.setWinRes(KfwdConstantsAndMethod.addHisRoundBattleRes(rres.getWinRes(), resMatch.getRound(), 2L));
                    final int getTicket = this.setNewTicketReward(resMatch, player1Id, resMatch.getP1Score(), false, rres.getWinRes());
                    this.createMactchTitle(resMatch, fightRes, player1Id, getTicket, rres.getScore());
                    this.kfwdRuntimeResultDao.update((IModel)rres);
                }
                if (player2Id != 0) {
                    rres = this.kfwdRuntimeResultDao.getPlayerByCId(resMatch.getSeasonId(), resMatch.getScheduleId(), player2Id);
                    if (rres != null) {
                        rres.setLastScore(rres.getScore());
                        rres.setWinNum(rres.getWinNum() + 1);
                        rres.setWinRes(KfwdConstantsAndMethod.addHisRoundBattleRes(rres.getWinRes(), resMatch.getRound(), 1L));
                        rres.addScore(resMatch.getP2WinScore(), resMatch.getRound());
                        final int getTicket = this.setNewTicketReward(resMatch, player2Id, resMatch.getP2Score(), true, rres.getWinRes());
                        this.createMactchTitle(resMatch, fightRes, player2Id, getTicket, rres.getScore());
                        this.kfwdRuntimeResultDao.update((IModel)rres);
                    }
                }
            }
        }
        final long battleId = KfwdConstantsAndMethod.getBattleIdByMatch(resMatch.getSeasonId(), resMatch.getScheduleId(), resMatch.getMatchId(), resMatch.getRound());
        final KfwdBattle battle2 = KfwdBattleManager.getBattleById(battleId);
        KfwdMatchService.battleReportLog.info(battle2.getResReport7(player1Id));
        KfwdBuilder.sendMsgToOne(player1Id, battle2.getResReport7(player1Id));
        if (player2Id > 0) {
            KfwdMatchService.battleReportLog.info(battle2.getResReport7(player2Id));
            KfwdBuilder.sendMsgToOne(player2Id, battle2.getResReport7(player2Id));
        }
        this.kfwdScheduleService.doFinishMatch(resMatch);
    }
    
    private void createMactchTitle(final KfwdRuntimeMatch match, final FightResult fightRes, final int player1Id, final int getTicket, final int score) {
        final long battleId = KfwdConstantsAndMethod.getBattleIdByMatch(match.getSeasonId(), match.getScheduleId(), match.getMatchId(), match.getRound());
        final KfwdBattle battle = KfwdBattleManager.getBattleById(battleId);
        final KfwdBattleRes res = battle.getNotNullRes();
        if (fightRes == null) {
            res.setP1Win(true);
            res.setP1Score(match.getP1WinScore());
            res.setP1TotalScore(score);
            res.setP1Ticket(getTicket);
            res.setP1Lost(0);
            return;
        }
        res.setP1Win(fightRes.getIsAttWin());
        if (player1Id == match.getPlayer1Id()) {
            res.setP1Score(match.getP1WinScore());
            res.setP1TotalScore(score);
            res.setP1Ticket(getTicket);
            res.setP1Lost(fightRes.getAttKilledForce());
        }
        else if (player1Id == match.getPlayer2Id()) {
            res.setP2Score(match.getP2WinScore());
            res.setP2TotalScore(score);
            res.setP2Ticket(getTicket);
            res.setP2Lost(fightRes.getDefKilledForce());
        }
    }
    
    private int setNewTicketReward(final KfwdRuntimeMatch resMatch, final int player1Id, final int winScore, final boolean isWin, final long winRes) {
        if (player1Id > 0) {
            final KfwdRewardDouble krd = this.kfwdCacheManager.getRewardDouble(player1Id);
            final RewardInfo info = KfwdTimeControlService.getRewardInfoBuyScheduleId(resMatch.getScheduleId());
            int ticket = KfwdConstantsAndMethod.getTicketByScore(info.getBasicScore(), info.getWinCoef(), winScore);
            if (krd != null) {
                ticket = krd.getRoundDoubleTicket(resMatch.getRound(), ticket);
            }
            if (!isWin) {
                ticket *= (int)0.3;
            }
            KfwdTicketReward kfwdTicketReward = this.kfwdCacheManager.getTicketInfo(player1Id);
            if (kfwdTicketReward == null) {
                kfwdTicketReward = (KfwdTicketReward)this.kfwdTicketRewardDao.read((Serializable)player1Id);
                if (kfwdTicketReward == null) {
                    kfwdTicketReward = new KfwdTicketReward();
                    kfwdTicketReward.setCompetitorId(player1Id);
                    kfwdTicketReward.setSeasonId(resMatch.getSeasonId());
                    kfwdTicketReward.setScheduleId(resMatch.getScheduleId());
                    final KfwdBattleWarrior w1 = this.kfwdCacheManager.getBattleWarrior(player1Id);
                    kfwdTicketReward.setGameServer(w1.getGameServer());
                    this.kfwdTicketRewardDao.create((IModel)kfwdTicketReward);
                }
            }
            final int day = KfwdTimeControlService.getDayByRound(resMatch.getRound());
            if (day > 1) {
                kfwdTicketReward.addReward(day - 1);
            }
            kfwdTicketReward.setWinRes(winRes);
            kfwdTicketReward.setRoundTicket(resMatch.getRound(), ticket);
            this.kfwdTicketRewardDao.update((IModel)kfwdTicketReward);
            this.kfwdCacheManager.putIntoCache(kfwdTicketReward);
            KfwdTicketRewardNoticeInfo.addNoticeInfo(kfwdTicketReward);
            return ticket;
        }
        return 0;
    }
}
