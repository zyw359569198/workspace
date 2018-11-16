package com.reign.kf.match.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.match.domain.*;
import org.springframework.stereotype.*;

@Component("matchPlayerGeneralDao")
public class MatchPlayerGeneralDao extends BaseDao<MatchPlayerGeneral, Integer> implements IMatchPlayerGeneralDao
{
}
