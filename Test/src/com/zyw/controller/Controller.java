package com.zyw.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zyw.util.Common;
import com.zyw.util.Utils;

public class Controller {
	private static CookieStore cookiestore=null;
	
	public static void login(){
		DefaultHttpClient  httpClient=new DefaultHttpClient();
		HttpPost hp=new HttpPost("https://login.taobao.com/member/login.jhtml");
		hp.setHeader(":authority", "login.taobao.com");
		hp.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		hp.setHeader("origin", "https://login.taobao.com");
		hp.setHeader("upgrade-insecure-requests", "1");
		hp.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
		hp.setHeader("referer", "https://login.taobao.com/member/login.jhtml");
		hp.setHeader("content-type", "application/x-www-form-urlencoded");
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair("TPL_username","zyw359569198"));
		formParams.add(new BasicNameValuePair("TPL_password",""));
		formParams.add(new BasicNameValuePair("ncoSig",""));
		formParams.add(new BasicNameValuePair("ncoSessionid",""));
		formParams.add(new BasicNameValuePair("ncoToken","4adbacc5840661494f988f798750c313ab77c2d2"));
		formParams.add(new BasicNameValuePair("slideCodeShow","false"));
		formParams.add(new BasicNameValuePair("useMobile","false"));
		formParams.add(new BasicNameValuePair("lang","zh_CN"));
		formParams.add(new BasicNameValuePair("loginsite","0"));
		formParams.add(new BasicNameValuePair("newlogin","0"));
		formParams.add(new BasicNameValuePair("TPL_redirect_url","https://shop117388594.taobao.com/search.htm?spm=a1z10.1-c.0.0.71c0a1d8bKVfJR&search=y"));
		formParams.add(new BasicNameValuePair("from","tb"));
		formParams.add(new BasicNameValuePair("fc","default"));
		formParams.add(new BasicNameValuePair("style","default"));
		formParams.add(new BasicNameValuePair("css_style",""));
		formParams.add(new BasicNameValuePair("keyLogin","false"));
		formParams.add(new BasicNameValuePair("qrLogin","true"));
		formParams.add(new BasicNameValuePair("newMini","false"));
		formParams.add(new BasicNameValuePair("newMini2","false"));
		formParams.add(new BasicNameValuePair("tid",""));
		formParams.add(new BasicNameValuePair("loginType","3"));
		formParams.add(new BasicNameValuePair("minititle",""));
		formParams.add(new BasicNameValuePair("minipara",""));
		formParams.add(new BasicNameValuePair("pstrong",""));
		formParams.add(new BasicNameValuePair("sign",""));
		formParams.add(new BasicNameValuePair("need_sign",""));
		formParams.add(new BasicNameValuePair("isIgnore",""));
		formParams.add(new BasicNameValuePair("full_redirect",""));
		formParams.add(new BasicNameValuePair("sub_jump",""));
		formParams.add(new BasicNameValuePair("popid",""));
		formParams.add(new BasicNameValuePair("callback",""));
		formParams.add(new BasicNameValuePair("guf",""));
		formParams.add(new BasicNameValuePair("not_duplite_str",""));
		formParams.add(new BasicNameValuePair("need_user_id",""));
		formParams.add(new BasicNameValuePair("poy",""));
		formParams.add(new BasicNameValuePair("gvfdcname","10"));
		formParams.add(new BasicNameValuePair("gvfdcre","68747470733A2F2F6C6F67696E2E74616F62616F2E636F6D2F6D656D6265722F6C6F676F75742E6A68746D6C3F73706D3D61317A31302E332D632E3735343839343433372E372E37353563313033616A6C7834725626663D746F70266F75743D7472756526726564697265637455524C3D687474707325334125324625324673686F703131373338383539342E74616F62616F2E636F6D2532467365617263682E68746D25334673706D25334461317A31302E312D632E302E302E3731633061316438624B56664A5225323673656172636825334479"));
		formParams.add(new BasicNameValuePair("from_encoding",""));
		formParams.add(new BasicNameValuePair("sub",""));
		formParams.add(new BasicNameValuePair("TPL_password_2","11301bbe2ba4c2986acbb4755174eab9ac11c7ad91eb1b3e00124423eae5c0698dbf7c033c43c62280c4293d3adcab7e2b9dcdf4fb4d3bc541900f3c8036874d325e4617b2d9845fc95c32e1405f71ffdb79f751fa2606418a4ef2113724117cc3cc0d97dffdf17bac3241b536d224382b09f724db652280c51b0d701a67bc01"));
		formParams.add(new BasicNameValuePair("loginASR","1"));
		formParams.add(new BasicNameValuePair("loginASRSuc","1"));
		formParams.add(new BasicNameValuePair("allp",""));
		formParams.add(new BasicNameValuePair("oslanguage","zh-CN"));
		formParams.add(new BasicNameValuePair("sr","1366*768"));
		formParams.add(new BasicNameValuePair("osVer","windows|6.1"));
		formParams.add(new BasicNameValuePair("naviVer","chrome|63.03239132"));
		formParams.add(new BasicNameValuePair("osACN","Mozilla"));
		formParams.add(new BasicNameValuePair("osAV","5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36"));
		formParams.add(new BasicNameValuePair("osPF","Win32"));
		formParams.add(new BasicNameValuePair("miserHardInfo",""));
		formParams.add(new BasicNameValuePair("appkey","00000000"));
		formParams.add(new BasicNameValuePair("nickLoginLink",""));
		formParams.add(new BasicNameValuePair("mobileLoginLink","https://login.taobao.com/member/login.jhtml?redirectURL=https://shop117388594.taobao.com/search.htm?spm=a1z10.1-c.0.0.71c0a1d8bKVfJR&search=y&useMobile=true"));
		formParams.add(new BasicNameValuePair("showAssistantLink",""));
		formParams.add(new BasicNameValuePair("um_token","HV01PAAZ0b8702e16eb842625a803c4a004d76f6"));
		formParams.add(new BasicNameValuePair("ua","107#ssPa/wS9s1uiZRN0kUQeXKX1lnbIg/lKXKtPiIZ9g2NslKxQlOB39/Eqlnxgg/hElDmgeAfZgLZ+uX7W5uhZgoVnX/2W8aRoKTeIeuAUTGcfJGdgZxqnaD5Ge5dM3gCW3xhRgRRH1BjuUWA5SmA8KCKMae25i8dL6sq9AFotbk3gcWdEZaikEos5XXEpzcOEzEFXXQ1+IX9LHFElY8qjl8j3XvLFPqYwPyZuuQR+VXxREpl3dOgth3jthvgy+HOD7yEHoxbu1dxqF0TBrYqUtu+0hvuk2QqH7T363cvlfJo0Q0jJrYEc3YVXhm3K7y/529p235vbb5aapfpka+jtZYOnwZub7fCn6Q/mvk0iDdc3QmFRrhTG3nha86igJlYS69emtd1rE2sq95QghVhbePEnZdg7WHHhijn8uTkDxD5wX0ELqSQalertefl7KK94gTt5GZSvZJdpc1DzwV/DGr9ftvgmBpK4gTt5GZSvF1BoZ1LshVgo83jchBQERJhWyiiyC1be10xruk9krqnOqqjx3Tt7yEpg7y9B3kJXfmkoZBQja/nIGnFq86yy7jMBsflihvw8F7zcvvyiqWytFMi18c922Qil6HpR3c1v1k2tx0XPqtheuuTvhdDgKEjoNfpb92cbfmU3X1pjhV/xqqjx3yQYPErHmTDmo2RZQJAec6WPteiDtV/IIJuSgFpYByg7tvoZQJAedZpywVrxCeeEwBujgQeT7LWH30SIl6G9Z09ybhEU3hilI2Cs2HML6QCL3B0cQ56oXghJeWpUGOYd8Bpijp+Bmg9JC5dCFBaaFBQpFsFZ8C+DCf3y7jM6SkEkucwS0mUwFTtmUeeGuq/03mKn2Q3HilVj8BwtQcztcvyiqWyhFMidhZrkyXQ/miLi37cI8f8bvx9krq/PtqOo3dXs7FeJiDOP30Sw16GbD0DTbenj9VHn97922QKmkpujC5IF0mUwFyQ+EaVDG/gotclhgTTAPKQoQgBVx7Bmh5QiatlWIrY/QxlvkT/gyTQ2Cm1312vbp9QnhVgrChTko9pnJDH6+cia30fqvp4rbdXnEargp3Vth6O2Rg+Tmij2XDz5tJAec6+xaOX4dSpI9TuSBEVrg9gn8jfOUT5iGJDmhOVDeV+S3Fl8+BiH6glnhkwnZ6mtdFlbdz+DeVCYciVUmErKkf/ztB2vbTsicZp7auDUpO+Fe1ikPxODPyFXXZXMH2XJ6XfPDKTnlcREXXMFPcOD"));
		HttpEntity entity=null;
		try {
			entity = new UrlEncodedFormEntity(formParams, "UTF-8");
			hp.setEntity(entity);
			HttpResponse  hr=httpClient.execute(hp);
			cookiestore=httpClient.getCookieStore();
			System.out.println(cookiestore);
			Document document  =Jsoup.parse(EntityUtils.toString(hr.getEntity(), "utf-8"));
			Elements script=document.getElementsByTag("head").get(0).getElementsByTag("script");
			String data=script.get(script.size()-1).data();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static List<Map<String,String>> queryShopList(Map paramter) {
		List<Map<String,String>> list=new ArrayList();
		DefaultHttpClient  httpClient=new DefaultHttpClient();
		HttpGet hp=new HttpGet("https://s.taobao.com/search?q=�ֻ���&bcoffset=4&ntoffset=4&p4ppushleft=1%2C48&s=44&sort=sale-desc");
		hp.getParams().setParameter("http.protocol.allow-circular-redirects", true);
		try {
			httpClient.setCookieStore(cookiestore);
			HttpResponse hr=httpClient.execute(hp);
			Document document  =Jsoup.parse(EntityUtils.toString(hr.getEntity(), "utf-8"));
			Elements script=document.getElementsByTag("head").get(0).getElementsByTag("script");
			String data=script.get(script.size()-1).data();
			list=Utils.jsonAnaly(data, Common.IS_TMALL,Common.SHOP_LINK,Common.LOCATION,Common.SHOPPING_ID,Common.TITLE,Common.USER_ID,Common.USER_NAME,Common.VIEW_FEE,Common.VIEW_PRICE,Common.VIEW_SALES);
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
		}
		
		return list;
	}
	
	public static List<Map<String,String>> queryShop(List<Map<String,String>> rList) {
		List<Map<String,String>> list=new ArrayList();
		DefaultHttpClient  httpClient=new DefaultHttpClient();
		HttpGet hp=new HttpGet("https:"+rList.get(0).get(Common.SHOP_LINK)+"&search=y&orderType=hotsell_desc");
		hp.getParams().setParameter("http.protocol.allow-circular-redirects", true);
		try {
			httpClient.setCookieStore(cookiestore);
			HttpResponse hr=httpClient.execute(hp);
			System.out.println(hr.getHeaders("url-hash")[0].getValue());
			Document document  =Jsoup.parse(EntityUtils.toString(hr.getEntity(), "utf-8"));
			Element totalCount=document.getElementById("J_ShopAsynSearchURL");
			String shopingTotal=queryShopingTotal(hr.getHeaders("url-hash")[0].getValue().substring(0, hr.getHeaders("url-hash")[0].getValue().lastIndexOf("/"))+totalCount.attr("value"));			
			System.out.println(shopingTotal);
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
		}
		
		return list;
	}
	
	public static String queryShopingTotal(String url) {
		String result="";
		DefaultHttpClient  httpClient=new DefaultHttpClient();
		HttpGet hp=new HttpGet("https://shop35339694.taobao.com/i/asynSearch.htm?mid=w-14914601192-0&wid=14914601192&path=/search.htm&user_number_id=57520996&search=y&orderType=hotsell_desc");
		//hp.getParams().setParameter("http.protocol.allow-circular-redirects", true);
		hp.setHeader(":authority", "shop35339694.taobao.com");
		hp.setHeader("accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
		hp.setHeader("x-requested-with", "XMLHttpRequest");
		hp.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
		hp.setHeader("referer", "https://shop35339694.taobao.com/i/asynSearch.htm?mid=w-14914601192-0&wid=14914601192&path=/search.htm&user_number_id=57520996&search=y&orderType=hotsell_desc");
		hp.setHeader("content-type", "application/x-www-form-urlencoded");
		try {
			httpClient.setCookieStore(cookiestore);
			HttpResponse hr=httpClient.execute(hp);
			Document document  =Jsoup.parse(EntityUtils.toString(hr.getEntity(), "utf-8"));
			System.out.println(document);
			Elements totalCount=document.getElementsByClass("search-result");
			System.out.println(totalCount);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

}
