package com.reign.gcld.gm.controller;

import com.reign.gcld.player.dto.*;
import com.reign.gcld.gm.common.*;
import com.reign.framework.netty.servlet.*;

public interface IGMController
{
    byte[] handle(final PlayerDto p0, final Command p1, final Request p2, final String... p3);
}
