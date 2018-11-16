package com.reign.framework.jdbc.orm.asm;

import com.reign.framework.jdbc.orm.asm.util.*;
import com.reign.framework.jdbc.orm.*;
import com.reign.util.*;
import com.reign.framework.jdbc.*;
import org.objectweb.asm.*;
import java.util.*;

public class ASMClassAdapter extends ClassAdapter
{
    private String enhanceSuperName;
    private String enhanceName;
    private Class<?> clazz;
    private NameStrategy strategy;
    public Set<String[]> fieldSet;
    
    public ASMClassAdapter(final ClassVisitor cv, final Class<?> clazz) {
        super(cv);
        this.strategy = new DefaultNameStrategy();
        this.fieldSet = new LinkedHashSet<String[]>();
        this.clazz = clazz;
    }
    
    @Override
	public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.enhanceName = String.valueOf(name) + "$EnhanceByASM";
        this.enhanceSuperName = name;
        super.visit(version, access, this.enhanceName, signature, this.enhanceSuperName, new String[] { ASMUtil.getClassName(IDynamicUpdate.class) });
    }
    
    @Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (name.equals("<init>")) {
            MethodVisitor wrapMV;
            final MethodVisitor mv = wrapMV = this.cv.visitMethod(access, name, desc, signature, exceptions);
            wrapMV = new ASMClassConstructorMethodAdapter(mv, this.enhanceSuperName);
            return wrapMV;
        }
        if (name.startsWith("get")) {
            this.fieldSet.add(new String[] { this.enhanceName, name, desc, this.getFieldName(name), this.getFieldType(desc) });
            return null;
        }
        if (name.startsWith("set")) {
            return null;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    @Override
	public FieldVisitor visitField(final int access, final String owner, final String desc, final String signature, final Object obj) {
        if (access == 8) {
            return super.visitField(access, owner, desc, signature, obj);
        }
        return null;
    }
    
    private String getFieldType(final String desc) {
        if (desc.indexOf("L") != -1) {
            return desc.substring(desc.indexOf("L") + 1, desc.length() - 1);
        }
        return desc;
    }
    
    private String getFieldName(final String name) {
        if (name.startsWith("get")) {
            return name.substring(3);
        }
        if (name.startsWith("is")) {
            return name.substring(2);
        }
        return name;
    }
    
    @Override
	public void visitEnd() {
        this.addMethodDynamicUpdateSQL();
        super.visitEnd();
    }
    
    private void addMethodDynamicUpdateSQL() {
        final MethodVisitor mv = this.cv.visitMethod(1, "dynamicUpdateSQL", "(" + ASMUtil.getDesc(JdbcModel.class, true) + ")" + ASMUtil.getDesc(Tuple.class, true), "(" + ASMUtil.getDesc(JdbcModel.class, true) + ")" + ASMUtil.getSignature(Tuple.class, new Class[][] { { String.class }, { List.class, Param.class } }), (String[])null);
        mv.visitCode();
        mv.visitTypeInsn(187, ASMUtil.getClassName(StringBuilder.class));
        mv.visitInsn(89);
        mv.visitMethodInsn(183, ASMUtil.getClassName(StringBuilder.class), "<init>", "()V");
        mv.visitVarInsn(58, 2);
        mv.visitInsn(3);
        mv.visitVarInsn(54, 3);
        mv.visitInsn(4);
        mv.visitVarInsn(54, 4);
        mv.visitTypeInsn(187, ASMUtil.getClassName(ArrayList.class));
        mv.visitInsn(89);
        mv.visitMethodInsn(183, ASMUtil.getClassName(ArrayList.class), "<init>", "()V");
        mv.visitVarInsn(58, 5);
        mv.visitInsn(1);
        mv.visitVarInsn(58, 6);
        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(193, ASMUtil.getClassName(this.clazz));
        final Label l0 = new Label();
        mv.visitJumpInsn(153, l0);
        final Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(192, ASMUtil.getClassName(this.clazz));
        mv.visitVarInsn(58, 6);
        mv.visitLabel(l0);
        mv.visitFrame(1, 1, new Object[] { ASMUtil.getClassName(this.clazz) }, 0, (Object[])null);
        for (final String[] name : this.fieldSet) {
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(182, name[0], name[1], name[2]);
            mv.visitVarInsn(25, 6);
            mv.visitMethodInsn(182, this.enhanceSuperName, name[1], name[2]);
            final Label l3 = new Label();
            if (name[2].equals("()I") || name[2].equals("()S") || name[2].equals("()F") || name[2].equals("()J") || name[2].equals("()D")) {
                mv.visitJumpInsn(159, l3);
            }
            else {
                mv.visitMethodInsn(182, name[4], "equals", "(Ljava/lang/Object;)Z");
                mv.visitJumpInsn(154, l3);
            }
            mv.visitVarInsn(21, 3);
            final Label l4 = new Label();
            mv.visitJumpInsn(154, l4);
            final Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(25, 2);
            mv.visitLdcInsn("UPDATE {0} SET ");
            mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitInsn(87);
            mv.visitInsn(4);
            mv.visitVarInsn(54, 3);
            mv.visitLabel(l4);
            mv.visitVarInsn(21, 4);
            mv.visitInsn(4);
            final Label l6 = new Label();
            mv.visitJumpInsn(159, l6);
            final Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(25, 2);
            mv.visitLdcInsn(" AND ");
            mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitInsn(87);
            mv.visitLabel(l6);
            mv.visitVarInsn(25, 2);
            mv.visitLdcInsn(String.valueOf(this.strategy.propertyNameToColumnName(name[3])) + " = ? ");
            mv.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitInsn(87);
            mv.visitTypeInsn(187, ASMUtil.getClassName(Param.class));
            mv.visitInsn(89);
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(182, name[0], name[1], name[2]);
            if (name[2].equals("()I") || name[2].equals("()S")) {
                mv.visitMethodInsn(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            }
            else if (name[2].equals("()F")) {
                mv.visitMethodInsn(184, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            }
            else if (name[2].equals("()J")) {
                mv.visitMethodInsn(184, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            }
            else if (name[2].equals("()D")) {
                mv.visitMethodInsn(184, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            }
            mv.visitMethodInsn(183, ASMUtil.getClassName(Param.class), "<init>", "(Ljava/lang/Object;)V");
            mv.visitVarInsn(58, 7);
            mv.visitVarInsn(25, 5);
            mv.visitVarInsn(25, 7);
            mv.visitMethodInsn(185, ASMUtil.getClassName(List.class), "add", "(Ljava/lang/Object;)Z");
            mv.visitInsn(87);
            mv.visitIincInsn(4, 1);
            mv.visitLabel(l3);
        }
        mv.visitTypeInsn(187, ASMUtil.getClassName(Tuple.class));
        mv.visitInsn(89);
        mv.visitVarInsn(25, 2);
        mv.visitMethodInsn(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
        mv.visitVarInsn(25, 5);
        mv.visitMethodInsn(183, ASMUtil.getClassName(Tuple.class), "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
        mv.visitVarInsn(58, 7);
        mv.visitVarInsn(25, 7);
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
