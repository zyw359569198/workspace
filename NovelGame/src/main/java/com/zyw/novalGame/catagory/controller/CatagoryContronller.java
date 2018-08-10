package com.zyw.novalGame.catagory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/catagory")
public class CatagoryContronller {
	public static final  Logger logger=LoggerFactory.getLogger(CatagoryContronller.class);
	
	@RequestMapping(value="/chuanyue",method= {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView test() {
		
	}

}
