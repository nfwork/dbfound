package com.nfwork.dbfound.model.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.util.DataUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ModelTool {

	private static Map<Integer, Class<?>> sql2javaType = new HashMap<Integer, Class<?>>();
	private static Set<Class<?>> simpleType = new HashSet<Class<?>>();
	static {
		// simpleType
		simpleType.add(boolean.class);
		simpleType.add(Boolean.class);
		simpleType.add(String.class);
		simpleType.add(char.class);
		simpleType.add(Character.class);
		simpleType.add(byte.class);
		simpleType.add(Byte.class);
		simpleType.add(Integer.class);
		simpleType.add(int.class);
		simpleType.add(Long.class);
		simpleType.add(long.class);
		simpleType.add(Short.class);
		simpleType.add(short.class);
		simpleType.add(Float.class);
		simpleType.add(float.class);
		simpleType.add(Double.class);
		simpleType.add(double.class);
		simpleType.add(java.util.Date.class);
		simpleType.add(java.util.UUID.class);
		// sql2javaType
		sql2javaType.put(java.sql.Types.BIGINT, Long.class);
		sql2javaType.put(java.sql.Types.INTEGER, Integer.class);
		sql2javaType.put(java.sql.Types.ARRAY, java.sql.Array.class);
		sql2javaType.put(java.sql.Types.BLOB, java.sql.Blob.class);
		sql2javaType.put(java.sql.Types.CLOB, java.sql.Clob.class);
		sql2javaType.put(java.sql.Types.BOOLEAN, Boolean.class);
		sql2javaType.put(java.sql.Types.CHAR, String.class);
		sql2javaType.put(java.sql.Types.DATE, java.util.Date.class);
		sql2javaType.put(java.sql.Types.DECIMAL, Double.class);
		sql2javaType.put(java.sql.Types.SMALLINT, Integer.class);
		sql2javaType.put(java.sql.Types.TINYINT, Integer.class);
		sql2javaType.put(java.sql.Types.TIME, java.util.Date.class);
		sql2javaType.put(java.sql.Types.TIMESTAMP, java.util.Date.class);
		sql2javaType.put(java.sql.Types.VARCHAR, String.class);
		sql2javaType.put(java.sql.Types.NUMERIC, Double.class);

		sql2javaType.put(java.sql.Types.STRUCT, java.sql.Struct.class);
		sql2javaType.put(java.sql.Types.REAL, Float.class);
		sql2javaType.put(java.sql.Types.LONGVARCHAR, String.class);
		sql2javaType.put(java.sql.Types.FLOAT, Float.class);
		sql2javaType.put(java.sql.Types.DOUBLE, Double.class);

		sql2javaType.put(java.sql.Types.BINARY, byte[].class);
		sql2javaType.put(java.sql.Types.BIT, Boolean.class);
		sql2javaType.put(java.sql.Types.REF, Object.class);
		sql2javaType.put(java.sql.Types.VARBINARY, byte[].class);
		sql2javaType.put(java.sql.Types.LONGVARBINARY, byte[].class);
	}
	public static void generateModel(String connectionProvide, String databse,String tablename, String pk, String fileName) {
		if (DataUtil.isNull(connectionProvide)) {
			connectionProvide = "_default";
		}
		File file = new File(getRealValue(fileName));
		Table table = getTable(connectionProvide, databse,tablename);
		for (Column column : table.getColumnlist()) {
			column.setType(getDataType(column.getDataType()));
			column.setParamName(column.getColumnName());
		}
		try {
			generateModel(table, pk, new OutputStreamWriter(new FileOutputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("生成model成功，model路径：" + file.getAbsolutePath());
	}

	public static void generateModel(String connectionProvide, String database,String tablename, String pk) {
		if (DataUtil.isNull(connectionProvide)) {
			connectionProvide = "_default";
		}
		Table table = getTable(connectionProvide, database,tablename);
		for (Column column : table.getColumnlist()) {
			column.setType(getDataType(column.getDataType()));
			column.setParamName(column.getColumnName());
		}
		generateModel(table, pk, new OutputStreamWriter(System.out));
	}
	
	public static void generateJavaBean(String connectionProvide,String database, String tableName, String packagename){
		generateJavaBean(connectionProvide, database,tableName, packagename, null);
	}

	public static void generateJavaBean(String connectionProvide, String database,String tableName, String packagename, String todir){

		Table table = getTable(connectionProvide, database, tableName);
		List<Column> columnlist = table.getColumnlist();

		StringBuilder str = new StringBuilder();
		StringBuilder getsetstr = new StringBuilder();
		String Classname = getClassName(table.getTableName());
		// packagename
		if (packagename != null && !packagename.trim().equals("")) {
			str.append("package " + packagename + ";\n\n");
		}
		// import
		str.append("import com.nfwork.dbfound.model.reflector.Column;\n\n");
		// class

		str.append("public class " + Classname + " {\n");
		for (Column column : columnlist) {
			String propertyName = getPropertyName(column.getColumnName());
			Class<?> typeClass = sql2javaType.get(column.getDataType());
			String typeName = "";
			if (!checkedSimpleType(typeClass)) {
				typeName = typeClass.getSimpleName();
			} else {
				typeName = typeClass.getName();
			}
			// property
		
			if (!propertyName.equals(column.getColumnName())) {
				str.append("\n\t@Column(name=\"" + column.getColumnName() + "\")");
			}
			str.append("\n\tprivate " + typeName + " " + propertyName + ";\n");
			
			// getsetstr
			getsetstr.append("\n\n\t" + "public void set" + (propertyName.charAt(0) + "").toUpperCase()
					+ propertyName.substring(1) + "(" + typeName + " " + propertyName + ") {");
			getsetstr.append("\n\t\tthis." + propertyName + " = " + propertyName + ";\n\t}");
			getsetstr.append("\n\n\t" + "public " + typeName + " get" + (propertyName.charAt(0) + "").toUpperCase()
					+ propertyName.substring(1) + "() {");
			getsetstr.append("\n\t\treturn " + propertyName + ";\n\t}");
		}
		str.append("\n\n\tpublic " + Classname + "() {");
		str.append("\n\t}");
		str.append(getsetstr);
		str.append("\n}");
		
		if (todir == null) {
			System.err.println(str);
		}else {
			File dir = new File(todir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(todir + "/" + Classname + ".java");
			try {
				FileUtils.writeStringToFile(file, str.toString(), "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("创建成功,文件路径："+file.getAbsolutePath());
		}
	}

	
	public static void generateBeanModel(String connectionProvide, String database,String tablename, String pk, String fileName) {
		if (DataUtil.isNull(connectionProvide)) {
			connectionProvide = "_default";
		}
		File file = new File(getRealValue(fileName));
		Table table = getTable(connectionProvide, database,tablename);
		for (Column column : table.getColumnlist()) {
			column.setType(getDataType(column.getDataType()));
			column.setParamName(getPropertyName(column.getColumnName()));
		}
		try {
			generateModel(table, pk, new OutputStreamWriter(new FileOutputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("生成model成功，model路径：" + file.getAbsolutePath());
	}

	public static void generateBeanModel(String connectionProvide, String database ,String tablename, String pk) {
		if (DataUtil.isNull(connectionProvide)) {
			connectionProvide = "_default";
		}
		Table table = getTable(connectionProvide, database,tablename);
		for (Column column : table.getColumnlist()) {
			column.setType(getDataType(column.getDataType()));
			column.setParamName(getPropertyName(column.getColumnName()));
		}
		generateModel(table, pk, new OutputStreamWriter(System.out));
	}
	
	private static String getRealValue(String value) {
		value = value.replace("${@classpath}", DBFoundConfig.getClasspath());
		String webRoot = DBFoundConfig.getProjectRoot();
		if (webRoot != null) {
			value = value.replace("${@projectRoot}", webRoot);
		}
		return value;
	}
	
	private static void generateModel(Table table, String pk, Writer writer) {
		try {
			Configuration cfg = FreemarkFactory.getConfig();
			Template template = cfg.getTemplate("model.ftl");
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("table", table);
			Column pkcColumn = null;
			for (Column column : table.getColumnlist()) {
				if (column.getColumnName().equals(pk)) {
					pkcColumn = column;
					break;
				}
			}
			table.getColumnlist().remove(pkcColumn);
			root.put("pk", pkcColumn);
			template.process(root, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String getDataType(int key) {
		switch (key) {
		case Types.VARCHAR:
			return "varchar";
		case Types.INTEGER:
			return "number";
		case Types.DOUBLE:
			return "number";
		case Types.FLOAT:
			return "number";
		case Types.DECIMAL:
			return "number";
		case Types.NUMERIC:
			return "number";
		case Types.VARBINARY:
			return "number";
		case Types.BIGINT:
			return "number";
		case Types.REAL:
			return "number";
		default:
			return "varchar";
		}
	}

	private static Table getTable(String connectionProvide, String database ,String tablename) {
		Context context = new Context();
		try {
			Connection connection = context.getConn(connectionProvide);

			DatabaseMetaData dbmetadata = connection.getMetaData();
			ResultSet rs = dbmetadata.getTables(database,null, tablename, new String[] { "TABLE" });

			List<Table> tablelist = new ArrayList<Table>();
			while (rs.next()) {
				Table table = new Table();
				table.setTableName(rs.getString("TABLE_NAME"));
				table.setRemarks(rs.getString("REMARKS"));
				tablelist.add(table);
			}
			rs.close();

			Table table = (Table) tablelist.get(0);
			rs = dbmetadata.getColumns(database, null, table.getTableName(), null);

			List<Column> columnlist = new ArrayList<Column>();
			while (rs.next()) {
				Column column = new Column();
				column.setColumnName(rs.getString("COLUMN_NAME"));
				column.setDataType(rs.getInt("DATA_TYPE"));
				column.setRemarks(rs.getString("REMARKS"));
				column.setColumnSize(rs.getInt("COLUMN_SIZE"));
				column.setNullAble(rs.getString("IS_NULLABLE"));
				column.setDefaultValue(rs.getString("COLUMN_DEF"));
				column.setAutoIncrement(rs.getString("IS_AUTOINCREMENT"));
				columnlist.add(column);
			}
			table.setColumnlist(columnlist);
			return table;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.closeConns();
			DBFoundConfig.destory();
		}
		return null;
	}

	private static boolean checkedSimpleType(Class<?> t) {
		if (simpleType.contains(t)) {
			return true;
		}
		return false;
	}

	private static String getClassName(String tableName) {
		StringBuilder className = new StringBuilder();
		String[] chars = tableName.split("");
		boolean upCase = true;
		for (String achar : chars) {
			if (achar == null || "".equals(achar)) {
				continue;
			}
			if (!achar.equals("_")) {
				if (upCase) {
					className.append(achar.toUpperCase());
					upCase = false;
				} else {
					className.append(achar);
				}
			} else {
				upCase = true;
			}
		}
		return className.toString();
	}

	private static String getPropertyName(String columnName) {
		StringBuilder propertyName = new StringBuilder();
		String[] chars = columnName.split("");
		boolean upCase = false;
		int num = 1;
		for (String achar : chars) {
			if (achar == null || "".equals(achar)) {
				continue;
			}
			if (!achar.equals("_")) {
				if (num == 1) {
					propertyName.append(achar.toLowerCase());
				} else if (upCase) {
					propertyName.append(achar.toUpperCase());
					upCase = false;
				} else {
					propertyName.append(achar);
				}
			} else {
				upCase = true;
			}
			num++;
		}
		return propertyName.toString();
	}

}
