package com.reign.kfgz.ai.behaviour;

import com.reign.kfgz.control.*;
import com.reign.kfgz.team.*;
import com.reign.framework.json.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;
import com.reign.kfgz.ai.event.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kfgz.constants.*;
import com.reign.kfgz.comm.*;
import com.reign.kfgz.world.*;
import com.reign.kf.match.sdata.domain.*;

public class MoveGeneral extends Behaviour
{
    private KfGeneralAI gAi;
    private KfGeneralInfo gInfo;
    private int fromCity;
    private int toCity;
    private boolean needCheckFromCity;
    private long executeTime;
    
    public MoveGeneral(final KfGeneralInfo gInfo) {
        this.gInfo = gInfo;
    }
    
    public KfGeneralInfo getgInfo() {
        return this.gInfo;
    }
    
    public void setgInfo(final KfGeneralInfo gInfo) {
        this.gInfo = gInfo;
    }
    
    public void setFromCity(final int fromCity) {
        this.fromCity = fromCity;
    }
    
    public int getFromCity() {
        return this.fromCity;
    }
    
    public void setToCity(final int toCity) {
        this.toCity = toCity;
    }
    
    public int getToCity() {
        return this.toCity;
    }
    
    public void setNeedCheckFromCity(final boolean needCheckFromCity) {
        this.needCheckFromCity = needCheckFromCity;
    }
    
    public boolean isNeedCheckFromCity() {
        return this.needCheckFromCity;
    }
    
    @Override
    public void run() {
        final KfPlayerInfo pInfo = this.gInfo.getpInfo();
        final int gzId = pInfo.getGzId();
        if (this.gAi != this.gInfo.getGeneralAI()) {
            return;
        }
        if (this.gInfo.getState() != 1) {
            return;
        }
        if (this.needCheckFromCity && this.fromCity != this.gInfo.getCityPos()) {
            return;
        }
        if (this.fromCity == this.toCity && this.gInfo.getGeneralState() == 6) {
            this.gInfo.easySetGeneralStateWhenMove();
            this.gInfo.pushHpData();
        }
        final KfWorld world = KfgzManager.getKfWorldByGzId(gzId);
        final KfCity cFrom = world.getCities().get(this.fromCity);
        final KfCity cTo = world.getCities().get(this.toCity);
        KfgzConstants.doLockCities(cFrom, cTo);
        Label_0681: {
            try {
                if (this.gAi == this.gInfo.getGeneralAI()) {
                    if (this.gInfo.getState() == 1) {
                        if (!(this.gInfo.getTeam() instanceof KfGroupArmyTeam)) {
                            if (!this.needCheckFromCity || this.fromCity == this.gInfo.getCityPos()) {
                                final KfgzWorldRoad road = world.getByCityIds(this.fromCity, this.toCity);
                                if (road == null) {
                                    final JsonDocument doc = new JsonDocument();
                                    doc.startObject();
                                    doc.createElement("gid", this.gInfo.getgId());
                                    doc.createElement("fromCity", this.fromCity);
                                    doc.createElement("toCity", this.fromCity);
                                    doc.createElement("currentRatio", 0);
                                    doc.createElement("generalState", this.gInfo.getGeneralState());
                                    doc.endObject();
                                    final byte[] result = doc.toByte();
                                    KfgzMessageSender.sendMsgToOne(pInfo.getCompetitorId(), result, PushCommand.PUSH_KF_WORLD_GENERALMOVE);
                                    return;
                                }
                                final long now = System.currentTimeMillis();
                                if (this.gInfo.getCanMoveTime() > now) {
                                    final AIEvent event = new AIEvent();
                                    event.setType(3);
                                    this.gAi.nextBehaviour(event);
                                    return;
                                }
                                cFrom.removeGeneral(this.gInfo);
                                cTo.addGeneral(this.gInfo);
                                this.gInfo.setStartMoveCity(this.fromCity);
                                this.gInfo.setStartMoveTime(now);
                                final Troop t = TroopCache.getTroopCacheById(this.gInfo.getCampArmy().getTroopId());
                                final long cd = KfgzCommConstants.getNextMoveCd(road.getLength(), t.getSpeed());
                                this.gInfo.setCanMoveTime(now + cd);
                                if (this.gInfo.getState() == 1) {
                                    this.gInfo.setGeneralState(6);
                                }
                                final JsonDocument doc2 = new JsonDocument();
                                doc2.startObject();
                                doc2.createElement("gid", this.gInfo.getgId());
                                doc2.createElement("fromCity", this.fromCity);
                                doc2.createElement("toCity", this.toCity);
                                doc2.createElement("currentRatio", 0);
                                doc2.createElement("generalState", this.gInfo.getGeneralState());
                                doc2.endObject();
                                final byte[] result2 = doc2.toByte();
                                KfgzMessageSender.sendMsgToOne(pInfo.getCompetitorId(), result2, PushCommand.PUSH_KF_WORLD_GENERALMOVE);
                                final AIEvent event2 = new AIEvent();
                                event2.setType(1);
                                this.gAi.nextBehaviour(event2);
                                break Label_0681;
                            }
                        }
                    }
                }
                return;
            }
            finally {
                KfgzConstants.doUnlockCities(cFrom, cTo);
            }
        }
        KfgzConstants.doUnlockCities(cFrom, cTo);
    }
    
    public void setgAi(final KfGeneralAI gAi) {
        this.gAi = gAi;
    }
    
    public KfGeneralAI getgAi() {
        return this.gAi;
    }
    
    @Override
    public long getExecuteTime() {
        return this.executeTime;
    }
    
    @Override
    public void setExecuteTime(final long executeTime) {
        this.executeTime = executeTime;
    }
}
