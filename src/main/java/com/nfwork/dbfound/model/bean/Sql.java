package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.base.SqlPartType;
import com.nfwork.dbfound.util.StringUtil;
import org.dom4j.Comment;
import org.dom4j.Element;

import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class Sql extends SqlEntity {
	protected List<SqlPart> sqlPartList  = new ArrayList<>();

	protected String sql;

	@Override
	public void doStartTag(Element element) {
		super.doStartTag(element);

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
		sql = StringUtil.sqlFullTrim(builder.toString());
	}

	@Override
	public void doEndTag() {
		Entity entity = getParent();
		if (entity instanceof Sqls) {
			Sqls sqls = (Sqls) entity;
			sqls.sqlList.add(this);
		} else if(entity instanceof Query){
			Query query = (Query) entity;
			query.setSql(this);
		}
	}

	protected boolean hasForChild(){
		if(sqlPartList.isEmpty()){
			return false;
		}
		for (SqlPart sqlPart : sqlPartList){
			if(sqlPart.type == SqlPartType.FOR || sqlPart.hasForChild() ){
				return true;
			}
		}
		return false;
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

	protected String getSqlPartSql(Map<String, Param> params, Context context, String provideName) {
		int sqlPartIndex = 0;
		int commaIndex = 0;
		int start = 0;

		Matcher m = SQL_PART_PATTERN.matcher(sql);
		StringBuilder builder = new StringBuilder();
		while (m.find()) {
			if(m.start() > start){
				builder.append(sql, start, m.start());
			}
			start = m.end();
			SqlPart sqlPart = sqlPartList.get(sqlPartIndex++);
			String partValue =  sqlPart.getPartSql(context,params,provideName);

			if(partValue.isEmpty()) {
				reduceBlank(builder);
			}else{
				builder.append(partValue);
				if(sqlPart.isAutoClearComma()){
					commaIndex = builder.length() - 1;
				}
			}
		}
		if(commaIndex > 0 && builder.charAt(commaIndex) == ',') {
			builder.deleteCharAt(commaIndex);
		}
		if(start < sql.length()) {
			builder.append(sql, start, sql.length());
		}
		if(builder.length()>0 && builder.charAt(0)==' '){
			builder.deleteCharAt(0);
		}
		return builder.toString();
	}

	@Override
	public void execute(Context context, Map<String, Param> params, String provideName) {

	}
}
