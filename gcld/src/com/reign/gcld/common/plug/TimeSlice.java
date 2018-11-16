package com.reign.gcld.common.plug;

import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.gcld.common.*;
import org.apache.commons.lang.*;
import com.reign.util.*;
import com.reign.gcld.player.action.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.log.*;

public class TimeSlice
{
    private static final Logger log;
    private static final DayReportLogger drLog;
    private static final TimeSlice timeSlice;
    private Map<Integer, short[]> slices;
    private Map<Integer, short[]> exceptionSlices;
    private Map<Integer, PlayerCount> countMap;
    private Map<Integer, Integer> rtBlockMap;
    private Map<Integer, Integer> unBlockCountMap;
    private int cursor;
    private Map<Integer, Long> blockSet;
    private Set<Integer> exceptionBlockSet;
    private Object lock;
    
    static {
        log = CommonLog.getLog(TimeSlice.class);
        drLog = new DayReportLogger();
        timeSlice = new TimeSlice();
    }
    
    public static TimeSlice getInstance() {
        return TimeSlice.timeSlice;
    }
    
    private TimeSlice() {
        this.slices = new ConcurrentHashMap<Integer, short[]>();
        this.exceptionSlices = new ConcurrentHashMap<Integer, short[]>();
        this.countMap = new ConcurrentHashMap<Integer, PlayerCount>();
        this.rtBlockMap = new ConcurrentHashMap<Integer, Integer>();
        this.unBlockCountMap = new ConcurrentHashMap<Integer, Integer>();
        this.cursor = 0;
        this.blockSet = new ConcurrentHashMap<Integer, Long>();
        this.exceptionBlockSet = new HashSet<Integer>();
        this.lock = new Object();
    }
    
    public void init() {
    }
    
    public void put(final int playerId) {
        short[] array = this.slices.get(playerId);
        if (array == null) {
            synchronized (this.lock) {
                if (this.slices.get(playerId) == null) {
                    array = new short[1440];
                    this.slices.put(playerId, array);
                }
            }
            // monitorexit(this.lock)
        }
        final short[] array2 = array;
        final int cursor = this.cursor;
        ++array2[cursor];
    }
    
    public void putException(final int playerId) {
        short[] array = this.exceptionSlices.get(playerId);
        if (array == null) {
            synchronized (this.lock) {
                if (this.exceptionSlices.get(playerId) == null) {
                    array = new short[1440];
                    this.exceptionSlices.put(playerId, array);
                }
            }
            // monitorexit(this.lock)
        }
        final short[] array2 = array;
        final int cursor = this.cursor;
        ++array2[cursor];
    }
    
    public boolean needBlock(final int playerId) {
        if (this.exceptionBlockSet.contains(playerId)) {
            return true;
        }
        if (!this.blockSet.containsKey(playerId)) {
            return false;
        }
        final Date date = new Date();
        final long blockStime = this.blockSet.get(playerId);
        final String invalidTimeStr = Configuration.getProperty("gcld.validate.code.invalid.time");
        int invalidTime = 4;
        if (StringUtils.isNotBlank(invalidTimeStr)) {
            invalidTime = Integer.valueOf(invalidTimeStr);
        }
        if (!CDUtil.isInCD(invalidTime * 60000L, blockStime, date)) {
            this.blockSet.remove(playerId);
            return false;
        }
        if (!CDUtil.isInCD(300000L, blockStime, date) && !ValidateCodeAction.playerCodeMap.containsKey(playerId)) {
            TimeSlice.log.error("needBlock 5min not show validateCode playerId:" + playerId + " blockStime:" + blockStime);
            this.blockSet.remove(playerId);
            return false;
        }
        return true;
    }
    
    public boolean rtBlock(final int playerId, final int flag) {
        if (this.block(playerId, 1)) {
            this.rtBlockMap.put(playerId, flag);
            return true;
        }
        return false;
    }
    
    public int getUnBlockCount(final int playerId) {
        final Integer flag = this.rtBlockMap.get(playerId);
        if (flag != null && flag == 0) {
            final Integer value = this.unBlockCountMap.get(playerId);
            return (value == null) ? 0 : value;
        }
        return -1;
    }
    
    public boolean block(final int playerId, final int type) {
        if (type == 1) {
            final PlayerCount pc = this.countMap.get(playerId);
            if (pc != null && CDUtil.isInCD(pc.nextBlockTime, new Date())) {
                this.blockSet.remove(playerId);
            }
            else if (!this.blockSet.containsKey(playerId)) {
                this.blockSet.put(playerId, System.currentTimeMillis());
                ValidateCodeAction.codeCountMap.remove(playerId);
                return true;
            }
            return false;
        }
        this.exceptionBlockSet.add(playerId);
        ValidateCodeAction.codeCountMap.remove(playerId);
        return true;
    }
    
    public void unBlock(final PlayerDto playerDto, final long nextBlockTime) {
        final int playerId = playerDto.playerId;
        this.blockSet.remove(playerId);
        this.exceptionBlockSet.remove(playerId);
        PlayerCount pc = this.countMap.get(playerId);
        if (pc == null) {
            pc = new PlayerCount();
            this.countMap.put(playerId, pc);
        }
        pc.nextBlockTime = nextBlockTime;
        final Integer flag = this.rtBlockMap.remove(playerId);
        if (flag != null && flag == 0) {
            final Integer value = this.unBlockCountMap.get(playerId);
            this.unBlockCountMap.put(playerId, (value == null) ? 1 : (value + 1));
        }
        TimeSlice.drLog.info(LogUtil.formatUnBlock(playerDto));
    }
    
    public void clear(final int playerId) {
    }
    
    public void clearBlockCountMap() {
        this.unBlockCountMap.clear();
    }
}
