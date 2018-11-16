package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.common.*;
import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.kfwd.service.*;
import java.util.concurrent.*;
import com.reign.gcld.player.dto.*;
import java.util.*;

public class PushReportRunner implements Runnable
{
    private int playerId;
    private StringBuilder report;
    private int reportIndex;
    private Match match;
    private MatchFight matchFight;
    private IDataGetter dataGetter;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public PushReportRunner(final int playerId, final StringBuilder report, final int reportIndex, final Match match, final MatchFight matchFight, final IDataGetter dataGetter) {
        this.playerId = playerId;
        this.report = report;
        this.reportIndex = reportIndex;
        this.match = match;
        this.matchFight = matchFight;
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void run() {
        try {
            final String[] reports = this.report.toString().split(":");
            final int totalNum = reports.length;
            final String needPushReport = reports[this.reportIndex - 1];
            final PushReportInfo pri = this.match.pushReportMap.get(this.playerId);
            final int index1 = needPushReport.indexOf("#") + 1;
            final int reportId = Integer.valueOf(needPushReport.substring(index1, needPushReport.indexOf("|", index1)));
            if (reportId == 21) {
                this.releaseReward(this.playerId, needPushReport, this.dataGetter);
            }
            synchronized (pri) {
                if (pri.isInScene()) {
                    Builder.sendMsgToOne(this.playerId, new StringBuilder(needPushReport));
                    final String headInfo = "MatchId " + this.matchFight.getMatchId() + " \u6218\u6597\u53d1\u9001\u7ed9\u67d0\u4eba:  " + this.playerId + "\n";
                    Builder.getLog(needPushReport, headInfo);
                }
                pri.setReportIndex(this.reportIndex);
            }
            if (this.reportIndex < totalNum) {
                final int delaySeconds = Integer.valueOf(needPushReport.substring(needPushReport.indexOf("|") + 1, needPushReport.indexOf("#")));
                MatchService.getExecutor().schedule(new PushReportRunner(this.playerId, this.report, this.reportIndex + 1, this.match, this.matchFight, this.dataGetter), delaySeconds, TimeUnit.SECONDS);
            }
        }
        catch (Exception e) {
            PushReportRunner.log.error("PushReportRunner Thread Error: ", e);
        }
    }
    
    public void releaseReward(final int playerId, final String report, final IDataGetter dataGetter) {
        final List<ResourceDto> list = new ArrayList<ResourceDto>();
        final String keyPart = report.split(";")[1];
        final String[] ss = keyPart.split("\\*");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (!s.isEmpty()) {
                final String[] as = s.split("\\|");
                final int type = Integer.valueOf(as[0]);
                final int count = Integer.valueOf(as[1]);
                if (count > 0) {
                    final ResourceDto rd = new ResourceDto(type, count);
                    list.add(rd);
                }
            }
        }
        dataGetter.getPlayerResourceDao().addResourceIgnoreMax(playerId, list, "\u8de8\u670d\u6b66\u6597\u83b7\u5f97\u8d44\u6e90", true);
    }
}
