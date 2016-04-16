package net.pdp7.v2f.core.web.handlers;

import static org.jooq.impl.DSL.field;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
import org.jooq.Field;

import net.pdp7.v2f.core.dao.DAO;
import net.pdp7.v2f.core.utils.ServletUtils;
import net.pdp7.v2f.core.web.FormStateStore;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.Router.FormInputName;

public class SaveHandler {

	public static final String FORM_STATE_PARAMETER = "form_state";

	protected final DAO dao;
	protected final FormStateStore formStateStore;

	public SaveHandler(DAO dao, FormStateStore formStateStore) {
		this.dao = dao;
		this.formStateStore = formStateStore;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request.getParameter("action").equals("save")) {
			try {
				request.getParameterMap().entrySet().stream()
						.filter(e -> Router.FormInputName.booleanIsFormInputName(e.getKey()))
						.map(FormValue::new)
						.collect(Collectors.groupingBy(FormValue::getTableAndIds))
						.entrySet().stream()
						.forEach(e -> save(e.getKey(), e.getValue()));
				ServletUtils.redirect(response, request.getParameter("success_url"));
			} catch (Exception e) {
				redirectToSavedForm(request, response, e);
			}
			return;
		}
		redirectToSavedForm(request, response, null);
	}

	protected void redirectToSavedForm(HttpServletRequest request, HttpServletResponse response, Exception error) throws IOException {
		Map<String, String[]> formState = new HashMap<>(request.getParameterMap());
		if (error != null) {
			formState.put("internal_error", new String[] {error.toString()});
		}
		UUID uuid = formStateStore.store(formState);
		try {
			String redirectURI = new URIBuilder(request.getPathInfo())
					.addParameter(FORM_STATE_PARAMETER, uuid.toString())
					.toString();
			ServletUtils.redirect(response, redirectURI);
		} catch (URISyntaxException e1) {
			throw new RuntimeException("Could not parse " + request.getPathInfo(), e1);
		}
	}

	protected void save(TableAndIds tableAndIds, List<FormValue> list) {
		String table = tableAndIds.table;
		Map<Field<Object>, Object> fields = list
				.stream()
				.filter(fv -> !fv.formInputName.column.endsWith("__lookup_search"))
				.collect(Collectors.toMap(fv -> field(fv.formInputName.column), fv -> convertToObject(fv)));
		if (tableAndIds.id == null) {
			dao.insert(table, fields);
		} else {
			dao.update(table, fields, tableAndIds.id);
		}
	}

	protected Object convertToObject(FormValue formValue) {
		String type = dao.getTable(formValue.formInputName.table).lookupColumn(formValue.formInputName.column).get().getColumnDataType().getFullName();
		if (type.equals("date")) {
			return parseDate(formValue.value);
		}
		if (type.equals("int4")) {
			return Integer.parseInt(formValue.value);
		}
		if (type.equals("tstzrange")) {
			// see https://github.com/jOOQ/jOOQ/issues/2968#issuecomment-210327626
			return field("?::tstzrange", Object.class, formValue.value);
		}
		return formValue.value;
	}

	protected Date parseDate(String value) {
		try {
			return new Date(new SimpleDateFormat("yyyy-MM-dd").parse(value).getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	protected static class FormValue {
		public final FormInputName formInputName;
		public final String value;

		protected FormValue(Map.Entry<String, String[]> entry) {
			formInputName = new Router.FormInputName(entry.getKey());
			value = entry.getValue()[0];
		}

		public TableAndIds getTableAndIds() {
			return new TableAndIds(formInputName.table, formInputName.id, formInputName.newFormId);
		}
	}

	protected static class TableAndIds {
		public final String table;
		public final String id;
		public final String newFormId;

		protected TableAndIds(String table, String id, String newFormId) {
			this.table = table;
			this.id = id;
			this.newFormId = newFormId;
		}

		@Override
		public int hashCode() {
			return Objects.hash(table, id, newFormId);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TableAndIds)) {
				return false;
			}
			TableAndIds other = (TableAndIds) obj;
			return Objects.equals(table, other.table)
					&& Objects.equals(id, other.id)
					&& Objects.equals(newFormId, other.newFormId);
		}
	}

}
