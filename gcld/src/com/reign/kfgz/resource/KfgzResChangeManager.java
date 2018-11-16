package com.reign.kfgz.resource;

import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import com.reign.kfgz.control.*;
import com.reign.kfgz.comm.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.dto.response.*;
import com.reign.kf.comm.util.*;
import com.reign.kfgz.resource.dto.*;
import java.util.*;
import com.reign.kf.match.log.*;

public class KfgzResChangeManager
{
    private static Logger logger;
    private static Map<Integer, Integer> resMapGold;
    private static Map<Integer, Long> resMapCopper;
    private static Map<Integer, Long> resMapWood;
    private static Map<Integer, Long> resMapFood;
    private static Map<Integer, Long> resMapIron;
    private static Map<Integer, Integer> resMapRecruitToken;
    private static Map<Integer, Integer> resMapPhantomCount;
    private static Map<Integer, Integer> mubing;
    private static final int lockSize = 1000;
    private static ReentrantLock[] teamLock;
    private static Map<Integer, ConcurrentLinkedQueue<ResChangeRecord>> resChangeMap;
    private static AtomicLong atomicId;
    
    static {
        KfgzResChangeManager.logger = new PlayerInfoLogger();
        KfgzResChangeManager.resMapGold = new ConcurrentHashMap<Integer, Integer>();
        KfgzResChangeManager.resMapCopper = new ConcurrentHashMap<Integer, Long>();
        KfgzResChangeManager.resMapWood = new ConcurrentHashMap<Integer, Long>();
        KfgzResChangeManager.resMapFood = new ConcurrentHashMap<Integer, Long>();
        KfgzResChangeManager.resMapIron = new ConcurrentHashMap<Integer, Long>();
        KfgzResChangeManager.resMapRecruitToken = new ConcurrentHashMap<Integer, Integer>();
        KfgzResChangeManager.resMapPhantomCount = new ConcurrentHashMap<Integer, Integer>();
        KfgzResChangeManager.mubing = new ConcurrentHashMap<Integer, Integer>();
        KfgzResChangeManager.teamLock = new ReentrantLock[1000];
        for (int i = 0; i < 1000; ++i) {
            KfgzResChangeManager.teamLock[i] = new ReentrantLock();
        }
        KfgzResChangeManager.resChangeMap = new HashMap<Integer, ConcurrentLinkedQueue<ResChangeRecord>>();
        KfgzResChangeManager.atomicId = new AtomicLong();
    }
    
    public static long getNowResourceVersion() {
        return KfgzResChangeManager.atomicId.get();
    }
    
    public static void resetAtomciId() {
        KfgzResChangeManager.atomicId.set(0L);
    }
    
    public static int getMubingNum(final int cId) {
        if (!KfgzResChangeManager.resMapRecruitToken.containsKey(cId)) {
            return 0;
        }
        return KfgzResChangeManager.resMapRecruitToken.get(cId);
    }
    
    public static void addGeneralExp(final int cId, final int gId, final int exp, final String reason) {
        final KfPlayerInfo kpi = KfgzPlayerManager.getPlayerByCId(cId);
        KfgzResChangeManager.teamLock[cId % 1000].lock();
        try {
            changeResourceGeneralExp(kpi, "gExp", exp, true, gId, reason);
        }
        finally {
            KfgzResChangeManager.teamLock[cId % 1000].unlock();
        }
        KfgzResChangeManager.teamLock[cId % 1000].unlock();
    }
    
    public static int getMubing(final int cId) {
        if (KfgzResChangeManager.mubing.containsKey(cId)) {
            return KfgzResChangeManager.mubing.get(cId);
        }
        return 0;
    }
    
    public static int getPhantomCount(final int cId) {
        if (KfgzResChangeManager.resMapPhantomCount.containsKey(cId)) {
            return KfgzResChangeManager.resMapPhantomCount.get(cId);
        }
        return 0;
    }
    
    public static void syncResChangeByVersion(final KfgzSyncDataParam param, final KfgzSyncDataResult result) {
        final int cId = param.getcId();
        KfgzResChangeManager.teamLock[cId % 1000].lock();
        try {
            KfgzResChangeManager.resMapGold.put(param.getcId(), param.getGold());
            KfgzResChangeManager.resMapCopper.put(param.getcId(), param.getCopper());
            KfgzResChangeManager.resMapWood.put(param.getcId(), param.getWood());
            KfgzResChangeManager.resMapFood.put(param.getcId(), param.getFood());
            KfgzResChangeManager.resMapIron.put(param.getcId(), param.getIron());
            KfgzResChangeManager.resMapPhantomCount.put(param.getcId(), param.getPhantomCount());
            KfgzResChangeManager.mubing.put(param.getcId(), param.getMubing());
            KfgzResChangeManager.resMapRecruitToken.put(param.getcId(), param.getRecruitToken());
            final ConcurrentLinkedQueue<ResChangeRecord> rcrQueue = KfgzResChangeManager.resChangeMap.get(param.getcId());
            if (rcrQueue == null) {
                return;
            }
            for (ResChangeRecord rr = rcrQueue.peek(); rr != null && rr.getId() <= param.getVersion(); rr = rcrQueue.peek()) {
                rcrQueue.poll();
            }
            result.setCopper(0L);
            result.setExp(0);
            result.setFood(0L);
            result.setGold(0);
            result.setIron(0L);
            result.setWood(0L);
            result.setPhantomCount(0);
            for (final ResChangeRecord r : rcrQueue) {
                if (r.getUnit().equalsIgnoreCase("gold")) {
                    if (r.getIncrease()) {
                        result.setGold(result.getGold() + (int)r.getValue());
                    }
                    else {
                        result.setGold(result.getGold() - (int)r.getValue());
                    }
                }
                else if (r.getUnit().equalsIgnoreCase("copper")) {
                    if (r.getIncrease()) {
                        result.setCopper(result.getCopper() + r.getValue());
                    }
                    else {
                        result.setCopper(result.getCopper() - r.getValue());
                    }
                }
                else if (r.getUnit().equalsIgnoreCase("wood")) {
                    if (r.getIncrease()) {
                        result.setWood(result.getWood() + r.getValue());
                    }
                    else {
                        result.setWood(result.getWood() - r.getValue());
                    }
                }
                else if (r.getUnit().equalsIgnoreCase("food")) {
                    if (r.getIncrease()) {
                        result.setFood(result.getFood() + r.getValue());
                    }
                    else {
                        result.setFood(result.getFood() - r.getValue());
                    }
                }
                else if (r.getUnit().equalsIgnoreCase("iron")) {
                    if (r.getIncrease()) {
                        result.setIron(result.getIron() + r.getValue());
                    }
                    else {
                        result.setIron(result.getIron() - r.getValue());
                    }
                }
                else if (r.getUnit().equalsIgnoreCase("recruitToken")) {
                    if (r.getIncrease()) {
                        result.setRecruitToken(result.getRecruitToken() + (int)r.getValue());
                    }
                    else {
                        result.setRecruitToken(result.getRecruitToken() - (int)r.getValue());
                    }
                }
                else if (r.getUnit().equalsIgnoreCase("phantomCount")) {
                    if (r.getIncrease()) {
                        result.setPhantomCount(result.getPhantomCount() + (int)r.getValue());
                    }
                    else {
                        result.setPhantomCount(result.getPhantomCount() - (int)r.getValue());
                    }
                }
                else if (r.getUnit().equalsIgnoreCase("exp")) {
                    if (r.getIncrease()) {
                        result.setExp(result.getExp() + (int)r.getValue());
                    }
                    else {
                        result.setExp(result.getExp() - (int)r.getValue());
                    }
                }
                else if (r.getUnit().equalsIgnoreCase("gExp") && r.getIncrease()) {
                    final Tuple<Integer, Integer> t = new Tuple();
                    t.left = r.getgId();
                    t.right = (int)r.getValue();
                    if (result.getgExp() == null) {
                        result.setgExp(new ArrayList());
                    }
                    result.getgExp().add(t);
                }
                result.setVersionTo(r.getId());
            }
            KfgzResChangeManager.resMapGold.put(param.getcId(), KfgzResChangeManager.resMapGold.get(param.getcId()) + result.getGold());
            KfgzResChangeManager.resMapCopper.put(param.getcId(), KfgzResChangeManager.resMapCopper.get(param.getcId()) + result.getCopper());
            KfgzResChangeManager.resMapWood.put(param.getcId(), KfgzResChangeManager.resMapWood.get(param.getcId()) + result.getWood());
            KfgzResChangeManager.resMapFood.put(param.getcId(), KfgzResChangeManager.resMapFood.get(param.getcId()) + result.getFood());
            KfgzResChangeManager.resMapIron.put(param.getcId(), KfgzResChangeManager.resMapIron.get(param.getcId()) + result.getIron());
            KfgzResChangeManager.resMapRecruitToken.put(param.getcId(), KfgzResChangeManager.resMapRecruitToken.get(param.getcId()) + result.getRecruitToken());
            KfgzResChangeManager.resMapPhantomCount.put(param.getcId(), KfgzResChangeManager.resMapPhantomCount.get(param.getcId()) + result.getPhantomCount());
        }
        finally {
            KfgzResChangeManager.teamLock[cId % 1000].unlock();
        }
        KfgzResChangeManager.teamLock[cId % 1000].unlock();
    }
    
    public static void addResource(final int cId, final int value, final String unit, final String reason) {
        final KfPlayerInfo kpi = KfgzPlayerManager.getPlayerByCId(cId);
        KfgzResChangeManager.teamLock[cId % 1000].lock();
        try {
            if (unit.equalsIgnoreCase("gold")) {
                KfgzResChangeManager.resMapGold.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapGold.get(kpi.getCompetitorId()) + value);
                changeResource(kpi, "gold", value, true, reason);
            }
            else if (unit.equalsIgnoreCase("copper")) {
                KfgzResChangeManager.resMapCopper.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapCopper.get(kpi.getCompetitorId()) + value);
                changeResource(kpi, "copper", value, true, reason);
            }
            else if (unit.equalsIgnoreCase("wood")) {
                KfgzResChangeManager.resMapWood.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapWood.get(kpi.getCompetitorId()) + value);
                changeResource(kpi, "wood", value, true, reason);
            }
            else if (unit.equalsIgnoreCase("food")) {
                KfgzResChangeManager.resMapFood.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapFood.get(kpi.getCompetitorId()) + value);
                changeResource(kpi, "food", value, true, reason);
            }
            else if (unit.equalsIgnoreCase("iron")) {
                KfgzResChangeManager.resMapIron.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapIron.get(kpi.getCompetitorId()) + value);
                changeResource(kpi, "iron", value, true, reason);
            }
            else if (unit.equalsIgnoreCase("exp")) {
                changeResource(kpi, "exp", value, true, reason);
            }
            else if (unit.equalsIgnoreCase("recruitToken")) {
                KfgzResChangeManager.resMapRecruitToken.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapRecruitToken.get(kpi.getCompetitorId()) + value);
                changeResource(kpi, "recruitToken", value, true, reason);
            }
            else if (unit.equalsIgnoreCase("phantomCount")) {
                KfgzResChangeManager.resMapPhantomCount.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapPhantomCount.get(kpi.getCompetitorId()) + value);
                changeResource(kpi, "phantomCount", value, true, reason);
            }
        }
        finally {
            KfgzResChangeManager.teamLock[cId % 1000].unlock();
        }
        KfgzResChangeManager.teamLock[cId % 1000].unlock();
    }
    
    public static boolean canConsumeResource(final int cId, final int value, final String unit) {
        final KfPlayerInfo kpi = KfgzPlayerManager.getPlayerByCId(cId);
        if (kpi == null) {
            return false;
        }
        if (unit.equalsIgnoreCase("gold")) {
            if (KfgzResChangeManager.resMapGold.containsKey(kpi.getCompetitorId()) && KfgzResChangeManager.resMapGold.get(kpi.getCompetitorId()) >= value) {
                return true;
            }
        }
        else if (unit.equalsIgnoreCase("copper")) {
            if (KfgzResChangeManager.resMapCopper.containsKey(kpi.getCompetitorId()) && KfgzResChangeManager.resMapCopper.get(kpi.getCompetitorId()) >= value) {
                return true;
            }
        }
        else if (unit.equalsIgnoreCase("wood")) {
            if (KfgzResChangeManager.resMapWood.containsKey(kpi.getCompetitorId()) && KfgzResChangeManager.resMapWood.get(kpi.getCompetitorId()) >= value) {
                return true;
            }
        }
        else if (unit.equalsIgnoreCase("food")) {
            if (KfgzResChangeManager.resMapFood.containsKey(kpi.getCompetitorId()) && KfgzResChangeManager.resMapFood.get(kpi.getCompetitorId()) >= value) {
                return true;
            }
        }
        else if (unit.equalsIgnoreCase("iron")) {
            if (KfgzResChangeManager.resMapIron.containsKey(kpi.getCompetitorId()) && KfgzResChangeManager.resMapIron.get(kpi.getCompetitorId()) >= value) {
                return true;
            }
        }
        else if (unit.equalsIgnoreCase("recruitToken")) {
            if (KfgzResChangeManager.resMapRecruitToken.containsKey(kpi.getCompetitorId()) && KfgzResChangeManager.resMapRecruitToken.get(kpi.getCompetitorId()) >= value) {
                return true;
            }
        }
        else if (unit.equalsIgnoreCase("phantomCount")) {
            if (KfgzResChangeManager.resMapPhantomCount.containsKey(kpi.getCompetitorId()) && KfgzResChangeManager.resMapPhantomCount.get(kpi.getCompetitorId()) >= value) {
                return true;
            }
        }
        else {
            unit.equalsIgnoreCase("exp");
        }
        return false;
    }
    
    public static boolean consumeResourceList(final int cId, final List<ConsumeResourceDto> crdList) {
        for (final ConsumeResourceDto crd : crdList) {
            if (!canConsumeResource(cId, crd.getValue(), crd.getUnit())) {
                return false;
            }
        }
        KfgzResChangeManager.teamLock[cId % 1000].lock();
        try {
            for (final ConsumeResourceDto crd : crdList) {
                if (!canConsumeResource(cId, crd.getValue(), crd.getUnit())) {
                    return false;
                }
            }
            for (final ConsumeResourceDto crd : crdList) {
                consumeResource(cId, crd.getValue(), crd.getUnit(), crd.getReason());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            KfgzResChangeManager.teamLock[cId % 1000].unlock();
        }
        KfgzResChangeManager.teamLock[cId % 1000].unlock();
        return true;
    }
    
    public static boolean consumeResource(final int cId, final int value, final String unit, final String reason) {
        final KfPlayerInfo kpi = KfgzPlayerManager.getPlayerByCId(cId);
        if (!canConsumeResource(cId, value, unit)) {
            return false;
        }
        KfgzResChangeManager.teamLock[cId % 1000].lock();
        try {
            if (!canConsumeResource(cId, value, unit)) {
                return false;
            }
            if (unit.equalsIgnoreCase("gold")) {
                KfgzResChangeManager.resMapGold.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapGold.get(kpi.getCompetitorId()) - value);
                changeResource(kpi, "gold", value, false, reason);
            }
            else if (unit.equalsIgnoreCase("copper")) {
                KfgzResChangeManager.resMapCopper.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapCopper.get(kpi.getCompetitorId()) - value);
                changeResource(kpi, "copper", value, false, reason);
            }
            else if (unit.equalsIgnoreCase("wood")) {
                KfgzResChangeManager.resMapWood.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapWood.get(kpi.getCompetitorId()) - value);
                changeResource(kpi, "wood", value, false, reason);
            }
            else if (unit.equalsIgnoreCase("food")) {
                KfgzResChangeManager.resMapFood.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapFood.get(kpi.getCompetitorId()) - value);
                changeResource(kpi, "food", value, false, reason);
            }
            else if (unit.equalsIgnoreCase("iron")) {
                KfgzResChangeManager.resMapIron.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapIron.get(kpi.getCompetitorId()) - value);
                changeResource(kpi, "iron", value, false, reason);
            }
            else if (unit.equalsIgnoreCase("recruitToken")) {
                KfgzResChangeManager.resMapRecruitToken.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapRecruitToken.get(kpi.getCompetitorId()) - value);
                changeResource(kpi, "recruitToken", value, false, reason);
            }
            else if (unit.equalsIgnoreCase("phantomCount")) {
                KfgzResChangeManager.resMapPhantomCount.put(kpi.getCompetitorId(), KfgzResChangeManager.resMapPhantomCount.get(kpi.getCompetitorId()) - value);
                changeResource(kpi, "phantomCount", value, false, reason);
            }
            else {
                unit.equalsIgnoreCase("exp");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            KfgzResChangeManager.teamLock[cId % 1000].unlock();
        }
        KfgzResChangeManager.teamLock[cId % 1000].unlock();
        return true;
    }
    
    private static void changeResourceGeneralExp(final KfPlayerInfo kpi, final String unit, final long value, final boolean increase, final int gId, final String reason) {
        doCchangeResource(kpi, unit, value, increase, gId, reason);
    }
    
    private static void changeResource(final KfPlayerInfo kpi, final String unit, final long value, final boolean increase, final String reason) {
        doCchangeResource(kpi, unit, value, increase, 0, reason);
    }
    
    private static void doCchangeResource(final KfPlayerInfo kpi, final String unit, final long value, final boolean increase, final int gId, final String reason) {
        final ResChangeRecord rcr = new ResChangeRecord();
        rcr.setId(KfgzResChangeManager.atomicId.incrementAndGet());
        rcr.setUnit(unit);
        rcr.setValue(value);
        rcr.setIncrease(increase);
        rcr.setTime(new Date());
        rcr.setgId(gId);
        if (!KfgzResChangeManager.resChangeMap.containsKey(kpi.getCompetitorId())) {
            final ConcurrentLinkedQueue<ResChangeRecord> queue = new ConcurrentLinkedQueue<ResChangeRecord>();
            KfgzResChangeManager.resChangeMap.put(kpi.getCompetitorId(), queue);
        }
        KfgzResChangeManager.resChangeMap.get(kpi.getCompetitorId()).add(rcr);
        KfgzResChangeManager.logger.info(LogUtil.formatPlayerInfoLog(kpi.getCompetitorId(), kpi.getPlayerName(), kpi.getPlayerLevel(), kpi.getServerName(), kpi.getServerId(), unit, value, increase ? "+" : "-", rcr.getId(), 0, reason));
    }
}
