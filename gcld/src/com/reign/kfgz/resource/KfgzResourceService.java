package com.reign.kfgz.resource;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.kf.match.log.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.comm.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.dto.response.*;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import java.io.*;
import com.reign.kfgz.dto.*;
import com.reign.kfgz.control.*;
import com.reign.framework.json.*;
import java.util.*;

@Component("kfgzResourceService")
public class KfgzResourceService implements IKfgzResourceService, Runnable, InitializingBean
{
    private static Logger logger;
    private static Thread singleThread;
    private static final long sleepTime = 10000L;
    private boolean mark;
    
    static {
        KfgzResourceService.logger = new PlayerInfoLogger();
        KfgzResourceService.singleThread = null;
    }
    
    public KfgzResourceService() {
        this.mark = true;
    }
    
    @Override
    public byte[] startMubing(final KfPlayerInfo player, final int gId) {
        final KfGeneralInfo g = player.getgMap().get(gId);
        if (g == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.COMM_1);
        }
        if (g.getState() != 1 || g.getGeneralState() != 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.WORLD_MOVE_1);
        }
        final KfCampArmy ca = g.getCampArmy();
        if (ca.getArmyHp() >= ca.getArmyHpOrg()) {
            return JsonBuilder.getJson(State.FAIL, "\u5175\u529b\u5df2\u6ee1");
        }
        g.setGeneralState(1);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public KfgzSyncDataResult syncResource(final KfgzSyncDataParam param) {
        final KfgzSyncDataResult result = new KfgzSyncDataResult();
        result.setPlayerId(param.getPlayerId());
        result.setCid(param.getcId());
        result.setVersionFrom(param.getVersion());
        KfgzResChangeManager.syncResChangeByVersion(param, result);
        if (KfgzResourceService.logger.isDebugEnabled()) {
            try {
                KfgzResourceService.logger.debug("request: " + Types.OBJECT_MAPPER.writeValueAsString(param));
                KfgzResourceService.logger.debug("response: " + Types.OBJECT_MAPPER.writeValueAsString(result));
            }
            catch (JsonGenerationException e) {
                e.printStackTrace();
            }
            catch (JsonMappingException e2) {
                e2.printStackTrace();
            }
            catch (IOException e3) {
                e3.printStackTrace();
            }
        }
        return result;
    }
    
    public static double getOutput(final int cId) {
        final int output = KfgzResChangeManager.getMubing(cId);
        return output * 1.0 / 3600.0;
    }
    
    @Override
    public void run() {
        while (this.mark) {
            try {
                final long nowTime = System.currentTimeMillis();
                for (final Map.Entry<Integer, KfgzBaseInfo> en : KfgzManager.gzMap.entrySet()) {
                    if (en.getValue().getState() == 1) {
                        if (nowTime < en.getValue().getLastMubingTime()) {
                            continue;
                        }
                        final int time = (int)((nowTime - en.getValue().getLastMubingTime()) / 1000L);
                        if (KfgzPlayerManager.getPlayerMapByGz(en.getKey()) != null) {
                            for (final KfPlayerInfo pif : KfgzPlayerManager.getPlayerMapByGz(en.getKey()).values()) {
                                final int num = (int)(getOutput(pif.getCompetitorId()) * time);
                                for (final KfGeneralInfo gInfo : pif.getgMap().values()) {
                                    if (gInfo.getState() == 1 && gInfo.getGeneralState() != 0) {
                                        gInfo.getCampArmy().Mubing(num);
                                    }
                                }
                            }
                        }
                        en.getValue().setLastMubingTime(nowTime);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10000L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
    
    public void init() {
        if (KfgzResourceService.singleThread != null) {
            return;
        }
        (KfgzResourceService.singleThread = new Thread(this)).start();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.init();
    }
    
    @Override
    public byte[] getInfo(final KfPlayerInfo player) {
        return JsonBuilder.getObjectJson(State.SUCCESS, this.getResult(player));
    }
    
    private byte[] getResult(final KfPlayerInfo player) {
        final JsonDocument doc = new JsonDocument();
        final int mubingNum = KfgzResChangeManager.getMubingNum(player.getCompetitorId());
        doc.createElement("recruitToken", mubingNum);
        final List<KfGeneralInfo> gList = new ArrayList<KfGeneralInfo>();
        for (final KfGeneralInfo gInfo : player.getgMap().values()) {
            gList.add(gInfo);
        }
        Collections.sort(gList, KfGeneralInfo.compare);
        doc.startArray("general");
        for (final KfGeneralInfo kfgi : gList) {
            doc.startObject();
            kfgi.createGeneralInfo(doc);
            doc.endObject();
        }
        doc.endArray();
        return doc.toByte();
    }
}
