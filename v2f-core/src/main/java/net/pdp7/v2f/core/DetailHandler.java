package net.pdp7.v2f.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;

public class DetailHandler {

	protected final DAO dao;
	protected final ViewRenderer viewRenderer;
	protected Router router;

	public DetailHandler(DAO dao, ViewRenderer viewRenderer) {
		this.dao = dao;
		this.viewRenderer = viewRenderer;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	/**
	 * @param id is null for "new" rows
	 */
	public void handle(String table, String id, HttpServletRequest request, HttpServletResponse response) {
		RowWrapper row = new RowWrapper(
				router,
				dao.catalog,
				table,
				id == null ? null : dao.loadRecord(table, id),
				id == null ? "0" : null);
		ImmutableMap<String, ?> model = new ImmutableMap.Builder<String, Object>()
				.put("row", row)
				.put("success_url", router.getListTableRoute(table))
				.build();
		viewRenderer.renderView(request, response, model, "detail");
	}


}
