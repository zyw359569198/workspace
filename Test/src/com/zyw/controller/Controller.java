package com.zyw.controller;

import java.io.IOException;
import java.io.InputStream;import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.zyw.util.Common;
import com.zyw.util.Utils;

public class Controller {
	
	public static List<Map<String,String>> queryShopList(Map paramter) {
		List<Map<String,String>> list=new ArrayList();
		HttpClient httpclient=HttpClients.createDefault();
		HttpGet hp=new HttpGet("https://s.taobao.com/search?q=�ֻ���&bcoffset=4&ntoffset=4&p4ppushleft=1%2C48&s=4356&sort=sale-desc");
		InputStream in=null;
		try {
			HttpResponse hr=httpclient.execute(hp);
			in=hr.getEntity().getContent();
			Document document  =Jsoup.parse(in, "utf-8","https://s.taobao.com");
			Elements script=document.getElementsByTag("head").get(0).getElementsByTag("script");
			String data=script.get(script.size()-1).data();
			list=Utils.strAnaly(data, Common.SHOP_LINK,Common.IS_TMALL,Common.LOCATION,Common.SHOPPING_ID,Common.TITLE,Common.USER_ID,Common.USER_NAME,Common.VIEW_FEE,Common.VIEW_PRICE,Common.VIEW_SALES);
			/*list.parallelStream().forEach(mp->{
				mp.forEach((key,value)->{
					System.out.println(key+":"+value);
				});
			});
			System.out.println(list.size());*/
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		return list;
	}
	
	public static List<Map<String,String>> queryShop(List<Map<String,String>> rList) {
		List<Map<String,String>> list=new ArrayList();
		HttpClient httpclient=HttpClients.createDefault();
		System.out.println(rList.get(0).get(Common.SHOP_LINK));
		HttpGet hp=new HttpGet("https:"+rList.get(0).get(Common.SHOP_LINK));
		InputStream in=null;
		try {
			HttpResponse hr=httpclient.execute(hp);
			in=hr.getEntity().getContent();
			Document document  =Jsoup.parse(in, "utf-8","https://s.taobao.com");
			System.out.println(document);
			Elements script=document.getElementsByTag("head").get(0).getElementsByTag("script");
			String data=script.get(script.size()-1).data();
			list=Utils.strAnaly(data, Common.SHOP_LINK,Common.IS_TMALL,Common.LOCATION,Common.SHOPPING_ID,Common.TITLE,Common.USER_ID,Common.USER_NAME,Common.VIEW_FEE,Common.VIEW_PRICE,Common.VIEW_SALES);
			list.parallelStream().forEach(mp->{
				mp.forEach((key,value)->{
					System.out.println(key+":"+value);
				});
			});
			System.out.println(list.size());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		return list;
	}

}
