package com.taotao.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;

@Controller
public class OrderCartController {

	@Value("${CART_KEY}")
	String CART_KEY;
	
	@Autowired
	OrderService orderService;
	
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		TbUser user = (TbUser) request.getAttribute("user");
		System.out.println("showOrderCart:"+user.getUsername());
		
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
	

	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	private String createOrder(OrderInfo orderInfo,Model model){
		
		TaotaoResult order = orderService.createOrder(orderInfo);
		model.addAttribute("orderId", order.getData().toString());
		model.addAttribute("payment",orderInfo.getPayment());
		
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(3);
		model.addAttribute("date", dateTime.toString("yyyy-MM-dd"));
		
		return "success";
	}

}
