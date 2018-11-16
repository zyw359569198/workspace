package com.reign.gcld.sdata.common;

public class TroopTerrain
{
    private int terrainId;
    private int show;
    private int attQuality;
    private int attEffect;
    private int defEffect;
    private int defQuality;
    
    public int getTerrainId() {
        return this.terrainId;
    }
    
    public void setTerrainId(final int terrainId) {
        this.terrainId = terrainId;
    }
    
    public int getShow() {
        return this.show;
    }
    
    public void setShow(final int show) {
        this.show = show;
    }
    
    public int getAttEffect() {
        return this.attEffect;
    }
    
    public void setAttEffect(final int attEffect) {
        this.attEffect = attEffect;
    }
    
    public int getDefEffect() {
        return this.defEffect;
    }
    
    public void setDefEffect(final int defEffect) {
        this.defEffect = defEffect;
    }
    
    public int getAttQuality() {
        return this.attQuality;
    }
    
    public void setAttQuality(final int attQuality) {
        this.attQuality = attQuality;
    }
    
    public int getDefQuality() {
        return this.defQuality;
    }
    
    public void setDefQuality(final int defQuality) {
        this.defQuality = defQuality;
    }
}
