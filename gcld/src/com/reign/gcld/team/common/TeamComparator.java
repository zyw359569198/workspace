package com.reign.gcld.team.common;

import java.util.*;

public class TeamComparator implements Comparator<TeamMember>
{
    @Override
    public int compare(final TeamMember arg0, final TeamMember arg1) {
        return (arg0.getPlayerLv() > arg1.getPlayerLv()) ? 1 : 0;
    }
}
