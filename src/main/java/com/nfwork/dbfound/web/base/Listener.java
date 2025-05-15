package com.nfwork.dbfound.web.base;

import javax.servlet.ServletContext;

public interface Listener {
	void init(ServletContext servletContext);

	void destroy();
}
