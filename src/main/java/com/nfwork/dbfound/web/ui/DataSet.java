package com.nfwork.dbfound.web.ui;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.web.WebExceptionHandle;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class DataSet extends TagSupport {

	private static final long serialVersionUID = 7492822892288859474L;
	private String templateName = "dataSet.ftl";

	private String id;
	private String queryName;
	private String modelName;
	private String sourcePath;
	private boolean loadData = true;
	private boolean autoCount = false;
	private String fields;

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		return executeFreemarker(out);
	}

	@SuppressWarnings("unchecked")
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

			root.put("id", id);
			root.put("loadData", loadData);
			String url = modelName + ".query";
			if (queryName != null && queryName != "_default") {
				url = url + "!" + queryName;
			}
			root.put("url", url);

			if (loadData) {
				if (queryName == null || "".equals(queryName)) {
					queryName = "_default";
				}
				QueryResponseObject ro = ModelEngine.query(context, modelName,
						queryName, sourcePath, autoCount);
				if (ro != null && ro.getDatas().size() > 0) {
					try {
						Map map0 = (Map) ro.getDatas().get(0);
						root.put("keySet", map0.keySet());
					} catch (Exception e) {
						LogUtil.warn("数据返回格式不是list<map>,自动转化field失败");
					}
				}
				root.put("qro", JsonUtil.toJson(ro));
			}

			if (fields != null) {
				String fieldsString = "'" + fields.replace(",", "','") + "'";
				root.put("fields", fieldsString);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public boolean isLoadData() {
		return loadData;
	}

	public void setLoadData(boolean loadData) {
		this.loadData = loadData;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public boolean isAutoCount() {
		return autoCount;
	}

	public void setAutoCount(boolean autoCount) {
		this.autoCount = autoCount;
	}

}
