package net.pdp7.v2f.core.dao;

import java.util.Map;

import org.jooq.Record;

import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.WidgetPolicy;
import schemacrawler.schema.Catalog;

public class RowWrapperFactory {

	protected final Router router;
	protected final Catalog catalog;
	protected final WidgetPolicy widgetPolicy;
	protected final String v2fSchema;

	public RowWrapperFactory(Router router, Catalog catalog, WidgetPolicy widgetPolicy, String v2fSchema) {
		this.router = router;
		this.catalog = catalog;
		this.widgetPolicy = widgetPolicy;
		this.v2fSchema = v2fSchema;
	}

	public RowWrapper build(String table, Record record, String newFormId, Map<String, String[]> formState) {
		return new RowWrapper(this, table, record, newFormId, formState);
	}
}
