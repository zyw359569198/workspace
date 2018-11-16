package com.reign.kf.match.controller;

import java.util.*;
import com.reign.kf.comm.protocol.*;

public interface IMatchController
{
    List<Response> handle(final RequestChunk p0);
}
