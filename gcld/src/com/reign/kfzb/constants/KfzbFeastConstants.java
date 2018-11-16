package com.reign.kfzb.constants;

public class KfzbFeastConstants
{
    public static final int FEASTROOMSIZE = 10;
    public static final int FEASTROOMIDMASK = 8;
    public static final int FEASTROOMIDRANKMASK = 255;
    
    public static long getRoomIdByRoomAndId(final long room, final long id) {
        return id << 8 | room;
    }
    
    public static int getRankIdByRoomId(final long roomId) {
        return (int)(roomId & 0xFFL);
    }
}
