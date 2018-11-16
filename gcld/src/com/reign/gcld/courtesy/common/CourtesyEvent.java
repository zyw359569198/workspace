package com.reign.gcld.courtesy.common;

import java.util.concurrent.atomic.*;

public class CourtesyEvent
{
    public static AtomicInteger atomicInteger;
    public static final int TYPE_1_HANDLE = 1;
    public static final int TYPE_2_REPLY = 2;
    public static final int STATE_1_NEED_HANDLE = 1;
    public static final int STATE_2_HANDLED = 2;
    public int id;
    public int playerId;
    public String playerName;
    public int playerPic;
    public int playerLv;
    public int eventId;
    public int type;
    public int state;
    
    static {
        CourtesyEvent.atomicInteger = new AtomicInteger(0);
    }
}
