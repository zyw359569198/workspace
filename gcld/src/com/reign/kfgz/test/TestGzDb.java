package com.reign.kfgz.test;

import org.springframework.context.support.*;
import com.reign.kfgz.service.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.constants.*;
import java.util.*;

public class TestGzDb
{
    static long time;
    
    static {
        TestGzDb.time = 0L;
    }
    
    public static void main(final String[] args) {
        System.out.println("|");
        System.exit(0);
    }
    
    public static void send() {
    }
    
    public static void add(final ClassPathXmlApplicationContext cpxac) {
        final IKfgzGatewayService ser = (IKfgzGatewayService)cpxac.getBean("kfgzGatewayService");
        final KfgzBattleResultInfo bInfo = new KfgzBattleResultInfo();
        final List<KfgzPlayerRankingInfoReq> pList = bInfo.getPlayerRes();
        final int seasonId = 10;
        final int layer_id = 1;
        final int g_id = 1;
        final int round = 3;
        final Random ran = new Random();
        final String[] gss = { "gcld_s1", "gcld_s2", "gcld_s3" };
        for (int i = 0; i < 3000; ++i) {
            final String gameServer = gss[ran.nextInt(gss.length)];
            final int killArmy = ran.nextInt(10000);
            final int solo = ran.nextInt(100);
            final int occupyCity = ran.nextInt(3);
            final KfgzPlayerRankingInfoReq req = new KfgzPlayerRankingInfoReq();
            req.setcId(i + 1000);
            req.setGameServer(gameServer);
            req.setSeasonId(seasonId);
            req.setgId(g_id);
            req.setLayerId(layer_id);
            req.setGzId(KfgzCommConstants.getGzId(layer_id, g_id, 0, round));
            req.setKillArmy(killArmy);
            req.setNation(ran.nextInt(3) + 1);
            req.setRound(round);
            req.setOccupyCity(occupyCity);
            req.setSoloNum(solo);
            pList.add(req);
        }
        printTime(1);
        ser.handleBattleResInfo(bInfo);
        printTime(100);
        System.out.println("end");
    }
    
    public static void printTime(final int t1) {
        final long newTime = System.currentTimeMillis();
        System.out.println(String.valueOf(t1) + "--" + (newTime - TestGzDb.time));
        TestGzDb.time = newTime;
    }
}
