package com.reign.kf.gw.common.util;

import org.apache.commons.logging.*;
import com.reign.framework.netty.servlet.*;
import java.util.zip.*;
import com.reign.kf.comm.protocol.*;
import java.io.*;

public class MatchUtil
{
    private static Log log;
    
    static {
        MatchUtil.log = LogFactory.getLog(MatchUtil.class);
    }
    
    public static <T> T resolveStream(final Request request, final Class<T> clazz, final boolean decompression) {
        T t = null;
        try {
            final InputStream is = new ByteArrayInputStream(request.getContent());
            if (decompression) {
                final GZIPInputStream gzin = new GZIPInputStream(is);
                t = (T)Types.OBJECT_MAPPER.readValue((InputStream)gzin, (Class)clazz);
                is.close();
                gzin.close();
            }
            else {
                t = (T)Types.OBJECT_MAPPER.readValue(is, (Class)clazz);
                is.close();
            }
        }
        catch (IOException e) {
            MatchUtil.log.error("", e);
            throw new RuntimeException(e);
        }
        return t;
    }
}
