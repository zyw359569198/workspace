package com.reign.kf.gw.common.web;

import com.reign.framework.netty.mvc.result.*;
import com.reign.kf.comm.protocol.*;

public class GWResult implements Result<Response>
{
    private Response result;
    
    public GWResult(final Response result) {
        this.result = result;
    }
    
    @Override
	public Response getResult() {
        return this.result;
    }
    
    @Override
	public String getViewName() {
        return "gwview";
    }
}
