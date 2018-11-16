package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class KtMzS implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer t;
    private String wei;
    private String shu;
    private String wu;
    private Integer n;
    private Set<Integer> weiSet;
    private Set<Integer> shuSet;
    private Set<Integer> wuSet;
    
    public KtMzS() {
        this.weiSet = new HashSet<Integer>();
        this.shuSet = new HashSet<Integer>();
        this.wuSet = new HashSet<Integer>();
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getT() {
        return this.t;
    }
    
    public void setT(final Integer t) {
        this.t = t;
    }
    
    public String getWei() {
        return this.wei;
    }
    
    public void setWei(final String wei) {
        this.wei = wei;
    }
    
    public String getShu() {
        return this.shu;
    }
    
    public void setShu(final String shu) {
        this.shu = shu;
    }
    
    public String getWu() {
        return this.wu;
    }
    
    public void setWu(final String wu) {
        this.wu = wu;
    }
    
    public Integer getN() {
        return this.n;
    }
    
    public void setN(final Integer n) {
        this.n = n;
    }
    
    public Set<Integer> getWeiSet() {
        return this.weiSet;
    }
    
    public void setWeiSet(final Set<Integer> weiSet) {
        this.weiSet = weiSet;
    }
    
    public Set<Integer> getShuSet() {
        return this.shuSet;
    }
    
    public void setShuSet(final Set<Integer> shuSet) {
        this.shuSet = shuSet;
    }
    
    public Set<Integer> getWuSet() {
        return this.wuSet;
    }
    
    public void setWuSet(final Set<Integer> wuSet) {
        this.wuSet = wuSet;
    }
}
