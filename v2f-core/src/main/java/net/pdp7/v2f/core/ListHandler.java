package net.pdp7.v2f.core;

import static org.jooq.impl.DSL.field;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.ImmutableMap;

public class ListHandler {

	protected final DSLContext dslContext;
	protected final ThymeleafViewResolver viewResolver;
	protected final LocaleResolver localeResolver;
	protected Router router;

	public ListHandler(DSLContext dslContext, ThymeleafViewResolver viewResolver, LocaleResolver localeResolver) {
		this.dslContext = dslContext;
		this.viewResolver = viewResolver;
		this.localeResolver = localeResolver;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public void handle(String table, HttpServletRequest request, HttpServletResponse response)
			throws DataAccessException {
		try {
			List<RowWrapper> rows = dslContext
					.select(field("_id"), field("_as_string"))
					.from(table)
					.fetch(record -> new RowWrapper(router, table, record));
			viewResolver
					.resolveViewName("list", localeResolver.resolve(request))
					.render(ImmutableMap.of("rows", rows), request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
