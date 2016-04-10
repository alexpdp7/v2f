package net.pdp7.v2f.samples.petclinic;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SmokeTest extends AbstractIntegrationTest {
	@Test
	public void smokeTest() {
		WebDriver driver = getDriver();
		driver.get(getRoute("/"));
		Assert.assertEquals(4, driver.findElements(By.tagName("li")).size());
	}
}
