package com.nfwork.dbfound.model;

import java.util.*;
import javax.servlet.http.Cookie;

import com.nfwork.dbfound.model.base.Count;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.ExecuteNotFoundException;
import com.nfwork.dbfound.exception.FileDownLoadInterrupt;
import com.nfwork.dbfound.exception.QueryNotFoundException;
import com.nfwork.dbfound.model.bean.Execute;
import com.nfwork.dbfound.model.bean.Filter;
import com.nfwork.dbfound.model.bean.Model;
import com.nfwork.dbfound.model.bean.Param;
import com.nfwork.dbfound.model.bean.Query;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.file.FileDownloadUtil;

public class ModelEngine {

	public static final String defaultBatchPath = "param.GridData";

	public static final String defaultPath = "param";

	/**
	 * 查询 根据传入的class返回对应的对象集合
	 * 
	 * @param context
	 * @param modelName
	 * @param queryName
	 * @param object
	 * @return
	 */
	public static <T> QueryResponseObject<T> query(Context context, String modelName, String queryName, Class<T> object) {
		return query(context, modelName, queryName, defaultPath, true, object);
	}

	/**
	 * 查询 返回一个list的Map集合
	 * 
	 * @param context
	 * @param modelName
	 * @param queryName
	 * @return
	 */
	public static QueryResponseObject query(Context context, String modelName, String queryName) {
		return query(context, modelName, queryName, defaultPath, true, null);
	}

	/**
	 * 可指定当前路径，是否自动分页的查询
	 * 
	 * @param context
	 * @param modelName
	 * @param queryName
	 * @param currentPath
	 * @param autoPaging
	 * @return
	 */
	public static QueryResponseObject query(Context context, String modelName, String queryName, String currentPath, boolean autoPaging) {
		return query(context, modelName, queryName, currentPath, autoPaging, null);
	}

	/**
	 * 查询 可以指定当前路径、是否自动分页、返回对象的查询
	 * 
	 * @param context
	 * @param modelName
	 * @param queryName
	 * @param currentPath
	 * @param autoPaging
	 * @param obect
	 * @return
	 */
	public static <T> QueryResponseObject<T> query(Context context, String modelName, String queryName, String currentPath, boolean autoPaging,
			Class<T> obect) {

		LogUtil.info("-----------------------query begin--------------------------------------");
		try {
			if (queryName == null || "".equals(queryName))
				queryName = "_default";

			LogUtil.info("Query info (modelName:" + modelName + ", queryName:" + queryName + ")");

			Model model = context.getModel(modelName);

			// 把model、currentPath对象放入到 当前线程里
			context.setCurrentModel(modelName);
			context.setCurrentPath(currentPath);

			Query query = model.getQuery(queryName);
			if (query == null) {
				throw new QueryNotFoundException("can not found Query:" + queryName + ", on Model:" + modelName);
			}
			if (context.isExport()) {
				context.setQueryLimitSize(context.getReportQueryLimitSize());
			}

			// 初始化查询参数param
			Map<String, Object> elCache = new HashMap<>();
			Map<String, Param> params = query.cloneParams();
			for (Param nfParam : params.values()) {
				setParam(nfParam, context, currentPath, elCache);
			}

			// 初始化查询过滤参数filter
			Map<String, Filter> filters = query.cloneFilters();
			for (Filter filter : filters.values()) {
				setParam(filter, context, currentPath, elCache);
				Object value = filter.getValue();
				if(DataUtil.isNotNull(value)){
					filter.setActive(true);
				}
				if(filter.isActive()){
					params.put(filter.getName(), filter);
				}
			}

			// 设想分页参数
			if (autoPaging) {
				String startMessage = context.getString("param.start");
				if (DataUtil.isNotNull(startMessage)) {
					long start = Long.parseLong(startMessage);
					context.setStartWith(start);
				}
				String sizeMessage = context.getString("param.limit");
				if (DataUtil.isNotNull(sizeMessage)) {
					int size = Integer.parseInt(sizeMessage);
					context.setPagerSize(size);
				}
			}

			if(query.getQueryAdapter() != null){
				query.getQueryAdapter().beforeQuery(context, params);
			}

			String provideName = model.getConnectionProvide(context);
			//获取querySql
			String querySql = query.getQuerySql(context, params, provideName);

			// 查询数据，返回结果
			List<T> datas = query.query(context, querySql, params, provideName, obect, autoPaging);

			QueryResponseObject<T> ro = new QueryResponseObject<T>();
			ro.setDatas(datas);

			int dataSize = datas.size();
			int pSize = context.getPagerSize();
			if(pSize ==0 && query.getPagerSize() != null){
				pSize = query.getPagerSize();
			}
			long start = context.getStartWith();
			if (!autoPaging || pSize == 0 || (pSize > dataSize && start == 0)) {
				ro.setTotalCounts(datas.size());
			} else {
				Count count = query.getCount(querySql);
				count.setDataSize(dataSize);
				count.setTotalCounts(dataSize);

				if(query.getQueryAdapter() != null){
					query.getQueryAdapter().beforeCount(context,params,count);
				}

				if(count.isExecuteCount()) {
					query.countItems(context, count, params, provideName);
				}
				ro.setTotalCounts(count.getTotalCounts());
			}
			ro.setSuccess(true);
			ro.setMessage("success");
			ro.setOutParam(getOutParams(context, params));

			if(query.getQueryAdapter() != null){
				query.getQueryAdapter().afterQuery(context,params,ro);
			}

			return ro;
		} finally {
			context.closeConns();
			LogUtil.info("-----------------------query end----------------------------------------");
		}
	}

	public static ResponseObject batchExecute(Context context, String modelName, String executeName) {
		return batchExecute(context, modelName, executeName, defaultBatchPath);
	}

	/**
	 * 批量执行操作
	 * 
	 * @param context
	 * @param modelName
	 * @param executeName
	 * @param sourcePath
	 * @return
	 */
	public static ResponseObject batchExecute(Context context, String modelName, String executeName, String sourcePath) {

		LogUtil.info("-----------------------batch execute begin------------------------------");
		try {
			if (executeName == null || "".equals(executeName))
				executeName = "addOrUpdate";

			// 批量执行查找客户端数据的路径
			String batchExecutePath = defaultBatchPath;

			// 查询数据，返回结果
			if (sourcePath != null && !"".equals(sourcePath)) {
				batchExecutePath = sourcePath;
			}

			int size = context.getDataLength(batchExecutePath);

			// 向客服端传送成功消息
			ResponseObject ro = new ResponseObject();
			ro.setSuccess(true);
			ro.setMessage("success");

			if (size > 0) {
				Map<String, Param> params = null;
				for (int j = 0; j < size; j++) {
					String en = null;
					String currentPath = batchExecutePath + "[" + j + "]";
					if ("addOrUpdate".equals(executeName)) {
						String status = context.getString(currentPath + "._status");
						if (status != null)
							status = status.toUpperCase();
						if ("NEW".equals(status)) {
							en = "add";
						} else if ("OLD".equals(status)) {
							en = "update";
						} else {
							throw new ExecuteNotFoundException("cant not found (_status) field，can not found Execute");
						}
					} else {
						en = executeName;
					}
					params = executeRun(context, modelName, en, currentPath);
				}
				ro.setOutParam(getOutParams(context, params));
			}
			return ro;

		} finally {
			context.closeConns();
			LogUtil.info("-----------------------batch execute end--------------------------------");
		}
	}

	public static ResponseObject execute(Context context, String modelName, String executeName) {
		return execute(context, modelName, executeName, defaultPath);
	}

	/**
	 * 执行操作
	 * 
	 * @param context
	 * @param modelName
	 * @param executeName
	 * @param currentPath
	 * @return
	 */
	public static ResponseObject execute(Context context, String modelName, String executeName, String currentPath) {

		LogUtil.info("-----------------------execute begin------------------------------------");
		try {
			Map<String, Param> params;

			if (executeName == null || "".equals(executeName))
				executeName = "_default";

			params = executeRun(context, modelName, executeName, currentPath);

			// 向客服端传送成功消息
			ResponseObject ro = new ResponseObject();
			ro.setSuccess(true);
			ro.setMessage("success");
			ro.setOutParam(getOutParams(context, params));
			return ro;
		} finally {
			context.closeConns();
			LogUtil.info("-----------------------execute end--------------------------------------");
		}
	}


	private static  Map<String, Param> executeRun(Context context, String modelName, String executeName, String currentPath) {

		LogUtil.info("Execute info (modelName:" + modelName + ", executeName:" + executeName + ")");

		Model model = context.getModel(modelName);

		Execute execute = model.getExecute(executeName);
		if (execute == null) {
			throw new ExecuteNotFoundException("can not found Execute:" + executeName + ", on Model:" + modelName);
		}

		// 把model、currentPath对象放入到 当前线程里
		context.setCurrentPath(currentPath);
		context.setCurrentModel(modelName);

		Map<String, Param> params = execute.cloneParams();

		// 设想sql查询参数
		Map<String, Object> elCache = new HashMap<>();
		for (Param nfParam : params.values()) {
			setParam(nfParam, context, currentPath, elCache);
		}

		if(execute.getExecuteAdapter()!=null){
			execute.getExecuteAdapter().beforeExecute(context,params);
		}

		execute.executeRun(context, params, model.getConnectionProvide(context)); // 执行

		if(execute.getExecuteAdapter()!=null){
			execute.getExecuteAdapter().afterExecute(context,params);
		}

		return params;
	}

	/**
	 * 处理参数 放入session、cookie、或者以outParam返回
	 * 
	 * @param context
	 * @param params
	 * @return
	 */
	private static Map<String, Object> getOutParams(Context context, Map<String, Param> params) {
		for (Param p : params.values()) {
			if (context.isInWebContainer()) {
				// 设定session参数
				if ("true".equals(p.getAutoSession())) {
					context.setSessionData(p.getName(), p.getValue());
				}
				// 设定cookie参数
				if ("true".equals(p.getAutoCookie())) {
					Cookie cookie = new Cookie(p.getName(), p.getStringValue(context));
					String path = context.request.getContextPath();
					if (!path.endsWith("/")) {
						path = path + "/";
					}
					cookie.setPath(path);
					cookie.setMaxAge(10 * 24 * 60 * 60);
					context.response.addCookie(cookie);
				}
				// 将out参数输出
				if (!"in".equals(p.getIoType())) {
					if (p.getDataType() == DataType.FILE) {
						Transaction transaction = context.getTransaction();
						if (transaction.isOpen()) {
							transaction.commit();
							transaction.end();
						}
						FileDownloadUtil.download(p, params, context.response);
						throw new FileDownLoadInterrupt("file download: " + p.getValue());
					} else {
						context.setOutParamData(p.getName(), p.getValue());
					}
				}
			} else {
				// 将out参数输出
				if (!"in".equals(p.getIoType())) {
					context.setOutParamData(p.getName(), p.getValue());
				}
			}
		}
		return context.getOutParamDatas();
	}

	/**
	 * 参数设定
	 * 
	 * @param nfParam
	 * @param context
	 * @param cp
	 */
	private static void setParam(Param nfParam, Context context, String cp, Map<String, Object> elCache) {

		// 增加UUID取值 在sql执行的时候动态的获取UUID 2012年8月8日8:47:08
		if (nfParam.isUUID()) {
			nfParam.setSourcePathHistory("UUID");
			nfParam.setDataType(DataType.VARCHAR);
			return;
		}
		// end 修改

		if ("out".equals(nfParam.getIoType())) {
			return; // out参数直接返回
		}

		// dbfound1.3新功能，根据sourcePath到pagerContext容器中取数据
		String scope = nfParam.getScope();

		// 设置 当前取值路径
		String currentPath = "";
		if (scope != null && !"".equals(scope)) {
			currentPath = scope;
		} else if (cp != null && !"".equals(cp)) {
			currentPath = cp;
		} else {
			currentPath = defaultPath;
		}

		String realPath = "";// 绝对路径

		// 得到取值的相对路径
		String sourcePath = nfParam.getSourcePath();
		if (sourcePath == null || "".equals(sourcePath)) {
			sourcePath = nfParam.getName();
			realPath = currentPath + "." + sourcePath;
		} else {
			// 判断sourcePath 是绝对路径，还是相当路径
			if (ELEngine.isAbsolutePath(sourcePath)) {
				realPath = sourcePath;
			} else {
				realPath = currentPath + "." + sourcePath;
			}
		}

		// 取值
		Object paramValue = context.getData(realPath, elCache);

		if(nfParam.getDataType() == DataType.COLLECTION){
			int length = DataUtil.getDataLength(paramValue);
			if (length <= 0){
				paramValue = null;
			}
		}

		if (paramValue != null && !"".equals(paramValue)) {
			nfParam.setValue(paramValue);
		}
		nfParam.setSourcePathHistory(realPath);
	}
}
