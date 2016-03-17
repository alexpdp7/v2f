package net.pdp7.v2f.core;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;

public class DAO {

	protected final DSLContext dslContext;
	protected final Catalog catalog;
	protected Router router;

	public DAO(DSLContext dslContext, Catalog catalog) {
		this.dslContext = dslContext;
		this.catalog = catalog;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public Collection<Table> getTables() {
		return catalog.getTables();
	}

	public Record loadRecord(String table, String id) {
		Record record = dslContext
				.select()
				.from(table)
				.where(field("_id").cast(String.class).equal(id))
				.fetchOne();
		if (record == null) {
			throw new RuntimeException("could not load record with id " + id);
		}
		return record;
	}

	public void insert(String table, Map<Field<Object>, String> fields) {
		dslContext.insertInto(table(table))
				.set(fields)
				.execute();
	}

	public void update(String table, Map<Field<Object>, String> fields, String id) {
		dslContext.update(table(table))
				.set(fields)
				.where(field("_id").cast(String.class).equal(id))
				.execute();
	}

	public List<RowWrapper> getList(String table) {
		return dslContext
				.select(field("_id"), field("_as_string"))
				.from(table)
				.fetch(record -> new RowWrapper(router, catalog, table, record, null));
	}
}
