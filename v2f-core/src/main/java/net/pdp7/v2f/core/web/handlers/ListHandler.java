package net.pdp7.v2f.core.web.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.exception.DataAccessException;

import com.google.common.collect.ImmutableMap;

import net.pdp7.v2f.core.dao.DAO;
import net.pdp7.v2f.core.dao.RowWrapper;
import net.pdp7.v2f.core.web.PaginationPolicy;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.ViewRenderer;

public class ListHandler {

	protected final DAO dao;
	public final ViewRenderer viewRenderer;
	protected Router router;
	protected final PaginationPolicy paginationPolicy;

	public ListHandler(DAO dao, ViewRenderer viewRenderer, PaginationPolicy paginationPolicy) {
		this.dao = dao;
		this.viewRenderer = viewRenderer;
		this.paginationPolicy = paginationPolicy;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public void handle(String table, HttpServletRequest request, HttpServletResponse response, ImmutableMap.Builder<String, Object> model)
			throws DataAccessException {
		assert router != null : this + " router not configured";
		String plainTextSearch = request.getParameter("_plain_text_search");
		List<RowWrapper> rows = dao.getList(table, paginationPolicy.defaultPageSize, plainTextSearch);
		model
				.put("rows", rows)
				.put("list_columns", dao.getListColumns(table))
				.put("has_plain_text_search", dao.hasPlainTextSearch(table))
				.put("new_url", router.getNewRoute(table))
				.put("table", table);
		if (plainTextSearch != null) {
			model.put("_plain_text_search", plainTextSearch);
		}

		viewRenderer.renderView(request, response, model.build(), "list");
	}
}
