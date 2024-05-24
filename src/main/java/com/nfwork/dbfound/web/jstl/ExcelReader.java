package com.nfwork.dbfound.web.jstl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.web.file.FilePart;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.web.ExceptionHandlerFacade;

public class ExcelReader extends TagSupport {

	private static final long serialVersionUID = -3641375765346919531L;

	private String sourceName;
	private String rootPath;

	@Override
	public int doEndTag() throws JspException {

		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();
		Context context = Context.getCurrentContext(request, response);
		try {
			Object object = request.getAttribute(sourceName);
			if (object instanceof FilePart) {
				List<List<Map<String,Object>>> datas = com.nfwork.dbfound.excel.ExcelReader.readExcel((FilePart) object);
				context.setData(rootPath, datas);
			}
		} catch (Exception e) {
			ExceptionHandlerFacade.handle(e, request, response);
			return SKIP_PAGE;
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

}
