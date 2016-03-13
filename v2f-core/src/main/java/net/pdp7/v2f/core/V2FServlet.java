package net.pdp7.v2f.core;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;

public class V2FServlet extends HttpServlet {

	protected final DSLContext dslContext;
	protected final Router router;
	protected final SaveHandler saveHandler;

	public V2FServlet(DSLContext dslContext, Router router, SaveHandler saveHandler) {
		this.dslContext = dslContext;
		this.router = router;
		this.saveHandler = saveHandler;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if(request.getMethod().equals("POST") && request.getParameter("action").equals("save")) {
			saveHandler.handle(request, response);
		}
		router.route(request, response);
	}

}
