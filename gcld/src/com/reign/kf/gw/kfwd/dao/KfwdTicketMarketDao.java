package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdTicketMarketDao extends DirectBaseDao<KfwdTicketMarket, Integer> implements IKfwdTicketMarketDao
{
    @Override
    public List<KfwdTicketMarket> getAllInfo() {
        final String hql = "from KfwdTicketMarket";
        return (List<KfwdTicketMarket>)this.getResultByHQLAndParam(hql);
    }
}
