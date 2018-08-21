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
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.Catagory;

@RestController
@RequestMapping("/book")
public class BookContronller {
	
	public static final  Logger logger=LoggerFactory.getLogger(BookContronller.class);
	
	@Autowired
	private BookService bookService;
	@Autowired
	private CatagoryService catagoryService;
	
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
				return catagoryService.queryCatagory();
			}).thenApplyAsync(list->{
				List<Object> li=new ArrayList<Object>();
					list.stream().forEach(catagory->{
						if(!catagory.getCataId().equalsIgnoreCase("0")) {//排除首页
						li.add(bookService.queryBookRelationByCataID(catagory.getCataId()));
						}
					});
				return li;
			});
			bookHitsFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookByHits();
			});
			bookCreateTimeFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookByCreateTime();
			});
			bookUpdateInfoFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookUpdateInfo();
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

}
