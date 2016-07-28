package net.pdp7.v2f.samples.security;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.V2FServlet;
import net.pdp7.v2f.core.web.handlers.SaveHandler;

@WebServlet(urlPatterns = {"", "/users/*", "/private_items/*"})
public class SecurityServlet extends V2FServlet {

	@Autowired
	public SecurityServlet(Router router, SaveHandler saveHandler) {
		super(router, saveHandler);
	}

}
