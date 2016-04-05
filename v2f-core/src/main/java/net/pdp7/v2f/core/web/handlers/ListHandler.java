package net.pdp7.v2f.core.web.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.exception.DataAccessException;

import com.google.common.collect.ImmutableMap;

import net.pdp7.v2f.core.dao.DAO;
import net.pdp7.v2f.core.dao.RowWrapper;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.ViewRenderer;

public class ListHandler {

	protected final DAO dao;
	public final ViewRenderer viewRenderer;
	protected final Router router;

	public ListHandler(DAO dao, ViewRenderer viewRenderer, Router router) {
		this.dao = dao;
		this.viewRenderer = viewRenderer;
		this.router = router;
	}

	public void handle(String table, HttpServletRequest request, HttpServletResponse response)
			throws DataAccessException {
		List<RowWrapper> rows = dao.getList(table);
		ImmutableMap<String, ?> model = new ImmutableMap.Builder<String, Object>()
				.put("rows", rows)
				.put("new_url", router.getNewRoute(table))
				.build();
		viewRenderer.renderView(request, response, model, "list");
	}
}
