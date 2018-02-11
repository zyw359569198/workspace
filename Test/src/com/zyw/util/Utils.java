package com.zyw.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
	
	public static List<Map<String,String>> strAnaly(String content,String... paramters) {
		List<Map<String,String>> list=new ArrayList();
		Map<String,String> productMap=null;
		int startIndex=0;
		int endIndex=0;
		int maxIndex=0;
		int nowIndex=0;
		boolean flag=false;
		String s1="";
		//System.out.println((content.split(Common.USER_ID)).length);
		while(true) {
			productMap=new HashMap();
			nowIndex=maxIndex;
			for(String str:paramters) {
				startIndex=content.indexOf(str, nowIndex)+str.length()+1;
				if(content.lastIndexOf(str)==content.indexOf(str, nowIndex)) {
					flag=true;
				}
				s1=content.substring(startIndex,content.length());
				endIndex=s1.indexOf(",",0)+startIndex;
				if(endIndex>maxIndex) {
					maxIndex=endIndex;
				}
				String url="";
				String sb="";
				try {
					sb=content.substring(startIndex,endIndex).replace("\\u", "");
					url=URLDecoder.decode( sb ,"utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				productMap.put(str, url);
				
			}
			list.add(productMap);
			if(flag) {
				break;
			}
		}
		
		return list;
		
	}

}