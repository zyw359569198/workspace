package com.zyw.novelGame.coreApi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zyw.novelGame.bussiness.service.StoreService;

@RestController
@RequestMapping("/mobile/mobileApi")
public class MobileApiController {
	public static final  Logger logger=LoggerFactory.getLogger(MobileApiController.class);
	
	@Autowired
	private StoreService storeService;
	
	/*@RequestMapping(value="/book/{bookNameEn}/{storeId}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map catagorySplitPage(HttpServletRequest request,HttpServletResponse response,@PathVariable String bookNameEn,@PathVariable String storeId) {
		Map resultMap=new HashMap();
		String newStore="";
		Cookie newCookie=null;
		Cookie[] cookies=null;
		int count=0;
		try {
			cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
					  for (Cookie cookie : cookies) {
						  if(cookie.getName().equalsIgnoreCase("myStore")) {
							  count=0;
							  for(String cook:cookie.getValue().split(",")){
								  if(null!=cook&&cook.length()>0) {
									  if(!bookNameEn.equalsIgnoreCase(cook.substring(0, cook.indexOf("_")))) {
										  newStore=newStore+","+cook;
									  }									  
									  count++;
								  }
							  }
						  }
				  } 
				}
			
			  newStore=bookNameEn+"_"+storeId+(newStore.length()>0?(","+newStore):"");
			  if(count>=10) {
				  newStore=newStore.substring(0,newStore.lastIndexOf(","));
			  }
			newCookie=new Cookie("myStore", newStore);
			newCookie.setMaxAge(10000);//设置有效时间
			newCookie.setPath("/");
			response.addCookie(newCookie);
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		resultMap.put("data", null);
		resultMap.put("errorCode", 200);
		return resultMap;
		
	}*/
	
	@RequestMapping(value="/book/{bookNameEn}/{pageSize}/{pageNum}",method= {RequestMethod.GET},produces = {"application/json;charset=UTF-8"})
	public Map initBookData(HttpServletRequest request,HttpServletResponse response,@PathVariable String bookNameEn,@PathVariable Integer pageNum,@PathVariable Integer pageSize) {
		Map resultMap=new HashMap();
		Map dataMap=new HashMap();
		PageInfo<HashMap> storeInfo=null;
		try {
				PageHelper.startPage(pageNum==null?1:pageNum, pageSize==null?100:pageSize, true);
				storeInfo=new PageInfo<HashMap>(storeService.queryBookStore(bookNameEn,null));
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("errorCode", 10086);
		}
		dataMap.put("bul", storeInfo);
		resultMap.put("data", dataMap);
		resultMap.put("errorCode", 200);
		return resultMap;
		
	}

}
