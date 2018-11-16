package com.reign.kf.comm.param.match;

public class KfSpecialGeneral
{
    public int generalType;
    public double param;
    public int rowNum;
    public double param2;
    
    public KfSpecialGeneral() {
    }
    
    public KfSpecialGeneral(final int generalType, final double pa) {
        this.generalType = generalType;
        this.param = pa;
    }
    
    public int getGeneralType() {
        return this.generalType;
    }
    
    public void setGeneralType(final int generalType) {
        this.generalType = generalType;
    }
    
    public double getParam() {
        return this.param;
    }
    
    public void setParam(final double param) {
        this.param = param;
    }
}
