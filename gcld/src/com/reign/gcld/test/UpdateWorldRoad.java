package com.reign.gcld.test;

import java.io.*;

public class UpdateWorldRoad
{
    public static final String str = "UPDATE  `world_road` SET LENGTH =  ";
    public static final String str2 = "  WHERE START  =   ";
    public static final String str3 = "  AND  END  =  ";
    
    public static void main(final String[] args) {
        final File file = new File("C:\\world_road.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            final StringBuilder sb = new StringBuilder();
            if ((tempString = reader.readLine()) != null) {
                final String[] srcs = tempString.split(";");
                String[] array;
                for (int length = (array = srcs).length, i = 0; i < length; ++i) {
                    final String temp = array[i];
                    final String[] records = temp.trim().split(":");
                    System.out.println(sb.append("UPDATE  `world_road` SET LENGTH =  ").append(Integer.parseInt(records[2]) * 200).append("  WHERE START  =   ").append(records[0]).append("  AND  END  =  ").append(records[1]).append(";"));
                    sb.delete(0, sb.length());
                }
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
