package com.zyw.novelGame.mobile.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.zyw.novelGame.bussiness.service.ModelService;
import com.zyw.novelGame.bussiness.service.StoreService;
import com.zyw.novelGame.model.Model;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

@Controller
@RequestMapping("/mobile/my")
public class MyController {
	
	public static final  Logger logger=LoggerFactory.getLogger(MyController.class);
	
	@Autowired
	private ModelService modelService;
	
	@Autowired
	private  Configuration configuration;
	
	@Autowired
	private StoreService storeService;
	
	@RequestMapping(value="",method= {RequestMethod.GET})
	public String getCookie(HttpServletRequest request,ModelMap  model) {
		CompletableFuture<List<Model>> modelFuture=null;
		CompletableFuture<List<HashMap>> cookFuture=null;
		Cookie[] cookies=null;
		List<String> ids=new ArrayList<String>();
		try {
			cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
				  for (Cookie cookie : cookies) {
					  if(cookie.getName().equalsIgnoreCase("myStore")) {
						  for(String value:cookie.getValue().split("@")) {
							  ids.add(value.split("_")[1]);
						  }
					  }
				  } 
				}
			
			if(ids.size()<1)
                {
					ids.add("-9999");
				  
				}
			cookFuture=CompletableFuture.supplyAsync(()->{
				return storeService.queryBookStoreAll(ids);
			});
			modelFuture=CompletableFuture.supplyAsync(()->{
				return modelService.queryModel("1");
			});
			CompletableFuture.allOf(modelFuture,cookFuture);
			model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
			model.addAttribute("cook", cookFuture.get(30, TimeUnit.SECONDS));
			Map mp=new HashMap();
			mp.put("mdl", model.get("mdl"));
			mp.put("cook", model.get("cook"));
			Utils.saveHtml(configuration,request, "mobile/my/index", "mobile/my", mp);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "mobile/my";
		}

}
