package com.reign.util.struct.charts;

public interface IChartsItemCondition<T extends ISetSeqable<T>>
{
    boolean isPass(final T p0);
}
