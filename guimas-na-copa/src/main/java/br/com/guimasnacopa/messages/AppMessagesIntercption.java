package br.com.guimasnacopa.messages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AppMessagesIntercption extends HandlerInterceptorAdapter {
	
	@Autowired
	AppMessages appMessages;
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		
		modelAndView.addObject("appMessages", appMessages);
		
	}

}
