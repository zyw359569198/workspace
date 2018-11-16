package com.reign.gcld.common.util.characterFilter;

import com.reign.gcld.common.util.characterFilter.dataGetter.*;
import com.reign.gcld.common.log.*;
import java.io.*;

public class KeyWordsVersionCheckerVersionFile implements IKeyWordsVersionChecker
{
    Logger logger;
    private KeyWordsVersion version;
    private IDataGetter dataGetter;
    
    public KeyWordsVersionCheckerVersionFile(final IDataGetter dataGetter) {
        this.logger = CommonLog.getLog(KeyWordsVersionCheckerVersionFile.class);
        this.dataGetter = dataGetter;
        this.version = new KeyWordsVersion();
    }
    
    private KeyWordsVersion getCurrentVersion() {
        KeyWordsVersion rtn = null;
        final BufferedReader br = this.dataGetter.getData();
        if (br != null) {
            try {
                final String versionStr = br.readLine();
                rtn = new KeyWordsVersion(versionStr);
                br.close();
            }
            catch (IOException e) {
                this.logger.error("IO\u5f02\u5e38!", e);
            }
        }
        return rtn;
    }
    
    @Override
    public void setVersion() {
        final KeyWordsVersion versionOnline = this.getCurrentVersion();
        if (versionOnline != null) {
            this.version = versionOnline;
        }
    }
    
    @Override
    public boolean needUpdate() {
        final KeyWordsVersion versionOnline = this.getCurrentVersion();
        return this.version.compareTo(versionOnline) < 0;
    }
    
    @Override
    public String getVersion() {
        return this.version.getVersion();
    }
    
    class KeyWordsVersion implements Comparable<KeyWordsVersion>
    {
        private int firstVersion;
        private int secondVersion;
        private int thirdVersion;
        private int lastVersion;
        
        public KeyWordsVersion() {
            this.firstVersion = 0;
            this.secondVersion = 0;
            this.thirdVersion = 0;
            this.lastVersion = 0;
        }
        
        public KeyWordsVersion(final String versionStr) {
            this.firstVersion = 0;
            this.secondVersion = 0;
            this.thirdVersion = 0;
            this.lastVersion = 0;
            this.setVersion(versionStr);
        }
        
        public KeyWordsVersion(final int firstVersion, final int secondVersion, final int thirdVersion, final int lastVersion) {
            this.firstVersion = firstVersion;
            this.secondVersion = secondVersion;
            this.thirdVersion = thirdVersion;
            this.lastVersion = lastVersion;
        }
        
        public int getFirstVersion() {
            return this.firstVersion;
        }
        
        public void setFirstVersion(final int firstVersion) {
            this.firstVersion = firstVersion;
        }
        
        public int getSecondVersion() {
            return this.secondVersion;
        }
        
        public void setSecondVersion(final int secondVersion) {
            this.secondVersion = secondVersion;
        }
        
        public int getThirdVersion() {
            return this.thirdVersion;
        }
        
        public void setThirdVersion(final int thirdVersion) {
            this.thirdVersion = thirdVersion;
        }
        
        public int getLastVersion() {
            return this.lastVersion;
        }
        
        public void setLastVersion(final int lastVersion) {
            this.lastVersion = lastVersion;
        }
        
        private void setVersion(final String versionStr) {
            if (versionStr == null || versionStr.isEmpty()) {
                return;
            }
            final String[] s = versionStr.split("\\.");
            if (s.length > 0) {
                this.firstVersion = Integer.parseInt(s[0]);
            }
            if (s.length > 1) {
                this.secondVersion = Integer.parseInt(s[1]);
            }
            if (s.length > 2) {
                this.thirdVersion = Integer.parseInt(s[2]);
            }
            if (s.length > 3) {
                this.lastVersion = Integer.parseInt(s[3]);
            }
        }
        
        public String getVersion() {
            return String.valueOf(this.firstVersion) + "." + this.secondVersion + "." + this.thirdVersion + "." + this.lastVersion;
        }
        
        @Override
        public int compareTo(final KeyWordsVersion kwv) {
            if (kwv == null) {
                return -1;
            }
            if (this.firstVersion > kwv.getFirstVersion()) {
                return 1;
            }
            if (this.firstVersion < kwv.getFirstVersion()) {
                return -1;
            }
            if (this.secondVersion > kwv.getSecondVersion()) {
                return 1;
            }
            if (this.secondVersion < kwv.getSecondVersion()) {
                return -1;
            }
            if (this.thirdVersion > kwv.getThirdVersion()) {
                return 1;
            }
            if (this.thirdVersion < kwv.getThirdVersion()) {
                return -1;
            }
            if (this.lastVersion > kwv.getLastVersion()) {
                return 1;
            }
            if (this.lastVersion < kwv.getLastVersion()) {
                return -1;
            }
            return 0;
        }
    }
}
