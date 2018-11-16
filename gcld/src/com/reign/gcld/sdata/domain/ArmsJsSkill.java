package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class ArmsJsSkill implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String intro;
    private String explain;
    private Double effect1;
    private Double effect2;
    private Double effect3;
    private Double effect4;
    private Double effect5;
    private Double upp1;
    private Double upp2;
    private Double upp3;
    private Double upp4;
    private String pic;
    
    public Double getByLv(final int lv) {
        if (lv == 1) {
            return this.effect1;
        }
        if (lv == 2) {
            return this.effect2;
        }
        if (lv == 3) {
            return this.effect3;
        }
        if (lv == 4) {
            return this.effect4;
        }
        if (lv == 5) {
            return this.effect5;
        }
        return 0.0;
    }
    
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
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getExplain() {
        return this.explain;
    }
    
    public void setExplain(final String explain) {
        this.explain = explain;
    }
    
    public Double getEffect1() {
        return this.effect1;
    }
    
    public void setEffect1(final Double effect1) {
        this.effect1 = effect1;
    }
    
    public Double getEffect2() {
        return this.effect2;
    }
    
    public void setEffect2(final Double effect2) {
        this.effect2 = effect2;
    }
    
    public Double getEffect3() {
        return this.effect3;
    }
    
    public void setEffect3(final Double effect3) {
        this.effect3 = effect3;
    }
    
    public Double getEffect4() {
        return this.effect4;
    }
    
    public void setEffect4(final Double effect4) {
        this.effect4 = effect4;
    }
    
    public Double getEffect5() {
        return this.effect5;
    }
    
    public void setEffect5(final Double effect5) {
        this.effect5 = effect5;
    }
    
    public Double getUpp1() {
        return this.upp1;
    }
    
    public void setUpp1(final Double upp1) {
        this.upp1 = upp1;
    }
    
    public Double getUpp2() {
        return this.upp2;
    }
    
    public void setUpp2(final Double upp2) {
        this.upp2 = upp2;
    }
    
    public Double getUpp3() {
        return this.upp3;
    }
    
    public void setUpp3(final Double upp3) {
        this.upp3 = upp3;
    }
    
    public Double getUpp4() {
        return this.upp4;
    }
    
    public void setUpp4(final Double upp4) {
        this.upp4 = upp4;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
}
