package com.nfwork.dbfound.model.bean;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.dom4j.Element;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.base.Entity;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.file.FileUtil;


public abstract class SqlEntity extends Sqls {

	private static final long serialVersionUID = 3035666882993092230L;

	String sql;

	protected static String replaceString = "\\$\\{\\@[ a-zA-Z_0-9\u4E00-\u9FA5]*\\}";

	@Override
	public void run(){
		Entity entity = getParent();
		if (entity instanceof Sqls) {
			Sqls sqls = (Sqls) entity;
			sqls.sqlList.add(this);
		}
	}

	@Override
	public void init(Element element){
		sql = element.getTextTrim();
		super.init(element);
	}

	public abstract void execute(Context context, Map<String, Param> params,
			String provideName) ;

	/**
	 * 得到最后执行的sql语句
	 * 
	 * @return
	 */
	public String getExecuteSql(String sql, Map<String, Param> params) {
		String executeSqlString = sql;
		// 转化执行的sql
		executeSqlString = executeSqlString.replaceAll(replaceString, "?");
		return executeSqlString;
	}

	/**
	 * 参数设定 sql为原生sql语句，用来寻找参数的位置
	 * 
	 * @throws SQLException
	 * @throws NumberFormatException
	 */
	public void initParam(PreparedStatement statement, String sql,
			Map<String, Param> params) throws NumberFormatException,
			SQLException {

		String paramValue;
		String paramDataType;

		// 设定参数
		Pattern p = Pattern.compile(replaceString);
		Matcher m = p.matcher(sql);
		int cursor = 1; // 游标记录参数的位置
		while (m.find()) {
			String param = m.group();
			String pn = param.substring(3, param.length() - 1);
			Param nfParam = params.get(pn.trim());

			if (nfParam == null) {
				throw new ParamNotFoundException("param: " + pn + " 没有定义");
			}

			if ("true".equals(nfParam.getUUID())) {
				paramValue = UUIDUtil.getUUID();
				nfParam.setValue(paramValue);
			} else {
				paramValue = nfParam.getStringValue();
			}
			paramDataType = nfParam.getDataType();

			if (paramValue == null) {
				statement.setString(cursor, null);
			} else if (paramDataType.equals("varchar")) {
				statement.setString(cursor, paramValue);
			} else if (paramDataType.equals("number")) {
				if ("".equals(paramValue.trim())) {
					statement.setString(cursor, null);
				} else if (paramValue.indexOf(".") == -1) {
					statement.setLong(cursor, Long.parseLong(paramValue));
				} else if (paramValue.endsWith(".0")) {
					paramValue = paramValue.substring(0,
							paramValue.length() - 2);
					nfParam.setValue(paramValue);
					statement.setLong(cursor, Long.parseLong(paramValue));
				} else {
					statement.setDouble(cursor, Double.parseDouble(paramValue));
				}
			} else if (paramDataType.equals("file")) {
				FileItem item = null;
				try {
					String saveType = nfParam.getFileSaveType();
					Object o = nfParam.getValue();
					if (o != null) {
						item = (FileItem) o;
						if ("db".equals(saveType)) {
							statement.setBinaryStream(cursor, item
									.getInputStream(), (int) item.getSize());
						} else {
							String filename = UUIDUtil.getUUID() + ".uf";
							String fileFoldName = FileUtil
									.getUploadFolderName();
							item.write(new File(FileUtil
									.getUploadFolder(fileFoldName), filename));
							statement.setString(cursor, fileFoldName + "/"
									+ filename);
						}
					}
				} catch (Exception e) {
					LogUtil.error(e.getMessage(), e);
				}
			} else {
				statement.setString(cursor, paramValue);
			}
			cursor++;
		}
	}

	public void log(String sql, Map<String, Param> params) {
		LogUtil.log(sql, params);
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public List<SqlEntity> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<SqlEntity> sqlList) {
		this.sqlList = sqlList;
	}

}
