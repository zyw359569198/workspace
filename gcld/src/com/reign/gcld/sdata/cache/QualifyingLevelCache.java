package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.util.*;
import com.reign.gcld.sdata.common.*;
import org.apache.commons.lang.*;
import java.util.*;

@Component("qualifyingLevelCache")
public class QualifyingLevelCache extends AbstractCache<Integer, QualifyingLevel>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, Tuple<Integer, Integer>> groupToScaleMap;
    
    public QualifyingLevelCache() {
        this.groupToScaleMap = new HashMap<Integer, Tuple<Integer, Integer>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<QualifyingLevel> resultList = this.dataLoader.getModels((Class)QualifyingLevel.class);
        int preGroup = resultList.get(0).getGroup();
        int prelevel = resultList.get(0).getRank();
        Tuple<Integer, Integer> tempTuple = new Tuple();
        tempTuple.left = prelevel;
        for (final QualifyingLevel temp : resultList) {
            if (temp.getGroup() != preGroup) {
                tempTuple.right = prelevel;
                this.groupToScaleMap.put(preGroup, tempTuple);
                tempTuple = new Tuple();
                tempTuple.left = temp.getRank();
            }
            preGroup = temp.getGroup();
            prelevel = temp.getRank();
            final ChiefNpc cn = new ChiefNpc();
            cn.setCheif(temp.getChief());
            final List<Integer> npcList = new ArrayList<Integer>();
            if (!StringUtils.isBlank(temp.getNpcs())) {
                String[] split;
                for (int length = (split = temp.getNpcs().split(";")).length, i = 0; i < length; ++i) {
                    final String str = split[i];
                    npcList.add(Integer.valueOf(str.trim()));
                }
            }
            cn.setNpcList(npcList);
            temp.setChiefNpc(cn);
            super.put((Object)temp.getRank(), (Object)temp);
        }
        tempTuple.right = prelevel;
        this.groupToScaleMap.put(preGroup, tempTuple);
    }
    
    public Tuple<Integer, Integer> getScale(final int group) {
        return this.groupToScaleMap.get(group);
    }
    
    public int getScaleSize() {
        return this.groupToScaleMap.size();
    }
    
    @Override
	public void clear() {
        super.clear();
        this.groupToScaleMap.clear();
    }
}
