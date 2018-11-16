package com.reign.gcld.battle.service;

import com.reign.gcld.rank.domain.*;
import com.reign.gcld.sdata.domain.*;
import java.util.*;
import com.reign.gcld.battle.scene.*;

public interface ITimerBattleService
{
    void addCountryNpc();
    
    void resetVip3PhantomCountEachDay();
    
    void BarbarainInvade();
    
    void BarbarainInvadeRecover();
    
    void worldExpeditionAddEachHour();
    
    void worldDoExpeditionEachMinute();
    
    void manZuExpeditionAddAfterFaDong(final ForceInfo p0, final WorldPaidB p1, final int p2);
    
    void worldDoManZuExpeditionEachMinute();
    
    void worldManZuExpeditionBobaoEach30Minutes();
    
    long getLastCountryEAsAddTime();
    
    void addCityEventEachMinute();
    
    boolean addNationTaskExpeditionArmy(final int p0);
    
    void recoverNationTaskExpeditionArmy();
    
    void addBarbarainFoodArmyAfterBesiegedTry(final int p0);
    
    void addTicketArmyDuringNationTask(final String p0);
    
    void triggerAddTicketArmyTask();
    
    void addRoundSaoDangManZu(final int p0, final int p1);
    
    Integer getLastManZuLeftPercent(final int p0, final int p1);
    
    void deleteAllTicketArmyAfterNationTaskEnded();
    
    void addRoundManZuForBianJiang(final int p0, final int p1, final Set<Integer> p2);
    
    void fireASinglePK(final String p0);
    
    void fireJiTuanJunForBianJiang(final Battle p0, final int p1, final int p2);
    
    void addManZuBeforeManWangLing(final int p0, final int p1);
    
    void removeManZuBeforeNationTask(final int p0, final Set<Integer> p1);
    
    void addMoonCakeArmyForThreeCountry(final String p0);
    
    void checkActivityPer3Seconds();
}
