package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.util.*;

public class WdSjpGem implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer start;
    private Integer end;
    private Integer ironInit;
    private Integer ironDe;
    private Double multi2;
    private Double multi4;
    private Double multi10;
    private Double multi20;
    private String multi;
    private List<Tuple<Double, Integer>> probList;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getStart() {
        return this.start;
    }
    
    public void setStart(final Integer start) {
        this.start = start;
    }
    
    public Integer getEnd() {
        return this.end;
    }
    
    public void setEnd(final Integer end) {
        this.end = end;
    }
    
    public Integer getIronInit() {
        return this.ironInit;
    }
    
    public void setIronInit(final Integer ironInit) {
        this.ironInit = ironInit;
    }
    
    public Integer getIronDe() {
        return this.ironDe;
    }
    
    public void setIronDe(final Integer ironDe) {
        this.ironDe = ironDe;
    }
    
    public Double getMulti2() {
        return this.multi2;
    }
    
    public void setMulti2(final Double multi2) {
        this.multi2 = multi2;
    }
    
    public Double getMulti4() {
        return this.multi4;
    }
    
    public void setMulti4(final Double multi4) {
        this.multi4 = multi4;
    }
    
    public Double getMulti10() {
        return this.multi10;
    }
    
    public void setMulti10(final Double multi10) {
        this.multi10 = multi10;
    }
    
    public Double getMulti20() {
        return this.multi20;
    }
    
    public void setMulti20(final Double multi20) {
        this.multi20 = multi20;
    }
    
    public String getMulti() {
        return this.multi;
    }
    
    public void setMulti(final String multi) {
        this.multi = multi;
    }
    
    public List<Tuple<Double, Integer>> getProbList() {
        return this.probList;
    }
    
    public void setProbList(final List<Tuple<Double, Integer>> probList) {
        this.probList = probList;
    }
}
