package net.pdp7.v2f.core;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.exception.DataAccessException;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.ImmutableMap;

public class ListHandler {

	protected final DAO dao;
	protected final ThymeleafViewResolver viewResolver;
	protected final LocaleResolver localeResolver;
	protected Router router;

	public ListHandler(DAO dao, ThymeleafViewResolver viewResolver, LocaleResolver localeResolver) {
		this.dao = dao;
		this.viewResolver = viewResolver;
		this.localeResolver = localeResolver;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public void handle(String table, HttpServletRequest request, HttpServletResponse response)
			throws DataAccessException {
		try {
			List<RowWrapper> rows = dao.getList(table)
					.fetch(record -> new RowWrapper(router, dao.catalog, table, record, null));
			Map<String, ?> model = new ImmutableMap.Builder<String, Object>()
					.put("rows", rows)
					.put("new_url", router.getNewRoute(table))
					.build();
			viewResolver
					.resolveViewName("list", localeResolver.resolve(request))
					.render(model, request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
