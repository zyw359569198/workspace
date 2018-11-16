package com.reign.kf.comm.transfer;

import java.util.*;
import com.reign.kf.comm.protocol.*;

public interface IRequestMergeStrategy
{
    List<Request> merge(final List<Request> p0);
}
