package com.dtnsbike.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SuppressWarnings("deprecation")
@Configuration
public class WebApplicationConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/methodNotAllowed").setViewName("redirect:/methodnotallowed.html");//405
		registry.addViewController("/notFound").setViewName("redirect:/pagenotfound.html");//404
		registry.addViewController("/badRequest").setViewName("redirect:/badrequest.html");//400
		registry.addViewController("/requestTimeOut").setViewName("redirect:/internalservererror.html");//408
		registry.addViewController("/internalServerError").setViewName("redirect:/requesttimeout.html");//500
	}
	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
		return container -> {
			container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notFound"));
			container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/badRequest"));
			container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/internalServerError"));
			container.addErrorPages(new ErrorPage(HttpStatus.REQUEST_TIMEOUT, "/requestTimeOut"));
			container.addErrorPages(new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED,"/methodNotAllowed"));
		};
	}

}