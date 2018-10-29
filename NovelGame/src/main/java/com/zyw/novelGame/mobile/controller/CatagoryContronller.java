package com.zyw.novelGame.mobile.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.CatagoryService;
import com.zyw.novelGame.bussiness.service.ModelService;

@Controller("mobileCatagory")
@RequestMapping("/mobile/catagory")
public class CatagoryContronller {
	public static final  Logger logger=LoggerFactory.getLogger(CatagoryContronller.class);
	
	@Autowired
	private ModelService modelService;
	
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private  Configuration configuration;
	
	@RequestMapping(value="",method= {RequestMethod.GET})
	public String init(HttpServletRequest request,ModelMap  model) {
		CompletableFuture<List<Catagory>> catagoryFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		try {
			catagoryFuture=CompletableFuture.supplyAsync(()->{
				return catagoryService.queryCatagory(new Catagory());
			});
			modelFuture=CompletableFuture.supplyAsync(()->{
				return modelService.queryModel("1");
			});
			CompletableFuture.allOf(catagoryFuture,modelFuture);
			model.addAttribute("cgl", catagoryFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
			Map mp=new HashMap();
			mp.put("cgl", model.get("cgl"));
			mp.put("mdl", model.get("mdl"));
			Utils.saveHtml(configuration,request, "mobile/catagory/index", "mobile/catagorys", mp);		}catch(Exception e) {
			e.printStackTrace();
		}
		return "mobile/catagorys";
		}
	
	@RequestMapping(value="/{cataNameEn}",method= {RequestMethod.GET})
	public String queryBookByHits(HttpServletRequest request,ModelMap  model,@PathVariable String cataNameEn) {
		CompletableFuture<PageInfo<HashMap>> bookUpdateInfoFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		try {
			bookUpdateInfoFuture=CompletableFuture.supplyAsync(()->{
				PageMethod.startPage(1, 24, true);
				return new PageInfo<HashMap>(bookService.queryBookUpdateInfo(cataNameEn,"a.create_time",-1));
			});
			modelFuture=CompletableFuture.supplyAsync(()->{
				return modelService.queryModel("1");
			});
			CompletableFuture.allOf(bookUpdateInfoFuture,modelFuture);
			model.addAttribute("bul", bookUpdateInfoFuture.get(30,TimeUnit.SECONDS));
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
			Map mp=new HashMap();
			mp.put("bul", model.get("bul"));
			mp.put("mdl", model.get("mdl"));
			Utils.saveHtml(configuration,request, "mobile/catagory/"+cataNameEn+"/index", "mobile/catagory", mp);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "mobile/catagory";
		}

}
