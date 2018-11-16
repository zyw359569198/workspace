package com.reign.kf.match.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.match.domain.*;
import org.springframework.stereotype.*;

@Component("matchReportDao")
public class MatchReportDao extends BaseDao<MatchReport, Integer> implements IMatchReportDao
{
}
