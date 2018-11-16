package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.sdata.common.*;
import java.util.*;
import com.reign.gcld.activity.common.*;

@Component("armyCache")
public class ArmyCache extends AbstractCache<Integer, Army>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<NpcInfo>> worldNpcMap;
    
    public ArmyCache() {
        this.worldNpcMap = new HashMap<Integer, List<NpcInfo>>();
    }
    
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
            List<NpcInfo> list = null;
            if (this.worldNpcMap.containsKey(army.getGeneralLv())) {
                list = this.worldNpcMap.get(army.getGeneralLv());
            }
            else {
                list = new ArrayList<NpcInfo>();
                this.worldNpcMap.put(army.getGeneralLv(), list);
            }
            list.add(new NpcInfo(army.getAtt(), army.getDef(), army.getArmyHp(), army.getTroopHp()));
        }
        this.checkThisCache();
    }
    
    private void checkThisCache() {
        for (final Integer armyId : MiddleAutumnCache.checkMap.keySet()) {
            if (this.get((Object)armyId) == null) {
                throw new RuntimeException("ArmyCache init fail, ArmyCache is absent, id:" + armyId);
            }
        }
    }
    
    public List<NpcInfo> getWorldNpcInfo(final int generalLv) {
        return this.worldNpcMap.get(generalLv);
    }
    
    @Override
	public void clear() {
        super.clear();
        this.worldNpcMap.clear();
    }
}
