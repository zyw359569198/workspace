package com.reign.kfzb.dto.response;

import com.reign.kfzb.dto.request.*;
import org.codehaus.jackson.annotate.*;
import com.reign.kfzb.constants.*;
import java.util.concurrent.*;
import java.util.*;

@JsonAutoDetect
public class KfzbRoomInfo
{
    public static final int STATE_WAIT = 1;
    public static final int STATE_FINISH = 2;
    public static final int STATE_NONE = 3;
    public static final int BUFF_STATE_HAS = 1;
    private long roomId;
    private int buff;
    private Date createDate;
    List<KfzbFeastParticipator> list;
    
    public KfzbRoomInfo() {
        this.list = new ArrayList<KfzbFeastParticipator>();
    }
    
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }
    
    public List<KfzbFeastParticipator> getList() {
        return this.list;
    }
    
    public void setList(final List<KfzbFeastParticipator> list) {
        this.list = list;
    }
    
    public long getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final long roomId) {
        this.roomId = roomId;
    }
    
    @JsonIgnore
    public int getRoomState() {
        final int roomNum = this.list.size();
        if (roomNum >= 10) {
            return 2;
        }
        if (this.createDate == null || this.createDate.getTime() + 180000L < new Date().getTime()) {
            return 3;
        }
        return 1;
    }
    
    public int getBuff() {
        return this.buff;
    }
    
    public void setBuff(final int buff) {
        this.buff = buff;
    }
    
    @JsonIgnore
    public boolean hasBuff() {
        return this.buff == 1;
    }
    
    @JsonIgnore
    public int getRoomRank() {
        return KfzbFeastConstants.getRankIdByRoomId(this.getRoomId());
    }
    
    public static void main(final String[] args) throws InterruptedException {
        final Map<Long, KfzbRoomInfo> roomMap = new ConcurrentHashMap<Long, KfzbRoomInfo>();
        final Map<Integer, List<KfzbRoomInfo>> roomListMap = new ConcurrentHashMap<Integer, List<KfzbRoomInfo>>();
        final long num = 390000L;
        final Date now = new Date();
        for (long i = 0L; i < num; ++i) {
            final KfzbRoomInfo room = new KfzbRoomInfo();
            room.setBuff(1);
            room.setCreateDate(now);
            final List<KfzbFeastParticipator> list = new ArrayList<KfzbFeastParticipator>();
            for (int j = 0; j < 10; ++j) {
                final KfzbFeastParticipator pa = new KfzbFeastParticipator();
                pa.setPlayerName("\u6211\u662f\u8c01\u6211\u662f\u8c01");
                pa.setServerId("3213");
                pa.setServerName("\u6211\u662f\u8c01\u6211\u662f\u8c01");
                list.add(pa);
            }
            room.setList(list);
            roomMap.put(num, room);
        }
        Thread.sleep(1000000L);
    }
}
