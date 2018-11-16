package com.reign.kfzb.control;

import java.util.*;
import com.reign.kf.comm.protocol.*;

public interface IKfzbMatchControl
{
    List<Response> handle(final RequestChunk p0);
}
