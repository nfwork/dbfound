package com.nfwork.dbfound.model;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import org.apache.commons.fileupload.FileItem;
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

			Query query = model.getQuery(queryName);
			if (query == null) {
				throw new QueryNotFoundException("can not found Query:" + queryName + ", on Model:" + modelName);
			}
			if (context.isExport) {
				context.queryLimitSize = context.reportQueryLimitSize;
			}

			query.setCurrentPath(currentPath);

			// 初始化查询参数param
			Collection<Param> params = query.getParams().values();
			for (Param nfParam : params) {
				setParam(nfParam, context, query.getCurrentPath());
			}

			// 初始化查询过滤参数filter
			Collection<Filter> filters = query.getFilters().values();
			for (Filter filter : filters) {
				setParam(filter, context, query.getCurrentPath());
				Object value = filter.getValue();
				if (value != null) {
					if (value instanceof String && !"".equals(value)) {
						filter.setActive(true);
						query.getParams().put(filter.getName(), filter);
					} else if (value instanceof Integer && (Integer) value != 0) {
						filter.setActive(true);
						query.getParams().put(filter.getName(), filter);
					} else if (value instanceof Long && (Long) value != 0) {
						filter.setActive(true);
						query.getParams().put(filter.getName(), filter);
					} else {
						filter.setActive(true);
						query.getParams().put(filter.getName(), filter);
					}
				} else if (filter.isActive()) {
					query.getParams().put(filter.getName(), filter);
				}
			}

			// 设想分页参数
			long start = 0;
			if (autoPaging) {
				String startMessage = context.getString("param.start");
				if (startMessage != null && startMessage != "") {
					start = Long.parseLong(startMessage);
					query.setStartWith(start);
				}
				String sizeMessage = context.getString("param.limit");
				if (sizeMessage != null && sizeMessage != "") {
					int size = Integer.parseInt(sizeMessage);
					query.setPagerSize(size);
				}
			}

			// 查询数据，返回结果
			String provideName = model.getConnectionProvide(context);
			List<T> datas = query.query(context, provideName, obect);

			QueryResponseObject<T> ro = new QueryResponseObject<T>();
			ro.setDatas(datas);

			int dataSize = datas.size();
			int pSize = query.getPagerSize();
			if (autoPaging == false || pSize == 0 || (pSize > dataSize && start == 0)) {
				ro.setTotalCounts(datas.size());
			} else {
				Connection conn = context.getConn(provideName);
				long totalCounts = query.countItems(conn);
				ro.setTotalCounts(totalCounts);
			}
			ro.setSuccess(true);
			ro.setMessage("操作成功!");
			ro.setOutParam(getOutParams(context, query.getParams()));
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
			Execute execute = null;

			Model model = context.getModel(modelName);

			// 查询数据，返回结果
			if (sourcePath != null && !"".equals(sourcePath)) {
				batchExecutePath = sourcePath;
			}

			Object object = context.getData(batchExecutePath);
			int size = 0;
			if (object != null) {
				if (object instanceof Object[]) {
					Object[] objects = (Object[]) object;
					size = objects.length;
				} else if (object instanceof List) {
					List list = (List) object;
					size = list.size();
				} else if (object instanceof Set) {
					Set set = (Set) object;
					size = set.size();
				}
			}

			// 向客服端传送成功消息
			ResponseObject ro = new ResponseObject();
			ro.setSuccess(true);
			ro.setMessage("操作成功!");

			if (size > 0) {
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
					execute = model.getExecute(en);
					if (execute == null) {
						throw new ExecuteNotFoundException("can not found Execute:" + executeName + ", on Model:" + modelName);
					}
					execute.setCurrentPath(currentPath);
					execute(context, model, execute);
				}
				ro.setOutParam(getOutParams(context, execute.getParams()));
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
			if (executeName == null || "".equals(executeName))
				executeName = "_default";

			Model model = context.getModel(modelName);

			Execute execute = model.getExecute(executeName);
			if (execute == null) {
				throw new ExecuteNotFoundException("can not found Execute:" + executeName + ", on Model:" + modelName);
			}
			execute.setCurrentPath(currentPath);
			execute(context, model, execute);

			// 向客服端传送成功消息
			ResponseObject ro = new ResponseObject();
			ro.setSuccess(true);
			ro.setMessage("操作成功!");
			ro.setOutParam(getOutParams(context, execute.getParams()));
			return ro;
		} finally {
			context.closeConns();
			LogUtil.info("-----------------------execute end--------------------------------------");
		}
	}

	/**
	 * 执行execute
	 * 
	 * @param context
	 * @param model
	 * @param execute
	 */
	private static void execute(Context context, Model model, Execute execute) {

		String modelName = model.getModelName();
		String currentPath = execute.getCurrentPath();
		LogUtil.info("Execute info (modelName:" + modelName + ", executeName:" + execute.getName() + ")");

		// 把model、currentPath对象放入到 当前线程里
		context.setCurrentPath(currentPath);
		context.setCurrentModel(modelName);

		// 设想sql查询参数
		Collection<Param> params = execute.getParams().values();
		for (Param nfParam : params) {
			setParam(nfParam, context, currentPath);
		}
		execute.execute(context, model.getConnectionProvide(context)); // 执行
	}

	/**
	 * 处理参数 放入session、cookie、或者以outParam返回
	 * 
	 * @param context
	 * @param params
	 * @return
	 */
	private static Map<String, Object> getOutParams(Context context, Map<String, Param> params) {
		if (params.isEmpty()) {
			return null;
		}

		for (Param p : params.values()) {
			if (context.isInWebContainer()) {
				// 设定session参数
				if ("true".equals(p.getAutoSession())) {
					context.setSessionData(p.getName(), p.getValue());
				}
				// 设定cookie参数
				if ("true".equals(p.getAutoCookie())) {
					Cookie cookie = new Cookie(p.getName(), p.getStringValue());
					String path = context.request.getContextPath();
					if (path.endsWith("/") == false) {
						path = path + "/";
					}
					cookie.setPath(path);
					cookie.setMaxAge(10 * 24 * 60 * 60);
					context.response.addCookie(cookie);
				}
				// 将out参数输出
				if (!"in".equals(p.getIoType())) {
					if ("file".equals(p.getDataType())) {
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
	private static void setParam(Param nfParam, Context context, String cp) {

		// 增加UUID取值 在sql执行的时候动态的获取UUID 2012年8月8日8:47:08
		if ("true".equals(nfParam.getUUID())) {
			nfParam.setSourcePathHistory("UUID");
			nfParam.setDataType("varchar");
			return;
		}
		// end 修改

		if ("out".equals(nfParam.getIoType())) {
			return; // out参数直接返回
		}

		// 如果是文件类型 就直接从request里面取值
		if ("file".equals(nfParam.getDataType())) {
			Object requestValue = context.getData("param." + nfParam.getName());
			if (requestValue != null && requestValue instanceof FileItem) {
				FileItem item = (FileItem) requestValue;
				nfParam.setValue(item);
				return;
			}
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
			if (sourcePath.startsWith(ELEngine.sessionScope) || sourcePath.startsWith(ELEngine.requestScope)
					|| sourcePath.startsWith(ELEngine.outParamScope) || sourcePath.startsWith(ELEngine.paramScope)) {
				realPath = sourcePath;
			} else {
				realPath = currentPath + "." + sourcePath;
			}
		}

		// 取值
		Object paramValue = context.getData(realPath);

		if (paramValue != null && !"".equals(paramValue)) {
			nfParam.setValue(paramValue);
		}
		nfParam.setSourcePathHistory(realPath);
	}
}
