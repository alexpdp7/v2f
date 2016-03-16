package net.pdp7.v2f.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.ImmutableMap;

import schemacrawler.schema.Catalog;

public class DetailHandler {

	protected final DAO dao;
	protected final ThymeleafViewResolver viewResolver;
	protected final LocaleResolver localeResolver;
	protected final Catalog catalog;
	protected Router router;

	public DetailHandler(DAO dao, ThymeleafViewResolver viewResolver, LocaleResolver localeResolver,
			Catalog catalog) {
		this.dao = dao;
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
					id == null ? null : dao.loadRecord(table, id),
					id == null ? "0" : null);
			ImmutableMap<String, ?> model = new ImmutableMap.Builder<String, Object>()
					.put("row", row)
					.put("success_url", router.getListTableRoute(table))
					.build();
			viewResolver
					.resolveViewName("detail", localeResolver.resolve(request))
					.render(model, request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
