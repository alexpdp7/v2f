package net.pdp7.v2f.core;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;

public class V2FServlet extends HttpServlet {

	protected final DSLContext dslContext;
	protected final Router router;

	public V2FServlet(DSLContext dslContext, Router router) {
		this.dslContext = dslContext;
		this.router = router;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		router.route(request, response);
	}

}
