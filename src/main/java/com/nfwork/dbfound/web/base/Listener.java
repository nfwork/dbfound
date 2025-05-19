package com.nfwork.dbfound.web.base;

import jakarta.servlet.ServletContext;

public interface Listener {
	void init(ServletContext servletContext);

	void destroy();
}
