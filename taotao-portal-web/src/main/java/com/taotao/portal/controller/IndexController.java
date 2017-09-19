package com.taotao.portal.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.common.utils.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.HomeBanner;

@Controller
public class IndexController {

	@Value("${HOME_BANNER_CATEGORY_ID}")
	private long HOME_BANNER_CATEGORY_ID; 
	
	@Value("${HOME_BANNER_WIDTH}")
	private int HOME_BANNER_WIDTH; 
	
	@Value("${HOME_BANNER_WIDTH_B}")
	private int HOME_BANNER_WIDTH_B; 
	
	@Value("${HOME_BANNER_HEIGHT}")
	private int HOME_BANNER_HEIGHT; 
	
	@Value("${HOME_BANNER_HEIGHT_B}")
	private int HOME_BANNER_HEIGHT_B; 
	
	@Autowired
	ContentService contentService;
	
	@RequestMapping("/index")
	public String showIndex(Model model) {
		List<TbContent> contentList = contentService.getContentByCategoryId(HOME_BANNER_CATEGORY_ID);
		
		List<HomeBanner> banners = new ArrayList<>();
		for(TbContent content:contentList) {
			HomeBanner homeBanner = new HomeBanner();
			homeBanner.setSrc(content.getPic());
			homeBanner.setSrcB(content.getPic2());
			homeBanner.setAlt(content.getTitle());
			homeBanner.setHref(content.getUrl());
			homeBanner.setWidth(HOME_BANNER_WIDTH);
			homeBanner.setWidthB(HOME_BANNER_WIDTH_B);
			homeBanner.setHeight(HOME_BANNER_HEIGHT);
			homeBanner.setHeightB(HOME_BANNER_HEIGHT_B);
			
			banners.add(homeBanner);
		}
		
		String bannerJson = JsonUtils.objectToJson(banners);
		model.addAttribute("ad1", bannerJson);
		return "index";
	}
}
