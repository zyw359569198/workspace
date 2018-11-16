package com.reign.kfgz.team;

import org.apache.commons.logging.*;
import com.reign.kfgz.control.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.util.*;
import com.reign.kfgz.battle.*;
import com.reign.framework.json.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.world.*;
import com.reign.kf.match.sdata.domain.*;

public class KfCity extends KfTeam
{
    private int rewardFinish;
    private static Log gzCommonLog;
    
    static {
        KfCity.gzCommonLog = LogFactory.getLog("astd.kfgz.log.comm");
    }
    
    public void changeNation(final int forceId) {
        final int n = this.forceId;
        this.forceId = forceId;
        KfgzManager.getKfWorldByGzId(this.gzId).reLoadForceCities(n, false);
        KfgzManager.getKfWorldByGzId(this.gzId).reLoadForceCities(forceId, false);
        if (n != this.forceId) {
            try {
                final String content = MessageFormatter.format(LocalMessages.CHAT_2, new Object[] { WorldCityCache.getById(this.getTeamId()).getName() });
                KfgzMessageSender.sendChatToForce(this.gzId, n, content);
            }
            catch (Exception ex) {}
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("reloadWorld", 1);
        doc.endObject();
        final byte[] result = doc.toByte();
        final KfWorld world = KfgzManager.getKfWorldByGzId(this.gzId);
        if (world != null) {
            final int force1Num = world.getForceCityNum(1);
            final int force2Num = world.getForceCityNum(2);
            KfCity.gzCommonLog.info("gzForceNum#" + this.gzId + "#" + force1Num + "#" + force2Num);
        }
        KfgzMessageSender.sendMsgToAll(result, PushCommand.PUSH_KF_WORLD_CITIES, this.gzId);
    }
    
    public KfCity(final int cityId, final int gzId) {
        super(1, cityId, gzId, cityId);
        this.rewardFinish = 0;
    }
    
    public boolean isCaptial() {
        final KfgzWorldCity wc = WorldCityCache.getById(this.getTeamId());
        return wc.getType() == 1;
    }
    
    public boolean isJieBingCity() {
        final KfgzWorldCity wc = WorldCityCache.getById(this.getTeamId());
        return wc.getType() == 4;
    }
    
    public void setRewardFinish(final int rewardFinish) {
        this.rewardFinish = rewardFinish;
    }
    
    public int getRewardFinish() {
        return this.rewardFinish;
    }
}
