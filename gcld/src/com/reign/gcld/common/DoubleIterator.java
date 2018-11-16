package com.reign.gcld.common;

import java.util.*;

public interface DoubleIterator<E> extends Iterator<E>
{
    boolean hasPrev();
    
    E prev();
}
