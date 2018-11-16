package com.reign.kfzb.notice;

import java.util.concurrent.*;
import com.reign.kfzb.dto.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;

public class KfzbTicketRewardNoticeInfo
{
    static ConcurrentHashMap<String, LinkedBlockingQueue<KfzbTicketReward>> ticketNoticeMap;
    public static final int MAXPOLLNUM = 300;
    
    static {
        KfzbTicketRewardNoticeInfo.ticketNoticeMap = new ConcurrentHashMap<String, LinkedBlockingQueue<KfzbTicketReward>>();
    }
    
    public static void addNoticeInfo(final KfzbTicketReward kfzbTicketReward) {
    }
    
    public static void addTicketRewardInfo(final List<Response> responseList, final String gameServer) {
    }
    
    public static void main(final String[] args) {
        final String gameServer = "aaa";
        List<Response> responseList;
        do {
            responseList = new ArrayList<Response>();
            addTicketRewardInfo(responseList, gameServer);
        } while (responseList.size() != 0);
    }
}
