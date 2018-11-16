package com.reign.gcld.general.domain;

import java.util.*;
import com.reign.gcld.player.domain.*;

public class PlayerTrainningInfoDto implements Comparable<PlayerTrainningInfoDto>
{
    private String playerName;
    private Integer playerId;
    private int playerLv;
    private int trainningState;
    private Date beginTimeDate;
    private Integer playerPic;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getPlayerPic() {
        return this.playerPic;
    }
    
    public void setPlayerPic(final Integer playerPic) {
        this.playerPic = playerPic;
    }
    
    public PlayerTrainningInfoDto(final Player player, final Integer trainningState, final Date beginTime, final Integer playerPic) {
        this.setPlayerName(player.getPlayerName());
        this.setPlayerLv(player.getPlayerLv());
        this.setTrainningState(trainningState);
        this.setBeginTimeDate(beginTime);
        this.setPlayerPic(playerPic);
        this.setPlayerId(player.getPlayerId());
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
    }
    
    public int getTrainningState() {
        return this.trainningState;
    }
    
    public void setTrainningState(final int trainningState) {
        this.trainningState = trainningState;
    }
    
    @Override
    public int compareTo(final PlayerTrainningInfoDto o) {
        if (o == null) {
            return 1;
        }
        if (this.trainningState > o.getTrainningState()) {
            return 0;
        }
        return 1;
    }
    
    public void setBeginTimeDate(final Date beginTimeDate) {
        this.beginTimeDate = beginTimeDate;
    }
    
    public Date getBeginTimeDate() {
        return this.beginTimeDate;
    }
}
