package com.reign.kfgz.team;

import com.reign.kf.match.common.*;

public class KfSoloTeam extends KfTeam
{
    public KfSoloTeam(final int gzId, final int cityId, final int terrain, final int terrainVal) {
        super(2, KfTeam.soloTeamId.addAndGet(1), gzId, cityId);
        this.terrain = terrain;
        switch (this.terrainVal = terrainVal) {
            case 1: {
                this.terrainName = LocalMessages.TERRAIN_NAME_1;
                break;
            }
            case 2: {
                this.terrainName = LocalMessages.TERRAIN_NAME_2;
                break;
            }
            case 3: {
                this.terrainName = LocalMessages.TERRAIN_NAME_3;
                break;
            }
            case 4: {
                this.terrainName = LocalMessages.TERRAIN_NAME_4;
                break;
            }
        }
    }
}
