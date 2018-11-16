package com.reign.kfgz.battle;

import org.apache.commons.logging.*;
import com.reign.kfgz.control.*;
import java.util.*;
import com.reign.kfgz.comm.*;
import com.reign.kf.match.common.web.session.*;
import com.reign.framework.netty.util.*;
import java.util.concurrent.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.json.*;
import com.reign.kf.match.common.*;

public class KfgzMessageSender
{
    private static Log battleReportLog;
    private static Log gzerrorLog;
    
    static {
        KfgzMessageSender.battleReportLog = LogFactory.getLog("mj.kfgz.battleReport.log");
        KfgzMessageSender.gzerrorLog = LogFactory.getLog("astd.kfgz.log.comm");
    }
    
    public static void sendMsgToAll(final KfBattle kfBattle, final StringBuilder battleMsg) {
        final StringBuilder sb2 = new StringBuilder();
        for (final int cId : kfBattle.inSceneSet) {
            final Integer teamId = KfBattleManager.getPlayerWatchBattleId(cId);
            sb2.append("sendCid#");
            if (teamId != null && teamId.equals(kfBattle.kfTeam.getTeamId())) {
                sendBattleMsgToOne(cId, battleMsg);
            }
        }
        sb2.append("#");
        sb2.append(battleMsg);
        KfgzMessageSender.battleReportLog.info(sb2.toString());
    }
    
    public static void sendMsgToAll(final byte[] msg, final PushCommand command, final int gzId) {
        try {
            final ConcurrentHashMap<Integer, KfPlayerInfo> map = KfgzPlayerManager.getPlayerMapByGz(gzId);
            if (map == null) {
                return;
            }
            for (final Map.Entry<Integer, KfPlayerInfo> entry : map.entrySet()) {
                final KfPlayerInfo pInfo = entry.getValue();
                final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(pInfo.getCompetitorId(), 2));
                if (session != null) {
                    final byte[] bytes = JsonBuilder.getJson(State.PUSH, command.getModule(), msg);
                    session.write(WrapperUtil.wrapper(command.getCommand(), 0, bytes));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendBattleMsgToOne(final int competitorId, final StringBuilder battleMsg) {
        if (competitorId <= 0) {
            return;
        }
        try {
            final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(competitorId, 2));
            if (session != null) {
                final byte[] bytes = JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_BATTLE_DOKFGZBATTLE.getModule(), (Object)battleMsg);
                session.write(WrapperUtil.wrapper(PushCommand.PUSH_BATTLE_DOKFGZBATTLE.getCommand(), 0, bytes));
            }
        }
        catch (Exception e) {
            KfgzMessageSender.gzerrorLog.error(e);
            KfgzMessageSender.gzerrorLog.error("error#cId=" + competitorId + "#" + battleMsg.toString());
        }
    }
    
    public static void sendMsgToOne(final int competitorId, final byte[] msg) {
        try {
            final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(competitorId, 2));
            if (session != null) {
                final byte[] bytes = JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_BATTLE_DOKFGZINFO.getModule(), msg);
                session.write(WrapperUtil.wrapper(PushCommand.PUSH_BATTLE_DOKFGZINFO.getCommand(), 0, bytes));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendMsgToOne(final int competitorId, final byte[] msg, final PushCommand command) {
        try {
            final Session session = Players.getSession(PlayerDto.getUIdByCompetitorIdAndPlayerType(competitorId, 2));
            if (session != null) {
                final byte[] bytes = JsonBuilder.getJson(State.PUSH, command.getModule(), msg);
                session.write(WrapperUtil.wrapper(command.getCommand(), 0, bytes));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void sendMsgToForce(final int gzId, final int forceId, final byte[] msg, final PushCommand command) {
        for (final KfPlayerInfo p : KfgzPlayerManager.getPlayerMapByGz(gzId).values()) {
            if (p.getForceId() == forceId) {
                sendMsgToOne(p.getCompetitorId(), msg, command);
            }
        }
    }
    
    public static void sendChatToForce(final int gzId, final int forceId, final String content) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("from", LocalMessages.SYSTEM_NAME);
        doc.createElement("type", "COUNTRY");
        doc.createElement("msg", content);
        doc.endObject();
        final byte[] msg = doc.toByte();
        sendMsgToForce(gzId, forceId, msg, PushCommand.PUSH_KF_CHAT);
    }
    
    public static void sendChatToAll(final int gzId, final String content) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("from", LocalMessages.SYSTEM_NAME);
        doc.createElement("type", "GLOBAL");
        doc.createElement("msg", content);
        doc.endObject();
        final byte[] msg = doc.toByte();
        sendMsgToAll(msg, PushCommand.PUSH_KF_CHAT, gzId);
    }
    
    public static void sendChatToPlayer(final int cId, final String content) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("from", LocalMessages.SYSTEM_NAME);
        doc.createElement("type", "SYS2ONE");
        doc.createElement("msg", content);
        doc.endObject();
        final byte[] msg = doc.toByte();
        sendMsgToOne(cId, msg, PushCommand.PUSH_KF_CHAT);
    }
    
    public static void sendBigChatToForce(final int gzId, final int forceId, final String content) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("type", "COUNTRY");
        doc.createElement("msg", content);
        doc.endObject();
        final byte[] msg = doc.toByte();
        sendMsgToForce(gzId, forceId, msg, PushCommand.PUSH_KF_NOTICE);
    }
}
