package com.reign.gcld.player.dto;

import com.reign.gcld.player.common.*;
import java.util.*;

public class PlayerDto
{
    public String userId;
    public int playerId;
    public String playerName;
    public int playerLv;
    public String yx;
    public String yxSource;
    public long loginTime;
    public int forceId;
    public int consumeLv;
    public long copyArmyReportTime;
    public char[] cs;
    public int gm;
    public PlatForm platForm;
    public Date createTime;
    
    public PlayerDto() {
    }
    
    public PlayerDto(final int playerId) {
        this.playerId = playerId;
    }
    
    public PlayerDto(final int playerId, final int forceId) {
        this.playerId = playerId;
        this.forceId = forceId;
    }
}
