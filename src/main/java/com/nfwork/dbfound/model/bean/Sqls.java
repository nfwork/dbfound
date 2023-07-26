package com.nfwork.dbfound.model.bean;

import java.util.ArrayList;
import java.util.List;

public class Sqls extends Sql {

	private static final long serialVersionUID = -5219823527523277120L;
	List<SqlEntity> sqlList = new ArrayList<>();

	@Override
	public void run() {
		if (getParent() instanceof Execute) {
			Execute execute = (Execute) getParent();
			execute.setSqls(this);
		}
	}

	public List<SqlEntity> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<SqlEntity> sqlList) {
		this.sqlList = sqlList;
	}

}
