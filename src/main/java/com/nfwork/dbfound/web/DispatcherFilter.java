package com.nfwork.dbfound.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.excel.ExcelWriter;
import com.nfwork.dbfound.exception.DBFoundErrorException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.URLUtil;
import com.nfwork.dbfound.web.file.FileUploadUtil;

public class DispatcherFilter implements Filter {

	private final char[] jsp = "jsp".toCharArray();
	private final char[] query = "query".toCharArray();
	private final char[] execute = "execute".toCharArray();
	private final char[] export = "export".toCharArray();
	private final char[] java = "do".toCharArray();
	private final int jspLength = jsp.length;
	private final int queryLength = query.length;
	private final int executeLength = execute.length;
	private final int exportLength = export.length;
	private final int javaLength = java.length;
	private static String configFilePath;

	/**
	 * 处理请求
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpServletRequest request = (HttpServletRequest) req;

		// 得到客服端请求的uri地址
		String requestUrl = request.getServletPath();
		int actionType = analysisActionType(requestUrl);
		boolean isFileUpload = false;

		if (actionType > 0 && !"OPTIONS".equalsIgnoreCase(request.getMethod())) {
			Transaction transaction = null;
			try {
				if (request.getCharacterEncoding() == null) {
					request.setCharacterEncoding(DBFoundConfig.getEncoding());// 编码设置
				}
				Context context = Context.getCurrentContext(request, response);

				// 初始化文件上传组件
				isFileUpload = FileUploadUtil.isUploadRequest(context);
				if(isFileUpload) {
					FileUploadUtil.initFileUpload(context);
				}

				// 开启事务
				transaction = context.getTransaction();

				// jsp请求
				if (actionType == 1) {
					String basePath = URLUtil.getBasePath(request);
					context.setRequestData("basePath", basePath);
					if (InterceptorEngine.jspInterceptor(request, response)) {
						chain.doFilter(request, response);
					}
				} else {
					requestUrl = URLUtil.clearUrl(requestUrl);
					int indexPlace = requestUrl.indexOf(".");
					String modelName = requestUrl.substring(1, indexPlace);

					// query请求
					if (actionType == 2) {
						String name = requestUrl.substring(indexPlace + 6);
						if (name.isEmpty()) {
							name = "_default";
						} else {
							name = name.substring(1);
						}
						if (InterceptorEngine.queryInterceptor(context, modelName, name)) {
							WebAction.query(context, modelName, name);
						}
					}

					// execute请求
					else if (actionType == 3) {
						String name = requestUrl.substring(indexPlace + 8);
						if (name.isEmpty()) {
							name = "_default";
						} else {
							name = name.substring(1);
						}
						if (InterceptorEngine.executeInterceptor(context, modelName, name)) {
							WebAction.execute(context, modelName, name);
						}
					}

					// export导出请求
					else if (actionType == 4) {
						String name = requestUrl.substring(indexPlace + 7);
						if (name.isEmpty()) {
							name = "_default";
						} else {
							name = name.substring(1);
						}
						if (InterceptorEngine.exportInterceptor(context, modelName, name)) {
							ExcelWriter.excelExport(context, modelName, name);
						}
					}

					// do Java请求
					else if (actionType == 5) {
						String method = requestUrl.substring(indexPlace + 3);
						if (method.isEmpty()) {
							method = "execute";
						} else {
							method = method.substring(1);
						}
						ActionBean actionBean = ActionEngine.findActionBean(modelName); // 得到对应的class类的名字
						if (actionBean != null) {
							if (InterceptorEngine.doInterceptor(context, actionBean.getClassName(), method)) {
								ActionReflect.reflect(context, actionBean.getClassName(), method, actionBean.isSingleton());
							}
						} else {
							String message = "cant not found url:" + requestUrl.substring(1) + " mapping class, please theck config";
							throw new DBFoundRuntimeException(message);
						}
					}
				}
				transaction.commit();
			} catch (Throwable throwable) {
				if (transaction != null) {
					transaction.rollback();
				}
				Exception exception;
				if(throwable instanceof Exception){
					exception = (Exception) throwable;
				}else{
					exception = new DBFoundErrorException("dbfound execute error, cause by "+ throwable.getMessage(), throwable);
				}
				WebExceptionHandle.handle(exception, request, response);
			}
			finally {
				if (transaction != null) {
					transaction.end();
				}
				if(isFileUpload) {
					FileUploadUtil.clearFileItemLocal();
				}
			}

		} else {
			chain.doFilter(request, response);
		}

	}

	/**
	 * 分析Action type
	 */
	public int analysisActionType(String requestUrl) {
		int actionType = 0;

		char[] uriChars = requestUrl.toCharArray();
		int uriLength = uriChars.length;

		for (int i = 1; i < uriLength - 2; i++) {
			if (uriChars[i] == '.') {
				int begin = i + 1;
				int j = 0;

				// jsp请求
				if (jsp[0] == uriChars[begin] && uriLength == begin + jspLength) {
					for (j = 1; j < jspLength; j++) {
						if (jsp[j] != uriChars[begin + j]) {
							break;
						}
					}
					if (j == jspLength) {
						actionType = 1;
						break;
					}
				}

				// query请求
				if (query[0] == uriChars[begin] && uriLength >= begin + queryLength) {
					for (j = 1; j < queryLength; j++) {
						if (query[j] != uriChars[begin + j]) {
							break;
						}
					}
					if (j == queryLength) {
						if (uriLength > begin + j && uriChars[begin + j] != '!') {
							i = i + j;
							continue;
						} else {
							actionType = 2;
							break;
						}
					}
				}

				// execute请求
				if (execute[0] == uriChars[begin] && uriLength >= begin + executeLength) {
					for (j = 1; j < executeLength; j++) {
						if (execute[j] != uriChars[begin + j]) {
							break;
						}
					}
					if (j == executeLength) {
						if (uriLength > begin + j && uriChars[begin + j] != '!') {
							i = i + j;
							continue;
						} else {
							actionType = 3;
							break;
						}
					}
				}

				// export请求
				if (export[0] == uriChars[begin] && uriLength >= begin + exportLength) {
					for (j = 1; j < exportLength; j++) {
						if (export[j] != uriChars[begin + j]) {
							break;
						}
					}
					if (j == exportLength) {
						if (uriLength > begin + j && uriChars[begin + j] != '!') {
							i = i + j;
							continue;
						} else {
							actionType = 4;
							break;
						}
					}
				}

				// java请求
				// 当mvc引擎ActionEngine初始化过，才进行拦截
				if (java[0] == uriChars[begin] && uriLength >= begin + javaLength && ActionEngine.isInited()) {
					for (j = 1; j < javaLength; j++) {
						if (java[j] != uriChars[begin + j]) {
							break;
						}
					}
					if (j == javaLength) {
						if (uriLength > begin + j && uriChars[begin + j] != '!') {
							i = i + j;
							continue;
						} else {
							actionType = 5;
							break;
						}
					}
				}

				i = i + j;
			}
		}
		return actionType;
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
