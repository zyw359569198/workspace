package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class PrisonLv implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer prisonLv;
    private Integer drawing;
    
    public Integer getPrisonLv() {
        return this.prisonLv;
    }
    
    public void setPrisonLv(final Integer prisonLv) {
        this.prisonLv = prisonLv;
    }
    
    public Integer getDrawing() {
        return this.drawing;
    }
    
    public void setDrawing(final Integer drawing) {
        this.drawing = drawing;
    }
}
