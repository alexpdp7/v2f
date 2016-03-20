package net.pdp7.v2f.core.web.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;

import net.pdp7.v2f.core.dao.DAO;
import net.pdp7.v2f.core.dao.RowWrapper;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.ViewRenderer;
import net.pdp7.v2f.core.web.WidgetPolicy;

public class DetailHandler {

	protected final DAO dao;
	protected final ViewRenderer viewRenderer;
	protected final WidgetPolicy widgetPolicy;
	protected Router router;

	public DetailHandler(DAO dao, ViewRenderer viewRenderer, WidgetPolicy widgetPolicy) {
		this.dao = dao;
		this.viewRenderer = viewRenderer;
		this.widgetPolicy = widgetPolicy;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	/**
	 * @param id is null for "new" rows
	 */
	public void handle(String table, String id, HttpServletRequest request, HttpServletResponse response) {
		assert router != null : this + " router not configured";
		RowWrapper row = new RowWrapper(
				router,
				dao.getCatalog(),
				widgetPolicy,
				table,
				id == null ? null : dao.loadRecord(table, id),
				id == null ? "0" : null,
				dao.v2fSchema);
		ImmutableMap<String, ?> model = new ImmutableMap.Builder<String, Object>()
				.put("row", row)
				.put("success_url", router.getListTableRoute(table))
				.build();
		viewRenderer.renderView(request, response, model, "detail");
	}


}
