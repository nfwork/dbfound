package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private DBFoundRuntimeException initException;

	@Override
	public void run() {
		super.run();

		if(DataUtil.isNull(sourcePath)){
			initException = new DBFoundRuntimeException("BatchExecuteSql attribute sourcePath can not be null");
			return;
		}
		if(DataUtil.isNull(sql)){
			initException = new DBFoundRuntimeException("BatchExecuteSql content sql can not be null");
			return;
		}

		autoCreateParam(sql,this);

		//sql 分解
		int indexBegin = sql.indexOf(BATCH_TEMPLATE_BEGIN);
		if(indexBegin == -1){
			initException = new DBFoundRuntimeException(BATCH_TEMPLATE_BEGIN + " not found in the sql");
			return;
		}
		int indexEnd = sql.indexOf(BATCH_TEMPLATE_END);
		if(indexEnd == -1){
			initException = new DBFoundRuntimeException(BATCH_TEMPLATE_END + " not found in the sql");
			return;
		}
		beforeTmpSql = sql.substring(0,indexBegin);
		tmpSql = sql.substring(indexBegin+BATCH_TEMPLATE_BEGIN.length(), indexEnd);
		afterTmpSql = sql.substring(indexEnd + BATCH_TEMPLATE_END.length());

		paramNameList = new ArrayList<String>();
		tmpSql = analysisTmpSql(tmpSql, paramNameList);
	}

	public void execute(Context context, Map<String, Param> params, String provideName){
		if(initException != null){
			throw  initException;
		}
		String exeSourcePath = sourcePath;

		//判断是否相对路径,如果是相对路径则进行转化
		if(!exeSourcePath.startsWith(ELEngine.sessionScope) && !exeSourcePath.startsWith(ELEngine.requestScope)
				&& !exeSourcePath.startsWith(ELEngine.outParamScope) && !exeSourcePath.startsWith(ELEngine.paramScope)
				&& !exeSourcePath.startsWith(ELEngine.cookieScope) && !exeSourcePath.startsWith(ELEngine.headerScope)) {
			if(DataUtil.isNotNull(context.getCurrentPath())){
				exeSourcePath = context.getCurrentPath() +"." +exeSourcePath;
			}
		}

		int dataSize =0;
		Object data = context.getData(exeSourcePath);
		if(data != null) {
			if (data instanceof List) {
				List dataList = (List) data;
				dataSize = dataList.size();
			} else if (data instanceof Set) {
				Set set = (Set) data;
				dataSize = set.size();
			} else if (data instanceof Object[]) {
				Object[] objects = (Object[]) data;
				dataSize = objects.length;
			}
		}

		if(dataSize ==0 ){
			return;
		}

		for (Param param : params.values()){
			if (DataUtil.isNotNull(param.getScope())){
				param.setBatchAssign(false);
			}else if ( DataUtil.isNotNull(param.getSourcePath())
					&& (param.getSourcePath().startsWith(ELEngine.sessionScope) || param.getSourcePath().startsWith(ELEngine.requestScope)
					|| param.getSourcePath().startsWith(ELEngine.outParamScope) || param.getSourcePath().startsWith(ELEngine.paramScope)
					|| param.getSourcePath().startsWith(ELEngine.cookieScope) || param.getSourcePath().startsWith(ELEngine.headerScope))) {
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

		String eSql = beforeTmpSql;

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
					newParam.setValue(context.getData(newParam.getSourcePathHistory()));
				}
				exeParams.put(newParam.getName(),newParam);
				listParam.add(newParam);
			}
			eSql = eSql + tmpSql.replaceAll("##",i+"" );
			if(i < end-1){
				eSql = eSql +",";
			}
		}
		eSql = eSql + afterTmpSql;
		return execute(context,exeParams,provideName,eSql,listParam);
	}

	private int execute(Context context, Map<String, Param> params, String provideName,String sql, List<Param> listParam) {

		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);

		sql = staticParamParse(sql, params);

		String esql = getExecuteSql(sql, params);
		// 方言处理
		esql = dialect.parseSql(esql);

		try {
			PreparedStatement statement = null;
			ResultSet rs = null;
			statement = conn.prepareStatement(esql);

			try {
				// 参数设定
				initParam(statement, sql, params);
				statement.execute();

				return statement.getUpdateCount();
			} finally {
				DBUtil.closeResultSet(rs);
				DBUtil.closeStatement(statement);
				log(esql, listParam);
			}
			
		} catch (SQLException e) {
			throw new DBFoundPackageException("ExecuteSql执行异常:" + e.getMessage(), e);
		}
	}

	private String analysisTmpSql(String sql, List<String> batchParamNameList ) {
		Pattern p = Pattern.compile(paramReplace);
		Matcher m = p.matcher(sql);
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
