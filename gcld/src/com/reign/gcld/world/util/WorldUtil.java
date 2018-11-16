package com.reign.gcld.world.util;

import com.reign.gcld.common.*;
import java.util.*;

public final class WorldUtil
{
    public static boolean isWholePointKill() {
        final Calendar c = Calendar.getInstance();
        final int hour = c.get(11);
        if ((hour >= 0 && hour <= 7) || 10 == hour || 11 == hour || 15 == hour || 16 == hour || 20 == hour || 21 == hour) {
            return false;
        }
        final String startServer = Configuration.getProperty("gcld.server.time");
        final long start = Long.parseLong(startServer);
        c.setTime(new Date(start));
        c.set(6, c.get(6) + 6);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        return c.getTime().before(new Date());
    }
}
