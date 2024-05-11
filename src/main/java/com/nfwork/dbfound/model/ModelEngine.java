package com.nfwork.dbfound.model;

import java.util.*;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.ExecuteNotFoundException;
import com.nfwork.dbfound.exception.QueryNotFoundException;
import com.nfwork.dbfound.model.bean.Execute;
import com.nfwork.dbfound.model.bean.Model;
import com.nfwork.dbfound.model.bean.Query;
import com.nfwork.dbfound.util.LogUtil;

public class ModelEngine {

	public static final String defaultBatchPath = "param.GridData";

	public static final String defaultPath = "param";

	private static final ModelCache modelCache = new ModelCache();

	/**
	 * 查询 根据传入的class返回对应的对象集合
	 * 
	 * @param context context
	 * @param modelName modelName
	 * @param queryName queryName
	 * @param clazz class
	 * @return T
	 */
	public static <T> QueryResponseObject<T> query(Context context, String modelName, String queryName, Class<T> clazz) {
		return query(context, modelName, queryName, defaultPath, true, clazz);
	}

	/**
	 * 查询 返回一个list的Map集合
	 * 
	 * @param context context
	 * @param modelName modelName
	 * @param queryName query name
	 * @return QueryResponseObject
	 */
	public static QueryResponseObject<Map<String,Object>> query(Context context, String modelName, String queryName) {
		return query(context, modelName, queryName, defaultPath, true, null);
	}

	/**
	 * 查询 返回一个list的Map集合
	 *
	 * @param context context
	 * @param modelName modelName
	 * @param queryName query name
	 * @param autoPaging 是否分页
	 * @return QueryResponseObject
	 */
	public static QueryResponseObject<Map<String,Object>> query(Context context, String modelName, String queryName, boolean autoPaging) {
		return query(context, modelName, queryName, defaultPath, autoPaging, null);
	}

	/**
	 * 查询 可以指定当前路径、是否自动分页、返回对象的查询
	 *
	 * @param context context
	 * @param modelName model name
	 * @param queryName query name
	 * @param autoPaging auto paging
	 * @param clazz clazz
	 * @return T
	 */
	public static <T> QueryResponseObject<T> query(Context context, String modelName, String queryName, boolean autoPaging, Class<T> clazz) {
		return query(context, modelName, queryName, defaultPath, autoPaging, clazz);
	}

	/**
	 * 可指定当前路径，是否自动分页的查询
	 *
	 * @param context context
	 * @param modelName modelName
	 * @param queryName query name
	 * @param currentPath current path
	 * @param autoPaging auto paging
	 * @return QueryResponseObject
	 */
	public static QueryResponseObject<Map<String,Object>> query(Context context, String modelName, String queryName, String currentPath, boolean autoPaging) {
		return query(context, modelName, queryName, currentPath, autoPaging, null);
	}

	/**
	 * 查询 可以指定当前路径、是否自动分页、返回对象的查询
	 * 
	 * @param context context
	 * @param modelName model name
	 * @param queryName query name
	 * @param currentPath current path
	 * @param autoPaging auto paging
	 * @param clazz clazz
	 * @return T
	 */
	public static <T> QueryResponseObject<T> query(Context context, String modelName, String queryName, String currentPath, boolean autoPaging, Class<T> clazz) {
		try {
			if(context.onTopModelDeep()) {
				LogUtil.info("-----------------------query begin--------------------------------------");
			}
			context.modelDeepIncrease();

			if (DataUtil.isNull(queryName)) {
				queryName = "_default";
			}
			if(DataUtil.isNull(currentPath)){
				currentPath = defaultPath;
			}
			Model model = modelCache.getModel(modelName);

			// 把model、currentPath对象放入到 当前线程里
			context.setCurrentModel(modelName);
			context.setCurrentPath(currentPath);

			Query query = model.getQuery(queryName);
			if (query == null) {
				throw new QueryNotFoundException("can not found Query:" + queryName + ", on Model:" + modelName);
			}
			return query.doQuery(context,modelName,queryName, currentPath, autoPaging, clazz);
		} finally {
			context.modelDeepReduce();
			if(context.onTopModelDeep()) {
				context.closeConns();
				LogUtil.info("-----------------------query end----------------------------------------");
			}
		}
	}


	public static ResponseObject batchExecute(Context context, String modelName, String executeName) {
		return batchExecute(context, modelName, executeName, defaultBatchPath);
	}

	/**
	 * 批量执行操作
	 * 
	 * @param context context
	 * @param modelName model name
	 * @param executeName execute name
	 * @param sourcePath sourcePath;
	 * @return ResponseObject
	 */
	public static ResponseObject batchExecute(Context context, String modelName, String executeName, String sourcePath) {
		try {
			if(context.onTopModelDeep()) {
				LogUtil.info("-----------------------batch execute begin------------------------------");
			}
			context.modelDeepIncrease();

			if (DataUtil.isNull(executeName)) {
				executeName = "addOrUpdate";
			}

			// 批量执行查找客户端数据的路径
			String batchExecutePath;
			if (DataUtil.isNull(sourcePath)) {
				batchExecutePath = defaultBatchPath;
			}else{
				batchExecutePath = sourcePath;
			}

			ResponseObject ro = null;

			int size = context.getDataLength(batchExecutePath);
			if (size > 0) {
				Model model = modelCache.getModel(modelName);

				// 把modelName对象放入到 当前线程里
				context.setCurrentModel(modelName);
				Map<String, Object> elCache = new HashMap<>();

				for (int j = 0; j < size; j++) {
					String en ;
					String currentPath = batchExecutePath + "[" + j + "]";
					// 把currentPath对象放入到 当前线程里
					context.setCurrentPath(currentPath);

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
					Execute execute = model.getExecute(en);
					if (execute == null) {
						throw new ExecuteNotFoundException("can not found Execute:" + executeName + ", on Model:" + modelName);
					}
					ro = execute.doExecute(context, modelName, executeName, currentPath, elCache);
				}
			}
			if(ro == null){
				ro = new ResponseObject();
				ro.setSuccess(true);
				ro.setMessage("success");
			}
			return ro;
		} finally {
			context.modelDeepReduce();
			if(context.onTopModelDeep()) {
				context.closeConns();
				LogUtil.info("-----------------------batch execute end--------------------------------");
			}
		}
	}

	public static ResponseObject execute(Context context, String modelName, String executeName) {
		return execute(context, modelName, executeName, defaultPath);
	}

	/**
	 * 执行操作
	 * 
	 * @param context context
	 * @param modelName model name
	 * @param executeName execute name
	 * @param currentPath current path
	 * @return ResponseObject
	 */
	public static ResponseObject execute(Context context, String modelName, String executeName, String currentPath) {
		try {
			if(context.onTopModelDeep()) {
				LogUtil.info("-----------------------execute begin------------------------------------");
			}
			context.modelDeepIncrease();

			if (DataUtil.isNull(executeName)) {
				executeName = "_default";
			}
			if(DataUtil.isNull(currentPath)){
				currentPath = defaultPath;
			}

			Model model = modelCache.getModel(modelName);
			Execute execute = model.getExecute(executeName);
			if (execute == null) {
				throw new ExecuteNotFoundException("can not found Execute:" + executeName + ", on Model:" + modelName);
			}
			// 把model、currentPath对象放入到 当前线程里
			context.setCurrentPath(currentPath);
			context.setCurrentModel(modelName);

			Map<String, Object> elCache = new HashMap<>();
			return execute.doExecute(context, modelName, executeName, currentPath, elCache);
		} finally {
			context.modelDeepReduce();
			if(context.onTopModelDeep()) {
				context.closeConns();
				LogUtil.info("-----------------------execute end--------------------------------------");
			}
		}
	}
}
