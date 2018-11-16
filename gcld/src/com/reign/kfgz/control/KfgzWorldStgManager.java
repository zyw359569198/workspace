package com.reign.kfgz.control;

import org.springframework.stereotype.*;
import com.reign.kfgz.dto.*;
import com.reign.framework.json.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;
import java.util.concurrent.*;
import com.reign.kfgz.team.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kfgz.comm.*;
import java.util.*;
import com.reign.kf.match.sdata.domain.*;

@Component
public class KfgzWorldStgManager
{
    static ScheduledThreadPoolExecutor exeutors;
    static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, KfgzWorldStgDto>> stgMap;
    
    static {
        KfgzWorldStgManager.exeutors = new ScheduledThreadPoolExecutor(1);
        KfgzWorldStgManager.stgMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, KfgzWorldStgDto>>();
    }
    
    public void ini(final int gzId, final int worldstgId) {
        final HashMap<Integer, KfgzWorldStg> wsMap = KfgzWorldStgCache.getWordStgsByGzId(worldstgId);
        if (wsMap == null) {
            return;
        }
        KfgzWorldStgManager.stgMap.put(gzId, new ConcurrentHashMap<Integer, KfgzWorldStgDto>());
        final KfgzBaseInfo baseInfo = KfgzManager.getGzBaseInfoById(gzId);
        final long iniDelay = baseInfo.getGzStartTime().getTime() - new Date().getTime();
        for (final Map.Entry<Integer, KfgzWorldStg> entry : wsMap.entrySet()) {
            final KfgzWorldStg wstg = entry.getValue();
            long delay = this.getWordStgExcuteDelay(wstg);
            if (delay >= 0L) {
                if (iniDelay > 0L) {
                    delay += iniDelay;
                }
                this.runNewStg(gzId, wstg, delay, null);
            }
        }
    }
    
    private void runNewStg(final int gzId, final KfgzWorldStg wstg, final long delay, final List<KfgzWorldStgResult> wsResultList) {
        final KfgzWorldStgDto stg = new KfgzWorldStgDto();
        stg.setGzId(gzId);
        stg.setNextExcuteTime(new Date(new Date().getTime() + delay));
        stg.setStg(wstg);
        KfgzWorldStgManager.stgMap.get(gzId).put(wstg.getId(), stg);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson(getWorldStgInfoXml(gzId, wsResultList).getBytes());
        doc.endObject();
        KfgzMessageSender.sendMsgToAll(doc.toByte(), PushCommand.PUSH_KF_WORLD_STGS, gzId);
        KfgzWorldStgManager.exeutors.schedule(new Runnable() {
            @Override
            public void run() {
                KfgzWorldStgManager.this.exeStg(gzId, wstg);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    protected void exeStg(final int gzId, final KfgzWorldStg wstg) {
        if (KfgzManager.isGzEndByGzId(gzId)) {
            return;
        }
        final Stratagem stg = StratagemCache.getStgById(wstg.getStg_id());
        final String effectCitys = wstg.getEffectCities();
        final String[] cs = effectCitys.split(",");
        final int selfCityId = wstg.getCity_id();
        final KfCity selfCity = KfgzManager.getKfWorldByGzId(gzId).getCities().get(selfCityId);
        final int selfForceId = selfCity.getForceId();
        final int selfArmyId = wstg.getSelfArmyId();
        final General general = GeneralCache.getGeneralById(selfArmyId);
        final KfPlayerInfo pInfo = new KfPlayerInfo(0, gzId);
        pInfo.setPlayerName(general.getName());
        final List<KfgzWorldStgResult> wsResultList = new ArrayList<KfgzWorldStgResult>();
        String[] array;
        for (int length = (array = cs).length, i = 0; i < length; ++i) {
            final String cityId = array[i];
            final int cId = Integer.parseInt(cityId);
            final KfCity city = KfgzManager.getKfWorldByGzId(gzId).getCities().get(cId);
            if (city != null) {
                final JsonDocument doc = new JsonDocument();
                final int allDamage = city.useDamageStgWithForceId(pInfo, stg, selfForceId);
                wsResultList.add(new KfgzWorldStgResult(cId, allDamage, wstg.getId()));
            }
        }
        final long delay = this.getWordStgExcuteInterval(wstg);
        if (delay > 0L) {
            this.runNewStg(gzId, wstg, delay, wsResultList);
        }
    }
    
    private long getWordStgExcuteInterval(final KfgzWorldStg wstg) {
        final List<String[]> sList = wstg.getConditionInfo();
        for (final String[] cs : sList) {
            final String key = cs[0];
            final String value = cs[1];
            if (key.trim().equals("interval")) {
                return Integer.parseInt(value) * 60000L;
            }
        }
        return -1L;
    }
    
    private long getWordStgExcuteDelay(final KfgzWorldStg wstg) {
        final List<String[]> sList = wstg.getConditionInfo();
        for (final String[] cs : sList) {
            final String key = cs[0];
            final String value = cs[1];
            if (key.trim().equals("starttime")) {
                return Integer.parseInt(value) * 60000L;
            }
        }
        return -1L;
    }
    
    public static Map<Integer, KfgzWorldStgDto> getWorldStgInfo(final int gzId) {
        return KfgzWorldStgManager.stgMap.get(gzId);
    }
    
    public static String getWorldStgInfoXml(final int gzId, final List<KfgzWorldStgResult> wsResultList) {
        final JsonDocument doc = new JsonDocument();
        int wsgId = 0;
        if (wsResultList != null && wsResultList.size() > 0) {
            wsgId = wsResultList.get(0).getwStgId();
        }
        doc.startArray("worldstg");
        final Map<Integer, KfgzWorldStgDto> wsMap = getWorldStgInfo(gzId);
        if (wsMap == null) {
            return "";
        }
        for (final Map.Entry<Integer, KfgzWorldStgDto> entry : wsMap.entrySet()) {
            final KfgzWorldStgDto wsdto = entry.getValue();
            doc.startObject();
            final Stratagem stg = StratagemCache.getStgById(wsdto.getStg().getStg_id());
            doc.createElement("stgName", stg.getName());
            final long cd = wsdto.getNextExcuteTime().getTime() - new Date().getTime();
            doc.createElement("cd", (cd > 0L) ? cd : 0L);
            final General g = GeneralCache.getGeneralById(wsdto.getStg().getSelfArmyId());
            doc.createElement("armyName", g.getName());
            doc.createElement("armyPic", g.getPic());
            doc.createElement("cityId", wsdto.getStg().getCity_id());
            if (wsdto.getStg().getId() == wsgId) {
                doc.startArray("wstgResult");
                for (final KfgzWorldStgResult wsr : wsResultList) {
                    doc.startObject();
                    doc.createElement("wsrcity", wsr.getCityId());
                    doc.createElement("killArmy", wsr.getAllDamage());
                    doc.endObject();
                }
                doc.endArray();
            }
            doc.endObject();
        }
        doc.endArray();
        return doc.toString();
    }
    
    public void clearInfoByGzId(final int gzId) {
        KfgzWorldStgManager.stgMap.remove(gzId);
    }
}
