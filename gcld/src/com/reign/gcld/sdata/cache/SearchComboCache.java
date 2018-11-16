package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.player.dto.*;

@Component("searchComboCache")
public class SearchComboCache extends AbstractCache<Integer, SearchCombo>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, List<SearchCombo>> posListMap;
    
    public SearchComboCache() {
        this.posListMap = new HashMap<Integer, List<SearchCombo>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<SearchCombo> list = this.dataLoader.getModels((Class)SearchCombo.class);
        for (final SearchCombo i : list) {
            final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(i.getReward());
            if (taskReward == null) {
                throw new RuntimeException("searchComboCache reward fail in id " + i.getId());
            }
            i.setTaskReward(taskReward);
            super.put((Object)i.getId(), (Object)i);
            List<SearchCombo> posList = this.posListMap.get(i.getPos());
            if (posList == null) {
                posList = new ArrayList<SearchCombo>();
                this.posListMap.put(i.getPos(), posList);
            }
            posList.add(i);
        }
    }
    
    public SearchCombo getByPos(final int pos, final PlayerDto playerDto) {
        final List<SearchCombo> posList = this.posListMap.get(pos);
        for (final SearchCombo sc : posList) {
            if (sc.getMaxLv() >= playerDto.playerLv && sc.getMinLv() <= playerDto.playerLv) {
                return sc;
            }
        }
        return null;
    }
    
    @Override
	public void clear() {
        super.clear();
        this.posListMap.clear();
    }
}
