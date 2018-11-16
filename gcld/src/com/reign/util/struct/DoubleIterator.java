package com.reign.util.struct;

import java.util.*;

public interface DoubleIterator<E> extends Iterator<E>
{
    boolean hasPrev();
    
    E prev();
}
