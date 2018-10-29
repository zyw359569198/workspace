package com.zyw.novelGame.coreApi.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.SearchInfoService;
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.SearchInfo;

@RestController
@RequestMapping("/pageApi")
public class PageApiController {
	public static final  Logger logger=LoggerFactory.getLogger(PageApiController.class);
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private SearchInfoService searchInfoService;
	
	@RequestMapping(value="/catagory/{cataNameEn}/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map catagorySplitPage(HttpServletRequest request,HttpServletResponse response,@PathVariable String cataNameEn,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap> bookUpdateInfo=null;
		try {
			PageMethod.startPage(pageNum==null?1:pageNum, pageSize==null?24:pageSize, true);
			bookUpdateInfo=new PageInfo<HashMap>(bookService.queryBookUpdateInfo(cataNameEn,"a.create_time",-1));		
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("bul", bookUpdateInfo);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		
	}
	
	@RequestMapping(value="/hot/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map hotSplitPage(HttpServletRequest request,HttpServletResponse response,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap> bookUpdateInfo=null;
		try {
			PageMethod.startPage(pageNum==null?1:pageNum, pageSize==null?20:pageSize, true);
			bookUpdateInfo=new PageInfo<HashMap>(bookService.queryBookUpdateInfo(null,"b.create_time",-1));		
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("bul", bookUpdateInfo);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}
	
	@RequestMapping(value="/recommend/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map recommend(HttpServletRequest request,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap> bookUpdateInfo=null;
		try {
			PageMethod.startPage(pageNum==null?1:pageNum, pageSize==null?24:pageSize, true);
			bookUpdateInfo=new PageInfo<HashMap>(bookService.queryBookUpdateInfo(null,"a.create_time",-1));
			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("bul", bookUpdateInfo);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}
	
	@RequestMapping(value="/full/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map full(HttpServletRequest request,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap> bookUpdateInfo=null;
		try {
			PageMethod.startPage(pageNum==null?1:pageNum, pageSize==null?20:pageSize, true);
			bookUpdateInfo=new PageInfo<HashMap>(bookService.queryBookUpdateInfo(null,"b.create_time",0));
			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("bul", bookUpdateInfo);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}
	
	@RequestMapping(value="/authors/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map authors(HttpServletRequest request,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<Book> book=null;
		try {
			PageMethod.startPage(pageNum==null?1:pageNum, pageSize==null?20:pageSize, true);
			book=new PageInfo<Book>(bookService.queryBookByHits());
			
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("bjl", book);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}
	
	@RequestMapping(value="/author/{authorNameEn}/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map initAuthorBookData(HttpServletRequest request,ModelMap  model,@PathVariable String  authorNameEn,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap>  authorBook=null;
		try {
			PageMethod.startPage(pageNum==null?1:pageNum, pageSize==null?10:pageSize, true);
			authorBook=new PageInfo<HashMap>(bookService.queryBookInfo(null,authorNameEn,null,null));
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("abl", authorBook);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}
	
	@RequestMapping(value="/search/{keyword}/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map search(HttpServletRequest request,ModelMap  model,@PathVariable String  keyword,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap>  authorBook=null;
		try {
			if((keyword!=null||keyword.length()>0)&&pageNum==1) {
				SearchInfo searchInfo=new SearchInfo();
				searchInfo.setKeyword(keyword);
				searchInfoService.updateRecord(searchInfo);
			}
			PageMethod.startPage(pageNum==null?1:pageNum, pageSize==null?10:pageSize, true);
			authorBook=new PageInfo<HashMap>(bookService.queryBookInfo(keyword,null,keyword,null));
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("abl", authorBook);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}
	
	@RequestMapping(value="/hits/{bookId}",method= {RequestMethod.POST},produces = {"application/json;charset=UTF-8"})
	public Map hits(HttpServletRequest request,ModelMap  model,@PathVariable String  bookId) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		int updateBookResult=0;
		try {
			updateBookResult=bookService.updateHits(bookId);
		}catch(Exception e) {
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("result", updateBookResult);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		}

}
