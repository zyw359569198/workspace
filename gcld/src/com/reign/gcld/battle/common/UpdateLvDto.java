package com.reign.gcld.battle.common;

import java.util.*;

public class UpdateLvDto
{
    public int upLv;
    List<UpdateExp> list;
    
    public UpdateLvDto() {
        this.list = new ArrayList<UpdateExp>();
    }
}
