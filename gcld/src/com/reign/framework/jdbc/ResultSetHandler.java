package com.reign.framework.jdbc;

import java.sql.*;

public interface ResultSetHandler<T>
{
    T handle(final ResultSet p0) throws SQLException;
}
