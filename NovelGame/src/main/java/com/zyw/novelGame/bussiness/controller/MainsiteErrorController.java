package com.zyw.novelGame.bussiness.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zyw.novelGame.bussiness.service.CatagoryService;
import com.zyw.novelGame.bussiness.service.ModelService;
import com.zyw.novelGame.bussiness.service.SearchInfoService;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.novelGame.model.SearchInfo;
import com.zyw.utils.Utils;

import freemarker.template.Configuration;

@Controller
class MainsiteErrorController implements ErrorController {
	
	@Autowired
	private ModelService modelService;
	
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private  Configuration configuration;
	
	@Autowired
	private SearchInfoService searchInfoService;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request,ModelMap  model){
    	CompletableFuture<List<Model>> modelFuture=null;
		CompletableFuture<List<Catagory>> catagoryFuture=null;
		CompletableFuture<List<SearchInfo>> searchInfoFuture=null;
		try {
		modelFuture=CompletableFuture.supplyAsync(()->{
			return modelService.queryModel("0");
		});
		catagoryFuture=CompletableFuture.supplyAsync(()->{
			return catagoryService.queryCatagory(new Catagory());
		});
		searchInfoFuture=CompletableFuture.supplyAsync(()->{
			return searchInfoService.querySearchInfo(new SearchInfo());
		});
		CompletableFuture.allOf(modelFuture,catagoryFuture,searchInfoFuture);
		model.addAttribute("mdl", modelFuture.get(30, TimeUnit.SECONDS));
		model.addAttribute("cgl", catagoryFuture.get(30,TimeUnit.SECONDS));
		model.addAttribute("sif", searchInfoFuture.get(30,TimeUnit.SECONDS));
		}catch(Exception e) {
			e.printStackTrace();
		}
		Map mp=new HashMap();
		mp.put("mdl", model.get("mdl"));
		mp.put("cgl", model.get("cgl"));
		mp.put("sif", model.get("sif"));
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        model.addAttribute("statusCode", statusCode);
        mp.put("statusCode", model.get("statusCode"));
		Utils.saveHtml(configuration,request, "model/error/index", "error", mp);
		return "error";
/*        if(statusCode == 401){
    		Utils.saveHtml(configuration,request, "model/401/index", "401", mp);
            return "/401";
        }else if(statusCode == 404){
    		Utils.saveHtml(configuration,request, "model/404/index", "404", mp);
            return "/404";
        }else if(statusCode == 403){
    		Utils.saveHtml(configuration,request, "model/403/index", "403", mp);
            return "/403";
        }else{
    		Utils.saveHtml(configuration,request, "model/500/index", "500", mp);
            return "/500";
        }*/

    }
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
