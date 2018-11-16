package com.reign.gcld.juben.common;

import com.reign.gcld.general.dto.*;
import com.reign.gcld.scenario.common.*;
import java.util.*;

public class JuBenDto
{
    public int juBen_id;
    public String title;
    public String target;
    public int player_id;
    public int grade;
    public long startTime;
    public long endTime;
    public long overTime;
    public int jieBingCount;
    public int maxJieBingCount;
    public Map<Integer, JuBenCityDto> juBenCityDtoMap;
    public int star;
    public int state;
    public boolean isWin;
    public int capital;
    public int npcCapital;
    public Map<Integer, GeneralMoveDto> generals;
    public List<ScenarioEvent> eventList;
    public long addTime;
    public int player_force_id;
    public long juben_buff_player_duration_time;
    public double juben_buff_player;
    public long juben_buff_npc_duration_time;
    public double juben_buff_npc;
    public long juben_att_def_base_buff_player_duration_time;
    public int juben_att_base_buff_player;
    public int juben_def_base_buff_player;
    public long juben_att_def_base_buff_npc_duration_time;
    public int juben_att_base_buff_npc;
    public int juben_def_base_buff_npc;
    public Set<Integer> roadLinked;
    public int royalJadeBelong;
    public long royalEndTime;
    public int mengdeLocation;
    public boolean isMengdeSafe;
    public Map<Integer, Integer> cityChangeMap;
    
    public JuBenDto() {
        this.juben_buff_player_duration_time = -1L;
        this.juben_buff_player = 1.0;
        this.juben_buff_npc_duration_time = -1L;
        this.juben_buff_npc = 1.0;
        this.juben_att_def_base_buff_player_duration_time = 0L;
        this.juben_att_base_buff_player = 0;
        this.juben_def_base_buff_player = 0;
        this.juben_att_def_base_buff_npc_duration_time = 0L;
        this.juben_att_base_buff_npc = 0;
        this.juben_def_base_buff_npc = 0;
    }
}
