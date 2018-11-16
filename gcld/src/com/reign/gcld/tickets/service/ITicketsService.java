package com.reign.gcld.tickets.service;

import com.reign.gcld.player.dto.*;

public interface ITicketsService
{
    byte[] getMarket(final PlayerDto p0);
    
    byte[] buy(final PlayerDto p0, final int p1, final int p2);
}
