package com.reign.kfzb.dto.request;

import java.util.*;

public class KfzbRoomKeyList
{
    Set<Long> list;
    
    public KfzbRoomKeyList() {
        this.list = new HashSet<Long>();
    }
    
    public Set<Long> getList() {
        return this.list;
    }
    
    public void setList(final Set<Long> list) {
        this.list = list;
    }
}
