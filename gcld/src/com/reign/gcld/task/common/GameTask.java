package com.reign.gcld.task.common;

import com.reign.gcld.task.request.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.task.message.*;
import java.util.*;
import com.reign.gcld.task.reward.*;

public class GameTask
{
    private int id;
    private String name;
    private String introl;
    private String intros;
    private ITaskRequest taskRequest;
    private ITaskReward taskReward;
    private int nextTaskId;
    private int seq;
    private GameTask prevTask;
    private GameTask nextTask;
    private int areaId;
    private String markTrace;
    private String iosMarktrace;
    private String target;
    private String reward;
    private String pic;
    private int type;
    private int group;
    private int index;
    private String newTrace;
    private String plot;
    private int telephone;
    public static final int TELEPHONE_0 = 0;
    public static final int TELEPHONE_1 = 1;
    
    public GameTask(final int id, final String name, final String intros, final String introl, final ITaskRequest taskRequest, final ITaskReward taskReward, final int nextTaskId, final int areaId, final String markTrace, final int type, final String target, final String reward, final String pic, final String newTrace, final String plot, final int telephone, final String iosMarktrace) {
        this.id = id;
        this.name = name;
        this.intros = intros;
        this.introl = introl;
        (this.taskRequest = taskRequest).setTask(this);
        this.taskReward = taskReward;
        this.nextTaskId = nextTaskId;
        this.areaId = areaId;
        this.markTrace = markTrace;
        this.setIosMarktrace(iosMarktrace);
        this.type = type;
        this.group = 0;
        this.index = 0;
        this.target = target;
        this.reward = reward;
        this.pic = pic;
        this.newTrace = newTrace;
        this.plot = plot;
        this.setTelephone(telephone);
    }
    
    public GameTask(final int group, final int index, final int type, final String name, final String intros, final String introl, final ITaskRequest taskRequest, final ITaskReward taskReward, final String markTrace, final String pic, final int telephone, final String iosMarktrace) {
        this.id = 0;
        this.group = group;
        this.index = index;
        this.type = type;
        this.name = name;
        this.intros = intros;
        this.introl = introl;
        (this.taskRequest = taskRequest).setTask(this);
        this.taskReward = taskReward;
        this.markTrace = markTrace;
        this.setIosMarktrace(iosMarktrace);
        this.pic = pic;
        this.newTrace = "";
        this.plot = "";
        this.setTelephone(telephone);
    }
    
    public byte[] getTaskJsonDescWithoutProcess(final int playerId, final IDataGetter taskDataGetter) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("state", 1);
        doc.createElement("taskId", this.id);
        doc.createElement("taskName", this.name);
        doc.createElement("introShort", this.intros);
        doc.endObject();
        return doc.toByte();
    }
    
    public byte[] getTaskRewardJsonDesc(final PlayerDto playerDto, final IDataGetter taskDataGetter) {
        final Map<Integer, Reward> rewardMap = this.taskReward.getReward(playerDto, taskDataGetter, null);
        return TaskBuilderJson.sendJsonTaskReward(rewardMap, taskDataGetter);
    }
    
    public byte[] getTaskRewardJsonDesc(final PlayerDto playerDto, final IDataGetter taskDataGetter, final Map<Integer, Reward> rewardMap) {
        return TaskBuilderJson.sendJsonTaskReward(rewardMap, taskDataGetter);
    }
    
    public Map<Integer, Reward> getTaskRewarList(final PlayerDto playerDto, final IDataGetter taskDataGetter) {
        return this.taskReward.getReward(playerDto, taskDataGetter, null);
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ITaskRequest getTaskRequest() {
        return this.taskRequest;
    }
    
    public void setTaskRequest(final ITaskRequest taskRequest) {
        this.taskRequest = taskRequest;
    }
    
    public ITaskReward getTaskReward() {
        return this.taskReward;
    }
    
    public void setTaskReward(final ITaskReward taskReward) {
        this.taskReward = taskReward;
    }
    
    public int getNextTaskId() {
        return this.nextTaskId;
    }
    
    public void setNextTaskId(final int nextTaskId) {
        this.nextTaskId = nextTaskId;
    }
    
    public int getSeq() {
        return this.seq;
    }
    
    public void setSeq(final int seq) {
        this.seq = seq;
    }
    
    public GameTask getPrevTask() {
        return this.prevTask;
    }
    
    public void setPrevTask(final GameTask prevTask) {
        this.prevTask = prevTask;
    }
    
    public GameTask getNextTask() {
        return this.nextTask;
    }
    
    public void setNextTask(final GameTask nextTask) {
        this.nextTask = nextTask;
    }
    
    public String getIntrol() {
        return this.introl;
    }
    
    public void setIntrol(final String introl) {
        this.introl = introl;
    }
    
    public String getIntros() {
        return this.intros;
    }
    
    public void setIntros(final String intros) {
        this.intros = intros;
    }
    
    public int getAreaId() {
        return this.areaId;
    }
    
    public void setAreaId(final int areaId) {
        this.areaId = areaId;
    }
    
    public String getMarkTrace() {
        return this.markTrace;
    }
    
    public void setMarkTrace(final String markTrace) {
        this.markTrace = markTrace;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getGroup() {
        return this.group;
    }
    
    public void setGroup(final int group) {
        this.group = group;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public String getTarget() {
        return this.target;
    }
    
    public void setTarget(final String target) {
        this.target = target;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public String getNewTrace() {
        return this.newTrace;
    }
    
    public void setNewTrace(final String newTrace) {
        this.newTrace = newTrace;
    }
    
    public String getPlot() {
        return this.plot;
    }
    
    public void setPlot(final String plot) {
        this.plot = plot;
    }
    
    public int getTelephone() {
        return this.telephone;
    }
    
    public void setTelephone(final int telephone) {
        this.telephone = telephone;
    }
    
    public String getIosMarktrace() {
        return this.iosMarktrace;
    }
    
    public void setIosMarktrace(final String iosMarktrace) {
        this.iosMarktrace = iosMarktrace;
    }
}
