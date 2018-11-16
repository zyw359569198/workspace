package com.reign.kfgz.rank;

import java.util.concurrent.*;
import com.reign.kfgz.dto.*;
import java.util.concurrent.locks.*;
import com.reign.framework.json.*;
import com.reign.kf.match.common.*;
import com.reign.kfgz.battle.*;
import java.util.*;

public class GzRankingInfo
{
    int type;
    ConcurrentHashMap<Integer, LinkedList<KfgzRankingDto>> rankingListMap;
    Map<Integer, KfgzRankingDto> rankingMap;
    ReentrantReadWriteLock lock;
    String rankingString;
    int[] ranPos;
    static Random ran;
    
    static {
        GzRankingInfo.ran = new Random();
    }
    
    public String getRankingString() {
        return this.rankingString;
    }
    
    public void setRankingString(final String rankingString) {
        this.rankingString = rankingString;
        final ArrayList<Integer> rankingPosList = new ArrayList<Integer>();
        String[] split;
        for (int length = (split = rankingString.split(",")).length, j = 0; j < length; ++j) {
            final String s = split[j];
            final String s2 = s.split(":")[0];
            rankingPosList.add(Integer.parseInt(s2));
        }
        final int[] ranP = new int[rankingPosList.size() + 1];
        for (int i = 1; i < ranP.length; ++i) {
            ranP[i] = rankingPosList.get(i - 1);
        }
        this.ranPos = ranP;
    }
    
    public GzRankingInfo(final int type) {
        this.rankingListMap = new ConcurrentHashMap<Integer, LinkedList<KfgzRankingDto>>();
        this.rankingMap = new ConcurrentHashMap<Integer, KfgzRankingDto>();
        this.lock = new ReentrantReadWriteLock();
        this.rankingString = null;
        this.ranPos = new int[6];
        this.type = type;
    }
    
    public void AddNewRanking(final int cId, final int forceId, final int addScore) {
        final int[] basePos = this.ranPos;
        int lastPos = basePos[1];
        final int posSize = basePos.length - 1;
        long lastRankScore = 0L;
        int lastRankPos = 0;
        try {
            this.lock.writeLock().lock();
            LinkedList<KfgzRankingDto> rankingList = this.rankingListMap.get(forceId);
            if (rankingList == null) {
                rankingList = new LinkedList<KfgzRankingDto>();
                this.rankingListMap.put(forceId, rankingList);
            }
            KfgzRankingDto rankingDto = this.rankingMap.get(cId);
            int oldRank = -1;
            if (rankingDto != null) {
                oldRank = rankingDto.getRank();
            }
            if (rankingDto == null) {
                rankingDto = new KfgzRankingDto(cId, 0);
            }
            else if (oldRank > 0 && oldRank <= rankingList.size()) {
                rankingList.remove(oldRank - 1);
            }
            rankingDto.setScore(rankingDto.getScore() + addScore);
            if (rankingList.size() == 0) {
                rankingDto.setRank(1);
                rankingList.add(rankingDto);
                rankingDto.setUpNeedScore(0L);
            }
            else {
                int pos = 0;
                int addPos = -1;
                for (final KfgzRankingDto dto : rankingList) {
                    if (addPos == -1 && dto.getScore() >= rankingDto.getScore()) {
                        if (pos == lastPos - 1) {
                            lastRankScore = dto.getScore() + 1L;
                            if (++lastRankPos + 1 > posSize) {
                                lastPos = -1;
                            }
                            else {
                                lastPos = basePos[lastRankPos + 1];
                            }
                        }
                    }
                    else if (addPos == -1 && dto.getScore() < rankingDto.getScore()) {
                        addPos = pos;
                        if (lastRankPos >= 1) {
                            rankingDto.setUpNeedScore(lastRankScore - rankingDto.getScore());
                        }
                        else {
                            rankingDto.setUpNeedScore(0L);
                        }
                        if (pos == lastPos - 1) {
                            lastRankScore = rankingDto.getScore() + 1L;
                            if (++lastRankPos + 1 > posSize) {
                                lastPos = -1;
                            }
                            else {
                                lastPos = basePos[lastRankPos + 1];
                            }
                        }
                        dto.setRank(pos + 2);
                        if (lastRankPos >= 1) {
                            dto.setUpNeedScore(lastRankScore - dto.getScore());
                        }
                        else {
                            dto.setUpNeedScore(0L);
                        }
                        if (pos + 1 == lastPos - 1) {
                            lastRankScore = dto.getScore() + 1L;
                            if (++lastRankPos + 1 > posSize) {
                                lastPos = -1;
                            }
                            else {
                                lastPos = basePos[lastRankPos + 1];
                            }
                        }
                        if (this.type == 1) {
                            this.sendRankingInfoChange(dto);
                        }
                    }
                    else if (addPos > -1) {
                        if (lastRankPos >= 1) {
                            dto.setUpNeedScore(lastRankScore - dto.getScore());
                        }
                        else {
                            dto.setUpNeedScore(0L);
                        }
                        if (pos + 1 == lastPos - 1) {
                            lastRankScore = dto.getScore() + 1L;
                            if (++lastRankPos + 1 > posSize) {
                                lastPos = -1;
                            }
                            else {
                                lastPos = basePos[lastRankPos + 1];
                            }
                        }
                        dto.setRank(pos + 2);
                        if (this.type == 1) {
                            this.sendRankingInfoChange(dto);
                        }
                    }
                    ++pos;
                }
                if (addPos == -1) {
                    rankingDto.setRank(pos + 1);
                    if (lastRankPos >= 1) {
                        rankingDto.setUpNeedScore(lastRankScore - rankingDto.getScore());
                    }
                    else {
                        rankingDto.setUpNeedScore(0L);
                    }
                    rankingList.addLast(rankingDto);
                }
                else {
                    rankingDto.setRank(addPos + 1);
                    rankingList.add(addPos, rankingDto);
                }
            }
            if (this.type == 1) {
                this.sendRankingInfoChange(rankingDto);
            }
            this.rankingMap.put(cId, rankingDto);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        this.lock.writeLock().unlock();
    }
    
    private void sendRankingInfoChange(final KfgzRankingDto dto) {
        if (dto == null) {
            return;
        }
        final byte[] res = getRankingXml(dto);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson(res);
        doc.endObject();
        KfgzMessageSender.sendMsgToOne(dto.getcId(), doc.toByte(), PushCommand.PUSH_KF_KILLARMYRANKING);
    }
    
    public static byte[] getRankingXml(final KfgzRankingDto dto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject("rankInfo");
        if (dto == null || dto.getPos() <= 0) {
            doc.createElement("selfBRank", (-1));
        }
        else {
            doc.createElement("selfBRank", dto.getPos());
            doc.createElement("selfKill", dto.getValue());
            if (dto.getUpNeedScore() > 0L) {
                doc.createElement("changeNeed", dto.getUpNeedScore());
            }
        }
        doc.endObject();
        return doc.toByte();
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public KfgzRankingDto getPlayerRankingInfo(final Integer cId, final int forceId) {
        try {
            this.lock.readLock().lock();
            return this.rankingMap.get(cId);
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    public KfgzRankingDto getPlayerRankingInfoSingle(final int cId, final int forceId) {
        LinkedList<KfgzRankingDto> list = this.rankingListMap.get(forceId);
        if (list == null) {
            this.rankingListMap.putIfAbsent(forceId, new LinkedList<KfgzRankingDto>());
            list = this.rankingListMap.get(forceId);
        }
        final KfgzRankingDto[] dtos = new KfgzRankingDto[2];
        KfgzRankingDto selfRd = null;
        try {
            this.lock.readLock().lock();
            int i = 0;
            for (final KfgzRankingDto rd : list) {
                if (rd.getcId() == cId) {
                    selfRd = rd;
                }
                ++i;
            }
            return selfRd;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    public static void main(final String[] args) {
        final GzRankingInfo rankingInfo = new GzRankingInfo(2);
        for (int i = 1; i <= 50; ++i) {
            final int num = (i - 1) * 20 + 1;
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = num; i < num + 20; ++i) {
                        rankingInfo.AddNewRanking(i, 1, 100);
                    }
                }
            });
            thread.start();
        }
        for (int i = 1; i <= 50; ++i) {
            final Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 1; i < 200; ++i) {
                        rankingInfo.AddNewRanking(GzRankingInfo.ran.nextInt(1000) + 1, 1, 10);
                    }
                }
            });
            thread2.start();
        }
        for (int i = 0; i < 100000; ++i) {}
        try {
            Thread.sleep(10000L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        final LinkedList<KfgzRankingDto> list = rankingInfo.rankingListMap.get(1);
        final Set<Integer> idSet = new HashSet<Integer>();
        int sum = 0;
        for (final KfgzRankingDto dto : list) {
            idSet.add(dto.getcId());
            sum += (int)dto.getScore();
            System.out.println(String.valueOf(dto.getPos()) + "-" + dto.getScore() + "-" + dto.getcId());
        }
        System.out.println(idSet.size());
        System.out.println(sum);
    }
}
