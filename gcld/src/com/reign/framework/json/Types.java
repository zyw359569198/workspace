package com.reign.framework.json;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;
import java.io.*;
import com.alibaba.fastjson.*;
import org.codehaus.jackson.type.*;

public class Types
{
    public static final ObjectMapper OBJECT_MAPPER;
    
    static {
        OBJECT_MAPPER = new ObjectMapper();
    }
    
    public static String toString(final Object o) throws JsonGenerationException, JsonMappingException, IOException {
        if (o == null) {
            return "null";
        }
        return Types.OBJECT_MAPPER.writeValueAsString(o);
    }
    
    public static byte[] writeValueAsBytes(final Object obj) {
        return JSON.toJSONString(obj).getBytes();
    }
    
    public static <T> T readValue(final byte[] bytes, final Class<T> clazz) {
        return (T)JSON.parseObject(new String(bytes), (Class)clazz);
    }
    
    public static <T> T readValue(final byte[] bytes, final JavaType javaType) {
        try {
            return (T)Types.OBJECT_MAPPER.readValue(bytes, 0, bytes.length, javaType);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
