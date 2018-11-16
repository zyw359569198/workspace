package com.reign.kfgz.world;

import com.reign.kfgz.team.*;
import java.util.concurrent.*;
import com.reign.kfgz.dto.*;
import java.util.concurrent.locks.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.comm.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;
import com.reign.kfwd.constants.*;
import com.reign.kf.match.common.*;
import java.util.*;
import com.reign.kfgz.battle.*;

public class KfWorld extends GzLifeCycle implements Runnable
{
    private boolean doLoop;
    private Map<Integer, int[]> roadMap;
    private Lock lock;
    private Thread singleThread;
    private static final long SLEEP_TIME = 60000L;
    private int second;
    public Set<Integer> allForceId;
    public int cityNum;
    private Map<Integer, KfCity> cities;
    private Map<Integer, KfCity> capitals;
    private final int disconnect = -1;
    private final int noWay = -1;
    private Map<Integer, MyTwoDimensionalArray> allForcePath;
    private Map<Integer, MyTwoDimensionalArray> allForcePathDetail;
    private Map<Integer, MyTwoDimensionalArray> allForceD;
    private Map<Integer, MyTwoDimensionalArray> allForcePath2;
    private Map<Integer, MyTwoDimensionalArray> allForcePathDetail2;
    private Map<Integer, MyTwoDimensionalArray> allForceD2;
    private MyTwoDimensionalArray allPath;
    private MyTwoDimensionalArray worldPath;
    private MyTwoDimensionalArray worldPathDetail;
    private MyTwoDimensionalArray worldD;
    private ConcurrentHashMap<Integer, KfgzOfficeToken> officeTokenMap;
    private ConcurrentHashMap<Integer, KfgzOrderToken> orderTokenMap;
    
    public KfWorld(final int gzId) {
        this.doLoop = true;
        this.roadMap = new HashMap<Integer, int[]>();
        this.lock = new ReentrantLock();
        this.singleThread = null;
        this.second = 0;
        this.allForceId = new HashSet<Integer>();
        this.cityNum = 0;
        this.cities = new HashMap<Integer, KfCity>();
        this.capitals = new HashMap<Integer, KfCity>();
        this.allForcePath = new HashMap<Integer, MyTwoDimensionalArray>();
        this.allForcePathDetail = new HashMap<Integer, MyTwoDimensionalArray>();
        this.allForceD = new HashMap<Integer, MyTwoDimensionalArray>();
        this.allForcePath2 = new HashMap<Integer, MyTwoDimensionalArray>();
        this.allForcePathDetail2 = new HashMap<Integer, MyTwoDimensionalArray>();
        this.allForceD2 = new HashMap<Integer, MyTwoDimensionalArray>();
        this.officeTokenMap = new ConcurrentHashMap<Integer, KfgzOfficeToken>();
        this.orderTokenMap = new ConcurrentHashMap<Integer, KfgzOrderToken>();
        this.gzId = gzId;
    }
    
    @Override
    public int getGzId() {
        return this.gzId;
    }
    
    private int[] safeGetChangeRoad(final int roadId) {
        if (!this.roadMap.containsKey(roadId)) {
            final int[] a = { 0, 0 };
            final KfgzWorldRoad wr = WorldRoadCache.getById(roadId);
            a[1] = wr.getDisconnect_minutes() / 2;
            this.roadMap.put(roadId, a);
        }
        return this.roadMap.get(roadId);
    }
    
    public int getConnect(final int roadId) {
        this.lock.lock();
        try {
            return this.safeGetChangeRoad(roadId)[0];
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void changeConnect(final int roadId) {
        this.lock.lock();
        try {
            final int[] a = this.safeGetChangeRoad(roadId);
            a[0] = (a[0] + 1) % 2;
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public int getMinutes(final int roadId) {
        this.lock.lock();
        try {
            return this.safeGetChangeRoad(roadId)[1];
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addMinutes(final int roadId) {
        this.lock.lock();
        try {
            final int[] safeGetChangeRoad = this.safeGetChangeRoad(roadId);
            final int n = 1;
            ++safeGetChangeRoad[n];
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setMinutes(final int roadId, final int minutes) {
        this.lock.lock();
        try {
            this.safeGetChangeRoad(roadId)[1] = minutes;
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public KfgzWorldRoad getByCityIds(final int cityId1, final int cityId2) {
        for (final KfgzWorldRoad wr : WorldRoadCache.getAllModels(KfgzManager.getWorldIdByGzId(this.gzId))) {
            if ((wr.getStart().equals(cityId1) && wr.getEnd().equals(cityId2)) || (wr.getStart().equals(cityId2) && wr.getEnd().equals(cityId1))) {
                if (1 == wr.getType() && this.getConnect(wr.getId()) == 0) {
                    return null;
                }
                return wr;
            }
        }
        return null;
    }
    
    public boolean isCityNearBy(final int cityId1, final int cityId2) {
        final KfgzWorldRoad road = this.getByCityIds(cityId1, cityId2);
        return road != null;
    }
    
    public KfgzOfficeToken getOfficeTokenByCityIdAndForceId(final int cityId, final int forceId) {
        if (!this.getCities().containsKey(cityId)) {
            return null;
        }
        final KfgzOfficeToken token = this.officeTokenMap.get(KfgzConstants.getWorldForcedKey(cityId, forceId));
        return token;
    }
    
    public void setNewOfficeTokenByCityIdAndForceId(final KfgzOfficeToken officeToken) {
        this.officeTokenMap.put(KfgzConstants.getWorldForcedKey(officeToken.getCityId(), officeToken.getForceId()), officeToken);
    }
    
    public int getCityNumFromCityToCapital(final int city, final int forceId) {
        if (this.capitals.get(forceId) == null) {
            return 0;
        }
        if (!this.getCities().containsKey(city)) {
            return 0;
        }
        final int capitalId = this.capitals.get(forceId).getTeamId();
        if (capitalId == city) {
            return 0;
        }
        int result = 0;
        for (int v = capitalId, w = city; w != this.worldPathDetail.get(w, v) && this.worldPathDetail.get(w, v) != -1; w = this.worldPathDetail.get(w, v)) {
            ++result;
        }
        if (result == 0) {
            return -1;
        }
        return result;
    }
    
    public synchronized void reLoadForceCities(final int forceId, final boolean changeAllPath) {
        if (changeAllPath) {
            this.clearAllPath();
        }
        this.clearForcePath(forceId);
        for (final KfgzWorldRoad wr : WorldRoadCache.getAllModels(KfgzManager.getWorldIdByGzId(this.gzId))) {
            if (1 == wr.getType() && this.getConnect(wr.getId()) == 0) {
                continue;
            }
            if (changeAllPath) {
                this.allPath.set(wr.getStart(), wr.getEnd(), wr.getLength());
                this.allPath.set(wr.getEnd(), wr.getStart(), wr.getLength());
            }
            if (this.cities.get(wr.getStart()).getForceId() != forceId || this.cities.get(wr.getEnd()).getForceId() != forceId) {
                continue;
            }
            this.allForcePath.get(forceId).set(wr.getStart(), wr.getEnd(), wr.getLength());
            this.allForcePath.get(forceId).set(wr.getEnd(), wr.getStart(), wr.getLength());
            this.allForcePath2.get(forceId).set(wr.getStart(), wr.getEnd(), 1);
            this.allForcePath2.get(forceId).set(wr.getEnd(), wr.getStart(), 1);
        }
        this.floyd(forceId);
    }
    
    private void doFloyd(final MyTwoDimensionalArray path, final MyTwoDimensionalArray pathDetail, final MyTwoDimensionalArray d) {
        for (final int i : this.getCities().keySet()) {
            for (final int j : this.getCities().keySet()) {
                d.set(i, j, path.get(i, j));
                if (i != j && path.get(i, j) != -1) {
                    pathDetail.set(i, j, i);
                }
                else {
                    pathDetail.set(i, j, -1);
                }
            }
        }
        for (final int k : this.getCities().keySet()) {
            for (final int l : this.getCities().keySet()) {
                for (final int m : this.getCities().keySet()) {
                    if (this.isLessThan(d.get(l, k), d.get(k, m), d.get(l, m))) {
                        d.set(l, m, d.get(l, k) + d.get(k, m));
                        pathDetail.set(l, m, pathDetail.get(k, m));
                    }
                }
            }
        }
    }
    
    private void floyd(final int forceId) {
        this.doFloyd(this.allForcePath.get(forceId), this.allForcePathDetail.get(forceId), this.allForceD.get(forceId));
        this.doFloyd(this.allForcePath2.get(forceId), this.allForcePathDetail2.get(forceId), this.allForceD2.get(forceId));
    }
    
    private void clearForcePath(final int forceId) {
        if (!this.allForcePath.containsKey(forceId)) {
            final MyTwoDimensionalArray forceP = new MyTwoDimensionalArray(this.cityNum);
            this.allForcePath.put(forceId, forceP);
        }
        if (!this.allForcePathDetail.containsKey(forceId)) {
            final MyTwoDimensionalArray forceDe = new MyTwoDimensionalArray(this.cityNum);
            this.allForcePathDetail.put(forceId, forceDe);
        }
        if (!this.allForceD.containsKey(forceId)) {
            final MyTwoDimensionalArray forceD = new MyTwoDimensionalArray(this.cityNum);
            this.allForceD.put(forceId, forceD);
        }
        if (!this.allForcePath2.containsKey(forceId)) {
            final MyTwoDimensionalArray forceP2 = new MyTwoDimensionalArray(this.cityNum);
            this.allForcePath2.put(forceId, forceP2);
        }
        if (!this.allForcePathDetail2.containsKey(forceId)) {
            final MyTwoDimensionalArray forceDe2 = new MyTwoDimensionalArray(this.cityNum);
            this.allForcePathDetail2.put(forceId, forceDe2);
        }
        if (!this.allForceD2.containsKey(forceId)) {
            final MyTwoDimensionalArray forceD2 = new MyTwoDimensionalArray(this.cityNum);
            this.allForceD2.put(forceId, forceD2);
        }
        for (final int i : this.getCities().keySet()) {
            for (final int j : this.getCities().keySet()) {
                this.allForcePathDetail.get(forceId).set(i, j, -1);
                this.allForcePathDetail2.get(forceId).set(i, j, -1);
                if (i == j) {
                    this.allForcePath.get(forceId).set(i, j, 0);
                    this.allForcePath2.get(forceId).set(i, j, 0);
                }
                else {
                    this.allForcePath.get(forceId).set(i, j, -1);
                    this.allForcePath2.get(forceId).set(i, j, -1);
                }
            }
        }
    }
    
    private void doInitWorldPath() {
        this.worldPath = new MyTwoDimensionalArray(this.cityNum);
        this.worldPathDetail = new MyTwoDimensionalArray(this.cityNum);
        this.worldD = new MyTwoDimensionalArray(this.cityNum);
        for (final int i : this.getCities().keySet()) {
            for (final int j : this.getCities().keySet()) {
                if (i == j) {
                    this.worldPath.set(i, j, 0);
                }
                else {
                    this.worldPath.set(i, j, -1);
                }
                this.worldPathDetail.set(i, j, -1);
                this.worldD.set(i, j, -1);
            }
        }
        for (final KfgzWorldRoad wr : WorldRoadCache.getAllModels(KfgzManager.getWorldIdByGzId(this.gzId))) {
            this.worldPath.set(wr.getStart(), wr.getEnd(), 1);
            this.worldPath.set(wr.getEnd(), wr.getStart(), 1);
        }
        this.doFloyd(this.worldPath, this.worldPathDetail, this.worldD);
    }
    
    private void clearAllPath() {
        for (final int i : this.getCities().keySet()) {
            for (final int j : this.getCities().keySet()) {
                if (i == j) {
                    this.allPath.set(i, j, 0);
                }
                else {
                    this.allPath.set(i, j, -1);
                }
            }
        }
    }
    
    public void init(final int worldId) {
        this.initWorldFromCache(worldId);
        this.doInitWorldPath();
        this.allPath = new MyTwoDimensionalArray(this.cityNum);
        boolean mark = true;
        for (final int forceId : this.allForceId) {
            this.reLoadForceCities(forceId, mark);
            mark = false;
        }
        if (this.singleThread == null) {
            (this.singleThread = new Thread(this)).start();
        }
    }
    
    private void initWorldFromCache(final int worldId) {
        final List<KfgzWorldCity> cList = WorldCityCache.getWorldCities(worldId);
        for (final KfgzWorldCity c : cList) {
            final KfCity kc = new KfCity(c.getId(), this.gzId);
            kc.setForceId(c.getForce_id());
            kc.setTeamId(c.getId());
            kc.setTeamType(1);
            kc.terrain = Integer.parseInt(c.getTerrain());
            switch (kc.terrainVal = KfwdConstantsAndMethod.getTerrainValByTerrain(kc.terrain)) {
                case 1: {
                    kc.terrainName = LocalMessages.TERRAIN_NAME_1;
                    break;
                }
                case 2: {
                    kc.terrainName = LocalMessages.TERRAIN_NAME_2;
                    break;
                }
                case 3: {
                    kc.terrainName = LocalMessages.TERRAIN_NAME_3;
                    break;
                }
                case 4: {
                    kc.terrainName = LocalMessages.TERRAIN_NAME_4;
                    break;
                }
            }
            this.getCities().put(c.getId(), kc);
            if (c.getType() == 1) {
                this.getCapitals().put(c.getForce_id(), kc);
            }
            ++this.cityNum;
            this.allForceId.add(c.getForce_id());
        }
    }
    
    private boolean isLessThan(final int a, final int b, final int c) {
        return a != -1 && b != -1 && (c == -1 || a + b < c);
    }
    
    private boolean isNear(final int fromCityId, final List<Integer> ll1, final List<Integer> ll2) {
        int s1 = 0;
        int s2 = fromCityId;
        for (final int i : ll1) {
            s1 += this.allPath.get(s2, i);
            s2 = i;
        }
        int s3 = 0;
        int ss = fromCityId;
        for (final int j : ll2) {
            s3 += this.allPath.get(ss, j);
            ss = j;
        }
        return s1 < s3;
    }
    
    public List<Integer> getDetailPath(final int forceId, final int fromCityId, final int toCityId) {
        List<Integer> result = new ArrayList<Integer>();
        if (fromCityId == toCityId) {
            return result;
        }
        final KfCity kc = this.cities.get(toCityId);
        final KfCity fromKc = this.cities.get(fromCityId);
        if (fromKc.getForceId() != forceId) {
            return null;
        }
        if (kc.getForceId() != forceId) {
            final List<Integer> citiesClosed = new ArrayList<Integer>();
            for (final int i : this.getCities().keySet()) {
                if (this.allPath.get(toCityId, i) > 0 && this.cities.get(i).getForceId() == forceId) {
                    citiesClosed.add(i);
                }
            }
            boolean mark = false;
            for (final int toC : citiesClosed) {
                final List<Integer> ll = this.getDetailPath(forceId, fromCityId, toC);
                if (ll == null) {
                    continue;
                }
                mark = true;
                ll.add(toCityId);
                if (result.size() <= 0 || ll.size() < result.size()) {
                    result = ll;
                }
                else {
                    if (ll.size() != result.size() || !this.isNear(fromCityId, ll, result)) {
                        continue;
                    }
                    result = ll;
                }
            }
            return mark ? result : null;
        }
        final List<Integer> result2 = new ArrayList<Integer>();
        for (int w = fromCityId; w != toCityId; w = this.allForcePathDetail.get(forceId).get(toCityId, w)) {
            if (this.allForcePathDetail.get(forceId).get(toCityId, w) == -1) {
                return null;
            }
            result2.add(this.allForcePathDetail.get(forceId).get(toCityId, w));
        }
        final List<Integer> result3 = new ArrayList<Integer>();
        for (int w = fromCityId; w != toCityId; w = this.allForcePathDetail2.get(forceId).get(toCityId, w)) {
            if (this.allForcePathDetail2.get(forceId).get(toCityId, w) == -1) {
                return null;
            }
            result3.add(this.allForcePathDetail2.get(forceId).get(toCityId, w));
        }
        if (result3.size() < result2.size()) {
            result = result3;
        }
        else {
            result = result2;
        }
        return result;
    }
    
    public Map<Integer, KfCity> getCapitals() {
        return this.capitals;
    }
    
    public Map<Integer, KfCity> getCities() {
        return this.cities;
    }
    
    public void test() {
        System.out.println("########################test world##########################");
        List<Integer> r = this.getDetailPath(1, 9, 1);
        if (r == null) {
            System.out.println("no way!");
        }
        else {
            for (final int i : r) {
                System.out.println(i);
            }
        }
        System.out.println("#############################################################");
        r = this.getDetailPath(1, 1, 9);
        if (r == null) {
            System.out.println("no way!");
        }
        else {
            for (final int i : r) {
                System.out.println(i);
            }
        }
        System.out.println("########################test world##########################");
    }
    
    public void main(final String[] args) {
        System.out.println("########################test world##########################");
        List<Integer> r = this.getDetailPath(1, 9, 1);
        if (r == null) {
            System.out.println("no way!");
        }
        else {
            for (final int i : r) {
                System.out.println(i);
            }
        }
        System.out.println("#############################################################");
        r = this.getDetailPath(1, 1, 9);
        if (r == null) {
            System.out.println("no way!");
        }
        else {
            for (final int i : r) {
                System.out.println(i);
            }
        }
        System.out.println("########################test world##########################");
    }
    
    public List<KfCity> getNearByCities(final int teamId) {
        final List<KfCity> clist = new ArrayList<KfCity>();
        final Set<Integer> cityIds = this.getNearCities(teamId);
        for (final Integer cId : cityIds) {
            clist.add(this.cities.get(cId));
        }
        return clist;
    }
    
    private Set<Integer> getNearCities(final int cityId) {
        final Set<Integer> result = new HashSet<Integer>();
        for (final KfgzWorldRoad wr : WorldRoadCache.getAllModels(KfgzManager.getWorldIdByGzId(this.gzId))) {
            if (1 == wr.getType() && this.getConnect(wr.getId()) == 0) {
                continue;
            }
            if (wr.getStart() == cityId) {
                result.add(wr.getEnd());
            }
            if (wr.getEnd() != cityId) {
                continue;
            }
            result.add(wr.getStart());
        }
        return result;
    }
    
    public int getForceCityNum(final int forceId) {
        int cityNum = 0;
        for (final Map.Entry<Integer, KfCity> entry : this.cities.entrySet()) {
            final KfCity c = entry.getValue();
            if (!c.isJieBingCity() && !c.isCaptial() && c.getForceId() == forceId) {
                ++cityNum;
            }
        }
        return cityNum;
    }
    
    @Override
    public void doEnd() {
    }
    
    public List<KfgzOfficeToken> getOfficeTokenListByForceId(final int forceId) {
        final List<KfgzOfficeToken> tokenList = new ArrayList<KfgzOfficeToken>();
        for (final KfgzOfficeToken token : this.officeTokenMap.values()) {
            if (token.isEffect() && token.getForceId() == forceId) {
                tokenList.add(token);
            }
        }
        return tokenList;
    }
    
    public void destroy() {
        this.doLoop = false;
    }
    
    @Override
    public void run() {
        final Calendar cal = Calendar.getInstance();
        this.second = cal.get(13);
        while (this.doLoop) {
            try {
                boolean needReload = false;
                int bobaoMark = 0;
                for (final KfgzWorldRoad wr : WorldRoadCache.getAllModels(KfgzManager.getWorldIdByGzId(this.gzId))) {
                    if (1 == wr.getType()) {
                        this.addMinutes(wr.getId());
                        if (this.getMinutes(wr.getId()) > wr.realGetLimitMinutes(this.getConnect(wr.getId()))) {
                            this.setMinutes(wr.getId(), 1);
                            this.changeConnect(wr.getId());
                            needReload = true;
                            if (this.getConnect(wr.getId()) != 1) {
                                continue;
                            }
                            bobaoMark = 1;
                        }
                        else {
                            if (this.getMinutes(wr.getId()) != wr.realGetLimitMinutes(wr.getId()) || this.getConnect(wr.getId()) != 1) {
                                continue;
                            }
                            bobaoMark = 2;
                        }
                    }
                }
                if (bobaoMark == 1) {
                    final String content = LocalMessages.CHAT_4;
                    KfgzMessageSender.sendChatToAll(this.gzId, content);
                }
                else if (bobaoMark == 2) {
                    final String content2 = LocalMessages.CHAT_5;
                    KfgzMessageSender.sendChatToAll(this.gzId, content2);
                }
                if (needReload) {
                    boolean changeAllPath = true;
                    for (final int forceId : this.allForceId) {
                        this.reLoadForceCities(forceId, changeAllPath);
                        changeAllPath = false;
                    }
                }
                Thread.sleep(60000L);
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(60000L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public int getBaseSecond() {
        return this.second;
    }
    
    public KfgzOrderToken getOrderTokenByCityIdAndForceId(final int cityId, final int forceId) {
        if (!this.getCities().containsKey(cityId)) {
            return null;
        }
        final KfgzOrderToken token = this.orderTokenMap.get(KfgzConstants.getWorldForcedKey(cityId, forceId));
        return token;
    }
    
    public void setNewOrderTokenByCityIdAndForceId(final KfgzOrderToken orderToken) {
        this.orderTokenMap.put(KfgzConstants.getWorldForcedKey(orderToken.getCityId(), orderToken.getForceId()), orderToken);
    }
    
    public List<KfgzOrderToken> getOrderTokenListByForceId(final int forceId) {
        final List<KfgzOrderToken> tokenList = new ArrayList<KfgzOrderToken>();
        for (final KfgzOrderToken token : this.orderTokenMap.values()) {
            if (token.isEffect() && token.getForceId() == forceId) {
                tokenList.add(token);
            }
        }
        return tokenList;
    }
}
