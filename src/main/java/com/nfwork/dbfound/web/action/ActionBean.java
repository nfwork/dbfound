package com.nfwork.dbfound.web.action;

/**
 * 对应配置文件中的Action
 */
public class ActionBean {  

	private String name; // action的名字
	private String className; // action对应的映射类的全名
	private boolean singleton;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
	
}
