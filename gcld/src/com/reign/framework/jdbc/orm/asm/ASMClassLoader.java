package com.reign.framework.jdbc.orm.asm;

public class ASMClassLoader extends ClassLoader
{
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        clazz = this.findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }
        try {
            clazz = super.loadClass(name);
        }
        catch (ClassNotFoundException ex) {}
        if (clazz != null) {
            return clazz;
        }
        throw new ClassNotFoundException();
    }
    
    public Class<?> loadClassFromBytes(final String name, final byte[] data) {
        return this.defineClass(name, data, 0, data.length);
    }
}
