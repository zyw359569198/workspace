package com.zyw.novelGame.bussiness.controller;

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
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.CatagoryService;
import com.zyw.novelGame.bussiness.service.ModelService;
import com.zyw.novelGame.bussiness.service.SearchInfoService;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.novelGame.model.SearchInfo;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

@Controller
@RequestMapping("/author")
public class AuthorContronller {
	public static final  Logger logger=LoggerFactory.getLogger(AuthorContronller.class);
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private ModelService modelService;
	
	@Autowired
	private  Configuration configuration;
	
	@Autowired
	private SearchInfoService searchInfoService;
	
	@RequestMapping(value="/{authorNameEn}",method= {RequestMethod.GET})
	public String initAuthorBookData(HttpServletRequest request,ModelMap  model,@PathVariable String authorNameEn) {
		CompletableFuture<PageInfo<HashMap>> authorBookFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		CompletableFuture<List<Catagory>> catagoryFuture=null;
		CompletableFuture<List<SearchInfo>> searchInfoFuture=null;
		try {
			authorBookFuture=CompletableFuture.supplyAsync(()->{
				PageMethod.startPage(1,10, true);
				return new PageInfo<HashMap>(bookService.queryBookInfo(null,authorNameEn,null,null));
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
			CompletableFuture.allOf(authorBookFuture,modelFuture,catagoryFuture,searchInfoFuture);
			model.addAttribute("abl",authorBookFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("cgl", catagoryFuture.get(30,TimeUnit.SECONDS));
			model.addAttribute("sif", searchInfoFuture.get(30,TimeUnit.SECONDS));
		}catch(Exception e) {
			e.printStackTrace();
		}
		Map mp=new HashMap();
		mp.put("abl", model.get("abl"));
		mp.put("mdl", model.get("mdl"));
		mp.put("cgl", model.get("cgl"));
		mp.put("sif", model.get("sif"));
		Utils.saveHtml(configuration,request, "author/"+authorNameEn+"/index", "author", mp);
		return "author";
		}

}
