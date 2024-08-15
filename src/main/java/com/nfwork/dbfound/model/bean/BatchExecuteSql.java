package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.exception.SqlExecuteException;
import com.nfwork.dbfound.model.base.IOType;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class BatchExecuteSql extends Sql {

	private String affectedCountParam;

	private String sourcePath;

	private Integer batchSize = 100;

	private String beforeTmpSql;

	private String tmpSql;

	private String afterTmpSql;

	private Set<String> paramNameSet;

	private String item;

	private String index;

	private static  final  String BATCH_TEMPLATE_BEGIN = "#BATCH_TEMPLATE_BEGIN#";
	private static  final  String BATCH_TEMPLATE_END = "#BATCH_TEMPLATE_END#";

	private String initError;

	@Override
	public void doEndTag() {
		super.doEndTag();

		if(DataUtil.isNull(sourcePath)){
			initError = "BatchExecuteSql attribute sourcePath can not be null";
			return;
		}
		if(DataUtil.isNull(sql)){
			initError = "BatchExecuteSql content sql can not be null";
			return;
		}

		autoCreateParam(sql,this);

		//sql 分解
		int indexBegin = sql.indexOf(BATCH_TEMPLATE_BEGIN);
		if(indexBegin == -1){
			initError = BATCH_TEMPLATE_BEGIN + " not found in the sql";
			return;
		}
		int indexEnd = sql.indexOf(BATCH_TEMPLATE_END);
		if(indexEnd == -1){
			initError = BATCH_TEMPLATE_END + " not found in the sql";
			return;
		}
		beforeTmpSql = sql.substring(0,indexBegin);
		tmpSql = sql.substring(indexBegin+BATCH_TEMPLATE_BEGIN.length(), indexEnd).trim();
		afterTmpSql = sql.substring(indexEnd + BATCH_TEMPLATE_END.length());

		paramNameSet = new LinkedHashSet<>();
		tmpSql = analysisTmpSql(tmpSql, paramNameSet);
	}

	public void execute(Context context, Map<String, Param> params, String provideName){
		if(initError != null){
			throw new DBFoundRuntimeException(initError);
		}
		String exeSourcePath = sourcePath;

		//判断是否相对路径,如果是相对路径则进行转化
		if(!ELEngine.isAbsolutePath(exeSourcePath)){
			exeSourcePath = context.getCurrentPath() +"." +exeSourcePath;
		}

		int dataSize = context.getDataLength(exeSourcePath);

		if(dataSize <= 0){
			return;
		}

		String realTmpSql = tmpSql;
		for (String paramName : paramNameSet){
			Param param = params.get(paramName);
			if(param == null){
				throw new ParamNotFoundException("param: "+ paramName +" not defined");
			}
			if (DataUtil.isNotNull(param.getScope())){
				param.setBatchAssign(false);
			}else if (ELEngine.isAbsolutePath(param.getSourcePath())) {
				param.setBatchAssign(false);
			}
			if(!param.isBatchAssign()){
				realTmpSql = realTmpSql.replace("@" +param.getName()+"_##", "@"+param.getName());
			}
		}

		int updateCount = 0;
		for (int begin= 0 ; begin < dataSize; begin=begin+batchSize){
			int end = begin + batchSize;
			if(end > dataSize) end = dataSize;
			int size = execute(context,realTmpSql,params,exeSourcePath,provideName,begin,end);
			updateCount = updateCount +size;
		}

		// 2014年8月14日11:38:34 添加affectedCountParam
		if (DataUtil.isNotNull(affectedCountParam)) {
			Param param = params.get(affectedCountParam);
			if (param == null) {
				throw new ParamNotFoundException("param: " + affectedCountParam + " not defined");
			}
			param.setValue(updateCount);

			if(param.getIoType() != IOType.IN){
				context.setOutParamData(param.getName(),param.getValue());
			}
		}
	}

	private int execute(Context context, String realTmpSql, Map<String, Param> params, String exeSourcePath,String provideName, int begin ,int end){
		Map<String, Param> exeParams = new LinkedHashMap<>(params);

		StringBuilder eSql = new StringBuilder(beforeTmpSql);

		Map<String, Object> elCache = new HashMap<>();

		for (int i =begin; i< end; i++){
			for (String paramName : paramNameSet){
				Param param = params.get(paramName);

				if (param.isBatchAssign()){
					Param newParam = (Param) param.cloneEntity();
					newParam.setName(newParam.getName()+"_"+i);
					if (exeParams.containsKey(newParam.getName())) {
						throw new DBFoundRuntimeException("BatchExecuteSql create param failed, the param '" + newParam.getName() + "' already exists");
					}
					String sp = DataUtil.isNull(param.getSourcePath())?param.getName():param.getSourcePath();
					if (index != null && index.equals(sp)) {
						newParam.setValue(i);
						newParam.setSourcePathHistory("set_by_index");
					}else {
						String sph;
						if (item != null && item.equals(sp)) {
							sph = exeSourcePath + "[" + i + "]";
						} else {
							sph = exeSourcePath + "[" + i + "]." + sp;
						}
						newParam.setSourcePathHistory(sph);
						Object value = context.getData(newParam.getSourcePathHistory(), elCache);
						if ("".equals(value) && param.isEmptyAsNull()) {
							value = null;
						}
						newParam.setValue(value);
					}
					exeParams.put(newParam.getName(), newParam);
				}
			}
			eSql.append(realTmpSql.replace("##", i + ""));
			if(i < end-1){
				eSql.append(",");
			}
		}
		eSql.append(afterTmpSql);
		return execute(context,exeParams,provideName, eSql.toString(), begin);
	}

	private int execute(Context context, Map<String, Param> params, String provideName,String sql, int begin) {
		Connection conn = context.getConn(provideName);

		sql = staticParamParse(sql, params);
		List<Object> exeParam = new ArrayList<>();
		String esql = getExecuteSql(sql, params, exeParam);

		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, exeParam);
			statement.execute();

			int updateCount =  statement.getUpdateCount();
			if (DataUtil.isNotNull(affectedCountParam)) {
				Param param = params.get(affectedCountParam);
				if (param == null) {
					throw new ParamNotFoundException("param: " + affectedCountParam + " not defined");
				}
				param.setRequireLog(true);
				param.setValue(updateCount);
				param.setSourcePathHistory("set_by_affectedCount");
			}
			return updateCount;
		} catch (SQLException e) {
			throw new SqlExecuteException(provideName, getSqlTask(context,"BatchExecuteSql"), sql, e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(statement);
			Collection<Param> collection = params.values();
			if(begin>0){
				collection = collection.stream().filter(p->p.getName().endsWith("_"+begin)).collect(Collectors.toList());
			}
			LogUtil.logSql("batchExecuteSql", esql, collection,exeParam);
		}
	}

	private String analysisTmpSql(String sql, Set<String> batchParamNameSet ) {
		Matcher m = paramPattern.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String param = m.group();
			String pn = param.substring(2, param.length() - 1).trim();
			batchParamNameSet.add(pn);
			String value = "{@" + pn +"_##}";
			m.appendReplacement(buf, value);
		}
		m.appendTail(buf);
		return buf.toString();
	}

	public String getAffectedCountParam() {
		return affectedCountParam;
	}

	public void setAffectedCountParam(String affectedCountParam) {
		this.affectedCountParam = affectedCountParam;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
}
