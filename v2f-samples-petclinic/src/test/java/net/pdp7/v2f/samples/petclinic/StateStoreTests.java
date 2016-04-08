package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.field;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class StateStoreTests extends AbstractIntegrationTest {

	@Test
	public void test() {
		String name = "species" + UUID.randomUUID();
		WebDriver driver = getDriver();
		createSpecies(driver, name);
		Assert.assertEquals(1, dslContext
				.select()
				.from("species")
				.where(field("name").eq(name))
				.execute());
		// we use the fact that the trigger that assigns ids to species won't
		// work if we add two species with the same name; it should redirect us
		// to the same page preserving form state
		createSpecies(driver, name);
		Assert.assertEquals(1, dslContext
				.select()
				.from("species")
				.where(field("name").eq(name))
				.execute());
		Assert.assertEquals(name, driver.findElement(By.tagName("textarea")).getText());
	}

	protected void createSpecies(WebDriver driver, String name) {
		driver.get(getRoute("/species/new/"));
		List<WebElement> labels = driver.findElements(By.tagName("label"));
		WebElement nameInput = labels.get(0).findElement(By.tagName("textarea"));
		nameInput.clear();
		nameInput.sendKeys(name);
		driver.findElement(By.cssSelector("input[type=submit]")).click();
	}

}
