package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LookupFieldTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		String common = UUID.randomUUID().toString();
		String owner1 = "lookupFieldTestOwner1" + common + UUID.randomUUID();
		insertOwner(owner1, "contactInformation", "emailAddress");
		String owner2 = "lookupFieldTestOwner2" + common + UUID.randomUUID();
		insertOwner(owner2, "contactInformation", "emailAddress");

		String name = "lookupFieldTestPet" + UUID.randomUUID();
		WebDriver driver = getDriver();
		// create a new pet
		driver.get(getRoute("/pets/new/"));

		// looking for common part yields two results (and the not-lookup-one)
		driver.findElement(By.cssSelector(".lookup_text_widget_column_owner")).sendKeys(common);
		driver.findElement(By.cssSelector(".lookup_submit_column_owner")).click();
		Assert.assertEquals(3, driver.findElements(By.cssSelector("input[type=radio]")).size());

		driver.findElement(By.cssSelector(".lookup_text_widget_column_owner")).sendKeys(owner1);
		driver.findElement(By.cssSelector(".lookup_submit_column_owner")).click();
		Assert.assertEquals(2, driver.findElements(By.cssSelector("input[type=radio]")).size());

		driver.findElement(By.cssSelector(".edit_table_pets.edit_column_name")).sendKeys(name);
		driver.findElement(By.cssSelector(".edit_table_pets.edit_column_birth")).sendKeys("1983-05-02");
		driver.findElements(By.cssSelector("input[type=radio]")).get(1).click();
		driver.findElement(By.cssSelector("input[name=action][value=save]")).click();

		Map<String, Object> inserted = dslContext
				.select()
				.from("pets")
				.where(field("name").eq(name))
				.fetchOneMap();
		Map<String, Object> insertedOwner = dslContext
				.select()
				.from(table("owners"))
				.where(field("_id").eq(inserted.get("owner")))
				.fetchOneMap();

		Assert.assertEquals(insertedOwner.get("name"), owner1);

		// load inserted pet
		driver.get(getRoute("/pets/detail/" + inserted.get("_id") + "/"));

		// check that owner is displayed
		Assert.assertTrue(driver.getPageSource().contains(owner1));

		// update owner
		driver.findElement(By.cssSelector(".lookup_text_widget_column_owner")).sendKeys(owner2);
		driver.findElement(By.cssSelector(".lookup_submit_column_owner")).click();
		driver.findElements(By.cssSelector("input[type=radio]")).get(1).click();
		driver.findElement(By.cssSelector("input[name=action][value=save]")).click();

		// check owner was updated correctly
		Map<String, Object> updated = dslContext
				.select()
				.from("pets")
				.where(field("name").eq(name))
				.fetchOneMap();
		Map<String, Object> updatedOwner = dslContext
				.select()
				.from(table("owners"))
				.where(field("_id").eq(updated.get("owner")))
				.fetchOneMap();

		Assert.assertEquals(updatedOwner.get("name"), owner2);
	}

}
