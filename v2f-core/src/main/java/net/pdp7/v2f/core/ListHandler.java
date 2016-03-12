package net.pdp7.v2f.core;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;

public class ListHandler {

	protected final DSLContext dslContext;

	public ListHandler(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	public void handle(String table, HttpServletResponse response) throws IOException {
		response.getWriter().write(dslContext.select().from(table).fetchMany().toString());
	}

}
