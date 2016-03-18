package net.pdp7.v2f.core.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ServletUtils {
	protected ServletUtils() {
	}

	public static void redirect(HttpServletResponse response, String successURL) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(successURL));
	}
}
