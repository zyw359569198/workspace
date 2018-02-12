package com.zyw.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.zyw.util.Common;
import com.zyw.util.Utils;

public class Controller {
	private static BasicCookieStore cookiestore=null;
	private static final String USERNAME="17318915967";
	private static final String PASSWORD="ZyW1987541x";
	
	public static void login(){
		HttpPost hp=new HttpPost("https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Fwww.taobao.com%2F");
		hp.setHeader(":authority", "login.taobao.com");
		hp.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		hp.setHeader("upgrade-insecure-requests", "1");
		hp.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setJavaScriptEnabled(false);  
        webClient.getOptions().setCssEnabled(false);  
        webClient.getOptions().setUseInsecureSSL(false);
        HtmlPage page=null;
        try {
			page = webClient.getPage("https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Fwww.taobao.com%2F");
			HtmlInput username = page.getHtmlElementById("TPL_username_1");
			HtmlInput password = page.getHtmlElementById("TPL_password_1");
			username.setValueAttribute(USERNAME);
			password.setValueAttribute(PASSWORD);
			HtmlButton btn = page.getHtmlElementById("J_SubmitStatic");
			btn.click();
			cookiestore = new BasicCookieStore();  
	        Set<Cookie> cookies2 =webClient.getCookieManager().getCookies();  
	        for (Cookie cookie : cookies2) {  
	        	System.out.println(cookie.getName()+"="+cookie.getValue());
	        	cookiestore.addCookie(cookie.toHttpClient());  
	        }
        }catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static List<Map<String,String>> queryShopList(Map paramter) {
		List<Map<String,String>> list=new ArrayList();
		SSLContext sslContext = null;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {  
			    //信任所有  
			    public boolean isTrusted(X509Certificate[] chain,String authType) throws CertificateException {  
			        return true;  
			    }
			}).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();
		HttpClientContext context = HttpClientContext.create();  
        context.setCookieStore(cookiestore);
		HttpGet hp=new HttpGet("https://s.taobao.com/search?q=手机壳&bcoffset=4&ntoffset=4&p4ppushleft=1%2C48&s=44&sort=sale-desc");
		hp.getParams().setParameter("http.protocol.allow-circular-redirects", true);
		try {
			HttpResponse hr=httpClient.execute(hp,context);
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
		SSLContext sslContext = null;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {  
			    //信任所有  
			    public boolean isTrusted(X509Certificate[] chain,String authType) throws CertificateException {  
			        return true;  
			    }
			}).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();
		HttpClientContext context = HttpClientContext.create();  
        context.setCookieStore(cookiestore);
		HttpGet hp=new HttpGet("https:"+rList.get(0).get(Common.SHOP_LINK)+"&search=y&orderType=hotsell_desc");
		hp.getParams().setParameter("http.protocol.allow-circular-redirects", true);
		try {
			//httpClient.setCookieStore(cookiestore);
			HttpResponse hr=httpClient.execute(hp,context);
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
		SSLContext sslContext = null;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {  
			    //信任所有  
			    public boolean isTrusted(X509Certificate[] chain,String authType) throws CertificateException {  
			        return true;  
			    }
			}).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();
		HttpClientContext context = HttpClientContext.create();  
        context.setCookieStore(cookiestore);
		HttpGet hp=new HttpGet(url);
		try {
			//httpClient.setCookieStore(cookiestore);
			HttpResponse hr=httpClient.execute(hp,context);
			Document document  =Jsoup.parse(EntityUtils.toString(hr.getEntity(), "utf-8"));
			//System.out.println(document);
			Elements totalCount=document.body().select("div");
			totalCount.forEach(e->{
				if("\"search-result\"".equalsIgnoreCase(e.className())) {
					System.out.println(e.text());
				}
			});
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
