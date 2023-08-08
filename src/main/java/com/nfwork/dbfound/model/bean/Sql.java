package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.util.StringUtil;
import org.dom4j.Comment;
import org.dom4j.Element;

import com.nfwork.dbfound.model.base.Entity;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Sql extends Entity {

	private static final long serialVersionUID = -6802842084359033490L;

	protected static final String SQL_PART = "#SQL_PART#";

	protected List<SqlPart> sqlPartList;

	protected final static Pattern SQL_PART_PATTERN  = Pattern.compile("#[A-Z_]+#");

	protected String sql;

	@Override
	public void init(Element element) {
		super.init(element);

		Class<?> className = getClass();
		if(className.equals(Sql.class) || className.equals(ExecuteSql.class)
				|| className.equals(BatchExecuteSql.class) || className.equals(QuerySql.class)) {
			sqlPartList = new ArrayList<>();
			List<Node> nodes = element.content();
			StringBuilder builder = new StringBuilder();
			for (Node node : nodes) {
				if(node instanceof Comment){
					continue;
				}
				if (node instanceof Element) {
					builder.append(" ").append(SQL_PART).append(" ");
				} else {
					String text = node.getText();
					builder.append(text);
				}
			}
			sql = StringUtil.fullTrim(builder.toString());
		}
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<SqlPart> getSqlPartList() {
		return sqlPartList;
	}

	@Override
	public void run() {
		if (getParent() instanceof Query) {
			Query query = (Query) getParent();
			query.setSql(sql);
			query.setSqlPartList(sqlPartList);
		}
	}
}
