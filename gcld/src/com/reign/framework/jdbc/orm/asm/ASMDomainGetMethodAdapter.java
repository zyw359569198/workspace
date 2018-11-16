package com.reign.framework.jdbc.orm.asm;

import java.util.*;
import org.objectweb.asm.*;

public class ASMDomainGetMethodAdapter extends MethodAdapter
{
    public static final Set<String[]> set;
    private String className;
    
    static {
        set = new HashSet<String[]>();
    }
    
    public ASMDomainGetMethodAdapter(final MethodVisitor mv, final String className) {
        super(mv);
        this.className = className;
    }
    
    @Override
	public void visitVarInsn(final int opcode, final int var) {
        super.visitVarInsn(opcode, var);
    }
    
    @Override
	public void visitCode() {
        super.visitCode();
    }
    
    @Override
	public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        super.visitFieldInsn(opcode, String.valueOf(this.className) + "$EnhanceByASM", name, desc);
    }
    
    @Override
	public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        super.visitMethodInsn(opcode, owner, name, desc);
        ASMDomainGetMethodAdapter.set.add(new String[] { owner, name, desc });
    }
}
