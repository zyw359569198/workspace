package com.reign.gcld.charge.service;

public interface IChargeItemService
{
    String getConfigByPlayer(final int p0);
    
    byte[] noDisturb(final int p0, final String[] p1, final int[] p2);
}
