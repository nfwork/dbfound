package com.nfwork.dbfound.dto;

import java.util.List;

public class QueryResponseObject<T> extends ResponseObject{

	private List<T> datas;

	private long totalCounts = -1;
	
	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public long getTotalCounts() {
		return totalCounts;
	}

	public void setTotalCounts(long totalCounts) {
		this.totalCounts = totalCounts;
	}

}
