package net.pdp7.v2f.core;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Router {

	protected final ListHandler listHandler;

	public Router(ListHandler listHandler) {
		this.listHandler = listHandler;
	}

	protected Route findRoute(String pathInfo) throws RouteNotFoundException {
		Matcher tableMatcher = Pattern.compile("^/(.*)/$").matcher(pathInfo);
		if (tableMatcher.matches()) {
			return new ListTableRoute(tableMatcher.group(1));
		}
		throw new RouteNotFoundException("no route found for " + pathInfo);
	}

	public void route(HttpServletRequest request, HttpServletResponse response)
			throws RouteNotFoundException, IOException {
		findRoute(request.getPathInfo()).execute(response);
	}

	protected abstract class Route {
		protected abstract void execute(HttpServletResponse response) throws IOException;
	}

	protected class ListTableRoute extends Route {
		public final String table;

		private ListTableRoute(String table) {
			this.table = table;
		}

		@Override
		protected void execute(HttpServletResponse response) throws IOException {
			listHandler.handle(table, response);
		}
	}

	protected static class RouteNotFoundException extends RuntimeException {
		private RouteNotFoundException(String message) {
			super(message);
		}
	}
}
