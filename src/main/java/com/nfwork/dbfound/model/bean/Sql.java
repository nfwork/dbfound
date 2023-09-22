package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.base.Entity;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;
import org.dom4j.Comment;
import org.dom4j.Element;

import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class Sql extends SqlEntity {

	private static final long serialVersionUID = -6802842084359033490L;

	protected List<SqlPart> sqlPartList  = new ArrayList<>();

	protected String sql;

	@Override
	public void init(Element element) {
		super.init(element);

		List<Node> nodes = element.content();
		StringBuilder builder = new StringBuilder();
		for (Node node : nodes) {
			if(node instanceof Comment){
				builder.append(" ");
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

	@Override
	public void run() {
		Entity entity = getParent();
		if (entity instanceof Sqls) {
			Sqls sqls = (Sqls) entity;
			sqls.sqlList.add(this);
		} else if(entity instanceof Query){
			Query query = (Query) entity;
			query.setSql(this);
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

	protected String initSqlPart(String sql, Map<String, Param> params, Context context, String provideName) {
		int sqlPartIndex = 0;
		int commaIndex = 0;

		Matcher m = SQL_PART_PATTERN.matcher(sql);
		StringBuffer buffer = new StringBuffer();
		while (m.find()) {
			String text = m.group();
			if (SQL_PART.equals(text)) {
				SqlPart sqlPart = sqlPartList.get(sqlPartIndex++);
				String partValue =  Matcher.quoteReplacement(getPartSql(sqlPart,context,params,provideName));
				m.appendReplacement(buffer, partValue);
				reduceBlank(buffer);

				if(sqlPart.isAutoClearComma() && DataUtil.isNotNull(partValue)){
					commaIndex = buffer.length() - 1;
				}
			}
		}
		if(commaIndex > 0 && buffer.charAt(commaIndex) == ',') {
			buffer.deleteCharAt(commaIndex);
		}
		m.appendTail(buffer);
		return buffer.toString();
	}

	@Override
	public void execute(Context context, Map<String, Param> params, String provideName) {

	}
}
