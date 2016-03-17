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
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.ViewRenderer;
import net.pdp7.v2f.core.web.handlers.DetailHandler;
import net.pdp7.v2f.core.web.handlers.IndexHandler;
import net.pdp7.v2f.core.web.handlers.ListHandler;
import net.pdp7.v2f.core.web.handlers.SaveHandler;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

@Configuration
public class DefaultConfiguration {

	@Bean
	public Router router() {
		return new Router(listHandler(), detailHandler(), indexHandler());
	}

	@Bean
	public DAO dao() {
		return new DAO(dslContext, schemaCrawlerOptions(), v2fSchema);
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
		return new ListHandler(dao(), viewRenderer());
	}

	@Bean
	public DetailHandler detailHandler() {
		return new DetailHandler(dao(), viewRenderer());
	}

	@Bean
	public SaveHandler saveHandler() {
		return new SaveHandler(dao());
	}

	@Bean
	public Object dummyCircular() {
		listHandler().setRouter(router());
		detailHandler().setRouter(router());
		indexHandler().setRouter(router());
		dao().setRouter(router());
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
}
