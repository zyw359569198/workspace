package com.reign.kf.gw.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.gw.domain.*;
import org.springframework.stereotype.*;

@Component("auctionInfoDao")
public class AuctionInfoDao extends BaseDao<AuctionInfo, Integer> implements IAuctionInfoDao
{
}
