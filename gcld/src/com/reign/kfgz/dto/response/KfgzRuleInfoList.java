package com.reign.kfgz.dto.response;

import java.util.*;

public class KfgzRuleInfoList
{
    Map<Integer, KfgzRuleInfoRes> rMap;
    Map<Integer, KfgzLayerInfoRes> layMap;
    
    public KfgzRuleInfoList() {
        this.rMap = new HashMap<Integer, KfgzRuleInfoRes>();
        this.layMap = new HashMap<Integer, KfgzLayerInfoRes>();
    }
    
    public Map<Integer, KfgzRuleInfoRes> getrMap() {
        return this.rMap;
    }
    
    public void setrMap(final Map<Integer, KfgzRuleInfoRes> rMap) {
        this.rMap = rMap;
    }
    
    public Map<Integer, KfgzLayerInfoRes> getLayMap() {
        return this.layMap;
    }
    
    public void setLayMap(final Map<Integer, KfgzLayerInfoRes> layMap) {
        this.layMap = layMap;
    }
}
