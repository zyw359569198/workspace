package com.reign.gcld.activity.common;

import com.reign.util.*;
import java.util.*;
import java.io.*;

public class BuchangXilian
{
    private static Map<String, Tuple<String, Integer>> shiftMap;
    
    static {
        BuchangXilian.shiftMap = new HashMap<String, Tuple<String, Integer>>();
    }
    
    public static void main(final String[] args) {
        BuchangXilian.shiftMap.put("gcld_gcld_2", new Tuple("gcld_gcld_1", 0));
        final String fileName = "C:/gold_1230.txt";
        final String fileName2 = "C:/que_1230.txt";
        inFileAndOutFile(fileName);
        inFileAndOutFile(fileName2);
    }
    
    public static void inFileAndOutFile(final String fileName) {
        final String enconding = "utf-8";
        final File inFile = new File(fileName);
        final File outFile = new File(String.valueOf(fileName) + ".new");
        try {
            final InputStreamReader read = new InputStreamReader(new FileInputStream(inFile), enconding);
            final BufferedReader bur = new BufferedReader(read);
            final BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
            String line = null;
            String[] lines = null;
            String key = null;
            while ((line = bur.readLine()) != null) {
                lines = line.split("\t| ");
                key = lines[0];
                if (key.equals("gcld_gcld_2")) {
                    System.out.println(key);
                }
                final Tuple<String, Integer> tuple = BuchangXilian.shiftMap.get(key);
                if (tuple == null) {
                    output.write(String.valueOf(line) + "\n");
                }
                else {
                    final int playerId = Integer.parseInt(lines[1]);
                    final int times = Integer.parseInt(lines[2]);
                    if (key.equals("gcld_gcld_2")) {
                        System.out.println(String.valueOf(tuple.left) + " " + (playerId + tuple.right) + " " + times + "\n");
                    }
                    output.write(String.valueOf(tuple.left) + " " + (playerId + tuple.right) + " " + times + "\n");
                }
            }
            bur.close();
            read.close();
            output.close();
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
