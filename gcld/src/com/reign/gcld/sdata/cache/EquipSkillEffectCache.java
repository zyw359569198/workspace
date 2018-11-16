package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

@Component("EquipSkillEffectCache")
public class EquipSkillEffectCache extends AbstractCache<Integer, EquipSkillEffect>
{
    @Autowired
    private SDataLoader dataLoader;
    private String ATT;
    private String DEF;
    private String BLOOD;
    private String ATT_B;
    private String DEF_B;
    private String TACTIC_ATT;
    private String TACTIC_DEF;
    LinkedHashMap<Integer, LinkedHashMap<Integer, EquipSkillEffect>> skillIdLvMap;
    
    public EquipSkillEffectCache() {
        this.ATT = "ATT";
        this.DEF = "DEF";
        this.BLOOD = "BLOOD";
        this.ATT_B = "ATT_B";
        this.DEF_B = "DEF_B";
        this.TACTIC_ATT = "TACTIC_ATT";
        this.TACTIC_DEF = "TACTIC_DEF";
        this.skillIdLvMap = new LinkedHashMap<Integer, LinkedHashMap<Integer, EquipSkillEffect>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<EquipSkillEffect> resultList = this.dataLoader.getModels((Class)EquipSkillEffect.class);
        for (final EquipSkillEffect temp : resultList) {
            LinkedHashMap<Integer, EquipSkillEffect> lvMap = this.skillIdLvMap.get(temp.getSkillId());
            if (lvMap == null) {
                lvMap = new LinkedHashMap<Integer, EquipSkillEffect>();
                this.skillIdLvMap.put(temp.getSkillId(), lvMap);
            }
            this.skillIdLvMap.get(temp.getSkillId()).put(temp.getSkillLv(), temp);
            final String[] effect = temp.getEffect().split("=");
            if (effect.length != 2) {
                throw new RuntimeException("EquipSkillEffectCache Effect init failed. id:" + temp.getId());
            }
            final AttDefHp attDefHp = new AttDefHp();
            final AttDef_B attDef_B = new AttDef_B();
            if (effect[0].equals(this.ATT)) {
                attDefHp.att = Integer.parseInt(effect[1]);
            }
            else if (effect[0].equals(this.DEF)) {
                attDefHp.def = Integer.parseInt(effect[1]);
            }
            else if (effect[0].equals(this.BLOOD)) {
                attDefHp.hp = Integer.parseInt(effect[1]);
            }
            else if (effect[0].equals(this.ATT_B)) {
                attDef_B.ATT_B = Integer.parseInt(effect[1]);
            }
            else if (effect[0].equals(this.DEF_B)) {
                attDef_B.DEF_B = Integer.parseInt(effect[1]);
            }
            else if (effect[0].equals(this.TACTIC_ATT)) {
                temp.setTACTIC_ATT(Integer.parseInt(effect[1]));
            }
            else {
                if (!effect[0].equals(this.TACTIC_DEF)) {
                    throw new RuntimeException("EquipSkillEffectCache Effect init failed. id:" + temp.getId());
                }
                temp.setTACTIC_DEF(Integer.parseInt(effect[1]));
            }
            temp.setAttDefHp(attDefHp);
            temp.setAttDef_B(attDef_B);
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
    
    public EquipSkillEffect getEquipSkillEffectByIdLV(final int skillId, final int skillLv) {
        if (this.skillIdLvMap.get(skillId) == null) {
            return null;
        }
        return this.skillIdLvMap.get(skillId).get(skillLv);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.skillIdLvMap.clear();
    }
}
