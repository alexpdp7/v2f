package net.pdp7.v2f.core.dao;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;

import net.pdp7.v2f.core.web.Router;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;

public class DAO {

	protected final DSLContext dslContext;
	protected final SchemaCrawlerOptions schemaCrawlerOptions;
	protected Router router;
	public final String v2fSchema;

	public DAO(DSLContext dslContext, SchemaCrawlerOptions schemaCrawlerOptions, String v2fSchema) {
		this.dslContext = dslContext;
		this.schemaCrawlerOptions = schemaCrawlerOptions;
		this.v2fSchema = v2fSchema;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public Catalog getCatalog() {
		// This should be cached, however note that this method should be careful
		// so that it does not execute until the database has been setup!
		return (Catalog) dslContext.connectionResult(connection -> SchemaCrawlerUtility.getCatalog(connection, schemaCrawlerOptions));
	}

	public List<Table> getTables() {
		return getCatalog()
				.getTables()
				.stream()
				.filter(this::isViewableView)
				.collect(Collectors.toList());
	}

	protected boolean isViewableView(Table table) {
		return isViewableView(table.getName());
	}

	protected boolean isViewableView(String tableName) {
		return !tableName.startsWith("_");
	}

	protected void assertViewableView(Table table) {
		assertViewableView(table.getName());
	}

	protected void assertViewableView(String tableName) {
		if (!isViewableView(tableName)) {
			throw new RuntimeException("table " + tableName + " is not viewable");
		}
	}

	public Record loadRecord(String table, String id) {
		assertViewableView(table);
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
		assertViewableView(table);
		dslContext.insertInto(table(table))
				.set(fields)
				.execute();
	}

	public void update(String table, Map<Field<Object>, String> fields, String id) {
		assertViewableView(table);
		dslContext.update(table(table))
				.set(fields)
				.where(field("_id").cast(String.class).equal(id))
				.execute();
	}

	public List<RowWrapper> getList(String table) {
		assertViewableView(table);
		return dslContext
				.select(field("_id"), field("_as_string"))
				.from(table)
				.fetch(record -> new RowWrapper(router, getCatalog(), table, record, null, v2fSchema));
	}
}
