package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;

@Component("loginRewardBaseCache")
public class LoginRewardBaseCache extends AbstractCache<Integer, LoginRewardBase>
{
    @Autowired
    private SDataLoader dataLoader;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<LoginRewardBase> list = this.dataLoader.getModels((Class)LoginRewardBase.class);
        for (final LoginRewardBase temp : list) {
            super.put((Object)temp.getId(), (Object)temp);
        }
    }
}
