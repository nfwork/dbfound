package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.util.StringUtil;

public class Filter extends Param {

	private static final long serialVersionUID = -7553494400564901864L;

	private String express;
	private boolean active;
	private String condition;

	@Override
	public void run(){
		if(express != null){
			express = StringUtil.fullTrim(express);
		}
		if (getParent() instanceof Query) {
			Query query = (Query) getParent();
			query.getFilters().put(getName(), this);
		}
	}

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
