package com.reign.gcld.rank.common;

import com.reign.gcld.common.*;

public class KillRank
{
    public int levelRank;
    public Node<RankData> levelData;
    public byte[] levelRankByte;
    
    public KillRank() {
        this.levelRank = -1;
        this.levelData = null;
        this.levelRankByte = null;
    }
}
