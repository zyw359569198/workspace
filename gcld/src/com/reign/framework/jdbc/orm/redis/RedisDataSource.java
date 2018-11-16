package com.reign.framework.jdbc.orm.redis;

import org.logicalcobwebs.proxool.*;
import java.sql.*;

public class RedisDataSource extends ProxoolDataSource
{
    @Override
	public Connection getConnection() throws SQLException {
        return new RedisConnection(super.getConnection());
    }
    
    @Override
	public Connection getConnection(final String s, final String s1) throws SQLException {
        return new RedisConnection(super.getConnection(s, s1));
    }
}
