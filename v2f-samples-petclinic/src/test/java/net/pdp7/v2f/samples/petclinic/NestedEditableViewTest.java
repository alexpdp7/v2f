package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.field;

import java.sql.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NestedEditableViewTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		int ownerId = insertOwner("name" + UUID.randomUUID(), "contactInformation" + UUID.randomUUID(), "emailAddress" + UUID.randomUUID());
		int petId = insertPet("name" + UUID.randomUUID(), ownerId, "cat", new Date(0));
		WebDriver driver = getDriver();
		driver.get(getRoute("/owners/detail/" + ownerId + "/"));
		WebElement petNameEdit = driver.findElement(By.cssSelector(".edit_column_name__list_edit.edit_table_owners__nested__pets"));
		petNameEdit.clear();
		petNameEdit.sendKeys("foo");
		driver.findElement(By.cssSelector("input[name=action][value=save]")).click();
		Assert.assertEquals("foo", dslContext.select().from("pets").where(field("_id").equal(petId)).fetchOne().getValue("name"));
	}

}
