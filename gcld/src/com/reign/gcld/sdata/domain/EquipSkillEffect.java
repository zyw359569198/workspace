package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.common.*;

public class EquipSkillEffect implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer skillId;
    private Integer skillLv;
    private String effect;
    private AttDefHp attDefHp;
    private AttDef_B attDef_B;
    private int TACTIC_ATT;
    private int TACTIC_DEF;
    
    public EquipSkillEffect() {
        this.TACTIC_ATT = 0;
        this.TACTIC_DEF = 0;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getSkillId() {
        return this.skillId;
    }
    
    public void setSkillId(final Integer skillId) {
        this.skillId = skillId;
    }
    
    public Integer getSkillLv() {
        return this.skillLv;
    }
    
    public void setSkillLv(final Integer skillLv) {
        this.skillLv = skillLv;
    }
    
    public String getEffect() {
        return this.effect;
    }
    
    public void setEffect(final String effect) {
        this.effect = effect;
    }
    
    public AttDefHp getAttDefHp() {
        return this.attDefHp;
    }
    
    public void setAttDefHp(final AttDefHp attDefHp) {
        this.attDefHp = attDefHp;
    }
    
    public AttDef_B getAttDef_B() {
        return this.attDef_B;
    }
    
    public void setAttDef_B(final AttDef_B attDef_B) {
        this.attDef_B = attDef_B;
    }
    
    public int getTACTIC_ATT() {
        return this.TACTIC_ATT;
    }
    
    public void setTACTIC_ATT(final int tACTIC_ATT) {
        this.TACTIC_ATT = tACTIC_ATT;
    }
    
    public int getTACTIC_DEF() {
        return this.TACTIC_DEF;
    }
    
    public void setTACTIC_DEF(final int tACTIC_DEF) {
        this.TACTIC_DEF = tACTIC_DEF;
    }
}
