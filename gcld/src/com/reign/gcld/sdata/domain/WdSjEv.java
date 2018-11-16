package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class WdSjEv implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String disc;
    private String pic;
    private String disc1;
    private String disc2;
    private String reward1;
    private String reward2;
    private String rewardDisc1;
    private String rewardDisc2;
    private Integer goldConsume1;
    private Integer goldConsume2;
    private Integer terrain;
    private Integer view;
    private Integer rewardLv;
    private String notice;
    private ITaskReward taskReward1;
    private ITaskReward taskReward2;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getDisc() {
        return this.disc;
    }
    
    public void setDisc(final String disc) {
        this.disc = disc;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getDisc1() {
        return this.disc1;
    }
    
    public void setDisc1(final String disc1) {
        this.disc1 = disc1;
    }
    
    public String getDisc2() {
        return this.disc2;
    }
    
    public void setDisc2(final String disc2) {
        this.disc2 = disc2;
    }
    
    public String getReward1() {
        return this.reward1;
    }
    
    public void setReward1(final String reward1) {
        this.reward1 = reward1;
    }
    
    public String getReward2() {
        return this.reward2;
    }
    
    public void setReward2(final String reward2) {
        this.reward2 = reward2;
    }
    
    public String getRewardDisc1() {
        return this.rewardDisc1;
    }
    
    public void setRewardDisc1(final String rewardDisc1) {
        this.rewardDisc1 = rewardDisc1;
    }
    
    public String getRewardDisc2() {
        return this.rewardDisc2;
    }
    
    public void setRewardDisc2(final String rewardDisc2) {
        this.rewardDisc2 = rewardDisc2;
    }
    
    public Integer getGoldConsume1() {
        return this.goldConsume1;
    }
    
    public void setGoldConsume1(final Integer goldConsume1) {
        this.goldConsume1 = goldConsume1;
    }
    
    public Integer getGoldConsume2() {
        return this.goldConsume2;
    }
    
    public void setGoldConsume2(final Integer goldConsume2) {
        this.goldConsume2 = goldConsume2;
    }
    
    public Integer getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(final Integer terrain) {
        this.terrain = terrain;
    }
    
    public Integer getView() {
        return this.view;
    }
    
    public void setView(final Integer view) {
        this.view = view;
    }
    
    public Integer getRewardLv() {
        return this.rewardLv;
    }
    
    public void setRewardLv(final Integer rewardLv) {
        this.rewardLv = rewardLv;
    }
    
    public String getNotice() {
        return this.notice;
    }
    
    public void setNotice(final String notice) {
        this.notice = notice;
    }
    
    public ITaskReward getTaskReward1() {
        return this.taskReward1;
    }
    
    public void setTaskReward1(final ITaskReward taskReward1) {
        this.taskReward1 = taskReward1;
    }
    
    public ITaskReward getTaskReward2() {
        return this.taskReward2;
    }
    
    public void setTaskReward2(final ITaskReward taskReward2) {
        this.taskReward2 = taskReward2;
    }
}
