package com.reign.gcld.activity.common;

import java.io.*;
import java.util.*;

public class CopyOfRewardGeneralTreasure
{
    public static void main(final String[] args) {
        final String fileName = "c:/gcld_1.txt";
        RewardGeneralTreasure.create(fileName);
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
            final String key = null;
            String value = null;
            final int name = 0;
            final int a = 0;
            final int b = 0;
            while ((line = bur.readLine()) != null) {
                lines = line.split(" ");
                if (lines[2].equals("token")) {
                    if (lines[1].equals("+")) {
                        value = "-- ";
                    }
                    else {
                        value = "-- ";
                    }
                }
                else if (lines[2].equals("copper")) {
                    value = "update player_resource set copper = copper - " + lines[3] + "where player_id =  " + lines[0] + " ;\n";
                }
                else if (!lines[2].equals("ugold") && !lines[2].equals("ticket")) {
                    if (lines[2].equals("food")) {
                        value = "update player_resource set food = food - " + lines[3] + "where player_id =  " + lines[0] + " ;\n";
                    }
                    else if (lines[2].equals("exp")) {
                        int curLv = 100;
                        int curExp = 50000;
                        final int _exp = 1000000;
                        int exp = curExp;
                        final int xi = 0;
                        int i = curLv;
                        if (exp <= _exp) {
                            for (i = curLv - 1; exp < _exp; exp += xi, --i) {}
                        }
                        curExp = exp - _exp;
                        curLv = i;
                    }
                    else if (lines[2].equals("sgold")) {
                        lines[1].equals("+");
                    }
                    else if (lines[2].equals("freephantom")) {
                        lines[1].equals("+");
                    }
                    else if (lines[2].equals("iron")) {
                        value = "update player_resource set iron = iron - " + lines[3] + "where player_id =  " + lines[0] + " ;\n";
                    }
                }
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
