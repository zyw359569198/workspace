package com.reign.gcld.courtesy.common;

import java.util.*;

public class PlayerCourtesyObj implements Comparable<PlayerCourtesyObj>
{
    public int playerId;
    public int playerLv;
    public int stage;
    public int recommendCount;
    public int newlyEventId;
    public int openPanelCount;
    public boolean inXinShouYInDao;
    public boolean liYiDuReachMax;
    public List<CourtesyEvent> needHandleList;
    public Set<Integer> first5s;
    public static final int MAX_NEED_HANDLE_SIZE = 4;
    public static final int STAGE_0 = 0;
    public static final int STAGE_1 = 1;
    public static final int STAGE_2 = 2;
    
    public PlayerCourtesyObj() {
        this.stage = 0;
        this.newlyEventId = 0;
        this.openPanelCount = 0;
        this.inXinShouYInDao = false;
        this.liYiDuReachMax = false;
        this.needHandleList = new LinkedList<CourtesyEvent>();
        this.first5s = new HashSet<Integer>();
    }
    
    public void cutNeedHandleList() {
        while (this.needHandleList.size() > 4) {
            this.needHandleList.remove(this.needHandleList.size() - 1);
        }
    }
    
    @Override
    public int compareTo(final PlayerCourtesyObj playerCourtesyObj) {
        return this.recommendCount - playerCourtesyObj.recommendCount;
    }
}
