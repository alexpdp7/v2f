package net.pdp7.v2f.core;

import junit.framework.TestCase;
import net.pdp7.v2f.core.Router.ListTableRoute;

public class RouterTest extends TestCase {

	public void testRouter() {
		ListTableRoute route = (ListTableRoute) new Router(null).findRoute("/table_name/");
		assertEquals("table_name", route.table);
	}
}
