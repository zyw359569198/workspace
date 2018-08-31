package com.zyw.novelGame.catagory.controller;

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

import com.zyw.novelGame.catagory.service.StoreService;
import com.zyw.novelGame.model.Store;

@RestController
@RequestMapping("/store")
public class StoreContronller {
	
	public static final  Logger logger=LoggerFactory.getLogger(StoreContronller.class);

}
