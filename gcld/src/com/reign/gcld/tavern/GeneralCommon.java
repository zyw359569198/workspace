package com.reign.gcld.tavern;

public class GeneralCommon
{
    public static Integer getShowAttribute(final Integer baseAttri, final Integer addAttri) {
        final int base = (baseAttri == null) ? 0 : baseAttri;
        final int add = (addAttri == null) ? 0 : addAttri;
        return base + add;
    }
}
