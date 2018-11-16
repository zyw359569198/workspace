package com.reign.gcld.activity.common;

import java.io.*;

public class StoFti
{
    static String filePath;
    static String fileName;
    static String outfileName;
    
    static {
        StoFti.filePath = "c://";
        StoFti.fileName = "package.properties";
        StoFti.outfileName = "package_ft.properties";
    }
    
    public static void main(final String[] args) throws IOException {
        final File infile = new File(String.valueOf(StoFti.filePath) + StoFti.fileName);
        final BufferedReader brin = new BufferedReader(new FileReader(infile));
        final File outfile = new File(String.valueOf(StoFti.filePath) + StoFti.outfileName);
        if (outfile.exists()) {
            outfile.delete();
        }
        outfile.createNewFile();
        final PrintWriter bout = new PrintWriter(new FileWriter(outfile));
        String newline = null;
        while ((newline = brin.readLine()) != null) {
            newline = newline.replaceAll("\u529f\u52cb", "\u529f\u52cb");
            newline = newline.replaceAll("\u5145\u503c", "\u5132\u503c");
            newline = newline.replaceAll("\u5dbd\u98db", "\u5cb3\u98db");
            newline = newline.replaceAll("\u8591\u7dad", "\u59dc\u7dad");
            newline = newline.replaceAll("\u5442\u4f48", "\u5442\u5e03");
            newline = newline.replaceAll("\u4f47\u5217", "\u968a\u5217");
            newline = newline.replaceAll("\u51e0\u7387", "\u6a5f\u7387");
            newline = newline.replaceAll("\u6982\u7387", "\u6a5f\u7387");
            newline = newline.replaceAll("\u4fe1\u606f", "\u8a0a\u606f");
            newline = newline.replaceAll("\u83dc\u5355", "\u9078\u55ae");
            newline = newline.replaceAll("\u9ed8\u8ba4", "\u9810\u8a2d");
            newline = newline.replaceAll("\u9f20\u6807", "\u6e38\u6a19");
            newline = newline.replaceAll("\u540e", "\u5f8c");
            newline = newline.replaceAll("\u5c4f\u5e55", "\u87a2\u5e55");
            newline = newline.replaceAll("\u953b\u5236", "\u935b\u88fd");
            newline = newline.replaceAll("\u5c45\u8bf4", "\u64da\u8aaa");
            newline = newline.replaceAll("\u5f53\u524d", "\u76ee\u524d");
            newline = newline.replaceAll("\u4fe1\u606f", "\u8a0a\u606f");
            newline = newline.replaceAll("\u6e38\u620f", "\u904a\u6232");
            newline = newline.replaceAll("\u5237\u65b0", "\u66f4\u65b0");
            newline = newline.replaceAll("\u793c\u5305", "\u79ae\u76d2");
            newline = newline.replaceAll("\u8bbe\u7f6e", "\u8a2d\u5b9a");
            newline = newline.replaceAll("\u9274\u5b9a", "\u9451\u5b9a");
            newline = newline.replaceAll("\u7f51\u5427", "\u7db2\u5496");
            newline = newline.replaceAll("\u754c\u9762", "\u4ecb\u9762");
            newline = newline.replaceAll("\u7a97\u53e3", "\u8996\u7a97");
            newline = newline.replaceAll("\u7c73", "\u516c\u5c3a");
            newline = newline.replaceAll("\u6254\u8272\u5b50", "\u64f2\u9ab0\u5b50");
            newline = newline.replaceAll("\u767b\u9646", "\u767b\u5165");
            newline = newline.replaceAll("\u80c0\u6237", "\u5e33\u6236");
            newline = newline.replaceAll("\u67e5\u627e", "\u67e5\u8a62");
            newline = newline.replaceAll("\u521b\u5efa", "\u5275\u7acb");
            newline = newline.replaceAll("\u5145\u503c", "\u5132\u503c");
            newline = newline.replaceAll("\u79c1\u5bc6", "\u5bc6\u8a9e");
            newline = newline.replaceAll("\u9ed8\u8ba4", "\u9810\u8a2d");
            newline = newline.replaceAll("\u6fc0\u6d3b", "\u555f\u52d5");
            bout.println(newline);
        }
        bout.close();
        brin.close();
    }
}
