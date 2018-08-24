package com.zyw.novelGame.catagory.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zyw.novelGame.catagory.service.BookService;
import com.zyw.novelGame.catagory.service.CatagoryService;
import com.zyw.novelGame.catagory.service.StoreService;
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Store;

@RestController
@RequestMapping("/book")
public class BookContronller {
	
	public static final  Logger logger=LoggerFactory.getLogger(BookContronller.class);
	
	@Autowired
	private BookService bookService;
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private StoreService storeService;
	
	@RequestMapping(value="/init",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map init(HttpServletRequest request,HttpServletResponse response) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		CompletableFuture<List<Book>> bookHitsFuture=null;
		CompletableFuture<Object> bookCreateTimeFuture=null;
		CompletableFuture<Object> catagoryFuture=null;
		CompletableFuture<Object> bookUpdateInfoFuture=null;
		try {
			catagoryFuture=CompletableFuture.supplyAsync(()->{
				return catagoryService.queryCatagory(new Catagory());
			}).thenApplyAsync(list->{
				List<Object> li=new ArrayList<Object>();
					list.stream().forEach(catagory->{
						if(!catagory.getCataId().equalsIgnoreCase("0")) {//排除首页
						li.add(bookService.queryBookRelationByCataID(catagory.getCataId(),7));
						}
					});
				return li;
			});
			bookHitsFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookByHits(6);
			});
			bookCreateTimeFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookByCreateTime();
			});
			bookUpdateInfoFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookUpdateInfo(null);
			});
			CompletableFuture.allOf(bookHitsFuture,catagoryFuture,bookCreateTimeFuture,bookUpdateInfoFuture);
			dataMap.put("bkl", bookHitsFuture.get(30, TimeUnit.SECONDS));
			dataMap.put("bcl", bookCreateTimeFuture.get(30, TimeUnit.SECONDS));
			dataMap.put("tjl", catagoryFuture.get(30, TimeUnit.SECONDS));
			dataMap.put("bul", bookUpdateInfoFuture.get(30, TimeUnit.SECONDS));
		}catch(Exception e) {
			resultMap.put("errorCode", 10086);
			e.printStackTrace();
		}
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}
	
	@RequestMapping(value="/queryBookByHits",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map queryBookByHits(HttpServletRequest request,HttpServletResponse response) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		String cataId=request.getParameter("cataId");
		List<HashMap> bookCataList=null;
		CompletableFuture<List<HashMap>> bookHitsFuture=null;
		CompletableFuture<Object> bookUpdateInfoFuture=null;
		try {
			bookHitsFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookRelationByCataID(cataId,6);
			});
			bookUpdateInfoFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookUpdateInfo(cataId);
			});
			CompletableFuture.allOf(bookHitsFuture,bookUpdateInfoFuture);
			dataMap.put("bcl",bookHitsFuture.get(30, TimeUnit.SECONDS));
			dataMap.put("bul",bookUpdateInfoFuture.get(30, TimeUnit.SECONDS));
		}catch(Exception e) {
			resultMap.put("errorCode", 10086);
			e.printStackTrace();
		}
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}
	
	
	@RequestMapping(value="/initBookData",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map initBookData(HttpServletRequest request,HttpServletResponse response) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		String bookId=request.getParameter("bookId");
		Book book=new Book();
		book.setBookId(bookId);
		Store store=new Store();
		store.setBookId(bookId);
		CompletableFuture<List<Book>> bookFuture=null;
		CompletableFuture<List<Store>> StoreFuture=null;
		CompletableFuture<List<Book>> bookHitsFuture=null;
		try {
			bookFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookInfo(book);
			});
			StoreFuture=CompletableFuture.supplyAsync(()->{
				return storeService.queryBookStore(store);
			});
			bookHitsFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookByHits(8);
			});
			CompletableFuture.allOf(bookFuture,StoreFuture,bookHitsFuture);
			dataMap.put("bil",bookFuture.get(30, TimeUnit.SECONDS));
			dataMap.put("sil",StoreFuture.get(30, TimeUnit.SECONDS));
			dataMap.put("bkl",bookHitsFuture.get(30, TimeUnit.SECONDS));
		}catch(Exception e) {
			resultMap.put("errorCode", 10086);
			e.printStackTrace();
		}
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}
	
	@RequestMapping(value="/initAuthorBookData",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map initAuthorBookData(HttpServletRequest request,HttpServletResponse response) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		String authorId=request.getParameter("authorId");
		Book book=new Book();
		book.setAuthorId(authorId);
		List<Book> authorBookList=null;
		try {
			authorBookList=bookService.queryBookInfo(book);
			
			dataMap.put("abl",authorBookList);
		}catch(Exception e) {
			resultMap.put("errorCode", 10086);
			e.printStackTrace();
		}
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}

}
