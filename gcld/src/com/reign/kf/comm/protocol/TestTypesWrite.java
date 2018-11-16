package com.reign.kf.comm.protocol;

import java.io.*;

public class TestTypesWrite
{
    public static final String domainP = "domain";
    public static final String daoP = "dao";
    public static final String serviceP = "service";
    public static final String Dao = "Dao";
    public static final String Service = "Service";
    public static final String prefixI = "I";
    static String packagePath;
    static String packageName;
    static final String java = "java";
    static final String djava = ".java";
    
    static {
        TestTypesWrite.packagePath = "\\kfzb\\com\\reign\\kfzb\\dto";
        TestTypesWrite.packageName = "com.reign.kfzb";
    }
    
    public static void main(final String[] args) throws IOException {
        final String userDir = System.getProperty("user.dir");
        final String realBaseDir = String.valueOf(userDir) + TestTypesWrite.packagePath;
        final File fdir = new File(realBaseDir);
        if (!fdir.isDirectory()) {
            return;
        }
        final File[] files = fdir.listFiles();
        File[] array;
        for (int length = (array = files).length, i = 0; i < length; ++i) {
            final File dtoFileDir = array[i];
            if (dtoFileDir.isDirectory()) {
                final File[] fs = dtoFileDir.listFiles();
                File[] array2;
                for (int length2 = (array2 = fs).length, j = 0; j < length2; ++j) {
                    final File dtoFile = array2[j];
                    final String[] fname = dtoFile.getName().split("\\.");
                    if (fname.length >= 2 && fname[1].equals("java")) {
                        final String fn = fname[0];
                        System.out.println("register(" + fn + ".class);");
                    }
                }
            }
        }
    }
    
    private static void writeIS(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestTypesWrite.packageName + ".service;");
        bout.println("public class " + getsName(outfile.getName()) + " {");
        bout.println("");
        bout.println("}");
        bout.close();
        bout.close();
    }
    
    private static void writeISInterface(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestTypesWrite.packageName + ".service;");
        bout.println("public interface " + getsName(outfile.getName()) + " {");
        bout.println("");
        bout.println("}");
        bout.close();
        bout.close();
    }
    
    private static void writeDao(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestTypesWrite.packageName + ".dao;");
        bout.println("import com.reign.kf.common.dao.DirectBaseDao;");
        bout.println("import " + TestTypesWrite.packageName + "." + "domain" + "." + sName + ";");
        bout.println("import org.springframework.stereotype.Component;");
        bout.println("@Component");
        bout.println("public class " + getsName(outfile.getName()) + " extends DirectBaseDao<" + sName + ", Integer> implements I" + getsName(outfile.getName()) + "{");
        bout.println("}");
        bout.close();
        bout.close();
    }
    
    private static String getsName(final String name) {
        return name.split("\\.")[0];
    }
    
    private static void writeIDaoInterface(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestTypesWrite.packageName + ".dao;");
        bout.println("import com.reign.framework.hibernate.dao.IBaseDao;");
        bout.println("import " + TestTypesWrite.packageName + "." + "domain" + "." + sName + ";");
        bout.println("public interface " + getsName(outfile.getName()) + " extends IBaseDao<" + sName + ", Integer> {");
        bout.println("}");
        bout.close();
        bout.close();
    }
}
