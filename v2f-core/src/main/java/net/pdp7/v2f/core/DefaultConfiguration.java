package net.pdp7.v2f.core;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

@Configuration
public class DefaultConfiguration {

	@Bean
	public Router router() {
		return new Router(listHandler(), detailHandler());
	}

	@Bean
	public ListHandler listHandler() {
		return new ListHandler(dslContext, viewResolver, localeResolver());
	}

	@Bean
	public DetailHandler detailHandler() {
		return new DetailHandler(dslContext, viewResolver, localeResolver());
	}

	@Bean
	public LocaleResolver localeResolver() {
		return new LocaleResolver();
	}

	@Bean
	public Object dummyCircular() {
		listHandler().setRouter(router());
		return new Object();
	}

	@Autowired
	protected DSLContext dslContext;

	@Autowired
	protected ThymeleafViewResolver viewResolver;
}
