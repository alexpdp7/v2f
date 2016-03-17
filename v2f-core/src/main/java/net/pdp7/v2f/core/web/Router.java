package net.pdp7.v2f.core.web;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.UrlEscapers;

import net.pdp7.v2f.core.web.handlers.DetailHandler;
import net.pdp7.v2f.core.web.handlers.IndexHandler;
import net.pdp7.v2f.core.web.handlers.ListHandler;

public class Router {

	public static final Pattern INDEX_PATTERN = Pattern.compile("^/$");
	public static final Pattern LIST_TABLE_PATTERN = Pattern.compile("^/([^/]*)/$");
	public static final Pattern DETAIL_TABLE_PATTERN = Pattern.compile("^/([^/]*)/detail/([^/]*)/$");
	public static final Pattern NEW_TABLE_PATTERN = Pattern.compile("^/([^/]*)/new/$");
	public static final Pattern FORM_INPUT_NAME_PATTERN = Pattern.compile("^([^/]*)/((?:new)|(?:id))/([^/]*)/([^/]*)$");

	protected final ListHandler listHandler;
	protected final DetailHandler detailHandler;
	protected final IndexHandler indexHandler;

	public Router(ListHandler listHandler, DetailHandler detailHandler, IndexHandler indexHandler) {
		this.listHandler = listHandler;
		this.detailHandler = detailHandler;
		this.indexHandler = indexHandler;
	}

	protected Route findRoute(String pathInfo) throws RouteNotFoundException {
		Matcher indexMatcher = INDEX_PATTERN.matcher(pathInfo);
		if (indexMatcher.matches()) {
			return new IndexRoute();
		}
		Matcher tableMatcher = LIST_TABLE_PATTERN.matcher(pathInfo);
		if (tableMatcher.matches()) {
			return new ListTableRoute(tableMatcher.group(1));
		}
		Matcher detailMatcher = DETAIL_TABLE_PATTERN.matcher(pathInfo);
		if (detailMatcher.matches()) {
			return new DetailRoute(detailMatcher.group(1), detailMatcher.group(2));
		}
		Matcher newMatcher = NEW_TABLE_PATTERN.matcher(pathInfo);
		if (newMatcher.matches()) {
			return new NewRoute(newMatcher.group(1));
		}
		throw new RouteNotFoundException("no route found for " + pathInfo);
	}

	public void route(HttpServletRequest request, HttpServletResponse response)
			throws RouteNotFoundException, IOException {
		findRoute(request.getPathInfo()).execute(request, response);
	}

	public String getListTableRoute(String table) {
		return new ListTableRoute(table).getPath();
	}

	public String getDetailRoute(String table, String id) {
		return new DetailRoute(table, id).getPath();
	}

	public String getNewRoute(String table) {
		return new NewRoute(table).getPath();
	}

	/** @see FormInputName */
	public String getFormInputName(String table, String id, String column, String newFormId) {
		return table + "/"
				+ (id == null ? "new/" + newFormId + "/" : "id/" + UrlEscapers.urlFormParameterEscaper().escape(id) + "/")
				+ column;
	}

	public static class FormInputName {
		public final String table;
		/** null for "new" rows */
		public final String id;
		public final String column;
		/** non-null for "new" rows; can use distinct values to have multiple
		 * new forms
		 */
		public final String newFormId;

		public FormInputName(String raw) {
			Matcher matcher = FORM_INPUT_NAME_PATTERN.matcher(raw);
			matcher.matches();
			table = matcher.group(1);
			id = matcher.group(2).equals("id") ? matcher.group(3) : null;
			newFormId = matcher.group(2).equals("id") ? null : matcher.group(3);
			column = matcher.group(4);
		}

		public static boolean booleanIsFormInputName(String raw) {
			return FORM_INPUT_NAME_PATTERN.matcher(raw).matches();
		}
	}

	protected abstract class Route {
		protected abstract void execute(HttpServletRequest request, HttpServletResponse response) throws IOException;
	}

	protected class IndexRoute extends Route {

		protected IndexRoute() {
		}

		public String getPath() {
			return "/";
		}

		@Override
		protected void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
			indexHandler.handle(request, response);
		}
	}

	protected class ListTableRoute extends Route {
		public final String table;

		protected ListTableRoute(String table) {
			this.table = table;
		}

		public String getPath() {
			return "/" + table + "/";
		}

		@Override
		protected void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
			listHandler.handle(table, request, response);
		}
	}

	protected class DetailRoute extends Route {
		public final String table;
		public final String id;

		protected DetailRoute(String table, String id) {
			this.table = table;
			this.id = id;
		}

		public String getPath() {
			return "/" + table + "/detail/" + UrlEscapers.urlFormParameterEscaper().escape(id) + "/";
		}

		@Override
		protected void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
			detailHandler.handle(table, id, request, response);
		}
	}

	protected class NewRoute extends Route {
		public final String table;

		protected NewRoute(String table) {
			this.table = table;
		}

		public String getPath() {
			return "/" + table + "/new/";
		}

		@Override
		protected void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
			detailHandler.handle(table, null, request, response);
		}
	}

	protected static class RouteNotFoundException extends RuntimeException {
		protected RouteNotFoundException(String message) {
			super(message);
		}
	}
}
