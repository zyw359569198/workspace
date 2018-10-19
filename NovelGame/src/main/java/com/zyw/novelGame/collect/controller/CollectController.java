package com.zyw.novelGame.collect.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zyw.novelGame.collect.CollectData;
import com.zyw.novelGame.collect.entity.BookInfo;
import com.zyw.novelGame.collect.entity.CollectInfo;
import com.zyw.novelGame.collect.entity.Rule;
import com.zyw.novelGame.collect.entity.StoreInfo;
import com.zyw.novelGame.collect.novelSite.BasicNovelSite;
import com.zyw.novelGame.collect.novelSite.ShuHuangGeNovelSite;
import com.zyw.novelGame.collect.novelSite.TXT2NovelSite;
import com.zyw.novelGame.collect.queue.Consumer;
import com.zyw.novelGame.collect.queue.Producer;
import com.zyw.novelGame.collect.queue.Resource;
import com.zyw.utils.Common;

@RestController
@RequestMapping("/collect")
public class CollectController {
	public static final  Logger logger=LoggerFactory.getLogger(CollectController.class);
	
	
	@Autowired
	private ExecutorService  executorService ;
	
	@RequestMapping(value="/init",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map init(HttpServletRequest request,HttpServletResponse response1) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		List<BasicNovelSite> monitorLists = new ArrayList<>();
		Reflections reflections = new Reflections("com.zyw.novelGame.collect.novelSite");
		Set<Class<? extends BasicNovelSite>> monitorClasses = reflections.getSubTypesOf(BasicNovelSite.class);
        for (Class<? extends BasicNovelSite> monitor : monitorClasses) { 
        	//monitorLists.add(ApplicationContext.getBean(monitor)); 
        	try {
				executorService.execute(monitor.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}
	

	//@RequestMapping(value="/initData",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	//@ResponseBody
	public Map initDataShuHuang(HttpServletRequest request,HttpServletResponse response1) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();	
		executorService.execute(new ShuHuangGeNovelSite());
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}
	
	/**
	 * 纵横中文网
	 * @param request
	 * @param response1
	 * @return
	 */
/*	@RequestMapping(value="/initData",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody*/
	public Map initData(HttpServletRequest request,HttpServletResponse response1) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}


}
