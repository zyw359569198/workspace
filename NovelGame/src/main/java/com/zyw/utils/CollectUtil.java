package com.zyw.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

import com.zyw.novelGame.bussiness.service.AuthorService;
import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.impl.AuthorServiceImpl;
import com.zyw.novelGame.bussiness.service.impl.BookServiceImpl;
import com.zyw.novelGame.model.Author;
import com.zyw.novelGame.model.Book;

public class CollectUtil {
	
	public static void main(String[] args) {
		AuthorService  authorService=new AuthorServiceImpl() ;
		BookService bookService=new BookServiceImpl();
		
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
                    		   }
                    	   }
                    	       Elements introClass=doc.getElementsByClass("intro");
                    		   author.setAuthorName(book.getBookName());
                			   List<HashMap> blist=bookService.queryBookInfo(author.getAuthorName(),null,book.getBookName(),null);
                			   if(blist.size()>0) {
                				   break;
                			   }
                    		   List<Author> alist=authorService.queryAuthorInfo(author);
                    		   if(alist.size()>0) {
                    			   book.setAuthorId(alist.get(0).getAuthorId());
                    		   }else {
                    			   String authorId=UUID.randomUUID().toString();
                    			   author.setAuthorId(authorId);
                    			   author.setId(UUID.randomUUID().toString());
                    			   authorService.insert(author);
                    			   book.setAuthorId(authorId);
                    		   }
                    		       book.setBookId(UUID.randomUUID().toString());
                    		       book.setId(UUID.randomUUID().toString());
                    		       book.setBookDesc(introClass.get(0).text());
                    		       bookService.insert(book);
                    	   break;
                	   }
                   };
                   //System.out.println("Response content: " + doc);  
                }  
                System.out.println("------------------------------------");  
            } catch (java.text.ParseException e1) {
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
	}

}
