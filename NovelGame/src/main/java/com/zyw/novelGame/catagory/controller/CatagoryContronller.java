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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
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
	
	@RequestMapping(value="/info",method= {RequestMethod.GET})
	public ModelAndView info() {
		return null;
	}
	
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

}
