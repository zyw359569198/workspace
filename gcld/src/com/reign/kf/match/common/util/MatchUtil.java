package com.reign.kf.match.common.util;

import org.apache.commons.logging.*;
import com.reign.framework.netty.servlet.*;
import com.reign.kf.comm.protocol.*;
import java.util.zip.*;
import java.io.*;
import org.codehaus.jackson.type.*;

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
    
    public static byte[] GZipByte(final byte[] bytes) {
        ByteArrayOutputStream bos = null;
        GZIPOutputStream out = null;
        try {
            bos = new ByteArrayOutputStream();
            out = new GZIPOutputStream(bos);
            out.write(bytes);
            out.finish();
            return bos.toByteArray();
        }
        catch (IOException ex) {}
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ex2) {}
            }
            if (bos != null) {
                try {
                    bos.close();
                }
                catch (IOException ex3) {}
            }
        }
        return null;
    }
    
    public static byte[] DeGZipByte(final byte[] bytes) {
        ByteArrayInputStream bis = null;
        GZIPInputStream in = null;
        ByteArrayOutputStream bos = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            in = new GZIPInputStream(bis);
            bos = new ByteArrayOutputStream();
            final byte[] buff = new byte[1024];
            int len = -1;
            while ((len = in.read(buff)) != -1) {
                bos.write(buff, 0, len);
            }
            return bos.toByteArray();
        }
        catch (IOException ex) {}
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex2) {}
            }
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException ex3) {}
            }
            if (bos != null) {
                try {
                    bos.close();
                }
                catch (IOException ex4) {}
            }
        }
        return null;
    }
    
    public static <T> T DeGZipByte(final byte[] bytes, final JavaType javaType) {
        ByteArrayInputStream bis = null;
        GZIPInputStream in = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            in = new GZIPInputStream(bis);
            return (T)Types.objectReader(javaType).readValue((InputStream)in);
        }
        catch (IOException ex) {}
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex2) {}
            }
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException ex3) {}
            }
        }
        return null;
    }
}
