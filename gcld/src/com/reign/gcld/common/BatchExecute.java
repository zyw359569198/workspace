package com.reign.gcld.common;

import org.springframework.stereotype.*;
import com.reign.gcld.common.log.*;
import org.apache.ibatis.session.*;
import org.mybatis.spring.*;
import java.math.*;
import java.util.*;
import java.sql.*;
import com.reign.framework.jdbc.*;

@Component("batchExecute")
public class BatchExecute implements IBatchExecute
{
    private static final Logger log;
    private int fetchsize;
    private int maxRows;
    private int exeSize;
    
    static {
        log = CommonLog.getLog(BatchExecute.class);
    }
    
    public BatchExecute() {
        this.fetchsize = 200;
        this.maxRows = 0;
        this.exeSize = 200;
    }
    
    @Override
    public int batch(final SqlSession sqlSession, final String sql, final List<List<Param>> paramsList) {
        PreparedStatement pstmt = null;
        SqlSessionTemplate st = null;
        Connection conn = null;
        boolean commiteSucc = false;
        try {
            int size = 0;
            st = (SqlSessionTemplate)sqlSession;
            conn = SqlSessionUtils.getSqlSession(st.getSqlSessionFactory(), st.getExecutorType(), st.getPersistenceExceptionTranslator()).getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setFetchSize(this.fetchsize);
            pstmt.setMaxRows(this.maxRows);
            for (final List<Param> params : paramsList) {
                buildParameters(pstmt, params);
                pstmt.addBatch();
                if (++size == this.exeSize) {
                    this.doExecuteBatch(pstmt, this.exeSize);
                    size = 0;
                }
            }
            if (size > 0) {
                this.doExecuteBatch(pstmt, size);
            }
            conn.commit();
            st.clearCache();
            commiteSucc = true;
        }
        catch (SQLException e) {
            BatchExecute.log.error("SQLException:" + sql, e);
        }
        catch (Exception e2) {
            BatchExecute.log.error("Exception:" + sql, e2);
        }
        finally {
            try {
                pstmt.close();
            }
            catch (Exception e3) {
                BatchExecute.log.error("pstmt.close():", e3);
            }
            try {
                conn.close();
            }
            catch (Exception e3) {
                BatchExecute.log.error("conn.close():", e3);
            }
            if (!commiteSucc) {
                try {
                    conn.rollback();
                }
                catch (SQLException e4) {
                    BatchExecute.log.error("conn.rollback():" + sql, e4);
                }
            }
        }
        try {
            pstmt.close();
        }
        catch (Exception e3) {
            BatchExecute.log.error("pstmt.close():", e3);
        }
        try {
            conn.close();
        }
        catch (Exception e3) {
            BatchExecute.log.error("conn.close():", e3);
        }
        if (!commiteSucc) {
            try {
                conn.rollback();
            }
            catch (SQLException e4) {
                BatchExecute.log.error("conn.rollback():" + sql, e4);
            }
        }
        return 1;
    }
    
    private void doExecuteBatch(final Statement stmt, final int batchSize) throws SQLException {
        final int[] result = stmt.executeBatch();
        if (batchSize != result.length) {
            throw new RuntimeException("batch error, excepted batch result len: " + batchSize + ", but jdbc return len: " + result.length);
        }
        int[] array;
        for (int length = (array = result).length, j = 0; j < length; ++j) {
            final int i = array[j];
            if (i == -3) {
                throw new RuntimeException("batch update failed: " + i);
            }
        }
    }
    
    public static void buildParameters(final PreparedStatement pstmt, final List<Param> params) throws SQLException {
        int index = 1;
        for (final Param param : params) {
            if (param == null) {
                pstmt.setObject(index, null);
            }
            else {
                fillParameter(index, pstmt, param);
            }
            ++index;
        }
    }
    
    private static void fillParameter(final int index, final PreparedStatement pstmt, final Param param) throws SQLException {
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
                pstmt.setTimestamp(index, new Timestamp(((Date)param.obj).getTime()));
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
