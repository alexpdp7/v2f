package net.pdp7.v2f.core;

import static org.jooq.impl.DSL.field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.ImmutableMap;

import schemacrawler.schema.Catalog;

public class DetailHandler {

	protected final DSLContext dslContext;
	protected final ThymeleafViewResolver viewResolver;
	protected final LocaleResolver localeResolver;
	protected final Catalog catalog;
	protected Router router;

	public DetailHandler(DSLContext dslContext, ThymeleafViewResolver viewResolver, LocaleResolver localeResolver, Catalog catalog) {
		this.dslContext = dslContext;
		this.viewResolver = viewResolver;
		this.localeResolver = localeResolver;
		this.catalog = catalog;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public void handle(String table, String id, HttpServletRequest request, HttpServletResponse response) {
		try {
			RowWrapper row = dslContext
				.select()
				.from(table)
				.where(field("_id").equal(id))
				.fetchOne(record -> new RowWrapper(router, catalog, table, record));
			viewResolver
				.resolveViewName("detail", localeResolver.resolve(request))
				.render(ImmutableMap.of("row", row), request, response);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
