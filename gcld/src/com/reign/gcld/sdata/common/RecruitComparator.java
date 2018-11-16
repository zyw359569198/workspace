package com.reign.gcld.sdata.common;

import java.util.*;

public class RecruitComparator implements Comparator<RecruitInfo>
{
    @Override
    public int compare(final RecruitInfo arg0, final RecruitInfo arg1) {
        return (arg0.getDropIndex() > arg1.getDropIndex()) ? 1 : 0;
    }
}
