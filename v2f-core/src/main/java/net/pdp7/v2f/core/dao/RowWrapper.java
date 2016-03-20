package net.pdp7.v2f.core.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Record;

import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.WidgetPolicy;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;

public class RowWrapper {
	/** null for "new" rows */
	protected final Record record;
	protected final String table;
	protected final Router router;
	protected final Catalog catalog;
	protected final WidgetPolicy widgetPolicy;
	/** non-null for new rows" */
	protected final String newFormId;
	protected final String v2fSchema;

	public RowWrapper(Router router, Catalog catalog, WidgetPolicy widgetPolicy, String table, Record record, String newFormId, String v2fSchema) {
		this.router = router;
		this.catalog = catalog;
		this.widgetPolicy = widgetPolicy;
		this.table = table;
		this.record = record;
		this.newFormId = newFormId;
		this.v2fSchema = v2fSchema;
	}

	public String getAsString() {
		return (String) record.getValue("_as_string");
	}

	/** @return null for "new" rows */
	public String getId() {
		return record == null ? null : record.getValue("_id").toString();
	}

	public String getLink() {
		return record == null ? router.getNewRoute(table) : router.getDetailRoute(table, getId());
	}

	public List<ColumnWrapper> getColumns() {
		return catalog.lookupTable(catalog.lookupSchema(v2fSchema).get(), table).get()
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
			return record == null ? null : record.getValue(getName());
		}

		public String getFormInputName() {
			return router.getFormInputName(table, getId(), getName(), newFormId);
		}

		public String getWidgetName() {
			return "widget-" + widgetPolicy.getWidgetName(column);
		}
	}
}
