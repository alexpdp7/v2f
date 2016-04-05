package net.pdp7.v2f.core.web.handlers;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import net.pdp7.v2f.core.dao.DAO;
import net.pdp7.v2f.core.dao.RowWrapper;
import net.pdp7.v2f.core.dao.RowWrapperFactory;
import net.pdp7.v2f.core.web.FormStateStore;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.ViewRenderer;

public class DetailHandler {

	protected final DAO dao;
	protected final RowWrapperFactory rowWrapperFactory;
	protected final FormStateStore formStateStore;
	protected final ViewRenderer viewRenderer;
	protected final Router router;

	public DetailHandler(DAO dao, RowWrapperFactory rowWrapperFactory, FormStateStore formStateStore,
			ViewRenderer viewRenderer, Router router) {
		this.dao = dao;
		this.rowWrapperFactory = rowWrapperFactory;
		this.formStateStore = formStateStore;
		this.viewRenderer = viewRenderer;
		this.router = router;
	}

	/**
	 * @param id is null for "new" rows
	 */
	public void handle(String table, String id, HttpServletRequest request, HttpServletResponse response) {
		Map<String, String[]> state = getState(request);
		String internalError = getInternalError(state);
		RowWrapper row = rowWrapperFactory.build(
				table,
				id == null ? null : dao.loadRecord(table, id),
				id == null ? "0" : null,
				state);
		Builder<String, Object> model = new ImmutableMap.Builder<String, Object>()
				.put("row", row)
				.put("success_url", router.getListTableRoute(table));

		if (internalError != null) {
			model.put("internal_error", internalError);
		}

		viewRenderer.renderView(request, response, model.build(), "detail");
	}

	protected Map<String, String[]> getState(HttpServletRequest request) {
		String formStateStr = request.getParameter(SaveHandler.FORM_STATE_PARAMETER);
		if (formStateStr == null) {
			return null;
		}
		return formStateStore.retrieve(UUID.fromString(formStateStr));
	}

	protected String getInternalError(Map<String, String[]> state) {
		return state == null || !state.containsKey("internal_error") ? null : state.get("internal_error")[0];
	}
}
