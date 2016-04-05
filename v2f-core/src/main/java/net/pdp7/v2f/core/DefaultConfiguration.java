package net.pdp7.v2f.core;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import net.pdp7.v2f.core.dao.DAO;
import net.pdp7.v2f.core.dao.RowWrapperFactory;
import net.pdp7.v2f.core.web.FormStateStore;
import net.pdp7.v2f.core.web.MemoryFormStateStore;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.ViewRenderer;
import net.pdp7.v2f.core.web.WidgetPolicy;
import net.pdp7.v2f.core.web.handlers.DetailHandler;
import net.pdp7.v2f.core.web.handlers.IndexHandler;
import net.pdp7.v2f.core.web.handlers.ListHandler;
import net.pdp7.v2f.core.web.handlers.SaveHandler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

@Configuration
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
	// TODO: depend on FlywayMigrationInitializer to make sure database
	// has been created so catalog is loaded correctly
	public DAO dao() {
		return new DAO(dslContext, schemaCrawlerOptions(), rowWrapperFactory());
	}

	@Bean
	public Catalog catalog() {
		return dao().getCatalog();
	}

	@Bean
	public FormStateStore formStateStore() {
		// This is a naive implementation, not for serious production usage
		return new MemoryFormStateStore();
	}

	@Bean
	public RowWrapperFactory rowWrapperFactory() {
		return new RowWrapperFactory(router(), catalog(), widgetPolicy(), v2fSchema);
	}

	@Bean
	public ViewRenderer viewRenderer() {
		return new ViewRenderer(viewResolver, new FixedLocaleResolver());
	}

	@Bean
	public IndexHandler indexHandler() {
		return new IndexHandler(dao(), viewRenderer(), router());
	}

	@Bean
	public ListHandler listHandler() {
		return new ListHandler(dao(), viewRenderer(), router());
	}

	@Bean
	public DetailHandler detailHandler() {
		return new DetailHandler(dao(), rowWrapperFactory(), formStateStore(), viewRenderer(), router());
	}

	@Bean
	public SaveHandler saveHandler() {
		return new SaveHandler(dao(), formStateStore());
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
}
