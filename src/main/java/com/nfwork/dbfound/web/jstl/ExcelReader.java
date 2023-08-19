package com.nfwork.dbfound.web.jstl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.web.file.FilePart;
import org.apache.commons.fileupload.FileItem;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.web.WebExceptionHandle;

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
				try(InputStream inputStream = ((FilePart) object).getContent()) {
					List<List<Map>> datas = com.nfwork.dbfound.excel.ExcelReader.readExcel(inputStream);
					context.setData(rootPath, datas);
				}
			}
		} catch (Exception e) {
			Transaction transaction = context.getTransaction();
			if (transaction.isOpen()) {
				transaction.rollback();
				transaction.end();
			}
			WebExceptionHandle.handle(e, request, response);
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
