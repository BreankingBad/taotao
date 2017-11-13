package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService userService;
	
	@Value("${TT_TOKEN}")
	String TT_TOKEN;

	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public TaotaoResult checkUserData(@PathVariable String param, @PathVariable Integer type) {

		TaotaoResult result = userService.checkData(param, type);
		return result;
	}
	
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult register(TbUser tbUser) {
		TaotaoResult taotaoResult = userService.register(tbUser);
		return taotaoResult;
	}
	
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult login(String username, String password,HttpServletRequest request,HttpServletResponse response) {
		TaotaoResult taotaoResult = userService.login(username, password);
		
		CookieUtils.setCookie(request,response,TT_TOKEN,taotaoResult.getData().toString());
		return taotaoResult;
	}
	

}
