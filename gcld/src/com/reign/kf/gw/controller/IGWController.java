package com.reign.kf.gw.controller;

import org.jboss.netty.channel.*;
import com.reign.kf.comm.protocol.*;

public interface IGWController
{
    Response handle(final Channel p0, final Request p1, final int p2);
}
