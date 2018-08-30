package com.zyw.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PingyingUtil {
		     /**
		      * 测试main方法
		      * @param args
		      */
		     public static void main(String[] args) {
		         System.out.println(ToFirstChar("国民CP（娱乐圈）").toUpperCase()); //转为首字母大写
		         System.out.println(ToPinyin("国民CP（娱乐圈）")); 
		     }
		     /**
		      * 获取字符串拼音的第一个字母
		      * @param chinese
		      * @return
		      */
		     public static String ToFirstChar(String chinese){         
		    	 chinese=formatString(chinese);
		         String pinyinStr = "";  
		         char[] newChar = chinese.toCharArray();  //转为单个字符
		         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat(); 
		         defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
		         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
		         for (int i = 0; i < newChar.length; i++) {  
		             if (newChar[i] > 128) {  
		                 try {  
		                     pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0].charAt(0);  
		                 } catch (BadHanyuPinyinOutputFormatCombination e) {  
		                     e.printStackTrace();  
		                 }  
		             }else{  
		                 pinyinStr += newChar[i];  
		             }  
		         }  
	         return pinyinStr;  
		     }  
		    
		     /**
		      * 汉字转为拼音
		      * @param chinese
		      * @return
		      */
		     public static String ToPinyin(String chinese){
		    	 chinese=formatString(chinese);
		         String pinyinStr = "";  
		         char[] newChar = chinese.toCharArray();  
		         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
		         defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
		         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
		         for (int i = 0; i < newChar.length; i++) {  
		             if (newChar[i] > 128) {  
		                 try {  
		                     pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];  
		                 } catch (BadHanyuPinyinOutputFormatCombination e) {  
		                     e.printStackTrace();  
		                 }  
		             }else{  
		                 pinyinStr += newChar[i];  
		             }  
		         }  
		         return pinyinStr;  
		     }  
		     
		     public static String formatString(String str) {
		    	return str.replace("）", "").replace("（", "").replace("，", "").replaceAll("。", "").replaceAll("：", "").replaceAll(":", "").replaceAll("-", "").replaceAll("<", "").replaceAll("《", "").replaceAll(">", "").replaceAll("》", "");
		     }
		 }

