package com.reign.framework.mongo.jdbc;

import org.logicalcobwebs.proxool.*;
import java.sql.*;

public class MongoDataSource extends ProxoolDataSource
{
    @Override
	public Connection getConnection() throws SQLException {
        return new MongoConnection(super.getConnection());
    }
    
    @Override
	public Connection getConnection(final String s, final String s1) throws SQLException {
        return new MongoConnection(super.getConnection(s, s1));
    }
}
