package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import org.jooq.DSLContext;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest(randomPort = true)
public abstract class AbstractIntegrationTest {

	@Value("${local.server.port}")
	int port;

	@Autowired
	public DSLContext dslContext;

	protected String getRoute(String route) {
		return "http://localhost:" + port + route;
	}

	protected WebDriver getDriver() {
		return new HtmlUnitDriver();
	}

	protected void insertOwner(String name, String contactInformation, String emailAddress) {
		dslContext.insertInto(table("owners"))
				.set(field("name"), name)
				.set(field("contact_information"), contactInformation)
				.set(field("email_address"), emailAddress)
				.execute();
	}

}
