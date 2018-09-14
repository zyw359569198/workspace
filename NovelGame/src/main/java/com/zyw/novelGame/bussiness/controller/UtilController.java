package com.zyw.novelGame.bussiness.controller;

import java.io.Closeable;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zyw.novelGame.bussiness.service.AuthorService;
import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.CataBookRelationService;
import com.zyw.novelGame.bussiness.service.CatagoryService;
import com.zyw.novelGame.bussiness.service.StoreService;
import com.zyw.novelGame.model.Author;
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.CataBookRelation;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Store;
import com.zyw.novelGame.model.StoreData;
import com.zyw.utils.PingyingUtil;
import com.zyw.utils.Utils;

@RestController
@RequestMapping("/util")
public class UtilController {
	public static final  Logger logger=LoggerFactory.getLogger(UtilController.class);
	
	private static final int MAX_BOOK_NUMS=1000000;
	
	private static final int MAX_TOPIC_NUMS=1000000;

	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private CataBookRelationService cataBookRelationService;
	
	@Autowired
	private StoreService storeService;
	
	
	@RequestMapping(value="/init",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map init(HttpServletRequest request,HttpServletResponse response1) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();	
		CloseableHttpClient  httpclient = null;
		for(int i=0;i<10;i++) {
        try {  
        	//采用绕过验证的方式处理https请求  
			SSLContext sslcontext = Utils.createIgnoreVerifySSL();
			//设置协议http和https对应的处理socket链接工厂的对象  
	        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
	            .register("http", PlainConnectionSocketFactory.INSTANCE)  
	            .register("https", new SSLConnectionSocketFactory(sslcontext))  
	            .build();  
	        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
	        HttpClients.custom().setConnectionManager(connManager);
    		httpclient =  HttpClients.custom().setConnectionManager(connManager).build();
            // 创建httpget.    
            HttpGet httpget = new HttpGet("https://txt2.cc/map/"+i+"/");  
            httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体    
                HttpEntity entity = response.getEntity();  
                System.out.println("--------------------------------------");  
                // 打印响应状态    
                System.out.println(response.getStatusLine());  
                if (entity != null) {  
                    // 打印响应内容长度    
                    System.out.println("Response content length: " + entity.getContentLength());  
                   Document doc = Jsoup.parse(EntityUtils.toString(entity));
                   Elements links = doc.getElementsByTag("a");
                   int bookNums=0;
                   for(Element e:links) {
                	   System.out.println(e.attr("href"));
                	   System.out.println(e.text());
                	   if(e.attr("href").startsWith("http")&&e.attr("href").contains("book")) {
                		   Book book=new Book();
                		   Author author=new Author();
                    	   httpget = new HttpGet(e.attr("href")); 
                           httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
                    	   response = httpclient.execute(httpget); 
                    	   entity = response.getEntity();
                    	   doc = Jsoup.parse(EntityUtils.toString(entity));
                    	   Elements h1=doc.getElementsByTag("h1");
                    	   book.setBookName(h1.text());
                    	   book.setBookNameEn(PingyingUtil.ToPinyin(h1.text()));
                    	   book.setHits(0L);
                    	   Elements ems=doc.getElementsByTag("em");
                    	   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    	   for(Element em:ems) {
                    		   if(em.text().contains("作者")) {
                    			   book.setAuthorName(em.text().substring(em.text().indexOf("：")+1).trim());
                    			   book.setAuthorNameEn(PingyingUtil.ToPinyin(book.getAuthorName()));
                    		   }else if(em.text().contains("状态")) {
                    			   if("已完结".equalsIgnoreCase(em.text().substring(em.text().indexOf("：")+1).trim())) {
                        			   book.setIsCompletion(0);
                    			   }else {
                    				   book.setIsCompletion(1);
                    			   }
                    		   }else if(em.text().contains("更新时间")) {                    			   
                    			   book.setUpdateTime(sdf.parse(em.text().substring(em.text().indexOf("：")+1).trim()) );
                    			   book.setCreateTime(sdf.parse(em.text().substring(em.text().indexOf("：")+1).trim()) );
                    		   }
                    	   }  
                    	       Elements placeClass=doc.getElementsByClass("place");
                    	       Catagory catagory=new Catagory();
                    	       CataBookRelation cataBookRelation=new CataBookRelation();
                    	       catagory.setCataName(placeClass.get(0).children().get(1).text());
            		           List<Catagory> cataList=catagoryService.queryCatagory(catagory);
            		           if(cataList.size()>0) {
            		        	   cataBookRelation.setCataId(cataList.get(0).getCataId());
            		           }else {
            		        	   cataBookRelation.setCataId("10");
            		           }
                    	       Elements introClass=doc.getElementsByClass("intro");
                    		   author.setAuthorName(book.getAuthorName());
                    		   author.setAuthorNameEn(PingyingUtil.ToPinyin(book.getAuthorName()));
                    		   List<HashMap> blist=bookService.queryBookInfo(author.getAuthorName(),null,book.getBookName(),null);
                			   if(blist.size()>0) {
                				   continue;
                			   }
                    		   List<Author> alist=authorService.queryAuthorInfo(author);
                    		   if(alist.size()>0) {
                    			   book.setAuthorId(alist.get(0).getAuthorId());
                    		   }else {
                    			   author.setAuthorId(UUID.randomUUID().toString());
                    			   author.setId(UUID.randomUUID().toString());
                    			   authorService.insert(author);
                    			   book.setAuthorId(author.getAuthorId());
                    		   }
                    		       book.setBookId(UUID.randomUUID().toString());
                    		       book.setId(UUID.randomUUID().toString());
                    		       book.setBookDesc(introClass.get(0).text());
                    		       Element lfClass=doc.getElementsByClass("lf").get(1).getElementsByTag("img").get(0);
                    		       String imageName=book.getBookId()+lfClass.attr("src").substring(lfClass.attr("src").lastIndexOf("."));
                    		       book.setImageUrl("/images/data/"+imageName);
                    		       bookService.insert(book);
                    		       cataBookRelation.setBookId(book.getBookId());
                    		       cataBookRelation.setId(UUID.randomUUID().toString());
                    		       cataBookRelationService.insert(cataBookRelation);
                    		       Utils.saveImages("https://txt2.cc"+lfClass.attr("src"),book.getImageUrl());
                    		       Elements liClass=doc.getElementsByClass("mulu").get(0).getElementsByTag("li");
                    		       String preStoreId="";
                    		       String nextStoreId="";
                    		       String curentStoreId="";
                    		       long count=1;
                    		       for(Element element:liClass) {
                    		    	   Thread.sleep(200);
                    		    	   if(count==1) {
                    		    		   preStoreId="0";
                        		    	   curentStoreId=UUID.randomUUID().toString();
                    		    	   }else {
                    		    		   curentStoreId=nextStoreId;
                    		    	   }
                    		    	   if(count==liClass.size()) {
                    		    		   nextStoreId="0";
                    		    	   }else {
                    		    		   nextStoreId=UUID.randomUUID().toString();
                    		    	   }
                    		    	   Store store=new Store();
                    		    	   StoreData storeData=new StoreData();
                    		    	   store.setBookId(book.getBookId());
                    		    	   store.setId(UUID.randomUUID().toString());
                    		    	   store.setStoreName(element.text());
                    		    	   store.setStoreUrl(element.getElementsByTag("a").attr("href"));
                    		    	   store.setPreStoreId(preStoreId);
                    		    	   store.setNextStoreId(nextStoreId);
                    		    	   store.setStoreId(curentStoreId);
                    		    	   httpget = new HttpGet(element.getElementsByTag("a").attr("href")); 
                                	   response = httpclient.execute(httpget); 
                                	   entity = response.getEntity();
                                	   doc = Jsoup.parse(EntityUtils.toString(entity));
                                	   Element ydClass=doc.getElementsByClass("yd_text2").get(0);
                                	   storeData.setStoreContent(ydClass.html().getBytes());
                                	   storeData.setId(UUID.randomUUID().toString());
                                	   storeData.setStoreId(curentStoreId);
                                	   store.setCreateTime(new Date());
                                	   store.setOrderIndex(count);
                                	   storeService.insert(store);
                                	   storeService.insertStoreData(storeData);
                                	   //更新最新章节
                                	   //if(count==liClass.size()||count==1) {
                                		   Book record=new Book();
                                		   record.setBookId(book.getBookId());
                                		   record.setLastStoreId(curentStoreId);
                                		   bookService.updateByBookID(record);
                                	   //}
                                	   preStoreId=curentStoreId;
                                	   if(count>MAX_TOPIC_NUMS) {
                                		   break;
                                	   }
                                	   count++;
                    		       };
                    		if(bookNums>MAX_BOOK_NUMS) {
                         	   break;
                    		}
                    		bookNums++;
                	   }
                   };
                   //System.out.println("Response content: " + doc);  
                }  
                System.out.println("------------------------------------");  
            } catch (java.text.ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (KeyManagementException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} finally {  
            // 关闭连接,释放资源    
            try {  
				httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
		}
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}

}
