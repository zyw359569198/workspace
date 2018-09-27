package com.zyw.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsoupParse {
	private static Logger logger = LoggerFactory.getLogger(JsoupParse.class);
	
	public static List parse(Document doc,String tag) {
		List resultList=new ArrayList();
		Elements elements = null;
		Boolean first=true;
		elements=doc.select(tag);		
		elements.forEach(element->{
			if(element.hasAttr("href")&&tag.contains("href")) {
				resultList.add(element.attr("href"));
			}else if(element.hasAttr("src")&&tag.contains("src")){
				resultList.add(element.attr("src"));
			}else {
				resultList.add(element.html());
			}
		});
/*        resultList.stream().forEach(x->{
			logger.info(x.toString());
		});*/
		return resultList;
		
	}
	
	public static void main(String[] args) {
/*        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("AST","1537521250443bc145d5bc3");
        cookie.setDomain(".zongheng.com");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        BasicClientCookie cookie1 = new BasicClientCookie("ZHID","45D2FD7BC0C712C6D58717AA8FD1DE48");
        cookie1.setDomain(".zongheng.com");
        cookie1.setPath("/");
        cookieStore.addCookie(cookie1);
        HttpConnectionPoolUtil.setCookieStore(cookieStore);*/
    		CloseableHttpClient httpClient=HttpConnectionPoolUtil.getHttpClient("https://txt2.cc");
    		HttpGet httpget = new HttpGet("https://txt2.cc/book/zhongshengnvxiuxianchuan/"); 
    		//httpget.setHeader("Referer","http://book.zongheng.com");
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
            try {
				HttpResponse response = httpClient.execute(httpget);
				Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity(),"gbk"));
				//parse(doc,"body > a[href]");
				parse(doc,"div.jieshao div.lf img[src]");
				//logger.info(EntityUtils.toString(response.getEntity()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	HttpConnectionPoolUtil.closeConnectionPool();
	}

}
