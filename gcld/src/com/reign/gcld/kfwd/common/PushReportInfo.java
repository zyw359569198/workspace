package com.reign.gcld.kfwd.common;

public class PushReportInfo
{
    private int reportIndex;
    private String report;
    private boolean inScene;
    
    public PushReportInfo(final int reportIndex, final String report) {
        this.inScene = false;
        this.reportIndex = reportIndex;
        this.report = report;
    }
    
    public int getReportIndex() {
        return this.reportIndex;
    }
    
    public void setReportIndex(final int reportIndex) {
        this.reportIndex = reportIndex;
    }
    
    public String getReport() {
        return this.report;
    }
    
    public void setReport(final String report) {
        this.report = report;
    }
    
    public boolean isInScene() {
        return this.inScene;
    }
    
    public void setInScene(final boolean inScene) {
        this.inScene = inScene;
    }
}
