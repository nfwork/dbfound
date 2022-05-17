package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
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

	private static  final  String BATCH_TEMPLATE_BEGIN = "#BATCH_TEMPLATE_BEGIN#";
	private static  final  String BATCH_TEMPLATE_END = "#BATCH_TEMPLATE_END#";

	@Override
	public void run() {
		super.run();
		autoCreateParam(sql,this);
	}

	public void execute(Context context, Map<String, Param> params, String provideName){
		if (DataUtil.isNull(sourcePath)){
			sourcePath = "param.GridData";
			if(context.getData(sourcePath)==null){
				sourcePath = "param.dataList";
			}
		}

		Pattern p = Pattern.compile(paramReplace);

		int indexBegin = sql.indexOf(BATCH_TEMPLATE_BEGIN);
		int indexEnd = sql.indexOf(BATCH_TEMPLATE_END);
		String esql = sql.substring(0,indexBegin);
		String tmpSql = sql.substring(indexBegin+BATCH_TEMPLATE_BEGIN.length(), indexEnd).replaceAll(" ","");
		String endSql = sql.substring(indexEnd + BATCH_TEMPLATE_END.length());

		int dataSize =0;

		Object data = context.getData(sourcePath);
		if(data instanceof  List){
			List dataList = (List) data;
			dataSize = dataList.size();
		}else if(data instanceof Set){
			Set set = (Set) data;
			dataSize = set.size();
		}else if(data instanceof  Object[]){
			Object[] objects = (Object[])data;
			dataSize = objects.length;
		}
		for (int i= 0 ; i < dataSize; i=i+batchSize){
			int begin = i;
			int end = i + batchSize;
			if( end >dataSize) end = dataSize;
			execute(context,params,provideName,begin,end,esql,tmpSql,endSql);
		}
	}

	private void execute(Context context, Map<String, Param> params, String provideName, int begin ,int end, String esql,String tmpSql,String endSql){
		Map<String, Param> exeParams = new HashMap<String, Param>();
		List<Param> listParam = new ArrayList<Param>();

		for (int i =begin; i< end; i++){
			for (Param param : params.values()){
				Param newParam = (Param) param.cloneEntity();
				newParam.setName(newParam.getName()+"_"+i);
				if (DataUtil.isNull(newParam.getScope()) && DataUtil.isNull(newParam.getSourcePath())){
					newParam.setSourcePath(sourcePath +"[" + i +"]."+param.getName());
					newParam.setSourcePathHistory(newParam.getSourcePath());
					newParam.setValue(context.getData(newParam.getSourcePath()));
				}
				exeParams.put(newParam.getName(),newParam);
				listParam.add(newParam);
			}
			esql = esql + tmpSql.replaceAll("}","_"+i +"}");
			if(i<end-1){
				esql = esql +",";
			}
		}
		esql = esql + endSql;
		execute(context,exeParams,provideName,esql,listParam);
	}

	private void execute(Context context, Map<String, Param> params, String provideName,String sql, List<Param> listParam) {

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

				// 2014年8月14日11:38:34 添加affectedCountParam
				if (DataUtil.isNotNull(affectedCountParam)) {
					int fetchSize = statement.getUpdateCount();

					Param param = params.get(affectedCountParam);
					if (param == null) {
						throw new ParamNotFoundException("param: " + affectedCountParam + " not defined");
					}
					param.setValue(fetchSize);
					param.setSourcePathHistory("set by affectedCount");
				}
				
			} finally {
				DBUtil.closeResultSet(rs);
				DBUtil.closeStatement(statement);
				log(esql, listParam);
			}
			
		} catch (SQLException e) {
			throw new DBFoundPackageException("ExecuteSql执行异常:" + e.getMessage(), e);
		}
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
