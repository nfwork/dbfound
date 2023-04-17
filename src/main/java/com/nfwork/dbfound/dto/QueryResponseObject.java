package com.nfwork.dbfound.dto;

import com.nfwork.dbfound.el.DBFoundEL;
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

	public T get(){
		if(datas == null || datas.size() == 0){
			return null;
		}
		return datas.get(0);
	}

	public Object getProperty(String propertyName){
		if(datas == null || datas.size() == 0){
			return null;
		}
		return DBFoundEL.getData(propertyName,datas.get(0));
	}

	public String getString(String propertyName){
		Object value = getProperty(propertyName);
		if(value == null){
			return null;
		}
		return value.toString();
	}

	public Integer getInt(String propertyName){
		Object value = getProperty(propertyName);
		if(value == null){
			return null;
		}
		if(value instanceof Integer){
			return (Integer)value;
		}
		return Integer.parseInt(value.toString());
	}

	public Long getLong(String propertyName){
		Object value = getProperty(propertyName);
		if(value == null){
			return null;
		}
		if(value instanceof Long){
			return (Long) value;
		}
		return Long.parseLong(value.toString());
	}

	public Double getDouble(String propertyName){
		Object value = getProperty(propertyName);
		if(value == null){
			return null;
		}
		if(value instanceof Double){
			return (Double) value;
		}
		return Double.parseDouble(value.toString());
	}

	public Float getFloat(String propertyName){
		Object value = getProperty(propertyName);
		if(value == null){
			return null;
		}
		if(value instanceof Float){
			return (Float) value;
		}
		return Float.parseFloat(value.toString());
	}

}
