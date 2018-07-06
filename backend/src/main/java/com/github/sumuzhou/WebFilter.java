package com.github.sumuzhou;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class WebFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String requestURI = req.getRequestURI();
		if (StringUtils.startsWith(requestURI, "/static/")) {
			res.setHeader("Cache-Control", "max-age=3600, public");
			res.setHeader("Pragma", "disabled");
		}
		if ("/favicon.ico".equals(requestURI)) {
			res.setHeader("Cache-Control", "max-age=3600, public");
			res.setHeader("Pragma", "disabled");
			return;
		}
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
	}
}
