package com.zyw.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
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
			}else {
				resultList.add(element.text());
			}
		});
		resultList.stream().forEach(x->{
			System.out.println(x);
		});
		return resultList;
		
	}
	
	public static void main(String[] args) {
    		CloseableHttpClient httpClient=HttpConnectionPoolUtil.getHttpClient("https://txt2.cc");
    		HttpGet httpget = new HttpGet("https://txt2.cc/book/douluozhizuixing/");  
            httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            try {
				HttpResponse response = httpClient.execute(httpget);
				Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
				//parse(doc,"body > a[href]");
				parse(doc,"div.jieshao div.rt div.msg em:eq(2)");
				//logger.info(EntityUtils.toString(response.getEntity()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	HttpConnectionPoolUtil.closeConnectionPool();
	}

}
