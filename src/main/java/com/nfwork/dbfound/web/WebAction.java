package com.nfwork.dbfound.web;

import java.util.List;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.JsonUtil;

class WebAction {

	static void query(Context context, String modelName, String queryName) {
		ResponseObject ro = ModelEngine.query(context, modelName,
				queryName, null, true);
		WebWriter.jsonWriter(context.response, JsonUtil.beanToJson(ro, context));
	}

	static void execute(Context context, String modelName, String executeName) {
		Object gridData = context.getData(ModelEngine.defaultBatchPath);

		Transaction transaction = context.getTransaction();
		transaction.begin(); // execute 开启事务
		
		// 向客服端传送成功消息
		ResponseObject ro ;
		if (gridData instanceof List) {
			ro = ModelEngine.batchExecute(context, modelName, executeName,
					ModelEngine.defaultBatchPath);
		} else {
			ro = ModelEngine.execute(context, modelName, executeName, ModelEngine.defaultPath);
		}
		
		// 提交关闭事务
		transaction.commit();
		transaction.end();
		
		if (context.isOutMessage()) {
			WebWriter.jsonWriter(context.response, JsonUtil.beanToJson(ro, context));
		}
	}

}
