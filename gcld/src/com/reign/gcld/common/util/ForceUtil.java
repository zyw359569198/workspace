package com.reign.gcld.common.util;

public class ForceUtil
{
    public static boolean isPlayerForce(final int forceId) {
        return forceId >= 1 && forceId <= 3;
    }
}
