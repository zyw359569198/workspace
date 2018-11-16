package com.reign.gcld.rank.common;

import com.reign.framework.json.*;
import java.util.*;
import com.reign.gcld.common.*;

public class IndividualJsonBuilder
{
    public static JsonDocument getTasksJson(final Collection<InMemmoryIndivTask> list) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("indivTasks");
        for (final InMemmoryIndivTask task : list) {
            final InMemmoryIndivTaskContent content = task.getDelegate().getContent();
            if (content != null) {
                if (content.hasRewarded) {
                    continue;
                }
                doc.startObject();
                doc.createElement("id", content.id);
                doc.createElement("goal", content.goal);
                doc.createElement("hasRewarded", content.hasRewarded);
                doc.createElement("hasUpgrade", content.hasUpgrade);
                doc.createElement("name", content.name);
                doc.createElement("intro", content.intro);
                doc.createElement("pic", content.pic);
                doc.createElement("process", content.process);
                doc.createElement("rewardNum", content.rewardNum);
                doc.createElement("rewardType", content.rewardType);
                doc.createElement("star", content.star);
                doc.createElement("maxStar", content.maxStar);
                doc.createElement("canUpdate", content.canUpdate);
                doc.createElement("gold", 5);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return doc;
    }
    
    public static JsonDocument getAppendTasksJson(final Collection<InMemmoryIndivTask> list, final int playerId, final IDataGetter getter) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("indivTasks");
        if (list != null) {
            for (final InMemmoryIndivTask task : list) {
                final InMemmoryIndivTaskContent content = task.getDelegate().getContent();
                if (content == null) {
                    continue;
                }
                doc.startObject();
                doc.createElement("id", content.id);
                doc.createElement("goal", content.goal);
                doc.createElement("hasRewarded", content.hasRewarded);
                doc.createElement("hasUpgrade", content.hasUpgrade);
                doc.createElement("name", content.name);
                doc.createElement("intro", content.intro);
                doc.createElement("pic", content.pic);
                doc.createElement("process", content.process);
                doc.createElement("rewardNum", content.rewardNum);
                doc.createElement("rewardType", content.rewardType);
                doc.createElement("star", content.star);
                doc.createElement("maxStar", content.maxStar);
                doc.createElement("canUpdate", content.canUpdate);
                doc.createElement("gold", 5);
                if (content.itemId > 0) {
                    final boolean flag = getter.getDiamondShopService().canRecvDropProps(playerId, content.itemId);
                    if (flag) {
                        doc.createElement("itemId", content.itemId);
                        doc.createElement("itemNum", content.itemNum);
                    }
                }
                doc.endObject();
            }
        }
        doc.endArray();
        return doc;
    }
    
    public static JsonDocument getTasksSimpleJson(final Collection<InMemmoryIndivTask> list) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("indivTasks");
        for (final InMemmoryIndivTask task : list) {
            final InMemmoryIndivTaskContent content = task.getDelegate().getContent();
            if (content != null) {
                if (content.hasRewarded) {
                    continue;
                }
                doc.startObject();
                doc.createElement("id", content.id);
                doc.createElement("identifier", task.req.identifier);
                doc.createElement("goal", content.goal);
                doc.createElement("hasRewarded", content.hasRewarded);
                doc.createElement("name", content.name);
                doc.createElement("intro", content.intro);
                doc.createElement("process", content.process);
                doc.createElement("rewardNum", content.rewardNum);
                doc.createElement("rewardType", content.rewardType);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return doc;
    }
    
    public static JsonDocument getAppendTasksSimpleJson(final Collection<InMemmoryIndivTask> list, final int playerId, final IDataGetter getter, final int hasNationTasks) {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("indivTasks");
        if (list != null) {
            for (final InMemmoryIndivTask task : list) {
                final InMemmoryIndivTaskContent content = task.getDelegate().getContent();
                if (content != null) {
                    if (content.hasRewarded) {
                        continue;
                    }
                    if (hasNationTasks <= 0 && content.process < content.goal) {
                        continue;
                    }
                    doc.startObject();
                    doc.createElement("id", content.id);
                    doc.createElement("identifier", task.req.identifier);
                    doc.createElement("goal", content.goal);
                    doc.createElement("hasRewarded", content.hasRewarded);
                    doc.createElement("name", content.name);
                    doc.createElement("intro", content.intro);
                    doc.createElement("process", content.process);
                    doc.createElement("rewardNum", content.rewardNum);
                    doc.createElement("rewardType", content.rewardType);
                    if (content.itemId > 0) {
                        final boolean flag = getter.getDiamondShopService().canRecvDropProps(playerId, content.itemId);
                        if (flag) {
                            doc.createElement("itemId", content.itemId);
                            doc.createElement("itemNum", content.itemNum);
                        }
                    }
                    doc.endObject();
                }
            }
        }
        doc.endArray();
        return doc;
    }
    
    public static JsonDocument getTasksSimpleJson(final InMemmoryIndivTask task, final int playerId, final IDataGetter getter) {
        final InMemmoryIndivTaskContent content = task.getDelegate().getContent();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("id", content.id);
        doc.createElement("intro", content.intro);
        doc.createElement("identifier", task.req.identifier);
        doc.createElement("goal", content.goal);
        doc.createElement("rewardType", content.rewardType);
        doc.createElement("rewardNum", content.rewardNum);
        doc.createElement("name", content.name);
        doc.createElement("process", content.process);
        doc.createElement("hasRewarded", content.hasRewarded);
        if (content.itemId > 0) {
            final boolean flag = getter.getDiamondShopService().canRecvDropProps(playerId, content.itemId);
            if (flag) {
                doc.createElement("itemId", content.itemId);
                doc.createElement("itemNum", content.itemNum);
            }
        }
        doc.endObject();
        return doc;
    }
    
    public static JsonDocument getTaskFinishInfo(final InMemmoryIndivTask task, final int playerId, final IDataGetter getter) {
        final InMemmoryIndivTaskContent content = task.getDelegate().getContent();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("id", content.id);
        doc.createElement("intro", content.intro);
        doc.createElement("pic", content.pic);
        doc.createElement("star", content.star);
        doc.createElement("goal", content.goal);
        doc.createElement("rewardType", content.rewardType);
        doc.createElement("rewardNum", content.rewardNum);
        if (content.itemId > 0) {
            final boolean flag = getter.getDiamondShopService().canRecvDropProps(playerId, content.itemId);
            if (flag) {
                doc.createElement("itemId", content.itemId);
                doc.createElement("itemNum", content.itemNum);
            }
        }
        doc.endObject();
        return doc;
    }
}
