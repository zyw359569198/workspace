package com.reign.framework.jdbc.orm.asm;

import com.reign.framework.jdbc.orm.*;
import org.objectweb.asm.*;
import java.io.*;

public class JdbcModelEnhancer
{
    private static ASMClassLoader loader;
    
    static {
        JdbcModelEnhancer.loader = new ASMClassLoader();
    }
    
    public static Class<?> enhance(final Class<?> clazz) {
        if (JdbcModel.class.isAssignableFrom(clazz)) {
            try {
                final Class<?> temp = JdbcModelEnhancer.loader.loadClass(String.valueOf(clazz.getName()) + "$EnhanceByASM");
                if (temp != null) {
                    return temp;
                }
            }
            catch (ClassNotFoundException ex) {}
            try {
                final ClassReader cr = new ClassReader(clazz.getName());
                final ClassWriter cw = new ClassWriter(1);
                final ASMClassAdapter adapter = new ASMClassAdapter(cw, clazz);
                cr.accept(adapter, 2);
                final byte[] data = cw.toByteArray();
                return JdbcModelEnhancer.loader.loadClassFromBytes(String.valueOf(clazz.getName()) + "$EnhanceByASM", data);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }
}
