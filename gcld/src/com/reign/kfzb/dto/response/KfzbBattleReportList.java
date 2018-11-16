package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbBattleReportList
{
    List<KfzbBattleReport> list;
    
    public KfzbBattleReportList() {
        this.list = new ArrayList<KfzbBattleReport>();
    }
    
    public List<KfzbBattleReport> getList() {
        return this.list;
    }
    
    public void setList(final List<KfzbBattleReport> list) {
        this.list = list;
    }
}
