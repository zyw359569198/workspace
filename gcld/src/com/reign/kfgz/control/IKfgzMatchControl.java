package com.reign.kfgz.control;

import java.util.*;
import com.reign.kf.comm.protocol.*;

public interface IKfgzMatchControl
{
    List<Response> handle(final RequestChunk p0);
}
