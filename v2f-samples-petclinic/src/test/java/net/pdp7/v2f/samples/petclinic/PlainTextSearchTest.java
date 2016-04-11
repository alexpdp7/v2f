package net.pdp7.v2f.samples.petclinic;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PlainTextSearchTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		String name1 = "name" + UUID.randomUUID();
		insertOwner(name1, "contactInformation" + UUID.randomUUID(), "emailAddress" + UUID.randomUUID());
		String name2 = "name" + UUID.randomUUID();
		insertOwner(name2, "contactInformation" + UUID.randomUUID(), "emailAddress" + UUID.randomUUID());
		WebDriver driver = getDriver();
		driver.get(getRoute("/owners/"));
		driver.findElement(By.cssSelector("#_plain_text_search_form input[type=text]")).sendKeys(name1);
		driver.findElement(By.cssSelector("#_plain_text_search_form input[type=submit]")).click();
		Assert.assertTrue(driver.getPageSource().contains(name1));
		Assert.assertFalse(driver.getPageSource().contains(name2));
	}
}
