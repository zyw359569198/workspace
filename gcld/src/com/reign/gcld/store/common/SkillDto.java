package com.reign.gcld.store.common;

import org.apache.commons.lang.*;

public class SkillDto
{
    private int skillId;
    private int skillLv;
    
    public SkillDto(final String skillString) {
        if (StringUtils.isBlank(skillString)) {
            this.setSkillId(0);
            this.setSkillLv(0);
        }
        else {
            final String[] ski = skillString.split(":");
            this.setSkillId(Integer.valueOf(ski[0]));
            this.setSkillLv(Integer.valueOf(ski[1]));
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.skillId) + ":" + this.skillLv;
    }
    
    public void setSkillId(final int skillId) {
        this.skillId = skillId;
    }
    
    public int getSkillId() {
        return this.skillId;
    }
    
    public void setSkillLv(final int skillLv) {
        this.skillLv = skillLv;
    }
    
    public int getSkillLv() {
        return this.skillLv;
    }
    
    public String upLv() {
        this.skillLv = ((this.skillLv >= 5) ? this.skillLv : (this.skillLv + 1));
        return String.valueOf(this.skillId) + ":" + this.skillLv + ";";
    }
}
