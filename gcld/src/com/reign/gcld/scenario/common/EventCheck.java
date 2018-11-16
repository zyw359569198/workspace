package com.reign.gcld.scenario.common;

import com.reign.gcld.juben.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.log.*;
import java.util.*;

public class EventCheck extends Thread
{
    private static final long SLEEP_TIME = 10L;
    private static final Logger errorLog;
    private static long nextExcuteTime;
    Map<Integer, JuBenDto> map;
    IDataGetter getter;
    
    static {
        errorLog = CommonLog.getLog(EventCheck.class);
        EventCheck.nextExcuteTime = 0L;
    }
    
    public EventCheck(final Map<Integer, JuBenDto> juBenMap, final IDataGetter dataGetter) {
        this.map = null;
        this.getter = null;
        this.map = juBenMap;
        this.getter = dataGetter;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    final boolean hasWork = this.map != null && !this.map.isEmpty();
                    final long now = System.currentTimeMillis();
                    if (hasWork && now >= EventCheck.nextExcuteTime) {
                        EventCheck.nextExcuteTime = now + 10L;
                        for (final Integer key : this.map.keySet()) {
                            final JuBenDto dto = this.map.get(key);
                            if (dto == null) {
                                continue;
                            }
                            final List<ScenarioEvent> events = dto.eventList;
                            if (events == null) {
                                continue;
                            }
                            if (events.isEmpty()) {
                                continue;
                            }
                            this.getter.getJuBenService().checkJadeTimeOver(dto);
                            for (final ScenarioEvent event : events) {
                                try {
                                    final long triggerTime = event.getTriggerTime();
                                    event.checkChasingInfo(this.getter, key, triggerTime);
                                    event.tick(this.getter, key);
                                    event.checkFail(this.getter, key);
                                    event.work(this.getter, key);
                                }
                                catch (Exception e) {
                                    EventCheck.errorLog.error("doWork fail...playerId:" + key, e);
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            catch (Exception e3) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ee) {
                    ee.printStackTrace();
                }
                EventCheck.errorLog.error("EventCheck Thread fail");
                EventCheck.errorLog.error(e3.getMessage());
                EventCheck.errorLog.error(this, e3);
                continue;
            }
            break;
        }
    }
}
