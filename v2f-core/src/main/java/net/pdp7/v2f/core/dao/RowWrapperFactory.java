package net.pdp7.v2f.core.dao;

import java.util.Map;

import org.jooq.Record;

import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.WidgetPolicy;
import schemacrawler.schema.Catalog;

public class RowWrapperFactory {

	protected Router router;
	protected Catalog catalog;
	protected final WidgetPolicy widgetPolicy;
	protected final String v2fSchema;

	public RowWrapperFactory(WidgetPolicy widgetPolicy, String v2fSchema) {
		this.widgetPolicy = widgetPolicy;
		this.v2fSchema = v2fSchema;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public RowWrapper build(String table, Record record, String newFormId, Map<String, String[]> formState) {
		assert catalog != null;
		assert router != null;
		return new RowWrapper(this, table, record, newFormId, formState);
	}
}
