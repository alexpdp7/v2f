package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.field;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasicCRUDTest extends AbstractIntegrationTest {

	@Test
	public void testCreateOwner() {
		String name = "name" + UUID.randomUUID();
		createOwner(name, "contactInformation", "emailAddress");
		Map<String, Object> inserted = dslContext
				.select()
				.from("owners")
				.where(field("name").eq(name))
				.fetchOneMap();
		Assert.assertEquals(name, inserted.get("name"));
		Assert.assertEquals("contactInformation", inserted.get("contact_information"));
		Assert.assertEquals("emailAddress", inserted.get("email_address"));
	}

	@Test
	public void testUpdateOwner() {
		String name = "name" + UUID.randomUUID();
		createOwner(name, "contactInformation", "emailAddress");
		Map<String, Object> inserted = dslContext
				.select()
				.from("owners")
				.where(field("name").eq(name))
				.fetchOneMap();
		int insertedId = (int) inserted.get("_id");
		updateOwner(insertedId, "newName", "newContactInformation", "newEmailAddress");
		Map<String, Object> updated = dslContext
				.select()
				.from("owners")
				.where(field("_id").eq(insertedId))
				.fetchOneMap();
		Assert.assertEquals("newName", updated.get("name"));
		Assert.assertEquals("newContactInformation", updated.get("contact_information"));
		Assert.assertEquals("newEmailAddress", updated.get("email_address"));
	}

	protected void createOwner(String name, String contactInformation, String emailAddress) {
		WebDriver driver = getDriver();
		driver.get(getRoute("/owners/new/"));
		fillFields(name, contactInformation, emailAddress, driver);
		Assert.assertEquals(getRoute("/owners/"), driver.getCurrentUrl());
	}

	protected void updateOwner(int id, String name, String contactInformation, String emailAddress) {
		WebDriver driver = getDriver();
		driver.get(getRoute("/owners/detail/" + id + "/"));
		fillFields(name, contactInformation, emailAddress, driver);
		Assert.assertEquals(getRoute("/owners/"), driver.getCurrentUrl());
	}

	protected void fillFields(String name, String contactInformation, String emailAddress, WebDriver driver) {
		List<WebElement> labels = driver.findElements(By.tagName("label"));
		WebElement nameInput = labels.get(0).findElement(By.tagName("textarea"));
		nameInput.clear();
		nameInput.sendKeys(name);
		WebElement contactInformationInput = labels.get(1).findElement(By.tagName("textarea"));
		contactInformationInput.clear();
		contactInformationInput.sendKeys(contactInformation);
		WebElement emailAddressInput = labels.get(2).findElement(By.tagName("input"));
		emailAddressInput.clear();
		emailAddressInput.sendKeys(emailAddress);
		driver.findElement(By.cssSelector("input[type=submit]")).click();
	}
}
