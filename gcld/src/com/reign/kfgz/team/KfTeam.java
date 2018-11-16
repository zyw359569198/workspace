package com.reign.kfgz.team;

import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import org.apache.commons.logging.*;
import com.reign.framework.json.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kfgz.ai.event.*;
import com.reign.kfgz.comm.*;
import java.util.*;
import com.reign.kfgz.control.*;
import org.springframework.beans.*;
import com.reign.kf.match.sdata.domain.*;

public class KfTeam extends GzLifeCycle
{
    public static final int TEAMTYPE_SOLO = 2;
    public static final int TEAMTYPE_CITY = 1;
    public static final int TEAMTYPE_GROUPARMY = 3;
    private static Log battleReportLog;
    public ReentrantReadWriteLock teamLock;
    public static final int SOLOTEAMBASEID = 10000;
    public static final int GROUPAMYBASEID = 5000;
    public static AtomicInteger groupArmyId;
    public static AtomicInteger soloTeamId;
    public static ReentrantReadWriteLock teamIdLock;
    int teamId;
    int forceId;
    int cityId;
    KfTeamBuffer kfTeamBuffer;
    public KfBattle battle;
    int teamType;
    public int terrain;
    public int terrainVal;
    public String terrainName;
    public List<KfGeneralInfo> attGList;
    public List<KfGeneralInfo> defGList;
    public LinkedBlockingQueue<KfGeneralInfo> kickBackQueue;
    public String teamName;
    
    static {
        KfTeam.battleReportLog = LogFactory.getLog("mj.kfgz.battleReport.log");
        KfTeam.groupArmyId = new AtomicInteger(5000);
        KfTeam.soloTeamId = new AtomicInteger(10000);
        KfTeam.teamIdLock = new ReentrantReadWriteLock();
    }
    
    protected static int getGroupArmyTeamId() {
        KfTeam.teamIdLock.writeLock().lock();
        try {
            int teamId = KfTeam.groupArmyId.getAndAdd(1);
            if (teamId >= 10000) {
                teamId = 5000;
                KfTeam.groupArmyId.set(5000);
            }
            return teamId;
        }
        finally {
            KfTeam.teamIdLock.writeLock().unlock();
        }
    }
    
    public void pushTeamInfo() {
        final JsonDocument doc = new JsonDocument();
        this.createCityInfoDoc(doc);
        final byte[] result = doc.toByte();
        KfgzMessageSender.sendMsgToAll(result, PushCommand.PUSH_KF_WORLD_CITYINFO, this.gzId);
    }
    
    public void createCityInfoDoc(final JsonDocument doc) {
        this.createCityInfoDoc(doc, -1);
    }
    
    public void createCityInfoDoc(final JsonDocument doc, final int cityS) {
        doc.startObject();
        doc.createElement("id", this.getTeamId());
        doc.createElement("force", this.forceId);
        doc.createElement("cityState", this.isInBattle() ? 1 : 0);
        if (cityS != -1) {
            doc.createElement("cityS", cityS);
        }
        if (this.isInBattle()) {
            doc.startArray("battleArmies");
            final Map<Integer, Integer> forceNumMap = new HashMap<Integer, Integer>();
            for (final KfGeneralInfo gInfo : this.attGList) {
                final int forceId = gInfo.getpInfo().getForceId();
                final Integer num = forceNumMap.get(forceId);
                if (num == null) {
                    forceNumMap.put(forceId, 0);
                }
                forceNumMap.put(forceId, forceNumMap.get(forceId) + 1);
            }
            for (final Map.Entry<Integer, Integer> entry : forceNumMap.entrySet()) {
                final int forceId = entry.getKey();
                final int num2 = entry.getValue();
                doc.startObject();
                doc.createElement("isAtt", true);
                doc.createElement("forceId", forceId);
                doc.createElement("num", num2);
                doc.endObject();
            }
            forceNumMap.clear();
            for (final KfGeneralInfo gInfo : this.defGList) {
                final int forceId = gInfo.getpInfo().getForceId();
                final Integer num = forceNumMap.get(forceId);
                if (num == null) {
                    forceNumMap.put(forceId, 0);
                }
                forceNumMap.put(forceId, forceNumMap.get(forceId) + 1);
            }
            for (final Map.Entry<Integer, Integer> entry : forceNumMap.entrySet()) {
                final int forceId = entry.getKey();
                final int num2 = entry.getValue();
                doc.startObject();
                doc.createElement("isAtt", false);
                doc.createElement("forceId", forceId);
                doc.createElement("num", num2);
                doc.endObject();
            }
            doc.endArray();
        }
        doc.endObject();
    }
    
    public void createCityInfoDocSimple(final JsonDocument doc, final int cityS) {
        doc.startObject();
        doc.createElement("id", this.getTeamId());
        final KfgzWorldCity kwc = WorldCityCache.getById(this.getTeamId());
        doc.createElement("type", kwc.getType());
        doc.createElement("exp", kwc.getExp());
        doc.createElement("food", kwc.getFood());
        doc.createElement("iron", kwc.getIron());
        doc.createElement("force", this.forceId);
        doc.createElement("cityState", this.isInBattle() ? 1 : 0);
        if (cityS != -1) {
            doc.createElement("cityS", cityS);
        }
        doc.endObject();
    }
    
    public String getTeamName() {
        return this.teamName;
    }
    
    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }
    
    public int getTeamId() {
        return this.teamId;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    @Override
    public int getGzId() {
        return this.gzId;
    }
    
    @Override
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getTeamType() {
        return this.teamType;
    }
    
    public void setTeamType(final int teamType) {
        this.teamType = teamType;
    }
    
    public void setTeamId(final int teamId) {
        this.teamId = teamId;
    }
    
    public KfTeam(final int teamtype, final int teamId, final int gzId, final int cityId) {
        this.teamLock = new ReentrantReadWriteLock();
        this.terrain = 1;
        this.terrainVal = 1;
        this.attGList = new LinkedList<KfGeneralInfo>();
        this.defGList = new LinkedList<KfGeneralInfo>();
        this.kickBackQueue = new LinkedBlockingQueue<KfGeneralInfo>();
        this.teamId = teamId;
        this.teamType = teamtype;
        this.gzId = gzId;
        this.cityId = cityId;
        KfgzTeamManager.createNewTeam(this);
    }
    
    public KfGeneralInfo removeGeneral(final KfGeneralInfo kfGeneralInfo) {
        try {
            this.teamLock.writeLock().lock();
            final boolean isPhantom = kfGeneralInfo.campArmy.isPhantom();
            KfTeam.battleReportLog.info("team=" + this.teamId + "#remove" + kfGeneralInfo.getCampArmy().getPlayerName() + "#" + kfGeneralInfo.getCampArmy().getGeneralName());
            final boolean attRemove = this.attGList.remove(kfGeneralInfo);
            final boolean defRemove = this.defGList.remove(kfGeneralInfo);
            if (!attRemove && !defRemove) {
                KfTeam.battleReportLog.info("team=" + this.teamId + "#removeerror");
            }
            if (this.teamType == 2 && this.attGList.size() == 0 && this.defGList.size() == 0) {
                this.doEnd();
            }
            if (this.teamType != 2) {
                kfGeneralInfo.getCampArmy().setTeamEffect(0.0);
                kfGeneralInfo.getCampArmy().setTeamGenreal(null);
            }
            return kfGeneralInfo;
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
    }
    
    public void checkAndSetForce() {
        try {
            this.teamLock.writeLock().lock();
            if (this.teamType == 1 && this.defGList.size() == 0 && this.attGList.size() > 0) {
                ((KfCity)this).changeNation(this.attGList.get(0).getpInfo().getForceId());
                final int newforceId = this.forceId;
                final List<KfGeneralInfo> newAttlist = new ArrayList<KfGeneralInfo>();
                final List<KfGeneralInfo> newDeflist = new ArrayList<KfGeneralInfo>();
                for (final KfGeneralInfo gInfo : this.attGList) {
                    if (gInfo.getpInfo().getForceId() != newforceId) {
                        newAttlist.add(gInfo);
                    }
                    else {
                        newDeflist.add(gInfo);
                    }
                }
                this.attGList = newAttlist;
                this.defGList = newDeflist;
            }
            if (this.checkNeedCreateBattle()) {
                this.createAndstartbattle();
            }
            else if (this.teamType == 1) {
                for (final KfGeneralInfo gInfo2 : this.defGList) {
                    final KfGeneralAI gAI = gInfo2.getGeneralAI();
                    if (gAI != null && gAI.getAIName().equals("moveroad")) {
                        final AIEvent event = new AIEvent();
                        event.setType(0);
                        gAI.nextBehaviour(event);
                    }
                }
            }
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
    }
    
    public void addGeneral(final KfGeneralInfo KfGeneralInfo) {
        try {
            this.teamLock.writeLock().lock();
            final boolean isAtt = this.isNewAddGeneralAtt(KfGeneralInfo);
            this.addGeneral(KfGeneralInfo, isAtt);
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
    }
    
    private boolean isNewAddGeneralAtt(final KfGeneralInfo info) {
        return this.isAtt(info.pInfo.getForceId());
    }
    
    public boolean isAtt(final int pforceId) {
        return pforceId != this.forceId;
    }
    
    public void addGeneral(final KfGeneralInfo generalInfo, final boolean isAtt) {
        try {
            this.teamLock.writeLock().lock();
            if (isAtt) {
                this.attGList.add(generalInfo);
            }
            else {
                this.defGList.add(generalInfo);
            }
            int genState = 1;
            if (this.getTeamType() == 3) {
                genState = 1015;
            }
            generalInfo.setGState(1, genState);
            generalInfo.team = this;
            if (this.teamType == 1) {
                generalInfo.setCityPos(this.teamId);
            }
            final boolean inBattle = this.isInBattle();
            if (inBattle) {
                int gState = 1003;
                if (this.getTeamType() == 2) {
                    gState = 1013;
                }
                generalInfo.setGState(2, gState);
                final KfPlayerInfo pInfo = generalInfo.pInfo;
                if (pInfo != null) {
                    final Integer cId = pInfo.getCompetitorId();
                    if (cId != null && cId > 0) {
                        this.battle.buildAndSendNewReport13(cId);
                    }
                }
                this.battle.doCheckAddMembertoBattle(isAtt, generalInfo);
            }
            else {
                final boolean needCreateBattle = this.checkNeedCreateBattle();
                if (needCreateBattle) {
                    this.createAndstartbattle();
                }
                else {
                    this.checkAndSetForce();
                }
            }
            if (this.teamType != 2 && generalInfo.getState() == 1) {
                final KfCampArmy ca = generalInfo.getCampArmy();
                ca.setTacticRemain(false);
                if (ca.getTacicId() > 0) {
                    ca.setTacticVal(1);
                    if (ca.getKfspecialGeneral() != null && ca.getKfspecialGeneral().generalType == 7) {
                        ca.setTacticVal((int)ca.getKfspecialGeneral().param);
                    }
                }
                else {
                    ca.setTacticVal(0);
                }
            }
            generalInfo.pushHpData();
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
    }
    
    private void createAndstartbattle() {
        this.battle = this.createkfBattle();
        this.moveNpcToLast();
        this.setAllGeneralStateToBattleTeam();
        this.battle.iniBattle();
        this.battle.runBattle();
    }
    
    private void moveNpcToLast() {
        final List<KfGeneralInfo> newAttGList = new LinkedList<KfGeneralInfo>();
        final List<KfGeneralInfo> newAttNpcList = new LinkedList<KfGeneralInfo>();
        for (final KfGeneralInfo gInfo : this.attGList) {
            if (gInfo.isNotNpc()) {
                newAttGList.add(gInfo);
            }
            else {
                newAttNpcList.add(gInfo);
            }
        }
        newAttGList.addAll(newAttNpcList);
        final List<KfGeneralInfo> newDefGList = new LinkedList<KfGeneralInfo>();
        final List<KfGeneralInfo> newDefNpcList = new LinkedList<KfGeneralInfo>();
        for (final KfGeneralInfo gInfo2 : this.defGList) {
            if (gInfo2.isNotNpc()) {
                newDefGList.add(gInfo2);
            }
            else {
                newDefNpcList.add(gInfo2);
            }
        }
        newDefGList.addAll(newDefNpcList);
        this.attGList = newAttGList;
        this.defGList = newDefGList;
    }
    
    private void setAllGeneralStateToBattleTeam() {
        int gState = 1003;
        if (this.getTeamType() == 2) {
            gState = 1013;
        }
        for (final KfGeneralInfo gInfo : this.attGList) {
            gInfo.setGState(2, gState);
            gInfo.pushHpData();
        }
        for (final KfGeneralInfo gInfo : this.defGList) {
            gInfo.setGState(2, gState);
            gInfo.pushHpData();
        }
    }
    
    private KfBattle createkfBattle() {
        final KfBattle kfbattle = new KfBattle(this, this.teamId);
        return kfbattle;
    }
    
    public List<KfGeneralInfo> getNewGeneral(final boolean isAtt) {
        try {
            this.teamLock.writeLock().lock();
        }
        finally {
            this.teamLock.writeLock().unlock();
        }
        this.teamLock.writeLock().unlock();
        return null;
    }
    
    private boolean checkNeedCreateBattle() {
        return this.attGList.size() > 0 && this.defGList.size() > 0;
    }
    
    private boolean isInBattle() {
        return this.battle != null && this.battle.getBattleState() == 2;
    }
    
    public List<KfGeneralInfo> getNewBattlePrePareGeneral(final boolean isAtt, int armyNum) {
        final List<KfGeneralInfo> list = new ArrayList<KfGeneralInfo>();
        Iterator<KfGeneralInfo> iter = null;
        if (isAtt) {
            iter = this.attGList.iterator();
        }
        else {
            iter = this.defGList.iterator();
        }
        if (armyNum > 0) {
            while (iter.hasNext()) {
                final KfGeneralInfo gInfo = iter.next();
                if (gInfo.getState() == 2) {
                    list.add(gInfo);
                    final int gArmyNum = gInfo.campArmy.calcuArmyNum();
                    armyNum -= gArmyNum;
                    if (armyNum <= 0) {
                        return list;
                    }
                    continue;
                }
            }
        }
        return list;
    }
    
    public static void main(final String[] args) {
    }
    
    public void processKfKickBack() {
        for (int i = 0; i < 30; ++i) {
            final KfGeneralInfo gInfo = this.kickBackQueue.poll();
            if (gInfo == null) {
                break;
            }
            this.kickBackGeneral(gInfo);
        }
    }
    
    private void kickBackGeneral(final KfGeneralInfo gInfo) {
        if (!gInfo.isPlayerRealGeneral()) {
            return;
        }
        final KfCity toCity = KfgzManager.getKfWorldByGzId(this.gzId).getCapitals().get(gInfo.getpInfo().getForceId());
        if (toCity == null) {
            return;
        }
        toCity.addGeneral(gInfo);
        gInfo.getCampArmy().setTacticRemain(false);
        gInfo.pushDirectMove();
    }
    
    public int getBattleType() {
        if (this.teamType == 1) {
            return 1025;
        }
        if (this.teamType == 2) {
            return 1026;
        }
        return 0;
    }
    
    public void addPhantom(final KfGeneralInfo chooseGInfo) {
        final KfGeneralInfo phantomGInfo = new KfGeneralInfo();
        phantomGInfo.setpInfo(chooseGInfo.getpInfo());
        phantomGInfo.setGeneralAI(null);
        phantomGInfo.setCanMoveTime(0L);
        phantomGInfo.setStartMoveTime(0L);
        phantomGInfo.setTeam(null);
        phantomGInfo.setgId(chooseGInfo.getgId());
        final KfCampArmy phantomCa = new KfCampArmy();
        BeanUtils.copyProperties(chooseGInfo.getCampArmy(), phantomCa);
        phantomCa.setGeneralName(String.valueOf(phantomCa.getGeneralName()) + "\u5e7b\u5f71");
        phantomCa.setGeneralInfo(phantomGInfo);
        phantomCa.isPhantom = true;
        phantomCa.setArmyHp(phantomCa.getArmyHpOrg());
        phantomGInfo.setCampArmy(phantomCa);
        this.addGeneral(phantomGInfo);
    }
    
    public int useDamageStgWithForceId(final KfPlayerInfo pInfo, final Stratagem stg, final int noEffectForceId) {
        if (stg.getType().equals("shuigong") || stg.getType().equals("huogong") || stg.getType().equals("luoshi")) {
            final int damage = stg.getPar2();
            int effectNum = stg.getPar3();
            try {
                this.teamLock.writeLock().lock();
                if (this.battle == null || this.battle.getBattleState() != 2) {
                    return 0;
                }
                final boolean isAtt = this.isAtt(pInfo.getForceId());
                List<KfGeneralInfo> gList = null;
                if (isAtt) {
                    gList = this.attGList;
                }
                else {
                    gList = this.defGList;
                }
                int allDamage = 0;
                for (final KfGeneralInfo gInfo : gList) {
                    if (effectNum <= 0) {
                        break;
                    }
                    if (!gInfo.isNotNpc() || gInfo.getpInfo().getForceId() == noEffectForceId || gInfo.getState() != 2) {
                        continue;
                    }
                    int newHp = gInfo.campArmy.armyHp - damage;
                    if (newHp <= 3) {
                        newHp = 3;
                    }
                    allDamage += gInfo.campArmy.armyHp - newHp;
                    gInfo.campArmy.armyHp = newHp;
                    --effectNum;
                }
                return allDamage;
            }
            finally {
                this.teamLock.writeLock().unlock();
            }
        }
        return 0;
    }
    
    @Override
    public void doEnd() {
        this.battle = null;
        this.attGList = new LinkedList<KfGeneralInfo>();
        this.defGList = new LinkedList<KfGeneralInfo>();
        KfgzTeamManager.moveKTeam(this.teamId, this.gzId);
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
}
