package com.taotao.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;

@Controller
public class OrderCartController {

	@Value("${CART_KEY}")
	String CART_KEY;
	
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		List<TbItem> itemList = getItemList(request);
		request.setAttribute("cartList", itemList);
		return "order-cart";
	}
	
	private List<TbItem> getItemList(HttpServletRequest request){
		String cookieValue = CookieUtils.getCookieValue(request, CART_KEY, true);
		if(StringUtils.isNoneBlank(cookieValue)) {
			List<TbItem> list = JsonUtils.jsonToList(cookieValue, TbItem.class);
			return list;
		}
		
		return new ArrayList<>();
	}
}
