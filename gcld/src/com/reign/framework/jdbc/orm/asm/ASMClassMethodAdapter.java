package com.reign.framework.jdbc.orm.asm;

import org.objectweb.asm.*;

public class ASMClassMethodAdapter extends MethodAdapter
{
    private String className;
    
    public ASMClassMethodAdapter(final MethodVisitor mv, final String className) {
        super(mv);
        this.className = className;
    }
    
    @Override
	public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        super.visitFieldInsn(opcode, this.className, name, desc);
    }
}
