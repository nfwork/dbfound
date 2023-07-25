package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.util.StringUtil;
import org.dom4j.Element;

import com.nfwork.dbfound.model.base.Entity;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;

public class Sql extends Entity {

	private static final long serialVersionUID = -6802842084359033490L;

	@Override
	public void init(Element element) {
		super.init(element);
		List<Node> nodes = element.content();

		StringBuilder builder = new StringBuilder();
		for(Node node: nodes){
			if(node instanceof Element){
				builder.append(" ").append(Query.SQL_PART).append(" ");
			}else{
				String text = node.getText();
				builder.append(text);
			}
		}
		sql = StringUtil.fullTrim(builder.toString());
	}

	private List<SqlPart> sqlPartList = new ArrayList<>();

	private String sql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<SqlPart> getSqlPartList() {
		return sqlPartList;
	}

	public void setSqlPartList(List<SqlPart> sqlPartList) {
		this.sqlPartList = sqlPartList;
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
