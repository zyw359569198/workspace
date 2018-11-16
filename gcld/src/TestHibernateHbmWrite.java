import java.util.regex.*;
import java.io.*;

public class TestHibernateHbmWrite
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
    static String pSplit;
    static String fSplit;
    
    static {
        TestHibernateHbmWrite.packagePath = "\\kfzb\\com\\reign\\kfzb";
        TestHibernateHbmWrite.packageName = "com.reign.kfzb";
        TestHibernateHbmWrite.pSplit = ".";
        TestHibernateHbmWrite.fSplit = "/";
    }
    
    public static void main(final String[] args) throws IOException {
        final String userDir = System.getProperty("user.dir");
        final String realBaseDir = String.valueOf(userDir) + TestHibernateHbmWrite.packagePath;
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
            final File f = listFiles[i];
            if (f.isFile()) {
                final String fileName = f.getName();
                if (fileName.indexOf(".java") > 0) {
                    final File tmpf = new File(String.valueOf(f.getParent()) + TestHibernateHbmWrite.fSplit + f.getName() + "_tmp");
                    tmpf.createNewFile();
                    final PrintWriter pw = new PrintWriter(tmpf);
                    final BufferedReader br = new BufferedReader(new FileReader(f));
                    String firstLine = null;
                    final Pattern pattern1 = Pattern.compile("\\s*public\\s+class\\s+(\\w+)\\s+(.+)");
                    final Pattern pattern2 = Pattern.compile("\\s*public\\s+(\\w+)\\s+get(\\w+)\\.*");
                    boolean over = false;
                    boolean firstget = true;
                    while ((firstLine = br.readLine()) != null) {
                        final Matcher mat = pattern1.matcher(firstLine);
                        if (firstLine.contains("@hibernate.class")) {
                            over = true;
                        }
                        else {
                            if (mat.find()) {
                                final String ClassName = mat.group(1);
                                final String tableName = getDBColName(ClassName);
                                println(pw, "/**");
                                println(pw, "*@hibernate.class table=\"" + tableName + "\"");
                                println(pw, "*/");
                            }
                            final Matcher mat2 = pattern2.matcher(firstLine);
                            if (mat2.find()) {
                                final String tname = mat2.group(2);
                                final String cName = getDBColName(tname);
                                if (firstget) {
                                    println(pw, "\t/**");
                                    println(pw, "\t* @hibernate.id unsaved-value=\"null\"");
                                    println(pw, "\t*@hibernate.column name=\"" + cName + "\"");
                                    println(pw, "\t* @hibernate.generator class=\"native\"");
                                    println(pw, "\t*/");
                                    firstget = false;
                                }
                                else {
                                    println(pw, "\t/**");
                                    println(pw, "\t*@hibernate.property column=\"" + cName + "\"");
                                    println(pw, "\t*/");
                                }
                            }
                            if (firstLine.contains("import com.xinyun.core.model.IModel;")) {
                                println(pw, "import com.reign.framework.hibernate.model.IModel;");
                            }
                            else {
                                if (firstLine.trim().startsWith("@")) {
                                    continue;
                                }
                                println(pw, firstLine);
                            }
                        }
                    }
                    if (over) {
                        br.close();
                        pw.close();
                        tmpf.delete();
                    }
                    else {
                        br.close();
                        pw.close();
                        f.delete();
                        tmpf.renameTo(f);
                    }
                }
            }
        }
    }
    
    private static String getDBColName(String st) {
        final StringBuilder sb = new StringBuilder();
        st = st.trim();
        int i = 0;
        String[] split;
        for (int length = (split = st.split("")).length, j = 0; j < length; ++j) {
            final String s = split[j];
            if (!s.equals("")) {
                if (i > 0 && s.equals(s.toUpperCase())) {
                    sb.append("_");
                }
                ++i;
                sb.append(s.toLowerCase());
            }
        }
        return sb.toString();
    }
    
    private static void println(final PrintWriter pw, final String content) {
        System.out.println(content);
        pw.println(content);
    }
    
    private static void writeIS(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestHibernateHbmWrite.packageName + ".service;");
        bout.println("public class " + getsName(outfile.getName()) + " {");
        bout.println("");
        bout.println("}");
        bout.close();
        bout.close();
    }
    
    private static void writeISInterface(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestHibernateHbmWrite.packageName + ".service;");
        bout.println("public interface " + getsName(outfile.getName()) + " {");
        bout.println("");
        bout.println("}");
        bout.close();
        bout.close();
    }
    
    private static void writeDao(final File outfile, final String sName) throws IOException {
        System.out.println("cr==" + outfile.getName() + "--" + outfile.getCanonicalPath());
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        bout.println("package " + TestHibernateHbmWrite.packageName + ".dao;");
        bout.println("import com.xinyun.core.dao.BaseDao;");
        bout.println("import " + TestHibernateHbmWrite.packageName + "." + "domain" + "." + sName + ";");
        bout.println("import org.springframework.stereotype.Component;");
        bout.println("@Component");
        bout.println("public class " + getsName(outfile.getName()) + " extends BaseDao<" + sName + ", Integer> implements I" + getsName(outfile.getName()) + "{");
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
        bout.println("package " + TestHibernateHbmWrite.packageName + ".dao;");
        bout.println("import com.xinyun.core.dao.IBaseDao;");
        bout.println("import " + TestHibernateHbmWrite.packageName + "." + "domain" + "." + sName + ";");
        bout.println("public interface " + getsName(outfile.getName()) + " extends IBaseDao<" + sName + ", Integer> {");
        bout.println("}");
        bout.close();
        bout.close();
    }
}
