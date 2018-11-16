package com.reign.gcld.kfwd.manager;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.kfwd.dao.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.*;
import org.springframework.beans.*;
import com.reign.gcld.kfwd.domain.*;
import java.util.*;
import com.reign.kfwd.constants.*;
import com.reign.kf.comm.entity.kfwd.response.*;

@Component
public class KfwdMatchManager implements InitializingBean
{
    @Autowired
    private IKfwdSignupDao kfwdSignupDao;
    private KfwdSeasonInfo seasonInfo;
    private static final KfwdMatchManager instance;
    private ConcurrentMap<Integer, KfwdMatch> KfwdMatchMap;
    private ConcurrentMap<Integer, KfwdSignInfo> kfwdSignInfoMap;
    private ConcurrentMap<Integer, Integer> kfwdCIdToPIdMap;
    private volatile int globeMatchState;
    private volatile KfwdState curWdState;
    private KfwdTicketMarketListInfo ticketInfo;
    private volatile long nextGlobalCD;
    
    static {
        instance = new KfwdMatchManager();
    }
    
    private KfwdMatchManager() {
        this.KfwdMatchMap = new ConcurrentHashMap<Integer, KfwdMatch>();
        this.kfwdSignInfoMap = new ConcurrentHashMap<Integer, KfwdSignInfo>();
        this.kfwdCIdToPIdMap = new ConcurrentHashMap<Integer, Integer>();
        this.globeMatchState = 0;
        this.curWdState = null;
        this.ticketInfo = null;
        this.nextGlobalCD = 0L;
    }
    
    public void setSeasonInfo(final KfwdSeasonInfo seasonInfo) {
        this.seasonInfo = seasonInfo;
    }
    
    public Date getSignUPtime() {
        if (this.seasonInfo != null) {
            return this.seasonInfo.getSignUpTime();
        }
        return null;
    }
    
    public static KfwdMatchManager getInstance() {
        return KfwdMatchManager.instance;
    }
    
    public void clearSeason() {
        this.KfwdMatchMap.clear();
        this.kfwdSignInfoMap.clear();
        this.globeMatchState = 0;
        this.curWdState = null;
    }
    
    public void iniNewMatch(final KfwdGwScheduleInfoDto sdto) {
        final KfwdMatch match = new KfwdMatch();
        BeanUtils.copyProperties(sdto, match);
        final String matchAddress = sdto.getMatchAdress();
        final String address = matchAddress.split(":")[0];
        final String port = matchAddress.split(":")[1];
        match.setMatchAdress(address);
        match.setMatchPort(port);
        this.KfwdMatchMap.put(match.getScheduleId(), match);
        final List<KfwdSignup> slist = this.kfwdSignupDao.getSignUpInfoBySeasonIdAndSchduleId(match.getSeasonId(), match.getScheduleId());
        for (final KfwdSignup sInfo : slist) {
            final KfwdSignInfo signInfo = sInfo.copyToKfwdSignInfo();
            this.kfwdSignInfoMap.put(signInfo.getPlayerId(), signInfo);
            this.kfwdCIdToPIdMap.put(signInfo.getCompletedId(), signInfo.getPlayerId());
        }
    }
    
    public void processWdState(final KfwdState wdState) {
        this.curWdState = wdState;
    }
    
    public KfwdState getCurWdState() {
        return this.curWdState;
    }
    
    public int getMatchState() {
        if (this.curWdState == null) {
            return -1;
        }
        return this.curWdState.getGlobalState();
    }
    
    public KfwdSignInfo getSignInfoByPlayerId(final int playerId) {
        return this.kfwdSignInfoMap.get(playerId);
    }
    
    public void addNewSignInfo(final KfwdSignup wdSignup) {
        if (wdSignup == null) {
            return;
        }
        final KfwdSignInfo signInfo = wdSignup.copyToKfwdSignInfo();
        this.kfwdSignInfoMap.put(signInfo.getPlayerId(), signInfo);
        this.kfwdCIdToPIdMap.put(signInfo.getCompletedId(), signInfo.getPlayerId());
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
    }
    
    public int getScheduleIdByPlayerLevel(final Integer playerLv) {
        for (final Map.Entry<Integer, KfwdMatch> entry : this.KfwdMatchMap.entrySet()) {
            final KfwdMatch match = entry.getValue();
            final Integer[] levelRange = KfwdConstantsAndMethod.parseLevelRangeString(match.getLevelRange());
            if (playerLv >= levelRange[0] && playerLv < levelRange[1]) {
                return match.getScheduleId();
            }
        }
        return 0;
    }
    
    public int getMinPlayerLevel() {
        int level = 999;
        for (final Map.Entry<Integer, KfwdMatch> entry : this.KfwdMatchMap.entrySet()) {
            final KfwdMatch match = entry.getValue();
            final Integer[] levelRange = KfwdConstantsAndMethod.parseLevelRangeString(match.getLevelRange());
            if (levelRange[0] < level) {
                level = levelRange[0];
            }
        }
        return level;
    }
    
    public Integer getPlayerInfoByCId(final int cId) {
        return this.kfwdCIdToPIdMap.get(cId);
    }
    
    public String getMatchAddress() {
        final Iterator<Map.Entry<Object, Object>> iterator = this.KfwdMatchMap.entrySet().iterator();
        if (iterator.hasNext()) {
            final Map.Entry<Integer, KfwdMatch> entry = (Map.Entry<Integer, KfwdMatch>)iterator.next();
            return entry.getValue().getMatchAdress();
        }
        return null;
    }
    
    public String getMatchPort() {
        final Iterator<Map.Entry<Object, Object>> iterator = this.KfwdMatchMap.entrySet().iterator();
        if (iterator.hasNext()) {
            final Map.Entry<Integer, KfwdMatch> entry = (Map.Entry<Integer, KfwdMatch>)iterator.next();
            return entry.getValue().getMatchPort();
        }
        return null;
    }
    
    public void putNewTicketMarketInfo(final KfwdTicketMarketListInfo ticketInfo) {
        this.ticketInfo = ticketInfo;
    }
    
    public List<KfwdTicketMarketInfo> getTicketRewardList() {
        if (this.ticketInfo == null) {
            return null;
        }
        return this.ticketInfo.getList();
    }
    
    public int getDayBattleTimes() {
        if (this.seasonInfo == null) {
            return 0;
        }
        return this.seasonInfo.getTotalRound() / 3;
    }
    
    public KfwdSeasonInfo getSeasonInfo() {
        return this.seasonInfo;
    }
    
    public int getCurSeasonId() {
        if (this.seasonInfo == null) {
            return -1;
        }
        return this.seasonInfo.getSeasonId();
    }
}
