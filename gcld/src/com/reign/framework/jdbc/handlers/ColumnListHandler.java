package com.reign.framework.jdbc.handlers;

import java.sql.*;

public class ColumnListHandler extends AbstractListHandler<Object>
{
    private final int columnIndex;
    private final String columnName;
    
    public ColumnListHandler() {
        this(1, null);
    }
    
    public ColumnListHandler(final int columnIndex) {
        this(columnIndex, null);
    }
    
    public ColumnListHandler(final String columnName) {
        this(1, columnName);
    }
    
    private ColumnListHandler(final int columnIndex, final String columnName) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }
    
    @Override
    public Object handleRow(final ResultSet rs) throws SQLException {
        if (this.columnName == null) {
            return rs.getObject(this.columnIndex);
        }
        return rs.getObject(this.columnName);
    }
}
