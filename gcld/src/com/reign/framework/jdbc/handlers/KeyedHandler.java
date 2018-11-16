package com.reign.framework.jdbc.handlers;

import java.util.*;
import com.reign.framework.jdbc.*;
import java.sql.*;

public class KeyedHandler extends AbstractKeyedHandler<Object, Map<String, Object>>
{
    protected final RowProcessor convert;
    protected final int columnIndex;
    protected final String columnName;
    
    public KeyedHandler() {
        this(ArrayHandler.ROW_PROCESSOR, 1, null);
    }
    
    public KeyedHandler(final RowProcessor convert) {
        this(convert, 1, null);
    }
    
    public KeyedHandler(final int columnIndex) {
        this(ArrayHandler.ROW_PROCESSOR, columnIndex, null);
    }
    
    public KeyedHandler(final String columnName) {
        this(ArrayHandler.ROW_PROCESSOR, 1, columnName);
    }
    
    private KeyedHandler(final RowProcessor convert, final int columnIndex, final String columnName) {
        this.convert = convert;
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }
    
    @Override
    protected Object createKey(final ResultSet rs) throws SQLException {
        return (this.columnName == null) ? rs.getObject(this.columnIndex) : rs.getObject(this.columnName);
    }
    
    @Override
    protected Map<String, Object> createRow(final ResultSet rs) throws SQLException {
        return this.convert.toMap(rs);
    }
}
