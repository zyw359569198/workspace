package com.reign.framework.common.appender;

import java.text.*;
import org.apache.log4j.*;
import org.apache.log4j.helpers.*;
import java.util.*;
import org.apache.log4j.spi.*;
import java.io.*;

public class MyDailyRollingFileAppender extends FileAppender
{
    static final int TOP_OF_TROUBLE = -1;
    static final int TOP_OF_MINUTE = 0;
    static final int TOP_OF_HOUR = 1;
    static final int HALF_DAY = 2;
    static final int TOP_OF_DAY = 3;
    static final int TOP_OF_WEEK = 4;
    static final int TOP_OF_MONTH = 5;
    private String datePattern;
    private String scheduledFilename;
    private String defaultFileName;
    private long nextCheck;
    Date now;
    SimpleDateFormat sdf;
    RollingCalendar rc;
    int checkPeriod;
    static final TimeZone gmtTimeZone;
    
    static {
        gmtTimeZone = TimeZone.getTimeZone("GMT");
    }
    
    public MyDailyRollingFileAppender() {
        this.datePattern = "'.'yyyy-MM-dd";
        this.nextCheck = System.currentTimeMillis() - 1L;
        this.now = new Date();
        this.rc = new RollingCalendar();
        this.checkPeriod = -1;
    }
    
    public MyDailyRollingFileAppender(final Layout layout, final String filename, final String datePattern) throws IOException {
        super(layout, filename, true);
        this.datePattern = "'.'yyyy-MM-dd";
        this.nextCheck = System.currentTimeMillis() - 1L;
        this.now = new Date();
        this.rc = new RollingCalendar();
        this.checkPeriod = -1;
        this.datePattern = datePattern;
        this.activateOptions();
    }
    
    public void setDatePattern(final String pattern) {
        this.datePattern = pattern;
    }
    
    public String getDatePattern() {
        return this.datePattern;
    }
    
    @Override
	public void activateOptions() {
        super.activateOptions();
        if (this.datePattern != null && this.fileName != null) {
            this.now.setTime(System.currentTimeMillis());
            this.sdf = new SimpleDateFormat(this.datePattern);
            final int type = this.computeCheckPeriod();
            this.printPeriodicity(type);
            this.rc.setType(type);
            this.defaultFileName = this.fileName;
            final File file = new File(this.fileName);
            if (file.exists()) {
                this.closeFile();
                file.delete();
            }
            String currentFileName = null;
            try {
                currentFileName = String.valueOf(this.fileName) + this.sdf.format(this.now);
                this.setFile(currentFileName, true, this.bufferedIO, this.bufferSize);
            }
            catch (IOException e) {
                this.errorHandler.error("setFile(" + this.fileName + ", true) call failed.");
            }
            this.scheduledFilename = currentFileName;
        }
        else {
            LogLog.error("Either File or DatePattern options are not set for appender [" + this.name + "].");
        }
    }
    
    void printPeriodicity(final int type) {
        switch (type) {
            case 0: {
                LogLog.debug("Appender [" + this.name + "] to be rolled every minute.");
                break;
            }
            case 1: {
                LogLog.debug("Appender [" + this.name + "] to be rolled on top of every hour.");
                break;
            }
            case 2: {
                LogLog.debug("Appender [" + this.name + "] to be rolled at midday and midnight.");
                break;
            }
            case 3: {
                LogLog.debug("Appender [" + this.name + "] to be rolled at midnight.");
                break;
            }
            case 4: {
                LogLog.debug("Appender [" + this.name + "] to be rolled at start of week.");
                break;
            }
            case 5: {
                LogLog.debug("Appender [" + this.name + "] to be rolled at start of every month.");
                break;
            }
            default: {
                LogLog.warn("Unknown periodicity for appender [" + this.name + "].");
                break;
            }
        }
    }
    
    int computeCheckPeriod() {
        final RollingCalendar rollingCalendar = new RollingCalendar(MyDailyRollingFileAppender.gmtTimeZone, Locale.getDefault());
        final Date epoch = new Date(0L);
        if (this.datePattern != null) {
            for (int i = 0; i <= 5; ++i) {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
                simpleDateFormat.setTimeZone(MyDailyRollingFileAppender.gmtTimeZone);
                final String r0 = simpleDateFormat.format(epoch);
                rollingCalendar.setType(i);
                final Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
                final String r2 = simpleDateFormat.format(next);
                if (r0 != null && r2 != null && !r0.equals(r2)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    void rollOver() throws IOException {
        if (this.datePattern == null) {
            this.errorHandler.error("Missing DatePattern option in rollOver().");
            return;
        }
        final String datedFilename = String.valueOf(this.defaultFileName) + this.sdf.format(this.now);
        if (this.scheduledFilename.equals(datedFilename)) {
            return;
        }
        this.closeFile();
        final File target = new File(datedFilename);
        if (target.exists()) {
            target.delete();
        }
        try {
            this.setFile(datedFilename, true, this.bufferedIO, this.bufferSize);
        }
        catch (IOException e) {
            this.errorHandler.error("setFile(" + this.fileName + ", true) call failed.");
        }
        this.scheduledFilename = datedFilename;
    }
    
    @Override
	protected void subAppend(final LoggingEvent event) {
        final long n = System.currentTimeMillis();
        if (n >= this.nextCheck) {
            this.now.setTime(n);
            this.nextCheck = this.rc.getNextCheckMillis(this.now);
            try {
                this.rollOver();
            }
            catch (IOException ioe) {
                if (ioe instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("rollOver() failed.", ioe);
            }
        }
        super.subAppend(event);
    }
}
