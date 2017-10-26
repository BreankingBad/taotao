package com.taotao.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

@Controller
public class ItemController {
	
	@Autowired
	ItemService itemService;
	
	
	@RequestMapping("/item/{itemId}")
	public String showItem(@PathVariable long itemId,Model model) {
		 TbItem  tbItem = itemService.getItemById(itemId);
		 
		 Item item = new Item(tbItem);
		 TbItemDesc itemDesc = itemService.getIemDescById(itemId);
		 
		 model.addAttribute("item", item);
		 model.addAttribute("itemDesc", itemDesc);
		 
		 return "item";
	}

}
