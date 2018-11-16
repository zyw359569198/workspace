package com.reign.framework.mongo.lang;

public class Or extends Query
{
    public Or add(final Where... wheres) {
        for (final Where where : wheres) {
            super.add(where);
        }
        return this;
    }
}
