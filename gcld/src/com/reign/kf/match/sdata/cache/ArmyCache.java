package com.reign.kf.match.sdata.cache;

import com.reign.kf.match.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("armyCache")
public class ArmyCache extends AbstractCache<Integer, Army>
{
    @Autowired
    private SDataLoader dataLoader;
    private static ArmyCache armyCache;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<Army> resultList = this.dataLoader.getModels((Class)Army.class);
        for (final Army army : resultList) {
            final String[] effects = army.getEffect().split(";");
            army.setAtt(Integer.valueOf(effects[0].split("=")[1]));
            army.setDef(Integer.valueOf(effects[1].split("=")[1]));
            if (effects.length > 2) {
                army.setBd(Integer.valueOf(effects[2].split("=")[1]));
            }
            super.put((Object)army.getGeneralId(), (Object)army);
        }
        ArmyCache.armyCache = this;
    }
    
    @Override
	public void clear() {
        super.clear();
    }
    
    public static Army getArmyById(final int npcId) {
        return (Army)ArmyCache.armyCache.get((Object)npcId);
    }
}
