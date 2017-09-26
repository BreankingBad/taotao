package com.taotao.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.service.SearchService;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	@Value("${SEARCH_DEFAULT_ROWS}")
	private int SEARCH_DEFAULT_ROWS;
	
	@RequestMapping("/search")
	public String search(@RequestParam("q")String keyword,@RequestParam(defaultValue="1")int page,
			Model model) {
		
		try {
			keyword = new String(keyword.getBytes("iso8859-1"),"utf-8");
			
			SearchResult searchResult = searchService.search(keyword, page, SEARCH_DEFAULT_ROWS);
			
			model.addAttribute("query", keyword);
			model.addAttribute("totalPages", searchResult.getTotalPages());
			model.addAttribute("itemList", searchResult.getItemList());
			model.addAttribute("page", page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return "search";
		
	}
}
