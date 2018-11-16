package com.reign.framework.hibernate.hql;

import javax.xml.namespace.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;

@XmlRegistry
public class ObjectFactory
{
    private static final QName _Hqls_QNAME;
    
    static {
        _Hqls_QNAME = new QName("http://com.reign.framework.hibernate/hql", "hqls");
    }
    
    public Hql createHql() {
        return new Hql();
    }
    
    public Hqls createHqls() {
        return new Hqls();
    }
    
    @XmlElementDecl(namespace = "http://com.reign.framework.hibernate/hql", name = "hqls")
    public JAXBElement<Hqls> createHqls(final Hqls value) {
        return new JAXBElement<Hqls>(ObjectFactory._Hqls_QNAME, Hqls.class, null, value);
    }
}
