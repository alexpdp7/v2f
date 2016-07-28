package net.pdp7.v2f.core.dao;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.springframework.security.core.context.SecurityContextHolder;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;

public class DAO {

	protected final DSLContext dslContext;
	protected final String v2fSchema;
	protected RowWrapperFactory rowWrapperFactory;
	protected final CatalogRepository catalogRepository;

	public DAO(DSLContext dslContext, String v2fSchema, CatalogRepository catalogRepository) {
		this.dslContext = dslContext;
		this.v2fSchema = v2fSchema;
		this.catalogRepository = catalogRepository;
	}

	public void setRowWrapperFactory(RowWrapperFactory rowWrapperFactory) {
		this.rowWrapperFactory = rowWrapperFactory;
	}

	public Catalog getCatalog() {
		return catalogRepository.getCatalog();
	}

	public Table getTable(String table) {
		return getTableOptional(table).get();
	}

	protected Optional<? extends Table> getTableOptional(String table) {
		return getCatalog().lookupTable(getCatalog().lookupSchema(v2fSchema).get(), table);
	}

	public List<Table> getTables() {
		return getCatalog()
				.getTables()
				.stream()
				.filter(this::isViewableView)
				.collect(Collectors.toList());
	}

	public List<Table> getNestedTables(String table) {
		return getCatalog()
				.getTables()
				.stream()
				.filter(t -> t.getName().startsWith(table + "__nested__"))
				.collect(Collectors.toList());
	}

	protected boolean isViewableView(Table table) {
		return isViewableView(table.getName());
	}

	protected boolean isViewableView(String tableName) {
		return !tableName.startsWith("_") && !tableName.contains("__");
	}

	protected boolean isEditableView(String tableName) {
		return isViewableView(tableName) || tableName.contains("__nested__");
	}

	protected void assertViewableView(Table table) {
		assertViewableView(table.getName());
	}

	protected void assertViewableView(String tableName) {
		if (!isViewableView(tableName)) {
			throw new DAOException("table " + tableName + " is not viewable");
		}
	}

	protected void assertEditableView(String tableName) {
		if (!isEditableView(tableName)) {
			throw new DAOException("table " + tableName + " is not viewable");
		}
	}

	public Record loadRecord(String table, String id) {
		assertViewableView(table);
		Record record = selectFrom(table)
				.and(field("_id").cast(String.class).equal(id))
				.fetchOne();
		if (record == null) {
			throw new DAOException("could not load record with id " + id);
		}
		return record;
	}

	public void insert(String table, Map<Field<Object>, Object> fields) {
		assertEditableView(table);
		dslContext.insertInto(getEditTable(table))
				.set(fields)
				.execute();
	}

	public void update(String table, Map<Field<Object>, Object> fields, String id) {
		assertEditableView(table);
		dslContext.update(getEditTable(table))
				.set(fields)
				.where(field("_id").cast(String.class).equal(id))
				.execute();
	}

	protected org.jooq.Table<Record> getEditTable(String table) {
		String editableTableName = "_" + table + "_editable";
		if (getTableOptional(editableTableName).isPresent()) {
			return table(editableTableName);
		}
		return table(table);
	}

	public List<RowWrapper> getList(String table, int numberOfRows, String searchTerms) {
		assertViewableView(table);
		assert rowWrapperFactory != null;
		SelectConditionStep<Record> select = selectFrom(table);
		if (searchTerms != null) {
			for (String term : searchTerms.split("\\s+")) {
				select.and(field("_plain_text_search").lower().contains(term.toLowerCase(Locale.getDefault())));
			}
		}
		return select
				.limit(numberOfRows)
				.fetch(record -> rowWrapperFactory.build(table, record, null, null));
	}

	protected SelectConditionStep<Record> selectFrom(String table) {
		SelectJoinStep<Record> allTable = dslContext
				.select()
				.from(table);
		String permissionsTableName = table + "__permissions";
		if (getTableOptional(permissionsTableName).isPresent()) {
			return allTable
					.join(permissionsTableName).using(field("_id"))
					.where(field("_user_id").equal(SecurityContextHolder.getContext().getAuthentication().getName()));
		}
		return allTable.where("true");
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

	public List<String> getListEditColumns(String table) {
		return getTable(table)
				.getColumns()
				.stream()
				.filter(c -> c.getName().endsWith("__list_edit"))
				.map(c -> c.getName().replace("__list_edit", ""))
				.collect(Collectors.toList());
	}

	public boolean hasPlainTextSearch(String table) {
		return getTable(table).lookupColumn("_plain_text_search").isPresent();
	}

	public List<RowWrapper> getNestedList(String table, int numberOfRows, String parentTable, String parentId, Map<String, String[]> formState) {
		return selectFrom(table)
				.and(field("_parent_id").cast(String.class).equal(parentId))
				.limit(numberOfRows)
				.fetch(record -> rowWrapperFactory.build(table, record, null, formState));
	}

	public String getLinkedTable(String table) {
		String tableComment = getTable(table).getRemarks();
		return tableComment.startsWith("link_") ? tableComment.replaceFirst("^link_", "") : table;
	}
}
