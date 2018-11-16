package com.reign.gcld.battle.common;

public class Terrain
{
    private int display;
    private int value;
    private int terrainPic;
    
    public Terrain() {
    }
    
    public Terrain(final int display, final int value, final int terrainPic) {
        this.display = display;
        this.terrainPic = terrainPic;
        this.value = value;
    }
    
    public int getTerrainPic() {
        return this.terrainPic;
    }
    
    public void setTerrainPic(final int terrainPic) {
        this.terrainPic = terrainPic;
    }
    
    public int getDisplay() {
        return this.display;
    }
    
    public void setDisplay(final int display) {
        this.display = display;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public void setValue(final int value) {
        this.value = value;
    }
}
