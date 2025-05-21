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
		return modelOperator.query(context, modelName, queryName, null, context.isAutoPaging(), clazz);
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
		return modelOperator.query(context, modelName, queryName, null, context.isAutoPaging(), null);
	}

	/**
	 * 可指定当前路径，是否自动分页的查询
	 *
	 * @param context context
	 * @param modelName modelName
	 * @param queryName query name
	 * @param sourcePath source path
	 * @return QueryResponseObject
	 */
	public static QueryResponseObject<Map<String,Object>> query(Context context, String modelName, String queryName, String sourcePath) {
		return modelOperator.query(context, modelName, queryName, sourcePath, context.isAutoPaging(), null);
	}

	/**
	 * 查询 可以指定当前路径、是否自动分页、返回对象的查询
	 * 
	 * @param context context
	 * @param modelName model name
	 * @param queryName query name
	 * @param sourcePath source path
	 * @param clazz clazz
	 * @return T
	 */
	public static <T> QueryResponseObject<T> query(Context context, String modelName, String queryName, String sourcePath, Class<T> clazz) {
		return modelOperator.query(context, modelName, queryName, sourcePath, context.isAutoPaging(), clazz);
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
