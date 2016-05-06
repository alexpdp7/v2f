package net.pdp7.v2f.core.dao;

import org.jooq.DSLContext;
import org.springframework.cache.annotation.Cacheable;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;

public class CatalogRepository {

	protected final DSLContext dslContext;
	protected final SchemaCrawlerOptions schemaCrawlerOptions;

	public CatalogRepository(DSLContext dslContext, SchemaCrawlerOptions schemaCrawlerOptions) {
		this.dslContext = dslContext;
		this.schemaCrawlerOptions = schemaCrawlerOptions;
	}

	@Cacheable(cacheNames = "catalogs")
	public Catalog getCatalog() {
		return (Catalog) dslContext
				.connectionResult(connection -> SchemaCrawlerUtility.getCatalog(connection, schemaCrawlerOptions));
	}
}
