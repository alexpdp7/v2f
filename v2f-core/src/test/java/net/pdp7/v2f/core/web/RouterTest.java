package net.pdp7.v2f.core.web;

import junit.framework.TestCase;
import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.Router.ListTableRoute;

public class RouterTest extends TestCase {

	public void testRouter() {
		ListTableRoute route = (ListTableRoute) new Router(null, null, null).findRoute("/table_name/");
		assertEquals("table_name", route.table);
	}

	public void testDetailRoute() {
		assertFalse(new Router(null, null, null).getDetailRoute("table", "funky/id").contains("funky/id"));
	}
}
