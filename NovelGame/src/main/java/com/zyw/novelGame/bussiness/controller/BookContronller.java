package com.zyw.novelGame.bussiness.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.CatagoryService;
import com.zyw.novelGame.bussiness.service.ModelService;
import com.zyw.novelGame.bussiness.service.StoreService;
import com.zyw.novelGame.model.Book;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.novelGame.model.Store;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

@Controller
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
	
	
	@RequestMapping(value="/{bookNameEn}",method= {RequestMethod.GET})
	public String initBookData(HttpServletRequest request,ModelMap  model,@PathVariable String bookNameEn) {
		CompletableFuture<List<HashMap>> bookFuture=null;
		CompletableFuture<List<HashMap>> StoreFuture=null;
		CompletableFuture<List<Book>> bookHitsFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		CompletableFuture<List<Catagory>> catagoryFuture=null;
		try {
			bookFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookInfo(null,null,null,bookNameEn);
			});
			StoreFuture=CompletableFuture.supplyAsync(()->{
				return storeService.queryBookStore(bookNameEn,null);
			});
			bookHitsFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBook(8,"hits",-1);
			});
			modelFuture=CompletableFuture.supplyAsync(()->{
				return modelService.queryModel();
			});
			catagoryFuture=CompletableFuture.supplyAsync(()->{
				return catagoryService.queryCatagory(new Catagory());
			});
			CompletableFuture.allOf(bookFuture,StoreFuture,bookHitsFuture,modelFuture,catagoryFuture);
			model.addAttribute("bil",bookFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("sil",StoreFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("bkl",bookHitsFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("cgl", catagoryFuture.get(30,TimeUnit.SECONDS));
		}catch(Exception e) {
			e.printStackTrace();
		}
		Map mp=new HashMap();
		mp.put("bil", model.get("bil"));
		mp.put("sil", model.get("sil"));
		mp.put("bkl", model.get("bkl"));
		mp.put("mdl", model.get("mdl"));
		mp.put("cgl", model.get("cgl"));
		Utils.saveHtml(configuration,request, "book\\"+bookNameEn+"\\index", "book", mp);
		return "book";
		}
	
	@RequestMapping(value="/{bookNameEn}/{storeId}",method= {RequestMethod.GET})
	public String init(HttpServletRequest request,ModelMap  model,@PathVariable String bookNameEn,@PathVariable String storeId) {
		CompletableFuture<List<HashMap>> storeDataFuture=null;
		CompletableFuture<List<HashMap>> storeFuture=null;
		try {
			storeFuture=CompletableFuture.supplyAsync(()->{
				return storeService.queryBookStore(bookNameEn,storeId);
			});
			storeDataFuture=CompletableFuture.supplyAsync(()->{
				return storeService.queryBookStoreData(storeId);
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
		Utils.saveHtml(configuration,request, "book\\"+bookNameEn+"\\"+storeId+"\\index", "store", mp);
		return "store";
		}

}
