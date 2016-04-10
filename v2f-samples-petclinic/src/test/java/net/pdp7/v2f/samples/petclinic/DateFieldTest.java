package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.field;

import java.sql.Date;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DateFieldTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		String name = "dateFieldTestPet" + UUID.randomUUID();
		WebDriver driver = getDriver();
		// create a new pet
		driver.get(getRoute("/pets/new/"));
		driver.findElement(By.cssSelector(".edit_table_pets.edit_column_name")).sendKeys(name);
		driver.findElement(By.cssSelector(".edit_table_pets.edit_column_birth")).sendKeys("1983-05-02");
		driver.findElement(By.cssSelector("input[type=submit]")).click();

		// check date was inserted correctly
		Map<String, Object> inserted = dslContext
				.select()
				.from("pets")
				.where(field("name").eq(name))
				.fetchOneMap();
		Assert.assertEquals(Date.valueOf("1983-05-02"), inserted.get("birth"));

		// load inserted pet
		driver.get(getRoute("/pets/detail/" + inserted.get("_id") + "/"));
		// check date is displayed correctly
		Assert.assertEquals("1983-05-02", driver.findElement(By.cssSelector(".edit_table_pets.edit_column_birth")).getAttribute("value"));

		// edit birth date
		driver.findElement(By.cssSelector(".edit_table_pets.edit_column_birth")).clear();
		driver.findElement(By.cssSelector(".edit_table_pets.edit_column_birth")).sendKeys("1972-04-06");
		driver.findElement(By.cssSelector("input[type=submit]")).click();

		// check date was updated correctly
		Map<String, Object> updated = dslContext
				.select()
				.from("pets")
				.where(field("name").eq(name))
				.fetchOneMap();
		Assert.assertEquals(Date.valueOf("1972-04-06"), updated.get("birth"));
	}

}
