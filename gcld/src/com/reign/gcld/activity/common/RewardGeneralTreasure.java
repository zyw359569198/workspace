package com.reign.gcld.activity.common;

import com.reign.gcld.common.util.*;
import java.io.*;
import java.util.*;

public class RewardGeneralTreasure
{
    public static void main(final String[] args) {
        for (int i = 1; i <= 34; ++i) {
            final String fileName = "C:/kuafu_query_20131111/gcld_match_" + i + ".txt";
            create(fileName);
            System.out.println(fileName);
        }
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
            int name = 0;
            int a = 0;
            int b = 0;
            while ((line = bur.readLine()) != null) {
                lines = line.split("\t");
                if (lines[0].equalsIgnoreCase("game_server")) {
                    continue;
                }
                ++name;
                key = "gcld_" + lines[0];
                key = key.replaceAll("_S", "_");
                a = WebUtil.nextInt(5) + 10;
                b = WebUtil.nextInt(5) + 10;
                int type = 3;
                String itemName = "\u6708\u5149\u676f";
                int n = 20;
                String itemLv = "\u7cbe\u54c1";
                if (name >= 1 && name <= 20) {
                    n = 20;
                    itemLv = "\u7cbe\u54c1";
                    a = 17;
                    b = 17;
                    type = 4;
                    itemName = "\u548c\u6c0f\u74a7";
                }
                else if (name >= 21 && name <= 100) {
                    n = 100;
                    itemLv = "\u9ad8\u7ea7";
                    a = 15;
                    b = 15;
                    type = 4;
                    itemName = "\u548c\u6c0f\u74a7";
                }
                else if (name >= 101 && name <= 200) {
                    n = 200;
                    itemLv = "\u6781\u54c1";
                    a = 14;
                    b = 14;
                    type = 3;
                    itemName = "\u591c\u5149\u676f";
                }
                value = "-- " + lines[1] + "\n" + "insert into store_house VALUES(null, " + lines[4] + ", " + type + ", 3, 2, 0, 0, '" + a + "," + b + "', " + type + ", 0, 1, 0, '', 0, null, null, null, 0)" + ";\n";
                value = String.valueOf(value) + "insert into `mail`(id, f_id, f_name, t_id, title, content, sendtime, is_read, is_delete, mail_type)value(null, 0 , '\u7cfb\u7edf', " + lines[4] + ",'11\u6708\u5148\u950b\u64c2\u53f0\u8d5b\u5b9d\u7269\u5956\u52b1',  '\u60a8\u5728\u672c\u6b21\u5148\u950b\u64c2\u53f0\u8d5b\u6218\u7ee9\u8f89\u714c\uff0c\u8363\u767b\u6240\u5c5e\u8d5b\u533a\u524d" + n + "\u540d\u4e4b\u5217\uff01\u83b7\u8d601\u4e2a" + itemLv + itemName + "\uff0c\u7edf\u52c7\u5404" + a + "\u3002\u5956\u52b1\u8bf7\u5728\u6e38\u620f\u5185\u4ed3\u5e93\u4e2d\u67e5\u6536\uff0c\u795d\u60a8\u6e38\u620f\u6109\u5feb\uff01', now(), 0, 0, 1);";
                Map<String, String> map_temp = map.get(key);
                if (map_temp == null) {
                    map_temp = new HashMap<String, String>();
                    map.put(key, map_temp);
                }
                map_temp.put(lines[4], value);
            }
            bur.close();
            read.close();
            int num = 0;
            for (final String str : map.keySet()) {
                file = new File("c:/sql/" + str + ".sql");
                final BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                final Map<String, String> map_temp = map.get(str);
                System.out.println(String.valueOf(str) + " #############");
                for (final String val : map_temp.values()) {
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
