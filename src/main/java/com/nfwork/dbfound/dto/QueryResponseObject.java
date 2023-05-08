package com.nfwork.dbfound.dto;

import com.nfwork.dbfound.el.DBFoundEL;

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
		return datas.stream().collect(Collectors.toMap(value -> stringValue(DBFoundEL.getData(keyProperty, value)), value -> value));
	}

	public Object getProperty(String propertyName){
		if(datas == null || datas.size() == 0){
			return null;
		}
		return DBFoundEL.getData(propertyName,datas.get(0));
	}

	public String getString(String propertyName){
		Object value = getProperty(propertyName);
		return stringValue(value);
	}

	public Integer getInt(String propertyName){
		Object value = getProperty(propertyName);
		return intValue(value);
	}

	public Long getLong(String propertyName){
		Object value = getProperty(propertyName);
		return longValue(value);
	}

	public Double getDouble(String propertyName){
		Object value = getProperty(propertyName);
		return doubleValue(value);
	}

	public Float getFloat(String propertyName){
		Object value = getProperty(propertyName);
		return floatValue(value);
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
		return datas.stream().map(value -> stringValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	public List<Integer> getIntList(String propertyName){
		if(datas == null ){
			return null;
		}
		return datas.stream().map(value -> intValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	public List<Long> getLongList(String propertyName){
		if(datas == null ){
			return null;
		}
		return datas.stream().map(value -> longValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	public List<Double> getDoubleList(String propertyName){
		if(datas == null ){
			return null;
		}
		return datas.stream().map(value -> doubleValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	public List<Float> getFloatList(String propertyName){
		if(datas == null ){
			return null;
		}
		return datas.stream().map(value -> floatValue(DBFoundEL.getData(propertyName, value))).collect(Collectors.toList());
	}

	private static String stringValue(Object value){
		if(value == null){
			return null;
		}
		return value.toString();
	}

	private static Integer intValue(Object value){
		if(value == null){
			return null;
		}
		if(value instanceof Integer){
			return (Integer)value;
		}
		return Integer.parseInt(value.toString());
	}

	private static Long longValue(Object value){
		if(value == null){
			return null;
		}
		if(value instanceof Long){
			return (Long) value;
		}
		return Long.parseLong(value.toString());
	}

	private static Double doubleValue(Object value){
		if(value == null){
			return null;
		}
		if(value instanceof Double){
			return (Double) value;
		}
		return Double.parseDouble(value.toString());
	}

	private static Float floatValue(Object value){
		if(value == null){
			return null;
		}
		if(value instanceof Float){
			return (Float) value;
		}
		return Float.parseFloat(value.toString());
	}

}
