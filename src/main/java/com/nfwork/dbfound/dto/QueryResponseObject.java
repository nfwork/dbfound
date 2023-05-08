package com.nfwork.dbfound.dto;

import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.util.DataUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public Map<String,T> getMap(String keyProperty){
		if(datas == null){
			return null;
		}
		return datas.stream().collect(Collectors.toMap(value -> DataUtil.stringValue(DBFoundEL.getData(keyProperty, value)), value -> value, (v1,v2)->v2));
	}

	public Object getProperty(String propertyName){
		if(datas == null || datas.size() == 0){
			return null;
		}
		return DBFoundEL.getData(propertyName,datas.get(0));
	}

	public String getString(String propertyName){
		Object value = getProperty(propertyName);
		return DataUtil.stringValue(value);
	}

	public Integer getInt(String propertyName){
		Object value = getProperty(propertyName);
		return DataUtil.intValue(value);
	}

	public Long getLong(String propertyName){
		Object value = getProperty(propertyName);
		return DataUtil.longValue(value);
	}

	public Double getDouble(String propertyName){
		Object value = getProperty(propertyName);
		return DataUtil.doubleValue(value);
	}

	public Float getFloat(String propertyName){
		Object value = getProperty(propertyName);
		return DataUtil.floatValue(value);
	}

	public List<Object> getPropertyList(String propertyName){
		if(datas == null){
			return null;
		}
		return datas.stream().map(value -> DBFoundEL.getData(propertyName, value)).collect(Collectors.toList());
	}

	public List<String> getStringList(String propertyName){
		if(datas == null){
			return null;
		}
		return datas.stream().map(value -> DataUtil.stringValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	public List<Integer> getIntList(String propertyName){
		if(datas == null ){
			return null;
		}
		return datas.stream().map(value -> DataUtil.intValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	public List<Long> getLongList(String propertyName){
		if(datas == null ){
			return null;
		}
		return datas.stream().map(value -> DataUtil.longValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	public List<Double> getDoubleList(String propertyName){
		if(datas == null ){
			return null;
		}
		return datas.stream().map(value -> DataUtil.doubleValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	public List<Float> getFloatList(String propertyName){
		if(datas == null ){
			return null;
		}
		return datas.stream().map(value -> DataUtil.floatValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}
}
