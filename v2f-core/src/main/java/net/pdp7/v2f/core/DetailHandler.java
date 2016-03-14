package net.pdp7.v2f.core;

import static org.jooq.impl.DSL.field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.ImmutableMap;

import schemacrawler.schema.Catalog;

public class DetailHandler {

	protected final DSLContext dslContext;
	protected final ThymeleafViewResolver viewResolver;
	protected final LocaleResolver localeResolver;
	protected final Catalog catalog;
	protected Router router;

	public DetailHandler(DSLContext dslContext, ThymeleafViewResolver viewResolver, LocaleResolver localeResolver,
			Catalog catalog) {
		this.dslContext = dslContext;
		this.viewResolver = viewResolver;
		this.localeResolver = localeResolver;
		this.catalog = catalog;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	/**
	 * @param id is null for "new" rows
	 */
	public void handle(String table, String id, HttpServletRequest request, HttpServletResponse response) {
		try {
			RowWrapper row = new RowWrapper(
					router,
					catalog,
					table,
					id == null ? null : loadRecord(table, id),
					id == null ? "0" : null);
			viewResolver
					.resolveViewName("detail", localeResolver.resolve(request))
					.render(ImmutableMap.of("row", row), request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Record loadRecord(String table, String id) {
		Record record = dslContext
				.select()
				.from(table)
				.where(field("_id").cast(String.class).equal(id))
				.fetchOne();
		if (record == null) {
			throw new RuntimeException("could not load record with id " + id);
		}
		return record;
	}

}
