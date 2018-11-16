package com.reign.kfwd.notice;

import java.util.concurrent.*;
import com.reign.kfwd.domain.*;
import java.util.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.kf.comm.protocol.*;

public class KfwdTicketRewardNoticeInfo
{
    static ConcurrentHashMap<String, LinkedBlockingQueue<KfwdTicketReward>> ticketNoticeMap;
    public static final int MAXPOLLNUM = 300;
    
    static {
        KfwdTicketRewardNoticeInfo.ticketNoticeMap = new ConcurrentHashMap<String, LinkedBlockingQueue<KfwdTicketReward>>();
    }
    
    public static void addNoticeInfo(final KfwdTicketReward kfwdTicketReward) {
        final String gameServer = kfwdTicketReward.getGameServer();
        KfwdTicketRewardNoticeInfo.ticketNoticeMap.putIfAbsent(gameServer, new LinkedBlockingQueue<KfwdTicketReward>());
        KfwdTicketRewardNoticeInfo.ticketNoticeMap.get(gameServer).add(kfwdTicketReward);
    }
    
    public static void addTicketRewardInfo(final List<Response> responseList, final String gameServer) {
        final LinkedBlockingQueue<KfwdTicketReward> ticketQueue = KfwdTicketRewardNoticeInfo.ticketNoticeMap.get(gameServer);
        if (ticketQueue == null || ticketQueue.size() <= 0) {
            return;
        }
        final KfwdGameServerRewardInfo rewardInfo = new KfwdGameServerRewardInfo();
        final List<KfwdTicketResultInfo> list = new ArrayList<KfwdTicketResultInfo>();
        rewardInfo.setList(list);
        final Response response = new Response();
        for (int i = 0; i < 300; ++i) {
            final KfwdTicketReward reward = ticketQueue.poll();
            if (reward == null) {
                break;
            }
            final KfwdTicketResultInfo resultInfo = new KfwdTicketResultInfo();
            resultInfo.setCompetitorId(reward.getCompetitorId());
            resultInfo.setGameServer(reward.getGameServer());
            resultInfo.setRewardInfo(reward.getRewardInfo());
            resultInfo.setSeasonId(reward.getSeasonId());
            resultInfo.setScheduleId(reward.getScheduleId());
            resultInfo.setDayReward(reward.getDayReward());
            resultInfo.setDayRanking(reward.getDayRanking());
            resultInfo.setDayTicket(reward.getDayTicket());
            resultInfo.setWinRes(reward.getWinRes());
            list.add(resultInfo);
        }
        response.setCommand(Command.KFWD_GAMESERVERREWARDTICKETINFO);
        response.setMessage(rewardInfo);
        responseList.add(response);
    }
    
    public static void main(final String[] args) {
        final String gameServer = "aaa";
        for (int i = 0; i < 2210; ++i) {
            final KfwdTicketReward kfwd = new KfwdTicketReward();
            kfwd.setCompetitorId(i);
            kfwd.setGameServer(gameServer);
            addNoticeInfo(kfwd);
        }
        KfwdGameServerRewardInfo rInfo;
        do {
            final List<Response> responseList = new ArrayList<Response>();
            addTicketRewardInfo(responseList, gameServer);
            if (responseList.size() == 0) {
                return;
            }
            rInfo = (KfwdGameServerRewardInfo)responseList.get(0).getMessage();
            System.out.println(rInfo.getList().size());
        } while (rInfo.getList().size() != 0);
    }
}
