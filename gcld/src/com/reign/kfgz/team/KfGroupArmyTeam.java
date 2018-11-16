package com.reign.kfgz.team;

import com.reign.kf.match.sdata.domain.*;
import com.reign.kfgz.control.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kfgz.resource.*;
import com.reign.framework.json.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;
import java.util.*;
import com.reign.kfgz.comm.*;

public class KfGroupArmyTeam extends KfTeam
{
    public static final int GROUPTEAM_STATE_START = 1;
    public static final int GROUPTEAM_STATE_DISMISS = 3;
    int toTeamId;
    String createPlayerName;
    int createCId;
    int createPLv;
    long createTime;
    int worldLegionId;
    int ownerAddExp;
    int maxNum;
    String createPic;
    int groupTeamState;
    double inspireEffect;
    private boolean order;
    
    public KfGroupArmyTeam(final int gzId, final int forceId, final KfPlayerInfo pInfo, final WorldLegion wl) {
        super(3, KfTeam.getGroupArmyTeamId(), gzId, KfgzManager.getKfWorldByGzId(gzId).getCapitals().get(forceId).getTeamId());
        this.groupTeamState = 0;
        this.setCreatePLv(pInfo.getPlayerLevel());
        this.setOwnerAddExp(SerialCache.getValue(wl.getOwnerExpS(), pInfo.getPlayerLevel()));
        this.setForceId(forceId);
        this.setCreateTime(System.currentTimeMillis());
        this.setCreatePlayerName(pInfo.getPlayerName());
        this.setCreateCId(pInfo.getCompetitorId());
        this.setMaxNum(wl.getMax());
        this.setWorldLegionId(wl.getId());
        this.setCreatePic(pInfo.getPic());
        KfgzGroupTeamManager.addNewTeam(this);
        this.setGroupTeamState(1);
    }
    
    public int getToTeamId() {
        return this.toTeamId;
    }
    
    public void setToTeamId(final int toTeamId) {
        this.toTeamId = toTeamId;
    }
    
    public String getCreatePlayerName() {
        return this.createPlayerName;
    }
    
    public void setCreatePlayerName(final String createPlayerName) {
        this.createPlayerName = createPlayerName;
    }
    
    public int getCreateCId() {
        return this.createCId;
    }
    
    public void setCreateCId(final int createCId) {
        this.createCId = createCId;
    }
    
    public long getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final long createTime) {
        this.createTime = createTime;
    }
    
    public int getCreatePLv() {
        return this.createPLv;
    }
    
    public void setCreatePLv(final int createPLv) {
        this.createPLv = createPLv;
    }
    
    public int getWorldLegionId() {
        return this.worldLegionId;
    }
    
    public void setWorldLegionId(final int worldLegionId) {
        this.worldLegionId = worldLegionId;
    }
    
    public int getOwnerAddExp() {
        return this.ownerAddExp;
    }
    
    public void setOwnerAddExp(final int ownerAddExp) {
        this.ownerAddExp = ownerAddExp;
    }
    
    public int getMaxNum() {
        return this.maxNum;
    }
    
    public void setMaxNum(final int maxNum) {
        this.maxNum = maxNum;
    }
    
    public String getCreatePic() {
        return this.createPic;
    }
    
    public void setCreatePic(final String createPic) {
        this.createPic = createPic;
    }
    
    public int getGroupTeamState() {
        return this.groupTeamState;
    }
    
    public void setGroupTeamState(final int groupTeamState) {
        this.groupTeamState = groupTeamState;
    }
    
    public double getInspireEffect() {
        return this.inspireEffect;
    }
    
    public void setInspireEffect(final double inspireEffect) {
        this.inspireEffect = inspireEffect;
    }
    
    public boolean isOrder() {
        return this.order;
    }
    
    public void setOrder(final boolean order) {
        this.order = order;
    }
    
    public int[] getTotalForces() {
        int num = 0;
        int num2 = 0;
        for (final KfGeneralInfo gInfo : this.defGList) {
            num += gInfo.getCampArmy().getArmyHp();
            num2 += gInfo.getCampArmy().getArmyHpOrg();
        }
        return new int[] { num, num2 };
    }
    
    public int getCurNum() {
        return this.defGList.size();
    }
    
    public boolean isActive() {
        return this.groupTeamState == 1;
    }
    
    @Override
    public void addGeneral(final KfGeneralInfo KfGeneralInfo) {
        try {
            this.teamLock.writeLock().lock();
            super.addGeneral(KfGeneralInfo);
            KfGeneralInfo.setCityPos(KfgzManager.getKfWorldByGzId(this.gzId).getCapitals().get(KfGeneralInfo.getpInfo().getForceId()).getCityId());
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
    }
    
    public void dismissTeam() {
        try {
            this.teamLock.writeLock().lock();
            while (this.defGList.size() > 0) {
                final KfGeneralInfo gInfo = this.defGList.get(0);
                final KfTeam toTeam = KfgzManager.getKfWorldByGzId(this.gzId).getCities().get(gInfo.getCityPos());
                this.removeGeneral(gInfo);
                toTeam.addGeneral(gInfo);
            }
            this.setGroupTeamState(3);
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
    }
    
    public void leaveGeneral(final KfGeneralInfo gInfo) {
        try {
            this.teamLock.writeLock().lock();
            final KfTeam toTeam = KfgzManager.getKfWorldByGzId(this.gzId).getCities().get(gInfo.getCityPos());
            this.removeGeneral(gInfo);
            toTeam.addGeneral(gInfo);
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
    }
    
    public void doBattle(final KfTeam toTeam, final int teamBatType) {
        try {
            this.teamLock.writeLock().lock();
            final WorldLegion wl = WorldLegionCache.getWorldLegionById(this.getWorldLegionId());
            final Set<Integer> cIdSet = new HashSet<Integer>();
            if (teamBatType == 1) {
                while (this.defGList.size() > 0) {
                    final KfGeneralInfo gInfo = this.defGList.get(0);
                    final int foodAdd = SerialCache.getValue(wl.getPFoodS(), gInfo.getpInfo().getPlayerLevel());
                    KfgzResChangeManager.addResource(gInfo.getpInfo().getCompetitorId(), foodAdd, "food", "\u56fd\u6218\u96c6\u56e2\u519b\u7cae\u98df");
                    this.removeGeneral(gInfo);
                    if (this.inspireEffect > 0.0) {
                        gInfo.getCampArmy().setTeamEffect(this.inspireEffect);
                        gInfo.getCampArmy().setTeamGenreal("\u96c6\u56e2\u519b");
                    }
                    toTeam.addGeneral(gInfo);
                    gInfo.pushDirectMove();
                    cIdSet.add(gInfo.getpInfo().getCompetitorId());
                }
            }
            else if (teamBatType == 2) {
                while (this.defGList.size() > 0) {
                    final KfGeneralInfo gInfo = this.defGList.get(0);
                    final int foodAdd = SerialCache.getValue(wl.getPFoodS(), gInfo.getpInfo().getPlayerLevel());
                    KfgzResChangeManager.addResource(gInfo.getpInfo().getCompetitorId(), foodAdd, "food", "\u56fd\u6218\u96c6\u56e2\u519b\u7cae\u98df");
                    this.removeGeneral(gInfo);
                    if (this.inspireEffect > 0.0) {
                        gInfo.getCampArmy().setTeamEffect(this.inspireEffect);
                        gInfo.getCampArmy().setTeamGenreal("\u96c6\u56e2\u519b");
                    }
                    toTeam.addGeneral(gInfo);
                    gInfo.pushDirectMove();
                    toTeam.battle.doSolo(gInfo);
                    cIdSet.add(gInfo.getpInfo().getCompetitorId());
                }
            }
            this.setGroupTeamState(3);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.endObject();
            for (final int cId : cIdSet) {
                if (cId != this.createCId) {
                    KfgzMessageSender.sendMsgToOne(cId, doc.toByte(), PushCommand.PUSH_KF_GROUPTEAMSTART);
                }
            }
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
    }
    
    public void doOrder() {
        try {
            this.teamLock.writeLock().lock();
            for (final KfGeneralInfo gInfo : this.defGList) {
                final KfCampArmy ca = gInfo.getCampArmy();
                ca.Mubing((ca.getArmyHpOrg() - ca.getArmyHp() > 0) ? (ca.getArmyHpOrg() - ca.getArmyHp()) : 0);
            }
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
    }
}
