package com.reign.kf.comm.transfer;

import java.util.*;
import com.reign.kf.comm.protocol.*;

public interface IConnection
{
    void send(final Request p0);
    
    void send(final Request p0, final RequestHandler p1);
    
    void sendSync(final Request p0);
    
    void send(final List<Request> p0);
    
    void sendSync(final List<Request> p0);
    
    void registerHandler(final int p0, final ResponseHandler p1);
    
    void addRequestMergeStrategy(final IRequestMergeStrategy p0);
}
