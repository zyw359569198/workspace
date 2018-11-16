package com.reign.kfzb.util;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import java.io.*;
import com.reign.kf.match.common.*;
import com.reign.util.*;
import java.util.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kfzb.domain.*;
import com.reign.kfzb.dto.response.*;

public class KfzbLogUtils
{
    public static final String INTERFACE = "interface";
    public static final String SHARP = "#";
    
    public static String formatInterfaceLog(final String ip, final RequestChunk chunk, final int length, final long time) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append("interface").append("#").append(ip).append("#").append("KfzbMatchAction").append("#").append("execute").append("#").append(length).append("#").append(time).append("#").append("[");
        if (chunk != null) {
            try {
                builder.append("Request:").append(Types.OBJECT_MAPPER.writeValueAsString(chunk));
            }
            catch (JsonGenerationException e) {
                e.printStackTrace();
            }
            catch (JsonMappingException e2) {
                e2.printStackTrace();
            }
            catch (IOException e3) {
                e3.printStackTrace();
            }
        }
        builder.append("]");
        return builder.toString();
    }
    
    public static String formatInterfaceLogInfo(final String ip, final RequestChunk chunk, final long time, final long dealTime) {
        final StringBuilder builder = new StringBuilder(100);
        final CommandWatch watch = (CommandWatch)ThreadLocalFactory.getThreadLocalObj();
        builder.append("interface").append("#").append(ip).append("#").append("KfzbMatchAction").append("#").append("execute").append("#").append(time).append("#").append(dealTime).append("#");
        if (watch != null) {
            builder.append(watch.toString());
            ThreadLocalFactory.setThreadLocalObj((Object)null);
        }
        builder.append("#").append("[");
        if (chunk != null) {
            builder.append("r:").append(chunk.getMachineId()).append(",len:").append((chunk.getRequestList() != null) ? chunk.getRequestList().size() : 0);
        }
        builder.append("]");
        return builder.toString();
    }
    
    public static String formatInterfaceLogInfo(final String ip, final List<Response> responseList) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append("interface").append("#").append(ip).append("#").append("KfzbMatchAction").append("#").append("execute").append("#").append("[");
        if (responseList != null) {
            try {
                builder.append("Response:").append(responseList.size());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        builder.append("]");
        return builder.toString();
    }
    
    public static String formatInterfaceLog(final String ip, final String actionName, final String methodName, final long time) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append("interface").append("#").append(ip).append("#").append(actionName).append("#").append(methodName).append("#").append(time);
        return builder.toString();
    }
    
    public static String formatGwSeasonInfo(final KfzbSeasonInfo newInfo) {
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
    
    public static String getMachInfo(final KfzbRuntimeMatch match) {
        final StringBuilder builder = new StringBuilder();
        builder.append("match").append("#");
        builder.append(match.getSeasonId()).append("#");
        builder.append(match.getLayer()).append("#");
        builder.append(match.getRound()).append("#");
        builder.append(match.getMatchId()).append("#");
        builder.append(match.getPlayer1Id()).append("-").append(match.getPlayer2Id()).append("#");
        builder.append(match.getRoundWinner()).append("#");
        builder.append(match.getLayerWinner()).append("#");
        return builder.toString();
    }
    
    public static String getRtInfoLog(final KfzbRTMatchInfo kfzbRTMatchInfo) {
        if (kfzbRTMatchInfo == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        builder.append("client:");
        builder.append(kfzbRTMatchInfo.getLayer()).append("#");
        builder.append(kfzbRTMatchInfo.getRound()).append("#");
        builder.append(String.valueOf(kfzbRTMatchInfo.getcId1()) + "-" + kfzbRTMatchInfo.getcId2()).append("#");
        builder.append(kfzbRTMatchInfo.getRes()).append("#");
        builder.append(kfzbRTMatchInfo.getState()).append("#");
        builder.append(kfzbRTMatchInfo.getRoundBattleTime()).append("#");
        builder.append(kfzbRTMatchInfo.getLastRoundBattleTime()).append("#");
        return builder.toString();
    }
    
    public static String getPhase2RtMatchInfo(final KfzbBattleInfo battleInfo) {
        if (battleInfo == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        builder.append("batInfo#").append(battleInfo.getMatchId()).append("#").append(battleInfo.getRound());
        try {
            final String value = Types.OBJECT_MAPPER.writeValueAsString(battleInfo);
            builder.append(value);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e2) {
            e2.printStackTrace();
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        return builder.toString();
    }
}
