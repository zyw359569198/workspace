package com.reign.kf.gw.common.web;

import com.reign.framework.netty.mvc.view.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.exception.*;
import com.reign.framework.common.*;
import com.reign.kf.comm.protocol.*;
import java.util.zip.*;
import java.io.*;
import org.jboss.netty.buffer.*;

public class GWView extends DefaultView
{
    private boolean compress;
    
    @Override
	public void setCompress(final boolean compress) {
        this.compress = compress;
    }
    
    @Override
	public boolean compress() {
        return this.compress;
    }
    
    public void doRender(final Result<?> result, final Request request, final Response response) throws Exception {
        if (result == null || !(result instanceof GWResult)) {
            throw new NotMatchResultException("un match result type, except ", GWResult.class);
        }
        final GWResult gwresult = (GWResult)result;
        if (ServerProtocol.TCP.equals((Object)response.getProtocol())) {
            response.write((Object)this.wrapper(gwresult.getResult()));
            return;
        }
        throw new RuntimeException("unsupport protocol " + response.getProtocol());
    }
    
    public void prepareRender(final Response response) {
    }
    
    private ChannelBuffer wrapper(final com.reign.kf.comm.protocol.Response response) throws Exception {
        byte[] bodyBytes = Types.OBJECT_MAPPER.writeValueAsBytes((Object)response);
        if (this.compress) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final GZIPOutputStream dis = new GZIPOutputStream(out);
            try {
                dis.write(bodyBytes);
                dis.finish();
                dis.close();
                bodyBytes = out.toByteArray();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (dis != null) {
                    try {
                        dis.close();
                    }
                    catch (IOException ex) {}
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                }
                catch (IOException ex2) {}
            }
        }
        final int dataLen = bodyBytes.length;
        final ChannelBuffer buffer = ChannelBuffers.buffer(dataLen + 4);
        buffer.writeInt(dataLen);
        buffer.writeBytes(bodyBytes);
        return buffer;
    }
}
