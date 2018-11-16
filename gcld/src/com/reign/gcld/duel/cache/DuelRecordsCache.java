package com.reign.gcld.duel.cache;

import org.springframework.stereotype.*;
import java.util.*;
import com.reign.gcld.duel.model.*;
import java.util.concurrent.*;

@Component("duelRecordsCache")
public class DuelRecordsCache
{
    private Map<Integer, Queue<Record>> duelRecord;
    
    public DuelRecordsCache() {
        this.duelRecord = new ConcurrentHashMap<Integer, Queue<Record>>();
    }
    
    public void put(final int playerId, final Record record) {
        Queue<Record> queue = this.duelRecord.get(playerId);
        if (queue == null) {
            queue = new ConcurrentLinkedQueue<Record>();
            this.duelRecord.put(playerId, queue);
        }
        if (queue.size() >= 7) {
            queue.poll();
        }
        queue.add(record);
    }
    
    public Queue<Record> getRecords(final int playerId) {
        return this.duelRecord.get(playerId);
    }
    
    public void clear() {
        this.duelRecord.clear();
    }
}
