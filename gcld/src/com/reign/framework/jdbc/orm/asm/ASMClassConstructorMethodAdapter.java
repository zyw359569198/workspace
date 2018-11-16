package com.reign.framework.jdbc.orm.asm;

import org.objectweb.asm.*;

public class ASMClassConstructorMethodAdapter extends MethodAdapter
{
    private String className;
    
    public ASMClassConstructorMethodAdapter(final MethodVisitor mv, final String className) {
        super(mv);
        this.className = className;
    }
    
    @Override
	public void visitMethodInsn(final int opcode, String owner, final String name, final String desc) {
        if (opcode == 183 && name.equals("<init>")) {
            owner = this.className;
        }
        super.visitMethodInsn(opcode, owner, name, desc);
    }
}
