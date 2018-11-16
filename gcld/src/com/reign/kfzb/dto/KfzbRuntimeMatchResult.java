package com.reign.kfzb.dto;

import com.reign.kfzb.domain.*;

public class KfzbRuntimeMatchResult
{
    private KfzbRuntimeMatch match;
    private String report;
    
    public KfzbRuntimeMatch getMatch() {
        return this.match;
    }
    
    public void setMatch(final KfzbRuntimeMatch match) {
        this.match = match;
    }
    
    public String getReport() {
        return this.report;
    }
    
    public void setReport(final String report) {
        this.report = report;
    }
}
