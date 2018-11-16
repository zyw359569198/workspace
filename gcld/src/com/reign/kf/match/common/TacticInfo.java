package com.reign.kf.match.common;

import java.util.*;

public class TacticInfo
{
    public boolean exeTactic;
    public boolean beStop;
    public int curTactic;
    public int nextTactic;
    public int startTactic;
    public int midTactic;
    public int addTactic;
    public int tacticId;
    public int tacticDisplayId;
    public String tacticStr;
    public String specialEffect;
    public int firstCReduce;
    public int allCReduce;
    public Map<Integer, Integer> reduceMap;
    
    public TacticInfo() {
        this.exeTactic = false;
        this.beStop = false;
        this.curTactic = 0;
        this.nextTactic = 0;
        this.startTactic = 0;
        this.midTactic = 0;
        this.addTactic = 0;
        this.tacticId = 0;
    }
}
