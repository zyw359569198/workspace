package com.reign.gcld.task.message;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.task.common.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.task.reward.*;
import java.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.util.*;

public class TaskBuilderJson
{
    private static List<Integer> rewardResourceTypes;
    private static List<Integer> quizs;
    private static int[] quiz;
    
    static {
        TaskBuilderJson.rewardResourceTypes = new ArrayList<Integer>();
        TaskBuilderJson.quizs = new ArrayList<Integer>();
        TaskBuilderJson.quiz = new int[] { 1, 3, 7, 15 };
        TaskBuilderJson.rewardResourceTypes.add(1);
        TaskBuilderJson.rewardResourceTypes.add(2);
        TaskBuilderJson.rewardResourceTypes.add(3);
        TaskBuilderJson.rewardResourceTypes.add(4);
        TaskBuilderJson.quizs.add(90);
        TaskBuilderJson.quizs.add(91);
        TaskBuilderJson.quizs.add(10002);
        TaskBuilderJson.quizs.add(10003);
    }
    
    public static byte[] sendJsonTaskInfo(final List<TaskChangeContent> taskChangeContentList, final PlayerDto playerDto, final IDataGetter taskDataGetter, final int playerId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("tasks");
        for (final TaskChangeContent taskChangeContent : taskChangeContentList) {
            doc.startObject();
            doc.createElement("type", taskChangeContent.getType());
            doc.createElement("state", taskChangeContent.isRequestCompleted() ? 2 : 1);
            doc.createElement("taskId", taskChangeContent.getTaskId());
            doc.createElement("group", taskChangeContent.getGroup());
            doc.createElement("index", taskChangeContent.getIndex());
            doc.createElement("taskName", taskChangeContent.getTaskName());
            doc.createElement("introShort", taskChangeContent.getIntros());
            doc.createElement("introLong", taskChangeContent.getIntrol());
            doc.createElement("processStr", taskChangeContent.getProcessStr());
            doc.createElement("requestCompleted", taskChangeContent.isRequestCompleted());
            doc.createElement("markTrace", taskChangeContent.getMarkTrace());
            doc.createElement("iosMarktrace", taskChangeContent.getIosMarkTrace());
            doc.createElement("newTrace", taskChangeContent.getNewTrace());
            doc.createElement("areaId", taskChangeContent.getArea());
            doc.createElement("pic", taskChangeContent.getPic());
            doc.createElement("plot", taskChangeContent.getPlot());
            final int taskId = taskChangeContent.getTaskId();
            if (TaskBuilderJson.quizs.contains(taskId)) {
                final int quizId = TaskBuilderJson.quiz[TaskBuilderJson.quizs.indexOf(taskId)];
                PlayerWorld pw = taskDataGetter.getPlayerWorldDao().read(playerId);
                if (pw == null) {
                    taskDataGetter.getWorldService().createRecord(playerId);
                    pw = taskDataGetter.getPlayerWorldDao().read(playerId);
                }
                final int quizInfo = (pw.getQuizinfo() == null) ? 0 : pw.getQuizinfo();
                if (quizId > quizInfo) {
                    doc.createElement("quizInfo", quizId);
                }
            }
            if (taskChangeContent.isRequestCompleted()) {
                ITaskReward taskReward = null;
                if (1 == taskChangeContent.getType()) {
                    taskReward = TaskFactory.getInstance().getTask(taskChangeContent.getTaskId()).getTaskReward();
                }
                else {
                    taskReward = TaskFactory.getInstance().getTask(taskChangeContent.getGroup(), taskChangeContent.getIndex(), taskChangeContent.getType()).getTaskReward();
                }
                final Map<Integer, Reward> rewardMap = taskReward.getReward(playerDto, taskDataGetter, null);
                doc.startArray("resource");
                for (final int rewardType : TaskBuilderJson.rewardResourceTypes) {
                    final Reward tempReward = rewardMap.get(rewardType);
                    if (tempReward != null) {
                        doc.startObject();
                        doc.createElement("type", tempReward.getType());
                        doc.createElement("value", tempReward.getNum());
                        doc.endObject();
                    }
                }
                doc.endArray();
                final Reward chiefReward = rewardMap.get(5);
                if (chiefReward != null) {
                    doc.createElement("chiefExp", chiefReward.getNum());
                }
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] sendJsonTaskReward(final Map<Integer, Reward> rewardMap, final IDataGetter taskDataGetter) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("tasks");
        doc.startObject();
        doc.startArray("resource");
        for (final int rewardType : TaskBuilderJson.rewardResourceTypes) {
            final Reward tempReward = rewardMap.get(rewardType);
            if (tempReward != null) {
                doc.startObject();
                doc.createElement("type", tempReward.getType());
                doc.createElement("value", tempReward.getNum());
                doc.endObject();
            }
        }
        doc.endArray();
        final Reward chiefReward = rewardMap.get(5);
        if (chiefReward != null) {
            doc.createElement("chiefExp", chiefReward.getNum());
        }
        final Reward newBuildingReward = rewardMap.get(6);
        if (newBuildingReward != null) {
            doc.createElement("newBuilding", ((Building)taskDataGetter.getBuildingCache().get((Object)newBuildingReward.getNum())).getName());
        }
        final Reward newConstructionReward = rewardMap.get(9);
        if (newConstructionReward != null) {
            doc.createElement("newConstruction", newConstructionReward.getName());
        }
        doc.endObject();
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] sendJsonTaskInfo(final List<Tuple<TaskChangeContent, Map<Integer, Reward>>> list, final IDataGetter taskDataGetter, final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("tasks");
        String newGuidId = null;
        for (final Tuple<TaskChangeContent, Map<Integer, Reward>> tuple : list) {
            doc.startObject();
            final TaskChangeContent taskChangeContent = tuple.left;
            final Map<Integer, Reward> rewardMap = tuple.right;
            if (taskChangeContent.getType() == 1) {
                newGuidId = taskChangeContent.getNewTrace();
            }
            doc.createElement("type", taskChangeContent.getType());
            doc.createElement("state", taskChangeContent.isRequestCompleted() ? 2 : 1);
            doc.createElement("taskId", taskChangeContent.getTaskId());
            doc.createElement("group", taskChangeContent.getGroup());
            doc.createElement("index", taskChangeContent.getIndex());
            doc.createElement("taskName", taskChangeContent.getTaskName());
            final int taskId = taskChangeContent.getTaskId();
            if (TaskBuilderJson.quizs.contains(taskId)) {
                final int quizId = TaskBuilderJson.quiz[TaskBuilderJson.quizs.indexOf(taskId)];
                final PlayerWorld pw = taskDataGetter.getPlayerWorldDao().read(playerDto.playerId);
                final int quizInfo = (pw.getQuizinfo() == null) ? 0 : pw.getQuizinfo();
                if (quizId > quizInfo) {
                    doc.createElement("quizInfo", quizId);
                }
            }
            doc.createElement("introShort", taskChangeContent.getIntros());
            doc.createElement("introLong", taskChangeContent.getIntrol());
            doc.createElement("processStr", taskChangeContent.getProcessStr());
            doc.createElement("requestCompleted", taskChangeContent.isRequestCompleted());
            doc.createElement("markTrace", taskChangeContent.getMarkTrace());
            doc.createElement("iosMarktrace", taskChangeContent.getIosMarkTrace());
            doc.createElement("areaId", taskChangeContent.getArea());
            doc.createElement("pic", taskChangeContent.getPic());
            doc.createElement("plot", taskChangeContent.getPlot());
            doc.startArray("resource");
            for (final int rewardType : TaskBuilderJson.rewardResourceTypes) {
                final Reward tempReward = rewardMap.get(rewardType);
                if (tempReward != null) {
                    doc.startObject();
                    doc.createElement("type", tempReward.getType());
                    doc.createElement("value", tempReward.getNum());
                    doc.endObject();
                }
            }
            doc.endArray();
            final Reward chiefReward = rewardMap.get(5);
            if (chiefReward != null) {
                doc.createElement("chiefExp", chiefReward.getNum());
            }
            final Reward newBuildingReward = rewardMap.get(6);
            if (newBuildingReward != null) {
                doc.createElement("newBuilding", ((Building)taskDataGetter.getBuildingCache().get((Object)newBuildingReward.getNum())).getName());
            }
            final Reward newConstructionReward = rewardMap.get(9);
            if (newConstructionReward != null) {
                doc.createElement("newConstruction", newConstructionReward.getName());
            }
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("newGuidId", newGuidId);
        doc.endObject();
        return doc.toByte();
    }
    
    public static byte[] sendJsonTaskChange(final TaskChangeContent taskChangeContent, final PlayerDto playerDto, final IDataGetter taskDataGetter) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("refreshTask");
        doc.startObject();
        doc.createElement("type", taskChangeContent.getType());
        doc.createElement("state", taskChangeContent.isRequestCompleted() ? 2 : 1);
        doc.createElement("taskId", taskChangeContent.getTaskId());
        doc.createElement("group", taskChangeContent.getGroup());
        doc.createElement("index", taskChangeContent.getIndex());
        doc.createElement("taskName", taskChangeContent.getTaskName());
        doc.createElement("introShort", taskChangeContent.getIntros());
        doc.createElement("introLong", taskChangeContent.getIntrol());
        doc.createElement("processStr", taskChangeContent.getProcessStr());
        doc.createElement("requestCompleted", taskChangeContent.isRequestCompleted());
        doc.createElement("markTrace", taskChangeContent.getMarkTrace());
        doc.createElement("iosMarktrace", taskChangeContent.getIosMarkTrace());
        doc.createElement("newTrace", taskChangeContent.getNewTrace());
        doc.createElement("areaId", taskChangeContent.getArea());
        doc.createElement("pic", taskChangeContent.getPic());
        final int taskId = taskChangeContent.getTaskId();
        if (TaskBuilderJson.quizs.contains(taskId)) {
            final int quizId = TaskBuilderJson.quiz[TaskBuilderJson.quizs.indexOf(taskId)];
            final PlayerWorld pw = taskDataGetter.getPlayerWorldDao().read(playerDto.playerId);
            final int quizInfo = (pw.getQuizinfo() == null) ? 0 : pw.getQuizinfo();
            if (quizId > quizInfo) {
                doc.createElement("quizInfo", quizId);
            }
        }
        doc.createElement("plot", taskChangeContent.getPlot());
        if (taskChangeContent.isRequestCompleted()) {
            ITaskReward taskReward = null;
            if (1 == taskChangeContent.getType()) {
                taskReward = TaskFactory.getInstance().getTask(taskChangeContent.getTaskId()).getTaskReward();
            }
            else {
                taskReward = TaskFactory.getInstance().getTask(taskChangeContent.getGroup(), taskChangeContent.getIndex(), taskChangeContent.getType()).getTaskReward();
            }
            final Map<Integer, Reward> rewardMap = taskReward.getReward(playerDto, taskDataGetter, null);
            doc.startArray("resource");
            for (final int rewardType : TaskBuilderJson.rewardResourceTypes) {
                final Reward tempReward = rewardMap.get(rewardType);
                if (tempReward != null) {
                    doc.startObject();
                    doc.createElement("type", tempReward.getType());
                    doc.createElement("value", tempReward.getNum());
                    doc.endObject();
                }
            }
            doc.endArray();
            final Reward chiefReward = rewardMap.get(5);
            if (chiefReward != null) {
                doc.createElement("chiefExp", chiefReward.getNum());
            }
        }
        doc.endObject();
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
}
