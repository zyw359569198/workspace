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

import com.zyw.novelGame.bussiness.service.BookService;
import com.zyw.novelGame.bussiness.service.ModelService;
import com.zyw.novelGame.model.Model;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

@Controller("mobileAuthor")
@RequestMapping("/mobile/author")
public class AuthorController {
	
	public static final  Logger logger=LoggerFactory.getLogger(AuthorController.class);
	
	@Autowired
	private ModelService modelService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private  Configuration configuration;
	
	@RequestMapping(value="/{authorNameEn}",method= {RequestMethod.GET})
	public String initBookData(HttpServletRequest request,ModelMap  model,@PathVariable String authorNameEn) {
		CompletableFuture<List<HashMap>> authorBookFuture=null;
		CompletableFuture<List<Model>> modelFuture=null;
		try {
			authorBookFuture=CompletableFuture.supplyAsync(()->{
				return bookService.queryBookInfo(null,authorNameEn,null,null);
			});
			modelFuture=CompletableFuture.supplyAsync(()->{
				return modelService.queryModel("1");
			});
			CompletableFuture.allOf(authorBookFuture,modelFuture);
			model.addAttribute("abl",authorBookFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
		}catch(Exception e) {
			e.printStackTrace();
		}
		Map mp=new HashMap();
		mp.put("abl", model.get("abl"));
		mp.put("mdl", model.get("mdl"));
		Utils.saveHtml(configuration,request, "mobile/author/"+authorNameEn+"/index", "mobile/author", mp);
		return "mobile/author";
		}

}
