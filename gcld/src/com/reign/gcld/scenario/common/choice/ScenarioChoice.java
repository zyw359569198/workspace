package com.reign.gcld.scenario.common.choice;

import org.apache.commons.lang.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

public class ScenarioChoice implements Cloneable
{
    private String choice1;
    private String choice2;
    private String operation1;
    private String operation2;
    private String title;
    private int choiceCity;
    private int curChoice;
    private String choiceCatogery;
    private String costs;
    public static final String FIGHT = "fight";
    public static final String DIALOG = "dailog";
    public static final String MULTICHOICE = "multichoice";
    public static final String BUFF = "buff";
    public static final String RANDCHOICE = "randchoice";
    public static final String DYNAMICCHOICE = "dychoice";
    
    public String getChoiceCatogery() {
        return this.choiceCatogery;
    }
    
    public String getCosts() {
        return this.costs;
    }
    
    public void setCosts(final String costs) {
        this.costs = costs;
    }
    
    public void setChoiceCatogery(final String choiceCatogery) {
        this.choiceCatogery = choiceCatogery;
    }
    
    public String getOperation1() {
        return this.operation1;
    }
    
    public void setOperation1(final String operation1) {
        this.operation1 = operation1;
    }
    
    public String getOperation2() {
        return this.operation2;
    }
    
    public void setOperation2(final String operation2) {
        this.operation2 = operation2;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public int getChoiceCity() {
        return this.choiceCity;
    }
    
    public void setChoiceCity(final int choiceCity) {
        this.choiceCity = choiceCity;
    }
    
    public int getCurChoice() {
        return this.curChoice;
    }
    
    public void setCurChoice(final int curChoice) {
        this.curChoice = curChoice;
    }
    
    public String getChoice1() {
        return this.choice1;
    }
    
    public void setChoice1(final String choice1) {
        this.choice1 = choice1;
    }
    
    public String getChoice2() {
        return this.choice2;
    }
    
    public void setChoice2(final String choice2) {
        this.choice2 = choice2;
    }
    
    public ScenarioChoice(final String choice1, final String choise2, final String flag2, final String title, final String dealOperation1, final String dealOperation2) {
        this.choice1 = null;
        this.choice2 = null;
        this.operation1 = null;
        this.operation2 = null;
        this.title = null;
        this.choiceCity = 0;
        this.curChoice = 0;
        this.choice1 = choice1;
        this.choice2 = choise2;
        this.operation1 = dealOperation1;
        this.operation2 = dealOperation2;
        if (!StringUtils.isBlank(flag2) && (!StringUtils.isBlank(choice1) || !StringUtils.isBlank(choise2))) {
            final String[] single = flag2.split(",");
            this.choiceCity = Integer.parseInt(single[1]);
            this.title = title;
        }
        this.choiceCatogery = this.getChoiceCatogeryByChoice();
        this.costs = this.getCostsByChoice(choice1);
    }
    
    private String getCostsByChoice(final String choice) {
        if (StringUtils.isBlank(choice)) {
            return null;
        }
        if (!this.choiceCatogery.equalsIgnoreCase("randchoice")) {
            return null;
        }
        final String[] single = this.choice1.split(",");
        if (single.length >= 2) {
            return single[single.length - 1];
        }
        return null;
    }
    
    @Override
	public ScenarioChoice clone() throws CloneNotSupportedException {
        return (ScenarioChoice)super.clone();
    }
    
    public JsonDocument getChoiceInfo(final List<SoloCity> neighbours, final int mengdeLocation) {
        if (this.choice1 == null && this.choice2 == null) {
            return null;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startObject("left");
        this.appendChoiceInfo(doc, this.choice1, this.operation1, neighbours);
        doc.endObject();
        doc.startObject("right");
        this.appendChoiceInfo(doc, this.choice2, this.operation2, neighbours);
        doc.endObject();
        if (!StringUtils.isBlank(this.title)) {
            doc.createElement("title", this.title);
        }
        if (mengdeLocation > 0) {
            doc.createElement("mengdeLocation", mengdeLocation);
        }
        doc.endObject();
        return doc;
    }
    
    private void appendChoiceInfo(final JsonDocument doc, final String choice1, final String operation, final List<SoloCity> neighbours) {
        try {
            if (!StringUtils.isBlank(choice1)) {
                final String[] single = choice1.split(",");
                if (single[0].equalsIgnoreCase("fight")) {
                    doc.createElement("type", single[0]);
                    doc.createElement("npc", this.choiceCity);
                    doc.createElement("terrian", single[2]);
                    doc.createElement("fightType", 20);
                }
                else if (single[0].equalsIgnoreCase("dailog")) {
                    doc.createElement("type", single[0]);
                    doc.createElement("content", single[1]);
                }
                else if (single[0].equalsIgnoreCase("multichoice")) {
                    doc.createElement("type", single[0]);
                    final StringBuffer sb = new StringBuffer();
                    for (int i = 1; i < single.length; ++i) {
                        final String[] cell = single[i].split("-");
                        sb.append(cell[0]).append(",");
                    }
                    SymbolUtil.removeTheLast(sb);
                    doc.createElement("content", sb.toString());
                }
                else if (single[0].equalsIgnoreCase("randchoice")) {
                    doc.createElement("type", single[0]);
                    doc.startArray("kits");
                    for (int j = 1; j < single.length - 1; ++j) {
                        final String eventStr = single[j];
                        doc.startObject();
                        doc.createElement("pic", eventStr);
                        doc.endObject();
                    }
                    doc.endArray();
                    doc.createElement("copper", single[single.length - 1]);
                }
                else if (single[0].equalsIgnoreCase("dychoice")) {
                    doc.createElement("type", single[0]);
                    doc.startArray("cities");
                    for (final SoloCity city : neighbours) {
                        doc.startObject();
                        doc.createElement("cityName", city.getName());
                        doc.createElement("cityId", city.getId());
                        doc.endObject();
                    }
                    doc.endArray();
                }
                if (!StringUtils.isBlank(operation)) {
                    final String[] reward = operation.split(";");
                    if (reward.length >= 1) {
                        doc.startArray("reward");
                        for (int i = 0; i < reward.length; ++i) {
                            final String[] rewardSingle = reward[i].split(",");
                            doc.startObject();
                            doc.createElement("rewardType", rewardSingle[0]);
                            if (rewardSingle.length == 2) {
                                doc.createElement("value", rewardSingle[1]);
                            }
                            else if ("buff".equalsIgnoreCase(rewardSingle[0])) {
                                doc.createElement("camp", rewardSingle[1]);
                                doc.createElement("effect", rewardSingle[2]);
                                doc.createElement("value", rewardSingle[3]);
                            }
                            doc.endObject();
                        }
                        doc.endArray();
                    }
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("getChoice fail....choice:" + choice1 + "   operation:" + operation);
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
        }
    }
    
    public boolean getHasFight() {
        boolean result = false;
        if (!StringUtils.isBlank(this.choice1)) {
            final String[] single = this.choice1.split(",");
            if (single[0].equalsIgnoreCase("fight")) {
                result = true;
            }
        }
        if (!result && !StringUtils.isBlank(this.choice2)) {
            final String[] single = this.choice2.split(",");
            if (single[0].equalsIgnoreCase("fight")) {
                return true;
            }
        }
        return result;
    }
    
    public int getMultiChoiceContent(final int choice) {
        try {
            if (StringUtils.isBlank(this.choice1)) {
                return -1;
            }
            final String[] single = this.choice1.split(",");
            if (!single[0].equalsIgnoreCase("multichoice")) {
                return -1;
            }
            final String realChoice = single[choice];
            if (StringUtils.isBlank(realChoice)) {
                return -1;
            }
            final String[] cell = realChoice.split("-");
            return Integer.parseInt(cell[1]);
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return -1;
        }
    }
    
    public String getChoiceCatogeryByChoice() {
        if (StringUtils.isBlank(this.choice1)) {
            return "";
        }
        return this.choice1.split(",")[0];
    }
    
    public int getRandChoiceContent(final int choice) {
        try {
            if (StringUtils.isBlank(this.choice1)) {
                return -1;
            }
            final String[] single = this.choice1.split(",");
            if (!single[0].equalsIgnoreCase("randchoice")) {
                return -1;
            }
            final int length = single.length;
            return length - 2;
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(this, e);
            return -1;
        }
    }
}
