package net.pdp7.v2f.core;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;

import net.pdp7.v2f.core.Router.FormInputName;

public class SaveHandler {

	protected final DSLContext dslContext;

	public SaveHandler(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getParameterMap().entrySet().stream()
				.filter(e -> Router.FormInputName.booleanIsFormInputName(e.getKey()))
				.map(FormValue::new)
				.collect(Collectors.groupingBy(FormValue::getTableAndIds))
				.entrySet().stream()
				.forEach(e -> save(e.getKey(), e.getValue()));
		response.sendRedirect(request.getParameter("success_url"));
	}

	protected void save(TableAndIds tableAndIds, List<FormValue> list) {
		if (tableAndIds.id == null) {
			insert(tableAndIds, list);
		} else {
			update(tableAndIds, list);
		}
	}

	protected void insert(TableAndIds tableAndIds, List<FormValue> list) {
		dslContext.insertInto(table(tableAndIds.table))
				.set(list.stream().collect(Collectors.toMap(fv -> field(fv.formInputName.column), fv -> fv.value)))
				.execute();
	}

	protected void update(TableAndIds tableAndIds, List<FormValue> list) {
		dslContext.update(table(tableAndIds.table))
				.set(list.stream().collect(Collectors.toMap(fv -> field(fv.formInputName.column), fv -> fv.value)))
				.where(field("_id").cast(String.class).equal(tableAndIds.id))
				.execute();
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
