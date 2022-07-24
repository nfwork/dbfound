package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;

public class BatchExecuteSql extends SqlEntity {

	private static final long serialVersionUID = 7525842037480200449L;

	private String affectedCountParam;

	private String sourcePath;

	private Integer batchSize = 100;

	private String beforeTmpSql;

	private String tmpSql;

	private String afterTmpSql;

	private List<String> paramNameList;

	private static  final  String BATCH_TEMPLATE_BEGIN = "#BATCH_TEMPLATE_BEGIN#";
	private static  final  String BATCH_TEMPLATE_END = "#BATCH_TEMPLATE_END#";

	private String initError;

	@Override
	public void run() {
		super.run();

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
		tmpSql = sql.substring(indexBegin+BATCH_TEMPLATE_BEGIN.length(), indexEnd);
		afterTmpSql = sql.substring(indexEnd + BATCH_TEMPLATE_END.length());

		paramNameList = new ArrayList<String>();
		tmpSql = analysisTmpSql(tmpSql, paramNameList);
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

		if(dataSize ==0 ){
			return;
		}

		for (Param param : params.values()){
			if (DataUtil.isNotNull(param.getScope())){
				param.setBatchAssign(false);
			}else if (ELEngine.isAbsolutePath(param.getSourcePath())) {
				param.setBatchAssign(false);
			}
		}

		int updateCount = 0;
		for (int i= 0 ; i < dataSize; i=i+batchSize){
			int begin = i;
			int end = i + batchSize;
			if(end > dataSize) end = dataSize;
			int size = execute(context,params,exeSourcePath,provideName,begin,end);
			updateCount = updateCount +size;
		}

		// 2014年8月14日11:38:34 添加affectedCountParam
		if (DataUtil.isNotNull(affectedCountParam)) {
			Param param = params.get(affectedCountParam);
			if (param == null) {
				throw new ParamNotFoundException("param: " + affectedCountParam + " not defined");
			}
			param.setValue(updateCount);
			param.setSourcePathHistory("set by affectedCount");

			if(!"in".equals(param.getIoType())){
				context.setOutParamData(param.getName(),param.getValue());
			}
		}
	}

	private int execute(Context context, Map<String, Param> params, String exeSourcePath,String provideName, int begin ,int end){
		Map<String, Param> exeParams = new HashMap<String, Param>();
		List<Param> listParam = new ArrayList<Param>();
		listParam.addAll(params.values());
		exeParams.putAll(params);

		StringBuilder eSql = new StringBuilder(beforeTmpSql);

		Map<String, Object> elCache = new HashMap<>();

		for (int i =begin; i< end; i++){
			for (String paramName : paramNameList){
				Param param = params.get(paramName);
				if(param == null){
					throw new DBFoundRuntimeException("param: "+ paramName +" not defined");
				}
				Param newParam = (Param) param.cloneEntity();
				newParam.setName(newParam.getName()+"_"+i);
				if (param.isBatchAssign()){
					String sp = param.getSourcePath()==null?param.getName():param.getSourcePath();
					newParam.setSourcePathHistory(exeSourcePath +"[" + i +"]."+ sp);
					Object value = context.getData(newParam.getSourcePathHistory(), elCache);
					if("".equals(value)){
						value = null;
					}
					newParam.setValue(value);
				}
				exeParams.put(newParam.getName(),newParam);
				listParam.add(newParam);
			}
			eSql.append(tmpSql.replace("##", i + ""));
			if(i < end-1){
				eSql.append(",");
			}
		}
		eSql.append(afterTmpSql);
		return execute(context,exeParams,provideName, eSql.toString(),listParam);
	}

	private int execute(Context context, Map<String, Param> params, String provideName,String sql, List<Param> listParam) {
		Connection conn = context.getConn(provideName);

		sql = staticParamParse(sql, params, context);
		List<Object> exeParam = new ArrayList<>();
		String esql = getExecuteSql(sql, params, exeParam, context);

		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, exeParam);
			statement.execute();
			return statement.getUpdateCount();
		} catch (SQLException e) {
			throw new DBFoundPackageException("ExecuteSql execute exception:" + e.getMessage(), e);
		}finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(statement);
			log(esql, listParam, context);
		}
	}

	private String analysisTmpSql(String sql, List<String> batchParamNameList ) {
		Matcher m = paramPattern.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String param = m.group();
			String pn = param.substring(2, param.length() - 1).trim();
			batchParamNameList.add(pn);
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
}
