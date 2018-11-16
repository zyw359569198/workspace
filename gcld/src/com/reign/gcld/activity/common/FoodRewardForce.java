package com.reign.gcld.activity.common;

import java.io.*;
import java.util.*;

public class FoodRewardForce
{
    public static void main(final String[] args) {
        final String enconding = "utf-8";
        File file = new File("c:/reduce_out_0422.txt");
        try {
            final InputStreamReader read = new InputStreamReader(new FileInputStream(file), enconding);
            final BufferedReader bur = new BufferedReader(read);
            String line = null;
            final Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
            String[] lines = null;
            String key = null;
            String value = null;
            while ((line = bur.readLine()) != null) {
                lines = line.split("#");
                key = "gcld_" + lines[0] + "_" + lines[1];
                value = "-- " + lines[4] + "\n" + "update player_battle_attribute set vip3_phantom_count = vip3_phantom_count + " + lines[6] + " where player_id = " + lines[3] + ";\n";
                final int times = Integer.valueOf(lines[6].split("\t")[0]);
                value = String.valueOf(value) + "insert into `mail`(id, f_id, f_name, t_id, title, content, sendtime, is_read, is_delete, mail_type)value(null, 0 , '\u7cfb\u7edf', " + lines[3] + ",  '\u796d\u7940\u7cae\u98df\u9001\u501f\u5175', '\u60a8\u57284\u670822\u53f7\u6d3b\u52a8\u671f\u95f4\uff0c\u91d1\u5e01\u796d\u7940\u7cae\u98df\u5171" + lines[5] + "\u6b21\uff0c\u5728\u6b64\uff0c\u9001\u60a8" + times + "\u6b21\u514d\u8d39\u501f\u5175\u3002\u52a9\u541b\u70ed\u8840\u56fd\u6218\uff01', now(), 0, 0, 1);";
                Map<String, String> map_temp = map.get(key);
                if (map_temp == null) {
                    map_temp = new HashMap<String, String>();
                    map.put(key, map_temp);
                }
                map_temp.put(lines[3], value);
            }
            bur.close();
            read.close();
            int num = 0;
            for (final String str : map.keySet()) {
                file = new File("c:/sql/" + str + ".sql");
                final BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                final Map<String, String> map_temp2 = map.get(str);
                System.out.println(String.valueOf(str) + " #############");
                output.write("DELETE  from mail where title = '\u796d\u7940\u7cae\u98df\u9001\u501f\u5175' and sendtime > '2013-04-23 00:00:00';\n");
                for (final String val : map_temp2.values()) {
                    System.out.println(val);
                    output.write(String.valueOf(val) + "\n");
                    ++num;
                }
                System.out.println("#############");
                output.close();
            }
            System.out.println("Total Num = " + num);
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
