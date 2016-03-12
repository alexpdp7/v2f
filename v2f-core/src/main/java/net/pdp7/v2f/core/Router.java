package net.pdp7.v2f.core;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Router {

	public static final Pattern LIST_TABLE_PATTERN = Pattern.compile("^/(.*)/$");

	protected final ListHandler listHandler;

	public Router(ListHandler listHandler) {
		this.listHandler = listHandler;
	}

	protected Route findRoute(String pathInfo) throws RouteNotFoundException {
		Matcher tableMatcher = LIST_TABLE_PATTERN.matcher(pathInfo);
		if (tableMatcher.matches()) {
			return new ListTableRoute(tableMatcher.group(1));
		}
		throw new RouteNotFoundException("no route found for " + pathInfo);
	}

	public void route(HttpServletRequest request, HttpServletResponse response)
			throws RouteNotFoundException, IOException {
		findRoute(request.getPathInfo()).execute(request, response);
	}

	public String getDetailRoute(String table, String id) {
		return new DetailRoute(table, id).getPath();
	}
	
	protected abstract class Route {
		protected abstract void execute(HttpServletRequest request, HttpServletResponse response) throws IOException;
	}

	protected class ListTableRoute extends Route {
		public final String table;

		private ListTableRoute(String table) {
			this.table = table;
		}

		@Override
		protected void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
			listHandler.handle(table, request, response);
		}
	}

	protected class DetailRoute extends Route {
		public final String table;
		public final String id;

		private DetailRoute(String table, String id) {
			this.table = table;
			this.id = id;
		}

		public String getPath() {
			return "/" + table + "/" + id + "/";
		}

		@Override
		protected void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
			throw new UnsupportedOperationException("not implemented yet");
		}
		
	}
	
	protected static class RouteNotFoundException extends RuntimeException {
		private RouteNotFoundException(String message) {
			super(message);
		}
	}
}
