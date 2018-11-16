package com.reign.gcld.player.common;

import com.reign.gcld.common.log.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.common.*;
import java.awt.*;
import com.reign.gcld.sdata.cache.*;
import java.util.*;
import com.reign.util.*;
import java.io.*;

public class CodeConfig
{
    private static Logger log;
    private static String fileName;
    private static String fileName_en;
    private static volatile long lastModifyTime;
    private static File file;
    private static InputStream is;
    private static List<String> words;
    private static Font[] fonts;
    
    static {
        CodeConfig.log = CommonLog.getLog(CodeConfig.class);
        CodeConfig.fileName = "code.txt";
        CodeConfig.fileName_en = "code_en.txt";
        CodeConfig.lastModifyTime = 0L;
        CodeConfig.fonts = new Font[5];
        if (WebUtil.isFt()) {
            CodeConfig.fonts[0] = loadFont("SIMYOU.TTF");
        }
        else {
            CodeConfig.fonts[0] = loadFont("SIMYOU.TTF");
            CodeConfig.fonts[1] = loadFont("FZSTK.TTF");
            CodeConfig.fonts[2] = loadFont("STCAIYUN.TTF");
            CodeConfig.fonts[3] = loadFont("STLITI.TTF");
            CodeConfig.fonts[4] = loadFont("STXINGKA.TTF");
        }
    }
    
    public static Font[] getFonts() {
        return CodeConfig.fonts;
    }
    
    private static Font loadFont(final String fontName) {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(String.valueOf(ListenerConstants.WEB_PATH) + "font" + File.separator + fontName));
            final Font font = Font.createFont(0, is);
            is.close();
            return font;
        }
        catch (FontFormatException e) {
            CodeConfig.log.error("", e);
        }
        catch (IOException e2) {
            CodeConfig.log.error("", e2);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex) {}
            }
        }
        return null;
    }
    
    public static String getRandomWords() {
        (CodeConfig.words = GeneralCache.getGeneralName()).addAll(EquipSkillCache.getNames());
        CodeConfig.words.addAll(ArmiesCache.getGeneralName());
        return CodeConfig.words.get(WebUtil.nextInt(CodeConfig.words.size()));
    }
    
    private static synchronized void reload() {
        if (needReload()) {
            try {
                if (CodeConfig.file != null) {
                    CodeConfig.lastModifyTime = CodeConfig.file.lastModified();
                    CodeConfig.is = new FileInputStream(CodeConfig.file);
                }
                CodeConfig.log.info("\u8f7d\u5165\u9a8c\u8bc1\u7801\u914d\u7f6e\u6587\u4ef6\u5f00\u59cb");
                CodeConfig.words = readerFileContent(CodeConfig.is);
                CodeConfig.log.info("\u8f7d\u5165\u9a8c\u8bc1\u7801\u914d\u7f6e\u6587\u4ef6\u7ed3\u675f");
            }
            catch (FileNotFoundException e) {
                CodeConfig.log.error("", e);
            }
        }
    }
    
    private static List<String> readerFileContent(final InputStream is) {
        final List<String> wordList = new ArrayList<String>(600);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new UnicodeReader(is, "UTF-8"));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                wordList.add(temp);
            }
        }
        catch (FileNotFoundException e) {
            CodeConfig.log.info("", e);
        }
        catch (IOException e2) {
            CodeConfig.log.info("", e2);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {}
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex2) {}
            }
        }
        if (reader != null) {
            try {
                reader.close();
            }
            catch (IOException ex3) {}
        }
        if (is != null) {
            try {
                is.close();
            }
            catch (IOException ex4) {}
        }
        return wordList;
    }
    
    private static boolean needReload() {
        boolean result = true;
        if (CodeConfig.file == null && CodeConfig.words != null) {
            result = false;
        }
        else if (CodeConfig.file != null && CodeConfig.file.lastModified() == CodeConfig.lastModifyTime) {
            result = false;
        }
        return result;
    }
}
