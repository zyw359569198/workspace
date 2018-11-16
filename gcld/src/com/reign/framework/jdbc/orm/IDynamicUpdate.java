package com.reign.framework.jdbc.orm;

import com.reign.util.*;
import java.util.*;
import com.reign.framework.jdbc.*;

public interface IDynamicUpdate
{
    Tuple<String, List<Param>> dynamicUpdateSQL(final JdbcModel p0);
}
