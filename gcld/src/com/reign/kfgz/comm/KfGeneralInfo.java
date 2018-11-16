package com.reign.kfgz.comm;

import com.reign.kfgz.team.*;
import com.reign.framework.json.*;
import com.reign.kfgz.resource.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;
import java.util.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;

public class KfGeneralInfo
{
    public static final int STATE_INBATTLETEAM = 2;
    public static final int STATE_INBATTLEFIGHT = 3;
    public static final int STATE_INNORMALTEAM = 1;
    public static Comparator<KfGeneralInfo> compare;
    public int gId;
    public KfTeam team;
    public int cityPos;
    private volatile int state;
    private volatile int generalState;
    public KfPlayerInfo pInfo;
    public KfCampArmy campArmy;
    private int startMoveCity;
    private long startMoveTime;
    private long canMoveTime;
    private List<Integer> cityList;
    public KfGeneralAI generalAI;
    
    static {
        KfGeneralInfo.compare = new Comparator<KfGeneralInfo>() {
            @Override
            public int compare(final KfGeneralInfo o1, final KfGeneralInfo o2) {
                final int gLv1 = o1.getCampArmy().getGeneralLv();
                final int gLv2 = o2.getCampArmy().getGeneralLv();
                if (gLv1 < gLv2) {
                    return 1;
                }
                if (gLv1 > gLv2) {
                    return -1;
                }
                if (o1.getCampArmy().getQuality() < o2.getCampArmy().getQuality()) {
                    return 1;
                }
                if (o1.getCampArmy().getQuality() > o2.getCampArmy().getQuality()) {
                    return -1;
                }
                final int gId1 = o1.getgId();
                final int gId2 = o2.getgId();
                if (gId1 < gId2) {
                    return 1;
                }
                if (gId1 > gId2) {
                    return -1;
                }
                return 0;
            }
        };
    }
    
    public void easySetGeneralState() {
        if (this.state == 1 && (this.generalState == 1 || this.generalState == 0)) {
            if (this.campArmy.needHp() > 0) {
                this.generalState = 1;
            }
            else {
                this.generalState = 0;
            }
        }
    }
    
    public void easySetGeneralStateWhenMove() {
        if (this.state == 1 && this.generalState == 6) {
            if (this.campArmy.needHp() > 0) {
                this.generalState = 1;
            }
            else {
                this.generalState = 0;
            }
        }
    }
    
    public int needMubingNum() {
        if (KfgzResourceService.getOutput(this.pInfo.getCompetitorId()) <= 0.0) {
            return 0;
        }
        final int needHp = this.campArmy.needHp();
        final double output5m = KfgzResourceService.getOutput(this.pInfo.getCompetitorId()) * 60.0 * 5.0;
        return (int)Math.ceil(needHp / output5m);
    }
    
    public int needMubingSecond() {
        if (KfgzResourceService.getOutput(this.pInfo.getCompetitorId()) <= 0.0) {
            return 0;
        }
        return (int)(this.campArmy.needHp() / KfgzResourceService.getOutput(this.pInfo.getCompetitorId()));
    }
    
    public void createGeneralInfo(final JsonDocument doc) {
        doc.createElement("gid", this.gId);
        if (KfgzResChangeManager.getMubingNum(this.pInfo.getCompetitorId()) <= 0) {
            doc.createElement("isGold", true);
        }
        final General gSdata = GeneralCache.getGeneralById(this.gId);
        if (gSdata != null) {
            doc.createElement("pic", gSdata.getPic());
            doc.createElement("name", gSdata.getName());
        }
        doc.createElement("quality", this.campArmy.getQuality());
        doc.createElement("troopId", this.campArmy.getTroopSerial());
        doc.createElement("gLv", this.campArmy.getGeneralLv());
        doc.createElement("cityId", this.cityPos);
        doc.createElement("hp", this.campArmy.getArmyHp());
        doc.createElement("maxHp", this.campArmy.getArmyHpOrg());
        doc.createElement("generalState", this.generalState);
        doc.createElement("teamId", this.getTeam().getTeamId());
        final long now = System.currentTimeMillis();
        if (this.getCanMoveTime() > now) {
            doc.createElement("startCity", this.getStartMoveCity());
            doc.createElement("moveRatio", (now - this.getStartMoveTime()) / (this.getCanMoveTime() - this.getStartMoveTime()));
            if (this.getCityList() != null && this.getCityList().size() > 0) {
                doc.startArray("cityList");
                for (final int c : this.getCityList()) {
                    doc.startObject();
                    doc.createElement("id", c);
                    doc.endObject();
                }
                doc.endArray();
            }
        }
        else if (this.generalState == 6) {
            this.easySetGeneralStateWhenMove();
        }
        if (this.campArmy.getArmyHpOrg() > this.campArmy.getArmyHp()) {
            doc.createElement("needMubingNum", this.needMubingNum());
        }
        doc.createElement("needMubingSecond", this.needMubingSecond());
    }
    
    public void createLeftGeneralInfo(final JsonDocument doc, final KfHpChangeInfo hpInfo) {
        doc.startObject();
        this.createGeneralInfo(doc);
        if (hpInfo != null) {
            doc.startObject("battleHpInfo");
            doc.createElement("killed", hpInfo.getKilled());
            doc.createElement("beKilled", hpInfo.getHpLost());
            final KfCampArmy oppArmy = hpInfo.getBeKilledCa();
            if (oppArmy != null) {
                final String vsPname = oppArmy.getGeneralInfo().getpInfo().getPlayerName();
                if (vsPname != null) {
                    doc.createElement("vsPname", vsPname);
                }
                final int gId = oppArmy.getGeneralInfo().getgId();
                final General general = GeneralCache.getGeneralById(gId);
                if (general != null) {
                    doc.createElement("vsGname", general.getName());
                    doc.createElement("vsGq", general.getQuality());
                }
            }
            doc.endObject();
        }
        doc.endObject();
    }
    
    public void pushHpData() {
        if (this.pInfo.getCompetitorId() == null || this.pInfo.getCompetitorId() == 0) {
            return;
        }
        if (this.campArmy.isPhantom) {
            return;
        }
        final JsonDocument doc = new JsonDocument();
        this.createLeftGeneralInfo(doc, null);
        final byte[] result = doc.toByte();
        KfgzMessageSender.sendMsgToOne(this.pInfo.getCompetitorId(), result, PushCommand.PUSH_KF_GENERAL_HP);
    }
    
    public void pushDirectMove() {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gid", this.gId);
        doc.createElement("teamId", this.getTeam().getTeamId());
        doc.createElement("cityId", this.cityPos);
        doc.endObject();
        KfgzMessageSender.sendMsgToOne(this.pInfo.getCompetitorId(), doc.toByte(), PushCommand.PUSH_KF_GENERAL_DIRECTMOVE);
    }
    
    public void pushHpDataWithHpChangeInfo(final KfHpChangeInfo hpInfo) {
        if (this.pInfo.getCompetitorId() == null || this.pInfo.getCompetitorId() == 0) {
            return;
        }
        if (this.campArmy.isPhantom) {
            return;
        }
        final JsonDocument doc = new JsonDocument();
        this.createLeftGeneralInfo(doc, hpInfo);
        final byte[] result = doc.toByte();
        KfgzMessageSender.sendMsgToOne(this.pInfo.getCompetitorId(), result, PushCommand.PUSH_KF_GENERAL_HP);
    }
    
    public int getgId() {
        return this.gId;
    }
    
    public void setgId(final int gId) {
        this.gId = gId;
    }
    
    public KfTeam getTeam() {
        return this.team;
    }
    
    public void setTeam(final KfTeam team) {
        this.team = team;
    }
    
    public int getCityPos() {
        return this.cityPos;
    }
    
    public void setCityPos(final int cityPos) {
        this.cityPos = cityPos;
    }
    
    public int getState() {
        return this.state;
    }
    
    public KfPlayerInfo getpInfo() {
        return this.pInfo;
    }
    
    public void setpInfo(final KfPlayerInfo pInfo) {
        this.pInfo = pInfo;
    }
    
    public KfCampArmy getCampArmy() {
        return this.campArmy;
    }
    
    public void setCampArmy(final KfCampArmy campArmy) {
        this.campArmy = campArmy;
    }
    
    public KfGeneralAI getGeneralAI() {
        return this.generalAI;
    }
    
    public void setGeneralAI(final KfGeneralAI generalAI) {
        this.generalAI = generalAI;
    }
    
    public void setCanMoveTime(final long canMoveTime) {
        this.canMoveTime = canMoveTime;
    }
    
    public long getCanMoveTime() {
        return this.canMoveTime;
    }
    
    public void setStartMoveCity(final int startMoveCity) {
        this.startMoveCity = startMoveCity;
    }
    
    public int getStartMoveCity() {
        return this.startMoveCity;
    }
    
    public void setStartMoveTime(final long startMoveTime) {
        this.startMoveTime = startMoveTime;
    }
    
    public long getStartMoveTime() {
        return this.startMoveTime;
    }
    
    public void setCityList(final List<Integer> cityList) {
        this.cityList = cityList;
    }
    
    public List<Integer> getCityList() {
        return this.cityList;
    }
    
    public void setGState(final int state, final int gState) {
        this.state = state;
        if (state == 1 && gState != 1015) {
            if (this.campArmy.needHp() > 0 && KfgzResChangeManager.canConsumeResource(this.pInfo.getCompetitorId(), 1, "food")) {
                this.generalState = 1;
            }
            else {
                this.generalState = 0;
            }
        }
        else {
            this.generalState = gState;
        }
    }
    
    public boolean isPlayerRealGeneral() {
        return this.pInfo.getCompetitorId() > 0 && !this.campArmy.isPhantom;
    }
    
    public int getGeneralState() {
        return this.generalState;
    }
    
    public void setGeneralState(final int generalState) {
        this.generalState = generalState;
    }
    
    public boolean isNotNpc() {
        return this.pInfo.getCompetitorId() > 0;
    }
}
