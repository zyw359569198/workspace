package com.zyw.tmallgod.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;

@RestController
@RequestMapping("/test")
public class TestController {
	
	@RequestMapping(value="/print",method=RequestMethod.GET)
	@ResponseBody
	public String printTest() {
		return "Hello World!";
		
	}
	
	@RequestMapping(value="/printTest",method=RequestMethod.GET)
	@ResponseBody
	public String taoBaoTest() {
	/*	TaobaoClient client = new DefaultTaobaoClient("", "", "");
		TbkItemGetRequest req = new TbkItemGetRequest();
		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick");
		req.setQ("女装");
		req.setCat("16,18");
		req.setItemloc("杭州");
		req.setSort("tk_rate_des");
		req.setIsTmall(false);
		req.setIsOverseas(false);
		req.setStartPrice(10L);
		req.setEndPrice(10L);
		req.setStartTkRate(123L);
		req.setEndTkRate(123L);
		req.setPlatform(1L);
		req.setPageNo(123L);
		req.setPageSize(20L);
		TbkItemGetResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());*/
		return "Hello World!";
		
	}

}
