package net.pdp7.v2f.core.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.Record;

import schemacrawler.schema.Column;

public class RowWrapper {
	protected final RowWrapperFactory rowWrapperFactory;
	protected final String table;
	/** null for "new" rows */
	protected final Record record;
	/** non-null for new rows" */
	protected final String newFormId;
	protected final Map<String, String[]> formState;

	public RowWrapper(RowWrapperFactory rowWrapperFactory, String table, Record record,
			String newFormId, Map<String, String[]> formState) {
		this.rowWrapperFactory = rowWrapperFactory;
		this.table = table;
		this.record = record;
		this.newFormId = newFormId;
		this.formState = formState;
	}

	public String getAsString() {
		return (String) record.getValue("_as_string");
	}

	/** @return null for "new" rows */
	public String getId() {
		return record == null ? null : record.getValue("_id").toString();
	}

	public String getLink() {
		return record == null ? rowWrapperFactory.router.getNewRoute(table) : rowWrapperFactory.router.getDetailRoute(table, getId());
	}

	public List<ColumnWrapper> getColumns() {
		return rowWrapperFactory.dao.getTable(table)
				.getColumns().stream()
				.filter(c -> !c.getName().startsWith("_"))
				.map(column -> new ColumnWrapper(column))
				.collect(Collectors.toList());
	}

	public class ColumnWrapper {

		protected final Column column;

		public ColumnWrapper(Column column) {
			this.column = column;
		}

		public String getName() {
			return column.getName();
		}

		/** @return null for new rows */
		public Object getValue() {
			return formState != null ? formState.get(getFormInputName())[0]
					: record != null ? record.getValue(getName()) : null;
		}

		public String getCssClass() {
			return "edit_column_" + getName() + " edit_table_" + table + " edit_id_" + getId();
		}

		public String getFormInputName() {
			return rowWrapperFactory.router.getFormInputName(table, getId(), getName(), newFormId);
		}

		public String getWidgetName() {
			return "widget-" + rowWrapperFactory.widgetPolicy.getWidgetName(column);
		}

		public List<RowWrapper> getOptions() {
			return rowWrapperFactory.dao.getList(column.getRemarks().replace("dropdown_", ""));
		}
	}
}
