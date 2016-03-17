package net.pdp7.v2f.core;

import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;

public class IndexHandler {

	protected final DAO dao;
	public final ViewRenderer viewRenderer;
	protected Router router;

	public IndexHandler(DAO dao, ViewRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
		this.dao = dao;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> tablesToRoutes = dao.getTables()
				.stream()
				.map(t -> t.getName())
				.collect(Collectors.toMap(s -> s, s -> router.getListTableRoute(s)));
		ImmutableMap<String, Map<String, String>> model = ImmutableMap.of("tables_to_routes", tablesToRoutes);
		viewRenderer.renderView(request, response, model, "index");
	}

	public void setRouter(Router router) {
		this.router = router;
	}
}
