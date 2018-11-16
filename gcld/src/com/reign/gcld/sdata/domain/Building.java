package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Building implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private Integer pos;
    private String name;
    private Integer timeBase;
    private Double timeE;
    private Integer timeS;
    private Integer timeR;
    private Integer timeT;
    private Double chiefExpE;
    private Integer chiefExpS;
    private Double lumberE;
    private Integer lumberS;
    private Double copperE;
    private Integer copperS;
    private Integer outputType;
    private Double outputE;
    private Integer outputS;
    private Double outputE1;
    private String outputRelatedBuilding;
    private String intro;
    private Integer drawing;
    private Integer openLv;
    private String drawingIntro;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getPos() {
        return this.pos;
    }
    
    public void setPos(final Integer pos) {
        this.pos = pos;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getTimeBase() {
        return this.timeBase;
    }
    
    public void setTimeBase(final Integer timeBase) {
        this.timeBase = timeBase;
    }
    
    public Double getTimeE() {
        return this.timeE;
    }
    
    public void setTimeE(final Double timeE) {
        this.timeE = timeE;
    }
    
    public Integer getTimeS() {
        return this.timeS;
    }
    
    public void setTimeS(final Integer timeS) {
        this.timeS = timeS;
    }
    
    public Integer getTimeR() {
        return this.timeR;
    }
    
    public void setTimeR(final Integer timeR) {
        this.timeR = timeR;
    }
    
    public Integer getTimeT() {
        return this.timeT;
    }
    
    public void setTimeT(final Integer timeT) {
        this.timeT = timeT;
    }
    
    public Double getChiefExpE() {
        return this.chiefExpE;
    }
    
    public void setChiefExpE(final Double chiefExpE) {
        this.chiefExpE = chiefExpE;
    }
    
    public Integer getChiefExpS() {
        return this.chiefExpS;
    }
    
    public void setChiefExpS(final Integer chiefExpS) {
        this.chiefExpS = chiefExpS;
    }
    
    public Double getLumberE() {
        return this.lumberE;
    }
    
    public void setLumberE(final Double lumberE) {
        this.lumberE = lumberE;
    }
    
    public Integer getLumberS() {
        return this.lumberS;
    }
    
    public void setLumberS(final Integer lumberS) {
        this.lumberS = lumberS;
    }
    
    public Double getCopperE() {
        return this.copperE;
    }
    
    public void setCopperE(final Double copperE) {
        this.copperE = copperE;
    }
    
    public Integer getCopperS() {
        return this.copperS;
    }
    
    public void setCopperS(final Integer copperS) {
        this.copperS = copperS;
    }
    
    public Integer getOutputType() {
        return this.outputType;
    }
    
    public void setOutputType(final Integer outputType) {
        this.outputType = outputType;
    }
    
    public Double getOutputE() {
        return this.outputE;
    }
    
    public void setOutputE(final Double outputE) {
        this.outputE = outputE;
    }
    
    public Integer getOutputS() {
        return this.outputS;
    }
    
    public void setOutputS(final Integer outputS) {
        this.outputS = outputS;
    }
    
    public Double getOutputE1() {
        return this.outputE1;
    }
    
    public void setOutputE1(final Double outputE1) {
        this.outputE1 = outputE1;
    }
    
    public String getOutputRelatedBuilding() {
        return this.outputRelatedBuilding;
    }
    
    public void setOutputRelatedBuilding(final String outputRelatedBuilding) {
        this.outputRelatedBuilding = outputRelatedBuilding;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getDrawing() {
        return this.drawing;
    }
    
    public void setDrawing(final Integer drawing) {
        this.drawing = drawing;
    }
    
    public Integer getOpenLv() {
        return this.openLv;
    }
    
    public void setOpenLv(final Integer openLv) {
        this.openLv = openLv;
    }
    
    public String getDrawingIntro() {
        return this.drawingIntro;
    }
    
    public void setDrawingIntro(final String drawingIntro) {
        this.drawingIntro = drawingIntro;
    }
}
