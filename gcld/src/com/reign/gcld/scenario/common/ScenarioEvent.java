package com.reign.gcld.scenario.common;

import com.reign.gcld.scenario.common.condition.*;
import com.reign.gcld.log.*;
import com.reign.gcld.scenario.common.choice.*;
import com.reign.util.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.scenario.common.action.*;
import com.reign.gcld.scenario.message.*;
import com.reign.gcld.common.web.*;
import com.reign.gcld.juben.common.*;

public class ScenarioEvent implements Cloneable
{
    private int state;
    private String flag1;
    private String flag2;
    private ScenarioCondition trigger;
    private ScenarioAction operation;
    private ScenarioCondition condition;
    private ScenarioAction dealOperation1;
    private ScenarioAction dealOperation2;
    private ScenarioCondition failCondition;
    private ScenarioChansingInfo scenarioChansingInfo;
    private long tickTime;
    private int mainChoice;
    private int curChoice;
    private int playerChoice;
    private int deadLine;
    private static ErrorLogger errorLogger;
    private long triggerTime;
    private SoloEvent soloEvent;
    private ScenarioChoice choice;
    
    static {
        ScenarioEvent.errorLogger = new ErrorLogger();
    }
    
    public ScenarioEvent() {
        this.state = 0;
        this.flag1 = null;
        this.flag2 = null;
        this.trigger = null;
        this.operation = null;
        this.condition = null;
        this.dealOperation1 = null;
        this.dealOperation2 = null;
        this.failCondition = null;
        this.scenarioChansingInfo = null;
        this.tickTime = 0L;
        this.mainChoice = 0;
        this.curChoice = 0;
        this.playerChoice = 0;
        this.deadLine = 0;
        this.triggerTime = 0L;
        this.choice = null;
    }
    
    public int getDeadLine() {
        return this.deadLine;
    }
    
    public void setDeadLine(final int deadLine) {
        this.deadLine = deadLine;
    }
    
    public Tuple<List<Integer>, Integer> getFightNpcList() {
        if (this.mainChoice == 0 || this.playerChoice == 0) {
            return null;
        }
        final String choiceString = (this.playerChoice == 1) ? this.choice.getChoice1() : this.choice.getChoice2();
        if (StringUtils.isBlank(choiceString)) {
            return null;
        }
        final String[] single = choiceString.split(",");
        if (single[0].equalsIgnoreCase("fight")) {
            final List<Integer> npcList = new ArrayList<Integer>();
            npcList.add(Integer.valueOf(single[1]));
            final Tuple<List<Integer>, Integer> tuple = new Tuple();
            tuple.left = npcList;
            tuple.right = Integer.valueOf(single[2]);
            return tuple;
        }
        return null;
    }
    
    public int getPlayerChoice() {
        return this.playerChoice;
    }
    
    public void setPlayerChoice(final int playerChoice) {
        this.playerChoice = playerChoice;
    }
    
    public int getCurChoice() {
        return this.curChoice;
    }
    
    public void setCurChoice(final int curChoice) {
        this.curChoice = curChoice;
    }
    
    public int getMainChoice() {
        return this.mainChoice;
    }
    
    public void setMainChoice(final int mainChoice) {
        this.mainChoice = mainChoice;
    }
    
    public ScenarioAction getDealOperation1() {
        return this.dealOperation1;
    }
    
    public void setDealOperation1(final ScenarioAction dealOperation1) {
        this.dealOperation1 = dealOperation1;
    }
    
    public ScenarioAction getDealOperation2() {
        return this.dealOperation2;
    }
    
    public void setDealOperation2(final ScenarioAction dealOperation2) {
        this.dealOperation2 = dealOperation2;
    }
    
    public ScenarioCondition getFailCondition() {
        return this.failCondition;
    }
    
    public void setFailCondition(final ScenarioCondition failCondition) {
        this.failCondition = failCondition;
    }
    
    public ScenarioChoice getChoice() {
        return this.choice;
    }
    
    public void setChoice(final ScenarioChoice choice) {
        this.choice = choice;
    }
    
    public SoloEvent getSoloEvent() {
        return this.soloEvent;
    }
    
    public void setSoloEvent(final SoloEvent soloEvent) {
        this.soloEvent = soloEvent;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public ScenarioCondition getTrigger() {
        return this.trigger;
    }
    
    public void setTrigger(final ScenarioCondition trigger) {
        this.trigger = trigger;
    }
    
    public ScenarioAction getOperation() {
        return this.operation;
    }
    
    public void setOperation(final ScenarioAction operation) {
        this.operation = operation;
    }
    
    public ScenarioCondition getCondition() {
        return this.condition;
    }
    
    public void setCondition(final ScenarioCondition condition) {
        this.condition = condition;
    }
    
    public long getTriggerTime() {
        return this.triggerTime;
    }
    
    public void setTriggerTime(final long triggerTime) {
        this.triggerTime = triggerTime;
    }
    
    public void setFlag1(final String flag1) {
        this.flag1 = flag1;
    }
    
    public void setFlag2(final String flag2) {
        this.flag2 = flag2;
    }
    
    public long getTickTime() {
        return this.tickTime;
    }
    
    public void setTickTime(final long tickTime) {
        this.tickTime = tickTime;
    }
    
    public ScenarioChansingInfo getScenarioChansingInfo() {
        return this.scenarioChansingInfo;
    }
    
    public void setScenarioChansingInfo(final ScenarioChansingInfo scenarioChansingInfo) {
        this.scenarioChansingInfo = scenarioChansingInfo;
    }
    
    public MultiResult getFlag(final int index) {
        final String flags = (index == 1) ? this.flag1 : this.flag2;
        if (StringUtils.isBlank(flags)) {
            return null;
        }
        final String[] flag = flags.split(",");
        final MultiResult result = new MultiResult();
        result.result1 = Integer.parseInt(flag[1]);
        result.result2 = ((flag.length < 3) ? "" : flag[2]);
        result.result3 = 1;
        if (flag.length >= 4) {
            result.result3 = flag[3];
        }
        return result;
    }
    
    public void work(final IDataGetter getter, final int playerId) {
        if (this.state == 0) {
            return;
        }
        if (this.state >= 1 && this.state < 3) {
            this.scenarioAction(getter, playerId, this.operation, false);
            if (this.state == 2) {
                ScenarioAction action = null;
                if (this.mainChoice == 0) {
                    action = this.dealOperation1;
                }
                else if (this.curChoice == 1) {
                    action = this.dealOperation1;
                }
                else if (this.curChoice == 2) {
                    action = this.dealOperation2;
                }
                this.scenarioAction(getter, playerId, action, true);
            }
        }
        else if (this.state >= 3) {
            return;
        }
    }
    
    private void scenarioAction(final IDataGetter getter, final int playerId, final ScenarioAction operation2, final boolean dealFlag) {
        if (operation2 != null) {
            operation2.work(getter, playerId, this);
        }
        if (dealFlag && (this.curChoice != 0 || this.mainChoice == 0)) {
            ++this.state;
            this.eventChangeToSave(getter, playerId);
            if (this.state == 3) {
                ScenarioEventMessageHelper.sendEventFinishMessage(playerId, this.soloEvent.getId(), this.curChoice);
                this.eventFinished(playerId, getter);
            }
        }
    }
    
    private void eventFinished(final int playerId, final IDataGetter getter) {
        try {
            final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
            if (dto == null) {
                return;
            }
            final int eventId = this.soloEvent.getId();
            if ((eventId >= 6 && eventId <= 8) || (eventId >= 10 && eventId <= 11)) {
                ScenarioEventJsonBuilder.sendEventFinished(playerId, eventId, getter);
            }
            else if (eventId == Constants.EVENT_YINXIONGJIJIE_SHOW_UP) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 3001, null);
            }
            else if (eventId == Constants.EVENT_ID_DONGLINGUAN) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 6001, null);
            }
            else if (eventId == Constants.EVENT_ID_LUOYANGBEI) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 6002, null);
            }
            else if (eventId == Constants.EVENT_ID_SISHUIGUAN) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 6003, null);
            }
            else if (eventId == Constants.EVENT_ID_HUANGSAO_1 || eventId == Constants.EVENT_ID_HUANGSAO_2) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 6006, null);
            }
            else if (eventId == Constants.EVENT_ID_YONGLIXINJUN_63) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 7001, null);
            }
            else if (eventId == Constants.EVENT_ID_YISHIYANYAN_73) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 8004, null);
            }
            else if (eventId == Constants.EVENT_ID_DANGPINGZHANGLU_72) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 8005, null);
            }
            else if (eventId == Constants.EVENT_ID_JIAOMIEZHANGREN_74) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 8006, null);
            }
            else if (eventId == Constants.EVENT_ID_WAHEKUNCHENG_92) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 9003, null);
            }
            else if (eventId == Constants.EVENT_ID_XUNZHAOYUXI1_103) {
                if (dto.grade == 1) {
                    ScenarioEventJsonBuilder.sendDialog(playerId, 10004, null);
                }
            }
            else if (eventId == Constants.EVENT_ID_XUNZHAOYUXI2_104 || eventId == Constants.EVENT_ID_XUNZHAOYUXI3_105 || eventId == Constants.EVENT_ID_XUNZHAOYUXI4_106) {
                if (dto.grade > 1) {
                    ScenarioEventJsonBuilder.sendDialog(playerId, 10005, JubenConstans.forceId2NameMap.get(dto.royalJadeBelong));
                }
            }
            else if (eventId == Constants.EVENT_ID_XUNZHAOSUNCE_167) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10008, null);
            }
            else if (eventId == Constants.EVENT_ID_HUIHESUNCE_168) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10009, null);
            }
            else if (eventId == Constants.EVENT_ID_YUXIOVER_166) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10014, null);
            }
            else if (eventId == Constants.EVENT_ID_TANXIYUEMA_10102) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10001001, null);
            }
            else if (eventId == Constants.EVENT_ID_DIAOCHANDUIHUA_10201) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10002001, null);
            }
            else if (eventId == Constants.EVENT_ID_BAIYIDUJIANG_10602) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10006001, null);
            }
            else if (eventId == Constants.EVENT_ID_DUOQUJINGZHOU_10603) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10006002, null);
            }
            else if (eventId == Constants.EVENT_ID_FANGZHICAIHUO_11001) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10010001, null);
            }
            else if (eventId == Constants.EVENT_ID_LAST_CHUANLINGBING_244) {
                dto.isMengdeSafe = true;
            }
            this.eventOver(playerId, getter, eventId);
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error(this, e);
        }
    }
    
    private void eventOver(final int playerId, final IDataGetter getter, final int eventId) {
        ScenarioEventJsonBuilder.sendEventOver(playerId, eventId, this.state);
        this.checkTrickIsOver(playerId, getter);
        this.checkGeneralAddIsOver(playerId, getter);
        if (eventId == Constants.EVENT_ID_ZHUWENCHOU) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 5002, null);
        }
        else if (eventId == Constants.EVENT_ID_HUOSHAOWUCHAO) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 5003, null);
        }
        if (this.scenarioChansingInfo != null) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            this.scenarioChansingInfo.appendChasingInfo(Long.MAX_VALUE, doc);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_CHASING_INFO, doc.toByte());
        }
    }
    
    private void checkGeneralAddIsOver(final int playerId, final IDataGetter getter) {
        try {
            if (this.operation == null) {
                return;
            }
            if (this.operation instanceof ScenarioActionGeneralAdd) {
                ScenarioEventJsonBuilder.sendGeneralAddInfo(playerId, this.operation, true);
            }
            else if (this.operation instanceof ScenarioActionAnd) {
                final ScenarioAction actionGeneralAdd = ((ScenarioActionAnd)this.operation).getMaxTimeDivisionAddAction();
                if (actionGeneralAdd != null) {
                    ScenarioEventJsonBuilder.sendGeneralAddInfo(playerId, actionGeneralAdd, true);
                }
            }
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error("checkGeneralAddIsOver fail...playerId:" + playerId);
            ScenarioEvent.errorLogger.error(e.getMessage());
            ScenarioEvent.errorLogger.error(this, e);
        }
    }
    
    private void checkTrickIsOver(final int playerId, final IDataGetter getter) {
        try {
            if (this.operation == null) {
                return;
            }
            final Stratagem stratagem = (Stratagem)getter.getStratagemCache().get((Object)Constants.STRATAGEM_HUOGONG_ID);
            final MultiResult result = this.getFlag(1);
            if (stratagem == null || result == null) {
                return;
            }
            if (this.operation instanceof ScenarioActionStratagem) {
                final ScenarioActionStratagem actionStratagem = (ScenarioActionStratagem)this.operation;
                if (actionStratagem != null && actionStratagem.getStratagemId() == Constants.STRATAGEM_HUOGONG_ID && actionStratagem.getCityId() == 0) {
                    ScenarioEventJsonBuilder.sendAllTrickInfo(result.result2, stratagem.getName(), 0L, playerId);
                }
            }
            else if (this.operation instanceof ScenarioActionAnd) {
                final ScenarioAction temp = ((ScenarioActionAnd)this.operation).getStratagemAction();
                if (temp != null) {
                    ScenarioEventJsonBuilder.sendAllTrickInfo(result.result2, stratagem.getName(), 0L, playerId);
                }
            }
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error("checkTrickIsOver fail...playerId:" + playerId);
            ScenarioEvent.errorLogger.error(e.getMessage());
            ScenarioEvent.errorLogger.error(this, e);
        }
    }
    
    public void checkCondition(final IDataGetter dataGetter, final int playerId, final ScenarioEventMessage scenarioEventMessage) {
        if (this.state >= 3) {
            return;
        }
        if (this.state == 1) {
            this.doCheckCondition(dataGetter, playerId, this.condition, scenarioEventMessage);
        }
        else if (this.state == 0) {
            this.doCheckCondition(dataGetter, playerId, this.trigger, scenarioEventMessage);
            if (this.state == 1) {
                this.doCheckCondition(dataGetter, playerId, this.condition, scenarioEventMessage);
            }
        }
    }
    
    private void doCheckCondition(final IDataGetter dataGetter, final int playerId, final ScenarioCondition condition2, final ScenarioEventMessage scenarioEventMessage) {
        boolean ok = true;
        if (condition2 != null) {
            final boolean isOk = condition2.check(dataGetter, playerId, this, scenarioEventMessage);
            if (!isOk) {
                ok = false;
            }
        }
        boolean isToSaveChange = false;
        synchronized (this) {
            if (ok && this.state < 2) {
                isToSaveChange = true;
                ++this.state;
                if (this.state == 1) {
                    this.triggerTime = System.currentTimeMillis();
                    this.eventTriggered(playerId);
                }
                else if (this.state >= 3) {
                    ScenarioEvent.errorLogger.error("doCheckCondition exception...playerId:" + playerId);
                }
            }
        }
        if (isToSaveChange) {
            this.eventChangeToSave(dataGetter, playerId);
        }
    }
    
    private void eventTriggered(final int playerId) {
        try {
            final JuBenDto dto = JuBenManager.getInstance().getByPid(playerId);
            if (dto == null) {
                return;
            }
            final int eventId = this.soloEvent.getId();
            if (eventId == Constants.EVENT_ZHANGHE_SHOW_UP) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 1004, null);
            }
            else if (eventId == Constants.EVENT_HUANGSAO_SHOW_UP) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 1004, null);
            }
            else if (eventId == Constants.EVENT_JIANGGAN_SHOW_UP) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 2001, null);
            }
            else if (eventId == Constants.EVENT_HUARONGDAO_SHOW_UP) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 2003, null);
            }
            else if (eventId == Constants.EVENT_JIEDONGFENG_SHOW_UP) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 2002, null);
            }
            else if (eventId == Constants.EVENT_KUROUJI_SHOW_UP) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 2004, null);
            }
            else if (eventId == Constants.EVENT_ID_DAOFEIZUOLUAN_58) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 6007, null);
            }
            else if (eventId == Constants.EVENT_ID_DIAOCHANQIUJIU_100) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 9001, null);
            }
            else if (eventId == Constants.EVENT_ID_LVBUBAIMENLOU_101) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 9002, null);
            }
            else if (eventId == Constants.EVENT_ID_XIUSHANHANLING_169) {
                if (dto.grade >= 3) {
                    ScenarioEventJsonBuilder.sendDialog(playerId, 10010, null);
                }
            }
            else if (eventId == Constants.EVENT_ID_ZHANGXUNZHUIJI_171 && dto.grade >= 4) {
                ScenarioEventJsonBuilder.sendDialog(playerId, 10013, null);
            }
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error(this, e);
        }
    }
    
    @Override
	public ScenarioEvent clone() throws CloneNotSupportedException {
        final ScenarioEvent scenarioEvent = (ScenarioEvent)super.clone();
        if (this.dealOperation1 != null) {
            scenarioEvent.dealOperation1 = this.dealOperation1.clone();
        }
        if (this.dealOperation2 != null) {
            scenarioEvent.dealOperation2 = this.dealOperation2.clone();
        }
        if (this.condition != null) {
            scenarioEvent.condition = this.condition.clone();
        }
        if (this.operation != null) {
            scenarioEvent.operation = this.operation.clone();
        }
        if (this.trigger != null) {
            scenarioEvent.trigger = this.trigger.clone();
        }
        if (this.choice != null) {
            scenarioEvent.choice = this.choice.clone();
        }
        if (this.failCondition != null) {
            scenarioEvent.failCondition = this.failCondition.clone();
        }
        if (this.scenarioChansingInfo != null) {
            scenarioEvent.scenarioChansingInfo = this.scenarioChansingInfo.clone();
        }
        return scenarioEvent;
    }
    
    public void selfCheck(final IDataGetter dataGetter, final int playerId) {
        if (this.trigger == null) {
            this.triggerTime = System.currentTimeMillis();
            ++this.state;
            if (this.condition == null) {
                ++this.state;
            }
        }
    }
    
    public void selfRestore(final IDataGetter dataGetter, final String eventInfo, final int playerId) {
        try {
            if (StringUtils.isBlank(eventInfo)) {
                return;
            }
            final String[] single = eventInfo.split("#");
            if (single.length <= 0) {
                return;
            }
            final String[] cell = single[0].split(",");
            final int eventId = Integer.parseInt(cell[0]);
            if (eventId != this.soloEvent.getId()) {
                return;
            }
            final int state = Integer.parseInt(cell[1]);
            this.setState(state);
            long triggerTime = 0L;
            if (cell.length >= 3) {
                triggerTime = Long.parseLong(cell[2]);
            }
            final long restoreTime = GcldInitManager.getRestoreTime(triggerTime);
            if (restoreTime > 0L) {
                triggerTime += restoreTime;
            }
            this.triggerTime = triggerTime;
            if (state == 3) {
                return;
            }
            if (state == 2) {
                final int curChoice = StringUtils.isBlank(single[1]) ? 0 : Integer.parseInt(single[1]);
                this.setCurChoice(curChoice);
            }
            else if (state == 1) {
                if (this.condition != null && single.length >= 2) {
                    this.condition.selfRestore(single[1], playerId);
                }
            }
            else if (this.trigger != null && single.length >= 2) {
                this.trigger.selfRestore(single[1], playerId);
            }
            if (single.length > 2 && this.scenarioChansingInfo != null) {
                final String chasingInfo = single[single.length - 1];
                if (!StringUtils.isBlank(chasingInfo)) {
                    this.scenarioChansingInfo.restore(chasingInfo, restoreTime);
                    this.operation.retoreChasing(chasingInfo, restoreTime);
                }
            }
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error("selfRestore fail....playerId:" + playerId + "  eventInfo:" + eventInfo);
            ScenarioEvent.errorLogger.error(e.getMessage());
            ScenarioEvent.errorLogger.error(this, e);
        }
    }
    
    public String selfStore() {
        final StringBuffer sb = new StringBuffer();
        try {
            sb.append(this.soloEvent.getId()).append(",").append(this.state).append(",").append(this.triggerTime);
            if (this.state < 3) {
                if (this.state == 2) {
                    sb.append("#").append(this.choice.getCurChoice());
                }
                else if (this.state == 1) {
                    if (this.condition == null) {
                        return sb.toString();
                    }
                    sb.append("#").append(this.condition.selfStore());
                }
                else if (this.state == 0) {
                    sb.append("#").append(this.trigger.selfStore());
                }
            }
            if (this.scenarioChansingInfo != null) {
                sb.append("#").append(this.scenarioChansingInfo.getSimpleInfoToStore());
            }
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error(e.getMessage());
            ScenarioEvent.errorLogger.error(this, e);
        }
        return sb.toString();
    }
    
    public void eventChangeToSave(final IDataGetter getter, final int playerId) {
        getter.getJuBenService().saveEventInfo(playerId);
    }
    
    public void setActionByIndex(final ScenarioAction action, final int index) {
        try {
            if (index < 0 || index > 2) {
                return;
            }
            if (action == null) {
                return;
            }
            switch (index) {
                case 0: {
                    this.operation = action.clone();
                    break;
                }
                case 1: {
                    this.dealOperation1 = action.clone();
                    break;
                }
                case 2: {
                    this.dealOperation2 = action.clone();
                    break;
                }
            }
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error("setActionByIndex fail....index:" + index + "action.class:");
            ScenarioEvent.errorLogger.error(e.getMessage());
            ScenarioEvent.errorLogger.error(this, e);
        }
    }
    
    public void checkFail(final IDataGetter getter, final Integer playerId) {
        try {
            if (this.failCondition == null) {
                return;
            }
            if (this.state < 1 || this.state >= 3) {
                return;
            }
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto == null) {
                return;
            }
            final boolean isFail = this.failCondition.checkRequest(getter, playerId, this);
            if (isFail) {
                this.state = 4;
                this.eventOver(playerId, getter, this.soloEvent.getId());
                if (this.soloEvent.getId() == Constants.EVENT_ID_TANXIYUEMA_10102) {
                    this.jubenFail(getter, playerId, juBenDto);
                }
                else if (this.soloEvent.getId() == Constants.EVENT_ID_XIANDIQIJIA_10401) {
                    this.jubenFail(getter, playerId, juBenDto);
                    ScenarioEventJsonBuilder.sendDialog(playerId, 10004003, null);
                }
            }
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error(e.getMessage());
            ScenarioEvent.errorLogger.error(this, e);
        }
    }
    
    public void jubenFail(final IDataGetter getter, final Integer playerId, final JuBenDto juBenDto) {
        if (juBenDto == null || juBenDto.state == 0) {
            return;
        }
        if (juBenDto.juBen_id == 10001) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 10001002, null);
        }
        else if (juBenDto.juBen_id == 10004) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 10004002, null);
        }
        else if (juBenDto.juBen_id == 10005) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 10005001, null);
        }
        else if (juBenDto.juBen_id == 11) {
            ScenarioEventJsonBuilder.sendDialog(playerId, 11001, null);
        }
        getter.getJuBenService().juBenOver(playerId, juBenDto, false);
    }
    
    public void tick(final IDataGetter getter, final Integer playerId) {
        if (this.state >= 1 && this.state < 3) {
            final long now = System.currentTimeMillis();
            if (now >= this.tickTime + 1000L) {
                ScenarioEventMessageHelper.sendTimeTickMessage(playerId);
                this.tickTime = now;
            }
        }
    }
    
    public void checkChasingInfo(final IDataGetter getter, final Integer key, final long triggerTime) {
        try {
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(key);
            if (juBenDto == null) {
                return;
            }
            if (key == null || this.state >= 2 || this.scenarioChansingInfo == null || this.state <= 0) {
                return;
            }
            final int totalTime = this.scenarioChansingInfo.getTotalTime();
            final long now = System.currentTimeMillis();
            final int blood = this.scenarioChansingInfo.getJenneyBlood();
            if (now - triggerTime >= totalTime * 1000L || blood <= 0) {
                this.state = 4;
                this.eventOver(key, getter, this.soloEvent.getId());
                ScenarioEventJsonBuilder.sendDialog(key, 6008, null);
                this.jubenFail(getter, key, juBenDto);
            }
            else {
                final int[] cities = this.scenarioChansingInfo.getJennyCities();
                final int jenneyCity = this.scenarioChansingInfo.getJenneyCity();
                final int cityId = cities[jenneyCity];
                final JuBenCityDto cityDto = juBenDto.juBenCityDtoMap.get(cityId);
                if (cityDto != null && cityDto.forceId == 0) {
                    ScenarioEventJsonBuilder.sendDialog(key, 6008, null);
                    this.jubenFail(getter, key, juBenDto);
                    this.state = 4;
                    return;
                }
                if (jenneyCity >= cities.length - 1) {
                    this.state = 2;
                }
            }
        }
        catch (Exception e) {
            ScenarioEvent.errorLogger.error(e.getMessage());
            ScenarioEvent.errorLogger.error(this, e);
        }
    }
}
