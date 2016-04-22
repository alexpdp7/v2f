package net.pdp7.v2f.samples.petclinic;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ListTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		WebDriver driver = getDriver();
		driver.get(getRoute("/species/"));
		Assert.assertEquals(
				dslContext.select().from("species").execute(),
				driver.findElements(By.tagName("tr")).size() -1  // -1 for table header
		);
	}
}
