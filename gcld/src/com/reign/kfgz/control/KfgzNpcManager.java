package com.reign.kfgz.control;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.kfgz.dto.*;
import org.apache.commons.lang.*;
import com.reign.kfgz.team.*;
import java.util.concurrent.*;
import java.util.*;
import java.util.regex.*;
import com.reign.kfgz.world.*;
import com.reign.kf.match.common.*;
import java.text.*;
import com.reign.kfgz.battle.*;
import com.reign.kfgz.ai.event.*;
import com.reign.kfgz.comm.*;
import com.reign.kfgz.ai.constants.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.common.*;
import com.reign.kf.match.sdata.domain.*;
import com.reign.framework.json.*;

@Component
public class KfgzNpcManager
{
    @Autowired
    KfgzNpcCache kfgzNpcCache;
    static ScheduledThreadPoolExecutor exeutors;
    public static Map<Integer, KfgzNpcAddTimeInfo> worldJieBingInfoMap;
    public static Map<Integer, KfgzNpcAddTimeInfo> worldAllyInfoMap;
    
    static {
        KfgzNpcManager.exeutors = new ScheduledThreadPoolExecutor(1);
        KfgzNpcManager.worldJieBingInfoMap = new ConcurrentHashMap<Integer, KfgzNpcAddTimeInfo>();
        KfgzNpcManager.worldAllyInfoMap = new ConcurrentHashMap<Integer, KfgzNpcAddTimeInfo>();
    }
    
    public void ini(final int gzId, final int worldNpcId, final KfgzBaseInfo baseInfo) {
        for (final KfgzNpc npc : this.kfgzNpcCache.getModels()) {
            if (npc.getWorldNpcid() != worldNpcId) {
                continue;
            }
            if (!baseInfo.canChoosenNpcAI() && npc.getAIInfoMap().get("chooseroadlist") != null) {
                baseInfo.setCanChoosenNpcAI(true);
            }
            if (StringUtils.isBlank(npc.getIniCondition())) {
                final String armyInfo = npc.getArmyInfo();
                final int iniPos = npc.getIniCityPos();
                final int forceId = npc.getForceId();
                final String[] ss = armyInfo.split(",");
                String[] array;
                for (int length = (array = ss).length, j = 0; j < length; ++j) {
                    final String armys = array[j];
                    final Pattern pattern0 = Pattern.compile("(\\d+)");
                    final Pattern pattern2 = Pattern.compile("(\\d+)\\*(\\d+)");
                    final Matcher mat0 = pattern0.matcher(armys);
                    final Matcher mat2 = pattern2.matcher(armys);
                    int armyId = 0;
                    int num = 0;
                    if (mat2.find()) {
                        armyId = Integer.parseInt(mat2.group(1));
                        num = Integer.parseInt(mat2.group(2));
                    }
                    else if (mat0.find()) {
                        armyId = Integer.parseInt(mat0.group(1));
                        num = 1;
                    }
                    if (num > 0) {
                        for (int i = 0; i < num; ++i) {
                            final KfPlayerInfo npcPInfo = new KfPlayerInfo(0, gzId);
                            npcPInfo.setForceId(forceId);
                            npcPInfo.setPlayerName("NPC");
                            npcPInfo.setCompetitorId(0);
                            final Army armyCach = ArmyCache.getArmyById(armyId);
                            final General general = GeneralCache.getGeneralById(armyCach.getGeneralId());
                            npcPInfo.setPic(general.getPic());
                            final KfGeneralInfo npcGInfo = new KfGeneralInfo();
                            final KfCampArmy ca = copyArmyFromCach(armyId, 0, 0, armyCach.getGeneralLv(), forceId);
                            ca.setGeneralInfo(npcGInfo);
                            npcGInfo.setCampArmy(ca);
                            npcGInfo.setpInfo(npcPInfo);
                            npcGInfo.setgId(ca.getGeneralId());
                            final KfCity city = KfgzManager.getKfWorldByGzId(gzId).getCities().get(iniPos);
                            if (city == null) {
                                System.out.println("iniPos=" + iniPos + " gzId=" + gzId + " worldId=" + KfgzManager.getWorldIdByGzId(gzId));
                            }
                            city.addGeneral(npcGInfo);
                        }
                    }
                }
            }
            else {
                final Long[] exeTime = npc.getExDelay(baseInfo);
                if (exeTime == null) {
                    continue;
                }
                final KfWorld world = KfgzManager.getKfWorldByGzId(gzId);
                final KfCity city2 = world.getCities().get(npc.getIniCityPos());
                final KfCity force1Capital = world.getCapitals().get(1);
                final KfCity force2Capital = world.getCapitals().get(2);
                if (city2.isJieBingCity() && world.isCityNearBy(force1Capital.getCityId(), city2.getCityId())) {
                    KfgzNpcAddTimeInfo jieBingInfo = KfgzNpcManager.worldJieBingInfoMap.get(gzId);
                    if (jieBingInfo == null) {
                        jieBingInfo = new KfgzNpcAddTimeInfo(gzId);
                        KfgzNpcManager.worldJieBingInfoMap.put(gzId, jieBingInfo);
                    }
                    jieBingInfo.getForce1cityCDMap().put(city2.getCityId(), exeTime);
                }
                else if (city2.isJieBingCity() && world.isCityNearBy(force2Capital.getCityId(), city2.getCityId())) {
                    KfgzNpcAddTimeInfo jieBingInfo = KfgzNpcManager.worldJieBingInfoMap.get(gzId);
                    if (jieBingInfo == null) {
                        jieBingInfo = new KfgzNpcAddTimeInfo(gzId);
                        KfgzNpcManager.worldJieBingInfoMap.put(gzId, jieBingInfo);
                    }
                    jieBingInfo.getForce2cityCDMap().put(city2.getCityId(), exeTime);
                }
                if (city2.isCaptial()) {
                    KfgzNpcAddTimeInfo allyInfo = KfgzNpcManager.worldAllyInfoMap.get(gzId);
                    if (allyInfo == null) {
                        allyInfo = new KfgzNpcAddTimeInfo(gzId);
                        KfgzNpcManager.worldAllyInfoMap.put(gzId, allyInfo);
                    }
                    if (city2.getForceId() == 1) {
                        allyInfo.getForce1cityCDMap().put(city2.getCityId(), exeTime);
                    }
                    else if (city2.getForceId() == 2) {
                        allyInfo.getForce2cityCDMap().put(city2.getCityId(), exeTime);
                    }
                }
                Long[] array2;
                for (int length2 = (array2 = exeTime).length, k = 0; k < length2; ++k) {
                    final Long t = array2[k];
                    final long delay = t - System.currentTimeMillis();
                    final KfgzNpc gzNc = npc;
                    if (delay > 0L) {
                        KfgzNpcManager.exeutors.schedule(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    KfgzNpcManager.this.addNpc(gzNc, gzId);
                                }
                                catch (Exception ex) {}
                            }
                        }, delay, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }
    
    public void addNpc(final KfgzNpc npc, final int gzId) {
        final String armyInfo = npc.getArmyInfo();
        final int iniPos = npc.getIniCityPos();
        final int forceId = npc.getForceId();
        final String[] ss = armyInfo.split(",");
        String[] array;
        for (int length = (array = ss).length, j = 0; j < length; ++j) {
            final String armys = array[j];
            final Pattern pattern0 = Pattern.compile("(\\d+)");
            final Pattern pattern2 = Pattern.compile("(\\d+)\\*(\\d+)");
            final Matcher mat0 = pattern0.matcher(armys);
            final Matcher mat2 = pattern2.matcher(armys);
            int armyId = 0;
            int num = 0;
            if (mat2.find()) {
                armyId = Integer.parseInt(mat2.group(1));
                num = Integer.parseInt(mat2.group(2));
            }
            else if (mat0.find()) {
                armyId = Integer.parseInt(mat0.group(1));
                num = 1;
            }
            final KfCity city = KfgzManager.getKfWorldByGzId(gzId).getCities().get(iniPos);
            final KfWorld world = KfgzManager.getKfWorldByGzId(gzId);
            final KfCity force1Capital = world.getCapitals().get(1);
            final KfCity force2Capital = world.getCapitals().get(2);
            if (city.isJieBingCity()) {
                if (world.isCityNearBy(force1Capital.getCityId(), city.getCityId())) {
                    final String content = MessageFormat.format(LocalMessages.KFGZ_ADD_JIEBING_NPC, WorldCityCache.getById(city.getCityId()).getName());
                    KfgzMessageSender.sendChatToForce(gzId, 1, content);
                }
                else if (world.isCityNearBy(force2Capital.getCityId(), city.getCityId())) {
                    final String content = MessageFormat.format(LocalMessages.KFGZ_ADD_JIEBING_NPC, WorldCityCache.getById(city.getCityId()).getName());
                    KfgzMessageSender.sendChatToForce(gzId, 2, content);
                }
            }
            if (num > 0) {
                for (int i = 0; i < num; ++i) {
                    final KfPlayerInfo npcPInfo = new KfPlayerInfo(0, gzId);
                    npcPInfo.setForceId(forceId);
                    npcPInfo.setPlayerName("NPC");
                    npcPInfo.setCompetitorId(0);
                    final Army armyCach = ArmyCache.getArmyById(armyId);
                    final General general = GeneralCache.getGeneralById(armyCach.getGeneralId());
                    npcPInfo.setPic(general.getPic());
                    final KfGeneralInfo npcGInfo = new KfGeneralInfo();
                    final KfCampArmy ca = copyArmyFromCach(armyId, 0, 0, armyCach.getGeneralLv(), forceId);
                    ca.setGeneralInfo(npcGInfo);
                    npcGInfo.setCampArmy(ca);
                    final String armyAI = npc.getArmyAI();
                    final KfGeneralAI generalAI = this.getArmyGeneralAI(npc, gzId, npcGInfo);
                    npcGInfo.setGeneralAI(generalAI);
                    npcGInfo.setpInfo(npcPInfo);
                    npcGInfo.setgId(ca.getGeneralId());
                    city.addGeneral(npcGInfo);
                    final AIEvent event = new AIEvent();
                    event.setType(0);
                    if (npcGInfo.getGeneralAI() != null) {
                        npcGInfo.getGeneralAI().nextBehaviour(event);
                    }
                }
            }
        }
    }
    
    private KfGeneralAI getArmyGeneralAI(final KfgzNpc npc, final int gzId, final KfGeneralInfo gInfo) {
        final Map<String, String> armyAI = npc.getAIInfoMap();
        final String roadListAI = armyAI.get("chooseroadlist");
        if (roadListAI != null) {
            final String[] roadList = roadListAI.split(",");
            final int res = KfgzNpcAIManager.getNpcAIChooseResult(gzId, npc.getForceId());
            String road = roadList[0];
            if (res > 0 && res <= roadList.length) {
                road = roadList[res - 1];
            }
            final KfGeneralAI gAi = new KfGeneralAI();
            gAi.setgInfo(gInfo);
            gAi.setScript(AIConstants.getMoveRoadCityString(road));
            gInfo.setGeneralAI(gAi);
            final AIEvent event = new AIEvent();
            event.setType(0);
            return gAi;
        }
        return null;
    }
    
    public static KfCampArmy copyArmyFromCach(final int npcId, final int npc_lost, final int id, final int npcLv, final int forceId) {
        final KfCampArmy KfCampArmy = new KfCampArmy();
        final Army armyCach = ArmyCache.getArmyById(npcId);
        KfCampArmy.setPlayerId(0);
        KfCampArmy.setPlayerName("NPC");
        KfCampArmy.setForceId(forceId);
        KfCampArmy.setPlayerLv(npcLv);
        KfCampArmy.setId(id);
        KfCampArmy.setPgmVId(0);
        KfCampArmy.setArmyName(armyCach.getName());
        KfCampArmy.setGeneralId(armyCach.getGeneralId());
        KfCampArmy.setGeneralName(armyCach.getName());
        KfCampArmy.setGeneralLv(armyCach.getGeneralLv());
        final General general = GeneralCache.getGeneralById(armyCach.getGeneralId());
        KfCampArmy.setKfspecialGeneral(new KfSpecialGeneral(general.getGeneralSpecialInfo().generalType, general.getGeneralSpecialInfo().param));
        KfCampArmy.setGeneralPic(general.getPic());
        KfCampArmy.setQuality(general.getQuality());
        KfCampArmy.setStrength(general.getStrength());
        KfCampArmy.setLeader(general.getLeader());
        KfCampArmy.setTacicId(general.getTacticId());
        final Troop troop = TroopCache.getTroopCacheById(general.getTroop());
        KfCampArmy.setTroopId(troop.getId());
        KfCampArmy.setTroopSerial(troop.getSerial());
        KfCampArmy.setTroopType(troop.getType());
        KfCampArmy.setTroopName(troop.getName());
        KfCampArmy.setTroopDropType(BattleDrop.getDropType(troop.getDrop()));
        KfCampArmy.setTroopDrop(troop.getTroopDrop());
        KfCampArmy.setAttEffect(armyCach.getAtt());
        KfCampArmy.setDefEffect(armyCach.getDef());
        KfCampArmy.setBdEffect(armyCach.getBd());
        KfCampArmy.setTroopHp(armyCach.getTroopHp());
        KfCampArmy.setMaxForces(armyCach.getTroopHp());
        int armyHp = armyCach.getArmyHp() - npc_lost;
        final int remainder = armyHp % 3;
        armyHp -= remainder;
        KfCampArmy.setArmyHp(armyHp);
        KfCampArmy.setArmyHpOrg(armyHp);
        if (general.getTacticId() > 0) {
            KfCampArmy.setTacticVal(1);
        }
        KfCampArmy.setColumn(armyCach.getArmyHp() / armyCach.getTroopHp());
        return KfCampArmy;
    }
    
    public static void clear() {
        KfgzNpcManager.worldJieBingInfoMap.clear();
    }
    
    public static String getJieBingInfo(final int gzId, final int forceId) {
        final KfgzNpcAddTimeInfo jieBingInfo = KfgzNpcManager.worldJieBingInfoMap.get(gzId);
        if (jieBingInfo == null) {
            return "";
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("jiebingInfo");
        Map<Integer, Long[]> cdMap = null;
        if (forceId == 1) {
            cdMap = jieBingInfo.getForce1cityCDMap();
        }
        else if (forceId == 2) {
            cdMap = jieBingInfo.getForce2cityCDMap();
        }
        if (cdMap == null) {
            return "";
        }
        for (final Map.Entry<Integer, Long[]> entry : cdMap.entrySet()) {
            doc.startObject();
            final int cityId = entry.getKey();
            final Long[] time = entry.getValue();
            doc.createElement("cityId", cityId);
            doc.createElement("cd", getCDByExtTime(time));
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return doc.toString();
    }
    
    public static String getAllyInfo(final int gzId, final int forceId) {
        final KfgzNpcAddTimeInfo jieBingInfo = KfgzNpcManager.worldAllyInfoMap.get(gzId);
        if (jieBingInfo == null) {
            return "";
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("allyInfo");
        Map<Integer, Long[]> cdMap = null;
        if (forceId == 1) {
            cdMap = jieBingInfo.getForce1cityCDMap();
        }
        else if (forceId == 2) {
            cdMap = jieBingInfo.getForce2cityCDMap();
        }
        if (cdMap == null) {
            return "";
        }
        for (final Map.Entry<Integer, Long[]> entry : cdMap.entrySet()) {
            doc.startObject();
            final int cityId = entry.getKey();
            final Long[] time = entry.getValue();
            doc.createElement("cd", getCDByExtTime(time));
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return doc.toString();
    }
    
    private static long getCDByExtTime(final Long[] time) {
        final long cd = -1L;
        if (time == null || time.length == 0) {
            return cd;
        }
        final Long now = System.currentTimeMillis();
        for (final Long t : time) {
            if (t > now) {
                return t - now;
            }
        }
        return cd;
    }
    
    public static void main(final String[] args) {
        final String s1 = "112101*40";
        final String[] ss = s1.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String armys = array[i];
            final Pattern pattern0 = Pattern.compile("(\\d+)");
            final Pattern pattern2 = Pattern.compile("(\\d+)\\*(\\d+)");
            final Matcher mat0 = pattern0.matcher(armys);
            final Matcher mat2 = pattern2.matcher(armys);
            int armyId = 0;
            int num = 0;
            if (mat2.find()) {
                armyId = Integer.parseInt(mat2.group(1));
                num = Integer.parseInt(mat2.group(2));
            }
            else if (mat0.find()) {
                armyId = Integer.parseInt(mat0.group(1));
                num = 1;
            }
            System.out.println(String.valueOf(armyId) + " " + num);
        }
    }
}
