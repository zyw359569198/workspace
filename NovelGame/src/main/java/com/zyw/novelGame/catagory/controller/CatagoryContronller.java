package com.zyw.novelGame.catagory.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

import com.zyw.novelGame.catagory.service.BookService;
import com.zyw.novelGame.catagory.service.CatagoryService;
import com.zyw.novelGame.catagory.service.ModelService;

@Controller
@RequestMapping("/catagory")
public class CatagoryContronller {
	public static final  Logger logger=LoggerFactory.getLogger(CatagoryContronller.class);
	
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private ModelService modelService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private  Configuration configuration;
	
	
	@RequestMapping(value="/init",method= {RequestMethod.GET})
	public String init(org.springframework.ui.Model model) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		CompletableFuture<List<Catagory>> catagoryFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		try {
			catagoryFuture=CompletableFuture.supplyAsync(()->{
				return catagoryService.queryCatagory(new Catagory());
			});
			modelFuture=CompletableFuture.supplyAsync(()->{
				return modelService.queryModel();
			});
			CompletableFuture.allOf(catagoryFuture,modelFuture);
			dataMap.put("cgl", catagoryFuture.get(30,TimeUnit.SECONDS));
			dataMap.put("mdl", modelFuture.get(30,TimeUnit.SECONDS));
			
			

		}catch(Exception e) {
			resultMap.put("errorCode", 10086);
			e.printStackTrace();
		}
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		model.addAllAttributes(resultMap);
		return "main";
	}
	
	@RequestMapping(value="/{cataNameEn}",method= {RequestMethod.GET})
	public String queryBookByHits(HttpServletRequest request,ModelMap  model,@PathVariable String cataNameEn) {
		CompletableFuture<List<HashMap>> bookHitsFuture=null;
		CompletableFuture<List<HashMap>> bookUpdateInfoFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		CompletableFuture<List<Catagory>> catagoryFuture=null;
		try {
			bookHitsFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookRelationByCataNameEn(cataNameEn,6);
			});
			bookUpdateInfoFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookUpdateInfo(cataNameEn);
			});
			modelFuture=CompletableFuture.supplyAsync(()->{
				return modelService.queryModel();
			});
			catagoryFuture=CompletableFuture.supplyAsync(()->{
				return catagoryService.queryCatagory(new Catagory());
			});
			CompletableFuture.allOf(bookHitsFuture,bookUpdateInfoFuture,modelFuture,catagoryFuture);
			model.addAttribute("bcl", bookHitsFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("bul", bookUpdateInfoFuture.get(30,TimeUnit.SECONDS));
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("cgl", catagoryFuture.get(30,TimeUnit.SECONDS));
			Map mp=new HashMap();
			mp.put("bcl", model.get("bcl"));
			mp.put("bul", model.get("bul"));
			mp.put("mdl", model.get("mdl"));
			mp.put("cgl", model.get("cgl"));
			Utils.saveHtml(configuration,request, "catagory", "catagory", mp);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "catagory";
		}

}
