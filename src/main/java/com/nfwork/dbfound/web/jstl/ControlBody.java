package com.nfwork.dbfound.web.jstl;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.web.WebWriter;

public class ControlBody extends InitProcedure {

	private static final long serialVersionUID = -5941376965347019531L;

	private boolean outMessage = true;

	@SuppressWarnings("unchecked")
	public int doEndTag() throws JspException {

		// 提交关闭事务
		Context context = getContext();
		Transaction transaction = context.getTransaction();
		transaction.commit();
		transaction.end();

		try {
			if (outMessage) {
				ResponseObject ro = new ResponseObject();
				Object result = context.getData("outParam");
				if (result != null) {
					ro.setOutParam((Map<String, Object>) result);
				}
				// 向客服端传送成功消息
				ro.setSuccess(true);
				ro.setMessage("success");

				WebWriter.jsonWriter((HttpServletResponse) pageContext.getResponse(), JsonUtil.beanToJson(ro, context));
			}
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
		return EVAL_PAGE;
	}

	public boolean isOutMessage() {
		return outMessage;
	}

	public void setOutMessage(boolean outMessage) {
		this.outMessage = outMessage;
	}
}
