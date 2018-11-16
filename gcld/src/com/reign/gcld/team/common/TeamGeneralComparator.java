package com.reign.gcld.team.common;

import java.util.*;

public class TeamGeneralComparator implements Comparator<GeneralInfo>
{
    @Override
    public int compare(final GeneralInfo arg0, final GeneralInfo arg1) {
        return (arg0.getGeneralLv() > arg1.getGeneralLv()) ? 1 : 0;
    }
}
