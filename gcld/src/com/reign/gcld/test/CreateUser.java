package com.reign.gcld.test;

import java.util.*;
import java.io.*;

public class CreateUser
{
    public static final String str = "INSERT INTO `user` VALUES ('";
    public static final String str2 = "', '";
    public static final String str3 = "', '";
    public static final String str4 = "', '1', '', '1', '1');";
    public static Set<Integer> passwdSet;
    
    static {
        CreateUser.passwdSet = new TreeSet<Integer>();
    }
    
    public static void main(final String[] args) {
        final File file = new File("C:\\ccc.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1001;
            final StringBuilder sb = new StringBuilder();
            final Map<String, Integer> userPassMap = new HashMap<String, Integer>();
            while ((tempString = reader.readLine()) != null) {
                final String[] srcs = tempString.split(":");
                final int passwd = getPasswd();
                System.out.println(sb.append("INSERT INTO `user` VALUES ('").append(line++).append("', '").append(srcs[1]).append("', '").append(passwd).append("', '1', '', '1', '1');").toString());
                sb.delete(0, sb.length());
                userPassMap.put(tempString, passwd);
            }
            reader.close();
            System.out.println("############################################################");
            for (final Map.Entry<String, Integer> temp : userPassMap.entrySet()) {
                final String[] srcs2 = temp.getKey().split(":");
                System.out.println(String.format("#\u7528\u6237:%3s   \u7528\u6237\u540d:%9s   \u5bc6\u7801     " + temp.getValue(), srcs2[0].trim(), srcs2[1].trim()));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static int getRandom() {
        final Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
    
    public static int getPasswd() {
        int passwd;
        for (passwd = getRandom(); CreateUser.passwdSet.contains(passwd); passwd = getRandom()) {}
        CreateUser.passwdSet.add(passwd);
        return passwd;
    }
}
