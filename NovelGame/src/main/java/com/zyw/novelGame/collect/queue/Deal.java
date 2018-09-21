package com.zyw.novelGame.collect.queue;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import com.zyw.utils.HttpConnectionPoolUtil;
import com.zyw.utils.JsoupParse;
import com.zyw.utils.PingyingUtil;
import com.zyw.utils.Utils;
@Component
@Scope("prototype")
public class Deal {
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private CataBookRelationService cataBookRelationService;
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private Producer producer;
		
	private HttpGet httpget=null;
	private CloseableHttpClient httpClient=null;
	private HttpResponse response=null;
	private Document doc=null;
	private Book book=null;
	private Author author=null;
	private String bookName="";
	private String authorName="";
	private String isCompletion="";
	private String updateTime="";
	private String bookDesc="";
	private String imageName="";
	private String imageUrl="";
	private Catagory catagory=null;
    private CataBookRelation cataBookRelation=null;
			
	public  void init(QueueInfo queueInfo){
   	 if("0".equalsIgnoreCase(queueInfo.getType())) {
		 deal(queueInfo);
	 }else if("1".equalsIgnoreCase(queueInfo.getType())) {
		 dealBook(queueInfo);
	 }else if("2".equalsIgnoreCase(queueInfo.getType())) {
		 dealStore(queueInfo);
	 }
	}
	
	public  void dealBook(QueueInfo queueInfo) {
		book=new Book();
		author=new Author();
	     SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	     catagory=new Catagory();
       cataBookRelation=new CataBookRelation();
		try {
				 Thread.sleep((long) (5000 * Math.random()));
				 httpClient=HttpConnectionPoolUtil.getHttpClient(queueInfo.getResult().toString());
		    	 httpget = new HttpGet(queueInfo.getResult().toString());  
		         httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");          
				 response = httpClient.execute(httpget);
				 doc = Jsoup.parse(EntityUtils.toString(response.getEntity(),queueInfo.getCollect().getNovelCharset()));
				 authorName=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getAuthorName().getUrlMatch()).get(0).toString();
         	    book.setAuthorName(authorName.trim());
			    book.setAuthorNameEn(PingyingUtil.ToPinyin(book.getAuthorName()));
			    bookDesc=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getBookDesc().getUrlMatch()).get(0).toString();
				 bookName=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getBookName().getUrlMatch()).get(0).toString();
				 book.setBookName(bookName);
         	     book.setBookNameEn(PingyingUtil.ToPinyin(bookName));
         	     book.setHits(0L);
         	     if( queueInfo.getCollect().getBookInfo().getUpdateTime()==null) {
   				 book.setUpdateTime(new Date());
 			     book.setCreateTime(new Date());
         	     }else {
    				 updateTime=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getUpdateTime().getUrlMatch()).get(0).toString();
    				 book.setUpdateTime(sdf.parse(updateTime.substring(updateTime.indexOf(queueInfo.getCollect().getBookInfo().getUpdateTime().getValueDeal())+1).trim()) );
     			     book.setCreateTime(sdf.parse(updateTime.substring(updateTime.indexOf(queueInfo.getCollect().getBookInfo().getUpdateTime().getValueDeal())+1).trim())  );

         	     }
         	     if(queueInfo.getCollect().getBookInfo().getIsCompletion()==null) {
         	    	book.setIsCompletion(1);
         	     }else {
         	    	 isCompletion=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getIsCompletion().getUrlMatch()).get(0).toString();
    				 if(isCompletion.contains("已完结")) {
         			   book.setIsCompletion(0);
     			   }else {
     				   book.setIsCompletion(1);
     			   }
         	     }
				
				 String cataName=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getCataName().getUrlMatch()).get(0).toString();
				 if(queueInfo.getCollect().getBookInfo().getCataName().getValueDeal()!=null) {
					 if(queueInfo.getCollect().getBookInfo().getCataName().getValueDeal().contains("substr")) {
						 String[] str=queueInfo.getCollect().getBookInfo().getCataName().getValueDeal().split(" ");
						 cataName=cataName.substring(Integer.parseInt(str[1]), Integer.parseInt(str[2]));
					 }
				 }
				 catagory.setCataName(cataName);
		           List<Catagory> cataList=catagoryService.queryCatagory(catagory);
		           if(cataList.size()>0) {
		        	   cataBookRelation.setCataId(cataList.get(0).getCataId());
		           }else {
		        	   cataBookRelation.setCataId("10");
		           }
		            author.setAuthorName(book.getAuthorName());
 		        author.setAuthorNameEn(PingyingUtil.ToPinyin(book.getAuthorName()));
  		   List<HashMap> blist=bookService.queryBookInfo(author.getAuthorName(),null,book.getBookName(),null);
			   if(blist.size()>0) {
				   return;
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
	            book.setBookDesc(bookDesc);
    		    imageUrl=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getImageUrl().getUrlMatch()).get(0).toString();
	           imageName=book.getBookId()+imageUrl.substring(imageUrl.lastIndexOf("."));
		       book.setImageUrl("/images/data/"+imageName);
		       bookService.insert(book);
		        cataBookRelation.setBookId(book.getBookId());
		             cataBookRelation.setId(UUID.randomUUID().toString());
		             cataBookRelationService.insert(cataBookRelation);
		             if(imageUrl.contains("http")) {
			             Utils.saveImages(imageUrl,book.getImageUrl());

		             }else {
			             Utils.saveImages(queueInfo.getCollect().getNovelSiteUrl()+imageUrl,book.getImageUrl());
		             }
		             if(queueInfo.getCollect().getBookInfo().getStoreCataUrl()!=null) {
		            	 httpClient=HttpConnectionPoolUtil.getHttpClient(queueInfo.getCollect().getNovelSiteUrl());
				    	 httpget = new HttpGet(JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getStoreCataUrl().getUrlMatch()).get(0).toString());  
				         httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");          
						 response = httpClient.execute(httpget);
						 doc = Jsoup.parse(EntityUtils.toString(response.getEntity(),queueInfo.getCollect().getNovelCharset()));
		             }
				      Map mp=new HashMap();
				      QueueInfo queue=new QueueInfo();
					 mp.put("bookId", book.getBookId());
					 queue.setCollect(queueInfo.getCollect());
					 queue.setType("2");
					 queue.setMark(mp);
					 queue.setResultList(JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getStoreRule().getUrlMatch()));
					 producer.add(queue);
				//logger.info(EntityUtils.toString(response.getEntity()));
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public   void dealStore(QueueInfo queueInfo) {
		String preStoreId="";
	    String nextStoreId="";
	    String curentStoreId="";
	    long count=1;
		String  storeName="";
	       try {
	    	   for(Object item:queueInfo.getResultList()) {
	    		   if(!item.toString().contains("http")) {
	    			   item=queueInfo.getCollect().getNovelSiteUrl()+item;
	    		   }
				 Thread.sleep((long) (5000 * Math.random()));
				 if(count==1) {
		    		   preStoreId="0";
			    	   curentStoreId=UUID.randomUUID().toString();
		    	   }else {
		    		   curentStoreId=nextStoreId;
		    	   }
		    	   if(count==queueInfo.getResultList().size()) {
		    		   nextStoreId="0";
		    	   }else {
		    		   nextStoreId=UUID.randomUUID().toString();
		    	   }
	    	   Store store=new Store();
	    	   StoreData storeData=new StoreData();
	    	   store.setBookId(queueInfo.getMark().get("bookId").toString());
	    	   store.setId(UUID.randomUUID().toString());
	    	   store.setStoreUrl(item.toString());
	    	   store.setPreStoreId(preStoreId);
	    	   store.setNextStoreId(nextStoreId);
	    	   store.setStoreId(curentStoreId);
	    	   httpClient=HttpConnectionPoolUtil.getHttpClient(item.toString());
		    	 httpget = new HttpGet(item.toString());  
		         httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");          
				 response = httpClient.execute(httpget);
				 doc = Jsoup.parse(EntityUtils.toString(response.getEntity(),queueInfo.getCollect().getNovelCharset()));
		   storeName=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getStoreInfo().getStoreName().getUrlMatch()).get(0).toString();
		   String storeContent=JsoupParse.parse(doc, queueInfo.getCollect().getBookInfo().getStoreInfo().getStoreContent().getUrlMatch()).get(0).toString();
		   if(queueInfo.getCollect().getNovelCharset().equalsIgnoreCase("gbk")) {
		        storeContent=Utils.getUTF8StringFromGBKString(storeContent);
		   }
     	   storeData.setStoreContent(storeContent.getBytes());
     	   storeData.setId(UUID.randomUUID().toString());
     	   storeData.setStoreId(store.getStoreId());
	    	   store.setStoreName(storeName);
     	   store.setCreateTime(new Date());
     	   store.setOrderIndex(count);
     	   storeService.insert(store);
     	   storeService.insertStoreData(storeData);
     	   //更新最新章节
     	   //if(count==liClass.size()||count==1) {
     		   Book record=new Book();
     		   record.setBookId(store.getBookId());
     		   record.setLastStoreId(store.getStoreId());
     		   bookService.updateByBookID(record);
     		    preStoreId=curentStoreId;
		     	 count++;
	    	   }
     	   //}
	       } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public   void deal(QueueInfo queueInfo) {		
			 try {
					 Thread.sleep((long) (5000 * Math.random()));
					 httpClient=HttpConnectionPoolUtil.getHttpClient(queueInfo.getResult().toString());
			    	 httpget = new HttpGet(queueInfo.getResult().toString());  
			         httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");          
					 response = httpClient.execute(httpget);
					 doc = Jsoup.parse(EntityUtils.toString(response.getEntity(),queueInfo.getCollect().getNovelCharset()));
					 QueueInfo queue=null;
					 int count=0;
					 List resultList=JsoupParse.parse(doc, queueInfo.getCollect().getBookRule().getUrlMatch());
					 for(Object item:resultList) {
						 queue=new QueueInfo();
						 queue.setCollect(queueInfo.getCollect());
						 queue.setType("1");
						 queue.setResult(item.toString());
						 producer.add(queue);
						 /*if(count>10) {
							 break;
						 }*/
						 count++;
					 };
					
					 
					//logger.info(EntityUtils.toString(response.getEntity()));
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public static void main(String[] args) {
		System.out.println("substr 0 2".split(" ")[2]);
	}

}
