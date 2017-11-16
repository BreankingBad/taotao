package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
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
		
		// 登录成功才写cookie
		if(taotaoResult.getStatus() == 200) {
			CookieUtils.setCookie(request,response,TT_TOKEN,taotaoResult.getData().toString());
		}
		return taotaoResult;
	}
	
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,
			// 指定响应数据的content-type
			produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserInfoByToken(@PathVariable String token, String callback) {
		TaotaoResult taotaoResult = userService.getUserInfoByToken(token);
		if (StringUtils.isNotBlank(callback)) {
			return callback + "(" + JsonUtils.objectToJson(taotaoResult) + ");";
		}
		return JsonUtils.objectToJson(taotaoResult);
	}

}
