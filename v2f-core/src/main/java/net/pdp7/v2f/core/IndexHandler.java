package net.pdp7.v2f.core;

import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.ImmutableMap;

import schemacrawler.schema.Catalog;

public class IndexHandler {

	protected final ThymeleafViewResolver viewResolver;
	protected final LocaleResolver localeResolver;
	protected final Catalog catalog;
	protected Router router;

	public IndexHandler(ThymeleafViewResolver viewResolver, LocaleResolver localeResolver, Catalog catalog) {
		this.viewResolver = viewResolver;
		this.localeResolver = localeResolver;
		this.catalog = catalog;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, String> tablesToRoutes = catalog
					.getTables()
					.stream()
					.map(t -> t.getName())
					.collect(Collectors.toMap(s -> s, s -> router.getListTableRoute(s)));
			ImmutableMap<String, Map<String, String>> model = ImmutableMap.of("tables_to_routes", tablesToRoutes);
			viewResolver
					.resolveViewName("index", localeResolver.resolve(request))
					.render(model, request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setRouter(Router router) {
		this.router = router;
	}
}
