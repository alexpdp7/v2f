package net.pdp7.v2f.core.dao;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;

public class DAO {

	protected final DSLContext dslContext;
	protected final String v2fSchema;
	protected RowWrapperFactory rowWrapperFactory;
	protected final SchemaCrawlerOptions schemaCrawlerOptions;

	public DAO(DSLContext dslContext, String v2fSchema, SchemaCrawlerOptions schemaCrawlerOptions) {
		this.dslContext = dslContext;
		this.v2fSchema = v2fSchema;
		this.schemaCrawlerOptions = schemaCrawlerOptions;
	}

	public void setRowWrapperFactory(RowWrapperFactory rowWrapperFactory) {
		this.rowWrapperFactory = rowWrapperFactory;
	}

	public Catalog getCatalog() {
		// This should be cached, however note that this method should be careful
		// so that it does not execute until the database has been setup!
		return (Catalog) dslContext.connectionResult(connection -> SchemaCrawlerUtility.getCatalog(connection, schemaCrawlerOptions));
	}

	public Table getTable(String table) {
		return getCatalog().lookupTable(getCatalog().lookupSchema(v2fSchema).get(), table).get();
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
			throw new DAOException("table " + tableName + " is not viewable");
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
			throw new DAOException("could not load record with id " + id);
		}
		return record;
	}

	public void insert(String table, Map<Field<Object>, Object> fields) {
		assertViewableView(table);
		dslContext.insertInto(table(table))
				.set(fields)
				.execute();
	}

	public void update(String table, Map<Field<Object>, Object> fields, String id) {
		assertViewableView(table);
		dslContext.update(table(table))
				.set(fields)
				.where(field("_id").cast(String.class).equal(id))
				.execute();
	}

	public List<RowWrapper> getList(String table, int numberOfRows) {
		assertViewableView(table);
		assert rowWrapperFactory != null;
		return dslContext
				.select()
				.from(table)
				.limit(numberOfRows)
				.fetch(record -> rowWrapperFactory.build(table, record, null, null));
	}

	public static class DAOException extends RuntimeException {

		protected DAOException(String message) {
			super(message);
		}
	}

	public List<String> getListColumns(String table) {
		return getTable(table)
				.getColumns()
				.stream()
				.filter(c -> c.getName().endsWith("__list"))
				.map(c -> c.getName().replace("__list", ""))
				.collect(Collectors.toList());
	}
}
