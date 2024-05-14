package com.nfwork.dbfound.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.URLUtil;
import com.nfwork.dbfound.web.handler.*;

public class DispatcherFilter implements Filter {

	private static String configFilePath;

	private final List<ActionHandler> handlerList = new ArrayList<>();

	/**
	 * 处理请求
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpServletRequest request = (HttpServletRequest) req;

		String requestUrl = request.getServletPath();
		for(ActionHandler handle : handlerList){
			if(handle.isSupport(requestUrl)){
				handle.handle(request,response);
				return;
			}
		}
		if (requestUrl.endsWith(".jsp")) {
			if (request.getCharacterEncoding() == null) {
				request.setCharacterEncoding(DBFoundConfig.getEncoding());// 编码设置
			}
			String basePath = URLUtil.getBasePath(request);
			request.setAttribute("basePath", basePath);
			try {
				if (!InterceptorHandler.jspInterceptor(request, response)) {
					return;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * DBFound引擎初始化
	 */
	public void init(FilterConfig cf) throws ServletException {
		configFilePath = cf.getInitParameter("configFilePath");
		// DBFoundConfig.setClasspath(cf.getServletContext().getRealPath("/WEB-INF/classes"));
		DBFoundConfig.setProjectRoot(cf.getServletContext().getRealPath(""));
		if (configFilePath != null && !configFilePath.isEmpty()) {
			DBFoundConfig.setConfigFilePath(configFilePath);
		}
		DBFoundConfig.init();
		this.handlerList.add(new QueryActionHandler());
		this.handlerList.add(new ExecuteActionHandler());
		this.handlerList.add(new ExportActionHandler());
		this.handlerList.add(new DoActionHandler());
	}

	/**
	 * 容器销毁
	 */
	public void destroy() {
		LogUtil.info("NFWork dbfound " + DBFoundConfig.VERSION +", closing dbfound service");
		DBFoundConfig.destroy();
		LogUtil.info("NFWork dbfound " + DBFoundConfig.VERSION +", dbfound service closed");
	}

	/**
	 * 得到configfile路径
	 *
	 */
	public static String getConfigFilePath() {
		return configFilePath;
	}

}
