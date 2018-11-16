package com.reign.framework.hibernate.hql;

import javax.xml.bind.annotation.*;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hqls", propOrder = { "hql" })
public class Hqls
{
    protected List<Hql> hql;
    
    public List<Hql> getHql() {
        if (this.hql == null) {
            this.hql = new ArrayList<Hql>();
        }
        return this.hql;
    }
}
