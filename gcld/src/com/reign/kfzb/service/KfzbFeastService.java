package com.reign.kfzb.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import java.util.concurrent.locks.*;
import com.reign.kfzb.dao.*;
import org.springframework.context.*;
import java.util.concurrent.atomic.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import com.reign.kf.comm.entity.*;
import com.reign.kfzb.domain.*;
import java.util.*;
import com.reign.kfzb.constants.*;
import org.springframework.beans.*;
import com.reign.framework.hibernate.model.*;
import org.springframework.transaction.annotation.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.dto.response.*;

@Component
public class KfzbFeastService implements IKfzbFeastService, InitializingBean, ApplicationContextAware
{
    static volatile int state;
    static volatile int seasonId;
    static volatile Date beginTime;
    static volatile Date feastEndTime;
    static volatile Date endTime;
    static volatile KfzbFeastInfo feastInfo;
    static final int TOPPLAYERNUM = 32;
    static final int STORENUM = 30;
    static final int BEGINHOUR = 22;
    @Autowired
    IKfzbSeasonInfoDDao kfzbSeasonInfoDDao;
    @Autowired
    IKfzbGameServerLimitDao kfzbGameServerLimitDao;
    Map<Long, KfzbRoomInfo> roomMap;
    Map<Integer, List<KfzbRoomInfo>> roomListMap;
    private static Log feastInfoLog;
    private static Log seasonInfoLog;
    private static ReentrantLock[] dbSaveLock;
    static ReentrantReadWriteLock changeSeasonLock;
    @Autowired
    IKfzbSeasonFeastInfoDao kfzbSeasonFeastInfoDao;
    ApplicationContext context;
    IKfzbFeastService self;
    public final long DAYMILLSECOND = 86400000L;
    LinkedBlockingQueue<KfzbFeastOrganizer> needToDbQueue;
    public static AtomicInteger roomBaseId;
    
    static {
        KfzbFeastService.feastInfo = new KfzbFeastInfo();
        KfzbFeastService.feastInfoLog = LogFactory.getLog("com.xinyun.kfFeast.commInfo");
        KfzbFeastService.seasonInfoLog = LogFactory.getLog("com.xinyun.kfzbSeasonInfo");
        KfzbFeastService.dbSaveLock = new ReentrantLock[33];
        for (int i = 0; i < KfzbFeastService.dbSaveLock.length; ++i) {
            KfzbFeastService.dbSaveLock[i] = new ReentrantLock();
        }
        KfzbFeastService.changeSeasonLock = new ReentrantReadWriteLock();
        KfzbFeastService.roomBaseId = new AtomicInteger(0);
    }
    
    public KfzbFeastService() {
        this.roomMap = new ConcurrentHashMap<Long, KfzbRoomInfo>();
        this.roomListMap = new ConcurrentHashMap<Integer, List<KfzbRoomInfo>>();
        this.self = null;
        this.needToDbQueue = new LinkedBlockingQueue<KfzbFeastOrganizer>();
    }
    
    @Override
    public void processLastSeasonInfo(final KfzbSeasonInfoD lastSeasonInfo) {
        if (lastSeasonInfo == null) {
            return;
        }
        final int getSeasonId = lastSeasonInfo.getSeasonId();
        if (getSeasonId == KfzbFeastService.seasonId) {
            return;
        }
        try {
            KfzbFeastService.changeSeasonLock.writeLock().lock();
            final Date endDate = lastSeasonInfo.getDay3BattleTime();
            final Calendar endC = Calendar.getInstance();
            endC.setTime(endDate);
            final Calendar newBeginTime = Calendar.getInstance();
            newBeginTime.setTime(endDate);
            final boolean isNextDay = true;
            newBeginTime.set(11, 22);
            newBeginTime.set(12, 0);
            newBeginTime.set(13, 0);
            final Calendar newEndTime = Calendar.getInstance();
            newEndTime.setTime(newBeginTime.getTime());
            newEndTime.add(6, 1);
            newEndTime.set(11, 0);
            newEndTime.add(6, 1);
            final Calendar newEndTime2 = Calendar.getInstance();
            newEndTime2.setTime(newBeginTime.getTime());
            newEndTime2.add(6, 1);
            newEndTime2.set(11, 0);
            newEndTime2.add(6, 1);
            KfzbFeastService.beginTime = newBeginTime.getTime();
            KfzbFeastService.feastEndTime = newEndTime.getTime();
            KfzbFeastService.endTime = newEndTime2.getTime();
            KfzbFeastService.seasonId = getSeasonId;
            KfzbFeastService.state = 1;
            KfzbFeastService.seasonInfoLog.info("kfzbFeastNew seasonId=" + KfzbFeastService.seasonId + " beginTime=" + KfzbFeastService.beginTime + " feastEndTime=" + KfzbFeastService.feastEndTime + " endTime=" + KfzbFeastService.endTime);
            this.iniSeasonFeastInfo(getSeasonId);
        }
        finally {
            KfzbFeastService.changeSeasonLock.writeLock().unlock();
        }
        KfzbFeastService.changeSeasonLock.writeLock().unlock();
    }
    
    private void iniSeasonFeastInfo(final int seasonId) {
        KfzbFeastService.feastInfo.getMap().clear();
        KfzbFeastService.feastInfo.setState(0);
        KfzbFeastService.feastInfo.setEndTime(KfzbFeastService.feastEndTime);
        KfzbFeastService.feastInfo.setSeasonId(seasonId);
        this.roomMap.clear();
        this.roomListMap.clear();
        final List<KfzbSeasonFeastInfo> list = this.kfzbSeasonFeastInfoDao.getFeastInfoBySeasonId(seasonId);
        for (final KfzbSeasonFeastInfo fInfo : list) {
            final KfzbFeastOrganizer orgzer = new KfzbFeastOrganizer();
            orgzer.setFeastTimes(fInfo.getFeastNum());
            orgzer.setPos(fInfo.getPos());
            orgzer.setGoldAddFeastTimes(fInfo.getGoldAddFeastNum());
            orgzer.setGoldFeastTime(fInfo.getGoldFeastNum());
            orgzer.setShuNum(fInfo.getShuNum());
            orgzer.setWeiNum(fInfo.getWeiNum());
            orgzer.setWuNum(fInfo.getWuNum());
            KfzbFeastService.feastInfo.getMap().put(orgzer.getPos(), orgzer);
        }
        for (int i = 1; i <= 32; ++i) {
            if (KfzbFeastService.feastInfo.getMap().get(i) == null) {
                final KfzbFeastOrganizer orgzer2 = new KfzbFeastOrganizer();
                orgzer2.setPos(i);
                KfzbFeastService.feastInfo.getMap().put(i, orgzer2);
            }
            this.roomListMap.put(i, new ArrayList<KfzbRoomInfo>());
        }
    }
    
    @Override
    public KfzbFeastInfo getFeastInfo(final GameServerEntity gs) {
        final KfzbSeasonInfoD si = this.kfzbSeasonInfoDDao.getLastSeaonInfo();
        if (si == null) {
            return null;
        }
        if (si.getUseLimit() == 1 && gs != null) {
            final KfzbGameServerLimit limitInfo = this.kfzbGameServerLimitDao.getGameServerByName(gs.getServerKey());
            if (limitInfo == null) {
                return null;
            }
        }
        try {
            KfzbFeastService.changeSeasonLock.readLock().lock();
            final KfzbFeastInfo copFeastInfo = new KfzbFeastInfo();
            BeanUtils.copyProperties(KfzbFeastService.feastInfo, copFeastInfo);
            return copFeastInfo;
        }
        finally {
            KfzbFeastService.changeSeasonLock.readLock().unlock();
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.self = (IKfzbFeastService)this.context.getBean("kfzbFeastService");
        final KfzbFeastThread fThread = new KfzbFeastThread("kfzb-feastThread");
        fThread.start();
    }
    
    public void processFeastInfo() {
        try {
            KfzbFeastService.changeSeasonLock.writeLock().lock();
            if (KfzbFeastService.state == 0) {
                return;
            }
            final Date now = new Date();
            int day = 0;
            if (KfzbFeastService.beginTime.before(now) && KfzbFeastService.feastEndTime.after(now)) {
                if (KfzbFeastService.feastInfo.getState() != 1) {
                    KfzbFeastService.feastInfo.setState(1);
                }
                day = (int)((now.getTime() - KfzbFeastService.beginTime.getTime() + 86400000L) / 86400000L);
                this.setFeastBasicTime(day);
            }
            if (KfzbFeastService.feastEndTime.before(now) && KfzbFeastService.endTime.after(now)) {
                KfzbFeastService.feastInfo.setState(2);
            }
            if (KfzbFeastService.endTime.before(now) && KfzbFeastService.feastInfo.getState() != 3) {
                KfzbFeastService.feastInfo.setState(3);
            }
            KfzbFeastService.feastInfoLog.info("setSeasonState=" + KfzbFeastService.feastInfo.getState());
        }
        finally {
            KfzbFeastService.changeSeasonLock.writeLock().unlock();
        }
        KfzbFeastService.changeSeasonLock.writeLock().unlock();
    }
    
    private void setFeastBasicTime(final int day) {
        final int basicFeastTimes = this.getBasicFeastTimeByDay(day);
        for (final Map.Entry<Integer, KfzbFeastOrganizer> entry : KfzbFeastService.feastInfo.getMap().entrySet()) {
            final KfzbFeastOrganizer kfzbFeastOrganizer = entry.getValue();
        }
    }
    
    private int getBasicFeastTimeByDay(final int day) {
        return 0;
    }
    
    public void selfproceesToDb() {
        for (int i = 0; i < 100; ++i) {
            final KfzbFeastOrganizer forg = this.needToDbQueue.poll();
            if (forg == null) {
                break;
            }
            try {
                final int pos = forg.getPos();
                try {
                    KfzbFeastService.dbSaveLock[pos % 32].lock();
                    this.self.saveFeastOrganizerInfo(forg);
                }
                finally {
                    KfzbFeastService.dbSaveLock[pos % 32].unlock();
                }
                KfzbFeastService.dbSaveLock[pos % 32].unlock();
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    public Map<Integer, Long> addNewFeastParticipate(final KfzbFeastParticipateInfo fpInfo) {
        final Map<Integer, Long> resMap = new HashMap<Integer, Long>();
        if (fpInfo == null) {
            return resMap;
        }
        try {
            KfzbFeastService.changeSeasonLock.writeLock().lock();
            if (KfzbFeastService.feastInfo.getState() != 1) {
                return resMap;
            }
            final Date now = new Date();
            final List<KfzbFeastParticipator> plist = fpInfo.getList();
            for (final KfzbFeastParticipator pf : plist) {
                final int rank = pf.getRank();
                final List<KfzbRoomInfo> rList = this.roomListMap.get(rank);
                KfzbRoomInfo lastRoom = null;
                boolean needCreate = false;
                if (rList.size() == 0) {
                    needCreate = true;
                }
                else {
                    lastRoom = rList.get(rList.size() - 1);
                    if (lastRoom.getRoomState() != 1) {
                        needCreate = true;
                    }
                }
                final KfzbFeastOrganizer forg = KfzbFeastService.feastInfo.getMap().get(rank);
                final int oldGFeastTime = forg.getGoldFeastTime();
                final boolean hasBuff = forg.getGoldAddFeastTimes() > oldGFeastTime;
                if (needCreate) {
                    lastRoom = this.createNewRoom(rank, hasBuff);
                    lastRoom.setCreateDate(new Date());
                    rList.add(lastRoom);
                    this.roomMap.put(lastRoom.getRoomId(), lastRoom);
                }
                boolean isRepeat = false;
                for (final KfzbFeastParticipator oldfp : lastRoom.getList()) {
                    if (oldfp.getPlayerName().equals(pf.getPlayerName()) && oldfp.getServerName() == pf.getServerName() && oldfp.getServerId() == pf.getServerId()) {
                        isRepeat = true;
                        break;
                    }
                }
                if (isRepeat) {
                    KfzbFeastService.feastInfoLog.info("in#roomId=" + lastRoom.getRoomId() + "-" + lastRoom.getRoomRank() + "-" + "#pId=" + pf.getPlayerId() + "#sInfo=" + pf.getServerName() + pf.getServerId() + "#error");
                    resMap.put(pf.getPlayerId(), lastRoom.getRoomId());
                }
                else {
                    lastRoom.getList().add(pf);
                    KfzbFeastService.feastInfoLog.info("in#roomId=" + lastRoom.getRoomId() + "-" + lastRoom.getRoomRank() + "#pId=" + pf.getPlayerId() + "#sInfo=" + pf.getServerName() + pf.getServerId());
                    resMap.put(pf.getPlayerId(), lastRoom.getRoomId());
                    final int oldfgTime = forg.getFeastTimes();
                    forg.setFeastTimes(oldfgTime + 1);
                    if (pf.getNation() == 1) {
                        forg.setWeiNum(forg.getWeiNum() + 1);
                    }
                    else if (pf.getNation() == 2) {
                        forg.setShuNum(forg.getShuNum() + 1);
                    }
                    else if (pf.getNation() == 3) {
                        forg.setWuNum(forg.getWuNum() + 1);
                    }
                    if (hasBuff) {
                        forg.setGoldFeastTime(oldGFeastTime + 1);
                    }
                    if ((oldfgTime + 1) % 200 != 0) {
                        continue;
                    }
                    this.needToDbQueue.add(forg);
                }
            }
        }
        finally {
            KfzbFeastService.changeSeasonLock.writeLock().unlock();
        }
        KfzbFeastService.changeSeasonLock.writeLock().unlock();
        return resMap;
    }
    
    private KfzbRoomInfo createNewRoom(final int rank, final boolean hasBuff) {
        final KfzbRoomInfo room = new KfzbRoomInfo();
        room.setRoomId(KfzbCommonConstants.getRoomIdByRoomAndId(rank, KfzbFeastService.roomBaseId.addAndGet(1)));
        room.setBuff(hasBuff ? 1 : 0);
        return room;
    }
    
    @Override
    public void organizerAddFeast(final KfzbFeastOrganizer forg) {
        try {
            KfzbFeastService.changeSeasonLock.writeLock().lock();
            if (KfzbFeastService.feastInfo.getState() != 1) {
                return;
            }
            final int fPos = forg.getPos();
            final KfzbFeastOrganizer zbforg = KfzbFeastService.feastInfo.getMap().get(fPos);
            zbforg.setGoldAddFeastTimes(forg.getGoldAddFeastTimes());
            KfzbFeastService.feastInfoLog.info("addGoldFeast#rank=" + forg.getPos() + "#num=" + forg.getGoldAddFeastTimes());
            try {
                KfzbFeastService.dbSaveLock[fPos % 32].lock();
                this.self.saveFeastOrganizerInfo(zbforg);
            }
            finally {
                KfzbFeastService.dbSaveLock[fPos % 32].unlock();
            }
            KfzbFeastService.dbSaveLock[fPos % 32].unlock();
        }
        finally {
            KfzbFeastService.changeSeasonLock.writeLock().unlock();
        }
        KfzbFeastService.changeSeasonLock.writeLock().unlock();
    }
    
    @Override
	public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }
    
    @Transactional
    @Override
    public void saveFeastOrganizerInfo(final KfzbFeastOrganizer forg) {
        KfzbSeasonFeastInfo dbforg = this.kfzbSeasonFeastInfoDao.getInfoBySeasonAndPos(KfzbFeastService.seasonId, forg.getPos());
        if (dbforg == null) {
            dbforg = new KfzbSeasonFeastInfo();
            dbforg.setSeasonId(KfzbFeastService.seasonId);
            dbforg.setFeastNum(forg.getFeastTimes());
            dbforg.setPos(forg.getPos());
            dbforg.setGoldFeastNum(forg.getGoldFeastTime());
            dbforg.setGoldAddFeastNum(forg.getGoldAddFeastTimes());
            this.kfzbSeasonFeastInfoDao.create((IModel)dbforg);
            return;
        }
        dbforg.setGoldFeastNum(forg.getGoldFeastTime());
        dbforg.setGoldAddFeastNum(forg.getGoldAddFeastTimes());
        dbforg.setFeastNum(forg.getFeastTimes());
        dbforg.setWeiNum(forg.getWeiNum());
        dbforg.setShuNum(forg.getShuNum());
        dbforg.setWuNum(forg.getWuNum());
        this.kfzbSeasonFeastInfoDao.update((IModel)dbforg);
    }
    
    @Override
    public KfzbRoomInfoList getRoomInfo(final KfzbRoomKeyList roomKeyList) {
        try {
            KfzbFeastService.changeSeasonLock.writeLock().lock();
            if (KfzbFeastService.state == 0) {
                return null;
            }
            final KfzbRoomInfoList resList = new KfzbRoomInfoList();
            for (final Long roomKey : roomKeyList.getList()) {
                final KfzbRoomInfo roomInfo = this.roomMap.get(roomKey);
                if (roomInfo != null) {
                    final List<KfzbFeastParticipator> list = roomInfo.getList();
                    resList.getList().add(roomInfo);
                }
                else {
                    final KfzbRoomInfo newEmptyRom = new KfzbRoomInfo();
                    newEmptyRom.setRoomId(roomKey);
                    resList.getList().add(newEmptyRom);
                }
            }
            return resList;
        }
        finally {
            KfzbFeastService.changeSeasonLock.writeLock().unlock();
        }
    }
    
    public static void main(final String[] args) {
        final KfzbFeastInfo feastInfo1 = new KfzbFeastInfo();
        for (int i = 1; i <= 32; ++i) {
            if (feastInfo1.getMap().get(i) == null) {
                final KfzbFeastOrganizer orgzer = new KfzbFeastOrganizer();
                orgzer.setPos(i);
                orgzer.setFeastTimes(i);
                feastInfo1.getMap().put(i, orgzer);
            }
        }
        for (int t = 0; t < 100000; ++t) {
            final KfzbFeastInfo copFeastInfo = new KfzbFeastInfo();
            BeanUtils.copyProperties(feastInfo1, copFeastInfo);
        }
    }
    
    class KfzbFeastThread extends Thread
    {
        public KfzbFeastThread(final String name) {
            super(name);
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        Thread.sleep(3000L);
                        KfzbFeastService.this.processFeastInfo();
                        KfzbFeastService.this.selfproceesToDb();
                    }
                }
                catch (Exception ex) {
                    continue;
                }
                break;
            }
        }
    }
}
