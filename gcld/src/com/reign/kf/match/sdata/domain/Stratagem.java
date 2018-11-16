package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;

public class Stratagem implements IModel
{
    public static final String TRICK_GUWU = "guwu";
    public static final String TRICK_DONGYAO = "dongyao";
    public static final String TRICK_HUOGONG = "huogong";
    public static final String TRICK_SHUIGONG = "shuigong";
    public static final String TRICK_LUOSHI = "luoshi";
    public static final String TRICK_XIANJING = "xianjing";
    public static final String TRICK_KONGCHENG = "kongcheng";
    public static final String TRICK_HUANGBAO = "huangbao";
    private static final long serialVersionUID = 1L;
    private String error;
    private Integer lastCd;
    private String par3Intro;
    private String pic;
    private String type;
    private String intro;
    private Integer id;
    private Integer par1;
    private String par4Intro;
    private Integer par2;
    private String par2Intro;
    private Integer par3;
    private Integer par4;
    private String name;
    private String par1Intro;
    private Integer quality;
    private Integer cd;
    
    public String getError() {
        return this.error;
    }
    
    public void setError(final String error) {
        this.error = error;
    }
    
    public Integer getLastCd() {
        return this.lastCd;
    }
    
    public void setLastCd(final Integer lastCd) {
        this.lastCd = lastCd;
    }
    
    public String getPar3Intro() {
        return this.par3Intro;
    }
    
    public void setPar3Intro(final String par3Intro) {
        this.par3Intro = par3Intro;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getPar1() {
        return this.par1;
    }
    
    public void setPar1(final Integer par1) {
        this.par1 = par1;
    }
    
    public String getPar4Intro() {
        return this.par4Intro;
    }
    
    public void setPar4Intro(final String par4Intro) {
        this.par4Intro = par4Intro;
    }
    
    public Integer getPar2() {
        return this.par2;
    }
    
    public void setPar2(final Integer par2) {
        this.par2 = par2;
    }
    
    public String getPar2Intro() {
        return this.par2Intro;
    }
    
    public void setPar2Intro(final String par2Intro) {
        this.par2Intro = par2Intro;
    }
    
    public Integer getPar3() {
        return this.par3;
    }
    
    public void setPar3(final Integer par3) {
        this.par3 = par3;
    }
    
    public Integer getPar4() {
        return this.par4;
    }
    
    public void setPar4(final Integer par4) {
        this.par4 = par4;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getPar1Intro() {
        return this.par1Intro;
    }
    
    public void setPar1Intro(final String par1Intro) {
        this.par1Intro = par1Intro;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
    
    public Integer getCd() {
        return this.cd;
    }
    
    public void setCd(final Integer cd) {
        this.cd = cd;
    }
}
