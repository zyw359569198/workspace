package com.reign.framework.jdbc;

import org.apache.commons.logging.*;
import java.math.*;
import java.util.*;
import java.sql.*;
import java.io.*;

public class SqlBuilder
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.framework.jdbc");
    }
    
    public void buildParameters(final PreparedStatement pstmt, final List<Param> params) throws SQLException {
        int index = 1;
        StringBuilder builder = null;
        if (SqlBuilder.log.isDebugEnabled()) {
            builder = new StringBuilder(params.size() * 10);
        }
        for (final Param param : params) {
            if (param == null) {
                pstmt.setObject(index, null);
            }
            else {
                this.fillParameter(index, pstmt, param);
            }
            if (SqlBuilder.log.isDebugEnabled()) {
                if (index != 1) {
                    builder.append(",").append(index).append(":").append((param == null) ? null : param.obj);
                }
                else {
                    builder.append("Parameters:[").append(index).append(":").append((param == null) ? null : param.obj);
                }
            }
            ++index;
        }
        if (SqlBuilder.log.isDebugEnabled() && builder.length() > 0) {
            builder.append("]");
            SqlBuilder.log.debug(builder.toString());
        }
    }
    
    private void fillParameter(final int index, final PreparedStatement pstmt, final Param param) throws SQLException {
        if (param.obj == null) {
            pstmt.setObject(index, param.obj);
            return;
        }
        switch (param.type) {
            case Object: {
                pstmt.setObject(index, param.obj);
                break;
            }
            case Like: {
                pstmt.setString(index, param.obj + "%");
                break;
            }
            case BigDecimal: {
                pstmt.setBigDecimal(index, (BigDecimal)param.obj);
                break;
            }
            case Blob: {
                pstmt.setBlob(index, (Blob)param.obj);
                break;
            }
            case Byte: {
                pstmt.setByte(index, (byte)param.obj);
                break;
            }
            case Bytes: {
                pstmt.setBytes(index, (byte[])param.obj);
                break;
            }
            case Clob: {
                pstmt.setClob(index, (Clob)param.obj);
                break;
            }
            case Date: {
                pstmt.setTimestamp(index, new Timestamp(((Date)param.obj).getTime() / 1000L * 1000L));
                break;
            }
            case SqlDate: {
                pstmt.setDate(index, (java.sql.Date)param.obj);
                break;
            }
            case Time: {
                pstmt.setTime(index, (Time)param.obj);
                break;
            }
            case Timestamp: {
                pstmt.setTimestamp(index, (Timestamp)param.obj);
                break;
            }
            case Double: {
                pstmt.setDouble(index, (double)param.obj);
                break;
            }
            case Float: {
                pstmt.setFloat(index, (float)param.obj);
                break;
            }
            case Int: {
                pstmt.setInt(index, (int)param.obj);
                break;
            }
            case Long: {
                pstmt.setLong(index, (long)param.obj);
                break;
            }
            case NClob: {
                pstmt.setNClob(index, (NClob)param.obj);
                break;
            }
            case String: {
                pstmt.setString(index, (String)param.obj);
                break;
            }
            case Bool: {
                pstmt.setBoolean(index, (boolean)param.obj);
                break;
            }
            case Out: {
                break;
            }
            default: {
                throw new RuntimeException("unknow type [type: " + param.type + "]");
            }
        }
    }
}
