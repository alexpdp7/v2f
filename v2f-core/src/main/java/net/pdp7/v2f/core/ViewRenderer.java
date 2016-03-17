package net.pdp7.v2f.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.google.common.collect.ImmutableMap;

public class ViewRenderer {

	protected final ThymeleafViewResolver viewResolver;
	protected final LocaleResolver localeResolver;

	public ViewRenderer(ThymeleafViewResolver viewResolver, LocaleResolver localeResolver) {
		this.viewResolver = viewResolver;
		this.localeResolver = localeResolver;
	}

	public void renderView(HttpServletRequest request, HttpServletResponse response, ImmutableMap<String, ?> model, String viewName) {
		try {
			viewResolver
					.resolveViewName(viewName, localeResolver.resolveLocale(request))
					.render(model, request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
