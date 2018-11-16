package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.common.*;

public class RePushReportRunner implements Runnable
{
    private int playerId;
    private boolean isAttSide;
    private Match match;
    private MatchFightMember member1;
    private MatchFightMember member2;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public RePushReportRunner(final int playerId, final boolean isAttSide, final Match match, final MatchFightMember member1, final MatchFightMember member2) {
        this.playerId = playerId;
        this.isAttSide = isAttSide;
        this.match = match;
        this.member1 = member1;
        this.member2 = member2;
    }
    
    @Override
    public void run() {
        try {
            final PushReportInfo pushReportInfo = this.match.pushReportMap.get(this.playerId);
            if (pushReportInfo == null) {
                return;
            }
            synchronized (pushReportInfo) {
                final int nowIndex = pushReportInfo.getReportIndex();
                final String totalReport = pushReportInfo.getReport();
                final StringBuilder needRepushReport = new StringBuilder();
                final String[] reports = totalReport.split(":");
                int reportIndex = 0;
                String[] array;
                for (int length = (array = reports).length, i = 0; i < length; ++i) {
                    final String report = array[i];
                    if (++reportIndex > nowIndex) {
                        break;
                    }
                    needRepushReport.append(report).append(":");
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                final String pushReport = BattleService.getKfCurReport(needRepushReport.toString(), this.isAttSide, this.member1, this.member2);
                final String headInfo = "MatchId " + this.match.getMatchId() + " \u91cd\u65b0\u8fdb\u5165\u6218\u6597\u4e2d:  " + this.playerId + "\n";
                Builder.getLog(pushReport, headInfo);
                doc.createElement("pushReport", pushReport);
                doc.endObject();
                Players.push(this.playerId, PushCommand.PUSH_KFWD_MATCH_REPORT, doc.toByte());
                pushReportInfo.setInScene(true);
            }
        }
        catch (Exception e) {
            RePushReportRunner.log.error("RePushReportRunner Thread Error:", e);
        }
    }
}
