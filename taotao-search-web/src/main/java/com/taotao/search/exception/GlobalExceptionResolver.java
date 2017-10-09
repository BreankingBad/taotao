package com.taotao.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全局异常处理器
 * @author mxm
 *
 */
public class GlobalExceptionResolver implements HandlerExceptionResolver {

	private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest arg0, HttpServletResponse arg1, Object handler,
			Exception e) {
		
		logger.info("进入全局异常处理器");
		logger.debug("handler类型："+handler.getClass());
		
		// 控制台打印异常
		e.printStackTrace();
		
		// 向日志文件写入异常
		logger.error("系统发生异常",e);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message","你的电脑有问题，请重试");
		modelAndView.setViewName("error/exception");
		
		return modelAndView;
	}

}
