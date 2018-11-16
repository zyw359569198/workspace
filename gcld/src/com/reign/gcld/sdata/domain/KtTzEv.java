package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtTzEv implements IModel, Comparable<KtTzEv>
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer s;
    private Integer i;
    private Integer t;
    private String name;
    private String intro;
    private String pic;
    private Integer cm;
    private Integer cc;
    private Integer cd;
    private Integer cdMax;
    private Integer er;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getS() {
        return this.s;
    }
    
    public void setS(final Integer s) {
        this.s = s;
    }
    
    public Integer getI() {
        return this.i;
    }
    
    public void setI(final Integer i) {
        this.i = i;
    }
    
    public Integer getT() {
        return this.t;
    }
    
    public void setT(final Integer t) {
        this.t = t;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getCm() {
        return this.cm;
    }
    
    public void setCm(final Integer cm) {
        this.cm = cm;
    }
    
    public Integer getCc() {
        return this.cc;
    }
    
    public void setCc(final Integer cc) {
        this.cc = cc;
    }
    
    public Integer getCd() {
        return this.cd;
    }
    
    public void setCd(final Integer cd) {
        this.cd = cd;
    }
    
    public Integer getCdMax() {
        return this.cdMax;
    }
    
    public void setCdMax(final Integer cdMax) {
        this.cdMax = cdMax;
    }
    
    public Integer getEr() {
        return this.er;
    }
    
    public void setEr(final Integer er) {
        this.er = er;
    }
    
    @Override
	public int compareTo(final KtTzEv o) {
        if (o == null) {
            return 1;
        }
        return this.getId() - o.getId();
    }
}
