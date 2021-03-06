package net.pdp7.v2f.core.dao;

import java.util.Map;

import org.jooq.Record;

import net.pdp7.v2f.core.web.PaginationPolicy;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.WidgetPolicy;

public class RowWrapperFactory {

	public DAO dao;
	protected Router router;
	protected final WidgetPolicy widgetPolicy;
	public final PaginationPolicy paginationPolicy;

	public RowWrapperFactory(WidgetPolicy widgetPolicy, PaginationPolicy paginationPolicy) {
		this.widgetPolicy = widgetPolicy;
		this.paginationPolicy = paginationPolicy;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	public RowWrapper build(String table, Record record, String newFormId, Map<String, String[]> formState) {
		assert router != null;
		return new RowWrapper(this, table, record, newFormId, formState);
	}
}
