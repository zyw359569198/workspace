package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("armsWeaponCache")
public class ArmsWeaponCache extends AbstractCache<Integer, ArmsWeapon>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<ArmsWeapon> list = this.dataLoader.getModels((Class)ArmsWeapon.class);
        for (final ArmsWeapon armsWeapon : list) {
            final Map<Integer, Integer> costMap = new HashMap<Integer, Integer>();
            final String[] strs = armsWeapon.getCost().split(";");
            String[] array;
            for (int length = (array = strs).length, i = 0; i < length; ++i) {
                final String str = array[i];
                final String[] values = str.split(",");
                if (values[0].trim().equals("copper")) {
                    costMap.put(1, Integer.valueOf(values[1]));
                }
                else if (values[0].trim().equals("lumber")) {
                    costMap.put(2, Integer.valueOf(values[1]));
                }
                else if (values[0].trim().equals("food")) {
                    costMap.put(3, Integer.valueOf(values[1]));
                }
                else if (values[0].trim().equals("iron")) {
                    costMap.put(4, Integer.valueOf(values[1]));
                }
            }
            armsWeapon.setCostMap(costMap);
            super.put((Object)armsWeapon.getId(), (Object)armsWeapon);
        }
    }
}
