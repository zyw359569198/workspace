package com.reign.kfwd.dto;

import com.reign.kfwd.domain.*;

public class KfwdRuntimeMatchResult
{
    private KfwdRuntimeMatch match;
    private String report;
    
    public KfwdRuntimeMatch getMatch() {
        return this.match;
    }
    
    public void setMatch(final KfwdRuntimeMatch match) {
        this.match = match;
    }
    
    public String getReport() {
        return this.report;
    }
    
    public void setReport(final String report) {
        this.report = report;
    }
}
