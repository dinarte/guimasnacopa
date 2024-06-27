package br.com.guimasnacopa.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SecurityInterception implements HandlerInterceptor {
	
	
	
	@Override
	public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception{
		
		//TODO: IMPLEMENTAR VERIFICA��O DE SEGURAN�A
		return false;
			
	}

	
	@Override
	public void postHandle(
	  HttpServletRequest request, 
	  HttpServletResponse response,
	  Object handler, 
	  ModelAndView modelAndView) throws Exception {
		Autenticacao autenticacao = (Autenticacao) request.getSession().getAttribute("autenticacao"); 
		if (autenticacao != null && modelAndView != null)
			modelAndView.addObject(autenticacao);
	}
}
