package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("armsGemCache")
public class ArmsGemCache extends AbstractCache<Integer, ArmsGem>
{
    @Autowired
    private SDataLoader dataLoader;
    public static int maxId;
    public static Map<Integer, Integer> skillNumLvMap;
    public static Map<Integer, Integer> skillNumLvReverseMap;
    public static Map<Integer, Integer> jsLvNumMap;
    public static int JS_MAX_LV;
    public static int JS_POINT_ID;
    
    static {
        ArmsGemCache.maxId = 0;
        ArmsGemCache.skillNumLvMap = new HashMap<Integer, Integer>();
        ArmsGemCache.skillNumLvReverseMap = new HashMap<Integer, Integer>();
        ArmsGemCache.jsLvNumMap = new HashMap<Integer, Integer>();
        ArmsGemCache.JS_MAX_LV = 0;
        ArmsGemCache.JS_POINT_ID = 1000;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<ArmsGem> list = this.dataLoader.getModels((Class)ArmsGem.class);
        final Map<Integer, List<Integer>> skillNumLvListMap = new HashMap<Integer, List<Integer>>();
        for (final ArmsGem temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
            if (temp.getId() <= 15) {
                if (temp.getId() <= ArmsGemCache.maxId) {
                    continue;
                }
                ArmsGemCache.maxId = temp.getId();
            }
            else {
                if (temp.getId() <= ArmsGemCache.JS_POINT_ID) {
                    continue;
                }
                List<Integer> lvList = skillNumLvListMap.get(temp.getSkillNum());
                if (lvList == null) {
                    lvList = new ArrayList<Integer>();
                    skillNumLvListMap.put(temp.getSkillNum(), lvList);
                }
                lvList.add(temp.getGemLv());
                ArmsGemCache.jsLvNumMap.put(temp.getGemLv(), temp.getUpgradeGem1());
                if (temp.getGemLv() <= ArmsGemCache.JS_MAX_LV) {
                    continue;
                }
                ArmsGemCache.JS_MAX_LV = temp.getGemLv();
            }
        }
        for (final Map.Entry<Integer, List<Integer>> entry : skillNumLvListMap.entrySet()) {
            final int skillNum = entry.getKey();
            final List<Integer> lvList2 = entry.getValue();
            int lv = Integer.MAX_VALUE;
            for (final Integer temp2 : lvList2) {
                if (temp2 < lv) {
                    lv = temp2;
                }
            }
            ArmsGemCache.skillNumLvMap.put(skillNum, lv);
            ArmsGemCache.skillNumLvReverseMap.put(lv, skillNum);
        }
    }
    
    @Override
	public void clear() {
        super.clear();
        ArmsGemCache.maxId = 0;
        ArmsGemCache.skillNumLvMap.clear();
        ArmsGemCache.skillNumLvReverseMap.clear();
        ArmsGemCache.jsLvNumMap.clear();
        ArmsGemCache.JS_MAX_LV = 0;
    }
}
