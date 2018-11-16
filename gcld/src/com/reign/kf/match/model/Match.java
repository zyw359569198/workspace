package com.reign.kf.match.model;

import com.reign.kf.match.service.*;
import java.util.concurrent.atomic.*;
import java.util.*;
import com.reign.kf.match.log.*;
import com.reign.kf.comm.entity.gw.*;
import java.util.concurrent.*;
import com.reign.kf.match.domain.*;
import com.reign.util.timer.*;
import com.reign.util.*;
import com.reign.kf.match.common.*;

public class Match
{
    private static final Logger log;
    private int state;
    private static MatchTimer timer;
    private MatchConfig config;
    private IDataGetter dataGetter;
    private MatchCacheManager cacheManager;
    private String matchTag;
    private Map<Integer, AtomicInteger> counter;
    private Map<Integer, Date> matchTimerMap;
    private Map<Integer, Date> matchLastTimerMap;
    private volatile boolean cancel;
    
    static {
        log = CommonLog.getLog(Match.class);
        Match.timer = new MatchTimer(Runtime.getRuntime().availableProcessors() * 2);
    }
    
    public Match(final SeasonInfoEntity entity, final IDataGetter dataGetter) {
        this.config = MatchConfig.config(entity);
        this.dataGetter = dataGetter;
        this.matchTag = entity.getTag();
        this.counter = new ConcurrentHashMap<Integer, AtomicInteger>();
        this.matchTimerMap = new ConcurrentHashMap<Integer, Date>();
        this.matchLastTimerMap = new ConcurrentHashMap<Integer, Date>();
        this.cacheManager = new MatchCacheManager(this.matchTag, this, dataGetter);
    }
    
    public void recover() {
        final Date nowDate = new Date();
        if (!nowDate.after(this.config.getSignEndTime())) {
            this.init();
            return;
        }
        final GcldMatch firstMatch = this.dataGetter.getGcldMatchDao().getFirstMatch(this.getMatchTag(), 1);
        if (firstMatch == null) {
            this.addScheduleMatchTask(this);
        }
        else {
            try {
                this.dataGetter.getMatchService().recover(this);
                this.state = 4;
            }
            catch (Exception e) {
                Match.log.error("recover error", e);
            }
        }
    }
    
    public void init() {
        this.state = 0;
        Match.timer.addTask(new BaseSystemTimeTimerTask(this.config.getSignStartTime().getTime()) {
            @Override
			public void run() {
                Match.log.info("\u6bd4\u8d5b\u5f00\u59cb\u62a5\u540d");
                Match.access$1(Match.this, 1);
            }
        });
        Match.timer.addTask(new BaseSystemTimeTimerTask(this.config.getSignEndTime().getTime()) {
            @Override
			public void run() {
                Match.log.info("\u6bd4\u8d5b\u62a5\u540d\u7ed3\u675f");
                Match.access$1(Match.this, 2);
                Match.this.dataGetter.getMatchService().scheduleMatch(Match.this, 1);
                if (Match.this.state < 3) {
                    Match.access$1(Match.this, 3);
                }
                Match.log.info("\u6bd4\u8d5b\u5b89\u6392\u7ed3\u675f");
            }
        });
    }
    
    public synchronized boolean cancel() {
        if (this.cancel) {
            return true;
        }
        this.clear();
        return true;
    }
    
    public int getState() {
        return this.state;
    }
    
    public MatchConfig getMatchConfig() {
        return this.config;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public long getCD() {
        final Date nowDate = new Date();
        if (this.state == 0) {
            return CDUtil.getCD(this.config.getSignStartTime(), nowDate);
        }
        if (this.state == 1) {
            return CDUtil.getCD(this.config.getSignEndTime(), nowDate);
        }
        return 0L;
    }
    
    public MatchCacheManager getCache() {
        return this.cacheManager;
    }
    
    public boolean hasNextTurn(final int turn) {
        return turn < this.config.getMaxTurn();
    }
    
    public Date getNextTurnTime(final int turn) {
        final Date date = this.matchTimerMap.get(turn);
        return new Date(date.getTime());
    }
    
    public Date getTurnLastTime(final int turn) {
        final Date date = this.matchLastTimerMap.get(turn);
        return new Date(date.getTime());
    }
    
    public void setMatchOver() {
        this.state = 6;
        Match.timer.addTask(new BaseSystemTimeTimerTask(System.currentTimeMillis() + 86400000L) {
            @Override
			public void run() {
                Match.this.clear();
            }
        });
    }
    
    private void clear() {
        this.getCache().clear();
        this.matchTimerMap.clear();
        this.matchLastTimerMap.clear();
        this.counter.clear();
        Match.timer.stop();
        MatchManager.getInstance().remove(this.getMatchTag());
    }
    
    private void addScheduleMatchTask(final Match match) {
        Match.timer.addTask(new BaseSystemTimeTimerTask(this.config.getSignEndTime().getTime()) {
            @Override
			public void run() {
                Match.this.dataGetter.getMatchService().scheduleMatch(match, 1);
                if (Match.this.state < 3) {
                    Match.access$1(Match.this, 3);
                }
                Match.log.info("\u6bd4\u8d5b\u5b89\u6392\u7ed3\u675f");
            }
        });
    }
    
    public void addMatch(final GcldMatch gcldMatch) {
        AtomicInteger atomicInteger = this.counter.get(gcldMatch.getTurn());
        if (atomicInteger == null) {
            synchronized (this.counter) {
                atomicInteger = this.counter.get(gcldMatch.getTurn());
                if (atomicInteger == null) {
                    atomicInteger = new AtomicInteger(0);
                    this.counter.put(gcldMatch.getTurn(), atomicInteger);
                }
            }
            // monitorexit(this.counter)
        }
        final Date date = this.matchTimerMap.get(gcldMatch.getTurn());
        if (date == null) {
            this.matchTimerMap.put(gcldMatch.getTurn(), gcldMatch.getMatchTime());
        }
        else if (date.before(gcldMatch.getMatchTime())) {
            this.matchTimerMap.put(gcldMatch.getTurn(), gcldMatch.getMatchTime());
        }
        atomicInteger.incrementAndGet();
        Match.timer.addTask(new MatchTask(this, gcldMatch));
        Match.log.info("\u6dfb\u52a0\u4e00\u573a\u6bd4\u8d5b:[id:" + gcldMatch.getId() + ", matchTime:" + gcldMatch.getMatchTime() + "]");
    }
    
    static /* synthetic */ void access$1(final Match match, final int state) {
        match.state = state;
    }
    
    public static class MatchConfig
    {
        private Date signStartTime;
        private Date signEndTime;
        private Date matchTime;
        private int season;
        private int maxTurn;
        
        public MatchConfig(final SeasonInfoEntity entity) {
            this.signStartTime = entity.getSignStartTime();
            this.signEndTime = entity.getSignEndTime();
            this.matchTime = entity.getMatchTime();
            this.season = entity.getSeason();
            this.maxTurn = entity.getMaxTurn();
        }
        
        public int getMaxTurn() {
            return this.maxTurn;
        }
        
        public static MatchConfig config(final SeasonInfoEntity entity) {
            final MatchConfig config = new MatchConfig(entity);
            return config;
        }
        
        public Date getSignEndTime() {
            return this.signEndTime;
        }
        
        public Date getSignStartTime() {
            return this.signStartTime;
        }
        
        public int getSeason() {
            return this.season;
        }
        
        public Date getMatchTime() {
            return this.matchTime;
        }
        
        public int getStepMatchNum() {
            return Configuration.getIntProperty("gcld.kfmatch.stepMatchNum");
        }
        
        public int getStepInterval() {
            return Configuration.getIntProperty("gcld.kfmatch.stepInterval");
        }
        
        public int getMatchInterval() {
            return Configuration.getIntProperty("gcld.kfmatch.matchInterval") + this.getPrepareMatchSec();
        }
        
        public int getPrepareMatchSec() {
            return Configuration.getIntProperty("gcld.kfmatch.prepareMatchSec");
        }
    }
    
    private class MatchTask extends BaseSystemTimeTimerTask
    {
        private GcldMatch gcldMatch;
        private Match match;
        
        public MatchTask(final Match match, final GcldMatch gcldMatch) {
            super(gcldMatch.getMatchTime().getTime() - 1000 * match.getMatchConfig().getPrepareMatchSec());
            this.match = match;
            this.gcldMatch = gcldMatch;
        }
        
        @Override
		public void run() {
            try {
                if (Match.this.state < 4) {
                    Match.access$1(Match.this, 4);
                    Match.log.info("\u6bd4\u8d5b\u8fdb\u884c\u4e2d");
                }
                Match.log.info("\u6267\u884c\u6bd4\u8d5b\u5f00\u59cb:[id:" + this.gcldMatch.getId() + "] start" + "time:" + new Date());
                if (this.gcldMatch.getPlayer2() == 0) {
                    Match.this.dataGetter.getMatchService().startMatch(this.match, this.gcldMatch.getId());
                    this.match.getCache().clearMatchCache(this.gcldMatch.getId());
                    final Date date = Match.this.matchLastTimerMap.get(this.gcldMatch.getTurn());
                    if (date == null) {
                        Match.this.matchLastTimerMap.put(this.gcldMatch.getTurn(), new Date());
                    }
                }
                else {
                    final MatchResult result = Match.this.dataGetter.getMatchService().startMatch(this.match, this.gcldMatch.getId());
                    this.match.getCache().updateMatchReport(result);
                    this.match.getCache().clearMatchCache(this.gcldMatch.getId());
                    final Date date2 = Match.this.matchLastTimerMap.get(this.gcldMatch.getTurn());
                    if (date2 == null) {
                        Match.this.matchLastTimerMap.put(this.gcldMatch.getTurn(), result.lastTime);
                    }
                    else if (date2.before(this.gcldMatch.getMatchTime())) {
                        Match.this.matchLastTimerMap.put(this.gcldMatch.getTurn(), result.lastTime);
                    }
                }
                Match.log.info("\u6267\u884c\u6bd4\u8d5b\u7ed3\u675f:[id:" + this.gcldMatch.getId() + "] end");
                final AtomicInteger atomicInteger = Match.this.counter.get(this.gcldMatch.getTurn());
                final int remain = atomicInteger.decrementAndGet();
                if (remain == 0) {
                    Match.this.dataGetter.getMatchService().scheduleMatch(this.match, this.gcldMatch.getTurn() + 1);
                }
            }
            catch (Throwable t) {
                Match.log.error("", t);
                final MatchResult result2 = Match.this.dataGetter.getMatchService().handleMatchException(this.match, this.gcldMatch.getId());
                this.match.getCache().updateMatchReport(result2);
                final AtomicInteger atomicInteger2 = Match.this.counter.get(this.gcldMatch.getTurn());
                final int remain2 = atomicInteger2.decrementAndGet();
                if (remain2 == 0) {
                    Match.this.dataGetter.getMatchService().scheduleMatch(this.match, this.gcldMatch.getTurn() + 1);
                }
            }
        }
    }
}
