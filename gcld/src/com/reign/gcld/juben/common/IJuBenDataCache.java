package com.reign.gcld.juben.common;

import java.util.*;

public interface IJuBenDataCache
{
    List<Integer> getMinPath(final int p0, final int p1, final int p2, final int[] p3);
    
    List<Integer> getMinPathJuBen(final int p0, final int p1, final int p2, final int[] p3, final int[] p4, final int[] p5);
    
    void initPath();
}
