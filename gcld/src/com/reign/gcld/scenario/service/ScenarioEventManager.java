package com.reign.gcld.scenario.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.juben.common.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.scenario.common.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.action.*;

@Component("scenarioEventManager")
public class ScenarioEventManager implements InitializingBean
{
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private SoloEventCache soloEventCache;
    @Autowired
    private SoloCityCache soloCityCache;
    EventCheck eventCheck;
    
    public ScenarioEventManager() {
        this.eventCheck = null;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        (this.eventCheck = new EventCheck(JuBenManager.juBenMap, this.dataGetter)).start();
    }
    
    public void handleMessage(final ScenarioEventMessage scenarioEventMessage) {
        final int playerId = scenarioEventMessage.getPlayerId();
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return;
        }
        final List<ScenarioEvent> events = dto.eventList;
        if (events == null || events.isEmpty()) {
            return;
        }
        for (final ScenarioEvent event : events) {
            event.checkCondition(this.dataGetter, playerId, scenarioEventMessage);
        }
    }
    
    public List<ScenarioEvent> addScenarioToPlayer(final int playerId, final String scenarioIds, final String eventStoreInfo) throws CloneNotSupportedException {
        if (StringUtils.isBlank(scenarioIds)) {
            return null;
        }
        final String[] scenarios = scenarioIds.split(";");
        String[] infos = null;
        if (!StringUtils.isBlank(eventStoreInfo)) {
            infos = eventStoreInfo.split("\\+");
        }
        ArrayList<ScenarioEvent> list = null;
        for (int i = 0; i < scenarios.length; ++i) {
            final int scenarioId = Integer.parseInt(scenarios[i]);
            final ScenarioEvent event = this.soloEventCache.getEventMap().get(scenarioId);
            if (event != null) {
                final ScenarioEvent eventCopy = event.clone();
                if (eventCopy != null) {
                    if (infos != null && infos.length > i) {
                        eventCopy.selfRestore(this.dataGetter, infos[i], playerId);
                    }
                    else {
                        eventCopy.selfCheck(this.dataGetter, playerId);
                    }
                    if (list == null) {
                        list = new ArrayList<ScenarioEvent>();
                        list.add(eventCopy);
                    }
                    else {
                        list.add(eventCopy);
                    }
                }
            }
        }
        return list;
    }
    
    public void getPlayerScenarioEventInfo(final int playerId, final JsonDocument doc) {
        final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
        if (dto == null) {
            return;
        }
        final List<ScenarioEvent> list = dto.eventList;
        if (list == null) {
            return;
        }
        doc.startArray("event");
        final long now = System.currentTimeMillis();
        for (final ScenarioEvent event : list) {
            if (event.getState() <= 0) {
                continue;
            }
            doc.startObject();
            doc.createElement("eventId", event.getSoloEvent().getId());
            doc.createElement("eventName", event.getSoloEvent().getName());
            doc.createElement("eventState", event.getState());
            boolean showTitle = false;
            if ((event.getTrigger() == null || event.getState() >= 1) && event.getState() < 3) {
                final MultiResult flag = event.getFlag(1);
                if (flag != null) {
                    doc.createElement("cityId1", flag.result1);
                    doc.createElement("pic1", flag.result2);
                    doc.createElement("isGeneral", flag.result3);
                }
                showTitle = true;
            }
            if ((event.getCondition() == null || event.getState() >= 2) && event.getState() < 3) {
                final MultiResult flag = event.getFlag(2);
                if (flag != null) {
                    doc.createElement("cityId2", flag.result1);
                    doc.createElement("pic2", flag.result2);
                    doc.createElement("isGeneral", flag.result3);
                }
            }
            if (showTitle) {
                final String title = event.getSoloEvent().getTitle();
                if (!StringUtils.isBlank(title)) {
                    doc.createElement("title", title);
                }
            }
            this.appendTroopAddMessage(event, doc, now);
            if (event.getState() >= 1 && event.getState() < 3) {
                final ScenarioChansingInfo chansingInfo = event.getScenarioChansingInfo();
                if (chansingInfo != null) {
                    final long triggerTime = event.getTriggerTime();
                    final long lastTime = System.currentTimeMillis() - triggerTime;
                    doc.startObject("chasingInfo");
                    chansingInfo.appendChasingInfo(lastTime, doc);
                    doc.endObject();
                }
                if (event != null && event.getDeadLine() >= 0) {
                    final long deadLine = event.getDeadLine() * 1000L - now + event.getTriggerTime();
                    if (deadLine > 0L) {
                        doc.createElement("deadLine", deadLine);
                        doc.createElement("name", event.getSoloEvent().getIntro());
                    }
                }
            }
            this.appendRepeatMarchingInfo(event, doc, now);
            doc.endObject();
        }
        doc.endArray();
        this.appendRoyalJadeInfo(dto, doc, now);
    }
    
    private void appendRoyalJadeInfo(final JuBenDto dto, final JsonDocument doc, final long now) {
        try {
            if (dto == null) {
                return;
            }
            final int soloId = dto.juBen_id;
            if (soloId != 10) {
                return;
            }
            final List<Integer> jubenList = this.soloCityCache.getForceIdList(dto.juBen_id);
            if (jubenList == null || jubenList.isEmpty()) {
                return;
            }
            doc.startArray("forceList");
            for (final Integer forceId : jubenList) {
                if (forceId == 1) {
                    continue;
                }
                doc.startObject();
                doc.createElement("forceId", forceId);
                doc.createElement("hasRoyalJade", dto.royalJadeBelong == forceId);
                if (dto.royalJadeBelong != -1) {
                    doc.createElement("leftTime", dto.royalEndTime - now);
                }
                doc.endObject();
            }
            doc.endArray();
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    private void appendRepeatMarchingInfo(final ScenarioEvent event, final JsonDocument doc, final long now) {
        try {
            if (event == null) {
                return;
            }
            final int state = event.getState();
            if (state >= 1 && state < 3) {
                final ScenarioAction action = event.getOperation();
                if (action == null) {
                    return;
                }
                if (action.hasMarching) {
                    doc.startArray("marchInfo");
                    if (action instanceof ScenarioActionAnd) {
                        final ArrayList<ScenarioAction> actions = ((ScenarioActionAnd)action).actions;
                        if (actions == null || actions.isEmpty()) {
                            return;
                        }
                        for (final ScenarioAction single : actions) {
                            if (single instanceof ScenarioActionRepeatAction) {
                                final long triggerTime = event.getTriggerTime();
                                final long lastTime = now - triggerTime;
                                final int serial = (int)(lastTime / single.repeatCirce);
                                doc.startObject();
                                final int serialExpect = serial + 1;
                                final int serialReal = actions.size();
                                doc.createElement("serial", Math.max(serialExpect, serialReal));
                                final long totalTime = ((ScenarioActionRepeatAction)single).getTotalTime();
                                doc.createElement("nextExcutedTime", single.repeatCirce - lastTime + totalTime);
                                doc.createElement("marchState", 0);
                                doc.createElement("curCityId", ((ScenarioActionRepeatAction)single).getStartCityId());
                                doc.createElement("nextCityId", ((ScenarioActionRepeatAction)single).getNextCityId());
                                doc.endObject();
                            }
                            else {
                                if (!(single instanceof ScenarioActionMarching)) {
                                    continue;
                                }
                                final ScenarioActionMarching marching = (ScenarioActionMarching)single;
                                if (marching.isDead()) {
                                    continue;
                                }
                                doc.startObject();
                                marching.appendMarchingInfo(doc);
                                doc.endObject();
                            }
                        }
                    }
                    else if (action instanceof ScenarioActionMarching) {
                        final ScenarioActionMarching marching2 = (ScenarioActionMarching)action;
                        if (!marching2.isDead()) {
                            doc.startObject();
                            marching2.appendMarchingInfo(doc);
                            doc.endObject();
                        }
                    }
                    doc.endArray();
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
        }
    }
    
    private void appendTroopAddMessage(final ScenarioEvent event, final JsonDocument doc, final long now) {
        try {
            ScenarioAction tempAction = null;
            if (event.getOperation() != null) {
                final ScenarioAction action = event.getOperation();
                if (action instanceof ScenarioActionGeneralAdd && ((ScenarioActionGeneralAdd)action).getCityId() != 0) {
                    final long time = action.excuteTime + action.repeatCirce - System.currentTimeMillis();
                    if (time > 0L) {
                        doc.createElement("addCity", ((ScenarioActionGeneralAdd)action).getCityId());
                        doc.createElement("nextTime", time);
                    }
                }
                else if (action instanceof ScenarioActionAnd) {
                    tempAction = ((ScenarioActionAnd)action).getMaxTimeDivisionAddAction();
                    if (tempAction != null) {
                        final long time = tempAction.excuteTime + tempAction.repeatCirce - now;
                        if (time > 0L) {
                            doc.createElement("addCity", ((ScenarioActionGeneralAdd)tempAction).getCityId());
                            doc.createElement("nextTime", time);
                        }
                    }
                    tempAction = ((ScenarioActionAnd)action).getStratagemAction();
                    if (tempAction != null) {
                        final long time = tempAction.excuteTime + tempAction.repeatCirce - now;
                        final Stratagem stratagem = (Stratagem)this.dataGetter.getStratagemCache().get((Object)Constants.STRATAGEM_HUOGONG_ID);
                        if (stratagem != null && time > 0L) {
                            doc.createElement("nextTrickTime", time);
                            doc.createElement("stratagemName", stratagem.getName());
                            doc.createElement("pic", "zhanghe");
                        }
                    }
                }
                else if (action instanceof ScenarioActionStratagem && ((ScenarioActionStratagem)action).getCityId() == 0) {
                    final ScenarioActionStratagem actionStratagem = (ScenarioActionStratagem)action;
                    final Stratagem stratagem2 = (Stratagem)this.dataGetter.getStratagemCache().get((Object)actionStratagem.getStratagemId());
                    if (stratagem2 != null && stratagem2.getType().equalsIgnoreCase("huogong")) {
                        final long time2 = action.excuteTime + action.repeatCirce - System.currentTimeMillis();
                        if (time2 > 0L) {
                            doc.createElement("nextTrickTime", time2);
                            doc.createElement("stratagemName", stratagem2.getName());
                            doc.createElement("pic", "zhanghe");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("appendTroppAddMessage fail...");
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
        }
    }
    
    public static int getEventState(final Integer eventIdZhuwenchou, final List<ScenarioEvent> eventList) {
        if (eventList == null || eventList.isEmpty()) {
            return -1;
        }
        for (final ScenarioEvent event : eventList) {
            if (event == null) {
                continue;
            }
            if (event.getSoloEvent().getId() == eventIdZhuwenchou) {
                return event.getState();
            }
        }
        return -1;
    }
}
