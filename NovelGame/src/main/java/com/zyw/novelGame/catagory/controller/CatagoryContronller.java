package com.zyw.novelGame.catagory.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.zyw.novelGame.model.Catagory;
import com.zyw.novelGame.model.Model;
import com.zyw.novelGame.catagory.service.CatagoryService;
import com.zyw.novelGame.catagory.service.ModelService;

@RestController
@RequestMapping("/catagory")
public class CatagoryContronller {
	public static final  Logger logger=LoggerFactory.getLogger(CatagoryContronller.class);
	
	@Autowired
	private CatagoryService catagoryService;
	
	@Autowired
	private ModelService modelService;
	
	@RequestMapping(value="/info",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public ModelAndView info() {
		return null;
	}
	
	@RequestMapping(value="/init",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Map init(HttpServletRequest request,HttpServletResponse response) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		List<Catagory> catagoryList=new ArrayList<Catagory>();
		List<Model> modelList=new ArrayList<Model>();
		try {
			catagoryList=catagoryService.queryCatagory();
			dataMap.put("cgl", catagoryList);
			modelList=modelService.queryModel();
			dataMap.put("mdl", modelList);

		}catch(Exception e) {
			resultMap.put("errorCode", 10086);
			e.printStackTrace();
		}
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
	}

}
