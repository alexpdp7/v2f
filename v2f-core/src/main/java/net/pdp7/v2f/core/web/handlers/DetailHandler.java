package net.pdp7.v2f.core.web.handlers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import net.pdp7.v2f.core.dao.DAO;
import net.pdp7.v2f.core.dao.RowWrapper;
import net.pdp7.v2f.core.dao.RowWrapperFactory;
import net.pdp7.v2f.core.web.FormStateStore;
import net.pdp7.v2f.core.web.PaginationPolicy;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.ViewRenderer;
import net.pdp7.v2f.core.web.WidgetPolicy;
import schemacrawler.schema.Table;

public class DetailHandler {

	protected final DAO dao;
	protected final RowWrapperFactory rowWrapperFactory;
	protected final FormStateStore formStateStore;
	protected final ViewRenderer viewRenderer;
	protected final WidgetPolicy widgetPolicy;
	protected final PaginationPolicy paginationPolicy;
	protected Router router;

	public DetailHandler(DAO dao, RowWrapperFactory rowWrapperFactory, FormStateStore formStateStore,
			ViewRenderer viewRenderer, WidgetPolicy widgetPolicy, PaginationPolicy paginationPolicy) {
		this.dao = dao;
		this.rowWrapperFactory = rowWrapperFactory;
		this.formStateStore = formStateStore;
		this.viewRenderer = viewRenderer;
		this.widgetPolicy = widgetPolicy;
		this.paginationPolicy = paginationPolicy;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	/**
	 * @param id is null for "new" rows
	 */
	public void handle(String table, String id, HttpServletRequest request, HttpServletResponse response) {
		assert router != null : this + " router not configured";
		Map<String, String[]> state = getState(request);
		String internalError = getInternalError(state);
		RowWrapper row = rowWrapperFactory.build(
				table,
				id == null ? null : dao.loadRecord(table, id),
				id == null ? "0" : null,
				state);
		Builder<String, Object> model = new ImmutableMap.Builder<String, Object>()
				.put("user", request.getRemoteUser())
				.put("row", row)
				.put("success_url", router.getListTableRoute(table))
				.put("table", table);

		if (internalError != null) {
			model.put("internal_error", internalError);
		}

		if (state != null) {
			Map<String, Object> searches = state.entrySet().stream()
					.filter(e -> e.getKey().endsWith("__lookup_search"))
					.collect(Collectors.toMap(
							e -> e.getKey().replace("__lookup_search", ""),
							e -> getList(new Router.FormInputName(e.getKey().replace("__lookup_search", "")), e.getValue())));
			model.put("lookups", searches);
		}

		List<Map<String, ?>> nestedTables = dao.getNestedTables(table)
				.stream()
				.map(t -> getNestedList(t, id, state))
				.collect(Collectors.toList());

		model.put("nested_tables", nestedTables);

		viewRenderer.renderView(request, response, model.build(), "detail");
	}

	protected Map<String, ?> getList(Router.FormInputName formInputName, String[] searchTerm) {
		String lookedUpTable = dao.getTable(formInputName.table).lookupColumn(formInputName.column).get().getRemarks().replace("lookup_", "");
		return new ImmutableMap.Builder<String, Object>()
				.put("rows", dao.getList(lookedUpTable, 1000, searchTerm[0]))
				.put("list_columns", dao.getListColumns(lookedUpTable))
				.build();
	}

	protected Map<String, ?> getNestedList(Table table, String id, Map<String, String[]> state) {
		String[] tableParts = table.getName().split("__");
		String parentTable = tableParts[0];
		String nestedName = tableParts[2];
		return new ImmutableMap.Builder<String, Object>()
				.put("rows", dao.getNestedList(
						table.getName(),
						paginationPolicy.defaultPageSize,
						parentTable,
						id,
						state))
				.put("list_columns", dao.getListColumns(table.getName()))
				.put("list_edit_columns", dao.getListEditColumns(table.getName()))
				.put("name", nestedName)
				.build();
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
