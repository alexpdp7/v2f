package net.pdp7.v2f.samples.petclinic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest(randomPort = true)
public class IntegrationTest {

	@Value("${local.server.port}")
	int port;

	@Test
	public void smokeTest() {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get("http://localhost:"+port);
		Assert.assertEquals(2, driver.findElementsByTagName("li").size());
	}
}
