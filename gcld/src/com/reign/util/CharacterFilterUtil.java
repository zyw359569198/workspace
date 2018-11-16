package com.reign.util;

import org.apache.commons.logging.*;
import org.apache.commons.lang.*;
import java.util.regex.*;
import java.util.*;
import java.io.*;

public class CharacterFilterUtil
{
    private static Log log;
    private static CharacterFilterUtil characterFilterUtil;
    private static String[][] playerNameRestrictWords;
    private static String[][] chatRestrictWords;
    public static final int CHAT = 1;
    public static final int ROLE_NAME = 2;
    private static final String CHAT_FILE = "chatFilterWords.txt";
    private static final String ROLE_FILE = "roleNameFilterWords.txt";
    private static int chatCursor;
    private static int roleCursor;
    private static Map<String, FilterClass> cacheMap;
    
    static {
        CharacterFilterUtil.log = LogFactory.getLog(CharacterFilterUtil.class);
        CharacterFilterUtil.characterFilterUtil = null;
        CharacterFilterUtil.playerNameRestrictWords = new String[2][];
        CharacterFilterUtil.chatRestrictWords = new String[2][];
        CharacterFilterUtil.chatCursor = 0;
        CharacterFilterUtil.roleCursor = 0;
        CharacterFilterUtil.cacheMap = new HashMap<String, FilterClass>();
    }
    
    public static CharacterFilterUtil getInstance() {
        if (CharacterFilterUtil.characterFilterUtil == null) {
            CharacterFilterUtil.characterFilterUtil = new CharacterFilterUtil();
        }
        return CharacterFilterUtil.characterFilterUtil;
    }
    
    public void init() {
        try {
            CharacterFilterUtil.log.info("load common character file");
            this.reload(CharacterFilterUtil.class.getClassLoader().getResourceAsStream("chatFilterWords.txt"), 1);
            this.reload(CharacterFilterUtil.class.getClassLoader().getResourceAsStream("roleNameFilterWords.txt"), 2);
        }
        catch (FileNotFoundException e) {
            CharacterFilterUtil.log.error("file not found", e);
        }
    }
    
    public boolean isStringValidate(final String sourceStr, final String fileName, final int type) {
        boolean isValid = true;
        String[] restrictWords = null;
        if (1 == type) {
            restrictWords = CharacterFilterUtil.chatRestrictWords[CharacterFilterUtil.chatCursor];
        }
        else if (2 == type) {
            restrictWords = CharacterFilterUtil.playerNameRestrictWords[CharacterFilterUtil.roleCursor];
        }
        if (restrictWords != null && restrictWords.length > 0) {
            for (int i = 0; i < restrictWords.length; ++i) {
                final String word = restrictWords[i];
                if (!word.trim().equals("")) {
                    final FilterClass fc = CharacterFilterUtil.cacheMap.get(word);
                    if (fc != null) {
                        String filterWord = sourceStr;
                        if (!fc.skipSpace) {
                            filterWord = filterWord.replaceAll(" ", "");
                        }
                        final Matcher matcher = fc.pattern.matcher(filterWord.replaceAll("&nbsp;", "").replaceAll("\n", ""));
                        if (matcher.matches()) {
                            isValid = false;
                            break;
                        }
                    }
                }
            }
        }
        return isValid;
    }
    
    public boolean isStringValidate(final String sourceStr, final int type) {
        return this.isStringValidate(sourceStr, null, type);
    }
    
    public String getFilterWordsOfString(String sourceStr, final String fileName, final int type) {
        String[] restrictWords = null;
        if (1 == type) {
            restrictWords = CharacterFilterUtil.chatRestrictWords[CharacterFilterUtil.chatCursor];
        }
        else if (2 == type) {
            restrictWords = CharacterFilterUtil.playerNameRestrictWords[CharacterFilterUtil.roleCursor];
        }
        if (restrictWords != null && restrictWords.length > 0) {
            for (int i = 0; i < restrictWords.length; ++i) {
                final String word = restrictWords[i];
                if (StringUtils.isNotBlank(word)) {
                    final FilterClass fc = CharacterFilterUtil.cacheMap.get(word);
                    if (fc != null) {
                        String filterWord = sourceStr;
                        if (!fc.skipSpace) {
                            filterWord = filterWord.replaceAll(" ", "");
                        }
                        final Matcher matcher = fc.pattern.matcher(filterWord.replaceAll("\n", ""));
                        if (matcher.matches()) {
                            String temp = sourceStr.toLowerCase();
                            if (!fc.skipSpace) {
                                temp = sourceStr.toLowerCase().replaceAll(" ", "");
                            }
                            temp = temp.replaceAll("&nbsp;", "").replaceAll("\n", "").replaceAll(word, StringUtils.leftPad("", word.replaceAll("\\\\", "").length(), '*'));
                            final StringBuilder buffer = new StringBuilder();
                            int index = 0;
                            for (int k = 0; k < sourceStr.length(); ++k) {
                                final char c1 = sourceStr.toLowerCase().charAt(k);
                                final char c2 = sourceStr.charAt(k);
                                if (index >= temp.length()) {
                                    buffer.append(c1);
                                }
                                else {
                                    final char c3 = temp.charAt(index);
                                    if (c1 != c3 && (c1 == ' ' || c1 == '\n') && c3 != '*') {
                                        buffer.append(c2);
                                    }
                                    else if (c1 != c3 && (c1 == ' ' || c1 == '\n') && c3 == '*') {
                                        buffer.append(c2);
                                    }
                                    else if (c1 != c3 && c1 != ' ' && c1 != '\n' && c3 == '*') {
                                        buffer.append('*');
                                        ++index;
                                    }
                                    else if (c1 == c3) {
                                        buffer.append(c2);
                                        ++index;
                                    }
                                    else {
                                        buffer.append(c2);
                                    }
                                }
                            }
                            sourceStr = buffer.toString();
                        }
                    }
                }
            }
        }
        return sourceStr;
    }
    
    public String getFilterWordsOfString(final String sourceStr, final int type) {
        return this.getFilterWordsOfString(sourceStr, null, type);
    }
    
    private void updateRestrictWords(final String fileName, final int type) {
        InputStream is = null;
        if (fileName == null || fileName.equals("")) {
            String path = CharacterFilterUtil.class.getClassLoader().getResource("").getPath();
            path = path.replaceAll("%20", " ");
            if (1 == type) {
                is = CharacterFilterUtil.class.getClassLoader().getResourceAsStream("chatFilterWords.txt");
            }
            else if (2 == type) {
                is = CharacterFilterUtil.class.getClassLoader().getResourceAsStream("roleNameFilterWords.txt");
            }
        }
        else {
            try {
                is = new FileInputStream(fileName);
            }
            catch (FileNotFoundException e) {
                CharacterFilterUtil.log.info("\u6587\u4ef6\u4e0d\u5b58\u5728", e);
            }
        }
        if (is != null) {
            if (1 == type) {
                if (CharacterFilterUtil.chatRestrictWords == null) {
                    CharacterFilterUtil.chatRestrictWords[CharacterFilterUtil.chatCursor] = this.readerFileContent(is);
                }
            }
            else if (2 == type && CharacterFilterUtil.playerNameRestrictWords == null) {
                CharacterFilterUtil.playerNameRestrictWords[CharacterFilterUtil.roleCursor] = this.readerFileContent(is);
            }
        }
        else {
            CharacterFilterUtil.log.info("\u6587\u4ef6\u4e0d\u5b58\u5728");
        }
    }
    
    private String[] readerFileContent(final InputStream is) {
        BufferedReader reader = null;
        final List<String> tempList = new ArrayList<String>(150);
        try {
            reader = new BufferedReader(new UnicodeReader(is, "UTF-8"));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                temp = temp.replaceAll("\\^", "\\\\^").replaceAll("\\$", "\\\\\\$").replaceAll("\\.", "\\\\.");
                final String[] tempStrs = temp.split("\\|");
                String[] array;
                for (int length = (array = tempStrs).length, i = 0; i < length; ++i) {
                    String tempStr = array[i];
                    tempStr = tempStr.replaceAll(" ", "");
                    final Pattern pattern = Pattern.compile("^.*" + tempStr.toLowerCase() + ".*$", 2);
                    CharacterFilterUtil.cacheMap.put(tempStr, new FilterClass(pattern, false));
                    tempList.add(tempStr);
                }
            }
            return tempList.toArray(new String[0]);
        }
        catch (FileNotFoundException e3) {
            CharacterFilterUtil.log.info("\u6587\u4ef6\u4e0d\u5b58\u5728");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return new String[0];
    }
    
    private void reload(final InputStream is, final int type) throws FileNotFoundException {
        if (type == 2) {
            CharacterFilterUtil.playerNameRestrictWords[1 - CharacterFilterUtil.roleCursor] = this.readerFileContent(is);
            CharacterFilterUtil.roleCursor = 1 - CharacterFilterUtil.roleCursor;
        }
        else if (type == 1) {
            CharacterFilterUtil.chatRestrictWords[1 - CharacterFilterUtil.chatCursor] = this.readerFileContent(is);
            CharacterFilterUtil.chatCursor = 1 - CharacterFilterUtil.chatCursor;
        }
    }
    
    public static void main(final String[] args) throws IOException {
        getInstance().init();
        final String value = getInstance().getFilterWordsOfString("A meat FLAPS", 1);
        System.out.println(value);
    }
    
    private class FileMonitor extends Thread
    {
        private long chatModify;
        private long roleModify;
        private File chatFile;
        private File roleFile;
        private String path;
        private boolean chatReload;
        private boolean roleReload;
        private static final long TIME_OUT = 600000L;
        
        public FileMonitor(final String threadName) {
            super(threadName);
            this.chatModify = -1L;
            this.roleModify = -1L;
            this.chatFile = null;
            this.roleFile = null;
            this.path = null;
            this.chatReload = false;
            this.roleReload = false;
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    this.chatReload = false;
                    this.roleReload = false;
                    if (this.path == null) {
                        this.path = CharacterFilterUtil.class.getClassLoader().getResource("").getPath();
                    }
                    if (this.chatFile == null) {
                        this.chatFile = new File(String.valueOf(this.path) + File.separator + "chatFilterWords.txt");
                        CharacterFilterUtil.this.reload(new FileInputStream(this.chatFile), 1);
                        this.chatReload = true;
                        this.chatModify = this.chatFile.lastModified();
                    }
                    if (this.roleFile == null) {
                        this.roleFile = new File(String.valueOf(this.path) + File.separator + "roleNameFilterWords.txt");
                        CharacterFilterUtil.this.reload(new FileInputStream(this.roleFile), 2);
                        this.roleReload = true;
                        this.roleModify = this.roleFile.lastModified();
                    }
                    if (!this.chatReload && this.roleFile.lastModified() != this.roleModify) {
                        CharacterFilterUtil.this.reload(new FileInputStream(this.roleFile), 2);
                        this.roleModify = this.roleFile.lastModified();
                    }
                    if (!this.roleReload && this.chatFile.lastModified() != this.chatModify) {
                        CharacterFilterUtil.this.reload(new FileInputStream(this.chatFile), 1);
                        this.chatModify = this.chatFile.lastModified();
                    }
                }
                catch (Exception e) {
                    CharacterFilterUtil.log.error("file-monitor", e);
                    try {
                        Thread.sleep(600000L);
                    }
                    catch (InterruptedException e2) {
                        CharacterFilterUtil.log.error("file-monitor-InterruptedException", e2);
                    }
                    continue;
                }
                finally {
                    try {
                        Thread.sleep(600000L);
                    }
                    catch (InterruptedException e2) {
                        CharacterFilterUtil.log.error("file-monitor-InterruptedException", e2);
                    }
                }
                try {
                    Thread.sleep(600000L);
                }
                catch (InterruptedException e2) {
                    CharacterFilterUtil.log.error("file-monitor-InterruptedException", e2);
                }
            }
        }
    }
    
    private class FilterClass
    {
        public Pattern pattern;
        public boolean skipSpace;
        
        public FilterClass(final Pattern pattern, final boolean skipSpace) {
            this.skipSpace = false;
            this.pattern = pattern;
            this.skipSpace = skipSpace;
        }
    }
}
