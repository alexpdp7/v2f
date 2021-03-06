package net.pdp7.v2f.core;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import net.pdp7.v2f.core.dao.CatalogRepository;
import net.pdp7.v2f.core.dao.DAO;
import net.pdp7.v2f.core.dao.RowWrapperFactory;
import net.pdp7.v2f.core.web.FormStateStore;
import net.pdp7.v2f.core.web.PaginationPolicy;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.SpringCacheFormStateStore;
import net.pdp7.v2f.core.web.ViewRenderer;
import net.pdp7.v2f.core.web.WidgetPolicy;
import net.pdp7.v2f.core.web.handlers.DetailHandler;
import net.pdp7.v2f.core.web.handlers.IndexHandler;
import net.pdp7.v2f.core.web.handlers.ListHandler;
import net.pdp7.v2f.core.web.handlers.SaveHandler;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

@Configuration
@EnableCaching
@SuppressWarnings("PMD.TooManyMethods")
public class DefaultConfiguration {

	@Bean
	public Router router() {
		return new Router(listHandler(), detailHandler(), indexHandler());
	}

	@Bean
	public WidgetPolicy widgetPolicy() {
		return new WidgetPolicy(1000);
	}

	@Bean
	public PaginationPolicy paginationPolicy() {
		return new PaginationPolicy(50);
	}

	@Bean
	public DAO dao() {
		return new DAO(dslContext, v2fSchema, catalogRepository());
	}

	@Bean
	public CatalogRepository catalogRepository() {
		return new CatalogRepository(dslContext, schemaCrawlerOptions());
	}
	@Bean
	public FormStateStore formStateStore() {
		return new SpringCacheFormStateStore(cacheManager, "formStates");
	}

	@Bean
	public RowWrapperFactory rowWrapperFactory() {
		return new RowWrapperFactory(widgetPolicy(), paginationPolicy());
	}
	@Bean
	public ViewRenderer viewRenderer() {
		return new ViewRenderer(viewResolver, new FixedLocaleResolver());
	}

	@Bean
	public IndexHandler indexHandler() {
		return new IndexHandler(dao(), viewRenderer());
	}

	@Bean
	public ListHandler listHandler() {
		return new ListHandler(dao(), viewRenderer(), paginationPolicy());
	}

	@Bean
	public DetailHandler detailHandler() {
		return new DetailHandler(dao(), rowWrapperFactory(), formStateStore(), viewRenderer(), widgetPolicy(), paginationPolicy());
	}

	@Bean
	public SaveHandler saveHandler() {
		return new SaveHandler(dao(), formStateStore());
	}

	@Bean
	// depend on FlywayMigrationInitializer to make sure database
	// has been created so catalog is loaded correctly
	public Object dummyCircular(FlywayMigrationInitializer flywayMigrationInitializer) {
		listHandler().setRouter(router());
		detailHandler().setRouter(router());
		indexHandler().setRouter(router());
		dao().setRowWrapperFactory(rowWrapperFactory());
		rowWrapperFactory().setDao(dao());
		rowWrapperFactory().setRouter(router());
		return new Object();
	}

	@Value("${v2f.schema}")
	public String v2fSchema;

	@Bean
	public SchemaCrawlerOptions schemaCrawlerOptions() {
		SchemaCrawlerOptions options = new SchemaCrawlerOptions();
		options.setSchemaInclusionRule(s -> s.equals(v2fSchema));
		return options;
	}

	@Autowired
	public DataSource dataSource;

	@Autowired
	public DSLContext dslContext;

	@Autowired
	public ThymeleafViewResolver viewResolver;

	@Autowired
	public CacheManager cacheManager;
}
