package com.zyw.novelGame.bussiness.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.CatagoryService;
import com.zyw.novelGame.bussiness.service.ModelService;
import com.zyw.novelGame.bussiness.service.SearchInfoService;
import com.zyw.novelGame.bussiness.service.StoreService;
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.novelGame.model.SearchInfo;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

@Controller("pcMainController")
@RequestMapping("")
public class MainContronller {
	
	public static final  Logger logger=LoggerFactory.getLogger(MainContronller.class);
	
	@Autowired
	private BookService bookService;
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private ModelService modelService;
	
	@Autowired
	private  Configuration configuration;
	
	@Autowired
	private SearchInfoService searchInfoService;
	
	@RequestMapping(value="",method= {RequestMethod.GET})
	public String init(HttpServletRequest request,ModelMap  model) {
		CompletableFuture<List<HashMap>> bookHitsFuture=null;
		CompletableFuture<List<HashMap>> bookCreateTimeFuture=null;
		CompletableFuture<List<Object>> catagoryBookRelationFuture=null;
		CompletableFuture<List<HashMap>> bookUpdateInfoFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		CompletableFuture<List<Catagory>> catagoryFuture=null;
		CompletableFuture<List<SearchInfo>> searchInfoFuture=null;
		try {
			catagoryBookRelationFuture=CompletableFuture.supplyAsync(()->{
				return catagoryService.queryCatagory(null);
			}).thenApplyAsync(list->{
				List<Object> li=new ArrayList<Object>();
					list.stream().forEach(catagory->{
						if(!catagory.getCataId().equalsIgnoreCase("0")) {//排除首页
						li.add(bookService.queryBookRelationByCataNameEn(catagory.getCataNameEn(),7));
						}
					});
				return li;
			});
			bookHitsFuture=CompletableFuture.supplyAsync(()->{
				PageHelper.startPage(1, 6, false);
				return (new PageInfo<HashMap>(bookService.queryBook(null,-1))).getList();
			});
			bookCreateTimeFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookByCreateTime();
			});
			bookUpdateInfoFuture=CompletableFuture.supplyAsync(()->{
				PageHelper.startPage(1, 30, false);
				return (new PageInfo<HashMap>(bookService.queryBookUpdateInfo(null,"a.create_time",-1))).getList();
			});
			modelFuture=CompletableFuture.supplyAsync(()->{
				return modelService.queryModel("0");
			});
			catagoryFuture=CompletableFuture.supplyAsync(()->{
				return catagoryService.queryCatagory(new Catagory());
			});
			searchInfoFuture=CompletableFuture.supplyAsync(()->{
				return searchInfoService.querySearchInfo(new SearchInfo());
			});
			CompletableFuture.allOf(bookHitsFuture,catagoryBookRelationFuture,bookCreateTimeFuture,bookUpdateInfoFuture,modelFuture,catagoryFuture,searchInfoFuture);
			model.addAttribute("bkl", bookHitsFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("bcl", bookCreateTimeFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("tjl", catagoryBookRelationFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("bul", bookUpdateInfoFuture.get(300, TimeUnit.SECONDS));
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("cgl", catagoryFuture.get(30,TimeUnit.SECONDS));
			model.addAttribute("sif", searchInfoFuture.get(30,TimeUnit.SECONDS));
			Map mp=new HashMap();
			mp.put("bkl", model.get("bkl"));
			mp.put("bcl", model.get("bcl"));
			mp.put("tjl", model.get("tjl"));
			mp.put("bul", model.get("bul"));
			mp.put("mdl", model.get("mdl"));
			mp.put("cgl", model.get("cgl"));
			mp.put("sif", model.get("sif"));
			Utils.saveHtml(configuration,request, "index", "index", mp);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "index";
		}

}
