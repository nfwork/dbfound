package com.nfwork.dbfound.model;

import java.util.*;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;

public class ModelEngine {

	private static ModelOperator modelOperator = new ModelOperator();

	public static final String defaultBatchPath = "param.GridData";

	public static final String defaultPath = "param";

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
		return modelOperator.query(context, modelName, queryName, null, true, clazz);
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
		return modelOperator.query(context, modelName, queryName, null, true, null);
	}

	/**
	 * 查询 可以指定当前路径、是否自动分页、返回对象的查询
	 *
	 * @param context context
	 * @param modelName model name
	 * @param queryName query name
	 * @param sourcePath source path
	 * @param autoPaging auto paging
	 * @param clazz clazz
	 * @return T
	 */
	public static <T> QueryResponseObject<T> query(Context context, String modelName, String queryName, String sourcePath, boolean autoPaging, Class<T> clazz) {
		return modelOperator.query(context, modelName, queryName, sourcePath, autoPaging, clazz);
	}

	/**
	 * query list
	 * @param context context
	 * @param modelName model name
	 * @param queryName query name
	 * @return List
	 */
	public static List<Map<String,Object>> queryList(Context context, String modelName, String queryName) {
		QueryResponseObject<Map<String, Object>> object = modelOperator.query(context, modelName, queryName, null, false,null);
		return object.getDatas();
	}

	/**
	 * query list data, return list object
	 * @param context context
	 * @param modelName model name
	 * @param queryName query name
	 * @param class1 entity class
	 * @param <T> T
	 * @return list of T
	 */
	public static <T> List<T> queryList(Context context, String modelName, String queryName, Class<T> class1) {
		return modelOperator.query(context, modelName, queryName, null, false, class1).getDatas();
	}

	/**
	 * query one line data, return a object
	 * @param context context
	 * @param modelName model name
	 * @param queryName query name
	 * @return Map
	 */
	public static Map<String,Object> queryOne(Context context, String modelName, String queryName) {
		List<Map<String,Object>> dataList = queryList(context, modelName, queryName);
		if (dataList != null && !dataList.isEmpty()) {
			return dataList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * query one line data, return a Object T
	 * @param context context
	 * @param modelName model name
	 * @param queryName query name
	 * @param class1 entity class
	 * @param <T> T
	 * @return T
	 */
	public static <T> T queryOne(Context context, String modelName, String queryName, Class<T> class1) {
		List<T> dataList = queryList(context, modelName, queryName, class1);
		if (dataList != null && !dataList.isEmpty()) {
			return dataList.get(0);
		} else {
			return null;
		}
	}

	public static boolean isBatchExecuteRequest(Context context){
		return context.getData(ModelEngine.defaultBatchPath) instanceof List;
	}

	public static ResponseObject batchExecute(Context context, String modelName, String executeName) {
		return modelOperator.batchExecute(context, modelName, executeName, null);
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
		return modelOperator.batchExecute(context, modelName, executeName, sourcePath);
	}

	public static ResponseObject execute(Context context, String modelName, String executeName) {
		return modelOperator.execute(context, modelName, executeName, null);
	}

	/**
	 * 执行操作
	 * 
	 * @param context context
	 * @param modelName model name
	 * @param executeName execute name
	 * @param sourcePath source path
	 * @return ResponseObject
	 */
	public static ResponseObject execute(Context context, String modelName, String executeName, String sourcePath) {
		return modelOperator.execute(context, modelName, executeName, sourcePath);
	}

	public static ModelOperator getModelOperator() {
		return modelOperator;
	}

	public static void setModelOperator(ModelOperator modelOperator) {
		ModelEngine.modelOperator = modelOperator;
	}
}
