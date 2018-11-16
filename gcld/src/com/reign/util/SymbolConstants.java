package com.reign.util;

public final class SymbolConstants
{
    public static final String TAB = "\t";
    public static final String WRAP = "\r\n";
    public static final String NEW_LINE = "\n";
    public static final String SPLIT_VERTICAL = "\\|";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String UNDERLINE = "_";
    public static final String EQUAL = "=";
    public static final String ADD = "+";
    public static final String MINUS = "-";
    public static final String QUESTION = "?";
    public static final String OR = "\\|\\|";
    public static final char[] B_COLON;
    public static final char[] B_COMMA;
    public static final char[] B_L_BRACE;
    public static final char[] B_R_BRACE;
    public static final char[] B_L_BRACKET;
    public static final char[] B_R_BRACKET;
    public static final char[] B_QUOT;
    public static final String LT = "<";
    public static final String LT_END = "</";
    public static final String RT = ">";
    public static final String RT_END = "/>";
    public static final String AMP = "&";
    public static final String QUOT = "\"";
    public static final String L_BRACE = "{";
    public static final String R_BRACE = "}";
    public static final String L_ROUND_BRACKET = "(";
    public static final String R_ROUND_BRACKET = ")";
    public static final String L_BRACKET = "[";
    public static final String R_BRACKET = "]";
    public static final String BLANK = " ";
    public static final String CHINESE_MULT = "\u00d7";
    public static final String CHINESE_COMMA = "\uff0c";
    public static final String CHINESE_DOT = "\u25aa";
    
    static {
        B_COLON = new char[] { ':' };
        B_COMMA = new char[] { ',' };
        B_L_BRACE = new char[] { '{' };
        B_R_BRACE = new char[] { '}' };
        B_L_BRACKET = new char[] { '[' };
        B_R_BRACKET = new char[] { ']' };
        B_QUOT = new char[] { '\"' };
    }
}
