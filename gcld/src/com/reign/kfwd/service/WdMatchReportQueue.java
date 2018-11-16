package com.reign.kfwd.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import org.codehaus.jackson.type.*;
import org.apache.commons.lang.time.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import org.codehaus.jackson.map.type.*;
import java.util.*;

@Component("wdMatchReportQueue")
public class WdMatchReportQueue
{
    private Log log;
    private BlockingQueue<Report> queue;
    private String writeURLSuffix;
    @Autowired
    IKfwdSeasonService kfwdSeasonService;
    private MonitorThread mt;
    private JavaType type;
    private static final String SUCC = "SUCC";
    private StopWatch stopWatch;
    private StopWatch stopWatch1;
    private static final Object lock;
    
    static {
        lock = new Object();
    }
    
    public WdMatchReportQueue() {
        this.log = LogFactory.getLog(WdMatchReportQueue.class);
        this.queue = new LinkedBlockingQueue<Report>();
        this.writeURLSuffix = "fight!multiWrite.action";
        this.mt = null;
        this.type = TypeFactory.mapType(Map.class, String.class, String.class);
        this.stopWatch = new StopWatch();
        this.stopWatch1 = new StopWatch();
        (this.mt = new MonitorThread()).setDaemon(true);
        this.mt.start();
    }
    
    public void addRequest(final String reportId, final String sReport) {
        if (reportId == null) {
            return;
        }
        this.queue.add(new Report(reportId, sReport));
        if (this.mt == null || !this.mt.isAlive()) {
            synchronized (WdMatchReportQueue.lock) {
                if (this.mt == null || !this.mt.isAlive()) {
                    (this.mt = new MonitorThread()).setDaemon(true);
                    this.mt.start();
                }
            }
            // monitorexit(WdMatchReportQueue.lock)
        }
    }
    
    public void clear() {
    }
    
    public class MonitorThread extends Thread
    {
        @Override
        public void run() {
        }
        
        private void handleException(final List<Report> list) {
            for (final Report re : list) {
                WdMatchReportQueue.this.addRequest(re.getReportId(), re.getReport());
            }
        }
    }
    
    public class Report
    {
        private String reportId;
        private String report;
        
        public String getReportId() {
            return this.reportId;
        }
        
        public void setReportId(final String reportId) {
            this.reportId = reportId;
        }
        
        public String getReport() {
            return this.report;
        }
        
        public void setReport(final String report) {
            this.report = report;
        }
        
        public Report(final String reportId, final String report) {
            this.reportId = reportId;
            this.report = report;
        }
    }
}
