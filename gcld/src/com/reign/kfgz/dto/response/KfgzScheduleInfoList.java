package com.reign.kfgz.dto.response;

import java.util.*;

public class KfgzScheduleInfoList
{
    String gameServer;
    List<KfgzScheduleInfoRes> list;
    
    public KfgzScheduleInfoList() {
        this.list = new ArrayList<KfgzScheduleInfoRes>();
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public List<KfgzScheduleInfoRes> getList() {
        return this.list;
    }
    
    public void setList(final List<KfgzScheduleInfoRes> list) {
        this.list = list;
    }
}
