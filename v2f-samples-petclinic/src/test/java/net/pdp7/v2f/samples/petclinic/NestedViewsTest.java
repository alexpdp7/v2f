package net.pdp7.v2f.samples.petclinic;

import static org.jooq.impl.DSL.field;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NestedViewsTest extends AbstractIntegrationTest {

	@Test
	public void test() {
		int upcomingVisitsCount = dslContext.select().from("vets__nested__upcoming_visits").where(field("_parent_id").equal(1)).execute();

		WebDriver driver = getDriver();
		driver.get(getRoute("/vets/detail/1/"));

		List<WebElement> rows = driver.findElements(By.cssSelector("tr"));
		Assert.assertEquals(upcomingVisitsCount, rows.size() - 1);  // -1 for header
	}

}
