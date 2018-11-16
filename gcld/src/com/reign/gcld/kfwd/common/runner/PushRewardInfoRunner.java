package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.common.*;
import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.battle.scene.*;
import java.util.*;

public class PushRewardInfoRunner implements Runnable
{
    private int playerId;
    private int result;
    private int rewardMode;
    private int reportIndex;
    private Match match;
    private MatchFight matchFight;
    private IDataGetter dataGetter;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public PushRewardInfoRunner(final int playerId, final int result, final int rewardMode, final int reportIndex, final MatchFight matchFight, final Match match, final IDataGetter dataGetter) {
        this.playerId = playerId;
        this.result = result;
        this.rewardMode = rewardMode;
        this.reportIndex = reportIndex;
        this.matchFight = matchFight;
        this.match = match;
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void run() {
        try {
            final List<ResourceDto> list = new ArrayList<ResourceDto>();
            final StringBuilder report = new StringBuilder();
            report.append(this.reportIndex);
            report.append("#");
            report.append(21);
            report.append("|");
            report.append(this.matchFight.getTurn());
            report.append("|");
            report.append(this.matchFight.getMatchNum());
            report.append("|");
            if (this.rewardMode == 1) {
                report.append(2);
            }
            else if (this.rewardMode == 2) {
                report.append(4);
            }
            else {
                report.append(1);
            }
            report.append("|");
            report.append(this.result);
            report.append(";");
            for (int resourceType = 1; resourceType < 5; ++resourceType) {
                int value = this.dataGetter.getBuildingOutputCache().getBuildingsOutput(this.playerId, resourceType);
                final int multiple = (this.result == 1) ? 2 : 1;
                value *= multiple;
                if (this.rewardMode == 1) {
                    value *= 2;
                }
                else if (this.rewardMode == 2) {
                    value *= 4;
                }
                report.append(resourceType);
                report.append("|");
                report.append(value);
                if (resourceType == 4) {
                    report.append(";");
                }
                else {
                    report.append("*");
                }
                final ResourceDto rd = new ResourceDto(resourceType, value);
                list.add(rd);
            }
            this.dataGetter.getPlayerResourceDao().addResourceIgnoreMax(this.playerId, list, "\u8de8\u670d\u6b66\u6597\u83b7\u5f97\u8d44\u6e90", true);
            final PushReportInfo pri = this.match.pushReportMap.get(this.playerId);
            if (pri.isInScene()) {
                Builder.sendMsgToOne(this.playerId, report);
            }
        }
        catch (Exception e) {
            PushRewardInfoRunner.log.error("PushRewardInfoRunner Thread Error: ", e);
        }
    }
}
