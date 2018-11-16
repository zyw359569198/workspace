package com.reign.framework.mybatis;

import org.mybatis.spring.support.*;

public class BaseDao<T extends IModel> extends SqlSessionDaoSupport implements IBaseDao<T>
{
}
