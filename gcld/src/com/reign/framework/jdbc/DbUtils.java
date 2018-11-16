package com.reign.framework.jdbc;

import org.apache.commons.logging.*;
import java.sql.*;
import javax.sql.*;
import org.springframework.jdbc.datasource.*;
import java.io.*;

public final class DbUtils
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog(DbUtils.class);
    }
    
    public static void close(final Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
    
    public static void close(final ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }
    
    public static void close(final Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }
    
    public static void closeQuietly(final Connection conn) {
        try {
            close(conn);
        }
        catch (SQLException e) {
            DbUtils.log.error("Could not close jdbc Connection", e);
        }
        catch (Throwable e2) {
            DbUtils.log.error("Unexpected exception on closing jdbc Connection", e2);
        }
    }
    
    public static void closeQuietly(final Statement stmt, final ResultSet rs) {
        try {
            closeQuietly(rs);
        }
        finally {
            closeQuietly(stmt);
        }
        closeQuietly(stmt);
    }
    
    public static void closeQuietly(final Connection conn, final Statement stmt, final ResultSet rs) {
        try {
            closeQuietly(rs);
        }
        finally {
            try {
                closeQuietly(stmt);
            }
            finally {
                closeQuietly(conn);
            }
            closeQuietly(conn);
        }
        try {
            closeQuietly(stmt);
        }
        finally {
            closeQuietly(conn);
        }
        closeQuietly(conn);
    }
    
    public static void closeQuietly(final Connection conn, final Statement stmt, final ResultSet rs, final DataSource dataSource) {
        try {
            closeQuietly(rs);
        }
        finally {
            try {
                closeQuietly(stmt);
            }
            finally {
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        try {
            closeQuietly(stmt);
        }
        finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
    
    public static void closeQuietly(final ResultSet rs) {
        try {
            close(rs);
        }
        catch (SQLException e) {
            DbUtils.log.error("Could not close jdbc ResultSet", e);
        }
        catch (Throwable e2) {
            DbUtils.log.error("Unexpected exception on closing jdbc ResultSet", e2);
        }
    }
    
    public static void closeQuietly(final Statement stmt) {
        try {
            close(stmt);
        }
        catch (SQLException e) {
            DbUtils.log.error("Could not close jdbc Statement", e);
        }
        catch (Throwable e2) {
            DbUtils.log.error("Unexpected exception on closing jdbc Statement", e2);
        }
    }
    
    public static void commitAndClose(final Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.commit();
            }
            finally {
                conn.close();
            }
            conn.close();
        }
    }
    
    public static void commitAndCloseQuietly(final Connection conn) {
        try {
            commitAndClose(conn);
        }
        catch (SQLException e) {
            DbUtils.log.error("commit jdbc error", e);
        }
        catch (Throwable e2) {
            DbUtils.log.error("Unexpected exception on commit jdbc", e2);
        }
    }
    
    public static boolean loadDriver(final String driverClassName) {
        try {
            Class.forName(driverClassName).newInstance();
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        catch (IllegalAccessException e2) {
            return true;
        }
        catch (InstantiationException e3) {
            return false;
        }
        catch (Throwable e4) {
            return false;
        }
    }
    
    public static void printStackTrace(final SQLException e) {
        printStackTrace(e, new PrintWriter(System.err));
    }
    
    public static void printStackTrace(final SQLException e, final PrintWriter pw) {
        SQLException next = e;
        while (next != null) {
            next.printStackTrace(pw);
            next = next.getNextException();
            if (next != null) {
                pw.println("Next SQLException:");
            }
        }
    }
    
    public static void printWarnings(final Connection conn) {
        printWarnings(conn, new PrintWriter(System.err));
    }
    
    public static void printWarnings(final Connection conn, final PrintWriter pw) {
        if (conn != null) {
            try {
                printStackTrace(conn.getWarnings(), pw);
            }
            catch (SQLException e) {
                printStackTrace(e, pw);
            }
        }
    }
    
    public static void rollback(final Connection conn) throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }
    
    public static void rollbackAndClose(final Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.rollback();
            }
            finally {
                conn.close();
            }
            conn.close();
        }
    }
    
    public static void rollbackAndCloseQuietly(final Connection conn) {
        try {
            rollbackAndClose(conn);
        }
        catch (SQLException e) {
            DbUtils.log.error("rollback jdbc error", e);
        }
        catch (Throwable e2) {
            DbUtils.log.error("Unexpected exception on rollback jdbc", e2);
        }
    }
}
