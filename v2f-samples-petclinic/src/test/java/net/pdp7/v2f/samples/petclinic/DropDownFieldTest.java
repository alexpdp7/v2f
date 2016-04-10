package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class DropDownFieldTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		String name = "dropDownFieldTestPet" + UUID.randomUUID();
		WebDriver driver = getDriver();
		// create a new pet
		driver.get(getRoute("/pets/new/"));
		driver.findElement(By.cssSelector(".edit_table_pets.edit_column_name")).sendKeys(name);
		driver.findElement(By.cssSelector(".edit_table_pets.edit_column_birth")).sendKeys("1983-05-02");
		new Select(driver.findElement(By.cssSelector(".edit_table_pets.edit_column_species"))).selectByVisibleText("Iguana");
		driver.findElement(By.cssSelector("input[type=submit]")).click();

		// check species was inserted correctly
		Map<String, Object> insertedPet = dslContext
				.select()
				.from(table("pets"))
				.where(field("name").eq(name))
				.fetchOneMap();
		Map<String, Object> insertedSpecies = dslContext
				.select()
				.from(table("species"))
				.where(field("_id").eq(insertedPet.get("species")))
				.fetchOneMap();

		Assert.assertEquals("Iguana", insertedSpecies.get("name"));

		// load inserted pet
		driver.get(getRoute("/pets/detail/" + insertedPet.get("_id") + "/"));

		// check that species is displayed correctly
		Assert.assertEquals("Iguana", new Select(driver.findElement(By.cssSelector(".edit_table_pets.edit_column_species"))).getFirstSelectedOption().getText());

		// edit species
		new Select(driver.findElement(By.cssSelector(".edit_table_pets.edit_column_species"))).selectByVisibleText("Lizard");
		driver.findElement(By.cssSelector("input[type=submit]")).click();

		// check species was updated correctly
		Map<String, Object> updatedPet = dslContext
				.select()
				.from(table("pets"))
				.where(field("name").eq(name))
				.fetchOneMap();
		Map<String, Object> updatedSpecies = dslContext
				.select()
				.from(table("species"))
				.where(field("_id").eq(updatedPet.get("species")))
				.fetchOneMap();

		Assert.assertEquals("Lizard", updatedSpecies.get("name"));
	}

}
