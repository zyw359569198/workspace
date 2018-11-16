package com.reign.gcld.activity.common;

import java.io.*;
import java.util.*;

public class ServerPlayerIdReward
{
    public static void main(final String[] args) {
        final String fileName = "c:/kf.txt";
        create(fileName);
    }
    
    public static void create(final String fileName) {
        final String enconding = "utf-8";
        File file = new File(fileName);
        try {
            final InputStreamReader read = new InputStreamReader(new FileInputStream(file), enconding);
            final BufferedReader bur = new BufferedReader(read);
            String line = null;
            final Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
            String[] lines = null;
            String key = null;
            String value = null;
            while ((line = bur.readLine()) != null) {
                lines = line.split(" ");
                key = "gcld_" + lines[0];
                value = "-- " + lines[2] + "\n" + "update player_tickets set tickets = tickets + " + 7000 + " where player_id = " + lines[1] + " limit 1;\n";
                value = String.valueOf(value) + "insert into `mail`(id, f_id, f_name, t_id, title, content, sendtime, is_read, is_delete, mail_type)value(null, 0 , '\u7cfb\u7edf', " + lines[1] + ",'\u8de8\u670d\u56fd\u6218\u70b9\u5238\u8865\u507f',  '\u4eb2\u7231\u7684\u73a9\u5bb6\uff0c\u7531\u4e8e\u7f51\u7edc\u901a\u4fe1\u95ee\u9898\uff0c\u5bfc\u81f4\u60a8\u6240\u53c2\u4e0e\u7684\u7b2c\u4e8c\u573a\u8de8\u670d\u56fd\u6218\u7684\u70b9\u5238\u7ed3\u7b97\u6709\u8bef\uff0c\u5bf9\u6b64\u6211\u4eec\u6df1\u8868\u6b49\u610f\uff0c\u5e76\u8865\u507f\u7ed9\u60a87000\u70b9\u5238\u3002\u795d\u60a8\u5728\u8de8\u670d\u56fd\u6218\u4e2d\u8d8a\u6218\u8d8a\u52c7\uff0c\u4e3a\u56fd\u5bb6\u8d62\u53d6\u66f4\u597d\u7684\u6218\u7ee9\uff01', now(), 0, 0, 1);";
                Map<String, String> map_temp = map.get(key);
                if (map_temp == null) {
                    map_temp = new HashMap<String, String>();
                    map.put(key, map_temp);
                }
                map_temp.put(lines[1], value);
            }
            bur.close();
            read.close();
            int num = 0;
            for (final String str : map.keySet()) {
                file = new File("c:/sql/" + str + ".sql");
                final BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                final Map<String, String> map_temp2 = map.get(str);
                System.out.println(String.valueOf(str) + " #############");
                for (final String val : map_temp2.values()) {
                    System.out.println(val);
                    output.write(String.valueOf(val) + "\n");
                    ++num;
                }
                System.out.println("#############" + num);
                output.close();
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
    }
}
