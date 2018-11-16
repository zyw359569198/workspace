package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbPlayerLimitInfo
{
    List<KfzbPlayerLimit> list;
    
    public KfzbPlayerLimitInfo() {
        this.list = new ArrayList<KfzbPlayerLimit>();
    }
    
    public List<KfzbPlayerLimit> getList() {
        return this.list;
    }
    
    public void setList(final List<KfzbPlayerLimit> list) {
        this.list = list;
    }
}
