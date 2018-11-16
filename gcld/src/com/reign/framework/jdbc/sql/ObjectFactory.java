package com.reign.framework.jdbc.sql;

import javax.xml.namespace.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

@XmlRegistry
public class ObjectFactory
{
    private static final QName _Sqls_QNAME;
    
    static {
        _Sqls_QNAME = new QName("http://com.reign.framework.jdbc/sql", "sqls");
    }
    
    public Sql createSql() {
        return new Sql();
    }
    
    public Sqls createSqls() {
        return new Sqls();
    }
    
    @XmlElementDecl(namespace = "http://com.reign.framework.jdbc/sql", name = "sqls")
    public JAXBElement<Sqls> createSqls(final Sqls value) {
        return new JAXBElement<Sqls>(ObjectFactory._Sqls_QNAME, Sqls.class, null, value);
    }
}
