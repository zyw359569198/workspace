package com.reign.kfwd.notice;

import java.util.concurrent.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.kf.comm.entity.kfwd.request.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;

public class KfwdDayBattleEndNoticeInfo
{
    static ConcurrentHashMap<Integer, KfwdDayBattleEndNotice> dayEndNoticeMap;
    static ConcurrentHashMap<String, Integer> gameSeverGetDayMap;
    
    static {
        KfwdDayBattleEndNoticeInfo.dayEndNoticeMap = new ConcurrentHashMap<Integer, KfwdDayBattleEndNotice>();
        KfwdDayBattleEndNoticeInfo.gameSeverGetDayMap = new ConcurrentHashMap<String, Integer>();
    }
    
    public static void addDayResult(final int seasonId, final int scheduleId, final int lastDay, final List<KfwdPlayerInfo> topList) {
        final KfwdDayBattleEndNotice info = new KfwdDayBattleEndNotice();
        info.setDay(lastDay);
        info.setScheduleId(scheduleId);
        info.setSeasonId(seasonId);
        info.setList(topList);
        KfwdDayBattleEndNoticeInfo.dayEndNoticeMap.put(scheduleId, info);
    }
    
    public static void addTicketRewardInfo(final List<Response> responseList, final String serverKey) {
        for (final Map.Entry<Integer, KfwdDayBattleEndNotice> entry : KfwdDayBattleEndNoticeInfo.dayEndNoticeMap.entrySet()) {
            final int scheduleId = entry.getKey();
            final KfwdDayBattleEndNotice noticeInfo = entry.getValue();
            final String key = getKey(serverKey, scheduleId);
            final Integer dayget = KfwdDayBattleEndNoticeInfo.gameSeverGetDayMap.get(key);
            if (dayget == null || dayget < noticeInfo.getDay()) {
                KfwdDayBattleEndNoticeInfo.gameSeverGetDayMap.put(key, noticeInfo.getDay());
                final Response response = new Response();
                response.setCommand(Command.KFWD_GAMESERVERDAYBATTLEENDNOTICE);
                response.setMessage(noticeInfo);
                responseList.add(response);
            }
        }
    }
    
    private static String getKey(final String serverKey, final int scheduleId) {
        return String.valueOf(scheduleId) + "#" + serverKey;
    }
    
    public static void clearAll() {
        KfwdDayBattleEndNoticeInfo.dayEndNoticeMap.clear();
        KfwdDayBattleEndNoticeInfo.gameSeverGetDayMap.clear();
    }
}
