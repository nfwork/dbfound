package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sqls extends SqlEntity {
	List<SqlEntity> sqlList = new ArrayList<>();

	@Override
	public void doEndTag() {
		Entity entity = getParent();
		if (entity instanceof Sqls) {
			Sqls sqls = (Sqls) entity;
			sqls.sqlList.add(this);
		}else if (entity instanceof Execute) {
			Execute execute = (Execute)entity;
			execute.setSqls(this);
		}
	}

	@Override
	public void execute(Context context, Map<String, Param> params, String provideName) {

	}

	public List<SqlEntity> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<SqlEntity> sqlList) {
		this.sqlList = sqlList;
	}

}
