package net.pdp7.v2f.core;

import static org.jooq.impl.DSL.field;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.Record;
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
			List<Row> rows = dslContext
					.select(field("_id"), field("_as_string"))
					.from(table)
					.fetch(record -> new Row(table, record));
			viewResolver
					.resolveViewName("list", localeResolver.resolve(request))
					.render(ImmutableMap.of("rows", rows), request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected class Row {
		protected final Record record;
		protected final String table;

		public Row(String table, Record record) {
			this.table = table;
			this.record = record;
		}

		public String getAsString() {
			return (String) record.getValue("_as_string");
		}

		public String getId() {
			return (String) record.getValue("_id");
		}

		public String getLink() {
			return router.getDetailRoute(table, getId());
		}
		
	}
}
