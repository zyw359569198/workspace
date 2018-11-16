package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbPlayerGroupInfo
{
    List<KfzbPlayerGroup> list;
    
    public KfzbPlayerGroupInfo() {
        this.list = new ArrayList<KfzbPlayerGroup>();
    }
    
    public List<KfzbPlayerGroup> getList() {
        return this.list;
    }
    
    public void setList(final List<KfzbPlayerGroup> list) {
        this.list = list;
    }
}
