package net.pdp7.v2f.core;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultConfiguration {

	@Bean
	public Router router() {
		return new Router(listHandler());
	}

	@Bean
	public ListHandler listHandler() {
		return new ListHandler(dslContext);
	}

	@Autowired
	protected DSLContext dslContext;
}
