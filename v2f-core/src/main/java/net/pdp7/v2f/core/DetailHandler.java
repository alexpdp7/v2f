package net.pdp7.v2f.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.jooq.impl.DSL.*;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.ImmutableMap;

public class DetailHandler {

	protected final DSLContext dslContext;
	protected final ThymeleafViewResolver viewResolver;
	protected final LocaleResolver localeResolver;
	protected Router router;

	public DetailHandler(DSLContext dslContext, ThymeleafViewResolver viewResolver, LocaleResolver localeResolver) {
		this.dslContext = dslContext;
		this.viewResolver = viewResolver;
		this.localeResolver = localeResolver;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public void handle(String table, String id, HttpServletRequest request, HttpServletResponse response) {
		try {
			Record record = dslContext
				.select()
				.from(table)
				.where(field("_id").equal(id))
				.fetchOne();
			viewResolver
				.resolveViewName("detail", localeResolver.resolve(request))
				.render(ImmutableMap.of("record", record), request, response);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
