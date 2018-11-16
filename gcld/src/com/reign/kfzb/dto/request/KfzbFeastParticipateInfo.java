package com.reign.kfzb.dto.request;

import java.util.*;

public class KfzbFeastParticipateInfo
{
    List<KfzbFeastParticipator> list;
    
    public KfzbFeastParticipateInfo() {
        this.list = new ArrayList<KfzbFeastParticipator>();
    }
    
    public List<KfzbFeastParticipator> getList() {
        return this.list;
    }
    
    public void setList(final List<KfzbFeastParticipator> list) {
        this.list = list;
    }
}
