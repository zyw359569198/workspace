package com.reign.gcld.store.common;

import java.util.*;
import com.reign.gcld.store.domain.*;

public class EquipComparator implements Comparator<StoreHouse>
{
    @Override
    public int compare(final StoreHouse o1, final StoreHouse o2) {
        final Integer ow1 = o1.getOwner();
        final int p1 = (ow1 == null) ? 0 : ow1;
        final Integer ow2 = o2.getOwner();
        final int p2 = (ow2 == null) ? 0 : ow2;
        if (p1 >= p2) {
            return 0;
        }
        if (o1.getQuality() >= o2.getQuality()) {
            return 0;
        }
        return 1;
    }
}
