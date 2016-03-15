package net.pdp7.v2f.core;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;

@Configuration
public class DefaultConfiguration {

	@Bean
	public Router router() {
		return new Router(listHandler(), detailHandler(), indexHandler());
	}

	@Bean
	public IndexHandler indexHandler() {
		return new IndexHandler(viewResolver, localeResolver(), catalog());
	}

	@Bean
	public ListHandler listHandler() {
		return new ListHandler(dslContext, viewResolver, localeResolver(), catalog());
	}

	@Bean
	public DetailHandler detailHandler() {
		return new DetailHandler(dslContext, viewResolver, localeResolver(), catalog());
	}

	@Bean
	public SaveHandler saveHandler() {
		return new SaveHandler(dslContext);
	}

	@Bean
	public LocaleResolver localeResolver() {
		return new LocaleResolver();
	}

	@Bean
	public Object dummyCircular() {
		listHandler().setRouter(router());
		detailHandler().setRouter(router());
		indexHandler().setRouter(router());
		return new Object();
	}

	@Bean
	public Catalog catalog() {
		try {
			SchemaCrawlerOptions options = new SchemaCrawlerOptions();
			// FIXME: configurable schema
			options.setSchemaInclusionRule(s -> s.equals("v2f"));
			return SchemaCrawlerUtility.getCatalog(dataSource.getConnection(), options);
		} catch (SchemaCrawlerException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Autowired
	protected DataSource dataSource;

	@Autowired
	protected DSLContext dslContext;

	@Autowired
	protected ThymeleafViewResolver viewResolver;
}
