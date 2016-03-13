package net.pdp7.v2f.core;

import org.jooq.Record;

public class RowWrapper {
	protected final Record record;
	protected final String table;
	protected final Router router;

	public RowWrapper(Router router, String table, Record record) {
		this.router = router;
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

}
