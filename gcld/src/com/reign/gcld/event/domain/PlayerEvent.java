package com.reign.gcld.event.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerEvent implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer eventId;
    private Integer param1;
    private Integer param2;
    private Integer param3;
    private Integer param4;
    private Integer param5;
    private Integer param6;
    private Integer param7;
    private Integer param8;
    private Integer param9;
    private Integer param10;
    private Date cd1;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getEventId() {
        return this.eventId;
    }
    
    public void setEventId(final Integer eventId) {
        this.eventId = eventId;
    }
    
    public Integer getParam1() {
        return this.param1;
    }
    
    public void setParam1(final Integer param1) {
        this.param1 = param1;
    }
    
    public Integer getParam2() {
        return this.param2;
    }
    
    public void setParam2(final Integer param2) {
        this.param2 = param2;
    }
    
    public Integer getParam3() {
        return this.param3;
    }
    
    public void setParam3(final Integer param3) {
        this.param3 = param3;
    }
    
    public Integer getParam4() {
        return this.param4;
    }
    
    public void setParam4(final Integer param4) {
        this.param4 = param4;
    }
    
    public Integer getParam5() {
        return this.param5;
    }
    
    public void setParam5(final Integer param5) {
        this.param5 = param5;
    }
    
    public Integer getParam6() {
        return this.param6;
    }
    
    public void setParam6(final Integer param6) {
        this.param6 = param6;
    }
    
    public Integer getParam7() {
        return this.param7;
    }
    
    public void setParam7(final Integer param7) {
        this.param7 = param7;
    }
    
    public Integer getParam8() {
        return this.param8;
    }
    
    public void setParam8(final Integer param8) {
        this.param8 = param8;
    }
    
    public Integer getParam9() {
        return this.param9;
    }
    
    public void setParam9(final Integer param9) {
        this.param9 = param9;
    }
    
    public Integer getParam10() {
        return this.param10;
    }
    
    public void setParam10(final Integer param10) {
        this.param10 = param10;
    }
    
    public Date getCd1() {
        return this.cd1;
    }
    
    public void setCd1(final Date cd1) {
        this.cd1 = cd1;
    }
}
