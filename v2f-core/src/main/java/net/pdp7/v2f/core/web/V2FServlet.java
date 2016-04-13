package net.pdp7.v2f.core.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.pdp7.v2f.core.web.handlers.SaveHandler;

public class V2FServlet extends HttpServlet {

	protected final Router router;
	protected final SaveHandler saveHandler;

	public V2FServlet(Router router, SaveHandler saveHandler) {
		this.router = router;
		this.saveHandler = saveHandler;
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request.getMethod().equals("POST")) {
			saveHandler.handle(request, response);
			return;
		}
		router.route(request, response);
	}

}
