package net.pdp7.v2f.core;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Record;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;

public class RowWrapper {
	protected final Record record;
	protected final String table;
	protected final Router router;
	protected final Catalog catalog;

	public RowWrapper(Router router, Catalog catalog, String table, Record record) {
		this.router = router;
		this.catalog = catalog;
		this.table = table;
		this.record = record;
	}

	public String getAsString() {
		return (String) record.getValue("_as_string");
	}

	public String getId() {
		return (String) record.getValue("_id");
	}

	public String getLink() {
		return router.getDetailRoute(table, getId());
	}

	public List<ColumnWrapper> getColumns() {
		return catalog.lookupTable(catalog.lookupSchema("v2f").get(), table).get()
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

		public Object getValue() {
			return record.getValue(getName());
		}

		public String getFormInputName() {
			return router.getFormInputName(table, getId(), getName());
		}
	}
}
