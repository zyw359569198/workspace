package com.reign.framework.jdbc.sql;

import javax.xml.bind.annotation.*;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sqls", propOrder = { "sql" })
public class Sqls
{
    protected List<Sql> sql;
    
    public List<Sql> getSql() {
        if (this.sql == null) {
            this.sql = new ArrayList<Sql>();
        }
        return this.sql;
    }
}
