package com.reign.framework.jdbc.handlers;

import com.reign.framework.jdbc.*;
import java.sql.*;

public class ArrayListHandler extends AbstractListHandler<Object[]>
{
    private final RowProcessor convert;
    
    public ArrayListHandler() {
        this(ArrayHandler.ROW_PROCESSOR);
    }
    
    public ArrayListHandler(final RowProcessor convert) {
        this.convert = convert;
    }
    
    @Override
    public Object[] handleRow(final ResultSet rs) throws SQLException {
        return this.convert.toArray(rs);
    }
}
