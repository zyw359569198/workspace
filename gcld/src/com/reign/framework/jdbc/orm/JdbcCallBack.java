package com.reign.framework.jdbc.orm;

import com.reign.framework.jdbc.orm.session.*;

public interface JdbcCallBack<T>
{
    T doInJdbcSession(final JdbcSession p0);
}
