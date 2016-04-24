package net.pdp7.v2f.samples.petclinic;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import net.pdp7.v2f.core.web.Router;
import net.pdp7.v2f.core.web.V2FServlet;
import net.pdp7.v2f.core.web.handlers.SaveHandler;

@WebServlet(urlPatterns = {"", "/pets/*", "/visits/*", "/species/*", "/vets/*", "/owners/*"})
public class PetclinicServlet extends V2FServlet {

	@Autowired
	public PetclinicServlet(Router router, SaveHandler saveHandler) {
		super(router, saveHandler);
	}

}
