package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.sso.service.UserService;

public class LoginInterceptor implements HandlerInterceptor {

	@Value("${TT_TOKEN}")
	String TT_TOKEN;
	@Value("${SSO_LOGIN_BASE_URL}")
	String SSO_LOGIN_BASE_URL;
	
	@Autowired
	UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		// 执行handler之前执行该方法 
	   // 返回true 放行， 返回false 拦截
		
		boolean isLogin = false;
		
		String token = CookieUtils.getCookieValue(request, TT_TOKEN);
		if(StringUtils.isNoneBlank(token)) {
			TaotaoResult result = userService.getUserInfoByToken(token);
			if(result.getStatus() == 200) {
				isLogin = true;
			}
		}
		
		System.out.println("islogin:"+isLogin);
		
		if(isLogin) {
			return true;
		}else {
			String url = request.getRequestURL().toString();
			response.sendRedirect(SSO_LOGIN_BASE_URL + "/page/login?url=" + url); 
			return false;
		}
 	}
	

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView modelAndView)
			throws Exception {
		// 执行handler之后执行该方法 ，modelAndView返回之前

	}

	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		// modelAndView返回之后 
	

	}



}
