package com.zyw.novelGame.coreApi.controller;

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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.utils.Utils;

@RestController
@RequestMapping("/pageApi")
public class PageApiController {
	public static final  Logger logger=LoggerFactory.getLogger(PageApiController.class);
	
	@Autowired
	private BookService bookService;
	
	@RequestMapping(value="/catagory/{cataNameEn}/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map catagorySplitPage(HttpServletRequest request,HttpServletResponse response,@PathVariable String cataNameEn,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap> bookUpdateInfo=null;
		try {
			PageHelper.startPage(pageNum==null?1:pageNum, pageSize==null?24:pageSize, true);
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
	
	@RequestMapping(value="/hot/{pageSize}/{pageNum}",method= {RequestMethod.GET})
	public Map hotSplitPage(HttpServletRequest request,HttpServletResponse response,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap> bookUpdateInfo=null;
		try {
			PageHelper.startPage(pageNum==null?1:pageNum, pageSize==null?20:pageSize, true);
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

}
