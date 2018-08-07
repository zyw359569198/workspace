package com.zyw.tmallgod.utils;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.response.TbkItemGetResponse;

public class TbkUtil {
	protected static final String url = "http://gw.api.taobao.com/router/rest";//沙箱环境调用地址 
    //正式环境需要设置为:http://gw.api.taobao.com/router/rest 
    protected static final String appkey = "24910779"; 
    protected static final String appSecret = "64cb6a0b27daad75a62d694ab0299801"; 
    
	public static void main(String[] args) {
		TaobaoClient client = new DefaultTaobaoClient(url,appkey, appSecret);
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
		TbkItemGetResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(rsp.getBody());
		
	}

}
