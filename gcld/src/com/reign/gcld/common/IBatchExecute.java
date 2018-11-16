package com.reign.gcld.common;

import org.apache.ibatis.session.*;
import java.util.*;
import com.reign.framework.jdbc.*;

public interface IBatchExecute
{
    int batch(final SqlSession p0, final String p1, final List<List<Param>> p2);
}
