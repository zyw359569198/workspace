package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.battle.common.*;
import java.util.*;

@Component("QualifyingGroupCache")
public class QualifyingGroupCache extends AbstractCache<Integer, QualifyingGroup>
{
    private Logger logger;
    @Autowired
    private SDataLoader dataLoader;
    @Autowired
    private BattleDropFactory battleDropFactory;
    
    public QualifyingGroupCache() {
        this.logger = CommonLog.getLog(QualifyingLevelCache.class);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<QualifyingGroup> resultList = this.dataLoader.getModels((Class)QualifyingGroup.class);
        BattleDropAnd battleDrop = null;
        for (final QualifyingGroup temp : resultList) {
            battleDrop = this.battleDropFactory.getBattleDropAnd(temp.getRewardQualifying());
            if (battleDrop == null) {
                this.logger.error("qualifyingGroupCache init rewardQualifying fail in groupId:" + temp.getGroup());
                super.put((Object)temp.getGroup(), (Object)temp);
            }
            else {
                temp.setBattleRewardQualifying(battleDrop);
                battleDrop = this.battleDropFactory.getBattleDropAnd(temp.getRewardWin());
                if (battleDrop == null) {
                    this.logger.error("qualifyingGroupCache init rewardWin fail in groupId:" + temp.getGroup());
                    super.put((Object)temp.getGroup(), (Object)temp);
                }
                else {
                    temp.setBattleRewardWin(battleDrop);
                    battleDrop = this.battleDropFactory.getBattleDropAnd(temp.getRewardLose());
                    if (battleDrop == null) {
                        this.logger.error("qualifyingGroupCache init rewardLose fail in groupId:" + temp.getGroup());
                        super.put((Object)temp.getGroup(), (Object)temp);
                    }
                    else {
                        temp.setBattleRewardLose(battleDrop);
                        super.put((Object)temp.getGroup(), (Object)temp);
                    }
                }
            }
        }
    }
}
