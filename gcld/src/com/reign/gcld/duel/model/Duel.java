package com.reign.gcld.duel.model;

import com.reign.gcld.battle.common.*;

public class Duel
{
    private int playerId;
    private int score;
    private int index;
    private Terrain terrain;
    
    public Duel(final int playerId, final int score, final int index, final Terrain terrain) {
        this.playerId = playerId;
        this.score = score;
        this.index = index;
        this.terrain = terrain;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public void setTerrain(final Terrain terrain) {
        this.terrain = terrain;
    }
    
    public Terrain getTerrain() {
        return this.terrain;
    }
}
