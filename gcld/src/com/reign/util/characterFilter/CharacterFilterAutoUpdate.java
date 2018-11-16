package com.reign.util.characterFilter;

import com.reign.util.characterFilter.dataGetter.*;
import com.reign.util.log.*;
import com.reign.util.timer.*;
import java.io.*;

public class CharacterFilterAutoUpdate extends CharacterFilterBase
{
    Logger logger;
    private ICharacterFilter filter;
    private IDataGetter dataGetter;
    private IKeyWordsVersionChecker versionChecker;
    private BaseSystemTimeTimer timer;
    private int checkInterval;
    
    public CharacterFilterAutoUpdate(final ICharacterFilter filter, final IDataGetter dataGetter, final IKeyWordsVersionChecker versionChecker) {
        this.logger = CommonLog.getLog(CharacterFilterAutoUpdate.class);
        this.filter = null;
        this.dataGetter = null;
        this.versionChecker = null;
        this.timer = new BaseSystemTimeTimer();
        this.checkInterval = 300000;
        this.filter = filter;
        this.dataGetter = dataGetter;
        this.versionChecker = versionChecker;
        final BufferedReader br = dataGetter.getData();
        filter.buildFilterKeyWord(br);
        versionChecker.setVersion();
        this.timer.schedule(new CheckVersionTask(this.checkInterval));
    }
    
    public CharacterFilterAutoUpdate(final ICharacterFilter filter, final IDataGetter dataGetter, final IKeyWordsVersionChecker versionChecker, final int checkInterval) {
        this.logger = CommonLog.getLog(CharacterFilterAutoUpdate.class);
        this.filter = null;
        this.dataGetter = null;
        this.versionChecker = null;
        this.timer = new BaseSystemTimeTimer();
        this.checkInterval = 300000;
        this.filter = filter;
        this.dataGetter = dataGetter;
        this.versionChecker = versionChecker;
        this.checkInterval = checkInterval * 1000;
        final BufferedReader br = dataGetter.getData();
        filter.buildFilterKeyWord(br);
        versionChecker.setVersion();
        this.timer.schedule(new CheckVersionTask(checkInterval));
    }
    
    @Override
    public String filter(final String str) {
        return this.filter.filter(str);
    }
    
    @Override
    public boolean isValid(final String str) {
        return this.filter.isValid(str);
    }
    
    @Override
    public void buildFilterKeyWord(final BufferedReader buff) {
        this.filter.buildFilterKeyWord(buff);
    }
    
    @Override
    public void setReplaceCharacterGetter(final IReplaceCharacterGetter replaceCharacterGetter) {
        this.filter.setReplaceCharacterGetter(replaceCharacterGetter);
    }
    
    private class CheckVersionTask extends BaseSystemTimeTimerTask
    {
        public CheckVersionTask() {
            super(System.currentTimeMillis());
        }
        
        public CheckVersionTask(final int interval) {
            super(System.currentTimeMillis() + interval);
        }
        
        @Override
        public void run() {
            try {
                if (CharacterFilterAutoUpdate.this.versionChecker.needUpdate()) {
                    final BufferedReader br = CharacterFilterAutoUpdate.this.dataGetter.getData();
                    CharacterFilterAutoUpdate.this.filter.buildFilterKeyWord(br);
                    CharacterFilterAutoUpdate.this.versionChecker.setVersion();
                }
            }
            catch (Exception e) {
                CharacterFilterAutoUpdate.this.logger.error("\u68c0\u67e5\u7248\u672c\u66f4\u65b0\u5b57\u5e93\u65f6\u53d1\u751f\u95ee\u9898!", e);
            }
            CharacterFilterAutoUpdate.this.timer.schedule(new CheckVersionTask(CharacterFilterAutoUpdate.this.checkInterval));
        }
    }
}
