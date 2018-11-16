package com.reign.kfwd.control;

import java.util.*;
import com.reign.kf.comm.protocol.*;

public interface IKfwdMatchControl
{
    List<Response> handle(final RequestChunk p0);
}
