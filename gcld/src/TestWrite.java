import org.apache.commons.lang.*;
import java.io.*;

public class TestWrite
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
        TestWrite.packagePath = "\\kfzb\\com\\reign\\kfzb";
        TestWrite.packageName = "com.reign.kfzb";
    }
    
    public static void main(final String[] args) throws IOException {
        final String userDir = System.getProperty("user.dir");
        final String realBaseDir = String.valueOf(userDir) + TestWrite.packagePath;
        final File fdir = new File(realBaseDir);
        if (!fdir.isDirectory()) {
            return;
        }
        final String sDomainDir = String.valueOf(realBaseDir) + File.separator + "domain";
        final String sDaoDir = String.valueOf(realBaseDir) + File.separator + "dao";
        final String sServiceDir = String.valueOf(realBaseDir) + File.separator + "service";
        final File domdir = new File(sDomainDir);
        if (!domdir.exists()) {
            return;
        }
        final File daodir = new File(sDaoDir);
        if (!daodir.exists()) {
            daodir.mkdir();
        }
        final File serdir = new File(sServiceDir);
        if (!serdir.exists()) {
            serdir.mkdir();
        }
        File[] listFiles;
        for (int length = (listFiles = domdir.listFiles()).length, i = 0; i < length; ++i) {
            final File df = listFiles[i];
            final String sName = getsName(df.getName());
            if (!StringUtils.isBlank(sName)) {
                final String DaoName = String.valueOf(sName) + "Dao";
                final String IDaoName = "I" + DaoName;
                final File outfile = new File(String.valueOf(sDaoDir) + File.separator + IDaoName + ".java");
                if (!outfile.exists()) {
                    outfile.createNewFile();
                    writeIDaoInterface(outfile, sName);
                }
                final File outfileDao = new File(String.valueOf(sDaoDir) + File.separator + DaoName + ".java");
                if (!outfileDao.exists()) {
                    outfileDao.createNewFile();
                    writeDao(outfileDao, sName);
                }
            }
        }
    }
    
    private static void writeIS(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestWrite.packageName + ".service;");
        bout.println("public class " + getsName(outfile.getName()) + " {");
        bout.println("");
        bout.println("}");
        bout.close();
        bout.close();
    }
    
    private static void writeISInterface(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestWrite.packageName + ".service;");
        bout.println("public interface " + getsName(outfile.getName()) + " {");
        bout.println("");
        bout.println("}");
        bout.close();
        bout.close();
    }
    
    private static void writeDao(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestWrite.packageName + ".dao;");
        bout.println("import com.reign.kf.common.dao.DirectBaseDao;");
        bout.println("import " + TestWrite.packageName + "." + "domain" + "." + sName + ";");
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
        bout.println("package " + TestWrite.packageName + ".dao;");
        bout.println("import com.reign.framework.hibernate.dao.IBaseDao;");
        bout.println("import " + TestWrite.packageName + "." + "domain" + "." + sName + ";");
        bout.println("public interface " + getsName(outfile.getName()) + " extends IBaseDao<" + sName + ", Integer> {");
        bout.println("}");
        bout.close();
        bout.close();
    }
}
