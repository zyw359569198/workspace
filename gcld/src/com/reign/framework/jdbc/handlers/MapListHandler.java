package com.reign.framework.jdbc.handlers;

import java.util.*;
import com.reign.framework.jdbc.*;
import java.sql.*;

public class MapListHandler extends AbstractListHandler<Map<String, Object>>
{
    private final RowProcessor convert;
    
    public MapListHandler() {
        this(ArrayHandler.ROW_PROCESSOR);
    }
    
    public MapListHandler(final RowProcessor convert) {
        this.convert = convert;
    }
    
    @Override
    public Map<String, Object> handleRow(final ResultSet rs) throws SQLException {
        return this.convert.toMap(rs);
    }
}
