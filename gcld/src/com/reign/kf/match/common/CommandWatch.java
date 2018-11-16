package com.reign.kf.match.common;

import com.reign.util.*;
import java.util.*;

public class CommandWatch
{
    private Map<Integer, Tuple<Integer, Long>> watch;
    
    public CommandWatch() {
        this.watch = new HashMap<Integer, Tuple<Integer, Long>>();
    }
    
    public void addWatch(final int command, final long time) {
        Tuple<Integer, Long> tuple = this.watch.get(command);
        if (tuple == null) {
            tuple = new Tuple(0, 0L);
            this.watch.put(command, tuple);
        }
        final Tuple<Integer, Long> tuple2 = tuple;
        tuple2.left = tuple2.left + 1;
        final Tuple<Integer, Long> tuple3 = tuple;
        tuple3.right = tuple3.right + time;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(100);
        final Set<Map.Entry<Integer, Tuple<Integer, Long>>> entrySet = this.watch.entrySet();
        for (final Map.Entry<Integer, Tuple<Integer, Long>> entry : entrySet) {
            builder.append("[c:").append(entry.getKey()).append(",n:").append(entry.getValue().left).append(",t:").append(entry.getValue().right).append("]");
        }
        return builder.toString();
    }
}
