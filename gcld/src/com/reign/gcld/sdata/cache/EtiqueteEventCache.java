package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

@Component("etiqueteEventCache")
public class EtiqueteEventCache extends AbstractCache<Integer, EtiqueteEvent>
{
    @Autowired
    public SDataLoader dataLoader;
    public EtiqueteEvent xiaoQianEtiqueteEvent;
    public EtiqueteEvent lvUpEtiqueteEvent;
    public static final int ID1_LV_UP = 1;
    public EtiqueteEvent techEtiqueteEvent;
    public static final int ID2_TECH = 2;
    public EtiqueteEvent generalEtiqueteEvent;
    public static final int ID3_GENERAL = 3;
    public EtiqueteEvent fbEtiqueteEvent;
    public static final int ID4_FB = 4;
    public EtiqueteEvent reachLimitEtiqueteEvent;
    public static final int ID5_REACH_LIMIT = 5;
    public EtiqueteEvent bonusEtiqueteEvent;
    public static final int ID6_BONUS = 6;
    public EtiqueteEvent openBoxEtiqueteEvent;
    public static final int ID7_OPEN_BOX = 7;
    public EtiqueteEvent attackCityEtiqueteEvent;
    public static final int ID8_ATTACK_CITY = 8;
    public EtiqueteEvent attackMistEtiqueteEvent;
    public static final int ID9_ATTACK_MIST = 9;
    public Map<Integer, EtiqueteEvent> taskMap;
    public static final int ID10_TASK = 10;
    public Map<Integer, EtiqueteEvent> brunchTaskMap;
    public static final int ID11_BRUNCH_TASK = 11;
    public static final String XIAO_QIAN = "xiaoqian";
    public static final String LV_UP = "lvup";
    public static final String TECH = "tech";
    public static final String GENERAL = "general";
    public static final String FB = "fb";
    public static final String TASK = "task";
    public static final String LV_REACH_LIMIT = "lv";
    public static final String BONUS = "bonus";
    public static final String OPEN_BOX = "openbox";
    public static final String ATTACK_CITY = "attackcity";
    public static final String ATTACK_MIST = "attackmist";
    public static final String BRUNCH_TASK = "task_brunch";
    
    public EtiqueteEventCache() {
        this.xiaoQianEtiqueteEvent = null;
        this.lvUpEtiqueteEvent = null;
        this.techEtiqueteEvent = null;
        this.generalEtiqueteEvent = null;
        this.fbEtiqueteEvent = null;
        this.reachLimitEtiqueteEvent = null;
        this.bonusEtiqueteEvent = null;
        this.openBoxEtiqueteEvent = null;
        this.attackCityEtiqueteEvent = null;
        this.attackMistEtiqueteEvent = null;
        this.taskMap = new HashMap<Integer, EtiqueteEvent>();
        this.brunchTaskMap = new HashMap<Integer, EtiqueteEvent>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<EtiqueteEvent> result = this.dataLoader.getModels((Class)EtiqueteEvent.class);
        for (final EtiqueteEvent temp : result) {
            final String[] conditionStrings = temp.getEvent().split(",");
            if ("xiaoqian".equalsIgnoreCase(conditionStrings[0])) {
                this.xiaoQianEtiqueteEvent = temp;
            }
            else if ("lvup".equalsIgnoreCase(conditionStrings[0])) {
                this.lvUpEtiqueteEvent = temp;
            }
            else if ("tech".equalsIgnoreCase(conditionStrings[0])) {
                this.techEtiqueteEvent = temp;
            }
            else if ("general".equalsIgnoreCase(conditionStrings[0])) {
                this.generalEtiqueteEvent = temp;
            }
            else if ("fb".equalsIgnoreCase(conditionStrings[0])) {
                this.fbEtiqueteEvent = temp;
            }
            else if ("task".equalsIgnoreCase(conditionStrings[0])) {
                final int taskId = Integer.parseInt(conditionStrings[1]);
                this.taskMap.put(taskId, temp);
            }
            else if ("lv".equalsIgnoreCase(conditionStrings[0])) {
                this.reachLimitEtiqueteEvent = temp;
            }
            else if ("bonus".equalsIgnoreCase(conditionStrings[0])) {
                this.bonusEtiqueteEvent = temp;
            }
            else if ("openbox".equalsIgnoreCase(conditionStrings[0])) {
                this.openBoxEtiqueteEvent = temp;
            }
            else if ("attackcity".equalsIgnoreCase(conditionStrings[0])) {
                this.attackCityEtiqueteEvent = temp;
            }
            else if ("attackmist".equalsIgnoreCase(conditionStrings[0])) {
                this.attackMistEtiqueteEvent = temp;
            }
            else if ("task_brunch".equalsIgnoreCase(conditionStrings[0])) {
                final int brunchTaskId = Integer.parseInt(conditionStrings[1]);
                this.brunchTaskMap.put(brunchTaskId, temp);
            }
            final BattleDrop sendRewardDrop = BattleDropFactory.getInstance().getBattleDrop(temp.getSendReward());
            temp.setSendRewardDrop(sendRewardDrop);
            final BattleDrop replyRewardDrop = BattleDropFactory.getInstance().getBattleDrop(temp.getReplyReward());
            temp.setReplyRewardDrop(replyRewardDrop);
            temp.setWordsArray(temp.getWords().split(";"));
            temp.setReplyArray(temp.getReply().split(";"));
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
    
    public EtiqueteEvent getEtiqueteEvent(final int eventID, final int taskId) {
        try {
            EtiqueteEvent etiqueteEvent = null;
            switch (eventID) {
                case 1: {
                    return null;
                }
                case 2: {
                    return this.techEtiqueteEvent;
                }
                case 3: {
                    return this.generalEtiqueteEvent;
                }
                case 4: {
                    return this.fbEtiqueteEvent;
                }
                case 5: {
                    return this.reachLimitEtiqueteEvent;
                }
                case 6: {
                    return this.bonusEtiqueteEvent;
                }
                case 7: {
                    return this.openBoxEtiqueteEvent;
                }
                case 8: {
                    return this.attackCityEtiqueteEvent;
                }
                case 9: {
                    return this.attackMistEtiqueteEvent;
                }
                case 10: {
                    etiqueteEvent = this.taskMap.get(taskId);
                    return etiqueteEvent;
                }
                case 11: {
                    etiqueteEvent = this.brunchTaskMap.get(taskId);
                    return etiqueteEvent;
                }
                default: {
                    ErrorSceneLog.getInstance().appendErrorMsg("eventID is invalid").append("eventID", eventID).append("taskId", taskId).appendClassName("EtiqueteEventCache").appendMethodName("getEtiqueteEvent").flush();
                    return null;
                }
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("EtiqueteEventCache.getEtiqueteEvent catch Exception", e);
            return null;
        }
    }
    
    @Override
	public void clear() {
        this.lvUpEtiqueteEvent = null;
        this.techEtiqueteEvent = null;
        this.generalEtiqueteEvent = null;
        this.fbEtiqueteEvent = null;
        this.taskMap.clear();
        this.brunchTaskMap.clear();
        this.reachLimitEtiqueteEvent = null;
        super.clear();
    }
}
