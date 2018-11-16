package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.battle.scene.*;

public class PushPointInfoRunner implements Runnable
{
    private MatchFight matchFight;
    private MatchAttendee matchAttendee;
    private int reportIndex;
    private Match match;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public PushPointInfoRunner(final MatchFight matchFight, final MatchAttendee matchAttendee, final int reportIndex, final Match match) {
        this.matchFight = matchFight;
        this.matchAttendee = matchAttendee;
        this.reportIndex = reportIndex;
        this.match = match;
    }
    
    @Override
    public void run() {
        try {
            final PushReportInfo pri = this.match.pushReportMap.get(this.matchAttendee.getPlayerId());
            if (pri.isInScene()) {
                final StringBuilder report = new StringBuilder();
                report.append(this.reportIndex);
                report.append("#");
                report.append(22);
                report.append("|");
                report.append(this.matchFight.getTurn());
                report.append("|");
                report.append(this.matchAttendee.getPoints());
                report.append("|");
                if (this.matchFight.getMember1().getCompetitorId() == this.matchAttendee.getCompetitorId()) {
                    report.append(this.matchFight.getMember1().getPoint());
                }
                else {
                    report.append(this.matchFight.getMember2().getPoint());
                }
                report.append("|");
                report.append(this.matchFight.getState() == 8);
                report.append(";");
                report.append(this.matchFight.getMember1().getPlayerName());
                report.append("|");
                report.append(this.matchFight.getMember1().getWinMatch());
                report.append("*");
                report.append(this.matchFight.getMember2().getPlayerName());
                report.append("|");
                report.append(this.matchFight.getMember2().getWinMatch());
                report.append(";");
                Builder.sendMsgToOne(this.matchAttendee.getPlayerId(), report);
            }
        }
        catch (Exception e) {
            PushPointInfoRunner.log.error("PushPointInfoRunner Thread Error:", e);
        }
    }
}
