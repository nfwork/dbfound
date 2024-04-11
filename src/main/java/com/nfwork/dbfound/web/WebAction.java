package com.nfwork.dbfound.web;

import java.util.List;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.dto.FileDownloadResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.web.file.FileDownloadUtil;

class WebAction {

	static void query(Context context, String modelName, String queryName) {
		ResponseObject ro = ModelEngine.query(context, modelName,
				queryName, null, true);
		WebWriter.jsonWriter(context.response, JsonUtil.toJson(ro));
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
			if(ro instanceof FileDownloadResponseObject){
				FileDownloadResponseObject fd = (FileDownloadResponseObject) ro;
				FileDownloadUtil.download(fd.getFile(),fd.getParams(),context.response);
			}else {
				WebWriter.jsonWriter(context.response, JsonUtil.toJson(ro));
			}
		}
	}

}
