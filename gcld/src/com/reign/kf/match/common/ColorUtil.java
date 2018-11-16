package com.reign.kf.match.common;

import com.reign.util.*;

public class ColorUtil
{
    public static String color1;
    public static String color2;
    public static String color3;
    public static String color4;
    public static String color5;
    public static String color6;
    public static String color7;
    public static String color8;
    public static String wei;
    public static String shu;
    public static String wu;
    public static String barbarain;
    public static String green;
    
    static {
        ColorUtil.color1 = "<font color=\"#D7D7D6\">{0}</font>";
        ColorUtil.color2 = "<font color=\"#5E8DA2\">{0}</font>";
        ColorUtil.color3 = "<font color=\"#669966\">{0}</font>";
        ColorUtil.color4 = "<font color=\"#94A354\">{0}</font>";
        ColorUtil.color5 = "<font color=\"#AA7366\">{0}</font>";
        ColorUtil.color6 = "<font color=\"#A7729D\">{0}</font>";
        ColorUtil.color7 = "<font color=\"#FFCC00\">{0}</font>";
        ColorUtil.color8 = "<font color=\"#FFFFCC\">{0}</font>";
        ColorUtil.wei = "<font color=\"#6EB4EE\">{0}</font>";
        ColorUtil.shu = "<font color=\"#EB9642\">{0}</font>";
        ColorUtil.wu = "<font color=\"#88D442\">{0}</font>";
        ColorUtil.barbarain = "<font color=\"#B88AFD\">{0}</font>";
        ColorUtil.green = "<font color=\"#00FF00\">{0}</font>";
    }
    
    public static String getBlueMsg(final String msg) {
        return MessageFormatter.format(ColorUtil.color2, new Object[] { msg });
    }
    
    public static String getWhiteMsg(final String msg) {
        return MessageFormatter.format(ColorUtil.color8, new Object[] { msg });
    }
    
    public static String getYellowMsg(final String msg) {
        return MessageFormatter.format(ColorUtil.color7, new Object[] { msg });
    }
    
    public static String getYellowMsg(final int msg) {
        return MessageFormatter.format(ColorUtil.color7, new Object[] { msg });
    }
    
    public static String getRedMsg(final String msg) {
        return MessageFormatter.format(ColorUtil.color5, new Object[] { msg });
    }
    
    public static String getRedMsg(final int msg) {
        return MessageFormatter.format(ColorUtil.color5, new Object[] { msg });
    }
    
    public static String getVioletMsg(final String msg) {
        return MessageFormatter.format(ColorUtil.color6, new Object[] { msg });
    }
    
    public static String getVioletMsg(final int msg) {
        return MessageFormatter.format(ColorUtil.color6, new Object[] { msg });
    }
    
    public static String getColorMsg(final int serial, final String msg) {
        switch (serial) {
            case 1: {
                return MessageFormatter.format(ColorUtil.color1, new Object[] { msg });
            }
            case 2: {
                return MessageFormatter.format(ColorUtil.color2, new Object[] { msg });
            }
            case 3: {
                return MessageFormatter.format(ColorUtil.color3, new Object[] { msg });
            }
            case 4: {
                return MessageFormatter.format(ColorUtil.color4, new Object[] { msg });
            }
            case 5: {
                return MessageFormatter.format(ColorUtil.color5, new Object[] { msg });
            }
            case 6: {
                return MessageFormatter.format(ColorUtil.color6, new Object[] { msg });
            }
            default: {
                return msg;
            }
        }
    }
    
    public static String getGreenMsg(final String msg) {
        return MessageFormatter.format(ColorUtil.green, new Object[] { msg });
    }
    
    public static String getGreenMsg(final int num) {
        return MessageFormatter.format(ColorUtil.green, new Object[] { num });
    }
}
