package br.com.guimasnacopa;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.guimasnacopa.security.SecurityInterception;

//@EnableWebMvc
//@Configuration
public class WebConfiguration implements WebMvcConfigurer{

	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(new SecurityInterception());
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//registry
        //.addResourceHandler("/resources/**")
        //.addResourceLocations("/resources/");     
		}
}
