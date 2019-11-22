package com.nfwork.dbfound.web.ui;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.MethodUtils;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.web.WebExceptionHandle;
import com.nfwork.dbfound.web.base.StoreDataProvide;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class DataStore extends TagSupport {

	private static final long serialVersionUID = 5802822892288859474L;
	private String templateName = "dataStore.ftl";
	private String id;
	private String url;
	private String dataProvideClass;
	private String dataProvideMethod = "execute";
	private String fields = "";
	private boolean autoLoad;

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		return executeFreemarker(out);
	}

	public int executeFreemarker(Writer out) {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();
		Context context = Context.getCurrentContext(request, response);
		try {
			Configuration cfg = FreemarkFactory.getConfig(pageContext
					.getServletContext());
			// 定义Template对象
			Template template = cfg.getTemplate(templateName);
			// 定义数据
			Map<String, Object> root = new HashMap<String, Object>();

			fields = "'" + fields.replaceAll(",", "','") + "'";
			root.put("ds", this);

			String data = getData(context);
			if (data != null) {
				root.put("data", data);
			}
			template.process(root, out);

		} catch (Exception e) {
			Transaction transaction = context.getTransaction();
			if (transaction.isOpen()) {
				transaction.rollback();
				transaction.end();
			}
			WebExceptionHandle.handle(e, (HttpServletRequest) pageContext
					.getRequest(), (HttpServletResponse) pageContext
					.getResponse());
			return SKIP_PAGE;
		}
		return EVAL_PAGE;
	}

	private String getData(Context context) throws Exception {
		if (dataProvideClass == null) {
			return null;
		}
		Object object = Class.forName(dataProvideClass).newInstance();
		if (object instanceof StoreDataProvide) {
			Object ro = MethodUtils.invokeMethod(object, dataProvideMethod,
					new Object[] { context });
			if (ro instanceof QueryResponseObject) {
				QueryResponseObject qro = (QueryResponseObject) ro;
				return JsonUtil.beanToJson(qro);
			}
		} else {
			throw new DBFoundRuntimeException("Provide必须实现StoreDataProvide接口");
		}

		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDataProvideClass() {
		return dataProvideClass;
	}

	public void setDataProvideClass(String dataProvideClass) {
		this.dataProvideClass = dataProvideClass;
	}

	public String getDataProvideMethod() {
		return dataProvideMethod;
	}

	public void setDataProvideMethod(String dataProvideMethod) {
		this.dataProvideMethod = dataProvideMethod;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public boolean isAutoLoad() {
		return autoLoad;
	}

	public void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

}
