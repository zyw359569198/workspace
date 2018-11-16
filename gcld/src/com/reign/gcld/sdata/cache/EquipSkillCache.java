package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("equipSkillCache")
public class EquipSkillCache extends AbstractCache<Integer, EquipSkill>
{
    @Autowired
    private SDataLoader dataLoader;
    private static ArrayList<String> names;
    public int SIZE;
    
    static {
        EquipSkillCache.names = new ArrayList<String>();
    }
    
    public EquipSkillCache() {
        this.SIZE = 0;
    }
    
    public static List<String> getNames() {
        return EquipSkillCache.names;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<EquipSkill> equipSkills = this.dataLoader.getModels((Class)EquipSkill.class);
        for (final EquipSkill es : equipSkills) {
            super.put((Object)es.getId(), (Object)es);
            EquipSkillCache.names.add(es.getName());
        }
        this.SIZE = this.getModels().size();
    }
    
    public void setDataLoader(final SDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    public SDataLoader getDataLoader() {
        return this.dataLoader;
    }
    
    public List<EquipSkill> getSkillByType(final int intValue) {
        final List<EquipSkill> es = new ArrayList<EquipSkill>();
        for (final EquipSkill e : this.getModels()) {
            if (e.getSkillType() == intValue) {
                es.add(e);
            }
        }
        return es;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.SIZE = 0;
    }
}
