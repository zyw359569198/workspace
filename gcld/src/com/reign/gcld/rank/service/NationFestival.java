package com.reign.gcld.rank.service;

import com.reign.gcld.log.*;
import com.reign.framework.json.*;
import org.apache.commons.lang.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.common.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.sdata.domain.*;
import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import sun.misc.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class NationFestival extends Thread
{
    public final int TOTAL_SERIAL = 5;
    private final int RESOURCE_CATEGORY = 3;
    private final long SLEEP_TIME = 1000L;
    private final int[] CAPITAL_CITYID;
    private final int MAP_MAX_SIZE = 21;
    public ExecutorService executor;
    volatile int miracleState;
    private static ErrorLogger errorlog;
    private static TimerLogger timerLogger;
    private int nextSupplyMin;
    private long nextSupplyTime;
    Map<Integer, SupplyWorker> map;
    private AtomicInteger supplyNumber;
    private List<KtMrTroop> troops;
    private List<KtMrTroop> supplyTroops;
    private IDataGetter dataGetter;
    private List<Integer> defaultTimeList;
    int[] serialWinner;
    private static Object[] nationLock;
    private static String[][] typeNames;
    private ConcurrentHashMap<Integer, Integer> playerHungerMap;
    private MultiResult[] playerConsumeArray;
    private int index;
    private int[][] nationResource;
    private volatile boolean[] nationTaskIsOver;
    int[][] resources;
    int[] curSerial;
    private boolean isReboot;
    
    static {
        NationFestival.errorlog = new ErrorLogger();
        NationFestival.timerLogger = new TimerLogger();
        NationFestival.nationLock = new Object[3];
        NationFestival.typeNames = new String[5][3];
        NationFestival.nationLock[0] = new Object();
        NationFestival.nationLock[1] = new Object();
        NationFestival.nationLock[2] = new Object();
        NationFestival.typeNames[0][0] = "nuomi";
        NationFestival.typeNames[0][1] = "zhima";
        NationFestival.typeNames[0][1] = "baitang";
        NationFestival.typeNames[1][0] = "heimi";
        NationFestival.typeNames[1][1] = "huasheng";
        NationFestival.typeNames[1][2] = "bingtang";
        NationFestival.typeNames[2][0] = "zishu";
        NationFestival.typeNames[2][1] = "hongzao";
        NationFestival.typeNames[2][2] = "hongtang";
        NationFestival.typeNames[3][0] = "nangua";
        NationFestival.typeNames[3][1] = "liangua";
        NationFestival.typeNames[3][2] = "guihua";
        NationFestival.typeNames[4][0] = "mocha";
        NationFestival.typeNames[4][1] = "huotui";
        NationFestival.typeNames[4][2] = "jiuniang";
    }
    
    public NationFestival(final Date startDate, final int interval, final List<KtMrTroop> troops, final IDataGetter dataGetter, final List<KtMrTarget> targets, final List<Integer> defaltTimeList) {
        this.CAPITAL_CITYID = new int[] { 123, 19, 207 };
        this.executor = null;
        this.miracleState = 0;
        this.nextSupplyTime = 0L;
        this.map = new ConcurrentHashMap<Integer, SupplyWorker>();
        this.supplyNumber = new AtomicInteger(0);
        this.playerHungerMap = new ConcurrentHashMap<Integer, Integer>();
        this.playerConsumeArray = new MultiResult[5];
        this.index = 0;
        this.executor = Executors.newCachedThreadPool();
        this.nextSupplyMin = interval;
        this.nextSupplyTime = System.currentTimeMillis() + this.nextSupplyMin * 1000L;
        this.troops = troops;
        this.supplyTroops = this.getSupplyTroops(troops);
        this.dataGetter = dataGetter;
        this.serialWinner = new int[5];
        this.nationResource = this.getNationTargets(targets);
        this.resources = new int[3][3];
        this.nationTaskIsOver = new boolean[3];
        this.defaultTimeList = defaltTimeList;
        this.isReboot = false;
        this.curSerial = new int[3];
        final int[] curSerial = this.curSerial;
        final int n = 0;
        final int[] curSerial2 = this.curSerial;
        final int n2 = 1;
        final int[] curSerial3 = this.curSerial;
        final int n3 = 2;
        final boolean b = true;
        curSerial3[n3] = (b ? 1 : 0);
        curSerial[n] = (curSerial2[n2] = (b ? 1 : 0));
    }
    
    public boolean isReboot() {
        return this.isReboot;
    }
    
    public void setReboot(final boolean isReboot) {
        this.isReboot = isReboot;
    }
    
    public boolean[] getNationTaskIsOver() {
        return this.nationTaskIsOver;
    }
    
    public void setNationTaskIsOver(final boolean[] nationTaskIsOver) {
        this.nationTaskIsOver = nationTaskIsOver;
    }
    
    private int[][] getNationTargets(final List<KtMrTarget> targets) {
        final int[][] requestResource = new int[3][3];
        if (targets != null && !targets.isEmpty()) {
            for (int i = 0; i < targets.size(); ++i) {
                final KtMrTarget target = targets.get(i);
                requestResource[i][0] = target.getSoil();
                requestResource[i][1] = target.getStone();
                requestResource[i][2] = target.getLumber();
            }
        }
        return requestResource;
    }
    
    private List<KtMrTroop> getSupplyTroops(final List<KtMrTroop> troopsAll) {
        final List<KtMrTroop> result = new ArrayList<KtMrTroop>();
        for (final KtMrTroop ktMrTroop : troopsAll) {
            if (ktMrTroop.getType() == 2) {
                result.add(ktMrTroop);
            }
        }
        return result;
    }
    
    @Override
    public void run() {
        if (!this.isReboot) {
            this.executeTransportWorker();
        }
        while (this.miracleState == 0) {
            try {
                final long now = System.currentTimeMillis();
                if (now >= this.nextSupplyTime) {
                    for (final KtMrTroop troop : this.supplyTroops) {
                        if (troop.getType() == 1) {
                            continue;
                        }
                        final int nation = troop.getNation();
                        if (this.nationTaskIsOver[nation - 1]) {
                            continue;
                        }
                        final int keyNumber = this.supplyNumber.getAndAdd(1);
                        final SupplyWorker worker = new SupplyWorker(troop, this, keyNumber, this.defaultTimeList);
                        if (this.executor.isShutdown() || this.executor.isTerminated()) {
                            continue;
                        }
                        this.executor.execute(worker);
                    }
                    this.nextSupplyTime = now + this.nextSupplyMin * 1000L;
                }
            }
            catch (Exception e) {
                NationFestival.errorlog.error(this, e);
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e2) {
                NationFestival.errorlog.error(this, e2);
            }
        }
        if (this.miracleState != 0) {
            this.executor.shutdownNow();
            NationFestival.errorlog.error("miracle is shutting down");
        }
    }
    
    private void executeTransportWorker() {
        for (final KtMrTroop troop : this.troops) {
            try {
                final int keyNumber = this.supplyNumber.getAndAdd(1);
                final SupplyWorker worker = new SupplyWorker(troop, this, keyNumber, this.defaultTimeList);
                if (this.executor.isShutdown() || this.executor.isTerminated()) {
                    continue;
                }
                this.executor.execute(worker);
            }
            catch (Exception e) {
                NationFestival.errorlog.error(this, e);
            }
        }
    }
    
    public static void getWorkerMarchingInfo(final JsonDocument doc, final IDataGetter dataGetter, final SupplyWorker supplyWorker) {
        try {
            doc.createElement("key", supplyWorker.key);
            doc.createElement("nextAheadTime", supplyWorker.nextAheadTime - System.currentTimeMillis());
            doc.createElement("forceId", supplyWorker.nation);
            doc.createElement("troopId", supplyWorker.troopId);
            final int curPosition = supplyWorker.curPosition;
            if (curPosition <= supplyWorker.path.length - 1) {
                doc.createElement("curCityId", supplyWorker.path[curPosition]);
                if (curPosition + 1 <= supplyWorker.path.length - 1) {
                    final int nextCityId = supplyWorker.path[curPosition + 1];
                    final WorldCity worldCity = (WorldCity)dataGetter.getWorldCityCache().get((Object)nextCityId);
                    doc.createElement("nextCityId", supplyWorker.path[curPosition + 1]);
                    if (worldCity != null) {
                        doc.createElement("nextCityName", worldCity.getName());
                    }
                }
            }
            final int type = dataGetter.getKtNfCache().getTroopMaterialType(supplyWorker.troopId);
            doc.createElement("transportType", type);
            doc.createElement("marchingState", supplyWorker.marchingState);
            doc.createElement("workerState", supplyWorker.workerState);
        }
        catch (Exception e) {
            NationFestival.errorlog.error(NationMiracle.class, e);
        }
    }
    
    public void onWorkerPositionChange(final int curPosition, final SupplyWorker supplyWorker) {
        try {
            final String storeInfo = this.getToStore();
            if (!StringUtils.isBlank(storeInfo)) {
                this.dataGetter.getNationTaskDao().updateTaskRelativeInfo(storeInfo);
            }
            NationFestival.timerLogger.error("worker...key:" + supplyWorker.key + "troopId:" + supplyWorker.troopId + "reach city:" + curPosition);
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
        }
    }
    
    public void onArriveDestination(final SupplyWorker supplyWorker) {
        try {
            if (supplyWorker == null) {
                return;
            }
            final int nation = supplyWorker.nation;
            final int[] supply = supplyWorker.resource;
            this.addResource(nation, supply);
            this.pushNationMiralChangeInfo(nation);
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
        }
    }
    
    private boolean addResource(final int nation, final int[] supply) {
        try {
            synchronized (NationFestival.nationLock[nation - 1]) {
                for (int i = 0; i < 3; ++i) {
                    final int tempNum = supply[i] + this.resources[nation - 1][i];
                    this.resources[nation - 1][i] = Math.min(tempNum, this.nationResource[nation - 1][i]);
                }
            }
            // monitorexit(NationFestival.nationLock[nation - 1])
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
            return false;
        }
        return true;
    }
    
    private void pushNationMiralChangeInfo(final int nation) {
        try {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            this.getNationMiracleInfo(doc, nation);
            doc.endObject();
            final Group group = GroupManager.getInstance().getGroup(ChatType.WORLD.toString());
            RankService.pushToAll(PushCommand.PUSH_NATIONMIRACLE, doc.toByte(), group);
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
        }
    }
    
    public void getNationMiracleInfo(final JsonDocument doc, final int nation) {
        doc.startArray("miracle");
        int maxSerial = 1;
        for (int i = 0; i < this.CAPITAL_CITYID.length; ++i) {
            if (nation == 0 || nation == i + 1) {
                doc.startObject();
                final int forceId = i + 1;
                final int serial = this.curSerial[forceId - 1];
                doc.createElement("serial", serial);
                doc.createElement("forceId", forceId);
                doc.createElement("cityId", this.CAPITAL_CITYID[i]);
                doc.createElement("soil", this.resources[i][0]);
                final int soilPct = this.getResourcePct(forceId, 0);
                doc.createElement("stone", this.resources[i][1]);
                final int stonePct = this.getResourcePct(forceId, 1);
                doc.createElement("lumber", this.resources[i][2]);
                final int lumberPct = this.getResourcePct(forceId, 2);
                doc.createElement("totalPct", (soilPct + stonePct + lumberPct) / 3);
                doc.endObject();
                if (serial > maxSerial) {
                    maxSerial = serial;
                }
            }
        }
        doc.endArray();
        final int firstNation = this.getTheFirst(maxSerial);
        final int totalPct = this.getTheTotalPct(firstNation);
        doc.createElement("force1st", firstNation);
        doc.createElement("pct1st", totalPct);
        doc.createElement("maxSerial", maxSerial);
    }
    
    public void onWorkerAdd(final SupplyWorker supplyWorker) {
        try {
            this.map.put(supplyWorker.key, supplyWorker);
            supplyWorker.checkWorkerMarchingState(supplyWorker.curPosition, supplyWorker.path, supplyWorker.curPosition);
            NationFestival.timerLogger.error("worker...key:" + supplyWorker.key + "troopId:" + supplyWorker.troopId + "start....");
            final int size = this.map.size();
            NationFestival.timerLogger.error("map size:" + size);
            if (size > 21) {
                this.executor.shutdownNow();
                NationFestival.errorlog.error("map size is too big .. to avoid heap full..shut down excuter.");
            }
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
        }
    }
    
    public void onWorkFinished(final SupplyWorker supplyWorker) {
        this.map.remove(supplyWorker.key);
        NationFestival.timerLogger.error("worker...key:" + supplyWorker.key + "troopId:" + supplyWorker.troopId + "finished....");
        if (supplyWorker.type == 1 && !this.executor.isShutdown() && !this.nationTaskIsOver[supplyWorker.nation - 1]) {
            final SupplyWorker worker = new SupplyWorker(supplyWorker);
            this.executor.execute(worker);
        }
    }
    
    public static int getCityType(final int defForceId, final int playerForceId, final int state) {
        return (defForceId == playerForceId) ? ((state == 0) ? 1 : 2) : ((state == 0) ? 4 : 3);
    }
    
    public boolean nationResourceFull(final int forceId) {
        boolean result = true;
        for (int i = 0; i < 3; ++i) {
            if (this.resources[forceId - 1][i] < this.nationResource[forceId - 1][i]) {
                result = false;
            }
        }
        return result;
    }
    
    public void clearResource(final int forceId, final KtMrTarget target) {
        synchronized (NationFestival.nationLock[forceId - 1]) {
            for (int i = 0; i < 3; ++i) {
                this.resources[forceId - 1][i] = 0;
            }
            if (target != null) {
                this.nationResource[forceId - 1][0] = target.getSoil();
                this.nationResource[forceId - 1][1] = target.getStone();
                this.nationResource[forceId - 1][2] = target.getLumber();
            }
        }
        // monitorexit(NationFestival.nationLock[forceId - 1])
        this.clearPlayerConsumeArray();
    }
    
    public void setNationTaskIsOver(final int forceId, final boolean isOver) {
        this.nationTaskIsOver[forceId - 1] = isOver;
    }
    
    public int getResourcePct(final int forceId, final int i) {
        if (forceId < 1 || forceId > 3) {
            return 0;
        }
        final int request = this.nationResource[forceId - 1][i];
        final int real = this.resources[forceId - 1][i];
        final int result = (int)(real / request * 100.0f);
        return result;
    }
    
    public void changeMiracleState(final int state) {
        if (this.miracleState != state) {
            this.miracleState = state;
        }
    }
    
    public String getToStore() {
        try {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < this.serialWinner.length; ++i) {
                sb.append(this.serialWinner[i]).append(",");
            }
            SymbolUtil.removeTheLast(sb);
            sb.append(";");
            sb.append(this.miracleState).append(";");
            for (int i = 0; i < this.resources.length; ++i) {
                for (int j = 0; j < 3; ++j) {
                    sb.append(this.resources[i][j]).append(",");
                }
            }
            SymbolUtil.removeTheLast(sb);
            sb.append(";");
            for (final SupplyWorker worker : this.map.values()) {
                if (worker != null) {
                    sb.append(worker.troopId).append(",").append(worker.curPosition).append("#");
                }
            }
            SymbolUtil.removeTheLast(sb);
            return sb.toString();
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
            return "";
        }
    }
    
    public void restoreMiracle(final String storeInfo, final boolean allIsOver) {
        try {
            if (StringUtils.isBlank(storeInfo)) {
                return;
            }
            final String[] strs = storeInfo.split(";");
            final String miracleInfoString = strs[2];
            String workerInfo = "";
            if (strs.length >= 4) {
                workerInfo = strs[3];
            }
            final int nationMiracleState = Integer.parseInt(strs[1]);
            final String serialWinnerInfo = strs[0];
            final int[] winner = SymbolUtil.StringtoIntArray(serialWinnerInfo, ",");
            this.serialWinner = winner;
            this.miracleState = nationMiracleState;
            this.restoreNationMiracle(miracleInfoString);
            if (this.miracleState == 0 && !allIsOver) {
                this.restoreWorkerInfo(workerInfo);
            }
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
        }
    }
    
    private void restoreNationMiracle(final String miracleInfoString) {
        try {
            final String[] single = miracleInfoString.split(",");
            if (single.length != 9) {
                NationFestival.errorlog.error("length is wrong....length" + single.length);
            }
            int forceId = 0;
            for (int i = 0; i < 9; ++i) {
                final int j = i % 3;
                if (j == 0 && i != 0) {
                    ++forceId;
                }
                this.resources[forceId][j] = Integer.parseInt(single[i]);
            }
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
        }
    }
    
    private void restoreWorkerInfo(final String workerInfo) {
        if (StringUtils.isBlank(workerInfo)) {
            return;
        }
        try {
            final String[] single = workerInfo.split("#");
            for (int i = 0; i < single.length; ++i) {
                if (!StringUtils.isBlank(single[i])) {
                    final String[] cell = single[i].split(",");
                    final int troopId = Integer.parseInt(cell[0]);
                    final int curCityId = Integer.parseInt(cell[1]);
                    final KtMrTroop troop = this.dataGetter.getKtNfCache().getTroop(troopId);
                    if (troop != null) {
                        final int key = this.supplyNumber.getAndAdd(1);
                        final SupplyWorker supplyWorker = new SupplyWorker(troop, this, key, this.defaultTimeList);
                        if (curCityId >= supplyWorker.path.length) {
                            NationFestival.errorlog.error("initial worker cityId Wrong....cityId:" + curCityId);
                        }
                        else {
                            supplyWorker.curPosition = curCityId;
                        }
                        this.map.put(key, supplyWorker);
                        if (!this.executor.isShutdown() && !this.executor.isTerminated()) {
                            this.executor.execute(supplyWorker);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
        }
    }
    
    public int getTheFirst(final int nowSerial) {
        try {
            final int force = this.serialWinner[nowSerial - 1];
            if (force == 0) {
                int max = 0;
                int index = 0;
                for (int i = 0; i < 3; ++i) {
                    final int nationSerial = this.curSerial[i];
                    if (nationSerial == nowSerial) {
                        final int total = this.resources[i][0] + this.resources[i][1] + this.resources[i][2];
                        if (total > max) {
                            max = total;
                            index = i;
                        }
                    }
                }
                return index + 1;
            }
            return force;
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
            return 0;
        }
    }
    
    public int getTheTotalPct(final int firstForce) {
        try {
            return (this.getResourcePct(firstForce, 0) + this.getResourcePct(firstForce, 1) + this.getResourcePct(firstForce, 2)) / 3;
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
            return 0;
        }
    }
    
    public String getResourceNamePic(final int type, int serial) {
        try {
            serial = ((serial < 1) ? 1 : (serial - 1));
            return NationFestival.typeNames[serial][type];
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
            return "";
        }
    }
    
    public ConcurrentHashMap<Integer, Integer> getPlayerHungerMap() {
        return this.playerHungerMap;
    }
    
    public void setPlayerHungerMap(final ConcurrentHashMap<Integer, Integer> playerHungerMap) {
        this.playerHungerMap = playerHungerMap;
    }
    
    public void updatePlayerHunger(final int number, final List<PlayerHunger> toUpdate) {
        if (number <= 0) {
            return;
        }
        for (final PlayerHunger hunger : toUpdate) {
            try {
                if (this.playerHungerMap.containsKey(hunger.getPlayerId())) {
                    final int value = this.playerHungerMap.get(hunger.getPlayerId());
                    this.playerHungerMap.put(hunger.getPlayerId(), value + hunger.getHunger());
                }
                else {
                    this.playerHungerMap.putIfAbsent(hunger.getPlayerId(), hunger.getHunger());
                }
            }
            catch (Exception e) {
                NationFestival.errorlog.error(this, e);
            }
        }
    }
    
    public int getplayerHunger(final int playerId) {
        final Integer hunger = this.playerHungerMap.get(playerId);
        return (hunger == null) ? 0 : hunger;
    }
    
    public void restorePlayerMap(final List<PlayerHunger> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (final PlayerHunger playerHunger : list) {
            this.playerHungerMap.put(playerHunger.getPlayerId(), playerHunger.getHunger());
        }
    }
    
    public boolean getNationTaskIsOver(final int forceId) {
        return this.nationTaskIsOver[forceId - 1];
    }
    
    public boolean updateResourceByGold(final int type, final int forceId) {
        try {
            synchronized (NationFestival.nationLock[forceId - 1]) {
                if (this.nationResource[forceId - 1][type] <= this.resources[forceId - 1][type]) {
                    // monitorexit(NationFestival.nationLock[forceId - 1])
                    return false;
                }
                final int[] supply = new int[3];
                supply[type] = this.nationResource[forceId - 1][type];
                // monitorexit(NationFestival.nationLock[forceId - 1])
                return this.addResource(forceId, supply);
            }
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
            return false;
        }
    }
    
    public void addPlayerInArray(final String playerName, final int type) {
        final MultiResult result = new MultiResult();
        result.result1 = playerName;
        result.result2 = type;
        this.playerConsumeArray[this.index++] = result;
        this.index %= this.playerConsumeArray.length;
    }
    
    public MultiResult[] getPlayerConsumeArray() {
        return this.playerConsumeArray;
    }
    
    public void setPlayerConsumeArray(final MultiResult[] playerConsumeArray) {
        this.playerConsumeArray = playerConsumeArray;
    }
    
    public void clearPlayerConsumeArray() {
        for (int i = 0; i < this.playerConsumeArray.length; ++i) {
            this.playerConsumeArray[i] = null;
        }
    }
    
    public int[] getCurSerial() {
        return this.curSerial;
    }
    
    public void setCurSerial(final int[] curSerial) {
        this.curSerial = curSerial;
    }
    
    public void transferPlayerHunger() {
        try {
            for (final Integer playerId : this.playerHungerMap.keySet()) {
                final int value = this.playerHungerMap.get(playerId);
                EventUtil.handleOperation(playerId, 21, value);
            }
            NationFestival.errorlog.error("transfer player hunger size:" + this.playerHungerMap.size());
        }
        catch (Exception e) {
            NationFestival.errorlog.error(this, e);
        }
    }
    
    public NationFestival copyAttributes(final NationFestival festival) {
        this.curSerial = festival.curSerial;
        this.nationResource = festival.nationResource;
        this.nationTaskIsOver = festival.nationTaskIsOver;
        this.serialWinner = festival.serialWinner;
        this.playerConsumeArray = festival.playerConsumeArray;
        this.playerHungerMap = festival.playerHungerMap;
        this.resources = festival.resources;
        return this;
    }
    
    public class SupplyWorker implements Runnable
    {
        int destination;
        int source;
        int type;
        int[] path;
        int[] resource;
        String name;
        NationFestival miracle;
        int workerState;
        int key;
        int curPosition;
        long nextAheadTime;
        int marchingState;
        int nation;
        int troopId;
        int defaultTime;
        
        public SupplyWorker() {
        }
        
        public SupplyWorker(final KtMrTroop troop, final NationFestival miracle, final int key, final List<Integer> defaultTimeList) {
            this.nation = troop.getNation();
            this.destination = troop.getNation();
            this.source = troop.getTroopId();
            this.type = troop.getType();
            this.path = SymbolUtil.StringtoIntArray(troop.getPath(), ";");
            (this.resource = new int[3])[0] = troop.getSoil();
            this.resource[1] = troop.getStone();
            this.resource[2] = troop.getLumber();
            this.name = troop.getName();
            this.miracle = miracle;
            this.workerState = 0;
            this.key = key;
            this.curPosition = 0;
            this.marchingState = 1;
            this.troopId = troop.getId();
            this.defaultTime = defaultTimeList.get(this.type - 1);
            this.nextAheadTime = System.currentTimeMillis() + this.defaultTime * 1000L;
        }
        
        public SupplyWorker(final SupplyWorker supplyWorker) {
            this.nation = supplyWorker.nation;
            this.destination = supplyWorker.destination;
            this.source = supplyWorker.source;
            this.type = supplyWorker.type;
            this.path = supplyWorker.path;
            this.resource = supplyWorker.resource;
            this.name = supplyWorker.name;
            this.miracle = supplyWorker.miracle;
            this.workerState = 0;
            this.key = supplyWorker.key;
            this.curPosition = 0;
            this.marchingState = 1;
            this.defaultTime = supplyWorker.defaultTime;
            this.troopId = supplyWorker.troopId;
            this.nextAheadTime = System.currentTimeMillis() + this.defaultTime * 1000L;
        }
        
        @Override
        public void run() {
            this.miracle.onWorkerAdd(this);
            final int workerNation = this.nation;
            while (this.workerState == 0 && !NationFestival.this.nationTaskIsOver[workerNation - 1] && NationFestival.this.miracleState == 0 && this.curPosition < this.path.length) {
                final long now = System.currentTimeMillis();
                final int beforePosition = this.curPosition;
                if (this.nextAheadTime < now) {
                    ++this.curPosition;
                    this.miracle.onWorkerPositionChange(this.curPosition, this);
                    if (this.curPosition >= this.path.length - 1) {
                        this.miracle.onArriveDestination(this);
                        this.workerState = 1;
                    }
                }
                this.checkWorkerMarchingState(this.curPosition, this.path, beforePosition);
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e) {
                    if (this.miracle.executor.isShutdown() || this.miracle.executor.isTerminated()) {
                        this.workerState = 1;
                    }
                    else {
                        NationFestival.errorlog.error(this, e);
                    }
                }
            }
            if (this.workerState == 0) {
                this.workerState = 1;
            }
            this.miracle.onWorkFinished(this);
        }
        
        private void checkWorkerMarchingState(final int curCity, final int[] path, final int beforePosition) {
            try {
                if (curCity >= path.length) {
                    NationFestival.errorlog.error("checkWorkerMarchingState.too big..curCity:" + curCity + "key:" + this.key + "source:" + path[0]);
                    return;
                }
                final City city = CityDataCache.cityArray[path[curCity]];
                if (city == null) {
                    NationFestival.errorlog.error("checkWorkerMarchingState..city null.curCity:" + curCity + "key:" + this.key + "source:" + path[0]);
                    return;
                }
                final int forceId = city.getForceId();
                final int state = city.getState();
                final int cityType = NationFestival.getCityType(forceId, this.nation, state);
                if (cityType != this.marchingState || beforePosition != curCity) {
                    final KtMrSpeed ori = NationFestival.this.dataGetter.getKtNfCache().getSpeedByTroopAndCityType(this.type, this.marchingState);
                    final KtMrSpeed nos = NationFestival.this.dataGetter.getKtNfCache().getSpeedByTroopAndCityType(this.type, cityType);
                    if (ori == null || nos == null) {
                        NationFestival.errorlog.error("checkWorkerMarchingState..ori is null...cityId:" + city.getId() + "nation:" + this.nation + "state:" + state + "pre:" + this.marchingState + "now:" + cityType);
                        return;
                    }
                    final int oriTime = ori.getTime();
                    final int nowTime = nos.getTime();
                    this.marchingState = cityType;
                    if (beforePosition != curCity) {
                        this.nextAheadTime += nowTime * 1000L;
                    }
                    else {
                        this.nextAheadTime += (nowTime - oriTime) * 1000L;
                    }
                    this.pushWorkerMarchingState();
                }
            }
            catch (Exception e) {
                NationFestival.errorlog.error(this, e);
            }
        }
        
        private void pushWorkerMarchingState() {
            try {
                if (NationFestival.this.miracleState != 0) {
                    return;
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                NationFestival.getWorkerMarchingInfo(doc, NationFestival.this.dataGetter, this);
                doc.endObject();
                final Group group = GroupManager.getInstance().getGroup(ChatType.WORLD.toString());
                RankService.pushToAll(PushCommand.PUSH_NATIONMIRAL_WORKERINFO, doc.toByte(), group);
            }
            catch (Exception e) {
                NationFestival.errorlog.error(this, e);
            }
        }
    }
}
