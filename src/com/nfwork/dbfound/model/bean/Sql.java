package com.nfwork.dbfound.model.bean;

import org.dom4j.Element;

import com.nfwork.dbfound.model.base.Entity;

public class Sql extends Entity {

	private static final long serialVersionUID = -6802842084359033490L;

	@Override
	public void init(Element element) {
		super.init(element);
		sql = element.getTextTrim();
	}

	private String sql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public void run() {
		if (getParent() instanceof Query) {
			Query query = (Query) getParent();
			query.setSql(sql);
		}
	}
}
