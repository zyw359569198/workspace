package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbRoomInfoList
{
    List<KfzbRoomInfo> list;
    
    public KfzbRoomInfoList() {
        this.list = new ArrayList<KfzbRoomInfo>();
    }
    
    public List<KfzbRoomInfo> getList() {
        return this.list;
    }
    
    public void setList(final List<KfzbRoomInfo> list) {
        this.list = list;
    }
}
