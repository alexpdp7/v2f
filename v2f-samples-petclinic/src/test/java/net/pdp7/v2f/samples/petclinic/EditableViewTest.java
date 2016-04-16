package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.*;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class EditableViewTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		Integer insertedId = null;

		String during1 = "[\"1979-12-03 10:00:00+01\",\"1979-12-03 12:00:00+01\")";
		String during2 = "[\"1979-12-04 10:00:00+01\",\"1979-12-04 12:00:00+01\")";
		WebDriver driver = getDriver();
		driver.get(getRoute("/visits/new/"));
		driver.findElement(By.cssSelector(".lookup_text_widget_column_pet")).sendKeys("cleo");
		driver.findElement(By.cssSelector(".lookup_submit_column_pet")).click();
		driver.findElements(By.cssSelector("input[type=radio]")).get(1).click();
		new Select(driver.findElement(By.cssSelector(".edit_column_vet"))).selectByVisibleText("Lana Jones");
		driver.findElement(By.cssSelector(".edit_column_during")).sendKeys(during1);
		driver.findElement(By.cssSelector("input[name=action][value=save]")).click();

		try {
			Map<String, Object> inserted = dslContext
					.select()
					.from("visits")
					.orderBy(field("_id").desc())
					.limit(1)
					.fetchOneMap();
			insertedId = (Integer) inserted.get("_id");
			Assert.assertEquals("Cleo", dslContext.select()
					.from("pets")
					.where(field("_id").equal(inserted.get("pet")))
					.fetchOne("name"));
			Assert.assertEquals("Lana Jones", dslContext.select()
					.from("vets")
					.where(field("_id").equal(inserted.get("vet")))
					.fetchOne("name"));
			Assert.assertEquals(1, dslContext.select().from("visits").where("during @> timestamp with time zone '1979-12-03 10:30+01'").execute());
			Assert.assertEquals(0, dslContext.select().from("visits").where("during @> timestamp with time zone '1979-12-04 11:30+01'").execute());

			driver.get(getRoute("/visits/detail/" + insertedId + "/"));
			driver.findElement(By.cssSelector(".lookup_text_widget_column_pet")).sendKeys("scully");
			driver.findElement(By.cssSelector(".lookup_submit_column_pet")).click();
			driver.findElements(By.cssSelector("input[type=radio]")).get(1).click();
			new Select(driver.findElement(By.cssSelector(".edit_column_vet"))).selectByVisibleText("Jules Qux");
			driver.findElement(By.cssSelector(".edit_column_during")).clear();
			driver.findElement(By.cssSelector(".edit_column_during")).sendKeys(during2);
			driver.findElement(By.cssSelector("input[name=action][value=save]")).click();

			Map<String, Object> updated = dslContext
					.select()
					.from("visits")
					.where(field("_id").equal(insertedId))
					.fetchOneMap();
			Assert.assertEquals("Scully", dslContext.select()
					.from("pets")
					.where(field("_id").equal(updated.get("pet")))
					.fetchOne("name"));
			Assert.assertEquals("Jules Qux", dslContext.select()
					.from("vets")
					.where(field("_id").equal(updated.get("vet")))
					.fetchOne("name"));
			Assert.assertEquals(0, dslContext.select().from("visits").where("during @> timestamp with time zone '1979-12-03 10:30+01'").execute());
			Assert.assertEquals(1, dslContext.select().from("visits").where("during @> timestamp with time zone '1979-12-04 11:30+01'").execute());
		}
		finally {
			if (insertedId != null) {
				int deleted = dslContext
					.deleteFrom(table("_visits_editable"))
					.where(field("_id").equal(insertedId))
					.execute();
				Assert.assertEquals(1, deleted);
			}
		}
	}
}
