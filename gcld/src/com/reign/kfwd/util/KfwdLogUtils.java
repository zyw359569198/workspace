package com.reign.kfwd.util;

import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.kfwd.domain.*;

public class KfwdLogUtils
{
    public static final String INTERFACE = "interface";
    public static final String SHARP = "#";
    
    public static String formatInterfaceLog(final String ip, final String actionName, final String methodName, final long time) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append("interface").append("#").append(ip).append("#").append(actionName).append("#").append(methodName).append("#").append(time);
        return builder.toString();
    }
    
    public static String formatGwSeasonInfo(final KfwdSeasonInfo newInfo) {
        final StringBuilder builder = new StringBuilder();
        builder.append("seasonInfo").append("#");
        try {
            builder.append("info:").append(Types.OBJECT_MAPPER.writeValueAsString(newInfo));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
    
    public static StringBuilder getClientInfoPrefix(final int seasonId, final String gameServer, final int nation, final int scheduleId, final int msId) {
        final StringBuilder builder = new StringBuilder();
        builder.append("client:");
        builder.append(seasonId).append("#");
        builder.append(gameServer).append("#");
        builder.append(nation).append("#");
        builder.append(scheduleId).append("#");
        builder.append(msId).append("#");
        return builder;
    }
    
    public static String getRtInfoLog(final KfwdRTMatchInfo kfwdRTMatchInfo) {
        if (kfwdRTMatchInfo == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        builder.append("client:");
        builder.append(kfwdRTMatchInfo.getRound()).append("#");
        builder.append(kfwdRTMatchInfo.getsRound()).append("#");
        builder.append(String.valueOf(kfwdRTMatchInfo.getCompetitorId1()) + "-" + kfwdRTMatchInfo.getCompetitorId2()).append("#");
        builder.append(kfwdRTMatchInfo.getRes()).append("#");
        builder.append(String.valueOf(kfwdRTMatchInfo.getHistoryRes1()) + "-" + kfwdRTMatchInfo.getHistoryRes2()).append("#");
        builder.append(String.valueOf(kfwdRTMatchInfo.getTicket()) + "-" + kfwdRTMatchInfo.getScore()).append("#");
        return builder.toString();
    }
    
    public static String getMachInfo(final KfwdRuntimeMatch match) {
        final StringBuilder builder = new StringBuilder();
        builder.append("match").append("#");
        builder.append(match.getSeasonId()).append("#");
        builder.append(match.getScheduleId()).append("#");
        builder.append(match.getRound()).append("#");
        builder.append(match.getsRound()).append("#");
        builder.append(match.getMatchId()).append("#");
        builder.append(match.getPlayer1Id()).append("-").append(match.getPlayer2Id()).append("#");
        builder.append(match.getsRoundWinner()).append("#");
        builder.append(match.getWinnerId()).append("#");
        return builder.toString();
    }
}
