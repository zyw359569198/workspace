package com.reign.gcld.duel.cache;

import org.springframework.stereotype.*;
import java.util.*;
import com.reign.gcld.duel.model.*;
import java.util.concurrent.*;

@Component("duelsCache")
public class DuelsCache
{
    private Map<Integer, List<Duel>> duelMap;
    
    public DuelsCache() {
        this.duelMap = new ConcurrentHashMap<Integer, List<Duel>>();
    }
    
    public void put(final int playerId, final List<Duel> duelList) {
        this.duelMap.put(playerId, duelList);
    }
    
    public List<Duel> getDuelList(final int playerId) {
        return this.duelMap.get(playerId);
    }
    
    public void remove(final int playerId) {
        this.duelMap.remove(playerId);
    }
    
    public void removeAll() {
        this.duelMap.clear();
    }
}
