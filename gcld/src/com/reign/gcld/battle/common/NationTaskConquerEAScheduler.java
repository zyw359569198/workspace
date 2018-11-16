package com.reign.gcld.battle.common;

import com.reign.gcld.sdata.domain.*;
import java.util.*;

public class NationTaskConquerEAScheduler
{
    public EfLv eflv;
    public long startTime;
    public BitSet bitSet;
    
    public NationTaskConquerEAScheduler() {
        this.eflv = null;
        this.bitSet = new BitSet();
    }
}
