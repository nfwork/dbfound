package com.nfwork.dbfound.model.base;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

import com.nfwork.dbfound.exception.DBFoundPackageException;

public abstract class Entity implements Serializable, Cloneable {

	private static final long serialVersionUID = -4264599446199548963L;

	private Entity parent;

	public abstract void run();

	public void init(Element element) {
		List<Attribute> list = element.attributes();
		for (Attribute attribute : list) {
			try {
				BeanUtils.setProperty(this, attribute.getName(), attribute
						.getValue());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public Object cloneEntity() {
		try {
			return clone();
		} catch (CloneNotSupportedException e) {
			throw new DBFoundPackageException("entity克隆异常:" + e.getMessage(), e);
		}
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}
}
