package net.pdp7.v2f.core;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class LocaleResolver {

	public Locale resolve(HttpServletRequest request) {
		return Locale.ENGLISH;
	}

}
