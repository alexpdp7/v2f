package net.pdp7.v2f.samples.petclinic;

import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;

import net.pdp7.v2f.core.Router;
import net.pdp7.v2f.core.SaveHandler;
import net.pdp7.v2f.core.V2FServlet;

@WebServlet
public class PetclinicServlet extends V2FServlet {

	@Autowired
	public PetclinicServlet(Router router, SaveHandler saveHandler) {
		super(router, saveHandler);
	}

}
