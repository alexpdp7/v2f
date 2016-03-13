package net.pdp7.v2f.core;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

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

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		request.getParameterMap().entrySet().stream()
				.filter(e -> Router.FormInputName.booleanIsFormInputName(e.getKey()))
				.map(FormValue::new)
				.collect(Collectors.groupingBy(FormValue::getTableAndId))
				.entrySet().stream()
				.forEach(e -> save(e.getKey(), e.getValue()));
	}

	protected void save(TableAndId tableAndId, List<FormValue> list) {
		dslContext.update(table(tableAndId.table))
				.set(list.stream().collect(Collectors.toMap(fv -> field(fv.formInputName.column), fv -> fv.value)))
				.where(field("_id").equal(tableAndId.id))
				.execute();
	}

	protected static class FormValue {
		public final FormInputName formInputName;
		public final String value;

		protected FormValue(Map.Entry<String, String[]> entry) {

			formInputName = new Router.FormInputName(entry.getKey());
			value = entry.getValue()[0];
		}

		public TableAndId getTableAndId() {
			return new TableAndId(formInputName.table, formInputName.id);
		}
	}

	protected static class TableAndId {
		public final String table;
		public final String id;

		protected TableAndId(String table, String id) {
			this.table = table;
			this.id = id;
		}

		@Override
		public int hashCode() {
			return Objects.hash(table, id);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TableAndId)) {
				return false;
			}
			TableAndId other = (TableAndId) obj;
			return table.equals(other.table) && id.equals(other.id);
		}
	}

}
