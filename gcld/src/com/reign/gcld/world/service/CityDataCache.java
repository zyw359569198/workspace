package com.reign.gcld.world.service;

import org.springframework.stereotype.*;
import com.reign.gcld.world.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.general.dao.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.battle.scene.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.world.graph.*;
import com.reign.gcld.common.util.*;
import java.util.*;

@Component("cityDataCache")
public class CityDataCache implements ICityDataCache
{
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private WorldRoadCache worldRoadCache;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    public static City[] cityArray;
    private static int[][] matrix_num;
    private static int[][] matrix_dis;
    private static ConcurrentMap<Integer, Integer> forceCityNum;
    private static ConcurrentMap<Integer, Integer> cityGuardStateMap;
    private static ConcurrentMap<Integer, Integer> forceCNpNum;
    private static ConcurrentMap<Integer, Integer> cityGeneralNum;
    public static int leagueInfo;
    private static ReentrantLock getCitysLock;
    public static Object[] cityLocks;
    
    static {
        CityDataCache.cityArray = new City[280];
        CityDataCache.matrix_num = new int[280][280];
        CityDataCache.matrix_dis = new int[280][280];
        CityDataCache.forceCityNum = new ConcurrentHashMap<Integer, Integer>();
        CityDataCache.cityGuardStateMap = new ConcurrentHashMap<Integer, Integer>();
        CityDataCache.forceCNpNum = new ConcurrentHashMap<Integer, Integer>();
        CityDataCache.cityGeneralNum = new ConcurrentHashMap<Integer, Integer>();
        CityDataCache.leagueInfo = 0;
        CityDataCache.getCitysLock = new ReentrantLock(false);
        CityDataCache.cityLocks = new Object[281];
        for (int i = 0; i < 280; ++i) {
            CityDataCache.cityLocks[i] = new Object();
        }
        for (int i = 0; i < 280; ++i) {
            CityDataCache.cityGeneralNum.put(i, 1);
        }
    }
    
    public static boolean isAllied(final int forceId1, final int forceId2) {
        if (CityDataCache.leagueInfo == 1) {
            return (forceId1 == 1 && forceId2 == 2) || (forceId1 == 2 && forceId2 == 1);
        }
        if (CityDataCache.leagueInfo == 2) {
            return (forceId1 == 1 && forceId2 == 3) || (forceId1 == 3 && forceId2 == 1);
        }
        return CityDataCache.leagueInfo == 3 && ((forceId1 == 2 && forceId2 == 3) || (forceId1 == 3 && forceId2 == 2));
    }
    
    @Override
    public int getGeneralNum(final int cityId) {
        return CityDataCache.cityGeneralNum.get(cityId);
    }
    
    @Override
    public void ressetGeneralNum(final int orgCityId, final int movCityId) {
        if (orgCityId <= 0 && movCityId > 0) {
            synchronized (CityDataCache.cityLocks[movCityId]) {
                CityDataCache.cityGeneralNum.put(movCityId, CityDataCache.cityGeneralNum.get(movCityId) + 1);
            }
            // monitorexit(CityDataCache.cityLocks[movCityId])
            return;
        }
        if (orgCityId > 0 && movCityId <= 0) {
            synchronized (CityDataCache.cityLocks[orgCityId]) {
                CityDataCache.cityGeneralNum.put(orgCityId, CityDataCache.cityGeneralNum.get(orgCityId) - 1);
            }
            // monitorexit(CityDataCache.cityLocks[orgCityId])
            return;
        }
        if (orgCityId < movCityId) {
            synchronized (CityDataCache.cityLocks[orgCityId]) {
                // monitorenter(o = CityDataCache.cityLocks[movCityId])
                try {
                    CityDataCache.cityGeneralNum.put(movCityId, CityDataCache.cityGeneralNum.get(movCityId) + 1);
                    CityDataCache.cityGeneralNum.put(orgCityId, CityDataCache.cityGeneralNum.get(orgCityId) - 1);
                }
                // monitorexit(o)
                finally {}
                // monitorexit(CityDataCache.cityLocks[orgCityId])
                return;
            }
        }
        synchronized (CityDataCache.cityLocks[movCityId]) {
            // monitorenter(o2 = CityDataCache.cityLocks[orgCityId])
            try {
                CityDataCache.cityGeneralNum.put(movCityId, CityDataCache.cityGeneralNum.get(movCityId) + 1);
                CityDataCache.cityGeneralNum.put(orgCityId, CityDataCache.cityGeneralNum.get(orgCityId) - 1);
            }
            // monitorexit(o2)
            finally {}
        }
        // monitorexit(CityDataCache.cityLocks[movCityId])
    }
    
    @Override
    public int getCityNum(final int forceId) {
        return CityDataCache.forceCityNum.get(forceId);
    }
    
    @Override
    public int getCNPNum(final int forceId) {
        if (!WorldCityCommon.nationMainCityIdMap.containsKey(forceId)) {
            return 0;
        }
        if (CityDataCache.forceCNpNum.containsKey(forceId)) {
            return CityDataCache.forceCNpNum.get(forceId);
        }
        try {
            CityDataCache.getCitysLock.lock();
            final List<City> cities = this.cityDao.getModels();
            int cnpWei = 0;
            int cnpShu = 0;
            int cnpWu = 0;
            final int cnpDefault = 0;
            for (final City city : cities) {
                final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)city.getId());
                if (city.getForceId() == 1) {
                    cnpWei += worldCity.getOutput();
                }
                else if (city.getForceId() == 2) {
                    cnpShu += worldCity.getOutput();
                }
                else {
                    if (city.getForceId() != 3) {
                        continue;
                    }
                    cnpWu += worldCity.getOutput();
                }
            }
            CityDataCache.forceCNpNum.put(1, cnpWei);
            CityDataCache.forceCNpNum.put(2, cnpShu);
            CityDataCache.forceCNpNum.put(3, cnpWu);
            CityDataCache.forceCNpNum.put(0, cnpDefault);
            return CityDataCache.forceCNpNum.get(forceId);
        }
        finally {
            CityDataCache.getCitysLock.unlock();
        }
    }
    
    @Override
    public void fireCityMoveMessage(final int playerId, final int curCity, final int nextCity, final String generalName) {
        final Player player = this.playerDao.read(playerId);
        final Session session = Players.getSession(Integer.valueOf(playerId));
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, nextCity);
        final Group curGroup = GroupManager.getInstance().getGroup(String.valueOf(ChatType.WORLD.toString()) + curCity);
        if (curGroup != null && !this.hasGeneralIncity(playerId, curCity) && session != null && session.getId() != null) {
            curGroup.leave(session.getId());
        }
        final Integer cityId = WorldFarmCache.forceCityIdMap.get(player.getForceId());
        if (cityId == null) {
            return;
        }
        if (battle != null || cityId == nextCity) {
            final CityMessage cityMessage1 = new MoveMessage(nextCity, player.getForceId(), player.getPlayerId(), generalName, LocalMessages.Message_TYPE_2, player.getPlayerName());
            final Group nextGroup = GroupManager.getInstance().getGroup(String.valueOf(ChatType.WORLD.toString()) + nextCity);
            if (nextGroup != null && session != null) {
                if (session.getId() != null) {
                    nextGroup.join(session);
                }
                this.sendCityMessage(cityMessage1);
            }
        }
    }
    
    private boolean hasGeneralIncity(final int playerId, final int cityId) {
        final List<PlayerGeneralMilitary> pList = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        for (final PlayerGeneralMilitary p : pList) {
            if (p.getLocationId() == cityId) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void fireCityTrickMessage(final int cityId, final int userForceId, final String trickName, final int cityForceId) {
    }
    
    @Override
    public void fireBattleMessage(final int cityId, final int att, final int def) {
    }
    
    @Override
    public void fireBattleEnd(final int cityId, final int forceId) {
    }
    
    @Override
    public void fireCityStateMessage(final int cityId, final int newForceId, final String type, final int value, final int state) {
        final CityMessage cityMessage = new CityStateMessage(cityId, newForceId, type, value, state);
        this.sendCityMessage(cityMessage);
    }
    
    @Override
    public void sendCityMessage(final CityMessage cityMessage) {
        this.fireCityMessageChanged(cityMessage);
    }
    
    @Override
    public void fireCityMessageChanged(final CityMessage cityMessage) {
        if (cityMessage == null) {
            return;
        }
        cityMessage.messageChanged();
    }
    
    @Override
    public void fireCityNumChangeEvent(final int orgForceId, final int curForceId) {
        if (WorldCityCommon.nationMainCityIdMap.containsKey(orgForceId)) {
            CityDataCache.forceCityNum.put(orgForceId, this.cityDao.getForceCounts(orgForceId));
            CityDataCache.forceCNpNum.remove(orgForceId);
        }
        CityDataCache.forceCityNum.put(curForceId, this.cityDao.getForceCounts(curForceId));
        CityDataCache.forceCNpNum.remove(curForceId);
    }
    
    @Override
    public int getCityGuardState(final int cityId) {
        return CityDataCache.cityGuardStateMap.get(cityId);
    }
    
    @Override
    public void upCityGuardState(final int cityId, final int state) {
        CityDataCache.cityGuardStateMap.put(cityId, state);
    }
    
    @Override
    public void initForceCityNum() {
        for (int i = 0; i <= 3; ++i) {
            CityDataCache.forceCityNum.put(i, 1);
            CityDataCache.forceCNpNum.put(i, 0);
        }
    }
    
    @Override
    public void initCityArray() {
        final List<City> cities = this.cityDao.getModels();
        for (final City city : cities) {
            CityDataCache.cityArray[city.getId()] = city;
        }
    }
    
    @Override
    public void init() {
        final List<City> cities = this.cityDao.getModels();
        int cnpWei = 0;
        int cnpShu = 0;
        int cnpWu = 0;
        for (final City city : cities) {
            final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)city.getId());
            if (city.getForceId() == 1) {
                cnpWei += worldCity.getOutput();
            }
            else if (city.getForceId() == 2) {
                cnpShu += worldCity.getOutput();
            }
            else {
                if (city.getForceId() != 3) {
                    continue;
                }
                cnpWu += worldCity.getOutput();
            }
        }
        CityDataCache.forceCNpNum.put(1, cnpWei);
        CityDataCache.forceCNpNum.put(2, cnpShu);
        CityDataCache.forceCNpNum.put(3, cnpWu);
        for (int i = 0; i <= 3; ++i) {
            CityDataCache.forceCityNum.put(i, this.cityDao.getForceCounts(i));
        }
        int num = 0;
        for (final City city2 : cities) {
            ++num;
            CityDataCache.cityArray[city2.getId()] = city2;
            CityDataCache.cityGuardStateMap.put(city2.getId(), 1);
        }
        this.initPath();
        this.initBorderState();
        final Map<Integer, Object> generalMap = this.playerGeneralMilitaryDao.getCityGenrealNum();
        for (final Integer key : generalMap.keySet()) {
            final Map<String, Long> gNum = generalMap.get(key);
            CityDataCache.cityGeneralNum.put(key, (int)(Object)gNum.get("generalNum"));
        }
    }
    
    private void initBorderState() {
        this.cityDao.resetBorder();
        this.cityDao.resetTitle();
        City[] cityArray;
        for (int length = (cityArray = CityDataCache.cityArray).length, i = 0; i < length; ++i) {
            final City city = cityArray[i];
            if (city != null) {
                final Set<Integer> nbSet = this.worldRoadCache.getNeighbors(city.getId());
                if (nbSet != null) {
                    for (final int key : nbSet) {
                        if (CityDataCache.cityArray[key].getForceId() != city.getForceId()) {
                            this.cityDao.initUpdateBorder(city.getId(), 1);
                            break;
                        }
                    }
                }
            }
        }
        City[] cityArray2;
        for (int length2 = (cityArray2 = CityDataCache.cityArray).length, j = 0; j < length2; ++j) {
            final City city = cityArray2[j];
            if (city != null) {
                final Set<Integer> nbSet = this.worldRoadCache.getNeighbors(city.getId());
                boolean title1 = true;
                boolean title2 = true;
                int linkNum = 0;
                int linkKey = 0;
                for (final int key2 : nbSet) {
                    if (CityDataCache.cityArray[key2].getForceId() == city.getForceId()) {
                        title1 = false;
                        ++linkNum;
                        linkKey = key2;
                        if (linkNum <= 1) {
                            continue;
                        }
                        title2 = false;
                    }
                }
                if (title1) {
                    this.cityDao.updateTitle(city.getId(), 1);
                }
                else if (title2 && linkKey > 0) {
                    final Set<Integer> subSet = this.worldRoadCache.getNeighbors(linkKey);
                    for (final int key3 : subSet) {
                        if (key3 == city.getId()) {
                            continue;
                        }
                        if (CityDataCache.cityArray[key3].getForceId() == city.getForceId()) {
                            title2 = false;
                            break;
                        }
                    }
                    if (title2) {
                        this.cityDao.updateTitle(city.getId(), 2);
                    }
                }
            }
        }
    }
    
    public void initPath() {
        for (int i = 0; i < 280; ++i) {
            for (int j = 0; j < 280; ++j) {
                CityDataCache.matrix_num[i][j] = -1;
                CityDataCache.matrix_dis[i][j] = -1;
                if (i == j) {
                    CityDataCache.matrix_num[i][j] = 0;
                    CityDataCache.matrix_dis[i][j] = 0;
                }
            }
        }
        final Map<String, WorldRoad> roadMap = this.worldRoadCache.getRoadMap();
        for (final String key : roadMap.keySet()) {
            final WorldRoad road = roadMap.get(key);
            CityDataCache.matrix_num[road.getStart()][road.getEnd()] = 1;
            CityDataCache.matrix_num[road.getEnd()][road.getStart()] = 1;
            CityDataCache.matrix_dis[road.getStart()][road.getEnd()] = road.getLength();
            CityDataCache.matrix_dis[road.getEnd()][road.getStart()] = road.getLength();
        }
    }
    
    @Override
    public List<Integer> getMinPath(final int start, final int end) {
        final List<Integer> listTemp = new ArrayList<Integer>();
        if (WorldCityCommon.specialNationIdMap.containsKey(start)) {
            listTemp.add(start);
        }
        for (int j = 1; j < CityDataCache.cityArray.length; ++j) {
            if (!WorldCityCommon.specialNationIdMap.containsKey(j)) {
                if (CityDataCache.cityArray[j] != null) {
                    listTemp.add(j);
                }
            }
        }
        final int[] arr = new int[listTemp.size()];
        for (int i = 0; i < listTemp.size(); ++i) {
            arr[i] = listTemp.get(i);
        }
        return this.getMinPath(start, end, arr);
    }
    
    @Override
    public List<Integer> getMinPath(final int start, final int end, final int[] arr) {
        int s = -1;
        int e = -1;
        for (int i = 0; i < arr.length; ++i) {
            if (start == arr[i]) {
                s = i;
            }
            if (end == arr[i]) {
                e = i;
            }
        }
        if (s == -1 || e == -1) {
            return null;
        }
        return ShortestPath.dijkstra(CityDataCache.matrix_num, CityDataCache.matrix_dis, s, e, arr);
    }
    
    @Override
    public List<Integer> getMinPathFire(final int start, final int end, final int[] arr, final Set<Integer> fireCities) {
        int s = -1;
        int e = -1;
        for (int i = 0; i < arr.length; ++i) {
            if (!fireCities.contains(start) && start == arr[i]) {
                s = i;
            }
            if (end == arr[i]) {
                e = i;
            }
        }
        if (s == -1 || e == -1) {
            return null;
        }
        return ShortestPath.dijkstraExceptFireCities(CityDataCache.matrix_num, CityDataCache.matrix_dis, s, e, arr, fireCities);
    }
    
    @Override
    public boolean hasCity(final int forceId, final int cityId) {
        final City city = CityDataCache.cityArray[cityId];
        return city != null && forceId == city.getForceId();
    }
    
    @Override
    public int getStrongestForce() {
        int forceId = 1;
        int max = 0;
        int cityNum = 0;
        int min = Integer.MAX_VALUE;
        int forceMin = 0;
        for (int i = 1; i <= 3; ++i) {
            cityNum = this.getCityNum(i);
            if (cityNum > max) {
                max = cityNum;
                forceId = i;
            }
            if (cityNum < min) {
                min = cityNum;
                forceMin = i;
            }
        }
        final int forceMid = (forceMin != forceId) ? (6 - forceId - forceMin) : (WebUtil.nextInt(3) + 1);
        final double[] boxProb = { 0.6, 0.3, 0.1 };
        final int[] timesArray = { forceId, forceMid, forceMin };
        return KillRankService.getCrit(boxProb, timesArray);
    }
    
    @Override
    public Set<Integer> getBFSCitySetByBreadth(final int centerCityId, final int breadth) {
        final Set<Integer> resultSet = new HashSet<Integer>();
        final Set<Integer> borderSet = new HashSet<Integer>();
        borderSet.add(centerCityId);
        resultSet.add(centerCityId);
        for (int i = 0; i < breadth; ++i) {
            final Set<Integer> tempSet = new HashSet<Integer>();
            tempSet.addAll(borderSet);
            for (final Integer temp : tempSet) {
                borderSet.remove(temp);
                final Set<Integer> neibors = this.worldRoadCache.getNeighbors(temp);
                for (final Integer nei : neibors) {
                    if (!resultSet.contains(nei)) {
                        borderSet.add(nei);
                    }
                }
                resultSet.addAll(neibors);
            }
        }
        return resultSet;
    }
    
    @Override
    public List<Integer> getBFSCityOrderListByBreadth(final int centerCityId, final int breadth) {
        final List<Integer> result = new LinkedList<Integer>();
        final Set<Integer> borderSet = new HashSet<Integer>();
        borderSet.add(centerCityId);
        result.add(centerCityId);
        for (int i = 0; i < breadth; ++i) {
            final Set<Integer> tempSet = new HashSet<Integer>();
            tempSet.addAll(borderSet);
            for (final Integer temp : tempSet) {
                borderSet.remove(temp);
                final Set<Integer> neibors = this.worldRoadCache.getNeighbors(temp);
                for (final Integer nei : neibors) {
                    if (!result.contains(nei)) {
                        borderSet.add(nei);
                    }
                }
                result.addAll(neibors);
            }
        }
        return result;
    }
}
