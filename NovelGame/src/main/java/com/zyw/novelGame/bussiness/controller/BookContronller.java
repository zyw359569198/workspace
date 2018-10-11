package com.zyw.novelGame.bussiness.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.CatagoryService;
import com.zyw.novelGame.bussiness.service.ModelService;
import com.zyw.novelGame.bussiness.service.SearchInfoService;
import com.zyw.novelGame.bussiness.service.StoreService;
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.BookData;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.novelGame.model.SearchInfo;
import com.zyw.novelGame.model.Store;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

@Controller("pcBook")
@RequestMapping("/book")
public class BookContronller {
	
	public static final  Logger logger=LoggerFactory.getLogger(BookContronller.class);
	
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
	
	
	@RequestMapping(value="/{bookNameEn}",method= {RequestMethod.GET})
	public String initBookData(HttpServletRequest request,ModelMap  model,@PathVariable String bookNameEn) {
		CompletableFuture<List<HashMap>> bookFuture=null;
		CompletableFuture<List<HashMap>> StoreFuture=null;
		CompletableFuture<List<HashMap>> bookHitsFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		CompletableFuture<List<Catagory>> catagoryFuture=null;
		CompletableFuture<List<SearchInfo>> searchInfoFuture=null;
		try {
			bookFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookInfo(null,null,null,bookNameEn);
			});
			StoreFuture=CompletableFuture.supplyAsync(()->{
				return storeService.queryBookStore(bookNameEn,null);
			});
			bookHitsFuture=CompletableFuture.supplyAsync(()->{
				PageHelper.startPage(1, 8, false);
				return (new PageInfo<HashMap>(bookService.queryBook("hits",-1))).getList();
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
			CompletableFuture.allOf(bookFuture,StoreFuture,bookHitsFuture,modelFuture,catagoryFuture,searchInfoFuture);
			model.addAttribute("bil",bookFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("sil",StoreFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("bkl",bookHitsFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("cgl", catagoryFuture.get(30,TimeUnit.SECONDS));
			model.addAttribute("sif", searchInfoFuture.get(30,TimeUnit.SECONDS));
		}catch(Exception e) {
			e.printStackTrace();
		}
		Map mp=new HashMap();
		mp.put("bil", model.get("bil"));
		mp.put("sil", model.get("sil"));
		mp.put("bkl", model.get("bkl"));
		mp.put("mdl", model.get("mdl"));
		mp.put("cgl", model.get("cgl"));
		mp.put("sif", model.get("sif"));
		Utils.saveHtml(configuration,request, "book/"+bookNameEn+"/index", "book", mp);
		return "book";
		}
	
	@RequestMapping(value="/{bookNameEn}/{storeId}",method= {RequestMethod.GET})
	public String init(HttpServletRequest request,HttpServletResponse response,ModelMap  model,@PathVariable String bookNameEn,@PathVariable String storeId) {
		CompletableFuture<List<BookData>> storeDataFuture=null;
		CompletableFuture<List<HashMap>> storeFuture=null;
		try {
			storeFuture=CompletableFuture.supplyAsync(()->{
				return storeService.queryBookStore(bookNameEn,storeId);
			});
			storeDataFuture=CompletableFuture.supplyAsync(()->{
				return storeService.queryBookStoreData(storeId).stream().map(o->{o.setvStoreContent(new String(o.getStoreContent()));return o;}).collect(Collectors.toList());
			});
			CompletableFuture.allOf(storeFuture,storeDataFuture);
			model.addAttribute("sdl",storeFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("sddl",storeDataFuture.get(30, TimeUnit.SECONDS));
		}catch(Exception e) {
			e.printStackTrace();
		}
		Map mp=new HashMap();
		mp.put("sdl", model.get("sdl"));
		mp.put("sddl", model.get("sddl"));
		Utils.saveHtml(configuration,request, "book/"+bookNameEn+"/"+storeId+"/index", "store", mp);
		return "store";
		}

}
