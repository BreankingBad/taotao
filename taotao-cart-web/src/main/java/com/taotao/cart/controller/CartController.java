package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

@Controller
public class CartController {

	@Value("${CART_KEY}")
	String CART_KEY;
	@Value("${CART_EXPIRE}")
	int CART_EXPIRE;
	
	@Autowired
	ItemService itemService;
	
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable long itemId,@RequestParam(defaultValue="1")int num,
			HttpServletRequest request,HttpServletResponse response) {
		
		List<TbItem> itemList = getItemList(request);
		
		boolean exist = false;
		for (TbItem tbItem : itemList) {
			if(tbItem.getId() == itemId) {
				exist = true;
				tbItem.setNum(tbItem.getNum() + num);
				break;
			}
		}
		
		if(!exist) {
			TbItem item = itemService.getItemById(itemId);
			item.setNum(num);
			if (StringUtils.isNoneBlank(item.getImage())) {
				String[] images = item.getImage().split(",");
				item.setImage(images[0]);
			}
			itemList.add(item);
		}
		
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(itemList) , CART_EXPIRE , true);
		return "cartSuccess";
	}
	
	private List<TbItem> getItemList(HttpServletRequest request){
		String cookieValue = CookieUtils.getCookieValue(request, CART_KEY, true);
		if(StringUtils.isNoneBlank(cookieValue)) {
			List<TbItem> list = JsonUtils.jsonToList(cookieValue, TbItem.class);
			return list;
		}
		
		return new ArrayList<>();
	}
	
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request) {
		List<TbItem> itemList = getItemList(request);
		request.setAttribute("cartList", itemList);
		return "cart";
	}
	
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateCartNum(@PathVariable long itemId,@PathVariable int num,HttpServletRequest request,
			HttpServletResponse response) {
		
		List<TbItem> itemList = getItemList(request);
		
		for (TbItem tbItem : itemList) {
			if(tbItem.getId() == itemId) {
				tbItem.setNum(num);
				break;
			}
		}
		
		CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(itemList) , CART_EXPIRE , true);
		return TaotaoResult.ok();
	}
}
