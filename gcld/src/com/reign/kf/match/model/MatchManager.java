package com.reign.kf.match.model;

import java.util.concurrent.*;
import com.reign.kf.comm.entity.gw.*;
import com.reign.kf.match.service.*;

public class MatchManager
{
    private static final MatchManager instance;
    private ConcurrentMap<String, Match> matchMap;
    
    static {
        instance = new MatchManager();
    }
    
    public MatchManager() {
        this.matchMap = new ConcurrentHashMap<String, Match>();
    }
    
    public static MatchManager getInstance() {
        return MatchManager.instance;
    }
    
    public Match createMatch(final SeasonInfoEntity entity, final IDataGetter dataGetter) {
        this.matchMap.containsKey(entity.getTag());
        final Match match = new Match(entity, dataGetter);
        final Match temp = this.matchMap.putIfAbsent(entity.getTag(), match);
        return (temp == null) ? match : temp;
    }
    
    public Match getMatch(final String tag) {
        return this.matchMap.get(tag);
    }
    
    public void remove(final String tag) {
        this.matchMap.remove(tag);
    }
}
