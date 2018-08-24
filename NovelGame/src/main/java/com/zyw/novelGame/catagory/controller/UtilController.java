package com.zyw.novelGame.catagory.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

import com.zyw.novelGame.catagory.service.AuthorService;
import com.zyw.novelGame.catagory.service.BookService;
import com.zyw.novelGame.catagory.service.CataBookRelationService;
import com.zyw.novelGame.catagory.service.CatagoryService;
import com.zyw.novelGame.catagory.service.StoreService;
import com.zyw.novelGame.model.Author;
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.CataBookRelation;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Store;

@RestController
@RequestMapping("/util")
public class UtilController {
	public static final  Logger logger=LoggerFactory.getLogger(UtilController.class);
	
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
		CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            // 创建httpget.    
            HttpGet httpget = new HttpGet("https://txt2.cc/map/0/");  
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
                   for(Element e:links) {
                	   System.out.println(e.attr("href"));
                	   System.out.println(e.text());
                	   if(e.attr("href").startsWith("http")&&e.attr("href").contains("book")) {
                		   Book book=new Book();
                		   Author author=new Author();
                    	   httpget = new HttpGet(e.attr("href")); 
                    	   response = httpclient.execute(httpget); 
                    	   entity = response.getEntity();
                    	   doc = Jsoup.parse(EntityUtils.toString(entity));
                    	   Elements h1=doc.getElementsByTag("h1");
                    	   book.setBookName(h1.text());
                    	   Elements ems=doc.getElementsByTag("em");
                    	   for(Element em:ems) {
                    		   if(em.text().contains("作者")) {
                    			   book.setAuthorName(em.text().substring(em.text().indexOf("：")+1).trim());
                    		   }else if(em.text().contains("状态")) {
                    			   if("已完结".equalsIgnoreCase(em.text().substring(em.text().indexOf("：")+1).trim())) {
                        			   book.setIsCompletion(0);
                    			   }else {
                    				   book.setIsCompletion(1);
                    			   }
                    		   }else if(em.text().contains("更新时间")) {                    			   
                    			   book.setUpdateTime(DateFormat.getDateInstance().parse(em.text().substring(em.text().indexOf("：")+1).trim()) );
                    			   book.setCreateTime(DateFormat.getDateInstance().parse(em.text().substring(em.text().indexOf("：")+1).trim()) );
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
                    		   Book qbook=new Book();
                			   qbook.setBookName(book.getBookName());
                			   qbook.setAuthorName(author.getAuthorName());
                			   List<Book> blist=bookService.queryBookInfo(qbook);
                			   if(blist.size()>0) {
                				   break;
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
                    		       book.setImageUrl(lfClass.attr("src"));
                    		       bookService.insert(book);
                    		       cataBookRelation.setBookId(book.getBookId());
                    		       cataBookRelation.setId(UUID.randomUUID().toString());
                    		       cataBookRelationService.insert(cataBookRelation);
                    		       Elements liClass=doc.getElementsByClass("mulu").get(0).getElementsByTag("li");
                    		       String preStoreId="";
                    		       String nextStoreId="";
                    		       String curentStoreId="";
                    		       int count=0;
                    		       for(Element element:liClass) {
                    		    	   Thread.sleep(500);
                    		    	   if(count==0) {
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
                                	   store.setStoreContent(ydClass.html());
                                	   store.setCreateTime(new Date());
                                	   storeService.insert(store);
                                	   preStoreId=curentStoreId;
                                	   count++;
                    		       };
                    		       
                    	   break;
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
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}

}
