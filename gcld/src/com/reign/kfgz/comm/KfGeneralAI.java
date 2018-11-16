package com.reign.kfgz.comm;

import com.reign.kfgz.ai.event.*;
import com.reign.kfgz.control.*;
import java.util.*;
import com.reign.kfgz.ai.*;
import com.reign.kfgz.ai.behaviour.*;

public class KfGeneralAI
{
    private KfGeneralInfo gInfo;
    private String script;
    
    public String getAIName() {
        return this.script.split(":")[0];
    }
    
    public void nextBehaviour(final AIEvent event) {
        final String[] ss = this.script.split(":");
        if (ss[0].equals("moveto")) {
            final int gzId = this.gInfo.getpInfo().getGzId();
            List<Integer> dp;
            if (Integer.valueOf(ss[1]) == 0) {
                dp = new ArrayList<Integer>();
            }
            else {
                dp = KfgzManager.getKfWorldByGzId(gzId).getDetailPath(this.gInfo.getpInfo().getForceId(), this.gInfo.getCityPos(), Integer.valueOf(ss[1]));
                if (dp == null) {
                    return;
                }
            }
            final MoveGeneral be = new MoveGeneral(this.gInfo);
            be.setFromCity(this.gInfo.getCityPos());
            be.setgAi(this);
            be.setNeedCheckFromCity(true);
            if (dp.size() <= 0) {
                this.gInfo.setCityList(null);
                be.setToCity(be.getFromCity());
            }
            else {
                this.gInfo.setCityList(dp);
                be.setToCity(dp.get(0));
            }
            if (System.currentTimeMillis() >= this.gInfo.getCanMoveTime()) {
                be.setExecuteTime(System.currentTimeMillis());
            }
            else {
                be.setExecuteTime(this.gInfo.getCanMoveTime());
            }
            AIBehaviourExecutor.getInstance().addAITask(be);
        }
        if (ss[0].equals("moveroad")) {
            final int gzId = this.gInfo.getpInfo().getGzId();
            final int cityPos = this.gInfo.cityPos;
            final List<Integer> dp2 = new ArrayList<Integer>();
            final String roadList = ss[1];
            final String[] rl = roadList.split("-");
            boolean choosen = false;
            String[] array;
            for (int length = (array = rl).length, i = 0; i < length; ++i) {
                final String s = array[i];
                final Integer note = Integer.parseInt(s);
                if (!choosen && note == cityPos) {
                    choosen = true;
                }
                else if (choosen) {
                    dp2.add(note);
                }
            }
            final MoveGeneral be2 = new MoveGeneral(this.gInfo);
            be2.setFromCity(cityPos);
            be2.setgAi(this);
            be2.setNeedCheckFromCity(false);
            if (dp2.size() <= 0) {
                this.gInfo.setCityList(null);
                be2.setToCity(be2.getFromCity());
            }
            else {
                this.gInfo.setCityList(dp2);
                be2.setToCity(dp2.get(0));
            }
            if (System.currentTimeMillis() >= this.gInfo.getCanMoveTime()) {
                be2.setExecuteTime(System.currentTimeMillis());
            }
            else {
                be2.setExecuteTime(this.gInfo.getCanMoveTime());
            }
            AIBehaviourExecutor.getInstance().addAITask(be2);
        }
        else if (!ss[0].equals("defend")) {
            ss[0].equals("attack");
        }
    }
    
    public void setScript(final String script) {
        this.script = script;
    }
    
    public String getScript() {
        return this.script;
    }
    
    public void setgInfo(final KfGeneralInfo gInfo) {
        this.gInfo = gInfo;
    }
    
    public KfGeneralInfo getgInfo() {
        return this.gInfo;
    }
}
